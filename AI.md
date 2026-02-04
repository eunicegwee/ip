# AI Use Record

## AI Tools Used
- **Gemini 3 Pro (Preview)**: Used for all increments and development tasks.
- **GitHub Copilot (GPT-5 mini)**: Used specifically for increment Level-9 (assisted code suggestions via the Copilot extension).

## Observations

### What Worked
- **Boilerplate Generation**: The AI was extremely effective at generating repetitive code structures, such as getters, setters, and standard interaction patterns in the `command` and `task` packages.
- **Refactoring**: It provided quick and cleaner ways to restructure code blocks, particularly when splitting the monolithic `Kira` class into smaller components (Storage, Ui, Parser).
- **Test Generation**: Generating unit tests for `Parser` and tasks saved significant manual effort, catching edge cases I hadn't initially considered.
- **Context Awareness**: The AI understood the project structure well, correctly identifying where to place new commands and how they should interact with the existing `TaskList` and `Ui`.

### What Didn't Work
- **Complex Logic Handling**: Sometimes, very specific logic for date parsing required a few iterations to get the format exactly right for the `Deadline` and `Event` classes.
- **Context Limits**: Occasionally, if a prompt expected too much implicit knowledge about a file not recently edited, I had to remind it of specific method signatures.

## Impact
- **Time Saved**: Overall, I estimate the AI saved approximately 40-50% of the total development time. The speed-up was most noticeable during the initial setup and when writing test cases.
