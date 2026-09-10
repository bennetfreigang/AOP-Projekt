package quirkle.game.debug;

import java.util.NoSuchElementException;
import java.util.Scanner;

public final class DebugConsole {

    private static final int SEPARATOR_WIDTH = 45;

    private static final Scanner SCANNER = new Scanner(System.in);

    private DebugConsole() {}

    public static void println(String line) {
        System.out.println(line);
    }

    public static void printBlank() {
        System.out.println();
    }

    public static void printHeading(String title) {
        System.out.println();
        System.out.println("-".repeat(SEPARATOR_WIDTH));
        System.out.println("  " + title);
        System.out.println("-".repeat(SEPARATOR_WIDTH));
    }

    public static void printSeparator() {
        System.out.println("-".repeat(SEPARATOR_WIDTH));
    }

    public static String readLine(String prompt) {
        System.out.print(prompt + " > ");
        try {
            return SCANNER.nextLine().trim();
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("no console input available; start the game from a terminal");
        }
    }

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

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt + " (" + min + ".." + max + ")");
            if (value >= min && value <= max) return value;
            println("  " + value + " is outside " + min + ".." + max + ".");
        }
    }

    public static boolean readYesNo(String prompt) {
        while (true) {
            String input = readLine(prompt + " (y/n)").toLowerCase();
            if (input.equals("y") || input.equals("j")) return true;
            if (input.equals("n")) return false;
            println("  please answer y or n.");
        }
    }

    public static <E extends Enum<E>> E readEnum(String prompt, E[] values) {
        println(prompt + ":");
        for (int i = 0; i < values.length; i++) {
            println("  [" + i + "] " + values[i].name());
        }
        return values[readInt("choice", 0, values.length - 1)];
    }
}