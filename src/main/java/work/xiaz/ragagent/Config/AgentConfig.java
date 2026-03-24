
package work.xiaz.ragagent.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.model.Model;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import work.xiaz.ragagent.Repository.ChatMessageRepository;

@Configuration
@RequiredArgsConstructor
public class AgentConfig {

    private final ChatMessageRepository chatMessageRepository;

    @Bean
    /**
     * 获取对话记忆
     */
    public ChatMemory ChatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository) // 通过这个拿到历史信息
                .maxMessages(10) // 10 条历史信息
                .build();
    }

    @Bean
    public ChatClient ChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.
                builder(openAiChatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor
                                .builder(ChatMemory(chatMessageRepository))
                                .build())
                .build();
    }
}
