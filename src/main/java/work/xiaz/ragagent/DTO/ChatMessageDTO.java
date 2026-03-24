package work.xiaz.ragagent.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;

import java.time.LocalDateTime;
import java.util.Map;

/**
 *  传递消息的类
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatMessageDTO implements Message {
    private String textContent;
    private MessageType role;
    private Map<String, Object> metaData;
    private LocalDateTime createTime;

    @Override
    public MessageType getMessageType() {
        return role;
    }

    @Override
    public @Nullable String getText() {
        return textContent;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metaData;
    }
}
