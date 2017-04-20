package fr.ign.cogit.geoxygene.sig3d.model.citygml.relief;


import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.relief.TINRelief;
import org.citygml4j.model.gml.geometry.primitives.Tin;
import org.citygml4j.model.gml.geometry.primitives.TriangulatedSurface;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertCityGMLtoGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_TriangulatedSurface;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_TINRelief extends CG_AbstractReliefComponent {

  private GM_TriangulatedSurface tin = null;

  public GM_TriangulatedSurface getTin() {
    return tin;
  }

  public CG_TINRelief(TINRelief rC) {
    super(rC);

    if (rC.isSetTin()) {

      if (rC.getTin().getTriangulatedSurface() instanceof Tin) {

        tin = ConvertCityGMLtoGeometry.convertGMLTin((Tin) (rC.getTin()
            .getTriangulatedSurface()));

      } else if (rC.getTin().getTriangulatedSurface() instanceof TriangulatedSurface) {
        tin = ConvertCityGMLtoGeometry
            .convertGMLTriangulatedSurface((TriangulatedSurface) (rC.getTin()
                .getTriangulatedSurface()));

      }

    }

  }

  @Override
  public AbstractCityObject export() {
    // TODO Auto-generated method stub
    return null;
  }
}
