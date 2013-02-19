package org.geoserver.wps.gs;

import org.geotools.process.factory.DescribeParameter;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;
import org.geotools.process.gs.GSProcess;

/**
*
*        This software is released under the licence CeCILL
* 
*        see Licence_CeCILL-C_fr.html
*        see Licence_CeCILL-C_en.html
* 
*        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
* 
* 
* @copyright IGN
* 
*/
@DescribeProcess(title="helloWPS", description="Hello WPS Sample")
public class HelloWPS implements GSProcess {

   @DescribeResult(name="result", description="output result")
   public String execute(@DescribeParameter(name="name", description="name to return") String name) {
     return "Hello, " + name;
   }

}