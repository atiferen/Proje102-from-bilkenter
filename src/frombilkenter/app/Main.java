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
}
