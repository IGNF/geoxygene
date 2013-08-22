package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.xml.parsers.ParserConfigurationException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.defaultschema.DefaultCreationFactory;
import fr.ign.cogit.cartagen.pearep.PeaRepGeneralisation;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.PostgisDB;

public class GUIMainClass extends JFrame implements ActionListener {

  /****/
  private static final long serialVersionUID = 1L;
  private JRadioButton rdScale, rdParams;
  private File themesFile;

  public GUIMainClass(File themesFile) {
    super("Choisissez l'interface d'édition à ouvrir");
    this.themesFile = themesFile;
    this.setSize(450, 120);
    this.setIconImage(new ImageIcon(GUIMainClass.class.getClassLoader()
        .getResource("resources/images/icons/logo.jpg")).getImage());

    JPanel rdPanel = new JPanel();
    rdScale = new JRadioButton("Fichier ScaleMaster.xml");
    rdParams = new JRadioButton("Fichier PeaRepParameters.xml");
    ButtonGroup bg = new ButtonGroup();
    bg.add(rdScale);
    bg.add(rdParams);
    rdScale.setSelected(true);
    rdPanel.add(rdScale);
    rdPanel.add(rdParams);
    rdPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    rdPanel.setLayout(new BoxLayout(rdPanel, BoxLayout.X_AXIS));
    JButton okButton = new JButton("OK");
    okButton.addActionListener(this);
    okButton.setActionCommand("ok");
    this.add(rdPanel);
    this.add(okButton);
    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    // ******************************************************
    // launch CartAGen as batch application
    // Objects creation factory
    CartagenApplication.getInstance().setCreationFactory(
        new DefaultCreationFactory());
    // Application initialisation
    CartagenApplication.getInstance().initApplication();
    CartAGenDoc doc = CartAGenDoc.getInstance();
    doc.setName("PEA_REP");
    doc.setPostGisDb(PostgisDB.get("PEA_REP", true));
    String jarPath = null;
    try {
      jarPath = new File(PeaRepGeneralisation.class.getProtectionDomain()
          .getCodeSource().getLocation().toURI().getPath().substring(1))
          .getParent();
    } catch (URISyntaxException e1) {
      e1.printStackTrace();
    }

    String path = jarPath + "\\" + "ScaleMasterThemes.xml";
    File themesFile = new File(path);
    GUIMainClass frame = new GUIMainClass(themesFile);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.setVisible(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("ok")) {
      if (rdScale.isSelected()) {
        EditScaleMasterFrame frame = null;
        try {
          frame = new EditScaleMasterFrame(themesFile, true);
        } catch (OWLOntologyCreationException e1) {
          e1.printStackTrace();
        } catch (ParserConfigurationException e1) {
          e1.printStackTrace();
        } catch (SAXException e1) {
          e1.printStackTrace();
        } catch (IOException e1) {
          e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
          e1.printStackTrace();
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
      } else if (rdParams.isSelected()) {
        EditPeaRepParamsFrame frame = new EditPeaRepParamsFrame(true);
        // THALES - FC : On positionne la sortie sur la fermeture de la frame
        // ici
        // sinon les frames ouvertes par d'autres frames font que l'application
        // entière se ferme.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
      }
      this.setVisible(false);
    }
  }

}
