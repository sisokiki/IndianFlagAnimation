import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class creates a window that first displays its own source code line-by-line with a scrolling effect,
 * then shows an animated Indian flag being drawn and subsequently waving.
 */
public class WavingIndianFlag extends JFrame {

    public WavingIndianFlag() {
        setTitle("Waving Indian Flag");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        add(new FlagPanel());
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WavingIndianFlag());
    }
}

/**
 * FlagPanel manages the different animation phases: showing the code, drawing the flag, and waving the flag.
 */
class FlagPanel extends JPanel implements ActionListener {

    // Enum to manage the animation state
    private enum State {
        SHOWING_CODE,
        DRAWING_FLAG,
        WAVING_FLAG
    }

    private State currentState = State.SHOWING_CODE;

    // --- Constants for the flag ---
    private static final int FLAG_WIDTH = 600;
    private static final int FLAG_HEIGHT = 400;
    private static final Color SAFFRON = new Color(255, 153, 51);
    private static final Color WHITE = Color.WHITE;
    private static final Color GREEN = new Color(19, 136, 8);
    private static final Color NAVY_BLUE = new Color(0, 0, 128);

    // --- Animation variables ---
    private Timer timer;
    // For code display
    private int currentLine = 0;
    private int codeDisplayPause = 0;
    private final String[] codeLines;
    // For flag drawing
    private int drawingProgress = 0;
    private final int totalPoints;
    // For flag waving
    private double wavePhase = 0;
    private final double waveAmplitude = 15.0;
    private final double waveFrequency = 0.02;

    public FlagPanel() {
        setBackground(Color.BLACK); // Start with a black background for code
        totalPoints = FLAG_WIDTH;

        // The source code is stored in a string to be displayed
        final String SOURCE_CODE = getSourceCode();
        codeLines = SOURCE_CODE.split("\n");

        timer = new Timer(25, this); // Timer fires every 25ms
        timer.start();
    }

