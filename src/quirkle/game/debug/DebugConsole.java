package quirkle.game.debug;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Reads and writes the debug mode's plain-text console on {@code System.in}/{@code System.out}.
 *
 * @note Knows only lines and numbers; the game's own types are read and formatted by
 *       {@link DebugPrompts}.
 * @note Every {@code read} method loops until the input parses, so a caller never sees a bad value.
 */
public final class DebugConsole {

    private static final int SEPARATOR_WIDTH = 45;

    private static final Scanner SCANNER = new Scanner(System.in);

    private DebugConsole() {}

    /** Prints one line. */
    public static void println(String line) {
        System.out.println(line);
    }

    /** Prints an empty line. */
    public static void printBlank() {
        System.out.println();
    }

    /** Prints {@code title} between two separators, preceded by a blank line. */
    public static void printHeading(String title) {
        System.out.println();
        System.out.println("-".repeat(SEPARATOR_WIDTH));
        System.out.println("  " + title);
        System.out.println("-".repeat(SEPARATOR_WIDTH));
    }

    /** Prints a separator line. */
    public static void printSeparator() {
        System.out.println("-".repeat(SEPARATOR_WIDTH));
    }

    /**
     * @return the next line of input, trimmed
     * @throws IllegalStateException if the game was not started from a terminal, so there is no
     *         console to read from
     */
    public static String readLine(String prompt) {
        System.out.print(prompt + " > ");
        try {
            return SCANNER.nextLine().trim();
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("no console input available; start the game from a terminal");
        }
    }

    /** @return the next line parsed as a whole number, re-asking until one is entered. */
    public static int readInt(String prompt) {
        while (true) {
            String input = readLine(prompt);
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                println("  '" + input + "' is not a whole number.");
            }
        }
    }

    /** @return a whole number within {@code min}..{@code max} inclusive, re-asking until it fits. */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt + " (" + min + ".." + max + ")");
            if (value >= min && value <= max) return value;
            println("  " + value + " is outside " + min + ".." + max + ".");
        }
    }

    /**
     * @return the answer to a yes/no question, re-asking until it is one
     * @note Accepts {@code j} alongside {@code y}, since the prompts are read by German testers too.
     */
    public static boolean readYesNo(String prompt) {
        while (true) {
            String input = readLine(prompt + " (y/n)").toLowerCase();
            if (input.equals("y") || input.equals("j")) return true;
            if (input.equals("n")) return false;
            println("  please answer y or n.");
        }
    }
}