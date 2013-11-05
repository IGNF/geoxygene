package fr.ign.cogit.osm.importexport;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.GeometryType;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.util.CRSConversion;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.osm.importexport.OSMRelation.RoleMembre;
import fr.ign.cogit.osm.importexport.OSMRelation.TypeRelation;
import fr.ign.cogit.osm.schema.OSMSchemaFactory;
import fr.ign.cogit.osm.schema.OsmCaptureTool;
import fr.ign.cogit.osm.schema.OsmGeneObj;
import fr.ign.cogit.osm.schema.OsmMapping;
import fr.ign.cogit.osm.schema.OsmMapping.OsmMatching;
import fr.ign.cogit.osm.schema.OsmSource;
import fr.ign.cogit.osm.schema.landuse.OsmLandUseTypology;
import fr.ign.cogit.osm.schema.landuse.OsmSimpleLandUseArea;

public class OSMLoader extends SwingWorker<Void, Void> {

  private Logger logger = Logger.getLogger(OSMLoader.class.getName());

  // the global tags of OSM files
  public static final String TAG_BOUNDS = "bounds";
  public static final String TAG_MIN_LAT = "minlat";
  public static final String TAG_MAX_LAT = "maxlat";
  public static final String TAG_MIN_LON = "minlon";
  public static final String TAG_MAX_LON = "maxlon";

  public enum OsmLoadingTask {
    POINTS, LINES, RELATIONS, OBJECTS;

    public String getLabel() {
      if (this.equals(POINTS))
        return "OSM Points";
      else if (this.equals(LINES))
        return "OSM Lines";
      else if (this.equals(RELATIONS))
        return "OSM Relations";
      else if (this.equals(OBJECTS))
        return "Objects";
      return "";
    }
  }

  private Set<OSMResource> nodes, ways, relations;
  private File fic;
  private OsmDataset dataset;
  private OsmMapping mapping;
  private JDialog dialog;
  private OsmLoadingTask currentTask = OsmLoadingTask.POINTS;
  private Runnable fillLayersTask;
  double xMin, yMin, xMax, yMax;
  double xCentr, yCentr;
  double surf;
  File file;
  int nbNoeuds, nbWays, nbRels, nbResources;

  public OSMLoader(File fic, OsmDataset dataset, Runnable fillLayersTask) {
    this.fic = fic;
    this.dataset = dataset;
    this.fillLayersTask = fillLayersTask;
  }

  public void importOsmData() throws SAXException, IOException,
      ParserConfigurationException, IllegalArgumentException,
      SecurityException, IllegalAccessException, NoSuchFieldException {
    this.nodes = new HashSet<OSMResource>();
    this.ways = new HashSet<OSMResource>();
    this.relations = new HashSet<OSMResource>();
    this.mapping = new OsmMapping();
    this.loadOsmFile(fic);
    if (this.logger.isLoggable(Level.FINE)) {
      this.logger.fine(this.nbResources + "RDF resources loaded");
      this.logger.fine(this.nbNoeuds + "nodes");
      this.logger.fine(this.nbWays + "ways");
      this.logger.fine(this.nbRels + "relations");
    }
    this.convertResourcesToGeneObjs();
  }

