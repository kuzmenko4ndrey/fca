/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author neophron
 */
public class SQLiteJDBC {

    public static final SQLiteJDBC db = new SQLiteJDBC();
    private Connection c;
    private Statement stmt;
    private ResultSet rs;

    public void closer() throws SQLException {
        rs.close();
        stmt.close();
        c.close();
    }

    private void createTables() throws SQLException {
        String sql;
        sql = "CREATE TABLE Entries "
                + "(NAME TEXT PRIMARY KEY NOT NULL,"
                + "COUNT INT NOT NULL)";
        stmt.executeUpdate(sql);
        sql = "CREATE TABLE Kontexts "
                + "(NAME TEXT PRIMARY KEY NOT NULL)";
        stmt.executeUpdate(sql);
        sql = "CREATE TABLE EntKon "
                + "(ENTRY TEXT NOT NULL,"
                + "KONTEXT TEXT NOT NULL,"
                + "COUNT INT NOT NULL,"
                + "FOREIGN KEY(ENTRY) REFERENCES Entries(NAME),"
                + "FOREIGN KEY(KONTEXT) REFERENCES Kontexts(NAME))";
        stmt.executeUpdate(sql);
        System.out.println("Tables created successfully.");
    }

    public boolean newEntry(String s) {
        try {
            String sql = "SELECT * FROM Entries WHERE NAME = \"" + s + "\"";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int count = rs.getInt("COUNT");
                count++;
                String sql2 = "UPDATE Entries set COUNT = " + count
                        + " where NAME = \"" + s + "\"";
                //rs.updateInt("COUNT", count);
//                rs.updateRow();
                stmt.executeUpdate(sql2);
                return false;
            }
            sql = "INSERT INTO Entries (NAME, COUNT) VALUES (\"" + s + "\", 1)";
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteJDBC.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public boolean newKontext(String s) {
        try {
            String sql = "SELECT * FROM Kontexts WHERE NAME = \"" + s + "\"";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                return false;
            }
            sql = "INSERT INTO Kontexts (NAME) VALUES (\"" + s + "\")";
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteJDBC.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public void dropAll() {
        File f = new File("/home/neophron/NetBeansProjects/FCA/fca.db");
        f.delete();
    }

    public boolean newRelation(String entry, String kontext) {
        try {
            String sql = "SELECT * FROM EntKon WHERE ENTRY = \"" + entry + "\" "
                    + "AND KONTEXT = \"" + kontext + "\"";
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                int count = rs.getInt("COUNT");
                count++;
                String sql2 = "UPDATE EntKon set COUNT = " + count
                        + " WHERE ENTRY = \"" + entry + "\" "
                        + "AND KONTEXT = \"" + kontext + "\"";
                //rs.updateInt("COUNT", count);
//                rs.updateRow();
                stmt.executeUpdate(sql2);
//                rs.updateInt("COUNT", count);
//                rs.updateRow();
                return false;
            }
            sql = "INSERT INTO EntKON (ENTRY, KONTEXT, COUNT)"
                    + "VALUES (\"" + entry + "\", \"" + kontext + "\", 1)";
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteJDBC.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public List<String> getEntry(int k) {
        try {
            List<String> res = new ArrayList<>();
            String sql = "SELECT * FROM Entries WHERE COUNT > " + k;
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                res.add(rs.getString("NAME"));
            }
            return res;
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteJDBC.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public Set<String> getKontexts(String s, int k) {
        try {
            Set<String> res = new HashSet<>();
            String sql = "SELECT * FROM EntKon WHERE ENTRY = \"" + s
                    + "\" AND COUNT > " + k;
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                res.add(rs.getString("KONTEXT"));
            }
            return res;
        } catch (SQLException ex) {
            Logger.getLogger(SQLiteJDBC.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private SQLiteJDBC() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:fca.db");
            stmt = c.createStatement();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        try {
            createTables();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

}
