package nl.saxion.Models;

import java.util.List;
import java.util.ArrayList;

/* Printer capable of printing multiple colors. */
public class MultiColor extends StandardFDM {
    private int maxColors;
    private Spool spool2;
    private Spool spool3;
    private Spool spool4;

    public MultiColor(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
        this.maxColors = maxColors;
    }

    public void setCurrentSpools(ArrayList<Spool> spools) {
        setCurrentSpool(spools.get(0));
        if(spools.size() > 1) spool2 = spools.get(1);
        if(spools.size() > 2) spool3 = spools.get(2);
        if(spools.size() > 3) spool4 = spools.get(3);
    }

    @Override
    public Spool[] getCurrentSpools() {
        Spool[] spools = new Spool[4];
        spools[0] = getCurrentSpool();
        spools[1] = spool2;
        spools[2] = spool3;
        spools[3] = spool4;
        return spools;
    }

    @Override
    public String toString() {
        String result = super.toString();
        String[] resultArray = result.split("- ");
        String spools = resultArray[resultArray.length-1];
        if(spool2 != null) {
            spools = spools.replace(System.lineSeparator(), ", " + spool2.getId() + System.lineSeparator());
        }
        if(spool3 != null) {
            spools = spools.replace(System.lineSeparator(), ", " + spool3.getId() + System.lineSeparator());
        }
        if(spool4 != null) {
            spools = spools.replace(System.lineSeparator(), ", " + spool4.getId() + System.lineSeparator());
        }
        spools = spools.replace("-------->", "- maxColors: " + maxColors + System.lineSeparator() +
                "-------->");
        resultArray[resultArray.length-1] = spools;
        result = String.join("- ", resultArray);

        return result;
    }

    public int getMaxColors() {
        return maxColors;
    }

    /**
     * All currently set spools must match the provided colors for the provided filament type.
     * For multicolor printers, the order of spools does matter, so the order has to match.
     *
     * @param colors List of colors as strings.
     * @param filament The filament type enum member for the filament requested.
     * @return False if one of the spools fails to match the respective color, else true.
     */
    public boolean allSpoolsMatch(List<String> colors, FilamentType filament) {
        Spool[] spools = getCurrentSpools();
        int upperBound = Math.min(spools.length, colors.size());

        for (int i = 0; i < upperBound; i++) {
            if (!spools[i].spoolMatch(colors.get(i), filament)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if the provided print task is valid for this printer.
     *
     * @param printTask The task we're matching with this printer.
     * @param doSpoolMatch True if we should match the spool on the printer to the task.
     * @return False if the task is not a match, else true.
     */
    public boolean isValidTask(PrintTask printTask, boolean doSpoolMatch) {
        // A spool should be set, the print should fit.
        if (getCurrentSpool() == null || !printFits(printTask.getPrint())) {
            return false;
        }

        List<String> taskColors = printTask.getColors();
        FilamentType filament = printTask.getFilamentType();

        boolean isABSFilament = filament == FilamentType.ABS;
        boolean isInvalidMaxColors = taskColors.size() > maxColors;

        // Cannot be for ABS filaments, or require more colors than maxColors.
        if (isABSFilament || isInvalidMaxColors) return false;

        // Invalid if spools do not match when they should, so return false.
        if (doSpoolMatch && !allSpoolsMatch(taskColors, filament)) return false;

        return true;
    }
}