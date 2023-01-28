package nl.saxion.utils;

import nl.saxion.Models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class Tools {
    public static boolean containsSpool(final List<Spool> list, final String name) {
        return list.stream().anyMatch(o -> o.getColor().equals(name));
    }

    public static ArrayList<Spool> sortSpoolsByLength(ArrayList<Spool> list) {
        Collections.sort(list, new Comparator<Spool>() {
            public int compare(Spool s1, Spool s2) {
                // compare two instances of `Spool` and return `int` as result.
                return Double.compare(s1.getLength(), s2.getLength());
            }
        });

        return list;
    }

    public static ArrayList<PrintTask> sortTasksByFilamentLength(ArrayList<PrintTask> list) {
        Collections.sort(list, new Comparator<PrintTask>() {
            public int compare(PrintTask s1, PrintTask s2) {
                // compare two instances of `PrintTask` and return `int` as result.
                return Double.compare(
                        Collections.max(s1.getPrint().getFilamentLength()),
                        Collections.max(s2.getPrint().getFilamentLength())
                );
            }
        });

        return list;
    }
}

