package nl.saxion;

import java.util.InputMismatchException;
import java.util.Scanner;

/*
 * This class contains the menu and menu options for a system.
 * It allows the user to choose different actions to perform, such as adding new print tasks or viewing pending print tasks.
 */


public class Menu {
    Scanner scanner = new Scanner(System.in);
    Main main;

    // Call this method from the Main class to display the menu and handle user input.
    public void menuSwitch(){

        main = new Main();
        int choice = 1; //Why 1?  1 because 0 is for exit function so if we set choice to 0 when created we will never enter the while loop.

        //Refactor this code with switch statement
        while (choice > 0 && choice < 10) {
            printMenu();
            choice = menuChoice(9); // add a while loop that as long as choice are not between 0 and 10 choice = menuChoice(9) is not optimized because the default case actually check if choice is ok. If no case are equals to choice then we do something. no unnecessary calculation
            System.out.println("----------------------------------->>");
            switch(choice) { // switch refactor is better than a lot of if else if because if else check all the conditions, switch just goes to the case which are the correct value and i add a default if choice are not in the current choice.
                case 1:
                    main.addNewPrintTask();
                    break;
                case 2:
                    main.registerPrintCompletion();
                    break;
                case 3:
                    main.registerPrinterFailure();
                    break;
                case 4:
                    main.changePrintStrategy();
                    break;
                case 5:
                    main.startPrintQueue();
                    break;
                case 6:
                    main.showPrints();
                    break;
                case 7:
                    main.showPrinters();
                    break;
                case 8:
                    main.showSpools();
                    break;
                case 9:
                    main.showPendingPrintTasks();
                    break;
                case 0:
                    exit();
                    break;
                default:
                    System.out.println("no existing orders"); // in the menuchoice we make a nextline which will recover anything not only a int between 0 and 9 so we restart menuswitch until we have a correct value.
                    menuSwitch();
            }
        }
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


    // Terminate the program.
    private void exit() {
        System.exit(0);
    }

}
