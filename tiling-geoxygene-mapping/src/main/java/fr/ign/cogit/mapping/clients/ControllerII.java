package fr.ign.cogit.mapping.clients;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.deegree.io.rtree.HyperBoundingBox;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.mapping.clients.frameworks.CadrageArea;
import fr.ign.cogit.mapping.clients.frameworks.Cadre;
import fr.ign.cogit.mapping.datastructure.Node;
import fr.ign.cogit.mapping.datastructure.RTreeIndex;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNeighbor;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNode;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaTree;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaTreeIndex;
import fr.ign.cogit.mapping.datastructure.management.HexaTreeMultiLevel;
import fr.ign.cogit.mapping.datastructure.management.ManageHexaTreeMultiLevel;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;
import fr.ign.cogit.mapping.generalindex.Record;
import fr.ign.cogit.mapping.util.HexaGoneUtil;
import fr.ign.cogit.mapping.util.ScaleInfoUtil;
import fr.ign.cogit.mapping.util.TransFormationUtil;
import fr.ign.cogit.mapping.webentity.spatial.SpatialIndex;

import org.geotools.referencing.operation.matrix.AffineTransform2D;

/*
 * Il se charge de retrouver des informations tuilés
 * en fonction des transformations appliquées au niveau
 * du clients... dans sa stratégie il ne recupère qu'une 
 * zone de données (zone de cadrage) ou simplement le cadre principal
 * est envoyé au client et ceux des autres echelles sont mis dans
 * des indexes et retirés ou ajouter au fur et à mesure que les transformations
 * sont réalisées chez le client.
 * @author Dr Tsatcha D.
 */
public class ControllerII extends Thread {

    /** Logger. */
    private final static Logger LOGGER = Logger.getLogger(ControllerII.class
            .getName());

    /*
     * Les informations à recuperer chez controleur du niveau I pour envoyer au
     * convertisseur... il devra aussi fabriquer un gestion des Hexatree basé
     * sur un cadrage des zones necessaires au points d'interets....
     */
    protected Iterator<Record<Geometry>> it;

    /*
     * @param ControllerI Une instance d'appel au controleur du niveau I
     */
    protected ControllerI controlI;
    /*
     * @param maxScale designe l'id de la transmission maximale
     */
    protected int maxScale = -1;
    /*
     * @param Convertisseur Deisgne un
     */

    protected Converter Convertisseur;

    /*
     * @param cadrageArea designe la zone de cadrage du convertisseur chargé de
     * visionner les contenus.
     */

    protected ScaleInfo scaleMax = null;
    /*
     * designe le repertoire de stockage des HexaTree indexée...
     */

    protected final String Directory = "C:/Users/dtsatcha/Desktop/IGN/java/tiling-geoxygene-mapping/src/main/java/fr/ign/cogit/mapping/clients/spatial";
    /*
     * @param cadrageArea designe le cadrage actuellement tuilé au voisinage du
     * client
     */

    protected CadrageArea cadrageArea;

    /*
     * @param mutexInfo est une variable qui permet de gerer la communication
     * entre les deux Threads celui qui tuile et celui qui envoie au client
     */
    protected boolean mutexInfo = false;

    /*
     * @param transPoint designe les informations sur des cadres à à recharger
     * ou à modifier l'entrée d'information dans la base d'index.
     */

    protected int[] transfertPoint = null;

    /*
     * Scale
     */
    protected ScaleInfo scaleInfo;
    /*
     * index HexaTree..
     */

    protected SpatialIndex hexaTreeIndex;

    /*
     * @param managerHex designe le gestionnaire des hexaTree multi-echelles...
     */

    protected ManageHexaTreeMultiLevel managerHex;

    /*
     * @param lstsortie designe la liste de geometrie en sortie.
     */

    protected volatile List<Record<Geometry>> lstsortie = null;

