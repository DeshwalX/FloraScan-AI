package classifier;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AccountView extends VBox {

    public AccountView() {
        setAlignment(Pos.TOP_LEFT);
        setSpacing(25);
        setPadding(new Insets(40));

        Label title = new Label("Account Settings");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));

        User user = Session.getCurrentUser();

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);

        addDetailRow(grid, 0, "Name:", user.getName());
        addDetailRow(grid, 1, "Username:", user.getUsername());
        addDetailRow(grid, 2, "Date of Birth:", user.getDob());
        addDetailRow(grid, 3, "Age:", String.valueOf(user.getAge()));
        addDetailRow(grid, 4, "Gender:", user.getGender());

        Label appearanceLabel = new Label("Appearance:");
        appearanceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        ToggleButton themeToggle = new ToggleButton(user.getModePreference().equals("DARK") ? "Switch to Light Mode" : "Switch to Dark Mode");
        themeToggle.getStyleClass().add("accent");
        
        themeToggle.setOnAction(e -> {
            String newMode = user.getModePreference().equals("DARK") ? "LIGHT" : "DARK";
            user.setModePreference(newMode);
            DatabaseManager.updateUserModePreference(user.getId(), newMode);
            MainApp.switchTheme(newMode);
            themeToggle.setText(newMode.equals("DARK") ? "Switch to Light Mode" : "Switch to Dark Mode");
        });

        grid.add(appearanceLabel, 0, 5);
        grid.add(themeToggle, 1, 5);

        getChildren().addAll(title, grid);
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label nameLabel = new Label(label);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label valLabel = new Label(value);
        valLabel.setStyle("-fx-font-size: 14px;");
        grid.add(nameLabel, 0, row);
        grid.add(valLabel, 1, row);
    }
}
