package fr.ign.cogit.geoxygene.sig3d.representation.citygml.relief;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.relief.CG_AbstractReliefComponent;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.relief.CG_ReliefFeature;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.relief.CG_TINRelief;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;

/**
 * 
 * @author MBrasebin
 *
 */
public class RP_ReliefFeature extends RP_CityObject {

  public RP_ReliefFeature(CG_ReliefFeature rF) {
    this(rF, null);
  }

  public RP_ReliefFeature(CG_ReliefFeature rF,
      List<CG_AbstractSurfaceData> lCGAIni) {
    super();

    List<CG_AbstractSurfaceData> lCGA = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGA.addAll(lCGAIni);
    }

    int nbAppProperty = rF.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(rF.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    List<CG_AbstractReliefComponent> rARC = rF.getReliefComponent();
    int nbARC = rARC.size();

    for (int i = 0; i < nbARC; i++) {
      CG_AbstractReliefComponent aRC = rARC.get(i);

      if (aRC instanceof CG_TINRelief) {
        
        RP_TINRelief rpT = new RP_TINRelief((CG_TINRelief)aRC, lCGA);
        this.getBGRep().addChild(rpT.getBGRep());

      }else{
        System.out.println("Relief non géré : "+aRC.getClass());
      }

    }
    
    rF.setRepresentation(this);

  }
}
