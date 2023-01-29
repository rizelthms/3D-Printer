package nl.saxion;

import nl.saxion.Models.*;
import nl.saxion.strategy.*;

import java.util.*;

public class PrinterManagerFacade {
    private ArrayList<Printer> printers = new ArrayList<>();
    private ArrayList<Spool> spools = new ArrayList<>();
    private ArrayList<Spool> freeSpools = new ArrayList<>();
    private ArrayList<Printer> freePrinters = new ArrayList<>();
    private ArrayList<Print> prints = new ArrayList<>();
    private ArrayList<PrintTask> pendingPrintTasks = new ArrayList<>();
    private HashMap<Printer, PrintTask> runningPrintTasks = new HashMap();
    Timer taskTimer = new Timer("taskTimer");
    private PrintStrategy printStrategy = new LessSpoolChangesStrategy();

    public void preload(ArrayList<Print> prints, ArrayList<Printer> printers, ArrayList<Spool> spools) {
        this.prints.addAll(prints);

        this.spools.addAll(spools);
        this.freeSpools.addAll(spools);

        this.printers.addAll(printers);
        this.freePrinters.addAll(printers);
    }


    public ArrayList<Print> getPrints() {
        return prints;
    }

    public ArrayList<Spool> getSpools() {
        return spools;
    }

    public ArrayList<Spool> getFreeSpools() {
        return freeSpools;
    }

    public ArrayList<Printer> getPrinters() {
        return printers;
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


    public void startInitialQueue() {
        for(Printer printer: printers) {
            selectPrintTask(printer);
        }
    }

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
            if (!found) { // Consider alternate implementation
                printError("Color " + color + " (" + type +") not found");
                return;
            }
        }

        PrintTask task = new PrintTask(print, colors, type);
        pendingPrintTasks.add(task);
        System.out.println("Added task to queue");

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
     */
    public void selectPrintTask(Printer printer) {
        if (getPrinterCurrentTask(printer) != null) {
            System.err.println("Selected printer " + printer + " is busy.");
            return;
        }

        PrintTask chosenTask = findMatchingTask(printer);
        if (chosenTask == null) {
            chosenTask = printStrategy.doSpoolSelection(this, printer);
        }

        if (chosenTask != null) {
            loadTask(printer, chosenTask);
        }
    }

    private PrintTask findMatchingTask(Printer printer) {
        if (printer.getCurrentSpools()[0] == null) {
            return null;
        }

        for (PrintTask printTask : pendingPrintTasks) {
            if (printer.isValidTask(printTask, true)) {
                return printTask;
            }
        }

        return null;
    }

    public void selectStrategy(StrategyOptions strategyOption) {
        switch (strategyOption) {
            case LessSpoolChange -> printStrategy = new LessSpoolChangesStrategy();
            case EfficientSpoolUsageStrategy -> printStrategy = new EfficientSpoolUsageStrategy();
            default -> {
            }
        }
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
                System.out.println("<-------------------------------------->>\n\n");

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
    }

    public void changeSpool(Printer printer, PrintTask printTask, ArrayList<Spool> chosenSpools) {
        Collections.addAll(freeSpools, printer.getCurrentSpools());
        printer.setCurrentSpools(chosenSpools);

        for (int i = 0; i < chosenSpools.size(); i++) {
            System.out.println("- Spool change: Please place spool " + chosenSpools.get(i).getId() + " in printer " + printer.getName() + " position " + (i + 1));
        }

        freeSpools.removeAll(chosenSpools);
    }

    public void printError(String s) {
        System.out.println("<<---------- Error Message ---------->");
        System.out.println("Error: " + s);
        System.out.println("<-------------------------------------->>");
    }

}