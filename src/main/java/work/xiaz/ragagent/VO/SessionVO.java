package work.xiaz.ragagent.VO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import work.xiaz.ragagent.constant.SessionEnum;

import java.time.LocalDateTime;

/**
 * 会话表现类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionVO {
    private String id;
    private String sessionTitle;
    private SessionEnum status;
    private LocalDateTime updateTime;
}
