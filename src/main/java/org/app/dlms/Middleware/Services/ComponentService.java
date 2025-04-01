package org.app.dlms.Middleware.Services;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ComponentService {
    public StackPane createStatCard(String title, String value, String icon) {
        StackPane card = new StackPane();
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 5); -fx-background-radius: 8;");

        VBox content = new VBox(5);
        content.setPadding(new Insets(15));
        content.setAlignment(Pos.CENTER_LEFT);

        Text iconText = new Text(icon);
        iconText.setFont(Font.font("Arial", 24));

        Text valueText = new Text(value);
        valueText.setFont(Font.font("Montserrat", FontWeight.BOLD, 20));
        valueText.setFill(Color.web("#303f9f"));

        Text titleText = new Text(title);
        titleText.setFont(Font.font("Montserrat", FontWeight.NORMAL, 14));
        titleText.setFill(Color.web("#757575"));

        content.getChildren().addAll(iconText, valueText, titleText);
        card.getChildren().add(content);

        return card;
    }
}
