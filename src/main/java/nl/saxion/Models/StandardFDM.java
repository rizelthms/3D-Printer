package nl.saxion.Models;

import java.util.ArrayList;

/* Standard cartesian FDM printer */
public class StandardFDM extends Printer {
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private Spool currentSpool;

    public StandardFDM(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ) {
        super(id, printerName, manufacturer);
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public void setCurrentSpools(ArrayList<Spool> spools) {
        this.currentSpool = spools.get(0);
    }

    public void setCurrentSpool(Spool spool) {
        this.currentSpool = spool;
    }

    public Spool getCurrentSpool() {
        return currentSpool;
    }

    public Spool[] getCurrentSpools() {
        Spool[] spools = new Spool[1];
        if(currentSpool != null) {
            spools[0] = currentSpool;
        }
        return spools;
    }

    @Override
    public boolean printFits(Print print) {
        return print.getHeight() <= maxZ && print.getWidth() <= maxX && print.getLength() <= maxY;
    }

    @Override
    public int CalculatePrintTime(String filename) {
        return 0;
    }

    @Override
    public String toString() {
        String result = super.toString();
        String append = "- maxX: " + maxX + System.lineSeparator() +
                "- maxY: " + maxY + System.lineSeparator() +
                "- maxZ: " + maxZ + System.lineSeparator();
        if (currentSpool != null) {
            append += "- Spool(s): " + currentSpool.getId()+ System.lineSeparator();
        }
        append += "-------->";
        result = result.replace("-------->", append);
        return result;
    }

    /**
     * Standard FDM can print tasks whose color matches the spool attached, as long
     *     as the filament type is not ABS, and the print fits.
     *
     * @param printTask The task we're matching with this printer.
     * @param doSpoolMatch True if we should match the spool on the printer to the task.
     * @return False if the task is not a match, else true.
     */
    public boolean isValidTask(PrintTask printTask, boolean doSpoolMatch) {
        // If we must match with the spool, spool should be non-null.
        if (doSpoolMatch && currentSpool == null) return false;

        // The print should fit.
        if (!printFits(printTask.getPrint())) return false;

        String taskColor = printTask.getColors().get(0);
        FilamentType filament = printTask.getFilamentType();

        boolean isABSFilament = filament == FilamentType.ABS;
        boolean isInvalidMaxColors = printTask.getColors().size() != 1;

        // Cannot be ABS filament, or an invalid number of maxColors (!= 1).
        if (isABSFilament || isInvalidMaxColors) return false;

        // Invalid if spool does not match when it should, so return false.
        if (doSpoolMatch && !currentSpool.spoolMatch(taskColor, filament)) return false;

        return true;
    }
}