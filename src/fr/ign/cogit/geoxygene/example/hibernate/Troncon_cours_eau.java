package fr.ign.cogit.geoxygene.example.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;


/** Classe geographique. Classe generee automatiquement par le chargeur de la plate-forme GeOxygene*/
@Entity
@Table(name="Troncon_cours_eau")
public class Troncon_cours_eau extends fr.ign.cogit.geoxygene.feature.FT_Feature {

     protected int gid;
     public int getGid() {return this.gid; }
     public void setGid (int Gid) {this.gid = Gid; }

     protected String source;
     public String getSource() {return this.source; }
     public void setSource (String Source) {this.source = Source; }

     protected int artif;
     public int getArtif() {return this.artif; }
     public void setArtif (int Artif) {this.artif = Artif; }

     protected int fictif;
     public int getFictif() {return this.fictif; }
     public void setFictif (int Fictif) {this.fictif = Fictif; }

     protected String franchisst;
     public String getFranchisst() {return this.franchisst; }
     public void setFranchisst (String Franchisst) {this.franchisst = Franchisst; }

     protected String nom;
     public String getNom() {return this.nom; }
     public void setNom (String Nom) {this.nom = Nom; }

     protected int posit_sol;
     public int getPosit_sol() {return this.posit_sol; }
     public void setPosit_sol (int Posit_sol) {this.posit_sol = Posit_sol; }

     protected String regime;
     public String getRegime() {return this.regime; }
     public void setRegime (String Regime) {this.regime = Regime; }

     protected int et_id;
     public int getEt_id() {return this.et_id; }
     public void setEt_id (int Et_id) {this.et_id = Et_id; }
     
     @Override
     @Id @GeneratedValue
     public int getId() {return super.getId();}
     @Override
     @Column(name = "geom")
     @Type(type = "fr.ign.cogit.geoxygene.datatools.hibernate.GeOxygeneGeometryUserType")
     public GM_Object getGeom() {return super.getGeom();}
}
