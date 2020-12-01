package com.dyzwj.springbootmybatisplugin.po;

/**
 * @author 作者 : ZhengWenjie
 * @version 创建时间：2020/12/1 11:44
 * 类说明
 */
public class User {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public void setPassword(String password) {


        this.password = password;
    }
}
