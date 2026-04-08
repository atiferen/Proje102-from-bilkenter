package frombilkenter.fx;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import java.io.File;

public class FxSupport {
    private FxSupport() {
    }

    public static Image imageFromPath(String path) {
        File file = new File(path);
        if (file.exists()) {
            return new Image(file.toURI().toString(), true);
        }
        return new Image(FxSupport.class.getResource("/images/logo.png").toExternalForm(), true);
    }

    public static ImageView imageView(String path, double width, double height) {
        ImageView view = new ImageView(imageFromPath(path));
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(false);
        view.setSmooth(true);
        return view;
    }

    public static Label wrapLabel(String text, String styleClass, double maxWidth) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setWrapText(true);
        label.setMaxWidth(maxWidth);
        label.setPadding(new Insets(2));
        return label;
    }

    public static Region spacer(double height) {
        Region region = new Region();
        region.setMinHeight(height);
        region.setPrefHeight(height);
        return region;
    }
}
