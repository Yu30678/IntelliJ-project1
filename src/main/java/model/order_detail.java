package model;

import java.math.BigDecimal;
import java.util.Objects;

public class order_detail {
    private int order_id;
    private int product_id;
    private int quantity;
    private BigDecimal price;

    public int getOrder_id() { return order_id; }
    public void setOrder_id(int order_id) { this.order_id = order_id; }

    public int getProduct_id() { return product_id; }
    public void setProduct_id(int product_id) { this.product_id = product_id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof order_detail)) return false;
        order_detail other = (order_detail) o;
        return order_id == other.order_id && product_id == other.product_id;
    }
    @Override
    public int hashCode() { return Objects.hash(order_id, product_id); }
}