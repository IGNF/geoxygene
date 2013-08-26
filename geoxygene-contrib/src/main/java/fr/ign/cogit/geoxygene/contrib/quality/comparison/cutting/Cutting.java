package fr.ign.cogit.geoxygene.contrib.quality.comparison.cutting;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 *            A class to perform different types of linestring cutting
 *            (extremity and section)
 * 
 * @author JFGirres
 * 
 * 
 */
public class Cutting<Geom extends IGeometry> {
    public List<List<Geom>> cut(Geom g1, Geom g2) {
        List<List<Geom>> result = new ArrayList<List<Geom>>();
        List<Geom> pair = new ArrayList<Geom>();
        pair.add(g1);
        pair.add(g2);
        result.add(pair);
        return result;
    }
}
