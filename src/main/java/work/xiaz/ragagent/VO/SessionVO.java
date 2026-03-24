package work.xiaz.ragagent.VO;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 会话表现类
 */
@AllArgsConstructor
@Data
public class SessionVO {
    private String sessionId;
    private String sessionTitle;
    private Integer status;
}
