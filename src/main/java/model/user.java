package model;

public class user {
    private int userId;
    private String name;
    private String password;
    private String account;
    private int level;

    // 构造函数


    public user(int userId, String name, String password, String account, int level) {
        this.userId = userId;
        this.name = name;
        this.password = password;
        this.account = account;
        this.level = level;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
