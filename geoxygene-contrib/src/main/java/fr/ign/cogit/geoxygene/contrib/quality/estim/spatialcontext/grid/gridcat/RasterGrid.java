package fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.gridcat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Geometry;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author JFGirres
 */
public abstract class RasterGrid {
    @SuppressWarnings("unused")
    private final static Logger logger = Logger.getLogger(RasterGrid.class.getName());
    private int tailleCellule;// la taille de chaque cellule
    private int radiusCellule;
    private double xBas, xHaut, yBas, yHaut;// coordonnées de l'étendue de la
                                            // zone
    private int nbLigne, nbColonne;// le nombre de lignes et de colonnes de la
                                   // grille
    private DirectPosition coordGrille;// coordonnée du point en haut à gauche
                                       // de
                                       // la grille
    private ArrayList<GridCell> listCellules;// les cellules de la grille
                                             // classées
                                             // par ligne
    // en commençant par la cellule en haut à gauche.
    private double seuilSimilarite;// en-dessous de cette valeur, deux classes
    // finales de cellules sont considérées comme similaires

    // données géographiques utilisées
    private HashMap<String, IFeatureCollection<? extends IFeature>> data;

    // définition des critères de clustering
    private HashMap<String, Vector<Number>> mapCriteres;// cette map associe
                                                        // un
                                                        // nom de critère un
                                                        // vecteur

    // contenant tous les param�tres pour ce crit�re : le poids (Double), le
    // type
    // de donn�es (String), le seuil Haut (Double ou Integer) et le seuil Bas.

    // ************************
    // GETTERS AND SETTERS
    // ************************
    public int getTailleCellule() {
        return tailleCellule;
    }

    public void setTailleCellule(int tailleCellule) {
        this.tailleCellule = tailleCellule;
    }

    public int getRadiusCellule() {
        return radiusCellule;
    }

    public void setRadiusCellule(int radiusCellule) {
        this.radiusCellule = radiusCellule;
    }

    public double getxBas() {
        return xBas;
    }

    public void setxBas(double xBas) {
        this.xBas = xBas;
    }

    public double getxHaut() {
        return xHaut;
    }

    public void setxHaut(double xHaut) {
        this.xHaut = xHaut;
    }

    public double getyBas() {
        return yBas;
    }

    public void setyBas(double yBas) {
        this.yBas = yBas;
    }

    public double getyHaut() {
        return yHaut;
    }

    public void setyHaut(double yHaut) {
        this.yHaut = yHaut;
    }

    public int getNbLigne() {
        return nbLigne;
    }

    public void setNbLigne(int nbLigne) {
        this.nbLigne = nbLigne;
    }

    public int getNbColonne() {
        return nbColonne;
    }

    public void setNbColonne(int nbColonne) {
        this.nbColonne = nbColonne;
    }

    public DirectPosition getCoordGrille() {
        return coordGrille;
    }

    public void setCoordGrille(DirectPosition coordGrille) {
        this.coordGrille = coordGrille;
    }

    public ArrayList<GridCell> getListCellules() {
        return listCellules;
    }

    public void setListCellules(ArrayList<GridCell> listCellules) {
        this.listCellules = listCellules;
    }

    public HashMap<String, Vector<Number>> getMapCriteres() {
        return mapCriteres;
    }

    public void setMapCriteres(HashMap<String, Vector<Number>> mapCriteres) {
        this.mapCriteres = mapCriteres;
    }

    public double getSeuilSimilarite() {
        return seuilSimilarite;
    }

    public void setSeuilSimilarite(double seuilSimilarite) {
        this.seuilSimilarite = seuilSimilarite;
    }

    public void setData(HashMap<String, IFeatureCollection<? extends IFeature>> data) {
        this.data = data;
    }

    public HashMap<String, IFeatureCollection<? extends IFeature>> getData() {
        return data;
    }

