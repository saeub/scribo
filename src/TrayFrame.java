import javax.swing.*;
import java.awt.event.*;

public class TrayFrame extends JFrame implements WindowListener, ActionListener {

    private JButton settingsButton;

    public TrayFrame() {
        super("scribo");

        settingsButton = new JButton("Settings");
        settingsButton.addActionListener(this);
        add(settingsButton);

        pack();
        setResizable(false);
        addWindowListener(this);
        addWindowListener(this);

        setVisible(true);
    }

    @Override
    public void windowOpened(WindowEvent e) { }

    @Override
    public void windowClosing(WindowEvent e) {
        Main.exit();
    }

    @Override
    public void windowClosed(WindowEvent e) { }

    @Override
    public void windowIconified(WindowEvent e) { }

    @Override
    public void windowDeiconified(WindowEvent e) { }

    @Override
    public void windowActivated(WindowEvent e) { }

    @Override
    public void windowDeactivated(WindowEvent e) { }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == settingsButton) {
            SettingsFrame.open();
        }
    }

}
