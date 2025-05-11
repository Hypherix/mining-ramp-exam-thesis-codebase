package org.example.visualiser;

import org.example.MAPFScenario;
import org.example.MAPFSolution;
import org.example.Ramp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MAPFVisualiser2 extends JFrame implements ActionListener {

    // DATA MEMBERS

    // Buttons
    UIButton startICTSButton;
    UIButton startAstarButton;
    UIButton startCBSButton;
    UIButton startCBSwPButton;
    UIButton resetButton;
    UIButton exitButton;

    // Solutions
    MAPFSolution ictsSolution;
    MAPFSolution astarSolution;
    MAPFSolution cbsSolution;
    MAPFSolution cbswpSolution;

    // Panels
    UIPanel simInfoPanel;
    UIPanel algSelectionPanel;
    UIPanel rampPanel;


    // Constructors
    public MAPFVisualiser2(Ramp ramp/*MAPFSolution ictsSolution, MAPFSolution astarSolution,
                           MAPFSolution cbsSolution, MAPFSolution cbswpSolution*/) {

        // UI main attributes
        this.setTitle("Ramp traffic simulator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 1000);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // UI main label
        UILabel mainLabel = new UILabel("Ramp traffic simulator", 30);
        mainLabel.setBounds(700, 5, 300, 50);
        this.add(mainLabel);

        // Simulation info panel
        simInfoPanel = new UIPanel();
        simInfoPanel.setLayout(new BoxLayout(simInfoPanel, BoxLayout.Y_AXIS));
        simInfoPanel.setBounds(10, 70, 400, 400);

        // Simulation info labels
        UILabel simInfoLabel = new UILabel("Simulation info", 20);
        UILabel currentAlgorithmLabel = new UILabel("Current algorithm:", 16);
        UILabel currentAlgorithmEmptyLabel = new UILabel("", 18);
        simInfoPanel.addLabel(currentAlgorithmEmptyLabel);
        UILabel costLabel = new UILabel("Solution cost:", 16);
        UILabel costNumberLabel = new UILabel("", 18);
        simInfoPanel.addLabel(costNumberLabel);
        UILabel timeStepLabel = new UILabel("Current time step:", 16);
        UILabel timeStepNumberLabel = new UILabel("", 18);
        simInfoPanel.addLabel(timeStepNumberLabel);

        // Align all labels to the center
        simInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentAlgorithmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentAlgorithmEmptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        costLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        costNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeStepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeStepNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add all elements to the simInfoPanel
        simInfoPanel.add(Box.createVerticalStrut(5));
        simInfoPanel.add(simInfoLabel);
        simInfoPanel.add(Box.createVerticalStrut(20));
        simInfoPanel.add(currentAlgorithmLabel);
        simInfoPanel.add(Box.createVerticalStrut(10));
        simInfoPanel.add(currentAlgorithmEmptyLabel);
        simInfoPanel.add(Box.createVerticalStrut(50));
        simInfoPanel.add(costLabel);
        simInfoPanel.add(Box.createVerticalStrut(10));
        simInfoPanel.add(costNumberLabel);
        simInfoPanel.add(Box.createVerticalStrut(50));
        simInfoPanel.add(timeStepLabel);
        simInfoPanel.add(Box.createVerticalStrut(10));
        simInfoPanel.add(timeStepNumberLabel);

        this.add(simInfoPanel);


        // Algorithm selection panel
        algSelectionPanel = new UIPanel();
        algSelectionPanel.setBounds(10, 480, 400, 200);

        UILabel algSelectionLabel = new UILabel("        Algorithm selection        ", 20);
        startAstarButton = new UIButton("A*");
        startICTSButton = new UIButton("ICTS");
        startCBSButton = new UIButton("CBS");
        startCBSwPButton = new UIButton("CBSwP");
        resetButton = new UIButton("Reset");

        algSelectionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        startAstarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startICTSButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startCBSButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startCBSwPButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension buttonSize = new Dimension(175, 50);
        startAstarButton.setPreferredSize(buttonSize);
        startICTSButton.setPreferredSize(buttonSize);
        startCBSButton.setPreferredSize(buttonSize);
        startCBSwPButton.setPreferredSize(buttonSize);
        resetButton.setPreferredSize(buttonSize);

        algSelectionPanel.add(algSelectionLabel);
        simInfoPanel.add(Box.createVerticalStrut(5));
        algSelectionPanel.add(startAstarButton);
        algSelectionPanel.add(startICTSButton);
        simInfoPanel.add(Box.createVerticalStrut(5));
        algSelectionPanel.add(startCBSButton);
        algSelectionPanel.add(startCBSwPButton);
        simInfoPanel.add(Box.createVerticalStrut(5));
        algSelectionPanel.add(resetButton);

        this.add(algSelectionPanel);


        // Ramp panel
        rampPanel = new UIPanel();
        rampPanel.setBounds(420, 70, 1100, 700);

        this.add(rampPanel);

        paintRamp(ramp);


        // General settings
        this.setLayout(null);
        this.setVisible(true);
    }




    // Methods
    private void paintRamp(Ramp ramp) {
        // Paint the ramp
        // TODO NEXT
        //  Afterwards, fix simulation

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