    /**
     * @param controlI
     *            TO DO
     * 
     *            aussi prendre le convertisseur en Paramètre plustard il faut
     *            faire un factory des convertisseur afin qu'on sache qu'elle
     *            convertisseur est sollicité... il doit prendre en paramètre :
     *            une table de la base et un point d'interet pour le cadrage
     */
    public ControllerII(Converter ConvertisseurParam) {
        super();

        Convertisseur = ConvertisseurParam;
        controlI = new ControllerI();

        cadrageArea = new CadrageArea(Convertisseur.getInterestPoint(),
                Convertisseur.getWeight(), Convertisseur.getHeight());
        // on itialise la cadrage sollicité par
        // le convertisseur...

        ConcurrentHashMap<ScaleInfo, HexaTreeIndex> multiLevelIndex = new ConcurrentHashMap<ScaleInfo, HexaTreeIndex>();
        /*
         * intialisation des paramètre du manager hexaTree
         */
        HexaTreeMultiLevel hexaTreemultilevel = new HexaTreeMultiLevel(
                multiLevelIndex);
        managerHex = new ManageHexaTreeMultiLevel(hexaTreemultilevel,
                Directory, Convertisseur, controlI.getManager());

    }

    /*
     * On retourne un iterateur des table au convertisseur en question
     */

    public ControllerII(Converter ConvertisseurParam, ScaleInfo Scaleinfo) {
        super();

        Convertisseur = ConvertisseurParam;
        controlI = new ControllerI();

        cadrageArea = new CadrageArea(Convertisseur.getInterestPoint(),
                Convertisseur.getWeight(), Convertisseur.getHeight());
        // on itialise la cadrage sollicité par
        // le convertisseur...

        ConcurrentHashMap<ScaleInfo, HexaTreeIndex> multiLevelIndex = new ConcurrentHashMap<ScaleInfo, HexaTreeIndex>();
        /*
         * intialisation des paramètre du manager hexaTree
         */
        HexaTreeMultiLevel hexaTreemultilevel = new HexaTreeMultiLevel(
                multiLevelIndex);
        managerHex = new ManageHexaTreeMultiLevel(hexaTreemultilevel,
                Directory, Convertisseur, controlI.getManager());

    }

    public List<Record<Geometry>> obtainIterFromTable() {

        ScaleInfo scale = ScaleInfoUtil.generateScale(Convertisseur.getTable());
        // System.out.println(" je suis controleur 2"+ scale.toString()+
        // Convertisseur.getTable()+ Convertisseur.getSignature());

        // appel du controleur II pour extraction d'information
        return controlI.obtainTable(Convertisseur.getTable(),
                Convertisseur.getSignature(), scale);

    }

    // permet de supprimer une echelle depuis l'indexation
    // du côté base de données
    /*
     * @param value la valeur de l'index qu'on souhaite supprimer
     */
    public void deleteScaleFromIndex(int value) {
        controlI.getManager().deleteOneLevel(value);

    }

    /*
     * Cette methode permet de demander au controleur de niveau I de recuperer
     * tous les objets qui rencontrent un point donné, et retourne ainsi un
     * cadrage de contenu.. qui seront pas la tuilé par le controleur de niveau
     * II afin de transmettre au client.
     * 
     * @param scaleInfo designe les informations de l'echelle à rechercher
     * 
     * @param focusPoint designe le point foyer ou il faut rechercher des
     * informations.
     * 
     * @param height designe la hauteur du support de visualisation
     * 
     * @param weight designe la largeur du spport de visualisation
     */
    public List<Record<Geometry>> obtainDataFromPoint(ScaleInfo scaleInfo) {

        // on recupère la transformation affine associé à cette echelle

        LinkedHashMap<Integer, List<Record<Geometry>>> allCadre = new LinkedHashMap<Integer, List<Record<Geometry>>>();

        HexaTreeIndex index = null;

        List<Record<Geometry>> lstsortie = null;

        System.out.println(" valeur convertisseur" + Convertisseur.toString());

        AffineTransform2D transFormation = TransFormationUtil
                .TransmissionFromScale(scaleInfo, controlI.getManager(),
                        Convertisseur);

        // l'ancien cadrage contenu dans le system indexé...;
        CadrageArea oldCadrageArea = new CadrageArea(managerHex
                .getConvertisseur().getInterestPoint(), managerHex
                .getConvertisseur().getWeight(), managerHex.getConvertisseur()
                .getHeight());
        // verifie que le point d'interet à été précedement indexée
        if (existInterestCadre(oldCadrageArea, Convertisseur.getInterestPoint()) != -1) {

            int currentCadre = existInterestCadre(oldCadrageArea,
                    Convertisseur.getInterestPoint());

            if (managerHex.existAnLevel(scaleInfo.getIdScale(), currentCadre)) {

                // retourne les elements indexes par le cadre 4
                index = managerHex.useScaleCadre(scaleInfo, currentCadre);

                lstsortie = findScreenCadreIndex(index);

                // *********/ mis à jour de tous les indexes ...de toutes
                // echelles...
            }

        }

        return lstsortie;
    }