    /**
     * Constructeur simple avec les param�tres de base.
     * @throws Exception
     * 
     */
    public RasterGrid(int cellule, int radius, double xB, double xH, double yB, double yH, double similarite) {
        this.tailleCellule = cellule;
        this.radiusCellule = radius;
        this.xBas = xB;
        this.xHaut = xH;
        this.yBas = yB;
        this.yHaut = yH;
        this.seuilSimilarite = similarite;
        data = new HashMap<String, IFeatureCollection<? extends IFeature>>();
        setTailleGrille();
        setCoordGrille();
    }

    private void setTailleGrille() {
        // this.nbLigne =
        // 1+(Double.valueOf(yHaut-yBas)).intValue()%tailleCellule;
        // this.nbColonne =
        // 1+(Double.valueOf(xHaut-xBas)).intValue()%tailleCellule;

        this.nbLigne = 1 + (int) Math.round((yHaut - yBas) / tailleCellule);
        this.nbColonne = 1 + (int) Math.round((xHaut - xBas) / tailleCellule);

    }

    private void setCoordGrille() {
        // on calcule l'écart entre la longueur de la grille et celle de la zone
        double ecartLong = nbColonne * tailleCellule - (xHaut - xBas);
        double ecartHauteur = nbLigne * tailleCellule - (yHaut - yBas);
        double xCoord = xBas - ecartLong / 2;
        double yCoord = yHaut + ecartHauteur / 2;
        coordGrille = new DirectPosition(xCoord, yCoord);
    }

    /**
     * Construction des cellules de la grille avec ses crit�res
     * @throws Exception
     */
    protected void construireCellules() {
        this.listCellules = new ArrayList<GridCell>();
        // on fait une boucle sur toutes les lignes et les colonnes
        for (int i = 1; i <= nbLigne * nbColonne; i++) {
            int colonne = i % nbColonne;
            if (colonne == 0) {
                colonne = nbColonne;
            }
            int ligne = (i - colonne) / nbColonne + 1;
            GridCell cellule = new GridCell(this, ligne, colonne);
            this.listCellules.add(cellule);
        }
    }

    /**
     * Regroupe les cellules en clusters de cellules de même classe. La méthode
     * renvoie un set de clusters qui sont des sets de cellules. Classifie selon
     * le critère passé en entrée : si c'est "total", la classif se fait selon
     * l'agrégation des critères.
     * 
     */
    public HashMap<HashSet<GridCell>, Double> creerClusters(String critere) {
        HashMap<HashSet<GridCell>, Double> clusters = new HashMap<HashSet<GridCell>, Double>();

        // on met les cellules dans une pile
        Stack<GridCell> pile = new Stack<GridCell>();
        pile.addAll(this.listCellules);
        // on parcourt la pile des cellules
        while (!pile.isEmpty()) {
            // System.out.println(pile.size());
            GridCell cellule = pile.peek();
            // on forme un cluster à partir de cette cellule
            CellClusterResult result = this.clusterAutourCellule(critere, cellule);
            // on l'ajoute aux clusters
            clusters.put(result.cluster, new Double(result.classValue));
            // on enlève ses �l�ments de la pile
            pile.removeAll(result.cluster);
        }

        return clusters;
    }

