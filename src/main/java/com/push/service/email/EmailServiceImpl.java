package com.push.service.email;

import com.push.entity.Message;
import com.push.enums.EmailTypeEnum;
import com.push.service.EmailService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.JdkIdGenerator;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @author xhzy
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String from;

    @Value("${spring.mail.subject}")
    private String title;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void send(Message message) {
        sendTemplateEmail(message);
    }

    public void sendTextEmail(){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo("toEmail");
        msg.setText("content");
        msg.setSubject(title);
        mailSender.send(msg);
    }

    @SneakyThrows
    public void sendHtmlEmail(Message message,String... type){
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom(from);
        //get email by receiverId
        helper.setTo("");
        helper.setText(message.getBody(),true);
        helper.setSubject(title);

        if(type.length > 0){
            FileSystemResource resource = new FileSystemResource(new File("path"));
            if(EmailTypeEnum.ATTACHMENT.getType().equals(type[0])){
                helper.addAttachment(resource.getFilename(),resource);
            }else if(EmailTypeEnum.IMAGE.getType().equals(type[0])){
                String contentId = new JdkIdGenerator().generateId().toString().replaceAll("-","");
                helper.setText("<html><img src=\'cid:"+contentId+"\'/></html>",true);
                helper.addInline(contentId,resource);
            }
        }
        mailSender.send(msg);
    }

    public void sendTemplateEmail(Message message){
        Context context = new Context();
        context.setVariable("id",message.getReceiverId());
        context.setVariable("content",message.getBody());
        message.setBody(templateEngine.process(message.getBizType().getTemplateName(), context));
        sendHtmlEmail(message);
    }
}
