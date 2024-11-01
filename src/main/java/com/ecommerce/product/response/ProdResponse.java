package com.ecommerce.product.response;

public class ProdResponse {
    private Long id;
    private String name;
    private String usp;
    private String description;
    private String file; // URL of the product image
    private Double price;
    private Integer stock;
    private String category;
    private Double rating;
    private Integer ratingCount;

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsp() { return usp; }
    public void setUsp(String usp) { this.usp = usp; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
