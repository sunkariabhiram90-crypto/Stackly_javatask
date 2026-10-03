package com;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Beginner-friendly console menus and JDBC operations.
 * Fixed-slot policy: a date/time is one appointment slot. CANCELLED appointments do not block it.
 */
public class HospitalManagementSystem {
    private final Scanner sc;
    private final Connection con;

    public HospitalManagementSystem(Scanner scanner, Connection connection) {
        this.sc = scanner;
        this.con = connection;
    }

    public void run() {
        boolean running = true;
        while (running) {
            heading("HOSPITAL MANAGEMENT SYSTEM");
            System.out.println("1. Patient Management\n2. Doctor Management\n3. Appointment Management");
            System.out.println("4. Prescription Management\n5. Reports and Search\n0. Exit");
            int choice = intInput("Enter your choice: ", 0, 5);
            switch (choice) {
                case 1: patientMenu(); break;
                case 2: doctorMenu(); break;
                case 3: appointmentMenu(); break;
                case 4: prescriptionMenu(); break;
                case 5: reportsMenu(); break;
                case 0: running = false; break;
                default: break;
            }
        }
    }

    private void patientMenu() {
        menuLoop("PATIENT MANAGEMENT", new String[]{
            "Register New Patient","View All Patients","Search Patient by ID","Search Patient by Name or Phone",
            "Update Patient Details","Delete Patient","View Patient Appointments","View Patient Prescription History"
        }, choice -> {
            switch (choice) {
                case 1: registerPatient(); break;
                case 2: listPatients(); break;
                case 3: patientById(); break;
                case 4: searchPatient(); break;
                case 5: updatePatient(); break;
                case 6: deletePatient(); break;
                case 7: patientAppointments(); break;
                case 8: patientPrescriptions(); break;
                default: return false;
            }
            return true;
        });
    }

    private void doctorMenu() {
        menuLoop("DOCTOR MANAGEMENT", new String[]{
            "Register New Doctor","View All Doctors","Search Doctor by ID","Search Doctors by Specialization",
            "Update Doctor Details","Change Doctor Availability","Delete Doctor","View Doctor Appointments"
        }, choice -> {
            switch (choice) {
                case 1: registerDoctor(); break;
                case 2: listDoctors(); break;
                case 3: doctorById(); break;
                case 4: searchDoctorSpecialization(); break;
                case 5: updateDoctor(); break;
                case 6: changeDoctorAvailability(); break;
                case 7: deleteDoctor(); break;
                case 8: doctorAppointments(); break;
                default: return false;
            }
            return true;
        });
    }

    private void appointmentMenu() {
        menuLoop("APPOINTMENT MANAGEMENT", new String[]{
            "Book Appointment","View All Appointments","View Appointment by ID","Search Appointments by Patient",
            "Search Appointments by Doctor","Reschedule Appointment","Update Appointment Status",
            "Cancel Appointment","Delete Appointment"
        }, choice -> {
            switch (choice) {
                case 1: bookAppointment(); break;
                case 2: listAppointments(""); break;
                case 3: appointmentById(); break;
                case 4: appointmentsByPatient(); break;
                case 5: appointmentsByDoctor(); break;
                case 6: rescheduleAppointment(); break;
                case 7: updateAppointmentStatus(); break;
                case 8: cancelAppointment(); break;
                case 9: deleteAppointment(); break;
                default: return false;
            }
            return true;
        });
    }

    private void prescriptionMenu() {
        menuLoop("PRESCRIPTION MANAGEMENT", new String[]{
            "Add Prescription","View Prescription by ID","View Prescriptions by Patient",
            "View Prescriptions by Doctor","Update Prescription","Delete Prescription"
        }, choice -> {
            switch (choice) {
                case 1: addPrescription(); break;
                case 2: prescriptionById(); break;
                case 3: prescriptionsByPatient(); break;
                case 4: prescriptionsByDoctor(); break;
                case 5: updatePrescription(); break;
                case 6: deletePrescription(); break;
                default: return false;
            }
            return true;
        });
    }

    private void reportsMenu() {
        menuLoop("REPORTS AND SEARCH", new String[]{
            "Search Patients","Search Doctors","Search Appointments by Date","View Today's Appointments",
            "View Upcoming Appointments","View Completed Appointments","View Cancelled Appointments",
            "View Patient Treatment History","View Doctor Appointment Summary"
        }, choice -> {
            switch (choice) {
                case 1: searchPatient(); break;
                case 2: searchDoctorSpecialization(); break;
                case 3: appointmentsByDate(); break;
                case 4: listAppointments(" AND a.appointment_date = CURDATE()"); break;
                case 5: listAppointments(" AND a.appointment_date >= CURDATE() AND a.status IN ('BOOKED','CONFIRMED')"); break;
                case 6: listAppointments(" AND a.status = 'COMPLETED'"); break;
                case 7: listAppointments(" AND a.status = 'CANCELLED'"); break;
                case 8: patientTreatmentHistory(); break;
                case 9: doctorSummary(); break;
                default: return false;
            }
            return true;
        });
    }

