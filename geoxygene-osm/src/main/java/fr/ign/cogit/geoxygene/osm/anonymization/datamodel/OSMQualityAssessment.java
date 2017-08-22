package fr.ign.cogit.geoxygene.osm.anonymization.datamodel;

/**
 * Classe incomplete destinée à
 * représenter l'évaluation apportée à
 * un contributeur ou un changeset
 * 
 * Contient un unique String
 * 
 * 
 * @author Matthieu Dufait
 */
public class OSMQualityAssessment {
  
  private String assessment;
  
  public OSMQualityAssessment(String assessment) {
    this.assessment = assessment;
  }

  public OSMQualityAssessment() {
    this.assessment = null;
  }

  public String getAssessment() {
    return assessment;
  }

  public void setAssessment(String assessment) {
    this.assessment = assessment;
  }
  
  public String toString() {
    return "OSMQualityAssessment: "+assessment;
  }
}
