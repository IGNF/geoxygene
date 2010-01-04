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
	InterfaceGeoxygene getFrameMirage() { return this.frameMirage; }

	public JButton b_=new JButton("-"); //$NON-NLS-1$
	public JCheckBox cAffichageEchelle=new JCheckBox("Echelle",false);
	public JCheckBox cVoirPositionCurseur=new JCheckBox("Voir position",false);
	public JLabel lX=new JLabel("X="); //$NON-NLS-1$
	public JLabel lY=new JLabel("Y="); //$NON-NLS-1$
	public JLabel lZ=new JLabel("Z="); //$NON-NLS-1$
	public JLabel lValPente=new JLabel("ValPente=");
	public JLabel lOrPente=new JLabel("OrPente=");

	public PanelBas(InterfaceGeoxygene frameMirage){
		this.frameMirage = frameMirage;

		//setBackground(new Color(190,190,255));
		setLayout(new FlowLayout(FlowLayout.LEFT));
		setFont(new Font("Arial",Font.PLAIN,9)); //$NON-NLS-1$

		this.b_.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				System.out.println(""); //$NON-NLS-1$
				System.out.println("============================================="); //$NON-NLS-1$
				System.out.println(""); //$NON-NLS-1$
			}});
		this.b_.setFont(getFont());
		this.b_.setSize(0,0);
		add(this.b_);

		this.cAffichageEchelle.setFont(getFont());
		this.cAffichageEchelle.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				getFrameMirage().getPanelVisu().setAffichageEchelle(PanelBas.this.cAffichageEchelle.isSelected());
				getFrameMirage().getPanelVisu().repaint();
			}});
		add(this.cAffichageEchelle);

		this.cVoirPositionCurseur.setFont(getFont());
		this.cVoirPositionCurseur.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (PanelBas.this.cVoirPositionCurseur.isSelected()) {
					getFrameMirage().getPanelVisu().suivrePositionCurseur = true;
				}
				else {
					getFrameMirage().getPanelVisu().suivrePositionCurseur = false;
				}
			}});
		add(this.cVoirPositionCurseur);
		this.lX.setFont(getFont());
		add(this.lX);
		this.lY.setFont(getFont());
		add(this.lY);
		this.lZ.setFont(getFont());
		add(this.lZ);
		this.lValPente.setFont(getFont());
		add(this.lValPente);		
		this.lOrPente.setFont(getFont());
		add(this.lOrPente);
	}
}
