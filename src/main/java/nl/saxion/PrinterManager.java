package nl.saxion;

import nl.saxion.Models.*;
import nl.saxion.facade.PrinterFacade;
import org.json.simple.JSONArray;

import java.util.*;

public class PrinterManager {

    PrinterFacade facade;

    public PrinterManager(PrinterFacade facade) {
        this.facade = facade;
    }

    public void addPrinter(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        facade.addPrinter(id, printerType, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
    }

    public boolean containsSpool(final List<Spool> list, final String name){
        return facade.containsSpool(list, name);
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

    //Unused
    public Spool getSpoolByID(int id) {
        return facade.getSpoolByID(id);
    }

    public void registerPrinterFailure(int printerId) {
        facade.registerPrinterFailure(printerId);
    }

    public void registerCompletion(int printerId) {
        facade.registerCompletion(printerId);
    }

}
