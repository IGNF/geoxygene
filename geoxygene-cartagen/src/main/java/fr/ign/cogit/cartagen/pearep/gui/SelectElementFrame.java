package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

import org.apache.batik.ext.swing.GridBagConstants;

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
    super(I18N.getString("EditScaleMasterFrame.lblEditElement"));
    this.parent = parent;
    this.selectedLine = parent.getSelectedLine();
    combo = new JComboBox(selectedLine.getAllElements().toArray());
    combo.setPreferredSize(new Dimension(200, 20));
    combo.setMinimumSize(new Dimension(200, 20));
    combo.setMaximumSize(new Dimension(200, 20));
    JButton okButton = new JButton("OK");
    okButton.addActionListener(this);
    okButton.setActionCommand("ok");
    this.getContentPane().setLayout(new GridBagLayout());
    this.getContentPane().add(
        combo,
        new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstants.CENTER,
            GridBagConstants.HORIZONTAL, new Insets(2, 2, 2, 2), 1, 1));
    this.getContentPane().add(
        okButton,
        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstants.CENTER,
            GridBagConstants.NONE, new Insets(2, 2, 2, 2), 1, 1));
    this.setSize(300, 100);
    this.setMinimumSize(new Dimension(300, 100));
    this.setResizable(false);
    this.setLocationRelativeTo(this.parent);
    this.validate();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("ok")) {
      selectedLine.removeElement((ScaleMasterElement) combo.getSelectedItem());
      AddScaleMasterEltFrame frame = new AddScaleMasterEltFrame(parent,
          selectedLine, (ScaleMasterElement) combo.getSelectedItem());
      frame.setVisible(true);
      this.setVisible(false);
    } else
      this.setVisible(false);
  }

}
