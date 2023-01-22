package nl.saxion.Models;

public class Spool {
    private final int id;
    private final String color;
    private final FilamentType filamentType;
    private double length;

    public Spool(int id, String color, FilamentType filamentType, double length) {
        this.id = id;
        this.color = color;
        this.filamentType = filamentType;
        this.length = length;
    }

    public int getId() {
        return this.id;
    }

    public double getLength() {
        return length;
    }

    /**
     * Check if provided color and filament is compatible with the current spool.
     *
     * @param color
     * @param type
     * @return
     */
    public boolean spoolMatch(String color, FilamentType type) {
        if (color.equals(this.color) && type == this.getFilamentType()) {
            return true;
        }
        return false;
    }

    /**
     * This method will check if we have enough available length on the spool for a reduction
     *     by the provided int.
     *
     * @param byLength Length to cut out.
     * @return boolean which tells you if it's possible.
     */
    public boolean reduceLength(double byLength) {
        if (this.length < byLength) return false;

        return true;
    }

    public String getColor() {
        return color;
    }

    public FilamentType getFilamentType(){
        return filamentType;
    }

    @Override
    public String toString() {
        return  "<--------" + System.lineSeparator() +
                "- id: " + id + System.lineSeparator() +
                "- color: " + color + System.lineSeparator() +
                "- filamentType: " + filamentType + System.lineSeparator() +
                "- length: " + length + System.lineSeparator() +
                "-------->";
    }
}