    /*
     * Cette methode permet de mettre à jour le multi-index Hexatree en fonction
     * des cadres de rencontres...et pour toutes les autres echelles
     * 
     * @param colapsePoint designe l'ensemble des cadres à rechercher...
     */

    public ScaleInfo getScaleInfo() {
        return scaleInfo;
    }

    public void setScaleInfo(ScaleInfo scaleInfo) {
        this.scaleInfo = scaleInfo;
    }

    public List<Record<Geometry>> getLstsortie() {
        return lstsortie;
    }

    public void setLstsortie(List<Record<Geometry>> lstsortie) {
        this.lstsortie = lstsortie;
    }

    public void updateHexaTreeIndex(int[] colapsePoint, HexaTreeIndex index) {

        for (ScaleInfo scale : controlI.getManager().getRtreemultilevel()
                .getMultiLevelIndex().keySet()) {
            // on fabrique le rapoort de transmission

            AffineTransform2D transFormation = TransFormationUtil
                    .TransmissionFromScale(scale, controlI.getManager(),
                            Convertisseur);

            tuilingAllINcontraint(Convertisseur, scale, transFormation,
                    colapsePoint, index);

        }
        managerHex.saveLevelsInFile();

    }

    /*
     * cette methode permet de renseigner le cadrage dans lequel appartient un
     * point d'interets...;
     * 
     * @param cadrage designe le cadrage qui est supposer contenir l'element
     * 
     * @param pt designe le point d'interet
     * 
     * @return la valeur du cadre qui contient le cadre sinon retourne -1
     */

    public int existInterestCadre(CadrageArea cadrage, Point pt) {

        if (cadrage != null) {
            for (int i = 0; i < 9; i++) {

                if (cadrage.getCadrageArea().get(i).buildPolygonOfCadre()
                        .contains(pt)) {
                    return i;
                }

            }
        }
        return -1;
    }

    /*
     * Cette methode permet de realiser un tuilage des contenus extraites en
     * tenant compte de la zone de cadrage de l'utilsateur...
     */

    public HexaTree tilingGeom(ScaleInfo scaleInfo) {

        List<Record<Geometry>> dataToTile = obtainDataFromPoint(scaleInfo);

        if (Convertisseur.getHexatree() != null) {
            for (Record<Geometry> rec : dataToTile) {
                // id n'est pas encore interessant...

                Convertisseur.getHexatree().insert(rec, scaleInfo, 0);
            }

        }

        return Convertisseur.getHexatree();
    }

    /*
     * Cette methode permet de fabriquer l'hexatree de tous les informations
     * correspondant au differentes echelles intégrées dans la base de données
     */

    public HexaTree tilingGeom() {
        LinkedHashMap<ScaleInfo, CadrageArea> content = new LinkedHashMap<ScaleInfo, CadrageArea>();

        for (ScaleInfo s : controlI.getManager().getRtreemultilevel()
                .getMultiLevelIndex().keySet()) {
            List<Record<Geometry>> dataToTile = obtainDataFromPoint(s);

            for (Record<Geometry> rec : dataToTile) {
                // id n'est pas encore interessant...
                Convertisseur.getHexatree().insert(rec, s, 0);
            }
        }

        return Convertisseur.getHexatree();
    }

    /*
     * 
     */

    public List<Record<Geometry>> searchGeoFromCadrage(ScaleInfo scaleinfo,
            CadrageArea cadrage) {
        List<Record<Geometry>> selectContent = new ArrayList<Record<Geometry>>();

        hexaTreeIndex.open();
        Iterator<Record<Geometry>> it = hexaTreeIndex.iterator();
        if (it != null) {
            while (it.hasNext()) {
                Record<Geometry> content = it.next();
                fr.ign.cogit.mapping.datastructure.Node node = content.getKey();
                // on verifie que le noeud contient bien les
                // information associé à l'entrée.
                Geometry geom = content.getValue();
                // on fabrique une geometrie geoxygene

                if (cadrage.buildPolygonOfCadrageArea().contains(geom)) {
                    selectContent.add(content);
                    hexaTreeIndex.add(content);
                }

            }
        } else {
            ControllerII.LOGGER.error("les information sur"
                    + scaleinfo.toString()
                    + " n'a été chargé dans le hexaIndex");
        }

        return selectContent;
    }

