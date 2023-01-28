package nl.saxion.strategy;

import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Printer;
import nl.saxion.PrinterManagerFacade;

public interface PrintStrategy {
    public PrintTask doSpoolSelection(PrinterManagerFacade facade, Printer printer);
}