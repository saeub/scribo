import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SelectionDialog extends JDialog implements KeyListener {

    private TextFrame parent;
    private CharacterClass characterClass;

    public SelectionDialog(TextFrame parent, char classKey) {
        super(parent, true);
        this.parent = parent;
        this.characterClass = Settings.getActiveScript().getCharacterClass(classKey);
        if (characterClass == null) {
            parent.onSelection(Settings.getActiveScript().getKeyString(classKey), null);
        } else {
            Character[] keyCharacters = characterClass.getKeyCharacters();
            setLayout(new GridLayout(2, keyCharacters.length));
            for (int i = 0; i < keyCharacters.length; i++) {
                String characterString = characterClass.getCharacterString(keyCharacters[i]);
                SelectionCharacterLabel characterLabel = new SelectionCharacterLabel(characterString);
                add(characterLabel);
            }
            for (int i = 0; i < keyCharacters.length; i++) {
                SelectionKeyLabel keyLabel = new SelectionKeyLabel(keyCharacters[i]);
                add(keyLabel);
            }
            setSize(keyCharacters.length * 30, 50);
            setLocation(parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
                    parent.getLocation().y - getHeight());
            addKeyListener(this);
            setUndecorated(true);
            setVisible(true);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        dispose();
        char keyChar = e.getKeyChar();
        String characterString = characterClass.getCharacterString(keyChar);
        parent.onSelection(characterString, characterString != null ? null : keyChar);
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private class SelectionCharacterLabel extends JLabel {

        private SelectionCharacterLabel(String characterString) {
            super();
            setText(characterString);
            setFont(Settings.getActiveScriptFont());
            setHorizontalAlignment(JLabel.CENTER);
        }

    }

    private class SelectionKeyLabel extends JLabel {

        private SelectionKeyLabel(char keyCharacter) {
            super();
            setText(Character.toString(keyCharacter));
            setFont(Settings.getActiveKeyFont());
            setForeground(new Color(0x666666));
            setHorizontalAlignment(JLabel.CENTER);
        }

    }

}