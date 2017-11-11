import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.dispatcher.SwingDispatchService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static TextFrame textFrame;

    public static void main(String[] args) {
        //SwingUtilities.invokeLater(new Runnable() {
        //    public void run() {
                Settings.load();
                textFrame = new TextFrame();
                initializeNativeHook();
                Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.WARNING);
        //    }
        //});
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