package fr.ign.cogit.mapping.clients.geoxygene;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections15.map.HashedMap;
import org.apache.log4j.Logger;
import org.deegree.io.rtree.HyperBoundingBox;
import org.deegree.io.rtree.HyperPoint;
import org.w3c.css.sac.SACMediaList;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

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
import fr.ign.cogit.mapping.clients.ControllerII;
import fr.ign.cogit.mapping.clients.Converter;
import fr.ign.cogit.mapping.clients.frameworks.CadrageArea;
import fr.ign.cogit.mapping.clients.frameworks.Cadre;
/*
 * Cette classe permet la communication entre
 * le controleur et le convertisseur
 */
import fr.ign.cogit.mapping.datastructure.Node;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNeighbor;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNode;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaTree;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;
import fr.ign.cogit.mapping.generalindex.Record;
import fr.ign.cogit.mapping.storage.database.extractor.sql.postgres.InteratorIndexForTable;
import fr.ign.cogit.mapping.util.GeometryUtil;
import fr.ign.cogit.mapping.util.HexaGoneUtil;
import fr.ign.cogit.mapping.webentity.spatial.GeometryRecord;
import fr.ign.cogit.mapping.webentity.spatial.SpatialIndex;

public class GeoxConverter extends Converter {

    /** Logger. */
    private final static Logger LOGGER = Logger.getLogger(GeoxConverter.class
            .getName());
    /*
     * @param control2 Designe une instance du contrôleur de niveau II permet la
     * communication avec le convertisseur
     */
    protected ControllerII control2;

    /*
     * @param height correspond à la hauteur du support de visualisation
     */

    protected double height;

    /*
     * @param weight correspond à la largeur du support de visualisation
     */

    protected double weight;

    public GeoxConverter(Point interestPoint, double height, double weight,
            String table) {
        super(interestPoint, height, weight, table);
        // TODO Auto-generated constructor stub
        LinkedHashMap<ScaleInfo, CadrageArea> content = new LinkedHashMap<ScaleInfo, CadrageArea>();
        hexatree = new HexaTree(content);
        super.setHexatree(hexatree);
        control2 = new ControllerII(this);

    }

    // Ce constructeur permet d'associer la signature
    /*
     * il permet aussi de calculer les paramètres associé à ce convertisseur de
     * données...
     */

    public GeoxConverter(Point interestPoint, String table, String signature) {
        super(interestPoint, table, signature);

        Dimension dimension = java.awt.Toolkit.getDefaultToolkit()
                .getScreenSize();
        height = dimension.getHeight();
        weight = dimension.getWidth();

        super.setHeight(height);
        super.setWeight(weight);
        // initialisation de l'hexaTree
      
        // TODO Auto-generated constructor stub
        control2 = new ControllerII(this);

    }
    
    public GeoxConverter(int width, int height) {

        Dimension dimension = java.awt.Toolkit.getDefaultToolkit()
                .getScreenSize();
        
        Coordinate c = new Coordinate(0, 0);
        interestPoint = new GeometryFactory().createPoint(c);
        super.setInterestPoint(interestPoint);
        super.setHeight(height);
        super.setWeight(weight);

        super.setHeight(height);
        super.setWeight(width);
        // initialisation de l'hexaTree

        // TODO Auto-generated constructor stub
        control2 = new ControllerII(this);

    }

    // on va generer les paramètres de l'ecran
    // comme HyperBoundingBox
    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.mapping.clients.Converter#generateScreenParam()
     */

    @Override
    public HyperBoundingBox generateScreenParam() {

        // Code
        Dimension dimension = java.awt.Toolkit.getDefaultToolkit()
                .getScreenSize();
        int height = (int) dimension.getHeight();
        int width = (int) dimension.getWidth();

        HyperPoint bornInfScale = new HyperPoint(new double[] { 0, 0 });
        /*
         * @param bornSupScale designe le coin supérieure gauche
         */

        HyperPoint bornSupScale = new HyperPoint(new double[] { width, height });
        /*
         * Designe le plus grand echelle utilisée pour les elements couramment
         * MaxRectangle intialisée actuellement comme un point.
         */

        HyperBoundingBox MaxRectangle = new HyperBoundingBox(bornInfScale,
                bornSupScale);

        // TODO Auto-generated method stub
        return MaxRectangle;
    }

