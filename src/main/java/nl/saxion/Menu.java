package nl.saxion;

import nl.saxion.Models.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

/*
 * This class contains the menu and menu options for a system.
 * It allows the user to choose different actions to perform, such as adding new print tasks or viewing pending print tasks.
 */
public class Menu {
    Scanner scanner = new Scanner(System.in);
    PrinterManagerFacade facade = null;
    private String printStrategy = "Less Spool Changes";

    // Call this method from the Main class to display the menu and handle user input.
    public void menuSwitch(PrinterManagerFacade printerManagerFacade) {
        this.facade = printerManagerFacade;
        int choice = 1;

        while (choice > 0 && choice < 10) {
            printMenu();
            choice = menuChoice(9);
            MainMenuOptions chosenOption = MainMenuOptions.values()[choice];
            System.out.println("----------------------------------->>");

            switch (chosenOption) { // switch refactor is better than a lot of if else if because if else check all the conditions, switch just goes to the case which are the correct value and i add a default if choice are not in the current choice.
                case AddNewPrintTask -> addNewPrintTask();
                case RegisterPrintCompletion -> registerPrintCompletion();
                case RegisterPrinterFailure -> registerPrinterFailure();
                case ChangePrintStrategy -> changePrintStrategy();
                case StartPrintQueue -> startPrintQueue();
                case ShowPrints -> facade.showPrints();
                case ShowPrinters -> facade.showPrinters();
                case ShowSpools -> facade.showSpools();
                case ShowPendingPrintTasks -> facade.showPendingPrintTasks();
                case InvalidOption -> exit();
                default -> {
                    System.out.println("no existing orders"); // in the menuchoice we make a nextline which will recover anything not only a int between 0 and 9 so we restart menuswitch until we have a correct value.
                    menuSwitch(printerManagerFacade);
                }
            }
        }
    }

    public static void printMenu() {
        System.out.println("<<------------- Menu ----------------");
        System.out.println("- 1) Add new Print Task");
        System.out.println("- 2) Register Printer Completion");
        System.out.println("- 3) Register Printer Failure");
        System.out.println("- 4) Change printing style");
        System.out.println("- 5) Start Print Queue");
        System.out.println("- 6) Show prints");
        System.out.println("- 7) Show printers");
        System.out.println("- 8) Show spools");
        System.out.println("- 9) Show pending print tasks");
        System.out.println("- 0) Exit");
    }

    // Prompt the user to choose an option from the menu and return their selection.
    // max: the highest valid menu option (used to validate user input).
    public int menuChoice(int max) {
        // Read an integer from the user and return it.
        // If the user enters invalid input (e.g. a non-integer value), display an error message and repeat the prompt until valid input is received.
        int choice = -1;
        while (choice < 0 || choice > max) {
            System.out.print("- Choose an option: ");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                // Consume the current line and try again
                System.out.println("- Error: Invalid input");
                scanner.nextLine();
            }
        }
        return choice;
    }

    // Terminate the program.
    private void exit() {
        System.exit(0);
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

    // TODO: This should be based on which printer is finished printing.
    public void registerPrintCompletion() {
        ArrayList<Printer> printers = facade.getPrinters();
        System.out.println("<<---------- Currently Running Printers ---------->");
        facade.showPrinters();

        System.out.print("- Printer that is done (ID): ");
        int printerId = Inputs.numberInput(-1, printers.size());
        System.out.println("<----------------------------------->>");

        facade.registerCompletion(printerId);
        System.out.println("Task for Printer: " + printerId + " marked as complete.");
        System.out.println("<----------------------------------->>");
    }

    public void registerPrinterFailure() {
        ArrayList<Printer> printers = facade.getPrinters();
        System.out.println("<<---------- Currently Running Printers ---------->");
        facade.showPrinters();
        System.out.print("- Printer ID that failed: ");
        int printerId = Inputs.numberInput(1, printers.size());
        System.out.println("<----------------------------------->>");

        facade.registerPrinterFailure(printerId);
        System.out.println("Task for Printer: " + printerId + " marked as failed.");
        System.out.println("<----------------------------------->>");
    }

    /** Method to change the print strategy.
     * The selectStrategy function from PrinterManager called via the facade
     * swaps the strategy instance it holds.
     */
    public void changePrintStrategy() {
        System.out.println("<<---------- Change Strategy ------------->");
        System.out.println("- Current strategy: " + printStrategy);
        System.out.println("- 1: Less Spool Changes");
        System.out.println("- 2: Efficient Spool Usage");
        System.out.println("- Choose strategy: ");
        int strategyChoice = Inputs.numberInput(1, 2);

        StrategyOptions strategyOption = StrategyOptions.values()[strategyChoice];
        facade.selectStrategy(strategyOption);
        System.out.println("<----------------------------------->>");
    }

    // Start printer queue
    public void startPrintQueue() {
        System.out.println("<<---------- Starting Print Queue ---------->");
        facade.startInitialQueue();
        System.out.println("<----------------------------------->>");
    }
}