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
 * Controls the main GUI and wires user actions to backend calls.
 *
 * Manages the dialog container UI and message presentation.
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
     * Initializes the controller.
     *
     * Binds the scroll pane to the dialog container height so new messages automatically
     * scroll into view and adjusts dialog wrap widths when the window is resized.
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
     * Injects the KiraResponder instance.
     *
     * Displays the welcome message and initial backend-provided commands list if available.
     *
     * @param k the KiraResponder to use for backend replies.
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
     * Handles the user input event (Send button click or Enter key).
     *
     * Adds the user's message to the dialog container, calls the backend asynchronously,
     * and appends Kira's joined response when available.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isEmpty()) {
            return;
        }

        // Add user dialog immediately on UI thread
        DialogBox userBox = createUserDialog(input);
        dialogContainer.getChildren().addAll(userBox);
        userInput.clear();

        // Run backend processing in background
        Task<CommandResult> task = createBackendTask(input);

        task.setOnSucceeded(evt -> onTaskSucceeded(task.getValue()));

        task.setOnFailed(evt -> onTaskFailed(task.getException()));

        new Thread(task).start();
    }

    /**
     * Creates a DialogBox for the user's message and sets appropriate widths.
     *
     * @param input the user's input text.
     * @return a configured DialogBox instance for the user message.
     */
    private DialogBox createUserDialog(String input) {
        DialogBox userBox = DialogBox.getUserDialog(input, userImage);
        double viewportWidth = scrollPane.getViewportBounds().getWidth();
        double availInit = Math.max(120, viewportWidth - 110);
        double userInitWidth = Math.max(120, Math.min(600, availInit * 0.45));
        userBox.setDialogWrapWidth(userInitWidth);
        userBox.setMaxWidth(viewportWidth);
        userBox.setPrefWidth(viewportWidth);
        return userBox;
    }

    /**
     * Creates a background Task that calls the backend responder.
     *
     * @param input the user input to send to the backend.
     * @return a Task that will execute the backend command.
     */
    private Task<CommandResult> createBackendTask(String input) {
        return new Task<>() {
            @Override
            protected CommandResult call() {
                return (kiraResponder != null) ? kiraResponder.executeCommand(input)
                        : new CommandResult(false, "Kira heard: " + input);
            }
        };
    }

    /**
     * Processes a successful backend result.
     *
     * Combines message lines into a single display string, creates the Kira dialog, and
     * handles application exit if requested by the result.
     *
     * @param result the CommandResult produced by the backend.
     */
    private void onTaskSucceeded(CommandResult result) {
        java.util.List<String> msgs = result.getMessages();
        if (msgs != null && !msgs.isEmpty()) {
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
            kiraBox.setDialogWrapWidth(availResp);
            kiraBox.setMaxWidth(vpw);
            kiraBox.setPrefWidth(vpw);
            dialogContainer.getChildren().addAll(kiraBox);
        }
        if (result.isExit()) {
            Platform.runLater(() -> {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Platform.exit();
            });
        }
    }

    /**
     * Processes a failed backend task and shows an error dialog.
     *
     * @param ex the exception that occurred, may be null.
     */
    private void onTaskFailed(Throwable ex) {
        DialogBox err = DialogBox.getKiraErrorDialog("An error occurred: "
                + (ex != null ? ex.getMessage() : "unknown"), kiraImage);
        double vp = scrollPane.getViewportBounds().getWidth();
        err.setDialogWrapWidth(Math.max(120, vp - 110));
        err.setMaxWidth(vp);
        dialogContainer.getChildren().addAll(err);
    }
}

// End of MainWindow.java
