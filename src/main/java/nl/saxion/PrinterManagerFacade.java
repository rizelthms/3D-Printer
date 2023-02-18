package nl.saxion;

import nl.saxion.Models.*;
import java.util.ArrayList;
import java.util.List;


public class PrinterManagerFacade {
    private PrinterManager printerManager = new PrinterManager();

    public void preload(String printsFile, String printersFile, String spoolsFile) {
        ArrayList<Print> prints = Inputs.loadPrintsFromFile(printsFile);
        ArrayList<Spool> spools = Inputs.loadSpoolsFromFile(spoolsFile);
        ArrayList<Printer> printers = Inputs.loadPrintersFromFile(printersFile);
        this.printerManager.preload(prints, printers, spools);
    }

    public ArrayList<Printer> getPrinters() {
        return printerManager.getPrinters();
    }

    public ArrayList<Spool> getSpools() {
        return printerManager.getSpools();
    }

    public void registerPrinterFailure(int printerId) {
        printerManager.registerPrinterFailure(printerId);
    }

    public void registerCompletion(int printerId) {
        printerManager.registerCompletion(printerId);
    }

    public Print findPrint(int index) {
        return printerManager.findPrint(index);
    }

    public ArrayList<Print> getPrints() {
        return printerManager.getPrints();
    }

    public void startInitialQueue() {
        printerManager.startInitialQueue();
    }

    public void addPrintTask(String printName, List<String> colors, FilamentType type) {
        printerManager.addPrintTask(printName, colors, type);
    }

    public void selectStrategy(StrategyOptions strategyOption) {
        printerManager.selectStrategy(strategyOption);
    }

    public void showPrints() {
        var prints = printerManager.getPrints();
        System.out.println("<<---------- Available prints ---------->");
        for (var p : prints) {
            System.out.println(p);
        }
        System.out.println("<-------------------------------------->>");
    }

    public void showSpools() {
        var spools = printerManager.getSpools();
        System.out.println("<<---------- Spools ---------->");
        for (var spool : spools) {
            System.out.println(spool);
        }
        System.out.println("<---------------------------->>");
    }

    public void showPrinters() {
        var printers = printerManager.getPrinters();
        System.out.println("<<--------- Available printers --------->");
        for (var p : printers) {
            String output = p.toString();
            PrintTask currentTask = printerManager.getPrinterCurrentTask(p);
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
        ArrayList<PrintTask> printTasks = printerManager.getPendingPrintTasks();
        System.out.println("<<--------- Pending Print Tasks --------->");

        for (var p : printTasks) {
            System.out.println(p);
        }
        System.out.println("<-------------------------------------->>");
    }
}
