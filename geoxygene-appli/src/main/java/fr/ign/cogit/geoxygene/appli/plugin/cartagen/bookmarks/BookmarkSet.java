/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.bookmarks;

import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.XMLFileFilter;
import fr.ign.cogit.cartagen.util.XMLUtil;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;

/**
 * @author GTouya
 * 
 *         A bookmark set is an organised set of bookmarks that represent stored
 *         localisations of the visualisation panel of CartAGen application.
 */
public class BookmarkSet {

  private static Logger logger = Logger.getLogger(BookmarkSet.class.getName());
  private HashSet<Bookmark> setBookmark;
  private CartAGenDB currentDataset;
  private ProjectFrame currentView;
  private File storingFile;

  /**
   * 
   */
  public BookmarkSet(CartAGenDB dataset, ProjectFrame view) {
    super();
    this.currentDataset = dataset;
    this.currentView = view;
    this.setBookmark = new HashSet<Bookmark>();
  }

  /**
   * Zoom to the current display window to the extents of a given bookmark.
   * @param book
   * @throws NoninvertibleTransformException
   */
  public void zoomToBookmark(Bookmark book)
      throws NoninvertibleTransformException {
    // check that the bookmark is related to the current Dataset
    if (!book.getDatasetName().equals(this.currentDataset.getName())) {
      BookmarkSet.logger
          .warn("Bookmark Error : the bookmark is not related to the current dataset !");
      return;
    }

    // zoom to the bookmark
    this.currentView.getLayerViewPanel().getViewport().zoom(book.getExtent());
    this.currentView.repaint();
  }

  /**
   * Filter the bookmarks stored in the set according to a given dataset.
   * @param dataset
   * @return
   */
  public HashSet<Bookmark> filterBookmarks(String dataset) {
    HashSet<Bookmark> filteredSet = new HashSet<Bookmark>();
    for (Bookmark book : this.setBookmark) {
      if (book.getDatasetName().equals(dataset)) {
        filteredSet.add(book);
      }
    }// while boucle sur setBookmark

    return filteredSet;
  }// filterBookmarks

  /**
   * Create a new bookmark in this for the current dataset and view.
   * 
   * @return Bookmark : the Bookmark object created.
   * 
   */
  public Bookmark buildNewBookmark() {
    // get the window coordinates
    IEnvelope geom = this.currentView.getLayerViewPanel().getViewport()
        .getEnvelopeInModelCoordinates();

    // ask for the name of the Bookmark
    String bookName = JOptionPane.showInputDialog(null,
        "Name of the created bookmark");

    return new Bookmark(this.currentDataset.getName(), geom, bookName);
  }

  /**
   * Load into this the bookmarks stored in the input XML file.
   * 
   * @param fic the XML file that contains the bookmarks.
   * 
   * @return void
   * 
   */
  public void loadXmlBookmarks(File file) throws ParserConfigurationException,
      SAXException, IOException {
    // on commence par ouvrir le doucment XML pour le parser
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    Document doc;
    doc = db.parse(file);
    doc.getDocumentElement().normalize();

    // get the root element
    Element bookSetElem = (Element) doc.getElementsByTagName("bookmark-set")
        .item(0);
    // loop on the different datasets
    for (int i = 0; i < bookSetElem.getElementsByTagName("cartagen-dataset")
        .getLength(); i++) {
      Element versionElem = (Element) bookSetElem.getElementsByTagName(
          "cartagen-dataset").item(i);
      // get the dataset name
      Element nomVersionElem = (Element) versionElem.getElementsByTagName(
          "name").item(0);
      String nomVersion = nomVersionElem.getChildNodes().item(0).getNodeValue();
      // now loop on the bookmark in this dataset
      for (int j = 0; j < versionElem.getElementsByTagName("bookmark")
          .getLength(); j++) {
        Element bookElem = (Element) versionElem.getElementsByTagName(
            "bookmark").item(j);
        // get its name
        Element nomBookElem = (Element) bookElem.getElementsByTagName("name")
            .item(0);
        String nomBook = nomBookElem.getChildNodes().item(0).getNodeValue();
        // get the "extent"
        Element extentElem = (Element) bookElem.getElementsByTagName("extent")
            .item(0);
        // get "x-min"
        Element xminElem = (Element) extentElem.getElementsByTagName("x-min")
            .item(0);
        String xminS = xminElem.getChildNodes().item(0).getNodeValue();
        Double xmin = new Double(xminS);
        // get "x-max"
        Element xmaxElem = (Element) extentElem.getElementsByTagName("x-max")
            .item(0);
        String xmaxS = xmaxElem.getChildNodes().item(0).getNodeValue();
        Double xmax = new Double(xmaxS);
        // get "y-min"
        Element yminElem = (Element) extentElem.getElementsByTagName("y-min")
            .item(0);
        String yminS = yminElem.getChildNodes().item(0).getNodeValue();
        Double ymin = new Double(yminS);
        // get "y-max"
        Element ymaxElem = (Element) extentElem.getElementsByTagName("y-max")
            .item(0);
        String ymaxS = ymaxElem.getChildNodes().item(0).getNodeValue();
        Double ymax = new Double(ymaxS);

        // create the Bookmark from XML data
        Bookmark nouveau = new Bookmark(nomVersion, new GM_Envelope(
            xmin.doubleValue(), xmax.doubleValue(), ymin.doubleValue(),
            ymax.doubleValue()), nomBook);
        this.setBookmark.add(nouveau);
      }// for j
    }// for i
  }

