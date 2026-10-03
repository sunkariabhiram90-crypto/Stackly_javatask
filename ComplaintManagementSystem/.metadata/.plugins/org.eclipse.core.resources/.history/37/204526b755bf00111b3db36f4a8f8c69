package com;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

/** Menu-driven complaint application. Uses one shared connection and PreparedStatement for values. */
public class ComplaintManagementSystem {
    private final Scanner scanner;
    private final Connection connection;

    private static final String[] TYPES = {"Road damage", "Water supply", "Garbage collection", "Streetlight failure", "Drainage problem", "Other"};
    private static final Set<String> PRIORITIES = new HashSet<>(Arrays.asList("LOW", "MEDIUM", "HIGH"));
    private static final Set<String> STATUSES = new HashSet<>(Arrays.asList("REGISTERED", "ASSIGNED", "IN_PROGRESS", "RESOLVED", "REJECTED"));
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}][\\p{L} .'-]{0,99}$");

    public ComplaintManagementSystem(Scanner scanner, Connection connection) {
        this.scanner = scanner;
        this.connection = connection;
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Select a module: ", 0, 4);
            try {
                switch (choice) {
                    case 1: citizenMenu(); break;
                    case 2: complaintMenu(); break;
                    case 3: officerMenu(); break;
                    case 4: reportsMenu(); break;
                    case 0:
                        running = false;
                        System.out.println("\nThank you for using the Complaint Management System.");
                        break;
                    default: System.out.println("Invalid choice. Please select 0-4.");
                }
            } catch (SQLException e) {
                printSqlError(e);
            }
            if (running) pause();
        }
    }

    private void printMainMenu() {
        System.out.println("\n+==========================================================+");
        System.out.println("|          MUNICIPAL COMPLAINT MANAGEMENT SYSTEM          |");
        System.out.println("+==========================================================+");
        System.out.println("|  1. Citizen Management                                  |");
        System.out.println("|  2. Complaint Management                                |");
        System.out.println("|  3. Officer Management                                  |");
        System.out.println("|  4. Reports and Search                                  |");
        System.out.println("|  0. Exit                                                |");
        System.out.println("+==========================================================+");
    }

    private void citizenMenu() throws SQLException {
        while (true) {
            System.out.println("\n--------------- CITIZEN MANAGEMENT ---------------");
            System.out.println("1. Register citizen");
            System.out.println("2. View all citizens");
            System.out.println("3. Update citizen details (choose fields)");
            System.out.println("4. Delete citizen");
            System.out.println("0. Back to main menu");
            int choice = readInt("Select an option: ", 0, 4);
            switch (choice) {
                case 1: registerCitizen(); break;
                case 2: viewCitizens(); break;
                case 3: updateCitizen(); break;
                case 4: deleteCitizen(); break;
                case 0: return;
                default: System.out.println("Invalid option.");
            }
            if (choice != 0) pauseSubmenu();
        }
    }

    private void complaintMenu() throws SQLException {
        while (true) {
            System.out.println("\n-------------- COMPLAINT MANAGEMENT --------------");
            System.out.println("1. Register complaint");
            System.out.println("2. Assign officer to complaint");
            System.out.println("3. Update complaint status / resolution");
            System.out.println("4. Update complaint details (choose fields)");
            System.out.println("5. View complaint details / resolution");
            System.out.println("6. Delete complaint");
            System.out.println("0. Back to main menu");
            int choice = readInt("Select an option: ", 0, 6);
            switch (choice) {
                case 1: registerComplaint(); break;
                case 2: assignOfficer(); break;
                case 3: updateComplaintStatus(); break;
                case 4: updateComplaintDetails(); break;
                case 5: viewComplaintResolution(); break;
                case 6: deleteComplaint(); break;
                case 0: return;
                default: System.out.println("Invalid option.");
            }
            if (choice != 0) pauseSubmenu();
        }
    }

    private void officerMenu() throws SQLException {
        while (true) {
            System.out.println("\n---------------- OFFICER MANAGEMENT ---------------");
            System.out.println("1. Register officer");
            System.out.println("2. View all officers");
            System.out.println("3. Update officer details (choose fields)");
            System.out.println("4. Delete officer");
            System.out.println("0. Back to main menu");
            int choice = readInt("Select an option: ", 0, 4);
            switch (choice) {
                case 1: registerOfficer(); break;
                case 2: viewOfficers(); break;
                case 3: updateOfficer(); break;
                case 4: deleteOfficer(); break;
                case 0: return;
                default: System.out.println("Invalid option.");
            }
            if (choice != 0) pauseSubmenu();
        }
    }

    private void reportsMenu() throws SQLException {
        while (true) {
            System.out.println("\n---------------- REPORTS AND SEARCH ----------------");
            System.out.println("1. View all complaints");
            System.out.println("2. View complaints by citizen ID");
            System.out.println("3. View complaints by officer ID");
            System.out.println("4. Search complaints by status / priority / ID");
            System.out.println("0. Back to main menu");
            int choice = readInt("Select an option: ", 0, 4);
            switch (choice) {
                case 1: viewAllComplaints(); break;
                case 2: viewCitizenComplaints(); break;
                case 3: viewOfficerComplaints(); break;
                case 4: searchComplaints(); break;
                case 0: return;
                default: System.out.println("Invalid option.");
            }
            if (choice != 0) pauseSubmenu();
        }
    }

    // -------------------- Citizen operations --------------------
    private void registerCitizen() throws SQLException {
        System.out.println("\n--- Register Citizen ---");
        String name = readName("Full name: ");
        String email = readEmail("Email: ");
        String phone = readPhone("Indian mobile number (10 digits): ");
        String address = readText("Residential address: ", 5, 250);
        String sql = "INSERT INTO users(full_name,email,phone,address) VALUES(?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name); ps.setString(2, email); ps.setString(3, phone); ps.setString(4, address);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) System.out.println("Citizen registered successfully! Generated User ID: " + keys.getInt(1));
            }
        }
    }

    private void viewCitizens() throws SQLException {
        String sql = "SELECT user_id,full_name,email,phone,address,created_at FROM users ORDER BY user_id";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.printf("%-6s %-22s %-30s %-12s %-25s %-20s%n", "ID", "NAME", "EMAIL", "PHONE", "ADDRESS", "CREATED");
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.printf("%-6d %-22s %-30s %-12s %-25s %-20s%n", rs.getInt("user_id"), clip(rs.getString("full_name"),22), clip(rs.getString("email"),30), rs.getString("phone"), clip(rs.getString("address"),25), rs.getTimestamp("created_at"));
            }
            if (count == 0) System.out.println("No citizens found.");
        }
    }

    private void updateCitizen() throws SQLException {
        int id = readInt("Citizen ID to update: ", 1, Integer.MAX_VALUE);
        if (!exists("SELECT 1 FROM users WHERE user_id=?", id)) {
            System.out.println("Citizen ID " + id + " does not exist.");
            return;
        }
        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Name only\n2. Email only\n3. Phone only\n4. Address only\n5. Update all fields\n0. Cancel");
        int choice = readInt("Choose field(s): ", 0, 5);
        if (choice == 0) { System.out.println("Update cancelled."); return; }
        String sql;
        if (choice == 1) sql = "UPDATE users SET full_name=? WHERE user_id=?";
        else if (choice == 2) sql = "UPDATE users SET email=? WHERE user_id=?";
        else if (choice == 3) sql = "UPDATE users SET phone=? WHERE user_id=?";
        else if (choice == 4) sql = "UPDATE users SET address=? WHERE user_id=?";
        else sql = "UPDATE users SET full_name=?,email=?,phone=?,address=? WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (choice == 1) { ps.setString(1, readName("New full name: ")); ps.setInt(2, id); }
            else if (choice == 2) { ps.setString(1, readEmail("New email: ")); ps.setInt(2, id); }
            else if (choice == 3) { ps.setString(1, readPhone("New phone: ")); ps.setInt(2, id); }
            else if (choice == 4) { ps.setString(1, readText("New address: ", 5, 250)); ps.setInt(2, id); }
            else {
                ps.setString(1, readName("New full name: "));
                ps.setString(2, readEmail("New email: "));
                ps.setString(3, readPhone("New phone: "));
                ps.setString(4, readText("New address: ", 5, 250));
                ps.setInt(5, id);
            }
            System.out.println(ps.executeUpdate() == 1 ? "Citizen details updated successfully." : "No citizen updated.");
        }
    }

    private void deleteCitizen() throws SQLException {
        int id = readInt("Citizen ID to delete: ", 1, Integer.MAX_VALUE);
        if (!exists("SELECT 1 FROM users WHERE user_id=?", id)) { System.out.println("Citizen not found."); return; }
        if (!confirm("Delete citizen " + id + "? This cannot be undone (y/n): ")) return;
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM users WHERE user_id=?")) {
            ps.setInt(1,id); ps.executeUpdate(); System.out.println("Citizen deleted.");
        }
    }

    // -------------------- Officer operations --------------------
    private void registerOfficer() throws SQLException {
        System.out.println("\n--- Register Officer ---");
        String name = readName("Officer name: ");
        String department = readText("Department: ", 2, 100);
        String email = readEmail("Email: ");
        String phone = readPhone("Indian mobile number (10 digits): ");
        try (PreparedStatement ps = connection.prepareStatement("INSERT INTO officers(officer_name,department,email,phone,is_available) VALUES(?,?,?,?,TRUE)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,name); ps.setString(2,department); ps.setString(3,email); ps.setString(4,phone); ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) System.out.println("Officer registered successfully! Officer ID: " + keys.getInt(1)); }
        }
    }

    private void viewOfficers() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT officer_id,officer_name,department,email,phone,is_available FROM officers ORDER BY officer_id"); ResultSet rs = ps.executeQuery()) {
            System.out.printf("%-6s %-22s %-22s %-30s %-12s %-10s%n", "ID", "NAME", "DEPARTMENT", "EMAIL", "PHONE", "AVAILABLE"); int count=0;
            while(rs.next()) { count++; System.out.printf("%-6d %-22s %-22s %-30s %-12s %-10s%n",rs.getInt(1),clip(rs.getString(2),22),clip(rs.getString(3),22),clip(rs.getString(4),30),rs.getString(5),rs.getBoolean(6)?"YES":"NO"); }
            if(count==0) System.out.println("No officers found.");
        }
    }

    private void updateOfficer() throws SQLException {
        int id = readInt("Officer ID to update: ", 1, Integer.MAX_VALUE);
        if (!exists("SELECT 1 FROM officers WHERE officer_id=?", id)) { System.out.println("Officer not found."); return; }
        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Officer name only\n2. Department only\n3. Email only\n4. Phone only\n5. Availability only\n6. Update all details\n0. Cancel");
        int choice = readInt("Choose field(s): ", 0, 6);
        if (choice == 0) { System.out.println("Update cancelled."); return; }
        String sql;
        if (choice == 1) sql = "UPDATE officers SET officer_name=? WHERE officer_id=?";
        else if (choice == 2) sql = "UPDATE officers SET department=? WHERE officer_id=?";
        else if (choice == 3) sql = "UPDATE officers SET email=? WHERE officer_id=?";
        else if (choice == 4) sql = "UPDATE officers SET phone=? WHERE officer_id=?";
        else if (choice == 5) sql = "UPDATE officers SET is_available=? WHERE officer_id=?";
        else sql = "UPDATE officers SET officer_name=?,department=?,email=?,phone=? WHERE officer_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (choice == 1) { ps.setString(1, readName("New officer name: ")); ps.setInt(2, id); }
            else if (choice == 2) { ps.setString(1, readText("New department: ", 2, 100)); ps.setInt(2, id); }
            else if (choice == 3) { ps.setString(1, readEmail("New email: ")); ps.setInt(2, id); }
            else if (choice == 4) { ps.setString(1, readPhone("New phone: ")); ps.setInt(2, id); }
            else if (choice == 5) { ps.setBoolean(1, readInt("Availability (1 = available, 2 = unavailable): ", 1, 2) == 1); ps.setInt(2, id); }
            else {
                ps.setString(1, readName("New officer name: "));
                ps.setString(2, readText("New department: ", 2, 100));
                ps.setString(3, readEmail("New email: "));
                ps.setString(4, readPhone("New phone: "));
                ps.setInt(5, id);
            }
            System.out.println(ps.executeUpdate() == 1 ? "Officer details updated successfully." : "No officer updated.");
        }
    }

    private void deleteOfficer() throws SQLException {
        int id=readInt("Officer ID to delete: ",1,Integer.MAX_VALUE);
        if(!exists("SELECT 1 FROM officers WHERE officer_id=?",id)){System.out.println("Officer not found.");return;}
        if(!confirm("Delete officer " + id + "? (y/n): "))return;
        try(PreparedStatement ps=connection.prepareStatement("DELETE FROM officers WHERE officer_id=?")){ps.setInt(1,id);ps.executeUpdate();System.out.println("Officer deleted.");}
    }

    // -------------------- Complaint operations --------------------
    private void registerComplaint() throws SQLException {
        System.out.println("\n--- Register Complaint ---");
        int userId=readInt("Citizen ID: ",1,Integer.MAX_VALUE);
        if(!exists("SELECT 1 FROM users WHERE user_id=?",userId)){System.out.println("Citizen ID " + userId + " does not exist.");return;}
        for(int i=0;i<TYPES.length;i++)System.out.println((i+1)+". "+TYPES[i]);
        String type=TYPES[readInt("Complaint type: ",1,TYPES.length)-1];
        String description=readText("Detailed description: ",10,2000);
        String location=readText("Problem location: ",3,250);
        String priority=readChoice("Priority (LOW/MEDIUM/HIGH): ",PRIORITIES);
        try(PreparedStatement ps=connection.prepareStatement("INSERT INTO complaints(user_id,complaint_type,description,location,priority,status) VALUES(?,?,?,?,?,'REGISTERED')",Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1,userId);ps.setString(2,type);ps.setString(3,description);ps.setString(4,location);ps.setString(5,priority);ps.executeUpdate();
            try(ResultSet keys=ps.getGeneratedKeys()){if(keys.next())System.out.println("Complaint registered! Complaint ID: "+keys.getInt(1)+" | Type: "+type+" | Priority: "+priority+" | Status: REGISTERED");}
        }
    }

    private void assignOfficer() throws SQLException {
        int complaintId=readInt("Complaint ID: ",1,Integer.MAX_VALUE); int officerId=readInt("Officer ID: ",1,Integer.MAX_VALUE);
        connection.setAutoCommit(false);
        try {
            String status=null;
            try(PreparedStatement ps=connection.prepareStatement("SELECT status FROM complaints WHERE complaint_id=? FOR UPDATE")) {ps.setInt(1,complaintId);try(ResultSet rs=ps.executeQuery()){if(rs.next())status=rs.getString(1);}}
            if(status==null){connection.rollback();System.out.println("Complaint not found.");return;}
            if("RESOLVED".equals(status)||"REJECTED".equals(status)){connection.rollback();System.out.println("A resolved or rejected complaint cannot be assigned.");return;}
            boolean available=false;
            try(PreparedStatement ps=connection.prepareStatement("SELECT is_available FROM officers WHERE officer_id=? FOR UPDATE")){ps.setInt(1,officerId);try(ResultSet rs=ps.executeQuery()){if(rs.next())available=rs.getBoolean(1);else{connection.rollback();System.out.println("Officer not found.");return;}}}
            if(!available){connection.rollback();System.out.println("This officer is not available for assignment.");return;}
            try(PreparedStatement ps=connection.prepareStatement("UPDATE complaints SET officer_id=?,status='ASSIGNED',updated_at=CURRENT_TIMESTAMP WHERE complaint_id=?")){ps.setInt(1,officerId);ps.setInt(2,complaintId);ps.executeUpdate();}
            connection.commit(); System.out.println("Officer assigned successfully. Complaint status: ASSIGNED.");
        } catch(SQLException e) { connection.rollback(); throw e; }
        finally { connection.setAutoCommit(true); }
    }

    private void updateComplaintStatus() throws SQLException {
        int id=readInt("Complaint ID: ",1,Integer.MAX_VALUE);
        String current=null;
        try(PreparedStatement ps=connection.prepareStatement("SELECT status FROM complaints WHERE complaint_id=?")){ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){if(rs.next())current=rs.getString(1);}}
        if(current==null){System.out.println("Complaint not found.");return;}
        System.out.println("Current status: "+current+" | Allowed statuses: "+STATUSES);
        String next=readChoice("New status: ",STATUSES);
        if("RESOLVED".equals(current)||"REJECTED".equals(current)){System.out.println("Closed complaints cannot be reopened through this menu.");return;}
        if(!validTransition(current,next)){System.out.println("Invalid transition from "+current+" to "+next+".");return;}
        String resolution=null;
        if("RESOLVED".equals(next))resolution=readText("Resolution details: ",5,2000);
        String sql="UPDATE complaints SET status=?,resolution_details=?,resolved_at="+("RESOLVED".equals(next)?"CURRENT_TIMESTAMP":"NULL")+",updated_at=CURRENT_TIMESTAMP WHERE complaint_id=?";
        try(PreparedStatement ps=connection.prepareStatement(sql)){ps.setString(1,next);if(resolution==null)ps.setNull(2,Types.VARCHAR);else ps.setString(2,resolution);ps.setInt(3,id);ps.executeUpdate();}
        System.out.println("Complaint status updated successfully. Current status: "+next);
    }

    private boolean validTransition(String current,String next) {
        if(current.equals(next))return true;
        if("REGISTERED".equals(current))return "ASSIGNED".equals(next)||"REJECTED".equals(next);
        if("ASSIGNED".equals(current))return "IN_PROGRESS".equals(next)||"REJECTED".equals(next);
        if("IN_PROGRESS".equals(current))return "RESOLVED".equals(next)||"REJECTED".equals(next);
        return false;
    }

    private void viewComplaintResolution() throws SQLException {
        int id=readInt("Complaint ID: ",1,Integer.MAX_VALUE);
        String sql="SELECT c.complaint_id,u.full_name,c.complaint_type,c.description,c.location,c.priority,o.officer_name,o.department,c.status,c.created_at,c.updated_at,c.resolution_details,c.resolved_at FROM complaints c JOIN users u ON c.user_id=u.user_id LEFT JOIN officers o ON c.officer_id=o.officer_id WHERE c.complaint_id=?";
        try(PreparedStatement ps=connection.prepareStatement(sql)){ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){if(!rs.next()){System.out.println("Complaint not found.");return;}
            System.out.println("\n--- Complaint Details ---");
            System.out.println("Complaint ID: "+rs.getInt("complaint_id"));System.out.println("Citizen: "+rs.getString("full_name"));System.out.println("Type: "+rs.getString("complaint_type"));System.out.println("Description: "+rs.getString("description"));System.out.println("Location: "+rs.getString("location"));System.out.println("Priority: "+rs.getString("priority"));System.out.println("Assigned officer: "+(rs.getString("officer_name")==null?"Not assigned":rs.getString("officer_name")));System.out.println("Department: "+(rs.getString("department")==null?"Not assigned":rs.getString("department")));System.out.println("Status: "+rs.getString("status"));System.out.println("Registered: "+rs.getTimestamp("created_at"));System.out.println("Last updated: "+rs.getTimestamp("updated_at"));System.out.println("Resolution: "+(rs.getString("resolution_details")==null?"Resolution pending":rs.getString("resolution_details")));System.out.println("Resolved at: "+(rs.getTimestamp("resolved_at")==null?"Not resolved":rs.getTimestamp("resolved_at")));
        }}
    }

    private void viewAllComplaints() throws SQLException { listComplaints("", null, null); }
    private void viewCitizenComplaints() throws SQLException { int id=readInt("Citizen ID: ",1,Integer.MAX_VALUE); listComplaints(" WHERE c.user_id=?",id,null); }
    private void viewOfficerComplaints() throws SQLException { int id=readInt("Officer ID: ",1,Integer.MAX_VALUE); listComplaints(" WHERE c.officer_id=?",id,null); }

    private void searchComplaints() throws SQLException {
        System.out.println("1. By status\n2. By priority\n3. By complaint ID"); int c=readInt("Search option: ",1,3);
        if(c==1)listComplaints(" WHERE c.status=?",null,readChoice("Status: ",STATUSES));
        else if(c==2)listComplaints(" WHERE c.priority=?",null,readChoice("Priority: ",PRIORITIES));
        else viewComplaintResolution();
    }

    private void listComplaints(String where,Integer id,String value) throws SQLException {
        String sql="SELECT c.complaint_id,u.full_name,c.complaint_type,c.priority,c.status,COALESCE(o.officer_name,'Not assigned') AS officer,c.location,c.created_at FROM complaints c JOIN users u ON c.user_id=u.user_id LEFT JOIN officers o ON c.officer_id=o.officer_id"+where+" ORDER BY c.created_at DESC LIMIT 500";
        try(PreparedStatement ps=connection.prepareStatement(sql)){if(id!=null)ps.setInt(1,id);else if(value!=null)ps.setString(1,value);try(ResultSet rs=ps.executeQuery()){int count=0;System.out.printf("%-6s %-20s %-22s %-8s %-13s %-20s %-22s %-20s%n","ID","CITIZEN","TYPE","PRIORITY","STATUS","OFFICER","LOCATION","CREATED");while(rs.next()){count++;System.out.printf("%-6d %-20s %-22s %-8s %-13s %-20s %-22s %-20s%n",rs.getInt("complaint_id"),clip(rs.getString("full_name"),20),clip(rs.getString("complaint_type"),22),rs.getString("priority"),rs.getString("status"),clip(rs.getString("officer"),20),clip(rs.getString("location"),22),rs.getTimestamp("created_at"));}if(count==0)System.out.println("No matching complaints found.");}}
    }

    private void updateComplaintDetails() throws SQLException {
        int id = readInt("Complaint ID: ", 1, Integer.MAX_VALUE);
        if (!exists("SELECT 1 FROM complaints WHERE complaint_id=?", id)) { System.out.println("Complaint not found."); return; }
        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Description only\n2. Location only\n3. Priority only\n4. Update all editable details\n0. Cancel");
        System.out.println("Note: Complaint status and resolution are changed only in the status menu.");
        int choice = readInt("Choose field(s): ", 0, 4);
        if (choice == 0) { System.out.println("Update cancelled."); return; }
        String sql;
        if (choice == 1) sql = "UPDATE complaints SET description=?,updated_at=CURRENT_TIMESTAMP WHERE complaint_id=?";
        else if (choice == 2) sql = "UPDATE complaints SET location=?,updated_at=CURRENT_TIMESTAMP WHERE complaint_id=?";
        else if (choice == 3) sql = "UPDATE complaints SET priority=?,updated_at=CURRENT_TIMESTAMP WHERE complaint_id=?";
        else sql = "UPDATE complaints SET description=?,location=?,priority=?,updated_at=CURRENT_TIMESTAMP WHERE complaint_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (choice == 1) { ps.setString(1, readText("Updated description: ", 10, 2000)); ps.setInt(2, id); }
            else if (choice == 2) { ps.setString(1, readText("Updated location: ", 3, 250)); ps.setInt(2, id); }
            else if (choice == 3) { ps.setString(1, readChoice("Priority (LOW/MEDIUM/HIGH): ", PRIORITIES)); ps.setInt(2, id); }
            else {
                ps.setString(1, readText("Updated description: ", 10, 2000));
                ps.setString(2, readText("Updated location: ", 3, 250));
                ps.setString(3, readChoice("Priority (LOW/MEDIUM/HIGH): ", PRIORITIES));
                ps.setInt(4, id);
            }
            System.out.println(ps.executeUpdate() == 1 ? "Complaint details updated successfully. Status was not changed." : "No complaint updated.");
        }
    }

    private void deleteComplaint() throws SQLException {
        int id=readInt("Complaint ID to delete: ",1,Integer.MAX_VALUE);
        if(!exists("SELECT 1 FROM complaints WHERE complaint_id=?",id)){System.out.println("Complaint not found.");return;}
        if(!confirm("Permanently delete complaint "+id+"? (y/n): "))return;
        try(PreparedStatement ps=connection.prepareStatement("DELETE FROM complaints WHERE complaint_id=?")){ps.setInt(1,id);ps.executeUpdate();System.out.println("Complaint deleted.");}
    }

    private boolean exists(String sql,int id) throws SQLException {try(PreparedStatement ps=connection.prepareStatement(sql)){ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){return rs.next();}}}

    // -------------------- Reusable input validation --------------------
    private String readName(String prompt) {
        while(true){String s=readLine(prompt).trim().replaceAll("\\s+"," ");if(NAME_PATTERN.matcher(s).matches()&&!s.matches(".*\\d.*"))return s;System.out.println("Enter a valid name (letters, spaces, apostrophes, periods or hyphens).");}
    }
    private String readEmail(String prompt) {
        while(true){String s=readLine(prompt).trim().toLowerCase();if(s.length()<=150&&EMAIL_PATTERN.matcher(s).matches())return s;System.out.println("Enter a valid email, for example name@example.com.");}
    }
    private String readPhone(String prompt) {
        while(true){String s=readLine(prompt).trim();if(PHONE_PATTERN.matcher(s).matches())return s;System.out.println("Enter a valid 10-digit Indian mobile number starting with 6, 7, 8 or 9.");}
    }
    private String readText(String prompt,int min,int max) {
        while(true){String s=readLine(prompt).trim();if(s.length()>=min&&s.length()<=max)return s;System.out.println("Input must contain between "+min+" and "+max+" characters.");}
    }
    private String readChoice(String prompt,Set<String> choices) {
        while(true){String s=readLine(prompt).trim().toUpperCase();if(choices.contains(s))return s;System.out.println("Allowed values: "+choices);}
    }
    private int readInt(String prompt,int min,int max) {
        while(true){String s=readLine(prompt).trim();try{if(!s.matches("\\d+"))throw new NumberFormatException();int n=Integer.parseInt(s);if(n>=min&&n<=max)return n;}catch(NumberFormatException ignored){}System.out.println("Enter a whole number between "+min+" and "+max+".");}
    }
    private String readLine(String prompt){System.out.print(prompt);return scanner.nextLine();}
    private boolean confirm(String prompt){while(true){String s=readLine(prompt).trim();if(s.equalsIgnoreCase("y"))return true;if(s.equalsIgnoreCase("n"))return false;System.out.println("Enter y or n.");}}
    private void pause(){System.out.print("\nPress Enter to continue...");scanner.nextLine();}
    private void pauseSubmenu(){System.out.print("\nPress Enter to return to this menu...");scanner.nextLine();}
    private String clip(String value,int max){if(value==null)return "";return value.length()<=max?value:value.substring(0,max-3)+"...";}

    private void printSqlError(SQLException e) {
        String state=e.getSQLState();int code=e.getErrorCode();
        if(code==1062)System.out.println("Error: This email address or phone number is already registered.");
        else if(code==1451||code==1452)System.out.println("Error: This record is linked to other records. Remove or reassign the related records first.");
        else if(state!=null&&state.startsWith("08"))System.out.println("Database connection lost. Check that MySQL is running.");
        else {System.out.println("Database operation failed: "+e.getMessage());}
    }
}
