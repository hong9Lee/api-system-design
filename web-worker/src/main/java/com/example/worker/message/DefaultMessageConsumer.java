package com.example.worker.message;

import com.example.shared.message.DefaultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.function.Consumer;

@Slf4j
@Component("defaultBroadcastMessageConsumer")
public class DefaultMessageConsumer implements Consumer<DefaultMessage> {
    @Override
    public void accept(DefaultMessage defaultMessage) {
        log.info("DefaultMessageConsumer uuid:{}, type:{}", defaultMessage.getUuid(), defaultMessage.getMessageType());
    }
}
