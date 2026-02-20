package kira.gui;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.StringTokenizer;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a text flow containing styled text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private TextFlow dialogFlow;
    @FXML
    private ImageView displayPicture;
    @FXML
    private Region spacer;
    // a container that groups the bubble and avatar so it can be pinned to the right
    private HBox rightContainer = new HBox();

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        displayPicture.setImage(img);

        // Make sure the ImageView uses the expected style class so CSS sizing rules apply
        displayPicture.getStyleClass().add("image-view");
        displayPicture.setPreserveRatio(true);
        displayPicture.setSmooth(true);

        // Apply a circular clip to make avatar round (if image present) and keep it centered
        if (displayPicture != null) {
            Circle clip = new Circle();
            // Bind to fitWidth/fitHeight so the clip stays centered and scales with the ImageView
            clip.centerXProperty().bind(displayPicture.fitWidthProperty().divide(2.0));
            clip.centerYProperty().bind(displayPicture.fitHeightProperty().divide(2.0));
            clip.radiusProperty().bind(
                    Bindings.min(displayPicture.fitWidthProperty(), displayPicture.fitHeightProperty()).divide(2.0)
            );
            displayPicture.setClip(clip);
        }

        // Prevent TextFlow from showing focus/selection outline
        if (dialogFlow != null) {
            dialogFlow.setFocusTraversable(false);
            // Ensure consistent line spacing for readability
            dialogFlow.setLineSpacing(4.0);
            // Allow the TextFlow to grow horizontally so it fills available width
            HBox.setHgrow(dialogFlow, Priority.ALWAYS);
            dialogFlow.setMaxWidth(Double.MAX_VALUE);
        }
        // Make spacer grow to push avatar to edge
        if (spacer != null) {
            HBox.setHgrow(spacer, Priority.ALWAYS);
            spacer.setMaxWidth(Double.MAX_VALUE);
        }

        // configure right container
        rightContainer.setAlignment(Pos.CENTER_RIGHT);
        rightContainer.setSpacing(8);
        // ensure the dialogFlow and avatar are inside the right container
        // actual ordering will be set in factory methods
        rightContainer.getChildren().clear();

        setTextWithMarkup(text);
    }

    private void setTextWithMarkup(String text) {
        dialogFlow.getChildren().clear();
        if (text == null || text.isEmpty()) {
            return;
        }
        // Very simple parser that supports <b>, <i>, <u>, and <color=#rrggbb>...</color>
        // This is not a full HTML parser; it's a pragmatic lightweight approach for the UI.
        Deque<String> tagStack = new ArrayDeque<>();
        StringTokenizer st = new StringTokenizer(text, "<>", true);
        boolean inTag = false;
        String pending = "";
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            if ("<".equals(tok)) {
                inTag = true;
                // flush pending text
                if (!pending.isEmpty()) {
                    dialogFlow.getChildren().add(makeTextNode(pending, tagStack));
                    pending = "";
                }
                continue;
            }
            if (">".equals(tok)) {
                inTag = false;
                continue;
            }
            if (inTag) {
                String tag = tok.trim();
                boolean isClosing = tag.startsWith("/");
                String bare = isClosing ? tag.substring(1).trim() : tag;
                if (isFormattingTag(bare)) {
                    if (isClosing) {
                        if (!tagStack.isEmpty()) {
                            tagStack.pop();
                        }
                    } else {
                        tagStack.push(bare);
                    }
                } else {
                    // Unknown tag: treat it as literal text (show the angle brackets and content)
                    if (isClosing) {
                        pending += "</" + bare + ">";
                    } else {
                        pending += "<" + bare + ">";
                    }
                }
            } else {
                pending += tok;
            }
        }
        if (!pending.isEmpty()) {
            dialogFlow.getChildren().add(makeTextNode(pending, tagStack));
        }
    }

    /**
     * Returns true if the token corresponds to a supported formatting tag.
     */
    private boolean isFormattingTag(String tag) {
        if (tag == null || tag.isEmpty()) {
            return false;
        }
        String lower = tag.toLowerCase();
        if (lower.equals("b") || lower.equals("i") || lower.equals("u")) {
            return true;
        }
        if (lower.startsWith("color=")) {
            return true;
        }
        return false;
    }

    private Text makeTextNode(String content, Deque<String> tagStack) {
        Text t = new Text(content);
        // default font
        double size = 14.0; // slightly larger for readability
        String family = "Inter"; // preferred nicer font, falls back to system fonts via CSS
        boolean bold = false;
        boolean italic = false;
        boolean underline = false;
        String color = null;
        if (tagStack != null) {
            for (String tag : tagStack) {
                if (tag.equalsIgnoreCase("b")) {
                    bold = true;
                } else if (tag.equalsIgnoreCase("i")) {
                    italic = true;
                } else if (tag.equalsIgnoreCase("u")) {
                    underline = true;
                } else if (tag.toLowerCase().startsWith("color=")) {
                    // expect color=#rrggbb
                    int eq = tag.indexOf('=');
                    if (eq >= 0 && eq + 1 < tag.length()) {
                        color = tag.substring(eq + 1).trim();
                    }
                }
            }
        }
        FontWeight fw = bold ? FontWeight.BOLD : FontWeight.NORMAL;
        FontPosture fp = italic ? FontPosture.ITALIC : FontPosture.REGULAR;
        t.setFont(Font.font(family, fw, fp, size));
        t.setUnderline(underline);
        if (color != null && color.startsWith("#") && color.length() == 7) {
            t.setStyle("-fx-fill: " + color + ";");
        }
        return t;
    }

    private void applyTextColorToFlow(Color c) {
        if (dialogFlow == null) {
            return;
        }
        ObservableList<Node> children = dialogFlow.getChildren();
        for (int i = 0; i < children.size(); i++) {
            Node n = children.get(i);
            if (n instanceof Text) {
                Text t = (Text) n;
                t.setFill(c);
            }
        }
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        // kept for backwards-compat but prefer explicit ordering in factory methods
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Adjusts the dialog flow wrap width to fit available space. Call from controller on resize.
     */
    public void setDialogWrapWidth(double width) {
        if (dialogFlow != null) {
            double wrapForText = Math.max(80, width - 24); // leave some padding inside bubble
            dialogFlow.setPrefWidth(width);
            dialogFlow.setMaxWidth(width);
            dialogFlow.setMinWidth(0);
            // also set wrapping width on each Text child so text wraps inside the TextFlow
            ObservableList<Node> children = dialogFlow.getChildren();
            for (int i = 0; i < children.size(); i++) {
                Node n = children.get(i);
                if (n instanceof Text) {
                    Text tnode = (Text) n;
                    tnode.setWrappingWidth(wrapForText);
                }
            }

            // Force a layout pass on the JavaFX thread so the TextFlow recomputes its height
            Platform.runLater(() -> {
                // Reapply CSS/layout to ensure wrapping changes take effect immediately
                dialogFlow.applyCss();
                dialogFlow.layout();
                // Ensure the parent containers re-layout as well
                if (this.getParent() instanceof javafx.scene.Parent) {
                    javafx.scene.Parent parent = (javafx.scene.Parent) this.getParent();
                    parent.requestLayout();
                }
                this.requestLayout();
            });
        }
    }

    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.getStyleClass().add("dialog-box");
        db.getStyleClass().add("user");
        if (db.dialogFlow != null) {
            db.dialogFlow.getStyleClass().add("dialog-label");
            // user text should contrast with blue-slate background
            db.applyTextColorToFlow(Color.WHITE);
            // enforce background for the flow to avoid outline issues
            String userStyle = "-fx-background-color: #1D6380; "
                    + "-fx-background-radius: 12px 12px 6px 12px; "
                    + "-fx-padding: 8 10 8 10; "
                    + "-fx-border-color: transparent;";
            db.dialogFlow.setStyle(userStyle);
            // For user, don't let the dialogFlow greedily take all horizontal space so
            // the avatar stays to the right; wrap width will be set by controller.
            HBox.setHgrow(db.dialogFlow, Priority.NEVER);
        }
        // allow the DialogBox to expand horizontally in the dialog container
        HBox.setHgrow(db, Priority.ALWAYS);
        db.setMaxWidth(Double.MAX_VALUE);
        // Use spacer + rightContainer so the rightContainer (bubble+avatar) is pinned to the right
        db.getChildren().setAll(db.spacer, db.rightContainer);
        // order inside rightContainer: bubble then avatar
        // Wrap avatar in a VBox so it can be bottom-aligned relative to the dialogFlow
        VBox avatarBoxUser = new VBox(db.displayPicture);
        avatarBoxUser.setAlignment(Pos.BOTTOM_CENTER);
        // bind the avatar box height to the dialogFlow height so avatar sits at bubble bottom
        if (db.dialogFlow != null) {
            avatarBoxUser.prefHeightProperty().bind(db.dialogFlow.heightProperty());
        }
        db.rightContainer.getChildren().setAll(db.dialogFlow, avatarBoxUser);
        // Keep rightContainer alignment consistent
        db.rightContainer.setAlignment(Pos.CENTER_RIGHT);
        return db;
    }

    public static DialogBox getKiraDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.getStyleClass().add("dialog-box");
        db.getStyleClass().add("kira");
        if (db.dialogFlow != null) {
            db.dialogFlow.getStyleClass().add("dialog-label");
            // Kira text should be light for contrast
            db.applyTextColorToFlow(Color.web("#F8F9FB"));
            // enforce background for the flow to avoid outline issues
            String kiraStyle = "-fx-background-color: #0C172E; "
                    + "-fx-background-radius: 12px 12px 12px 6px; "
                    + "-fx-padding: 8 10 8 10; "
                    + "-fx-border-color: transparent;";
            db.dialogFlow.setStyle(kiraStyle);
            // For Kira, allow the dialogFlow to grow so the bubble can expand left-to-right
            HBox.setHgrow(db.dialogFlow, Priority.ALWAYS);
        }
        // allow the DialogBox to expand horizontally in the dialog container
        HBox.setHgrow(db, Priority.ALWAYS);
        db.setMaxWidth(Double.MAX_VALUE);
        // For Kira put the rightContainer first and spacer after it so the group sits at the left
        // Wrap avatar in a VBox so it can be bottom-aligned relative to the dialogFlow
        VBox avatarBoxKira = new VBox(db.displayPicture);
        avatarBoxKira.setAlignment(Pos.BOTTOM_CENTER);
        if (db.dialogFlow != null) {
            avatarBoxKira.prefHeightProperty().bind(db.dialogFlow.heightProperty());
        }
        db.rightContainer.getChildren().setAll(avatarBoxKira, db.dialogFlow);
        // ensure the rightContainer doesn't grow; spacer should take remaining space
        HBox.setHgrow(db.rightContainer, Priority.NEVER);
        db.getChildren().setAll(db.rightContainer, db.spacer);
        db.setAlignment(Pos.TOP_LEFT);
        return db;
    }

    public static DialogBox getKiraErrorDialog(String text, Image img) {
        var db = new DialogBox(text, img);
        db.getStyleClass().add("dialog-box");
        db.getStyleClass().add("kira");
        db.getStyleClass().add("error");
        if (db.dialogFlow != null) {
            db.dialogFlow.getStyleClass().add("dialog-label");
            // Use a dark red rounded background and white text for good contrast
            db.applyTextColorToFlow(Color.WHITE);
            String errStyle = "-fx-background-color: linear-gradient(#8B0000, #6A0000); "
                    + "-fx-border-color: transparent; "
                    + "-fx-border-width: 0px; "
                    + "-fx-background-radius: 12px 12px 12px 6px; "
                    + "-fx-padding: 8 10 8 10;";
            db.dialogFlow.setStyle(errStyle);
        }
        // allow the DialogBox to expand horizontally in the dialog container
        HBox.setHgrow(db, Priority.ALWAYS);
        db.setMaxWidth(Double.MAX_VALUE);
        VBox avatarBoxErr = new VBox(db.displayPicture);
        avatarBoxErr.setAlignment(Pos.BOTTOM_CENTER);
        if (db.dialogFlow != null) {
            avatarBoxErr.prefHeightProperty().bind(db.dialogFlow.heightProperty());
        }
        db.rightContainer.getChildren().setAll(avatarBoxErr, db.dialogFlow);
        HBox.setHgrow(db.rightContainer, Priority.NEVER);
        db.getChildren().setAll(db.rightContainer, db.spacer);
        return db;
    }
}

// End of DialogBox.java
