package frombilkenter.fx.controller;

import frombilkenter.data.AppState;
import frombilkenter.fx.FxSupport;
import frombilkenter.fx.PageController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;

public class SellController implements PageController {
    @FXML private Label formTitleLabel;
    @FXML private Label giveawayNoticeLabel;
    @FXML private Button saleModeButton;
    @FXML private Button giveawayModeButton;
    @FXML private TextField titleField;
    @FXML private ChoiceBox<String> categoryChoice;
    @FXML private TextField brandField;
    @FXML private ChoiceBox<String> sizeChoice;
    @FXML private TextField courseCodeField;
    @FXML private TextField phoneField;
    @FXML private ChoiceBox<String> colorChoice;
    @FXML private TextField priceField;
    @FXML private ChoiceBox<String> conditionChoice;
    @FXML private TextArea descriptionArea;
    @FXML private CheckBox emailVisibilityCheck;
    @FXML private ImageView uploadPreview;
    @FXML private Label uploadName;

    private AppState appState;
    private ShellController shellController;
    private String selectedImagePath;
    private boolean giveawayMode;

    @Override
    public void init(AppState appState, ShellController shellController) {
        this.appState = appState;
        this.shellController = shellController;
        categoryChoice.getItems().setAll("Category", "Electronics", "Books / Course Materials", "Clothing", "Vehicles");
        colorChoice.getItems().setAll("Color", "Black", "White", "Gray", "Red", "Blue", "Green", "Brown", "Silver");
        conditionChoice.getItems().setAll("Condition", "New", "Like New", "Good", "Fair", "Poor");
        sizeChoice.getItems().setAll("Size", "XS", "S", "M", "L", "XL");
        categoryChoice.setValue("Category");
        colorChoice.setValue("Color");
        conditionChoice.setValue("Condition");
        sizeChoice.setValue("Size");
        emailVisibilityCheck.setSelected(false);
        categoryChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> updateDynamicFields());
        switchToSaleMode();
        refreshUpload();
    }

    @FXML
    private void switchToSaleMode() {
        giveawayMode = false;
        formTitleLabel.setText("Create Sale Ad Request");
        giveawayNoticeLabel.setVisible(false);
        giveawayNoticeLabel.setManaged(false);
        priceField.setVisible(true);
        priceField.setManaged(true);
        priceField.setDisable(false);
        saleModeButton.getStyleClass().setAll("primary-button");
        giveawayModeButton.getStyleClass().setAll("secondary-button");
        updateDynamicFields();
    }

    @FXML
    private void switchToGiveawayMode() {
        giveawayMode = true;
        formTitleLabel.setText("Create Giveaway Ad");
        giveawayNoticeLabel.setVisible(true);
        giveawayNoticeLabel.setManaged(true);
        priceField.clear();
        priceField.setVisible(false);
        priceField.setManaged(false);
        priceField.setDisable(true);
        saleModeButton.getStyleClass().setAll("secondary-button");
        giveawayModeButton.getStyleClass().setAll("primary-button");
        updateDynamicFields();
    }

    @FXML
    private void chooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select listing image");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp"));
        File file = chooser.showOpenDialog(uploadPreview.getScene().getWindow());
        if (file != null) {
            selectedImagePath = file.getAbsolutePath();
            refreshUpload();
        }
    }

    @FXML
    private void submitRequest() throws Exception {
        if (selectedImagePath == null || selectedImagePath.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Please upload an image before sending the request.");
            return;
        }
        if ("Category".equals(categoryChoice.getValue()) || "Color".equals(colorChoice.getValue()) || "Condition".equals(conditionChoice.getValue())) {
            showAlert(Alert.AlertType.WARNING, "Please select category, color and condition.");
            return;
        }

        String category = categoryChoice.getValue();
        String size = "";
        String courseCode = "";
        String brand = "";

        if ("Clothing".equals(category)) {
            if ("Size".equals(sizeChoice.getValue())) {
                showAlert(Alert.AlertType.WARNING, "Please select a size.");
                return;
            }
            size = sizeChoice.getValue();
        } else if ("Books / Course Materials".equals(category)) {
            courseCode = courseCodeField.getText() == null ? "" : courseCodeField.getText().trim();
            if (courseCode.isBlank()) {
                showAlert(Alert.AlertType.WARNING, "Please enter a course code.");
                return;
            }
        } else if ("Electronics".equals(category)) {
            brand = brandField.getText() == null ? "" : brandField.getText().trim();
        }

        int price = 0;
        if (!giveawayMode) {
            try {
                price = Integer.parseInt(priceField.getText().trim());
            } catch (Exception exception) {
                showAlert(Alert.AlertType.WARNING, "Please enter a valid price.");
                return;
            }
        }

        appState.submitRequest(
            titleField.getText(),
            category,
            colorChoice.getValue(),
            brand,
            price,
            conditionChoice.getValue(),
            descriptionArea.getText(),
            selectedImagePath,
            phoneField.getText() == null ? "" : phoneField.getText().trim(),
            emailVisibilityCheck.isSelected(),
            giveawayMode,
            size,
            courseCode
        );
        showAlert(Alert.AlertType.INFORMATION, "Request submitted for admin approval.");
        shellController.showProfile();
    }

    @Override
    public void refresh() {
    }

    private void updateDynamicFields() {
        String category = categoryChoice.getValue();
        boolean electronics = "Electronics".equals(category);
        boolean clothing = "Clothing".equals(category);
        boolean books = "Books / Course Materials".equals(category);

        brandField.setVisible(electronics);
        brandField.setManaged(electronics);
        sizeChoice.setVisible(clothing);
        sizeChoice.setManaged(clothing);
        courseCodeField.setVisible(books);
        courseCodeField.setManaged(books);
    }

    private void refreshUpload() {
        boolean hasImage = selectedImagePath != null && !selectedImagePath.isBlank();
        uploadPreview.setVisible(hasImage);
        uploadPreview.setManaged(hasImage);
        if (hasImage) {
            uploadPreview.setImage(FxSupport.imageFromPath(selectedImagePath));
            uploadName.setText(new File(selectedImagePath).getName());
        } else {
            uploadPreview.setImage(null);
            uploadName.setText("No file selected");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.getDialogPane().setHeaderText(null);
        alert.showAndWait();
    }
}
