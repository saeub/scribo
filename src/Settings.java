import org.jnativehook.keyboard.NativeKeyEvent;

import java.awt.*;
import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Settings {

    public static final HashMap<String, Integer> CONTROL_KEY_MAP;
    public static final HashMap<String, Integer> KEY_MAP;

    private static final String FILE_NAME = "res/.settings";
    private static final String SCRIPT_KEY = "script";
    private static final String ACTIVATION_CONTROL_KEY_KEY = "activationControlKey";
    private static final String ACTIVATION_KEY_KEY = "activationKey";

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

    private static String scriptFileName;
    private static Script script;
    private static String activationControlKeyString;
    private static int activationControlKey;
    private static String activationKeyString;
    private static int activationKey;
    private static Font scriptFont, keyFont;

    public static void load() {
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(FILE_NAME), "UTF-8");
            parse(reader);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("res/GentiumPlus-R.ttf")));
            scriptFont = new Font("Gentium Plus", Font.BOLD, 20);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            scriptFont = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
        }*/
        scriptFont = new Font(Font.SERIF, Font.PLAIN, 20);
        keyFont = new Font(Font.MONOSPACED, Font.BOLD, 15);
    }

    private static void parse(InputStreamReader reader) throws IOException {
        final int KEY = 0;
        final int VALUE = 1;
        int mode = KEY;
        StringBuilder keyStringBuilder = null;
        StringBuilder valueStringBuilder = null;
        while (true) {
            int readInt = reader.read();
            char readChar = (char) readInt;
            if (readChar == '\n' || readInt == -1) {
                if (keyStringBuilder != null && valueStringBuilder != null) {
                    String keyString = keyStringBuilder.toString();
                    String valueString = valueStringBuilder.toString();
                    if (keyString.equals(SCRIPT_KEY)) {
                        // script parsed
                        setScript(valueString);
                    } else if (keyString.equals(ACTIVATION_CONTROL_KEY_KEY)) {
                        // activation control key parsed
                        activationControlKeyString = valueString;
                        activationControlKey = CONTROL_KEY_MAP.get(activationControlKeyString);
                    } else if (keyString.equals(ACTIVATION_KEY_KEY)) {
                        // activation control key parsed
                        activationKeyString = valueString;
                        activationKey = KEY_MAP.get(activationKeyString);
                    } else {
                        // ERROR: unknown key
                    }
                    mode = KEY;
                } else {
                    // ERROR: incomplete definition
                }
                keyStringBuilder = null;
                valueStringBuilder = null;
                if (readInt == -1) {
                    break;
                }
            } else if (!Character.isWhitespace(readChar)) {
                switch (readChar) {
                    case '=':
                        if (mode == KEY) {
                            mode = VALUE;
                        } else {
                            // ERROR: multiple equal signs
                        }
                        break;
                    default:
                        if (mode == KEY) {
                            if (keyStringBuilder == null) {
                                keyStringBuilder = new StringBuilder();
                            }
                            keyStringBuilder.append(readChar);
                        } else if (mode == VALUE) {
                            if (valueStringBuilder == null) {
                                valueStringBuilder = new StringBuilder();
                            }
                            valueStringBuilder.append(readChar);
                        }
                        break;
                }
            }
        }
    }

    public static void save() {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(FILE_NAME));
            writer.write(
                    SCRIPT_KEY              + " = " + scriptFileName + "\n" +
                    ACTIVATION_CONTROL_KEY_KEY  + " = " + activationControlKeyString + "\n" +
                    ACTIVATION_KEY_KEY          + " = " + activationKeyString
            );
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setActivationKeys(String controlKeyString, String keyString) {
        activationControlKeyString = controlKeyString;
        activationControlKey = CONTROL_KEY_MAP.get(controlKeyString);
        activationKeyString = keyString;
        activationKey = KEY_MAP.get(keyString);
    }

    public static void setScript(String fileName) {
        scriptFileName = fileName;
        script = new Script(fileName);
    }

    public static String[] getAvailableScriptFileNames() {
        ArrayList<String> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("res"))) {
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

    public static Script getScript() {
        return script;
    }

    public static String getScriptFileName() {
        return scriptFileName;
    }

    public static int getActivationControlKey() {
        return activationControlKey;
    }

    public static int getActivationKey() {
        return activationKey;
    }

    public static String getActivationControlKeyString() {
        return activationControlKeyString;
    }

    public static String getActivationKeyString() {
        return activationKeyString;
    }

    public static Font getScriptFont() {
        return scriptFont;
    }

    public static Font getKeyFont() {
        return keyFont;
    }

}
