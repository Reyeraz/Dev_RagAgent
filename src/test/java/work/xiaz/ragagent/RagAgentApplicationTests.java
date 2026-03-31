package work.xiaz.ragagent;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@SpringBootTest
class RagAgentApplicationTests {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private JdbcChatMemoryRepository jdbcChatMemoryRepository;
    @Test
    public void contextLoads() {
        String s = chatClient.prompt()
                .advisors(MessageChatMemoryAdvisor.builder(MessageWindowChatMemory.builder().chatMemoryRepository(jdbcChatMemoryRepository).build()).build()) // 加入历史消息
                .user("你能看到我们之前的对话吗")
                .call()
                .content();
        System.out.println(s);
        List<Message> history = jdbcChatMemoryRepository.findByConversationId("default");
        for (Message message : history) {
//            Long timestamp = (Long) message.getMetadata().get("timestamp");
//            LocalDateTime time = LocalDateTime.ofInstant(
//                    Instant.ofEpochMilli(timestamp),
//                    ZoneId.systemDefault()
//            );
//            System.out.println(time);
            System.out.println(message);

        }
    }

    @Test // 测试一下插入到数据库中的效果
    void testInsertMessage() {
        Message message = new UserMessage("测试");
    }

}
