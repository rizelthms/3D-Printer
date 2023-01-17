package nl.saxion;

import nl.saxion.Models.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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

    public static void showPrints() {
        var prints = taskManager.getPrints();
        System.out.println("<<---------- Available prints ---------->");
        for (var p : prints) {
            System.out.println(p);
        }
        System.out.println("<-------------------------------------->>");
    }

    public void showSpools() {
        var spools = manager.getSpools();
        System.out.println("<<---------- Spools ---------->");
        for (var spool : spools) {
            System.out.println(spool);
        }
        System.out.println("<---------------------------->>");
    }

    public void showPrinters() {
        var printers = manager.getPrinters();
        System.out.println("<<--------- Available printers --------->");
        for (var p : printers) {
            String output = p.toString();
            PrintTask currentTask = taskManager.getPrinterCurrentTask(p);
            if(currentTask != null) {
                output = output.replace("-------->", "- Current Print Task: " + currentTask + System.lineSeparator() +
                        "-------->");
            }
            System.out.println(output);
        }
        System.out.println("<-------------------------------------->>");
    }

    public void showPendingPrintTasks() {
        ArrayList<PrintTask> printTasks = taskManager.getPendingPrintTasks();
        System.out.println("<<--------- Pending Print Tasks --------->");
        for (var p : printTasks) {
            System.out.println(p);
        }
        System.out.println("<-------------------------------------->>");
    }

    public static ArrayList<Print> loadPrintsFromFile(String filename) {
        JSONParser jsonParser = new JSONParser();
        ArrayList<Print> printObjs = new ArrayList<Print>();

        if(filename.length() == 0) {
            filename = "prints.json";
        }

        URL printResource = getClass().getResource("/" + filename);
        if (printResource == null) {
            System.err.println("Warning: Could not find prints.json file");
            return;
        }
        try (FileReader reader = new FileReader(URLDecoder.decode(printResource.getPath(), StandardCharsets.UTF_8))) {
            JSONArray prints = (JSONArray) jsonParser.parse(reader);
            System.out.println(prints);
            for (Object p : prints) {
                JSONObject print = (JSONObject) p;
                String name = (String) print.get("name");
                int height = ((Long) print.get("height")).intValue();
                int width = ((Long) print.get("width")).intValue();
                int length = ((Long) print.get("length")).intValue();
                //int filamentLength = ((Long) print.get("filamentLength")).intValue();
                JSONArray fLength = (JSONArray) print.get("filamentLength");
                int printTime = ((Long) print.get("printTime")).intValue();
                ArrayList<Double> filamentLength = new ArrayList<>();
                for(int i = 0; i < fLength.size(); i++) {
                    filamentLength.add(((Double) fLength.get(i)));
                }

                printObjs.add(
                        new Print(name, height, width, length, filamentLength, printTime)
                );
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return printObjs;
    }

    public static ArrayList<Printer> loadPrintersFromFile(String filename) {
        JSONParser jsonParser = new JSONParser();
        ArrayList<Printer> printerObjs = new ArrayList<Printer>();

        if(filename.length() == 0) {
            filename = "printers.json";
        }
        URL printersResource = getClass().getResource("/" + filename);
        if (printersResource == null) {
            System.err.println("Warning: Could not find printers.json file");
            return;
        }
        try (FileReader reader = new FileReader(URLDecoder.decode(printersResource.getPath(), StandardCharsets.UTF_8))) {
            JSONArray printers = (JSONArray) jsonParser.parse(reader);
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

                // ----- Use Factory here for printer subtypes.
                printerObjs.add(
                        new Printer(id, type, name, manufacturer, maxX, maxY, maxZ, maxColors)
                );
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return printerObjs;
    }

    public static ArrayList<Spool> loadSpoolsFromFile(String filename) {
        JSONParser jsonParser = new JSONParser();
        ArrayList<Spool> spoolObjs = new ArrayList<Spool>();

        if(filename.length() == 0) {
            filename = "spools.json";
        }
        URL spoolsResource = getClass().getResource("/" + filename);
        if (spoolsResource == null) {
            System.err.println("Warning: Could not find spools.json file");
            return;
        }
        try (FileReader reader = new FileReader(URLDecoder.decode(spoolsResource.getPath(), StandardCharsets.UTF_8))) {
            JSONArray spools = (JSONArray) jsonParser.parse(reader);
            for (Object p : spools) {
                JSONObject spool = (JSONObject) p;
                int id = ((Long) spool.get("id")).intValue();
                String color = (String) spool.get("color");
                String filamentType = (String) spool.get("filamentType");
                double length = (Double) spool.get("length");
                FilamentType type;
                switch (filamentType) {
                    case "PLA" -> type = FilamentType.PLA;
                    case "PETG" -> type = FilamentType.PETG;
                    case "ABS" -> type = FilamentType.ABS;
                    default -> {
                        System.out.println("- Not a valid filamentType, bailing out");
                        return;
                    }
                }

                spoolObjs.add(new Spool(id, color, type, length));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
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