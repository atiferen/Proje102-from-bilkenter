package frombilkenter.fx.controller;

import frombilkenter.data.AppState;
import frombilkenter.fx.FxSupport;
import frombilkenter.fx.PageController;
import frombilkenter.model.Listing;
import frombilkenter.model.User;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class DetailController implements PageController {
    @FXML private ImageView listingImage;
    @FXML private Label titleLabel;
    @FXML private Label priceLabel;
    @FXML private Label expiresLabel;
    @FXML private Button favoriteButton;
    @FXML private GridPane infoGrid;
    @FXML private TextArea descriptionArea;

    private AppState appState;
    private Listing listing;

    @Override
    public void init(AppState appState, ShellController shellController) {
        this.appState = appState;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    @FXML
    private void toggleFavorite() {
        if (listing != null) {
            appState.toggleFavorite(listing);
            refresh();
        }
    }

    @FXML
    private void contactSeller() {
        if (listing == null) {
            return;
        }
        if (listing.getPhoneNumber() != null && !listing.getPhoneNumber().isBlank()) {
            Alert alert = new Alert(
                Alert.AlertType.INFORMATION,
                "You can contact seller via this phone number:\n" + listing.getPhoneNumber(),
                ButtonType.OK
            );
            alert.setTitle("Contact Seller");
            alert.getDialogPane().setHeaderText(null);
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
            alert.getDialogPane().getStyleClass().add("app-dialog");
            alert.showAndWait();
            return;
        }
        User seller = appState.getSeller(listing);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Contact seller at: " + seller.getEmail(), ButtonType.OK);
        alert.setTitle("Contact Seller");
        alert.getDialogPane().setHeaderText(null);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("app-dialog");
        alert.showAndWait();
    }

    @Override
    public void refresh() {
        if (listing == null && appState != null) {
            java.util.List<Listing> approvedListings =
                appState.getApprovedListings(new AppState.FilterState(), AppState.SortMode.ALPHABETICAL);
            if (!approvedListings.isEmpty()) {
                listing = approvedListings.get(0);
            }
        }
        if (listing == null) {
            return;
        }

        User seller = appState.getSeller(listing);
        listingImage.setImage(FxSupport.imageFromPath(listing.getImagePath()));
        titleLabel.setText(listing.getTitle());
        priceLabel.setText(listing.getPrice() == 0 ? "FREE" : "TL " + listing.getPrice());
        expiresLabel.setText("Expires in: " + listing.getRemainingDays() + " days");
        favoriteButton.setText(appState.getCurrentUser().getFavoriteListingIds().contains(listing.getListingId()) ? "♥" : "♡");
        descriptionArea.setText(listing.getDescription());

        infoGrid.getChildren().clear();
        int row = 0;
        addRow(row++, "Seller", seller.getFullName(), false);
        if (listing.isEmailVisible()) {
            addRow(row++, "Email", seller.getEmail(), true);
        }
        addRow(row++, "Department", seller.getDepartment(), false);
        addRow(row++, "Condition", listing.getCondition(), false);
        addRow(row++, "Color", listing.getColor(), false);
        addRow(row, "Status", seller.isPremium() ? "Premium Seller" : "Standard Seller", seller.isPremium());
    }

    private void addRow(int row, String key, String value, boolean subtleValue) {
        Label keyLabel = new Label(key);
        keyLabel.getStyleClass().add("muted-label");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add(subtleValue ? "subtle-value" : "info-value");

        GridPane.setMargin(keyLabel, new Insets(0, 0, 10, 0));
        GridPane.setMargin(valueLabel, new Insets(0, 0, 10, 0));
        infoGrid.add(keyLabel, 0, row);
        infoGrid.add(valueLabel, 1, row);
    }
}
