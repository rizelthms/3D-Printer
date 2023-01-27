package nl.saxion.strategy;

import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Printer;

public class LessSpoolChangesStrategy implements PrintStrategy{
    // This strategy is optimized for minimizing the number of times the spool needs to be changed
    // during a print job. It may accomplish this by printing with a single color or material
    // for as long as possible, or by selecting a specific order of printing layers to reduce
    // the number of spool changes required.

    private String name = "Less Spool Changes";

    //The following method should be used, but I need to pass information along
//    public void executeStrategy(Printer printer, PrintTask task) {
//    }

    public void executeStrategy() {
        //TODO: Implement some logic for the 'less spool changes' strategy
        System.out.println("Using strategy: " + this.name);

        // Example behavior:
        // - Analyze the 3D model to be printed and determine which color or material
        //   can be used for the longest period of time without needing to change the spool.
        // - Reorder the layers of the 3D model to be printed in a way that reduces the number
        //   of spool changes required.
        // - Begin printing with the selected color or material, only switching spools when
        //   absolutely necessary.

    }

    public String getName() {
        return name;
    }
}