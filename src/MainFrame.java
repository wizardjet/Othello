/**
 * This class contains the JFrame of the main menu and all of its various components.
 * This class implements the ActionListener interface  in order to determine which
 * button the user clicked on
 */


import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MainFrame extends JFrame implements ActionListener {

    private final int GUI_WIDTH = 600;
    private final int GUI_HEIGHT = 800;
    private final int TITLE_FONT_SIZE = 80;
    private final int TITLE_FONT_WIDTH = 400;
    private final int TITLE_FONT_HEIGHT = 400;
    private final int BUTTON_FONT_SIZE = 24;
    private final int BUTTON_WIDTH = 300;
    private final int BUTTON_HEIGHT = 60;
    private JButton singlePlayer;
    private JButton networkMultiplayer;
    private JButton settingsButton;
    private JButton localMultiplayer;
    private JButton aiVSai;
    private JLabel titleLabel;

    /**
     * The main menu constructor
     */
    public MainFrame() {
        //compoenents for the main frame
        this.setTitle("Othello");
        this.setSize(GUI_WIDTH, GUI_HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        //background
        //image taken from http://www.kissthemachine.com/images/reversi-hd-screenshot-2.png
        JLabel background = new JLabel(new ImageIcon("background.png"));
        this.add(background);
        background.setLayout(new BoxLayout(background, BoxLayout.Y_AXIS));

        //title
        titleLabel = new JLabel("OTHELLO");
        titleLabel.setFont(new Font("Imprint MT Shadow", Font.PLAIN, TITLE_FONT_SIZE));
        titleLabel.setForeground(Color.white);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setMinimumSize(new Dimension(TITLE_FONT_WIDTH, TITLE_FONT_HEIGHT));

        //single player button
        singlePlayer = new JButton("Singleplayer vs AI");
        singlePlayer.setFont(new Font ("Imprint MT Shadow", Font.PLAIN, BUTTON_FONT_SIZE));
        singlePlayer.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        singlePlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
        singlePlayer.addActionListener(this);

        //Local Mulitplayer
        localMultiplayer = new JButton("Local Multiplayer");
        localMultiplayer.setFont(new Font ("Imprint MT Shadow", Font.PLAIN, BUTTON_FONT_SIZE));
        localMultiplayer.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        localMultiplayer.setAlignmentX(Component.CENTER_ALIGNMENT);
        localMultiplayer.addActionListener(this);

        //Network Multiplayer button
        networkMultiplayer = new JButton("Network Multiplayer");
        networkMultiplayer.setFont(new Font ("Imprint MT Shadow", Font.PLAIN, BUTTON_FONT_SIZE));
        networkMultiplayer.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        networkMultiplayer.setAlignmentX(Component.CENTER_ALIGNMENT);
        networkMultiplayer.addActionListener(this);

        //settings button
        settingsButton = new JButton("Settings");
        settingsButton.setFont(new Font ("Imprint MT Shadow", Font.PLAIN, BUTTON_FONT_SIZE));
        settingsButton.setMaximumSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsButton.addActionListener(this);

        //adding components to the background image
        background.add(titleLabel);
        background.add(Box.createVerticalStrut(15));
        background.add(singlePlayer);
        background.add(Box.createVerticalStrut(10));
        background.add(localMultiplayer);
        background.add(Box.createVerticalStrut(10));
        background.add(networkMultiplayer);
        background.add(Box.createVerticalStrut(10));
        background.add(settingsButton);

        //display the GUI in the centre of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        //set the GUI to be non resizable and show the GUI
        this.setResizable(false);
        this.setVisible(true); 
    }

    /**
     * This method is launched whenever the user performs clicks on a button
     */
    public void actionPerformed(ActionEvent event) {
        //singleplayer button pressed
        if (event.getSource() == singlePlayer) {
            GameFrame gf = new GameFrame(true, 1);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            gf.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        
        //multiplayer button pressed
        } else if (event.getSource() == localMultiplayer) {
            GameFrame gf = new GameFrame(false, 1);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            gf.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        } else if (event.getSource() == networkMultiplayer) {
            GameFrame gf = new GameFrame(false, 2);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            gf.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        //settings button pressed
        } else if (event.getSource() == settingsButton) {
            SettingsFrame sf = new SettingsFrame();
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            sf.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        }
    }
}
