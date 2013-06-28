package fr.ign.cogit.cartagen.pearep.derivation.processes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.leastsquares.core.LSCurvatureConstraint;
import fr.ign.cogit.cartagen.leastsquares.core.LSMovementConstraint;
import fr.ign.cogit.cartagen.leastsquares.core.LSMovementDirConstraint;
import fr.ign.cogit.cartagen.leastsquares.core.LSProximityConstraint;
import fr.ign.cogit.cartagen.leastsquares.core.LSScheduler;
import fr.ign.cogit.cartagen.leastsquares.core.MapspecsLS;
import fr.ign.cogit.cartagen.leastsquares.core.LSScheduler.MatrixSolver;
import fr.ign.cogit.cartagen.mrdb.scalemaster.MultiThemeParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterMultiProcess;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterTheme;
import fr.ign.cogit.cartagen.pearep.enrichment.CutNetworkPreProcess;
import fr.ign.cogit.cartagen.pearep.enrichment.DeleteDoublePreProcess;
import fr.ign.cogit.cartagen.pearep.enrichment.MakeNetworkPlanarDir;
import fr.ign.cogit.cartagen.pearep.mgcp.hydro.MGCPWaterLine;
import fr.ign.cogit.cartagen.pearep.mgcp.transport.MGCPRailwayLine;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.network.flexibilitygraph.MinimumSeparation;
import fr.ign.cogit.cartagen.spatialanalysis.network.flexibilitygraph.OverlapConflict;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

public class DisplacementLSAProcess extends ScaleMasterMultiProcess {

  private final Set<MinimumSeparation> minSeps = new HashSet<MinimumSeparation>();
  private static DisplacementLSAProcess instance = null;

  public DisplacementLSAProcess() {
    // Exists only to defeat instantiation.
  }

  public static DisplacementLSAProcess getInstance() {
    if (instance == null) {
      instance = new DisplacementLSAProcess();
    }
    return instance;
  }

  @Override
  public void parameterise() {
    CartAGenDB db = CartAGenDoc.getInstance().getCurrentDataset()
        .getCartAGenDB();
    for (MultiThemeParameter param : this.getParameters()) {
      String theme1Name = param.getTheme1();
      ScaleMasterTheme theme1 = this.getScaleMaster().getThemeFromName(
          theme1Name);
      Set<Class<?>> classes = new HashSet<Class<?>>();
      classes.addAll(theme1.getRelatedClasses());
      Class<?> class1 = db.getGeneObjImpl().filterClasses(classes).iterator()
          .next();
      String theme2Name = param.getTheme2();
      ScaleMasterTheme theme2 = this.getScaleMaster().getThemeFromName(
          theme2Name);
      classes.clear();
      classes.addAll(theme2.getRelatedClasses());
      Class<?> class2 = db.getGeneObjImpl().filterClasses(classes).iterator()
          .next();
      MinimumSeparation minSep = new MinimumSeparation(class1, class2,
          (Double) param.getValue());
      this.minSeps.add(minSep);
    }
  }

  @Override
  public void execute(IFeatureCollection<? extends IGeneObj> features)
      throws Exception {
    // first, trigger enrichments
    try {
      Set<Class<? extends IGeneObj>> classes = new HashSet<Class<? extends IGeneObj>>();

      // remove double features in rivers
      classes.clear();
      classes.add(MGCPWaterLine.class);
      DeleteDoublePreProcess processDbl = DeleteDoublePreProcess.getInstance();
      processDbl.setProcessedClasses(classes);
      processDbl.execute(CartAGenDoc.getInstance().getCurrentDataset()
          .getCartAGenDB());

      // make planar and enrich the river network
      classes.clear();
      classes.add(MGCPWaterLine.class);
      MakeNetworkPlanarDir processRiver = MakeNetworkPlanarDir.getInstance();
      processRiver.setProcessedClasses(classes);
      processRiver.execute(CartAGenDoc.getInstance().getCurrentDataset()
          .getCartAGenDB());

      // cut the railroad network in smaller sections
      classes.clear();
      classes.add(MGCPRailwayLine.class);
      CutNetworkPreProcess processCut = CutNetworkPreProcess.getInstance();
      processCut.setProcessedClasses(classes);
      processCut.execute(CartAGenDoc.getInstance().getCurrentDataset()
          .getCartAGenDB());
      // enrich the rail network
      NetworkEnrichment.enrichNetwork(CartAGenDoc.getInstance()
          .getCurrentDataset(), CartAGenDoc.getInstance().getCurrentDataset()
          .getRailwayNetwork(), false);
    } catch (Exception e1) {
      e1.printStackTrace();
    }

    // then, identify conflicts
    Collection<INetworkSection> sections = new HashSet<INetworkSection>();
    for (IGeneObj obj : features)
      if (!obj.isEliminated())
        if (obj instanceof INetworkSection)
          sections.add((INetworkSection) obj);
    Set<OverlapConflict> conflicts = OverlapConflict
        .searchOverlapConflictSimple(sections, this.minSeps);

    // loop to apply least squares on each conflict
    for (OverlapConflict conflict : conflicts) {

      // construction des mapspecs LSA
      Set<String> contraintesMalleables = new HashSet<String>();
      contraintesMalleables.add(LSMovementConstraint.class.getName());
      contraintesMalleables.add(LSCurvatureConstraint.class.getName());
      contraintesMalleables.add(LSMovementDirConstraint.class.getName());
      Set<String> classesMalleables = new HashSet<String>();
      Map<String[], Double> contraintesExternes = new HashMap<String[], Double>();

      for (MinimumSeparation minSep : this.minSeps) {
        classesMalleables.add(minSep.getClass1().getName());
        classesMalleables.add(minSep.getClass2().getName());
        contraintesExternes.put(new String[] {
            LSProximityConstraint.class.getName(),
            minSep.getClass1().getName(), minSep.getClass2().getName() },
            minSep.getMinSep());
      }
      Map<String, Double> poidsContraintes = new HashMap<String, Double>();
      poidsContraintes.put(LSMovementConstraint.class.getName(), 1.0);
      poidsContraintes.put(LSCurvatureConstraint.class.getName(), 6.0);
      poidsContraintes.put(LSMovementDirConstraint.class.getName(), 4.0);
      poidsContraintes.put(LSProximityConstraint.class.getName(), 15.0);

      MapspecsLS mapspecs = new MapspecsLS(this.getScale(),
          new HashSet<String>(), new HashSet<String>(), contraintesMalleables,
          contraintesExternes, new HashSet<String>(), new HashSet<String>(),
          classesMalleables, poidsContraintes);

      // puis on construit un scheduler
      LSScheduler sched = new LSScheduler(mapspecs);
      sched.setObjs(conflict.getSections());
      sched.setSolver(MatrixSolver.JAMA);
      // on lance la généralisation
      sched.triggerAdjustment(false, true);
    }
  }

  @Override
  public String getProcessName() {
    return "DisplacementLSA";
  }

  @Override
  public Set<MultiThemeParameter> getDefaultParameters() {
    // no parameter by default
    return new HashSet<MultiThemeParameter>();
  }

}
