package org.example.visualiser;

import org.example.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/*
* TODO: Make reset button clear all VertexPanels
*  Enable buttons after simulation is done.
*  Show stats to the left (put it inside the timer loop)
*
* */

public class MAPFVisualiser2 extends JFrame implements ActionListener {

    // DATA MEMBERS

    // Constants
    public static final int RAMP_PANEL_WIDTH = 1100;
    public static final int RAMP_PANEL_HEIGHT = 700;
    public static final int RAMP_PANEL_X = 420;
    public static final int RAMP_PANEL_Y = 70;
    public static final int MIN_RAMP_LENGTH_FOR_WIDTH = 10;
    public static final int ASTAR = 0;
    public static final int ICTS = 1;
    public static final int CBS = 2;
    public static final int CBSWP = 3;

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

    // Data structures used by simulate()
    HashMap<Integer, VertexPanel> rampVertices;
    HashMap<Agent, Color> agentColours;
    ArrayList<Color> assignedColours;

    // Constructors
    public MAPFVisualiser2(Ramp ramp, MAPFSolution astarSolution, MAPFSolution ictsSolution,
                           MAPFSolution cbsSolution, MAPFSolution cbswpSolution) {

        // Store solutions as data members
        this.astarSolution = astarSolution;
        this.ictsSolution = ictsSolution;
        this.cbsSolution = cbsSolution;
        this.cbswpSolution = cbswpSolution;

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

        startAstarButton.addActionListener(this);
        startICTSButton.addActionListener(this);
        startCBSButton.addActionListener(this);
        startCBSwPButton.addActionListener(this);

        this.add(algSelectionPanel);


        // Ramp panel
        rampPanel = new UIPanel();
        rampPanel.setBounds(RAMP_PANEL_X, RAMP_PANEL_Y, RAMP_PANEL_WIDTH, RAMP_PANEL_HEIGHT);

        this.add(rampPanel);
        rampPanel.setLayout(null);

        paintRamp(rampPanel, ramp);

        // Assign a unique colour to each agent
        Set<Agent> allAgents = astarSolution.getSolutionSet().getLast().getAgentLocations().keySet();
        this.assignedColours = new ArrayList<>();
        this.agentColours = new HashMap<>();

        for(Agent agent : allAgents) {
            assignColourToAgent(agent);
        }

        // General settings
        this.setLayout(null);
        this.setVisible(true);
    }


    // Methods
    private Color generateRandomColour() {
        Random rand = new Random();
        float hue = rand.nextFloat();
        float sat = 0.6f + (rand.nextFloat() * 0.4f);
        float bright = 0.6f + (rand.nextFloat() * 0.4f);

        return Color.getHSBColor(hue, sat, bright);
    }

    private void assignColourToAgent(Agent agent) {
        this.agentColours.computeIfAbsent(agent, _ -> generateRandomColour());
    }

    private int xStart(int vertexLength, int widthTracker) {
        // Returns the x of where the next VertexPanel should start
        return vertexLength * widthTracker;
    }

    private int yStart(int vertexLength, int heightTracker) {
        // Returns the y of where the next queue VertexPanel should start
        return (RAMP_PANEL_HEIGHT / 6) + vertexLength * heightTracker;
    }

