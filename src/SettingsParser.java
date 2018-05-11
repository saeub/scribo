import java.io.*;

public class SettingsParser {

    private static final String SCRIPT_KEY = "script";
    private static final String ACTIVATION_KEY_KEY = "activationKey";
    private static final String ACTIVATION_KEY_PRESSES_KEY = "activationKeyPresses";

    private static final int KEY = 0;
    private static final int VALUE = 1;

    private String fileName;
    private int mode;
    private StringBuilder keyStringBuilder, valueStringBuilder;

    public SettingsParser(String fileName) {
        this.fileName = fileName;
    }

    public Settings parse() throws IOException {
        String scriptFileName = null, activationKeyString = null;
        int activationKeyPresses = -1;
        resetVariables();
        InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
        while (true) {
            int readInt = reader.read();
            char readChar = (char) readInt;
            if (readChar == '\n' || readInt == -1) { // end of line: check and store parsed property
                if (keyStringBuilder != null && valueStringBuilder != null) {
                    String keyString = keyStringBuilder.toString();
                    String valueString = valueStringBuilder.toString();
                    if (keyString.equals(SCRIPT_KEY)) { // script parsed
                        scriptFileName = valueString;
                    } else if (keyString.equals(ACTIVATION_KEY_KEY)) { // activation key parsed
                        activationKeyString = valueString;
                    } else if (keyString.equals(ACTIVATION_KEY_PRESSES_KEY)) { // activation key presses parsed
                        activationKeyPresses = Integer.parseInt(valueString);
                    } else {
                        System.out.println("(SettingsParser) WARNING: unknown key '" + keyString + "'");
                    }
                } else {
                    System.out.println("(SettingsParser) WARNING: incomplete definition");
                }
                if (readInt == -1) {
                    break;
                }
                resetVariables();
            } else if (!Character.isWhitespace(readChar)) {
                parseCharacter(readChar);
            }
        }
        reader.close();
        return new Settings(scriptFileName, activationKeyString, activationKeyPresses);
    }

    private void resetVariables() {
        mode = KEY;
        keyStringBuilder = null;
        valueStringBuilder = null;
    }

    private void parseCharacter(char readChar) {
        switch (readChar) {
            case '=':
                if (mode == KEY) {
                    mode = VALUE;
                } else {
                    System.out.println("(SettingsParser) WARNING: unexpected '='");
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

    public void save(Settings settings) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName));
        writer.write(
            SCRIPT_KEY             + " = " + settings.getScriptFileName() + "\n" +
            ACTIVATION_KEY_KEY         + " = " + settings.getActivationKeyString() + "\n" +
            ACTIVATION_KEY_PRESSES_KEY + " = " + settings.getActivationKeyPresses()
        );
        writer.close();
    }

}
