package nl.saxion;

import nl.saxion.Models.*;
import java.util.ArrayList;
import java.util.List;


public class PrinterManager {
    private PrinterManagerFacade facade = new PrinterManagerFacade();

    public void preload(String printsFile, String printersFile, String spoolsFile) {
//        ArrayList<Print> prints = Inputs.loadPrintsFromFile(printsFile);
//        ArrayList<Spool> spools = Inputs.loadSpoolsFromFile(spoolsFile);
//        ArrayList<Printer> printers = Inputs.loadPrintersFromFile(printersFile);
//        this.facade.preload(prints, printers, spools);
    }

    public void addPrinter(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        facade.addPrinter(id, printerType, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
    }

    public ArrayList<Printer> getPrinters() {
        return facade.getPrinters();
    }

    public void addSpool(Spool spool) {
        facade.addSpool(spool);
    }

    public List<Spool> getSpools() {
        return facade.getSpools();
    }

    // Unused
    public Spool getSpoolByID(int id) {
        return facade.getSpoolByID(id);
    }

    public void registerPrinterFailure(int printerId) {
        facade.registerPrinterFailure(printerId);
    }

    public void registerCompletion(int printerId) {
        facade.registerCompletion(printerId);
    }

    public Print findPrint(String printName) {
        return facade.findPrint(printName);
    }

    public Print findPrint(int index) {
        return facade.findPrint(index);
    }

    public PrintTask getPrinterCurrentTask(Printer printer) {
        return facade.getPrinterCurrentTask(printer);
    }

    public ArrayList<PrintTask> getPendingPrintTasks() {
        return facade.getPendingPrintTasks();
    }

    public ArrayList<Print> getPrints() {
        return facade.getPrints();
    }

    public void addPrint(String name, int height, int width, int length, ArrayList<Double> filamentLength, int printTime) {
        facade.addPrint(name, height, width, length, filamentLength, printTime);
    }

    public void startInitialQueue() {
        facade.startInitialQueue();
    }

    public void addPrintTask(String printName, List<String> colors, FilamentType type) {
        facade.addPrintTask(printName, colors, type);
    }

    public void selectPrintTask(Printer printer) {
        facade.selectPrintTask(printer);
    }

    public void showPrints() {
        var prints = facade.getPrints();
        System.out.println("<<---------- Available prints ---------->");
        for (var p : prints) {
            System.out.println(p);
        }
        System.out.println("<-------------------------------------->>");
    }

    public void showSpools() {
        var spools = facade.getSpools();
        System.out.println("<<---------- Spools ---------->");
        for (var spool : spools) {
            System.out.println(spool);
        }
        System.out.println("<---------------------------->>");
    }

    public void showPrinters() {
        var printers = facade.getPrinters();
        System.out.println("<<--------- Available printers --------->");
        for (var p : printers) {
            String output = p.toString();
            PrintTask currentTask = facade.getPrinterCurrentTask(p);
            if(currentTask != null) {
                output = output.replace("-------->",
                        "- Current Print Task: " + currentTask +
                                System.lineSeparator() + "-------->");
            }
            System.out.println(output);
        }
        System.out.println("<-------------------------------------->>");
    }

    public void showPendingPrintTasks() {
        ArrayList<PrintTask> printTasks = facade.getPendingPrintTasks();
        System.out.println("<<--------- Pending Print Tasks --------->");

        for (var p : printTasks) {
            System.out.println(p);
        }
        System.out.println("<-------------------------------------->>");
    }
}