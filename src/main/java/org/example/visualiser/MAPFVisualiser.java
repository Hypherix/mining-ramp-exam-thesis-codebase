package org.example.visualiser;

import org.example.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

/*
* TODO: Make reset button clear all VertexPanels
*  Enable buttons after simulation is done.
*  Show stats to the left (put it inside the timer loop)
*
* */

public class MAPFVisualiser extends JFrame implements ActionListener {

    // DATA MEMBERS

    // Constants
    public static final int RAMP_PANEL_WIDTH = 1100;
    public static final int RAMP_PANEL_HEIGHT = 700;
    public static final int RAMP_PANEL_X = 420;
    public static final int RAMP_PANEL_Y = 70;
    public static final int MIN_RAMP_LENGTH_FOR_WIDTH = 10;
    public static final int IN_GOAL = 0;

    // Buttons
    UIButton submitTicksButton;
    UIButton startICTSButton;
    UIButton startAstarButton;
    UIButton startCBSButton;
    UIButton startCBSwPButton;
    UIButton resetButton;
    UIButton pauseButton;
    UIButton resumeButton;

    // Text fields
    UITextField ticksPerSecText;

    // Solutions
    MAPFSolution ictsSolution;
    MAPFSolution astarSolution;
    MAPFSolution cbsSolution;
    MAPFSolution cbswpSolution;

    // Panels
    UIPanel simInfoPanel;
    UIPanel algSelectionPanel;
    UIPanel rampPanel;

    // Labels
    UILabel simInfoLabel;
    UILabel algSelectionLabel;
    UILabel currentAlgorithmLabel;
    UILabel currentAlgorithmEmptyLabel;
    UILabel timeToSolveLabel;
    UILabel timeToSolveNumberLabel;
    UILabel costLabel;
    UILabel costNumberLabel;
    UILabel timeStepLabel;
    UILabel timeStepNumberLabel;
    UILabel ticksPerSecLabel;
    UILabel ticksPerSecNumberLabel;

    // Data structures used by simulate()
    HashMap<Integer, VertexPanel> rampVertices;
    HashMap<Agent, Color> agentColours;
    ArrayList<Color> assignedColours;
    Timer simTimer;
    Ramp ramp;
    int tickLength;

    // Booleans
    boolean astarNull = true;
    boolean ictsNull = true;
    boolean cbsNull = true;
    boolean cbswpNull = true;

    // Constructors
    public MAPFVisualiser(Ramp ramp, MAPFSolution astarSolution, MAPFSolution ictsSolution,
                          MAPFSolution cbsSolution, MAPFSolution cbswpSolution) {

        // Store solutions as data members
        this.astarSolution = astarSolution;
        this.ictsSolution = ictsSolution;
        this.cbsSolution = cbsSolution;
        this.cbswpSolution = cbswpSolution;
        this.ramp = ramp;

        if (astarSolution != null) {
            astarNull = false;
        }
        if (ictsSolution != null) {
            ictsNull = false;
        }
        if (cbsSolution != null) {
            cbsNull = false;
        }
        if (cbswpSolution != null) {
            cbswpNull = false;
        }


        // UI main attributes
        this.setTitle("Ramp traffic simulator");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 1000);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // UI main label
        UILabel mainLabel = new UILabel("Ramp traffic simulator", Font.BOLD, 30);
        mainLabel.setBounds(700, 5, 300, 50);
        this.add(mainLabel);

        // Simulation info panel
        simInfoPanel = new UIPanel();
        simInfoPanel.setLayout(new BoxLayout(simInfoPanel, BoxLayout.Y_AXIS));
        simInfoPanel.setBounds(10, 70, 400, 350);

        // Simulation info labels
        simInfoLabel = new UILabel("Simulation info", Font.BOLD, 20);
        currentAlgorithmLabel = new UILabel("Current algorithm:", Font.BOLD, 16);
        currentAlgorithmEmptyLabel = new UILabel(" ", 18);
        simInfoPanel.addLabel(currentAlgorithmEmptyLabel);
        timeToSolveLabel = new UILabel("Time to solve (ms):", Font.BOLD, 16);
        timeToSolveNumberLabel = new UILabel(" ", 18);
        simInfoPanel.addLabel(timeToSolveLabel);
        costLabel = new UILabel("Solution cost:", Font.BOLD, 16);
        costNumberLabel = new UILabel(" ", 18);
        simInfoPanel.addLabel(costNumberLabel);
        timeStepLabel = new UILabel("Current time step:", Font.BOLD, 16);
        timeStepNumberLabel = new UILabel(" ", 18);
        simInfoPanel.addLabel(timeStepNumberLabel);
        ticksPerSecLabel = new UILabel("Ticks per second:", Font.BOLD, 16);
        ticksPerSecNumberLabel = new UILabel(" ", 18);

