package fr.ign.cogit.geoxygene.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * @author julien Gaffuri
 * 6 mars 2007
 *
 */
public final class PanelGauche extends JPanel {
	private static final long serialVersionUID = -3719644987901188303L;

	//la fenetre a laquelle le panel est eventuellement lie
	private InterfaceGeoxygene frame = null;
	InterfaceGeoxygene getFrame() { return frame; }

	//
	public JCheckBox cSymbole=new JCheckBox("Afficher symboles",true);

	private JPanel panneau = null;
	public JPanel getPanneau() {
		if (panneau == null) panneau = new JPanel();
		return panneau;
	}

	private GridBagConstraints c_ = null;
	public GridBagConstraints getGBC() {
		if ( c_ == null ){
			c_=new GridBagConstraints();
			c_.gridwidth = GridBagConstraints.REMAINDER;
			c_.anchor=GridBagConstraints.NORTHWEST;
			c_.insets = new Insets(1,5,1,5);
		}
		return c_;
	}

	public PanelGauche(InterfaceGeoxygene frame){
		this.frame = frame;
		
		setFont(new Font("Arial",Font.PLAIN,9));
	
		getPanneau().setLayout(new GridBagLayout());
		getPanneau().setFont(getFont());

		//symbolisation
		cSymbole.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (cSymbole.isSelected()) {
					getFrame().getPanelVisu().affichageSymbolisation = true;
				}
				else {
					getFrame().getPanelVisu().affichageSymbolisation = false;
				}
			}});
		cSymbole.setFont(getFont());
		getPanneau().add(cSymbole);

		JScrollPane scroll = new JScrollPane(getPanneau(),ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(scroll);
	}

}
