# Kira — User Guide

Welcome to Kira, your simple CLI/GUI task-managing chatbot. This user guide explains how to start Kira and how to use all of its important features (adding tasks, listing, finding, filtering, marking/unmarking, deleting, undoing, and exiting). Examples use the exact command syntax Kira accepts.

---

## Quick start

1. Build or run the app:
   - From the project root you can run the app with Gradle (if Gradle wrapper is available):

```bash
# macOS / Linux (zsh)
./gradlew run
```

   - If a packaged JAR is available (after building), you can run it with:

```bash
java -jar build/libs/ip-kira.jar
```

   - The repository also contains convenience scripts under `build/scripts/` when a distribution is created.

2. When Kira starts you'll see a welcome message and a short list of supported commands. You can type commands in the console or use the GUI if you launch the GUI build.

3. Commands are typed in plain text. Press Enter to submit a command.

---

## Command summary

Kira supports the following commands (user-facing syntax):

- `todo <description>`
- `deadline <description> /by <yyyy-MM-dd HH:mm>`
- `event <description> /from <yyyy-MM-dd HH:mm> /to <yyyy-MM-dd HH:mm>`
- `list`
- `mark <task number>`
- `unmark <task number>`
- `delete <task number>`
- `find <keyword>`
- `filter <yyyy-MM-dd>`
- `undo`
- `bye`

Notes:
- Task numbers shown by `list` are 1-based (i.e., the first task is `1`). Internally Kira uses 0-based indexes, but you should always enter numbers starting from 1.
- Date/time format for deadlines and events is `yyyy-MM-dd HH:mm` (for example: `2026-02-20 18:00`).
- `filter` accepts a date only in `yyyy-MM-dd` format (for example: `filter 2026-02-20`).

---

## Detailed command guide and examples

### 1) Adding a ToDo

Action: Add a simple task with no associated date/time.

Syntax:
```
todo <description>
```
Example:
```
todo Read design chapter
```
Expected: Kira confirms the ToDo and shows the updated total number of tasks.


### 2) Adding a Deadline

Action: Add a task that must be done by a specified date and time.

Syntax:
```
deadline <description> /by <yyyy-MM-dd HH:mm>
```
Example:
```
deadline Submit assignment /by 2026-03-01 23:59
```
Notes: If the `/by` part or a correctly formatted datetime is missing, Kira will show an error instructing you to provide the `/by` marker and the `yyyy-MM-dd HH:mm` format.


### 3) Adding an Event

Action: Add a task that occurs between two date/time points.

Syntax:
```
event <description> /from <yyyy-MM-dd HH:mm> /to <yyyy-MM-dd HH:mm>
```
Example:
```
event Team meeting /from 2026-04-01 09:00 /to 2026-04-01 10:30
```
Notes: Both `/from` and `/to` must be provided and datetimes must be valid.


### 4) List all tasks

Syntax:
```
list
```
Behavior: Shows all tasks in the current task list, with their index numbers, type label (ToDo / Deadline / Event), completion state (`[ ]` or `[X]`), and date/time information if present.


### 5) Mark / Unmark tasks

Syntax:
```
mark <task number>
unmark <task number>
```
Example:
```
mark 2
unmark 2
```
Notes: Use the index displayed by `list`. Marking sets the task as done; unmarking clears the done flag.


### 6) Delete a task

Syntax:
```
delete <task number>
```
Example:
```
delete 3
```
Behavior: Removes the task from the list and saves the change.


### 7) Find tasks by keyword

Syntax:
```
find <keyword>
```
Example:
```
find assignment
```
Behavior: Returns tasks whose descriptions contain the keyword (case-insensitive substring match).


### 8) Filter tasks by date

Syntax:
```
filter <yyyy-MM-dd>
```
Example:
```
filter 2026-04-01
```
Behavior: Shows only tasks that occur on (or have a deadline on) the given date. Date must be `yyyy-MM-dd`.


### 9) Undo last change

Syntax:
```
undo
```
Behavior: Reverts the most recent change that modified the task list (for example, adding, deleting, marking). There is a single undo stack. If there is no history to undo, Kira will inform you.


### 10) Exit

Syntax:
```
bye
```
Behavior: Exits the application.

---

## Storage and persistence

- Kira saves tasks to a plain text file so your tasks persist between runs. By default the project contains `data/kira.txt` used for storing tasks.
- When you run Kira it will load tasks from that file (if present) and save changes after modifying the list.

---

## GUI vs Console

- Kira provides both a console mode (run from terminal) and a GUI frontend. The GUI will typically show the same welcome text and supported commands and accept the same command text input.
- If you use the GUI, typed commands are processed the same way as in the console; the UI simply displays responses differently.

---

## Error messages and troubleshooting

- "OOPS! The description of a todo cannot be empty." — you typed `todo` without a description.
- "OOPS! Please add a deadline time (use /by)." — `deadline` without `/by` or time.
- "OOPS! Invalid Date format. Use yyyy-MM-dd HH:mm" — wrong date/time format for deadlines or events.
- "OOPS! Please specify the task number." — `mark`, `unmark`, or `delete` without an index.
- "OOPS! That is not a valid number." — the provided task number was not an integer.
- If `undo` reports "Nothing to undo." there is no earlier modifying action to revert.

If you encounter unexpected behavior, make sure you used the exact command syntax and validated date/time formats.

---

## Tips and examples

- Save time by chaining commands in separate lines (type multiple commands one after another).
- Use `find` to search for keywords before deleting to avoid removing the wrong task.

Example session:

```
> todo Read chapter 5
Added: [T][ ] Read chapter 5
> deadline Project milestone /by 2026-03-15 23:59
Added: [D][ ] Project milestone (by: 2026-03-15 23:59)
> list
1. [T][ ] Read chapter 5
2. [D][ ] Project milestone (by: 2026-03-15 23:59)
> mark 2
Marked as done: Project milestone
> undo
Okay, undone last action.
> bye
Goodbye! Hope to see you soon.
```

---

## Where to get help

- The application shows a short commands block at startup. Use that as a quick reference.

----