        // Align all labels to the center
        simInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentAlgorithmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentAlgorithmEmptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeToSolveLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeToSolveNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        costLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        costNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeStepLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timeStepNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ticksPerSecLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ticksPerSecNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add all elements to the simInfoPanel
        simInfoPanel.add(Box.createVerticalStrut(5));
        simInfoPanel.add(simInfoLabel);
        simInfoPanel.add(Box.createVerticalStrut(10));
        simInfoPanel.add(currentAlgorithmLabel);
        simInfoPanel.add(Box.createVerticalStrut(5));
        simInfoPanel.add(currentAlgorithmEmptyLabel);
        simInfoPanel.add(Box.createVerticalStrut(10));
        simInfoPanel.add(timeToSolveLabel);
        simInfoPanel.add(Box.createVerticalStrut(5));
        simInfoPanel.add(timeToSolveNumberLabel);
        simInfoPanel.add(Box.createVerticalStrut(10));
        simInfoPanel.add(costLabel);
        simInfoPanel.add(Box.createVerticalStrut(5));
        simInfoPanel.add(costNumberLabel);
        simInfoPanel.add(Box.createVerticalStrut(10));
        simInfoPanel.add(timeStepLabel);
        simInfoPanel.add(Box.createVerticalStrut(5));
        simInfoPanel.add(timeStepNumberLabel);
        simInfoPanel.add(Box.createVerticalStrut(10));
        simInfoPanel.add(ticksPerSecLabel);
        simInfoPanel.add(Box.createVerticalStrut(5));
        simInfoPanel.add(ticksPerSecNumberLabel);

        this.add(simInfoPanel);


        // Algorithm selection panel
        algSelectionPanel = new UIPanel();
        algSelectionPanel.setBounds(10, 430, 400, 300);

        algSelectionLabel = new UILabel("        Algorithm selection        ", Font.BOLD, 20);
        ticksPerSecText = new UITextField("Valid ticks: 1-20", "Specify the number of ticks per second (1-20)");
        submitTicksButton = new UIButton("Submit");
        startAstarButton = new UIButton("A*");
        startICTSButton = new UIButton("ICTS");
        startCBSButton = new UIButton("CBS");
        startCBSwPButton = new UIButton("CBSwP");
        pauseButton = new UIButton("Pause");
        resumeButton = new UIButton("Resume");
        resetButton = new UIButton("Reset");

        algSelectionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ticksPerSecText.setAlignmentX(Component.CENTER_ALIGNMENT);
        startAstarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startICTSButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startCBSButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startCBSwPButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        pauseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension buttonSize = new Dimension(175, 50);
        submitTicksButton.setPreferredSize(new Dimension(100, 40));
        startAstarButton.setPreferredSize(buttonSize);
        startICTSButton.setPreferredSize(buttonSize);
        startCBSButton.setPreferredSize(buttonSize);
        startCBSwPButton.setPreferredSize(buttonSize);
        pauseButton.setPreferredSize(buttonSize);
        resumeButton.setPreferredSize(buttonSize);
        resetButton.setPreferredSize(buttonSize);

        algSelectionPanel.add(algSelectionLabel);
        algSelectionPanel.add(Box.createVerticalStrut(5));
        algSelectionPanel.add(ticksPerSecText);
        algSelectionPanel.add(submitTicksButton);
        algSelectionPanel.add(Box.createVerticalStrut(5));
        algSelectionPanel.add(startAstarButton);
        algSelectionPanel.add(startICTSButton);
        algSelectionPanel.add(Box.createVerticalStrut(5));
        algSelectionPanel.add(startCBSButton);
        algSelectionPanel.add(startCBSwPButton);
        algSelectionPanel.add(Box.createVerticalStrut(5));
        algSelectionPanel.add(pauseButton);
        algSelectionPanel.add(resumeButton);
        algSelectionPanel.add(Box.createVerticalStrut(5));
        algSelectionPanel.add(resetButton);

        addMouseListenerToTextField(ticksPerSecText);
        submitTicksButton.addActionListener(this);
        startAstarButton.addActionListener(this);
        startICTSButton.addActionListener(this);
        startCBSButton.addActionListener(this);
        startCBSwPButton.addActionListener(this);
        pauseButton.addActionListener(this);
        resumeButton.addActionListener(this);
        resetButton.addActionListener(this);

        startAstarButton.setEnabled(false);
        startICTSButton.setEnabled(false);
        startCBSButton.setEnabled(false);
        startCBSwPButton.setEnabled(false);
        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        resetButton.setEnabled(true);

        this.add(algSelectionPanel);


        // Ramp panel
        rampPanel = new UIPanel();
        rampPanel.setBounds(RAMP_PANEL_X, RAMP_PANEL_Y, RAMP_PANEL_WIDTH, RAMP_PANEL_HEIGHT);

        this.add(rampPanel);
        rampPanel.setLayout(null);

        paintRamp(rampPanel);

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

