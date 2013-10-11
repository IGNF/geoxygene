package fr.ign.cogit.process.geoxygene.cartetopo;

import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

import fr.ign.cogit.process.geoxygene.GeoxygeneProcess;

/**
 * 
 * 
 *
 */
@DescribeProcess(title = "ShortestPathProcess", description = "ShortestPathProcess")
public class ShortestPathProcess implements GeoxygeneProcess {
    
    @DescribeResult(name = "result", description = "output result")
    public String execute() {
        return "OK";
    }

}
