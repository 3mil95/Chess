import java.net.*;
import java.io.*;
import java.util.*;


public class Server4713 {
    public static void main( String[] args) {
        try {
            ServerSocket sock = new ServerSocket(4713,100);
			while (true) 
			
            new Lobby(sock.accept()).start();
        }
        catch(IOException e)
            {System.err.println(e);
        }
    }
} 


class Client {
	String name;
	BufferedReader in;
	PrintWriter ut;
	
	Client(Socket socket) {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			ut= new PrintWriter(socket.getOutputStream());
		}
		catch(IOException e) {
			System.err.println(e);
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getMessage() {
		try {
			return in.readLine();
		}
			catch(Exception e) {
			System.err.println(e);
		}
		return null;
	}

	public void sendMessage(String message) {
		ut.println(message);
        ut.flush();
	}
}


class Lobby extends Thread {

	static public List<Game> games = new ArrayList<Game>();	
	private Client client;

	Lobby(Socket sock) {
		this.client = new Client(sock);
	}

 	String getGames() {
		StringBuilder sb = new StringBuilder();
		for (Game g: games) {
			sb.append(g.toString() + ";");
		}
		return sb.toString();
	}

	public void run() {
		try {
			client.setName(client.getMessage());
			System.out.println(client.getName());
			client.sendMessage(getGames());
			while(true) {
				String m = client.getMessage();
				if (m.equals("exit")) {
					break;
				}
				if (m.equals("new")) {
					client.sendMessage("new game name");
					String name = client.getMessage();
					Game newGame = new Game(name, client);
					games.add(newGame);
					//newGame.start();
					break;
				}
				try {
					int n = Integer.valueOf(m);
					Game g = games.get(n);
					if (g.isFull()) {
						client.sendMessage("game " + g +" is full");
					} else {
						g.addPlayer(client);
					}
					break;
				} catch(Exception e) {
					System.out.println(client.getName() + ": " + m);
					client.sendMessage("ok");
				}

			}
		}
			catch(Exception e) {
			System.err.println(e);
		}
		System.out.println("done!");
	}
}

class Game extends Thread {
	private String name;
	public List<Client> players = new ArrayList<Client>();
	private int numOfPlayer = 0;
	private int maxPlayer = 2;
	private ChessGame game;
	private String prevMove = "4,4,4,4";

	Game(String name, Client client) {
		this.name = name;
		addPlayer(client);
	}

	@Override
	public String toString() {
		return name + " " + String.valueOf(numOfPlayer) + "/" + String.valueOf(maxPlayer); 
	}

	public boolean isFull() {
		return numOfPlayer == maxPlayer;
	}

	public void addPlayer(Client client) {
		players.add(client);
		numOfPlayer++;
		System.out.println(this);
		if (numOfPlayer > 1) {
			this.start();
		}
	}

	boolean doMove(int player) {
		players.get(player).sendMessage(prevMove + ",pick");
		while(true) {
			String pick = players.get(player).getMessage();
			if (pick.equals("exit")) {
				return true;
			}

			String[] pickArrey = pick.split(",");	

			if (!game.move(Integer.valueOf(pickArrey[0]), Integer.valueOf(pickArrey[1]))) {
				players.get(player).sendMessage(prevMove + ",pick");
				continue;
			}
			players.get(player).sendMessage(prevMove + ",move");
			String move = players.get(player).getMessage();
			pickArrey = move.split(",");
			if (game.move(Integer.valueOf(pickArrey[0]), Integer.valueOf(pickArrey[1]))) {
				prevMove = pick + "," + move;
				players.get(player).sendMessage(prevMove + ",ok");
				break;
			}
			players.get(player).sendMessage(prevMove + ",pick");
		}
		return false;
	}


	@Override
	public void run() {
		game = new ChessGame();
		String[] playerColers = new String[2];
		int i = Math.toIntExact(Math.round(Math.random()));
		int firstClient = i;
		int secondClient = (i + 1) % maxPlayer;
		
		playerColers[firstClient] = "W";
		playerColers[secondClient] = "B";

		players.get(firstClient).sendMessage(playerColers[firstClient]);
		players.get(secondClient).sendMessage(playerColers[secondClient]);

		for (Client c : players) {
			String m = c.getMessage();
		}

		while(true) {
			if (doMove(i)) {
				break;
			}
			i = (i + 1) % maxPlayer;
		}
	System.out.println("game: " + name + " done!");	
	}
}