    /*
     * 
     * To DO...;
     */
    // un convertisseur sans cadrage
    public GeoxConverter(String table, String signature) {
        super(table, signature);
        // TODO Auto-generated constructor stub
        Dimension dimension = java.awt.Toolkit.getDefaultToolkit()
                .getScreenSize();
        height = dimension.getHeight();
        weight = dimension.getWidth();

        // origine de l'ecran est pris comme point d'interet
        Coordinate c = new Coordinate(0, 0);

        interestPoint = new GeometryFactory().createPoint(c);
        super.setInterestPoint(interestPoint);
        super.setHeight(height);
        super.setWeight(weight);
        control2 = new ControllerII(this);

    }

    public GeoxConverter() {
        Dimension dimension = java.awt.Toolkit.getDefaultToolkit()
                .getScreenSize();
        height = dimension.getHeight();
        weight = dimension.getWidth();
        // origine du cadrage qui l'origine de l'ecran et identifié par le
        // quatrième
        // cadre...
        Coordinate c = new Coordinate(0, 0);
        interestPoint = new GeometryFactory().createPoint(c);
        super.setInterestPoint(interestPoint);
        super.setHeight(height);
        super.setWeight(weight);
        control2 = new ControllerII(this);

        // TODO Auto-generated constructor stub
    }

    /*
     * Cette methode permet de convertir des geometries en population
     * geoxygenes. Les données de la table sont transmises par le Controlleur
     * niveau 2 comme des interateurs.
     */
    public Population<DefaultFeature> tableToPopulation() {
        Population<DefaultFeature> pop = new Population<DefaultFeature>();
        // demande l'extraction des geometries qui sont contenus dans la table
        // renseigné par dans son constructeur...
        List<Record<Geometry>> it = control2.obtainIterFromTable();
        int i = 0;
        String geotype = null;
        // String tableName = "Any";
        if (it != null && it.size() > 0) {

            while (i < it.size()) {
                Record<Geometry> content = it.get(i);
                // index.add(content);
                Geometry geo = content.getValue();
                if (i == 0) {
                    geotype = GeometryUtil.getType(geo);

                }
                // System.out.println("je travaille ici"+ geotype);

                Node node = content.getKey();
                // tableName = node.getTableName();
                IGeometry igeo = getFromtext(geo.toText(), node.getSrid());
                DefaultFeature defaultIgeo = new DefaultFeature(igeo);
                // voir comment charge les attributs
                pop.add(defaultIgeo);
                i++;

            }
            return setPopulationType(pop, geotype, getTable());

        } else {
            return null;
        }

    }

    /*
     * Cette methode fabrique l'index associé à la table sans toute fois
     * l'envoyer au client...
     */

    public boolean BuildtableIndex() {
        Population<DefaultFeature> pop = new Population<DefaultFeature>();
        // demande l'extraction des geometries qui sont contenus dans la table
        // renseigné par dans son constructeur...
        List<Record<Geometry>> it = control2.obtainIterFromTable();
        int i = 0;
        String geotype = null;
        // String tableName = "Any";
        if (it != null && it.size() > 0) {
            return true;

        } else {
            return false;
        }

    }

    /*
     * @param strGeom designe un string de geometrie
     * 
     * @param sridT designe la valeur en string du srid utilisé
     * 
     * @return retour la valeur une IIGeometry
     */

