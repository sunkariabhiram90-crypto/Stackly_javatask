package com;

import java.math.BigDecimal;

public class Product {
    private int productId;
    private String productName;
    private String sku;
    private String category;
    private int supplierId;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private int stockQuantity;
    private int reorderLevel;
    private String unit;
    private String status;

    public Product(int productId, String productName, String sku, String category, int supplierId,
                   BigDecimal purchasePrice, BigDecimal sellingPrice, int stockQuantity,
                   int reorderLevel, String unit, String status) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.category = category;
        this.supplierId = supplierId;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
        this.reorderLevel = reorderLevel;
        this.unit = unit;
        this.status = status;
    }

    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getSku() { return sku; }
    public String getCategory() { return category; }
    public int getSupplierId() { return supplierId; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public int getReorderLevel() { return reorderLevel; }
    public String getUnit() { return unit; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("%-5d %-22s %-13s %-15s %-8d %-10s %-10s %-7d %-7d %-8s %-8s",
                productId, productName, sku, category, supplierId, purchasePrice,
                sellingPrice, stockQuantity, reorderLevel, unit, status);
    }
}
