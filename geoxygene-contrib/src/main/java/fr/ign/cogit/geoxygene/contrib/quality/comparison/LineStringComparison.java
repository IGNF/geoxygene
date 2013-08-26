package fr.ign.cogit.geoxygene.contrib.quality.comparison;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.AppariementIO;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.quality.comparison.measure.Measure;
import fr.ign.cogit.geoxygene.contrib.quality.comparison.parameters.LineStringMatchingParameters;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

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
 *            A class to execute the comparison between two polyline datasets
 * 
 * @author JFGirres
 */
public class LineStringComparison extends AbstractComparison<ILineString> {

    public LineStringComparison(IFeatureCollection<IFeature> jddRef, IFeatureCollection<IFeature> jddComp,
            List<Class<? extends Measure>> measures) {
        super(jddRef, jddComp, measures);
    }

    /**
     * Execute the comparison between two polyline datasets
     * @author JFGirres
     */
    @Override
    public void executeComparison() {

        // Appariement automatique
        if (this.isAutomaticMatching()) {

            // Initialisation des parametres
            ParametresApp param = LineStringMatchingParameters.parametresDefaut(this.getJddComp(), this.getJddRef());
            // Seb: pour avoir les liens 1-n entre les arcs directement et non
            // les
            // liens 1-1 entre les objets de d�part
            param.debugBilanSurObjetsGeo = false;

            // Lance les traitement et recupere les liens d'appariement
            List<ReseauApp> cartesTopo = new ArrayList<ReseauApp>();
            EnsembleDeLiens liens = AppariementIO.appariementDeJeuxGeo(param, cartesTopo);

            // Recuperation des liens puis classement en surs, incertains et
            // tres
            // incertains
            List<Double> valeursClassementL = new ArrayList<Double>();
            valeursClassementL.add(new Double(0.5));
            valeursClassementL.add(new Double(1));

            for (Lien lien : liens) {
                // filtrage: on ne garde que les surs
                if (lien.getEvaluation() < 1.0) {
                    continue;
                }

                // recupération des objets pointés par le lien
                List<IFeature> listObjComp = lien.getObjetsRef();
                List<IFeature> listObjRef = lien.getObjetsComp();

                // on ne garde que les arcs
                List<ILineString> listLsComp = new ArrayList<ILineString>();
                for (IFeature ft : listObjComp) {
                    if (ft instanceof Arc) {
                        listLsComp.add((GM_LineString) ft.getGeom());
                    }
                }
                List<ILineString> listLsRef = new ArrayList<ILineString>();
                for (IFeature ft : listObjRef) {
                    if (ft instanceof Arc) {
                        listLsRef.add((GM_LineString) ft.getGeom());
                    }
                }

                // on ne s'interesse pas aux appariements vers les noeuds
                if (listLsComp.size() == 0 || listLsRef.size() == 0) {
                    continue;
                }
                // pretraitement sur les polylignes (union)
                ILineString lsRefFusion = Operateurs.union(listLsRef);
                ILineString lsCompFusion = Operateurs.union(listLsComp);

                // choix de la méthode de comparaison
                List<List<ILineString>> pairs = this.getCuttingMethod().cut(lsRefFusion, lsCompFusion);
                for (List<ILineString> pair : pairs) {
                    this.computeIndicators(pair.get(0), pair.get(1));
                    this.fillDatasetsOutputs(pair.get(0), pair.get(1));
                }
            }
        } else { // Appariement manuel
            // on ne garde que les arcs
            List<ILineString> listLsComp = new ArrayList<ILineString>();
            for (IFeature ft : this.getJddComp()) {
                listLsComp.add((GM_LineString) ft.getGeom());
            }
            List<ILineString> listLsRef = new ArrayList<ILineString>();
            for (IFeature ft : this.getJddRef()) {
                listLsRef.add((GM_LineString) ft.getGeom());
            }

            // pretraitement sur les polylignes (union)
            ILineString lsRefFusion = Operateurs.union(listLsRef);
            ILineString lsCompFusion = Operateurs.union(listLsComp);

            // choix de la méthode de comparaison
            List<List<ILineString>> pairs = this.getCuttingMethod().cut(lsRefFusion, lsCompFusion);
            for (List<ILineString> pair : pairs) {
                this.computeIndicators(pair.get(0), pair.get(1));
                this.fillDatasetsOutputs(pair.get(0), pair.get(1));
            }
        }
    }
}