    public IGeometry getFromtext(String strGeom, String sridT) {

        /*
         * In version 1.0.x of PostGIS, SRID is added to the beginning of the
         * pgGeom string
         */

        if (strGeom == null) {
            throw new RuntimeException(strGeom + " est une valeur null");

        } else if (sridT == null) {

            throw new RuntimeException(sridT + " est une valeur null");
        }

        String geom = strGeom;

        int srid = Integer.parseInt(sridT);
        IGeometry geOxyGeom = null;
        try {
            geOxyGeom = WktGeOxygene.makeGeOxygene(strGeom);
        } catch (fr.ign.cogit.geoxygene.util.conversion.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (geOxyGeom instanceof IMultiPoint) {
            IMultiPoint aggr = (IMultiPoint) geOxyGeom;
            if (aggr.size() == 1) {
                aggr.get(0);
                aggr.setCRS(srid);
                return aggr;
            }
        }

        if (geOxyGeom instanceof IMultiCurve) {
            IMultiCurve<IOrientableCurve> aggr = (IMultiCurve<IOrientableCurve>) geOxyGeom;
            if (aggr.size() == 1) {
                aggr.get(0);
                aggr.setCRS(srid);
                return aggr;
            }
        }

        if (geOxyGeom instanceof IMultiSurface) {
            IMultiSurface<IOrientableSurface> aggr = (IMultiSurface<IOrientableSurface>) geOxyGeom;
            if (aggr.size() == 1) {
                aggr.get(0);
                aggr.setCRS(srid);
                return aggr;
            }
        }
        geOxyGeom.setCRS(srid);
        return geOxyGeom;

    }

    /*
     * @param geom designe un string de geometrie de jts
     * 
     * @return retour la valeur une IGeometry compactible avec geoxygene
     */

    public IGeometry getFromtext(Geometry geo) {

        /*
         * In version 1.0.x of PostGIS, SRID is added to the beginning of the
         * pgGeom string
         */

        if (geo == null) {
            throw new RuntimeException(geo + " est une valeur null");

        }

        String geom = geo.toText();
        int srid = geo.getSRID();
        IGeometry geOxyGeom = null;
        try {
            geOxyGeom = WktGeOxygene.makeGeOxygene(geom);
        } catch (fr.ign.cogit.geoxygene.util.conversion.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (geOxyGeom instanceof IMultiPoint) {
            IMultiPoint aggr = (IMultiPoint) geOxyGeom;
            if (aggr.size() == 1) {
                aggr.get(0);
                aggr.setCRS(srid);
                return aggr;
            }
        }

        if (geOxyGeom instanceof IMultiCurve) {
            IMultiCurve<IOrientableCurve> aggr = (IMultiCurve<IOrientableCurve>) geOxyGeom;
            if (aggr.size() == 1) {
                aggr.get(0);
                aggr.setCRS(srid);
                return aggr;
            }
        }

        if (geOxyGeom instanceof IMultiSurface) {
            IMultiSurface<IOrientableSurface> aggr = (IMultiSurface<IOrientableSurface>) geOxyGeom;
            if (aggr.size() == 1) {
                aggr.get(0);
                aggr.setCRS(srid);
                return aggr;
            }
        }
        geOxyGeom.setCRS(srid);
        return geOxyGeom;

    }

    protected Population<DefaultFeature> doAddPopulation(
            InteratorIndexForTable records) {

        Population<DefaultFeature> pop = new Population<DefaultFeature>();
        int i = 0;

        while (records.hasNext()) {
            Node node = records.next().getKey();
            // on verifie que le noeud contient bien les
            // information associé à l'entrée.
            Geometry geom = records.next().getValue();
            // on fabrique une geometrie geoxygene
            IGeometry igeo = getFromtext(geom.toText(), node.getSrid());
            // on surchage le node pour un DefaultFeauture
            DefaultFeature defaultIgeo = new DefaultFeature();
            defaultIgeo.setAttribute(node.toString(), igeo);
            pop.add(defaultIgeo);

        }

        return pop;
    }

    /*
     * @param pop designe une population
     * 
     * @param geotype designe le type des geometries utilisées retourne une
     * population adaptée à géoxygene Cette methode doit etre etendue chaque
     * fois qu'on a une nouvelle structure dans geoxygene
     */

    public Population<DefaultFeature> setPopulationType(
            Population<DefaultFeature> pop, String geotype, String tableName) {
        Population<DefaultFeature> newpop = new Population<DefaultFeature>(
                "TilingPlugin " + " " + tableName);
        FeatureType newFeatureType = new FeatureType();
        if (geotype != null && !geotype.isEmpty() ) {

            if (geotype.contains("POINT")) {

                newFeatureType.setGeometryType(IPoint.class);

            } else

            if (geotype.contains("LINESTRING")) {

                newFeatureType.setGeometryType(ILineString.class);

            } else

            if (geotype.contains("POLYGON")) {

                newFeatureType.setGeometryType(IPolygon.class);
                // System.out.println("oui c'est cela");

            } else {

                throw new RuntimeException(
                        "la geometrie est inconnue mettre à jour"
                                + "le segmentateur la classe GeometryConverter"+geotype);
            }

            newpop.setClasse(DefaultFeature.class);
            newpop.setPersistant(false);
            newFeatureType.setGeometryType(IPolygon.class);
            newpop.setFeatureType(newFeatureType);
            newpop.addAll(pop);

            return newpop;
        } else {
            throw new RuntimeException("le type est inconnu" + geotype);
        }

    }

    @Override
    public void deleteScale(int value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteTable(String table) {
        // TODO Auto-generated method stub

    }

    @Override
    public void translateCadrage(Point interestPoint1, Point interestPoint2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void zoomingeCadrage(Point interestPoint) {
        // TODO Auto-generated method stub

    }

    @Override
    public void offZoomingeCadrage(Point interestPoint) {
        // TODO Auto-generated method stub

    }

    @Override
    public void rotateCadrage(Point interet, double angle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void affineTransformation(Point interestPoint1, Point interestPoint2) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Record<Geometry>> sendGeomReferTO(Point pt, ScaleInfo scale) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HexaTree buildHexaTree() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HexaTree buildHexaTree(ScaleInfo scale) {
        // TODO Auto-generated method stub
        HexaTree tiling = control2.tilingGeom(scale);

        return tiling;
    }

    // permet de construire un tiling en donnée de population
    /*
     * cette methode permet de recuperer le cadrage necessaire pour une echelle
     * données.... puisqu'on connait la resolution du convertisseur on fabrique
     * un cadrage qu'on projete dans l'espace réel en équivant à une
     * homothétie...
     * 
     * @param scale designe l'echelle utilisée ou sollicitée...
     */

    public List<Population<DefaultFeature>> vueContaint(ScaleInfo scale) {
        Population<DefaultFeature> pop = new Population<DefaultFeature>();
        Population<DefaultFeature> newpop = new Population<DefaultFeature>();

        Population<DefaultFeature> popAll = new Population<DefaultFeature>();
        List<Population<DefaultFeature>> popAllIN = new ArrayList<Population<DefaultFeature>>();

        String srid = "4559";
        Node node = null;
        int i = 0;
       // ControllerII.TransfertCurrentData clientData =  control2.new TransfertCurrentData(scale);
      //   control2.setScaleInfo(scale);
        //control2.Apply();;
       //  ControllerII control21 = new ControllerII(this, scale);
       //  ControllerII.TransfertCurrentData clientData =  control21.new TransfertCurrentData(scale);
        //List<Record<Geometry>> contains = control2.obtainDataFromPoint(scale);
        
       //  clientData.obtainDataFromPoint(scale);
       //  List<Record<Geometry>> contains = clientData.getMysortie();
          control2.ApplyDaeamon(scale);
          List<Record<Geometry>> contains = control2.getLstsortie(); 
     //   List<Record<Geometry>> contains = control21.getLstsortie();
        


        ConcurrentHashMap<String, List<DefaultFeature>> storeToCollection = 
                storeToCollection(contains);

        for (String key : storeToCollection.keySet()) {

            pop = new Population<DefaultFeature>();
            List<DefaultFeature> listdfaultFeat = storeToCollection.get(key);

            for (DefaultFeature defaultf : listdfaultFeat) {

                pop.add(defaultf);

            }

            newpop = setPopulationType(pop, key, null);
            popAllIN.add(newpop);

        }

        return popAllIN;

    }
    
    /*
     * methode permet d'appliquer le daemon de 
     * mise a jour des contenus....une fois les
     * données transmise au client
     */
    
//    public void applyUpdateDaemon(boolean value){
//        
//        control2.ApplyDaeamon(value);
//
//    }

    public ConcurrentHashMap<String, List<DefaultFeature>> storeToCollection(
            List<Record<Geometry>> records) {
        Node node = null;
        List<DefaultFeature> lst = new ArrayList<DefaultFeature>();
        List<DefaultFeature> newlst = new ArrayList<DefaultFeature>();
        ConcurrentHashMap<String, List<DefaultFeature>> store = new ConcurrentHashMap<String, List<DefaultFeature>>();
        if (records != null && records.size() > 0) {
            for (Record<Geometry> record : records) {

                node = record.getKey();
                Geometry geo = record.getValue();
                IGeometry igeo = getFromtext(geo.toText(), node.getSrid());
                DefaultFeature defaultIgeo = new DefaultFeature(igeo);
                String geotype = node.getTransmittedInfo().getGeoType();
                String tableName = node.getTableName();

                lst = store.get(geotype);

                if (lst == null) {
                    newlst.add(defaultIgeo);
                    lst = store.putIfAbsent(geotype, newlst);

                    if (lst == null) {
                        // put succeeded, use new value
                        lst = newlst;
                        store.remove(geotype);
                        store.put(geotype, lst);

                    }

                } else {

                    lst.add(defaultIgeo);
                    store.remove(geotype);
                    store.put(geotype, lst);

                }
            }
        }
        return store;
    }

    public boolean existTypeInMap(String type,
            ConcurrentHashMap<String, List<DefaultFeature>> concurent) {

        for (String key : concurent.keySet()) {
            if (key.equals(type)) {
                return true;
            }

        }

        return false;
    }

    public LinkedHashMap<Integer, List<Population<DefaultFeature>>> displayTiling(
            ScaleInfo scale) {

        LinkedHashMap<Integer, List<Population<DefaultFeature>>> cadragePop = new LinkedHashMap<Integer, List<Population<DefaultFeature>>>();
        Population<DefaultFeature> popAll = new Population<DefaultFeature>();
        List<Population<DefaultFeature>> popAllIN = new ArrayList<Population<DefaultFeature>>();

        Population<DefaultFeature> pop = new Population<DefaultFeature>();
        // les donnees tuilées en fonction de la zone de cadrage
        HexaTree tiling = buildHexaTree(scale);
        int i = 0;

        for (ScaleInfo myscale : tiling.getTree().keySet()) {
            if (myscale.getIdScale() == scale.getIdScale()) {
                CadrageArea cadrageArea = tiling.getTree().get(myscale);

                System.out.println(cadrageArea.toString());

                for (Integer c : cadrageArea.getCadrageArea().keySet()) {
                    popAllIN = convertCadreData(cadrageArea.getCadrageArea()
                            .get(c));
                    cadragePop.put(c, popAllIN);
                }
                break;
            }
        }

        return cadragePop;
    }

    public Population<DefaultFeature> convertCadre(Cadre cadre) {
        HexaNode[] hexaCadre = cadre.HexaForFrameNumberised();
        Population<DefaultFeature> pop = new Population<DefaultFeature>();
        Population<DefaultFeature> popAll = new Population<DefaultFeature>();
        String srid = "4559";
        for (HexaNode hex : hexaCadre) {

            HexaNeighbor neighborSister = new HexaNeighbor(hex);
            // on fabrique toutes ces soeurs.
            neighborSister.generatePossibleSister();
            // fabrication du polygone hexagone
            Polygon Hexpoly = HexaGoneUtil.builHexagone(Arrays
                    .asList(neighborSister.getSisters()));
            Node node = null;
            for (Integer key : hex.getConnectedSpatialEntities().keySet()) {
                Record<Geometry> record = hex.getConnectedSpatialEntities()
                        .get(key);
                node = record.getKey();
                Geometry geo = record.getValue();
                IGeometry igeo = getFromtext(geo.toText(), node.getSrid());
                DefaultFeature defaultIgeo = new DefaultFeature(igeo);
                String geotype = node.getTransmittedInfo().getGeoType();
                String tableName = node.getTableName();

                // defaultIgeo.setFeatureType(geo.toText());
                // // voir comment charge les attributs
                pop.add(defaultIgeo);
                popAll.addCollection(setPopulationType(pop, geotype, tableName));
                pop = new Population<DefaultFeature>();

            }

            if (node != null) {
                srid = node.getSrid();
            }
            IGeometry igeo = getFromtext(Hexpoly.toText(), srid);
            DefaultFeature defaultIgeo = new DefaultFeature(igeo);
            pop.add(defaultIgeo);
            popAll.addCollection(setPopulationType(pop, "POLYGON", "TILING"));
            pop = new Population<DefaultFeature>();
        }

        return popAll;
    }

    public List<Population<DefaultFeature>> convertCadreData(Cadre cadre) {
        HexaNode[] hexaCadre = cadre.HexaForFrameNumberised();
        Population<DefaultFeature> pop = new Population<DefaultFeature>();
        Population<DefaultFeature> popAll = new Population<DefaultFeature>();

        List<Population<DefaultFeature>> popAllIN = new ArrayList<Population<DefaultFeature>>();
        String srid = "4559";
        for (HexaNode hex : hexaCadre) {

            HexaNeighbor neighborSister = new HexaNeighbor(hex);
            // on fabrique toutes ces soeurs.
            neighborSister.generatePossibleSister();
            // fabrication du polygone hexagone
            Polygon Hexpoly = HexaGoneUtil.builHexagone(Arrays
                    .asList(neighborSister.getSisters()));
            Node node = null;
            for (Integer key : hex.getConnectedSpatialEntities().keySet()) {
                Record<Geometry> record = hex.getConnectedSpatialEntities()
                        .get(key);
                node = record.getKey();
                Geometry geo = record.getValue();
                IGeometry igeo = getFromtext(geo.toText(), node.getSrid());
                DefaultFeature defaultIgeo = new DefaultFeature(igeo);
                String geotype = node.getTransmittedInfo().getGeoType();
                String tableName = node.getTableName();

                // defaultIgeo.setFeatureType(geo.toText());
                // // voir comment charge les attributs
                pop.add(defaultIgeo);
                popAllIN.add(setPopulationType(pop, geotype, tableName));
                pop = new Population<DefaultFeature>();

            }

            if (node != null) {
                srid = node.getSrid();
            }
            IGeometry igeo = getFromtext(Hexpoly.toText(), srid);
            DefaultFeature defaultIgeo = new DefaultFeature(igeo);
            pop.add(defaultIgeo);
            popAllIN.add(setPopulationType(pop, "POLYGON", "TILING"));
            pop = new Population<DefaultFeature>();
        }

        return popAllIN;
    }
}
