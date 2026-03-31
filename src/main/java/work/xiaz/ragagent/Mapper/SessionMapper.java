package work.xiaz.ragagent.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import work.xiaz.ragagent.Entity.Session;
import work.xiaz.ragagent.VO.SessionVO;

import java.util.List;

@Mapper
public interface SessionMapper extends BaseMapper<Session> {

    //TODO 应完成

    /**
     * 被设计为一次插入多个会话记录, 可能不包括历史消息
     * @param sessions 需要插入的对话
     */
    void insertBatch(List<Session> sessions);

    /**
     * 从用户 ID 中拿到会话记录
     * @param userId
     * @return
     */
    @Select("SELECT * from session where user_id = #{userId}")
    List<SessionVO> selectByUserId(String userId);

    /**
     * 查询用户是否持有某个会话
     * @param sessionId
     * @param userId
     * @return
     */
    @Select(("SELECT * from session where id = #{sessionId} AND user_id = #{userId}"))
    Session selectByUserIdAndSessionId(String sessionId, String userId);
}
