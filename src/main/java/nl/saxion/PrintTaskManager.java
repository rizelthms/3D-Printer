package nl.saxion;

import nl.saxion.Models.*;
import nl.saxion.facade.PrinterFacade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PrintTaskManager {

    PrinterFacade facade;

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

    private void printError(String s) {
        facade.printError(s);
    }

}
