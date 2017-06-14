package fr.ign.cogit.geoxygene.sig3d.calculation;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.analysis.roof.RoofDetection;
import fr.ign.cogit.geoxygene.sig3d.convert.transform.Extrusion2DObject;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.7
 * 
 **/
public class OrientedBoundingBox {

	private IPolygon poly = null;
	private double zMin = Double.NaN;
	private double zMax = Double.NaN;
	private IGeometry sol = null;
	private double width = -1;
	private double length = -1;
	private double angle = -1;
	private Vecteur shortestDirection = null;
	private Vecteur longestDirection = null;

	private IDirectPosition centre = null;

	public OrientedBoundingBox(IGeometry geom) {

		if (geom != null && geom.coord().size() != 0) {
			this.computeOrientedBoundingBox(geom);
		}

	}
	
	public double getWidth() {
		if (width == -1) {
			calculateWidthAndLength();
		}

		return width;
	}

	public double getLength() {
		if (length == -1) {
			calculateWidthAndLength();
		}

		return length;
	}
	
	
	
	/////////////////////////Calculation
	
	private void computeOrientedBoundingBox(IGeometry geom){

		IMultiSurface<IOrientableSurface> lOS = (IMultiSurface<IOrientableSurface>) RoofDetection.detectRoof(geom,
				0.2, false);

		if (lOS == null || lOS.size() == 0) {
			return;
		}

		poly = SmallestSurroundingRectangleComputation.getSSR(lOS);

		Box3D b = new Box3D(geom);

		zMin = b.getLLDP().getZ();
		zMax = b.getURDP().getZ();
	}



	private void calculateWidthAndLength() {

		IDirectPositionList dpl = poly.coord();
		double v1 = dpl.get(0).distance(dpl.get(1));
		double v2 = dpl.get(1).distance(dpl.get(2));

		length = Math.max(v1, v2);
		width = Math.min(v1, v2);

	}



	private void calculateDirection() {

		IDirectPositionList dpl = this.getPoly().coord();
		double v1 = dpl.get(0).distance(dpl.get(1));
		double v2 = dpl.get(1).distance(dpl.get(2));

		if (v1 > v2) {
			shortestDirection = new Vecteur(dpl.get(1), dpl.get(2));
			longestDirection = new Vecteur(dpl.get(0), dpl.get(1));
		} else {
			shortestDirection = new Vecteur(dpl.get(0), dpl.get(1));
			longestDirection = new Vecteur(dpl.get(1), dpl.get(2));
		}

	}
	
	
	private List<ILineString> shortestEdges = null, longestEdges = null;
	
	
	
	
	
	public List<ILineString> getShortestEdges(){
		if(shortestEdges == null){
			calculateEdges();
		}
		return shortestEdges;
		
	}
	
	
	
	public List<ILineString> getLongestEdges(){
		if(longestEdges == null){
			calculateEdges();
		}
		return longestEdges;
	}
	
	private void calculateEdges(){
		shortestEdges = new ArrayList<>();
		longestEdges = new ArrayList<>();
		
		IDirectPositionList dpl = this.getPoly().coord();
		double v1 = dpl.get(0).distance(dpl.get(1));
		double v2 = dpl.get(1).distance(dpl.get(2));
		
		IDirectPositionList dpl1 = new DirectPositionList()  ;
		IDirectPositionList dpl2 =  new DirectPositionList() ;
		
		
		dpl1.add(dpl.get(1));
		dpl1.add(dpl.get(2));
		
		dpl2.add(dpl.get(3));
		dpl2.add(dpl.get(4));
		
		
		
		IDirectPositionList dpl3 = new DirectPositionList()  ;
		IDirectPositionList dpl4 =  new DirectPositionList() ;
		
		dpl3.add(dpl.get(0));
		dpl3.add(dpl.get(1));
		
		dpl4.add(dpl.get(2));
		dpl4.add(dpl.get(3));
		

		if (v1 > v2) {

			
			shortestEdges.add(new GM_LineString(dpl1));
			shortestEdges.add(new GM_LineString(dpl2));
			longestEdges.add(new GM_LineString(dpl3));
			longestEdges.add(new GM_LineString(dpl4));
			

		}else{
			
			shortestEdges.add(new GM_LineString(dpl3));
			shortestEdges.add(new GM_LineString(dpl4));
			longestEdges.add(new GM_LineString(dpl1));
			longestEdges.add(new GM_LineString(dpl2));
	
		}
		
	}

	public Vecteur shortestDirection() {

		if (shortestDirection == null) {
			calculateDirection();

		}
		return shortestDirection;

	}


	public Vecteur longestDirection() {

		if (longestDirection == null) {
			calculateDirection();
		}
		return longestDirection;

	}

	public double getHeight() {
		return (this.getzMax() - this.getzMin());
	}

	public IGeometry get3DGeom() {

		if (sol == null && poly != null) {

			sol = Extrusion2DObject.convertFromPolygon(poly, zMin, zMax);

		}

		return sol;

	}

	public IDirectPosition getCentre() {

		if (centre == null) {

			IDirectPosition dp1 = poly.coord().get(0);
			IDirectPosition dp2 = poly.coord().get(2);

			double x = (dp1.getX() + dp2.getX()) / 2;
			double y = (dp1.getY() + dp2.getY()) / 2;

			centre = new DirectPosition(x, y);

		}
		return centre;
	}

	public IPolygon getPoly() {
		return poly;
	}

	public double getzMin() {
		return zMin;
	}

	public double getzMax() {
		return zMax;
	}

	public double getAngle() {
		if (angle == -1) {

			IDirectPosition dp1 = poly.coord().get(0);
			IDirectPosition dp2 = poly.coord().get(1);
			IDirectPosition dp3 = poly.coord().get(2);

			if (dp1.distance2D(dp2) < dp1.distance2D(dp3)) {

				dp2 = dp3;
			}

			Vecteur v = new Vecteur(dp1, dp2);
			v.normalise();

			double angleTemp = Math.acos(v.getX());

			angle = angleTemp % (Math.PI / 2);

			if (angle < 0) {
				angle = Math.PI / 2 + angle;
			}

		}
		return angle;
	}

}
