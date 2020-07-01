package com.anc.theticdiemconek.DataBase;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import com.anc.theticdiemconek.Model.ThongTinKhachHang;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.transform.Result;

public class DataProvider {
    private static DataProvider instance;
    private final String ipServer = "125.212.201.52";
    private final String userName = "coneknfc";
    private final String passWord = "Conek@123";
    private final String nameDB = "NFC";

    private DataProvider() { }

    public static DataProvider getInstance(){
        if (instance == null){
            instance = new DataProvider();
        }
        return instance;
    }
    @SuppressLint("NewApi")
    public Connection CONN(String _user, String _pass, String _DB,
                           String _server) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://" + _server + ";"
                    + "databaseName=" + _DB + ";user=" + _user + ";password="
                    + _pass + ";"; // c: // :c
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }

    public ThongTinKhachHang LayThongTinKhachHang(String UIDKH){
        ThongTinKhachHang thongTinKhachHang = new ThongTinKhachHang("ABC","ABC","ABC","ABC",0);
        Connection connection;
        ResultSet resultSet;
        connection = CONN(userName,passWord,nameDB,ipServer);
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery("Select *from dbo.TheTichDiemSpa where UID = '" + UIDKH + "'");
            if(resultSet != null && resultSet.next()){
                thongTinKhachHang.setUIDKH(resultSet.getString("UID"));
                thongTinKhachHang.setNameKH(resultSet.getString("HoTen"));
                thongTinKhachHang.setAddressKH(resultSet.getString("DiaChi"));
                thongTinKhachHang.setPhoneKH(resultSet.getString("Phone"));
                thongTinKhachHang.setTichDiem(resultSet.getInt("TichDiem"));
            }
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return thongTinKhachHang;
    }

    public int InsertNewCustomer(String UID, String NameKH, String AddressKH, String PhoneKH, Integer TichDiem){
        int resultInsert = 0;
        Connection connection;
        connection = CONN(userName,passWord,nameDB,ipServer);
        String querryInsert = "Insert into dbo.TheTichDiemSpa(UID, HoTen, DiaChi, Phone, TichDiem) values(?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(querryInsert);
            preparedStatement.setString(1,UID);
            preparedStatement.setString(2,NameKH);
            preparedStatement.setString(3,AddressKH);
            preparedStatement.setString(4,PhoneKH);
            preparedStatement.setInt(5,TichDiem);
            resultInsert = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultInsert;
    }

    public int UpdateThongTinKhachHang(String UID, String NameKH, String AddressKH, String PhoneKH, Integer TichDiem){
        int resultInsert = 0;
        Connection connection;
        connection = CONN(userName,passWord,nameDB,ipServer);
        String querryInsert = "UPDATE dbo.TheTichDiemSpa SET HoTen = ?, DiaChi = ?, Phone = ?, TichDiem = ? where UID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(querryInsert);
            preparedStatement.setString(1,NameKH);
            preparedStatement.setString(2,AddressKH);
            preparedStatement.setString(3,PhoneKH);
            preparedStatement.setInt(4,TichDiem);
            preparedStatement.setString(5,UID);
            resultInsert = preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultInsert;
    }
}
