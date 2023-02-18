package nl.saxion.strategy;

import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Printer;
import nl.saxion.PrinterManager;

public interface PrintStrategy {
    PrintTask doSpoolSelection(PrinterManager printerManager, Printer printer);
}