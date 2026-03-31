package work.xiaz.ragagent.Service;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import work.xiaz.ragagent.DTO.MessageDTO;

import java.io.IOException;
import java.util.List;

public interface RAGService {
    List<MessageDTO> getHistoryMessageBySessionId(String sessionId);

    List<String> searchDocuments(String query);


    /**
     * 上传文档
     * @param resources
     */
    void uploadDocument(List<Resource> resources) throws IOException;
}
