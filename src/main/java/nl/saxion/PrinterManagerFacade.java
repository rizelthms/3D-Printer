package nl.saxion;

import nl.saxion.Models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.Date;
import java.util.TimerTask;

public class PrinterManagerFacade {
    private ArrayList<Printer> printers = new ArrayList<Printer>(); // TODO use interface
    private ArrayList<Spool> spools = new ArrayList<Spool>(); // TODO use interface
    private ArrayList<Spool> freeSpools = new ArrayList<>(); // TODO: Decide if this should be used at all.
    private ArrayList<Printer> freePrinters = new ArrayList<>();
    private ArrayList<Print> prints = new ArrayList<>(); // TODO use interface
    private ArrayList<PrintTask> pendingPrintTasks = new ArrayList<>();
    private HashMap<Printer, PrintTask> runningPrintTasks = new HashMap();
    Timer taskTimer = new Timer("taskTimer");

    public void preload(ArrayList<Print> prints, ArrayList<Printer> printers, ArrayList<Spool> spools) {
        this.prints.addAll(prints);

        this.printers.addAll(printers);
        this.freePrinters.addAll(printers);

        this.spools.addAll(spools);
        this.freeSpools.addAll(spools);
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

    public void registerPrinterFailure(int printerId) {
        Map.Entry<Printer, PrintTask> foundEntry = null;
        for (Map.Entry<Printer, PrintTask> entry : runningPrintTasks.entrySet()) {
            if (entry.getKey().getId() == printerId) {
                foundEntry = entry;
                break;
            }
        }
        if (foundEntry == null) {
            printError("cannot find a running task on printer with ID " + printerId);
            return;
        }
        PrintTask task = foundEntry.getValue();
        pendingPrintTasks.add(task); // add the task back to the queue.
        runningPrintTasks.remove(foundEntry.getKey());

        System.out.println("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());

        Printer printer = foundEntry.getKey();
        Spool[] spools = printer.getCurrentSpools();
        for(int i=0; i<spools.length && i < task.getColors().size();i++) {
            spools[i].reduceLength(task.getPrint().getFilamentLength().get(i));
        }
        selectPrintTask(printer);
    }

    public void registerCompletion(int printerId) {
        Map.Entry<Printer, PrintTask> foundEntry = null;
        for (Map.Entry<Printer, PrintTask> entry : runningPrintTasks.entrySet()) {
            if (entry.getKey().getId() == printerId) {
                foundEntry = entry;
                break;
            }
        }
        if (foundEntry == null) {
            printError("cannot find a running task on printer with ID " + printerId);
            return;
        }
        PrintTask task = foundEntry.getValue();
        runningPrintTasks.remove(foundEntry.getKey());

        System.out.println("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());

        Printer printer = foundEntry.getKey();
        Spool[] spools = printer.getCurrentSpools();

        for(int i = 0; i < spools.length && i < task.getColors().size(); i++) {
            spools[i].reduceLength(task.getPrint().getFilamentLength().get(i));
        }
        selectPrintTask(printer);
    }

    //PrintTaskManager methods
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

    // ----- TODO: REDO THIS
    public void addPrintTask(String printName, List<String> colors, FilamentType type) {
        Print print = findPrint(printName);
        if (print == null) {
            printError("Could not find print with name " + printName);
            return;
        }
        if (colors.size() == 0) {
            printError("Need at least one color, but none given");
            return;
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
                printError("Color " + color + " (" + type +") not found");
                return;
            }
        }

        PrintTask task = new PrintTask(print, colors, type);
        pendingPrintTasks.add(task);
        System.out.println("Added task to queue");

    }

    private void scheduleCompletion(int printTime, int printerId) {
        long timeInMs = printTime * 1000;
        Date delay = new Date(System.currentTimeMillis() + timeInMs);
        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println("\n\n<-------------------------------------->>");
                System.out.println("Printer: " + printerId + " is done printing.");
                registerCompletion(printerId);
                System.out.println("Task performed on: " + new Date() + "\n" +
                        "Thread's name: " + Thread.currentThread().getName() + "\n");

                // Reprint menu prompt.
                Menu.printMenu();
                System.out.print("- Choose an option: ");
            }
        };

        taskTimer.schedule(task, delay);
    }

    private void loadTask(Printer printer, PrintTask printTask) {
        runningPrintTasks.put(printer, printTask);
        freePrinters.remove(printer);
        pendingPrintTasks.remove(printTask);

        scheduleCompletion(printTask.getPrint().getPrintTime(), printer.getId());

        System.out.println("- Started task: " + printTask +
                " on printer " + printer.getName());
        return;
    }

    private void changeSpool(Printer printer, PrintTask printTask, ArrayList<Spool> chosenSpools) {
        for (Spool spool : printer.getCurrentSpools()) {
            freeSpools.add(spool);
        }
        printer.setCurrentSpools(chosenSpools);
        int position = 1;

        for (Spool spool : chosenSpools) {
            System.out.println("- Spool change: Please place spool " + spool.getId() + " in printer " + printer.getName() + " position " + position);
            freeSpools.remove(spool);
            position++;
        }
    }

    /**
     * Iterate through all pending tasks, and find a valid one for the provided printer
     *     with its attached spool.
     * If one is found, load that task.
     * If not, iterate through pending tasks and find a valid one for the printer
     *     with any of the free spools.
     * If one is found, change spools on printer, and load task.
     * If not, exit.
     *
     * @param printer The printer we're finding a task for.
     * @return The first valid print task from pendingPrintTasks.
     */
    public void selectPrintTask(Printer printer) {
        if (getPrinterCurrentTask(printer) != null) {
            System.err.println("Selected printer " + printer + " is busy.");
            return;
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
                        changeSpool(printer, printTask, newSpools);
                        chosenTask = printTask;
                        break;
                    }
                }
            }
        }

        // Load chosen task on printer.
        if (chosenTask != null) loadTask(printer, chosenTask);
    }

    public void printError(String s) {
        System.out.println("<<---------- Error Message ---------->");
        System.out.println("Error: " + s);
        System.out.println("<-------------------------------------->>");
    }

}