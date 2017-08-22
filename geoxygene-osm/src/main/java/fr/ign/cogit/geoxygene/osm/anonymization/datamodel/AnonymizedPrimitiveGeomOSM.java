package fr.ign.cogit.geoxygene.osm.anonymization.datamodel;

import fr.ign.cogit.geoxygene.osm.anonymization.datamodel.util.OSMPrimitiveType;

/**
 * Classe abstraite mère des primitives géographique
 * d'OpenStreetMap
 * 
 * @author Matthieu Dufait
 */
public abstract class AnonymizedPrimitiveGeomOSM {
  private OSMAnonymizedResource objet;

  public void setObjet(OSMAnonymizedResource objet) {
    this.objet = objet;
  }

  public OSMAnonymizedResource getObjet() {
    return objet;
  }

  public abstract OSMPrimitiveType getOSMPrimitiveType();

  @Override
  public String toString() {
    return getOSMPrimitiveType().toString() ;
  }
}
