package buffalo.suny.software.atmedicine.model;

public class User {
    private String emailId, lastName, firstName, phoneNumber, insuranceProvider, dateOfBirth, height, interpretedBMI;
    private int weightLbs, userId;
    private double latitude, longitude;
    private boolean isMale;

    private static User user;

    public User() {

    }

    public static User getCurrentUser() {
        if (null == user) {
            user = new User();
        }

        return user;
    }

    public static void closeUser() {
        user = null;
    }

    public User(int latitude, int longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public User(boolean isMale, String lastName, String firstName, String phoneNumber, String insuranceProvider, String dateOfBirth, String height, int weightLbs) {
        this.isMale = isMale;
        this.lastName = lastName;
        this.firstName = firstName;
        this.phoneNumber = phoneNumber;
        this.insuranceProvider = insuranceProvider;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.weightLbs = weightLbs;
    }

    public int getUserId() {
        return userId;
    }

    public boolean isMale() {
        return isMale;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getHeight() {
        return height;
    }

    public int getWeightLbs() {
        return weightLbs;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getInterpretedBMI() {
        return interpretedBMI;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setMale(boolean isMale) {
        this.isMale = isMale;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWeightLbs(int weightLbs) {
        this.weightLbs = weightLbs;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setInterpretedBMI(String interpretedBMI) {
        this.interpretedBMI = interpretedBMI;
    }
}