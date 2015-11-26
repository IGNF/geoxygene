package fr.ign.cogit.mapping.clients;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import com.vividsolutions.jts.precision.SimpleGeometryPrecisionReducer;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;
import fr.ign.cogit.mapping.clients.frameworks.CadrageArea;
import fr.ign.cogit.mapping.clients.frameworks.Cadre;
import fr.ign.cogit.mapping.datastructure.Node;
import fr.ign.cogit.mapping.datastructure.RTreeIndex;
import fr.ign.cogit.mapping.datastructure.RtreeMultiLevelIndex;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNeighbor;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNode;
import fr.ign.cogit.mapping.datastructure.management.ManageRtreeMultiLevel;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;
import fr.ign.cogit.mapping.generalindex.Record;
import fr.ign.cogit.mapping.storage.database.extractor.sql.postgres.InteratorIndexForTable;
import fr.ign.cogit.mapping.storage.database.extractor.sql.postgres.PostgresExtractor;
import fr.ign.cogit.mapping.util.HexaGoneUtil;
import fr.ign.cogit.mapping.webentity.spatial.GeometryRecord;
import fr.ign.cogit.mapping.webentity.spatial.SpatialIndex;

/*
 * Cette classe permet de faire une delegation
 * d'activité... le controleur de niveau II est 
 * chargé d'aller rechercher des informations
 * au niveau de la base de données ou des indexes
 * qui seront par suite envoyé au controleur de niveau..
 * qui pourra ainsi fabriquer une  indexation tuilé en hexagone
 * afin de facilement communiqué avec le client
 * @author Dr Tsatcha D.
 */

public class ControllerI {

    /** Logger. */
    private final static Logger LOGGER = Logger.getLogger(ControllerI.class
            .getName());

    /*
     * @param myextractor designe l'extracteur de la base de données postgre
     */
    protected PostgresExtractor myextractor;
    /*
     * @param designe le manager qui s'occupe de la gestion du Rtree
     * multi-echelle
     */

    protected ManageRtreeMultiLevel manager;

    /**
     * @param manager
     *            designe le manager qui permet de gerer l'index
     * @param myextractorParam
     *            se charge pour la connexion à la base de données
     * 
     */

    private boolean bulkLoading = false;

    // initalisation de la structure du mutli-index
    ConcurrentHashMap<ScaleInfo, RTreeIndex> multiLevelIndex;
    // le chemin de stockage des differents indexes
    protected final String Directory = "C:/Users/dtsatcha/Desktop/IGN/java/tiling-geoxygene-mapping/src/main/java/fr/ign/cogit/mapping/webentity/spatial";

    // on fabrique une connexion à la base de donnée

    // on initialise le multi-index
    protected RtreeMultiLevelIndex multiLevel;

    // on intialise le manager permettant de gerer la communication
    // la base de données et le client.

    public ControllerI(ManageRtreeMultiLevel managerParam,
            PostgresExtractor myextractorParam) {
        super();
        this.manager = managerParam;
        this.myextractor = myextractorParam;
    }

    public ControllerI() {
        super();

        multiLevelIndex = new ConcurrentHashMap<ScaleInfo, RTreeIndex>();
        multiLevel = new RtreeMultiLevelIndex(multiLevelIndex);
        // manager de gestion des indexes
        this.manager = new ManageRtreeMultiLevel(multiLevel, Directory);
        // l'extracteur...
        this.myextractor = new PostgresExtractor();
    }

    /*
     * permet de generer une population d'une table pour affichage à geoxygene
     * 
     * @param tableName designe la table à laquelle on s'interesse
     * 
     * @param signature elle peut etre : geometrie ou empreinte
     * 
     * @return retourne une population geoxygene de DefaultFeature
     */
    public List<Record<Geometry>> generateTablePopFromPostgis(String tableName,
            String signature, ScaleInfo scale, RTreeIndex index) {

        // retourne un iterateur d'information de la base de données
        InteratorIndexForTable it = myextractor.generateIteratorTable(
                tableName, signature, scale);

        return loadValues(index, it, scale);

    }

