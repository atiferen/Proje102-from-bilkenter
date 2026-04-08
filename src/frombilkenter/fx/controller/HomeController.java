package frombilkenter.fx.controller;

import frombilkenter.data.AppState;
import frombilkenter.fx.FxSupport;
import frombilkenter.fx.PageController;
import frombilkenter.model.Listing;
import frombilkenter.model.User;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class HomeController implements PageController {
    @FXML private TextField searchField;
    @FXML private Button filterButton;
    @FXML private ChoiceBox<AppState.SortMode> sortChoice;
    @FXML private FlowPane premiumPane;
    @FXML private FlowPane otherPane;
    @FXML private VBox filtersPane;

    @FXML private CheckBox booksCheck;
    @FXML private CheckBox electronicsCheck;
    @FXML private CheckBox clothingCheck;
    @FXML private CheckBox vehiclesCheck;
    @FXML private CheckBox newCheck;
    @FXML private CheckBox likeNewCheck;
    @FXML private CheckBox goodCheck;
    @FXML private CheckBox fairCheck;
    @FXML private CheckBox poorCheck;
    @FXML private CheckBox premiumOnlyCheck;
    @FXML private Button blackColorButton;
    @FXML private Button whiteColorButton;
    @FXML private Button grayColorButton;
    @FXML private Button redColorButton;
    @FXML private Button blueColorButton;
    @FXML private Button greenColorButton;

    private AppState appState;
    private ShellController shellController;
    private final AppState.FilterState filterState = new AppState.FilterState();

    @Override
    public void init(AppState appState, ShellController shellController) {
        this.appState = appState;
        this.shellController = shellController;
        sortChoice.getItems().setAll(AppState.SortMode.values());
        sortChoice.setValue(AppState.SortMode.ALPHABETICAL);
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            filterState.search = newValue == null ? "" : newValue.trim();
            refresh();
        });
        sortChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> refresh());
        refresh();
    }

    @FXML
    private void toggleFilters() {
        boolean show = !filtersPane.isVisible();
        filtersPane.setVisible(show);
        filtersPane.setManaged(show);
    }

    @FXML
    private void applyFilters() {
        syncFilterState();
        refresh();
    }

    @FXML private void toggleBlack() { toggleColor("Black", blackColorButton); }
    @FXML private void toggleWhite() { toggleColor("White", whiteColorButton); }
    @FXML private void toggleGray() { toggleColor("Gray", grayColorButton); }
    @FXML private void toggleRed() { toggleColor("Red", redColorButton); }
    @FXML private void toggleBlue() { toggleColor("Blue", blueColorButton); }
    @FXML private void toggleGreen() { toggleColor("Green", greenColorButton); }

    @FXML
    private void clearFilters() {
        booksCheck.setSelected(false);
        electronicsCheck.setSelected(false);
        clothingCheck.setSelected(false);
        vehiclesCheck.setSelected(false);
        newCheck.setSelected(false);
        likeNewCheck.setSelected(false);
        goodCheck.setSelected(false);
        fairCheck.setSelected(false);
        poorCheck.setSelected(false);
        premiumOnlyCheck.setSelected(false);
        filterState.categories.clear();
        filterState.colors.clear();
        filterState.conditions.clear();
        filterState.premiumOnly = false;
        searchField.clear();
        resetColorButtons();
        refresh();
    }

    @Override
    public void refresh() {
        if (appState == null) {
            return;
        }
        syncFilterState();
        updateFilterButtonLabel();
        premiumPane.getChildren().clear();
        otherPane.getChildren().clear();

        List<Listing> all = appState.getApprovedListings(filterState, sortChoice.getValue());
        List<Listing> premiumListings = new java.util.ArrayList<>();
        List<Listing> regularListings = new java.util.ArrayList<>();

        for (Listing listing : all) {
            if (appState.getSeller(listing).isPremium()) {
                premiumListings.add(listing);
            } else {
                regularListings.add(listing);
            }
        }

        for (Listing listing : premiumListings) {
            premiumPane.getChildren().add(card(listing));
        }
        for (Listing listing : regularListings) {
            otherPane.getChildren().add(card(listing));
        }
    }

    private void syncFilterState() {
        filterState.categories.clear();
        filterState.conditions.clear();
        if (booksCheck.isSelected()) {
            filterState.categories.add("Books / Course Materials");
            filterState.categories.add("Books");
        }
        if (electronicsCheck.isSelected()) {
            filterState.categories.add("Electronics");
        }
        if (clothingCheck.isSelected()) {
            filterState.categories.add("Clothing");
        }
        if (vehiclesCheck.isSelected()) {
            filterState.categories.add("Vehicles");
        }
        if (newCheck.isSelected()) {
            filterState.conditions.add("New");
        }
        if (likeNewCheck.isSelected()) {
            filterState.conditions.add("Like New");
        }
        if (goodCheck.isSelected()) {
            filterState.conditions.add("Good");
        }
        if (fairCheck.isSelected()) {
            filterState.conditions.add("Fair");
        }
        if (poorCheck.isSelected()) {
            filterState.conditions.add("Poor");
        }
        filterState.premiumOnly = premiumOnlyCheck.isSelected();
    }

    private void updateFilterButtonLabel() {
        int count = filterState.categories.size()
            + filterState.colors.size()
            + filterState.conditions.size()
            + (filterState.premiumOnly ? 1 : 0);
        filterButton.setText(count == 0 ? "Filters" : "Filters (" + count + ")");
    }

    private void toggleColor(String color, Button button) {
        if (filterState.colors.contains(color)) {
            filterState.colors.remove(color);
            button.getStyleClass().remove("color-button-selected");
        } else {
            filterState.colors.add(color);
            if (!button.getStyleClass().contains("color-button-selected")) {
                button.getStyleClass().add("color-button-selected");
            }
        }
        refresh();
    }

    private void resetColorButtons() {
        for (Button button : List.of(blackColorButton, whiteColorButton, grayColorButton, redColorButton, blueColorButton, greenColorButton)) {
            button.getStyleClass().remove("color-button-selected");
        }
    }

    private VBox card(Listing listing) {
        VBox card = new VBox(10);
        card.getStyleClass().add("listing-card");
        card.setPrefWidth(320);
        card.setMaxWidth(320);
        card.setCursor(Cursor.HAND);

        StackPane imageHolder = new StackPane();
        imageHolder.getStyleClass().add("card-image-holder");
        imageHolder.getChildren().add(FxSupport.imageView(listing.getImagePath(), 320, 235));

        User seller = appState.getSeller(listing);
        if (seller.isPremium()) {
            Label premiumBadge = new Label("Premium");
            premiumBadge.getStyleClass().add("premium-badge");
            StackPane.setAlignment(premiumBadge, Pos.TOP_RIGHT);
            StackPane.setMargin(premiumBadge, new Insets(14, 14, 0, 0));
            imageHolder.getChildren().add(premiumBadge);
        }
        if (listing.getPrice() == 0) {
            Label freeBadge = new Label("FREE");
            freeBadge.getStyleClass().add("free-badge");
            StackPane.setAlignment(freeBadge, Pos.TOP_LEFT);
            StackPane.setMargin(freeBadge, new Insets(14, 0, 0, 14));
            imageHolder.getChildren().add(freeBadge);
        }

        Label title = FxSupport.wrapLabel(listing.getTitle(), "card-title", 320);
        Label price = new Label(listing.getPrice() == 0 ? "FREE" : "₺" + listing.getPrice());
        price.getStyleClass().add("price-label");
        Label sellerLabel = new Label(seller.getName() + " " + seller.getSurname().charAt(0) + ".");
        sellerLabel.getStyleClass().add("muted-label");

        card.getChildren().addAll(imageHolder, title, price, sellerLabel);
        card.setOnMouseClicked(event -> {
            try {
                shellController.openListing(listing);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
        return card;
    }
}