    private void paintRamp(UIPanel rampPanel) {

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
        resetButton.setEnabled(false);
        pauseButton.setEnabled(true);

        timeToSolveNumberLabel.setText(String.valueOf(solution.getObtainTime()));

        ArrayList<MAPFState> solutionStates = solution.getSolutionSet();

        // Show the solution
        simTimer = new Timer(tickLength, new ActionListener() {

            int timeStep = 0;
            int surfaceExit = ramp.getSurfaceExit();
            int undergroundExit = ramp.getUndergroundExit();
            int cost = 0;
            final int maxTimeStep = solutionStates.size();
            ArrayList<VertexPanel> prevLocations = new ArrayList<>();
            MAPFState currentState;
            HashMap<Agent, Integer> agentLocations;
            HashSet<Agent> agentInGoal = new HashSet<>();

            @Override
            public void actionPerformed(ActionEvent e) {

                // Get all agents' current locations
                currentState = solutionStates.get(timeStep);
                agentLocations = currentState.getAgentLocations();

                // Clear any vertices
                for(VertexPanel vertex : prevLocations) {
                    vertex.clearAgent();
                }
                prevLocations.clear();

                // Print the agents (as dots) in their respective vertex locations
                for (Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
                    Agent agent = entry.getKey();
                    int location = entry.getValue();

                    if (agent.direction == Constants.DOWN && location == undergroundExit) {
                        agentInGoal.add(agent);
                    }
                    else if (agent.direction == Constants.UP && location == surfaceExit) {
                        agentInGoal.add(agent);
                    }

                    if (!agentInGoal.contains(agent)) {
                        costNumberLabel.setText(String.valueOf(++cost));
                    }

                    VertexPanel vertex = rampVertices.get(location);
                    vertex.addAgent(agentColours.get(agent));
                    prevLocations.add(vertex);
                }

                timeStepNumberLabel.setText(String.valueOf(timeStep++));

                if(timeStep >= maxTimeStep) {
                    if (!astarNull) {
                        startAstarButton.setEnabled(true);
                    }
                    if (!ictsNull) {
                        startICTSButton.setEnabled(true);
                    }
                    if (!cbsNull) {
                        startCBSButton.setEnabled(true);
                    }
                    if (!cbswpNull) {
                        startCBSwPButton.setEnabled(true);
                    }

                    pauseButton.setEnabled(false);
                    resumeButton.setEnabled(false);
                    resetButton.setEnabled(true);

                    ((Timer) e.getSource()).stop();
                }
            }
        });
        simTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == submitTicksButton) {

            try {
                int ticksPerSec = Integer.parseInt(ticksPerSecText.getText());
                if (ticksPerSec >= 1 && ticksPerSec <= 20) {
                    tickLength = 1000 / ticksPerSec;

                    // If a new tick rate is submitted during pause
                    if(simTimer != null) {
                        simTimer.setDelay(tickLength);
                    }

                    ticksPerSecNumberLabel.setText(String.valueOf(tickLength));

                    if (!astarNull) {
                        startAstarButton.setEnabled(true);
                    }
                    if (!ictsNull) {
                        startICTSButton.setEnabled(true);
                    }
                    if (!cbsNull) {
                        startCBSButton.setEnabled(true);
                    }
                    if (!cbswpNull) {
                        startCBSwPButton.setEnabled(true);
                    }
                }
                else {
                    JOptionPane.showMessageDialog(null, "Enter a number between 1-20");
                }
            }
            catch (NumberFormatException nex) {
                JOptionPane.showMessageDialog(null, "Enter a number between 1-20");
            }
        }

        else if (e.getSource() == startAstarButton) {
            currentAlgorithmEmptyLabel.setText("A*");

            if (astarSolution != null) {
                simulate(astarSolution);
            }
        }

        else if (e.getSource() == startICTSButton) {
            currentAlgorithmEmptyLabel.setText("ICTS");

            if (ictsSolution != null) {
                simulate(ictsSolution);
            }
        }

        else if (e.getSource() == startCBSButton) {
            currentAlgorithmEmptyLabel.setText("CBS");

            if (cbsSolution != null) {
                simulate(cbsSolution);
            }
        }

        else if (e.getSource() == startCBSwPButton) {
            currentAlgorithmEmptyLabel.setText("CBSw/P");

            if (cbswpSolution != null) {
                simulate(cbswpSolution);
            }
        }

        else if (e.getSource() == pauseButton) {
            if(simTimer != null && simTimer.isRunning()) {
                simTimer.stop();
            }
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(true);
        }

        else if (e.getSource() == resumeButton) {
            if (simTimer != null && !simTimer.isRunning()) {
                simTimer.start();
            }
            pauseButton.setEnabled(true);
            resumeButton.setEnabled(false);
        }

        else if (e.getSource() == resetButton) {
            // Clear everything on the ramp
            for(VertexPanel vertex : rampVertices.values()) {
                vertex.clearAgent();
            }
            currentAlgorithmEmptyLabel.setText(" ");
            timeToSolveNumberLabel.setText(" ");
            costNumberLabel.setText(" ");
            timeStepNumberLabel.setText(" ");
        }
    }

    private void addMouseListenerToTextField(UITextField textField) {
        // Remove text from the text field when clicked on
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textField.setText("");
            }
        });
    }
}
