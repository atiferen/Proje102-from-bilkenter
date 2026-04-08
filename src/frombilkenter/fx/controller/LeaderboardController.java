package frombilkenter.fx.controller;

import frombilkenter.data.AppState;
import frombilkenter.fx.PageController;
import frombilkenter.model.User;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardController implements PageController {
    public static class LeaderboardRow {
        private final int rank;
        private final String name;
        private final String department;
        private final int sales;
        private final String status;

        public LeaderboardRow(int rank, String name, String department, int sales, String status) {
            this.rank = rank;
            this.name = name;
            this.department = department;
            this.sales = sales;
            this.status = status;
        }

        public int getRank() { return rank; }
        public String getName() { return name; }
        public String getDepartment() { return department; }
        public int getSales() { return sales; }
        public String getStatus() { return status; }
    }

    @FXML private TableView<LeaderboardRow> leaderboardTable;
    @FXML private TableColumn<LeaderboardRow, Number> rankColumn;
    @FXML private TableColumn<LeaderboardRow, String> nameColumn;
    @FXML private TableColumn<LeaderboardRow, String> departmentColumn;
    @FXML private TableColumn<LeaderboardRow, Number> salesColumn;
    @FXML private TableColumn<LeaderboardRow, String> statusColumn;

    private AppState appState;

    @Override
    public void init(AppState appState, ShellController shellController) {
        this.appState = appState;
        rankColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getRank()));
        nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        departmentColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDepartment()));
        salesColumn.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getSales()));
        statusColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus()));
        refresh();
    }

    @Override
    public void refresh() {
        List<User> users = appState.getLeaderboardUsers();
        List<LeaderboardRow> rows = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            rows.add(new LeaderboardRow(
                i + 1,
                user.getFullName(),
                user.getDepartment(),
                user.getCompletedSales(),
                user.isPremium() ? "Premium" : "—"
            ));
        }
        leaderboardTable.setItems(FXCollections.observableArrayList(rows));
    }
}
