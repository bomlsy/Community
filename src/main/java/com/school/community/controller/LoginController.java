package com.school.community.controller;

import com.google.code.kaptcha.Producer;
import com.school.community.entity.User;
import com.school.community.service.UserService;
import com.school.community.util.CommunityConstant;
import com.school.community.util.CommunityUtil;
import com.school.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/*
    登录注册界面
 */
@Controller
public class LoginController implements CommunityConstant {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptcha;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("{server.servlet.context-path}")
    private String contextPath;

    //返回注册页面
    @RequestMapping(path = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    //返回登录页面
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    //注册功能
    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，我们已向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }

    }
    //激活成功返回响应页面
    //http://localhost:8080/community/activation/101/code
    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code)
    {
        int result = userService.activation(userId,code);
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg", "激活成功，您的账号已经可以正常使用了！");
            model.addAttribute("target", "/login");
        }else if(result == ACTIVATION_REPEAT){
            model.addAttribute("msg", "无效操作，该账号已经被激活！");
            model.addAttribute("target", "/index");
        }else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    //返回验证码的图片
    @RequestMapping(path = "/kaptcha", method = RequestMethod.GET)
    @ResponseBody
    //生成的验证码图片需要保存，在登录时用于对比是否一致
    //Redis:重构验证码功能，不使用session
    public void getKaptcha(HttpServletResponse response/*, HttpSession session*/){
        //生成验证码
        String text = kaptcha.createText();
        BufferedImage image = kaptcha.createImage(text);

        //将验证码存入session
        //session.setAttribute("kaptcha", text);

        //将验证码存入Redis
        //验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();//生成一个随机字符串，用于标识验证码归属于哪个用户
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);//cookie的有效时间
        cookie.setPath(contextPath);//cookie在整个项目下都有效
        response.addCookie(cookie);

        //将验证码存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);//设置生效时间为60s

        //把图片输出给浏览器
        response.setContentType("image/png");//声明返回给浏览器的是一个图片，且是png格式
        try {
            OutputStream outputStream = response.getOutputStream();//获取输出给浏览器的数据流，Stream指字节流
            ImageIO.write(image,"png", outputStream);
        } catch (IOException e) {
            logger.error("响应验证码失败："+ e.getMessage());
        }

    }

    //登录
    //使用Redis重构登录方法，不从session中取验证码，而是从Redis中取验证码
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    //login函数中的参数如果是User这种类，则MVC会自动装到model中，而如果是String这种简单类型的参数，则不会
    //因此，当想在表单上（login页面）上显示用户输入的这些参数时，需要从request请求中取，详细见login.html
    //code:用户输入验证码 remember：记住我 model：返回给浏览器的数据 session：浏览器得到的验证码 response：用于生成cookie
    public String login(String username, String password, String code, boolean remember,
                        Model model, /*HttpSession session,*/ HttpServletResponse response,
                        @CookieValue("kaptchaOwner") String kaptchaOwner)//获取cookie的值
    {
        //检查验证码
        //String kaptcha = (String) session.getAttribute("kaptcha");
        //从Redis中取验证码，需要生成redisKey,而生成redisKey需要kaptchaOwner,需要从cookie中取
        String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg", "您输入的验证码不正确！");
            return "/site/login"; //验证码不正确返回登录页面
        }

        //检查账号、密码
        int expiredSeconds = remember?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        //map中存入了ticket，则成功
        if (map.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);
            return "redirect:/index";//重定向：首页
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }


    }

    //登出
    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    //用一个String去接受cookie中的ticket的值
    public String logOut(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
    }
}
