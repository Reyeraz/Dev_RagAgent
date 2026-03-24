
package work.xiaz.ragagent.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import work.xiaz.ragagent.DTO.ChatMessageDTO;
import work.xiaz.ragagent.Entity.ChatMessage;
import work.xiaz.ragagent.Entity.Session;
import work.xiaz.ragagent.Mapper.ChatMessageMapper;
import work.xiaz.ragagent.Mapper.SessionMapper;
import work.xiaz.ragagent.Repository.ChatMessageRepository;
import work.xiaz.ragagent.Service.ChatService;
import work.xiaz.ragagent.VO.SessionVO;
import work.xiaz.ragagent.constant.SessionEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;
    private final ChatMessageMapper chatMessageMapper;
    private final SessionMapper sessionMapper;
    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessageDTO> getHistoryMessageBySessionId(String sessionId, String userId) {
        // TODO 要先查出用户是否持有这个会话id

        // TODO 如果持有, 则继续查
        List<ChatMessageDTO> messages = chatMessageMapper.selectBySessionId(sessionId);
        if (messages == null || messages.isEmpty()) {
            return List.of();
        }
        // 复制出类
        return messages;
    }

    /**
     * 发送一条消息, 返回值为大模型的答复
     * @param chatMessageDTO 前端传输过来的DTO
     * @return
     */
    public ChatMessageDTO sendMessage(ChatMessageDTO chatMessageDTO, String sessionId) {
        if (chatMessageDTO == null) {
            // TODO 这里应该抛出异常
            return null;
        }
        String prompt = chatMessageDTO.getTextContent();
        // 尝试进行回复, 历史记录已在配置类给出
        String agentReply = chatClient.prompt()
                .messages()
                .user(prompt)
                .call()
                .content();
        // TODO 应抛出报错
        if (agentReply == null) {}
        // 回复后保存记录到数据库中
        ChatMessage userMessage = ChatMessage.builder()
                .role(MessageType.USER)
                .textContent(prompt)
                .sessionId(sessionId)
                .build();
        ChatMessage agentMessage = ChatMessage.builder()
                .role(MessageType.ASSISTANT)
                .sessionId(sessionId)
                .build();
        List<ChatMessage> messages = List.of(userMessage, agentMessage);

        // TODO 等待实现
        chatMessageMapper.insertMessageBatch(messages);

        ChatMessageDTO agentMessageDTO = new ChatMessageDTO();
        BeanUtils.copyProperties(agentMessage, agentMessageDTO);
        return agentMessageDTO;
    }

    /**
     * 根据 userId 获取所有的Session
     * @param userId
     * @return
     */
    public List<SessionVO> getTotalSessionByUserId(String userId) {
        List<SessionVO> sessions = sessionMapper.selectByUserId(userId);
        if (sessions == null || sessions.isEmpty()) {
            return List.of();
        }
        return sessions;
    }

    @Override
    public Session createSession(String userId, ChatMessageDTO chatMessageDTO) {
        String title = "新对话";
        if (chatMessageDTO != null &&
            chatMessageDTO.getTextContent() != null  &&
            !chatMessageDTO.getTextContent().isEmpty() &&
            chatMessageDTO.getRole().name().equalsIgnoreCase(MessageType.USER.name())) {

            // TODO 这里应该由大模型做一次标题摘要
            title = chatMessageDTO.getTextContent();
        }
        Session session = Session.builder()
                .userId(userId)
                .sessionTitle(title)
                .status(SessionEnum.ACTIVE)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        sessionMapper.InsertBatch(session);
        if (title.equalsIgnoreCase("新对话")) {
            this.sendMessage(chatMessageDTO, session.getId());
        }
        return session;
    }
}
