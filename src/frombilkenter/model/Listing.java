
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Listing {
    private final String listingId;
    private final String title;
    private final String sellerId;
    private final String category;
    private final String color;
    private final String condition;
    private final int price;
    private final boolean giveaway;
    private final String brand;
    private final String size;
    private final String courseCode;
    private final String phoneNumber;
    private final String imagePath;
    private final String description;
    private final boolean emailVisible;
    private LocalDateTime createdAt;
    private LocalDateTime expireAt;
    private ListingStatus status;

    public Listing(String listingId, String title, String sellerId, String category, String color, String condition,
                   int price, boolean giveaway, String brand, String size, String courseCode, String phoneNumber,
                   String imagePath, String description, boolean emailVisible,
                   LocalDateTime createdAt, LocalDateTime expireAt, ListingStatus status) {
        this.listingId = listingId;
        this.title = title;
        this.sellerId = sellerId;
        this.category = category;
        this.color = color;
        this.condition = condition;
        this.price = price;
        this.giveaway = giveaway;
        this.brand = brand;
        this.size = size;
        this.courseCode = courseCode;
        this.phoneNumber = phoneNumber;
        this.imagePath = imagePath;
        this.description = description;
        this.emailVisible = emailVisible;
        this.createdAt = createdAt;
        this.expireAt = expireAt;
        this.status = status;
    }

    public String getListingId() { 
        return listingId; }
    public String getTitle() { 
        return title; }
    public String getSellerId() { 
        return sellerId; }

    public String getCategory() { 
        return category; }
    public String getColor() { 
        return color; }
    public String getCondition() { 
        return condition; }
    public int getPrice() { 
        return price; }
        
    public boolean isGiveaway() { 
        return giveaway; }
    public String getBrand() { 
        return brand; }
    public String getSize() { 
        return size; }

    public String getCourseCode() { 
        return courseCode; }
    public String getPhoneNumber() { 
        return phoneNumber; }
    public String getImagePath() { 
        return imagePath; }
    public String getDescription() { 
        return description; }

    public boolean isEmailVisible() { 
        return emailVisible; }
    public LocalDateTime getCreatedAt() { 
        return createdAt; }

    public LocalDateTime getExpireAt() { 
        return expireAt; }
    public ListingStatus getStatus() { 
        return status; }
    public void setStatus(ListingStatus status) { 
        this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; }
    public void setExpireAt(LocalDateTime expireAt) { 
        this.expireAt = expireAt; }
    public boolean isExpired() { 
        return LocalDateTime.now().isAfter(expireAt); }

    public long getRemainingDays() {
        return Math.max(0, ChronoUnit.DAYS.between(LocalDateTime.now(), expireAt));
    }
}