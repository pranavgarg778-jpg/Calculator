# Java Calculator

A standalone calculator desktop application built with Java Swing. All UI and calculation logic are Java; no database, web technologies, or third-party libraries are used.

## Features
- Addition, subtraction, multiplication and division
- Percentage calculation
- Positive / negative sign toggle
- Decimal numbers
- Clear all (AC) and backspace
- Chained operations
- Keyboard support (numbers, +, -, *, /, Enter, Backspace, Esc, %)
- Division-by-zero error handling
- Dark, simple calculator interface

## Requirements
- JDK 8 or newer
- No external dependencies

## Run from terminal

From the project folder:

```bash
javac -d out src/Calculator.java
java -cp out Calculator
```

## Run in an IDE
Open the folder in IntelliJ IDEA, Eclipse or NetBeans, then run `src/Calculator.java`.

## Notes
This is a basic calculator project. It does not store calculation history or use a database.
