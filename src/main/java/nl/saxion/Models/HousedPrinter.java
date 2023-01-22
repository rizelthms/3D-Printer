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
    public boolean isValidTask(PrintTask printTask, boolean doSpoolMatch) {
        Spool spool = getCurrentSpool();
        boolean isInvalidMaxColors = printTask.getColors().size() != 1;

        // A spool should be set, the print should fit, maxColors should be valid.
        if (spool == null || !printFits(printTask.getPrint()) || isInvalidMaxColors) {
            return false;
        }

        String taskColor = printTask.getColors().get(0);
        FilamentType filament = printTask.getFilamentType();

        // Invalid if spool does not match when it should, so return false.
        if (doSpoolMatch && !spool.spoolMatch(taskColor, filament)) return false;

        return true;
    }
}