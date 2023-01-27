package nl.saxion.strategy;

import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Printer;

public class EfficientSpoolUsageStrategy implements PrintStrategy{
    // This strategy is optimized for minimizing the amount of material wasted during a print job.
    // It may accomplish this by printing with a lower infill percentage, or by using a more
    // efficient support structure generation algorithm.

    private String name = "Efficient Spool Usage";

    //The following method should be used, but I need to pass information along
//    public void executeStrategy(Printer printer, PrintTask task) {
//    }

    public void executeStrategy() {
        //TODO: Implement some logic for the 'efficient spool changes' strategy
        System.out.println("Using strategy: " + this.name);

        // Example behavior:
        // - Analyze the 3D model to be printed and determine the optimal infill percentage
        //   that will minimize material waste while still ensuring the structural integrity of
        //   the final printed object.
        // - Use an efficient support structure generation algorithm to further reduce material waste.
        // - Begin printing with the determined infill percentage and support structure.
    }

    public String getName() {
        return name;
    }
}