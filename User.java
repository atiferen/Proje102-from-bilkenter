package frombilkenter.model;
import java.util.ArrayList;
import java.util.List;

public class User {
    private final String userId;
    private final String email;
    private String name;
    private String surname;
    private String department;
    private final UserRole role;
    private String password;
    private boolean premium;
    private int completedSales;
    private final List<String> favoriteListingIds = new ArrayList<>();

    public User(String userId, String email, String name, String surname, String department,
                UserRole role, boolean premium, int completedSales) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.department = department;
        this.role = role;
        this.password = "1234";
        this.premium = premium;
        this.completedSales = completedSales;
    }

    public String getUserId() { 
        return userId; }
    public String getEmail() { 
        return email; }
    public String getName() { 
        return name; }
    public void setName(String name) { 
        this.name = name; }
    public String getSurname() { 
        return surname; }
    public void setSurname(String surname) { 
        this.surname = surname; }
    public String getFullName() { 
        return name + " " + surname; }
    public String getDepartment() { 
        return department; }
    public void setDepartment(String department) { 
        this.department = department; }
    public UserRole getRole() { 
        return role; }
    public String getPassword() { 
        return password; }
    public void setPassword(String password) { 
        this.password = password; }

    public boolean isPremium() { 
        return premium; }
    public void setPremium(boolean premium) { 
        this.premium = premium; }
    public int getCompletedSales() { 
        return completedSales; }

    public void setCompletedSales(int completedSales) { 
        this.completedSales = completedSales; }

        
    public void incrementCompletedSales() { 
        this.completedSales++; }
    public List<String> getFavoriteListingIds() { 
        return favoriteListingIds; }
    public boolean isAdmin() { 
        
        return role == UserRole.ADMIN; }
    public boolean isSeller() { 
        
        return role == UserRole.SELLER || role == UserRole.ADMIN; }
    public boolean matchesPassword(String rawPassword) { 
        return password != null && password.equals(rawPassword); }

    public void toggleFavorite(String listingId) {
        if (favoriteListingIds.contains(listingId)) {
            favoriteListingIds.remove(listingId);
        } else {

            favoriteListingIds.add(listingId);
        }
    }
}