package fr.ign.cogit.geoxygene.util.batchrenderer;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.jdbc.postgis.PostgisReader;

public class PostgisConnector implements DataConnector{

  private Map<String, String> params;
  private List<String> couches;
  private String geomColumn = "geometrie";
  private final double BUFFERDIVIDE = 3;

  public PostgisConnector(Map<String, String> params, List<String> couches, String geomColumn) {
    this.params = params;
    this.couches = couches;
    this.geomColumn = geomColumn;
  }
  
  public PostgisConnector(Map<String, String> params, List<String> couches) {
      this.params = params;
      this.couches = couches;
    }

  public IPopulation<IFeature> getPopulation(String table, IEnvelope env) {
    IGeometry geomBuff = env.getGeom().buffer(env.length() / BUFFERDIVIDE);
    IEnvelope envBuf = geomBuff.envelope();
    String filter = "BBOX(geometrie, " + envBuf.minX() + "," + envBuf.minY() + "," + envBuf.maxX() + "," + envBuf.maxY()
        + ")";
    System.out.println("************** Loading " + table);
    IPopulation<IFeature> pop = null;
    try {
      pop = PostgisReader.read(params, table, table, null, false, "geometrie", filter);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("************** Finished " + table + " : " + pop.size() + " entities");
    return pop;

  }

  public List<String> getCouches() {
    return couches;
  }

}
