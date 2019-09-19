import java.awt.Button;
import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ChessGame{
    Piece[][] state ={
        {new Rook("B"),new Horse("B"),new Bishop("B"),new Queen("B"),new King("B"),new Bishop("B"),new Horse("B"),new Rook("B")},
        {new Pawn("B"),new Pawn("B"),new Pawn("B"),new Pawn("B"),new Pawn("B"),new Pawn("B"),new Pawn("B"),new Pawn("B")},
        {null,null,null,null,null,null,null,null},
        {null,null,null,null,null,null,null,null},
        {null,null,null,null,null,null,null,null},
        {null,null,null,null,null,null,null,null},
        {new Pawn("W"),new Pawn("W"),new Pawn("W"),new Pawn("W"),new Pawn("W"),new Pawn("W"),new Pawn("W"),new Pawn("W")},
        {new Rook("W"),new Horse("W"),new Bishop("W"),new Queen("W"),new King("W"),new Bishop("W"),new Horse("W"),new Rook("W")}};

    
    private Boolean pickState = true;
    private char[] players = {'W', 'B'};
    private int playerPlaying = 0;
    private int pickOneI, pickOneJ, pickTwoI, pickTwoJ;
    private Piece pickedPiece;

    ChessGame(){

    }

    public boolean move(int i, int j){
        boolean okMove;
        if (pickState) {
            okMove = pick(i, j);
            if (okMove){
                pickState = !pickState;       
            }
        } else {
            okMove = moveTo(i, j);
            if (okMove){    
                swap();
                swapPlayer();    
            }
            pickState = !pickState;
        }
        //System.out.println(okMove);
        return okMove;
    }

    public Boolean moveTo(int i, int j){
        pickTwoI = i;
        pickTwoJ = j;
        if (pickedPiece.checkMove()){
            pickedPiece.firstMove();  
            return true;
        }
        return false;
    }

    public Boolean pick(int i, int j){
        if (state[i][j] != null){
            if (state[i][j].getPlayer() == players[playerPlaying]){
                System.out.println(i + " " + j);
                pickOneI = i;
                pickOneJ = j;
                pickedPiece = state[pickOneI][pickOneJ];
                return true;
            }
        }
        return false;
    }

    private void swap(){
        state[pickTwoI][pickTwoJ] = state[pickOneI][pickOneJ];
        state[pickOneI][pickOneJ] = null;
    }

    private void swapPlayer(){
        playerPlaying++;
        if (playerPlaying >= players.length){
            playerPlaying = 0;
        }
        //System.out.println(players[playerPlaying]);
    }

    String getString(int i, int j){
        Piece piece = state[i][j];
        if (piece != null)
            return piece.toString();
        return null;
    }

    Piece stringToIntArray(String i){
        return state[Character.getNumericValue(i.charAt(0))][Character.getNumericValue(i.charAt(1))];
    }



    class Piece{
        String piecesString;
        char player;
        int[][] moves = {{-1,0},{0,-1},{1,0},{0,1}};
        int moveLength = 8;

        boolean isMoved = false;
    
        Piece(String ps){
            piecesString = ps;
            player = ps.charAt(0);
        }

        Piece(String ps, int[][] m){
            this(ps);
            moves = m;
        }
    
        @Override
        public String toString() {
            return piecesString;
        }
    
        public char getPlayer(){
            return player;
        }
    
        public boolean checkMove(){
            for (int i = 0; i < moves.length; i++){
                for (int n = 1; n <= moveLength; n++){
                    int ni = pickOneI + (moves[i][0] * n);
                    int nj = pickOneJ + (moves[i][1] * n);

                    if (ni >= 8 || ni < 0 || nj >= 8 || nj < 0)
                        break; 
                    else if (state[ni][nj] != null){
                        if (ni == pickTwoI && nj == pickTwoJ && state[ni][nj].getPlayer() != pickedPiece.getPlayer())
                        {
                            //System.out.println("[" + moves[i][0] + "," + moves[i][1] + "]" + n );
                            return true;
                        }
                        break;
                    }
                    if (ni == pickTwoI && nj == pickTwoJ){
                        //System.out.println("[" + moves[i][0] + "," + moves[i][1] + "]" + n );
                        return true;
                    }
                }
            }
            return false;
        }

        void firstMove(){
            isMoved = true;
        }
    
    }
    
    class Pawn extends Piece{

        int[][] move = {{-1,0}}; 
        int[][] takeMoves = {{-1,1},{-1,-1}};
        
    
        Pawn(String player){
            super(player + "P");
            moveLength = 2; 
            if (player.equals("B")){
                 move[0][0] = 1;
                 takeMoves[0][0] = 1;
                 takeMoves[1][0] = 1;
            }
            super.moves = move;
        }

        @Override
        void firstMove() {
            super.firstMove();
            moveLength = 1;
        }

        @Override
        public boolean checkMove() {
            boolean okMove = false;
            if (super.checkMove()){
                if (state[pickTwoI][pickTwoJ] != null){
                    if (state[pickTwoI][pickTwoJ].getPlayer() != pickedPiece.getPlayer())
                        okMove = false;
                }
                else 
                    okMove = true;
            }

            for (int i = 0; i < takeMoves.length; i++){
                int ni = pickOneI + takeMoves[i][0];
                int nj = pickOneJ + takeMoves[i][1];
                if (ni >= 8 || ni < 0 || nj >= 8 || nj < 0)
                    break; 

                if (state[ni][nj] != null){
                    if (ni == pickTwoI && nj == pickTwoJ && state[ni][nj].getPlayer() != pickedPiece.getPlayer())
                    {
                        //System.out.println("[" + takeMoves[i][0] + "," + takeMoves[i][1] + "]");
                        okMove = true;
                    }
                }      
            }     
            /*if (okMove){
                if (pickTwoI == 0 || pickTwoI == state.length - 1)
                    promotion("Q");
            }*/    
            return okMove;
        }
    }

    private void promotion(String newPiece){
        state[pickOneI][pickOneJ] = new Queen(state[pickOneI][pickOneJ].getPlayer() + "");
    }

    class Rook extends Piece{

        Rook(String player){
            super(player + "R");
        }
    }

    class Bishop extends Piece{

        final private int[][] move = {{1,1}, {1,-1}, {-1,-1}, {-1,1}};

        Bishop(String player){
            super(player + "B");
            super.moves = move;
        }
    }

    class Horse extends Piece{

        final private int[][] move = {{1,2}, {1,-2}, {-1,-2}, {-1,2}, {2,1}, {2,-1}, {-2,-1}, {-2,1}};

        Horse(String player){
            super(player + "H");
            super.moves = move;
            moveLength = 1;
        }
    }

    class Queen extends Piece{
        
        final private int[][] move = {{1,1}, {1,-1}, {-1,-1}, {-1,1}, {-1,0},{0,-1},{1,0},{0,1}};

        Queen(String player){
            super(player + "Q");
            super.moves = move;
            
        }
    }

    class King extends Piece{

        final private int[][] move = {{1,1}, {1,-1}, {-1,-1}, {-1,1}, {-1,0},{0,-1},{1,0},{0,1}};

        King(String player){
            super(player + "K");
            super.moves = move;
            moveLength = 1;     
        }
    }

}