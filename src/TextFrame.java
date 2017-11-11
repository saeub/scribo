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

public class TextFrame extends JFrame implements WindowFocusListener, KeyListener, NativeKeyListener, ClipboardOwner {

    private JTextField textField;
    private boolean selecting;
    private boolean activationControlKeyPressed;

    public TextFrame() {
        super();
        setUndecorated(true);
        setAlwaysOnTop(true);
        addWindowFocusListener(this);
        textField = new JTextField();
        textField.setEditable(false);
        textField.getCaret().setVisible(true);
        textField.addKeyListener(this);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setMargin(new Insets(0, 5, 0, 5));
        textField.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        add(textField);
        resize();
        //setVisible(true);
        try {
            TrayIcon icon = new TrayIcon(ImageIO.read(new FileInputStream("res/trayicon.png")));
            icon.setImageAutoSize(true);
            icon.addActionListener(e -> new SettingsFrame());
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

    public void onSelection(String characterString) {
        selecting = false;
        if (characterString != null) {
            StringBuilder text = new StringBuilder(textField.getText());
            int caret = textField.getCaretPosition();
            text.insert(caret, characterString);
            caret += characterString.length();
            textField.setText(text.toString());
            textField.setCaretPosition(caret);
            resize();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new StringSelection(text.toString()), null);
        }
    }

    private void removeChar(int index) {
        StringBuilder text = new StringBuilder(textField.getText());
        if (index >= 0 && index < text.length()) {
            text.deleteCharAt(index);
            textField.setText(text.toString());
            textField.setCaretPosition(index);
            resize();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new StringSelection(text.toString()), null);
        }
    }

    public void chooseChar(char classKey) {
        selecting = true;
        new SelectionDialog(this, classKey);
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {
        textField.getCaret().setVisible(true);
    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        if (!selecting) {
            setVisible(false);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (!selecting) {
            char keyChar = e.getKeyChar();
            int caret = textField.getCaretPosition();
            if (keyChar == KeyEvent.VK_LEFT) {
                textField.setCaretPosition(caret - 1);
            } else if (keyChar == KeyEvent.VK_RIGHT) {
                textField.setCaretPosition(caret + 1);
            } else if (keyChar == KeyEvent.VK_BACK_SPACE) {
                removeChar(caret - 1);
            } else if (keyChar == KeyEvent.VK_DELETE) {
                removeChar(caret);
            } else {
                chooseChar(keyChar);
            }
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
        if (!isVisible()) {
            if (keyCode == Settings.getActivationControlKey()) {
                activationControlKeyPressed = true;
            } else if (keyCode == Settings.getActivationKey() && activationControlKeyPressed) {
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
        if (keyCode == Settings.getActivationControlKey()) {
            activationControlKeyPressed = false;
        }
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }

}