package org.example.visualiser;

import org.example.Agent;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class VertexPanel extends JPanel {

    // Data member
    private final int id;
    private Color agentColour = null;
    private boolean hasPriority = false;
    private final int width;
    private final int height;

    // Constructor
    public VertexPanel(int x, int y, int width, int height, int digit) {
        this.setBounds(x, y, width, height);
        this.setBorder(new LineBorder(Color.BLACK, 2));

        // Show vertex number
        UILabel vertexLabel = new UILabel(String.valueOf(digit), SwingConstants.CENTER);
        vertexLabel.setFont(new Font("Calibri", Font.PLAIN, 16));
        this.add(vertexLabel, BorderLayout.CENTER);

        this.id = digit;
        this.width = width;
        this.height = height;
    }


    // Methods

    public void addAgent(Color colour, boolean priority) {
        // Invoked whenever the simulation says that an agent is on this vertex
        this.agentColour = colour;
        this.hasPriority = priority;
        repaint();          // Invokes the paintComponent method
    }

    public void clearAgent() {
        // Invoked whenever the simulation says that an agent leaves
        this.agentColour = null;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Update the indicator of an agent present/absent

        super.paintComponent(g);

        if(this.agentColour != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            int radius = this.width / 2;
            int x = (this.width - radius) / 2;
            int y = (this.height - radius) / 2;
            g2d.setColor(agentColour);
            g2d.fillOval(x, y, radius, radius);

            // Create a red border for all agents with higher priority
            if (hasPriority) {
                g2d.setColor(Color.RED);
                g2d.setStroke(new BasicStroke(4));
                g2d.drawOval(x, y, radius, radius);
            }
        }
    }
}
