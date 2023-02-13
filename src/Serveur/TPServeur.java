import java.net.*;
import java.io.*;
import java.util.*;

/**
 * <b>TPServeur est la classe qui contient le serveur du jeu.</b>
 * <p>
 * Cette classe est la classe à exécuter quand on veut lancer le serveur. Elle marche avec les classes TPMapManager et TPServeurThread
 * </p>
 * 
 * @see TPServeurThread
 * @see TPMapManager
 * 
 * @author Lilou Marchesin 
 * @version 1.0
 */

public class TPServeur {
	/** 
     * @Vector liste des joueurs actuellement connectés
	 * @see#addJoueur(TPService)
	 * @see#delJoueur(TPService)
     */
	private Vector<TPServeurThread> listeJoueurs = new Vector<>();
	
	/**
     * La carte actuelle, évoluant au fil des des déplacements
     * 
     * @see Zero#getEtat()
     */
	private byte [] etat = new byte [2*10*10];

	/**
	 * Contient tous les membres de l'équipe 1
	 * 
	 * @see#getEquipe1()
	 */
	private HashMap<Integer, Boolean> equipe1 = new HashMap<>();

	/**
	 * Contient tous les membres de l'équipe 2
	 * 
	 * @see#getEquipe2()
	 */
	private HashMap<Integer, Boolean> equipe2 = new HashMap<>();

	/**
	 * Indique si la partie est terminée ou non
	 */
	private boolean termine = false;

	/**
	 * Lance le serveur et attends que des clients se connectent à celui-ci
	 * @param args
	 */
	public static void main(String[] args){
		try{
			TPServeur serveur = new TPServeur();
			ServerSocket serveurSocket = new ServerSocket(8800);
			TPMapManager mapManager = new TPMapManager(serveur);
			mapManager.start();
			while(!serveur.termine){
			  Socket client = serveurSocket.accept();
			  DataInputStream lecteur = new DataInputStream(client.getInputStream());
			  new TPServeurThread(client, serveur, lecteur.read(), lecteur.read(), lecteur.read(), lecteur.read()).start();
			}
			serveurSocket.close();
		}
		catch(IOException e){
		 	e.printStackTrace();
		}
	}


	/**
	 * Ajoute un joueur à la liste des joueurs quand il se connecte
	 * @param st
	 */
	synchronized public void addJoueur(TPServeurThread st)
  	{
    	this.listeJoueurs.add(st);
  	}

	/**
     * Supprime un joueur de la liste des joueurs quand il se déconnecte
	 * @param st
	 */
	synchronized public void delJoueur(TPServeurThread st)
	{
		delPoint(st.getX(), st.getY());
		if(st.getEquipe() == 1){
			this.equipe1.remove((int)st.getId());
		}else{
			this.equipe2.remove((int)st.getId());
		}
		this.listeJoueurs.remove(st);
	}

	/**
	 * Retourne la valeur actuelle d'état
	 * @return la valeur d'état
	 */
	synchronized public byte[] getEtat(){
		return this.etat;
	}
	/**
	 * Retourne la liste de tous les threads de joueurs actuellement connectés
	 * @return la valeur de listeJoueurs
	 */
	synchronized public Vector<TPServeurThread> getListeJoueurs(){
		return this.listeJoueurs;
	}

	/**
	 * Ajoute un point sur la carte lors de la création du joueur, et l'ajoute à la bonne équipe
	 * @param posX la position en X sur la carte du point à mettre
	 * @param posY la position en Y sur la carte du point à mettre
	 * @param equipe l'equipe dont fait partie le joueur
	 * @param id l'identifiant du joueur
	 */
	synchronized public void addPoint(int posX, int posY, int equipe, int id){
		this.etat[posX*2 + posY*20] = 1;
		this.etat[posX*2 + posY*20 + 1] = (byte)equipe;
		if(equipe == 1){
			this.equipe1.put(id, true);
		}else{
			System.out.println(equipe);
			this.equipe2.put(id, true);
		}
	}

	/**
	 * Enlève de la carte le point du joueur en fonction de sa localisation
	 * @param posX la position en X sur la carte du point à enlever
	 * @param posY la position en Y sur la carte du point à enlever
	 */
	synchronized public void delPoint(int posX, int posY){
		this.etat[posX*2 + posY*20] = 0;
		this.etat[posX*2 + posY*20 + 1] = 0;
	}

