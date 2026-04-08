package classifier;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.Period;

public class SignupView extends VBox {

    public SignupView() {
        setAlignment(Pos.CENTER);
        setSpacing(15);
        setStyle("-fx-padding: 30;");

        Label title = new Label("Create an Account");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        VBox formBox = new VBox(15);
        formBox.setMaxWidth(400);
        formBox.setAlignment(Pos.CENTER);
        formBox.setStyle("-fx-background-color: -color-bg-subtle; -fx-padding: 30; -fx-background-radius: 10;");

        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Date of Birth");
        dobPicker.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female", "Other", "Prefer not to say");
        genderBox.setPromptText("Gender");
        genderBox.setMaxWidth(Double.MAX_VALUE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: -color-danger-fg;");
        errorLabel.setVisible(false);

        Label successLabel = new Label();
        successLabel.setStyle("-fx-text-fill: -color-success-fg;");
        successLabel.setVisible(false);

        Button registerButton = new Button("Register");
        registerButton.setDefaultButton(true);
        registerButton.setPrefWidth(Double.MAX_VALUE);
        registerButton.getStyleClass().add("accent");

        Hyperlink loginLink = new Hyperlink("Already have an account? Login here");
        loginLink.setOnAction(e -> MainApp.switchView(new LoginView()));

        registerButton.setOnAction(e -> {
            String name = nameField.getText();
            String username = usernameField.getText();
            LocalDate dob = dobPicker.getValue();
            String gender = genderBox.getValue();
            String pwd = passwordField.getText();
            String confirmPwd = confirmPasswordField.getText();

            errorLabel.setVisible(false);
            successLabel.setVisible(false);

            if (name == null || name.trim().isEmpty() ||
                username == null || username.trim().isEmpty() ||
                dob == null || gender == null ||
                pwd == null || pwd.trim().isEmpty() ||
                confirmPwd == null || confirmPwd.trim().isEmpty()) {
                
                errorLabel.setText("Please fill out all fields.");
                errorLabel.setVisible(true);
                return;
            }

            if (!pwd.equals(confirmPwd)) {
                errorLabel.setText("Passwords do not match.");
                errorLabel.setVisible(true);
                return;
            }

            int age = Period.between(dob, LocalDate.now()).getYears();

            try {
                boolean success = DatabaseManager.registerUser(username, pwd, name, dob.toString(), age, gender);
                if (success) {
                    successLabel.setText("Registration successful! Please login.");
                    successLabel.setVisible(true);
                    
                    // Clear fields
                    nameField.clear();
                    usernameField.clear();
                    dobPicker.setValue(null);
                    genderBox.setValue(null);
                    passwordField.clear();
                    confirmPasswordField.clear();
                } else {
                    errorLabel.setText("Registration failed. Please try again.");
                    errorLabel.setVisible(true);
                }
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage()); // Likely username exists message
                errorLabel.setVisible(true);
            }
        });

        formBox.getChildren().addAll(
            nameField, usernameField, dobPicker, genderBox, 
            passwordField, confirmPasswordField, 
            errorLabel, successLabel, registerButton
        );

        getChildren().addAll(title, formBox, loginLink);
    }
}
