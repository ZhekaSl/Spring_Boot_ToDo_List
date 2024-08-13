package ua.zhenya.todo.service.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.zhenya.todo.model.MailType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MailStrategyFactory {
    private final List<MailStrategy> mailStrategies;

    public MailStrategy getMailStrategy(MailType mailType) {
        return mailStrategies.stream()
                .filter(strategy -> strategy.getMailType() == mailType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported mail type: " + mailType));
    }
}
