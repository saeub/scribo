import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Script {

    private HashMap<Character, CharacterClass> characterClasses;

    public Script(String fileName) {
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
        final int START = 0;
        final int COMMENT = 1;
        final int CLASS_KEY = 2;
        final int PLUS = 3;
        final int CHAR_KEY = 4;
        final int EQUALS = 5;
        final int CHAR = 6;
        int lastCharMode = START;
        Character classKey = null, charKey = null;
        StringBuilder charStringBuilder = null;
        while (true) {
            int readInt = reader.read();
            char readChar = (char) readInt;
            if (readChar == '\n' || readInt == -1) {
                if (classKey != null && charKey != null && charStringBuilder != null) {
                    if (!characterClasses.containsKey(classKey)) {
                        characterClasses.put(classKey, new CharacterClass(new HashMap<>()));
                    }
                    CharacterClass characterClass = characterClasses.get(classKey);
                    if (characterClass.containsKeyCharacter(charKey)) {
                        System.out.println("WARNING: duplicate character" +
                                "(classkey=" + classKey + " charKey=" + charKey + ")");
                    }
                    characterClass.putCharacter(charKey, charStringBuilder.toString());
                }
                lastCharMode = START;
                classKey = charKey = null;
                charStringBuilder = null;
                if (readInt == -1) {
                    break;
                }
            } else if (lastCharMode != COMMENT && !Character.isWhitespace(readChar)) {
                switch (readChar) {
                    case '#':
                        lastCharMode = COMMENT;
                        break;
                    case '+':
                        if (lastCharMode == CLASS_KEY) {
                            lastCharMode = PLUS;
                        } else {
                            lastCharMode = COMMENT;
                        }
                        break;
                    case '=':
                        if (lastCharMode == CHAR_KEY) {
                            lastCharMode = EQUALS;
                        } else {
                            lastCharMode = COMMENT;
                        }
                        break;
                    default:
                        if (lastCharMode == START) {
                            classKey = readChar == '_' ? ' ' : readChar;
                            lastCharMode = CLASS_KEY;
                        } else if (lastCharMode == PLUS) {
                            charKey = readChar == '_' ? ' ' : readChar;
                            lastCharMode = CHAR_KEY;
                        } else if (lastCharMode == EQUALS) {
                            charStringBuilder = new StringBuilder();
                            charStringBuilder.append(readChar == '_' ? ' ' : readChar);
                            lastCharMode = CHAR;
                        } else if (lastCharMode == CHAR) {
                            charStringBuilder.append(readChar == '_' ? ' ' : readChar);
                        } else {
                            lastCharMode = COMMENT;
                        }
                        break;
                }
            }
        }
    }

    public CharacterClass getCharacterClass(char classCharacter) {
        return characterClasses.get(classCharacter);
    }

}
