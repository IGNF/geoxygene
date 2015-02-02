package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import org.citygml4j.model.citygml.appearance.TexCoordGen;
import org.citygml4j.model.citygml.appearance.TexCoordList;
import org.citygml4j.model.citygml.appearance.TextureParameterization;

/**
 * 
 * @author MBrasebin
 * 
 */
public abstract class CG_AbstractTextureParameterization

{

  public static CG_AbstractTextureParameterization generateAbstractTextureParameterization(
      TextureParameterization tPT) {

    if (tPT instanceof TexCoordList) {

      return new CG_TexCoordList((TexCoordList) tPT);

    } else if (tPT instanceof TexCoordGen) {
      return new CG_TexCoordGen((TexCoordGen) tPT);
    }

    System.out
        .println("Classe non gérée :  AbstractTextureParameterizationType "
            + tPT.getClass().getCanonicalName());
    return null;

  }
}
