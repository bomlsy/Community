package com.school.community.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/*
    发送邮件工具类
 */
@Component
public class MailClient {
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);
    //注入JavaMailSender（这个类也是被Spring容器管理的）
    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content) {
        //创建一个MimeMessage对象
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =  new MimeMessageHelper(message);
        try {
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            //加上true这个参数表明，这个文本可以是html的格式
            helper.setText(content,true);
            //把内容发送出去
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败："+e.getMessage());
        }

    }
}
