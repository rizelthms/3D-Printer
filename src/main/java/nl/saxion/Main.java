package nl.saxion;

import nl.saxion.facade.PrinterFacade;

public class Main {

    PrinterFacade facade;
    PrinterManager manager;
    FileReader fileReader = new FileReader();

    public static void main(String[] args) {
        new Main().run(args);
    }

    //Find filename and start the menu
    public void run(String[] args) {
        String printsFile = "";
        String spoolsFile = "";
        String printersFile = "";

        if (args.length > 0) {
            printsFile = args[0];
            spoolsFile = args[1];
            printersFile = args[2];
        }

        fileReader.readFile(printsFile, spoolsFile, printersFile);

        facade = new PrinterFacade();
        manager = new PrinterManager(facade);

        //Start Menu
        Menu menu = new Menu(facade,manager);
        menu.start();
    }
}