    private void paintRamp(UIPanel rampPanel, Ramp ramp) {

        // Ramp variables
        // Width = ramp length + exit vertices
        int rampWidth = (ramp.getRampLength() < MIN_RAMP_LENGTH_FOR_WIDTH)
                ? MIN_RAMP_LENGTH_FOR_WIDTH + 2
                : ramp.getRampLength() + 2;

        int vertexLength = RAMP_PANEL_WIDTH / rampWidth;
        int rampVerticalStart = (RAMP_PANEL_HEIGHT / 6);

        int widthTracker = 0;

        rampVertices = new HashMap<>();

        // Ramp and exits
        VertexPanel surfaceExit = new VertexPanel(xStart(vertexLength, widthTracker++), rampVerticalStart,
                vertexLength, vertexLength, ramp.getSurfaceExit());
        rampPanel.add(surfaceExit);
        rampVertices.put(ramp.getSurfaceExit(), surfaceExit);

        for (Integer vertex : ramp.getVerticesInActualRamp()) {
            VertexPanel rampVertex = new VertexPanel(xStart(vertexLength, widthTracker++), rampVerticalStart,
                    vertexLength, vertexLength, vertex);
            rampPanel.add(rampVertex);
            rampVertices.put(vertex, rampVertex);
        }

        VertexPanel undergroundExit = new VertexPanel(xStart(vertexLength, widthTracker), rampVerticalStart,
                vertexLength, vertexLength, ramp.getUndergroundExit());
        rampPanel.add(undergroundExit);
        rampVertices.put(ramp.getUndergroundExit(), undergroundExit);

        // Surface queue
        widthTracker = 1;
        int heightTracker = ramp.getVerticesInSurfaceQ().size();
        for (Integer vertex : ramp.getVerticesInSurfaceQ()) {
            VertexPanel surfaceQVertex = new VertexPanel(xStart(vertexLength, widthTracker),
                    yStart(vertexLength, heightTracker--), vertexLength, vertexLength, vertex);
            rampPanel.add(surfaceQVertex);
            rampVertices.put(vertex, surfaceQVertex);
        }

        // Underground queue
        widthTracker = ramp.getRampLength();
        heightTracker = 1;
        for (Integer vertex : ramp.getVerticesInUndergroundQ()) {
            VertexPanel undergroundQVertex = new VertexPanel(xStart(vertexLength, widthTracker),
                    yStart(vertexLength, heightTracker++), vertexLength, vertexLength, vertex);
            rampPanel.add(undergroundQVertex);
            rampVertices.put(vertex, undergroundQVertex);
        }

        // Passing bays
        int[] passBaysAdjVertex = ramp.getPassBaysAdjVertex();
        ArrayList<Integer> recordedAdjVertices = new ArrayList<>();
        ArrayList<ArrayList<Integer>> passingBayVertices = ramp.getPassingBayVertices();
        for (int i = 0; i < passBaysAdjVertex.length; i++) {
            heightTracker = -1;
            if (i == 0) {
                VertexPanel passBayVertexFirst = new VertexPanel(xStart(vertexLength, passBaysAdjVertex[i]),
                        yStart(vertexLength, heightTracker), vertexLength, vertexLength,
                        passingBayVertices.get(i).getFirst());
                VertexPanel passBayVertexSecond = new VertexPanel(xStart(vertexLength, passBaysAdjVertex[i] + 1),
                        yStart(vertexLength, heightTracker), vertexLength, vertexLength,
                        passingBayVertices.get(i).getLast());
                rampPanel.add(passBayVertexFirst);
                rampPanel.add(passBayVertexSecond);
                rampVertices.put(passingBayVertices.get(i).getFirst(), passBayVertexFirst);
                rampVertices.put(passingBayVertices.get(i).getLast(), passBayVertexSecond);

                // If a second passing bay starts at the same ramp vertex, it should be painted below instead
                recordedAdjVertices.add(passBaysAdjVertex[i]);
            }
            else {
                heightTracker = (recordedAdjVertices.contains(passBaysAdjVertex[i])) ? 1 : -1;
                VertexPanel passBayVertexFirst = new VertexPanel(xStart(vertexLength, passBaysAdjVertex[i]),
                        yStart(vertexLength, heightTracker), vertexLength, vertexLength,
                        passingBayVertices.get(i).getFirst());
                VertexPanel passBayVertexSecond = new VertexPanel(xStart(vertexLength, passBaysAdjVertex[i] + 1),
                        yStart(vertexLength, heightTracker), vertexLength, vertexLength,
                        passingBayVertices.get(i).getLast());
                rampPanel.add(passBayVertexFirst);
                rampPanel.add(passBayVertexSecond);
                rampVertices.put(passingBayVertices.get(i).getFirst(), passBayVertexFirst);
                rampVertices.put(passingBayVertices.get(i).getLast(), passBayVertexSecond);
            }
        }
    }

    private void simulate(MAPFSolution solution) {
        // Simulate the given solution

        // Disable all buttons
        startAstarButton.setEnabled(false);
        startICTSButton.setEnabled(false);
        startCBSButton.setEnabled(false);
        startCBSwPButton.setEnabled(false);

        ArrayList<MAPFState> solutionStates = solution.getSolutionSet();

        // Show the solution
        Timer timer = new Timer(400, new ActionListener() {
            int timeStep = 0;
            int maxTimeStep = solutionStates.size();
            ArrayList<VertexPanel> prevLocations = new ArrayList<>();

            @Override
            public void actionPerformed(ActionEvent e) {

                // Get all agents' current locations
                MAPFState currentState = solutionStates.get(timeStep);
                HashMap<Agent, Integer> agentLocations = currentState.getAgentLocations();

                // Clear any vertices
                for(VertexPanel vertex : prevLocations) {
                    vertex.clearAgent();
                }
                prevLocations.clear();

                // Print the agents (as dots) in their respective vertex locations
                for (Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
                    Agent agent = entry.getKey();
                    int location = entry.getValue();

                    VertexPanel vertex = rampVertices.get(location);
                    vertex.addAgent(agentColours.get(agent));
                    prevLocations.add(vertex);
                }

                timeStep++;
                if(timeStep >= maxTimeStep) {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == startAstarButton) {
            simulate(astarSolution);
        }

        if (e.getSource() == startICTSButton) {
            simulate(ictsSolution);
        }

        if (e.getSource() == startCBSButton) {
            simulate(cbsSolution);
        }

        if (e.getSource() == startCBSwPButton) {
            simulate(cbswpSolution);
        }
    }
}
