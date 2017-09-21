package fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurfaceBoundary;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * 
 * @author MBrasebin
 * 
 */
public class GML_Polygon extends GM_Polygon {

	public GML_Polygon( ICurve curve, double tolerance) throws Exception {
		super(curve, tolerance);
		// TODO Auto-generated constructor stub
	}

	public GML_Polygon(ICurve curve) {
		super(curve);
		// TODO Auto-generated constructor stub
	}

	public GML_Polygon(IEnvelope env) {
		super(env);
		// TODO Auto-generated constructor stub
	}

	public GML_Polygon(ILineString ls, double tolerance) throws Exception {
		super(ls, tolerance);
		// TODO Auto-generated constructor stub
	}

	public GML_Polygon(ILineString ls) {
		super(ls);
		// TODO Auto-generated constructor stub
	}

	public GML_Polygon(GM_Polygon geom) {
		super(geom);
		// TODO Auto-generated constructor stub
	}

	public GML_Polygon(IRing ring) {
		super(ring);
		// TODO Auto-generated constructor stub
	}

	public GML_Polygon(ISurfaceBoundary boundary, ISurface spanSurf) {
		super(boundary, spanSurf);
		// TODO Auto-generated constructor stub
	}

	public GML_Polygon(ISurfaceBoundary bdy) {
		super(bdy);
		// TODO Auto-generated constructor stub
	}

	public GML_Polygon() {
		super();
	}

	private String ID = "";

	public String getID() {
		return this.ID;
	}

	public void setID(String iD) {
		this.ID = iD;
	}

}
