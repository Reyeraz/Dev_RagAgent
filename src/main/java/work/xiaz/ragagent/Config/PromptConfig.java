package work.xiaz.ragagent.Config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置类
 */
@Component
@RequiredArgsConstructor
public class PromptConfig {
    private final ResourceLoader resourceLoader; // 资源加载器
    private final Map<String, String> prompts = new ConcurrentHashMap<>();

    public static final String SUMMARIZE = "classpath:prompts/summarize.prompt";
    /**
     * 预加载配置
     */
    @PostConstruct
    public void init() {
        load(SUMMARIZE);
    }

    // 加载并缓存
    public String load(String location) {
        return prompts.computeIfAbsent(location, this::readResource);
    }

    // 热重载：清空缓存，下次自动重新加载
    public void reload(String location) {
        prompts.remove(location);
    }

    // 读取文件
    private String readResource(String location) {
        try {
            Resource resource = resourceLoader.getResource(location);
            return new String(resource.getInputStream().readAllBytes()).trim();
        } catch (Exception e) {
            throw new RuntimeException("加载提示词失败: " + location, e);
        }
    }
}
