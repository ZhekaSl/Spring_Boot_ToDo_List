package ua.zhenya.todo.service;

import freemarker.template.Configuration;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ua.zhenya.todo.model.MailType;
import ua.zhenya.todo.model.User;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class MailService {
    private final Configuration configuration;
    private final JavaMailSender mailSender;

    @Async
    public void sendMail(User user, MailType mailType, Properties properties) {
        switch (mailType) {
            case REGISTER -> sendRegistrationEmail(user, properties);
            case REMINDER -> sendReminderEmail(user, properties);
        }
    }

    @SneakyThrows
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
        model.put("title", properties.getProperty("task.title"));
        model.put("description", properties.getProperty("task.description"));
        configuration.getTemplate("reminder.ftlh")
                .process(model, stringWriter);

        return stringWriter.getBuffer().toString();
    }
}
