package org.rampTraffic.visualiser;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTextField;

public class UITextField extends JTextField{
    
    UITextField(String text, String toolTip) {
        this.setPreferredSize(new Dimension(250, 40));
        this.setFont(new Font("Calibri", Font.PLAIN, 15));
        this.setText(text);
        this.setToolTipText(toolTip);
    }
}
