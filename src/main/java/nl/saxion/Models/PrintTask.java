package nl.saxion.Models;

import nl.saxion.utils.Tools;
import java.util.List;
import java.util.ArrayList;

public class PrintTask {
    private Print print;
    private List<String> colors;
    private FilamentType filamentType;

    public PrintTask(Print print, List<String> colors, FilamentType filamentType){
        this.print = print;
        this.colors = colors;
        this.filamentType = filamentType;
    }

    public List<String> getColors() {
        return colors;
    }

    public FilamentType getFilamentType() {
        return filamentType;
    }

    public Print getPrint(){
        return print;
    }

    @Override
    public String toString() {
        return "< " + print.getName() + " " + filamentType + " " + colors.toString() + " >";
    }

    public ArrayList<Spool> getValidSpools(ArrayList<Spool> spools) {
        ArrayList<Spool> validSpools = new ArrayList<Spool>();

        for (int i = 0; i < colors.size(); i++) {
            for (Spool spool : spools) {
                String curColor = colors.get(i);
                boolean spoolMatched = spool.spoolMatch(curColor, filamentType);

                if (spoolMatched && !Tools.containsSpool(validSpools, curColor)) {
                    validSpools.add(spool);
                    break; // Match only one spool per color.
                }
            }
        }

        return validSpools;
    }

}