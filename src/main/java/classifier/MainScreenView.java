package classifier;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class MainScreenView extends BorderPane {

    private VBox drawer;
    private Region contentArea;

    public MainScreenView() {
        // Drawer initialization
        drawer = new VBox(15);
        drawer.setPadding(new Insets(20));
        drawer.setStyle("-fx-background-color: -color-bg-subtle; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        drawer.setPrefWidth(250);
        drawer.setVisible(false);
        drawer.setManaged(false); // Initially hidden completely

        Button accountBtn = createMenuButton("👤 Account");
        Button historyBtn = createMenuButton("🕒 History");
        Button classifyBtn = createMenuButton("📷 Classifier");
        Button logoutBtn = createMenuButton("🚪 Log Out");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        drawer.getChildren().addAll(classifyBtn, accountBtn, historyBtn, spacer, logoutBtn);

        // Header with Hamburger
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setStyle("-fx-background-color: #2e7d32;");

        Button hamburgerBtn = new Button("≡");
        hamburgerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 24px; -fx-cursor: hand; -fx-font-weight: bold;");
        hamburgerBtn.setOnAction(e -> toggleDrawer());

        header.getChildren().add(hamburgerBtn);

        // Set layout
        setTop(header);
        setLeft(drawer);
        
        // Initial Content
        setContent(new ClassifierView());

        // Event Listeners for menu
        classifyBtn.setOnAction(e -> setContent(new ClassifierView()));
        accountBtn.setOnAction(e -> setContent(new AccountView()));
        historyBtn.setOnAction(e -> setContent(new HistoryView()));
        logoutBtn.setOnAction(e -> {
            Session.logout();
            MainApp.switchView(new LoginView());
        });
    }

    private void toggleDrawer() {
        boolean isVisible = drawer.isVisible();
        drawer.setVisible(!isVisible);
        drawer.setManaged(!isVisible); // Keeps layout clean when hidden
    }

    private void setContent(Region view) {
        if (drawer.isVisible()) {
            toggleDrawer(); // Hide drawer on selection
        }
        setCenter(view);
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-alignment: center-left; -fx-cursor: hand;");
        btn.setMaxWidth(Double.MAX_VALUE);
        return btn;
    }
}
