package buffalo.suny.software.atmedicine.database;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import buffalo.suny.software.atmedicine.model.User;
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

    public int registerUser(String newUserEmailId, String newUserMD5Password) {
        Log.v(Globals.TAG, "registerUser() : newUserEmailId : " + newUserEmailId + " , newUserMD5Password : " + newUserMD5Password);

        try {
            stmt.executeQuery("insert into ATM_USER_PROFILE_TB (USER_ID,EMAIL_ID,PASSWORD) values(SEQ_USER_ID.nextval,'" + newUserEmailId + "','" + newUserMD5Password + "')");

            return 1;
        } catch (SQLIntegrityConstraintViolationException e) {
            e.printStackTrace();
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int isRegisteredUser(String userEmailId, String userMD5Password) {
        Log.v(Globals.TAG, "isRegisteredUser() : userEmailId : " + userEmailId + " , userMD5Password : " + userMD5Password);

        ResultSet isRegisteredUserSet;
        int uniqueUserId = -1;
        try {
            isRegisteredUserSet = stmt.executeQuery("select USER_ID from ATM_USER_PROFILE_TB where EMAIL_ID='" + userEmailId + "' and PASSWORD='" + userMD5Password + "'");

            while (isRegisteredUserSet.next()) {
                uniqueUserId = isRegisteredUserSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return uniqueUserId;
    }

    public ArrayList<ArrayList<String>> fetchHealthcareCentres(int userId, String insuranceProvider, double userLatitude, double userLongitude) {
        ResultSet fetchHealthcareCentresSet;
        ArrayList<ArrayList<String>> healthcareCentresList = new ArrayList<>();

        ArrayList<String> myList;
        String name, address, phone, email, latitude, longitude, distanceFromUser;

        try {
            fetchHealthcareCentresSet = stmt.executeQuery("Select * from (SELECT HEALTHCARE_CENTER_ID, HEALTHCARE_CENTER_NAME, ADDRESS, PHONE_NUMBER, EMAIL_ID, LATITUDE, LONGITUDE, distance (LATITUDE,LONGITUDE," + userLatitude + "," + userLongitude + ") dist FROM ATM_HEALTHCARE_CENTER_TB where HEALTHCARE_CENTER_ID in (select HEALTHCARE_CENTER_ID from ATM_INS_PRO_HEALTH_CEN_JOIN_TB where INSURANCE_PROVIDER_ID=(select INSURANCE_PROVIDER_ID from ATM_INSURANCE_PROVIDER_TB where INSURANCE_PROVIDER_NAME='" + insuranceProvider + "')) ORDER BY dist ASC) where rownum<=3");

            while (fetchHealthcareCentresSet.next()) {
                myList = new ArrayList<>();

                name = fetchHealthcareCentresSet.getString(2);
                address = fetchHealthcareCentresSet.getString(3);
                phone = fetchHealthcareCentresSet.getString(4);
                email = fetchHealthcareCentresSet.getString(5);
                latitude = fetchHealthcareCentresSet.getString(6);
                longitude = fetchHealthcareCentresSet.getString(7);
                distanceFromUser = fetchHealthcareCentresSet.getString(8);

                myList.add(name);
                myList.add(address);
                myList.add(phone);
                myList.add(email);
                myList.add(latitude);
                myList.add(longitude);
                myList.add(distanceFromUser);

                healthcareCentresList.add(myList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return healthcareCentresList;
    }

    public HashMap<String, ArrayList<String>> fetchRemedyData() {
        HashMap<String, ArrayList<String>> remedyDataMap = new HashMap<String, ArrayList<String>>();
        ArrayList<String> symptom = new ArrayList<>();

        try {
            ResultSet fetchRemedyDataSet = stmt.executeQuery("select BODY_PART,SYMPTOM from ATM_BODY_TB a,ATM_SYMPTOM_TB b,(select distinct BODY_PART_ID,SYMPTOM_ID from ATM_REMEDY_TB) c where a.BODY_PART_ID=c.BODY_PART_ID and b.SYMPTOM_ID=c.SYMPTOM_ID order by BODY_PART");

            while (fetchRemedyDataSet.next()) {
                if (remedyDataMap.containsKey(fetchRemedyDataSet.getString(1))) {
                    symptom.add(fetchRemedyDataSet.getString(2));
                    remedyDataMap.put(fetchRemedyDataSet.getString(1), symptom);
                } else {
                    symptom = new ArrayList<String>();
                    symptom.add(fetchRemedyDataSet.getString(2));
                    remedyDataMap.put(fetchRemedyDataSet.getString(1), symptom);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return remedyDataMap;

    }

    public ArrayList<String> findRemedy(String bodyPart, String symptom) {
        ArrayList<String> remedyList = new ArrayList<>();

        Log.v(Globals.TAG, "findRemedy() : bodyPart : " + bodyPart + " , symptom : " + symptom);

        ResultSet findRemedySet;

        try {
            findRemedySet = stmt.executeQuery("select o1.OTC,o2.OTC,o3.OTC,h1.HOME_REMEDY,h2.HOME_REMEDY,h3.HOME_REMEDY from ATM_OTC_TB o1,ATM_OTC_TB o2,ATM_OTC_TB o3,ATM_HOME_REMEDY_TB h1,ATM_HOME_REMEDY_TB h2,ATM_HOME_REMEDY_TB h3,ATM_REMEDY_TB where OTC_ID1=o1.OTC_ID and OTC_ID2=o2.OTC_ID and OTC_ID3=o3.OTC_ID and HOME_REMEDY_ID1=h1.HOME_REMEDY_ID and HOME_REMEDY_ID2=h2.HOME_REMEDY_ID and HOME_REMEDY_ID3=h3.HOME_REMEDY_ID and BODY_PART_ID=(select BODY_PART_ID from ATM_BODY_TB where BODY_PART='" + bodyPart + "') and SYMPTOM_ID=(select SYMPTOM_ID from ATM_SYMPTOM_TB where SYMPTOM='" + symptom + "')");


            while (findRemedySet.next()) {
                int counter = 1;

                //There are 6 remedy suggestions.  3 OTC and 3 home remedies
                while (counter < 7) {
                    remedyList.add(findRemedySet.getString(counter));
                    ++counter;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return remedyList;

    }

    public boolean saveUserMedicalHistory(int userId, String bodyPart, String symptom) {
        try {
            stmt.executeQuery("insert into ATM_HISTORY_TB values(sysdate," + userId + ",(select remedy_id from ATM_REMEDY_TB where BODY_PART_ID=(select BODY_PART_ID from ATM_BODY_TB where BODY_PART='" + bodyPart + "') and SYMPTOM_ID=(select SYMPTOM_ID from ATM_SYMPTOM_TB where SYMPTOM='" + symptom + "')))");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        }
    }

    public ArrayList<String> fetchUserMedicalHistory(int userId) {
        ResultSet fetchUserMedicalHistorySet;
        ArrayList<String> medicalHistoryList = new ArrayList<>();
        String date, bodyPart, symptom;

        try {
            fetchUserMedicalHistorySet = stmt.executeQuery("select a1.SEARCH_DATE,a3.BODY_PART,a4.SYMPTOM from ATM_HISTORY_TB a1,ATM_REMEDY_TB a2,ATM_BODY_TB a3,ATM_SYMPTOM_TB a4 where a1.USER_ID=" + userId + " and a1.REMEDY_ID=a2.REMEDY_ID and a2.BODY_PART_ID=a3.BODY_PART_ID and a2.SYMPTOM_ID=a4.SYMPTOM_ID order by a1.SEARCH_DATE");

            while (fetchUserMedicalHistorySet.next()) {
                date = fetchUserMedicalHistorySet.getDate(1).toString();
                bodyPart = fetchUserMedicalHistorySet.getString(2);
                symptom = fetchUserMedicalHistorySet.getString(3);

                medicalHistoryList.add(date + "#" + bodyPart + "#" + symptom);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return medicalHistoryList;
    }

    public boolean updateProfileInfo(String lastName, String firstName, String contact, String insuranceProvider, String DOB, String height, int weight, String password) {
        return true;
    }

    public boolean resetPassword(String userEmailId) {
        return true;
    }

    public boolean updatePassword(int userId, String userMD5Password) {
        return true;
    }


    public void closeConnection() {
        db = null;
        User.closeUser();
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
