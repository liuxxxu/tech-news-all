package com.liuxu.model.behavior.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * APP不喜欢行为表
 * </p>
 *
 */
@Data
@Document("ap_unlikes_behavior")
public class AppUnlikesBehavior implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    /**
     * 实体ID
     */
    private String entryId;
    /**
     * 文章ID
     */
    private Long articleId;
    /**
     * 0 不喜欢
     * 1 取消不喜欢
     */
    private Short type;
    /**
     * 登录时间
     */
    private Date createdTime;

    // 定义不喜欢操作的类型
    public enum Type {
        UNLIKE((short) 0), CANCEL((short) 1);

        short code;

        Type(short code) {
            this.code = code;
        }

        public short getCode() {
            return this.code;
        }
    }
}