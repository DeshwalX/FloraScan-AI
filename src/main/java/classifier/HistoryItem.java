package classifier;

public class HistoryItem {
    private int id;
    private int userId;
    private String imagePath;
    private String species;
    private String timestamp;

    public HistoryItem(int id, int userId, String imagePath, String species, String timestamp) {
        this.id = id;
        this.userId = userId;
        this.imagePath = imagePath;
        this.species = species;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getImagePath() { return imagePath; }
    public String getSpecies() { return species; }
    public String getTimestamp() { return timestamp; }
}
