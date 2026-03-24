package work.xiaz.ragagent.constant;

import lombok.Getter;

@Getter
public enum SessionEnum {
    ACTIVE(1, "活动会话"),
    CLOSED(2, "会话已关闭");




    private final int code;
    private final String description;
    private SessionEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

}
