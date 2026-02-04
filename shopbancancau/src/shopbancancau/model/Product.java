package shopbancancau.model;

public class Product {
    private int productId;
    private String productName;
    private double price;
    private int quantity;
    private String category;          
    private String description;       
    private String supplier;          
    private String imagePath;         

    
    public Product() {}

    
    public Product(int productId, String productName, double price, int quantity,
                   String category, String description, String supplier, String imagePath) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.description = description;
        this.supplier = supplier;
        this.imagePath = imagePath;
    }

  
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    
    @Override
    public String toString() {
        return productName + " (" + category + " - " + String.format("%,.0f", price) + "đ)";
    }
}