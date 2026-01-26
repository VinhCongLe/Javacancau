package shopbancancau.model;

public class Product {
    private int productId;
    private String productName;
    private double price;
    private int quantity;
    private String category;

    // getter & setter
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

    // QUAN TRỌNG: để ComboBox hiển thị tên
    @Override
    public String toString() {
        return productName;
    }
}