	/**
	 *  Renvoie le thread du joueur situé en (X,Y)
	 * @param X la position en X sur la carte du point dont on recherche le joueur
	 * @param Y la position en Y sur la carte du point dont on recherche le joueur
	 * @return le thread du joueur situé à la position indiquée
	 */
	synchronized public TPServeurThread getServeurThreadParLocalisation(int X, int Y){
		for(TPServeurThread st : this.listeJoueurs){
			if(st.getX() == X && st.getY() == Y){
				return st;
			}
		}
		return null;
	}

	/**
	 * La fonction vérifie que pour un point avec une couleur donné, lorsque celui-ci est bougé, bloque ou non un point de l'équipe adverse. Si oui, le point qui est bloqué change de couleur et la variable bloquee du thread correspondant est passée à vraie. Sinon, rien ne se passe.
	 * @param X  la position en X sur la carte du point
	 * @param Y  la position en Y sur la carte du point
	 * @param couleur la couleur du point
	 */
	synchronized public void checkBloqueGeneral(int X, int Y, int couleur){
		byte droite1 = 0, droite2 = 0, gauche1 = 0, gauche2 = 0, haut1 = 0, haut2 = 0, bas1 = 0, bas2 = 0;
		int position = X*2 + Y*20;
		//droite = + 2, gauche = -2, haut = -20, bas = +20
		if(X + 1 < 9) droite1 = this.etat[position + 2];
		if(X + 2 < 9) droite2 = this.etat[position + 4];

		if(X - 1 > 0) gauche1 = this.etat[position - 2];
		if(X - 2 > 0) gauche2 = this.etat[position - 4];

		if(Y - 1 > 0) haut1 = this.etat[position - 20];
		if(Y - 2 > 0) haut2 = this.etat[position - 40];

		if(Y + 1 < 9) bas1 = this.etat[position + 20];
		if(Y + 2 < 9) bas2 = this.etat[position + 40];

		if(droite1 == 1 && droite2 == 1){
			if(this.etat[position + 2 + 1] != couleur && this.etat[position + 4 + 1] == couleur && this.etat[position + 2 + 1] <= 2 ){
				int nouvelleCouleur = (int)this.etat[(X + 1)*2+Y*20 + 1] + 2;
				this.etat[position + 2 + 1] = (byte) nouvelleCouleur;
				getServeurThreadParLocalisation(X + 1, Y).setBloque();
			}
		}
		
		if(gauche1 == 1 && gauche2 == 1){
			if(this.etat[position - 2 + 1] != couleur && this.etat[position - 4 + 1] == couleur && this.etat[position - 2 + 1] <= 2 ){
				int nouvelleCouleur = (int)this.etat[(X - 1)*2+Y*20 + 1] + 2;
				this.etat[position - 2 + 1] = (byte) nouvelleCouleur;
				getServeurThreadParLocalisation(X - 1, Y).setBloque();
			}
		}
		
		if(haut1 == 1 && haut2 == 1){
			if(this.etat[position - 20 + 1] != couleur && this.etat[position - 40 + 1] == couleur && this.etat[position - 20 + 1] <= 2 ){
				int nouvelleCouleur = (int)this.etat[X*2+(Y - 1)*20 + 1] + 2;
				this.etat[position - 20 + 1] = (byte) nouvelleCouleur;
				getServeurThreadParLocalisation(X, Y - 1).setBloque();
			}
		}
		
		if(bas1 == 1 && bas2 == 1){
			if(this.etat[position + 20 + 1] != couleur && this.etat[position + 40 + 1] == couleur && this.etat[position + 20 + 1] <= 2 ){
				int nouvelleCouleur = (int)this.etat[position + 20 + 1] + 2;
				this.etat[position + 20 + 1] = (byte) nouvelleCouleur;
				getServeurThreadParLocalisation(X, Y + 1).setBloque();
			}
		}

		if(bas1 == 1 && haut1 == 1){
			if(this.etat[position + 20 + 1] == this.etat[position - 20 + 1] && this.etat[position + 1] != this.etat[position - 20 + 1]){
				int nouvelleCouleur = (int)this.etat[position + 1] + 2;
				this.etat[position + 1] = (byte) nouvelleCouleur;
				getServeurThreadParLocalisation(X, Y).setBloque();
			}
		}

		if(gauche1 == 1 && droite1 == 1){
			if(this.etat[position - 2 + 1] == this.etat[position + 2 + 1] && this.etat[position + 1] != this.etat[position - 2 + 1]){
				int nouvelleCouleur = (int)this.etat[position + 1] + 2;
				this.etat[position + 1] = (byte) nouvelleCouleur;
				getServeurThreadParLocalisation(X, Y).setBloque();
			}
		}
	}

