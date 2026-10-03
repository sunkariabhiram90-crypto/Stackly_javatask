package com;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

public class InventoryManagementSystem {
    private final Connection con;
    private final Scanner sc;

    public InventoryManagementSystem(Connection connection, Scanner scanner) {
        this.con = connection;
        this.sc = scanner;
    }

    public void run() {
        boolean running = true;
        while (running) {
            heading("INVENTORY MANAGEMENT SYSTEM");
            System.out.println("1. Product Management\n2. Supplier Management\n3. Stock Management");
            System.out.println("4. Sales Management\n5. Inventory and Sales Reports\n6. Batch Operations\n0. Exit");
            int choice = integer("Enter your choice: ", 0, 6);
            try {
                switch (choice) {
                    case 1 -> productMenu();
                    case 2 -> supplierMenu();
                    case 3 -> stockMenu();
                    case 4 -> salesMenu();
                    case 5 -> reportsMenu();
                    case 6 -> batchMenu();
                    case 0 -> running = false;
                    default -> { }
                }
            } catch (SQLException e) {
                error(e);
            }
        }
    }

    // ------------------------------- Menus -------------------------------

    private void productMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            heading("PRODUCT MANAGEMENT");
            System.out.println("1. Add New Product\n2. View All Products\n3. Search Product by ID");
            System.out.println("4. Search Product by Name or SKU\n5. Search by Category");
            System.out.println("6. Update Product Details\n7. Delete Product\n8. Change Product Status\n0. Back");
            switch (integer("Enter your choice: ", 0, 8)) {
                case 1 -> addProduct();
                case 2 -> listProducts("SELECT * FROM products ORDER BY product_id", null);
                case 3 -> showProduct(integer("Product ID: ", 1, Integer.MAX_VALUE));
                case 4 -> searchProduct();
                case 5 -> listProducts("SELECT * FROM products WHERE category LIKE ? ORDER BY product_name",
                        "%" + required("Category: ") + "%");
                case 6 -> updateProduct();
                case 7 -> deleteProduct();
                case 8 -> changeStatus("products", "product_id");
                case 0 -> back = true;
            }
        }
    }

    private void supplierMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            heading("SUPPLIER MANAGEMENT");
            System.out.println("1. Register Supplier\n2. View All Suppliers\n3. Search Supplier by ID");
            System.out.println("4. Search by Name or Phone\n5. Update Supplier Details");
            System.out.println("6. Change Supplier Status\n7. View Supplier Products\n8. Delete Supplier\n0. Back");
            switch (integer("Enter your choice: ", 0, 8)) {
                case 1 -> addSupplier();
                case 2 -> listSuppliers("SELECT * FROM suppliers ORDER BY supplier_id", null);
                case 3 -> showSupplier(integer("Supplier ID: ", 1, Integer.MAX_VALUE));
                case 4 -> searchSupplier();
                case 5 -> updateSupplier();
                case 6 -> changeStatus("suppliers", "supplier_id");
                case 7 -> listProducts("SELECT * FROM products WHERE supplier_id=? ORDER BY product_name",
                        integer("Supplier ID: ", 1, Integer.MAX_VALUE));
                case 8 -> deleteSupplier();
                case 0 -> back = true;
            }
        }
    }

    private void stockMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            heading("STOCK MANAGEMENT");
            System.out.println("1. Add Stock\n2. Reduce Stock (Adjustment)\n3. Set Stock Quantity");
            System.out.println("4. View Current Stock\n5. Low-Stock Products\n6. Out-of-Stock Products");
            System.out.println("7. View Stock by Category\n0. Back");
            switch (integer("Enter your choice: ", 0, 7)) {
                case 1 -> adjustStock(true);
                case 2 -> adjustStock(false);
                case 3 -> setStock();
                case 4 -> listProducts("SELECT * FROM products ORDER BY product_id", null);
                case 5 -> listProducts("SELECT * FROM products WHERE status='ACTIVE' AND stock_quantity>0 AND stock_quantity<=reorder_level", null);
                case 6 -> listProducts("SELECT * FROM products WHERE status='ACTIVE' AND stock_quantity=0", null);
                case 7 -> listProducts("SELECT * FROM products WHERE category LIKE ? ORDER BY product_name",
                        "%" + required("Category: ") + "%");
                case 0 -> back = true;
            }
        }
    }

    private void salesMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            heading("SALES MANAGEMENT");
            System.out.println("1. Record New Sale\n2. View All Sales\n3. View Sale by Invoice Number");
            System.out.println("4. View Sale Details\n5. Search Sales by Date\n6. Cancel Sale");
            System.out.println("7. View Sales for a Product\n0. Back");
            switch (integer("Enter your choice: ", 0, 7)) {
                case 1 -> recordSale();
                case 2 -> listSales("SELECT * FROM sales ORDER BY sale_date DESC", null);
                case 3 -> saleByInvoice(required("Invoice number: "));
                case 4 -> saleDetails(longValue("Sale ID: ", 1, Long.MAX_VALUE));
                case 5 -> salesByDate();
                case 6 -> cancelSale();
                case 7 -> salesForProduct(integer("Product ID: ", 1, Integer.MAX_VALUE));
                case 0 -> back = true;
            }
        }
    }

    private void reportsMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            heading("INVENTORY AND SALES REPORTS");
            System.out.println("1. Total Products and Stock Value\n2. Low-Stock Report\n3. Out-of-Stock Report");
            System.out.println("4. Total Sales Summary\n5. Daily Sales Report\n6. Monthly Sales Report");
            System.out.println("7. Best-Selling Products\n8. Products with No Sales\n9. Supplier-Wise Product Report");
            System.out.println("10. Estimated Profit Report\n11. Sales by Payment Method\n0. Back");
            switch (integer("Enter your choice: ", 0, 11)) {
                case 1 -> inventorySummary();
                case 2 -> listProducts("SELECT * FROM products WHERE status='ACTIVE' AND stock_quantity>0 AND stock_quantity<=reorder_level", null);
                case 3 -> listProducts("SELECT * FROM products WHERE status='ACTIVE' AND stock_quantity=0", null);
                case 4 -> salesSummary();
                case 5 -> dailyReport();
                case 6 -> monthlyReport();
                case 7 -> bestSellers();
                case 8 -> noSalesReport();
                case 9 -> supplierReport();
                case 10 -> profitReport();
                case 11 -> paymentReport();
                case 0 -> back = true;
            }
        }
    }

    private void batchMenu() throws SQLException {
        boolean back = false;
        while (!back) {
            heading("BATCH OPERATIONS");
            System.out.println("1. Add Multiple Products\n2. Update Multiple Product Prices");
            System.out.println("3. Update Multiple Stock Quantities\n4. Batch Validation Preview\n0. Back");
            switch (integer("Enter your choice: ", 0, 4)) {
                case 1 -> batchAddProducts();
                case 2 -> batchPrices();
                case 3 -> batchStocks();
                case 4 -> batchPreview();
                case 0 -> back = true;
            }
        }
    }

    // ------------------------------- Products -------------------------------

    private void addProduct() throws SQLException {
        heading("ADD PRODUCT");
        String name = required("Product name: ");
        String sku = required("SKU: ");
        String category = required("Category: ");
        int supplierId = integer("Active supplier ID: ", 1, Integer.MAX_VALUE);
        requireActiveSupplier(supplierId);
        BigDecimal purchase = money("Purchase price (>= 0): ", true);
        BigDecimal selling = money("Selling price (> 0): ", false);
        int stock = integer("Initial stock (>= 0): ", 0, Integer.MAX_VALUE);
        int reorder = integer("Reorder level (>= 0): ", 0, Integer.MAX_VALUE);
        String unit = required("Unit (piece/kg/litre/pack): ");
        String sql = "INSERT INTO products(product_name,sku,category,supplier_id,purchase_price,selling_price,stock_quantity,reorder_level,unit) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindProduct(ps, name, sku, category, supplierId, purchase, selling, stock, reorder, unit);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) System.out.println("Product added. Generated Product ID: " + keys.getInt(1));
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("ERROR: SKU already exists, or the supplier relationship is invalid.");
        }
    }

    private void updateProduct() throws SQLException {
        int id = integer("Product ID: ", 1, Integer.MAX_VALUE);
        Product p = getProduct(id);
        if (p == null) { System.out.println("ERROR: Product not found."); return; }
        showProduct(id);
        System.out.println("1.Name  2.SKU  3.Category  4.Supplier  5.Purchase price");
        System.out.println("6.Selling price  7.Reorder level  8.Unit  9.Multiple fields  10.All fields  0.Cancel");
        int choice = integer("Choice: ", 0, 10);
        if (choice == 0) return;
        Map<Integer, Object> changes = new LinkedHashMap<>();
        if (choice == 9) {
            System.out.println("Enter field numbers separated by commas (1-8): ");
            String[] fields = required("Fields: ").split(",");
            for (String f : fields) {
                try { collectProductField(Integer.parseInt(f.trim()), p, changes); }
                catch (NumberFormatException e) { System.out.println("Invalid field skipped."); }
            }
        } else if (choice == 10) {
            for (int i = 1; i <= 8; i++) collectProductField(i, p, changes);
        } else collectProductField(choice, p, changes);
        if (changes.isEmpty()) { System.out.println("No changes selected."); return; }

        StringBuilder sql = new StringBuilder("UPDATE products SET ");
        List<Object> values = new ArrayList<>();
        for (Map.Entry<Integer,Object> e : changes.entrySet()) {
            String column = switch (e.getKey()) {
                case 1 -> "product_name"; case 2 -> "sku"; case 3 -> "category";
                case 4 -> "supplier_id"; case 5 -> "purchase_price"; case 6 -> "selling_price";
                case 7 -> "reorder_level"; default -> "unit";
            };
            if (!values.isEmpty()) sql.append(", ");
            sql.append(column).append("=?");
            values.add(e.getValue());
        }
        sql.append(", updated_at=CURRENT_TIMESTAMP WHERE product_id=?");
        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i = 1;
            for (Object v : values) ps.setObject(i++, v);
            ps.setInt(i, id);
            ps.executeUpdate();
            System.out.println("Product updated successfully. Changed fields: " + changes.keySet());
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("ERROR: Duplicate SKU or invalid supplier.");
        }
    }

    private void collectProductField(int field, Product p, Map<Integer,Object> changes) throws SQLException {
        switch (field) {
            case 1 -> changes.put(1, required("New product name: "));
            case 2 -> changes.put(2, required("New SKU: "));
            case 3 -> changes.put(3, required("New category: "));
            case 4 -> {
                int sid = integer("New active supplier ID: ", 1, Integer.MAX_VALUE);
                requireActiveSupplier(sid); changes.put(4, sid);
            }
            case 5 -> changes.put(5, money("New purchase price: ", true));
            case 6 -> changes.put(6, money("New selling price: ", false));
            case 7 -> changes.put(7, integer("New reorder level: ", 0, Integer.MAX_VALUE));
            case 8 -> changes.put(8, required("New unit: "));
            default -> System.out.println("Unknown field: " + field);
        }
    }

    private void deleteProduct() throws SQLException {
        int id = integer("Product ID: ", 1, Integer.MAX_VALUE);
        Product p = getProduct(id);
        if (p == null) { System.out.println("Product not found."); return; }
        showProduct(id);
        if (!yes("Delete this product? Sale history prevents deletion. (y/n): ")) return;
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM products WHERE product_id=?")) {
            ps.setInt(1, id);
            int n = ps.executeUpdate();
            System.out.println(n == 1 ? "Product deleted." : "Product was not deleted.");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Cannot delete: sale history references this product. Mark it INACTIVE instead.");
        }
    }

    private void searchProduct() throws SQLException {
        String q = required("Enter product name or SKU: ");
        listProducts("SELECT * FROM products WHERE product_name LIKE ? OR sku LIKE ? ORDER BY product_name",
                new String[]{ "%" + q + "%", "%" + q + "%" });
    }

    private Product getProduct(int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM products WHERE product_id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? productFrom(rs) : null; }
        }
    }

    private void showProduct(int id) throws SQLException {
        Product p = getProduct(id);
        if (p == null) { System.out.println("Product not found."); return; }
        System.out.printf("%nID: %d%nName: %s%nSKU: %s%nCategory: %s%nSupplier ID: %d%nPurchase: %s%nSelling: %s%nStock: %d%nReorder: %d%nUnit: %s%nStatus: %s%n",
                p.getProductId(), p.getProductName(), p.getSku(), p.getCategory(), p.getSupplierId(),
                p.getPurchasePrice(), p.getSellingPrice(), p.getStockQuantity(), p.getReorderLevel(),
                p.getUnit(), p.getStatus());
    }

    // ------------------------------- Suppliers -------------------------------

    private void addSupplier() throws SQLException {
        String name = required("Supplier name: ");
        String contact = optional("Contact person (Enter to skip): ");
        String phone = phone("10-digit Indian mobile number: ");
        String email = email("Email (Enter to skip): ");
        String address = required("Address: ");
        try (PreparedStatement ps = con.prepareStatement(
                "INSERT INTO suppliers(supplier_name,contact_person,phone,email,address) VALUES(?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name); setNullable(ps, 2, contact); ps.setString(3, phone);
            setNullable(ps, 4, email); ps.setString(5, address);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) System.out.println("Supplier registered. ID: " + keys.getInt(1));
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("ERROR: Phone number or email already exists.");
        }
    }

    private void updateSupplier() throws SQLException {
        int id = integer("Supplier ID: ", 1, Integer.MAX_VALUE);
        Supplier s = getSupplier(id);
        if (s == null) { System.out.println("Supplier not found."); return; }
        showSupplier(id);
        System.out.println("1.Name  2.Contact person  3.Phone  4.Email  5.Address  6.Multiple fields  7.All fields  0.Cancel");
        int choice = integer("Choice: ", 0, 7);
        if (choice == 0) return;
        Map<Integer,Object> changes = new LinkedHashMap<>();
        if (choice == 6) {
            for (String f : required("Field numbers separated by commas: ").split(",")) {
                try { collectSupplierField(Integer.parseInt(f.trim()), changes); }
                catch (NumberFormatException e) { System.out.println("Invalid field skipped."); }
            }
        } else if (choice == 7) {
            for (int i=1;i<=5;i++) collectSupplierField(i, changes);
        } else collectSupplierField(choice, changes);
        if (changes.isEmpty()) return;
        String[] cols = {"", "supplier_name", "contact_person", "phone", "email", "address"};
        StringBuilder sql = new StringBuilder("UPDATE suppliers SET ");
        for (int field : changes.keySet()) {
            if (sql.charAt(sql.length()-1) != ' ') sql.append(", ");
            sql.append(cols[field]).append("=?");
        }
        sql.append(", updated_at=CURRENT_TIMESTAMP WHERE supplier_id=?");
        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int i=1;
            for (Object v : changes.values()) { if (v == null) ps.setNull(i++, Types.VARCHAR); else ps.setObject(i++,v); }
            ps.setInt(i,id); ps.executeUpdate();
            System.out.println("Supplier updated. Changed fields: " + changes.keySet());
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("ERROR: Phone number or email already exists.");
        }
    }

    private void collectSupplierField(int field, Map<Integer,Object> changes) {
        switch (field) {
            case 1 -> changes.put(1, required("New supplier name: "));
            case 2 -> changes.put(2, optional("New contact person (Enter to clear): "));
            case 3 -> changes.put(3, phone("New 10-digit phone: "));
            case 4 -> changes.put(4, email("New email (Enter to clear): "));
            case 5 -> changes.put(5, required("New address: "));
            default -> System.out.println("Unknown field: " + field);
        }
    }

    private void deleteSupplier() throws SQLException {
        int id = integer("Supplier ID: ", 1, Integer.MAX_VALUE);
        if (getSupplier(id) == null) { System.out.println("Supplier not found."); return; }
        if (!yes("Permanently delete this supplier? (y/n): ")) return;
        try (PreparedStatement ps = con.prepareStatement("DELETE FROM suppliers WHERE supplier_id=?")) {
            ps.setInt(1,id); ps.executeUpdate(); System.out.println("Supplier deleted if it had no associated products.");
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Supplier has associated products and cannot be deleted. Mark it INACTIVE instead.");
        }
    }

    private Supplier getSupplier(int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM suppliers WHERE supplier_id=?")) {
            ps.setInt(1,id);
            try (ResultSet rs=ps.executeQuery()) {
                return rs.next() ? new Supplier(rs.getInt("supplier_id"),rs.getString("supplier_name"),
                        rs.getString("contact_person"),rs.getString("phone"),rs.getString("email"),
                        rs.getString("address"),rs.getString("status")) : null;
            }
        }
    }

    private void showSupplier(int id) throws SQLException {
        Supplier s=getSupplier(id);
        if(s==null){System.out.println("Supplier not found.");return;}
        System.out.println("ID: "+s.getSupplierId()+"\nName: "+s.getSupplierName()+"\nContact: "+s.getContactPerson()
                +"\nPhone: "+s.getPhone()+"\nEmail: "+s.getEmail()+"\nAddress: "+s.getAddress()+"\nStatus: "+s.getStatus());
    }

    private void searchSupplier() throws SQLException {
        String q=required("Supplier name or phone: ");
        listSuppliers("SELECT * FROM suppliers WHERE supplier_name LIKE ? OR phone LIKE ? ORDER BY supplier_name",
                new String[]{"%"+q+"%","%"+q+"%"});
    }

    // ------------------------------- Stock -------------------------------

    private void adjustStock(boolean add) throws SQLException {
        int id=integer("Product ID: ",1,Integer.MAX_VALUE);
        Product p=getProduct(id);
        if(p==null){System.out.println("Product not found.");return;}
        int qty=integer("Quantity to "+(add?"add":"reduce")+": ",1,Integer.MAX_VALUE);
        String sql=add
                ? "UPDATE products SET stock_quantity=stock_quantity+?, updated_at=CURRENT_TIMESTAMP WHERE product_id=?"
                : "UPDATE products SET stock_quantity=stock_quantity-?, updated_at=CURRENT_TIMESTAMP WHERE product_id=? AND stock_quantity>=?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setInt(1,qty);ps.setInt(2,id);if(!add)ps.setInt(3,qty);
            int n=ps.executeUpdate();
            if(n==0)System.out.println("ERROR: Insufficient stock or product not found.");
            else System.out.println("Stock changed from "+p.getStockQuantity()+" to "+(add?(long)p.getStockQuantity()+qty:p.getStockQuantity()-qty));
        }
    }

    private void setStock() throws SQLException {
        int id=integer("Product ID: ",1,Integer.MAX_VALUE);
        Product p=getProduct(id);
        if(p==null){System.out.println("Product not found.");return;}
        int target=integer("New stock quantity: ",0,Integer.MAX_VALUE);
        if(!yes("Change stock from "+p.getStockQuantity()+" to "+target+"? (y/n): "))return;
        try(PreparedStatement ps=con.prepareStatement("UPDATE products SET stock_quantity=?,updated_at=CURRENT_TIMESTAMP WHERE product_id=?")){
            ps.setInt(1,target);ps.setInt(2,id);ps.executeUpdate();System.out.println("Stock quantity updated.");
        }
    }

    // ------------------------------- Sales -------------------------------

    private static final class CartLine {
        int productId, quantity;
        String name, sku;
        BigDecimal price, cost, total;
        CartLine(Product p,int q){
            productId=p.getProductId();quantity=q;name=p.getProductName();sku=p.getSku();
            price=p.getSellingPrice();cost=p.getPurchasePrice();
            total=price.multiply(BigDecimal.valueOf(q)).setScale(2,RoundingMode.HALF_UP);
        }
    }

    private void recordSale() throws SQLException {
        heading("RECORD SALE");
        String customer=optional("Customer name (Enter to skip): ");
        String phone=optionalPhone("Customer phone (Enter to skip): ");
        Map<Integer,Integer> cart=new LinkedHashMap<>();
        while(true){
            int id=integer("Product ID (0 to finish): ",0,Integer.MAX_VALUE);
            if(id==0)break;
            Product p=getProduct(id);
            if(p==null||!"ACTIVE".equals(p.getStatus())){System.out.println("Product not found or inactive.");continue;}
            System.out.println(p.getProductName()+" | Price: "+p.getSellingPrice()+" | Available: "+p.getStockQuantity());
            int q=integer("Quantity: ",1,Integer.MAX_VALUE);
            cart.merge(id,q,Integer::sum);
            if(!yes("Add another product? (y/n): "))break;
        }
        if(cart.isEmpty()){System.out.println("Sale cancelled: no items.");return;}
        List<CartLine> lines=new ArrayList<>();
        BigDecimal subtotal=BigDecimal.ZERO;
        for(Map.Entry<Integer,Integer> e:cart.entrySet()){
            Product p=getProduct(e.getKey());
            if(p==null||!"ACTIVE".equals(p.getStatus())||p.getStockQuantity()<e.getValue()){
                System.out.println("ERROR: Insufficient stock or product became unavailable for ID "+e.getKey());return;
            }
            CartLine line=new CartLine(p,e.getValue());lines.add(line);subtotal=subtotal.add(line.total);
        }
        BigDecimal discount=money("Discount (0 to "+subtotal+"): ",true);
        if(discount.compareTo(subtotal)>0){System.out.println("ERROR: Discount cannot exceed subtotal.");return;}
        String payment=choice("Payment method (CASH/UPI/CARD/OTHER): ",List.of("CASH","UPI","CARD","OTHER"));
        BigDecimal total=subtotal.subtract(discount).setScale(2,RoundingMode.HALF_UP);
        printBill("PREVIEW",customer,payment,lines,subtotal,discount,total);
        if(!yes("Confirm and save sale? (y/n): "))return;

        boolean oldAuto=con.getAutoCommit();
        try{
            con.setAutoCommit(false);
            String invoice;
            long saleId;
            try(PreparedStatement ps=con.prepareStatement(
                    "INSERT INTO sales(invoice_number,customer_name,customer_phone,subtotal,discount_amount,total_amount,payment_method,status) VALUES('PENDING',?,?,?,?,?,?,'COMPLETED')",
                    Statement.RETURN_GENERATED_KEYS)){
                setNullable(ps,1,customer);setNullable(ps,2,phone);ps.setBigDecimal(3,subtotal);
                ps.setBigDecimal(4,discount);ps.setBigDecimal(5,total);ps.setString(6,payment);
                ps.executeUpdate();
                try(ResultSet keys=ps.getGeneratedKeys()){keys.next();saleId=keys.getLong(1);}
            }
            invoice=String.format("INV-%06d",saleId);
            try(PreparedStatement ps=con.prepareStatement("UPDATE sales SET invoice_number=? WHERE sale_id=?")){
                ps.setString(1,invoice);ps.setLong(2,saleId);ps.executeUpdate();
            }
            try(PreparedStatement item=con.prepareStatement(
                    "INSERT INTO sale_items(sale_id,product_id,quantity,unit_price,purchase_price_at_sale,line_total) VALUES(?,?,?,?,?,?)");
                PreparedStatement stock=con.prepareStatement(
                    "UPDATE products SET stock_quantity=stock_quantity-?,updated_at=CURRENT_TIMESTAMP WHERE product_id=? AND status='ACTIVE' AND stock_quantity>=?")){
                for(CartLine line:lines){
                    item.setLong(1,saleId);item.setInt(2,line.productId);item.setInt(3,line.quantity);
                    item.setBigDecimal(4,line.price);item.setBigDecimal(5,line.cost);item.setBigDecimal(6,line.total);
                    item.addBatch();
                    stock.setInt(1,line.quantity);stock.setInt(2,line.productId);stock.setInt(3,line.quantity);
                    stock.addBatch();
                }
                int[] itemCounts=item.executeBatch();
                for(int n:itemCounts)if(n==Statement.EXECUTE_FAILED)throw new SQLException("A sale item could not be inserted.");
                int[] stockCounts=stock.executeBatch();
                for(int n:stockCounts)if(n==Statement.EXECUTE_FAILED||n==0)throw new SQLException("Stock changed during sale; transaction rolled back.");
            }
            con.commit();
            printBill(invoice,customer,payment,lines,subtotal,discount,total);
            System.out.println("Sale recorded successfully.");
        }catch(Exception e){
            con.rollback();
            System.out.println("ERROR: Sale failed; no changes were committed. "+e.getMessage());
        }finally{con.setAutoCommit(oldAuto);}
    }

    private void cancelSale() throws SQLException {
        long id=longValue("Sale ID to cancel: ",1,Long.MAX_VALUE);
        try(PreparedStatement ps=con.prepareStatement("SELECT status,invoice_number FROM sales WHERE sale_id=?")){
            ps.setLong(1,id);
            try(ResultSet rs=ps.executeQuery()){
                if(!rs.next()){System.out.println("Sale not found.");return;}
                if("CANCELLED".equals(rs.getString("status"))){System.out.println("Sale is already cancelled.");return;}
                System.out.println("Invoice: "+rs.getString("invoice_number"));
            }
        }
        if(!yes("Cancel this sale and restore its stock? (y/n): "))return;
        boolean old=con.getAutoCommit();
        try{
            con.setAutoCommit(false);
            try(PreparedStatement ps=con.prepareStatement(
                    "UPDATE products p JOIN sale_items i ON p.product_id=i.product_id SET p.stock_quantity=p.stock_quantity+i.quantity,p.updated_at=CURRENT_TIMESTAMP WHERE i.sale_id=?")){
                ps.setLong(1,id);ps.executeUpdate();
            }
            try(PreparedStatement ps=con.prepareStatement("UPDATE sales SET status='CANCELLED' WHERE sale_id=? AND status='COMPLETED'")){
                ps.setLong(1,id);
                if(ps.executeUpdate()!=1)throw new SQLException("Sale status changed; cancellation aborted.");
            }
            con.commit();System.out.println("Sale cancelled and stock restored.");
        }catch(SQLException e){con.rollback();System.out.println("Cancellation failed; transaction rolled back: "+e.getMessage());}
        finally{con.setAutoCommit(old);}
    }

    private void saleByInvoice(String invoice) throws SQLException {
        try(PreparedStatement ps=con.prepareStatement("SELECT * FROM sales WHERE invoice_number=?")){
            ps.setString(1,invoice);
            try(ResultSet rs=ps.executeQuery()){
                if(!rs.next()){System.out.println("Invoice not found.");return;}
                printSaleRow(rs);
            }
        }
    }

    private void saleDetails(long id) throws SQLException {
        try(PreparedStatement ps=con.prepareStatement("SELECT * FROM sales WHERE sale_id=?")){
            ps.setLong(1,id);
            try(ResultSet rs=ps.executeQuery()){
                if(!rs.next()){System.out.println("Sale not found.");return;}
                printSaleRow(rs);
            }
        }
        try(PreparedStatement ps=con.prepareStatement(
                "SELECT p.product_name,p.sku,i.quantity,i.unit_price,i.line_total FROM sale_items i JOIN products p ON p.product_id=i.product_id WHERE i.sale_id=?")){
            ps.setLong(1,id);
            try(ResultSet rs=ps.executeQuery()){
                System.out.printf("%-24s %-14s %8s %12s %12s%n","Product","SKU","Qty","Price","Total");
                while(rs.next())System.out.printf("%-24s %-14s %8d %12s %12s%n",
                        rs.getString(1),rs.getString(2),rs.getInt(3),rs.getBigDecimal(4),rs.getBigDecimal(5));
            }
        }
    }

    private void salesByDate() throws SQLException {
        LocalDate d=date("Date (YYYY-MM-DD): ");
        listSales("SELECT * FROM sales WHERE DATE(sale_date)=? ORDER BY sale_date",java.sql.Date.valueOf(d));
    }

    private void salesForProduct(int id) throws SQLException {
        String sql="SELECT s.* FROM sales s JOIN sale_items i ON i.sale_id=s.sale_id WHERE i.product_id=? ORDER BY s.sale_date DESC";
        listSales(sql,id);
    }

    private void printBill(String invoice,String customer,String payment,List<CartLine> lines,
                           BigDecimal subtotal,BigDecimal discount,BigDecimal total){
        System.out.println("\n======================= SALES INVOICE =======================");
        System.out.println("Invoice: "+invoice+" | Date: "+LocalDateTime.now());
        System.out.println("Customer: "+(customer.isBlank()?"Walk-in customer":customer)+" | Payment: "+payment);
        System.out.printf("%-22s %6s %12s %12s%n","Product","Qty","Price","Total");
        for(CartLine l:lines)System.out.printf("%-22s %6d %12s %12s%n",l.name,l.quantity,l.price,l.total);
        System.out.println("-------------------------------------------------------------");
        System.out.println("Subtotal: "+subtotal+"\nDiscount: "+discount+"\nTOTAL: "+total);
        System.out.println("=============================================================");
    }

    // ------------------------------- Reports -------------------------------

    private void inventorySummary() throws SQLException {
        String sql="SELECT COUNT(*) products, COALESCE(SUM(stock_quantity),0) units, COALESCE(SUM(stock_quantity*purchase_price),0) cost_value, COALESCE(SUM(stock_quantity*selling_price),0) selling_value FROM products WHERE status='ACTIVE'";
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery(sql)){
            rs.next();
            System.out.println("Active products: "+rs.getLong("products"));
            System.out.println("Stock units: "+rs.getLong("units"));
            System.out.println("Inventory purchase value: "+rs.getBigDecimal("cost_value"));
            System.out.println("Potential selling value: "+rs.getBigDecimal("selling_value"));
        }
    }

    private void salesSummary() throws SQLException {
        String sql="SELECT COUNT(*) transactions, COALESCE(SUM(i.quantity),0) units, COALESCE(SUM(s.subtotal),0) gross, COALESCE(SUM(s.discount_amount),0) discounts, COALESCE(SUM(s.total_amount),0) revenue, COALESCE(AVG(s.total_amount),0) avg_sale, COALESCE(MAX(s.total_amount),0) highest, COALESCE(MIN(s.total_amount),0) lowest FROM sales s LEFT JOIN sale_items i ON i.sale_id=s.sale_id AND s.status='COMPLETED' WHERE s.status='COMPLETED'";
        // Aggregate units separately to avoid multiplying sale totals by item count.
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery(
                "SELECT COUNT(*) transactions,COALESCE(SUM(subtotal),0) gross,COALESCE(SUM(discount_amount),0) discounts,COALESCE(SUM(total_amount),0) revenue,COALESCE(AVG(total_amount),0) avg_sale,COALESCE(MAX(total_amount),0) highest,COALESCE(MIN(total_amount),0) lowest FROM sales WHERE status='COMPLETED'")){
            rs.next();
            System.out.println("Completed sales: "+rs.getLong("transactions"));
            System.out.println("Gross sales: "+rs.getBigDecimal("gross"));
            System.out.println("Discounts: "+rs.getBigDecimal("discounts"));
            System.out.println("Net revenue: "+rs.getBigDecimal("revenue"));
            System.out.println("Average sale: "+rs.getBigDecimal("avg_sale"));
            System.out.println("Highest sale: "+rs.getBigDecimal("highest"));
            System.out.println("Lowest sale: "+rs.getBigDecimal("lowest"));
        }
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery(
                "SELECT COALESCE(SUM(i.quantity),0) FROM sale_items i JOIN sales s ON s.sale_id=i.sale_id WHERE s.status='COMPLETED'")){
            rs.next();System.out.println("Units sold: "+rs.getLong(1));
        }
    }

    private void dailyReport() throws SQLException {
        LocalDate d=date("Date (YYYY-MM-DD): ");
        try(PreparedStatement ps=con.prepareStatement("SELECT COUNT(*) transactions,COALESCE(SUM(subtotal),0) gross,COALESCE(SUM(discount_amount),0) discounts,COALESCE(SUM(total_amount),0) revenue FROM sales WHERE status='COMPLETED' AND sale_date>=? AND sale_date<?")){
            ps.setTimestamp(1,Timestamp.valueOf(d.atStartOfDay()));ps.setTimestamp(2,Timestamp.valueOf(d.plusDays(1).atStartOfDay()));
            try(ResultSet rs=ps.executeQuery()){rs.next();System.out.println("Date: "+d+" | Sales: "+rs.getLong(1)+" | Gross: "+rs.getBigDecimal(2)+" | Discounts: "+rs.getBigDecimal(3)+" | Revenue: "+rs.getBigDecimal(4));}
        }
    }

    private void monthlyReport() throws SQLException {
        int year=integer("Year: ",2000,9999),month=integer("Month (1-12): ",1,12);
        try(PreparedStatement ps=con.prepareStatement("SELECT COUNT(*),COALESCE(SUM(total_amount),0) FROM sales WHERE status='COMPLETED' AND YEAR(sale_date)=? AND MONTH(sale_date)=?")){
            ps.setInt(1,year);ps.setInt(2,month);
            try(ResultSet rs=ps.executeQuery()){rs.next();System.out.println("Sales: "+rs.getLong(1)+" | Revenue: "+rs.getBigDecimal(2));}
        }
    }

    private void bestSellers() throws SQLException {
        int limit=integer("Top N products: ",1,100);
        String sql="SELECT p.product_id,p.product_name,SUM(i.quantity) units,SUM(i.line_total) revenue FROM sale_items i JOIN sales s ON s.sale_id=i.sale_id JOIN products p ON p.product_id=i.product_id WHERE s.status='COMPLETED' GROUP BY p.product_id,p.product_name ORDER BY units DESC,revenue DESC LIMIT ?";
        try(PreparedStatement ps=con.prepareStatement(sql)){ps.setInt(1,limit);try(ResultSet rs=ps.executeQuery()){
            System.out.printf("%-8s %-25s %-12s %-12s%n","ID","Product","Units","Revenue");
            boolean any=false;while(rs.next()){any=true;System.out.printf("%-8d %-25s %-12d %-12s%n",rs.getInt(1),rs.getString(2),rs.getLong(3),rs.getBigDecimal(4));}
            if(!any)System.out.println("No completed sales yet.");
        }}
    }

    private void noSalesReport() throws SQLException {
        String sql="SELECT p.product_id,p.product_name,p.sku FROM products p LEFT JOIN sale_items i ON i.product_id=p.product_id LEFT JOIN sales s ON s.sale_id=i.sale_id AND s.status='COMPLETED' WHERE s.sale_id IS NULL ORDER BY p.product_name";
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery(sql)){
            boolean any=false;while(rs.next()){any=true;System.out.println(rs.getInt(1)+" | "+rs.getString(2)+" | "+rs.getString(3));}
            if(!any)System.out.println("Every product has completed-sale history.");
        }
    }

    private void supplierReport() throws SQLException {
        String sql="SELECT s.supplier_name,COUNT(p.product_id) products,COALESCE(SUM(p.stock_quantity),0) stock_units FROM suppliers s LEFT JOIN products p ON p.supplier_id=s.supplier_id GROUP BY s.supplier_id,s.supplier_name ORDER BY s.supplier_name";
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery(sql)){
            while(rs.next())System.out.println(rs.getString(1)+" | Products: "+rs.getLong(2)+" | Stock units: "+rs.getLong(3));
        }
    }

    private void profitReport() throws SQLException {
        String sql="SELECT COALESCE(SUM(i.line_total),0) sales_before_discount,COALESCE(SUM(i.quantity*i.purchase_price_at_sale),0) cost_of_goods,COALESCE(SUM(i.line_total-i.quantity*i.purchase_price_at_sale),0) estimated_gross_profit FROM sale_items i JOIN sales s ON s.sale_id=i.sale_id WHERE s.status='COMPLETED'";
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery(sql)){rs.next();
            System.out.println("Item sales before discount: "+rs.getBigDecimal(1));
            System.out.println("Cost of goods sold: "+rs.getBigDecimal(2));
            System.out.println("Estimated gross profit before sale-level discounts/expenses: "+rs.getBigDecimal(3));
        }
    }

    private void paymentReport() throws SQLException {
        String sql="SELECT payment_method,COUNT(*) transactions,SUM(total_amount) revenue FROM sales WHERE status='COMPLETED' GROUP BY payment_method HAVING COUNT(*)>0 ORDER BY revenue DESC";
        try(Statement st=con.createStatement();ResultSet rs=st.executeQuery(sql)){
            while(rs.next())System.out.println(rs.getString(1)+" | Transactions: "+rs.getLong(2)+" | Revenue: "+rs.getBigDecimal(3));
        }
    }

    // ------------------------------- Batch operations -------------------------------

    private void batchAddProducts() throws SQLException {
        int count=integer("How many products? ",1,100);
        List<String[]> rows=new ArrayList<>();Set<String> skus=new HashSet<>();
        for(int i=1;i<=count;i++){
            System.out.println("Product "+i);
            String name=required("Name: "),sku=required("SKU: "),cat=required("Category: ");
            if(!skus.add(sku.toUpperCase(Locale.ROOT)))throw new IllegalArgumentException("Duplicate SKU in this batch: "+sku);
            int sid=integer("Supplier ID: ",1,Integer.MAX_VALUE);requireActiveSupplier(sid);
            BigDecimal cost=money("Purchase price: ",true),price=money("Selling price: ",false);
            int stock=integer("Stock: ",0,Integer.MAX_VALUE),reorder=integer("Reorder level: ",0,Integer.MAX_VALUE);
            String unit=required("Unit: ");
            rows.add(new String[]{name,sku,cat,String.valueOf(sid),cost.toPlainString(),price.toPlainString(),String.valueOf(stock),String.valueOf(reorder),unit});
        }
        for(String[] r:rows)try(PreparedStatement ps=con.prepareStatement("SELECT 1 FROM products WHERE sku=?")){ps.setString(1,r[1]);try(ResultSet rs=ps.executeQuery()){if(rs.next())throw new IllegalArgumentException("SKU already exists: "+r[1]);}}
        System.out.println("Validated "+rows.size()+" products. First SKU: "+rows.get(0)[1]);
        if(!yes("Insert all products? (y/n): "))return;
        boolean old=con.getAutoCommit();
        try{
            con.setAutoCommit(false);
            try(PreparedStatement ps=con.prepareStatement("INSERT INTO products(product_name,sku,category,supplier_id,purchase_price,selling_price,stock_quantity,reorder_level,unit) VALUES(?,?,?,?,?,?,?,?,?)")){
                for(String[] r:rows){ps.setString(1,r[0]);ps.setString(2,r[1]);ps.setString(3,r[2]);ps.setInt(4,Integer.parseInt(r[3]));ps.setBigDecimal(5,new BigDecimal(r[4]));ps.setBigDecimal(6,new BigDecimal(r[5]));ps.setInt(7,Integer.parseInt(r[6]));ps.setInt(8,Integer.parseInt(r[7]));ps.setString(9,r[8]);ps.addBatch();}
                int[] results=ps.executeBatch();for(int n:results)if(n==Statement.EXECUTE_FAILED)throw new SQLException("Batch insert failed.");
            }
            con.commit();System.out.println("All products inserted successfully.");
        }catch(Exception e){con.rollback();System.out.println("Batch failed; all changes rolled back: "+e.getMessage());}
        finally{con.setAutoCommit(old);}
    }

    private void batchPrices() throws SQLException {
        int n=integer("Number of products to update: ",1,100);
        Map<Integer,BigDecimal> updates=new LinkedHashMap<>();
        for(int i=0;i<n;i++){int id=integer("Product ID: ",1,Integer.MAX_VALUE);if(updates.containsKey(id))throw new IllegalArgumentException("Duplicate product ID: "+id);if(getProduct(id)==null)throw new IllegalArgumentException("Product not found: "+id);updates.put(id,money("New selling price: ",false));}
        if(!yes("Update "+updates.size()+" prices? (y/n): "))return;
        batchUpdate("UPDATE products SET selling_price=?,updated_at=CURRENT_TIMESTAMP WHERE product_id=?",updates,true);
    }

    private void batchStocks() throws SQLException {
        int n=integer("Number of products to set stock for: ",1,100);
        Map<Integer,Integer> updates=new LinkedHashMap<>();
        for(int i=0;i<n;i++){int id=integer("Product ID: ",1,Integer.MAX_VALUE);if(updates.containsKey(id))throw new IllegalArgumentException("Duplicate product ID: "+id);if(getProduct(id)==null)throw new IllegalArgumentException("Product not found: "+id);updates.put(id,integer("Target stock quantity: ",0,Integer.MAX_VALUE));}
        if(!yes("Apply "+updates.size()+" stock quantities? (y/n): "))return;
        batchUpdate("UPDATE products SET stock_quantity=?,updated_at=CURRENT_TIMESTAMP WHERE product_id=?",updates,false);
    }

    private void batchUpdate(String sql,Map<Integer,? extends Object> values,boolean price) throws SQLException {
        boolean old=con.getAutoCommit();
        try{con.setAutoCommit(false);try(PreparedStatement ps=con.prepareStatement(sql)){
            for(Map.Entry<Integer,? extends Object> e:values.entrySet()){ps.setObject(1,e.getValue());ps.setInt(2,e.getKey());ps.addBatch();}
            int[] counts=ps.executeBatch();for(int c:counts)if(c==Statement.EXECUTE_FAILED)throw new SQLException("A batch item failed.");
        }con.commit();System.out.println("Batch completed successfully.");}
        catch(BatchUpdateException e){con.rollback();System.out.println("Batch failed; rolled back. Successful counts before failure: "+Arrays.toString(e.getUpdateCounts()));}
        catch(Exception e){con.rollback();System.out.println("Batch failed; rolled back: "+e.getMessage());}
        finally{con.setAutoCommit(old);}
    }

    private void batchPreview() {
        System.out.println("Batch operations validate all entered rows before writing, use addBatch()/executeBatch(),");
        System.out.println("and wrap changes in a transaction. A failure triggers rollback.");
        System.out.println("Batches reduce repeated database round trips; use the other batch menu options to run them.");
    }

    // ------------------------------- Shared SQL/display helpers -------------------------------

    private void listProducts(String sql,Object param) throws SQLException {
        try(PreparedStatement ps=con.prepareStatement(sql)){
            bindParams(ps,param);
            try(ResultSet rs=ps.executeQuery()){
                System.out.printf("%-5s %-22s %-13s %-15s %-8s %-10s %-10s %-7s %-7s %-8s %-8s%n",
                        "ID","Name","SKU","Category","Supplier","Cost","Price","Stock","Reorder","Unit","Status");
                boolean any=false;while(rs.next()){any=true;System.out.println(productFrom(rs));}
                if(!any)System.out.println("No products found.");
            }
        }
    }

    private void listSuppliers(String sql,Object param) throws SQLException {
        try(PreparedStatement ps=con.prepareStatement(sql)){bindParams(ps,param);try(ResultSet rs=ps.executeQuery()){
            System.out.printf("%-5s %-22s %-18s %-12s %-25s %-25s %-8s%n","ID","Name","Contact","Phone","Email","Address","Status");
            boolean any=false;while(rs.next()){any=true;System.out.println(new Supplier(rs.getInt("supplier_id"),rs.getString("supplier_name"),rs.getString("contact_person"),rs.getString("phone"),rs.getString("email"),rs.getString("address"),rs.getString("status")));}
            if(!any)System.out.println("No suppliers found.");
        }}
    }

    private void listSales(String sql,Object param) throws SQLException {
        try(PreparedStatement ps=con.prepareStatement(sql)){bindParams(ps,param);try(ResultSet rs=ps.executeQuery()){
            System.out.printf("%-6s %-18s %-20s %-12s %-12s %-12s %-10s %-10s%n","ID","Invoice","Customer","Subtotal","Discount","Total","Payment","Status");
            boolean any=false;while(rs.next()){any=true;printSaleRow(rs);}
            if(!any)System.out.println("No sales found.");
        }}
    }

    private void printSaleRow(ResultSet rs) throws SQLException {
        System.out.printf("%-6d %-18s %-20s %-12s %-12s %-12s %-10s %-10s%n",
                rs.getLong("sale_id"),rs.getString("invoice_number"),
                rs.getString("customer_name")==null?"Walk-in":rs.getString("customer_name"),
                rs.getBigDecimal("subtotal"),rs.getBigDecimal("discount_amount"),
                rs.getBigDecimal("total_amount"),rs.getString("payment_method"),rs.getString("status"));
    }

    private Product productFrom(ResultSet rs) throws SQLException {
        return new Product(rs.getInt("product_id"),rs.getString("product_name"),rs.getString("sku"),
                rs.getString("category"),rs.getInt("supplier_id"),rs.getBigDecimal("purchase_price"),
                rs.getBigDecimal("selling_price"),rs.getInt("stock_quantity"),rs.getInt("reorder_level"),
                rs.getString("unit"),rs.getString("status"));
    }

    private void bindProduct(PreparedStatement ps,String name,String sku,String category,int sid,
                             BigDecimal cost,BigDecimal price,int stock,int reorder,String unit) throws SQLException {
        ps.setString(1,name);ps.setString(2,sku);ps.setString(3,category);ps.setInt(4,sid);
        ps.setBigDecimal(5,cost);ps.setBigDecimal(6,price);ps.setInt(7,stock);ps.setInt(8,reorder);ps.setString(9,unit);
    }

    private void bindParams(PreparedStatement ps,Object param) throws SQLException {
        if(param==null)return;
        if(param instanceof Object[] arr){for(int i=0;i<arr.length;i++)ps.setObject(i+1,arr[i]);}
        else ps.setObject(1,param);
    }

    private void changeStatus(String table,String idColumn) throws SQLException {
        int id=integer("Record ID: ",1,Integer.MAX_VALUE);
        String status=choice("New status (ACTIVE/INACTIVE): ",List.of("ACTIVE","INACTIVE"));
        try(PreparedStatement ps=con.prepareStatement("UPDATE "+table+" SET status=?,updated_at=CURRENT_TIMESTAMP WHERE "+idColumn+"=?")){
            ps.setString(1,status);ps.setInt(2,id);
            System.out.println(ps.executeUpdate()==1?"Status updated.":"Record not found.");
        }
    }

    private void requireActiveSupplier(int id) throws SQLException {
        try(PreparedStatement ps=con.prepareStatement("SELECT status FROM suppliers WHERE supplier_id=?")){
            ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){
                if(!rs.next())throw new IllegalArgumentException("Supplier does not exist: "+id);
                if(!"ACTIVE".equals(rs.getString(1)))throw new IllegalArgumentException("Supplier is inactive: "+id);
            }
        }
    }

    private void setNullable(PreparedStatement ps,int index,String value) throws SQLException {
        if(value==null||value.isBlank())ps.setNull(index,Types.VARCHAR);else ps.setString(index,value);
    }

    // ------------------------------- Input validation -------------------------------

    private String required(String prompt) {
        while(true){System.out.print(prompt);String s=sc.nextLine().trim();if(!s.isEmpty())return s;System.out.println("Value cannot be blank.");}
    }

    private String optional(String prompt) {
        System.out.print(prompt);String s=sc.nextLine().trim();return s.isEmpty()?null:s;
    }

    private boolean yes(String prompt) {
        while(true){System.out.print(prompt);String s=sc.nextLine().trim().toLowerCase(Locale.ROOT);if(s.equals("y")||s.equals("yes"))return true;if(s.equals("n")||s.equals("no"))return false;System.out.println("Enter y or n.");}
    }

    private int integer(String prompt,int min,int max) {
        while(true){System.out.print(prompt);String s=sc.nextLine().trim();try{int n=Integer.parseInt(s);if(n>=min&&n<=max)return n;}catch(NumberFormatException ignored){}System.out.println("Enter a whole number between "+min+" and "+max+".");}
    }

    private long longValue(String prompt,long min,long max) {
        while(true){System.out.print(prompt);try{long n=Long.parseLong(sc.nextLine().trim());if(n>=min&&n<=max)return n;}catch(NumberFormatException ignored){}System.out.println("Enter a valid whole number.");}
    }

    private BigDecimal money(String prompt,boolean allowZero) {
        while(true){System.out.print(prompt);try{BigDecimal n=new BigDecimal(sc.nextLine().trim()).setScale(2,RoundingMode.UNNECESSARY);if(n.signum()>0||allowZero&&n.signum()==0)return n;}catch(Exception ignored){}System.out.println("Enter a valid amount with at most two decimal places.");}
    }

    private String phone(String prompt) {
        while(true){System.out.print(prompt);String s=sc.nextLine().trim();if(s.matches("[6-9][0-9]{9}"))return s;System.out.println("Enter a valid 10-digit Indian mobile number.");}
    }

    private String optionalPhone(String prompt) {
        while(true){System.out.print(prompt);String s=sc.nextLine().trim();if(s.isEmpty())return null;if(s.matches("[6-9][0-9]{9}"))return s;System.out.println("Enter a valid 10-digit Indian mobile number or leave blank.");}
    }

    private String email(String prompt) {
        while(true){System.out.print(prompt);String s=sc.nextLine().trim();if(s.isEmpty())return null;if(s.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))return s;System.out.println("Enter a valid email or leave blank.");}
    }

    private LocalDate date(String prompt) {
        while(true){System.out.print(prompt);try{return LocalDate.parse(sc.nextLine().trim());}catch(DateTimeParseException e){System.out.println("Use YYYY-MM-DD.");}}
    }

    private String choice(String prompt,List<String> allowed) {
        while(true){System.out.print(prompt);String s=sc.nextLine().trim().toUpperCase(Locale.ROOT);if(allowed.contains(s))return s;System.out.println("Choose one of: "+allowed);}
    }

    private void heading(String title) {
        System.out.println("\n========================================================");
        System.out.println("             "+title);
        System.out.println("========================================================");
    }

    private void error(SQLException e) {
        String state=e.getSQLState();
        if("23000".equals(state))System.out.println("Database constraint error: check duplicate values and related records.");
        else System.out.println("Database operation failed: "+e.getMessage());
    }
}
