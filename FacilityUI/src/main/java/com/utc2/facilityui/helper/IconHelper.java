package com.utc2.facilityui.helper;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.Node;

public class IconHelper {
    public static Node getUserIcon() {
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.USER);
        icon.setStyle("-fx-fill: #333; -fx-font-size: 24px;");
        return icon;
    }

    public static Node getLockIcon() {
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.LOCK);
        icon.setStyle("-fx-fill: #333; -fx-font-size: 24px;");
        return icon;
    }
}
