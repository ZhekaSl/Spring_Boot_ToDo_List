package ua.zhenya.todo.service.mail;

import freemarker.template.Configuration;
import ua.zhenya.todo.model.MailType;
import ua.zhenya.todo.model.User;

import java.util.Properties;

public interface MailStrategy {
    String setSubject(Properties properties);
    String setContent(Properties properties);
    MailType getMailType();
}