  /**
   * Load the bookmarks stored in the XML file chosen by the user with a
   * JFileChooser.
   * 
   * @return void
   * 
   */
  public void loadXmlBookmarks() throws ParserConfigurationException,
      SAXException, IOException {
    // initialisation
    this.setBookmark = new HashSet<Bookmark>();

    // on choisit le fichier XML � importer
    JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory(new File("goth_dataroot"));
    fc.setDialogTitle("Ouvrir un fichier XML de mapspecs pour les Moindres carr�s");
    fc.setFileFilter(new XMLFileFilter());
    int returnVal = fc.showOpenDialog(this.currentView.getMainFrame().getGui());
    if (returnVal != JFileChooser.APPROVE_OPTION) {
      return;
    }
    // initialisation apr�s le choix du fichier
    this.storingFile = fc.getSelectedFile();

    this.loadXmlBookmarks(this.storingFile);
  }

  /**
   * Save the bookmarks in XML. The name and the path of the file are chosen by
   * the user in a JFileChooser if the storingFile field is null.
   * 
   * @return void
   * @throws TransformerException
   * 
   */
  public void saveToXml() throws IOException, TransformerException {

    // first check if storingFile is null
    if (this.storingFile == null) {
      JFileChooser fc = new JFileChooser();
      int returnVal = fc.showSaveDialog(this.currentView.getMainFrame()
          .getGui());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      this.storingFile = fc.getSelectedFile();
    }// if(fic==null)

    Node n = null;
    // ********************************************
    // CREATION OF THE XML DOCUMENT
    // Document (Xerces implementation only).
    Document xmldoc = new DocumentImpl();
    // Root element.
    Element root = xmldoc.createElement("bookmark-set");
    // on extrait le set des versions
    Set<String> datasets = this.extractAllDatasets();
    // loop on the datasets
    for (String datasetName : datasets) {
      // create an element with this name
      Element datasetElem = xmldoc.createElement("cartagen-dataset");
      root.appendChild(datasetElem);
      // create the name element and its value
      Element dataNameElem = xmldoc.createElement("name");
      n = xmldoc.createTextNode(datasetName);
      dataNameElem.appendChild(n);
      datasetElem.appendChild(dataNameElem);

      // get all the bookmarks of this dataset
      HashSet<Bookmark> bookmarks = this.filterBookmarks(datasetName);
      // loop on the bookmarks
      for (Bookmark book : bookmarks) {
        // create the bookmark element
        Element bookElem = xmldoc.createElement("bookmark");
        // create the name element and its value
        Element nomBookElem = xmldoc.createElement("name");
        n = xmldoc.createTextNode(book.getName());
        nomBookElem.appendChild(n);
        bookElem.appendChild(nomBookElem);
        // create the extent element
        Element extentElem = xmldoc.createElement("extent");
        // add the coordinates elements and the values
        Element xminElem = xmldoc.createElement("x-min");
        n = xmldoc.createTextNode(String.valueOf(book.getExtent()
            .getLowerCorner().getX()));
        xminElem.appendChild(n);
        extentElem.appendChild(xminElem);
        Element yminElem = xmldoc.createElement("y-min");
        n = xmldoc.createTextNode(String.valueOf(book.getExtent()
            .getLowerCorner().getY()));
        yminElem.appendChild(n);
        extentElem.appendChild(yminElem);
        Element xmaxElem = xmldoc.createElement("x-max");
        n = xmldoc.createTextNode(String.valueOf(book.getExtent()
            .getUpperCorner().getX()));
        xmaxElem.appendChild(n);
        extentElem.appendChild(xmaxElem);
        Element ymaxElem = xmldoc.createElement("y-max");
        n = xmldoc.createTextNode(String.valueOf(book.getExtent()
            .getUpperCorner().getY()));
        ymaxElem.appendChild(n);
        extentElem.appendChild(ymaxElem);
        bookElem.appendChild(extentElem);
        datasetElem.appendChild(bookElem);
      }
    }

    xmldoc.appendChild(root);
    XMLUtil.writeDocumentToXml(xmldoc, this.storingFile);
  }

