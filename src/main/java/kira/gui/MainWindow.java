package kira.gui;

import java.util.StringJoiner;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import kira.CommandResult;
import kira.ResponseUi;

/**
 * Controller for the main GUI. Responsible for wiring user actions to backend calls and
 * managing the dialog container UI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private KiraResponder kiraResponder;

    private Image userImage = loadImageSafe("/images/DaUser.png");
    private Image kiraImage = loadImageSafe("/images/DaKira.png");

    private static Image loadImageSafe(String path) {
        try {
            var is = MainWindow.class.getResourceAsStream(path);
            if (is != null) {
                return new Image(is);
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes and return null
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Initializes the controller. Binds the scroll pane to the dialog container height so it
     * automatically scrolls to the bottom when new messages are added and adjusts dialog wrap
     * widths when the window is resized.
     */
    @FXML
    public void initialize() {
        // Make the scroll pane fit content to its width so children can fill the viewport
        scrollPane.setFitToWidth(true);
        // Bind the scroll pane to the dialog container height so it scrolls to the bottom
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        // Ensure VBox children fill available width
        dialogContainer.setFillWidth(true);

        // Adjust dialog wrap widths when scroll pane viewport size changes
        scrollPane.viewportBoundsProperty().addListener((obs, oldV, newV) -> {
            // Make dialog container match the viewport width so content can fill to the edges
            dialogContainer.setPrefWidth(newV.getWidth());
            dialogContainer.setMaxWidth(newV.getWidth());
            double avail = newV.getWidth() - 110; // conservative margin for avatar/padding
            if (avail < 120) {
                avail = 120;
            }
            for (var node : dialogContainer.getChildren()) {
                if (node instanceof DialogBox) {
                    DialogBox db = (DialogBox) node;
                    // allow the dialog box to expand horizontally and occupy full width
                    double vw = newV.getWidth();
                    db.setMaxWidth(vw);
                    db.setPrefWidth(vw);
                    // user bubbles should remain compact and right-aligned; don't expand to full width
                    if (db.getStyleClass().contains("user")) {
                        double userWidth = Math.max(120, Math.min(600, avail * 0.45));
                        db.setDialogWrapWidth(userWidth);
                    } else {
                        // Kira messages can use the full available width
                        db.setDialogWrapWidth(avail);
                    }
                }
            }
        });
    }

    /**
     * Injects the KiraResponder instance and displays the welcome message along with a
     * commands list. This method is called by the application after FXML loading to provide
     * a backend-adapter for the controller to use.
     *
     * @param k the KiraResponder to use for backend replies
     */
    public void setKiraResponder(KiraResponder k) {
        this.kiraResponder = k;
        // show welcome from backend if available
        if (kiraResponder != null) {
            // Run a task to get welcome output without blocking UI
            Task<Void> t = new Task<>() {
                @Override
                protected Void call() {
                    CommandResult res = kiraResponder.handleWelcome();
                    java.util.List<String> messages = res.getMessages();
                    if (messages != null && !messages.isEmpty()) {
                        Platform.runLater(() -> {
                            for (String msg : messages) {
                                boolean isError = false;
                                String display = msg;
                                if (msg != null && msg.startsWith(ResponseUi.ERROR_PREFIX)) {
                                    isError = true;
                                    display = msg.substring(ResponseUi.ERROR_PREFIX.length());
                                }
                                if (isError) {
                                    dialogContainer.getChildren().add(DialogBox.getKiraErrorDialog(display, kiraImage));
                                } else {
                                    dialogContainer.getChildren().add(DialogBox.getKiraDialog(display, kiraImage));
                                }
                            }
                        });
                    }
                    return null;
                }
            };
            new Thread(t).start();
        }
    }

    /**
     * Handles the user input event (Send button click or Enter key). It appends the user's
     * message to the dialog container immediately, calls the backend asynchronously, and
     * appends Kira's single joined response when available.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isEmpty()) {
            return;
        }

        // Add user dialog immediately on UI thread
        DialogBox userBox = DialogBox.getUserDialog(input, userImage);
        // Set wrap width for newly created user box based on viewport width (compact)
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double availInit = Math.max(120, viewportWidth - 110);
        double userInitWidth = Math.max(120, Math.min(600, availInit * 0.45));
        userBox.setDialogWrapWidth(userInitWidth);
        userBox.setMaxWidth(viewportWidth);
        userBox.setPrefWidth(viewportWidth);
        dialogContainer.getChildren().addAll(userBox);
        userInput.clear();

        // Run backend processing in background
        Task<CommandResult> task = new Task<>() {
            @Override
            protected CommandResult call() {
                return (kiraResponder != null) ? kiraResponder.executeCommand(input)
                        : new CommandResult(false, "Kira heard: " + input);
            }
        };

        task.setOnSucceeded(evt -> {
            CommandResult result = task.getValue();
            java.util.List<String> msgs = result.getMessages();
            if (msgs != null && !msgs.isEmpty()) {
                // Combine all returned lines into a single bubble for standard responses
                StringJoiner joiner = new StringJoiner(System.lineSeparator());
                boolean anyError = false;
                for (String line : msgs) {
                    if (line != null && line.startsWith(ResponseUi.ERROR_PREFIX)) {
                        anyError = true;
                        joiner.add(line.substring(ResponseUi.ERROR_PREFIX.length()));
                    } else {
                        joiner.add(line);
                    }
                }
                String combined = joiner.toString();
                DialogBox kiraBox;
                if (anyError) {
                    kiraBox = DialogBox.getKiraErrorDialog(combined, kiraImage);
                } else {
                    kiraBox = DialogBox.getKiraDialog(combined, kiraImage);
                }
                double vpw = scrollPane.getViewportBounds().getWidth();
                double availResp = Math.max(120, vpw - 110);
                // Kira responses should take advantage of available width
                kiraBox.setDialogWrapWidth(availResp);
                kiraBox.setMaxWidth(vpw);
                kiraBox.setPrefWidth(vpw);
                dialogContainer.getChildren().addAll(kiraBox);
            }
            if (result.isExit()) {
                // close the application after a short delay to allow user to see message
                Platform.runLater(() -> {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    Platform.exit();
                });
            }
        });

        task.setOnFailed(evt -> {
            Throwable ex = task.getException();
            DialogBox err = DialogBox.getKiraErrorDialog("An error occurred: "
                    + (ex != null ? ex.getMessage() : "unknown"), kiraImage);
            double vp = scrollPane.getViewportBounds().getWidth();
            err.setDialogWrapWidth(Math.max(120, vp - 110));
            err.setMaxWidth(vp);
            dialogContainer.getChildren().addAll(err);
        });

        new Thread(task).start();
    }
}

// End of MainWindow.java
