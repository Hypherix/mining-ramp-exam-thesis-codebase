package org.rampTraffic.visualiser;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;


public class UILabel extends JLabel{
    
    UILabel(String text, int fontSize) {
        this.setText(text);
        this.setFont(new Font("Calibri", Font.PLAIN, fontSize));
        this.setForeground(Color.BLACK);
    }

    UILabel(String text, int font, int fontSize) {
        this.setText(text);

        switch(font) {
            case 0:
                this.setFont(new Font("Calibri", Font.PLAIN, fontSize));
                break;
            case 1:
                this.setFont(new Font("Calibri", Font.BOLD, fontSize));
                break;
            case 2:
                this.setFont(new Font("Calibri", Font.ITALIC, fontSize));
                break;
            default:
                this.setFont(new Font("Calibri", Font.PLAIN, fontSize));
                break;
        }

        this.setForeground(Color.BLACK);
    }
}