	/**
	 * Vérifie que l'espace dans lequel le point veut se déplacer est vide. Si oui, le déplace, sinon, laisse la situation inchangée
	 * @param dir direction dans laquelle le point veut aller
	 * @param oldX la position en X sur la carte du point avant le déplacement
	 * @param oldY la position en Y sur la carte du point avant le déplacement
	 * @param couleur la couleur du point
	 * @return la position du point en (X,Y)
	 */
	synchronized public int[] movePoint(int dir, int oldX, int oldY, int couleur){
		if(this.etat[(oldX)*2+oldY*20 + 1] == 3 ||  this.etat[(oldX)*2+oldY*20 + 1] == 4) return null;
		if(oldX >= 0 && oldX < 10 && oldY >= 0 && oldY < 10){

			this.etat[oldX*2+oldY*20] = 0;
			this.etat[oldX*2+oldY*20 + 1] = 0;
			
			switch(dir){
				case 0 /*droit*/ : {
					if(oldX < 9 && this.etat[(oldX + 1)*2+oldY*20] == 0){
						this.etat[(oldX + 1)*2+oldY*20] = 1;
						this.etat[(oldX + 1)*2+oldY*20 + 1] = (byte) couleur;

						return new int[]{oldX + 1, oldY};
					}
					break;
				}

				case 1 /*gauche*/ : {
					if(oldX > 0 && this.etat[(oldX - 1)*2+oldY*20] == 0){
						this.etat[(oldX - 1)*2+oldY*20] = 1;
						this.etat[(oldX - 1)*2+oldY*20 + 1] = (byte) couleur;
	
						return new int[]{oldX - 1, oldY};
					}
					break;
				}

				case 2 /*haut*/ :{
					if(oldY > 0 && this.etat[oldX*2+(oldY - 1)*20] == 0){
						this.etat[oldX*2+(oldY - 1)*20] = 1;
						this.etat[oldX*2+(oldY - 1)*20 + 1] = (byte) couleur;

						return new int[]{oldX, oldY - 1};
					}
					break;
				}

				case 3 /*bas*/ : {
					if(oldY < 9 && this.etat[oldX*2+(oldY + 1)*20] == 0){
						this.etat[oldX*2+(oldY + 1)*20] = 1;
						this.etat[oldX*2+(oldY + 1)*20 + 1] = (byte) couleur;

						return new int[]{oldX, oldY + 1};
					}
					break;
				}
			}
		}
		
		this.etat[oldX*2+oldY*20] = 1;
		this.etat[oldX*2+oldY*20 + 1] = (byte) couleur;
		return new int[]{oldX, oldY};
	}

	/**
	 * Quand un point est bloqué, il est noté comme tel dans son équipe. La fonction vérifie ensuite qu'il reste plus d'un joueur dans chaque équipe, si oui, la partie est erminée, sinon, la partie continue
	 * @param team équipe dont le joueur bloqué fait parti
	 * @param id identifiant du joueur
	 */
	public void fini(int team, int id){
		int vivant = 0;

		System.out.println("Team n°" + team);
		if(team == 1){
			System.out.println(equipe1.toString());
			this.equipe1.put(id, false);
			for (int _id : this.equipe1.keySet()){
				if(this.equipe1.get(_id) == true) vivant++;
			}
			if(vivant <= 1){
				this.etat[0] = 6;
			}
		}else{
			System.out.println(equipe2.toString());
			this.equipe2.put(id, false);
			for (int _id : this.equipe2.keySet()){
				if(this.equipe2.get(_id) == true) vivant++;
			}
			if(vivant <= 1){
				this.etat[0] = 5;
			}
		}
		this.termine = true;
	}
}
