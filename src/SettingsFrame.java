/**
 * This class contains the code to create the SettingsFrame GUI. This class
 * allows you set game variables such as board size and AI difficulty.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public class SettingsFrame extends JFrame implements ActionListener {

    private final int GUI_WIDTH = 600;
    private final int GUI_HEIGHT = 800;
    private final int INPUT_WIDTH = 80;
    private final int INPUT_HEIGHT = 30;
    private final int LABEL_FONT_SIZE = 20;
    private final int LABEL_WIDTH = 80;
    private final int LABEL_HEIGHT = 30;

    private JButton cancelSettings;
    private JButton saveSettings;
    private JCheckBox enableAES;
    private JCheckBox enableSound;
    private JComboBox<String> newAIDifficulty;
    private JLabel aesEncrpytion;
    private JLabel aiDifficulty;
    private JLabel boardSize;
    private JLabel sound;
    private JLabel userName;
    private JTextField newBoardSize;
    private JTextField newUserName;    

    /**
     * Constructor for the settings frame
     */
    public SettingsFrame() {
        //main components for the settings frame
        setTitle("Settings");
        setSize(GUI_WIDTH, GUI_HEIGHT);
        

        //grid layout of the GUI
        GridLayout guiGrid = new GridLayout(0,2);

        //setting for board size
        boardSize = new JLabel("Boardsize: ");
        boardSize.setFont(new Font ("Imprint MT Shadow", Font.PLAIN, LABEL_FONT_SIZE));
        newBoardSize = new JTextField("8");
        newBoardSize.setFont(new Font ("Imprint MT Shadow", Font.PLAIN, LABEL_FONT_SIZE));
        newBoardSize.setMaximumSize(new Dimension(INPUT_WIDTH, INPUT_HEIGHT));

        //setting for user name
        userName = new JLabel("Username: ");
        userName.setFont(new Font ("Imprint MT Shadow", Font.PLAIN, LABEL_FONT_SIZE));
        newUserName = new JTextField();
        String desktopName;
        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            desktopName = addr.getHostName();
            newUserName.setText(desktopName);
        } catch (UnknownHostException e) {
            newUserName.setText("Player1");
        }
        newUserName.setFont(new Font ("Imprint MT Shadow", Font.PLAIN, LABEL_FONT_SIZE));
        newUserName.setMaximumSize(new Dimension(INPUT_WIDTH, INPUT_HEIGHT));

        //setting for enabling AES encrpytion
        aesEncrpytion = new JLabel("Enable AES Encryption: ");
        aesEncrpytion.setFont(new Font ("Imprint MT Shadow", Font.PLAIN, LABEL_FONT_SIZE));
        enableAES = new JCheckBox();

        //setting for AI difficulty
        aiDifficulty = new JLabel("AI Difficulty");
        aiDifficulty.setFont(new Font ("Imprint MT Shadow", Font.PLAIN, LABEL_FONT_SIZE));
        String[] difficultyLevels = {"Novice", "Adept", "Expert", "Master"};
        newAIDifficulty = new JComboBox<>(difficultyLevels);
        newAIDifficulty.setFont(new Font ("Imprint MT Shadow", Font.PLAIN, LABEL_FONT_SIZE));
        newAIDifficulty.setMaximumSize(new Dimension(INPUT_WIDTH, INPUT_HEIGHT));

        //setting for sound
        sound = new JLabel("Sound: ");
        sound.setFont(new Font ("Imprint MT Shadow", Font.PLAIN, LABEL_FONT_SIZE));
        enableSound = new JCheckBox();

        //button to save settings
        saveSettings = new JButton("Save Settings");
        saveSettings.setFont(new Font ("Imprint MT Shadow", Font.PLAIN, LABEL_FONT_SIZE));
        saveSettings.setMaximumSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
        saveSettings.setActionCommand("SAVE");
        saveSettings.addActionListener(this);

        //button to cancel settings
        cancelSettings = new JButton("Cancel");
        cancelSettings.setFont(new Font ("Imprint MT Shadow", Font.PLAIN, LABEL_FONT_SIZE));
        cancelSettings.setMaximumSize(new Dimension(LABEL_WIDTH, LABEL_HEIGHT));
        cancelSettings.setActionCommand("CANCEL");
        cancelSettings.addActionListener(this);

        //add all all the components to a panel
        JPanel panel = new JPanel();
        panel.setLayout(guiGrid);
        panel.add(boardSize);
        panel.add(newBoardSize);
        panel.add(userName);
        panel.add(newUserName);
        panel.add(aesEncrpytion);
        panel.add(enableAES);
        panel.add(aiDifficulty);
        panel.add(newAIDifficulty);
        panel.add(sound);
        panel.add(enableSound);

        //add all the components to a panel
        JPanel panel2 = new JPanel();
        panel2.add(saveSettings);
        panel2.add(cancelSettings);

        //add main components onto panel to add to frame
        add(panel, BorderLayout.CENTER);
        //add panel which contains save and cancel buttons to the frame
        add(panel2, BorderLayout.SOUTH);
        //pack everything together
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
        panel2.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        pack();

        //set the frame to visible and disable resizing
        setResizable(false);
        setVisible(true); 
    }

    /**
     * This method is triggered when the user clicks a button.
     * @param The event that happened
     */
    public void actionPerformed(ActionEvent event) {
        //if the save button is pressed
        if (event.getSource() == saveSettings) {
            saveSettings();       
        //if the cancel button is pressed, default settings are loaded
        } else if (event.getSource() == cancelSettings) {            
            //apply default settings
            applyDefaultSettings();
            //notify the user default settings has been applied
            JOptionPane.showMessageDialog(null, "Default settings will be applied", "Cancel", JOptionPane.INFORMATION_MESSAGE);
            //get rid of the frame
            dispose();
        }
    }

    /**
     * This method is triggered if the user clicks cancel or there is an error with saving
     * the user's custom settings.
     * It creates a game properties file with the default game settings according to
     * the specification found here:
     * https://studres.cs.st-andrews.ac.uk/CS1006/Lectures/L04-P03-Othello.pdf
     */
    private void applyDefaultSettings() {
        Properties prop = new Properties();
        OutputStream output = null;
        try {
            //set the properties file to default settings
            output = new FileOutputStream("game.properties");
            prop.setProperty("Boardsize", "8");
            prop.setProperty("Username", newUserName.getText());
            boolean selected = enableAES.isSelected();
            prop.setProperty("EnableAESEncrpytion", Boolean.toString(selected));
            prop.setProperty("AIDifficulty", newAIDifficulty.getSelectedItem().toString());
            selected = enableSound.isSelected();
            prop.setProperty("EnableSound", Boolean.toString(selected));
            prop.store(output, null);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "IO Exception Error, please try again", "IO Error", JOptionPane.WARNING_MESSAGE);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "IO Exception Error, please try again", "IO Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    /**
     * This method is triggered if the user wants to use custom settings for the
     * game.
     */
    private void saveSettings() {
        Properties prop = new Properties();
        OutputStream output = null;
        try {
            //set the properties file
            output = new FileOutputStream("game.properties");

            //board size
            int boardDimension =  Integer.parseInt(newBoardSize.getText());
            prop.setProperty("Boardsize", newBoardSize.getText());

            //network username
            prop.setProperty("Username", newUserName.getText());

            //AES encryption
            boolean selected = enableAES.isSelected();
            prop.setProperty("EnableAESEncrpytion", Boolean.toString(selected));

            //AI difficulty
            prop.setProperty("AIDifficulty", newAIDifficulty.getSelectedItem().toString());

            //sound
            selected = enableSound.isSelected();
            prop.setProperty("EnableSound", Boolean.toString(selected));
            prop.store(output, null);
        
        //if there is an IO error apply default settings
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "IO Exception Error, default settings will be applied", "IO Error", JOptionPane.WARNING_MESSAGE);
            applyDefaultSettings();

        //if there is a number format exception apply default settings
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Board Size cannot be a string default settings applied", "Number Format Exception", JOptionPane.WARNING_MESSAGE);
            applyDefaultSettings();

        } finally {
            if (output != null) {
                try {
                    output.close();
                    //remove the frame
                    dispose();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "IO Exception Error, please try again", "IO Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }
}
