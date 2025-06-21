package model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class order {
    private int order_id;
    private int member_id;
    private LocalDateTime create_at;
    private List<order_detail> orderDetails;

    public int getOrder_id() { return order_id; }
    public void setOrder_id(int order_id) { this.order_id = order_id; }

    public int getMember_id() { return member_id; }
    public void setMember_id(int member_id) { this.member_id = member_id; }

    public LocalDateTime getCreate_at() { return create_at; }
    public void setCreate_at(LocalDateTime create_at) { this.create_at = create_at; }

    public List<order_detail> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<order_detail> orderDetails) { this.orderDetails = orderDetails; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof order)) return false;
        order other = (order) o;
        return order_id == other.order_id;
    }
    @Override
    public int hashCode() { return Objects.hash(order_id); }
}
