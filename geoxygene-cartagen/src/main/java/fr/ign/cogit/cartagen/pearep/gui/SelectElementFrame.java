package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;

public class SelectElementFrame extends JFrame implements ActionListener {

	/****/
	private static final long serialVersionUID = 1L;
	private JComboBox combo;
	private EditScaleMasterFrame parent;
	private ScaleLine selectedLine;

	public SelectElementFrame(EditScaleMasterFrame parent) {
		super(I18N.getString("EditScaleMasterFrame.lblEditElt"));
		this.parent = parent;
		this.selectedLine = parent.getSelectedLine();
		combo = new JComboBox(selectedLine.getLine().values().toArray());
		combo.setPreferredSize(new Dimension(140, 20));
		combo.setPreferredSize(new Dimension(140, 20));
		combo.setPreferredSize(new Dimension(140, 20));
		JButton okButton = new JButton("OK");
		okButton.addActionListener(this);
		okButton.setActionCommand("ok");
		this.add(combo);
		this.add(okButton);
		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("ok")) {
			AddScaleMasterEltFrame frame = new AddScaleMasterEltFrame(parent,
					selectedLine, (ScaleMasterElement) combo.getSelectedItem());
			frame.setVisible(true);
			this.setVisible(false);
		} else
			this.setVisible(false);
	}

}