    /*
     * L'interet de cette approche est de fabriquer le noeud afin d'aller
     * recuperer ces geometries dans la table d'index comme des interateur...
     * l'index existe deja par contre la table peut ne pas exister... afin de
     * les afficher
     * 
     * @param tableName designe la table à laquelle on s'interesse
     * 
     * @param signature elle peut etre : geometrie ou empreinte
     * 
     * @return retourne une population geoxygene de DefaultFeature
     */

    public List<Record<Geometry>> obtainTableFromIndex(String tableName,
            String signature, ScaleInfo scale, RTreeIndex index) {

        // index.open();
        index.doOpen();
        // on demande un interateur des indexes... de cette echelle
        // contenu dans la base de stockage

        List<Record<Geometry>> selectedInfo = new ArrayList<Record<Geometry>>();

        Iterator<Record<Geometry>> it = index.doIterator();
        // uniquement les contenus de la table sollicitée
        Iterator<Record<Geometry>> itFinal = null;
        String geotype = null;
        int i = 0;
        if (it == null) {
            ControllerI.LOGGER
                    .error("Aucune donnée n'a été extraite de l'index");
        } else {
            // on parcourt l'index en verifiant que les contenus
            // de la table sont presents
            while (it.hasNext()) {

                // System.out.println(geotype);
                Record<Geometry> content = it.next();
                Node node = content.getKey();

                // on recupère uniquement les elements
                // de la table en question...
                // System.out.println("voila leur noeud"+node.toString());
                if ((node.getTableName().equals(tableName)
                        && node.getSignature().equals(signature) && node
                        .getScale().equals(scale))) {
                    selectedInfo.add(content);
                    i++;
                }
            }
            index.close();

        }

        // dans le cas que l'index existe mais la
        // la table n'est pas inddiqué on recupère de la
        // la base de données

        if (i == 0) {
            // l'echelle existe mais la table en question n'existe pas
            return generateTablePopFromPostgis(tableName, signature, scale,
                    index);

        } else {
            return selectedInfo;
        }
        //
        // RTreeIndex index = manager.useAnLevel(scale.getMinScaleValue());
        // loadValues(index, it);
        //

    }

    // pour recuperer les valeurs de geometries pour une echelle donnée
    /*
     * @value designe une valeur quelque dont on voudrait afficher ces
     * informations sur geoxygene
     */

    public Iterator<Record<Geometry>> obtainGeoFromScale(int value) {

        RTreeIndex index = manager.useAnLevel(value);
        index.open();
        Population<DefaultFeature> pop = new Population<DefaultFeature>();
        int i = 0;
        String geotype = null;
        ;
        Iterator<Record<Geometry>> it = index.iterator();
        return it;
    }

    /*
     * Cette methode permet de rechercher les geometries d'une echelle
     * appartenant au cadrage.
     * 
     * @param scaleinfo designe les informations sur l'echelle
     * 
     * @param cadrage designe les informations sur le cadrage
     */
    public List<Record<Geometry>> searchGeoFromCadrage(ScaleInfo scaleinfo,
            CadrageArea cadrage) {
        PrecisionModel precModel = new PrecisionModel();
        SimpleGeometryPrecisionReducer reducer = new SimpleGeometryPrecisionReducer(
                precModel);
        // return reducer.reduce(g);

        List<Record<Geometry>> selectContent = new ArrayList<Record<Geometry>>();

        if (manager.existAnLevel(scaleinfo.getMinScaleValue())) {
            RTreeIndex index = manager.useAnLevel(scaleinfo.getMinScaleValue());

            index.open();
            Iterator<Record<Geometry>> it = index.iterator();
            index.open();
            while (it.hasNext()) {
                Record<Geometry> content = it.next();
                Node node = content.getKey();
                // on verifie que le noeud contient bien les
                // information associé à l'entrée.
                Geometry geom = content.getValue();
                // on fabrique une geometrie geoxygene

                if (reducer.reduce(cadrage.buildPolygonOfCadrageArea())
                        .intersects(reducer.reduce(geom))) {

                    System.out.println(cadrage.toString());
                    System.out.println(geom.toText());

                    // j'ai encore des problème de validité des geometries
                    // ceci est à voir..

                    try {
                        Geometry newgeo = reducer.reduce(
                                cadrage.buildPolygonOfCadrageArea())
                                .intersection(reducer.reduce(geom));
                        Record<Geometry> newcontent = GeometryRecord.create(
                                node, newgeo);
                        selectContent.add(newcontent);
                    } catch (TopologyException e) {
                        System.out.println(e.getMessage());
                    }

                }

            }
        } else {
            ControllerI.LOGGER.error("les information sur"
                    + scaleinfo.toString() + " n'a été chargé");
        }

        return selectContent;
    }

