package fr.ign.cogit.geoxygene.sig3d.convert.geom;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

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
 * @version 0.1
 * 
 * 
 * 
 */
public class FromPolygonToLineString {

	public static List<ILineString> convertListPolToLineStrings(IMultiSurface<IOrientableSurface> iOS) {

		return convertListPolToLineStrings(iOS.getList());
	}

	public static List<ILineString> convertListPolToLineStrings(List<IOrientableSurface> lOS) {
		List<ILineString> lLS = new ArrayList<ILineString>();

		for (IOrientableSurface oS : lOS) {
			lLS.addAll(convertPolToLineStrings((IPolygon) oS));
		}

		return lLS;

	}

	public static List<ILineString> convertPolToLineStrings(IOrientableSurface pol) {

		List<ILineString> lLS = new ArrayList<ILineString>();

		if (pol.boundary() == null) {
			return lLS;
		}

		List<IRing> lRing = new ArrayList<>();

		IRing rExt = pol.boundary().getExterior();

		lRing.add(rExt);

		lRing.addAll(pol.boundary().getInterior());

		for (IRing r : lRing) {
			if (r == null)
				continue;

			IDirectPositionList dpl = r.coord();

			for (int i = 0; i < dpl.size() - 1; i++) {

				IDirectPositionList dplTemp = new DirectPositionList();
				dplTemp.add(dpl.get(i));
				dplTemp.add(dpl.get(i + 1));

				lLS.add(new GM_LineString(dplTemp));

			}
		}

		return lLS;

	}
}
