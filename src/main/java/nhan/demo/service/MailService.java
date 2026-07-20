package nhan.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String mailFrom;

    public String sendEmail(String recipient, String subject, String content, MultipartFile[] files) throws MessagingException, UnsupportedEncodingException {
        log.info("Sennding...");

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true,"UTF-8");
        mimeMessageHelper.setFrom(mailFrom, "Nhan");
        if (recipient.contains(",")){
            mimeMessageHelper.setTo(InternetAddress.parse(recipient));
        }else {
            mimeMessageHelper.setTo(recipient);
        }


        if (files != null) {
            for (MultipartFile file : files) {
                mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);

            }
        }

        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true);
        mailSender.send(mimeMessage);
        log.info("Sent email successfully, recipient={}", recipient);
        return "Sent";
    }


}
