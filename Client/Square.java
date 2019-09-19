import java.awt.*;
import javax.swing.*;

public class Square extends JButton{
    String text;
    private Color color;
    private Color pickColor;
    private Color errorColor;

    Square(){
        //setVisible(true);     
    }

    Square(Color c, Color pc, Color ec){
        color = c;
        pickColor = pc;
        errorColor = ec;
        setBackground(color);
    }

    void updateSquare(String text){
        setIcon(new ImageIcon(String.format("Icons/%s.png", text)));
        this.text = text;
    }

    String getSquareState(){
        return text;
    }

    public void updateSquare(Icon icon){
        setIcon(icon);
    }
    
    public void pickSquare() {
        setBackground(pickColor);
    }

    public void errorSquare() {
        setBackground(errorColor);
    }

    public void resetSquare() {
        setBackground(color);
    }

}