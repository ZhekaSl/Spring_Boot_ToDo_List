package ua.zhenya.todo.service.mail;

import freemarker.template.Configuration;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ua.zhenya.todo.model.MailType;
import ua.zhenya.todo.model.User;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class RegistrationMailStrategy implements MailStrategy {
    private final Configuration configuration;

    @Override
    public String setSubject(Properties properties) {
        User user = (User) properties.get("user");
        return "Thank you for your registration, " + user.getFirstname() + "!";
    }

    @SneakyThrows
    @Override
    public String setContent(Properties properties) {
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        User user = (User) properties.get("user");
        model.put("firstname", user.getFirstname());
        configuration.getTemplate("register.ftlh")
                .process(model, stringWriter);

        return stringWriter.getBuffer().toString();
    }

    @Override
    public MailType getMailType() {
        return MailType.REGISTRATION;
    }
}