    @FunctionalInterface private interface MenuAction { boolean execute(int choice) throws SQLException; }

    private void menuLoop(String title, String[] options, MenuAction action) {
        boolean back = false;
        while (!back) {
            heading(title);
            for (int i = 0; i < options.length; i++) System.out.printf("%2d. %s%n", i + 1, options[i]);
            System.out.println(" 0. Back");
            int choice = intInput("Enter your choice: ", 0, options.length);
            if (choice == 0) back = true;
            else try { back = !action.execute(choice); }
            catch (SQLException e) { sqlError(e); }
            catch (CancelOperation e) { cancelled(); }
            pause();
        }
    }

    private void heading(String title) {
        System.out.println("\n============================================================");
        System.out.println("                 " + title);
        System.out.println("============================================================");
    }
    private void pause() { System.out.print("\nPress Enter to continue..."); sc.nextLine(); }
    private String line(String prompt) { System.out.print(prompt); return sc.nextLine().trim(); }
    private String required(String prompt, int max) {
        while (true) {
            String s = line(prompt);
            if (s.equalsIgnoreCase("!q")) throw new CancelOperation();
            if (!s.isEmpty() && s.length() <= max) return s;
            System.out.println("Required field. Enter 1-" + max + " characters, or !q to cancel.");
        }
    }
    private String optional(String prompt, int max) {
        while (true) {
            String s = line(prompt);
            if (s.equalsIgnoreCase("!q")) throw new CancelOperation();
            if (s.length() <= max) return s.isEmpty() ? null : s;
            System.out.println("Maximum length is " + max + " characters.");
        }
    }
    private int intInput(String prompt, int min, int max) {
        while (true) {
            String s = line(prompt);
            try {
                if (!s.matches("\\d+")) throw new NumberFormatException();
                int n = Integer.parseInt(s);
                if (n >= min && n <= max) return n;
            } catch (NumberFormatException ignored) { }
            System.out.println("Enter a whole number from " + min + " to " + max + ".");
        }
    }
    private int positiveId(String prompt) { return intInput(prompt, 1, Integer.MAX_VALUE); }
    private boolean confirm(String prompt) {
        while (true) {
            String s = line(prompt + " (Y/N): ");
            if (s.equalsIgnoreCase("Y")) return true;
            if (s.equalsIgnoreCase("N")) return false;
            System.out.println("Enter Y or N.");
        }
    }
    private String validName(String prompt) {
        while (true) {
            String s = required(prompt, 100);
            if (s.matches("(?U)[\\p{L}][\\p{L} .'-]*")) return s;
            System.out.println("Use letters, spaces, apostrophes, periods, or hyphens.");
        }
    }
    private String phone(String prompt) {
        while (true) {
            String s = required(prompt, 10);
            if (s.matches("[6-9]\\d{9}")) return s;
            System.out.println("Enter a valid 10-digit Indian mobile number starting with 6-9.");
        }
    }
    private String email(String prompt, boolean allowBlank) {
        while (true) {
            String s = allowBlank ? Optional.ofNullable(optional(prompt, 254)).orElse("") : required(prompt, 254);
            if (s.isEmpty() && allowBlank) return null;
            if (s.matches("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+$")) return s;
            System.out.println("Enter a valid email address" + (allowBlank ? " or leave blank." : "."));
        }
    }
    private LocalDate date(String prompt, boolean futureAllowed) {
        while (true) {
            String s = required(prompt, 10);
            try {
                LocalDate d = LocalDate.parse(s);
                if (futureAllowed || !d.isAfter(LocalDate.now())) return d;
                System.out.println("Date cannot be in the future.");
            } catch (DateTimeParseException e) { System.out.println("Expected yyyy-MM-dd, for example 1998-05-12."); }
        }
    }
    private LocalDate futureDate(String prompt) {
        while (true) {
            LocalDate d = date(prompt, true);
            if (d.isAfter(LocalDate.now()) || (d.equals(LocalDate.now()))) return d;
            System.out.println("Appointment date cannot be in the past.");
        }
    }
    private LocalTime time(String prompt) {
        while (true) {
            String s = required(prompt, 5);
            try { return LocalTime.parse(s); }
            catch (DateTimeParseException e) { System.out.println("Expected 24-hour HH:mm, for example 14:30."); }
        }
    }
    private LocalDateTimePair futureSlot() {
        while (true) {
            LocalDate d = futureDate("Appointment date (yyyy-MM-dd): ");
            LocalTime t = time("Appointment time (HH:mm): ");
            if (d.isAfter(LocalDate.now()) || t.isAfter(LocalTime.now())) return new LocalDateTimePair(d, t);
            System.out.println("Appointment must be in the future.");
        }
    }
    private static class LocalDateTimePair {
        LocalDate date; LocalTime time;
        LocalDateTimePair(LocalDate d, LocalTime t) { date=d; time=t; }
    }
    private static class CancelOperation extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
    private void sqlError(SQLException e) {
        String state = e.getSQLState();
        if (e.getErrorCode() == 1062) System.out.println("ERROR: A unique value (phone/email) already exists.");
        else if (e.getErrorCode() == 1451 || e.getErrorCode() == 1452)
            System.out.println("ERROR: This operation conflicts with a related database record.");
        else System.out.println("Database operation failed: " + e.getMessage());
    }
    private void success(String message) { System.out.println("SUCCESS: " + message); }
    private void cancelled() { System.out.println("Operation cancelled. No changes were saved."); }

