package model;

import java.time.LocalDateTime;

public class cart {
    private int member_id;
    private int product_id;
    private int quantity;
    private LocalDateTime create_at;

    // Getters & Setters
    public int getMember_id() { return member_id; }
    public void setMember_id(int member_id) { this.member_id = member_id; }

    public int getProduct_id() { return product_id; }
    public void setProduct_id(int product_id) { this.product_id = product_id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public LocalDateTime getCreate_at() { return create_at; }
    public void setCreate_at(LocalDateTime create_at) { this.create_at = create_at; }
}
