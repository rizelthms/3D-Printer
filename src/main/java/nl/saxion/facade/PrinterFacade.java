package nl.saxion.facade;

import nl.saxion.Models.*;
import nl.saxion.PrinterManagerFacade;

import java.util.ArrayList;
import java.util.List;


public class PrinterFacade {
    private final PrinterManagerFacade manager = new PrinterManagerFacade();

    public void addPrinter(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        manager.addPrinter(id, printerType, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
    }

    public ArrayList<Printer> getPrinters() {
        return manager.getPrinters();
    }

    public void addSpool(Spool spool) {
        manager.addSpool(spool);
    }

    public List<Spool> getSpools() {
        return manager.getSpools();
    }

    // Unused
    public Spool getSpoolByID(int id) {
        return manager.getSpoolByID(id);
    }

    public void registerPrinterFailure(int printerId) {
        manager.registerPrinterFailure(printerId);
    }

    public void registerCompletion(int printerId) {
        manager.registerCompletion(printerId);
    }

    public Print findPrint(String printName) {
        return manager.findPrint(printName);
    }

    public Print findPrint(int index) {
        return manager.findPrint(index);
    }

    public PrintTask getPrinterCurrentTask(Printer printer) {
        return manager.getPrinterCurrentTask(printer);
    }

    public ArrayList<PrintTask> getPendingPrintTasks() {
        return manager.getPendingPrintTasks();
    }

    public ArrayList<Print> getPrints() {
        return manager.getPrints();
    }

    public void addPrint(String name, int height, int width, int length, ArrayList<Double> filamentLength, int printTime) {
        manager.addPrint(name, height, width, length, filamentLength, printTime);
    }

    public void startInitialQueue() {
        manager.startInitialQueue();
    }

    public void addPrintTask(String printName, List<String> colors, FilamentType type) {
        manager.addPrintTask(printName, colors, type);
    }

    public void selectPrintTask(Printer printer) {
        manager.selectPrintTask(printer);
    }

    private void printError(String s) {
        manager.printError(s);
    }
}