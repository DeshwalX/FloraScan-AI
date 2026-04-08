package classifier;

public class User {
    private int id;
    private String username;
    private String name;
    private String dob;
    private int age;
    private String gender;
    private String modePreference;

    public User(int id, String username, String name, String dob, int age, String gender, String modePreference) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.dob = dob;
        this.age = age;
        this.gender = gender;
        this.modePreference = modePreference;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getDob() { return dob; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getModePreference() { return modePreference; }
    public void setModePreference(String modePreference) { this.modePreference = modePreference; }
}
