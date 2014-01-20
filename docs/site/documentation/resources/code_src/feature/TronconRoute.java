import fr.ign.cogit.geoxygene.feature.FT_Feature;

public class TronconDeRoute extends FT_Feature {
  
  /** Renvoie la géométrie de l'objet, castée plus précisément qu'avec la méthode getGeom() */
  public GM_LineString getGeometrie() {return (GM_LineString)geom;}
  /** Définit la géométrie de l'objet, castée plus précisément qu'avec la méthode setGeom() */
  public void setGeometrie(GM_LineString G) {this.geom = G;}
  
  protected double nb_voies;
  public double getNb_voies() {return this.nb_voies; }
  public void setNb_voies (double Nb_voies) {nb_voies = Nb_voies; }
  
  protected String revetement;
  public String getRevetement() {return this.revetement; }
  public void setRevetement (String revetement) {revetement = revetement; }

}
