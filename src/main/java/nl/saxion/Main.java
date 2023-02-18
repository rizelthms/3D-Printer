package nl.saxion;

public class Main {
    Menu menu = new Menu();
    PrinterManagerFacade facade = new PrinterManagerFacade();

    // Run run() method
    public static void main(String[] args) {
        new Main().run(args);
    }

    // Read data and loop menu options
    public void run(String[] args) {
        String printsFile = "";
        String printersFile = "";
        String spoolsFile = "";

        if (args.length > 0) {
            printsFile = args[0];
            spoolsFile = args[1];
            printersFile = args[2];
        }
        facade.preload(printsFile, printersFile, spoolsFile);
        menu.menuSwitch(facade);
    }

}
