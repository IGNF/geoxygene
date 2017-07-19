/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.dataset;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.plugin.cartagen.util.renderer.LoadingListCellRenderer;

/**
 * Shapefile data loader
 * 
 * @author MVieira Internship July-August 2010
 * 
 */

public class LoadingFrame extends JFrame implements ActionListener {
  private static final long serialVersionUID = -6992190369890036500L;
  private static Logger logger = Logger.getLogger(LoadingFrame.class.getName());

  // Utils
  public static String cheminAbsolu;
  private ReportingListTransferHandler arrayListHandler = new ReportingListTransferHandler();
  private DefaultListModel[] modele = new DefaultListModel[20];
  private JList[] list = new JList[20];
  private String dataSet = null;
  private Vector<?> filesList;
  private JList selectionList;

  // Components
  private JPanel majorPanel;
  private JPanel leftListPanel;
  private JPanel rightListPanel;
  private JPanel commandPanel;
  private JPanel commandSubPanel;
  private JScrollPane jsp;
  private JTabbedPane jtp;
  private JButton validate;

  /**
   * Construction of the frame
   */

  public LoadingFrame() {
    if (this.chooseFile()) {
      return;
    }
    this.setTitle("Traduction of the needed layers");
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.initFrame();
    this.setLocation(200, 300);
    this.setSize(840, 500);
    this.setResizable(false);
    this.pack();
  }

  public LoadingFrame(File file) {

    this.dataSet = file.getAbsolutePath();
    this.filesList = LoaderUtil.listerRepertoire(file);
    // Already traducted dataset
    if (this.dataSet.contains("loaded_data")) {
      LoadingFrame.logger.info("The dataset exists and will be launched");
      EnrichFrame.getInstance().setVisible(true);
      return;
    }
    this.setTitle("Traduction of the needed layers");
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.initFrame();
    this.setLocation(200, 100);
    this.setSize(840, 500);
    this.setResizable(false);
    this.pack();
  }

  /**
   * Initialisation of the frame
   */

  public void initFrame() {

    // LEFT LIST PANEL

    this.leftListPanel = new JPanel(new GridLayout(1, 1));
    this.leftListPanel
        .setBorder(BorderFactory.createTitledBorder("Original layers"));
    this.leftListPanel.setMinimumSize(new Dimension(200, 450));
    this.leftListPanel.setMaximumSize(new Dimension(200, 450));

    // model creation
    DefaultListModel modeleTest = new DefaultListModel();
    for (int i = 0; i < this.filesList.size(); i++) {
      modeleTest.addElement(this.filesList.elementAt(i));
    }

    // list creation
    this.selectionList = new JList(modeleTest);
    this.selectionList.setCellRenderer(new LoadingListCellRenderer());
    this.selectionList.setName("listeDeSelection");
    this.selectionList
        .setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    this.selectionList.setTransferHandler(this.arrayListHandler);
    this.selectionList.setDragEnabled(true);
    this.selectionList.setFixedCellWidth(190);
    this.jsp = new JScrollPane(this.selectionList);

    this.leftListPanel.add(this.jsp);

    // RIGHT LIST PANEL

    this.rightListPanel = new JPanel();
    this.rightListPanel
        .setBorder(BorderFactory.createTitledBorder("Corresponding layers"));
    this.rightListPanel.setMinimumSize(new Dimension(500, 450));
    this.rightListPanel.setMaximumSize(new Dimension(500, 450));

    int taille = LoaderUtil.type.length;
    for (int i = 0; i < taille; i++) {
      this.modele[i] = new DefaultListModel();
      this.list[i] = new JList(this.modele[i]);
      this.list[i].setName("liste" + i);
      this.list[i]
          .setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      this.list[i].setTransferHandler(this.arrayListHandler);
      this.list[i].setDragEnabled(true);
    }

    this.jtp = new JTabbedPane();
    this.jtp.setTabPlacement(SwingConstants.LEFT);
    taille = LoaderUtil.type.length;
    for (int i = 0; i < taille; i++) {
      this.jtp.addTab(LoaderUtil.layerTypes().get(LoaderUtil.type[i]),
          this.list[i]);
      this.jtp.setMnemonicAt(i, i);
    }
    this.jtp.setPreferredSize(new Dimension(510, 20 * taille));

    this.rightListPanel.add(this.jtp);
    this.rightListPanel.add(
        new JLabel("<html><h2><i color='#084B8A'>Dataset being processed : "
            + new File(this.dataSet).getName() + "</i></h2>"
            + "<p><i color='#009900'>Green : automated matching ensured</i><br/>"
            + "<i color='#FF0000'>Red : manual matching needed by Drag & Drop</i></p></html>"));

    // COMMAND PANEL

    this.commandPanel = new JPanel();
    this.commandPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
    this.commandSubPanel = new JPanel(new GridLayout(2, 1));
    this.commandPanel
        .setLayout(new BoxLayout(this.commandPanel, BoxLayout.Y_AXIS));
    this.commandSubPanel.setMaximumSize(new Dimension(140, 50));
    this.commandSubPanel.add(this.validate = new JButton("Traduction"));
    this.commandPanel.add(this.commandSubPanel);
    this.validate.addActionListener(this);

    // MAJOR PANEL

    this.majorPanel = new JPanel();
    this.majorPanel.setLayout(new BoxLayout(this.majorPanel, BoxLayout.X_AXIS));
    this.majorPanel.add(this.leftListPanel);
    this.majorPanel.add(this.rightListPanel);
    this.majorPanel.add(this.commandPanel);
    this.majorPanel.setPreferredSize(new Dimension(900, 450));
    this.majorPanel.setMaximumSize(new Dimension(900, 450));
    this.add(this.majorPanel);

  }

