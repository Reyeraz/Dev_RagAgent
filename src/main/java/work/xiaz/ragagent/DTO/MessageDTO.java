package work.xiaz.ragagent.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class MessageDTO{
    private String content;
    private MessageType type;
    private Map<String, Object> metaData;
}
