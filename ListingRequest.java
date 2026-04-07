import java.time.LocalDate;

public class ListingRequest {
    private final String requestId;
    private final Listing listing;
    private final LocalDate submittedDate;
    private String reason;

    public ListingRequest(String requestId, Listing listing, LocalDate submittedDate, String reason) {
        this.requestId = requestId;
        this.listing = listing;
        this.submittedDate = submittedDate;
        this.reason = reason;
    }

    public String getRequestId() { return requestId; }
    public Listing getListing() { return listing; }
    public LocalDate getSubmittedDate() { return submittedDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
