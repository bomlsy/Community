package com.school.community;

import com.school.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
//在这个测试类中也启用CommunityApplication作为配置类
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username","yhb");

        //调用模板引擎，生成html
        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("754594493@qq.com","htmlTest",content);
    }

    @Test
    public void testMailTest(){
        mailClient.sendMail("754594493@qq.com","Test","Java真的好难");
    }

}
