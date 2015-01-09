package at.sbc.firework.service;

/**
 * Created by daniel on 07.01.2015.
 */
public class Console {

    public static boolean OutputEnabled = true;

    public static void print(Object s) {
        if (OutputEnabled)
            System.out.print(s);
    }

    public static void println(Object s) {
        if (OutputEnabled)
            System.out.println(s);
    }
}
