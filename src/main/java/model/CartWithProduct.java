package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartWithProduct {
    private int member_id;
    private int product_id;
    private int quantity;
    private LocalDateTime create_at;
    
    // Product information
    private String product_name;
    private BigDecimal product_price;
    private String image_url;
    
    // Constructors
    public CartWithProduct() {}
    
    // Getters and Setters
    public int getMember_id() { return member_id; }
    public void setMember_id(int member_id) { this.member_id = member_id; }
    
    public int getProduct_id() { return product_id; }
    public void setProduct_id(int product_id) { this.product_id = product_id; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public LocalDateTime getCreate_at() { return create_at; }
    public void setCreate_at(LocalDateTime create_at) { this.create_at = create_at; }
    
    public String getProduct_name() { return product_name; }
    public void setProduct_name(String product_name) { this.product_name = product_name; }
    
    public BigDecimal getProduct_price() { return product_price; }
    public void setProduct_price(BigDecimal product_price) { this.product_price = product_price; }
    
    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }
} 