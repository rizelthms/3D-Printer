package nl.saxion.Models;

/* Printer capable of printing ABS */
public class HousedPrinter extends StandardFDM {
    public HousedPrinter(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
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
        Spool spool = getCurrentSpool();
        boolean isInvalidMaxColors = printTask.getColors().size() != 1;

        // If we must match with the spool, spool should be non-null.
        if (doSpoolMatch && spool == null) return false;

        // The print should fit, maxColors should be valid.
        if (!printFits(printTask.getPrint()) || isInvalidMaxColors) {
            return false;
        }

        String taskColor = printTask.getColors().get(0);
        FilamentType filament = printTask.getFilamentType();

        // Invalid if spool does not match when it should, so return false.
        if (doSpoolMatch && !spool.spoolMatch(taskColor, filament)) return false;

        return true;
    }
}