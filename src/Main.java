import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.dispatcher.SwingDispatchService;

import javax.swing.*;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static TextFrame textFrame;

    public static boolean debug;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--debug")) {
            debug = true;
        } else {
            debug = false;
        }
        try {
            RandomAccessFile lockFile = new RandomAccessFile(Settings.RES_PATH + ".lock", "rw");
            if (lockFile.getChannel().tryLock() == null) {
                System.out.println("Another instance of scribo is already running. Quitting...");
                System.exit(0);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            Settings.load();
            textFrame = new TextFrame();
            initializeNativeHook();
            Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.WARNING);
        });
    }

    public static void initializeNativeHook() {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        GlobalScreen.setEventDispatcher(new SwingDispatchService());
        GlobalScreen.addNativeKeyListener(textFrame);
    }

    public static void exit() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        System.runFinalization();
        System.exit(0);
    }

}