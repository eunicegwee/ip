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
     * automatically scrolls to the bottom when new messages are added.
     */
    @FXML
    public void initialize() {
        // Bind the scroll pane to the dialog container height so it scrolls to the bottom
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
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
                                dialogContainer.getChildren().add(DialogBox.getKiraDialog(msg, kiraImage));
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
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input, userImage));
        userInput.clear();

        // Run backend processing in background
        Task<CommandResult> task = new Task<>() {
            @Override
            protected CommandResult call() {
                return (kiraResponder != null) ? kiraResponder.executeCommand(input)
                        : new CommandResult(java.util.List.of("Kira heard: " + input), false);
            }
        };

        task.setOnSucceeded(evt -> {
            CommandResult result = task.getValue();
            java.util.List<String> msgs = result.getMessages();
            if (msgs != null && !msgs.isEmpty()) {
                // Combine all returned lines into a single bubble for standard responses
                StringJoiner joiner = new StringJoiner(System.lineSeparator());
                for (String line : msgs) {
                    joiner.add(line);
                }
                dialogContainer.getChildren().addAll(DialogBox.getKiraDialog(joiner.toString(), kiraImage));
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
            dialogContainer.getChildren().addAll(DialogBox.getKiraDialog("An error occurred: "
                    + (ex != null ? ex.getMessage() : "unknown"), kiraImage));
        });

        new Thread(task).start();
    }
}

// End of MainWindow.java
