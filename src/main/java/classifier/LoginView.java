package classifier;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginView extends VBox {

    public LoginView() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setStyle("-fx-padding: 50;");

        Label title = new Label("Welcome to Plant Classifier");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label subtitle = new Label("Please login to continue");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");

        VBox formBox = new VBox(15);
        formBox.setMaxWidth(350);
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle("-fx-background-color: -color-bg-subtle; -fx-padding: 30; -fx-background-radius: 10;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: -color-danger-fg;");
        errorLabel.setVisible(false);

        Button loginButton = new Button("Login");
        loginButton.setDefaultButton(true);
        loginButton.setPrefWidth(Double.MAX_VALUE);
        loginButton.getStyleClass().add("accent");
        
        loginButton.setOnAction(e -> {
            String uname = usernameField.getText();
            String pwd = passwordField.getText();
            try {
                User user = DatabaseManager.loginUser(uname, pwd);
                if (user != null) {
                    Session.setCurrentUser(user);
                    MainApp.switchTheme(user.getModePreference());
                    MainApp.switchView(new MainScreenView());
                } else {
                    errorLabel.setText("Invalid username or password (No match in DB)");
                    errorLabel.setVisible(true);
                }
            } catch (Exception ex) {
                errorLabel.setText("DB Error: " + ex.getMessage());
                errorLabel.setVisible(true);
                ex.printStackTrace();
            }
        });

        Hyperlink signupLink = new Hyperlink("Don't have an account? Sign up");
        signupLink.setOnAction(e -> MainApp.switchView(new SignupView()));

        formBox.getChildren().addAll(usernameField, passwordField, errorLabel, loginButton, signupLink);
        getChildren().addAll(title, subtitle, formBox);
    }
}
