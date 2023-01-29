package nl.saxion.strategy;

import nl.saxion.PrinterManagerFacade;
import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Printer;
import nl.saxion.Models.Spool;
import nl.saxion.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class EfficientSpoolUsageStrategy implements PrintStrategy {
    /**
     * Does spool selection, changes the spool if a valid set was found, and
     *     returns the task for which spools were matched.
     *
     * @param facade The PrinterManagerFacade object, i.e. the caller.
     * @param printer The Printer object we're finding spools for.
     * @return The task for which spools were found.
     */
    public PrintTask doSpoolSelection(PrinterManagerFacade facade, Printer printer) {
        ArrayList<Spool> sortedSpools = Tools.sortSpoolsByLength(facade.getFreeSpools());
        ArrayList<Spool> validSpools = new ArrayList<Spool>();
        ArrayList<PrintTask> filteredPrints = new ArrayList<PrintTask>();
        PrintTask chosenTask = null;

        for (Spool spool : sortedSpools) {
            for (PrintTask printTask : facade.getPendingPrintTasks()) {
                List<String> colors = printTask.getColors();

                if (printer.isValidTask(printTask, false)) {
                    for (int i = 0; i < colors.size(); i++) {
                        String curColor = colors.get(i);
                        boolean spoolMatched = spool.spoolMatch(curColor, printTask.getFilamentType());
                        boolean hasSpace = spool.isValidCut(printTask.getPrint().getFilamentLength().get(i));

                        if (hasSpace && spoolMatched) {
                            filteredPrints.add(chosenTask);
                            break;
                        }
                    }
                }
            }
        }

        filteredPrints = Tools.sortTasksByFilamentLength(filteredPrints);
        chosenTask = filteredPrints.get(0);
        // Do a check here to see if it's multicolor that all colors have a spool available.
        validSpools = matchSpools(validSpools, chosenTask);

        if (chosenTask != null && validSpools.size() == chosenTask.getColors().size()) {
            facade.changeSpool(printer, validSpools);
            return chosenTask;
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