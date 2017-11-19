import java.util.HashMap;

public class Script {

    private HashMap<Character, String> keyStrings;
    private HashMap<Character, CharacterClass> characterClasses;
    private boolean normalize;

    public Script(HashMap<Character, String> keyStrings,
                  HashMap<Character, CharacterClass> characterClasses,
                  boolean normalize) {
        this.keyStrings = keyStrings;
        this.characterClasses = characterClasses;
        this.normalize = normalize;
    }

    public String getKeyString(char keyCharacter) {
        String keyString = keyStrings.get(keyCharacter);
        return keyString != null ? keyString : String.valueOf(keyCharacter);
    }

    public CharacterClass getCharacterClass(char classCharacter) {
        return characterClasses.get(classCharacter);
    }

    public boolean requiresNormalization() {
        return normalize;
    }

}
