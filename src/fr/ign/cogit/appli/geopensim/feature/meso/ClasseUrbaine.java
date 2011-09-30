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

/**
 * @author Florence Curie
 */
public class ClasseUrbaine {
    public static final int Inconnu = -1;
    public static final int HabitatDiscontinuTypePavillonaireIndividuel = 0;
    public static final int HabitatContinuDenseTypeCentrevilleCentrebourg = 1;
    public static final int HabitatDiscontinuTypeCollectifGrandeEnsemble = 2;
    public static final int EmpriseSpecialiseeBatie = 3;
    public static final int TissuUrbainMixteDense = 4;
    public static final int HabitatDiscontinuMixteDense = 5;
    public static final int HabitatDiscontinuMixtePeuDense = 6;
    public static final int TissuUrbainMixtePeuDense = 7;
    public static final int EmpriseSpecialiseePeuBatie = 8;
    public static final int ReseauCommunication = 9;
    public static final int ReseauHydrographique = 10;

    public static String toString(int numClasse) {
        switch (numClasse) {
            case 0:
                return "HabitatDiscontinuTypePavillonaireIndividuel";
            case 1:
                return "HabitatContinuDenseTypeCentrevilleCentrebourg";
            case 2:
                return "HabitatDiscontinuTypeCollectifGrandeEnsemble";
            case 3:
                return "EmpriseSpecialiseeBatie";
            case 4:
                return "TissuUrbainMixteDense";
            case 5:
                return "HabitatDiscontinuMixteDense";
            case 6:
                return "HabitatDiscontinuMixtePeuDense";
            case 7:
                return "TissuUrbainMixtePeuDense";
            case 8:
                return "EmpriseSpecialiseePeuBatie";
            case 9:
                return "ReseauCommunication";
            case 10:
                return "ReseauHydrographique";
            default:
                return "Inconnu";
        }
    }

    public static int getVal(String nomClasse) {
        if (nomClasse.equals("HabitatDiscontinuTypePavillonaireIndividuel")) {
            return 0;
        }
        if (nomClasse.equals("HabitatContinuDenseTypeCentrevilleCentrebourg")) {
            return 1;
        }
        if (nomClasse.equals("HabitatDiscontinuTypeCollectifGrandeEnsemble")) {
            return 2;
        }
        if (nomClasse.equals("EmpriseSpecialiseeBatie")) {
            return 3;
        }
        if (nomClasse.equals("TissuUrbainMixteDense")) {
            return 4;
        }
        if (nomClasse.equals("HabitatDiscontinuMixteDense")) {
            return 5;
        }
        if (nomClasse.equals("HabitatDiscontinuMixtePeuDense")) {
            return 6;
        }
        if (nomClasse.equals("TissuUrbainMixtePeuDense")) {
            return 7;
        }
        if (nomClasse.equals("EmpriseSpecialiseePeuBatie")) {
            return 8;
        }
        if (nomClasse.equals("ReseauCommunication")) {
            return 9;
        }
        if (nomClasse.equals("ReseauHydrographique")) {
            return 10;
        }
        return -1;
    }

    public static int getValFromSimpleName(String nomClasse) {
        if (nomClasse.equalsIgnoreCase("h_indiv")
                || nomClasse.equalsIgnoreCase("hdtpi")) {
            return 0;
        }
        if (nomClasse.equalsIgnoreCase("h_cont")
                || nomClasse.equalsIgnoreCase("hcdtcc")) {
            return 1;
        }
        if (nomClasse.equalsIgnoreCase("h_coll")
                || nomClasse.equalsIgnoreCase("hdtcge")) {
            return 2;
        }
        if (nomClasse.equalsIgnoreCase("em_spec")
                || nomClasse.equalsIgnoreCase("esb")) {
            return 3;
        }
        if (nomClasse.equalsIgnoreCase("tissu")
                || nomClasse.equalsIgnoreCase("tumd")) {
            return 4;
        }
        if (nomClasse.equalsIgnoreCase("h_mixte")
                || nomClasse.equalsIgnoreCase("hdmd")) {
            return 5;
        }
        if (nomClasse.equalsIgnoreCase("hdmpd")) {
            return 6;
        }
        if (nomClasse.equalsIgnoreCase("tumpd")) {
            return 7;
        }
        if (nomClasse.equalsIgnoreCase("em_pas_b")
                || nomClasse.equalsIgnoreCase("espb")) {
            return 8;
        }
        if (nomClasse.equalsIgnoreCase("rc")) {
            return 9;
        }
        if (nomClasse.equalsIgnoreCase("rh")) {
            return 10;
        }
        return -1;
    }
}
