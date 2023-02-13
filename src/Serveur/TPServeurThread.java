import java.net.*;
import java.io.*;

/**
 * <b>TPServeurThread est la classe qui contient le chaque client connecté.</b>
 * <p>
 * Cette classe est la classe à exécuter quand on veut lancer le serveur. Elle marche avec les classes TPMapManager et TPServeurThread
 * </p>
 * 
 * @see TPServeurThread
 * 
 * @author Lilou Marchesin 
 * @version 1.0
 */
public class TPServeurThread extends Thread{
  /**
   * contient le thread du joueur
   */
  private Thread thread;

  /**
   * socket du serveur
   */
  private Socket socket;

  /**
   * flux d'écriture qui permet d'envoyer un messaye au client
   */
  private DataOutputStream ecrivain;

  /**
   * flux de lecture qui permet de recevoir un messaye au client
   */
  private DataInputStream lecteur;

  /**
   * serveur auquel est connecté le client
   */
  private TPServeur tpServ;

  /**
   * Positions actuelles du joueur, ainsi que l'équipe dans laquelle il est et son identifiant
   */
  private int posX, posY, equipe, idJoueur;

  /**
   * Indicateur de si le joueur a été bloqué ou pas
   */
  private boolean bloque;

  /**
   * Constructeur du thread pour un client
   * @param s socket du serveur
   * @param tpServ serveur auquel est connecté le client
   * @param id identifiant du joueur
   * @param equipe équipe du joueur
   * @param posX position initiale du point en X
   * @param posY position initiale du point en Y
   */
  TPServeurThread(Socket s, TPServeur tpServ, int id, int equipe, int posX, int posY)
  {
    System.out.println("client reçu dans la classe TPServeurThread");
    this.tpServ=tpServ;
    this.socket=s;
    this.idJoueur = id;
    this.equipe = equipe;
    this.posX = posX -1;
    this.posY = posY -1;

    tpServ.addJoueur(this);

    thread = new Thread(this);
  }

  /**
   * Retourne le flux d'écriture
   * @return l'ecrivain
   */
  public DataOutputStream getEcrivain(){
    return this.ecrivain;
  }

  /**
   * Retourne la position en X du joueur
   * @return X
   */
  public int getX(){
    return this.posX;
  }

  /**
   * Retourne la position en Y du joueur
   * @return Y
   */

  public int getY(){
    return this.posY;
  }


  /**
   * Retourne l'équipe du joueur
   * @return l'équipe
   */
  public int getEquipe(){
    return this.equipe;
  }


  /**
   * Retourne l'identifiant du joueur
   * @return l'idJoueur
   */
  public int getIdJoueur(){
    return this.idJoueur;
  }

  /**
   * Tourne tant que le client est connecté. Permet de gérer les déplacements du client
   */
  @Override
  public void run()
  {
    // on indique dans la console la connexion d'un nouveau client
    System.out.println("Un nouveau client s'est connecte, de l'équipe " + this.idJoueur + "de equipe " + this.equipe);
    System.out.println("Il démarre en X = " + this.posX + " et Y = " + this.posY);

    this.tpServ.addPoint(this.posX, this.posY, this.equipe, this.idJoueur);
    try
    {
      this.ecrivain = new DataOutputStream(this.socket.getOutputStream());
      this.lecteur = new DataInputStream(this.socket.getInputStream());
      while(!bloque)
      {
        int[] positions = tpServ.movePoint(this.lecteur.read(), this.posX, this.posY, this.equipe);
        if(positions != null){
          this.posX = positions[0];
          this.posY = positions[1];
          tpServ.checkBloqueGeneral(this.posX, this.posY, this.equipe);
        }
      }
      while(true){
      }
    }
    catch (Exception e){
      System.out.println(e);
      try{
        tpServ.delJoueur(this);
        this.socket.close();
      }catch(IOException ie){
        System.out.println(ie);
      }
    }
    finally{
      tpServ.delJoueur(this);
    }
  }


  /**
   * Si le joueur est bloqué, change la valeur de bloqué à true et demande au serveur de vérifier si le jeu est terminé
   */
  public void setBloque() {
      this.bloque = true;
      tpServ.fini(this.equipe, this.idJoueur);
  }

}