    /*
     * Cette methode permet de rechercher les elements correspondant au cadrage
     * dans l'indexHexaTree...
     */

    public List<Record<Geometry>> searchGeoFromCadre(ScaleInfo scaleinfo,
            Cadre cadre) {
        List<Record<Geometry>> selectContent = new ArrayList<Record<Geometry>>();

        hexaTreeIndex.open();
        Iterator<Record<Geometry>> it = hexaTreeIndex.iterator();
        if (it != null) {
            while (it.hasNext()) {
                Record<Geometry> content = it.next();
                fr.ign.cogit.mapping.datastructure.Node node = content.getKey();
                // on verifie que le noeud contient bien les
                // information associé à l'entrée.
                Geometry geom = content.getValue();
                // on fabrique une geometrie geoxygene

                if (cadre.buildPolygonOfCadre().contains(geom)) {
                    selectContent.add(content);
                    hexaTreeIndex.add(content);
                }

            }
        } else {
            ControllerII.LOGGER.error("les information sur"
                    + scaleinfo.toString()
                    + " n'a été chargé dans le hexaIndex");
        }

        return selectContent;
    }

    public List<Record<Geometry>> tileGeoHexagone(ScaleInfo scaleinfo,
            HexaNode hex) {
        List<Record<Geometry>> selectContent = new ArrayList<Record<Geometry>>();

        hexaTreeIndex.open();
        Iterator<Record<Geometry>> it = hexaTreeIndex.iterator();
        if (it != null) {
            while (it.hasNext()) {
                Record<Geometry> content = it.next();
                fr.ign.cogit.mapping.datastructure.Node node = content.getKey();
                // on verifie que le noeud contient bien les
                // information associé à l'entrée.
                Geometry geom = content.getValue();
                // on fabrique une geometrie geoxygene
                HexaNeighbor neighborSister = new HexaNeighbor(hex);
                // neighborSister.generatePossibleSister();
                Polygon Hexpoly = HexaGoneUtil.builHexagone(Arrays
                        .asList(neighborSister.getSisters()));
                if (Hexpoly.contains(geom)) {
                    selectContent.add(content);
                    hexaTreeIndex.add(content);
                }

            }
        } else {
            ControllerII.LOGGER.error("les information sur"
                    + scaleinfo.toString()
                    + " n'a été chargé dans le hexaIndex");
        }

        return selectContent;
    }

    /*
     * Cette methode permet de rechercher les elements correspondant au cadrage
     * dans l'indexHexaTree...
     */

    public List<Record<Geometry>> findScreenCadreIndex(HexaTreeIndex index) {
        List<Record<Geometry>> selectContent = new ArrayList<Record<Geometry>>();
        index.open();
        Iterator<Record<Geometry>> it = index.iterator();

        while (it.hasNext()) {
            Record<Geometry> content = it.next();

            Node node = content.getKey();

            selectContent.add(content);

        }

        return selectContent;

    }

    /*
     * permet de retourner le cadre de visualisation une fois l'indexation
     * hexatree a été mise à jour...
     * 
     * @param scaleInfo designe l'echelle prise en entrée à partir des
     * paramètres du convertisseur, on pourra verifier les differents composants
     * noeuds à mettre à jour ou recalculer..
     */

    public List<Record<Geometry>> updateHexaTreeIndex(ScaleInfo scaleInfo,
            Converter newConverter) {

        CadrageArea NewcadrageArea = new CadrageArea(
                Convertisseur.getInterestPoint(), Convertisseur.getHeight(),
                Convertisseur.getWeight());

        // on recupère les informations sur l'echelle deja structurées
        // par le manager...

        return null;

    }

    /*
     * Le but de cette methode est de permettre d'aller chercher uniquement les
     * cadres qui n'ont pas encore été chargé dans l'index spatial du côté
     * client... Pour cela on prend les informations de l'echelle courante et
     * part rechercher les cadres n'ont renseignées par l'index...
     */

    public int[] getcolapseInfo(CadrageArea oldCadrageArea,
            CadrageArea newCadrageArea) {

        boolean testall = false;

        newCadrageArea.collapseCadrage(oldCadrageArea);

        int[] transfert = newCadrageArea.getIndicesTranfertCadre();

        return transfert;

    }
    
    
    /*cette methode permet de verifier que les cadrage
     * son identique et dans ce cas aucune modification
     * de l'indexation hexaTree n'est envisagée..;
     * @param  oldCadrageArea
     * designe le cadrage contenu dans l'index 
     * @param newCadrageArea
     * designe le nouveau cadrage avec lequel arrive
     * le client... 
     */
    
