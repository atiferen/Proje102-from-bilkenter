package frombilkenter.fx.controller;

import frombilkenter.data.AppState;
import frombilkenter.fx.FxSupport;
import frombilkenter.fx.PageController;
import frombilkenter.model.Listing;
import frombilkenter.model.ListingRequest;
import frombilkenter.model.ListingStatus;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;

public class AdminController implements PageController {
    @FXML private VBox requestContainer;
    @FXML private VBox approvedContainer;
    @FXML private VBox rejectedContainer;
    private AppState appState;
    private ShellController shellController;

    @Override
    public void init(AppState appState, ShellController shellController) {
        this.appState = appState;
        this.shellController = shellController;
        refresh();
    }

    @Override
    public void refresh() {
        // Draw all three admin sections again.
        requestContainer.getChildren().clear();
        approvedContainer.getChildren().clear();
        rejectedContainer.getChildren().clear();
        for (ListingRequest request : appState.getRequests()) {
            requestContainer.getChildren().add(requestCard(request));
        }
        for (Listing listing : appState.getApprovedListingsForAdmin()) {
            approvedContainer.getChildren().add(listingRow(listing, false));
        }
        for (Listing listing : appState.getRejectedListings()) {
            rejectedContainer.getChildren().add(listingRow(listing, true));
        }
    }

    private HBox requestCard(ListingRequest request) {
        HBox row = new HBox(16);
        row.getStyleClass().add("admin-row");
        row.setPadding(new Insets(14));
        row.getChildren().add(FxSupport.imageView(request.getListing().getImagePath(), 70, 70));

        VBox meta = new VBox(6);
        meta.getChildren().add(FxSupport.wrapLabel(request.getListing().getTitle(), "card-title", 220));
        meta.getChildren().add(new Label(appState.getSeller(request.getListing()).getFullName()));
        meta.getChildren().add(new Label(request.getListing().getCategory()));
        meta.getChildren().add(new Label(request.getListing().getPrice() == 0 ? "FREE" : "TL " + request.getListing().getPrice()));
        HBox.setHgrow(meta, Priority.ALWAYS);

        HBox actions = new HBox(8);
        Button approve = new Button("Approve");
        approve.getStyleClass().add("primary-button");
        Button reject = new Button("Reject");
        reject.getStyleClass().add("secondary-button");
        approve.setOnAction(e -> {
            appState.approveRequest(request);
            shellController.refreshAll();
        });
        reject.setOnAction(e -> {
            appState.rejectRequest(request, "Rejected by admin");
            shellController.refreshAll();
        });
        actions.getChildren().addAll(approve, reject);
        row.getChildren().addAll(meta, actions);
        return row;
    }

    private HBox listingRow(Listing listing, boolean rejected) {
        HBox row = new HBox(16);
        row.getStyleClass().addAll("admin-row", "admin-section-row");
        row.setPadding(new Insets(14));
        row.getChildren().add(FxSupport.imageView(listing.getImagePath(), 70, 70));

        VBox meta = new VBox(6);
        meta.getChildren().add(FxSupport.wrapLabel(listing.getTitle(), "card-title", 220));
        meta.getChildren().add(new Label(appState.getSeller(listing).getFullName()));
        meta.getChildren().add(new Label(listing.getCategory()));
        meta.getChildren().add(new Label(listing.getPrice() == 0 ? "FREE" : "TL " + listing.getPrice()));
        if (rejected) {
            Label reason = new Label("Reason: " + findRejectionReason(listing));
            reason.getStyleClass().add("subtle-value");
            meta.getChildren().add(reason);
        } else {
            Label condition = new Label("Condition: " + listing.getCondition());
            condition.getStyleClass().add("subtle-value");
            meta.getChildren().add(condition);
        }
        HBox.setHgrow(meta, Priority.ALWAYS);

        Button delete = new Button("Delete");
        delete.getStyleClass().add("secondary-button");
        delete.setOnAction(event -> {
            appState.deleteListing(listing);
            shellController.refreshAll();
        });
        row.getChildren().addAll(meta, delete);
        return row;
    }

    private String findRejectionReason(Listing listing) {
        for (ListingRequest request : appState.getRequests()) {
            if (request.getListing().getListingId().equals(listing.getListingId())) {
                String reason = request.getReason();
                if (reason != null && !reason.isBlank()) {
                    return reason;
                }
            }
        }
        return "Rejected by admin";
    }
}
