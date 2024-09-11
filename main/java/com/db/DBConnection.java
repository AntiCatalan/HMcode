package com.db;
import java.sql.*;
public class DBConnection {
    private static Connection conn;
    public static Connection getConn(){
        try {
            //加载数据库驱动
            Class.forName("com.mysql.jdbc.Driver");
            conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/envdb",
                    "root","1234");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return conn;
    }

    /**
     * 关闭数据库操作方法
     * */
    public static void closeAll(ResultSet rst,PreparedStatement pst,Connection conn) {
        try {
            if(rst!=null)rst.close();
            if(pst!=null)pst.close();
            if(conn!=null)conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        System.out.println(getConn());
    }


}

