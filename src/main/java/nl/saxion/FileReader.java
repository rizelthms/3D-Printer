package nl.saxion;

import nl.saxion.Models.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class FileReader {

    private final PrinterManagerFacade facade = new PrinterManagerFacade();

    public void readFile(String printsFile, String spoolsFile, String printersFile) {
        ArrayList<Print> prints = loadPrintsFromFile(printsFile);
        ArrayList<Spool> spools = loadSpoolsFromFile(spoolsFile);
        ArrayList<Printer> printers = loadPrintersFromFile(printersFile);
        this.facade.preload(prints, printers, spools);
    }

    public static ArrayList<Print> loadPrintsFromFile(String filename) {
        ArrayList<Print> printObjs = new ArrayList<>();

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
            ArrayList<Double> filamentLength = new ArrayList<>();

            for (Object o : fLength) {
                filamentLength.add(((Double) o));
            }

            printObjs.add(
                    new Print(name, height, width, length, filamentLength, printTime)
            );
        }

        return printObjs;
    }

    public static ArrayList<Spool> loadSpoolsFromFile(String filename) {
        ArrayList<Spool> spoolObjs = new ArrayList<>();

        if(filename.length() == 0) {
            filename = "spools.json";
        }
        JSONArray spools = Inputs.readJSON(filename);

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
                    continue;
                }
            }
            spoolObjs.add(new Spool(id, color, type, length));
        }

        return spoolObjs;
    }

    public static ArrayList<Printer> loadPrintersFromFile(String filename) {
        ArrayList<Printer> printerObjs = new ArrayList<>();

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
}
