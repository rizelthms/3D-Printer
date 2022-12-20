package nl.saxion.Models;

public enum MainMenuOptions {
        InvalidOption(0),
        AddNewPrintTask(1),
        RegisterPrintCompletion(2),
        RegisterPrinterFailure(3),
        ChangePrintStrategy(4),
        StartPrintQueue(5),
        ShowPrints(6),
        ShowPrinters(7),
        ShowSpools(8),
        ShowPendingPrintTasks(9);

        private int option;

        MainMenuOptions (int option) {
            this.option = option;
        }
    }
