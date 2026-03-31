package work.xiaz.ragagent.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import work.xiaz.ragagent.DTO.MessageDTO;
import work.xiaz.ragagent.Entity.Session;
import work.xiaz.ragagent.Service.ChatService;
import work.xiaz.ragagent.VO.SessionVO;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    /**
     * 发送一条消息, 并返回大模型的一条对话
     * @param messageDTO
     * @return
     */
    @PostMapping(path = "/{userId}/session/{sessionId}", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> sendMessage(@RequestBody MessageDTO messageDTO, @PathVariable("userId") String userId, @PathVariable("sessionId") String sessionId) {
        Flux<String> message = chatService.sendMessage(messageDTO, sessionId);
        if (message == null) {
            throw new RuntimeException("消息为空");
        }
        return message;
    }


    /**
     * 根据传来的 ID 获取历史记录
     * @param sessionId 用户的会话id
     */
    @GetMapping("/{userId}/session/{sessionId}")
    public ResponseEntity<List<MessageDTO>> getHistoryMessageBySessionId(@PathVariable("sessionId") String sessionId, @PathVariable String userId){
        // TODO 传递这个 Id 给 Service
        return ResponseEntity.ok(chatService.getHistoryMessageBySessionId(sessionId, userId));
    }

    /**
     * 根据 UserId 获取他的所有历史对话记录
     * @param userId
     */
    @GetMapping("/{userId}/session")
    public ResponseEntity<List<SessionVO>> getTotalSessionByUserId(@PathVariable("userId") String userId){
        List<SessionVO> sessions = chatService.getTotalSessionByUserId(userId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * 为指定用户建立一个新的对话, 并可能附带第一条消息
     * * @param userId
     * @return
     */
    @PostMapping("/{userId}/session")
    public ResponseEntity<Session> createSession(@PathVariable String userId, @RequestBody(required = false) MessageDTO messageDTO) {
        Session session = chatService.createSession(userId, messageDTO);
        return ResponseEntity.ok(session);
    }
}
