package nl.saxion.facade;

import nl.saxion.Menu;
import nl.saxion.Models.*;

import java.util.*;


public class PrinterFacade {

    private ArrayList<Printer> printers = new ArrayList<>();
    private ArrayList<Spool> spools = new ArrayList<>();
    private ArrayList<Spool> freeSpools = new ArrayList<>();
    private ArrayList<Printer> freePrinters = new ArrayList<>();
    private ArrayList<Print> prints = new ArrayList<>();

    private ArrayList<PrintTask> pendingPrintTasks = new ArrayList<>();
    private HashMap<Printer, PrintTask> runningPrintTasks = new HashMap<>();

    Timer taskTimer = new Timer("taskTimer");
    private StringBuilder stringBuilder;

    public void preload(ArrayList<Print> prints, ArrayList<Spool> spools, ArrayList<Printer> printers) {
        this.prints.addAll(prints);

        this.spools.addAll(spools);
        this.freeSpools.addAll(spools);

        this.printers.addAll(printers);
        this.freePrinters.addAll(printers);
    }

    /**
     * Add a Printer to the list of all printers and the list of free printers.
     * Instantiate a Printer from the list of input params required below.
     *
     * @param id Integer ID to assign to the printer.
     * @param printerType The printer type as an integer.
     * @param printerName The printer name to assign.
     * @param manufacturer Manufacturer of the printer, a string.
     * @param maxX Maximum supported range in the X axis of the print.
     * @param maxY Maximum supported range in the Y axis of the print.
     * @param maxZ Maximum supported range in the Z axis of the print.
     * @param maxColors Maximum number of colors the printer supports.
     */
    public void addPrinter(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        PrinterType type = PrinterType.values()[printerType];
        Printer printer = PrinterFactory.getPrinter(id, type, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
        printers.add(printer);
        freePrinters.add(printer);
    }

    public ArrayList<Printer> getPrinters() {
        return printers;
    }

    public void addSpool(Spool spool) {
        spools.add(spool);
        freeSpools.add(spool);
    }

    public List<Spool> getSpools() {
        return spools;
    }

    public Spool getSpoolByID(int id) {
        for(Spool s: spools) {
            if(s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    public String registerPrinterFailure(int printerId) {
        stringBuilder = new StringBuilder();

        Map.Entry<Printer, PrintTask> foundEntry = (Map.Entry<Printer, PrintTask>) filterPrintTasksList(printerId);
        Printer printer = foundEntry.getKey();

        PrintTask task = foundEntry.getValue();
        pendingPrintTasks.add(task); // add the task back to the queue.
        runningPrintTasks.remove(printer);

        stringBuilder = appendString("Task " + task + " removed from printer "
                + printer.getName());

        filterSpoolsList(printer, task);

        return stringBuilder.toString();
    }

    private void filterSpoolsList(Printer printer, PrintTask task) {
        Spool[] spools = printer.getCurrentSpools();

        for(int i=0; i<spools.length && i < task.getColors().size();i++) {
            spools[i].reduceLength(task.getPrint().getFilamentLength().get(i));
        }
        selectPrintTask(printer);
    }

    public String registerCompletion(int printerId) {
        stringBuilder = new StringBuilder();

        Map.Entry<Printer, PrintTask> foundEntry = (Map.Entry<Printer, PrintTask>) filterPrintTasksList(printerId);

        PrintTask task = foundEntry.getValue();
        runningPrintTasks.remove(foundEntry.getKey());

        stringBuilder = appendString("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());

        Printer printer = foundEntry.getKey();
        filterSpoolsList(printer, task);
        return stringBuilder.toString();
    }

    private Object filterPrintTasksList(int printerId) {
        Map.Entry<Printer, PrintTask> foundEntry = null;
        for (Map.Entry<Printer, PrintTask> entry : runningPrintTasks.entrySet()) {
            if (entry.getKey().getId() == printerId) {
                foundEntry = entry;
                break;
            }
        }
        if (foundEntry == null) {
            stringBuilder = appendString(printError("cannot find a running task on printer with ID " + printerId));
            return stringBuilder.toString();
        }
        return foundEntry;
    }

    public Print findPrint(String printName) {
        for (Print p : prints) {
            if (p.getName().equals(printName)) {
                return p;
            }
        }
        return null;
    }

    public Print findPrint(int index) {
        if(index > prints.size() -1) {
            return null;
        }
        return prints.get(index);
    }

    public PrintTask getPrinterCurrentTask(Printer printer) {
        if(!runningPrintTasks.containsKey(printer)) {
            return null;
        }
        return runningPrintTasks.get(printer);
    }

    public ArrayList<PrintTask> getPendingPrintTasks() {
        return pendingPrintTasks;
    }

    public ArrayList<Print> getPrints() {
        return prints;
    }

    public void addPrint(String name, int height, int width, int length, ArrayList<Double> filamentLength, int printTime) {
        Print p = new Print(name, height, width, length, filamentLength, printTime);
        prints.add(p);
    }

    public void startInitialQueue() {
        for(Printer printer: printers) {
            selectPrintTask(printer);
        }
    }

    //TODO: REDO THIS
    //Println
    public String addPrintTask(String printName, List<String> colors, FilamentType type) {
        stringBuilder = new StringBuilder();

        Print print = findPrint(printName);
        if (print == null) {
            stringBuilder = appendString(printError("Could not find print with name " + printName));
            return stringBuilder.toString();
        }
        if (colors.size() == 0) {
            stringBuilder = appendString(printError("Need at least one color, but none given"));
            return stringBuilder.toString();
        }
        for (String color : colors) {
            boolean found = false;
            for (Spool spool : spools) {
                if (spool.getColor().equals(color) && spool.getFilamentType() == type) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                stringBuilder = appendString(printError("Color " + color + " (" + type +") not found"));
                return stringBuilder.toString();
            }
        }

        PrintTask task = new PrintTask(print, colors, type);
        pendingPrintTasks.add(task);

        stringBuilder = appendString("Added task to queue");

        return stringBuilder.toString();
    }

    public String selectPrintTask(Printer printer) {
        stringBuilder = new StringBuilder();
        if (getPrinterCurrentTask(printer) != null) {
            stringBuilder = appendString(printError("Selected printer " + printer + " is busy."));
            return stringBuilder.toString();
        }

        PrintTask chosenTask = null;
        if (printer.getCurrentSpools()[0] != null) {
            // See if there's a task that matches the current spool on the printer.
            for (PrintTask printTask : pendingPrintTasks) {
                if (printer.isValidTask(printTask, true)) {
                    chosenTask = printTask;
                    break;
                }
            }
        }

        if (chosenTask == null) {
            // If we didn't find a print for the current spool we search for a print with the free spools.
            for (PrintTask printTask : pendingPrintTasks) {
                if (printer.isValidTask(printTask, false)) {
                    ArrayList<Spool> newSpools = printTask.getValidSpools(freeSpools);

                    if (newSpools.size() == printTask.getColors().size()) {

                        stringBuilder = appendString(changeSpool(printer, printTask, newSpools));
                        chosenTask = printTask;
                        break;
                    }
                }
            }
        }

        // Load chosen task on printer.
        if (chosenTask != null) loadTask(printer, chosenTask);
        return stringBuilder.toString();
    }

    private String changeSpool(Printer printer, PrintTask printTask, ArrayList<Spool> chosenSpools) {
        stringBuilder = new StringBuilder();
        for (Spool spool : printer.getCurrentSpools()) {
            freeSpools.add(spool);
        }
        printer.setCurrentSpools(chosenSpools);
        int position = 1;

        for (Spool spool : chosenSpools) {
            stringBuilder = appendString("- Spool change: Please place spool " + spool.getId() + " in printer " + printer.getName() + " position " + position);
            freeSpools.remove(spool);
            position++;
        }

        return stringBuilder.toString();
    }

    private String loadTask(Printer printer, PrintTask printTask) {
        stringBuilder = new StringBuilder();

        runningPrintTasks.put(printer, printTask);
        freePrinters.remove(printer);
        pendingPrintTasks.remove(printTask);

        stringBuilder = appendString(scheduleCompletion(printTask.getPrint().getPrintTime(), printer.getId()));

        stringBuilder = appendString("- Started task: " + printTask +
                " on printer " + printer.getName());

        return stringBuilder.toString();
    }

    private String scheduleCompletion(int printTime, int printerId) {
        stringBuilder = new StringBuilder();
        long timeInMs = printTime * 1000;
        Date delay = new Date(System.currentTimeMillis() + timeInMs);
        TimerTask task = new TimerTask() {
            public void run() {
                stringBuilder = appendString("\n\n<-------------------------------------->>");
                stringBuilder = appendString("Printer: " + printerId + " is done printing.");
                registerCompletion(printerId);
                stringBuilder = appendString("Task performed on: " + new Date() + "\n" +
                        "Thread's name: " + Thread.currentThread().getName() + "\n");
                stringBuilder = appendString("<-------------------------------------->>\n\n");

                // Reprint menu prompt.
                Menu.printMenu();
                System.out.print("- Choose an option: ");
            }
        };

        taskTimer.schedule(task, delay);

        return stringBuilder.toString();
    }

    private String printError(String s) {
        stringBuilder = new StringBuilder();
        stringBuilder = appendString("<<---------- Error Message ---------->");
        stringBuilder = appendString("\nError: " + s);
        stringBuilder = appendString("\n<-------------------------------------->>");

        return stringBuilder.toString();
    }

    public String showPrints() {
        stringBuilder = new StringBuilder();

        var prints = getPrints();
        stringBuilder = appendString("<<---------- Available prints ---------->");
        for (var print : prints) {
            stringBuilder = appendString("\n"+ print);
        }
        stringBuilder = appendString("\n<-------------------------------------->>");
        return stringBuilder.toString();
    }

    public String showSpools() {
        stringBuilder = new StringBuilder();

        var spools = getSpools();
        stringBuilder = appendString("<<---------- Spools ---------->");
        for (var spool : spools) {
            stringBuilder = appendString("\n"+ spool);
        }
        stringBuilder = appendString("\n<---------------------------->>");

        return stringBuilder.toString();
    }

    public String showPrinters() {
        stringBuilder = new StringBuilder();

        var printers = getPrinters();
        stringBuilder = appendString("<<--------- Available printers --------->");
        for (var p : printers) {
            String output = p.toString();
            PrintTask currentTask = getPrinterCurrentTask(p);
            if(currentTask != null) {
                output = output.replace("-------->",
                        "- Current Print Task: " + currentTask +
                                System.lineSeparator() + "-------->");
            }
            stringBuilder = appendString("\n"+output);
        }
        stringBuilder = appendString("\n<-------------------------------------->>");

        return stringBuilder.toString();
    }

    public String showPendingPrintTasks() {
        stringBuilder = new StringBuilder();

        ArrayList<PrintTask> printTasks = getPendingPrintTasks();
        stringBuilder = appendString("<<--------- Pending Print Tasks --------->");

        for (var p : printTasks) {
            stringBuilder = appendString("\n"+p);
        }
        stringBuilder = appendString("\n<-------------------------------------->>");

        return stringBuilder.toString();
    }

    private StringBuilder appendString(String s){
        stringBuilder.append(s);
        return stringBuilder;
    }

}