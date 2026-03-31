package work.xiaz.ragagent.Service;

import reactor.core.publisher.Flux;
import work.xiaz.ragagent.DTO.MessageDTO;
import work.xiaz.ragagent.Entity.Session;
import work.xiaz.ragagent.VO.SessionVO;

import java.util.List;

public interface ChatService {


    /**
     * 从会话id中获取历史消息
     * @param sessionId
     * @return
     */
    List<MessageDTO> getHistoryMessageBySessionId(String sessionId, String userId);

    /**
     * 发送一条消息, 并得到对应大模型的回应
     * @param messageDTO
     * @return
     */
    Flux<String> sendMessage(MessageDTO messageDTO, String sessionId);

    /**
     * 得到一个用户的所有历史会话
     * @param userId
     * @return
     */
    List<SessionVO> getTotalSessionByUserId(String userId);

    /**
     * 创建一个新的对话, 并可能附带有消息
     * @param userId
     * @param messageDTO
     * @return
     */
    Session createSession(String userId, MessageDTO messageDTO);
}
