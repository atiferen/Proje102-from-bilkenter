package frombilkenter.fx.controller;

import frombilkenter.app.Main;
import frombilkenter.data.AppState;
import frombilkenter.fx.PageController;
import frombilkenter.model.Listing;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class ShellContoller {
    @FXML private ImageView logoImage;
    @FXML private Button homeButton;
    @FXML private Button sellButton;
    @FXML private Button leaderboardButton;
    @FXML private Button profileButton;
    @FXML private Button adminButton;
    @FXML private StackPane contentHost;

    private Main app;
    private AppState appState;
    private final Map<String, PageController> controllers = new HashMap<>();
    private final Map<String, Parent> views = new HashMap<>();
    private DetailController detailController;

    public void init(Main app, AppState appState) throws Exception {
        this.app = app;
        this.appState = appState;
        logoImage.setImage(new Image(getClass().getResource("/images/logo.png").toExternalForm()));
        sellButton.setVisible(appState.getCurrentUser().isSeller());
        adminButton.setVisible(appState.getCurrentUser().isAdmin());
        showPage("home");
    }

    @FXML public void showHome() throws Exception { showPage("home"); }
    @FXML public void showSell() throws Exception { showPage("sell"); }
    @FXML public void showLeaderboard() throws Exception { showPage("leaderboard"); }
    @FXML public void showProfile() throws Exception { showPage("profile"); }
    @FXML public void showAdmin() throws Exception { showPage("admin"); }
    @FXML
    public void signOut() throws Exception {
        Alert confirmDialog = new Alert(Alert.AlertType.NONE, "", ButtonType.CANCEL, ButtonType.OK);
        confirmDialog.setTitle("Confirm Sign Out");
        confirmDialog.getDialogPane().setHeaderText("Are you sure you want to sign out?");
        styleDialog(confirmDialog.getDialogPane());
        confirmDialog.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                try {
                    app.showLogin();
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            }
        });
    }

    public void openListing(Listing listing) throws Exception {
        showPage("detail");
        detailController.setListing(listing);
        detailController.refresh();
    }

    public void refreshAll() {
        controllers.values().forEach(PageController::refresh);
    }

    private void showPage(String key) throws Exception {
        if (!views.containsKey(key)) {
            loadPage(key);
        }
        contentHost.getChildren().setAll(views.get(key));
        refreshAll();
    }

    private void loadPage(String key) throws Exception {
        String fxml = switch (key) {
            case "home" -> "/fxml/HomeView.fxml";
            case "detail" -> "/fxml/DetailView.fxml";
            case "sell" -> "/fxml/SellView.fxml";
            case "leaderboard" -> "/fxml/LeaderboardView.fxml";
            case "profile" -> "/fxml/ProfileView.fxml";
            case "admin" -> "/fxml/AdminView.fxml";
            default -> throw new IllegalArgumentException("Unknown page: " + key);
        };
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        PageController controller = loader.getController();
        controller.init(appState, this);
        controllers.put(key, controller);
        if (controller instanceof DetailController casted) {
            detailController = casted;
        }
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("page-scroll");
        views.put(key, scrollPane);
    }

    private void styleDialog(DialogPane pane) {
        pane.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        pane.getStyleClass().add("app-dialog");
    }
}
