import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ScriptParser {

    private static final int FLAG = 0;
    private static final int CLASS_KEY = 1;
    private static final int CHAR_KEY = 2;
    private static final int CHAR = 3;
    private static final int COMMENT = 4;

    private String fileName;
    private int mode;
    private Character classKey, charKey;
    private StringBuilder flagStringBuilder, charStringBuilder;

    public ScriptParser(String fileName) {
        this.fileName = fileName;
    }

    public Script parse() throws IOException {
        HashMap<Character, String> keyStrings = new HashMap<>();
        HashMap<Character, CharacterClass> characterClasses = new HashMap<>();
        boolean normalize = false;
        resetVariables();
        InputStreamReader reader = new InputStreamReader(new FileInputStream(Settings.RES_PATH + fileName), "UTF-8");
        while (true) {
            int readInt = reader.read();
            char readChar = (char) readInt;
            if (readChar == '\n' || readInt == -1) {
                if (flagStringBuilder != null) { // flag parsed
                    if (flagStringBuilder.toString().equals("normalize")) {
                        normalize = true;
                    } else {
                        System.out.println("WARNING: invalid flag " + flagStringBuilder.toString());
                    }
                    resetVariables();
                } else if (classKey != null && charStringBuilder != null) {
                    if (charKey == null) { // formula x + y = z parsed
                        if (keyStrings.containsKey(classKey)) {
                            System.out.println("WARNING: duplicate character " +
                                    "(classkey=" + classKey + ")");
                        }
                        keyStrings.put(classKey, charStringBuilder.toString());
                    } else { // formula x = z parsed
                        if (!characterClasses.containsKey(classKey)) {
                            characterClasses.put(classKey, new CharacterClass(classKey, new HashMap<>()));
                        }
                        CharacterClass characterClass = characterClasses.get(classKey);
                        if (characterClass.containsKeyCharacter(charKey)) {
                            System.out.println("WARNING: duplicate character " +
                                    "(classkey=" + classKey + " charKey=" + charKey + ")");
                        }
                        characterClass.putCharacter(charKey, charStringBuilder.toString());
                    }
                }
                if (readInt == -1) {
                    break;
                }
                resetVariables();
            } else if (mode != COMMENT && !Character.isWhitespace(readChar)) {
                if (mode == FLAG) { // there may be a flag in this line
                    if (flagStringBuilder == null) { // no flag has started (yet)
                        if (readChar == '@') { // there is a flag, start it
                            flagStringBuilder = new StringBuilder();
                        } else { // there is no flag, move on with regular parsing
                            mode = CLASS_KEY;
                            parseCharacter(readChar);
                        }
                    } else { // flag has started
                        flagStringBuilder.append(readChar);
                    }
                } else { // there is no flag in this line
                    parseCharacter(readChar);
                }
            }
        }
        reader.close();
        return new Script(keyStrings, characterClasses, normalize);
    }

    private void resetVariables() {
        mode = FLAG;
        classKey = charKey = null;
        flagStringBuilder = charStringBuilder = null;
    }

    private void parseCharacter(char readChar) {
        switch (readChar) {
            case '#':
                mode = COMMENT;
                break;
            case '+':
                if (mode == CLASS_KEY) {
                    mode = CHAR_KEY;
                } else {
                    mode = COMMENT;
                }
                break;
            case '=':
                if (mode == CLASS_KEY || (mode == CHAR_KEY && charKey != null)) {
                    mode = CHAR;
                } else {
                    mode = COMMENT;
                }
                break;
            default:
                if (mode == CLASS_KEY) {
                    if (classKey == null) {
                        classKey = readChar == '_' ? ' ' : readChar;
                    } else {
                        mode = COMMENT;
                        // ERROR: multiple class key characters
                    }
                } else if (mode == CHAR_KEY) {
                    if (charKey == null) {
                        charKey = readChar == '_' ? ' ' : readChar;
                    } else {
                        mode = COMMENT;
                        // ERROR: multiple character key characters
                    }
                } else if (mode == CHAR) {
                    if (charStringBuilder == null) {
                        charStringBuilder = new StringBuilder();
                    }
                    charStringBuilder.append(readChar == '_' ? ' ' : readChar);
                } else {
                    mode = COMMENT;
                }
                break;
        }
    }

}
