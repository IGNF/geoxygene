package fr.ign.cogit.cartagen.core.genericschema.land;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjPoint;

public interface ITreePoint extends IGeneObjPoint {

  public String getNature();

  public void setNature(String nature);

  public String getType();

  public void setType(String type);

  public static final String FEAT_TYPE_NAME = "TreePoint"; //$NON-NLS-1$

}
