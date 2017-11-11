import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class SettingsFrame extends JFrame implements ActionListener {

    private JPanel panel;
    private JPanel settingsPanel, buttonsPanel;
    private JPanel keysPanel, scriptPanel;
    private JLabel keysLabel;
    private JComboBox<String> controlKeyCombo, keyCombo;
    private JLabel scriptLabel;
    private JComboBox<String> scriptCombo;
    private JButton scriptHelpButton;
    private JButton cancelButton, okButton;

    public SettingsFrame() {
        super("Settings");

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        add(panel);

        settingsPanel = new JPanel();
        GroupLayout settingsLayout = new GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsLayout);
        panel.add(settingsPanel);

        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        panel.add(buttonsPanel, new GridBagConstraints(0, 1, 1, 1,
                0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));

        keysPanel = new JPanel();
        keysPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        keysLabel = new JLabel("Shortcut:");

        controlKeyCombo = new JComboBox<>();
        ArrayList<String> controlKeyStrings = new ArrayList<>(Settings.CONTROL_KEY_MAP.keySet());
        Collections.sort(controlKeyStrings);
        controlKeyStrings.forEach((keyString) -> controlKeyCombo.addItem(keyString));
        controlKeyCombo.setSelectedItem(Settings.getActivationControlKeyString());
        controlKeyCombo.addActionListener(this);
        keysPanel.add(controlKeyCombo);

        keyCombo = new JComboBox<>();
        ArrayList<String> keyStrings = new ArrayList<>(Settings.KEY_MAP.keySet());
        Collections.sort(keyStrings);
        keyStrings.forEach((keyString) -> keyCombo.addItem(keyString));
        keyCombo.setSelectedItem(Settings.getActivationKeyString());
        keyCombo.addActionListener(this);
        keysPanel.add(keyCombo);

        scriptPanel = new JPanel();
        scriptPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        scriptLabel = new JLabel("Script:");
        scriptPanel.add(scriptLabel);

        scriptCombo = new JComboBox<>(Settings.getAvailableScriptFileNames());
        scriptCombo.setSelectedItem(Settings.getScriptFileName());
        scriptPanel.add(scriptCombo);

        scriptHelpButton = new JButton("What's this?");
        scriptHelpButton.setMargin(new Insets(2, 2, 2, 2));
        scriptPanel.add(scriptHelpButton);

        settingsLayout.setAutoCreateGaps(true);
        settingsLayout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup hGroup = settingsLayout.createSequentialGroup();
        hGroup.addGroup(settingsLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addComponent(keysLabel)
                .addComponent(scriptLabel));
        hGroup.addGroup(settingsLayout.createParallelGroup()
                .addComponent(keysPanel)
                .addComponent(scriptPanel));
        settingsLayout.setHorizontalGroup(hGroup);

        GroupLayout.SequentialGroup vGroup = settingsLayout.createSequentialGroup();
        vGroup.addGroup(settingsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(keysLabel)
                .addComponent(keysPanel));
        vGroup.addGroup(settingsLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(scriptLabel)
                .addComponent(scriptPanel));
        settingsLayout.setVerticalGroup(vGroup);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(this);

        okButton = new JButton("OK");
        okButton.addActionListener(this);

        buttonsPanel.add(cancelButton);
        buttonsPanel.add(okButton);

        setSize(panel.getPreferredSize());
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == cancelButton) {
            dispose();
        } else if (source == okButton) {
            Settings.setActivationKeys((String) controlKeyCombo.getSelectedItem(), (String) keyCombo.getSelectedItem());
            String scriptFileName = (String) scriptCombo.getSelectedItem();
            if (!scriptFileName.equals(Settings.getScriptFileName())) {
                Settings.setScript(scriptFileName);
            }
            Settings.save();
            dispose();
        }
    }

}