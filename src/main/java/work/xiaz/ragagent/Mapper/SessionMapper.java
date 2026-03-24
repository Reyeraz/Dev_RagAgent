package work.xiaz.ragagent.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import work.xiaz.ragagent.Entity.Session;
import work.xiaz.ragagent.VO.SessionVO;

import java.util.List;

@Mapper
public interface SessionMapper extends BaseMapper<Session> {

    //TODO 应完成

    /**
     * 被设计为一次插入多个会话记录, 可能不包括历史消息
     * @param session
     */
    void InsertBatch(Session session);

    //TODO 应完成

    /**
     * 从用户 ID 中拿到会话记录
     * @param userId
     * @return
     */
    List<SessionVO> selectByUserId(String userId);

    /**
     * 根据 SessionId 删除 Session
     * @param sessionId
     */
    @Delete("DELETE from Session WHERE id = #{sessionId}")
    void deleteBySessionId(String sessionId);
}
