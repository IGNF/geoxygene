package fr.ign.cogit.mapping.datastructure.hexaTree;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.geotools.referencing.operation.matrix.AffineTransform2D;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;
import com.vividsolutions.jts.operation.valid.IsValidOp;

import fr.ign.cogit.mapping.clients.ControllerII;
import fr.ign.cogit.mapping.clients.Converter;
import fr.ign.cogit.mapping.clients.frameworks.CadrageArea;
import fr.ign.cogit.mapping.clients.frameworks.Cadre;
import fr.ign.cogit.mapping.datastructure.Node;
import fr.ign.cogit.mapping.datastructure.management.ManageRtreeMultiLevel;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;
import fr.ign.cogit.mapping.generalindex.Record;
import fr.ign.cogit.mapping.util.GeometryUtil;
import fr.ign.cogit.mapping.util.HexaGoneUtil;
import fr.ign.cogit.mapping.util.ScaleInfoUtil;
import fr.ign.cogit.mapping.util.TransFormationUtil;
import fr.ign.cogit.mapping.webentity.spatial.GeometryRecord;

/*
 * Description de l'algorithme...
 * On prend un objet  geometrique on teste s'il intersecte le cadrage si oui...
 * on effecteur la difference symetrique et garde ce qui appartiennent au cadre.
 * Pour tous les sous cadres qui composent le cadrage on teste l'interesection si oui
 * prend la difference symetrique...si ce n'est pas le cas on continue...
 * Ensuite on teste si cette differnce est contenue dans le hexagone geneteur du cadre dont 
 * On determine les coordonnées de cette hexagone en utilisant les fonctions  de 
 * de HexaGoneUtil.java plus precisement la fonction computeHexaCenterFromPoint, les valeur de x
 * vont tenir compte de la borne supérieur de l'element le plus à gauche de la carte (x0, y0)...
 * dont les valeur à envoyer comme parametre sont x-x0, y-y0
 * il intersecte
 */

public class HexaTree implements java.io.Closeable {
    // architecture de l'arbre d'adressage

    protected LinkedHashMap<ScaleInfo, CadrageArea> tree = new LinkedHashMap<ScaleInfo, CadrageArea>();

    /*
     * @param Cadrage designe le cadre suivant lequel les objets retenus seront
     * placés..
     */

    protected CadrageArea cadrage;
    /*
     * @param scale Les informations sur l'echelle
     * 
     * @see ScaleInfo
     * 
     * @see the file IndexInfo in the project folder
     */

    protected ScaleInfo scale;

    /*
     * @param filepath designe le chemin de sauvegarde de des contenus indexes.
     */

    /*
     * 
     * 
     */
    
    /*
     * @param manager 
     * designe le manager
     */
    protected ManageRtreeMultiLevel manager;
    
    /*
     * @param convertisseur
     * designe le convertisseur avec tous ces paramètres
     * de visualisation....
     */
    protected Converter convertisseur;
    

    
    
    public HexaTree(String pathstore) {
        super();
        this.pathstore = pathstore;
    }
    
    
    

    /**
     * @param manager
     * @param convertisseur
     * @param pathstore
     */
    public HexaTree(ManageRtreeMultiLevel manager, Converter convertisseur,
            String pathstore) {
        super();
        this.manager = manager;
        this.convertisseur = convertisseur;
        this.pathstore = pathstore;
        initialiseHexaTree(manager,convertisseur);
        
    }

/*
 * Initialisation des paramètres de configuration de l'hexatree
 * en fonction des informations forunier 
 * par le copnvertisseur et la manager...
 */
    

