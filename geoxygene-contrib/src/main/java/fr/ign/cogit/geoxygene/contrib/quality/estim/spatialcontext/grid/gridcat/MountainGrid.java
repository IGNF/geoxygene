package fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.gridcat;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Vector;

import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.Contour;


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
public class MountainGrid extends RasterGrid {

  private IFeatureCollection<Contour> contours;

  static final String FC_CONTOURS = "CONTOURS";

  public MountainGrid(int cellule, int radius, double xB, double xH, double yB,
      double yH, double similarite, IFeatureCollection<Contour> contours) {
    super(cellule, radius, xB, xH, yB, yH, similarite);
    this.setMapCriteres(new HashMap<String, Vector<Number>>());
    this.construireCellules();
    this.setContours(contours);
    this.getData().put(FC_CONTOURS, contours);
  }

  /**
   * Choisit les crit�res qui seront utilis�s dans le clustering de cette grille
   * pour chacune des cellules. Remplit �galement la map des crit�res qui
   * associe un poids � chaque crit�re. Cette m�thode contient tous les
   * param�tres de seuils que l'on d�finit pour chaque crit�re choisi. Une fois
   * les crit�res d�finis, ils sont affect�s � chaque cellule de la grille.
   * 
   * @param critCourbes true si on utilise le crit�re de densit� des courbes de
   *          niveau.
   * @param poidsN poids du crit�re de densit� des courbes de niveau (somme des
   *          poids = 1)
   * @param seuilBasN seuil bas du crit�re de densit� des courbes de niveau
   *          (conseil : 6 * (radiusCellule))
   * @param seuilHautN seuil haut du crit�re de densit� des courbes de niveau
   *          (conseil : 15 * (radiusCellule))
   * @param critDeniv true si on utilise le crit�re de d�nivel�e.
   * @param poidsR poids du crit�re de d�nivel�e (somme des poids = 1)
   * @param seuilBasR seuil bas du crit�re de d�nivel�e (30 m)
   * @param seuilHautR seuil haut du crit�re de d�nivel�e (100 m)
   * @throws NoSuchMethodException
   * @throws ClassNotFoundException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws GothicException
   * @throws SecurityException
   * @throws IllegalArgumentException
   */
  @SuppressWarnings("boxing")
  public void setCriteres(boolean critCourbes, double poidsC, double seuilBasC,
      double seuilHautC, boolean critDeniv, double poidsD, double seuilBasD,
      double seuilHautD) throws IllegalArgumentException, SecurityException,
      InstantiationException, IllegalAccessException,
      InvocationTargetException, ClassNotFoundException, NoSuchMethodException {
    if (critCourbes) {
      // cr�ation du crit�re de densit�
      String nomCrit = "ContourLineDensityCriterion";
      // d�finition des param�tres
      // cr�ation du vecteur de param�tres
      Vector<Number> params1 = new Vector<Number>();
      params1.add(0, poidsC);
      params1.add(1, seuilBasC * getRadiusCellule());
      params1.add(2, seuilHautC * getRadiusCellule());
      // on ajoute le crit�re
      this.getMapCriteres().put(nomCrit, params1);
    }
    if (critDeniv) {
      // cr�ation du crit�re de d�nivel�e
      String nomCrit = "HeightRatioCriterion";
      // d�finition des param�tres
      // cr�ation du vecteur de param�tres
      Vector<Number> params2 = new Vector<Number>();
      params2.add(0, poidsD);
      params2.add(1, seuilBasD);
      params2.add(2, seuilHautD);
      // on ajoute le crit�re
      this.getMapCriteres().put(nomCrit, params2);
    }

    // on construit les crit�res dans chaque cellule
    for (GridCell cell : getListCellules()) {
      cell.calculerCriteres();
      cell.setClasseFinale();
    }
  }

  public void setContours(IFeatureCollection<Contour> contours) {
    this.contours = contours;
  }

  public IFeatureCollection<Contour> getContours() {
    return contours;
  }

}
