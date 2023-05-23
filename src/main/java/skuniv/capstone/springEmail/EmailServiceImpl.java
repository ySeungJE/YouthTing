package skuniv.capstone.springEmail;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService{
 
    @Autowired
    JavaMailSender emailSender;
    private MimeMessage createMessage(String to)throws Exception{
        MimeMessage  message = emailSender.createMimeMessage();
 
        message.addRecipients(Message.RecipientType.TO, to);//보내는 대상
        message.setSubject("사용자 인증 메일");//제목
 
        String msgg="";
        msgg+= "<div style='margin:20px;'>";
        msgg+= "<h1>YouthTing 학교인증</h1>";
        msgg+= "<br>";
        msgg+= "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg+= "<h3 style='color:blue;'>고유 코드를 붙여넣어 주세요</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg+= "</div>";
        msgg += "<form action='http://localhost:8080/user/confirm' method='POST'>";
        msgg += "<div class='form-group'>";
        msgg += "<input type='text' class='form-control' id='code' name='code'>";
        msgg+="</div>";
        msgg += "<button type='submit' >인증하기</button>";
        msgg += "</form>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("2017301050@skuniv.ac.kr","YouthTing"));//보내는 사람

        return message;
    }
    @Override
    public String sendSimpleMessage(String to)throws Exception {
        // TODO Auto-generated method stub
        MimeMessage message = createMessage(to);
        try{//예외처리
            emailSender.send(message);
        }catch(MailException es){
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        log.info("이메일 전송 완료");
        return null;
    }
}