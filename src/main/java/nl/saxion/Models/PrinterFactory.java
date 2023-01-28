package nl.saxion.Models;

public class PrinterFactory {

    /**
     * Instantiate a new Printer type from the factory.
     * Can be any of StandardFDM, HousedPrinter, or MultiColor.
     *
     * @param id Integer ID to assign to the printer.
     * @param printerType The printer type as an integer.
     * @param printerName The printer name to assign.
     * @param manufacturer Manufacturer of the printer, a string.
     * @param maxX Maximum supported range in the X axis of the print.
     * @param maxY Maximum supported range in the Y axis of the print.
     * @param maxZ Maximum supported range in the Z axis of the print.
     * @param maxColors Maximum number of colors the printer supports.
     * @return Return the instantiated type.
     */
    public static Printer getPrinter(
            int id,
            PrinterType printerType,
            String printerName,
            String manufacturer,
            int maxX,
            int maxY,
            int maxZ,
            int maxColors
    ) {
        switch(printerType) {
            case StandardFDM:
                return new StandardFDM(
                        id,
                        printerName,
                        manufacturer,
                        maxX,
                        maxY,
                        maxZ
                );
            case HousedPrinter:
                return new HousedPrinter(
                        id,
                        printerName,
                        manufacturer,
                        maxX,
                        maxY,
                        maxZ
                );
            case MultiColor:
                return new MultiColor(
                        id,
                        printerName,
                        manufacturer,
                        maxX,
                        maxY,
                        maxZ,
                        maxColors
                );
            case HousedMultiColorPrinter:
                return new HousedMultiColorPrinter(
                        id,
                        printerName,
                        manufacturer,
                        maxX,
                        maxY,
                        maxZ,
                        maxColors
                );
            default:
                System.err.println("Invalid printer type: " + printerType);
        }

        return null;
    }
}