    public boolean sameCadrage(CadrageArea oldCadrageArea,
         CadrageArea newCadrageArea){
        
        if(oldCadrageArea.buildPolygonOfCadrageArea().equalsExact
                (newCadrageArea.buildPolygonOfCadrageArea())){
            return true;
        }
        return false;
    }

    /*
     * Tuiler tous les cadre d'une echelle donnéer
     */

    public void tuilingAllINcontraint(Converter Newconvertisseur,
            ScaleInfo scale, AffineTransform2D transFormation, int[] contraint,
            HexaTreeIndex index) {

        CadrageArea cadrageAreaImg = cadrageArea
                .imageCadrageArea(transFormation);

        LinkedHashMap<Integer, List<Record<Geometry>>> allCadre = new LinkedHashMap<Integer, List<Record<Geometry>>>();

        cadrageAreaImg = cadrageArea.imageCadrageArea(transFormation);
        // on parcourt tous les cadres du cadrage afin de determiner
        // afin de trouver les cellules à tuiler
        for (int k : cadrageAreaImg.getCadrageArea().keySet()) {

            Cadre cadre = cadrageAreaImg.getCadrageArea().get(k);

            List<Record<Geometry>> listContent = controlI.searchGeoFromCadre(
                    scale, cadre);
            allCadre.put(k, listContent);
        }

        // on effectue le tuilage en fonction des cadre
        // dans le repertoire correspondant
        for (int k : allCadre.keySet()) {

            if (existCadre(contraint, k)) {

                if (!managerHex.existAnLevel(scale.getIdScale(), k)) {

                    managerHex.createOneLevel(scale.getIdScale(),
                            scale.getIdScale(), k);
                    index = managerHex.useScaleCadre(scale, k);
                } else {

                    index = managerHex.useScaleCadre(scale, k);

                }
                // on tuile tout le cadrage associée...

                index.open();
                index.setConvertisseur(Newconvertisseur);
                // on tuille le cadrage necessaire

                for (Record<Geometry> rec : allCadre.get(k)) {

                    index.add(rec);

                }
                index.close();
            }
            /*
             * Une partie de cette information est indexée /.../.../
             */
            // on retourne le cadre principal necessaire
            // à la visualisation..
            // on reintialise
        }

    }

    /*
     * 
     */

    public boolean existCadre(int[] contraint, int cadre) {

        if (contraint != null) {
            if (cadre < contraint.length) {
                // le tuilage est possible...
                if (contraint[cadre] == -1) {
                    return false;
                }
            }
        }
        return false;
    }

    /*
     * Cet inner classe permet de gerer l'accès concurrent entre le fournisseur
     * de contenu et la classe charger de tuiler la base de données... HexaTree
     * ou des mises à jour...
     */

    public class TransfertCurrentData implements
            Callable<List<Record<Geometry>>> {

        /**
         * @param scaleInfo
         */
        public TransfertCurrentData(ScaleInfo scaleInfoParam) {
            super();
            this.scaleInfo = scaleInfoParam;
        }

        protected List<Record<Geometry>> mysortie;

        public List<Record<Geometry>> getMysortie() {
            return mysortie;
        }

        public void setMysortie(List<Record<Geometry>> mysortie) {
            this.mysortie = mysortie;
        }

        protected ScaleInfo scaleInfo;

        public List<Record<Geometry>> obtainDataFromPoint(ScaleInfo scaleInfo) {

            // on recupère la transformation affine associé à cette echelle

            LinkedHashMap<Integer, List<Record<Geometry>>> allCadre = new LinkedHashMap<Integer, List<Record<Geometry>>>();

            HexaTreeIndex index = null;

            List<Record<Geometry>> lstsortie = null;

            System.out.println(" valeur convertisseur"
                    + Convertisseur.toString());

            AffineTransform2D transFormation = TransFormationUtil
                    .TransmissionFromScale(scaleInfo, controlI.getManager(),
                            Convertisseur);

            // l'ancien cadrage contenu dans le system indexé...;
            CadrageArea oldCadrageArea = new CadrageArea(managerHex
                    .getConvertisseur().getInterestPoint(), managerHex
                    .getConvertisseur().getWeight(), managerHex
                    .getConvertisseur().getHeight());
            // verifie que le point d'interet à été précedement indexée
            if (existInterestCadre(oldCadrageArea,
                    Convertisseur.getInterestPoint()) != -1) {

                int currentCadre = existInterestCadre(oldCadrageArea,
                        Convertisseur.getInterestPoint());

                if (managerHex.existAnLevel(scaleInfo.getIdScale(),
                        currentCadre)) {

                    // retourne les elements indexes par le cadre 4
                    index = managerHex.useScaleCadre(scaleInfo, currentCadre);
                    mutexInfo = true;
                    return findScreenCadreIndex(index);

                }

            }else{
                 
                CadrageArea newCadrageArea = new CadrageArea(
                        Convertisseur.getInterestPoint(),
                        Convertisseur.getWeight(), Convertisseur.getHeight());
                // on envoie le cadre central et on restera tuiler les autres...
                return controlI.searchGeoFromCadre(scaleInfo, newCadrageArea
                        .getCadrageArea().get(4));
                // le demande au controleur du niveau deux...
            }

            return lstsortie;
        }

        @Override
        public List<Record<Geometry>> call() throws Exception {
            // TODO Auto-generated method stub
            return obtainDataFromPoint(scaleInfo);
        }

    }

