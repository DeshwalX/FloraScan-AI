package classifier;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;

public class MainApp extends Application {
    
    private static Stage mainStage;
    private static Scene mainScene;
    private static StackPane rootContainer;

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        mainStage.setTitle("Plant Species Classifier");

        rootContainer = new StackPane();
        mainScene = new Scene(rootContainer, 1000, 750);
        
        // Setup initial Theme
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        mainScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        mainStage.setScene(mainScene);
        mainStage.show();

        // Switch to Login View initially
        switchView(new LoginView());
    }

    public static void switchView(Region view) {
        rootContainer.getChildren().clear();
        rootContainer.getChildren().add(view);
    }
    
    public static void switchTheme(String mode) {
        if ("DARK".equalsIgnoreCase(mode)) {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
        } else {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
