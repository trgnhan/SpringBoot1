package nhan.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nhan.demo.dto.response.ResponseData;
import nhan.demo.dto.response.ResponseError;
import nhan.demo.service.MailService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/common")
public class CommonController {
    private final MailService mailService;

    @PostMapping("/send-email")
    public ResponseData<String> sentEmail(@RequestParam String recipient, @RequestParam String subject, @RequestParam String content, @RequestParam(required = false) MultipartFile[] files) {
        try {
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), mailService.sendEmail(recipient, subject, content, files));
        }
        catch (Exception e) {
            log.error("Send email error, errorMessage={}", e.getMessage());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Send email error");
        }
    }
}
