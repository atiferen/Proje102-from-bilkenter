package frombilkenter.data;

import frombilkenter.model.Listing;
import frombilkenter.model.ListingRequest;
import frombilkenter.model.ListingStatus;
import frombilkenter.model.User;
import frombilkenter.model.UserRole;
import frombilkenter.mail.SmtpMailService;

import jakarta.mail.MessagingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class AppState {
    public enum SortMode {
        ALPHABETICAL("Alphabetical"),
        PRICE_LOW_HIGH("Price: Low to High"),
        PRICE_HIGH_LOW("Price: High to Low");

        private final String label;

        SortMode(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public static class FilterState {
        public final Set<String> categories = new HashSet<>();
        public final Set<String> colors = new HashSet<>();
        public final Set<String> conditions = new HashSet<>();
        public boolean premiumOnly;
        public String search = "";
    }

    public record ActionResult(boolean success, String message) {}

    private record PendingRegistration(String email, String name, String surname, String department, String password, String code) {}

    private final Map<String, User> users = new HashMap<>();
    private final List<Listing> listings = new ArrayList<>();
    private final List<ListingRequest> requests = new ArrayList<>();
    private final Map<String, String> passwordResetCodes = new HashMap<>();
    private final Map<String, PendingRegistration> pendingRegistrations = new HashMap<>();
    private User currentUser;
    private final MongoSyncManager mongoSyncManager;
    private final SmtpMailService mailService;
    private MongoRepository repository;

    public AppState(MongoSyncManager mongoSyncManager) {
        this.mongoSyncManager = mongoSyncManager;
        this.mailService = SmtpMailService.fromEnvironment();
        seed();
        initializePersistence();
    }

    private void seed() {
        users.clear();
        listings.clear();
        requests.clear();
        User lina = new User("u1", "lina.acar@ug.bilkent.edu.tr", "Lina", "Acar", "Computer Science", UserRole.SELLER, true, 14);
        User mert = new User("u2", "mert.yalcin@ug.bilkent.edu.tr", "Mert", "Yalcin", "Industrial Engineering", UserRole.SELLER, true, 12);
        User selin = new User("u3", "selin.arda@ug.bilkent.edu.tr", "Selin", "Arda", "Psychology", UserRole.SELLER, true, 10);
        User bora = new User("u4", "bora.cetin@ug.bilkent.edu.tr", "Bora", "Cetin", "Economics", UserRole.SELLER, false, 6);
        User dila = new User("u5", "dila.eren@ug.bilkent.edu.tr", "Dila", "Eren", "Electrical Engineering", UserRole.SELLER, false, 4);
        User kerem = new User("u6", "kerem.aydin@ug.bilkent.edu.tr", "Kerem", "Aydin", "Mechanical Engineering", UserRole.SELLER, false, 2);
        User admin = new User("admin1", "eren.kurt@ug.bilkent.edu.tr", "Eren", "Kurt", "Computer Science", UserRole.ADMIN, false, 0);
        lina.setPassword("1234");
        mert.setPassword("1234");
        selin.setPassword("1234");
        bora.setPassword("1234");
        dila.setPassword("1234");
        kerem.setPassword("1234");
        admin.setPassword("1234");

        lina.toggleFavorite("l2");
        lina.toggleFavorite("l4");

        for (User user : List.of(lina, mert, selin, bora, dila, kerem, admin)) {
            users.put(user.getUserId(), user);
        }
        normalizePremiumStatuses();

        listings.add(new Listing("l1", "iPhone 15 Pro Max - 256 GB", "u1", "Electronics", "Black", "Like New",
            62000, false, "Apple", "", "", "+90 555 123 4567", "assets/iphone.png",
            "Clean iPhone 15 Pro Max in very good condition. Battery health is strong and the device is fully functional.",
            true, LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(13), ListingStatus.APPROVED));
        listings.add(new Listing("l2", "Monster Tulpar Gaming Laptop", "u2", "Electronics", "Black", "Good",
            38500, false, "Monster", "", "", "+90 555 245 9011", "assets/monster.png",
            "Strong gaming laptop with clean keyboard and stable performance for both gaming and coursework.", true,
            LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(18), ListingStatus.APPROVED));
        listings.add(new Listing("l3", "Electric Bicycle - Great Condition", "u3", "Vehicles", "White", "Good",
            27500, false, "Volta", "", "", "+90 555 341 0099", "assets/ebike.png",
            "Electric bicycle with solid battery performance and comfortable city riding experience.", true,
            LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(10), ListingStatus.APPROVED));
        listings.add(new Listing("l4", "Galatasaray Home Jersey", "u4", "Clothing", "Red", "Like New",
            1800, false, "Puma", "M", "", "+90 555 612 1104", "assets/jersey.png",
            "Official jersey in very good condition. Worn a few times only.", true,
            LocalDateTime.now().minusDays(4), LocalDateTime.now().plusDays(15), ListingStatus.APPROVED));
        listings.add(new Listing("l5", "Nutuk - Annotated Edition", "u5", "Books / Course Materials", "Red", "Good",
            250, false, "Akkoyunlu", "", "HIST200", "+90 555 442 1178", "assets/nutuk.png",
            "Clean copy of Nutuk with light highlights on a few pages.", true,
            LocalDateTime.now().minusDays(6), LocalDateTime.now().plusDays(20), ListingStatus.APPROVED));
        listings.add(new Listing("l6", "Basys 3 FPGA Development Board", "u6", "Electronics", "Blue", "Good",
            3200, false, "Digilent", "", "", "+90 555 721 8830", "assets/fpga.png",
            "Working FPGA board suitable for digital design and embedded lab assignments.", true,
            LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(12), ListingStatus.APPROVED));
        listings.add(new Listing("l7", "Statistics Course Pack", "u4", "Books / Course Materials", "Brown", "Good",
            450, false, "Bilkent Print", "", "STAT201", "+90 555 612 1104", "assets/books.jpeg",
            "Course pack with notes, solved examples and past exercise sheets.", true,
            LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(14), ListingStatus.APPROVED));
        listings.add(new Listing("l8", "Minimal Study Lamp", "u5", "Electronics", "White", "New",
            300, false, "No Brand", "", "", "+90 555 442 1178", "assets/lamp.jpeg",
            "Compact study lamp with simple modern design and soft white light.", true,
            LocalDateTime.now().minusDays(3), LocalDateTime.now().plusDays(21), ListingStatus.APPROVED));

        requests.add(new ListingRequest("r1",
            new Listing("p1", "iPhone 14 Pro - Space Black", "u1", "Electronics", "Black", "Good",
                48000, false, "Apple", "", "", "+90 555 123 4567", "assets/iphone.png", "Good condition phone with original box.", true,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), ListingStatus.PENDING),
            LocalDate.now().minusDays(1), ""));
        requests.add(new ListingRequest("r2",
            new Listing("p2", "Galatasaray Match Jersey", "u4", "Clothing", "Red", "Good",
                1500, false, "Puma", "L", "", "+90 555 612 1104", "assets/jersey.png", "Clean jersey with no tears or stains.", true,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), ListingStatus.PENDING),
            LocalDate.now().minusDays(2), ""));
        requests.add(new ListingRequest("r3",
            new Listing("p3", "FPGA Starter Kit", "u6", "Electronics", "Blue", "Good",
                2900, false, "Basys", "", "", "+90 555 721 8830", "assets/fpga.png", "Board works well and includes extra cables.", true,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), ListingStatus.PENDING),
            LocalDate.now().minusDays(3), ""));
        requests.add(new ListingRequest("r4",
            new Listing("p4", "Used Nutuk Copy", "u5", "Books / Course Materials", "Red", "Fair",
                120, false, "Akkoyunlu", "", "HIST200", "+90 555 442 1178", "assets/nutuk.png", "Readable copy with visible wear.", true,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30), ListingStatus.PENDING),
            LocalDate.now().minusDays(4), ""));

        currentUser = lina;
    }
      private void initializePersistence() {
        if (!mongoSyncManager.isConfigured()) {
            return;
        }
        try {
            repository = mongoSyncManager.openRepository();
            repository.seedIfEmpty(new ArrayList<>(users.values()), new ArrayList<>(listings), new ArrayList<>(requests));
            loadFromRepository();
            normalizeAllUserPasswords();
        } catch (Exception exception) {
            repository = null;
            System.err.println("MongoDB connection failed, continuing with seed data: " + exception.getMessage());
        }
    }

    private void loadFromRepository() {
        users.clear();
        listings.clear();
        requests.clear();
        for (User user : repository.loadUsers()) {
            users.put(user.getUserId(), user);
        }
        normalizePremiumStatuses();
        listings.addAll(repository.loadListings());
        requests.addAll(repository.loadRequests());
        cleanupExpiredListings();
        currentUser = users.getOrDefault("u1", users.values().stream().findFirst().orElse(null));
    }

    public MongoSyncManager getMongoSyncManager() {
        return mongoSyncManager;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User findUserByEmail(String email) {
        return users.values().stream()
            .filter(user -> user.getEmail().equalsIgnoreCase(email))
            .findFirst()
            .orElse(null);
    }

    public User registerUser(String email, String name, String surname, String department, String password) {
        String userId = "u" + (users.size() + 10);
        User user = new User(userId, email, name, surname, department, UserRole.SELLER, false, 0);
        user.setPassword(password);
        updatePremiumStatus(user);
        users.put(userId, user);
        persistUser(user);
        currentUser = user;
        return user;
    }

    public User authenticate(String email, String password) {
        User user = findUserByEmail(email);
        if (user != null && user.matchesPassword(password)) {
            currentUser = user;
            return user;
        }
        return null;
    }
     public ActionResult sendSignUpVerificationCode(String email, String name, String surname, String department, String password) {
        if (findUserByEmail(email) != null) {
            return new ActionResult(false, "This e-mail is already registered.");
        }
        if (!mailService.isConfigured()) {
            return new ActionResult(false, "SMTP settings are missing. Please configure e-mail sending first.");
        }
        String code = generateCode();
        try {
            mailService.sendVerificationCode(email, code, "Sign Up");
            pendingRegistrations.put(email.toLowerCase(), new PendingRegistration(email, name, surname, department, password, code));
            return new ActionResult(true, "Verification code sent to " + email + ".");
        } catch (MessagingException exception) {
            return new ActionResult(false, "E-mail could not be sent: " + exception.getMessage());
        }
    }

    public ActionResult verifySignUpCode(String email, String code) {
        PendingRegistration pending = pendingRegistrations.get(email.toLowerCase());
        if (pending == null) {
            return new ActionResult(false, "No pending sign-up request found for this e-mail.");
        }
        if (!pending.code().equals(code)) {
            return new ActionResult(false, "Verification code is incorrect.");
        }
        registerUser(pending.email(), pending.name(), pending.surname(), pending.department(), pending.password());
        pendingRegistrations.remove(email.toLowerCase());
        return new ActionResult(true, "Account created successfully.");
    }

    public ActionResult sendPasswordResetCode(String email) {
        User user = findUserByEmail(email);
        if (user == null) {
            return new ActionResult(false, "No account found for this e-mail.");
        }
        if (!mailService.isConfigured()) {
            return new ActionResult(false, "SMTP settings are missing. Please configure e-mail sending first.");
        }
        String code = generateCode();
        try {
            mailService.sendVerificationCode(email, code, "Password Reset");
            passwordResetCodes.put(email.toLowerCase(), code);
            return new ActionResult(true, "Verification code sent to " + email + ".");
        } catch (MessagingException exception) {
            return new ActionResult(false, "E-mail could not be sent: " + exception.getMessage());
        }
    }

    public ActionResult resetPassword(String email, String code, String newPassword) {
        User user = findUserByEmail(email);
        if (user == null) {
            return new ActionResult(false, "No account found for this e-mail.");
        }
        String expectedCode = passwordResetCodes.get(email.toLowerCase());
        if (expectedCode == null) {
            return new ActionResult(false, "No reset request found for this e-mail.");
        }
        if (!expectedCode.equals(code)) {
            return new ActionResult(false, "Verification code is incorrect.");
        }
        user.setPassword(newPassword);
        persistUser(user);
        passwordResetCodes.remove(email.toLowerCase());
        return new ActionResult(true, "Password updated successfully.");
    }

    public ActionResult changeCurrentUserPassword(String currentPassword, String newPassword, String confirmPassword) {
        if (currentUser == null) {
            return new ActionResult(false, "No active user found.");
        }
        if (currentPassword == null || currentPassword.isBlank() || newPassword == null || newPassword.isBlank()
            || confirmPassword == null || confirmPassword.isBlank()) {
            return new ActionResult(false, "Please fill in all password fields.");
        }
        if (!currentUser.matchesPassword(currentPassword)) {
            return new ActionResult(false, "Current password is incorrect.");
        }
        if (!newPassword.equals(confirmPassword)) {
            return new ActionResult(false, "New passwords do not match.");
        }
        currentUser.setPassword(newPassword);
        persistUser(currentUser);
        return new ActionResult(true, "Password updated successfully.");
    }

    public ActionResult updateCurrentUserProfile(String name, String surname, String department) {
        if (currentUser == null) {
            return new ActionResult(false, "No active user found.");
        }
        if (name == null || name.isBlank() || surname == null || surname.isBlank()
            || department == null || department.isBlank() || "Choose Department".equals(department)) {
            return new ActionResult(false, "Please fill in all profile fields.");
        }
        currentUser.setName(name.trim());
        currentUser.setSurname(surname.trim());
        currentUser.setDepartment(department.trim());
        persistUser(currentUser);
        return new ActionResult(true, "Profile updated successfully.");
    }public User getSeller(Listing listing) {
        return users.get(listing.getSellerId());
    }

    public List<User> getLeaderboardUsers() {
        cleanupExpiredListings();
        return users.values().stream()
            .filter(User::isSeller)
            .sorted(Comparator.comparingInt(User::getCompletedSales).reversed())
            .collect(Collectors.toList());
    }

    public List<Listing> getApprovedListings(FilterState filter, SortMode sortMode) {
        cleanupExpiredListings();
        return listings.stream()
            .filter(l -> l.getStatus() == ListingStatus.APPROVED)
            .filter(l -> !l.isExpired())
            .filter(l -> filter.categories.isEmpty() || filter.categories.contains(l.getCategory()))
            .filter(l -> filter.colors.isEmpty() || filter.colors.contains(l.getColor()))
            .filter(l -> filter.conditions.isEmpty() || filter.conditions.contains(l.getCondition()))
            .filter(l -> !filter.premiumOnly || getSeller(l).isPremium())
            .filter(l -> filter.search.isBlank()
                || l.getTitle().toLowerCase().contains(filter.search.toLowerCase())
                || l.getDescription().toLowerCase().contains(filter.search.toLowerCase()))
            .sorted(getComparator(sortMode))
            .collect(Collectors.toList());
    }

    private Comparator<Listing> getComparator(SortMode sortMode) {
        return switch (sortMode) {
            case PRICE_LOW_HIGH -> Comparator.comparingInt(Listing::getPrice);
            case PRICE_HIGH_LOW -> Comparator.comparingInt(Listing::getPrice).reversed();
            case ALPHABETICAL -> Comparator.comparing(Listing::getTitle, String.CASE_INSENSITIVE_ORDER);
        };
    }

    public List<ListingRequest> getRequests() {
        return requests;
    }

    public List<Listing> getApprovedListingsForAdmin() {
        cleanupExpiredListings();
        return listings.stream()
            .filter(listing -> listing.getStatus() == ListingStatus.APPROVED)
            .toList();
    }

    public List<Listing> getRejectedListings() {
        cleanupExpiredListings();
        return listings.stream()
            .filter(listing -> listing.getStatus() == ListingStatus.REJECTED)
            .toList();
    }

    public List<Listing> getFavorites() {
        cleanupExpiredListings();
        return listings.stream()
            .filter(l -> currentUser.getFavoriteListingIds().contains(l.getListingId()))
            .filter(l -> !l.isExpired())
            .collect(Collectors.toList());
    }

    public List<Listing> getMyListings() {
        cleanupExpiredListings();
        return listings.stream()
            .filter(l -> l.getSellerId().equals(currentUser.getUserId()))
            .filter(l -> !l.isExpired())
            .collect(Collectors.toList());
    }

    public void toggleFavorite(Listing listing) {
        currentUser.toggleFavorite(listing.getListingId());
        persistUser(currentUser);
    }