  /**
   * Add a bookmark in an existing XML file.
   * 
   * @param file : the XML file to modify.
   * @param book : the bookmark object to add in the XML file.
   * 
   * @return void
   * @throws TransformerException
   * 
   */
  public void addBookmarkToXml(File file, Bookmark book)
      throws ParserConfigurationException, SAXException, IOException,
      TransformerException {
    // check if the file exists
    if (!file.exists()) {
      Document xmldoc = new DocumentImpl();
      Element root = xmldoc.createElement("bookmark-set");
      // it's a new file, everything has to written
      // first the dataset name
      Element versionElem = xmldoc.createElement("cartagen-dataset");
      // create the name element and its value
      Element nomVersionElem = xmldoc.createElement("name");
      Node n = xmldoc.createTextNode(book.getDatasetName());
      nomVersionElem.appendChild(n);
      versionElem.appendChild(nomVersionElem);
      // create a bookmark element
      Element bookElem = xmldoc.createElement("bookmark");
      // create the name element and its value
      Element nomBookElem = xmldoc.createElement("name");
      n = xmldoc.createTextNode(book.getName());
      nomBookElem.appendChild(n);
      bookElem.appendChild(nomBookElem);
      // create the extent element
      Element extentElem = xmldoc.createElement("extent");
      // add the coordinates elements and the values
      Element xminElem = xmldoc.createElement("x-min");
      n = xmldoc.createTextNode(String.valueOf(book.xLo()));
      xminElem.appendChild(n);
      extentElem.appendChild(xminElem);
      Element yminElem = xmldoc.createElement("y-min");
      n = xmldoc.createTextNode(String.valueOf(book.yLo()));
      yminElem.appendChild(n);
      extentElem.appendChild(yminElem);
      Element xmaxElem = xmldoc.createElement("x-max");
      n = xmldoc.createTextNode(String.valueOf(book.xHi()));
      xmaxElem.appendChild(n);
      extentElem.appendChild(xmaxElem);
      Element ymaxElem = xmldoc.createElement("y-max");
      n = xmldoc.createTextNode(String.valueOf(book.yHi()));
      ymaxElem.appendChild(n);
      extentElem.appendChild(ymaxElem);
      bookElem.appendChild(extentElem);
      versionElem.appendChild(bookElem);
      root.appendChild(versionElem);

      // �criture dans le fichier
      xmldoc.appendChild(root);
      XMLUtil.writeDocumentToXml(xmldoc, file);

      return;
    }

    // first open the XML document in order to parse it
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    Document doc;
    doc = db.parse(file);
    doc.getDocumentElement().normalize();

    Element root = (Element) doc.getElementsByTagName("bookmark-set").item(0);

    // look for a cartagen-dataset element with the same name as book's one
    Element bonneVersion = null;
    for (int i = 0; i < doc.getElementsByTagName("cartagen-dataset")
        .getLength(); i++) {
      Element verElem = (Element) doc.getElementsByTagName("cartagen-dataset")
          .item(i);
      Element nomElem = (Element) verElem.getElementsByTagName("name").item(0);
      String nomVersion = nomElem.getChildNodes().item(0).getNodeValue();
      if (nomVersion.equals(book.getDatasetName())) {
        bonneVersion = verElem;
        break;
      }
    }

    // ***************************************
    // case where the dataset already exists in the file
    if (bonneVersion != null) {
      // create the bookmark element
      Element bookElem = doc.createElement("bookmark");
      // create the name element and its value
      Element nomBookElem = doc.createElement("name");
      Node n = doc.createTextNode(book.getName());
      nomBookElem.appendChild(n);
      bookElem.appendChild(nomBookElem);
      // create the extent element
      Element extentElem = doc.createElement("extent");
      // add the coordinates elements and the values
      Element xminElem = doc.createElement("x-min");
      n = doc.createTextNode(String.valueOf(book.xLo()));
      xminElem.appendChild(n);
      extentElem.appendChild(xminElem);
      Element yminElem = doc.createElement("y-min");
      n = doc.createTextNode(String.valueOf(book.yLo()));
      yminElem.appendChild(n);
      extentElem.appendChild(yminElem);
      Element xmaxElem = doc.createElement("x-max");
      n = doc.createTextNode(String.valueOf(book.xHi()));
      xmaxElem.appendChild(n);
      extentElem.appendChild(xmaxElem);
      Element ymaxElem = doc.createElement("y-max");
      n = doc.createTextNode(String.valueOf(book.yHi()));
      ymaxElem.appendChild(n);
      extentElem.appendChild(ymaxElem);
      bookElem.appendChild(extentElem);
      bonneVersion.appendChild(bookElem);
      // ***************************************
      // in this case, a new dataset has to be added in the file
    } else {
      // first create the new dataset element
      Element versionElem = doc.createElement("cartagen-dataset");
      // create the name element and its value
      Element nomVersionElem = doc.createElement("name");
      Node n = doc.createTextNode(book.getDatasetName());
      nomVersionElem.appendChild(n);
      versionElem.appendChild(nomVersionElem);
      // create the bookmark element
      Element bookElem = doc.createElement("bookmark");
      // create the name element and its value
      Element nomBookElem = doc.createElement("name");
      n = doc.createTextNode(book.getName());
      nomBookElem.appendChild(n);
      bookElem.appendChild(nomBookElem);
      // create the extent element
      Element extentElem = doc.createElement("extent");
      // add the coordinates elements and the values
      Element xminElem = doc.createElement("x-min");
      n = doc.createTextNode(String.valueOf(book.xLo()));
      xminElem.appendChild(n);
      extentElem.appendChild(xminElem);
      Element yminElem = doc.createElement("y-min");
      n = doc.createTextNode(String.valueOf(book.yLo()));
      yminElem.appendChild(n);
      extentElem.appendChild(yminElem);
      Element xmaxElem = doc.createElement("x-max");
      n = doc.createTextNode(String.valueOf(book.xHi()));
      xmaxElem.appendChild(n);
      extentElem.appendChild(xmaxElem);
      Element ymaxElem = doc.createElement("y-max");
      n = doc.createTextNode(String.valueOf(book.yHi()));
      ymaxElem.appendChild(n);
      extentElem.appendChild(ymaxElem);
      bookElem.appendChild(extentElem);
      versionElem.appendChild(bookElem);
      root.appendChild(versionElem);
    }

    // save the file
    XMLUtil.writeDocumentToXml(doc, file);

  }

  /**
   * Just add a bookmark to this.
   * 
   * @param book the Bookmark object to add.
   * 
   * @return void
   * 
   */
  public void addBookmark(Bookmark book) {
    this.setBookmark.add(book);
    return;
  }

  /**
   * Extracts a set of dataset names for which bookmarks are stored.
   * 
   * @return Set définis dans this.
   * 
   */
  private Set<String> extractAllDatasets() {
    HashSet<String> datasets = new HashSet<String>();
    for (Bookmark book : this.setBookmark) {
      if (!datasets.contains(book.getDatasetName())) {
        datasets.add(book.getDatasetName());
      }
    }

    return datasets;
  }

  public CartAGenDB getCurrentDataset() {
    return this.currentDataset;
  }

  public void setCurrentDataset(CartAGenDB currentDataset) {
    this.currentDataset = currentDataset;
  }

  public ProjectFrame getCurrentView() {
    return this.currentView;
  }

  public void setCurrentView(ProjectFrame currentView) {
    this.currentView = currentView;
  }

}
