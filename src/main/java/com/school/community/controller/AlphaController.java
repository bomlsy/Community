package com.school.community.controller;

import com.school.community.service.AlphaService;
import com.school.community.util.CommunityUtil;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    //注入AlphaService
    @Autowired
    private AlphaService alphaService;

    @ResponseBody
    @RequestMapping("/hello")
    public String sayHello(){
        return "Hello Spring Boot";
    }

    //模拟一个查询请求
    @ResponseBody
    @RequestMapping("/data")//网站访问路径
    public String getData(){
        return alphaService.find();
    }

    //处理浏览器的请求以及响应
    //获取请求对象和响应对象，作为参数传入函数中
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取请求数据
        System.out.println(request.getMethod());//请求方式
        System.out.println(request.getServletPath());//请求路径
        //得到所有请求行的key，存入一个迭代器中
        Enumeration<String> enumeration = request.getHeaderNames();
        //遍历迭代器中元素
        while(enumeration.hasMoreElements()){
            //将元素（key值）存入name中
            String name = enumeration.nextElement();
            //获取value值
            String value = request.getHeader(name);
            System.out.println(name + ":" + value);
        }
        //请求体
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");//定义返回给浏览器什么类型的数据
        try ( //获取输出流
              PrintWriter writer = response.getWriter();
              ) {
            //输出网页内容
            writer.write("<h1>lsy</h1>");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //GET:默认方式并且含义是向浏览器获取某些数据
    //假设要查询所有的学生,查询路径如下:当前查询第一页，每一页最多显示20条数据
    // /students?current=1&limit=20
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
        public String getStudents(
                //获取请求中的参数，传入current和limit中
                @RequestParam(name = "current", required = false, defaultValue = "1") int current,
                @RequestParam(name = "limit", required = false, defaultValue = "10") int limit
    ){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    //获取某一个学生信息
    // /student/123
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){//从请求中获取参数id并传入int变量id里面
        System.out.println(id);
        return "a student";
    }

    //POST:当浏览器向服务器提交数据的时候
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    //要想获得浏览器提交的数据，向函数中传入的参数应该与表单中的变量名一致即可
    public String saveStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //服务器向浏览器响应一个动态的html数据
    //要响应html的话，不用写ResponseBody这个注解
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView mav = new ModelAndView();
        //响应的数据
        mav.addObject("name","张三");
        mav.addObject("age","30");
        //设置模板的路径名和名字，demo是路径名，view是名字
        mav.setViewName("/demo/view");
        return mav;
    }

    //第二种响应html的方式
    @RequestMapping(path = "/school", method = RequestMethod.GET)
    //DispatcherServlet会自动实例化一个Model，并传入到model这个参数中
    public String getSchool(Model model){
        model.addAttribute("name", "北京大学");
        model.addAttribute("age",80);

        //返回的是模板的路径
        return "/demo/view";
    }

    //服务器向浏览器响应JSON数据（一般在异步请求中响应JSON数据）
    /*
        因为我们是用java语言编写程序，当服务器向浏览器返回一个java对象的数据时，浏览器用js语言解析，js语言也是
        面向对象的，所以希望得到的是一个js对象
        但是java语言和js语言不兼容，无法直接转换
        所以JSON是两边都兼容的，JSON语言是一个具有特定格式的字符串
        Java对象 -> JSON字符串 -> JS语言
     */
    //假设要查询一个员工
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    //返回的是JSON数据，需要加上这个注解
    @ResponseBody
    //DispatcherServlet看到ResponseBody这个注解和返回的类型是Map，会自动把数据转换成JSON字符串
    public Map<String, Object> getEmp(){
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "lsy");
        emp.put("age", 30);
        emp.put("salary", 8000.00);
        return emp;
    }

    //返回多个相似数据时，比如：查询多个员工
    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "lsy");
        emp.put("age", 30);
        emp.put("salary", 8000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "gz");
        emp.put("age", 30);
        emp.put("salary", 9000.00);
        list.add(emp);

        return list;
    }

    //cookie示例
    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //设置cookie生效范围
        cookie.setPath("/community/alpha");
        //设置cookie生效时间：默认情况下浏览器关闭，则cookie消失
        cookie.setMaxAge(60 * 10);//60s * 10
        //发送cookie
        response.addCookie(cookie);

        return "set cookie";
    }
    //浏览器再次向服务器发送请求时，在RequestHeaders中的cookie
    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    //@CookieValue这个注解的意思是，从cookie中获取key为code的cookie的值，存入字符串code中
    public String getCookie(@CookieValue("code") String code){
        System.out.println(code);
        return "get cookie";
    }

    //session示例
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    //Session对象无需自己创建，Spring MVC会自动创建，因此只需声明即可
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        session.getAttribute("id");
        session.getAttribute("name");
        return "get session";
    }

    //ajax示例：处理异步请求
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    //传入的参数：浏览器提交给服务器的信息
    public String testAjax(String name, int age) {
        System.out.println(name);
        System.out.println(age);

        return CommunityUtil.getJSONString(0, "ok");
    }



}
