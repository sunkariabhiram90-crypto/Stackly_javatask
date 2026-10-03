package com;

public class Supplier {
    private int supplierId;
    private String supplierName;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;
    private String status;

    public Supplier(int supplierId, String supplierName, String contactPerson, String phone,
                    String email, String address, String status) {
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.status = status;
    }

    public int getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public String getContactPerson() { return contactPerson; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("%-5d %-22s %-18s %-12s %-25s %-25s %-8s",
                supplierId, supplierName, contactPerson == null ? "" : contactPerson,
                phone, email == null ? "" : email, address, status);
    }
}
