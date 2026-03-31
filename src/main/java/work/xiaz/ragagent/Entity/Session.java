package work.xiaz.ragagent.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import work.xiaz.ragagent.constant.SessionEnum;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
@TableName("session")
public class Session {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String userId; // 所属用户的名字
    private SessionEnum status; // 当前会话状态 使用 SessionConstant常量表明
    private String sessionTitle; // 会话的标题
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
