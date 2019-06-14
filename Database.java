package mine;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.sql.rowset.*;
import com.sun.rowset.JdbcRowSetImpl;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;


public class Database {
    static final String DATABASE_URL = "jdbc:mysql://localhost/test";
    static final String USERNAME = "root";
    static final String PASSWORD = "";
    
    /**
     * select all titles stored in a table in the database and returns them
     * @return the titles in the database
     */
    public static String[] getTitles() throws SQLException {
        ArrayList<String> names = new ArrayList<>(1);
        try ( JdbcRowSetImpl rowSet = new JdbcRowSetImpl(DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root",""))){
            
            rowSet.setCommand("SELECT title from pics;");
            rowSet.execute();
            
            while(rowSet.next())
                names.add(rowSet.getObject(1).toString());
            
        } catch (SQLException e) {
            throw new SQLException("Error getting data: Database not connected");
        }
        return names.toArray(new String[names.size()]);
    }
    
    /**
     * 
     * get the location of the image with the given title
     * @param title title of the image to get location
     * @return location of the image
    * */
    public static String getLocation(String title) throws SQLException {
        ImageIcon image = null;
        try(Connection con = (DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root",""));
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("SELECT image_location from pics where title = '"+title+"'")){
            
            
            if(rs.next()){ //if data exist
                return rs.getString(1); //return the location
            }

            
        } catch (SQLException e) {
            throw new SQLException("Error getting data: Database not connected");
        }
        
        return null; //if an error occured
    }
    
    /**
     * inserts the image title and location into the database
     * @param title the title of image to insert
     * @param location the location of image to be inserted
     * @return the row where data was inserted. if value < 0 the data not inserted
     */
    public static int insert(String title, String location) throws SQLException {
         try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test","root","");
                PreparedStatement statement = connection.prepareStatement("insert into pics(title, image_location) " + "values(?,?)"); ){
            
            statement.setString(1, title);
            statement.setString(2, location);
           
            return statement.executeUpdate();
            
            
        }catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
    }
    
    private Database() {} //to avoid instantiation
}
