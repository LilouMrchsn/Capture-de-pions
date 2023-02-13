import java.io.*;

/**
 * <b>TPMapManager est la classe qui contient un thread gérant la carte du jeu.</b>
 * <p>
 * Fonctionnement : Lorsque cette classe est lancée par TPServeur, un thread est créé et envoie toutes les 0,1 seconde la dernière version de la map
 * </p>
 * 
 * @see TPServeur
 * 
 * @author Lilou Marchesin 
 * @version 1.0
 */
public class TPMapManager extends Thread{
    /**
     * Serveur dont fait partie la carte
     */
    TPServeur tpServeur;

    /**
     * Constructeur du TPMapManager
     * @param tpServ serveur auquel est relié le thread
     */
    public TPMapManager(TPServeur tpServ){
        this.tpServeur = tpServ;
    }

    /**
     * Envoie à intervalles réguliers l'état actuel de la carte à tous les clients connectés au serveur
     */
    public void run(){
        DataOutputStream out;
        while(true){
            try{
                Thread.sleep(100);
            }catch(InterruptedException io){
                System.out.println(io);
            }
            if(tpServeur.getListeJoueurs() != null){
                for (TPServeurThread servThread : tpServeur.getListeJoueurs())
                {
                    out = (DataOutputStream) servThread.getEcrivain();
                    if (out != null)
                    {
                        try{
                            out.write(tpServeur.getEtat());
                            out.flush(); 
                        }catch(IOException ie){
                            System.out.println(ie);
                        }
                    }
                }
            }
	        
        }
        
    }
    


}
