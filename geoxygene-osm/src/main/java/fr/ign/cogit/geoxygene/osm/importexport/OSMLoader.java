/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.osm.importexport;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.datatools.CRSConversion;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.RoleMembre;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.TypeRelation;
import fr.ign.cogit.geoxygene.osm.schema.OSMDefaultFeature;
import fr.ign.cogit.geoxygene.osm.schema.OSMFeature;
import fr.ign.cogit.geoxygene.osm.schema.OsmCaptureTool;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeometryConversion;
import fr.ign.cogit.geoxygene.osm.schema.OsmSource;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

public class OSMLoader extends SwingWorker<Void, Void> {

    private Logger logger = Logger.getLogger(OSMLoader.class.getName());

    private enum GeometryType {
        POINT, LINE, POLYGON
    }

    // the global tags of OSM files
    public static final String TAG_BOUNDS = "bounds";
    public static final String TAG_MIN_LAT = "minlat";
    public static final String TAG_MAX_LAT = "maxlat";
    public static final String TAG_MIN_LON = "minlon";
    public static final String TAG_MAX_LON = "maxlon";

    public enum OsmLoadingTask {
        POINTS, LINES, RELATIONS, OBJECTS, PARSING, MULTIGEOMETRIES;

        public String getLabel() {
            if (this.equals(POINTS))
                return "OSM Points";
            else if (this.equals(LINES))
                return "OSM Lines";
            else if (this.equals(RELATIONS))
                return "OSM Relations";
            else if (this.equals(PARSING))
                return "Parsing";
            else if (this.equals(OBJECTS))
                return "Objects";
            else if (this.equals(MULTIGEOMETRIES))
                return "Multiple Geometries";
            return "";
        }
    }

    public enum OSMLoaderType {
        XML, POSTGIS
    }

    private OsmGeometryConversion convertor;
    private Set<OSMResource> nodes, ways, relations;
    private File fic;
    private StyledLayerDescriptor sld;
    private JDialog dialog;
    private OsmLoadingTask currentTask = OsmLoadingTask.POINTS;
    private Runnable fillLayersTask;
    private String tagFilter;
    double xMin, yMin, xMax, yMax;
    double xCentr, yCentr;
    double surf;
    File file;
    private int nbNoeuds = 0, nbWays = 0, nbRels = 0, nbResources = 0;
    private OSMLoaderType usedLoader;

    public OSMLoader(File fic, StyledLayerDescriptor sld,
            Runnable fillLayersTask, String epsg, String tagFilter,
            OSMLoaderType usedLoader) {
        this.fic = fic;
        this.sld = sld;
        this.fillLayersTask = fillLayersTask;
        this.convertor = new OsmGeometryConversion(epsg);
        this.tagFilter = tagFilter;
        this.usedLoader = usedLoader;
    }

