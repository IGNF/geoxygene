/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.appli.geopensim.feature.meso;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;

/**
 * Cette classification des zones élémentaires est basée sur le travail de
 * Julien Lesbegueries.
 *
 * @author Florence Curie
 */
public abstract class ClassificationZoneElementaire {
    static Logger logger = Logger.getLogger(ClassificationZoneElementaire.class
            .getName());
    public static int classifierZoneElementaire(ZoneElementaireUrbaine zone) {
        if ((zone.densite > 0.031106) && (zone.aire <= 98662.025991)) {
            return classeBTree(zone);
        }
        if (zone.densite <= 0.031106) {
            return classeATree(zone);
        }
        if ((zone.homogeneiteTailleBatiments <= 2)
                && (zone.homogeneiteTailleBatiments > 0)) {
            return classeATree(zone);
        }
        return classeBTree(zone);
    }

    public static int classeATree(ZoneElementaireUrbaine zone) {
        // espb
        if (zone.nombreBatiments > 0) {
            return ClasseUrbaine.EmpriseSpecialiseePeuBatie;
        }
        // rc
        if (zone.elongation <= 0.121907) {
            return ClasseUrbaine.ReseauCommunication;
        }
        // espb
        if (zone.aire > 926.259858) {
            return ClasseUrbaine.EmpriseSpecialiseePeuBatie;
        }
        // rc
        return ClasseUrbaine.ReseauCommunication;
    }

