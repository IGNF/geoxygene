package fr.ign.cogit.geoxygene.matching.dst.geomatching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.matching.dst.evidence.ChoiceType;
import fr.ign.cogit.geoxygene.matching.dst.evidence.EvidenceResult;
import fr.ign.cogit.geoxygene.matching.dst.evidence.Source;
import fr.ign.cogit.geoxygene.matching.dst.sources.linear.LineOrientation;
import fr.ign.cogit.geoxygene.matching.dst.sources.linear.PartialFrechetDistance;
import fr.ign.cogit.geoxygene.matching.dst.sources.surface.SurfaceDistance;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

public class GeoMatchingTest {
  @Test
  public void testRunSurface() throws Exception {
    GeoMatching matching = new GeoMatching();
    Collection<Source<GeomHypothesis>> criteria = new ArrayList<Source<GeomHypothesis>>();
    criteria.add(new SurfaceDistance());
    IPopulation<IFeature> bdtopo = ShapefileReader.read("H:\\Data\\SIGPARIS\\parcelles_bdtopo.shp");
    IPopulation<IFeature> vasserot = ShapefileReader
        .read("H:\\Data\\SIGPARIS\\parcelles_vasserot.shp");
    IFeature reference = bdtopo.get(0);
    List<IFeature> candidates = new ArrayList<IFeature>(vasserot.select(reference.getGeom()));
    boolean closed = false;
    System.out.println(candidates.size() + " candidates");
    EvidenceResult<GeomHypothesis> result = matching.run(criteria, reference, candidates,
        ChoiceType.PIGNISTIC, closed);
    System.out.println("result = " + result);
    System.out.println("reference = " + reference.getGeom());
    System.out.println("value = " + result.getValue());
    System.out.println("conflict = " + result.getConflict());
    System.out.println("with " + result.getHypothesis().size());
    for (int i = 0; i < result.getHypothesis().size(); i++) {
      System.out.println("\tobj " + i + " = " + result.getHypothesis().get(i));
    }
  }

  @Test
  public void testRunLine() throws Exception {
    GeoMatching matching = new GeoMatching();
    Collection<Source<GeomHypothesis>> criteria = new ArrayList<Source<GeomHypothesis>>();
    criteria.add(new PartialFrechetDistance());
    criteria.add(new LineOrientation());
    IPopulation<IFeature> bdtopo = ShapefileReader
        .read("H:\\Data\\SIGPARIS\\rues\\poubelle\\test.shp");
    IPopulation<IFeature> vasserot = ShapefileReader
        .read("H:\\Data\\SIGPARIS\\rues\\vasserot\\test.shp");
    IFeature reference = bdtopo.get(1);
    List<IFeature> candidates = new ArrayList<IFeature>(vasserot.select(reference.getGeom().buffer(
        20)));
    boolean closed = true;
    System.out.println(candidates.size() + " candidates");
    EvidenceResult<GeomHypothesis> result = matching.run(criteria, reference, candidates,
        ChoiceType.PIGNISTIC, closed);
    System.out.println("result = " + result);
    System.out.println("reference = " + reference.getGeom());
    System.out.println("value = " + result.getValue());
    System.out.println("conflict = " + result.getConflict());
    System.out.println("with " + result.getHypothesis().size());
    for (int i = 0; i < result.getHypothesis().size(); i++) {
      System.out.println("\tobj " + i + " = " + result.getHypothesis().get(i));
    }
  }
}
