package model;

import java.time.LocalDateTime;

public class Member {
    private int member_id;
    private String name;
    private String password;
    private String phone;
    private String address;
    private String email;
    private LocalDateTime create_at;


    // Getters and Setters
    public int getMember_id() { return member_id; }
    public void setMember_id(int member_id) { this.member_id = member_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDateTime getCreate_at() { return create_at; }
    public void setCreate_at(LocalDateTime create_at) { this.create_at = create_at; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

}