    /**
     * Pour une cellule de la grille, crée un cluster de cellules de même
     * classe. Classifie selon le critère passé en entrée : si c'est "total", la
     * classif se fait selon l'agrégation des critères.
     * 
     * @return Vector[2] : un set de cellules de la grille en 0 et la classif en
     *         1
     */
    private CellClusterResult clusterAutourCellule(String critere, GridCell cell) {
        HashSet<GridCell> cluster = new HashSet<GridCell>();

        // on commence par ajouter cell au cluster
        cluster.add(cell);

        // on détermine la classe de cell selon critere
        double classe = cell.getClasseFinale();
        // System.out.println(classe);
        if (!critere.equals("total")) {
            CellCriterion crit = cell.getCritere(critere);
            classe = crit.getClassif();
        }

        // on met les voisins de cell dans la queue
        Stack<GridCell> pile = new Stack<GridCell>();
        pile.addAll(cell.recupererCellulesVoisines());
        // tant qu'il y a des voisins � tester
        while (!pile.isEmpty()) {
            GridCell voisine = pile.pop();
            // on d�termine la classe de voisine
            double classeVoisine = voisine.getClasseFinale();
            if (!critere.equals("total")) {
                CellCriterion crit = voisine.getCritere(critere);
                classeVoisine = crit.getClassif();
            }// if(!critere.equals("total"))

            // si les classes ne sont pas identiques, on passe au suivant
            // if (logger.isdebugEnabled()) {
            // logger.debug(classe);
            // logger.debug(classeVoisine);
            // logger.debug("difference : "+Math.abs(classe-classeVoisine));
            // }
            if (Math.abs(classe - classeVoisine) > getSeuilSimilarite()) {
                continue;
            }
            // ici, on a une voisine de m�me classe
            // on affecte la classe moyenne aux deux cellules
            voisine.setClasseFinale((classe + classeVoisine) / 2.0);
            cell.setClasseFinale((classe + classeVoisine) / 2.0);
            // on l'ajoute au cluster
            cluster.add(voisine);
            // on ajoute les voisines de voisine � la queue
            HashSet<GridCell> voisins = voisine.recupererCellulesVoisines();
            voisins.removeAll(cluster);
            pile.addAll(voisins);
        }// while, tant que la queue n'est pas vide

        return new CellClusterResult(classe, cluster);
    }// clusterAutourCellule(String critere,CelluleGrille cell)

    public void afficheCluster(HashSet<GridCell> cluster, int classe) {
        Iterator<GridCell> iter = cluster.iterator();
        while (iter.hasNext()) {
            GridCell cellule = iter.next();
            cellule.afficheCellule(classe);
        }
    }// afficheCluster(HashSet cluster)

    public GM_Polygon creerGeomCluster(HashSet<GridCell> cluster) throws Exception {
        // on commence par r�cup�rer une cellule du cluster
        GridCell cell = cluster.iterator().next();
        HashSet<GridCell> voisins = new HashSet<GridCell>();
        voisins.addAll(cluster);
        GM_Polygon geomCluster = cell.construireGeom();
        voisins.retainAll(cell.recupererCellulesVoisines());
        HashSet<GridCell> traites = new HashSet<GridCell>();
        traites.add(cell);
        // tant qu'il y a des voisins, on les agr�ge
        while (!voisins.isEmpty()) {
            HashSet<GridCell> copie = new HashSet<GridCell>();
            copie.addAll(voisins);
            Iterator<GridCell> iter = copie.iterator();
            while (iter.hasNext()) {
                GridCell cellule = iter.next();
                GM_Polygon geomCell = cellule.construireGeom();
                geomCluster = (GM_Polygon) geomCluster.union(geomCell);

                // on enl�ve cellule de voisins
                voisins.remove(cellule);
                // on la met dans traites
                traites.add(cellule);
                // on cherche les nouveaux voisins
                HashSet<GridCell> nouveauxVoisins = cellule.recupererCellulesVoisines();
                nouveauxVoisins.retainAll(cluster);
                nouveauxVoisins.removeAll(traites);
                voisins.addAll(nouveauxVoisins);
            }// while, boucle sur la copie de voisins � ce tour de boucle
        }// tant qu'il y a des voisins

        // on fait une fermeture des 2/3 de la taille de la cellule pour
        // supprimer les trous de la taille d'une cellule
        Geometry fermee = JtsAlgorithms.fermeture(JtsGeOxygene.makeJtsGeom(geomCluster), 0.66 * tailleCellule, 4);
        geomCluster = (GM_Polygon) JtsGeOxygene.makeGeOxygeneGeom(fermee);

        return geomCluster;
    }

    class CellClusterResult {
        public double classValue;
        public HashSet<GridCell> cluster;

        public CellClusterResult(double classValue, HashSet<GridCell> cluster) {
            super();
            this.classValue = classValue;
            this.cluster = cluster;
        }
    }

}// class GrilleRaster
