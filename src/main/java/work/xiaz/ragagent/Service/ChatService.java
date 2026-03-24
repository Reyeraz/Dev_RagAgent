package work.xiaz.ragagent.Service;

import work.xiaz.ragagent.DTO.ChatMessageDTO;
import work.xiaz.ragagent.Entity.Session;
import work.xiaz.ragagent.VO.SessionVO;

import java.util.List;

public interface ChatService {


    /**
     * 从会话id中获取历史消息
     * @param sessionId
     * @return
     */
    List<ChatMessageDTO> getHistoryMessageBySessionId(String sessionId, String userId);

    /**
     * 发送一条消息, 并得到对应大模型的回应
     * @param chatMessageDTO
     * @return
     */
    ChatMessageDTO sendMessage(ChatMessageDTO chatMessageDTO, String sessionId);

    /**
     * 得到一个用户的所有历史会话
     * @param userId
     * @return
     */
    List<SessionVO> getTotalSessionByUserId(String userId);

    /**
     * 创建一个新的对话, 并可能附带有消息
     * @param userId
     * @param chatMessageDTO
     * @return
     */
    Session createSession(String userId, ChatMessageDTO chatMessageDTO);
}
