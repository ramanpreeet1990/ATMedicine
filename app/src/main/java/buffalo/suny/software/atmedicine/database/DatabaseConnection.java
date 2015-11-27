package buffalo.suny.software.atmedicine.database;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import buffalo.suny.software.atmedicine.utility.Globals;


public class DatabaseConnection {
    private String driver = "oracle.jdbc.driver.OracleDriver";
    private String serverName = "atm-sec.cizjdaebgen0.us-east-1.rds.amazonaws.com";
    private String portNumber = "1521";
    private String database = "ORCL";
    private String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + database;
    private String user = "atm_user";
    private String pwd = "rsvak_sec";

    private Connection conn = null;
    private Statement stmt = null;

    private static DatabaseConnection db;

    public DatabaseConnection() {
        new makeConnection().execute();
    }

    public static DatabaseConnection getInstance() {
        if (null == db) {
            db = new DatabaseConnection();
        }

        return db;
    }

    private class makeConnection extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                conn = DriverManager.getConnection(url, user, pwd);
                stmt = conn.createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            Log.v(Globals.TAG, "Database Connection Established : " + isConnected);
        }
    }

    public boolean registerUser(String newUserEmailId, String newUserPassword) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        return true;
    }

    public boolean isRegisteredUser(String userEmailId, String userPassword) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        return true;
    }

    public boolean updatePassword(String userEmailId, String password) {
/*        ResultSet regUser = null;
        try {
            regUser = stmt.executeQuery("insert into ATM_USER_PROFILE_TB (user_id, email_id) values(seq_user_id.nextval,)");

            Log.v(Globals.TAG, regUser.toString());

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        }*/
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        return true;
    }


    public boolean fetchRemedyData() {
        ResultSet regUser = null;
        try {
            regUser = stmt.executeQuery("insert into ATM_USER_PROFILE_TB (user_id, email_id) values(seq_user_id.nextval,)");

            Log.v(Globals.TAG, regUser.toString());

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        }
    }

    public boolean findRemedy(String bodyPart, String symptom) {
        Log.v(Globals.TAG, "findRemedy() : bodyPart : " + bodyPart + " , symptom : " + symptom);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        return true;

    }

    public boolean fetchHealthcareCentres(String insuranceProvider, double userLatitude, double userLongitude) {
        Log.v(Globals.TAG, "fetchHealthcareCentres() : insuranceProvider : " + insuranceProvider + " , userLatitude : " + userLatitude + " , userLongitude : " + userLongitude);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        return true;



/*        ResultSet regUser = null;
        try {
            regUser = stmt.executeQuery("insert into ATM_USER_PROFILE_TB (user_id, email_id) values(seq_user_id.nextval)");

            Log.v(Globals.TAG, regUser.toString());

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        }*/
    }

    public boolean getDietPlan(int userBMI) {
        ResultSet regUser = null;
        try {
            regUser = stmt.executeQuery("insert into ATM_USER_PROFILE_TB (user_id, email_id) values(seq_user_id.nextval,");

            Log.v(Globals.TAG, regUser.toString());

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        }
    }

    public boolean fetchHistory(String userEmailId) {
        ResultSet regUser = null;
        try {
            regUser = stmt.executeQuery("insert into ATM_USER_PROFILE_TB (user_id, email_id) values(seq_user_id.nextval");
            Log.v(Globals.TAG, regUser.toString());

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        }
    }

    public boolean updateProfileInfo(String lastName, String firstName, String contact, String insuranceProvider, String DOB, String height, int weight, String password) {
        ResultSet regUser = null;
        try {
            regUser = stmt.executeQuery("insert into ATM_USER_PROFILE_TB (user_id, email_id) values(seq_user_id.nextval,");

            Log.v(Globals.TAG, regUser.toString());

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        }
    }


    public boolean resetPassword(String userEmailId) {
        ResultSet regUser = null;
        try {
            regUser = stmt.executeQuery("insert into ATM_USER_PROFILE_TB (user_id, email_id) values(seq_user_id.nextval,')");

            Log.v(Globals.TAG, regUser.toString());

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        }
    }

    public void closeConnection() {
        new closeConnectionTask().execute();
    }

    private class closeConnectionTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            if (null != conn) {
                try {
                    conn.close();
                    return true;

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return true;
            }
        }

        @Override
        protected void onPostExecute(Boolean isConnected) {
            Log.v(Globals.TAG, "Database Connection Closed : ");
        }
    }

}
