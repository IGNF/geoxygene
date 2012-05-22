package fr.ign.cogit.geoxygene.example.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * Classe geographique. Classe generee automatiquement par le chargeur de la
 * plate-forme GeOxygene
 */
@Entity
@Table(name = "Point_eau")
public class Point_eau extends fr.ign.cogit.geoxygene.feature.FT_Feature {

  protected int gid;

  public int getGid() {
    return this.gid;
  }

  public void setGid(int Gid) {
    this.gid = Gid;
  }

  protected String source;

  public String getSource() {
    return this.source;
  }

  public void setSource(String Source) {
    this.source = Source;
  }

  protected String nature;

  public String getNature() {
    return this.nature;
  }

  public void setNature(String Nature) {
    this.nature = Nature;
  }

  @Override
  @Id
  @GeneratedValue
  public int getId() {
    return super.getId();
  }

  @Override
  @Column(name = "geom")
  @Type(type = "fr.ign.cogit.geoxygene.datatools.hibernate.GeOxygeneGeometryUserType")
  public IGeometry getGeom() {
    return super.getGeom();
  }
}
