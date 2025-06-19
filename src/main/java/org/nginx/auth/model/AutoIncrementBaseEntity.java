package org.nginx.auth.model;

import com.baomidou.mybatisplus.annotation.*;
import org.apache.ibatis.type.JdbcType;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author dongpo.li
 * @date 2024/1/23 11:26
 */
public abstract class AutoIncrementBaseEntity {

    /**
     * 自增主键
     */
    @TableId(type = IdType.AUTO)
    @TableField(jdbcType = JdbcType.BIGINT)
    private Long id;

//    /**
//     * 创建时间,不变更
//     */
//    @TableField(value = "created_at", jdbcType = JdbcType.DATE)
//    private LocalDateTime createdAt;
//
//    /**
//     * 修改时间,不手动变更,数据库自动变更
//     */
//    @TableField(value = "updated_at", jdbcType = JdbcType.DATE)
//    private LocalDateTime updatedAt;
//
//    /**
//     * 逻辑删除
//     */
//    @TableLogic
//    @TableField(value = "deleted_at", jdbcType = JdbcType.DATE, updateStrategy = FieldStrategy.IGNORED)
//    private LocalDateTime deletedAt;

    @TableField(value = "create_time", jdbcType = JdbcType.DATE)
    private Date createTime;
    @TableField(value = "update_time", jdbcType = JdbcType.DATE)
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
