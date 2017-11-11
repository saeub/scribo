import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class CharacterClass {

    private HashMap<Character, String> characters;

    public CharacterClass(HashMap<Character, String> characters) {
        this.characters = characters;
    }

    public boolean containsKeyCharacter(Character keyCharacter) {
        return characters.containsKey(keyCharacter);
    }

    public void putCharacter(Character keyCharacter, String characterString) {
        characters.put(keyCharacter, characterString);
    }

    public Character[] getKeyCharacters() {
        return characters.keySet().toArray(new Character[0]);
    }

    public String getCharacterString(char keyCharacter) {
        return characters.get(keyCharacter);
    }

    public int size() {
        return characters.size();
    }

}
