package com;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Sale {
    private long saleId;
    private String invoiceNumber;
    private String customerName;
    private String customerPhone;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private LocalDateTime saleDate;
    private String status;

    public Sale(long saleId, String invoiceNumber, String customerName, String customerPhone,
                BigDecimal subtotal, BigDecimal discountAmount, BigDecimal totalAmount,
                String paymentMethod, LocalDateTime saleDate, String status) {
        this.saleId = saleId;
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.saleDate = saleDate;
        this.status = status;
    }

    public long getSaleId() { return saleId; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("%-6d %-18s %-20s %-12s %-12s %-12s %-10s %-10s",
                saleId, invoiceNumber, customerName == null ? "" : customerName,
                subtotal, discountAmount, totalAmount, paymentMethod, status);
    }
}
