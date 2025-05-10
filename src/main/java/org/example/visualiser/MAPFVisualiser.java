package org.example.visualiser;

import org.example.visualiser.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MAPFVisualiser extends JFrame implements ActionListener {

    // Data members

    // Constants
    public static final int MIN_ROWS_COLS = 50;
    public static final int MAX_ROWS_COLS = 150;
    public static final float MIN_WATER_SHARE = 0f;
    public static final float MAX_WATER_SHARE = 1f;
    public static final int MIN_WIND_VELOCITY = 0;
    public static final int MAX_WIND_VELOCITY = 50;
    public static final int MIN_HUMIDITY = 0;
    public static final int MAX_HUMIDITY = 10;
    public static final int MIN_TICK_RATE = 1;
    public static final int MAX_TICK_RATE = 10;
    public static final int MIN_DURATION = 2;
    public static final int MAX_DURATION = 1000;
    public static final String[] WIND_DIRECTIONS = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
    private boolean randIgnitionOnOff = false;
    private boolean ignitionOrExtinguish = false;

    // Restart button test
    UIButton restartButton;
    UIButton exitButton;


    // Object initializations
//    Configurator conf;
//    Map map;
//    Simulator sim;
//    display display;
//    Statistic stats;
//    InputValidator inputValidator = new InputValidator();
//    CellPanel[][] cellPanels;

    // Map info declarations
    UITextField waterShareTextField;
    UITextField mapColumnsTextField;
    UITextField mapRowsTextField;
    UIButton mapInfoButton;
    //Cell[][] mapCells;

    // Weather & sim info declarations
    UITextField windDirectionTextField;
    UITextField windVelocityTextField;
    UITextField humidityTextField;
    UITextField tickRateTextField;
    UITextField simDurationTextField;
    UIButton simInfoButton;

    // Sim control declarations
    UIButton startButton;
    UIButton pauseButton;
    UIButton stopButton;
    UIButton resumeButton;
    UIButton randomIgnitionButton;
    UIButton ignitionControlButton;

    // Map panel declarations
    UIPanel mapPanel;

    // Statistics panel declarations
    UIPanel statPanel;

    // Constructors
    public MAPFVisualiser() {
        // UI main attributes
        this.setTitle("Wildfire Simulator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1500, 1000);
        this.getContentPane().setBackground(new Color(0x2D2D2D));
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // UI main label
        UILabel mainLabel = new UILabel("Wildfire Simulator", 30);
        mainLabel.setBounds(625, 5, 300, 50);
        this.add(mainLabel);

        // Map info panel
        UIPanel mapInfoPanel = new UIPanel();
        UILabel menuLabel = new UILabel("Map info", 20);
        mapColumnsTextField = new UITextField("Number of columns", "Set a value for the number of cells on the x-axis. Allowed range is 50-150.");
        mapRowsTextField = new UITextField("Number of rows", "Set a value for the number of cells on the y-axis. Allowed range is 50-150.");
        waterShareTextField = new UITextField("Water share", "Set a value for the share of water in the map. Allowed range is 0-1.");
        mapInfoButton = new UIButton("Generate map");
        mapInfoButton.addActionListener(this);

        mapInfoPanel.setBounds(10, 70, 300, 220);

        mapInfoPanel.add(menuLabel);
        mapInfoPanel.add(mapColumnsTextField);
        mapInfoPanel.add(mapRowsTextField);
        mapInfoPanel.add(waterShareTextField);
        mapInfoPanel.add(mapInfoButton);
        this.add(mapInfoPanel);

        addMouseListenerToTextField(mapColumnsTextField);
        addMouseListenerToTextField(mapRowsTextField);
        addMouseListenerToTextField(waterShareTextField);

        // Weather info panel
        UIPanel weatherInfoPanel = new UIPanel();
        UILabel weatherInfoLabel = new UILabel("Weather info", 20);
        windDirectionTextField = new UITextField("Wind direction", "Set the direction of the wind. Allowed inputs are the eight directions N, NE, E, etc.");
        windVelocityTextField = new UITextField("Wind velocity", "Set the velocity of the wind in metres per second. Allowed range is 0-50.");
        humidityTextField = new UITextField("Humidity", "Set the level of humidity. Allowed range is 0-10.");

        windDirectionTextField.setEnabled(false);
        windVelocityTextField.setEnabled(false);
        humidityTextField.setEnabled(false);

        weatherInfoPanel.setBounds(10, 300, 300, 180);

        weatherInfoPanel.add(weatherInfoLabel);
        weatherInfoPanel.add(windDirectionTextField);
        weatherInfoPanel.add(windVelocityTextField);
        weatherInfoPanel.add(humidityTextField);

        this.add(weatherInfoPanel);

        // Simulation info panel
        UIPanel simInfoPanel = new UIPanel();
        UILabel simInfoLabel = new UILabel("Simulation info", 20);
        tickRateTextField = new UITextField("Hours per seconds", "Set the number of simulation ticks to be performed per second. Allowed range is 1-10.");
        simDurationTextField = new UITextField("Simulation duration (hours)", "Set the duration for which the simulation should run for. 1 tick simulates 1 hour. Allowed range is 2-1000.");
        simInfoButton = new UIButton("Submit simulation info");
        simInfoButton.addActionListener(this);
        randomIgnitionButton = new UIButton("Toggle random ignition");
        randomIgnitionButton.setForeground(Color.RED);
        randomIgnitionButton.addActionListener(this);
        randomIgnitionButton.setToolTipText("Activate to create random ignition point on the map during simulation(Humidity has to be 1 or lower).");


        ignitionControlButton = new UIButton("Set ignition");
        ignitionControlButton.setToolTipText("Set ignition = Left mouse click on the map to ignite a green cell, Exhaust- Left mouse click on the map to exhaust a red cell");
        ignitionControlButton.addActionListener(this);

        randomIgnitionButton.setPreferredSize(new Dimension(140, 30));
        ignitionControlButton.setPreferredSize(new Dimension(140, 30));

        tickRateTextField.setEnabled(false);
        simDurationTextField.setEnabled(false);
        simInfoButton.setEnabled(false);

        randomIgnitionButton.setEnabled(false);
        simInfoButton.setEnabled(false);
        ignitionControlButton.setEnabled(false);

        simInfoPanel.setBounds(10, 490, 300, 200);

        simInfoPanel.add(simInfoLabel);
        simInfoPanel.add(tickRateTextField);
        simInfoPanel.add(simDurationTextField);
        simInfoPanel.add(randomIgnitionButton);
        simInfoPanel.add(ignitionControlButton);
        simInfoPanel.add(simInfoButton);

        this.add(simInfoPanel);

        // Restart button
        restartButton = new UIButton("Restart");
        restartButton.addActionListener(this);

        // Exit button
        exitButton = new UIButton("Exit program");
        exitButton.addActionListener(this);

        // Simulation control panel
        UIPanel simControlPanel = new UIPanel();
        startButton = new UIButton("Start simulation");
        pauseButton = new UIButton("Pause");
        stopButton = new UIButton("Stop");
        resumeButton = new UIButton("Resume");

        startButton.addActionListener(this);
        pauseButton.addActionListener(this);
        stopButton.addActionListener(this);
        resumeButton.addActionListener(this);

        startButton.setEnabled(false);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        resumeButton.setEnabled(false);

        startButton.setPreferredSize(new Dimension(250, 50));
        pauseButton.setPreferredSize(new Dimension(125, 30));
        resumeButton.setPreferredSize(new Dimension(125, 30));
        stopButton.setPreferredSize(new Dimension(250, 40));

        simControlPanel.setBounds(10, 700, 300, 160);

        simControlPanel.add(startButton);
        simControlPanel.add(pauseButton);
        simControlPanel.add(resumeButton);
        simControlPanel.add(stopButton);

        this.add(simControlPanel);

        // Map panel
        mapPanel = new UIPanel();
        mapPanel.setBounds(320, 70, 750, 750);
        mapPanel.setLayout(null);

        this.add(mapPanel);

        // Statistics panel
        statPanel = new UIPanel();
        statPanel.setLayout(new BoxLayout(statPanel, BoxLayout.Y_AXIS));
        statPanel.setBounds(1080, 70, 400, 650);

        // Create labels for the statistics to be displayed on
        UILabel statLabel = new UILabel("Statistics", 20);
        UILabel remainingVegCellsLabel = new UILabel("Remaining vegetation cells:", 16);
        UILabel remainingVegCellsNumberLabel = new UILabel("", 18);
        statPanel.addLabel(remainingVegCellsNumberLabel);
        UILabel burningCellsLabel = new UILabel("Number of burning cells:", 16);
        UILabel burningCellsNumberLabel = new UILabel("", 18);
        statPanel.addLabel(burningCellsNumberLabel);
        UILabel burntCellsLabel = new UILabel("Number of burnt cells (% of green area):", 16);
        UILabel burntCellsNumberLabel = new UILabel("", 18);
        statPanel.addLabel(burntCellsNumberLabel);
        UILabel burntPerTickLabel = new UILabel("Average number of burnt cells per hour:", 16);
        UILabel burntPerTickNumberLabel = new UILabel("", 18);
        statPanel.addLabel(burntPerTickNumberLabel);

        UILabel tickInfoLabel = new UILabel("Hour  of ", 16);
        statPanel.addLabel(tickInfoLabel);

        // Align all labels to the center
        statLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        remainingVegCellsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        remainingVegCellsNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        burningCellsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        burntCellsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        burntPerTickLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        burningCellsNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        burntCellsNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        burntPerTickNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tickInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add all elements to the statPanel
        statPanel.add(Box.createVerticalStrut(5));
        statPanel.add(statLabel);
        statPanel.add(Box.createVerticalStrut(20));
        statPanel.add(remainingVegCellsLabel);
        statPanel.add(Box.createVerticalStrut(10));
        statPanel.add(remainingVegCellsNumberLabel);
        statPanel.add(Box.createVerticalStrut(50));
        statPanel.add(burningCellsLabel);
        statPanel.add(Box.createVerticalStrut(10));
        statPanel.add(burningCellsNumberLabel);
        statPanel.add(Box.createVerticalStrut(50));
        statPanel.add(burntCellsLabel);
        statPanel.add(Box.createVerticalStrut(10));
        statPanel.add(burntCellsNumberLabel);
        statPanel.add(Box.createVerticalStrut(50));
        statPanel.add(burntPerTickLabel);
        statPanel.add(Box.createVerticalStrut(10));
        statPanel.add(burntPerTickNumberLabel);
        statPanel.add(Box.createVerticalStrut(50));
        statPanel.add(tickInfoLabel);

        UIPanel buttonPanel = new UIPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBounds(1080, 730, 400, 90);
        restartButton.setPreferredSize(new Dimension(170, 75));
        exitButton.setPreferredSize(new Dimension(170, 75));
        exitButton.addActionListener(this);

        buttonPanel.add(restartButton);
        buttonPanel.add(exitButton);

        this.add(buttonPanel);
        this.add(statPanel);

        // General settings
        this.setLayout(null);
        this.setVisible(true);
    }

    // Methods
    // Method to add mouse click listener to text fields for clearing text
    private void addMouseListenerToTextField(UITextField textField) {
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                textField.setText(""); // Clear text when clicked
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
