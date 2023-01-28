package nl.saxion.Models;

import java.util.List;

/* Printer capable of printing ABS */
public class HousedMultiColorPrinter extends MultiColor {
    public HousedMultiColorPrinter(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
    }

    /**
     * The housed printer is the only one that can print ABS, but it can also print the others.
     *
     * @param printTask The task we're matching with this printer.
     * @param doSpoolMatch True if we should match the spool on the printer to the task.
     * @return False if the task is not a match, else true.
     */
    @Override
    public boolean isValidTask(PrintTask printTask, boolean doSpoolMatch) {
        // If we must match with the spool, spool should be non-null.
        if (doSpoolMatch && getCurrentSpool() == null) return false;

        // The print should fit.
        if (!printFits(printTask.getPrint())) return false;

        List<String> taskColors = printTask.getColors();
        FilamentType filament = printTask.getFilamentType();

        // Cannot require more colors than maxColors.
        if (taskColors.size() > this.getMaxColors()) return false;

        // Invalid if spools do not match when they should, so return false.
        if (doSpoolMatch && !allSpoolsMatch(taskColors, filament)) return false;

        return true;
    }
}
