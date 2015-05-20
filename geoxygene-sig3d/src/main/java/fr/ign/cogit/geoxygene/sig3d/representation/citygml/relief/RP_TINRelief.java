package fr.ign.cogit.geoxygene.sig3d.representation.citygml.relief;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.relief.CG_TINRelief;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;

public class RP_TINRelief extends RP_CityObject {

  public RP_TINRelief(CG_TINRelief tin) {
    this(tin, null);
  }

  public RP_TINRelief(CG_TINRelief tin, List<CG_AbstractSurfaceData> lCGAIni) {
    super();

    
    List<CG_AbstractSurfaceData> lCGA = new ArrayList<CG_AbstractSurfaceData>();

    if (lCGAIni != null) {
      lCGA.addAll(lCGAIni);
    }

    int nbAppProperty = tin.getAppearanceProperty().size();

    for (int i = 0; i < nbAppProperty; i++) {

      lCGA.addAll(tin.getAppearanceProperty().get(i).getAppearance()
          .getSurfaceDataMember());
    }

    
    
    if(tin.getTin() != null){
      
      RepresentationTin rpTin = new RepresentationTin(tin.getTin());
      
      
      this.bGRep.addChild(rpTin.getBGRep());
      
    }
    
    tin.setRepresentation(this);

  }

}
