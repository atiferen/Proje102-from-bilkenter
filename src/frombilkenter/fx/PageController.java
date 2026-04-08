package frombilkenter.fx;

import frombilkenter.data.AppState;
import frombilkenter.fx.controller.ShellController;

public interface PageController {
    void init(AppState appState, ShellController shellController);
    void refresh();
}
