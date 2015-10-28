package buffalo.suny.software.atmedicine.database;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class DatabaseConnection {
    private String driver = "oracle.jdbc.driver.OracleDriver";
    private String serverName = "atm_user";
    private String portNumber = "1521";
    private String database = "atm-sec";

    private String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + database;
    private String user = "atm_user"; // username of oracle database
    private String pwd = "rsvak_sec"; // password of oracle database
    private Connection con = null;
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;


    DatabaseConnection() {
        makeconnection();
    }

    public void makeconnection(){
    try {
        Class.forName(driver);// for loading the jdbc driver

        System.out.println("JDBC Driver loaded");

        con = DriverManager.getConnection(url, user, pwd);// for
        // establishing
        // connection
        // with database
        Statement stmt = con.createStatement();

        serverSocket = new ServerSocket(8888);
        System.out.println("Listening :8888");

        while (true) {
            try {

                socket = serverSocket.accept();
                System.out.println("Connection Created");
                dataInputStream = new DataInputStream(
                        socket.getInputStream());
                dataOutputStream = new DataOutputStream(
                        socket.getOutputStream());
                System.out.println("ip: " + socket.getInetAddress());
                // System.out.println("message: " +
                // dataInputStream.readUTF());

                ResultSet res=stmt.executeQuery("select * from user");
                while(res.next()){
                    System.out.println(res.getString(1));
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
}
