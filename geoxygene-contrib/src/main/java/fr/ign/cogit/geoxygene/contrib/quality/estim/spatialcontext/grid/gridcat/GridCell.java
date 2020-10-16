package fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.gridcat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

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
public class GridCell {
    private final static Logger logger = LogManager.getLogger(GridCell.class.getName());
    private RasterGrid grille;// la grille dont fait partie la cellule
    private int ligne, colonne;// coordonnées de la cellule dans la grille
    private double xCentre, yCentre;// les coord du centre de la cellule

    // classe de la cellule
    private double classeFinale;
    private HashSet<CellCriterion> criteres;

    private static final String nomPack = "fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.gridcat.";

    public RasterGrid getGrille() {
        return grille;
    }

    public void setGrille(RasterGrid grille) {
        this.grille = grille;
    }

    public int getLigne() {
        return ligne;
    }

    public void setLigne(int ligne) {
        this.ligne = ligne;
    }

    public int getColonne() {
        return colonne;
    }

    public void setColonne(int colonne) {
        this.colonne = colonne;
    }

    public double getxCentre() {
        return xCentre;
    }

    public void setxCentre(double xCentre) {
        this.xCentre = xCentre;
    }

    public double getyCentre() {
        return yCentre;
    }

    public void setyCentre(double yCentre) {
        this.yCentre = yCentre;
    }

    public double getClasseFinale() {
        return classeFinale;
    }

    public void setClasseFinale(double classeFinale) {
        this.classeFinale = classeFinale;
    }

    public HashSet<CellCriterion> getCriteres() {
        return criteres;
    }

    public void setCriteres(HashSet<CellCriterion> criteres) {
        this.criteres = criteres;
    }

    GridCell(RasterGrid grille, int l, int c) {
        this.grille = grille;
        ligne = l;
        colonne = c;
        xCentre = grille.getCoordGrille().getX() + (c - 1) * grille.getTailleCellule() + grille.getTailleCellule() / 2;
        yCentre = grille.getCoordGrille().getY() - (l) * grille.getTailleCellule() + grille.getTailleCellule() / 2;
        criteres = new HashSet<CellCriterion>();
    }

    @Override
    public boolean equals(Object obj) {
        if (ligne != ((GridCell) obj).ligne) {
            return false;
        }
        if (colonne != ((GridCell) obj).colonne) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ligne : " + ligne + "   colonne : " + colonne;
    }

    void afficheCellule(@SuppressWarnings("unused") int classe) {
        // TODO
    }

    GM_Polygon construireGeom() {
        DirectPositionList points = new DirectPositionList();
        points.add(new DirectPosition(xCentre - grille.getTailleCellule() / 2, yCentre - grille.getTailleCellule() / 2));
        points.add(new DirectPosition(xCentre + grille.getTailleCellule() / 2, yCentre - grille.getTailleCellule() / 2));
        points.add(new DirectPosition(xCentre + grille.getTailleCellule() / 2, yCentre + grille.getTailleCellule() / 2));
        points.add(new DirectPosition(xCentre - grille.getTailleCellule() / 2, yCentre + grille.getTailleCellule() / 2));
        points.add(new DirectPosition(xCentre - grille.getTailleCellule() / 2, yCentre - grille.getTailleCellule() / 2));
        GM_Polygon geom = new GM_Polygon(new GM_LineString(points));
        return geom;
    }

    /**
     * Crée les critères contenus dans grille.mapCriteres pour cette cellule et
     * les stocke dans le set criteres. La valeur du critère est également
     * calculée ainsi que sa classification.
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * 
     */
    @SuppressWarnings("unchecked")
    void calculerCriteres() throws IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException, ClassNotFoundException, SecurityException, NoSuchMethodException {
        // on calcule les critères en fonction de la map de critères de la
        // grille
        Iterator iter = grille.getMapCriteres().keySet().iterator();
        while (iter.hasNext()) {
            String nomCrit = (String) iter.next();
            if (logger.isDebugEnabled()) {
                logger.debug("critere : " + nomCrit);
            }
            logger.debug("Nom de la classe = " + nomPack + nomCrit);
            Class classe = Class.forName(nomPack + nomCrit);
            Constructor<? extends CellCriterion> constr = classe.getConstructor(GridCell.class, double.class,
                    Number.class, Number.class);
            Vector params = grille.getMapCriteres().get(nomCrit);
            // on construit le critère
            Double poids = (Double) params.get(0);
            Object seuilBas = params.get(1);
            Object seuilHaut = params.get(2);
            CellCriterion critere = constr.newInstance(this, poids, seuilBas, seuilHaut);
            // on calcule sa valeur
            critere.setValue();
            // on calcule sa classification
            critere.setCategory();
            if (logger.isDebugEnabled()) {
                logger.debug(this.toString() + " : " + critere.getClassif());
            }
            this.criteres.add(critere);
        }
    }// calculerCriteres()

    /**
     * Détermine la classe finale de la cellule en agrégeant tous les critères.
     * La classe d'un critère pouvant prendre 3 valeurs, la classe finale peut
     * prendre nbCriteres*3-1 valeurs comprises entre 1 et 3.
     * 
     */
    void setClasseFinale() {
        // on parcourt le set des crit�res
        this.classeFinale = 0.0;
        Iterator<CellCriterion> iter = criteres.iterator();
        while (iter.hasNext()) {
            CellCriterion critere = iter.next();
            // on ajoute au total la classe de ce critere fois son poids
            classeFinale += critere.getPoids() * critere.getClassif();
        }
        // le total est forc�ment compris entre 1 et 3
    }// setClasseFinale()

    /**
     * R�cup�re les quatre (au mieux) voisines d'une cellule
     * 
     */
    HashSet<GridCell> recupererCellulesVoisines() {
        HashSet<GridCell> voisins = new HashSet<GridCell>();
        // on cherche le voisin du haut
        if (this.ligne > 1) {
            voisins.add(grille.getListCellules().get(grille.getNbColonne() * (ligne - 2) + colonne - 1));
        }
        // on cherche le voisin du bas
        if (this.ligne < grille.getNbLigne()) {
            voisins.add(grille.getListCellules().get(grille.getNbColonne() * (ligne) + colonne - 1));
        }
        // on cherche le voisin de gauche
        if (this.colonne > 1) {
            voisins.add(grille.getListCellules().get(grille.getNbColonne() * (ligne - 1) + colonne - 1 - 1));
        }
        // on cherche le voisin de droite
        if (this.colonne < grille.getNbColonne()) {
            voisins.add(grille.getListCellules().get(grille.getNbColonne() * (ligne - 1) + colonne + 1 - 1));
        }
        return voisins;
    }// recupererCellulesVoisines()

    public CellCriterion getCritere(String critere) {
        Iterator<CellCriterion> iter = this.criteres.iterator();
        while (iter.hasNext()) {
            CellCriterion critCourant = iter.next();
            if (critCourant.getNom().equals(critere)) {
                return critCourant;
            }
        }// while, boucle sur criteres de this

        return null;
    }// getCritere(String critere)
}
