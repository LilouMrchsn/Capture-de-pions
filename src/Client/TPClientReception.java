import java.net.*;
import java.io.*;
/**
 * <b>TPClientReception est la classe qui reçoit la map envoyée par le serveur.</b>
 * <p>
 * Le thread tourne jusqu'à ce que la fenêtre soit coupée, en lisant toutes les 0,1 seconde les données reçues par le client, les enregistrant dans état
 * </p>
 * 
 * @see TPServeurThread
 * 
 * @author Lilou Marchesin 
 * @version 1.0
 */
public class TPClientReception extends Thread
{

  /**
   * Thread contenant la lecture du lecteur
   */
  private Thread thread;

  /**
   * Lecteur du client
   */
  private DataInputStream lecteur;

  /**
   * Client auquel appartient le thread
   */
  private TPClient client;

  /**
   * Constructeur du thread
   * @param client client auquel appartient le thread
   * @param lecteur lecteur du client
   */
  public TPClientReception(TPClient client, DataInputStream lecteur)
  {
    this.client = client;
    this.lecteur = lecteur;
  }


  /**
   * Réceptionne la carte actuelle du jeu tous les 0.1 seconde
   */
  @Override
  public void run() {
    try{
      while(true){
        try{
          Thread.sleep(100);
        }catch(InterruptedException ie){
          System.out.println(ie);
        }
        lecteur.read(client.getEtat(), 0, 2*10*10);
        if(client.getEtat()[0] == 5 || client.getEtat()[0] == 6) break;
      }
    }
    catch(IOException io){
      System.out.println(io);
    }
  }
}