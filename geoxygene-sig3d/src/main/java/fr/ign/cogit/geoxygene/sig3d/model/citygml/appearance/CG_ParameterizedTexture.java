package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.appearance.ParameterizedTexture;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_ParameterizedTexture extends CG_AbstractTexture {

  public CG_ParameterizedTexture(ParameterizedTexture pT) {
    super(pT);

    if (pT.isSetTarget()) {

    }

    if (pT.isSetTarget()) {

      int nbTarget = pT.getTarget().size();

      for (int i = 0; i < nbTarget; i++) {

        this.getTextureAssociation().add(pT.getTarget().get(i).getUri());

        this.getTarget().add(
            CG_AbstractTextureParameterization
                .generateAbstractTextureParameterization(pT.getTarget().get(i)
                    .getTextureParameterization()));
      }

    }

  }

  private List<String> textureAssociation = new ArrayList<String>();

  public List<String> getTextureAssociation() {
    return this.textureAssociation;
  }

  protected List<CG_AbstractTextureParameterization> target;

  public List<CG_AbstractTextureParameterization> getTarget() {
    if (this.target == null) {
      this.target = new ArrayList<CG_AbstractTextureParameterization>();
    }
    return this.target;
  }

  public boolean isSetTarget() {
    return ((this.target != null) && (!this.target.isEmpty()));
  }

  public void unsetTarget() {
    this.target = null;
  }

}
