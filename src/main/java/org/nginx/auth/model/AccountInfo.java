package org.nginx.auth.model;

/**
 * @author dongpo.li
 * @date 2023/12/15
 */
public class AccountInfo extends AutoIncrementBaseEntity {

    /**
     * 绑定的电子邮箱
     */
    private String email;
    /**
     * 加密后的密码
     */
    private String license;
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

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

}
