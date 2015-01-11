package at.sbc.firework.gui;

import at.sbc.firework.service.Console;
import at.sbc.firework.service.ServiceFactory;

/**
 * Created by daniel on 11.01.2015.
 */
public class Test {
    public static void main(String[] args) {

         Console.println( ServiceFactory.getDefaultFactoryAddress());

    }
}
