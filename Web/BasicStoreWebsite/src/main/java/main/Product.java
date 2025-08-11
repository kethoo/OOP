package main;

import java.math.BigDecimal;

public class Product {
    private String productId;
    private String name;
    private String imageFile;
    private BigDecimal price;

    public Product(String productId, String name, String imageFile, BigDecimal price) {
        this.productId = productId;
        this.name = name;
        this.imageFile = imageFile;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}