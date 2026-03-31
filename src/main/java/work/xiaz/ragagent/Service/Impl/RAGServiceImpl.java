package work.xiaz.ragagent.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.codelibs.jhighlight.tools.FileUtils;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.aot.PdfReaderRuntimeHints;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import work.xiaz.ragagent.DTO.MessageDTO;
import work.xiaz.ragagent.Mapper.SessionMapper;
import work.xiaz.ragagent.Service.RAGService;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RAGServiceImpl implements RAGService {
    private final SessionMapper sessionMapper;
    private final JdbcChatMemoryRepository jdbcChatMemoryRepository;
    private final VectorStore vectorStore;

    /**
     * 直接获取指定会话的历史记录
     * @param sessionId 会话id
     * @return
     */
    @Override
    public List<MessageDTO> getHistoryMessageBySessionId(String sessionId) {
        List<Message> messages = jdbcChatMemoryRepository.findByConversationId(sessionId);
        List<MessageDTO> messageDTOList = new ArrayList<>();
        for (Message message : messages) {
            messageDTOList.add(new MessageDTO(message.getText(), message.getMessageType(), null));
        }
        return messageDTOList;
    }


    /**
     * 输入一串文本, 返回匹配度最高的数个文本
     * @param query 需要匹配的文本
     * @return
     */
    @Override
    public List<String> searchDocuments(String query) {
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query) // 需要匹配的话
                .topK(10) // TODO 应在配置文件内写
                .build();
        List<Document> documents = vectorStore.similaritySearch(searchRequest);
        return documents.stream().map(Document::getText).toList();
    }

    /**
     * 通过传入的文件作为
     * @param resources
     */
    @Override
    public void uploadDocument(List<Resource> resources) throws IOException {
        List<Document> documents = new ArrayList<>(); // 预备添加的 Chunking
        for (Resource resource : resources) {
            
            List<Document> docs = new TikaDocumentReader(resource).get();
            documents.addAll(docs);

            // 尽量分批插入, 防止 Out Of Memory
            if (documents.size() >= 100){ // TODO 这里需要做配置类. 防止写死
                vectorStore.add(documents);
                documents.clear(); // 清理
            }
        }
        if (!documents.isEmpty()){
            vectorStore.add(documents);
        }
    }
}
