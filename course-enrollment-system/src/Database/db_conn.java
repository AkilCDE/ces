package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class db_conn {
   
    public static Connection conn(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ces", "root", "");
            return con;
        }catch(SQLException | ClassNotFoundException e){
            System.out.println(e);
        }
        return null;
    }
}

