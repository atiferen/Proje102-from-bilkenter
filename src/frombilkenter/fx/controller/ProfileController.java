package frombilkenter.fx.controller;

import frombilkenter.data.AppState;
import frombilkenter.fx.FxSupport;
import frombilkenter.fx.PageController;
import frombilkenter.model.Listing;
import frombilkenter.model.ListingRequest;
import frombilkenter.model.ListingStatus;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProfileController implements PageController {
    @FXML private Label nameLabel;
    @FXML private Label departmentValue;
    @FXML private Label activeListingsValue;
    @FXML private Label completedSalesValue;
    @FXML private Label statusValue;
    @FXML private TableView<RequestRow> requestsTable;
    @FXML private TableColumn<RequestRow, String> requestTitleColumn;
    @FXML private TableColumn<RequestRow, String> requestCategoryColumn;
    @FXML private TableColumn<RequestRow, String> requestPriceColumn;
    @FXML private TableColumn<RequestRow, String> requestStatusColumn;
    @FXML private TableColumn<RequestRow, String> requestDateColumn;
    @FXML private FlowPane myListingsPane;
    @FXML private FlowPane favoritesPane;

    private AppState appState;
    private ShellController shellController;
    private record RequestRow(String title, String category, String price, String status, String submitted) {}

    @Override
    public void init(AppState appState, ShellController shellController) {
        this.appState = appState;
        this.shellController = shellController;
        requestTitleColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().title()));
        requestCategoryColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().category()));
        requestPriceColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().price()));
        requestStatusColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().status()));
        requestDateColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().submitted()));
        requestStatusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }
                setText(item);
                if ("Approved".equals(item)) {
                    setStyle("-fx-text-fill: #16A34A;");
                } else if ("Rejected".equals(item)) {
                    setStyle("-fx-text-fill: #DC2626;");
                } else {
                    setStyle("-fx-text-fill: #2563EB;");
                }
            }
        });
        refresh();
    }

    @FXML
    private void openEditProfile() {
        Stage popup = createPopup("Edit Profile");

        TextField nameField = new TextField(appState.getCurrentUser().getName());
        nameField.setPromptText("Name");
        nameField.getStyleClass().add("app-text-field");

        TextField surnameField = new TextField(appState.getCurrentUser().getSurname());
        surnameField.setPromptText("Surname");
        surnameField.getStyleClass().add("app-text-field");

        ChoiceBox<String> departmentChoice = new ChoiceBox<>();
        departmentChoice.getItems().setAll(
            "Choose Department",
            "Computer Science",
            "Electrical Engineering",
            "Industrial Engineering",
            "Mechanical Engineering",
            "Economics",
            "Law",
            "Psychology",
            "Political Science and Public Administration",
            "History",
            "International Relations",
            "Physics",
            "Chemistry",
            "Mathematics",
            "Molecular Biology and Genetics"
        );
        departmentChoice.setValue(appState.getCurrentUser().getDepartment());
        departmentChoice.getStyleClass().add("app-choicebox");
        departmentChoice.setPrefWidth(360);

        VBox body = popupBody(
            "Update your name and department",
            nameField,
            surnameField,
            departmentChoice
        );

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("secondary-button");
        Button okButton = new Button("OK");
        okButton.getStyleClass().add("primary-button");
        HBox actions = actionBar(cancelButton, okButton);
        body.getChildren().add(actions);

        Scene scene = createPopupScene(body, 400, 300);
        popup.setScene(scene);

        cancelButton.setOnAction(event -> popup.close());
        okButton.setOnAction(event -> {
            AppState.ActionResult result = appState.updateCurrentUserProfile(
                nameField.getText(),
                surnameField.getText(),
                departmentChoice.getValue()
            );
            if (result.success()) {
                popup.close();
                refresh();
            }
            showInfo("Edit Profile", result.message());
        });

        popup.show();
    }

    @FXML
    private void openChangePassword() {
        Stage popup = createPopup("Change Password");

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");
        currentPasswordField.getStyleClass().add("app-text-field");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        newPasswordField.getStyleClass().add("app-text-field");

        PasswordField newPasswordAgainField = new PasswordField();
        newPasswordAgainField.setPromptText("New Password Again");
        newPasswordAgainField.getStyleClass().add("app-text-field");

        VBox body = popupBody(
            "Enter your current password and choose a new one",
            currentPasswordField,
            newPasswordField,
            newPasswordAgainField
        );

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("secondary-button");
        Button okButton = new Button("OK");
        okButton.getStyleClass().add("primary-button");
        HBox actions = actionBar(cancelButton, okButton);
        body.getChildren().add(actions);

        Scene scene = createPopupScene(body, 400, 290);
        popup.setScene(scene);

        cancelButton.setOnAction(event -> popup.close());
        okButton.setOnAction(event -> {
            AppState.ActionResult result = appState.changeCurrentUserPassword(
                currentPasswordField.getText(),
                newPasswordField.getText(),
                newPasswordAgainField.getText()
            );
            if (result.success()) {
                popup.close();
            }
            showInfo("Change Password", result.message());
        });

        popup.show();
    }

    @Override
    public void refresh() {
        nameLabel.setText(appState.getCurrentUser().getFullName());
        departmentValue.setText(appState.getCurrentUser().getDepartment());
        activeListingsValue.setText(String.valueOf(appState.getMyListings().size()));
        completedSalesValue.setText(String.valueOf(appState.getCurrentUser().getCompletedSales()));
        statusValue.setText(appState.getCurrentUser().isPremium() ? "Premium" : "Standard");
        requestsTable.setItems(FXCollections.observableArrayList(buildRequestRows()));

        myListingsPane.getChildren().clear();
        for (Listing listing : appState.getMyListings()) {
            myListingsPane.getChildren().add(compactCard(listing, true));
        }
        favoritesPane.getChildren().clear();
        for (Listing listing : appState.getFavorites()) {
            favoritesPane.getChildren().add(compactCard(listing, false));
        }
    }

    private VBox compactCard(Listing listing, boolean mine) {
        VBox box = new VBox(8);
        box.getStyleClass().add("compact-card");
        box.setCursor(Cursor.HAND);
        box.getChildren().add(FxSupport.imageView(listing.getImagePath(), 220, 140));
        box.getChildren().add(FxSupport.wrapLabel(listing.getTitle(), "card-title", 220));
        box.getChildren().add(new Label(listing.getPrice() == 0 ? "FREE" : "TL " + listing.getPrice()));

        if (mine) {
            Label statusLabel = new Label("Expires in: " + listing.getRemainingDays() + " days");
            statusLabel.getStyleClass().add("subtle-value");

            Button removeButton = new Button("Remove Listing");
            removeButton.getStyleClass().add("secondary-button");
            removeButton.setOnMouseClicked(event -> event.consume());
            removeButton.setOnAction(event -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Remove this listing?", ButtonType.OK, ButtonType.CANCEL);
                confirm.setTitle("Remove Listing");
                confirm.getDialogPane().setHeaderText(null);
                confirm.getDialogPane().getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
                confirm.getDialogPane().getStyleClass().add("app-dialog");
                confirm.showAndWait().ifPresent(type -> {
                    if (type == ButtonType.OK) {
                        appState.deleteListing(listing);
                        showInfo("Remove Listing", "Listing removed successfully.");
                        shellController.refreshAll();
                    }
                });
                event.consume();
            });

            CheckBox soldCheck = new CheckBox("Product sold");
            soldCheck.getStyleClass().add("filter-check");
            soldCheck.setOnMouseClicked(event -> event.consume());
            soldCheck.setOnAction(event -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Mark this listing as sold?", ButtonType.OK, ButtonType.CANCEL);
                confirm.setTitle("Mark as Sold");
                confirm.getDialogPane().setHeaderText(null);
                confirm.getDialogPane().getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
                confirm.getDialogPane().getStyleClass().add("app-dialog");
                confirm.showAndWait().ifPresent(type -> {
                    if (type == ButtonType.OK) {
                        AppState.ActionResult result = appState.markListingSold(listing);
                        showInfo("Product Sold", result.message());
                        shellController.refreshAll();
                    } else {
                        soldCheck.setSelected(false);
                    }
                });
                event.consume();
            });
            box.getChildren().addAll(statusLabel, removeButton, soldCheck);
        } else {
            Label sellerLabel = new Label("Seller: " + appState.getSeller(listing).getFullName());
            sellerLabel.getStyleClass().add("subtle-value");
            Label expiresLabel = new Label("Remaining Time: " + listing.getRemainingDays() + " days");
            expiresLabel.getStyleClass().add("subtle-value");
            box.getChildren().addAll(sellerLabel, expiresLabel);
        }
        box.setOnMouseClicked(event -> {
            if (event.getTarget() instanceof CheckBox) {
                return;
            }
            try {
                shellController.openListing(listing);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
        return box;
    }

    private java.util.List<RequestRow> buildRequestRows() {
        java.util.List<RequestRow> rows = new java.util.ArrayList<>();
        String currentUserId = appState.getCurrentUser().getUserId();

        for (ListingRequest request : appState.getRequests()) {
            if (request.getListing().getSellerId().equals(currentUserId)) {
                rows.add(new RequestRow(
                    request.getListing().getTitle(),
                    request.getListing().getCategory(),
                    request.getListing().getPrice() == 0 ? "FREE" : "₺" + request.getListing().getPrice(),
                    "Pending Admin Approval",
                    request.getSubmittedDate().toString()
                ));
            }
        }

        for (Listing listing : appState.getApprovedListingsForAdmin()) {
            if (listing.getSellerId().equals(currentUserId)) {
                rows.add(new RequestRow(
                    listing.getTitle(),
                    listing.getCategory(),
                    listing.getPrice() == 0 ? "FREE" : "₺" + listing.getPrice(),
                    "Approved",
                    listing.getCreatedAt().toLocalDate().toString()
                ));
            }
        }

        for (Listing listing : appState.getRejectedListings()) {
            if (listing.getSellerId().equals(currentUserId)) {
                rows.add(new RequestRow(
                    listing.getTitle(),
                    listing.getCategory(),
                    listing.getPrice() == 0 ? "FREE" : "₺" + listing.getPrice(),
                    "Rejected",
                    listing.getCreatedAt().toLocalDate().toString()
                ));
            }
        }

        rows.sort(java.util.Comparator.comparing(RequestRow::submitted).reversed());
        return rows;
    }

    private Stage createPopup(String title) {
        Stage popup = new Stage(StageStyle.DECORATED);
        popup.initModality(Modality.NONE);
        popup.setTitle(title);
        popup.setResizable(false);
        return popup;
    }

    private VBox popupBody(String helperText, javafx.scene.Node... fields) {
        Label helper = new Label(helperText);
        helper.getStyleClass().add("muted-label");

        VBox body = new VBox(14);
        body.setPadding(new Insets(18));
        body.getChildren().add(helper);
        body.getChildren().addAll(fields);
        return body;
    }

    private HBox actionBar(Button cancelButton, Button okButton) {
        HBox actions = new HBox(10, cancelButton, okButton);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setPadding(new Insets(8, 0, 0, 0));
        return actions;
    }

    private Scene createPopupScene(VBox body, double width, double height) {
        Scene scene = new Scene(body, width, height);
        scene.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        return scene;
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.getDialogPane().setHeaderText(null);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("app-dialog");
        alert.showAndWait();
    }
}
