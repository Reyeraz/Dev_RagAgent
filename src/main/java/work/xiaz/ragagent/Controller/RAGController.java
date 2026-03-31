package work.xiaz.ragagent.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import work.xiaz.ragagent.DTO.MessageDTO;
import work.xiaz.ragagent.Service.ChatService;
import work.xiaz.ragagent.Service.RAGService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RAG Controller
 * 用于上传知识给向量数据库
 * 支持上传选定的片段, 包括会话记录
 */
@RestController
@RequestMapping("/RAG")
@RequiredArgsConstructor
public class RAGController {
    private final JdbcChatMemoryRepository jdbcChatMemoryRepository;
    private final RAGService ragService;
    /**
     * 直接获取一个  sessionId , 应跳过检查
     * @param sessionId
     * @return
     */
    @GetMapping("/{sessionId}")
    public List<MessageDTO> getHistoryMessageBySessionId(@PathVariable String sessionId) {
        // 获取历史记录
        return ragService.getHistoryMessageBySessionId(sessionId);
    }

    /**
     * 上传信息, 在前端层面, 应将获取到的历史记录聚合成text或者json文档供chunking分割
     * @param files
     * @return
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFiles(@RequestParam("files") List<MultipartFile> files) throws IOException {
        if (files.isEmpty()) {
            return ResponseEntity.badRequest().body("文件不能为空");
        }
        List<Resource> resources = files.stream().map(file -> {
            try {
                return (Resource) new InputStreamResource(file.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();


        ragService.uploadDocument(resources);
        return ResponseEntity.ok("完成");
    }
}
