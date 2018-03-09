package fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;

public class GML_Triangle extends GM_Triangle implements GML_Geometry{
	
	
	public GML_Triangle(IDirectPosition dp1, IDirectPosition dp2, IDirectPosition dp3){
		super(dp1,dp2,dp3);
	}

	public IPolygon getGeometry() {
		return this;
	}

	public GML_Triangle() {
		super();
	}

	public GML_Triangle(GML_Ring r) {
		super(r);
	}

	private String ID = "";

	public String getID() {
		return this.ID;
	}

	public void setID(String iD) {
		this.ID = iD;
	}

	public String toString() {
		return "ID " + this.getID() + "  " + super.toString();
	}

}
