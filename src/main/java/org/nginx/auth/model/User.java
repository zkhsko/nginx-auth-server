package org.nginx.auth.model;

/**
 * @author dongpo.li
 * @date 2023/12/15
 */
public class User extends AutoIncrementBaseEntity {

    /**
     * 绑定的电子邮箱
     */
    private String email;
    /**
     * 加密后的口令,管理账号和请求服务的唯一凭证
     */
    private String accessKey;
    /**
     * 账号是否被禁用
     */
    private Boolean blocked;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

}
