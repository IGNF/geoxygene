/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.util.LastSessionParameters;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;

public class LoadSelectionFrame extends JFrame implements ActionListener {

  /****/
  private static final long serialVersionUID = 1L;
  private GeOxygeneApplication appli;

  private ArrayList<ObjectSelection> sels = new ArrayList<ObjectSelection>();
  private JList liste;
  private File file;

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("Load")) {
      ObjectSelection sel = (ObjectSelection) liste.getSelectedValue();
      sel.addToSelection();
      LastSessionParameters params = LastSessionParameters.getInstance();
      Map<String, String> attributes = new HashMap<String, String>();
      attributes.put("selectionName", sel.getName());
      try {
        params
            .setParameter("last object selection", file.getPath(), attributes);
      } catch (TransformerException | IOException e1) {
        e1.printStackTrace();
      }

    } else if (e.getActionCommand().equals("Close")) {
      this.setVisible(false);
    }
  }

  public LoadSelectionFrame(GeOxygeneApplication appli, File file)
      throws ParserConfigurationException, SAXException, IOException {
    super("Load an object selection");
    this.setSize(400, 400);
    this.appli = appli;
    this.file = file;

    // ***********************************
    // PANNEAU CONTENANT LES BOUTONS CHARGER ET FERMER
    // ***********************************
    JPanel pBoutons = new JPanel();
    JButton btnFermer = new JButton("Close");
    btnFermer.addActionListener(this);
    btnFermer.setActionCommand("Close");
    btnFermer.setPreferredSize(new Dimension(100, 50));
    JButton btnCharger = new JButton("Load");
    btnCharger.addActionListener(this);
    btnCharger.setActionCommand("Load");
    btnCharger.setPreferredSize(new Dimension(100, 50));
    pBoutons.add(btnCharger);
    pBoutons.add(btnFermer);
    pBoutons.setLayout(new BoxLayout(pBoutons, BoxLayout.X_AXIS));

    // *********************************
    // PANNEAU CONTENANT LA LISTE DES SELECTIONS
    // *********************************
    JPanel panelListe = new JPanel();
    DefaultListModel dlm = new DefaultListModel();
    // on charge les sélections depuis le fichier XML
    chargerSelections(file, appli, sels);
    for (ObjectSelection sel : sels)
      dlm.addElement(sel);
    liste = new JList(dlm);
    liste.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    liste.setPreferredSize(new Dimension(70, 200));
    panelListe.add(new JScrollPane(liste));
    panelListe.setLayout(new BoxLayout(panelListe, BoxLayout.X_AXIS));

    // *********************************
    // LA FRAME
    // *********************************
    this.getContentPane().add(panelListe);
    this.getContentPane().add(pBoutons);
    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
  }

  public static void chargerSelections(File fic, GeOxygeneApplication appli,
      List<ObjectSelection> sels) throws ParserConfigurationException,
      SAXException, IOException {

    // on commence par ouvrir le doucment XML pour le parser
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    Document doc;
    doc = db.parse(fic);
    doc.getDocumentElement().normalize();

    // on récupère la racine du fichier
    Element root = (Element) doc.getElementsByTagName("object-selection").item(
        0);
    for (int i = 0; i < root.getElementsByTagName("selection").getLength(); i++) {
      Element elem = (Element) root.getElementsByTagName("selection").item(i);
      sels.add(new ObjectSelection(appli, elem));
    }
  }
}