    /*
     * Cette methode permet de rechercher uniquement un cadre 
     * dans l'index Rtree..; 
     * @param scaleinfo : 
     * 
     * sont les informations sur 'echelle
     * @param cadre 
     * designe le cadre du cadrage qui nous interesse pour
     * extraire des contenus..
     */
    
    
    public List<Record<Geometry>> searchGeoFromCadre(ScaleInfo scaleinfo,
            Cadre cadre) {
        PrecisionModel precModel = new PrecisionModel();
        SimpleGeometryPrecisionReducer reducer = new SimpleGeometryPrecisionReducer(
                precModel);
        // return reducer.reduce(g);

        List<Record<Geometry>> selectContent = new ArrayList<Record<Geometry>>();

        if (manager.existAnLevel(scaleinfo.getMinScaleValue())) {
            RTreeIndex index = manager.useAnLevel(scaleinfo.getMinScaleValue());

            index.open();
            Iterator<Record<Geometry>> it = index.iterator();
            index.open();
            while (it.hasNext()) {
                Record<Geometry> content = it.next();
                Node node = content.getKey();
                // on verifie que le noeud contient bien les
                // information associé à l'entrée.
                Geometry geom = content.getValue();
                // on fabrique une geometrie geoxygene

                if (reducer.reduce(cadre.buildPolygonOfCadre()).intersects(
                        reducer.reduce(geom))) {

                    // System.out.println(cadrage.toString());
                    // System.out.println(geom.toText());

                    // j'ai encore des problème de validité des geometries
                    // ceci est à voir..

                    try {
                        Geometry newgeo = reducer.reduce(
                                cadre.buildPolygonOfCadre()).intersection(
                                reducer.reduce(geom));
                        Record<Geometry> newcontent = GeometryRecord.create(
                                node, newgeo);
                        selectContent.add(newcontent);
                    } catch (TopologyException e) {
                        System.out.println(e.getMessage());
                    }

                }

            }
        } else {
            ControllerI.LOGGER.error("les information sur"
                    + scaleinfo.toString() + " n'a été chargé");
        }

        return selectContent;
    }
    
    
    /*
     * Cette methode permet de recuperer les objets geometriques
     * connectés à  un cellule hexagonale
     * @param scaleinfo 
     * designe les informations sur l'echelle
     * @param hex 
     * designe la cellule hexagonale.
     */
    
