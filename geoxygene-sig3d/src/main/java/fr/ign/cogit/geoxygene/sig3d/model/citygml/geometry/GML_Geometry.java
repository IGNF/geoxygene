package fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;

public interface GML_Geometry {
	
	
	public String getID();
	
	public void setID(String iD);
	
	
	public String toString();
	
	
	public IPolygon getGeometry();

}
