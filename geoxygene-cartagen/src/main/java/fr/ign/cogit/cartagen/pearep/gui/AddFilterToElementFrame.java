package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component.OGCFilterPanel;

public class AddFilterToElementFrame extends JFrame implements ActionListener {

	/****/
	private static final long serialVersionUID = 1L;
	private OGCFilterPanel filterPanel;
	private AddScaleMasterEltFrame parentFrame;
	private HashMap<Integer, JPanel> mapLevelPanel = new HashMap<Integer, JPanel>();
	private JPanel displayedPanel;
	private JButton btnBack, btnNext;
	private int level = 0;
	private final static int LEVELS = 2;
	private CartAGenDB db;

	public AddFilterToElementFrame(AddScaleMasterEltFrame frame) {
		super();
		this.setTitle(I18N.getString("AddScaleMasterEltFrame.filterFrameTitle"));
		this.parentFrame = frame;

		// ***********************************
		// a panel to define the OGC Filter
		Class<?> selectedClass = null;
		for (Class<?> classObj : this.parentFrame.getLine().getTheme()
				.getRelatedClasses()) {
			SourceDLM source = SourceDLM.valueOf((String) parentFrame
					.getComboDbs().getSelectedItem());
			GeneObjImplementation impl = parentFrame.getParent()
					.getImplFromName(source.name());
			if (impl.containsClass(classObj)) {
				selectedClass = classObj;
				break;
			}
		}
		this.filterPanel = new OGCFilterPanel(this, selectedClass);

		// ***********************************
		// a panel for the buttons
		JPanel pButtons = new JPanel();
		this.btnBack = new JButton(I18N.getString("MainLabels.lblBack"));
		this.btnBack.addActionListener(this);
		this.btnBack.setActionCommand("Back");
		this.btnBack.setPreferredSize(new Dimension(100, 40));
		this.btnBack.setEnabled(false);
		this.btnNext = new JButton(I18N.getString("MainLabels.lblNext"));
		this.btnNext.addActionListener(this);
		this.btnNext.setActionCommand("Next");
		this.btnNext.setPreferredSize(new Dimension(100, 40));
		JButton btnCancel = new JButton(I18N.getString("MainLabels.lblCancel"));
		btnCancel.addActionListener(this);
		btnCancel.setActionCommand("Cancel");
		btnCancel.setPreferredSize(new Dimension(100, 40));
		JButton btnQuery = new JButton("OK");
		btnQuery.addActionListener(this);
		btnQuery.setActionCommand("Ok");
		btnQuery.setPreferredSize(new Dimension(100, 40));
		pButtons.add(this.btnBack);
		pButtons.add(this.btnNext);
		pButtons.add(btnQuery);
		pButtons.add(btnCancel);
		pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));

		// ***********************************
		// final layout of the frame
		this.mapLevelPanel.put(0, this.filterPanel);
		this.displayedPanel = this.filterPanel;
		this.getContentPane().add(this.filterPanel);
		this.getContentPane().add(pButtons);
		this.getContentPane().setLayout(
				new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Cancel")) {
			this.setVisible(false);
		} else if (e.getActionCommand().equals("Ok")) {
			this.parentFrame.setFilter(filterPanel.getFilter());
			this.parentFrame.getFilterTxt().setEditable(true);
			this.parentFrame.getFilterTxt().setText(
					filterPanel.getFilter().toString());
			this.parentFrame.getFilterTxt().setEditable(false);
			this.setVisible(false);
		} else if (e.getActionCommand().equals("Back")) {
			this.level--;
			// first remove the displayed panel
			this.getContentPane().remove(this.displayedPanel);
			// change the displayed panel
			this.displayedPanel = this.mapLevelPanel.get(this.level);
			this.getContentPane().add(this.displayedPanel, 0);
			if (this.level == 0) {
				this.btnBack.setEnabled(false);
			}
			if (!this.btnNext.isEnabled()) {
				this.btnNext.setEnabled(true);
			}
			this.pack();
		} else if (e.getActionCommand().equals("Next")) {
			this.level++;
			// first remove the displayed panel
			this.getContentPane().remove(this.displayedPanel);
			// change the displayed panel
			this.displayedPanel = this.mapLevelPanel.get(this.level);
			this.getContentPane().add(this.displayedPanel, 0);
			if (this.level == LEVELS - 1) {
				this.btnNext.setEnabled(false);
			}
			if (!this.btnBack.isEnabled()) {
				this.btnBack.setEnabled(true);
			}
			this.pack();
		}
	}

	public OGCFilterPanel getFilterPanel() {
		return filterPanel;
	}

	public void setFilterPanel(OGCFilterPanel filterPanel) {
		this.filterPanel = filterPanel;
	}

	public AddScaleMasterEltFrame getParentFrame() {
		return parentFrame;
	}

	public void setParentFrame(AddScaleMasterEltFrame parentFrame) {
		this.parentFrame = parentFrame;
	}

}