    public void initialiseHexaTree(ManageRtreeMultiLevel manager,
            Converter convertisseur) {
        if (tree != null) {
            // recuperation des données du cadrage
            CadrageArea cadrageArea = new CadrageArea(
                    convertisseur.getInterestPoint(),
                    convertisseur.getHeight(), convertisseur.getWeight());
            // parcours du manager des echelles
            for (ScaleInfo s : manager.getRtreemultilevel()
                    .getMultiLevelIndex().keySet()) {
                // deduction de la transformation associée à chaque echelle
                AffineTransform2D transFormation = TransFormationUtil
                        .TransmissionFromScale(s, manager, convertisseur);
                // correspondance associée au cadrage à une echelle
                // quelconque...
                CadrageArea cadrageAreaImg = cadrageArea
                        .imageCadrageArea(transFormation);
                // on charge les instance des cadrage dans l'hexaTree
                tree.put(s, cadrageAreaImg);
                // recherche l'image de ce cadrage dans l'echelle associée

            }
        }
    }


    protected String pathstore;

    /**
     * @param hexatree
     * @param cadrage
     *            designe le cadrage courant de visualisation en fonction du
     *            point d'interet de l'utilisateur. On devra préalablement
     *            initialiser la hexaTree avec les differents cadrage
     *            intialement calculer
     */
    public HexaTree(LinkedHashMap<ScaleInfo, CadrageArea> hexatreeParam,
            String path, AffineTransform2D Trans) {
        super();
        this.tree = hexatreeParam;

    }

    /*
     * simple hexatree
     */

    public HexaTree(LinkedHashMap<ScaleInfo, CadrageArea> hexatreeParam) {
        super();
        this.tree = hexatreeParam;

    }
    
    /*
     * Le constructeur responsable de la gestion de l'index...
     */
    
    

    // ajouter un contenu dans un hexaTree
    /*
     * @param geom la geometrique qu'on souhaite ajouter..;
     */

  

    /**
     * 
     */

