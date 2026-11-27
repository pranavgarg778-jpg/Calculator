import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;

/**
 * Java Calculator - Java Swing only.
 * Features: +, -, ×, ÷, %, sign toggle, decimal, clear, backspace,
 * keyboard input, and division-by-zero handling.
 *
 * Compile: javac -d out src/Calculator.java
 * Run:     java -cp out Calculator
 */
public class Calculator extends JFrame {
    private final Color BACKGROUND = new Color(28, 31, 38);
    private final Color DISPLAY = new Color(20, 23, 29);
    private final Color NUMBER = new Color(54, 59, 70);
    private final Color FUNCTION = new Color(78, 85, 98);
    private final Color OPERATOR = new Color(232, 145, 65);
    private final Color EQUALS = new Color(45, 160, 112);
    private final Color TEXT = new Color(247, 248, 250);

    private final JLabel expressionLabel = new JLabel(" ");
    private final JLabel displayLabel = new JLabel("0", SwingConstants.RIGHT);

    private String currentInput = "0";
    private String storedValue = null;
    private String pendingOperator = null;
    private boolean startNewInput = true;
    private boolean errorState = false;

    public Calculator() {
        super("Java Calculator");
        buildUI();
        installKeyboardSupport();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(370, 570);
        setMinimumSize(new Dimension(330, 500));
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("CALCULATOR");
        title.setForeground(new Color(190, 198, 210));
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel subtitle = new JLabel("JAVA SWING");
        subtitle.setForeground(new Color(120, 132, 148));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        header.add(title, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        JPanel displayPanel = new JPanel(new BorderLayout(4, 12));
        displayPanel.setBackground(DISPLAY);
        displayPanel.setBorder(new EmptyBorder(18, 14, 18, 14));

        expressionLabel.setForeground(new Color(150, 160, 175));
        expressionLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        expressionLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        displayLabel.setForeground(TEXT);
        displayLabel.setFont(new Font("SansSerif", Font.BOLD, 38));
        displayLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        displayPanel.add(expressionLabel, BorderLayout.NORTH);
        displayPanel.add(displayLabel, BorderLayout.CENTER);
        root.add(displayPanel, BorderLayout.CENTER);

        JPanel keypad = new JPanel(new GridLayout(5, 4, 9, 9));
        keypad.setOpaque(false);

        addButton(keypad, "AC", FUNCTION, "clear");
        addButton(keypad, "⌫", FUNCTION, "backspace");
        addButton(keypad, "%", FUNCTION, "percent");
        addButton(keypad, "÷", OPERATOR, "operator");

        addButton(keypad, "7", NUMBER, "digit");
        addButton(keypad, "8", NUMBER, "digit");
        addButton(keypad, "9", NUMBER, "digit");
        addButton(keypad, "×", OPERATOR, "operator");

        addButton(keypad, "4", NUMBER, "digit");
        addButton(keypad, "5", NUMBER, "digit");
        addButton(keypad, "6", NUMBER, "digit");
        addButton(keypad, "−", OPERATOR, "operator");

        addButton(keypad, "1", NUMBER, "digit");
        addButton(keypad, "2", NUMBER, "digit");
        addButton(keypad, "3", NUMBER, "digit");
        addButton(keypad, "+", OPERATOR, "operator");

        addButton(keypad, "±", FUNCTION, "sign");
        addButton(keypad, "0", NUMBER, "digit");
        addButton(keypad, ".", NUMBER, "decimal");
        addButton(keypad, "=", EQUALS, "equals");

        root.add(keypad, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void addButton(JPanel panel, String text, Color color, String action) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, text.length() > 1 ? 16 : 21));
        button.setForeground(TEXT);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> handleButton(text, action));
        panel.add(button);
    }

    private void handleButton(String text, String action) {
        switch (action) {
            case "digit": inputDigit(text); break;
            case "decimal": inputDecimal(); break;
            case "operator": chooseOperator(text); break;
            case "equals": calculateResult(); break;
            case "clear": clearAll(); break;
            case "backspace": backspace(); break;
            case "percent": percent(); break;
            case "sign": toggleSign(); break;
        }
    }

    private void inputDigit(String digit) {
        if (errorState) clearAll();
        if (startNewInput) {
            currentInput = digit;
            startNewInput = false;
        } else if (currentInput.equals("0")) {
            currentInput = digit;
        } else if (currentInput.length() < 16) {
            currentInput += digit;
        }
        updateDisplay();
    }

    private void inputDecimal() {
        if (errorState) clearAll();
        if (startNewInput) {
            currentInput = "0.";
            startNewInput = false;
        } else if (!currentInput.contains(".")) {
            currentInput += ".";
        }
        updateDisplay();
    }

    private void chooseOperator(String operator) {
        if (errorState) return;
        if (pendingOperator != null && !startNewInput) {
            calculateResult();
            if (errorState) return;
        }
        storedValue = currentInput;
        pendingOperator = operator;
        startNewInput = true;
        expressionLabel.setText(format(storedValue) + " " + operator);
    }

    private void calculateResult() {
        if (pendingOperator == null || storedValue == null || errorState) return;
        try {
            BigDecimal left = new BigDecimal(storedValue);
            BigDecimal right = new BigDecimal(currentInput);
            BigDecimal result;

            switch (pendingOperator) {
                case "+": result = left.add(right); break;
                case "−": result = left.subtract(right); break;
                case "×": result = left.multiply(right); break;
                case "÷":
                    if (right.compareTo(BigDecimal.ZERO) == 0) {
                        showError("Cannot divide by zero");
                        return;
                    }
                    result = left.divide(right, MathContext.DECIMAL128);
                    break;
                default: return;
            }

            expressionLabel.setText(format(storedValue) + " " + pendingOperator
                    + " " + format(currentInput) + " =");
            currentInput = normalize(result);
            storedValue = null;
            pendingOperator = null;
            startNewInput = true;
            updateDisplay();
        } catch (NumberFormatException ex) {
            showError("Invalid number");
        }
    }

    private void percent() {
        if (errorState) return;
        try {
            BigDecimal value = new BigDecimal(currentInput)
                    .divide(new BigDecimal("100"), MathContext.DECIMAL128);
            currentInput = normalize(value);
            startNewInput = true;
            updateDisplay();
        } catch (NumberFormatException ignored) { }
    }

    private void toggleSign() {
        if (errorState || currentInput.equals("0")) return;
        currentInput = currentInput.startsWith("-")
                ? currentInput.substring(1) : "-" + currentInput;
        updateDisplay();
    }

    private void backspace() {
        if (errorState) {
            clearAll();
            return;
        }
        if (startNewInput) return;
        if (currentInput.length() <= 1
                || (currentInput.length() == 2 && currentInput.startsWith("-"))) {
            currentInput = "0";
            startNewInput = true;
        } else {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        }
        updateDisplay();
    }

    private void clearAll() {
        currentInput = "0";
        storedValue = null;
        pendingOperator = null;
        startNewInput = true;
        errorState = false;
        expressionLabel.setText(" ");
        updateDisplay();
    }

    private void showError(String message) {
        currentInput = message;
        displayLabel.setText(message);
        displayLabel.setFont(new Font("SansSerif", Font.BOLD, 21));
        storedValue = null;
        pendingOperator = null;
        startNewInput = true;
        errorState = true;
    }

    private void updateDisplay() {
        displayLabel.setFont(new Font("SansSerif", Font.BOLD, 38));
        displayLabel.setText(format(currentInput));
    }

    private String normalize(BigDecimal value) {
        BigDecimal stripped = value.stripTrailingZeros();
        if (stripped.scale() < 0) stripped = stripped.setScale(0);
        return stripped.toPlainString();
    }

    private String format(String value) {
        if (value == null) return "0";
        if (value.length() > 18) return value;
        try {
            BigDecimal number = new BigDecimal(value);
            DecimalFormat df = new DecimalFormat("#,##0.################");
            return df.format(number);
        } catch (NumberFormatException ex) {
            return value;
        }
    }

    private void installKeyboardSupport() {
        JRootPane pane = getRootPane();
        InputMap input = pane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actions = pane.getActionMap();

        bind(input, actions, "0", "0"); bind(input, actions, "1", "1");
        bind(input, actions, "2", "2"); bind(input, actions, "3", "3");
        bind(input, actions, "4", "4"); bind(input, actions, "5", "5");
        bind(input, actions, "6", "6"); bind(input, actions, "7", "7");
        bind(input, actions, "8", "8"); bind(input, actions, "9", "9");
        bind(input, actions, ".", "decimal");
        bind(input, actions, "+", "+"); bind(input, actions, "-", "−");
        bind(input, actions, "*", "×"); bind(input, actions, "/", "÷");
        bind(input, actions, "ENTER", "equals"); bind(input, actions, "=", "equals");
        bind(input, actions, "BACK_SPACE", "backspace");
        bind(input, actions, "ESCAPE", "clear");
        bind(input, actions, "%", "percent");
    }

    private void bind(InputMap input, ActionMap actions, String key, String command) {
        String actionKey = "key_" + key;
        input.put(KeyStroke.getKeyStroke(key), actionKey);
        actions.put(actionKey, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                switch (command) {
                    case "decimal": inputDecimal(); break;
                    case "equals": calculateResult(); break;
                    case "backspace": backspace(); break;
                    case "clear": clearAll(); break;
                    case "percent": percent(); break;
                    case "+": case "−": case "×": case "÷": chooseOperator(command); break;
                    default: inputDigit(command); break;
                }
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new Calculator().setVisible(true));
    }
}
