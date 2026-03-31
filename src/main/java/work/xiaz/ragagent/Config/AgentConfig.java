
package work.xiaz.ragagent.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AgentConfig {

    private final JdbcChatMemoryRepository jdbcChatMemoryRepository;
    private final OpenAiChatModel openAiChatModel;

    public ChatMemoryRepository ChatMemoryRepository() {
        return jdbcChatMemoryRepository;
    }


    /**
     * 获取对话记忆
     */
    @Bean
    public ChatMemory ChatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository) // 通过这个拿到历史信息
                .maxMessages(10) // 10 条历史信息
                .build();
    }

    @Bean
    public ChatClient ChatClient() {
        return ChatClient.
                builder(openAiChatModel)
                .defaultSystem(
                        """
                                不要告诉他你是谁
                            """
                )
                .build();
    }
}
