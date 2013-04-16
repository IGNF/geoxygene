package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.pearep.derivation.XMLParser;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.XMLFileFilter;

public class EditPeaRepParamsFrame extends JFrame implements ActionListener {

  /****/
  private static final long serialVersionUID = 1L;

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("save")) {
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new XMLFileFilter());
      int returnVal = fc.showSaveDialog(this);
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File file = fc.getSelectedFile();
      this.saveToXml(file);
    } else if (e.getActionCommand().equals("cancel"))
      this.setVisible(false);
  }

  public EditPeaRepParamsFrame() {
    super();
    this.setTitle(I18N.getString("EditPeaRepParamsFrame.frameTitle"));

    // a panel for the buttons
    JPanel pButtons = new JPanel();
    JButton btnSave = new JButton(I18N.getString("MainLabels.lblSave"));
    btnSave.addActionListener(this);
    btnSave.setActionCommand("save");
    JButton btnCancel = new JButton(I18N.getString("MainLabels.lblCancel"));
    btnCancel.addActionListener(this);
    btnCancel.setActionCommand("cancel");
    pButtons.add(btnSave);
    pButtons.add(btnCancel);
    pButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));

    // a panel for the output scale and the export folder
    JPanel pDefinition = new JPanel();
    // TODO
    pDefinition.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pDefinition.setLayout(new BoxLayout(pDefinition, BoxLayout.X_AXIS));

    // a panel for databases import
    JPanel pDatabase = new JPanel();
    // TODO
    pDatabase.setLayout(new BoxLayout(pDatabase, BoxLayout.Y_AXIS));

    // frame main setup
    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(pDefinition);
    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(pDatabase);
    this.getContentPane().add(Box.createVerticalGlue());
    this.getContentPane().add(pButtons);
    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
  }

  public EditPeaRepParamsFrame(File file) {
    this();

    // update the frame fields with the loaded file
    XMLParser xmlParser = new XMLParser(file);
    try {
      xmlParser.parseParameters(null);
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    // TODO
  }

  private void saveToXml(File file) {
    // TODO
  }
}
