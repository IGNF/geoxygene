package fr.ign.cogit.geoxygene.matching.dst.geomatching;


public class GeoMatchingTest {
//  @Test
//  public void testRunSurface() throws Exception {
//    GeoMatching matching = new GeoMatching();
//    Collection<Source<IFeature, GeomHypothesis>> criteria = new ArrayList<Source<IFeature, GeomHypothesis>>();
//    criteria.add(new SurfaceDistance());
//    IPopulation<IFeature> bdtopo = ShapefileReader.read("H:\\Data\\SIGPARIS\\parcelles_bdtopo.shp");
//    IPopulation<IFeature> vasserot = ShapefileReader
//        .read("H:\\Data\\SIGPARIS\\parcelles_vasserot.shp");
//    IFeature reference = bdtopo.get(0);
//    List<IFeature> candidates = new ArrayList<IFeature>(vasserot.select(reference.getGeom()));
//    boolean closed = false;
//    System.out.println(candidates.size() + " candidates");
//    EvidenceResult<GeomHypothesis> result = matching.run(criteria, reference, candidates,
//        ChoiceType.PIGNISTIC, closed);
//    System.out.println("result = " + result);
//    System.out.println("reference = " + reference.getGeom());
//    System.out.println("value = " + result.getValue());
//    System.out.println("conflict = " + result.getConflict());
//    System.out.println("with " + result.getHypothesis().size());
//    for (int i = 0; i < result.getHypothesis().size(); i++) {
//      System.out.println("\tobj " + i + " = " + result.getHypothesis().get(i));
//    }
//  }

  /*@Test
  public void testRunLine() throws Exception {
    GeoMatching matching = new GeoMatching();
    List<Source<IFeature, GeomHypothesis>> criteria = new ArrayList<Source<IFeature, GeomHypothesis>>();
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
    List<Double> weights = new ArrayList<Double>();
    weights.add(0.9);
    weights.add(0.9);
    result = matching.runAppriou(criteria, reference, candidates, weights,
        ChoiceType.PIGNISTIC, closed);
    System.out.println("result = " + result);
    System.out.println("reference = " + reference.getGeom());
    System.out.println("value = " + result.getValue());
    System.out.println("conflict = " + result.getConflict());
    System.out.println("with " + result.getHypothesis().size());
    for (int i = 0; i < result.getHypothesis().size(); i++) {
      System.out.println("\tobj " + i + " = " + result.getHypothesis().get(i));
    }
  }*/
}