  /**
   * Validation of the frame
   */

  @Override
  public void actionPerformed(ActionEvent ev) {
    try {

      File source = new File(this.dataSet); // original dataset

      // Modif Cecile: data should be loaded in src/resources loaded data, not
      // in target, because they must be retrievable during a further
      // execution, even after a build (that would have deleted the content of
      // the target directory)
      // Old code:
      // File destination = new File(LoadingFrame.class
      // .getResource("/loaded_data").getPath().replaceAll("%20", " ")
      // + "/" + source.getName()); // future
      // loaded
      // dataset
      // New code:
      String destinationPath = "src/main/resources/loaded_data" + "/"
          + source.getName();
      File destination = new File(destinationPath);
      // End modif Cecile

      boolean ok = LoaderUtil.copyDirectory(source, destination);

      if (ok) {
        // Traductions
        int taille = LoaderUtil.type.length;
        FileOutputStream fos = new FileOutputStream(
            destination + ".properties");
        for (int i = 0; i < taille; i++) {
          LoaderUtil.traductionDe(fos, this.modele[i], destination,
              "\\" + LoaderUtil.type[i]);
        }
        fos.close();
        // Added Cecile: Now we can set the systemPath of the CartAGenDB that
        // is associated to the current dataset that is being loaded
        // CartAGenDoc doc = CartAGenDoc.getInstance();
        // CartAGenDataSet curDS = doc.getCurrentDataset();
        // ShapeFileDB curDB = (ShapeFileDB) curDS.getCartAGenDB();
        // curDB.setSystemPath(destinationPath);
        // End added Cecile
        int rep = JOptionPane.showConfirmDialog(this,
            "Do you want to open this dataset ?");
        if (rep == JOptionPane.OK_OPTION) {
          // Modif Cecile: LoadingFrame.cheminAbsolu and
          // CartagenApplication.getInstance().cheminDonnees should not be
          // modified here
          // Old code:
          // LoadingFrame.cheminAbsolu = destination.toString();
          // CartagenApplication.getInstance().setCheminDonnees(
          // LoadingFrame.cheminAbsolu);
          // End modif Cecile
          EnrichFrame.getInstance().setVisible(true);
        }
      } else {
        JOptionPane.showMessageDialog(null, "Traduction cancelled...");
      }
      this.setVisible(false);

    } catch (IOException e) {
      System.out.println(e);
      JOptionPane.showMessageDialog(null, "Error in the file treatment");
    }

  }

  /**
   * File chooser before loading
   * @return true if the user wants to launch a chosen file that already exists
   *         false otherwise, meaning that traduction will be needed
   */

  private boolean chooseFile() {

    // File chooser
    JFileChooser choix = new JFileChooser("loaded_data");
    choix.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    choix.setApproveButtonText("Load this dataset");
    int retour = choix.showDialog(null, "Select a dataset");
    if (retour == JFileChooser.APPROVE_OPTION) {
      this.dataSet = choix.getSelectedFile().getAbsolutePath();
      LoadingFrame.cheminAbsolu = this.dataSet;

    } else {
      this.dataSet = null;
    }

    // Already traducted dataset
    if (this.dataSet.contains("CartAGen")
        && this.dataSet.contains("loaded_data")) {
      LoadingFrame.logger.info("The dataset exists and will be launched");
      EnrichFrame.getInstance().setVisible(true);
      return true;
    }

    // Test of existence
    if (this.test(this.dataSet)) {
      LoadingFrame.logger.info("The dataset exists and will be launched");
      EnrichFrame.getInstance().setVisible(true);
      return true;
    }
    if (this.dataSet != null) {
      // Creation of the dataset list
      this.filesList = LoaderUtil.listerRepertoire(new File(this.dataSet));
    }
    // Activation of the traduction panel
    this.setVisible(true);
    return false;

  }

  /**
   * Test of existence of a dataset, and if it fits the user
   * 
   * @param dataSetTest
   * @return true if the traducted file already exists false if there is no
   *         traduction or if the user don't want to use it
   */

  private boolean test(String dataSetTest) {
    LoadingFrame.logger.info("test of the existence of the file");
    // Modif Cecile
    // LoadingFrame.cheminAbsolu should not be modified here + the path search
    // for is corrupted
    // Old code:
    // Traducted file path
    // LoadingFrame.cheminAbsolu = "loaded_data//"
    // + new File(dataSetTest).getName();

    // Tests is a traducted file already exists
    // if (new File(LoadingFrame.cheminAbsolu).exists()) {
    // int rep = JOptionPane
    // .showConfirmDialog(
    // null,
    // "This dataset has already been loaded before !!\n Do you want to erase
    // the old version ?",
    // "CAUTION !", JOptionPane.YES_NO_OPTION);
    // if (rep == JOptionPane.OK_OPTION) {
    // return false; // Re-traduction of the dataset
    // }
    // New code:
    String datasetDirSimpleName = new File(dataSetTest).getName();
    String pathToTest = "src/main/resources/loaded_data" + File.separatorChar
        + datasetDirSimpleName;
    if (new File(pathToTest).exists()) {
      int rep = JOptionPane.showConfirmDialog(null,
          "This dataset has already been loaded before !!\n Do you want to erase the old version ?",
          "CAUTION !", JOptionPane.YES_NO_OPTION);
      if (rep == JOptionPane.OK_OPTION) {
        return false; // Re-traduction of the dataset
      }
      // End modif Cecile
      return true; // Ok for the old traduction
    }
    return false; // First traduction of the dataset
  }

  public boolean test() {
    return this.test(this.dataSet);
  }

}
