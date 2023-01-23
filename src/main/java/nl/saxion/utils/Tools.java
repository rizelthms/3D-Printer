package nl.saxion.utils;

import nl.saxion.Models.*;
import java.util.List;

public class Tools {
    public static boolean containsSpool(final List<Spool> list, final String name) {
        return list.stream().anyMatch(o -> o.getColor().equals(name));
    }
}