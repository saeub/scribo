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

    public static final HashMap<String, Integer> KEY_MAP;
    static {
        KEY_MAP = new HashMap<>();
        KEY_MAP.put("Ctrl", NativeKeyEvent.VC_CONTROL);
        KEY_MAP.put("Alt", NativeKeyEvent.VC_ALT);
        KEY_MAP.put("Shift", NativeKeyEvent.VC_SHIFT);
        KEY_MAP.put("Meta", NativeKeyEvent.VC_META);
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
    private String activationKeyString;
    private int activationKey;
    private int activationKeyPresses;
    private Font scriptFont, keyFont;

    public Settings(String scriptFileName, String activationKeyString, int activationKeyPresses) {
        setScript(scriptFileName);
        setActivationKey(activationKeyString);
        setActivationKeyPresses(activationKeyPresses);
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

    private void setActivationKey(String keyString) {
        activationKeyString = keyString;
        activationKey = KEY_MAP.get(keyString);
    }

    public static void setActiveActivationKey(String keyString) {
        activeSettings.setActivationKey(keyString);
    }

    private void setActivationKeyPresses(int keyPresses) {
        activationKeyPresses = keyPresses;
    }

    public static void setActiveActivationKeyPresses(int keyPresses) {
        activeSettings.setActivationKeyPresses(keyPresses);
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

    public static int getActiveActivationKey() {
        return activeSettings.activationKey;
    }

    public String getActivationKeyString() {
        return activationKeyString;
    }

    public static String getActiveActivationKeyString() {
        return activeSettings.activationKeyString;
    }

    public int getActivationKeyPresses() {
        return activationKeyPresses;
    }

    public static int getActiveActivationKeyPresses() {
        return activeSettings.activationKeyPresses;
    }

    public static Font getActiveScriptFont() {
        return activeSettings.scriptFont;
    }

    public static Font getActiveKeyFont() {
        return activeSettings.keyFont;
    }

}
