package nl.saxion;

import nl.saxion.Models.*;
import nl.saxion.strategy.EfficientSpoolUsageStrategy;
import nl.saxion.strategy.LessSpoolChangesStrategy;
import nl.saxion.strategy.PrintStrategy;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

/*
 * This class contains the menu and menu options for a system.
 * It allows the user to choose different actions to perform, such as adding new print tasks or viewing pending print tasks.
 */
public class Menu {
    PrinterManagerFacade facade;
    PrinterManager manager;
    FileReader fileReader;

    Scanner scanner = new Scanner(System.in);
    private PrintStrategy printStrategy = new LessSpoolChangesStrategy();

    public Menu(PrinterManagerFacade facade, PrinterManager manager, FileReader fileReader) {
        this.facade = facade;
        this.manager = manager;
        this.fileReader = fileReader;
    }

    public void start() {
        menuSwitch();
    }

    // Call this method from the Main class to display the menu and handle user input.
    public void menuSwitch() {
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
                case ShowPrints -> manager.showPrints();
                case ShowPrinters -> manager.showPrinters();
                case ShowSpools -> manager.showSpools();
                case ShowPendingPrintTasks -> manager.showPendingPrintTasks();
                case InvalidOption -> exit();
                default -> {
                    System.out.println("no existing orders"); // in the menuchoice we make a nextline which will recover anything not only a int between 0 and 9 so we restart menuswitch until we have a correct value.
                    menuSwitch();
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
        var prints = manager.getPrints();
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
        Print print = manager.findPrint(printNumber - 1);
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
        var spools = manager.getSpools();
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

        manager.addPrintTask(printName, colors, type);
        System.out.println("<---------------------------->>");
    }

    // TODO: This should be based on which printer is finished printing.
    public void registerPrintCompletion() {
        ArrayList<Printer> printers = manager.getPrinters();
        System.out.println("<<---------- Currently Running Printers ---------->");
        manager.showPrinters();

        System.out.print("- Printer that is done (ID): ");
        int printerId = Inputs.numberInput(-1, printers.size());
        System.out.println("<----------------------------------->>");

        manager.registerCompletion(printerId);
        System.out.println("Task for Printer: " + printerId + " marked as complete.");
        System.out.println("<----------------------------------->>");
    }

    public void registerPrinterFailure() {
        ArrayList<Printer> printers = manager.getPrinters();
        System.out.println("<<---------- Currently Running Printers ---------->");
        manager.showPrinters();
        System.out.print("- Printer ID that failed: ");
        int printerId = Inputs.numberInput(1, printers.size());
        System.out.println("<----------------------------------->>");

        manager.registerPrinterFailure(printerId);
        System.out.println("Task for Printer: " + printerId + " marked as failed.");
        System.out.println("<----------------------------------->>");
    }

    //Strategy class has been added
    public void changePrintStrategy() {
        System.out.println("<<---------- Change Strategy ------------->");
        System.out.println("- Current strategy: " + printStrategy.getName());

        System.out.println("- 1: " + new LessSpoolChangesStrategy().getName());
        System.out.println("- 2: " + new EfficientSpoolUsageStrategy().getName());
        System.out.println("- Choose strategy: ");
        int strategyChoice = Inputs.numberInput(1, 2);

        PrintStrategy strategy;
        switch(strategyChoice) {
            case 1:
                if(!(printStrategy instanceof LessSpoolChangesStrategy))
                    strategy = new LessSpoolChangesStrategy();
                else {
                    System.out.println("Strategy is already set to Less Spool Changes.");
                    return;
                }
                break;
            case 2:
                if(!(printStrategy instanceof EfficientSpoolUsageStrategy))
                    strategy = new EfficientSpoolUsageStrategy();
                else {
                    System.out.println("Strategy is already set to Efficient Spool Usage.");
                    return;
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid strategy choice");
        }

        printStrategy = strategy;
        //Next line needs to be adjusted and should pass parameters for the strategy to change
        printStrategy.executeStrategy();
        System.out.println("<----------------------------------->>");
    }

    // Start printer queue
    public void startPrintQueue() {
        System.out.println("<<---------- Starting Print Queue ---------->");
        manager.startInitialQueue();
        System.out.println("<----------------------------------->>");
    }
}