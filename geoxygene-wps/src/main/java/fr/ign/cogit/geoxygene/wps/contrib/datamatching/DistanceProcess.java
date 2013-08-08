package fr.ign.cogit.geoxygene.wps.contrib.datamatching;

import org.geoserver.wps.gs.GeoServerProcess;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.factory.DescribeResult;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ParamDistanceNetworkDataMatching;

@DescribeProcess(title = "DistanceProcess", description = "DistanceProcess")
public class DistanceProcess implements GeoServerProcess {

    @DescribeResult(name = "paramDistance", description = "Ok !!")
    public ParamDistanceNetworkDataMatching execute() {
        
        // On ne fait rien, on renvoie juste un xml ....
        
        ParamDistanceNetworkDataMatching paramDistance = new ParamDistanceNetworkDataMatching();
        
        System.out.println("====================================================================");
        System.out.println(paramDistance.toString());
        System.out.println("====================================================================");
        
        return paramDistance;
    }

}
