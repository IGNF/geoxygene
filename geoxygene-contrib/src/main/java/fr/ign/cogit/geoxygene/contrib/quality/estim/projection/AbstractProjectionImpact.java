package fr.ign.cogit.geoxygene.contrib.quality.estim.projection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

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
 *            Abstract class to compute projection error
 * @author JFGirres
 * 
 */
public abstract class AbstractProjectionImpact {

    static Logger logger = LogManager.getLogger(AbstractProjectionImpact.class.getName());

    private IFeatureCollection<IFeature> jddGrilleAlteration;

    public void setJddGrilleAlteration(IFeatureCollection<IFeature> jddGrilleAlteration) {
        this.jddGrilleAlteration = jddGrilleAlteration;
    }

    public IFeatureCollection<IFeature> getJddGrilleAlteration() {
        return jddGrilleAlteration;
    }

    private IFeatureCollection<IFeature> jddAEvaluer;

    public void setJddAEvaluer(IFeatureCollection<IFeature> jddAEvaluer) {
        this.jddAEvaluer = jddAEvaluer;
    }

    public IFeatureCollection<IFeature> getJddAEvaluer() {
        return jddAEvaluer;
    }

    private IFeatureCollection<IFeature> jddPtsAlteration = new FT_FeatureCollection<IFeature>();

    public void setJddPtsAlteration(FT_FeatureCollection<IFeature> jddPtsAlteration) {
        this.jddPtsAlteration = jddPtsAlteration;
    }

    public IFeatureCollection<IFeature> getJddPtsAlteration() {
        return jddPtsAlteration;
    }

    public AbstractProjectionImpact() {
    }

    /**
     * Compute linestring length according to alterations involved by projection
     * system
     * @param lineStringAEvaluer
     * @return
     */
    public double computeCorrectedLength(ILineString lineStringAEvaluer) {
        double correctedLength = 0;
        double initialLength = 0;

        // Code pour créer un schéma afin de récupérer les attributs dans Jump
        SchemaDefaultFeature schemaDefaultFeature = new SchemaDefaultFeature();
        /** créer un featuretype de jeu correspondant */
        fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType newFeatureType = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType();
        Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>();
        AttributeType type = new AttributeType();
        String nomField = "AltLin";
        String memberName = "AltLin";
        String valueType = "Double";
        type.setNomField(nomField);
        type.setMemberName(memberName);
        type.setValueType(valueType);
        newFeatureType.addFeatureAttribute(type);
        attLookup.put(new Integer(0), new String[] { nomField, memberName });
        /** création d'un schéma associé au featureType */
        newFeatureType.setGeometryType(GM_Point.class);
        schemaDefaultFeature.setFeatureType(newFeatureType);
        newFeatureType.setSchema(schemaDefaultFeature);
        schemaDefaultFeature.setAttLookup(attLookup);
        jddPtsAlteration.setFeatureType(newFeatureType);

        for (int i = 0; i < lineStringAEvaluer.coord().size() - 1; i++) {

            // calcul du module linéaire en chaque point
            IDirectPosition dp1 = lineStringAEvaluer.coord().get(i);
            double altLinDp1 = computeAlterationLineaire(dp1, jddGrilleAlteration);
            // System.out.println("Module lineaire dp1 = " + modLinDp1);

            DefaultFeature dfFeature = new DefaultFeature(dp1.toGM_Point());

            // System.out.println("Point n°" + i + " - AltLin = " + altLinDp1);

            dfFeature.setFeatureType(schemaDefaultFeature.getFeatureType());
            dfFeature.setSchema(schemaDefaultFeature);
            dfFeature.setAttributes(new Object[] { altLinDp1 });

            jddPtsAlteration.add(dfFeature);

            IDirectPosition dp2 = lineStringAEvaluer.coord().get(i + 1);
            double altLinDp2 = computeAlterationLineaire(dp2, jddGrilleAlteration);

            // Calcul de la longueur corrigée pour chaque segment de la
            // linestring
            double length12 = Distances.distance2D(dp1, dp2); // dp1.distance2D(dp2);

            double altLinMedian = ((altLinDp1 + altLinDp2) / 2);

            double modLinMedian = 1 + (altLinMedian / 1000000);

            // System.out.println("Longueur = " + length12 +
            // " -- Module Lineaire = "
            // + modLinMedian);

            double correctedLength12 = length12 / modLinMedian;
            initialLength = initialLength + length12;
            correctedLength = correctedLength + correctedLength12;
        }

        logger.info("LINESTRING -- " + "Initial Length = " + initialLength + "|| Corrected length = " + correctedLength
                + "---> Error = " + (correctedLength - initialLength));

        return correctedLength;
    }

    /**
     * Compute linestring area according to alterations involved by projection
     * system
     * @param polyAEvaluer
     * @return
     */
    public double computeCorrectedArea(IPolygon polyAEvaluer) {

        IDirectPosition dpCentroid = polyAEvaluer.centroid();
        double modLinDpCentroid = computeAlterationLineaire(dpCentroid, jddGrilleAlteration);
        modLinDpCentroid = modLinDpCentroid / 1000000;
        double initialArea = polyAEvaluer.area();
        double correctedArea = initialArea / (1 + (2 * modLinDpCentroid) + (modLinDpCentroid * modLinDpCentroid));
        logger.info("POLYGON -- " + "Initial Area = " + initialArea + "|| Corrected Area = " + correctedArea
                + "---> Error = " + (correctedArea - initialArea));

        return correctedArea;
    }

    /**
     * Compute Linear Module for a point according to an existing grid
     * @param dp
     * @param grilleAlteration
     * @return
     */
    public double computeAlterationLineaire(final IDirectPosition dp, IFeatureCollection<IFeature> grilleAlteration) {

        // Calculer la valeur d'altération linéaire en chaque point
        IPolygon polyBuffer = (IPolygon) dp.toGM_Point().buffer(6000);
        Collection<IFeature> jddGrilleSelect = jddGrilleAlteration.select(polyBuffer);
        ArrayList<IFeature> sortedList = new ArrayList<IFeature>(jddGrilleSelect);
        Collections.sort(sortedList, new Comparator<IFeature>() {
            @Override
            public int compare(IFeature o1, IFeature o2) {
                double d1 = dp.distance(o1.getGeom().centroid());
                double d2 = dp.distance(o2.getGeom().centroid());
                return Double.compare(d1, d2);
            }
        });

        int numberOfFeatures = 4;
        int weight = 1;
        String attributeName = "AltLin";
        double value = inverseDistanceWeightedAverage(dp, sortedList, numberOfFeatures, weight, attributeName);
        return value;
    }

    /**
     * Interpolate IDWA
     * @param dp
     * @param jdd
     * @param numberOfFeatures
     * @param weight
     * @param attributeName
     * @return
     */
    private double inverseDistanceWeightedAverage(IDirectPosition dp, Collection<IFeature> jdd, int numberOfFeatures,
            int weight, String attributeName) {
        double numerator = 0;
        double denominator = 0;
        int i = 0;
        for (Iterator<IFeature> it = jdd.iterator(); it.hasNext() && i < numberOfFeatures; i++) {
            IFeature feature = it.next();
            double distance = dp.distance(feature.getGeom().centroid());
            double weightedDistance = Math.pow(distance, -weight);
            denominator += weightedDistance;
            numerator += weightedDistance * ((Double) feature.getAttribute(attributeName)).doubleValue();
        }
        return numerator / denominator;
    }

}
