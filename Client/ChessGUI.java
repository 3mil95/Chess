import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ChessGUI extends JFrame{

    private int size = 8;
    private Square[][] board;
    private Color nextColor;
    private Color nextColorPick;
    private Color nextColorError;

    private Color darkColor = new Color(153, 102, 51);
    private Color darkColorPick = new Color(253, 202, 51);
    private Color darkColorError = new Color(253, 102, 51);

    private Color liteColor = new Color(241, 228, 218);
    private Color liteColorPick = new Color(241, 228, 118);
    private Color liteColorError = new Color(241, 128, 118);

    Client cGame;

    public ChessGUI(ActionListener listener, Client g){
        super(g.name);
        //super(new GridLayout(8,8));
        setLayout(new GridLayout(8,8));
        cGame = g;
        board = new Square[size][size];
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
    


        

        nextColor = liteColor;
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++){
                Square newSquare = new Square(nextColor, nextColorPick, nextColorError);
                board[i][j] = newSquare;
                newSquare.addActionListener(listener);
                newSquare.setActionCommand(String.valueOf(i) + "," + String.valueOf(j));
                newSquare.updateSquare(cGame.getString(i,j));


                newSquare.setBackground(nextColor);
                if (j != size - 1)
                    changToNextColor();
                
                add(newSquare);             
            }
        } 
        setVisible(true);  
    }

    private void changToNextColor(){
        if (nextColor == liteColor) {
            nextColor = darkColor;
            nextColorPick = darkColorPick;
            nextColorError = darkColorError;
        } else {
            nextColor = liteColor;
            nextColorPick = liteColorPick;
            nextColorError = liteColorError;
        }
    }

    public void updateSquares(){
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++){
                board[i][j].updateSquare(cGame.getString(i,j));            
            }
        } 
    }

    public void setEnabed(boolean state) {
        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++){
                board[i][j].setEnabled(state);           
            }
        } 
    }

    public void swop(String n, String m){
        System.out.println(n + " " + m);
        String pes = board[Character.getNumericValue(n.charAt(0))][Character.getNumericValue(n.charAt(1))].getSquareState();
        board[Character.getNumericValue(n.charAt(0))][Character.getNumericValue(n.charAt(1))].updateSquare(board[Character.getNumericValue(m.charAt(0))][Character.getNumericValue(m.charAt(1))].getSquareState());
        board[Character.getNumericValue(m.charAt(0))][Character.getNumericValue(m.charAt(1))].updateSquare(pes);
    }
    
    public void pickSquare(Square square) {
        if (square != null)
            square.pickSquare();
    }

    public void errorSquare(Square square){
        if (square != null)
            square.errorSquare();
    }

    public void resetSquare(Square square) {
        if (square != null)
            square.resetSquare();
    }

}
