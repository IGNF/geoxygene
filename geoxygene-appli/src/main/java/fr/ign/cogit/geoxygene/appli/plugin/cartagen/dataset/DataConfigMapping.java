/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.dataset;

/**
 * Classe qui mappe le fichier de configuration de données
 * configurationDonnees.xml pour permettre une sérialisation et une
 * désérialisation simples basées sur modèle en s'appuyant sur XStream
 * 
 * @author MVieira
 * 
 */
public class DataConfigMapping {

  private String cheminRepertoireDonneesSHP;
  private String resolutionMNT;
  private String enrichissementBati;
  private String enrichissementBatiAlign;
  private String enrichissementRoutier;
  private String enrichissementHydro;
  private String enrichissementRelief;
  private String enrichissementOccSol;
  private String construireFacesReseau;

  /**
   * Constructeur par défaut
   */
  public DataConfigMapping() {
    super();
  }

  public String getCheminRepertoireDonneesSHP() {
    return this.cheminRepertoireDonneesSHP;
  }

  public void setCheminRepertoireDonneesSHP(String cheminRepertoireDonneesSHP) {
    this.cheminRepertoireDonneesSHP = cheminRepertoireDonneesSHP;
  }

  public String getResolutionMNT() {
    return this.resolutionMNT;
  }

  public void setResolutionMNT(String resolutionMNT) {
    this.resolutionMNT = resolutionMNT;
  }

  public String getEnrichissementBati() {
    return this.enrichissementBati;
  }

  public void setEnrichissementBati(String enrichissementBati) {
    this.enrichissementBati = enrichissementBati;
  }

  public String getEnrichissementBatiAlign() {
    return this.enrichissementBatiAlign;
  }

  public void setEnrichissementBatiAlign(String enrichissementBatiAlign) {
    this.enrichissementBatiAlign = enrichissementBatiAlign;
  }

  public String getEnrichissementRoutier() {
    return this.enrichissementRoutier;
  }

  public void setEnrichissementRoutier(String enrichissementRoutier) {
    this.enrichissementRoutier = enrichissementRoutier;
  }

  public String getEnrichissementHydro() {
    return this.enrichissementHydro;
  }

  public void setEnrichissementHydro(String enrichissementHydro) {
    this.enrichissementHydro = enrichissementHydro;
  }

  public String getEnrichissementRelief() {
    return this.enrichissementRelief;
  }

  public void setEnrichissementRelief(String enrichissementRelief) {
    this.enrichissementRelief = enrichissementRelief;
  }

  public String getEnrichissementOccSol() {
    return this.enrichissementOccSol;
  }

  public void setEnrichissementOccSol(String enrichissementOccSol) {
    this.enrichissementOccSol = enrichissementOccSol;
  }

  public String getConstruireFacesReseau() {
    return this.construireFacesReseau;
  }

  public void setConstruireFacesReseau(String construireFacesReseau) {
    this.construireFacesReseau = construireFacesReseau;
  }

}