    private boolean exists(String table, String idColumn, int id) throws SQLException {
        String sql = "SELECT 1 FROM " + table + " WHERE " + idColumn + "=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
    private boolean existsValue(String table, String column, String value, int exceptId, String idColumn) throws SQLException {
        String sql = "SELECT 1 FROM " + table + " WHERE " + column + "=? AND " + idColumn + "<>?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, value); ps.setInt(2, exceptId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
    private int generatedId(PreparedStatement ps) throws SQLException {
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) return rs.getInt(1);
            throw new SQLException("MySQL did not return a generated ID.");
        }
    }
    private void printQuery(String sql, Object... params) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i=0; i<params.length; i++) ps.setObject(i+1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int count = md.getColumnCount();
                int rows = 0;
                while (rs.next()) {
                    rows++;
                    for (int i=1; i<=count; i++) System.out.printf("%-22s: %s%n", md.getColumnLabel(i), Objects.toString(rs.getObject(i), "-"));
                    System.out.println("------------------------------------------------------------");
                }
                if (rows == 0) System.out.println("No records found.");
                else System.out.println("Records found: " + rows);
            }
        }
    }

    // ---------------- PATIENTS ----------------
    private void registerPatient() throws SQLException {
        try {
            String name=validName("Full name: ");
            LocalDate dob=date("Date of birth (yyyy-MM-dd): ", false);
            String gender=enumValue("Gender [MALE/FEMALE/OTHER]: ", "MALE","FEMALE","OTHER");
            String ph=phone("Phone: ");
            String em=email("Email (optional, Enter to skip): ", true);
            String address=required("Address: ",250);
            String bg=enumOptional("Blood group [A+, A-, B+, B-, AB+, AB-, O+, O-] (optional): ",
                    "A+","A-","B+","B-","AB+","AB-","O+","O-");
            String sql="INSERT INTO patients(full_name,date_of_birth,gender,phone,email,address,blood_group) VALUES(?,?,?,?,?,?,?)";
            try (PreparedStatement ps=con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1,name); ps.setDate(2,java.sql.Date.valueOf(dob)); ps.setString(3,gender); ps.setString(4,ph);
                ps.setString(5,em); ps.setString(6,address); ps.setString(7,bg);
                ps.executeUpdate(); success("Patient registered. Patient ID: " + generatedId(ps));
            }
        } catch (CancelOperation e) { cancelled(); }
    }
    private void listPatients() throws SQLException {
        printQuery("SELECT patient_id,full_name,date_of_birth,gender,phone,email,address,blood_group FROM patients ORDER BY patient_id");
    }
    private void patientById() throws SQLException {
        int id=positiveId("Patient ID: ");
        printQuery("SELECT patient_id,full_name,date_of_birth,gender,phone,email,address,blood_group FROM patients WHERE patient_id=?",id);
    }
    private void searchPatient() throws SQLException {
        String q=required("Enter name or phone search text: ",100);
        printQuery("SELECT patient_id,full_name,date_of_birth,gender,phone,email,address,blood_group FROM patients WHERE full_name LIKE ? OR phone LIKE ? ORDER BY full_name","%"+q+"%","%"+q+"%");
    }
    private void updatePatient() throws SQLException {
        int id=positiveId("Patient ID: ");
        if (!exists("patients","patient_id",id)) { System.out.println("Patient not found."); return; }
        Map<Integer,Object> values=new LinkedHashMap<>();
        boolean done=false;
        while (!done) {
            System.out.println("1.Name  2.Date of Birth  3.Gender  4.Phone  5.Email  6.Address  7.Blood Group");
            System.out.println("8.Multiple fields  9.All fields  0.Save/Cancel");
            int c=intInput("Select field: ",0,9);
            if (c==0) { done=true; continue; }
            if (c==9) { values.clear(); for(int i=1;i<=7;i++) values.put(i,patientField(i,id)); done=true; }
            else if(c==8) {
                while(true) {
                    int f=intInput("Field to change (1-7, 0=finish): ",0,7);
                    if(f==0) break;
                    values.put(f,patientField(f,id));
                }
            } else values.put(c,patientField(c,id));
        }
        if(values.isEmpty()) { cancelled(); return; }
        if(!confirm("Save "+values.size()+" field update(s)?")) { cancelled(); return; }
        String[] cols={"full_name","date_of_birth","gender","phone","email","address","blood_group"};
        StringBuilder sql=new StringBuilder("UPDATE patients SET ");
        for(Integer f:values.keySet()) { if(sql.charAt(sql.length()-1)!=' ') sql.append(","); sql.append(cols[f-1]).append("=?"); }
        sql.append(" WHERE patient_id=?");
        try(PreparedStatement ps=con.prepareStatement(sql.toString())) {
            int i=1; for(Map.Entry<Integer,Object> e:values.entrySet()) bind(ps,i++,e.getKey(),e.getValue());
            ps.setInt(i,id); ps.executeUpdate(); success("Patient updated. Fields: "+values.keySet());
        }
    }
    private Object patientField(int f,int id) throws SQLException {
        switch(f) {
            case 1: return validName("New name: ");
            case 2: return java.sql.Date.valueOf(date("New date of birth (yyyy-MM-dd): ",false));
            case 3: return enumValue("New gender [MALE/FEMALE/OTHER]: ","MALE","FEMALE","OTHER");
            case 4: { String p=phone("New phone: "); if(existsValue("patients","phone",p,id,"patient_id")) throw new CancelOperation(); return p; }
            case 5: { String e=email("New email (blank clears it): ",true); if(e!=null && existsValue("patients","email",e,id,"patient_id")) throw new CancelOperation(); return e; }
            case 6: return required("New address: ",250);
            default: return enumOptional("New blood group (blank clears it): ","A+","A-","B+","B-","AB+","AB-","O+","O-");
        }
    }
    private void deletePatient() throws SQLException {
        int id=positiveId("Patient ID: ");
        printQuery("SELECT patient_id,full_name,phone FROM patients WHERE patient_id=?",id);
        if (!exists("patients","patient_id",id)) return;
        if (!confirm("Delete this patient?")) { cancelled(); return; }
        try(PreparedStatement ps=con.prepareStatement("DELETE FROM patients WHERE patient_id=?")) {
            ps.setInt(1,id); ps.executeUpdate(); success("Patient deleted.");
        } catch(SQLException e) { System.out.println("Patient has related appointment history and cannot be deleted. Keep the history intact."); }
    }
    private void patientAppointments() throws SQLException {
        int id=positiveId("Patient ID: ");
        listAppointments(" AND a.patient_id = "+id);
    }
    private void patientPrescriptions() throws SQLException {
        int id=positiveId("Patient ID: ");
        printQuery(prescriptionJoin()+" WHERE a.patient_id=? ORDER BY pr.created_at DESC",id);
    }

    // ---------------- DOCTORS ----------------
    private void registerDoctor() throws SQLException {
        try {
            String n=validName("Doctor name: ");
            String s=required("Specialization: ",100);
            String p=phone("Phone: ");
            String e=email("Email: ",false);
            BigDecimal fee=fee("Consultation fee: ");
            try(PreparedStatement ps=con.prepareStatement("INSERT INTO doctors(full_name,specialization,phone,email,consultation_fee) VALUES(?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1,n); ps.setString(2,s); ps.setString(3,p); ps.setString(4,e); ps.setBigDecimal(5,fee);
                ps.executeUpdate(); success("Doctor registered. Doctor ID: "+generatedId(ps));
            }
        } catch(CancelOperation e) { cancelled(); }
    }
    private void listDoctors() throws SQLException {
        printQuery("SELECT doctor_id,full_name,specialization,phone,email,consultation_fee,availability_status FROM doctors ORDER BY doctor_id");
    }
    private void doctorById() throws SQLException {
        printQuery("SELECT doctor_id,full_name,specialization,phone,email,consultation_fee,availability_status FROM doctors WHERE doctor_id=?",positiveId("Doctor ID: "));
    }
    private void searchDoctorSpecialization() throws SQLException {
        String s=required("Specialization/name search: ",100);
        printQuery("SELECT doctor_id,full_name,specialization,phone,email,consultation_fee,availability_status FROM doctors WHERE specialization LIKE ? OR full_name LIKE ? ORDER BY full_name","%"+s+"%","%"+s+"%");
    }
    private void updateDoctor() throws SQLException {
        int id=positiveId("Doctor ID: ");
        if(!exists("doctors","doctor_id",id)){System.out.println("Doctor not found.");return;}
        Map<Integer,Object> vals=new LinkedHashMap<>(); boolean done=false;
        while(!done){
            System.out.println("1.Name  2.Specialization  3.Phone  4.Email  5.Fee  6.Multiple fields  7.All fields  0.Finish");
            int c=intInput("Choice: ",0,7);
            if(c==0)done=true;
            else if(c==7){vals.clear();for(int f=1;f<=5;f++)vals.put(f,doctorField(f,id));done=true;}
            else if(c==6){while(true){int f=intInput("Field (1-5, 0=finish): ",0,5);if(f==0)break;vals.put(f,doctorField(f,id));}}
            else vals.put(c,doctorField(c,id));
        }
        if(vals.isEmpty()||!confirm("Save "+vals.size()+" field update(s)?")){cancelled();return;}
        String[] cols={"full_name","specialization","phone","email","consultation_fee"};
        StringBuilder sql=new StringBuilder("UPDATE doctors SET ");
        for(Integer f:vals.keySet()){if(sql.charAt(sql.length()-1)!=' ')sql.append(",");sql.append(cols[f-1]).append("=?");}
        sql.append(" WHERE doctor_id=?");
        try(PreparedStatement ps=con.prepareStatement(sql.toString())){
            int i=1;for(Map.Entry<Integer,Object> e:vals.entrySet())bind(ps,i++,e.getKey(),e.getValue());
            ps.setInt(i,id);ps.executeUpdate();success("Doctor details updated.");
        }
    }
    private Object doctorField(int f,int id)throws SQLException{
        switch(f){
            case 1:return validName("New name: ");
            case 2:return required("New specialization: ",100);
            case 3:{String p=phone("New phone: ");if(existsValue("doctors","phone",p,id,"doctor_id"))throw new CancelOperation();return p;}
            case 4:{String e=email("New email: ",false);if(existsValue("doctors","email",e,id,"doctor_id"))throw new CancelOperation();return e;}
            default:return fee("New consultation fee: ");
        }
    }
    private void changeDoctorAvailability()throws SQLException{
        int id=positiveId("Doctor ID: ");
        try(PreparedStatement ps=con.prepareStatement("SELECT availability_status FROM doctors WHERE doctor_id=?")){
            ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){
                if(!rs.next()){System.out.println("Doctor not found.");return;}
                String current=rs.getString(1);System.out.println("Current status: "+current);
                String next=current.equals("ACTIVE")?"INACTIVE":"ACTIVE";
                if(confirm("Change status to "+next+"?"))try(PreparedStatement up=con.prepareStatement("UPDATE doctors SET availability_status=? WHERE doctor_id=?")){
                    up.setString(1,next);up.setInt(2,id);up.executeUpdate();success("Doctor status changed to "+next);
                }else cancelled();
            }
        }
    }
    private void deleteDoctor()throws SQLException{
        int id=positiveId("Doctor ID: ");printQuery("SELECT doctor_id,full_name,specialization,availability_status FROM doctors WHERE doctor_id=?",id);
        if(!exists("doctors","doctor_id",id))return;
        if(!confirm("Delete this doctor?")){cancelled();return;}
        try(PreparedStatement ps=con.prepareStatement("DELETE FROM doctors WHERE doctor_id=?")){
            ps.setInt(1,id);ps.executeUpdate();success("Doctor deleted.");
        }catch(SQLException e){System.out.println("Doctor has appointment history and cannot be deleted. Mark the doctor INACTIVE instead.");}
    }
    private void doctorAppointments()throws SQLException{int id=positiveId("Doctor ID: ");listAppointments(" AND a.doctor_id = "+id);}

    // ---------------- APPOINTMENTS ----------------
    private void bookAppointment()throws SQLException{
        try{
            int pid=positiveId("Patient ID: "),did=positiveId("Doctor ID: ");
            if(!exists("patients","patient_id",pid)){System.out.println("Patient not found.");return;}
            if(!doctorActive(did)){System.out.println("Doctor not found or inactive.");return;}
            LocalDateTimePair slot=futureSlot();
            String reason=required("Reason for visit: ",250);
            con.setAutoCommit(false);
            try{
                // Lock doctor row to serialize concurrent bookings for the same doctor.
                try(PreparedStatement lock=con.prepareStatement("SELECT doctor_id FROM doctors WHERE doctor_id=? AND availability_status='ACTIVE' FOR UPDATE")){
                    lock.setInt(1,did);try(ResultSet rs=lock.executeQuery()){if(!rs.next())throw new SQLException("Doctor is inactive.");}
                }
                if(slotTaken("doctor_id",did,slot.date,slot.time,0)||slotTaken("patient_id",pid,slot.date,slot.time,0))
                    throw new SQLException("SLOT_CONFLICT");
                try(PreparedStatement ps=con.prepareStatement("INSERT INTO appointments(patient_id,doctor_id,appointment_date,appointment_time,reason,status) VALUES(?,?,?,?,?,'BOOKED')",Statement.RETURN_GENERATED_KEYS)){
                    ps.setInt(1,pid);ps.setInt(2,did);ps.setDate(3,java.sql.Date.valueOf(slot.date));ps.setTime(4,Time.valueOf(slot.time));ps.setString(5,reason);
                    ps.executeUpdate();int id=generatedId(ps);con.commit();success("Appointment booked. Appointment ID: "+id+" | Status: BOOKED");
                }
            }catch(SQLException e){con.rollback();if("SLOT_CONFLICT".equals(e.getMessage()))System.out.println("ERROR: Patient or doctor already has an active appointment in this slot.");else throw e;}
            finally{con.setAutoCommit(true);}
        }catch(CancelOperation e){cancelled();}
    }
    private boolean doctorActive(int id)throws SQLException{
        try(PreparedStatement ps=con.prepareStatement("SELECT 1 FROM doctors WHERE doctor_id=? AND availability_status='ACTIVE'")){
            ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){return rs.next();}
        }
    }
    private boolean slotTaken(String column,int id,LocalDate d,LocalTime t,int exclude)throws SQLException{
        if(!column.equals("doctor_id")&&!column.equals("patient_id"))throw new IllegalArgumentException("Invalid column");
        String sql="SELECT appointment_id FROM appointments WHERE "+column+"=? AND appointment_date=? AND appointment_time=? AND status IN ('BOOKED','CONFIRMED') AND appointment_id<>? FOR UPDATE";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setInt(1,id);ps.setDate(2,java.sql.Date.valueOf(d));ps.setTime(3,Time.valueOf(t));ps.setInt(4,exclude);
            try(ResultSet rs=ps.executeQuery()){return rs.next();}
        }
    }
    private void listAppointments(String extra)throws SQLException{
        // extra is selected only from internal fixed strings or validated integer IDs.
        String sql=appointmentJoin()+" WHERE 1=1 "+extra+" ORDER BY a.appointment_date,a.appointment_time";
        printQuery(sql);
    }
    private String appointmentJoin(){
        return "SELECT a.appointment_id,a.appointment_date,a.appointment_time,a.status,a.reason,"+
               "p.patient_id,p.full_name AS patient_name,d.doctor_id,d.full_name AS doctor_name,d.specialization "+
               "FROM appointments a JOIN patients p ON p.patient_id=a.patient_id JOIN doctors d ON d.doctor_id=a.doctor_id";
    }
    private void appointmentById()throws SQLException{printQuery(appointmentJoin()+" WHERE a.appointment_id=?",positiveId("Appointment ID: "));}
    private void appointmentsByPatient()throws SQLException{listAppointments(" AND a.patient_id = "+positiveId("Patient ID: "));}
    private void appointmentsByDoctor()throws SQLException{listAppointments(" AND a.doctor_id = "+positiveId("Doctor ID: "));}
    private void appointmentsByDate()throws SQLException{LocalDate d=date("Date (yyyy-MM-dd): ",true);listAppointments(" AND a.appointment_date = '"+d+"'");}
    private void rescheduleAppointment()throws SQLException{
        int id=positiveId("Appointment ID: ");
        try(PreparedStatement ps=con.prepareStatement("SELECT patient_id,doctor_id,status FROM appointments WHERE appointment_id=?")){
            ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){
                if(!rs.next()){System.out.println("Appointment not found.");return;}
                int pid=rs.getInt(1),did=rs.getInt(2);String status=rs.getString(3);
                if(status.equals("COMPLETED")||status.equals("CANCELLED")||status.equals("NO_SHOW")){System.out.println("This appointment cannot be rescheduled.");return;}
                LocalDateTimePair slot=futureSlot();
                con.setAutoCommit(false);
                try{
                    try(PreparedStatement lock=con.prepareStatement("SELECT doctor_id FROM doctors WHERE doctor_id=? FOR UPDATE")){lock.setInt(1,did);try(ResultSet r=lock.executeQuery()){r.next();}}
                    if(slotTaken("doctor_id",did,slot.date,slot.time,id)||slotTaken("patient_id",pid,slot.date,slot.time,id))throw new SQLException("SLOT_CONFLICT");
                    try(PreparedStatement up=con.prepareStatement("UPDATE appointments SET appointment_date=?,appointment_time=? WHERE appointment_id=?")){
                        up.setDate(1,java.sql.Date.valueOf(slot.date));up.setTime(2,Time.valueOf(slot.time));up.setInt(3,id);up.executeUpdate();
                    }
                    con.commit();success("Appointment rescheduled to "+slot.date+" "+slot.time);
                }catch(SQLException e){con.rollback();if("SLOT_CONFLICT".equals(e.getMessage()))System.out.println("ERROR: The patient or doctor already has an active appointment in that slot.");else throw e;}
                finally{con.setAutoCommit(true);}
            }
        }
    }
    private void updateAppointmentStatus()throws SQLException{
        int id=positiveId("Appointment ID: ");
        try(PreparedStatement ps=con.prepareStatement("SELECT status FROM appointments WHERE appointment_id=?")){
            ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){
                if(!rs.next()){System.out.println("Appointment not found.");return;}
                String old=rs.getString(1);System.out.println("Current status: "+old);
                String next=enumValue("New status [BOOKED/CONFIRMED/COMPLETED/CANCELLED/NO_SHOW]: ","BOOKED","CONFIRMED","COMPLETED","CANCELLED","NO_SHOW");
                if(!validTransition(old,next)){System.out.println("Invalid transition: "+old+" -> "+next);return;}
                try(PreparedStatement up=con.prepareStatement("UPDATE appointments SET status=? WHERE appointment_id=?")){
                    up.setString(1,next);up.setInt(2,id);up.executeUpdate();success("Status updated to "+next);
                }
            }
        }
    }
    private boolean validTransition(String old,String next){
        if(old.equals(next))return true;
        switch(old){
            case "BOOKED":return next.equals("CONFIRMED")||next.equals("CANCELLED");
            case "CONFIRMED":return next.equals("COMPLETED")||next.equals("CANCELLED")||next.equals("NO_SHOW");
            default:return false;
        }
    }
    private void cancelAppointment()throws SQLException{
        int id=positiveId("Appointment ID: ");
        try(PreparedStatement ps=con.prepareStatement("SELECT status FROM appointments WHERE appointment_id=?")){
            ps.setInt(1,id);try(ResultSet rs=ps.executeQuery()){
                if(!rs.next()){System.out.println("Appointment not found.");return;}
                String s=rs.getString(1);if(!validTransition(s,"CANCELLED")){System.out.println("Appointment in status "+s+" cannot be cancelled.");return;}
                if(confirm("Cancel appointment "+id+"?"))try(PreparedStatement up=con.prepareStatement("UPDATE appointments SET status='CANCELLED' WHERE appointment_id=?")){
                    up.setInt(1,id);up.executeUpdate();success("Appointment cancelled.");
                }else cancelled();
            }
        }
    }
    private void deleteAppointment()throws SQLException{
        int id=positiveId("Appointment ID: ");printQuery("SELECT appointment_id,patient_id,doctor_id,appointment_date,appointment_time,status FROM appointments WHERE appointment_id=?",id);
        if(!exists("appointments","appointment_id",id))return;
        if(!confirm("Delete appointment (prescription history may prevent this)?")){cancelled();return;}
        try(PreparedStatement ps=con.prepareStatement("DELETE FROM appointments WHERE appointment_id=?")){
            ps.setInt(1,id);ps.executeUpdate();success("Appointment deleted.");
        }catch(SQLException e){System.out.println("Appointment has prescription history and cannot be deleted.");}
    }

    // ---------------- PRESCRIPTIONS ----------------
    private String prescriptionJoin(){
        return "SELECT pr.prescription_id,pr.appointment_id,p.patient_id,p.full_name AS patient_name,"+
            "d.doctor_id,d.full_name AS doctor_name,pr.diagnosis,pr.medication_name,pr.dosage,pr.frequency,"+
            "pr.duration,pr.notes,pr.created_at FROM prescriptions pr JOIN appointments a ON a.appointment_id=pr.appointment_id "+
            "JOIN patients p ON p.patient_id=a.patient_id JOIN doctors d ON d.doctor_id=a.doctor_id";
    }
    private void addPrescription()throws SQLException{
        try{
            int aid=positiveId("Appointment ID: ");
            try(PreparedStatement check=con.prepareStatement("SELECT status FROM appointments WHERE appointment_id=?")){
                check.setInt(1,aid);try(ResultSet rs=check.executeQuery()){
                    if(!rs.next()){System.out.println("Appointment not found.");return;}
                    if(!rs.getString(1).equals("COMPLETED")){System.out.println("Prescription can be added only to a COMPLETED appointment.");return;}
                }
            }
            String diag=required("Diagnosis (as provided by authorized clinician): ",500);
            String med=required("Medication name: ",150),dos=required("Dosage instructions: ",150);
            String freq=required("Frequency instructions: ",150),dur=required("Duration instructions: ",150);
            String notes=optional("Notes (optional): ",1000);
            try(PreparedStatement ps=con.prepareStatement("INSERT INTO prescriptions(appointment_id,diagnosis,medication_name,dosage,frequency,duration,notes) VALUES(?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS)){
                ps.setInt(1,aid);ps.setString(2,diag);ps.setString(3,med);ps.setString(4,dos);ps.setString(5,freq);ps.setString(6,dur);ps.setString(7,notes);
                ps.executeUpdate();success("Prescription saved. Prescription ID: "+generatedId(ps));
            }
        }catch(CancelOperation e){cancelled();}
    }
    private void prescriptionById()throws SQLException{printQuery(prescriptionJoin()+" WHERE pr.prescription_id=?",positiveId("Prescription ID: "));}
    private void prescriptionsByPatient()throws SQLException{printQuery(prescriptionJoin()+" WHERE a.patient_id=? ORDER BY pr.created_at DESC",positiveId("Patient ID: "));}
    private void prescriptionsByDoctor()throws SQLException{printQuery(prescriptionJoin()+" WHERE a.doctor_id=? ORDER BY pr.created_at DESC",positiveId("Doctor ID: "));}
    private void updatePrescription()throws SQLException{
        int id=positiveId("Prescription ID: ");
        if(!exists("prescriptions","prescription_id",id)){System.out.println("Prescription not found.");return;}
        Map<Integer,Object> vals=new LinkedHashMap<>();boolean done=false;
        while(!done){
            System.out.println("1.Diagnosis  2.Medication  3.Dosage  4.Frequency  5.Duration  6.Notes  7.Multiple fields  8.All fields  0.Finish");
            int c=intInput("Choice: ",0,8);
            if(c==0)done=true;
            else if(c==8){vals.clear();for(int f=1;f<=6;f++)vals.put(f,prescriptionField(f));done=true;}
            else if(c==7){while(true){int f=intInput("Field (1-6, 0=finish): ",0,6);if(f==0)break;vals.put(f,prescriptionField(f));}}
            else vals.put(c,prescriptionField(c));
        }
        if(vals.isEmpty()||!confirm("Save "+vals.size()+" field update(s)?")){cancelled();return;}
        String[] cols={"diagnosis","medication_name","dosage","frequency","duration","notes"};
        StringBuilder sql=new StringBuilder("UPDATE prescriptions SET ");
        for(Integer f:vals.keySet()){if(sql.charAt(sql.length()-1)!=' ')sql.append(",");sql.append(cols[f-1]).append("=?");}
        sql.append(" WHERE prescription_id=?");
        try(PreparedStatement ps=con.prepareStatement(sql.toString())){
            int i=1;for(Map.Entry<Integer,Object> e:vals.entrySet())bind(ps,i++,e.getKey(),e.getValue());
            ps.setInt(i,id);ps.executeUpdate();success("Prescription updated.");
        }
    }
    private Object prescriptionField(int f){
        switch(f){
            case 1:return required("New diagnosis: ",500);
            case 2:return required("New medication name: ",150);
            case 3:return required("New dosage: ",150);
            case 4:return required("New frequency: ",150);
            case 5:return required("New duration: ",150);
            default:return optional("New notes (blank clears notes): ",1000);
        }
    }
    private void deletePrescription()throws SQLException{
        int id=positiveId("Prescription ID: ");printQuery(prescriptionJoin()+" WHERE pr.prescription_id=?",id);
        if(!exists("prescriptions","prescription_id",id))return;
        if(!confirm("Delete this prescription? Only authorized staff should do this.")){cancelled();return;}
        try(PreparedStatement ps=con.prepareStatement("DELETE FROM prescriptions WHERE prescription_id=?")){
            ps.setInt(1,id);ps.executeUpdate();success("Prescription deleted.");
        }
    }

    // ---------------- REPORTS ----------------
    private void patientTreatmentHistory()throws SQLException{
        int id=positiveId("Patient ID: ");
        if(!exists("patients","patient_id",id)){System.out.println("Patient not found.");return;}
        printQuery("SELECT p.patient_id,p.full_name,a.appointment_id,a.appointment_date,a.appointment_time,a.status,"+
            "d.full_name AS doctor_name,a.reason,pr.prescription_id,pr.diagnosis,pr.medication_name,pr.dosage,pr.frequency,pr.duration "+
            "FROM patients p LEFT JOIN appointments a ON a.patient_id=p.patient_id "+
            "LEFT JOIN doctors d ON d.doctor_id=a.doctor_id LEFT JOIN prescriptions pr ON pr.appointment_id=a.appointment_id "+
            "WHERE p.patient_id=? ORDER BY a.appointment_date DESC",id);
    }
    private void doctorSummary()throws SQLException{
        printQuery("SELECT d.doctor_id,d.full_name,d.specialization,d.availability_status,COUNT(a.appointment_id) AS total_appointments,"+
            "SUM(CASE WHEN a.status='COMPLETED' THEN 1 ELSE 0 END) AS completed_appointments "+
            "FROM doctors d LEFT JOIN appointments a ON a.doctor_id=d.doctor_id GROUP BY d.doctor_id,d.full_name,d.specialization,d.availability_status ORDER BY d.full_name");
    }

    // ---------------- VALIDATION HELPERS ----------------
    private String enumValue(String prompt,String... allowed){
        while(true){
            String s=required(prompt,30).toUpperCase(Locale.ROOT);
            for(String a:allowed)if(a.equals(s))return s;
            System.out.println("Allowed values: "+Arrays.toString(allowed));
        }
    }
    private String enumOptional(String prompt,String... allowed){
        while(true){
            String s=optional(prompt,10);
            if(s==null)return null;
            s=s.toUpperCase(Locale.ROOT);
            for(String a:allowed)if(a.equals(s))return s;
            System.out.println("Allowed values: "+Arrays.toString(allowed));
        }
    }
    private BigDecimal fee(String prompt){
        while(true){
            String s=required(prompt,20);
            try{
                if(!s.matches("\\d{1,8}(\\.\\d{1,2})?"))throw new NumberFormatException();
                BigDecimal b=new BigDecimal(s);
                if(b.compareTo(BigDecimal.ZERO)>=0)return b;
            }catch(NumberFormatException ignored){}
            System.out.println("Enter a non-negative amount with up to two decimal places, e.g. 800.00.");
        }
    }
    private void bind(PreparedStatement ps,int index,int field,Object value)throws SQLException{
        if(value==null)ps.setNull(index,Types.VARCHAR);
        else if(value instanceof java.sql.Date)ps.setDate(index,(java.sql.Date)value);
        else if(value instanceof BigDecimal)ps.setBigDecimal(index,(BigDecimal)value);
        else ps.setObject(index,value);
    }
}
