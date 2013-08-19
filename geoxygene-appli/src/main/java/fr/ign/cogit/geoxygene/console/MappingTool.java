package fr.ign.cogit.geoxygene.console;

/**
 * 
 * @author MDVan-Damme
 * 
 */
public interface MappingTool {

  /**
   * Mapping non reconnu par la console.
   */
  int MAPPING_UNKNOWN = 0;

  /**
   * Castor.
   */
  int MAPPING_CASTOR = 1;

  /**
   * OJB.
   */
  int MAPPING_OJB = 2;

  /**
   * Hibernate.
   */
  int MAPPING_HIBERNATE = 3;

}
