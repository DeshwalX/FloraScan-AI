package classifier;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;

public class ClassifierView extends VBox {

    private ImageView imageView;
    private Label resultLabel;
    private ProgressBar confidenceBar;
    private Label confidenceLabel;
    private Label detailsLabel;
    private OnnxInference inferenceEngine;
    private VBox dropPlaceholder;

    public ClassifierView() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-padding: 30;");

        try {
            inferenceEngine = new OnnxInference();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to initialize ONNX Runtime or load model.");
        }

        Label titleLabel = new Label("AI Plant Identifier");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        resultLabel = new Label("Upload or drag-and-drop an image to identify the plant.");
        resultLabel.setStyle("-fx-font-size: 22px; -fx-text-fill: -color-success-fg; -fx-font-weight: bold; -fx-alignment: center; -fx-text-alignment: center;");
        resultLabel.setWrapText(true);

        // Drag and drop placeholder
        dropPlaceholder = new VBox(10);
        dropPlaceholder.setAlignment(Pos.CENTER);
        dropPlaceholder.setStyle("-fx-border-color: gray; -fx-border-width: 2; -fx-border-style: dashed; -fx-background-color: -color-bg-subtle; -fx-border-radius: 10; -fx-background-radius: 10;");
        dropPlaceholder.setPrefSize(350, 350);
        dropPlaceholder.setMaxSize(350, 350);

        Label dropIcon = new Label("📥");
        dropIcon.setStyle("-fx-font-size: 48px;");
        Label dropText = new Label("Drag and drop an image here\nor use the buttons below");
        dropText.setStyle("-fx-font-size: 16px; -fx-text-fill: gray; -fx-text-alignment: center;");
        dropPlaceholder.getChildren().addAll(dropIcon, dropText);

        imageView = new ImageView();
        imageView.setFitWidth(350);
        imageView.setFitHeight(350);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("image-view");

        StackPane imageContainer = new StackPane(dropPlaceholder, imageView);
        imageContainer.setAlignment(Pos.CENTER);

        Button selectButton = new Button("Select Plant Image");
        selectButton.getStyleClass().add("accent");

        Button webcamButton = new Button("📷 Use Webcam");
        webcamButton.getStyleClass().add("accent");

        HBox buttonBox = new HBox(20, selectButton, webcamButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Instructional Cards
        HBox stepsBox = new HBox(20);
        stepsBox.setAlignment(Pos.CENTER);
        stepsBox.getChildren().addAll(
            createStepCard("1", "Add Image", "Upload, drag-and-drop, or snap a photo of a leaf or plant."),
            createStepCard("2", "Get Info", "The AI instantly analyzes it to determine the exact species."),
            createStepCard("3", "Save Scans", "Up to 10 scans are automatically saved in History.")
        );

        confidenceBar = new ProgressBar(0);
        confidenceBar.setPrefWidth(300);
        confidenceBar.setVisible(false);

        confidenceLabel = new Label("");
        confidenceLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
        confidenceLabel.setVisible(false);

        detailsLabel = new Label("");
        detailsLabel.setWrapText(true);
        detailsLabel.setStyle(
                "-fx-font-size: 14px; -fx-padding: 10px; -fx-background-color: -color-bg-subtle; -fx-border-radius: 5px;");
        detailsLabel.setVisible(false);
        detailsLabel.setMaxWidth(500);

        // Drag and drop support
        setOnDragOver(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        setOnDragDropped(event -> {
            boolean success = false;
            if (event.getDragboard().hasFiles()) {
                File file = event.getDragboard().getFiles().get(0);
                processImage(file);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });

        selectButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Plant Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp"));
            File selectedFile = fileChooser.showOpenDialog(getScene().getWindow());

            if (selectedFile != null) {
                processImage(selectedFile);
            }
        });

        webcamButton.setOnAction(e -> {
            WebcamDialog dialog = new WebcamDialog(selectedFile -> {
                if (selectedFile != null) {
                    processImage(selectedFile);
                }
            });
            dialog.showAndWait();
        });

        getChildren().addAll(titleLabel, resultLabel, imageContainer, buttonBox, stepsBox, confidenceBar, confidenceLabel, detailsLabel);
    }

    private VBox createStepCard(String icon, String titleText, String description) {
        VBox card = new VBox(5);
        card.setPadding(new javafx.geometry.Insets(15));
        card.setStyle("-fx-background-color: -color-bg-subtle; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(220);
        card.setMinHeight(140);

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");

        Label title = new Label(titleText);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label desc = new Label(description);
        desc.setStyle("-fx-font-size: 12px; -fx-text-fill: gray; -fx-text-alignment: center;");
        desc.setWrapText(true);

        card.getChildren().addAll(iconLabel, title, desc);
        return card;
    }

    private void processImage(File selectedFile) {
        dropPlaceholder.setVisible(false);
        dropPlaceholder.setManaged(false);
        imageView.toFront();
        Image image = new Image(selectedFile.toURI().toString());
        imageView.setImage(image);
        resultLabel.setText("Analyzing image...");
        confidenceBar.setVisible(false);
        confidenceLabel.setVisible(false);
        detailsLabel.setVisible(false);

        new Thread(() -> {
            try {
                OnnxInference.Prediction prediction = inferenceEngine.predict(selectedFile.getAbsolutePath());
                PlantDetails details = DatabaseManager.getPlantDetails(prediction.species);

                javafx.application.Platform.runLater(() -> {
                    resultLabel.setText("Predicted Species: " + prediction.species);
                    confidenceBar.setProgress(prediction.confidence);
                    confidenceBar.setVisible(true);
                    confidenceLabel.setText(String.format("Confidence: %.2f%%", prediction.confidence * 100));
                    confidenceLabel.setVisible(true);

                    if (details != null) {
                        detailsLabel.setText("Description: " + details.getDescription() + "\n" +
                                "Care: " + details.getCareInstructions() + "\n" +
                                "Toxicity: " + details.getToxicityWarning());
                        detailsLabel.setVisible(true);
                    }

                    // Save to history
                    User currentUser = Session.getCurrentUser();
                    if (currentUser != null) {
                        int count = DatabaseManager.getHistoryCount(currentUser.getId());
                        if (count >= 10) {
                            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                                    javafx.scene.control.Alert.AlertType.WARNING);
                            alert.setTitle("History Limit Reached");
                            alert.setHeaderText(null);
                            alert.setContentText(
                                    "History full: Image classified but not saved. Delete past scans to save new ones.");
                            alert.showAndWait();
                        } else {
                            // Copy file to persistent scans directory
                            try {
                                java.io.File scansDir = new java.io.File("scans");
                                if (!scansDir.exists()) {
                                    scansDir.mkdir();
                                }
                                String newFileName = java.util.UUID.randomUUID().toString() + "_"
                                        + selectedFile.getName();
                                java.nio.file.Path destPath = java.nio.file.Paths.get("scans", newFileName);
                                java.nio.file.Files.copy(selectedFile.toPath(), destPath,
                                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                                DatabaseManager.saveHistory(currentUser.getId(), destPath.toAbsolutePath().toString(),
                                        prediction.species);
                            } catch (Exception ex) {
                                System.err.println("Failed to save history image: " + ex.getMessage());
                            }
                        }
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    resultLabel.setText("Error during classification: " + ex.getMessage());
                    confidenceBar.setVisible(false);
                    confidenceLabel.setVisible(false);
                });
            }
        }).start();
    }
}
