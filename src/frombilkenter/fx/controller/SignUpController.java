package frombilkenter.fx.controller;

import frombilkenter.app.Main;
import frombilkenter.data.AppState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController {
    @FXML private TextField emailField;
    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private ChoiceBox<String> departmentChoice;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private Main app;
    private AppState appState;

    public void init(Main app, AppState appState) {
        this.app = app;
        this.appState = appState;
        departmentChoice.setItems(FXCollections.observableArrayList(
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
        ));
        departmentChoice.setValue("Choose Department");
    }

    @FXML
    private void handleCreateAccount() throws Exception {
        String email = text(emailField);
        String name = text(nameField);
        String surname = text(surnameField);
        String department = departmentChoice.getValue();
        String password = text(passwordField);
        String confirm = text(confirmPasswordField);

        if (email.isBlank() || name.isBlank() || surname.isBlank() || department == null || "Choose Department".equals(department)
            || password.isBlank() || confirm.isBlank()) {
            showInfo("Create Account", "Please fill in all fields.");
            return;
        }
        if (!isBilkentEmail(email)) {
            showInfo("Create Account", "Please use a Bilkent e-mail address.");
            return;
        }
        if (!password.equals(confirm)) {
            showInfo("Create Account", "Confirm password must match the password.");
            return;
        }
        AppState.ActionResult sendResult = appState.sendSignUpVerificationCode(email, name, surname, department, password);
        if (!sendResult.success()) {
            showInfo("Create Account", sendResult.message());
            return;
        }
        Alert verificationDialog = new Alert(Alert.AlertType.NONE, "", ButtonType.CANCEL, ButtonType.OK);
        verificationDialog.setTitle("Verification Code");
        verificationDialog.getDialogPane().setHeaderText("Check your Bilkent e-mail to get your verification code");
        TextField verificationField = new TextField();
        verificationField.setPromptText("Enter verification code");
        verificationDialog.getDialogPane().setContent(verificationField);
        styleDialog(verificationDialog.getDialogPane());
        verificationDialog.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK && !text(verificationField).isBlank()) {
                AppState.ActionResult verifyResult = appState.verifySignUpCode(email, text(verificationField));
                showInfo("Verification Code", verifyResult.message());
                if (verifyResult.success()) {
                    try {
                        app.showLogin();
                    } catch (Exception exception) {
                        throw new RuntimeException(exception);
                    }
                }
            }
        });
    }

    @FXML
    private void handleBackToLogin() throws Exception {
        app.showLogin();
    }

    private String text(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private boolean isBilkentEmail(String email) {
        String normalized = email == null ? "" : email.trim().toLowerCase();
        int atIndex = normalized.indexOf('@');
        if (atIndex < 1 || atIndex == normalized.length() - 1) {
            return false;
        }
        String domain = normalized.substring(atIndex + 1);
        return domain.equals("bilkent.edu.tr") || domain.endsWith(".bilkent.edu.tr");
    }

    private void styleDialog(DialogPane pane) {
        pane.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        pane.getStyleClass().add("app-dialog");
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.getDialogPane().setHeaderText(null);
        styleDialog(alert.getDialogPane());
        alert.showAndWait();
    }
}
