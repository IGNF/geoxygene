package fr.ign.cogit.geoxygene.datatools.hibernate.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_Feature;

/**
 * 
 * This class aims at testing that geoxygene (particularly postgis-jdbc dependency) 
 *  has no interference with hibernate spatial
 * 
 * @author MBorne
 *
 */
@Entity
public class ClassWithJtsGeometry extends FT_Feature {
	
	private Point jtsGeometry ;

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}
	
	@Override
	@Type(type="fr.ign.cogit.geoxygene.datatools.hibernate.GeOxygeneGeometryUserType")
	public IGeometry getGeom() {
		return super.getGeom();
	}

	public Point getJtsGeometry() {
		return jtsGeometry;
	}

	public void setJtsGeometry(Point jtsGeometry) {
		this.jtsGeometry = jtsGeometry;
	}

}
