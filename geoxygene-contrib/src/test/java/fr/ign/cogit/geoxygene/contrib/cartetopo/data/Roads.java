package fr.ign.cogit.geoxygene.contrib.cartetopo.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_Feature;

@Entity
@Table(name = "Roads")
public class Roads extends FT_Feature {
  
  protected int source;

  public int getSource() {
    return this.source;
  }

  public void setSource(int Source) {
    this.source = Source;
  }
  
  protected int target;

  public int getTarget() {
    return this.target;
  }

  public void setTarget(int Target) {
    this.target = Target;
  }
  
  protected Double x1;

  public Double getX1() {
    return this.x1;
  }

  public void setX1(Double X1) {
    this.x1 = X1;
  }
  
  protected Double y1;

  public Double getY1() {
    return this.y1;
  }

  public void setY1(Double Y1) {
    this.y1 = Y1;
  }
  
  protected Double x2;

  public Double getX2() {
    return this.x2;
  }

  public void setX2(Double X2) {
    this.x2 = X2;
  }
  
  protected Double y2;

  public Double getY2() {
    return this.y2;
  }

  public void setY2(Double Y2) {
    this.y2 = Y2;
  }
  
  protected Double cost;

  public Double getCost() {
    return this.cost;
  }

  public void setCost(Double Cost) {
    this.cost = Cost;
  }
  
  protected Double reverse_Cost;

  public Double getReverse_Cost() {
    return this.reverse_Cost;
  }

  public void setReverse_Cost(Double Reverse_Cost) {
    this.reverse_Cost = Reverse_Cost;
  }
  
  /*protected String oneway;

  public String getOneway() {
    return this.oneway;
  }

  public void setOneway(String Oneway) {
    this.oneway = Oneway;
  }
  
  protected Double length;

  public Double getLength() {
    return this.length;
  }

  public void setLength(Double Length) {
    this.length = Length;
  }*/
  
  protected String sens;

  public String getSens() {
    return this.sens;
  }

  public void setSens(String Sens) {
    this.sens = Sens;
  }
  
  protected Integer edge_Id;

  public Integer getEdge_Id() {
    return this.edge_Id;
  }

  public void setEdge_Id(Integer Edge_Id) {
    this.edge_Id = Edge_Id;
  }
  
  @Override
  @Id
  @GeneratedValue
  public int getId() {
    return super.getId();
  }

  @Override
  @Column(name = "the_geom")
  @Type(type = "fr.ign.cogit.geoxygene.datatools.hibernate.GeOxygeneGeometryUserType")
  public IGeometry getGeom() {
    return super.getGeom();
  }

}
