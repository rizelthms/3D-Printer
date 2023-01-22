package nl.saxion;

import java.util.*;


public class Main {
    Menu menu = new Menu();
    PrinterManager manager = new PrinterManager();
    Scanner scanner = new Scanner(System.in);

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
        manager.preload(printsFile, printersFile, spoolsFile);
        menu.menuSwitch(manager);
    }

    // Exit (does nothing)
    private void exit() {}
}