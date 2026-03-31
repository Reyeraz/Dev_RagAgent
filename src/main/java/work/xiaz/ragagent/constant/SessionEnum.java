package work.xiaz.ragagent.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum SessionEnum {
    ACTIVE("ACTIVE"),
    CLOSED("CLOSED");


    @EnumValue
    @JsonValue
    private final String code;
    SessionEnum(String code) {
        this.code = code;
    }

}
