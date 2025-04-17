package model;

import java.math.BigDecimal;

public class product {
    public int product_id;
    public String name;
    public BigDecimal price;
    public int soh ;
    public int category_id;
    private String category_name;

    // Getter / Setter
    public int getProduct_id() { return product_id; }
    public void setProduct_id(int product_id) { this.product_id = product_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getSoh() { return soh; }
    public void setSoh(int soh) { this.soh = soh; }

    public int getCategory_id() { return category_id; }
    public void setCategory_id(int category_id) { this.category_id = category_id; }

    public String getCategory_name() { return category_name; }
    public void setCategory_name(String category_name) { this.category_name = category_name; }
}
