package nl.saxion.strategy;

import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Printer;

public interface PrintStrategy{

    //The following method should be used, but I need to pass information along
//    void executeStrategy(Printer printer, PrintTask task);

    void executeStrategy();

    String getName();

}