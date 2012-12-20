/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.dataloading;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.ign.cogit.cartagen.software.CartagenApplication;

/**
 * Classe permettant la ré-exportation des jeux de données au schéma originel
 * 
 * @author MVieira
 * 
 */

public class RexportFrame extends JFrame implements ListSelectionListener,
    ActionListener {
  private static final long serialVersionUID = -6992190369890036500L;

  // Components
  private Vector<?> filesList;
  private JPanel panel;
  private JPanel leftListPanel;
  private JList selectionList;
  private JPanel commandPanel;
  private JPanel subCommandPanel;
  private JButton validate;
  private JButton quit;

  /**
   * Construction of the frame
   */

  public RexportFrame() {
    this.setTitle("Select a dataset to export");
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.initFrame();
    this.setLocation(400, 300);
    this.setSize(310, 300);
    this.setResizable(false);
    this.setVisible(true);
  }

  /**
   * Initialisation of the frame
   */

  public void initFrame() {

    this.panel = new JPanel();
    this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.X_AXIS));

    // LEFT LIST PANEL

    this.leftListPanel = new JPanel(new GridLayout(1, 1));
    this.leftListPanel.setBorder(BorderFactory
        .createTitledBorder("Liste des jeux de données traduits"));

    this.filesList = LoaderUtil.listerRepertoire(new File("loaded_data"));
    // model creation
    DefaultListModel modele = new DefaultListModel();
    for (int i = 0; i < this.filesList.size(); i++) {
      modele.addElement(this.filesList.elementAt(i));
    }
    // list creation
    this.selectionList = new JList(modele);
    this.selectionList.addListSelectionListener(this);
    this.selectionList.setName("selectionList");
    this.selectionList
        .setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    this.selectionList.setFixedCellWidth(190);

    this.leftListPanel.add(new JScrollPane(this.selectionList));
    this.panel.add(this.leftListPanel);

    // COMMAND PANEL

    this.commandPanel = new JPanel();
    this.commandPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
    this.subCommandPanel = new JPanel(new GridLayout(2, 1));
    this.commandPanel.setLayout(new BoxLayout(this.commandPanel,
        BoxLayout.Y_AXIS));
    this.subCommandPanel.setMinimumSize(new Dimension(80, 50));
    this.subCommandPanel.setMaximumSize(new Dimension(80, 50));
    this.subCommandPanel.add(this.validate = new JButton("Validate"))
        .setEnabled(false);
    this.subCommandPanel.add(this.quit = new JButton("Quit"));
    this.commandPanel.add(this.subCommandPanel);
    this.validate.addActionListener(this);
    this.quit.addActionListener(this);
    this.panel.add(this.commandPanel);

    this.panel.setPreferredSize(new Dimension(700, 400));
    this.panel.setMaximumSize(new Dimension(700, 400));
    this.add(this.panel);

  }

  /**
   * Validation of the frame
   */

  @Override
  public void actionPerformed(ActionEvent ev) {
    Object src = ev.getSource();
    if (src == this.validate) {
      try {

        System.out.println(this.selectionList.getSelectedValue().toString());

        File source = new File("donnees_TDT//"
            + this.selectionList.getSelectedValue().toString() + "_TDT");
        File destination = new File(this.ChoixDossier(source));
        System.out.println(destination);
        // creation du nouveau repertoire
        // boolean ok=UtilStage.copyDirectory(source, destination);
        // Traduction des fichiers par modif du libele
        if (LoaderUtil.copyDirectory(source, destination)) {
          /**
           * reconstitution du jeu traduit
           * 
           */

          LoaderUtil.reconstructionDe(destination, this.selectionList
              .getSelectedValue().toString());
          CartagenApplication.getInstance().resetFrameExport();
        }
      } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error in the file treatment");
      }
    } else if (src == this.quit) {
      CartagenApplication.getInstance().resetFrameExport();
      this.setVisible(false);
    }
  }

  /**
   * File chooser
   * @param source
   * @return
   */

  public String ChoixDossier(File source) {
    JFileChooser choix = new JFileChooser();
    choix.setSelectedFile(source);
    choix.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int retour = choix.showSaveDialog(new JFrame());
    if (retour == JFileChooser.APPROVE_OPTION) {
      return choix.getSelectedFile().getAbsolutePath();
    }
    return "The file is not chosen";

  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting() == false) {
      if (!this.selectionList.isSelectionEmpty()) {
        this.validate.setEnabled(true);
      } else {
        this.validate.setEnabled(false);
      }
    }
  }

}
