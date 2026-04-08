package frombilkenter.data;

public class MongoSyncManager {
    private final String connectionString;
    private final String databaseName;
    private MongoRepository repository;

    public MongoSyncManager(String connectionString, String databaseName) {
        this.connectionString = connectionString;
        this.databaseName = databaseName;
    }

    public boolean isConfigured() {
        return connectionString != null && !connectionString.isBlank()
            && databaseName != null && !databaseName.isBlank();
    }

    public String getStatusText() {
        if (!isConfigured()) {
            return "MongoDB hazir degil: uygulama seed verilerle calisiyor.";
        }
        return "MongoDB baglantisi yapilandirildi: " + databaseName;
    }

    public MongoRepository openRepository() {
        if (!isConfigured()) {
            return null;
        }
        if (repository == null) {
            repository = new MongoRepository(connectionString, databaseName);
        }
        return repository;
    }
}