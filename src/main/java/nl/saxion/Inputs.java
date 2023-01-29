package nl.saxion;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import nl.saxion.Models.*;

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

        return data;
    }

    public static List<String[]> readCSV(String filename) {
        List<String[]> data = new ArrayList<String[]>();
        String line = "";
        URL resource = Inputs.class.getResource("/" + filename);

        if (resource == null) {
            System.err.println("Warning: Could not find " + filename);
            return data;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(URLDecoder.decode(resource.getPath(), StandardCharsets.UTF_8)))) {
            int count = 0;

            while ((line = br.readLine()) != null) {
                if (count == 0) {
                    count += 1;
                    continue; // Skip header
                }

                data.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static ArrayList<Print> loadPrintsFromFile(String filename) {
        ArrayList<Print> printObjs = new ArrayList<Print>();

        if(filename.length() == 0) {
            filename = "prints.json";
        }
        JSONArray prints = Inputs.readJSON(filename);

        for (Object p : prints) {
            JSONObject print = (JSONObject) p;
            String name = (String) print.get("name");
            int height = ((Long) print.get("height")).intValue();
            int width = ((Long) print.get("width")).intValue();
            int length = ((Long) print.get("length")).intValue();
            // int filamentLength = ((Long) print.get("filamentLength")).intValue();
            JSONArray fLength = (JSONArray) print.get("filamentLength");
            int printTime = ((Long) print.get("printTime")).intValue();
            ArrayList<Double> filamentLength = new ArrayList();

            for(int i = 0; i < fLength.size(); i++) {
                filamentLength.add(((Double) fLength.get(i)));
            }

            printObjs.add(
                    new Print(name, height, width, length, filamentLength, printTime)
            );
        }

        return printObjs;
    }

    public static ArrayList<Printer> loadPrintersFromFile(String filename) {
        ArrayList<Printer> printerObjs = new ArrayList<Printer>();

        if(filename.length() == 0) {
            filename = "printers.json";
        }
        JSONArray printers = Inputs.readJSON(filename);

        for (Object p : printers) {
            JSONObject printer = (JSONObject) p;
            int id = ((Long) printer.get("id")).intValue();
            int type = ((Long) printer.get("type")).intValue();
            String name = (String) printer.get("name");
            String manufacturer = (String) printer.get("manufacturer");
            int maxX = ((Long) printer.get("maxX")).intValue();
            int maxY = ((Long) printer.get("maxY")).intValue();
            int maxZ = ((Long) printer.get("maxZ")).intValue();
            int maxColors = ((Long) printer.get("maxColors")).intValue();
            PrinterType printerType = PrinterType.values()[type];

            printerObjs.add(
                    PrinterFactory.getPrinter(id, printerType, name, manufacturer, maxX, maxY, maxZ, maxColors)
            );
        }

        return printerObjs;
    }

    public static ArrayList<Spool> loadSpoolsFromFile(String filename) {
        ArrayList<Spool> spoolObjs = new ArrayList<Spool>();

        if(filename.length() == 0) {
            filename = "spools1.csv";
        }
        List<String[]> spools = Inputs.readCSV(filename);

        for (String[] p : spools) {
            int id = Integer.parseInt(p[0]);
            String color = p[1];
            String filamentType = p[2];
            double length = Double.parseDouble(p[3]);
            FilamentType type;

            switch (filamentType) {
                case "PLA" -> type = FilamentType.PLA;
                case "PETG" -> type = FilamentType.PETG;
                case "ABS" -> type = FilamentType.ABS;
                default -> {
                    System.out.println("- Not a valid filamentType, bailing out");
                    continue;
                }
            }
            spoolObjs.add(new Spool(id, color, type, length));
        }

        return spoolObjs;
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