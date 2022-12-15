package nl.saxion;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {

    Scanner scanner = new Scanner(System.in);

    Main main;

    //Call this method from the Main class
    public void menuSwitch(){
        int choice = 1; //Why 1?

        //Refactor this code with switch statement

        while (choice > 0 && choice < 10) {
            printMenu();
            choice = menuChoice(9);
            System.out.println("----------------------------------->>");
            if (choice == 1) {
                main.addNewPrintTask();
            } else if (choice == 2) {
                main.registerPrintCompletion();
            } else if (choice == 3) {
                main.registerPrinterFailure();
            } else if (choice == 4) {
                main.changePrintStrategy();
            } else if (choice == 5) {
                main.startPrintQueue();
            } else if (choice == 6) {
                main.showPrints();
            } else if (choice == 7) {
                main.showPrinters();
            } else if (choice == 8) {
                main.showSpools();
            } else if (choice == 9) {
                main.showPendingPrintTasks();
            }
        }
        exit();
    }

    public void printMenu() {
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

    public int menuChoice(int max) {
        int choice = -1;
        while (choice < 0 || choice > max) {
            System.out.print("- Choose an option: ");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                //try again after consuming the current line
                System.out.println("- Error: Invalid input");
                scanner.nextLine();
            }
        }
        return choice;
    }

//    //Unused method
//    public int numberInput() {
//        int input = scanner.nextInt();
//        return input;
//    }
//
//    //Unused method
//    public int numberInput(int min, int max) {
//        int input = numberInput();
//        while (input < min || input > max) {
//            input = numberInput();
//        }
//        return input;
//    }

    private void exit() {
        System.exit(0);
    }

}
