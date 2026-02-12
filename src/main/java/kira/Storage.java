package kira;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import kira.task.Deadline;
import kira.task.Event;
import kira.task.Task;
import kira.task.TaskList;
import kira.task.ToDo;

/**
 * Handles loading tasks from the file and saving tasks back to the file.
 */
public class Storage {
    private static final String SEP = " \\| ";
    private static final String SAVE_ERROR = "Error saving tasks: ";

    private final String filePath;

    public Storage(String filePath) {
        // Preconditions
        assert filePath != null : "filePath must not be null";
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the storage file.
     * @return An ArrayList of Tasks loaded from the file.
     * @throws KiraException If the file format is corrupted or cannot be read.
     */
    public ArrayList<Task> load() throws KiraException {
        // Precondition
        assert filePath != null : "filePath must not be null";

        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            throw new KiraException("No data file found.");
        }

        try {
            Scanner s = new Scanner(file);
            while (s.hasNext()) {
                String line = s.nextLine();
                String[] parts = line.split(SEP); // Split by " | "

                if (parts.length < 3) {
                    throw new KiraException("Corrupted data line: " + line);
                }

                String type = parts[0];
                boolean isDone = parts[1].equals("1");
                String description = parts[2];

                Task task = null;
                // Use if/else instead of switch to satisfy indentation rules
                if ("T".equals(type)) {
                    task = new ToDo(description);
                } else if ("D".equals(type)) {
                    if (parts.length < 4) {
                        throw new KiraException("Corrupted deadline line: " + line);
                    }
                    task = new Deadline(description, parts[3]);
                } else if ("E".equals(type)) {
                    if (parts.length < 5) {
                        throw new KiraException("Corrupted event line: " + line);
                    }
                    task = new Event(description, parts[3], parts[4]);
                } else {
                    task = null;
                }

                if (task != null) {
                    if (isDone) {
                        task.markAsDone();
                    }
                    tasks.add(task);
                }
            }
        } catch (Exception e) {
            throw new KiraException("Error loading data: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Saves the current list of tasks to the storage file.
     * @param tasks The TaskList containing tasks to save.
     */
    public void save(TaskList tasks) {
        // Preconditions
        assert filePath != null : "filePath must not be null";
        assert tasks != null : "tasks must not be null";

        try {
            File file = new File(filePath);
            File parent = file.getParentFile();
            if (parent != null) {
                boolean dirsCreated = parent.mkdirs();
                if (!dirsCreated && !parent.exists()) {
                    // Directory creation failed or was unnecessary; FileWriter will throw if path is invalid.
                }
            }

            FileWriter fw = new FileWriter(filePath);
            for (Task task : tasks.getAll()) {
                fw.write(task.toFileFormat() + System.lineSeparator());
            }
            fw.close();
        } catch (IOException e) {
            System.out.println(SAVE_ERROR + e.getMessage());
        }
    }
}
