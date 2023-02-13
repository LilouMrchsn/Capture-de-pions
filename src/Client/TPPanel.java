import java.awt.Panel;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.IOException;

/**
 * @author Alain BOUJU
 *
 */
public class TPPanel extends Panel {
	TPClient main=null;
	Button bDroit, bGauche, bHaut, bBas;

	public TPPanel(TPClient pTPClient)
	{
		this.main = pTPClient;
		// Button Droit
		bDroit=new Button("Droit");
		bDroit.addActionListener( new ListenBoutonDroit());
		this.add(bDroit);
		// Button Gauche
		bGauche =new Button("Gauche");
		bGauche.addActionListener( new ListenBoutonGauche());
		this.add(bGauche);
		// Button Haut
		bHaut =new Button("Haut");
		bHaut.addActionListener( new ListenBoutonHaut());
		this.add(bHaut);
		// Button Gauche
		bBas =new Button("Bas");
		bBas.addActionListener( new ListenBoutonBas());
		this.add(bBas);
	}

	class ListenBoutonDroit implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try{
				main.droit();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	class ListenBoutonGauche implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{	
			try{
				main.gauche();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	class ListenBoutonHaut implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try{
				main.haut();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
	class ListenBoutonBas implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			try{
				main.bas();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
}

