package nl.saxion;

import nl.saxion.Models.*;
import nl.saxion.facade.PrinterFacade;

import java.util.ArrayList;
import java.util.List;


public class PrinterManager {
//    private PrinterManagerFacade facade = new PrinterManagerFacade();
    PrinterFacade facade;

    public PrinterManager(PrinterFacade facade) {
        this.facade = facade;
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
        System.out.println(facade.registerPrinterFailure(printerId));
    }

    public void registerCompletion(int printerId) {
        System.out.println(facade.registerCompletion(printerId));
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
        System.out.println(facade.addPrintTask(printName, colors, type));
    }

    public void selectPrintTask(Printer printer) {
        System.out.println(facade.selectPrintTask(printer));
    }

    //Shows in menu
    public void showPrints() {
        System.out.println(facade.showPrints());
    }

    //Shows in menu
    public void showSpools() {
        System.out.println(facade.showSpools());
    }

    //Shows in menu
    public void showPrinters() {
        System.out.println(facade.showPrinters());
    }

    //Shows in menu
    public void showPendingPrintTasks() {
        System.out.println(facade.showPendingPrintTasks());
    }
}