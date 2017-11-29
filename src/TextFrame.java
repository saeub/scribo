import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Normalizer;

public class TextFrame extends JFrame implements WindowFocusListener, KeyListener, NativeKeyListener, ClipboardOwner {

    private JTextField textField;
    private boolean selecting;
    private boolean activationControlKeyPressed;
    private int tempStart, tempEnd;

    public TextFrame() {
        super();
        setUndecorated(true);
        setAlwaysOnTop(true);
        addWindowFocusListener(this);

        // TODO prevent or handle text selection
        textField = new JTextField();
        textField.setEditable(false);
        textField.getCaret().setVisible(true); // show caret even though textField is technically not editable
        textField.addKeyListener(this);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setMargin(new Insets(0, 5, 0, 5));
        textField.setFont(Settings.getActiveScriptFont());

        add(textField);
        resize();

        // add system tray icon and menu
        try {
            TrayIcon icon = new TrayIcon(ImageIO.read(new FileInputStream(Settings.RES_PATH + "trayicon.png")));
            icon.setImageAutoSize(true);
            icon.addActionListener(e -> SettingsFrame.open());
            PopupMenu menu = new PopupMenu();
            MenuItem quitItem = new MenuItem("Quit");
            quitItem.addActionListener(e -> Main.exit());
            menu.add(quitItem);
            icon.setPopupMenu(menu);
            SystemTray.getSystemTray().add(icon);
        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }

    private void resize() {
        int stringWidth = textField.getFontMetrics(textField.getFont()).stringWidth(textField.getText());
        setSize(stringWidth + 20, 30);
        setLocationRelativeTo(null);
    }

    private void handleInput(Character inputCharacter) {
        switch (inputCharacter) {
            case KeyEvent.VK_ENTER:
                break;
            case KeyEvent.VK_ESCAPE:
                break;
            case KeyEvent.VK_BACK_SPACE:
                removeCharacter(-1);
                break;
            case KeyEvent.VK_DELETE:
                removeCharacter(0);
                break;
            default: // letter key is entered (probably...)
                chooseCharacter(inputCharacter);
                break;
        }
    }

    public void onSelection(String characterString, Character nextCharacter) {
        selecting = false;
        if (characterString != null) {
            addString(characterString, true, false);
        }
        if (nextCharacter != null) {
            handleInput(nextCharacter);
        }
    }

    private void addString(String string, boolean replaceTemp, boolean newTemp) {
        StringBuilder text = new StringBuilder(textField.getText());
        int caret = textField.getCaretPosition();

        // set or replace temporary string
        if (replaceTemp) {
            text.delete(tempStart, tempEnd);
            textField.setText(text.toString());
            caret = tempStart;
        }
        if (newTemp) {
            tempStart = caret;
            tempEnd = caret + string.length();
        }

        text.insert(caret, string);

        // normalize if added String is not temporary
        if (Settings.getActiveScript().requiresNormalization() && !newTemp) {
            String normalizedText = Normalizer.normalize(text.toString(), Normalizer.Form.NFC);
            caret += string.length() + normalizedText.length() - text.length(); // adjust caret for normalization
            text = new StringBuilder(normalizedText);
        } else {
            caret += string.length();
        }

        // update clipboard
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(text.toString()), this);

        textField.setText(text.toString());
        textField.setCaretPosition(caret);
        resize();
    }

    private void removeCharacter(int offset) {
        StringBuilder text = new StringBuilder(textField.getText());

        int index = textField.getCaretPosition() + offset;
        if (index >= 0 && index < text.length()) {
            text.deleteCharAt(index);
            textField.setText(text.toString());
            textField.setCaretPosition(index);
            resize();

            // update clipboard
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new StringSelection(text.toString()), this);
        }
    }

    public void chooseCharacter(char classKey) {
        selecting = true;
        addString(Settings.getActiveScript().getKeyString(classKey), false, true);
        new SelectionDialog(this, classKey);
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        // show caret, even though textField is technically not editable
        textField.getCaret().setVisible(true);
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        // hide window if user clicks away
        if (!selecting) { // necessary, since TextFrame automatically loses focus when SelectionDialog is opened
            setVisible(false);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (!selecting) {
            char keyChar = e.getKeyChar();
            handleInput(keyChar);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        int keyCode = nativeKeyEvent.getKeyCode();

        // show/hide frame
        if (!isVisible()) {
            if (keyCode == Settings.getActiveActivationControlKey()) {
                activationControlKeyPressed = true;
            } else if (keyCode == Settings.getActiveActivationKey() && activationControlKeyPressed) {
                setVisible(true);
            }
        } else {
            if (!selecting && (keyCode == NativeKeyEvent.VC_ENTER || keyCode == NativeKeyEvent.VC_ESCAPE)) {
                textField.setText(null);
                textField.invalidate();
                setVisible(false);
                resize();
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        int keyCode = nativeKeyEvent.getKeyCode();
        if (keyCode == Settings.getActiveActivationControlKey()) {
            activationControlKeyPressed = false;
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }

}