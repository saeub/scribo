import org.jnativehook.keyboard.NativeKeyEvent;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Settings {

    public static final HashMap<String, Integer> CONTROL_KEY_MAP;
    public static final HashMap<String, Integer> KEY_MAP;
    static {
        CONTROL_KEY_MAP = new HashMap<>();
        CONTROL_KEY_MAP.put("Ctrl", NativeKeyEvent.VC_CONTROL);
        CONTROL_KEY_MAP.put("Alt", NativeKeyEvent.VC_ALT);
        CONTROL_KEY_MAP.put("Shift", NativeKeyEvent.VC_SHIFT);

        KEY_MAP = new HashMap<>();
        KEY_MAP.put("1", NativeKeyEvent.VC_1);
        KEY_MAP.put("2", NativeKeyEvent.VC_2);
        KEY_MAP.put("3", NativeKeyEvent.VC_3);
        KEY_MAP.put("4", NativeKeyEvent.VC_4);
        KEY_MAP.put("5", NativeKeyEvent.VC_5);
        KEY_MAP.put("6", NativeKeyEvent.VC_6);
        KEY_MAP.put("7", NativeKeyEvent.VC_7);
        KEY_MAP.put("8", NativeKeyEvent.VC_8);
        KEY_MAP.put("9", NativeKeyEvent.VC_9);
        KEY_MAP.put("0", NativeKeyEvent.VC_0);
        KEY_MAP.put("F1", NativeKeyEvent.VC_F1);
        KEY_MAP.put("F2", NativeKeyEvent.VC_F2);
        KEY_MAP.put("F3", NativeKeyEvent.VC_F3);
        KEY_MAP.put("F4", NativeKeyEvent.VC_F4);
        KEY_MAP.put("F5", NativeKeyEvent.VC_F5);
        KEY_MAP.put("F6", NativeKeyEvent.VC_F6);
        KEY_MAP.put("F7", NativeKeyEvent.VC_F7);
        KEY_MAP.put("F8", NativeKeyEvent.VC_F8);
        KEY_MAP.put("F9", NativeKeyEvent.VC_F9);
        KEY_MAP.put("F10", NativeKeyEvent.VC_F10);
        KEY_MAP.put("F11", NativeKeyEvent.VC_F11);
        KEY_MAP.put("F12", NativeKeyEvent.VC_F12);
    }

    public static final String RES_PATH;
    static {
        if (Main.debug) {
            RES_PATH = "res/";
        } else {
            String path;
            try {
                // directory containing JAR
                File baseFile = new File(Settings.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                path = baseFile.getParent() + "/res/";
            } catch (URISyntaxException e) {
                e.printStackTrace();
                // use working directory (equals directory containing JAR in most cases)
                path = "res/";
            }
            RES_PATH = path;
        }
    }

    private static final String FILE_NAME = RES_PATH + ".settings";

    private static Settings activeSettings;

    private String scriptFileName;
    private Script script;
    private String activationControlKeyString;
    private int activationControlKey;
    private String activationKeyString;
    private int activationKey;
    private Font scriptFont, keyFont;

    public Settings(String scriptFileName, String activationControlKeyString, String activationKeyString) {
        setScript(scriptFileName);
        setActivationControlKey(activationControlKeyString);
        setActivationKey(activationKeyString);
        try {
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT,
                    new File(RES_PATH + "GentiumPlus-R.ttf")));
            scriptFont = new Font("Gentium Plus", Font.BOLD, 20);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            scriptFont = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
        }
        scriptFont = new Font(Font.SERIF, Font.PLAIN, 20);
        keyFont = new Font(Font.MONOSPACED, Font.BOLD, 15);
    }

    public static void load() {
        try {
            SettingsParser parser = new SettingsParser(FILE_NAME);
            activeSettings = parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            SettingsParser parser = new SettingsParser(FILE_NAME);
            parser.save(activeSettings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActivationControlKey(String keyString) {
        activationControlKeyString = keyString;
        activationControlKey = CONTROL_KEY_MAP.get(keyString);
    }

    public static void setActiveActivationControlKey(String keyString) {
        activeSettings.setActivationControlKey(keyString);
    }

    private void setActivationKey(String keyString) {
        activationKeyString = keyString;
        activationKey = KEY_MAP.get(keyString);
    }

    public static void setActiveActivationKey(String keyString) {
        activeSettings.setActivationKey(keyString);
    }

    private void setScript(String fileName) {
        scriptFileName = fileName;
        ScriptParser parser = new ScriptParser(scriptFileName);
        try {
            script = parser.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setActiveScript(String fileName) {
        activeSettings.setScript(fileName);
    }

    public static String[] getAvailableScriptFileNames() {
        ArrayList<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(Settings.RES_PATH))) {
            for (Path entry: stream) {
                String fileName = entry.getFileName().toString();
                if (fileName.endsWith(".chars")) {
                    fileNames.add(fileName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileNames.toArray(new String[0]);
    }

    public static Script getActiveScript() {
        return activeSettings.script;
    }

    public String getScriptFileName() {
        return scriptFileName;
    }

    public static String getActiveScriptFileName() {
        return activeSettings.scriptFileName;
    }

    public static int getActiveActivationControlKey() {
        return activeSettings.activationControlKey;
    }

    public static int getActiveActivationKey() {
        return activeSettings.activationKey;
    }

    public String getActivationControlKeyString() {
        return activationControlKeyString;
    }

    public static String getActiveActivationControlKeyString() {
        return activeSettings.activationControlKeyString;
    }

    public String getActivationKeyString() {
        return activationKeyString;
    }

    public static String getActiveActivationKeyString() {
        return activeSettings.activationKeyString;
    }

    public static Font getActiveScriptFont() {
        return activeSettings.scriptFont;
    }

    public static Font getActiveKeyFont() {
        return activeSettings.keyFont;
    }

}