    public static int classeBTree(ZoneElementaireUrbaine zone) {
        // hdtpi
        if (zone.moyenneAiresBatiments <= 171.629524) {
            return ClasseUrbaine.HabitatDiscontinuTypePavillonaireIndividuel;
        }
        // hcdtcc
        if ((zone.moyenneAiresBatiments > 656.142315)
                && (zone.densite > 0.406492) && (zone.aire <= 41140.984891)
                && (zone.ecartTypeAiresBatiments <= 2986.567836)) {
            return ClasseUrbaine.HabitatContinuDenseTypeCentrevilleCentrebourg;
        }
        // hdtcge
        if ((zone.medianeAiresBatiments > 328.365)
                && (zone.maxAiresBatiments <= 2679.126326)
                && (zone.densite <= 0.37018) && (zone.nombreBatiments <= 14)) {
            return ClasseUrbaine.HabitatDiscontinuTypeCollectifGrandeEnsemble;
        }
        // esb
        if ((zone.moyenneConvexiteBatiments > 0.701878)
                && (zone.moyenneAiresBatiments > 1767.543773)) {
            return ClasseUrbaine.EmpriseSpecialiseeBatie;
        }
        // tumd
        if ((zone.densite > 0.287589) && (zone.minAiresBatiments <= 211.755)
                && (zone.moyenneAiresBatiments > 784.078571)
                && (zone.aire <= 72194.020039)) {
            return ClasseUrbaine.TissuUrbainMixteDense;
        }
        // tumd
        if ((zone.densite > 0.287589)
                && (zone.moyenneAiresBatiments <= 764.976028)
                && (zone.nombreBatiments > 15)
                && (zone.medianeAiresBatiments <= 148.744952)) {
            return ClasseUrbaine.TissuUrbainMixteDense;
        }
        // hdmd
        if ((zone.densite > 0.307257)
                && (zone.moyenneAiresBatiments <= 764.976028)
                && (zone.medianeAiresBatiments <= 194.514638)) {
            return ClasseUrbaine.HabitatDiscontinuMixteDense;
        }
        // tumd
        if ((zone.densite > 0.307592)
                && (zone.moyenneAiresBatiments <= 1008.0875)
                && (zone.nombreBatiments > 15)) {
            return ClasseUrbaine.TissuUrbainMixteDense;
        }
        // hcdtcc
        if ((zone.densite > 0.313162) && (zone.nombreBatiments <= 15)
                && (zone.moyenneConvexiteBatiments <= 0.916188)
                && (zone.medianeAiresBatiments <= 1526.9)
                && (zone.ecartTypeElongationBatiments <= 0.186925)) {
            return ClasseUrbaine.HabitatContinuDenseTypeCentrevilleCentrebourg;
        }
        // hdmd
        if ((zone.densite > 0.313162) && (zone.maxAiresBatiments <= 1412.87766)) {
            return ClasseUrbaine.HabitatDiscontinuMixteDense;
        }
        // tumd
        if ((zone.densite > 0.314158)
                && (zone.moyenneConvexiteBatiments > 0.850118)
                && (zone.ecartTypeConvexiteBatiments <= 0.105648)) {
            return ClasseUrbaine.TissuUrbainMixteDense;
        }
        // hcdtcc
        if ((zone.densite > 0.315033) && (zone.aire <= 25147.195)) {
            return ClasseUrbaine.HabitatContinuDenseTypeCentrevilleCentrebourg;
        }
        // esb
        if ((zone.moyenneElongationBatiments > 0.411677)
                && (zone.moyenneAiresBatiments > 847.803)) {
            return ClasseUrbaine.EmpriseSpecialiseeBatie;
        }
        // hdmpd
        if ((zone.moyenneElongationBatiments > 0.411677)
                && (zone.ecartTypeAiresBatiments <= 280.188765)
                && (zone.moyenneConvexiteBatiments > 0.922103)
                && (zone.minAiresBatiments > 44.495)) {
            return ClasseUrbaine.HabitatDiscontinuMixtePeuDense;
        }
        // tumd
        if ((zone.maxAiresBatiments > 1642.005) && (zone.densite > 0.264232)
                && (zone.medianeAiresBatiments <= 301.958713)
                && (zone.ecartTypeAiresBatiments <= 650.0675)) {
            return ClasseUrbaine.TissuUrbainMixteDense;
        }
        // tumpd
        if ((zone.maxAiresBatiments > 1642.005)
                && (zone.moyenneElongationBatiments > 0.560503)
                && (zone.ecartTypeConvexiteBatiments <= 0.108707)) {
            return ClasseUrbaine.TissuUrbainMixtePeuDense;
        }
        // hdtcge
        if (zone.moyenneElongationBatiments <= 0.411677) {
            return ClasseUrbaine.HabitatDiscontinuTypeCollectifGrandeEnsemble;
        }
        // hdmd
        if ((zone.ecartTypeAiresBatiments <= 175.610268)
                && (zone.moyenneElongationBatiments <= 0.620362)) {
            return ClasseUrbaine.HabitatDiscontinuMixteDense;
        }
        // tumd
        if ((zone.ecartTypeAiresBatiments > 168.188745)
                && (zone.moyenneAiresBatiments > 355.018571)
                && (zone.aire > 52090.55) && (zone.elongation <= 0.638917)) {
            return ClasseUrbaine.TissuUrbainMixteDense;
        }
        // tumpd
        if ((zone.ecartTypeAiresBatiments > 168.188745)
                && (zone.moyenneAiresBatiments > 355.018571)) {
            return ClasseUrbaine.TissuUrbainMixtePeuDense;
        }
        // hdmd
        if ((zone.ecartTypeAiresBatiments > 168.188745)
                && (zone.moyenneConvexiteBatiments <= 0.927048)
                && (zone.minAiresBatiments > 55.525)) {
            return ClasseUrbaine.HabitatDiscontinuMixteDense;
        }
        // hdmd
        if ((zone.ecartTypeAiresBatiments > 168.188745)
                && (zone.densite > 0.208801) && (zone.convexite <= 0.953749)) {
            return ClasseUrbaine.HabitatDiscontinuMixteDense;
        }
        // hdmpd
        if ((zone.ecartTypeAiresBatiments > 168.188745)
                && (zone.moyenneConvexiteBatiments > 0.898589)
                && (zone.moyenneAiresBatiments > 217.008825)
                && (zone.ecartTypeConvexiteBatiments <= 0.118533)) {
            return ClasseUrbaine.HabitatDiscontinuMixtePeuDense;
        }
        // hdmpd
        if (zone.moyenneConvexiteBatiments <= 0.900746) {
            return ClasseUrbaine.HabitatDiscontinuMixtePeuDense;
        }
        // tumpd
        if (zone.minAiresBatiments <= 51.517935) {
            return ClasseUrbaine.TissuUrbainMixtePeuDense;
        }
        // hdtpi
        return ClasseUrbaine.HabitatDiscontinuTypePavillonaireIndividuel;
    }
    public static int classifierZoneElementaireIJDMM(ZoneElementaireUrbaine zone) {
        if (zone.getDensite() <= 0.045) {
            return ClasseUrbaine.EmpriseSpecialiseePeuBatie;
        }
        if (containsAtLeastQuantileLessThanOrEqualTo(zone, "aire", 8, 20, 200.0)) {
            if (containsAtLeastQuantileLessThanOrEqualTo(zone, "aire", 18, 20, 443.0)) {
                if (containsAtLeastQuantileLessThanOrEqualTo(zone, "elongation", 9, 20, 0.6901)) {
                    return ClasseUrbaine.HabitatDiscontinuMixteDense;
                }
                if (containsAtLeastQuantileLessThanOrEqualTo(zone, "aire", 18, 20, 299.4)) {
                    return ClasseUrbaine.HabitatDiscontinuTypePavillonaireIndividuel;
                }
                if (zone.getElongation() <= 0.505) {
                    return ClasseUrbaine.HabitatDiscontinuTypePavillonaireIndividuel;
                }
                return ClasseUrbaine.HabitatDiscontinuMixteDense;
            }
            if (zone.getAire() <= 25524) {
                return ClasseUrbaine.HabitatDiscontinuMixteDense;
            }
            return ClasseUrbaine.TissuUrbainMixteDense;
        }
        if (containsAtLeastQuantileLessThanOrEqualTo(zone, "aire", 19, 20, 1639.0)) {
            if (containsAtLeastQuantileLessThanOrEqualTo(zone, "aire", 2, 20, 161.8)) {
                return ClasseUrbaine.HabitatDiscontinuMixteDense;
            }
            return ClasseUrbaine.HabitatDiscontinuTypeCollectifGrandeEnsemble;
        }
        if (zone.getAire() <= 16831) {
            if (containsAtLeastQuantileLessThanOrEqualTo(zone, "aire", 1, 20, 918.2)) {
                return ClasseUrbaine.HabitatDiscontinuTypeCollectifGrandeEnsemble;
            }
            return ClasseUrbaine.EmpriseSpecialiseeBatie;
        }
        return ClasseUrbaine.EmpriseSpecialiseeBatie;
    }
    public static boolean containsAtLeastQuantileLessThanOrEqualTo(ZoneElementaireUrbaine zone, String attribute, int k, int q, double value) {
        int n = 0;
        for (Batiment b : zone.getBatiments()) {
          System.out.println(attribute);
            if (((Number) b.getAttribute(attribute)).doubleValue() <= value) {
                n++;
            }
        }
        return n >= k * zone.getBatiments().size() / q;
    }
}
