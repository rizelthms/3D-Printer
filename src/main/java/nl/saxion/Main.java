package nl.saxion;

import nl.saxion.Models.*;
import nl.saxion.facade.PrinterFacade;
import java.util.*;

public class Main {
    PrinterFacade facade = new PrinterFacade();
    Menu menu = new Menu();
    Scanner scanner = new Scanner(System.in);
    private String printStrategy = "Less Spool Changes";

    // Run run() method
    public static void main(String[] args) {
        new Main().run(args);
    }

    // Read data and loop menu options
    public void run(String[] args) {
        if(args.length > 0) {
            Inputs.loadPrintsFromFile(args[0]);
            Inputs.loadSpoolsFromFile(args[1]);
            Inputs.loadPrintersFromFile(args[2]);
        } else {
            Inputs.loadPrintsFromFile("");
            Inputs.loadSpoolsFromFile("");
            Inputs.loadPrintersFromFile("");
        }

        menu.menuSwitch();
    }

    // Start printer queue
    public void startPrintQueue() {
        System.out.println("<<---------- Starting Print Queue ---------->");
        facade.startInitialQueue();
        System.out.println("<----------------------------------->>");
    }

    // Exit (does nothing)
    private void exit() {

    }

    // This method only changes the name but does not actually work.
    // It exists to demonstrate the output.
    // in the future strategy might be added.
    public void changePrintStrategy() {
        System.out.println("<<---------- Change Strategy ------------->");
        System.out.println("- Current strategy: " + printStrategy);
        System.out.println("- 1: Less Spool Changes");
        System.out.println("- 2: Efficient Spool Usage");
        System.out.println("- Choose strategy: ");
        int strategyChoice = Inputs.numberInput(1, 2);
        if(strategyChoice == 1) {
            printStrategy = "- Less Spool Changes";
        } else if( strategyChoice == 2) {
            printStrategy = "- Efficient Spool Usage";
        }
        System.out.println("<----------------------------------->>");
    }

    // TODO: This should be based on which printer is finished printing.
    public void registerPrintCompletion() {
        ArrayList<Printer> printers = facade.getPrinters();
        System.out.println("<<---------- Currently Running Printers ---------->");
        for(Printer p: printers) {
            PrintTask printerCurrentTask= facade.getPrinterCurrentTask(p);
            if(printerCurrentTask != null) {
                System.out.println("- " + p.getId() + ": " +p.getName() + " - " + printerCurrentTask);
            }
        }
        System.out.print("- Printer that is done (ID): ");
        int printerId = Inputs.numberInput(-1, printers.size());
        System.out.println("<----------------------------------->>");
        facade.registerCompletion(printerId);
    }

    public void registerPrinterFailure() {
        ArrayList<Printer> printers = facade.getPrinters();
        System.out.println("<<---------- Currently Running Printers ---------->");
        for(Printer p: printers) {
            PrintTask printerCurrentTask= facade.getPrinterCurrentTask(p);
            if(printerCurrentTask != null) {
                System.out.println("- " + p.getId() + ": " +p.getName() + " > " + printerCurrentTask);
            }
        }
        System.out.print("- Printer ID that failed: ");
        int printerId = Inputs.numberInput(1, printers.size());

        facade.registerPrinterFailure(printerId);
        System.out.println("<----------------------------------->>");
    }

    public void addNewPrintTask() {
        List<String> colors = new ArrayList<>();
        var prints = facade.getPrints();
        System.out.println("<<---------- New Print Task ---------->");
        System.out.println("<---------- Available prints ----------");
        int counter = 1;
        for (var p : prints) {
            System.out.println("- " + counter + ": " + p.getName());
            counter++;
        }

        System.out.print("- Print number: ");
        int printNumber = Inputs.numberInput(1, prints.size());
        System.out.println("-------------------------------------->");
        Print print = facade.findPrint(printNumber - 1);
        String printName = print.getName();
        System.out.println("<---------- Filament Type ----------");
        System.out.println("- 1: PLA");
        System.out.println("- 2: PETG");
        System.out.println("- 3: ABS");
        System.out.print("- Filament type number: ");
        int ftype = Inputs.numberInput(1, 3);
        System.out.println("-------------------------------------->");
        FilamentType type;
        switch (ftype) {
            case 1 -> type = FilamentType.PLA;
            case 2 -> type = FilamentType.PETG;
            case 3 -> type = FilamentType.ABS;
            default -> {
                System.out.println("- Not a valid filamentType, bailing out");
                return;
            }
        }
        var spools = facade.getSpools();
        System.out.println("<---------- Colors ----------");
        ArrayList<String> availableColors = new ArrayList<>();
        counter = 1;
        for (var spool : spools) {
            String colorString = spool.getColor();
            if(type == spool.getFilamentType() && !availableColors.contains(colorString)) {
                System.out.println("- " + counter + ": " + colorString + " (" + spool.getFilamentType() + ")");
                availableColors.add(colorString);
                counter++;
            }
        }
        System.out.print("- Color number: ");
        int colorChoice = Inputs.numberInput(1, availableColors.size());
        colors.add(availableColors.get(colorChoice-1));
        for(int i = 1; i < print.getFilamentLength().size(); i++) {
            System.out.print("- Color number: ");
            colorChoice = Inputs.numberInput(1, availableColors.size());
            colors.add(availableColors.get(colorChoice-1));
        }
        System.out.println("-------------------------------------->");

        facade.addPrintTask(printName, colors, type);
        System.out.println("<---------------------------->>");
    }
}