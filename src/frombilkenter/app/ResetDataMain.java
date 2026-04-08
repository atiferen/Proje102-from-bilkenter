package frombilkenter.app;

import frombilkenter.data.AppState;
import frombilkenter.data.MongoSyncManager;

public class ResetDataMain {
    public static void main(String[] args) {
        String mongoUri = System.getenv("FROM_BILKENTER_MONGO_URI");
        String mongoDb = System.getenv("FROM_BILKENTER_MONGO_DB");
        AppState appState = new AppState(new MongoSyncManager(mongoUri, mongoDb));
        AppState.ActionResult result = appState.resetPersistenceToSeed();
        System.out.println(result.message());
    }
}
