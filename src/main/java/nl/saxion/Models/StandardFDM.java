package nl.saxion.Models;

import java.util.ArrayList;
import java.util.List;

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
     * Internal subroutine to run printer-type-specific validations for the provided
     *     print task.
     *
     * @param printTask The printTask we're validating.
     * @return True if the task is compatible with this printer, false otherwise.
     */
    private boolean validations(PrintTask printTask) {
        boolean isABSFilament = printTask.getFilamentType() == FilamentType.ABS;
        boolean isInvalidMaxColors = printTask.getColors().size() != 1;

        // Cannot be ABS filament, or an invalid number of maxColors (!= 1).
        // And the print should fit.
        if (isABSFilament || isInvalidMaxColors || !printFits(printTask.getPrint())) {
            return false;
        }

        return true;
    }

    /**
     * Standard FDM can print tasks whose color matches the spool attached, as long
     *     as the filament type is not ABS, and the print fits.
     *
     * @param printTask The task we're matching with this printer.
     * @return False if the task is not a match, else true.
     */
    public boolean isValidTask(PrintTask printTask) {
        // A spool should be set, and initial validations pass.
        if (currentSpool == null || !validations(printTask)) return false;

        String taskColor = printTask.getColors().get(0);

        // Valid if spool matches.
        if (currentSpool.spoolMatch(taskColor, printTask.getFilamentType())) {
            return true;
        }

        return false;
    }

    /**
     * For a given print task, return a subset of the provided list of spools that match.
     *
     * @param printTask The task we're matching with this printer.
     * @param spools The list of spools we're trying to match with.
     * @return False if the task is not a match, else true.
     */
    public ArrayList<Spool> getValidSpoolsForTask(PrintTask printTask, ArrayList<Spool> spools) {
        // Spools should not be empty, and initial validations pass.
        if (spools.isEmpty() || !validations(printTask)) return null;

        String taskColor = printTask.getColors().get(0);
        ArrayList<Spool> validSpools = new ArrayList<Spool>();

        // Return matching spool.
        for (Spool spool : spools) {
            if (spool.spoolMatch(taskColor, printTask.getFilamentType())) {
                validSpools.add(spool);
                break; // Break since we only need one matching spool for this printer type.
            }
        }

        return validSpools;
    }
}