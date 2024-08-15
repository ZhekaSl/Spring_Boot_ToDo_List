package ua.zhenya.todo.service.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua.zhenya.todo.model.MailType;
import ua.zhenya.todo.model.User;

import java.util.Properties;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private final MailStrategyFactory mailStrategyFactory;

    @Async
    public void sendMail(String email, MailType mailType, Properties properties) {
        MailStrategy strategy = mailStrategyFactory.getMailStrategy(mailType);
        String subject = strategy.setSubject(properties);
        String content = strategy.setContent(properties);
        sendEmail(email, subject, content);
    }

    @SneakyThrows
    private void sendEmail(String email, String subject, String content) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        helper.setSubject(subject);
        helper.setTo(email);
        helper.setText(content, true);
        mailSender.send(mimeMessage);
    }
}
