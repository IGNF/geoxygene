package fr.ign.cogit.geoxygene.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * @author julien Gaffuri
 *
 */
public final class PanelDroit extends JPanel {
	private static final long serialVersionUID = -3719644987901188303L;

	//la fenetre a laquelle le panel est eventuellement lie
	private InterfaceGeoxygene frame = null;
	InterfaceGeoxygene getFrame() { return frame; }

	public JPanel pSelection=new JPanel(new GridBagLayout());
	public JLabel lNbSelection=new JLabel("Nb=0");
	public JCheckBox cVoirSelection=new JCheckBox("Afficher selection",true);
	public JButton bViderSelection=new JButton("Annuler selection");

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

	public PanelDroit(InterfaceGeoxygene frame){
		this.frame = frame;

		setLayout(new GridBagLayout());
		setFont(new Font("Arial", Font.PLAIN,9));

		GridBagConstraints c=new GridBagConstraints();
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.anchor=GridBagConstraints.FIRST_LINE_START;
		c.insets = new Insets(1,5,1,5);

		//selection
		pSelection.setFont(getFont());
		pSelection.setBorder(BorderFactory.createTitledBorder("Sélection"));

		lNbSelection.setFont(getFont());
		pSelection.add(lNbSelection, c);
		
		cVoirSelection.setFont(getFont());
		pSelection.add(cVoirSelection, c);

		bViderSelection.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				getFrame().getPanelVisu().objetsSelectionnes.clear();
				lNbSelection.setText("Nb=0");
				getFrame().getPanelVisu().repaint();
			}});
		bViderSelection.setFont(getFont());
		pSelection.add(bViderSelection, c);

		add(pSelection, getGBC());
		
	}

}
