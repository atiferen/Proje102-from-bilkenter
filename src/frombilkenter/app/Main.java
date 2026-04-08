package frombilkenter.app;
import frombilkenter.data.AppState;
import frombilkenter.data.MongoSyncManager;
import frombilkenter.fx.controller.LoginController;
import frombilkenter.fx.controller.SignUpController;
import frombilkenter.fx.controller.ShellController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Main extends Application {
    private AppState appState;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        String mongoUri = System.getenv("FROM_BILKENTER_MONGO_URI");
        String mongoDb = System.getenv("FROM_BILKENTER_MONGO_DB");
        this.appState = new AppState(new MongoSyncManager(mongoUri, mongoDb));
        stage.setTitle("From Bilkenter");
        stage.setMinWidth(1280);
        stage.setMinHeight(820);
        showLogin();
        stage.show();
    }

    public void showLogin() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        controller.init(this, appState);
        Scene scene = new Scene(root, 1440, 900);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public void showShell() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ShellView.fxml"));
        Parent root = loader.load();
        ShellController controller = loader.getController();
        controller.init(this, appState);
        Scene scene = new Scene(root, 1440, 900);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public void showSignUp() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SignUpView.fxml"));
        Parent root = loader.load();
        SignUpController controller = loader.getController();
        controller.init(this, appState);
        Scene scene = new Scene(root, 1440, 900);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
