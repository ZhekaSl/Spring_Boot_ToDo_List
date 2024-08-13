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

/*    @SneakyThrows
    private void sendRegistrationEmail(User user, Properties properties) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        helper.setSubject("Thank you for registration, " + user.getFirstname() + "!");
        helper.setTo(user.getEmail());
        String emailContent = getRegisterEmailContent(user, properties);
        helper.setText(emailContent, true);
        mailSender.send(mimeMessage);
    }

    @SneakyThrows
    private void sendReminderEmail(User user, Properties properties) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        helper.setSubject("You have tasks to do in 1 hour!");
        helper.setTo(user.getEmail());
        String emailContent = getReminderEmailContent(user, properties);
        helper.setText(emailContent, true);
        mailSender.send(mimeMessage);
    }

    @SneakyThrows
    private String getRegisterEmailContent(User user, Properties properties) {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("firstname", user.getFirstname());
        configuration.getTemplate("register.ftlh")
                .process(model, stringWriter);

        return stringWriter.getBuffer().toString();
    }

    @SneakyThrows
    private String getReminderEmailContent(User user, Properties properties) {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("firstname", user.getFirstname());
        model.put("tasks", properties.get("tasks"));
        configuration.getTemplate("reminder.ftlh")
                .process(model, stringWriter);

        return stringWriter.getBuffer().toString();
    }*/
}
