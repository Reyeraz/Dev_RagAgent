package work.xiaz.ragagent.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import work.xiaz.ragagent.DTO.ChatMessageDTO;
import work.xiaz.ragagent.Entity.ChatMessage;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    /**
     * 批量插入历史记录, 可以没有id
     * @param messages
     */
    void insertMessageBatch(List<ChatMessage> messages);

    /**
     * 根据 SessionId 获取整个对话的历史记录
     * @param sessionId
     * @return
     */
    List<ChatMessageDTO> selectBySessionId(String sessionId);
}
