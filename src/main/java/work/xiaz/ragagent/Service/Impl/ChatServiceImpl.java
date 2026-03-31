
package work.xiaz.ragagent.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AdvisorUtils;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import work.xiaz.ragagent.Config.PromptConfig;
import work.xiaz.ragagent.DTO.MessageDTO;
import work.xiaz.ragagent.Entity.Session;
import work.xiaz.ragagent.Mapper.SessionMapper;
import work.xiaz.ragagent.Service.ChatService;
import work.xiaz.ragagent.Service.RAGService;
import work.xiaz.ragagent.VO.SessionVO;
import work.xiaz.ragagent.constant.SessionEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMemory chatMemory;
    private final ChatClient chatClient;
    private final JdbcChatMemoryRepository jdbcChatMemoryRepository;
    private final SessionMapper sessionMapper;
    private final PromptConfig promptConfig;
    private final RAGService ragService;
    private final VectorStore vectorStore;


    public List<MessageDTO> getHistoryMessageBySessionId(String sessionId, String userId) {
        // 要先查出用户是否持有这个会话id
        Session session = sessionMapper.selectByUserIdAndSessionId(sessionId, userId);
        if (session == null) {
            throw new RuntimeException("会话不属于这个用户");
        }
        // 如果持有, 则继续查
        List<Message> mes = jdbcChatMemoryRepository.findByConversationId(sessionId);
        if (mes.isEmpty()) {
            return List.of();
        }
        List<MessageDTO> messages = new ArrayList<>();
        for (Message m : mes) {
            messages.add(new MessageDTO(m.getText(), m.getMessageType(), m.getMetadata()));
        }
        // 复制出类
        return messages;
    }

    /**
     * 发送一条消息, 返回值为大模型的答复
     *
     * @param messageDTO 前端传输过来的DTO
     * @return
     */
    public Flux<String> sendMessage(MessageDTO messageDTO, String sessionId) {
        if (messageDTO == null || messageDTO.getContent() == null || messageDTO.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("聊天消息不能为空");
        }
        Message userMessage = new UserMessage(messageDTO.getContent()); // 转换为 userMessage

        // 第一次对话时, 更新摘要
        // 条件, 当获取的历史记录为空
        if (chatMemory.get(sessionId).isEmpty()) {
            String summary = summarizeMessage(userMessage);//  获取摘要
            Session session = sessionMapper.selectById(sessionId);
            session.setSessionTitle(summary);
            sessionMapper.updateById(session); // 更新摘要
        }
        Prompt prompt = new Prompt(userMessage);

        // 尝试进行回复, 历史记录已在配置类给出
        Flux<String> message = chatClient
                .prompt(prompt)
                .advisors(
                        // 向量搜索模块 TODO 可能需要重做
                        QuestionAnswerAdvisor.builder(vectorStore).searchRequest( // TODO 可以选择继续实现增强
                                SearchRequest.builder()
                                        .similarityThreshold(0.6d) // 相似度阈值
                                        .topK(3) // 返回的文档片段的最大值 TODO 需要在配置类设置召回程度
                                        .build()
                        ).build(),
                        // 记忆模块
                        MessageChatMemoryAdvisor
                                .builder(chatMemory)
                                .conversationId(sessionId)
                                .build()
                )
                .stream()
                .content();

        return message;
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
    public Session createSession(String userId, MessageDTO messageDTO) {
        String title = "新对话";
        if (messageDTO != null &&
            messageDTO.getContent() != null  &&
            !messageDTO.getContent().isEmpty() &&
            messageDTO.getType().name().equalsIgnoreCase(MessageType.USER.name())) {

            // TODO 这里应该由大模型做一次标题摘要
            title = summarizeMessage(new UserMessage(messageDTO.getContent())) ;
        }
        Session session = Session.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .sessionTitle(title)
                .status(SessionEnum.ACTIVE)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        // 使用MyBatis Plus的insert方法插入单条记录，更可靠
        sessionMapper.insert(session);
        if (!title.equalsIgnoreCase("新对话")) {
            this.sendMessage(messageDTO, session.getId());
        }
        return session;
    }

    /**
     * 对传递过来的 message 做一个缩短总结
     * @param message 需要总结的消息
     * @return
     */
    private String summarizeMessage(Message message){
        return chatClient
                .prompt(new Prompt(message))
                .system(promptConfig.load(PromptConfig.SUMMARIZE)) // 系统提示词
                .call()
                .content();

    }

}
