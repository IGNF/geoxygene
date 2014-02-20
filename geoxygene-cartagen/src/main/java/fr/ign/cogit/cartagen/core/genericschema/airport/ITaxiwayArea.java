/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.genericschema.airport;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObjSurf;

public interface ITaxiwayArea extends IGeneObjSurf {

  public enum TaxiwayType {
    TAXIWAY, APRON
  }

  public static final String FEAT_TYPE_NAME = "TaxiwayArea"; //$NON-NLS-1$

  public TaxiwayType getType();

  public IAirportArea getAirport();

  public void setAirport(IAirportArea airport);
}
