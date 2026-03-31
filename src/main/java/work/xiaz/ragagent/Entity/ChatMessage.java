package work.xiaz.ragagent.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;
import work.xiaz.ragagent.Handler.JsonbTypeHandler;

import java.time.LocalDateTime;
import java.util.Map;

/*
    信息类, 用于存储单条对话信息
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatMessage {
    // 使用雪花算法
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String sessionId; // 存储对话 id
    private String textContent;
    private MessageType role; // 存储对话角色
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> metadata;
    private LocalDateTime createTime;
}
