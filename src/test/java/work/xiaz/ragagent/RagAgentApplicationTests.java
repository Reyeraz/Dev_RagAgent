package work.xiaz.ragagent;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RagAgentApplicationTests {
    @Autowired
    private ChatClient chatClient;

    @Test
    void contextLoads() {
        String s = chatClient.prompt()
                .user("你是谁,能干什么")
                .call()
                .content();
        System.out.println(s);
    }

}
