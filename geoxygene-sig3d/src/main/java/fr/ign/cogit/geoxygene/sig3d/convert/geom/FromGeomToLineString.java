package fr.ign.cogit.geoxygene.sig3d.convert.geom;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

public class FromGeomToLineString {

	public static List<IOrientableCurve> convert(IGeometry geom) {

		if (geom instanceof IMultiCurve<?>) {

			@SuppressWarnings("unchecked")
			IMultiCurve<IOrientableCurve> iMC = (IMultiCurve<IOrientableCurve>) geom;

			return iMC.getList();

		} else if (geom instanceof IOrientableCurve) {
			List<IOrientableCurve> l = new ArrayList<IOrientableCurve>();
			l.add((IOrientableCurve) geom);

			return l;

		} else if (geom instanceof IPolygon) {
			
			IPolygon pol = (IPolygon) geom;
			
			List<IOrientableCurve> l = new ArrayList<IOrientableCurve>();
			l.add(pol.getExterior());
			if(pol.getInterior() != null){
				l.addAll(pol.getInterior());
			}
			return l;
		}

		System.out.println("ConvertToLineString : cas non traité " + geom.getClass());

		return new ArrayList<IOrientableCurve>();

	}
}
