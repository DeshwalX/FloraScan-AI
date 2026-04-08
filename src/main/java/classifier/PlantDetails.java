package classifier;

public class PlantDetails {
    private String speciesName;
    private String description;
    private String careInstructions;
    private String toxicityWarning;

    public PlantDetails(String speciesName, String description, String careInstructions, String toxicityWarning) {
        this.speciesName = speciesName;
        this.description = description;
        this.careInstructions = careInstructions;
        this.toxicityWarning = toxicityWarning;
    }

    public String getSpeciesName() { return speciesName; }
    public String getDescription() { return description; }
    public String getCareInstructions() { return careInstructions; }
    public String getToxicityWarning() { return toxicityWarning; }
}
