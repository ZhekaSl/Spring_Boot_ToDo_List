package ua.zhenya.todo.events.event;


import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import ua.zhenya.todo.model.User;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {
    private final User user;
    public UserRegisteredEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
