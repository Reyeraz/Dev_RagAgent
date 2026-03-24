package work.xiaz.ragagent.Repository;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Repository;
import work.xiaz.ragagent.DTO.ChatMessageDTO;
import work.xiaz.ragagent.Mapper.ChatMessageMapper;
import work.xiaz.ragagent.Mapper.SessionMapper;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepository implements ChatMemoryRepository {
    private final SessionMapper sessionMapper;
    private final ChatMessageMapper chatMessageMapper;

    @Override
    /**
     * 获取历史会话ID的数组
     * 返回 limit
     */
    public List<String> findConversationIds() {
        return List.of();
    }

    // TODO 重写数据库逻辑
    @Override
    /**
     * 从ID中获取会话的历史记录
     */
    public List<Message> findByConversationId(String s) {
        return new ArrayList<>(chatMessageMapper.selectBySessionId(s));
    }

    /**
     * 增量追加历史消息
     * 不做实现
     * @param s
     * @param list
     */
    @Override
    public void saveAll(String s, List<Message> list) {

    }

    /**
     * 删除对话, 不实现
     * @param s
     */
    @Override
    public void deleteByConversationId(String s) {

    }
}
