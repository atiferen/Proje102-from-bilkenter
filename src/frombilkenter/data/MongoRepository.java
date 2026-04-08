package frombilkenter.data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import frombilkenter.model.Listing;
import frombilkenter.model.ListingRequest;
import frombilkenter.model.ListingStatus;
import frombilkenter.model.User;
import frombilkenter.model.UserRole;
import org.bson.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoRepository implements AutoCloseable {
    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> users;
    private final MongoCollection<Document> listings;
    private final MongoCollection<Document> requests;

    public MongoRepository(String connectionString, String databaseName) {
        this.client = MongoClients.create(connectionString);
        this.database = client.getDatabase(databaseName);
        this.users = database.getCollection("users");
        this.listings = database.getCollection("listings");
        this.requests = database.getCollection("listing_requests");
    }

    public void seedIfEmpty(List<User> seedUsers, List<Listing> seedListings, List<ListingRequest> seedRequests) {
        if (users.countDocuments() == 0 && listings.countDocuments() == 0 && requests.countDocuments() == 0) {
            for (User user : seedUsers) {
                saveUser(user);
            }
            for (Listing listing : seedListings) {
                saveListing(listing);
            }
            for (ListingRequest request : seedRequests) {
                saveRequest(request);
            }
        }
    }

    public List<User> loadUsers() {
        List<User> result = new ArrayList<>();
        for (Document doc : users.find()) {
            User user = new User(
                doc.getString("userId"),
                doc.getString("email"),
                doc.getString("name"),
                doc.getString("surname"),
                doc.getString("department"),
                UserRole.valueOf(doc.getString("role")),
                doc.getBoolean("premium", false),
                doc.getInteger("completedSales", 0)
            );
            user.setPassword(doc.getString("password") == null ? "1234" : doc.getString("password"));
            List<String> favorites = doc.getList("favoriteListingIds", String.class, new ArrayList<>());
            for (String favorite : favorites) {
                if (!user.getFavoriteListingIds().contains(favorite)) {
                    user.toggleFavorite(favorite);
                }
            }
            result.add(user);
        }
        return result;
    }

    public List<Listing> loadListings() {
        List<Listing> result = new ArrayList<>();
        for (Document doc : listings.find()) {
            result.add(toListing(doc));
        }
        return result;
    }

    public List<ListingRequest> loadRequests() {
        List<ListingRequest> result = new ArrayList<>();
        for (Document doc : requests.find()) {
            result.add(new ListingRequest(
                doc.getString("requestId"),
                toListing(doc.get("listing", Document.class)),
                toLocalDate(doc.getDate("submittedDate")),
                doc.getString("reason")
            ));
        }
        return result;
    }

    public void saveUser(User user) {
        users.replaceOne(eq("userId", user.getUserId()), toDocument(user), new ReplaceOptions().upsert(true));
    }

    public void saveListing(Listing listing) {
        listings.replaceOne(eq("listingId", listing.getListingId()), toDocument(listing), new ReplaceOptions().upsert(true));
    }

    public void deleteListing(String listingId) {
        listings.deleteOne(eq("listingId", listingId));
    }

    public void saveRequest(ListingRequest request) {
        requests.replaceOne(eq("requestId", request.getRequestId()), toDocument(request), new ReplaceOptions().upsert(true));
    }

    public void deleteRequest(String requestId) {
        requests.deleteOne(eq("requestId", requestId));
    }

    public void clearAll() {
        users.deleteMany(new Document());
        listings.deleteMany(new Document());
        requests.deleteMany(new Document());
    }

    private Document toDocument(User user) {
        return new Document("userId", user.getUserId())
            .append("email", user.getEmail())
            .append("name", user.getName())
            .append("surname", user.getSurname())
            .append("department", user.getDepartment())
            .append("role", user.getRole().name())
            .append("password", user.getPassword())
            .append("premium", user.isPremium())
            .append("completedSales", user.getCompletedSales())
            .append("favoriteListingIds", user.getFavoriteListingIds());
    }

    private Document toDocument(Listing listing) {
        return new Document("listingId", listing.getListingId())
            .append("title", listing.getTitle())
            .append("sellerId", listing.getSellerId())
            .append("category", listing.getCategory())
            .append("color", listing.getColor())
            .append("condition", listing.getCondition())
            .append("price", listing.getPrice())
            .append("giveaway", listing.isGiveaway())
            .append("brand", listing.getBrand())
            .append("size", listing.getSize())
            .append("courseCode", listing.getCourseCode())
            .append("phoneNumber", listing.getPhoneNumber())
            .append("imagePath", listing.getImagePath())
            .append("description", listing.getDescription())
            .append("emailVisible", listing.isEmailVisible())
            .append("createdAt", toDate(listing.getCreatedAt()))
            .append("expireAt", toDate(listing.getExpireAt()))
            .append("status", listing.getStatus().name());
    }

    private Document toDocument(ListingRequest request) {
        return new Document("requestId", request.getRequestId())
            .append("listing", toDocument(request.getListing()))
            .append("submittedDate", toDate(request.getSubmittedDate().atStartOfDay()))
            .append("reason", request.getReason());
    }

    private Listing toListing(Document doc) {
        return new Listing(
            doc.getString("listingId"),
            doc.getString("title"),
            doc.getString("sellerId"),
            doc.getString("category"),
            doc.getString("color"),
            doc.getString("condition"),
            doc.getInteger("price", 0),
            doc.getBoolean("giveaway", false),
            doc.getString("brand"),
            doc.getString("size"),
            doc.getString("courseCode"),
            doc.getString("phoneNumber"),
            doc.getString("imagePath"),
            doc.getString("description"),
            doc.getBoolean("emailVisible", true),
            toLocalDateTime(doc.getDate("createdAt")),
            toLocalDateTime(doc.getDate("expireAt")),
            ListingStatus.valueOf(doc.getString("status"))
        );
    }

    private Date toDate(LocalDateTime value) {
        return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDateTime toLocalDateTime(Date value) {
        return Instant.ofEpochMilli(value.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private LocalDate toLocalDate(Date value) {
        return Instant.ofEpochMilli(value.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public void close() {
        client.close();
    }
}