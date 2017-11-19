import sun.awt.WindowClosingListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;

public class SettingsFrame extends JFrame implements WindowListener, ActionListener {

    private static SettingsFrame singleInstance;

    private JLabel keysLabel;
    private JComboBox<String> controlKeyCombo, keyCombo;
    private JLabel scriptLabel;
    private JComboBox<String> scriptCombo;
    private JButton scriptHelpButton;
    private JButton cancelButton, okButton;

    private SettingsFrame() {
        super("Settings");

        keysLabel = new JLabel("Shortcut:");

        controlKeyCombo = new JComboBox<>();
        ArrayList<String> controlKeyStrings = new ArrayList<>(Settings.CONTROL_KEY_MAP.keySet());
        Collections.sort(controlKeyStrings);
        controlKeyStrings.forEach((keyString) -> controlKeyCombo.addItem(keyString));
        controlKeyCombo.setSelectedItem(Settings.getActivationControlKeyString());
        controlKeyCombo.addActionListener(this);
        //keysPanel.add(controlKeyCombo);

        keyCombo = new JComboBox<>();
        ArrayList<String> keyStrings = new ArrayList<>(Settings.KEY_MAP.keySet());
        Collections.sort(keyStrings);
        keyStrings.forEach((keyString) -> keyCombo.addItem(keyString));
        keyCombo.setSelectedItem(Settings.getActivationKeyString());
        keyCombo.addActionListener(this);
        //keysPanel.add(keyCombo);

        scriptLabel = new JLabel("Script:");

        scriptCombo = new JComboBox<>(Settings.getAvailableScriptFileNames());
        scriptCombo.setSelectedItem(Settings.getScriptFileName());

        scriptHelpButton = new JButton("What's this?");
        scriptHelpButton.setMargin(new Insets(2, 2, 2, 2));

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        okButton = new JButton("OK");
        okButton.addActionListener(this);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(keysLabel)
                    .addComponent(scriptLabel)
                )
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(controlKeyCombo)
                        .addComponent(keyCombo)
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

        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(keysLabel)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(controlKeyCombo)
                    .addComponent(keyCombo)
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

        setSize(getPreferredSize());
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
            Settings.setActivationKeys((String) controlKeyCombo.getSelectedItem(), (String) keyCombo.getSelectedItem());
            String scriptFileName = (String) scriptCombo.getSelectedItem();
            if (!scriptFileName.equals(Settings.getScriptFileName())) {
                Settings.setScript(scriptFileName);
            }
            Settings.save();
            close();
        }
    }

    @Override
    public void windowOpened(WindowEvent e) { }

    @Override
    public void windowClosing(WindowEvent e) {
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