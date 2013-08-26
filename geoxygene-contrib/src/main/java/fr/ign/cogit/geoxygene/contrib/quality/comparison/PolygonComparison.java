package fr.ign.cogit.geoxygene.contrib.quality.comparison;

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.surfaces.AppariementSurfaces;
import fr.ign.cogit.geoxygene.contrib.appariement.surfaces.ParametresAppSurfaces;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.quality.comparison.measure.Measure;
import fr.ign.cogit.geoxygene.contrib.quality.comparison.parameters.PolygonMatchingParameters;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author JFGirres
 */
public class PolygonComparison extends AbstractComparison<IPolygon> {
  public PolygonComparison(IFeatureCollection<IFeature> jddRef,
      IFeatureCollection<IFeature> jddComp,
      List<Class<? extends Measure>> measures) {
    super(jddRef, jddComp, measures);
  }

  /**
   * Execute the comparison between two polgon datasets
   * @author JFGirres
   */
  @Override
  public void executeComparison() {
    // Appariement automatique
    if (this.isAutomaticMatching()) {
      ParametresAppSurfaces param = PolygonMatchingParameters
          .parametresDefaut();
      FT_FeatureCollection<IFeature> liensACarto = new FT_FeatureCollection<IFeature>();

      EnsembleDeLiens liensPoly = AppariementSurfaces.appariementSurfaces(
          this.getJddComp(), this.getJddRef(), param);

      int nbLiensTraites = 0;

      FT_FeatureCollection<IFeature> polyCompFus = new FT_FeatureCollection<IFeature>();
      FT_FeatureCollection<IFeature> polyRefFus = new FT_FeatureCollection<IFeature>();

      liensACarto.addAll(liensPoly);

      // Traitement préalable sur les objets et les liens
      for (Lien lien : liensPoly) {

        FT_FeatureCollection<IFeature> polyComp = new FT_FeatureCollection<IFeature>();
        FT_FeatureCollection<IFeature> polyRef = new FT_FeatureCollection<IFeature>();

        // recupération des objets pointés par le lien
        List<IFeature> objetsComp = lien.getObjetsRef();
        List<IFeature> objetsRef = lien.getObjetsComp();

        // on récupère les géométries
        for (IFeature feature : objetsComp) {
          polyComp.add(feature);
        }
        for (IFeature feature : objetsRef) {
          polyRef.add(feature);
        }
        Population<IFeature> popObjetsRef = new Population<IFeature>();
        Population<IFeature> popObjetsComp = new Population<IFeature>();

        popObjetsComp.setPersistant(false);
        popObjetsRef.setPersistant(false);

        popObjetsComp.addCollection(polyComp);
        popObjetsRef.addCollection(polyRef);

        Operateurs.fusionneSurfaces(popObjetsComp);
        Operateurs.fusionneSurfaces(popObjetsRef);

        polyCompFus.addAll(popObjetsComp);
        polyRefFus.addAll(popObjetsRef);

        // modif du lien avec les nouveaux objets fusionnés
        lien.setObjetsComp(popObjetsRef.getElements());
        lien.setObjetsRef(popObjetsComp.getElements());

        // on ne s'interesse pas aux appariements vers les noeuds
        if (polyComp.isEmpty() || polyRef.isEmpty()) {
          continue;
        }
        nbLiensTraites++;
      }
      for (Lien lien : liensPoly) {
        GM_Polygon pgRef = (GM_Polygon) lien.getObjetsComp().get(0).getGeom();
        GM_Polygon pgComp = (GM_Polygon) lien.getObjetsRef().get(0).getGeom();
        // choix de la méthode de comparaison
        List<List<IPolygon>> pairs = this.getCuttingMethod().cut(pgRef, pgComp);
        for (List<IPolygon> pair : pairs) {
          this.computeIndicators(pair.get(0), pair.get(1));
          this.fillDatasetsOutputs(pair.get(0), pair.get(1));
        }
      }
    } else {
      FT_FeatureCollection<IFeature> polyCompFus = new FT_FeatureCollection<IFeature>();
      FT_FeatureCollection<IFeature> polyRefFus = new FT_FeatureCollection<IFeature>();

      Population<IFeature> popObjetsRef = new Population<IFeature>();
      Population<IFeature> popObjetsComp = new Population<IFeature>();

      popObjetsComp.setPersistant(false);
      popObjetsRef.setPersistant(false);

      popObjetsComp.addCollection(this.getJddComp());
      popObjetsRef.addCollection(this.getJddRef());

      Operateurs.fusionneSurfaces(popObjetsComp);
      Operateurs.fusionneSurfaces(popObjetsRef);

      polyCompFus.addAll(popObjetsComp);
      polyRefFus.addAll(popObjetsRef);

      GM_Polygon pgRef = (GM_Polygon) polyRefFus.get(0).getGeom();
      GM_Polygon pgComp = (GM_Polygon) polyCompFus.get(0).getGeom();
      // choix de la méthode de comparaison
      List<List<IPolygon>> pairs = this.getCuttingMethod().cut(pgRef, pgComp);
      for (List<IPolygon> pair : pairs) {
        this.computeIndicators(pair.get(0), pair.get(1));
        this.fillDatasetsOutputs(pair.get(0), pair.get(1));
      }
    }
  }
}
