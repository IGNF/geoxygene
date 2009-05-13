package fr.ign.cogit.geoxygene.example.hibernate;


import javax.persistence.*;

import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import org.hibernate.annotations.Type;

/** Classe geographique. Classe generee automatiquement par le chargeur de la plate-forme GeOxygene*/
@Entity
@Table(name="Point_eau")
public class Point_eau extends fr.ign.cogit.geoxygene.feature.FT_Feature {

     protected int gid;
     public int getGid() {return this.gid; }
     public void setGid (int Gid) {gid = Gid; }

     protected String source;
     public String getSource() {return this.source; }
     public void setSource (String Source) {source = Source; }

     protected String nature;
     public String getNature() {return this.nature; }
     public void setNature (String Nature) {nature = Nature; }
     
     @Override
     @Id @GeneratedValue
     public int getId() {return super.getId();}
     @Override
     @Column(name = "geom")
     @Type(type = "fr.ign.cogit.geoxygene.datatools.hibernate.GeOxygeneGeometryUserType")
     public GM_Object getGeom() {return super.getGeom();}
}
