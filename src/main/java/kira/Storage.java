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
    private String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads tasks from the storage file.
     * @return An ArrayList of Tasks loaded from the file.
     * @throws KiraException If the file format is corrupted or cannot be read.
     */
    public ArrayList<Task> load() throws KiraException {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            throw new KiraException("No data file found.");
        }

        try {
            Scanner s = new Scanner(file);
            while (s.hasNext()) {
                String line = s.nextLine();
                String[] parts = line.split(" \\| "); // Split by " | "

                String type = parts[0];
                boolean isDone = parts[1].equals("1");
                String description = parts[2];

                Task task = switch (type) {
                    case "T" -> new ToDo(description);
                    case "D" -> new Deadline(description, parts[3]);
                    case "E" -> new Event(description, parts[3], parts[4]);
                    default -> null;
                };

                if (task != null) {
                    if (isDone) task.markAsDone();
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
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();

            FileWriter fw = new FileWriter(filePath);
            for (Task task : tasks.getAll()) {
                fw.write(task.toFileFormat() + System.lineSeparator());
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }
}
