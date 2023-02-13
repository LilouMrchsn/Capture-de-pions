import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter; // Window Event
import java.awt.event.WindowEvent; // Window Event

import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.net.*;
import java.io.*;

/**
 * <b>TPServeurThread est la classe qui contient le chaque client connecté.</b>
 * <p>
 * Cette classe permet d'afficher le client. Il envoie des les actions faites par le joueur au serveur, et reçoit grâce au thread dans TPClientReception la nouvelle carte à afficher.
 * </p>
 * 
 * @see TPClientReception
 * @see TPCanvas
 * @see TPPanel
 * 
 * @see TPServeurThread
* @author Alain BOUJU
*
*/
public class TPClient extends Frame {

	byte [] etat = new byte [2*10*10];
	int team;
	int x;
	int y;
	int number;
	int port = 2000;
	Socket socket = null;
	TPPanel tpPanel;
	TPCanvas tpCanvas;
	Timer timer;
	DataOutputStream ecrivain;
	DataInputStream lecteur;


	/** Constructeur */
	public TPClient(int number,int team, int x, int y)
	{
		setLayout(new BorderLayout());
		tpPanel = new TPPanel(this);
		add("North", tpPanel);
		tpCanvas = new TPCanvas(this.etat);
		add("Center", tpCanvas);
		this.number = number;
		this.x = x;
		this.y = y;
		this.team = team;
		try{
			this.socket = new Socket(InetAddress.getLocalHost(), 8800);
			this.ecrivain = new DataOutputStream(this.socket.getOutputStream());
			this.lecteur = new DataInputStream(this.socket.getInputStream());
		}catch(IOException io){}
		timer = new Timer();
		timer.schedule ( new MyTimerTask ( ) , 100,100) ;
	}


	/** Action vers droit
	 * @throws IOException
	 */
	public synchronized void droit() throws IOException {
		if(this.x < 10){
			this.ecrivain.write(0);
			this.ecrivain.flush();
		}
		tpCanvas.repaint();

	}
	/** Action vers gauche 
	 * @throws IOException 
	 * */
	public synchronized void gauche() throws IOException {
		if(this.x > 0){
			this.ecrivain.write(1);
			this.ecrivain.flush();
		}

		tpCanvas.repaint();

	}
	/** Action vers gauche 
	 * @throws IOException 
	 * */
	public synchronized void haut() throws IOException {
		if(this.y > 0){
			try{
				this.ecrivain.write(2);
				this.ecrivain.flush();
			}catch(IOException ie){
				System.out.println(ie);
			}
			
		}

		tpCanvas.repaint();

	}
	/** Action vers bas 
	 * @throws IOException */
	public synchronized void bas () throws IOException {
		if(this.y < 10){
			this.ecrivain.write(3);
			this.ecrivain.flush();
		}
		tpCanvas.repaint();

	}
	/** Pour rafraichir la situation
	 * @throws IOException */
	public synchronized void refresh () throws IOException {
		tpCanvas.repaint();
	}
	

	/** Initialisations 
	 * @throws IOException */
	public void minit(int number, int pteam, int px, int py) throws IOException {
		try{
		this.ecrivain.write((byte) number);
		this.ecrivain.flush();
		this.ecrivain.write((byte) pteam);
		this.ecrivain.flush();
		this.ecrivain.write((byte) px);
		this.ecrivain.flush();
		this.ecrivain.write((byte) py);
		this.ecrivain.flush();
		}catch(IOException ie){
			System.out.println(ie);
		}
	}

	/**
	 * Retourne l'état de la carte
	 * @return l'etat
	 */
	public byte[] getEtat(){
		return this.etat;
	}

	/**
	 * Retourne le socket utilisé par le client
	 * @return le socket
	 */
	public Socket getSocket(){
		return this.socket;
	}

	/**
	 * Retourne le lecteur du socket
	 * @return le lecteur
	 */
	public DataInputStream getLecteur(){
		return this.lecteur;
	}

	/**
	 * S'exécute lorsque le client est ouvert
	* @param args
	*/
	public static void main(String[] args) {
		System.out.println("args :"+args.length);

		if (args.length != 4) {
			System.out.println("Usage : java TPClient number color positionX positionY ");
			System.exit(0);
		}

		try {
			int number = Integer.parseInt(args[0]);
			int team = Integer.parseInt(args[1]);
			int x = Integer.parseInt(args[2]);
			int y = Integer.parseInt(args[3]);
		    
		    
			TPClient tPClient = new TPClient(number,team,x,y);
			tPClient.minit(number, team, x, y);


			// Pour fermeture
			tPClient.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

			// Create Panel back forward

			tPClient.pack();
			tPClient.setSize(1000, 1000+200);
			tPClient.setVisible(true);

			new TPClientReception(tPClient, tPClient.getLecteur()).start();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	/** Pour rafraichir */
	class MyTimerTask extends TimerTask{

		public void run ()
		{
			try {
				refresh();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
