package classifier;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.File;
import java.util.List;

public class HistoryView extends VBox {

    private FlowPane tilePane;

    public HistoryView() {
        setAlignment(Pos.TOP_LEFT);
        setSpacing(20);
        setPadding(new Insets(30));

        Label title = new Label("Classification History");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));

        Label subtitle = new Label("View your past plant classifications. You can save up to 10 scans.");
        subtitle.setStyle("-fx-text-fill: gray; -fx-font-size: 14px;");

        tilePane = new FlowPane();
        tilePane.setHgap(20);
        tilePane.setVgap(20);
        
        ScrollPane scrollPane = new ScrollPane(tilePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(title, subtitle, scrollPane);

        loadHistory();
    }

    private void loadHistory() {
        tilePane.getChildren().clear();
        User user = Session.getCurrentUser();
        if (user == null) return;

        List<HistoryItem> history = DatabaseManager.getHistory(user.getId());
        
        if (history.isEmpty()) {
            Label noHistoryLabel = new Label("No history found. Try classifying some plants!");
            noHistoryLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            tilePane.getChildren().add(noHistoryLabel);
            return;
        }

        for (HistoryItem item : history) {
            tilePane.getChildren().add(createHistoryCard(item));
        }
    }

    private VBox createHistoryCard(HistoryItem item) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0); -fx-cursor: hand;");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(220);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);
        
        File imgFile = new File(item.getImagePath());
        if (imgFile.exists()) {
            imageView.setImage(new Image(imgFile.toURI().toString()));
        }

        Label speciesLabel = new Label(item.getSpecies());
        speciesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label dateLabel = new Label(item.getTimestamp());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("danger");
        deleteBtn.setOnAction(e -> {
            e.consume(); // Prevent click from opening details
            DatabaseManager.deleteHistory(item.getId());
            // optionally delete the file from disk to save space
            if (imgFile.exists()) {
                imgFile.delete();
            }
            loadHistory();
        });

        card.getChildren().addAll(imageView, speciesLabel, dateLabel, deleteBtn);

        // Click to view details
        card.setOnMouseClicked(e -> showDetailsDialog(item));

        return card;
    }

    private void showDetailsDialog(HistoryItem item) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Plant Details: " + item.getSpecies());
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);
        File imgFile = new File(item.getImagePath());
        if (imgFile.exists()) {
            imageView.setImage(new Image(imgFile.toURI().toString()));
        }

        PlantDetails details = DatabaseManager.getPlantDetails(item.getSpecies());

        Label nameLabel = new Label(item.getSpecies());
        nameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        if (details != null) {
            Label descLabel = new Label("Description: " + details.getDescription());
            descLabel.setWrapText(true);
            
            Label careLabel = new Label("Care: " + details.getCareInstructions());
            careLabel.setWrapText(true);
            
            Label toxicityLabel = new Label("Toxicity: " + details.getToxicityWarning());
            toxicityLabel.setWrapText(true);
            toxicityLabel.setStyle("-fx-text-fill: -color-danger-fg;");

            content.getChildren().addAll(imageView, nameLabel, descLabel, careLabel, toxicityLabel);
        } else {
            Label noInfo = new Label("No additional information available for this species.");
            content.getChildren().addAll(imageView, nameLabel, noInfo);
        }

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
}
