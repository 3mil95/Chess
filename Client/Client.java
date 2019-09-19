import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Client implements ActionListener {

    Scanner sc = new Scanner(System.in);
    Socket socket;
    BufferedReader in;
    PrintWriter ut;
    String name;
    String[][] state ={
        {"BR","BH","BB","BQ","BK","BB","BH","BR"},
        {"BP","BP","BP","BP","BP","BP","BP","BP"},
        {"  ","  ","  ","  ","  ","  ","  ","  "},
        {"  ","  ","  ","  ","  ","  ","  ","  "},
        {"  ","  ","  ","  ","  ","  ","  ","  "},
        {"  ","  ","  ","  ","  ","  ","  ","  "},
        {"WP","WP","WP","WP","WP","WP","WP","WP"},
        {"wR","WH","WB","WQ","WK","WB","WH","WR"}};
    ChessGUI gui;
    String playerColor;
    int index = 0;
    Square[] pickedSquares = new Square[2];


    public Client(){
        connect("localhost", 4713);
        runText();
    }

    private void turnBord() {
        if (playerColor.equals("B")) {
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < state[0].length; j++) {
                    String s = state[i][j];
                    state[i][j] = state[7 - i][7 - j];
                    state[7 - i][7 - j] = s;
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        pickedSquares[index] = (Square) e.getSource();
        sendChoiceToServer(convertPos(e.getActionCommand()));
    }

    public void connect(String server, int port){
        try {
            socket=new Socket(server, port);
            ut=new PrintWriter(socket.getOutputStream());
            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ut.println(name = inputString("name: ")); ut.flush();
            System.out.println(in.readLine());
        } catch(Exception e){
            System.out.println(e.getLocalizedMessage());
        }
    }

    public String getChoiceFromServer(){
        try {
            return in.readLine();
        } catch(Exception e){
            System.out.println(e.getLocalizedMessage());
            return null;
        }
    }

    public void show() {
        show(state);
    }

    public String convertPos(String pos) {
        if (playerColor.equals("B")) {
            String[] posArray = pos.split(",");
            pos = Integer.valueOf((7- Integer.valueOf(posArray[0]))) + "," + Integer.valueOf((7- Integer.valueOf(posArray[1])));
        }
        return pos;
    }

    public void show(String[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++){
                String str = matrix[i][j] + ' ';
                System.out.print(str);
            }     
            System.out.println();
        }
    }

    private void swap(int i1, int j1, int i2, int j2){
        if (playerColor.equals("B")) {
            i1 = 7 - i1;
            i2 = 7 - i2;
            j1 = 7 - j1;
            j2 = 7 - j2;
        }
        state[i2][j2] = state[i1][j1];
        state[i1][j1] = "  ";
    }

    public void sendChoiceToServer(String choice){
        try{
            ut.println(choice); ut.flush();
        } catch(Exception e){
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void guiGame() {
        sendChoiceToServer("ok");
        gui = new ChessGUI(this, this);
        turnBord();
        gui.updateSquares();
        if (playerColor.equals("B")) {
            gui.setEnabed(false);
        }
        while(true) {
            String res = getChoiceFromServer();
            String[] resArrey = res.split(",");
            gui.setEnabed(true);
            swap(Integer.valueOf(resArrey[0]), Integer.valueOf(resArrey[1]),Integer.valueOf(resArrey[2]), Integer.valueOf(resArrey[3]));
            gui.updateSquares();
            while(true) {
                resArrey = res.split(",");
                System.out.println(res);
                if (resArrey[4].equals("move")) {
                    gui.pickSquare(pickedSquares[index]);
                    index = 1;
                }
                if (resArrey[4].equals("ok")) {
                    swap(Integer.valueOf(resArrey[0]), Integer.valueOf(resArrey[1]),Integer.valueOf(resArrey[2]), Integer.valueOf(resArrey[3]));
                    index = 0;
                    gui.updateSquares();
                    gui.setEnabed(false);
                    break;
                }
                res = getChoiceFromServer();
            } 
        }
    }

    private void gameText() {
        sendChoiceToServer("ok");
        gui = new ChessGUI(this, this);
        gui.updateSquares();
        
        while(true) {
            String res = getChoiceFromServer();
            String[] resArrey = res.split(",");

            swap(Integer.valueOf(resArrey[0]), Integer.valueOf(resArrey[1]),Integer.valueOf(resArrey[2]), Integer.valueOf(resArrey[3]));
            gui.updateSquares();
            show();
            while(true) {
                resArrey = res.split(",");
                System.out.println(res);
                if (resArrey[4].equals("ok")) {
                    swap(Integer.valueOf(resArrey[0]), Integer.valueOf(resArrey[1]),Integer.valueOf(resArrey[2]), Integer.valueOf(resArrey[3]));
                    gui.updateSquares();
                    show();
                    break;
                }
                String input = inputString(resArrey[4] + ": ");
                sendChoiceToServer(input);
                res = getChoiceFromServer();
            } 
        }
    }

    private void runText(){
        while(true) {
            String input = inputString("Name: ");
            sendChoiceToServer(input);
            if (input.equals("exit")) {
                break;
            }
            String res = getChoiceFromServer();
            System.out.println(res);
            if (res.equals("W") || res.equals("B")) {
                //gameText();
                playerColor = res;
                guiGame();
            }
        } 
        sc.close(); 
    }

    public String getString(int i, int j) {
        return state[i][j];
    }


    private String inputString(String prompt){
        System.out.print(prompt);
        String input = sc.nextLine();
       
        return input;
    }

    public static void main(String[] args) {
        new Client();
    }
}