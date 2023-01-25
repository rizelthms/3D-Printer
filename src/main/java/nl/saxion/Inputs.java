package nl.saxion;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Inputs {
    static Scanner scanner = new Scanner(System.in);

    public static JSONArray readJSON(String filename) {
        JSONArray data = null;
        JSONParser jsonParser = new JSONParser();
        URL resource = Inputs.class.getResource("/" + filename);

        if (resource == null) {
            System.err.println("Warning: Could not find " + filename);
            return data;
        }

        try (FileReader reader = new FileReader(URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8))) {
            data = (JSONArray) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        // ----- TEST PRINT - REMOVE
        System.out.println(data);
        return data;
    }

    /**
     * Wait for and grab the latest string input from stdin.
     *
     * @return The line read by scanner.
     */
    public static String stringInput() {
        String input = null;
        while(input == null || input.length() == 0){
            input = scanner.nextLine();
        }
        return input;
    }
    /**
     * Wait for and grab the latest number input from stdin.
     *
     * @return The number entered.
     */
    public static int numberInput() {
        return scanner.nextInt();
    }
    /**
     * Loop to request a new number from stdin if the number isn't within the
     * specified bounds.
     *
     * @param min The lower bound the input must satisfy.
     * @param max The upper bound the input must satisfy.
     * @return A valid number input.
     */
    public static int numberInput(int min, int max) {
        int input = Inputs.numberInput();
        while (input < min || input > max) {
            input = Inputs.numberInput();
        }
        return input;
    }
}