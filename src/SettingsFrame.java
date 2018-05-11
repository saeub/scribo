import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class SettingsFrame extends JFrame implements WindowListener, ActionListener {

    private static SettingsFrame singleInstance;

    private JLabel keysLabel;
    private JComboBox<String> keyCombo;
    private JSpinner keyPressesSpinner;
    private JLabel scriptLabel;
    private JComboBox<String> scriptCombo;
    private JButton scriptHelpButton;
    private JButton cancelButton, okButton;

    private SettingsFrame() {
        super("Settings");

        /*
        KEY COMPONENTS:
        */
        keysLabel = new JLabel("Shortcut:");

        keyCombo = new JComboBox<>();
        ArrayList<String> keyStrings = new ArrayList<>(Settings.KEY_MAP.keySet());
        Collections.sort(keyStrings);
        keyStrings.forEach((keyString) -> keyCombo.addItem(keyString));
        keyCombo.setSelectedItem(Settings.getActiveActivationKeyString());
        keyCombo.addActionListener(this);

        keyPressesSpinner = new JSpinner(
            new SpinnerNumberModel(Settings.getActiveActivationKeyPresses(), 1, 10, 1)
        );

        /*
        SCRIPT COMPONENTS:
        */
        scriptLabel = new JLabel("Script:");

        scriptCombo = new JComboBox<>(Settings.getAvailableScriptFileNames());
        scriptCombo.setSelectedItem(Settings.getActiveScriptFileName());

        scriptHelpButton = new JButton("What's this?");
        scriptHelpButton.setMargin(new Insets(2, 2, 2, 2));
        scriptHelpButton.addActionListener(this);

        /*
        DIALOG COMPONENTS:
        */
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        okButton = new JButton("OK");
        okButton.addActionListener(this);

        /*
        LAYOUT:
        */
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        // horizontal axis
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(keysLabel)
                    .addComponent(scriptLabel)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(keyCombo)
                        .addComponent(keyPressesSpinner)
                    )
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scriptCombo)
                        .addComponent(scriptHelpButton)
                    )
                )
            )
            .addGroup(layout.createSequentialGroup()
                .addComponent(cancelButton)
                .addComponent(okButton)
            )
        );

        // vertical axis
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(keysLabel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(keyCombo)
                    .addComponent(keyPressesSpinner)
                )
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(scriptLabel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(scriptCombo)
                    .addComponent(scriptHelpButton)
                )
            )
            .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup()
                .addComponent(cancelButton)
                .addComponent(okButton)
            )
        );

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        addWindowListener(this);
        setVisible(true);
    }

    public static void open() {
        if (singleInstance != null) {
            close();
        }
        singleInstance = new SettingsFrame();
    }

    public static void close() {
        if (singleInstance != null) {
            singleInstance.dispose();
            singleInstance = null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == cancelButton) {
            close();
        } else if (source == okButton) {
            Settings.setActiveActivationKey((String) keyCombo.getSelectedItem());
            Settings.setActiveActivationKeyPresses((int) keyPressesSpinner.getValue());
            String scriptFileName = (String) scriptCombo.getSelectedItem();
            if (!scriptFileName.equals(Settings.getActiveScriptFileName())) {
                Settings.setActiveScript(scriptFileName);
            }
            Settings.save();
            close();
        } else if (source == scriptHelpButton) {
            try {
                Runtime.getRuntime().exec("edit " + Settings.RES_PATH + scriptCombo.getSelectedItem());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void windowOpened(WindowEvent e) { }

    @Override
    public void windowClosing(WindowEvent e) {
        // native "x" button clicked
        close();
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

}