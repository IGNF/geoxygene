package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.appearance.TexCoordList;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_TexCoordList extends CG_AbstractTextureParameterization {

  protected List<CG_TextureCoordinates> textureCoordinates = new ArrayList<CG_TextureCoordinates>();

  public List<CG_TextureCoordinates> getTextureCoordinates() {
    return this.textureCoordinates;
  }

  public CG_TexCoordList(TexCoordList tPT) {
    if (tPT.isSetTextureCoordinates()) {

      int nbTC = tPT.getTextureCoordinates().size();

      for (int i = 0; i < nbTC; i++) {

        this.textureCoordinates.add(new CG_TextureCoordinates(tPT
            .getTextureCoordinates().get(i)));

      }

    }

  }

}
