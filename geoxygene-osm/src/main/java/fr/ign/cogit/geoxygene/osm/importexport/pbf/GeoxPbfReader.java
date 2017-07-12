/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.osm.importexport.pbf;

import java.io.File;

import org.openstreetmap.osmosis.pbf2.v0_6.PbfReader;

public class GeoxPbfReader {

    public static GeoxSink mySink = new GeoxSink();

    public static void main(String[] args) {
        long tdeb = System.currentTimeMillis();
        PbfReader reader = new PbfReader(
                new File("D:/Users/qttruong/data/nepal.osm.pbf"),
                1);
        reader.setSink(mySink);
        System.out.println("on entre dans le run");
        reader.run();
        if (mySink.nodeValues.length() > 0) {
        	mySink.nodeValues.deleteCharAt(mySink.nodeValues.length() - 1);
        	mySink.myQueries
                    .append("INSERT INTO node (idnode,id,uid,vnode,changeset,username,datemodif, tags, lat, lon, geom) VALUES ")
                    .append(mySink.nodeValues).append(";");
        	mySink.executeQuery();
        	mySink.nodeValues.setLength(0);
        }
        if (mySink.wayValues.length() > 0) {
        	mySink.wayValues.deleteCharAt(mySink.wayValues.length() - 1);
        	mySink.myQueries
                    .append("INSERT INTO way (idway, id, uid, vway, changeset,username,datemodif, tags, composedof) VALUES ")
                    .append(mySink.wayValues).append(";");
        	mySink.executeQuery();
        	mySink.wayValues.setLength(0);
        }
        if (mySink.relValues.length() > 0) {
            mySink.relValues.deleteCharAt(mySink.relValues.length() - 1);
            mySink.myQueries
                    .append("INSERT INTO relation (idrel, id, uid, vrel,changeset,username, datemodif, tags) VALUES ")
                    .append(mySink.relValues).append(";");
            // System.out.println("Requête\n" + mySink.myQueries.toString());
             mySink.executeQuery();
            mySink.relValues.setLength(0);
        }
        
        if (mySink.relmbValues.length() > 0) {
            mySink.relmbValues.deleteCharAt(mySink.relmbValues.length() - 1);
            mySink.myQueries.append("INSERT INTO relationmember (idrel,idmb,idrelmb,typemb,rolemb) VALUES ").append(mySink.relmbValues).append(";");
            // System.out.println("Requête\n" + mySink.myQueries.toString());
             mySink.executeQuery();
            mySink.relmbValues.setLength(0);
        }
        System.out.println("Durée traitement : "
                + ((System.currentTimeMillis() - tdeb) / 1000 / 60) + " min");
    }
}
