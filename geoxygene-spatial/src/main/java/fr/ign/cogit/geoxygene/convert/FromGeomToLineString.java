package fr.ign.cogit.geoxygene.convert;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;

public class FromGeomToLineString {
	
	public static IMultiCurve<IOrientableCurve> convertMC(IGeometry geom) {
		 IMultiCurve<IOrientableCurve>  iMC = new GM_MultiCurve<>(convert( geom));
		return iMC;
	}

	
	public static List<ILineString> convertLineString(IGeometry geom) {
		
		List<IOrientableCurve> iml = convert(geom);
		List<ILineString> ls = new ArrayList<ILineString>();
		for(IOrientableCurve curve:iml) {
			ls.add((ILineString)curve);
		}
		return ls;
	}
	
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
			return convertSurface((IPolygon) geom);
		}else if (geom instanceof IMultiSurface<?> ){
			List<IOrientableCurve> l = new ArrayList<IOrientableCurve>();
			IMultiSurface<IOrientableSurface> lOS = FromGeomToSurface.convertMSGeom(geom);
			for(IOrientableSurface os :lOS) {
				if(os instanceof IPolygon) {
					l.addAll(convertSurface((IPolygon) os));
				}else {
					System.out.println("ConvertToLineString : cas non traité dans une multi-surface :" + os.getClass());
				}
			}
			
			return l;
			
		}

		System.out.println("ConvertToLineString : cas non traité " + geom.getClass());

		return new ArrayList<IOrientableCurve>();

	}

	private static List<IOrientableCurve> convertSurface(IPolygon pol) {

		List<IOrientableCurve> l = new ArrayList<IOrientableCurve>();
		l.add(pol.exteriorLineString());
		if (pol.getInterior() != null) {
		  for (int i = 0 ; i < pol.getInterior().size() ; i++) {
		    l.add(pol.interiorLineString(i));
		  }
//			l.addAll(pol.getInterior());
		}
		return l;
	}

	public static List<IOrientableCurve> convertInSegment(IGeometry geom) {

		if (geom instanceof IMultiCurve<?>) {

			@SuppressWarnings("unchecked")
			IMultiCurve<IOrientableCurve> iMC = (IMultiCurve<IOrientableCurve>) geom;

			return iMC.getList();

		} else if (geom instanceof IOrientableCurve) {
			List<IOrientableCurve> l = new ArrayList<IOrientableCurve>();

			l.addAll(generateListOrientableSurfaceFromCoord(geom.coord()));

			return l;

		} else if (geom instanceof IPolygon) {

			IPolygon pol = (IPolygon) geom;

			List<IOrientableCurve> l = new ArrayList<IOrientableCurve>();

			l.addAll(generateListOrientableSurfaceFromCoord(pol.getExterior().coord()));
			if (pol.getInterior() != null) {
				for (IRing r : pol.getInterior()) {

					l.addAll(generateListOrientableSurfaceFromCoord(r.coord()));
				}

			}
			return l;
		}

		System.out.println("ConvertToLineString : cas non traité " + geom.getClass());

		return new ArrayList<IOrientableCurve>();

	}

	private static List<IOrientableCurve> generateListOrientableSurfaceFromCoord(IDirectPositionList dpl) {

		int nbPoints = dpl.size();

		List<IOrientableCurve> lC = new ArrayList<>();

		for (int i = 1; i < nbPoints; i++) {

			lC.add(new GM_LineString(new DirectPositionList(dpl.get(i - 1), dpl.get(i))));

		}

		return lC;
	}
}
