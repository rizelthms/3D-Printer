package nl.saxion.strategy;

import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Printer;
import nl.saxion.Models.Spool;
import nl.saxion.PrinterManagerFacade;
import nl.saxion.utils.Tools;
import java.util.ArrayList;
import java.util.List;

public class LessSpoolChangesStrategy implements PrintStrategy {
    /**
     * Does spool selection, changes the spool if a valid set was found, and
     *     returns the task for which spools were matched.
     *
     * @param facade The PrinterManagerFacade object, i.e. the caller.
     * @param printer The Printer object we're finding spools for.
     * @return The task for which spools were found.
     */
    public PrintTask doSpoolSelection(PrinterManagerFacade facade, Printer printer) {
        for (PrintTask printTask : facade.getPendingPrintTasks()) {
            if (printer.isValidTask(printTask, false)) {
                ArrayList<Spool> newSpools = matchSpools(facade.getFreeSpools(), printTask);

                if (newSpools.size() == printTask.getColors().size()) {
                    facade.changeSpool(printer, newSpools);
                    return printTask;
                }
            }
        }
        return null;
    }

    public ArrayList<Spool> matchSpools(ArrayList<Spool> spools, PrintTask task) {
        ArrayList<Spool> validSpools = new ArrayList<Spool>();
        List<String> colors = task.getColors();

        for (int i = 0; i < colors.size(); i++) {
            for (Spool spool : spools) {
                String curColor = colors.get(i);
                boolean spoolMatched = spool.spoolMatch(curColor, task.getFilamentType());
                boolean hasSpace = spool.isValidCut(task.getPrint().getFilamentLength().get(i));

                if (hasSpace && spoolMatched && !Tools.containsSpool(validSpools, curColor)) {
                    validSpools.add(spool);
                    break; // Match only one spool per color.
                }
            }
        }

        return validSpools;
    }
}

