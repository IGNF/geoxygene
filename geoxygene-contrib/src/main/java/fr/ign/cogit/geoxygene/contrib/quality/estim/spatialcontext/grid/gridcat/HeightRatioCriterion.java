package fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.gridcat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.Contour;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.MathUtil;

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
public class HeightRatioCriterion extends CellCriterion {

  public HeightRatioCriterion(GridCell cell, double poids, Number seuilBas,
      Number seuilHaut) {
    super(cell, poids, seuilBas, seuilHaut);
  }

  @Override
  public void setCategory() {
    // cas d'un crit�re entier
    int valeurInt = getValeur().intValue();
    if (valeurInt < getSeuilBas().intValue()) {
      setClassif(1);
    } else if (valeurInt > getSeuilHaut().intValue()) {
      setClassif(3);
    } else {
      setClassif(2);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setValue() {
    DirectPosition centre = new DirectPosition(getCellule().getxCentre(),
        getCellule().getyCentre());
    GM_Polygon circle = GeometryFactory.buildCircle(centre, getCellule()
        .getGrille().getRadiusCellule(), 24);
    FT_FeatureCollection<Contour> data = (FT_FeatureCollection<Contour>) this
        .getCellule().getGrille().getData().get(MountainGrid.FC_CONTOURS);
    Collection<Contour> cellContours = data.select(circle);
    if (cellContours.size() == 0) {
      this.setValeur(new Integer(0));
    } else {
      double heightMin = Double.MAX_VALUE;
      double heightMax = 0;
      List<Double> listHeight = new ArrayList<Double>();
      for (Contour contour : cellContours) {
        listHeight.add(new Double(contour.getValeur()));
        double height = new Double(contour.getValeur());
        if (height < heightMin)
          heightMin = height;
        if (height > heightMax)
          heightMax = height;
      }
      double heightMean = MathUtil.moyenne(listHeight);

      double value = 2 * (heightMax) - heightMean - heightMin;
      // double value2 = (2*(heightMax))/(heightMean+heightMin);

      this.setValeur(value);

      listHeight.clear();
    }
  }
}