    public List<Record<Geometry>> searchGeoFromHex(ScaleInfo scaleinfo,
            HexaNode hex) {
        PrecisionModel precModel = new PrecisionModel();
        SimpleGeometryPrecisionReducer reducer = new SimpleGeometryPrecisionReducer(
                precModel);
        // return reducer.reduce(g);

        List<Record<Geometry>> selectContent = new ArrayList<Record<Geometry>>();

        if (manager.existAnLevel(scaleinfo.getMinScaleValue())) {
            RTreeIndex index = manager.useAnLevel(scaleinfo.getMinScaleValue());

            index.open();
            Iterator<Record<Geometry>> it = index.iterator();
            index.open();
            while (it.hasNext()) {
                Record<Geometry> content = it.next();
                Node node = content.getKey();
                // on verifie que le noeud contient bien les
                // information associé à l'entrée.
                Geometry geom = content.getValue();
                
                HexaNeighbor neighborSister = new HexaNeighbor(hex);
                // neighborSister.generatePossibleSister();
                 Polygon Hexpoly = HexaGoneUtil
                         .builHexagone(Arrays
                                 .asList(neighborSister
                                         .getSisters()));
                // on fabrique une geometrie geoxygene

                if (reducer.reduce(Hexpoly).intersects(
                        reducer.reduce(geom))) {

                    // System.out.println(cadrage.toString());
                    // System.out.println(geom.toText());

                    // j'ai encore des problème de validité des geometries
                    // ceci est à voir..

                    try {
                        Geometry newgeo = reducer.reduce(
                                Hexpoly.intersection(
                                reducer.reduce(geom)));
                        Record<Geometry> newcontent = GeometryRecord.create(
                                node, newgeo);
                        selectContent.add(newcontent);
                    } catch (TopologyException e) {
                        System.out.println(e.getMessage());
                    }

                }

            }
        } else {
            ControllerI.LOGGER.error("les information sur"
                    + scaleinfo.toString() + " n'a été chargé");
        }

        return selectContent;
    }
    
     /*
     * Cette methode permet de verifier si un index est deja creer afin de
     * choisir dans quelle direction ou aller rechercher l'information si
     * l'information n'est pas encore dans l'index on le cree
     */
    public List<Record<Geometry>> obtainTable(String tableName,
            String signature, ScaleInfo scale) {

        if (manager.existAnLevel(scale.getMinScaleValue())) {
            RTreeIndex index = manager.useAnLevel(scale.getMinScaleValue());

            // System.out.println(" je suis controleur 1" + scale.toString()
            // + tableName + signature);

            return obtainTableFromIndex(tableName, signature, scale, index);

        } else {
            manager.createOneLevel(scale.getMinScaleValue(),
                    scale.getMaxScaleValue());
            RTreeIndex index = manager.useAnLevel(scale.getMinScaleValue());
            //
            return generateTablePopFromPostgis(tableName, signature, scale,
                    index);

        }

    }

    public PostgresExtractor getMyextractor() {
        return myextractor;
    }

    public void setMyextractor(PostgresExtractor myextractor) {
        this.myextractor = myextractor;
    }

    public ManageRtreeMultiLevel getManager() {
        return manager;
    }

    public void setManager(ManageRtreeMultiLevel manager) {
        this.manager = manager;
    }

    // le plus gros changement...
    /*
     * permet de retourner une liste de geometrie venant d'une table et les
     * indexés par Rtree multi-echelle..
     */

    public List<Record<Geometry>> loadValues(RTreeIndex index,
            InteratorIndexForTable it, ScaleInfo scale) {
        Population<DefaultFeature> pop = new Population<DefaultFeature>();
        int i = 0;
        List<Record<Geometry>> selectContent = new ArrayList<Record<Geometry>>();
        String geotype = null;
        String tableName = "Any";
        if (it != null) {
            // je recupère les informations au niveau du manager
            // recemment inséreré
            // for (ScaleInfo s : manager.getRtreemultilevel()
            // .getMultiLevelIndex().keySet()) {
            //
            // if (s.getIdScale() == scale.getIdScale()) {
            // index.setMaxRectangle(s.getMaxRectangle());
            //
            // }
            // }

            index.open();
            while (it.hasNext()) {
                Record<Geometry> content = it.next();
                selectContent.add(content);
                index.add(content);
            }
        } else {
            ControllerI.LOGGER.error("geometry est indexe" + it
                    + " n'a été chargé");
        }

        /*
         * il faut sauvergarder le contexte de l'index avec son nouveau
         * boundingbox
         */

        scale.setMaxRectangle(index.getMaxRectangle());
        manager.upDateScale(scale, index);
        // manager.getRtreemultilevel().getMultiLevelIndex().put(scale, index);
        manager.saveLevelsInFile();
        index.close();
        return selectContent;
    }

}