    public void importOsmData(OSMLoaderType loaderType) throws Exception {
        this.nodes = new HashSet<OSMResource>();
        this.ways = new HashSet<OSMResource>();
        this.relations = new HashSet<OSMResource>();
        // this.loadOsmFile(fic);
        if (loaderType.equals(OSMLoaderType.XML))
            loadOsmFileSAX(fic);
        else {
            // TODO allow to switch loaders

        }

        if (this.logger.isLoggable(Level.FINE)) {
            this.logger.fine(this.nbResources + "RDF resources loaded");
            this.logger.fine(this.nbNoeuds + "nodes");
            this.logger.fine(this.nbWays + "ways");
            this.logger.fine(this.nbRels + "relations");
        }

        try {
            this.convertResourcesToFeatures();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A SAX parser that is quicker than the default DOM parser loadOsmFile.
     * 
     * @param fic
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    private void loadOsmFileSAX(File fic)
            throws ParserConfigurationException, SAXException, IOException {
        this.file = fic;
        long size = file.length();
        int nbElements = Math.round(size / 220);
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        DefaultHandler handler = new OsmResourceHandler(this, nbElements);
        parser.parse(file, handler);
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
    @SuppressWarnings("unused")
    private void loadOsmFile(File fic)
            throws SAXException, IOException, ParserConfigurationException {
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
        Element limitElem = (Element) root
                .getElementsByTagName(OSMLoader.TAG_BOUNDS).item(0);
        double minlat = Double
                .valueOf(limitElem.getAttribute(OSMLoader.TAG_MIN_LAT));
        double minlon = Double
                .valueOf(limitElem.getAttribute(OSMLoader.TAG_MIN_LON));
        double maxlat = Double
                .valueOf(limitElem.getAttribute(OSMLoader.TAG_MAX_LAT));
        double maxlon = Double
                .valueOf(limitElem.getAttribute(OSMLoader.TAG_MAX_LON));

        // on convertit ces coordonnées en Lambert 93
        IDirectPosition coinMin = CRSConversion.wgs84ToLambert93(minlat,
                minlon);
        IDirectPosition coinMax = CRSConversion.wgs84ToLambert93(maxlat,
                maxlon);
        this.xMin = coinMin.getX();
        this.yMin = coinMin.getY();
        this.xMax = coinMax.getX();
        this.yMax = coinMax.getY();
        this.xCentr = this.xMin + (this.xMax - this.xMin) / 2;
        this.yCentr = this.yMin + (this.yMax - this.yMin) / 2;

        // on calcule la surface en km²
        this.surf = (this.xMax - this.xMin) * (this.yMax - this.yMin)
                / 1000000.0;

        DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        // on charge les objets ponctuels
        this.nbNoeuds = root.getElementsByTagName(OSMFeature.TAG_NODE)
                .getLength();
        for (int i = 0; i < this.nbNoeuds; i++) {
            // Sleep for up to one second.
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignore) {
            }
            Element elem = (Element) root
                    .getElementsByTagName(OSMFeature.TAG_NODE).item(i);
            // on récupère les attributs de l'élément
            long id = Long.valueOf(elem.getAttribute(OSMFeature.ATTR_ID));
            String versionAttr = elem.getAttribute(OSMFeature.ATTR_VERSION);
            int version = 1;
            if (versionAttr != null) {
                version = Integer.valueOf(versionAttr);
            }
            int changeSet = Integer
                    .valueOf(elem.getAttribute(OSMFeature.ATTR_SET));
            String contributeur = elem.getAttribute(OSMFeature.ATTR_USER);
            int uid = 0;
            if (!contributeur.equals("")) {
                uid = Integer.valueOf(elem.getAttribute(OSMFeature.ATTR_UID));
            }
            String timeStamp = elem.getAttribute(OSMFeature.ATTR_DATE);
            Date date = null;
            try {
                date = formatDate.parse(timeStamp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // on récupère sa géométrie
            double lat = Double.valueOf(elem.getAttribute(OSMFeature.ATTR_LAT));
            double lon = Double.valueOf(elem.getAttribute(OSMFeature.ATTR_LON));
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
            System.out.println(
                    "Chargement des points " + i * 100 / this.nbNoeuds + " %");
        }
        System.out.println(this.nbNoeuds + " points chargés");

        // Sleep for up to one second.
        try {
            Thread.sleep(1);
        } catch (InterruptedException ignore) {
        }
        this.currentTask = OsmLoadingTask.LINES;
        this.setProgress(0);
        // on charge les objets linéaires
        this.nbWays = root.getElementsByTagName(OSMFeature.TAG_WAY).getLength();
        for (int i = 0; i < this.nbWays; i++) {
            Element elem = (Element) root
                    .getElementsByTagName(OSMFeature.TAG_WAY).item(i);
            // Sleep for up to one second.
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignore) {
            }
            // on récupère les attributs de l'élément
            int id = Integer.valueOf(elem.getAttribute(OSMFeature.ATTR_ID));
            int version = Integer
                    .valueOf(elem.getAttribute(OSMFeature.ATTR_VERSION));
            int changeSet = Integer
                    .valueOf(elem.getAttribute(OSMFeature.ATTR_SET));
            String contributeur = elem.getAttribute(OSMFeature.ATTR_USER);
            int uid = 0;
            if (!contributeur.equals("")) {
                uid = Integer.valueOf(elem.getAttribute(OSMFeature.ATTR_UID));
            }
            String timeStamp = elem.getAttribute(OSMFeature.ATTR_DATE);
            Date date = null;
            try {
                date = formatDate.parse(timeStamp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // on récupère sa géométrie
            ArrayList<Long> vertices = new ArrayList<Long>();
            for (int j = 0; j < elem.getElementsByTagName("nd")
                    .getLength(); j++) {
                Element ndElem = (Element) elem.getElementsByTagName("nd")
                        .item(j);
                long ref = Long.valueOf(ndElem.getAttribute("ref"));
                vertices.add(ref);
            }
            // on ne charge pas les lignes ne contenant qu'un point
            if (vertices.size() == 1)
                continue;
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
            System.out.println(
                    "Chargement des lignes " + i * 100 / this.nbWays + " %");
        }
        System.out.println(this.nbWays + " lignes chargées");

        // Sleep for up to one second.
        try {
            Thread.sleep(1);
        } catch (InterruptedException ignore) {
        }
        this.currentTask = OsmLoadingTask.RELATIONS;
        this.setProgress(0);
        // on charge les relations
        this.nbRels = root.getElementsByTagName(OSMFeature.TAG_REL).getLength();
        for (int i = 0; i < this.nbRels; i++) {
            Element elem = (Element) root
                    .getElementsByTagName(OSMFeature.TAG_REL).item(i);
            // Sleep for up to one second.
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignore) {
            }
            // on récupère les attributs de l'élément
            int id = Integer.valueOf(elem.getAttribute(OSMFeature.ATTR_ID));
            int version = Integer
                    .valueOf(elem.getAttribute(OSMFeature.ATTR_VERSION));
            int changeSet = Integer
                    .valueOf(elem.getAttribute(OSMFeature.ATTR_SET));
            String contributeur = elem.getAttribute(OSMFeature.ATTR_USER);
            int uid = 0;
            if (!contributeur.equals("")) {
                uid = Integer.valueOf(elem.getAttribute(OSMFeature.ATTR_UID));
            }
            String timeStamp = elem.getAttribute(OSMFeature.ATTR_DATE);
            Date date = null;
            try {
                date = formatDate.parse(timeStamp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // on récupère sa primitive
            TypeRelation type = TypeRelation.NON_DEF;
            for (int j = 0; j < elem.getElementsByTagName("tag")
                    .getLength(); j++) {
                Element tagElem = (Element) elem.getElementsByTagName("tag")
                        .item(j);
                String cle = tagElem.getAttribute("k");
                if (cle.equals("type")) {
                    type = TypeRelation.valueOfTexte(tagElem.getAttribute("v"));
                }
            }
            List<OsmRelationMember> membres = new ArrayList<OsmRelationMember>();
            for (int j = 0; j < elem.getElementsByTagName("member")
                    .getLength(); j++) {
                Element memElem = (Element) elem.getElementsByTagName("member")
                        .item(j);
                long ref = Long.valueOf(memElem.getAttribute("ref"));
                String role = memElem.getAttribute("role");
                membres.add(new OsmRelationMember(RoleMembre.valueOfTexte(role),
                        true, ref));
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
            System.out.println(
                    "Chargement des relations " + i * 100 / this.nbRels + " %");
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
            Element tagElem = (Element) elem.getElementsByTagName("tag")
                    .item(j);
            String cle = tagElem.getAttribute("k");
            // cas du tag outil
            if (cle.equals(OSMFeature.TAG_OUTIL)) {
                String txt = tagElem.getAttribute("v");
                OsmCaptureTool outil = OsmCaptureTool.valueOfTexte(txt);
                obj.setCaptureTool(outil);
                continue;
            }
            // cas du tag source
            if (cle.equals(OSMFeature.TAG_SOURCE)) {
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
     * @throws Exception
     */
    private void convertResourcesToFeatures() throws Exception {
        // Sleep for up to one second.
        try {
            Thread.sleep(1);
        } catch (InterruptedException ignore) {
        }
        Map<Long, OSMFeature> mapIdObj = new HashMap<Long, OSMFeature>();
        this.currentTask = OsmLoadingTask.OBJECTS;
        this.setProgress(0);

        // create empty feature collections for display
        IPopulation<IFeature> nodeFeats = new Population<>("nodes");
        IPopulation<IFeature> lineFeats = new Population<>("lines");
        IPopulation<IFeature> polygonFeats = new Population<>("polygons");

        // first create nodes
        int i = 0;
        for (OSMResource node : nodes) {
            // Sleep for up to one second.
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignore) {
            }

            // check if there is a tag filter on the loader
            if (tagFilter != null) {
                if (!tagFilter.equals(""))
                    if (!node.getTags().containsKey(tagFilter))
                        continue;
            }

            OSMDefaultFeature feature = new OSMDefaultFeature(
                    node.getContributeur(),
                    convertor.convertOsmPoint((OSMNode) node.getGeom(), true),
                    node.getId(), node.getChangeSet(), node.getVersion(),
                    node.getUid(), node.getDate(), node.getTags());
            feature.setSource(OsmSource.valueOfTag(node.getSource()));
            feature.setCaptureTool(node.getCaptureTool());
            nodeFeats.add(feature);

            i++;
            setProgress(i * 100 / (nodes.size() + ways.size()));
        }

        // then create lines
        for (OSMResource way : ways) {
            // Sleep for up to one second.
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignore) {
            }

            // check if there is a tag filter on the loader
            if (tagFilter != null) {
                if (!tagFilter.equals(""))
                    if (!way.getTags().containsKey(tagFilter))
                        continue;
            }

            if (((OSMWay) way.getGeom()).isPolygon()) {
                OSMDefaultFeature feature = new OSMDefaultFeature(
                        way.getContributeur(),
                        convertor.convertOSMPolygon((OSMWay) way.getGeom(),
                                nodes, true),
                        way.getId(), way.getChangeSet(), way.getVersion(),
                        way.getUid(), way.getDate(), way.getTags());
                feature.setSource(OsmSource.valueOfTag(way.getSource()));
                feature.setCaptureTool(way.getCaptureTool());
                polygonFeats.add(feature);
            } else {
                OSMDefaultFeature feature = new OSMDefaultFeature(
                        way.getContributeur(),
                        convertor.convertOSMLine((OSMWay) way.getGeom(), nodes,
                                true),
                        way.getId(), way.getChangeSet(), way.getVersion(),
                        way.getUid(), way.getDate(), way.getTags());
                feature.setSource(OsmSource.valueOfTag(way.getSource()));
                feature.setCaptureTool(way.getCaptureTool());
                lineFeats.add(feature);
            }

            i++;
            setProgress(i * 100 / (nodes.size() + ways.size()));
        }

        // ******************************
        // compute multipolygons
        this.currentTask = OsmLoadingTask.OBJECTS;
        this.setProgress(0);
        i = 0;
        for (OSMResource rel : relations) {
            // Sleep for up to one second.
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignore) {
            }
            // check if it's a multipolygon relation
            if (rel.getTags().containsKey("type")) {
                if (rel.getTags().get("type").equals("multipolygon")) {
                    OSMRelation primitive = (OSMRelation) rel.getGeom();
                    List<OsmRelationMember> outers = primitive
                            .getOuterMembers();
                    List<OsmRelationMember> inners = primitive
                            .getInnerMembers();
                    if (outers.size() > 1) {

                        // it means that the outer ways form the polygonal
                        // geometry
                        IDirectPositionList coords = new DirectPositionList();
                        for (OsmRelationMember outer : outers) {
                            OSMResource resource = getWayFromId(outer.getRef());
                            if (resource == null)
                                continue;
                            coords.addAll(convertor
                                    .convertOSMLine((OSMWay) resource.getGeom(),
                                            nodes, true)
                                    .coord());
                        }
                        IPolygon polygon = GeometryEngine.getFactory()
                                .createIPolygon(coords);
                        // add inner rings to polygon
                        for (OsmRelationMember inner : inners) {
                            OSMResource resource = getWayFromId(inner.getRef());
                            if (resource == null)
                                continue;
                            IRing ring = convertor.convertOSMPolygon(
                                    (OSMWay) resource.getGeom(), nodes, true)
                                    .getExterior();
                            if (ring.coord().size() < 4)
                                continue;
                            polygon.addInterior(ring);
                        }

                        OSMDefaultFeature feature = new OSMDefaultFeature(
                                rel.getContributeur(),
                                convertor.convertOSMLine((OSMWay) rel.getGeom(),
                                        nodes, true),
                                rel.getId(), rel.getChangeSet(),
                                rel.getVersion(), rel.getUid(), rel.getDate(),
                                rel.getTags());
                        feature.setSource(
                                OsmSource.valueOfTag(rel.getSource()));
                        feature.setCaptureTool(rel.getCaptureTool());
                        polygonFeats.add(feature);

                        mapIdObj.put(rel.getId(), feature);

                    } else if (outers.size() == 1) {
                        OsmRelationMember outer = outers.iterator().next();
                        IFeature outerObj = mapIdObj.get(outer.getRef());
                        if (outerObj == null) {
                            // new object to create
                            // first, create the holed geometry
                            // TODO
                            continue;
                        }
                        // only polygons can have inner rings
                        if (!(outerObj.getGeom() instanceof IPolygon))
                            continue;
                        // add inner rings in the outer geometry
                        for (OsmRelationMember inner : inners) {
                            // get the geneobj corresponding to this osm id
                            IFeature obj = mapIdObj.get(inner.getRef());
                            // case of untagged inner geometry
                            if (obj == null) {
                                OSMResource resource = getWayFromId(
                                        inner.getRef());
                                if (resource == null)
                                    continue;
                                IPolygon polygon = (IPolygon) ((IPolygon) outerObj
                                        .getGeom()).clone();
                                IRing ring = convertor.convertOSMPolygon(
                                        (OSMWay) resource.getGeom(), nodes,
                                        true).getExterior();
                                if (ring.coord().size() < 4)
                                    continue;
                                polygon.addInterior(ring);
                                if (polygon.getExterior().coord().size() >= 4)
                                    outerObj.setGeom(polygon);
                                continue;
                            }
                            IPolygon polygon = (IPolygon) ((IPolygon) outerObj
                                    .getGeom()).clone();
                            IRing ring = ((IPolygon) obj.getGeom())
                                    .getExterior();
                            polygon.addInterior(ring);
                            if (polygon.getExterior().coord().size() >= 4)
                                outerObj.setGeom(polygon);
                        }
                    }
                }
            }
            i++;
            setProgress(i * 100 / nbRels);
        }

        // add layers to sld
        sld.getDataSet().addPopulation(nodeFeats);
        sld.getDataSet().addPopulation(lineFeats);
        sld.getDataSet().addPopulation(polygonFeats);
        sld.createLayerRandomColor("nodes", IPoint.class);
        sld.createLayerRandomColor("lines", ILineString.class);
        sld.createLayerRandomColor("polygons", IPolygon.class);
    }

    private OSMResource getWayFromId(long id) {
        for (OSMResource way : this.ways) {
            if (way.getId() != id)
                continue;

            return way;
        }
        return null;
    }

    @Override
    protected Void doInBackground() throws Exception {
        importOsmData(this.usedLoader);
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

    public void setProgressForBar(int i) {
        this.setProgress(i);
    }

    public Set<OSMResource> getNodes() {
        return nodes;
    }

    public void setNodes(Set<OSMResource> nodes) {
        this.nodes = nodes;
    }

    public Set<OSMResource> getWays() {
        return ways;
    }

    public void setWays(Set<OSMResource> ways) {
        this.ways = ways;
    }

    public Set<OSMResource> getRelations() {
        return relations;
    }

    public void setRelations(Set<OSMResource> relations) {
        this.relations = relations;
    }

    public int getNbNoeuds() {
        return nbNoeuds;
    }

    public void setNbNoeuds(int nbNoeuds) {
        this.nbNoeuds = nbNoeuds;
    }

    public int getNbWays() {
        return nbWays;
    }

    public void setNbWays(int nbWays) {
        this.nbWays = nbWays;
    }

    public int getNbRels() {
        return nbRels;
    }

    public void setNbRels(int nbRels) {
        this.nbRels = nbRels;
    }

    public int getNbResources() {
        return nbResources;
    }

    public void setNbResources(int nbResources) {
        this.nbResources = nbResources;
    }

    public OSMLoaderType getUsedLoader() {
        return usedLoader;
    }

    public void setUsedLoader(OSMLoaderType usedLoader) {
        this.usedLoader = usedLoader;
    }

}
