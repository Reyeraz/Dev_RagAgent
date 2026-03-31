package work.xiaz.ragagent.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import work.xiaz.ragagent.DTO.MessageDTO;
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
    @Select("SELECT * FROM chat_message where session_id = #{sessionId} ORDER BY create_time ASC")
    List<MessageDTO> selectBySessionId(String sessionId);
}
