package com.school.community.controller;

import com.school.community.entity.DiscussPost;
import com.school.community.entity.Page;
import com.school.community.entity.User;
import com.school.community.service.DiscussPostService;
import com.school.community.service.LikeService;
import com.school.community.service.UserService;
import com.school.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    //方法调用前，SpringMVC会自动实例化Model和Page，并将Page注入Model
    //所以在thymeleaf中可以直接访问page中的数据，不需要再model.addAttribute(page)了
    public String getIndex(Model model, Page page) {
        //数据库总行数
        page.setRows(discussPostService.findDiscussRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        //新建一个Map对象，用于存放findDiscussPosts返回的值和用户名
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null){
            //当集合list的值不为null，遍历此集合并获取数据
            for (DiscussPost post:list){
                Map<String, Object> map = new HashMap<>();
                //将查询到的discussPost对象放入map中
                map.put("post", post);
                //根据ID查询UserId
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);

                //帖子赞的数量
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
        }
        //将得到的数据封装到model中
        model.addAttribute("discussPosts", discussPosts);
        //返回的是模板的路径 /templates/index.html
        return "/index";
    }

    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }


}
