package com.elderlycare;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:elderlycare.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Appointments Table
            String sqlAppointments = "CREATE TABLE IF NOT EXISTS appointments ("
                    + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " doctor_name TEXT NOT NULL,"
                    + " appt_date TEXT NOT NULL,"
                    + " appt_time TEXT NOT NULL,"
                    + " notes TEXT,"
                    + " location TEXT,"
                    + " status INTEGER DEFAULT 0" // 0=Upcoming, 1=Completed
                    + ");";
            stmt.execute(sqlAppointments);

            // Medications Table
            String sqlMedications = "CREATE TABLE IF NOT EXISTS medications ("
                    + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " med_name TEXT NOT NULL,"
                    + " dosage TEXT NOT NULL,"
                    + " time TEXT NOT NULL,"
                    + " icon_name TEXT,"
                    + " frequency TEXT,"
                    + " meal_timing TEXT"
                    + ");";
            stmt.execute(sqlMedications);

            // Health Metrics Table
            String sqlMetrics = "CREATE TABLE IF NOT EXISTS health_metrics ("
                    + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + " date_recorded TEXT NOT NULL,"
                    + " bp_sys INTEGER,"
                    + " bp_dia INTEGER,"
                    + " heart_rate INTEGER,"
                    + " blood_sugar INTEGER,"
                    + " weight REAL"
                    + ");";
            stmt.execute(sqlMetrics);

            // Run ALTER TABLE for existing DBs safely
            try { stmt.execute("ALTER TABLE appointments ADD COLUMN location TEXT;"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE appointments ADD COLUMN status INTEGER DEFAULT 0;"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE medications ADD COLUMN frequency TEXT;"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE medications ADD COLUMN meal_timing TEXT;"); } catch (Exception ignored) {}
            try { stmt.execute("ALTER TABLE health_metrics ADD COLUMN weight REAL;"); } catch (Exception ignored) {}

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Appointments ---
    public static class Appointment {
        public int id;
        public String doctorName;
        public String date;
        public String time;
        public String notes;
        public String location;
        public int status;
        
        public Appointment(int id, String doctorName, String date, String time, String notes, String location, int status) {
            this.id = id; this.doctorName = doctorName; this.date = date; this.time = time; this.notes = notes;
            this.location = location; this.status = status;
        }
    }

    public static void addAppointment(String doctor, String date, String time, String notes, String location) {
        String sql = "INSERT INTO appointments(doctor_name, appt_date, appt_time, notes, location, status) VALUES(?,?,?,?,?,0)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctor);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            pstmt.setString(4, notes);
            pstmt.setString(5, location);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void updateAppointmentStatus(int id, int status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<Appointment> getAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT * FROM appointments ORDER BY status ASC, appt_date ASC, appt_time ASC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Appointment(rs.getInt("id"), rs.getString("doctor_name"),
                        rs.getString("appt_date"), rs.getString("appt_time"), rs.getString("notes"),
                        rs.getString("location"), rs.getInt("status")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // --- Medications ---
    public static class Medication {
        public int id;
        public String name;
        public String dosage;
        public String time;
        public String icon;
        public String frequency;
        public String mealTiming;

        public Medication(int id, String name, String dosage, String time, String icon, String frequency, String mealTiming) {
            this.id = id; this.name = name; this.dosage = dosage; this.time = time; this.icon = icon;
            this.frequency = frequency; this.mealTiming = mealTiming;
        }
    }

    public static void addMedication(String name, String dosage, String time, String icon, String frequency, String mealTiming) {
        String sql = "INSERT INTO medications(med_name, dosage, time, icon_name, frequency, meal_timing) VALUES(?,?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name); pstmt.setString(2, dosage); pstmt.setString(3, time); pstmt.setString(4, icon);
            pstmt.setString(5, frequency); pstmt.setString(6, mealTiming);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<Medication> getMedications() {
        List<Medication> list = new ArrayList<>();
        String sql = "SELECT * FROM medications";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Medication(rs.getInt("id"), rs.getString("med_name"), rs.getString("dosage"),
                        rs.getString("time"), rs.getString("icon_name"), rs.getString("frequency"), rs.getString("meal_timing")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static void deleteMedication(int id) {
        String sql = "DELETE FROM medications WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- Health Metrics ---
    public static class HealthMetric {
        public int id;
        public String date;
        public int bpSys;
        public int bpDia;
        public int heartRate;
        public int bloodSugar;
        public double weight;

        public HealthMetric(int id, String date, int bpSys, int bpDia, int heartRate, int bloodSugar, double weight) {
            this.id = id; this.date = date; this.bpSys = bpSys; this.bpDia = bpDia;
            this.heartRate = heartRate; this.bloodSugar = bloodSugar; this.weight = weight;
        }
    }

    public static void addMetric(String date, int bpSys, int bpDia, int heartRate, int bloodSugar, double weight) {
        String sql = "INSERT INTO health_metrics(date_recorded, bp_sys, bp_dia, heart_rate, blood_sugar, weight) VALUES(?,?,?,?,?,?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, date); pstmt.setInt(2, bpSys); pstmt.setInt(3, bpDia);
            pstmt.setInt(4, heartRate); pstmt.setInt(5, bloodSugar); pstmt.setDouble(6, weight);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<HealthMetric> getMetrics() {
        List<HealthMetric> list = new ArrayList<>();
        String sql = "SELECT * FROM health_metrics ORDER BY date_recorded ASC";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new HealthMetric(rs.getInt("id"), rs.getString("date_recorded"),
                        rs.getInt("bp_sys"), rs.getInt("bp_dia"), rs.getInt("heart_rate"), rs.getInt("blood_sugar"), rs.getDouble("weight")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
