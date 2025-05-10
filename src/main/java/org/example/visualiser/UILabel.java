package org.example.visualiser;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;


public class UILabel extends JLabel{
    
    UILabel(String text, int fontSize) {
        
        this.setText(text);
        this.setFont(new Font("Calibri", Font.PLAIN, fontSize));
        this.setForeground(new Color(0xCCCCCC));
    }
}
