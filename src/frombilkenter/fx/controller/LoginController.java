package frombilkenter.fx.controller;

import frombilkenter.app.Main;
import frombilkenter.data.AppState;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private Main app;
    private AppState appState;

    public void init(Main app, AppState appState) {
        this.app = app;
        this.appState = appState;
    }

    @FXML
    private void handleLogin() throws Exception {
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText().trim();
        if (email.isBlank() || password.isBlank()) {
            showInfo("Sign In", "Please enter your e-mail and password.");
            return;
        }
        if (!isBilkentEmail(email)) {
            showInfo("Sign In", "Please use a Bilkent e-mail address.");
            return;
        }
        if (appState.authenticate(email, password) == null) {
            showInfo("Sign In", "E-mail or password is incorrect.");
            return;
        }
        app.showShell();
    }

    @FXML
    private void handleCreateAccount() throws Exception {
        app.showSignUp();
    }

    @FXML
    private void handleForgotPassword() {
        TextField emailInput = new TextField();
        emailInput.setPromptText("Bilkent E-mail");
        VBox content = new VBox(12, new Label("Enter your Bilkent e-mail to receive a verification code"), emailInput);
        content.getStyleClass().add("dialog-body");

        Alert emailDialog = new Alert(Alert.AlertType.NONE, "", ButtonType.CANCEL, ButtonType.OK);
        emailDialog.setTitle("Forgot Password");
        emailDialog.getDialogPane().setHeaderText(null);
        emailDialog.getDialogPane().setContent(content);
        styleDialog(emailDialog.getDialogPane());
        emailDialog.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                String email = emailInput.getText().trim();
                if (email.isBlank()) {
                    showInfo("Forgot Password", "Please enter your Bilkent e-mail.");
                    return;
                }
                if (!isBilkentEmail(email)) {
                    showInfo("Forgot Password", "Please use a Bilkent e-mail address.");
                    return;
                }
                AppState.ActionResult result = appState.sendPasswordResetCode(email);
                if (!result.success()) {
                    showInfo("Forgot Password", result.message());
                    return;
                }
                showResetDialog(email);
            }
        });
    }

    private void showResetDialog(String email) {
        TextField codeField = new TextField();
        codeField.setPromptText("Verification Code");
        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("New Password");
        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm New Password");

        VBox content = new VBox(12,
            new Label("Verification code sent to " + email),
            codeField,
            newPassword,
            confirmPassword
        );
        content.getStyleClass().add("dialog-body");

        Alert resetDialog = new Alert(Alert.AlertType.NONE, "", ButtonType.CANCEL, ButtonType.OK);
        resetDialog.setTitle("Reset Password");
        resetDialog.getDialogPane().setHeaderText(null);
        resetDialog.getDialogPane().setContent(content);
        styleDialog(resetDialog.getDialogPane());
        resetDialog.showAndWait().ifPresent(type -> {
            if (type == ButtonType.OK) {
                if (codeField.getText().isBlank() || newPassword.getText().isBlank() || confirmPassword.getText().isBlank()) {
                    showInfo("Reset Password", "Please fill in all fields.");
                    return;
                }
                if (!newPassword.getText().equals(confirmPassword.getText())) {
                    showInfo("Reset Password", "Confirm password must match the new password.");
                    return;
                }
                AppState.ActionResult result = appState.resetPassword(email, codeField.getText().trim(), newPassword.getText());
                showInfo("Reset Password", result.message());
            }
        });
    }

    private void styleDialog(DialogPane pane) {
        pane.getStylesheets().add(getClass().getResource("/styles/app.css").toExternalForm());
        pane.getStyleClass().add("app-dialog");
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

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.getDialogPane().setHeaderText(null);
        styleDialog(alert.getDialogPane());
        alert.showAndWait();
    }
}