    public boolean insert(Record<Geometry> geom, ScaleInfo scale, int Id) {

        // on recupère le cadrage correspondant
        HexaNode[] hexaCadre = null;
        CadrageArea cadrageArea = null;
        CadrageArea MycadrageArea = null;
        Cadre cadre = null;
        ScaleInfo scaleLook = null;
        TransmittedNode trans =null;  
         Node node = geom.getKey();
         Geometry geo=geom.getValue();
        com.vividsolutions.jts.geom.Geometry geoc = null;
        Geometry geoC = null;
        LinkedHashMap<Integer, Cadre> cadres = null;

        boolean test = false;
        if (tree != null && tree.size() > 0) {

            for (ScaleInfo s : tree.keySet()) {
                if (scale.getIdScale() == s.getIdScale()) {
                    cadrageArea = tree.get(s);
                    scaleLook = s;
                    System.out.println(s.printScale());
                    break;
                }
            }

            if (cadrageArea != null) {
                // stockage du cadrage dans l'indexation hexatree.
                geo= cadrageArea.buildPolygonOfCadrageArea();
                trans = new TransmittedNode(
                        0,0,0,GeometryUtil.getType(geo),-1,-1);
                   node.setTransmittedInfo(trans);
                   
                   GeometryRecord.create(node,
                           geo);

                if (cadrageArea.buildPolygonOfCadrageArea().intersects(
                        (geom.getValue()))) {
                    // la zone d'intersection au cadrage

                    try {
                        geoc = cadrageArea.buildPolygonOfCadrageArea()
                                .intersection(geom.getValue());
                    } catch (TopologyException e) {

                        System.out.println(e.getMessage());
                    }

                    cadres = cadrageArea.getCadrageArea();
                    // la zone d'intersection à chaque cadre
                    for (Integer key : cadres.keySet()) {
                        cadre = cadres.get(key);
                        if (cadres.get(key).buildPolygonOfCadre()
                                .intersects(geoc)) {
                            Geometry geocHex = cadres.get(key)
                                    .buildPolygonOfCadre().intersection(geoc);
                            
                            /*
                             * charge le cadre
                             */
                            
                             node = geom.getKey();
                             geo = cadres.get(key).buildPolygonOfCadre();
                            // on recupère les paramètres de la cellule
                            // hexagone qui permis la transmission..
                             trans = new TransmittedNode(
                                   0,0,0,GeometryUtil.getType(geo),key,-1);
                              node.setTransmittedInfo(trans);
                              
                              GeometryRecord.create(node,
                                      geo);
                              
                              //creation du cadre...
                            
                            // la liste des hexagones associée au cadre
                            hexaCadre = cadres.get(key)
                                    .HexaForFrameNumberised();
                            int i = 0;

                            for (i = 0; i < hexaCadre.length; i++) {
                                // for (HexaNode hex : hexaCadre) {
                                HexaNode hex = hexaCadre[i];
                                HexaNeighbor neighborSister = new HexaNeighbor(
                                        hexaCadre[i]);
                                // on fabrique toutes ces soeurs.
                                neighborSister.generatePossibleSister();

                                // (ArrayList)Arrays.asList(neighborSister.getSisters());
                                // conversion d'un tableau en liste...
                                // on prendre la liste des soeurs pour fabriquer
                                // le polygone parce qu'elle est ordonnée
                                Polygon Hexpoly = HexaGoneUtil
                                        .builHexagone(Arrays
                                                .asList(neighborSister
                                                        .getSisters()));
                                
                               /*
                                * creation de l'hexagone...
                                */
                                
                                node = geom.getKey();
                                geo = cadres.get(key).buildPolygonOfCadre();
                               // on recupère les paramètres de la cellule
                               // hexagone qui permis la transmission..
                                trans = new TransmittedNode(
                                      0,0,0,GeometryUtil.getType(geo),key,i);
                                 node.setTransmittedInfo(trans);
                                 
                                 GeometryRecord.create(node,
                                         geo);
                                 //fin creation de l'hexagone

                                if (Hexpoly.intersects(geocHex)) {
                                    try {
                                        geoC = Hexpoly.intersection(geocHex);
                                    } catch (TopologyException e) {
                                        System.out.println(e.getMessage());
                                    }
                                     node = geom.getKey();
                                     geo = geom.getValue();
                                    // on recupère les paramètres de la cellule
                                    // hexagone qui permis la transmission..
                                     trans = new TransmittedNode(
                                            hex.getIndiceX(), hex.getIndiceY(),
                                            hex.getGeneration(),node.getTransmittedInfo().geoType);
                                     node.setTransmittedInfo(trans);

                                    // on fabrique l'enregistrement...
                                     // creation de la cellule
                                    hex.getConnectedSpatialEntities()
                                            .put(hexaCadre[i]
                                                    .getConnectedSpatialEntities()
                                                    .size(),
                                                    GeometryRecord.create(node,
                                                            geo));
                                    // fin creation de la cellule???
                                    // on enregistre pas des geometries...
                                    // ici on remonte jusqu'au hexaTree et ces
                                    // cadre...
                                    // on garde le cadrage, le cadre et les
                                    // cellule connected...
                                    // qu'on retire pour inserer le nouveau

                                    return true;

                                }

                            }

                        }
                        // on remet a jour le cadre

                    }

                }

            }
        }
        return false;

    }

    public LinkedHashMap<ScaleInfo, CadrageArea> getTree() {
        return tree;
    }

    public void setTree(LinkedHashMap<ScaleInfo, CadrageArea> tree) {
        this.tree = tree;
    }

    public boolean delete(Geometry geom, ScaleInfo scale, int Id) {

        return false;
    }

    /*
     * @param geom
     */
    public boolean remove(Geometry geom) {

        return false;

    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }
    
    /* Cette methode vise 
     * à modifier les contenus des informations 
     * dans l'hexatreeIndex en rechargeant uniquement
     * les contenus qu'il faut modifier...dans l'index
     * et ceux qui qui deviendrons cadre4 centrale et la relation
     * avec des autres ... cependant il faut voir qui sera.. 
     * maintenant cadre centrale en faisant une intersection des deux
     * cadrage... et recherche les cadre restant dans  rtreeMulti-echelle
     * 
     * 
     */
    public void hexaIndexUpdate (Converter newConvertiseur, ScaleInfo scale,
            ControllerII controlII
            
            ){
        
        
      }


}