    /**
     * The main drawing method, which delegates to other methods based on the current state.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        switch (currentState) {
            case SHOWING_CODE:
                drawSourceCode(g2d);
                break;
            case DRAWING_FLAG:
            case WAVING_FLAG:
                // Set the sky-blue background for the flag animation
                setBackground(new Color(0, 0, 0));
                int startX = (getWidth() - FLAG_WIDTH) / 2;
                int startY = (getHeight() - FLAG_HEIGHT) / 2;
                int stripeHeight = FLAG_HEIGHT / 3;

                if (currentState == State.DRAWING_FLAG) {
                    drawStripesProgressively(g2d, startX, startY, stripeHeight);
                } else {
                    drawWavingFlag(g2d, startX, startY, stripeHeight);
                }
                break;
        }
    }

    /**
     * Draws the source code on the panel, line by line, with a scrolling effect.
     */
    private void drawSourceCode(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));

        int lineHeight = 15;
        int topMargin = 20;
        int maxVisibleLines = (getHeight() - topMargin * 2) / lineHeight;

        int startLine = 0;
        // If the number of revealed lines exceeds what can be shown, start scrolling
        if (currentLine > maxVisibleLines) {
            startLine = currentLine - maxVisibleLines;
        }

        // Draw only the visible lines
        for (int i = startLine; i < currentLine; i++) {
            // Calculate the y position relative to the visible area
            int yPos = topMargin + (i - startLine) * lineHeight;
            g2d.drawString(codeLines[i], 20, yPos);
        }
    }

    /**
     * Draws the flag stripes progressively.
     */
    private void drawStripesProgressively(Graphics2D g2d, int startX, int startY, int stripeHeight) {
        for (int x = 0; x < drawingProgress; x++) {
            g2d.setColor(SAFFRON);
            g2d.drawLine(startX + x, startY, startX + x, startY + stripeHeight);
            g2d.setColor(WHITE);
            g2d.drawLine(startX + x, startY + stripeHeight, startX + x, startY + 2 * stripeHeight);
            g2d.setColor(GREEN);
            g2d.drawLine(startX + x, startY + 2 * stripeHeight, startX + x, startY + 3 * stripeHeight);
        }
        if (drawingProgress >= totalPoints) {
            drawAshokaChakra(g2d, startX + FLAG_WIDTH / 2, startY + FLAG_HEIGHT / 2);
        }
    }

    /**
     * Draws the complete flag with a waving effect.
     */
    private void drawWavingFlag(Graphics2D g2d, int startX, int startY, int stripeHeight) {
        for (int x = 0; x < FLAG_WIDTH; x++) {
            int yOffset = (int) (waveAmplitude * Math.sin(waveFrequency * (x + wavePhase)));
            g2d.setColor(SAFFRON);
            g2d.drawLine(startX + x, startY + yOffset, startX + x, startY + stripeHeight + yOffset);
            g2d.setColor(WHITE);
            g2d.drawLine(startX + x, startY + stripeHeight + yOffset, startX + x, startY + 2 * stripeHeight + yOffset);
            g2d.setColor(GREEN);
            g2d.drawLine(startX + x, startY + 2 * stripeHeight + yOffset, startX + x, startY + 3 * stripeHeight + yOffset);
        }
        int chakraCenterYOffset = (int) (waveAmplitude * Math.sin(waveFrequency * (FLAG_WIDTH / 2 + wavePhase)));
        drawAshokaChakra(g2d, startX + FLAG_WIDTH / 2, startY + FLAG_HEIGHT / 2 + chakraCenterYOffset);
    }

    /**
     * Draws the Ashoka Chakra.
     */
    private void drawAshokaChakra(Graphics2D g2d, int centerX, int centerY) {
        g2d.setColor(NAVY_BLUE);
        int radius = FLAG_HEIGHT / 3 / 2 - 5;
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);
        for (int i = 0; i < 24; i++) {
            double angle = Math.toRadians(i * 15);
            g2d.drawLine(centerX, centerY, (int) (centerX + Math.cos(angle) * radius), (int) (centerY + Math.sin(angle) * radius));
        }
        int innerRadius = 5;
        g2d.fillOval(centerX - innerRadius, centerY - innerRadius, 2 * innerRadius, 2 * innerRadius);
    }

    /**
     * The main animation loop, called by the Timer. It manages state transitions.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (currentState) {
            case SHOWING_CODE:
                if (currentLine < codeLines.length) {
                    currentLine++; // Reveal one new line per frame
                } else {
                    codeDisplayPause++;
                    if (codeDisplayPause > 60) { // Pause for a moment after code is shown
                        currentState = State.DRAWING_FLAG;
                    }
                }
                break;
            case DRAWING_FLAG:
                if (drawingProgress < totalPoints) {
                    drawingProgress += 10;
                } else {
                    currentState = State.WAVING_FLAG;
                }
                break;
            case WAVING_FLAG:
                wavePhase += 5;
                break;
        }
        repaint();
    }

    /**
     * Helper method to hold the source code as a string.
     * @return The source code of the application.
     */
    private String getSourceCode() {
        // This string contains the full source code of the application to be displayed.
        return "import javax.swing.*;\n" +
                "import java.awt.*;\n" +
                "import java.awt.event.ActionEvent;\n" +
                "import java.awt.event.ActionListener;\n" +
                "\n" +
                "public class WavingIndianFlag extends JFrame {\n" +
                "\n" +
                "    public WavingIndianFlag() {\n" +
                "        setTitle(\"Waving Indian Flag\");\n" +
                "        setSize(800, 600);\n" +
                "        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);\n" +
                "        setLocationRelativeTo(null);\n" +
                "        add(new FlagPanel());\n" +
                "        setVisible(true);\n" +
                "    }\n" +
                "\n" +
                "    public static void main(String[] args) {\n" +
                "        SwingUtilities.invokeLater(() -> new WavingIndianFlag());\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "class FlagPanel extends JPanel implements ActionListener {\n" +
                "\n" +
                "    private enum State {\n" +
                "        SHOWING_CODE,\n" +
                "        DRAWING_FLAG,\n" +
                "        WAVING_FLAG\n" +
                "    }\n" +
                "\n" +
                "    private State currentState = State.SHOWING_CODE;\n" +
                "\n" +
                "    private static final int FLAG_WIDTH = 600;\n" +
                "    private static final int FLAG_HEIGHT = 400;\n" +
                "    private static final Color SAFFRON = new Color(255, 153, 51);\n" +
                "    private static final Color WHITE = Color.WHITE;\n" +
                "    private static final Color GREEN = new Color(19, 136, 8);\n" +
                "    private static final Color NAVY_BLUE = new Color(0, 0, 128);\n" +
                "\n" +
                "    private Timer timer;\n" +
                "    private int currentLine = 0;\n" +
                "    private int codeDisplayPause = 0;\n" +
                "    private final String[] codeLines;\n" +
                "    private int drawingProgress = 0;\n" +
                "    private final int totalPoints;\n" +
                "    private double wavePhase = 0;\n" +
                "    private final double waveAmplitude = 15.0;\n" +
                "    private final double waveFrequency = 0.02;\n" +
                "\n" +
                "    public FlagPanel() {\n" +
                "        setBackground(Color.BLACK);\n" +
                "        totalPoints = FLAG_WIDTH;\n" +
                "        final String SOURCE_CODE = getSourceCode();\n" +
                "        codeLines = SOURCE_CODE.split(\"\\n\");\n" +
                "        timer = new Timer(25, this);\n" +
                "        timer.start();\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    protected void paintComponent(Graphics g) {\n" +
                "        super.paintComponent(g);\n" +
                "        Graphics2D g2d = (Graphics2D) g;\n" +
                "        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);\n" +
                "\n" +
                "        switch (currentState) {\n" +
                "            case SHOWING_CODE:\n" +
                "                drawSourceCode(g2d);\n" +
                "                break;\n" +
                "            case DRAWING_FLAG:\n" +
                "            case WAVING_FLAG:\n" +
                "                setBackground(new Color(200, 220, 255));\n" +
                "                int startX = (getWidth() - FLAG_WIDTH) / 2;\n" +
                "                int startY = (getHeight() - FLAG_HEIGHT) / 2;\n" +
                "                int stripeHeight = FLAG_HEIGHT / 3;\n" +
                "                if (currentState == State.DRAWING_FLAG) {\n" +
                "                    drawStripesProgressively(g2d, startX, startY, stripeHeight);\n" +
                "                } else {\n" +
                "                    drawWavingFlag(g2d, startX, startY, stripeHeight);\n" +
                "                }\n" +
                "                break;\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private void drawSourceCode(Graphics2D g2d) {\n" +
                "        g2d.setColor(Color.GREEN);\n" +
                "        g2d.setFont(new Font(\"Monospaced\", Font.PLAIN, 12));\n" +
                "        int lineHeight = 15;\n" +
                "        int topMargin = 20;\n" +
                "        int maxVisibleLines = (getHeight() - topMargin * 2) / lineHeight;\n" +
                "        int startLine = 0;\n" +
                "        if (currentLine > maxVisibleLines) {\n" +
                "            startLine = currentLine - maxVisibleLines;\n" +
                "        }\n" +
                "        for (int i = startLine; i < currentLine; i++) {\n" +
                "            int yPos = topMargin + (i - startLine) * lineHeight;\n" +
                "            g2d.drawString(codeLines[i], 20, yPos);\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    private void drawStripesProgressively(Graphics2D g2d, int startX, int startY, int stripeHeight) {\n" +
                "    }\n" +
                "\n" +
                "    private void drawWavingFlag(Graphics2D g2d, int startX, int startY, int stripeHeight) {\n" +
                "    }\n" +
                "\n" +
                "    private void drawAshokaChakra(Graphics2D g2d, int centerX, int centerY) {\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public void actionPerformed(ActionEvent e) {\n" +
                "    }\n" +
                "}";
    }
}