  /**
   * Charge les données contenues dans un fichier XML .osm dans la mémoire:
   * remplit la zone couverte et le set des objets OSM
   * 
   * @param fic
   * @throws SAXException
   * @throws IOException
   * @throws ParserConfigurationException
   */
  private void loadOsmFile(File fic) throws SAXException, IOException,
      ParserConfigurationException {
    this.file = fic;
    // on commence par ouvrir le doucment XML pour le parser
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db;
    db = dbf.newDocumentBuilder();
    Document doc;
    doc = db.parse(fic);
    doc.getDocumentElement().normalize();
    Element root = (Element) doc.getElementsByTagName("osm").item(0);
    // on commence par récupérer les limites de la zone
    Element limitElem = (Element) root.getElementsByTagName(
        OSMLoader.TAG_BOUNDS).item(0);
    double minlat = Double.valueOf(limitElem
        .getAttribute(OSMLoader.TAG_MIN_LAT));
    double minlon = Double.valueOf(limitElem
        .getAttribute(OSMLoader.TAG_MIN_LON));
    double maxlat = Double.valueOf(limitElem
        .getAttribute(OSMLoader.TAG_MAX_LAT));
    double maxlon = Double.valueOf(limitElem
        .getAttribute(OSMLoader.TAG_MAX_LON));

    // on convertit ces coordonnées en Lambert 93
    IDirectPosition coinMin = CRSConversion.wgs84ToLambert93(minlat, minlon);
    IDirectPosition coinMax = CRSConversion.wgs84ToLambert93(maxlat, maxlon);
    this.xMin = coinMin.getX();
    this.yMin = coinMin.getY();
    this.xMax = coinMax.getX();
    this.yMax = coinMax.getY();
    this.xCentr = this.xMin + (this.xMax - this.xMin) / 2;
    this.yCentr = this.yMin + (this.yMax - this.yMin) / 2;

    // on calcule la surface en km²
    this.surf = (this.xMax - this.xMin) * (this.yMax - this.yMin) / 1000000.0;

    DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    // on charge les objets ponctuels
    this.nbNoeuds = root.getElementsByTagName(OsmGeneObj.TAG_NODE).getLength();
    for (int i = 0; i < this.nbNoeuds; i++) {
      // Sleep for up to one second.
      try {
        Thread.sleep(2);
      } catch (InterruptedException ignore) {
      }
      Element elem = (Element) root.getElementsByTagName(OsmGeneObj.TAG_NODE)
          .item(i);
      // on récupère les attributs de l'élément
      long id = Long.valueOf(elem.getAttribute(OsmGeneObj.ATTR_ID));
      String versionAttr = elem.getAttribute(OsmGeneObj.ATTR_VERSION);
      int version = 1;
      if (versionAttr != null) {
        version = Integer.valueOf(versionAttr);
      }
      int changeSet = Integer.valueOf(elem.getAttribute(OsmGeneObj.ATTR_SET));
      String contributeur = elem.getAttribute(OsmGeneObj.ATTR_USER);
      int uid = 0;
      if (!contributeur.equals("")) {
        uid = Integer.valueOf(elem.getAttribute(OsmGeneObj.ATTR_UID));
      }
      String timeStamp = elem.getAttribute(OsmGeneObj.ATTR_DATE);
      Date date = null;
      try {
        date = formatDate.parse(timeStamp);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      // on récupère sa géométrie
      double lat = Double.valueOf(elem.getAttribute(OsmGeneObj.ATTR_LAT));
      double lon = Double.valueOf(elem.getAttribute(OsmGeneObj.ATTR_LON));
      OSMNode geom = new OSMNode(lat, lon);
      // on construit le nouvel objet ponctuel
      OSMResource obj = new OSMResource(contributeur, geom, id, changeSet,
          version, uid, date);
      geom.setObjet(obj);
      // on lui assigne ses tags
      this.instancierTagsObjet(obj, elem);
      // on ajoute obj aux objets chargés
      this.nodes.add(obj);
      setProgress(i * 100 / this.nbNoeuds);
      System.out.println("Chargement des points " + i * 100 / this.nbNoeuds
          + " %");
    }
    System.out.println(this.nbNoeuds + " points chargés");

    // Sleep for up to one second.
    try {
      Thread.sleep(2);
    } catch (InterruptedException ignore) {
    }
    this.currentTask = OsmLoadingTask.LINES;
    this.setProgress(0);
    // on charge les objets linéaires
    this.nbWays = root.getElementsByTagName(OsmGeneObj.TAG_WAY).getLength();
    for (int i = 0; i < this.nbWays; i++) {
      Element elem = (Element) root.getElementsByTagName(OsmGeneObj.TAG_WAY)
          .item(i);
      // Sleep for up to one second.
      try {
        Thread.sleep(2);
      } catch (InterruptedException ignore) {
      }
      // on récupère les attributs de l'élément
      int id = Integer.valueOf(elem.getAttribute(OsmGeneObj.ATTR_ID));
      int version = Integer.valueOf(elem.getAttribute(OsmGeneObj.ATTR_VERSION));
      int changeSet = Integer.valueOf(elem.getAttribute(OsmGeneObj.ATTR_SET));
      String contributeur = elem.getAttribute(OsmGeneObj.ATTR_USER);
      int uid = 0;
      if (!contributeur.equals("")) {
        uid = Integer.valueOf(elem.getAttribute(OsmGeneObj.ATTR_UID));
      }
      String timeStamp = elem.getAttribute(OsmGeneObj.ATTR_DATE);
      Date date = null;
      try {
        date = formatDate.parse(timeStamp);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      // on récupère sa géométrie
      ArrayList<Long> vertices = new ArrayList<Long>();
      for (int j = 0; j < elem.getElementsByTagName("nd").getLength(); j++) {
        Element ndElem = (Element) elem.getElementsByTagName("nd").item(j);
        long ref = Long.valueOf(ndElem.getAttribute("ref"));
        vertices.add(ref);
      }
      OSMWay geom = new OSMWay(vertices);
      // on construit le nouvel objet ponctuel
      OSMResource obj = new OSMResource(contributeur, geom, id, changeSet,
          version, uid, date);
      geom.setObjet(obj);
      // on lui assigne ses tags
      this.instancierTagsObjet(obj, elem);
      // on ajoute obj aux objets chargés
      this.ways.add(obj);
      setProgress(i * 100 / this.nbWays);
      System.out.println("Chargement des lignes " + i * 100 / this.nbWays
          + " %");
    }
    System.out.println(this.nbWays + " lignes chargées");

    // Sleep for up to one second.
    try {
      Thread.sleep(2);
    } catch (InterruptedException ignore) {
    }
    this.currentTask = OsmLoadingTask.RELATIONS;
    this.setProgress(0);
    // on charge les relations
    this.nbRels = root.getElementsByTagName(OsmGeneObj.TAG_REL).getLength();
    for (int i = 0; i < this.nbRels; i++) {
      Element elem = (Element) root.getElementsByTagName(OsmGeneObj.TAG_REL)
          .item(i);
      // Sleep for up to one second.
      try {
        Thread.sleep(2);
      } catch (InterruptedException ignore) {
      }
      // on récupère les attributs de l'élément
      int id = Integer.valueOf(elem.getAttribute(OsmGeneObj.ATTR_ID));
      int version = Integer.valueOf(elem.getAttribute(OsmGeneObj.ATTR_VERSION));
      int changeSet = Integer.valueOf(elem.getAttribute(OsmGeneObj.ATTR_SET));
      String contributeur = elem.getAttribute(OsmGeneObj.ATTR_USER);
      int uid = 0;
      if (!contributeur.equals("")) {
        uid = Integer.valueOf(elem.getAttribute(OsmGeneObj.ATTR_UID));
      }
      String timeStamp = elem.getAttribute(OsmGeneObj.ATTR_DATE);
      Date date = null;
      try {
        date = formatDate.parse(timeStamp);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      // on récupère sa primitive
      TypeRelation type = TypeRelation.NON_DEF;
      for (int j = 0; j < elem.getElementsByTagName("tag").getLength(); j++) {
        Element tagElem = (Element) elem.getElementsByTagName("tag").item(j);
        String cle = tagElem.getAttribute("k");
        if (cle.equals("type")) {
          type = TypeRelation.valueOfTexte(tagElem.getAttribute("v"));
        }
      }
      LinkedHashMap<Long, RoleMembre> membres = new LinkedHashMap<Long, RoleMembre>();
      for (int j = 0; j < elem.getElementsByTagName("member").getLength(); j++) {
        Element memElem = (Element) elem.getElementsByTagName("member").item(j);
        long ref = Long.valueOf(memElem.getAttribute("ref"));
        String role = memElem.getAttribute("role");
        membres.put(ref, RoleMembre.valueOfTexte(role));
      }
      OSMRelation geom = new OSMRelation(type, membres);
      // on construit le nouvel objet ponctuel
      OSMResource obj = new OSMResource(contributeur, geom, id, changeSet,
          version, uid, date);
      geom.setObjet(obj);
      // on lui assigne ses tags
      this.instancierTagsObjet(obj, elem);
      // on ajoute obj aux objets chargés
      this.relations.add(obj);
      setProgress(i * 100 / this.nbRels);
      System.out.println("Chargement des relations " + i * 100 / this.nbRels
          + " %");
    }
    System.out.println(this.nbRels + " relations chargées");
    this.nbResources = this.nbNoeuds + this.nbWays + this.nbRels;
  }

  /**
   * For a RDF resource represented by its Java feature and the DOM element of
   * the file, retrieves all additional tags like "highway", "name", etc.
   * 
   * @param obj
   * @param elem
   */
  private void instancierTagsObjet(OSMResource obj, Element elem) {
    for (int j = 0; j < elem.getElementsByTagName("tag").getLength(); j++) {
      Element tagElem = (Element) elem.getElementsByTagName("tag").item(j);
      String cle = tagElem.getAttribute("k");
      // cas du tag outil
      if (cle.equals(OsmGeneObj.TAG_OUTIL)) {
        String txt = tagElem.getAttribute("v");
        OsmCaptureTool outil = OsmCaptureTool.valueOfTexte(txt);
        obj.setCaptureTool(outil);
        continue;
      }
      // cas du tag source
      if (cle.equals(OsmGeneObj.TAG_SOURCE)) {
        String txt = tagElem.getAttribute("v");
        obj.setSource(txt);
        continue;
      }
      // autres tags
      obj.addTag(cle, tagElem.getAttribute("v"));
    }
  }

  /**
   * Create {@link OsmGeneObj} features from the RDF resources loaded in this
   * loader, in the given dataset. It uses a mapping object to match tags to
   * {@link IGeneObj} classes.
   * 
   * @param dataset
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws IllegalArgumentException
   */
  @SuppressWarnings("unchecked")
  private void convertResourcesToGeneObjs() throws IllegalArgumentException,
      SecurityException, IllegalAccessException, NoSuchFieldException {
    // Sleep for up to one second.
    try {
      Thread.sleep(2);
    } catch (InterruptedException ignore) {
    }
    this.currentTask = OsmLoadingTask.OBJECTS;
    this.setProgress(0);
    // get the Gene Obj factory
    OSMSchemaFactory factory = new OSMSchemaFactory();
    CartagenApplication.getInstance().setCreationFactory(factory);
    // the conversion is made mapping-by-mapping
    int i = 0;
    for (OsmMatching matching : mapping.getMatchings()) {
      // Sleep for up to one second.
      try {
        Thread.sleep(2);
      } catch (InterruptedException ignore) {
      }
      String featTypeName = (String) matching.getCartagenClass()
          .getField("FEAT_TYPE_NAME").get(null);
      IPopulation<IGeneObj> pop = (IPopulation<IGeneObj>) dataset
          .getCartagenPop(
              dataset.getPopNameFromClass(matching.getCartagenClass()),
              featTypeName);
      OsmGeoClass geoClass = new OsmGeoClass(pop.getNom(), featTypeName,
          matching.getType());
      dataset.getCartAGenDB().getClasses().add(geoClass);

      // case with point geometry
      if (matching.getType().equals(GeometryType.POINT)) {
        Collection<OSMResource> resources = this.getNodesFromTag(
            matching.getTag(), matching.getTagValues());
        for (OSMResource resource : resources) {
          OsmGeneObj obj = factory.createGeneObj(matching.getCartagenClass(),
              resource, this.nodes);
          obj.setCaptureTool(resource.getCaptureTool());
          obj.setChangeSet(resource.getChangeSet());
          obj.setOsmId(resource.getId());
          obj.setContributor(resource.getContributeur());
          obj.setDate(resource.getDate());
          obj.setSource(OsmSource.valueOfTag(resource.getSource()));
          obj.setTags(resource.getTags());
          obj.setVersion(resource.getVersion());
          obj.setUid(resource.getUid());
          pop.add(obj);
        }
      } else {
        Collection<OSMResource> resources = this.getWaysFromTag(
            matching.getTag(), matching.getTagValues());
        for (OSMResource resource : resources) {
          OsmGeneObj obj = factory.createGeneObj(matching.getCartagenClass(),
              resource, this.nodes);
          obj.setCaptureTool(resource.getCaptureTool());
          obj.setChangeSet(resource.getChangeSet());
          obj.setOsmId(resource.getId());
          obj.setContributor(resource.getContributeur());
          obj.setDate(resource.getDate());
          obj.setSource(OsmSource.valueOfTag(resource.getSource()));
          obj.setTags(resource.getTags());
          obj.setVersion(resource.getVersion());
          obj.setUid(resource.getUid());
          pop.add(obj);
          if (matching.getTag().equals("landuse")) {
            ((OsmSimpleLandUseArea) obj).setType(OsmLandUseTypology
                .valueOfTagValue(obj.getTags().get("landuse")).ordinal());
          }
        }
      }
      i++;
      setProgress(i * 100 / mapping.getMatchings().size());
    }
  }

  private Collection<OSMResource> getNodesFromTag(String tag,
      Set<String> tagValues) {
    Set<OSMResource> resources = new HashSet<OSMResource>();
    for (OSMResource node : this.nodes) {
      if (!node.getTags().containsKey(tag)) {
        continue;
      }
      if (tagValues == null) {
        resources.add(node);
      } else {
        if (tagValues.size() == 0) {
          resources.add(node);
        }
        if (tagValues.contains(node.getTags().get(tag))) {
          resources.add(node);
        }
      }
    }
    return resources;
  }

  private Collection<OSMResource> getWaysFromTag(String tag,
      Set<String> tagValues) {
    Set<OSMResource> resources = new HashSet<OSMResource>();
    for (OSMResource way : this.ways) {
      if (!way.getTags().containsKey(tag)) {
        continue;
      }
      if (tagValues == null) {
        resources.add(way);
      } else {
        if (tagValues.size() == 0) {
          resources.add(way);
        }
        if (tagValues.contains(way.getTags().get(tag))) {
          resources.add(way);
        }
      }
    }
    return resources;
  }

  @Override
  protected Void doInBackground() throws Exception {
    importOsmData();
    return null;
  }

  @Override
  protected void done() {
    this.dialog.setVisible(false);
    super.done();
    SwingUtilities.invokeLater(fillLayersTask);
  }

  public OsmLoadingTask getCurrentTask() {
    return currentTask;
  }

  public void setCurrentTask(OsmLoadingTask currentTask) {
    this.currentTask = currentTask;
  }

  public JDialog getDialog() {
    return dialog;
  }

  public void setDialog(JDialog dialog) {
    this.dialog = dialog;
  }
}
