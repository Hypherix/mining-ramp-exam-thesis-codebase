package org.rampTraffic.visualiser;

import java.awt.Color;
import javax.swing.JButton;


public class UIButton extends JButton{
    
    UIButton(String text) {
        
        this.setForeground(new Color(0x333333));
        this.setBackground(new Color(0xCCCCCC));
        this.setText(text);
    }
}