    /*
     * Cette classe sera charge de mettre jour en les contenu de la base d'index
     * Hexatree...
     */

    public class upadateIndexHexaTree implements Callable<Integer> {

        protected int[] colapsePoint;
        protected HexaTreeIndex index;
        protected boolean nochange;

        /**
         * @param colapsePoint
         * @param index
         */
        public upadateIndexHexaTree(int[] colapsePointParam,
                HexaTreeIndex indexParam) {
            super();
            this.colapsePoint = colapsePointParam;
            this.index = indexParam;
        }
        
        public upadateIndexHexaTree(boolean nochangeParam, int[] colapsePointParam,
                HexaTreeIndex indexParam) {
            super();
            this.colapsePoint = colapsePointParam;
            this.index = indexParam;
            this.nochange=nochangeParam; 
        }

        public int updateHexaTreeIndex() {
            
             if(nochange==true){
            // on devra mettre à  jour le système de fichier en tenant compte des
            // cadres de chevauchement..colapsePoint
            for (ScaleInfo scale : controlI.getManager().getRtreemultilevel()
                    .getMultiLevelIndex().keySet()) {
                // on fabrique le rapoort de transmission

                AffineTransform2D transFormation = TransFormationUtil
                        .TransmissionFromScale(scale, controlI.getManager(),
                                Convertisseur);

                tuilingAllINcontraint(Convertisseur, scale, transFormation,
                        colapsePoint, index);

            }
            managerHex.saveLevelsInFile();
            }
            return 0;

        }

        @Override
        public Integer call() throws Exception {
            // TODO Auto-generated method stub
            return updateHexaTreeIndex();
        }

    }

    public void ApplyDaeamon(ScaleInfo scaleInfo) {
        // TODO Auto-generated method stub

        HexaTreeIndex index = null;

        TransfertCurrentData clientData = new TransfertCurrentData(scaleInfo);
        CadrageArea oldCadrageArea = new CadrageArea(managerHex
                .getConvertisseur().getInterestPoint(), managerHex
                .getConvertisseur().getWeight(), managerHex.getConvertisseur()
                .getHeight());

        transfertPoint = getcolapseInfo(oldCadrageArea, cadrageArea);
        boolean meetTogether =  sameCadrage(oldCadrageArea, cadrageArea);
        upadateIndexHexaTree udapteData = new upadateIndexHexaTree(meetTogether,
                transfertPoint, index);

        FutureTask<List<Record<Geometry>>> futureTask1 = new FutureTask<List<Record<Geometry>>>(
                clientData);

        FutureTask<Integer> futureTask2 = new FutureTask<Integer>(udapteData);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(futureTask1);
        executor.execute(futureTask2);

        try {
            if (futureTask1.isDone() && futureTask2.isDone()) {
                System.out.println("Done");
                // shut down executor service
                executor.shutdown();
            }

            if (!futureTask1.isDone()) {
                // wait indefinitely for future task to complete
                setLstsortie(futureTask1.get());
            }

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public boolean isMutexInfo() {
        return mutexInfo;
    }

    public void setMutexInfo(boolean mutexInfo) {
        this.mutexInfo = mutexInfo;
    }

}
