package nl.saxion;

import nl.saxion.Models.*;
import nl.saxion.facade.PrinterFacade;
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
public class Main {
    PrinterFacade facade = new PrinterFacade();
    PrinterManager manager = new PrinterManager();
    PrintTaskManager taskManager = new PrintTaskManager();
    Menu menu = new Menu();
    Scanner scanner = new Scanner(System.in);

    private String printStrategy = "Less Spool Changes";

    // Run run() method
    public static void main(String[] args) {
        new Main().run(args);
    }

    // Read data and loop menu options
    public void run(String[] args) {
        if(args.length > 0) {
            readPrintsFromFile(args[0]);
            readSpoolsFromFile(args[1]);
            readPrintersFromFile(args[2]);
        } else {
            readPrintsFromFile("");
            readSpoolsFromFile("");
            readPrintersFromFile("");
        }

        menu.menuSwitch();
    }

    // Start printer queue
    public void startPrintQueue() {
        System.out.println("<<---------- Starting Print Queue ---------->");
        taskManager.startInitialQueue();
        System.out.println("<----------------------------------->>");
    }

    // Exit (does nothing)
    private void exit() {

    }
    // This method only changes the name but does not actually work.
    // It exists to demonstrate the output.
    // in the future strategy might be added.
    public void changePrintStrategy() {
        System.out.println("<<---------- Change Strategy ------------->");
        System.out.println("- Current strategy: " + printStrategy);
        System.out.println("- 1: Less Spool Changes");
        System.out.println("- 2: Efficient Spool Usage");
        System.out.println("- Choose strategy: ");
        int strategyChoice = numberInput(1, 2);
        if(strategyChoice == 1) {
            printStrategy = "- Less Spool Changes";
        } else if( strategyChoice == 2) {
            printStrategy = "- Efficient Spool Usage";
        }
        System.out.println("<----------------------------------->>");
    }
    // TODO: This should be based on which printer is finished printing.
    public void registerPrintCompletion() {
        ArrayList<Printer> printers = manager.getPrinters();
        System.out.println("<<---------- Currently Running Printers ---------->");
        for(Printer p: printers) {
            PrintTask printerCurrentTask= taskManager.getPrinterCurrentTask(p);
            if(printerCurrentTask != null) {
                System.out.println("- " + p.getId() + ": " +p.getName() + " - " + printerCurrentTask);
            }
        }
        System.out.print("- Printer that is done (ID): ");
        int printerId = numberInput(-1, printers.size());
        System.out.println("<----------------------------------->>");
        manager.registerCompletion(printerId);
    }
    public void registerPrinterFailure() {
        ArrayList<Printer> printers = manager.getPrinters();
        System.out.println("<<---------- Currently Running Printers ---------->");
        for(Printer p: printers) {
            PrintTask printerCurrentTask= taskManager.getPrinterCurrentTask(p);
            if(printerCurrentTask != null) {
                System.out.println("- " + p.getId() + ": " +p.getName() + " > " + printerCurrentTask);
            }
        }
        System.out.print("- Printer ID that failed: ");
        int printerId = numberInput(1, printers.size());
        manager.registerPrinterFailure(printerId);
        System.out.println("<----------------------------------->>");
    }
    public void addNewPrintTask() {
        List<String> colors = new ArrayList<>();
        var prints = taskManager.getPrints();
        System.out.println("<<---------- New Print Task ---------->");
        System.out.println("<---------- Available prints ----------");
        int counter = 1;
        for (var p : prints) {
            System.out.println("- " + counter + ": " + p.getName());
            counter++;
        }
        System.out.print("- Print number: ");
        int printNumber = numberInput(1, prints.size());
        System.out.println("-------------------------------------->");
        Print print = taskManager.findPrint(printNumber - 1);
        String printName = print.getName();
        System.out.println("<---------- Filament Type ----------");
        System.out.println("- 1: PLA");
        System.out.println("- 2: PETG");
        System.out.println("- 3: ABS");
        System.out.print("- Filament type number: ");
        int ftype = numberInput(1, 3);
        System.out.println("-------------------------------------->");
        FilamentType type;
        switch (ftype) {
            case 1 -> type = FilamentType.PLA;
            case 2 -> type = FilamentType.PETG;
            case 3 -> type = FilamentType.ABS;
            default -> {
                System.out.println("- Not a valid filamentType, bailing out");
                return;
            }
        }
        var spools = manager.getSpools();
        System.out.println("<---------- Colors ----------");
        ArrayList<String> availableColors = new ArrayList<>();
        counter = 1;
        for (var spool : spools) {
            String colorString = spool.getColor();
            if(type == spool.getFilamentType() && !availableColors.contains(colorString)) {
                System.out.println("- " + counter + ": " + colorString + " (" + spool.getFilamentType() + ")");
                availableColors.add(colorString);
                counter++;
            }
        }
        System.out.print("- Color number: ");
        int colorChoice = numberInput(1, availableColors.size());
        colors.add(availableColors.get(colorChoice-1));
        for(int i = 1; i < print.getFilamentLength().size(); i++) {
            System.out.print("- Color number: ");
            colorChoice = numberInput(1, availableColors.size());
            colors.add(availableColors.get(colorChoice-1));
        }
        System.out.println("-------------------------------------->");
        taskManager.addPrintTask(printName, colors, type);
        System.out.println("<---------------------------->>");
    }
    public void showPrints() {
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
    private void readPrintsFromFile(String filename) {
        JSONParser jsonParser = new JSONParser();
        if(filename.length() == 0) {
            filename = "prints.json";
        }

        System.out.println(Main.class.getResource("Main.class"));

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
                //manager class
                taskManager.addPrint(name, height, width, length, filamentLength, printTime);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    private void readPrintersFromFile(String filename) {
        JSONParser jsonParser = new JSONParser();
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
                manager.addPrinter(id, type, name, manufacturer, maxX, maxY, maxZ, maxColors);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    private void readSpoolsFromFile(String filename) {
        JSONParser jsonParser = new JSONParser();
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
                manager.addSpool(new Spool(id, color, type, length));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    public String stringInput() {
        String input = null;
        while(input == null || input.length() == 0){
            input = scanner.nextLine();
        }
        return input;
    }
    public int numberInput() {
        return scanner.nextInt();
    }
    public int numberInput(int min, int max) {
        int input = numberInput();
        while (input < min || input > max) {
            input = numberInput();
        }
        return input;
    }
}
