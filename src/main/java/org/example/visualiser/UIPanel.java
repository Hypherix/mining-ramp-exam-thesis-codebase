package org.example.visualiser;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JPanel;

public class UIPanel extends JPanel {
    
    // Used for statPanel to provide the Statistic class with text fields to
    // display the statistics on
    private ArrayList<UILabel> labels = new ArrayList<>();
    
    UIPanel() {
        this.setBackground(new Color(0xd6dbdf));
    }
    
    public void addLabel(UILabel label) {
        labels.add(label);
    }
    
    public UILabel getLabel(int index) {
        return labels.get(index);
    }
}
