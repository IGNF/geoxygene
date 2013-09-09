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
package fr.ign.cogit.process.geoxygene.cartetopo;

import org.apache.log4j.Logger;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

import fr.ign.cogit.process.geoxygene.GeoxygeneProcess;

/**
 * 
 * 
 *
 */
@DescribeProcess(title = "ImportAsEdgesProcess", description = "Do network data matching")
public class CreateCarteTopoProcess implements GeoxygeneProcess {
    
    /** LOGGER. */
    private final static Logger LOGGER = Logger.getLogger(CreateCarteTopoProcess.class.getName());

    /**
     * 
     * @param popRef
     * @return
     */
    @DescribeResult(name = "Statut", description = "Statut OK")
    public String execute(
        @DescribeParameter(name = "popRef", description = "Less detailed network") SimpleFeatureCollection popRef) {
        
        LOGGER.debug("Debut ici");
        
        return "OK";
    }

}
