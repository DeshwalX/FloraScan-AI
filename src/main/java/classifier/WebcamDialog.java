package classifier;

import com.github.sarxos.webcam.Webcam;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class WebcamDialog extends Stage {

    private Webcam webcam;
    private ImageView imageView;
    private AtomicBoolean isCapturing;
    private Consumer<File> onCaptureCallback;

    public WebcamDialog(Consumer<File> onCaptureCallback) {
        this.onCaptureCallback = onCaptureCallback;
        this.isCapturing = new AtomicBoolean(true);

        initModality(Modality.APPLICATION_MODAL);
        setTitle("Capture from Webcam");
        setWidth(700);
        setHeight(600);

        Label infoLabel = new Label("Align your plant in the frame and click Capture.");
        infoLabel.setStyle("-fx-font-size: 16px;");

        imageView = new ImageView();
        imageView.setFitWidth(640);
        imageView.setFitHeight(480);
        imageView.setPreserveRatio(true);

        Button captureBtn = new Button("📷 Capture");
        captureBtn.setStyle("-fx-font-size: 18px; -fx-padding: 10 20;");
        captureBtn.getStyleClass().addAll("accent", "success");
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-font-size: 18px; -fx-padding: 10 20;");

        HBox btnBox = new HBox(20, captureBtn, cancelBtn);
        btnBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, infoLabel, imageView, btnBox);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20;");

        Scene scene = new Scene(layout);
        setScene(scene);
        
        setOnCloseRequest(e -> stopWebcam());
        cancelBtn.setOnAction(e -> {
            stopWebcam();
            close();
        });

        captureBtn.setOnAction(e -> captureImage());

        startWebcam();
    }

    private void startWebcam() {
        new Thread(() -> {
            webcam = Webcam.getDefault();
            if (webcam != null) {
                // Attempt to set a high resolution if possible, otherwise rely on default
                Dimension[] sizes = webcam.getViewSizes();
                if (sizes != null && sizes.length > 0) {
                    webcam.setViewSize(sizes[sizes.length - 1]);
                }
                
                if (webcam.open()) {
                    while (isCapturing.get() && webcam.isOpen()) {
                        BufferedImage bi = webcam.getImage();
                        if (bi != null) {
                            Image fxImage = SwingFXUtils.toFXImage(bi, null);
                            Platform.runLater(() -> imageView.setImage(fxImage));
                        }
                        try {
                            Thread.sleep(30); // ~33 FPS
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            } else {
                Platform.runLater(() -> {
                    imageView.setImage(null);
                });
            }
        }).start();
    }

    private void captureImage() {
        if (webcam != null && webcam.isOpen()) {
            BufferedImage bi = webcam.getImage();
            stopWebcam();
            close();

            if (bi != null) {
                new Thread(() -> {
                    try {
                        File tempFile = File.createTempFile("webcam_", ".png");
                        ImageIO.write(bi, "PNG", tempFile);
                        Platform.runLater(() -> onCaptureCallback.accept(tempFile));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
        }
    }

    private void stopWebcam() {
        isCapturing.set(false);
        if (webcam != null) {
            new Thread(() -> webcam.close()).start(); // Run in separate thread to prevent UI freezing
        }
    }
}
