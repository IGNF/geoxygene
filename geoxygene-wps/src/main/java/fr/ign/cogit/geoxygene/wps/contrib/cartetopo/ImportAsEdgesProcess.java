/**
*
*        This software is released under the licence CeCILL
* 
*        see Licence_CeCILL_V2-fr.txt
*        see Licence_CeCILL_V2-en.txt
* 
*        see <a href="http://www.cecill.info/">http://www.cecill.info</a>
* 
* 
* @copyright IGN
*
*/
package fr.ign.cogit.geoxygene.wps.contrib.cartetopo;

import org.apache.log4j.Logger;
import org.geoserver.wps.gs.GeoServerProcess;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

@DescribeProcess(title = "ImportAsEdgesProcess", description = "Do network data matching")
public class ImportAsEdgesProcess implements GeoServerProcess {
    
    /** LOGGER. */
    private final static Logger LOGGER = Logger.getLogger(ImportAsEdgesProcess.class.getName());

    /**
     * 
     * @param popRef
     * @return
     */
    @DescribeResult(name = "Statut", description = "Statut OK")
    public String execute(
        @DescribeParameter(name = "popRef", description = "Less detailed network") SimpleFeatureCollection popRef) {
        
        
        
        return "OK";
    }

}
