package fr.ign.cogit.geoxygene.gui;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * @author julien Gaffuri
 *
 */
public final class PanelBas extends JPanel {
	private static final long serialVersionUID = -3719644987901188303L;

	//la fenetre a laquelle le panel est eventuellement lie
	private InterfaceGeoxygene frameMirage = null;
	InterfaceGeoxygene getFrameMirage() { return frameMirage; }

	public JButton b_=new JButton("-");
	public JCheckBox cAffichageEchelle=new JCheckBox("Echelle",false);
	public JCheckBox cVoirPositionCurseur=new JCheckBox("Voir position",false);
	public JLabel lX=new JLabel("X=");
	public JLabel lY=new JLabel("Y=");
	public JLabel lZ=new JLabel("Z=");
	public JLabel lValPente=new JLabel("ValPente=");
	public JLabel lOrPente=new JLabel("OrPente=");

	public PanelBas(InterfaceGeoxygene frameMirage){
		this.frameMirage = frameMirage;

		//setBackground(new Color(190,190,255));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setFont(new Font("Arial",Font.PLAIN,9));

		b_.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("");
				System.out.println("=============================================");
				System.out.println("");
			}});
		b_.setFont(getFont());
		b_.setSize(0,0);
		add(b_);

		cAffichageEchelle.setFont(getFont());
		cAffichageEchelle.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				getFrameMirage().getPanelVisu().setAffichageEchelle(cAffichageEchelle.isSelected());
				getFrameMirage().getPanelVisu().repaint();
			}});
		add(cAffichageEchelle);

		cVoirPositionCurseur.setFont(getFont());
		cVoirPositionCurseur.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (cVoirPositionCurseur.isSelected()) {
					getFrameMirage().getPanelVisu().suivrePositionCurseur = true;
				}
				else {
					getFrameMirage().getPanelVisu().suivrePositionCurseur = false;
				}
			}});
		add(cVoirPositionCurseur);
		lX.setFont(getFont());
		add(lX);
		lY.setFont(getFont());
		add(lY);
		lZ.setFont(getFont());
		add(lZ);
		lValPente.setFont(getFont());
		add(lValPente);		
		lOrPente.setFont(getFont());
		add(lOrPente);
	}
}
