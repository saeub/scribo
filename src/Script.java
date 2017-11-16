import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Script {

    private HashMap<Character, String> keyStrings;
    private HashMap<Character, CharacterClass> characterClasses;

    public Script(String fileName) {
        keyStrings = new HashMap<>();
        characterClasses = new HashMap<>();
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(new FileInputStream("res/" + fileName), "UTF-8");
            parse(reader);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parse(InputStreamReader reader) throws IOException {
        final int CLASS_KEY = 0;
        final int CHAR_KEY = 1;
        final int CHAR = 3;
        final int COMMENT = 4;
        int mode = CLASS_KEY;
        Character classKey = null, charKey = null;
        StringBuilder charStringBuilder = null;
        while (true) {
            int readInt = reader.read();
            char readChar = (char) readInt;
            if (readChar == '\n' || readInt == -1) {
                if (classKey != null && charStringBuilder != null) {
                    if (charKey == null) { // formula x + y = z
                        System.out.println(classKey + " = " + charStringBuilder.toString());
                        if (keyStrings.containsKey(classKey)) {
                            System.out.println("WARNING: duplicate character" +
                                    "(classkey=" + classKey + ")");
                        }
                        keyStrings.put(classKey, charStringBuilder.toString());
                    } else { // formula x = z
                        System.out.println(classKey + " + " + charKey + " = " + charStringBuilder.toString());
                        if (!characterClasses.containsKey(classKey)) {
                            characterClasses.put(classKey, new CharacterClass(classKey, new HashMap<>()));
                        }
                        CharacterClass characterClass = characterClasses.get(classKey);
                        if (characterClass.containsKeyCharacter(charKey)) {
                            System.out.println("WARNING: duplicate character" +
                                    "(classkey=" + classKey + " charKey=" + charKey + ")");
                        }
                        characterClass.putCharacter(charKey, charStringBuilder.toString());
                    }
                }
                mode = CLASS_KEY;
                classKey = charKey = null;
                charStringBuilder = null;
                if (readInt == -1) {
                    break;
                }
            } else if (mode != COMMENT && !Character.isWhitespace(readChar)) {
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
    }

    public String getKeyString(char keyCharacter) {
        String keyString = keyStrings.get(keyCharacter);
        return keyString != null ? keyString : String.valueOf(keyCharacter);
    }

    public CharacterClass getCharacterClass(char classCharacter) {
        return characterClasses.get(classCharacter);
    }

}
