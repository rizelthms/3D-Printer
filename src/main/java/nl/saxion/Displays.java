package nl.saxion;

import nl.saxion.Models.*;
import nl.saxion.facade.PrinterFacade;
import java.util.*;

public class Displays {
     static PrinterFacade facade = new PrinterFacade();

        public static void showPrints() {
            var prints = facade.getPrints();
            System.out.println("<<---------- Available prints ---------->");
            for (var p : prints) {
                System.out.println(p);
            }
            System.out.println("<-------------------------------------->>");
        }

        public static void showSpools() {
            var spools = facade.getSpools();
            System.out.println("<<---------- Spools ---------->");
            for (var spool : spools) {
                System.out.println(spool);
            }
            System.out.println("<---------------------------->>");
        }

        public static void showPrinters() {
            var printers = facade.getPrinters();
            System.out.println("<<--------- Available printers --------->");
            for (var p : printers) {
                String output = p.toString();
                PrintTask currentTask = facade.getPrinterCurrentTask(p);
                if(currentTask != null) {
                    output = output.replace("-------->", "- Current Print Task: " + currentTask + System.lineSeparator() +
                            "-------->");
                }
                System.out.println(output);
            }
            System.out.println("<-------------------------------------->>");
        }

        public static void showPendingPrintTasks() {
            ArrayList<PrintTask> printTasks = facade.getPendingPrintTasks();
            System.out.println("<<--------- Pending Print Tasks --------->");

            for (var p : printTasks) {
                System.out.println(p);
            }
            System.out.println("<-------------------------------------->>");
        }
    }
