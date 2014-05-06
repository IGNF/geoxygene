package fr.ign.cogit.geoxygene.schemageo.schema.exnathalie;
/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at providing an open framework which implements OGC/ISO specifications for the development and deployment of geographic (GIS) applications. It is a open source contribution of the COGIT laboratory at the Institut Géographique National (the French National Mapping Agency). See: http://oxygene-project.sourceforge.net 
 * Copyright (C) 2005 Institut Géographique National 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or any later version. This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser General Public License along with this library (see file LICENSE if present); if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.type.FC_FeatureAttributeValue;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.SchemaConceptuelProduit;

public class SchemaBDCarto3Nat {

  /**
   * Crée le schéma de la BDCarto.
   * @return le schéma de produit de la BDCarto selon le modèle ISO
   */
  public static SchemaConceptuelProduit creeSchemaBDCarto3() {

    /***************************************************************************
     * Creation du catalogue de la base de données *
     ************************************************************************/
    SchemaConceptuelProduit sProduit = new SchemaConceptuelProduit();
    sProduit.setNomSchema("Catalogue BDCARTO");
    sProduit.setBD("BDCARTO V3");
    sProduit.setTagBD(1);
    sProduit.setDate("juin 2005");
    sProduit.setVersion("3");
    sProduit.setSource("Photogrammétrie");
    sProduit.setSujet("Composante topographique du RGE");

    /***************************************************************************
     * Ajout du thème "Bâtiments, sites touristiques" *
     ************************************************************************/

    // Classe Zone d'habitat///////////////////////////////////////////////////

    sProduit.createFeatureType("Zone d'habitat");
    FeatureType zoneDHabitat = (FeatureType) (sProduit
        .getFeatureTypeByName("Zone d'habitat"));
    zoneDHabitat.setDefinition("");
    zoneDHabitat.setIsAbstract(false);
    // Attribut Importance
    sProduit.createFeatureAttribute(zoneDHabitat, "Importance", "string", true);
    AttributeType importance1 = zoneDHabitat
        .getFeatureAttributeByName("Importance");
    importance1.setDefinition("");
    sProduit.createFeatureAttributeValue(importance1, "Sans objet");
    sProduit.createFeatureAttributeValue(importance1, "Chef-lieu de commune");
    sProduit.createFeatureAttributeValue(importance1, "Quartier de ville");
    sProduit.createFeatureAttributeValue(importance1, "Hameau");

    // Attribut Présence d'un office de tourisme
    sProduit.createFeatureAttribute(zoneDHabitat,
        "Présence d'un office de tourisme", "string", true);
    AttributeType presenceDUnOfficeDeTourisme = zoneDHabitat
        .getFeatureAttributeByName("Présence d'un office de tourisme");
    presenceDUnOfficeDeTourisme.setDefinition("");
    sProduit.createFeatureAttributeValue(presenceDUnOfficeDeTourisme,
        "Sans objet");
    sProduit.createFeatureAttributeValue(presenceDUnOfficeDeTourisme, "Oui");
    sProduit.createFeatureAttributeValue(presenceDUnOfficeDeTourisme, "Non");

    // Attribut Activités culturelles
    sProduit.createFeatureAttribute(zoneDHabitat, "Activités culturelles",
        "string", true);
    AttributeType activitesCulturelles = zoneDHabitat
        .getFeatureAttributeByName("Activités culturelles");
    activitesCulturelles.setDefinition("");
    sProduit.createFeatureAttributeValue(activitesCulturelles, "Sans objet");
    sProduit.createFeatureAttributeValue(activitesCulturelles, "Pas de musée");
    sProduit.createFeatureAttributeValue(activitesCulturelles,
        "Présence d'un ou plusieurs musées hors ville d'art");
    sProduit.createFeatureAttributeValue(activitesCulturelles, "Ville d'art");

    // Attribut Station thermale
    sProduit.createFeatureAttribute(zoneDHabitat, "Station thermale", "string",
        true);
    AttributeType stationThermale = zoneDHabitat
        .getFeatureAttributeByName("Station thermale");
    stationThermale.setDefinition("");
    sProduit.createFeatureAttributeValue(stationThermale, "Sans objet");
    sProduit.createFeatureAttributeValue(stationThermale, "Oui");
    sProduit.createFeatureAttributeValue(stationThermale, "Non");

    // Attribut Station verte
    sProduit.createFeatureAttribute(zoneDHabitat, "Station verte", "string",
        true);
    AttributeType stationVerte = zoneDHabitat
        .getFeatureAttributeByName("Station verte");
    stationVerte.setDefinition("");
    sProduit.createFeatureAttributeValue(stationVerte, "Sans objet");
    sProduit.createFeatureAttributeValue(stationVerte, "Oui");
    sProduit.createFeatureAttributeValue(stationVerte, "Non");

    // Attribut Station balnéaire
    sProduit.createFeatureAttribute(zoneDHabitat, "Station balnéaire",
        "string", true);
    AttributeType stationBalneaire = zoneDHabitat
        .getFeatureAttributeByName("Station balnéaire");
    stationBalneaire.setDefinition("");
    sProduit.createFeatureAttributeValue(stationBalneaire, "Sans objet");
    sProduit.createFeatureAttributeValue(stationBalneaire, "Oui");
    sProduit.createFeatureAttributeValue(stationBalneaire, "Non");

    // Attribut Station de sports d'hiver
    sProduit.createFeatureAttribute(zoneDHabitat, "Station de sports d'hiver",
        "string", true);
    AttributeType stationDeSportsDHiver = zoneDHabitat
        .getFeatureAttributeByName("Station de sports d'hiver");
    stationDeSportsDHiver.setDefinition("");
    sProduit.createFeatureAttributeValue(stationDeSportsDHiver, "Sans objet");
    sProduit.createFeatureAttributeValue(stationDeSportsDHiver, "Ski alpin");
    sProduit.createFeatureAttributeValue(stationDeSportsDHiver, "Ski de fond");
    sProduit.createFeatureAttributeValue(stationDeSportsDHiver,
        "Ski alpin et ski de fond");
    sProduit.createFeatureAttributeValue(stationDeSportsDHiver, "Non");

    // Attribut Patrimoine architectural
    sProduit.createFeatureAttribute(zoneDHabitat, "Patrimoine architectural",
        "string", true);
    AttributeType patrimoineArchitectural = zoneDHabitat
        .getFeatureAttributeByName("Patrimoine architectural");
    patrimoineArchitectural.setDefinition("");
    sProduit.createFeatureAttributeValue(patrimoineArchitectural, "Sans objet");
    sProduit.createFeatureAttributeValue(patrimoineArchitectural, "Oui");
    sProduit.createFeatureAttributeValue(patrimoineArchitectural, "Non");

    // Attribut Importance touristique
    sProduit.createFeatureAttribute(zoneDHabitat, "Importance touristique",
        "string", true);
    AttributeType importanceTouristique = zoneDHabitat
        .getFeatureAttributeByName("Importance touristique");
    importanceTouristique.setDefinition("");
    sProduit.createFeatureAttributeValue(importanceTouristique, "Sans objet");
    sProduit.createFeatureAttributeValue(importanceTouristique,
        "Sans interêt touristique");
    sProduit.createFeatureAttributeValue(importanceTouristique,
        "Importance locale");
    sProduit.createFeatureAttributeValue(importanceTouristique,
        "Importance régionale");
    sProduit.createFeatureAttributeValue(importanceTouristique,
        "Importance nationale");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(zoneDHabitat, "Toponyme", "string", false);
    AttributeType toponyme = zoneDHabitat.getFeatureAttributeByName("Toponyme");
    toponyme.setDefinition("");

    // Attribut Classification
    sProduit.createFeatureAttribute(zoneDHabitat, "Classification", "string",
        true);
    AttributeType classification = zoneDHabitat
        .getFeatureAttributeByName("Classification");
    classification.setDefinition("");
    sProduit.createFeatureAttributeValue(classification, "Plus de 99950 hab");
    sProduit
        .createFeatureAttributeValue(classification, "De 24950 à 99949 hab");
    sProduit.createFeatureAttributeValue(classification, "De 4950 à 24949 hab");
    sProduit.createFeatureAttributeValue(classification, "De 950 à 4949 hab");
    sProduit.createFeatureAttributeValue(classification, "De 175 à 949 hab");
    sProduit.createFeatureAttributeValue(classification,
        "Moins de 174hab quartiers de plus de 20 bâtiments");
    sProduit.createFeatureAttributeValue(classification,
        "Groupes d'habitations de 4 à 10 feux ou quartier de 8 à 20 bâtiments");
    sProduit.createFeatureAttributeValue(classification,
        "Groupes d'habitations de 2 à 3 feux ou quartier de 4 à 7 bâtiments");

    // Classe Zone d'activités 

    sProduit.createFeatureType("Zone d'activité");
    FeatureType zoneDActivites = (FeatureType) (sProduit
        .getFeatureTypeByName("Zone d'activité"));
    zoneDActivites.setDefinition("");
    zoneDActivites.setIsAbstract(false);
    // Attribut Toponyme
    sProduit
        .createFeatureAttribute(zoneDActivites, "Toponyme", "string", false);
    AttributeType toponyme2 = zoneDActivites
        .getFeatureAttributeByName("Toponyme");
    toponyme2.setDefinition("");

    // Classe Etablissement administratif ou
    // public///////////////////////////////////////////////////

    sProduit.createFeatureType("Etablissement administratif ou public");
    FeatureType etablissementAdministratifOuPublic = (FeatureType) (sProduit
        .getFeatureTypeByName("Etablissement administratif ou public"));
    etablissementAdministratifOuPublic.setDefinition("");
    etablissementAdministratifOuPublic.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(etablissementAdministratifOuPublic,
        "Nature", "string", true);
    AttributeType nature1 = etablissementAdministratifOuPublic
        .getFeatureAttributeByName("Nature");
    nature1.setDefinition("");
    sProduit.createFeatureAttributeValue(nature1, "Préfecture");
    sProduit.createFeatureAttributeValue(nature1, "Hôtel départemental");
    sProduit.createFeatureAttributeValue(nature1, "Hôtel régional");
    sProduit.createFeatureAttributeValue(nature1, "Hôpital");
    sProduit
        .createFeatureAttributeValue(nature1, "Etablisement d'enseignement");
    sProduit.createFeatureAttributeValue(nature1, "Aérogare");
    sProduit.createFeatureAttributeValue(nature1, "Office du tourisme");
    sProduit.createFeatureAttributeValue(nature1, "Mairie");
    sProduit.createFeatureAttributeValue(nature1, "Palais de justice");

    // Attribut Monument historique
    sProduit.createFeatureAttribute(etablissementAdministratifOuPublic,
        "Monument historique", "string", true);
    AttributeType monumentHistorique = etablissementAdministratifOuPublic
        .getFeatureAttributeByName("Monument historique");
    monumentHistorique.setDefinition("");
    sProduit.createFeatureAttributeValue(monumentHistorique, "Non classé");
    sProduit.createFeatureAttributeValue(monumentHistorique, "Inscrit");
    sProduit.createFeatureAttributeValue(monumentHistorique, "Classé");

    // Attribut Présence d'un musée
    sProduit.createFeatureAttribute(etablissementAdministratifOuPublic,
        "Présence d'un musée", "string", true);
    AttributeType presenceDUnMusee = etablissementAdministratifOuPublic
        .getFeatureAttributeByName("Présence d'un musée");
    presenceDUnMusee.setDefinition("");
    sProduit.createFeatureAttributeValue(presenceDUnMusee, "Oui");
    sProduit.createFeatureAttributeValue(presenceDUnMusee, "Non");

    // Attribut Importance touristique
    sProduit.createFeatureAttribute(etablissementAdministratifOuPublic,
        "Importance touristique", "string", true);
    AttributeType importanceTouristique2 = etablissementAdministratifOuPublic
        .getFeatureAttributeByName("Importance touristique");
    importanceTouristique2.setDefinition("");
    sProduit.createFeatureAttributeValue(importanceTouristique2, "Sans objet");
    sProduit.createFeatureAttributeValue(importanceTouristique2,
        "Sans interêt touristique");
    sProduit.createFeatureAttributeValue(importanceTouristique2,
        "Importance locale");
    sProduit.createFeatureAttributeValue(importanceTouristique2,
        "Importance régionale");
    sProduit.createFeatureAttributeValue(importanceTouristique2,
        "Importance nationale");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(etablissementAdministratifOuPublic,
        "Toponyme", "string", false);
    AttributeType toponyme3 = etablissementAdministratifOuPublic
        .getFeatureAttributeByName("Toponyme");
    toponyme3.setDefinition("");

    // Classe Bâtiments remarquables à vocation relgieuse ou touristique

    sProduit
        .createFeatureType("Bâtiments remarquables à vocation relgieuse ou touristique");
    FeatureType batimentsRemarquablesAVocationRelgieuseOuTouristique = (FeatureType) (sProduit
        .getFeatureTypeByName("Bâtiments remarquables à vocation relgieuse ou touristique"));
    batimentsRemarquablesAVocationRelgieuseOuTouristique.setDefinition("");
    batimentsRemarquablesAVocationRelgieuseOuTouristique.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(
        batimentsRemarquablesAVocationRelgieuseOuTouristique, "Nature",
        "string", true);
    AttributeType nature2 = batimentsRemarquablesAVocationRelgieuseOuTouristique
        .getFeatureAttributeByName("Nature");
    nature2.setDefinition("");
    sProduit.createFeatureAttributeValue(nature2, "Edifice religieux chrétien");
    sProduit.createFeatureAttributeValue(nature2, "Mosquée");
    sProduit.createFeatureAttributeValue(nature2, "Synagogue");
    sProduit.createFeatureAttributeValue(nature2,
        "Edifice religieux d'autre confession");
    sProduit.createFeatureAttributeValue(nature2, "Phare");
    sProduit.createFeatureAttributeValue(nature2, "Château");
    sProduit.createFeatureAttributeValue(nature2, "Musée");
    sProduit.createFeatureAttributeValue(nature2,
        "Autre bâtiment d'importance touristique");
    sProduit.createFeatureAttributeValue(nature2, "Maison du parc");

    // Attribut Orientation
    sProduit.createFeatureAttribute(
        batimentsRemarquablesAVocationRelgieuseOuTouristique, "Orientation",
        "string", false);
    AttributeType orientation = batimentsRemarquablesAVocationRelgieuseOuTouristique
        .getFeatureAttributeByName("Orientation");
    orientation.setDefinition("");

    // Attribut Importance touristique
    sProduit.createFeatureAttribute(
        batimentsRemarquablesAVocationRelgieuseOuTouristique,
        "Importance touristique", "string", true);
    AttributeType importanceTouristique3 = batimentsRemarquablesAVocationRelgieuseOuTouristique
        .getFeatureAttributeByName("Importance touristique");
    importanceTouristique3.setDefinition("");
    sProduit.createFeatureAttributeValue(importanceTouristique3, "Sans objet");
    sProduit.createFeatureAttributeValue(importanceTouristique3,
        "Sans interêt touristique");
    sProduit.createFeatureAttributeValue(importanceTouristique3,
        "Importance locale");
    sProduit.createFeatureAttributeValue(importanceTouristique3,
        "Importance régionale");
    sProduit.createFeatureAttributeValue(importanceTouristique3,
        "Importance nationale");

    // Attribut Monument historique
    sProduit.createFeatureAttribute(
        batimentsRemarquablesAVocationRelgieuseOuTouristique,
        "Monument historique", "string", true);
    AttributeType monumentHistorique1 = batimentsRemarquablesAVocationRelgieuseOuTouristique
        .getFeatureAttributeByName("Monument historique");
    monumentHistorique1.setDefinition("");
    sProduit.createFeatureAttributeValue(monumentHistorique1, "Non classé");
    sProduit.createFeatureAttributeValue(monumentHistorique1, "Inscrit");
    sProduit.createFeatureAttributeValue(monumentHistorique1, "Classé");

    // Attribut Accès
    sProduit.createFeatureAttribute(
        batimentsRemarquablesAVocationRelgieuseOuTouristique, "Accès",
        "string", true);
    AttributeType acces = batimentsRemarquablesAVocationRelgieuseOuTouristique
        .getFeatureAttributeByName("Accès");
    acces.setDefinition("");
    sProduit.createFeatureAttributeValue(acces, "Sans objet");
    sProduit.createFeatureAttributeValue(acces, "Ouvert au public");
    sProduit.createFeatureAttributeValue(acces, "Non ouvert au public");

    // Attribut Présence d'un musée
    sProduit.createFeatureAttribute(
        batimentsRemarquablesAVocationRelgieuseOuTouristique,
        "Présence d'un musée", "string", true);
    AttributeType presenceDUnMusee1 = batimentsRemarquablesAVocationRelgieuseOuTouristique
        .getFeatureAttributeByName("Présence d'un musée");
    presenceDUnMusee1.setDefinition("");
    sProduit.createFeatureAttributeValue(presenceDUnMusee1, "Oui");
    sProduit.createFeatureAttributeValue(presenceDUnMusee1, "Non");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(
        batimentsRemarquablesAVocationRelgieuseOuTouristique, "Toponyme",
        "string", false);
    AttributeType toponyme4 = batimentsRemarquablesAVocationRelgieuseOuTouristique
        .getFeatureAttributeByName("Toponyme");
    toponyme4.setDefinition("");

    // Classe Refuge gardé, gîte d'étape

    sProduit.createFeatureType("Refuge gardé, gîte d'étape");
    FeatureType refugeGardeGiteDEtape = (FeatureType) (sProduit
        .getFeatureTypeByName("Refuge gardé, gîte d'étape"));
    refugeGardeGiteDEtape.setDefinition("");
    refugeGardeGiteDEtape.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(refugeGardeGiteDEtape, "Nature", "string",
        true);
    AttributeType nature3 = refugeGardeGiteDEtape
        .getFeatureAttributeByName("Nature");
    nature3.setDefinition("");
    sProduit.createFeatureAttributeValue(nature3, "Refuge");
    sProduit.createFeatureAttributeValue(nature3, "Gîte d'étape");
    sProduit.createFeatureAttributeValue(nature3, "Hôtel de montagne");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(refugeGardeGiteDEtape, "Toponyme",
        "string", false);
    AttributeType toponyme5 = refugeGardeGiteDEtape
        .getFeatureAttributeByName("Toponyme");
    toponyme5.setDefinition("");

    // Classe Equipement de loisirs

    sProduit.createFeatureType("Equipement de loisirs");
    FeatureType equipementDeLoisirs = (FeatureType) (sProduit
        .getFeatureTypeByName("Equipement de loisirs"));
    equipementDeLoisirs.setDefinition("");
    equipementDeLoisirs.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(equipementDeLoisirs, "Nature", "string",
        true);
    AttributeType nature4 = equipementDeLoisirs
        .getFeatureAttributeByName("Nature");
    nature2.setDefinition("");
    sProduit.createFeatureAttributeValue(nature4, "Parc de loisirs");
    sProduit.createFeatureAttributeValue(nature4, "Terrain de golf");
    sProduit.createFeatureAttributeValue(nature4, "Parc ou jardin");
    sProduit.createFeatureAttributeValue(nature4, "Stade");
    sProduit.createFeatureAttributeValue(nature4, "Hippodrome");
    sProduit.createFeatureAttributeValue(nature4, "Circuit automobile");
    sProduit.createFeatureAttributeValue(nature4, "Port de plaisance");

    // Attribut Importance touristique
    sProduit.createFeatureAttribute(equipementDeLoisirs,
        "Importance touristique", "string", true);
    AttributeType importanceTouristique4 = equipementDeLoisirs
        .getFeatureAttributeByName("Importance touristique");
    importanceTouristique4.setDefinition("");
    sProduit.createFeatureAttributeValue(importanceTouristique4, "Sans objet");
    sProduit.createFeatureAttributeValue(importanceTouristique4,
        "Importance locale");
    sProduit.createFeatureAttributeValue(importanceTouristique4,
        "Importance régionale");
    sProduit.createFeatureAttributeValue(importanceTouristique4,
        "Importance nationale");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(equipementDeLoisirs, "Toponyme", "string",
        false);
    AttributeType toponyme6 = equipementDeLoisirs
        .getFeatureAttributeByName("Toponyme");
    toponyme6.setDefinition("");

    // Classe Table d'orientation////////////////////////////////////////////
    sProduit.createFeatureType("Table d'orientation");
    FeatureType tableDOrientation = (FeatureType) (sProduit
        .getFeatureTypeByName("Table d'orientation"));
    tableDOrientation.setDefinition("");
    tableDOrientation.setIsAbstract(false);

    // Classe Site et curiosité touristique//////////////////////////////////
    sProduit.createFeatureType("Site et curiosité touristique");
    FeatureType siteEtCuriositeTouristique = (FeatureType) (sProduit
        .getFeatureTypeByName("Site et curiosité touristique"));
    siteEtCuriositeTouristique.setDefinition("");
    siteEtCuriositeTouristique.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(siteEtCuriositeTouristique, "Nature",
        "string", true);
    AttributeType nature5 = siteEtCuriositeTouristique
        .getFeatureAttributeByName("Nature");
    nature5.setDefinition("");
    sProduit.createFeatureAttributeValue(nature5, "Grotte, goufre aménagés");
    sProduit.createFeatureAttributeValue(nature5,
        "Monument mégalithique ou préhistorique");
    sProduit.createFeatureAttributeValue(nature5, "Vestige antique");
    sProduit.createFeatureAttributeValue(nature5,
        "Ruine d'époque médiévale ou postérieure");
    sProduit.createFeatureAttributeValue(nature5, "Autre curiosité");

    // Attribut Importance touristique
    sProduit.createFeatureAttribute(siteEtCuriositeTouristique,
        "Importance touristique", "string", true);
    AttributeType importanceTouristique5 = siteEtCuriositeTouristique
        .getFeatureAttributeByName("Importance touristique");
    importanceTouristique5.setDefinition("");
    sProduit.createFeatureAttributeValue(importanceTouristique5,
        "Sans interêt touristique");
    sProduit.createFeatureAttributeValue(importanceTouristique5,
        "Importance locale");
    sProduit.createFeatureAttributeValue(importanceTouristique5,
        "Importance régionale");
    sProduit.createFeatureAttributeValue(importanceTouristique5,
        "Importance nationale");

    // Attribut Monument historique
    sProduit.createFeatureAttribute(siteEtCuriositeTouristique,
        "Monument historique", "string", true);
    AttributeType monumentHistorique2 = siteEtCuriositeTouristique
        .getFeatureAttributeByName("Monument historique");
    monumentHistorique2.setDefinition("");
    sProduit.createFeatureAttributeValue(monumentHistorique2, "Non classé");
    sProduit.createFeatureAttributeValue(monumentHistorique2, "Inscrit");
    sProduit.createFeatureAttributeValue(monumentHistorique2, "Classé");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(siteEtCuriositeTouristique, "Toponyme",
        "string", false);
    AttributeType toponyme7 = siteEtCuriositeTouristique
        .getFeatureAttributeByName("Toponyme");
    toponyme7.setDefinition("");

    // Classe Site sportif///////////////////////////////////////////////////

    sProduit.createFeatureType("Site sportif");
    FeatureType siteSportif = (FeatureType) (sProduit
        .getFeatureTypeByName("Site sportif"));
    siteSportif.setDefinition("");
    siteSportif.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(siteSportif, "Nature", "string", true);
    AttributeType nature6 = siteSportif.getFeatureAttributeByName("Nature");
    nature6.setDefinition("");
    sProduit.createFeatureAttributeValue(nature6, "Escalade");
    sProduit.createFeatureAttributeValue(nature6, "Vol libre");

    // Attribut Importance
    sProduit.createFeatureAttribute(siteSportif, "Importance", "string", true);
    AttributeType importance2 = siteSportif
        .getFeatureAttributeByName("Importance");
    importance2.setDefinition("");
    sProduit.createFeatureAttributeValue(importance2, "Importance locale");
    sProduit.createFeatureAttributeValue(importance2, "Importance régionale");
    sProduit.createFeatureAttributeValue(importance2, "Importance nationale");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(siteSportif, "Toponyme", "string", false);
    AttributeType toponyme8 = siteSportif.getFeatureAttributeByName("Toponyme");
    toponyme8.setDefinition("");

    // Classe Zone règlementée d'interêt touristique

    sProduit.createFeatureType("Zone règlementée d'interêt touristique");
    FeatureType zoneReglementeeDInteretTouristique = (FeatureType) (sProduit
        .getFeatureTypeByName("Zone règlementée d'interêt touristique"));
    zoneReglementeeDInteretTouristique.setDefinition("");
    zoneReglementeeDInteretTouristique.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(zoneReglementeeDInteretTouristique,
        "Nature", "string", true);
    AttributeType nature7 = zoneReglementeeDInteretTouristique
        .getFeatureAttributeByName("Nature");
    nature7.setDefinition("");
    sProduit.createFeatureAttributeValue(nature7, "Parc national");
    sProduit.createFeatureAttributeValue(nature7,
        "Parc national (zone périphérique)");
    sProduit.createFeatureAttributeValue(nature7, "Parc naturel régional");
    sProduit.createFeatureAttributeValue(nature7, "Forêt domaniale");
    sProduit.createFeatureAttributeValue(nature7, "Reserve naturelle");
    sProduit
        .createFeatureAttributeValue(nature7, "Reserve nationale de chasse");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(zoneReglementeeDInteretTouristique,
        "Toponyme", "string", false);
    AttributeType toponyme9 = zoneReglementeeDInteretTouristique
        .getFeatureAttributeByName("Toponyme");
    toponyme9.setDefinition("");

    // Classe Musée non localisé

    sProduit.createFeatureType("Musée non localisé");
    FeatureType museeNonLocalise = (FeatureType) (sProduit
        .getFeatureTypeByName("Musée non localisé"));
    museeNonLocalise.setDefinition("");
    museeNonLocalise.setIsAbstract(false);

    // Attribut Toponyme
    sProduit.createFeatureAttribute(museeNonLocalise, "Toponyme", "string",
        false);
    AttributeType toponyme10 = museeNonLocalise
        .getFeatureAttributeByName("Toponyme");
    toponyme10.setDefinition("");

    // Attribut Description
    sProduit.createFeatureAttribute(museeNonLocalise, "Description", "string",
        false);
    AttributeType description = museeNonLocalise
        .getFeatureAttributeByName("Description");
    description.setDefinition("");

    // Attribut Adresse
    sProduit.createFeatureAttribute(museeNonLocalise, "Adresse", "string",
        false);
    AttributeType adresse = museeNonLocalise
        .getFeatureAttributeByName("Adresse");
    adresse.setDefinition("");

    // Attribut Toponyme d'un objet localisé
    sProduit.createFeatureAttribute(museeNonLocalise,
        "Toponyme d'un objet localisé", "string", false);
    AttributeType toponymeDUnObjetLocalise = museeNonLocalise
        .getFeatureAttributeByName("Toponyme d'un objet localisé");
    toponymeDUnObjetLocalise.setDefinition("");

    // Classe Fête traditionnelle et manifestation artistique
    sProduit
        .createFeatureType("Fête traditionnelle et manifestation artistique");
    FeatureType feteTraditionnelleEtManifestationArtistique = (FeatureType) (sProduit
        .getFeatureTypeByName("Fête traditionnelle et manifestation artistique"));
    feteTraditionnelleEtManifestationArtistique.setDefinition("");
    feteTraditionnelleEtManifestationArtistique.setIsAbstract(false);

    // Attribut Intitulé
    sProduit.createFeatureAttribute(
        feteTraditionnelleEtManifestationArtistique, "Intitulé", "string",
        false);
    AttributeType intitule = feteTraditionnelleEtManifestationArtistique
        .getFeatureAttributeByName("Intitulé");
    intitule.setDefinition("");

    // Attribut Description
    sProduit.createFeatureAttribute(
        feteTraditionnelleEtManifestationArtistique, "Description", "string",
        false);
    AttributeType description2 = feteTraditionnelleEtManifestationArtistique
        .getFeatureAttributeByName("Description");
    description2.setDefinition("");

    // Attribut Période
    sProduit
        .createFeatureAttribute(feteTraditionnelleEtManifestationArtistique,
            "Période", "string", false);
    AttributeType periode = feteTraditionnelleEtManifestationArtistique
        .getFeatureAttributeByName("Période");
    periode.setDefinition("");

    // Attribut Toponyme Zone d'habitat
    sProduit.createFeatureAttribute(
        feteTraditionnelleEtManifestationArtistique, "Toponyme Zone d'habitat",
        "string", false);
    AttributeType toponymeZoneDHabitat = feteTraditionnelleEtManifestationArtistique
        .getFeatureAttributeByName("Toponyme Zone d'habitat");
    toponymeZoneDHabitat.setDefinition("");

    // Attribut Commune
    sProduit
        .createFeatureAttribute(feteTraditionnelleEtManifestationArtistique,
            "Commune", "string", false);
    AttributeType commune = feteTraditionnelleEtManifestationArtistique
        .getFeatureAttributeByName("Commune");
    commune.setDefinition("");

    // relation d'association
    sProduit.createFeatureAssociation("Toponyme d'un objet localisé1",
        museeNonLocalise, etablissementAdministratifOuPublic,
        "est localisé par", "localise");
    sProduit.createFeatureAssociation("Toponyme d'un objet localisé2",
        museeNonLocalise, batimentsRemarquablesAVocationRelgieuseOuTouristique,
        "est localisé par", "localise");
    sProduit.createFeatureAssociation("Toponyme d'un objet localisé3",
        museeNonLocalise, siteEtCuriositeTouristique, "est localisé par",
        "localise");
    sProduit.createFeatureAssociation("Toponyme d'un objet localisé4",
        museeNonLocalise, zoneDHabitat, "est localisé par", "localise");
    sProduit.createFeatureAssociation("Toponyme zone d'habitat 2",
        feteTraditionnelleEtManifestationArtistique, zoneDHabitat,
        "est localisé par", "localise");

    /***************************************************************************
     * Ajout du thème "Réseau routier" *
     ************************************************************************/

    // Classe Tronçon de route

    sProduit.createFeatureType("Tronçon de route");
    FeatureType tronconDeRoute = (FeatureType) (sProduit
        .getFeatureTypeByName("Tronçon de route"));
    tronconDeRoute
        .setDefinition("Cette classe comprend les tronçons de routes, chemins et sentiers. "
        		+ "Les voies en construction sont retenues, sans raccordement au réseau existant, dans la mesure où les terrassement ont débuté sur le terrain.");
    tronconDeRoute.setIsAbstract(false);

    // Attribut Vocation de liaison
    sProduit.createFeatureAttribute(tronconDeRoute, "Vocation de liaison",
        "string", true);
    AttributeType vocationDeLiaison = tronconDeRoute
        .getFeatureAttributeByName("Vocation de liaison");
    vocationDeLiaison
        .setDefinition("Cet attribut matérialise une hiérarchisation du réseau routier basée non pas sur un critère administratif, "
        		+ "mais sur l'importance des tronçons der oute pour le trafic routier. Ainsi ses 4 valeurs permettent un maillage "
        		+ "de plus en plus dense du territoire.");
    sProduit.createFeatureAttributeValue(vocationDeLiaison, "Type autoroutier");
    sProduit.createFeatureAttributeValue(vocationDeLiaison,
        "Liaison principale");
    sProduit
        .createFeatureAttributeValue(vocationDeLiaison, "Liaison regionale");
    sProduit.createFeatureAttributeValue(vocationDeLiaison, "Liaison locale");
    sProduit.createFeatureAttributeValue(vocationDeLiaison, "Bretelle");
    sProduit.createFeatureAttributeValue(vocationDeLiaison, "Piste cyclable");

    // Attribut Nombre de chaussées
    sProduit.createFeatureAttribute(tronconDeRoute, "Nombre de chaussées",
        "string", true);
    AttributeType nombreDeChaussees = tronconDeRoute
        .getFeatureAttributeByName("Nombre de chaussées");
    nombreDeChaussees.setDefinition("");
    sProduit.createFeatureAttributeValue(nombreDeChaussees, "1 chaussée");
    sProduit.createFeatureAttributeValue(nombreDeChaussees, "2 chaussées");

    // Attribut Nombre total de voies
    sProduit.createFeatureAttribute(tronconDeRoute, "Nombre total de voies",
        "string", true);
    AttributeType nombreTotalDeVoies = tronconDeRoute
        .getFeatureAttributeByName("Nombre total de voies");
    nombreTotalDeVoies.setDefinition("");
    sProduit.createFeatureAttributeValue(nombreTotalDeVoies, "sans objet");
    sProduit.createFeatureAttributeValue(nombreTotalDeVoies, "1 voie");
    sProduit
        .createFeatureAttributeValue(nombreTotalDeVoies, "2 voies étroites");
    sProduit.createFeatureAttributeValue(nombreTotalDeVoies,
        "3 voies (chaussée normalisée 10.50 m)");
    sProduit.createFeatureAttributeValue(nombreTotalDeVoies, "4 voies");
    sProduit.createFeatureAttributeValue(nombreTotalDeVoies,
        "2 voies (chaussée normalisée 7 m)");
    sProduit.createFeatureAttributeValue(nombreTotalDeVoies, "Plus de 4 voies");

    // Attribut Etat physique de la route
    sProduit.createFeatureAttribute(tronconDeRoute,
        "Etat physique de la route", "string", true);
    AttributeType etatPhysiqueDeLaRoute = tronconDeRoute
        .getFeatureAttributeByName("Etat physique de la route");
    etatPhysiqueDeLaRoute.setDefinition("");
    sProduit
        .createFeatureAttributeValue(etatPhysiqueDeLaRoute, "Route revêtue");
    sProduit.createFeatureAttributeValue(etatPhysiqueDeLaRoute,
        "Route non revêtue");
    sProduit.createFeatureAttributeValue(etatPhysiqueDeLaRoute,
        "En construction");
    sProduit.createFeatureAttributeValue(etatPhysiqueDeLaRoute,
        "Chemin d'exploitation");
    sProduit.createFeatureAttributeValue(etatPhysiqueDeLaRoute, "Sentier");

    // Attribut Accès
    sProduit.createFeatureAttribute(tronconDeRoute, "Accès", "string", true);
    AttributeType acces2 = tronconDeRoute.getFeatureAttributeByName("Accès");
    acces2.setDefinition("");
    sProduit.createFeatureAttributeValue(acces2, "Libre");
    sProduit.createFeatureAttributeValue(acces2, "A péage");
    sProduit.createFeatureAttributeValue(acces2, "Interdit au public");
    sProduit.createFeatureAttributeValue(acces2, "Fermeture saisonnière");

    // Attribut Position par rapport au sol
    sProduit.createFeatureAttribute(tronconDeRoute,
        "Position par rapport au sol", "string", true);
    AttributeType positionParRapportAuSol = tronconDeRoute
        .getFeatureAttributeByName("Position par rapport au sol");
    positionParRapportAuSol.setDefinition("");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol, "Normal");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol,
        "Sur viaduc ou sur pont");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol,
        "En tunnel, souterrain, couvert ou semi-couvert");

    // Attribut Appartenance au réseau vert
    sProduit.createFeatureAttribute(tronconDeRoute,
        "Appartenance au réseau vert", "string", true);
    AttributeType appartenanceAuReseauVert = tronconDeRoute
        .getFeatureAttributeByName("Appartenance au réseau vert");
    appartenanceAuReseauVert.setDefinition("");
    sProduit
        .createFeatureAttributeValue(appartenanceAuReseauVert, "Appartient");
    sProduit.createFeatureAttributeValue(appartenanceAuReseauVert,
        "N'appartient pas");

    // Attribut Sens
    sProduit.createFeatureAttribute(tronconDeRoute, "Sens", "string", true);
    AttributeType sens = tronconDeRoute.getFeatureAttributeByName("Sens");
    sens.setDefinition("Le sens de circulation est géré de façon obligatoire sur les tronçons composant les voies à chaussées éloignées et sur les tronçons "
    		+ "constituant un échangeur détaillé; dans les autres cas, le sens est géré si l'information est connue.");
    sProduit.createFeatureAttributeValue(sens, "Double sens");
    sProduit.createFeatureAttributeValue(sens, "Sens unique (sens du tronçon)");
    sProduit.createFeatureAttributeValue(sens,
        "Sens unique (sens inverse à celui du tronçon)");

    // Attribut Nombre de voies chaussée montante
    sProduit.createFeatureAttribute(tronconDeRoute,
        "Nombre de voies chaussée montante", "string", true);
    AttributeType nombreDeVoiesChausseeMontante = tronconDeRoute
        .getFeatureAttributeByName("Nombre de voies chaussée montante");
    nombreDeVoiesChausseeMontante
        .setDefinition("Concerne uniquement les tronçons à chaussées séparées. La chaussée montante est la chaussée dont la circulation "
        		+ "se fait dans le sens noeud initial - noeud final.");
    sProduit.createFeatureAttributeValue(nombreDeVoiesChausseeMontante,
        "Sans objet");
    sProduit.createFeatureAttributeValue(nombreDeVoiesChausseeMontante,
        "1 voie");
    sProduit.createFeatureAttributeValue(nombreDeVoiesChausseeMontante,
        "2 voies");
    sProduit.createFeatureAttributeValue(nombreDeVoiesChausseeMontante,
        "3 voies");
    sProduit.createFeatureAttributeValue(nombreDeVoiesChausseeMontante,
        "4 voies");
    sProduit.createFeatureAttributeValue(nombreDeVoiesChausseeMontante,
        "Plus de 4 voies");

    // Attribut Nombre de voies chaussée descendante
    sProduit.createFeatureAttribute(tronconDeRoute,
        "Nombre de voies chaussée descendante", "string", true);
    AttributeType nombreDeVoiesChausseeDescendante = tronconDeRoute
        .getFeatureAttributeByName("Nombre de voies chaussée descendante");
    nombreDeVoiesChausseeDescendante
        .setDefinition("Concerne uniquement les tronçons à chaussées séparées. La chaussée descendante est la chaussée dont la circulation se fait "
        		+ "dans le sens noeud final - noeud initial.");
    sProduit.createFeatureAttributeValue(nombreDeVoiesChausseeDescendante,
        "Sans objet");
    sProduit.createFeatureAttributeValue(nombreDeVoiesChausseeDescendante,
        "1 voie");
    sProduit.createFeatureAttributeValue(nombreDeVoiesChausseeDescendante,
        "2 voies");
    sProduit.createFeatureAttributeValue(nombreDeVoiesChausseeDescendante,
        "3 voies");
    sProduit.createFeatureAttributeValue(nombreDeVoiesChausseeDescendante,
        "4 voies");
    sProduit.createFeatureAttributeValue(nombreDeVoiesChausseeDescendante,
        "Plus de 4 voies");

    // Attribut Toponyme
    sProduit
        .createFeatureAttribute(tronconDeRoute, "Toponyme", "string", false);
    AttributeType toponyme11 = tronconDeRoute
        .getFeatureAttributeByName("Toponyme");
    toponyme11
        .setDefinition("Seuls les noms de ponts, viaducs, et tunnels sont portés par les tronçons de route; les autres toponymes sont portés par les itinéraires routiers.");

    // Attribut Utilisation
    sProduit.createFeatureAttribute(tronconDeRoute, "Utilisation", "string",
        true);
    AttributeType utilisation = tronconDeRoute
        .getFeatureAttributeByName("Utilisation");
    utilisation
        .setDefinition("Cet attribut permet de distinguer les tronçons en fonction de leur utilisation potentielle pour la description de la "
        		+ "logique de communication et/ou une représentation cartographique.");
    sProduit.createFeatureAttributeValue(utilisation,
        "Logique et cartographique");
    sProduit.createFeatureAttributeValue(utilisation, "Logique seule");
    sProduit.createFeatureAttributeValue(utilisation, "Cartographique seule");

    // Attribut Date de mise en service
    sProduit.createFeatureAttribute(tronconDeRoute, "Date de mise en service",
        "string", true);
    AttributeType dateDeMiseEnService = tronconDeRoute
        .getFeatureAttributeByName("Date de mise en service");
    dateDeMiseEnService
        .setDefinition("Cet attribut n'est rempli que pour les tronçons en construction.");
    sProduit.createFeatureAttributeValue(dateDeMiseEnService, "Sans objet");
    sProduit.createFeatureAttributeValue(dateDeMiseEnService,
        "Date de mise en service");

    // Classe Noeud du réseau routier

    sProduit.createFeatureType("Noeud du réseau routier");
    FeatureType noeudDuReseauRoutier = (FeatureType) (sProduit
        .getFeatureTypeByName("Noeud du réseau routier"));
    noeudDuReseauRoutier
        .setDefinition("Un noeud du réseau routier correspond à une extrémité de tronçon de route ou de liaison maritime; "
        		+ "il traduit une modification des conditions de circulation: ce peut être une intersection, un obstacle ou un changement de valeur d'attribut.");
    noeudDuReseauRoutier.setIsAbstract(false);

    // Attribut Type de noeud
    sProduit.createFeatureAttribute(noeudDuReseauRoutier, "Type de noeud",
        "string", true);
    AttributeType typeDeNoeud = noeudDuReseauRoutier
        .getFeatureAttributeByName("Type de noeud");
    typeDeNoeud.setDefinition("");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Carrefour simple, cul de sac, carrefour aménagé d'une extension inférieure à 100 m, rond-point d'un diamètre inférieur à 30 m.");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Intersection représentant un carrefour aménagé d'une extension supérieure à 100 m sans toboggan ni passage inférieur.");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Intersection représentant un rond-point (ou giratoire) d'un diamètre supérieur à 100 m d'axe à axe.");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Carrefour aménagé avec passage inférieur ou toboggan quelle que soit son extension.");
    sProduit.createFeatureAttributeValue(typeDeNoeud,
        "Intersection représentant un échangeur complet.");
    sProduit.createFeatureAttributeValue(typeDeNoeud,
        "Intersection représentant un échangeur partiel.");
    sProduit.createFeatureAttributeValue(typeDeNoeud,
        "Rond-point (ou giratoire) d'un diamètre compris entre 30 et 100 m.");
    sProduit.createFeatureAttributeValue(typeDeNoeud,
        "Embarcadère de bac ou liaison maritime.");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Embarcadère de liaison maritime situé hors du territoire BDCarto positionné de façon fictive en limite de ce territoire.");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Barrière interdisant la communication libre entre deux portions de route régulièrement ou irrégulièrement entretenue");
    sProduit.createFeatureAttributeValue(typeDeNoeud,
        "Barrière de douane (hors CEE).");
    sProduit.createFeatureAttributeValue(typeDeNoeud, "Changement d'attribut.");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Noeud créé par l'intersection entre une route nationale et la limite d'un département quand il n'existe pas de noeud au lieu "
            + "de l'intersection ou noeud créé pour découper des grands tronçons de route (ex: autoroute).");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Noeud de communication restreinte: noeud créé quand il n'existe pas de noeud correspondant aux valeurs ci-dessus au lieu de la restriction.");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(noeudDuReseauRoutier, "Toponyme", "string",
        false);
    AttributeType toponyme12 = noeudDuReseauRoutier
        .getFeatureAttributeByName("Toponyme");
    toponyme12
        .setDefinition("Un noeud du réseau routier peut porter un toponyme si l'un au moins des tronçons connectés appartient au réseau classé, "
        		+ "et si le noeud est de type 'carrefour simple', 'rond-point' ou 'carrefour aménagé avec passage inférieur ou toboggan'. "
        		+ "Un noeud composant un carrefour complexe ne porte généralement pas de toponyme.");

    // Attribut cote
    sProduit.createFeatureAttribute(noeudDuReseauRoutier, "Cote", "entier",
        false);
    AttributeType cote = noeudDuReseauRoutier.getFeatureAttributeByName("Cote");
    cote.setDefinition("Nombre entier donnant l'altitude en mètres; cet attribut peut ne porter aucune valeur (inconnu). La densité des points cotés "
    		+ "est d'environ 30 par feuille 1:50 000.");

    // Classe Equipement routier

    sProduit.createFeatureType("Equipement routier");
    FeatureType equipementRoutier = (FeatureType) (sProduit
        .getFeatureTypeByName("Equipement routier"));
    equipementRoutier
        .setDefinition("La classe des équipements routiers regroupe: les aires de repos et les aires de service sur le réseau de type autoroutier; "
        		+ "les tunnels routiers d'une longueur inférieure à 200m s'ils ne correspondent pas à une intersection avec d'autres tronçons du réseau "
        		+ "routier et ferré (sinon ce sont des franchissements); les gares de péage.");
    equipementRoutier.setIsAbstract(false);

    // Attribut Nature
    sProduit
        .createFeatureAttribute(equipementRoutier, "Nature", "string", true);
    AttributeType nature8 = equipementRoutier
        .getFeatureAttributeByName("Nature");
    nature8.setDefinition("");
    sProduit.createFeatureAttributeValue(nature8, "Aire de service");
    sProduit.createFeatureAttributeValue(nature8, "Aire de repos");
    sProduit.createFeatureAttributeValue(nature8, "tunnel de moins de 200 m");
    sProduit.createFeatureAttributeValue(nature8, "Gare de péage");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(equipementRoutier, "Toponyme", "string",
        false);
    AttributeType toponyme13 = equipementRoutier
        .getFeatureAttributeByName("Toponyme");
    toponyme13.setDefinition("Un équipement porte en général un toponyme.");

    // Classe Liaison maritime ou bac 

    sProduit.createFeatureType("Liaison maritime ou bac");
    FeatureType liaisonMaritimeOuBac = (FeatureType) (sProduit
        .getFeatureTypeByName("Liaison maritime ou bac"));
    liaisonMaritimeOuBac
        .setDefinition("Liaison maritime ou ligne de bac reliant deux embarcadères.");
    liaisonMaritimeOuBac.setIsAbstract(false);

    // Attribut Ouverture
    sProduit.createFeatureAttribute(liaisonMaritimeOuBac, "Ouverture",
        "string", true);
    AttributeType ouverture = liaisonMaritimeOuBac
        .getFeatureAttributeByName("Ouverture");
    ouverture.setDefinition("");
    sProduit.createFeatureAttributeValue(ouverture, "Toute l'année");
    sProduit.createFeatureAttributeValue(ouverture, "En saison seulement");

    // Attribut Vocation
    sProduit.createFeatureAttribute(liaisonMaritimeOuBac, "Vocation", "string",
        true);
    AttributeType vocation = liaisonMaritimeOuBac
        .getFeatureAttributeByName("Vocation");
    vocation.setDefinition("");
    sProduit.createFeatureAttributeValue(vocation, "Piétons seulement");
    sProduit.createFeatureAttributeValue(vocation, "Piétons et automobiles");

    // Attribut Durée
    sProduit.createFeatureAttribute(liaisonMaritimeOuBac, "Durée", "entier",
        false);
    AttributeType duree = liaisonMaritimeOuBac
        .getFeatureAttributeByName("Durée");
    duree
        .setDefinition("Durée de la traversée en minutes, pouvant éventuellement ne porter aucune valeur (inconnu). "
        		+ "Note: Quand il ya plusieurs temps de parcours pour une même liaison, c'est le temps le plus long qui est retenu.");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(liaisonMaritimeOuBac, "Toponyme", "string",
        false);
    AttributeType toponyme14 = liaisonMaritimeOuBac
        .getFeatureAttributeByName("Toponyme");
    toponyme14
        .setDefinition("Texte d'au plus 80 caractères, pouvant éventuellement ne porter aucune valeur (inconnu), spécifiant la localisation "
        		+ "des embarcadères de départ et d'arrivée.");

    // Classe Communication restreinte

    sProduit.createFeatureType("Communication restreinte");
    FeatureType communicationRestreinte = (FeatureType) (sProduit
        .getFeatureTypeByName("Communication restreinte"));
    communicationRestreinte
        .setDefinition("Relation topologique participant à la logique de parcours du réseau routier: elle explicite les restrictions éventuelles "
        		+ "au passage d'un tronçon de route à un autre via un noeud routier commun aux deux tronçons.");
    communicationRestreinte.setIsAbstract(false);

    // Attribut Interdiction
    sProduit.createFeatureAttribute(communicationRestreinte, "Interdiction",
        "string", true);
    AttributeType interdiction = communicationRestreinte
        .getFeatureAttributeByName("Interdiction");
    interdiction.setDefinition("");
    sProduit.createFeatureAttributeValue(interdiction, "Totale");
    sProduit.createFeatureAttributeValue(interdiction,
        "Restreinte au dépassement d'une hauteur et/ou d'un poids maximal");

    // Attribut Restriction poids
    sProduit.createFeatureAttribute(communicationRestreinte,
        "Restriction poids", "entier", false);
    AttributeType restrictionPoids = communicationRestreinte
        .getFeatureAttributeByName("Restriction poids");
    restrictionPoids
        .setDefinition("Poids maximum autorisé en tonnes (à une décimale).");

    // Attribut Restriction taille
    sProduit.createFeatureAttribute(communicationRestreinte,
        "Restriction taille", "entier", false);
    AttributeType restrictionTaille = communicationRestreinte
        .getFeatureAttributeByName("Restriction taille");
    restrictionTaille
        .setDefinition("Hauteur maximum autorisée en mètres (à deux décimales).");

    // Classe Carrefour complexe

    sProduit.createFeatureType("Carrefour complexe");
    FeatureType carrefourComplexe = (FeatureType) (sProduit
        .getFeatureTypeByName("Carrefour complexe"));
    carrefourComplexe
        .setDefinition("La classe des carrefours comples regroupe: les échangeurs, diffuseurs, carrefours dénivelés et 'tourne à gauche' dénivelés situés "
        		+ "sur les tronçons de type autoroutier ou de liaison principale; les ronds-points et carrefours aménagés d'un diamètre supérieur à 100 m d'axe à axe.");
    carrefourComplexe.setIsAbstract(false);

    // Attribut Numéro
    sProduit.createFeatureAttribute(carrefourComplexe, "Numéro", "string",
        false);
    AttributeType numero1 = carrefourComplexe
        .getFeatureAttributeByName("Numéro");
    numero1
        .setDefinition("Texte d'au plus 10 caractères: numérotation mise en place par la Direction de la Sécurité de la Circulation Routières. "
        		+ "Cette numérotation concerne uniquement les échangeurs sur autoroutes, voies express aux normes, et certaines routes nationales importantes.");

    // Attribut Nature
    sProduit
        .createFeatureAttribute(carrefourComplexe, "Nature", "string", true);
    AttributeType nature9 = carrefourComplexe
        .getFeatureAttributeByName("Nature");
    nature9.setDefinition("");
    sProduit.createFeatureAttributeValue(nature9,
        "Echangeur, sorties d'autoroute, diffuseurs, carrefours dénivelés");
    sProduit.createFeatureAttributeValue(nature9, "Rond-point");
    sProduit.createFeatureAttributeValue(nature9, "Carrefour aménagé");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(carrefourComplexe, "Toponyme", "string",
        false);
    AttributeType toponyme15 = carrefourComplexe
        .getFeatureAttributeByName("Toponyme");
    toponyme15
        .setDefinition("Il s'agit du nom géographique de l'échangeur, du rond-point ou du carrefour aménagé.");

    // Classe Route

    sProduit.createFeatureType("Route");
    FeatureType route = (FeatureType) (sProduit.getFeatureTypeByName("Route"));
    route
        .setDefinition("Une route est un parcours classé par l'autorité administrative nationale, régionale ou départementale, "
        		+ "reliant entre elles des villes ou des pôles d'attraction (ports, aéroports, lieux touristiques, etc.) et identifié par un numéro. "
        		+ "Ce dernier correspond au numéro donné par l'organisme chargé de gérer la route et résulte de procédures administratives. Il peut être "
        		+ "différent de celui présent sur le terrain qui peut ne pas être actualisé ou anticiper un classement futur. "
        		+ "Sur le territoire national, cette classe comprend les autoroutes, les routes nationales, les routes départementales et les "
        		+ "bretelles d'échangeur identifiés par les gestionnaires. A l'étranger, elle ne comprend que les autoroutes et les routes nationales "
        		+ "(ou équivalent). Des routes spécifiques sont gérées et saisies sur les bretelles d'échangeurs dans la BDCarto quand l'information "
        		+ "est données par le gestionnaires de la route ou le METLTM. En général, le numéro des bretelles d'échangeur est codé sur 7 caractères. "
        		+ "Les autres types de classement administratif (route vicinale, forestières) ne sont pas gérés dans la BDCarto.");
    route.setIsAbstract(false);

    // Attribut Numéro de la route
    sProduit.createFeatureAttribute(route, "Numéro de la route", "string",
        false);
    AttributeType numeroDeLaRoute = route
        .getFeatureAttributeByName("Numéro de la route");
    numeroDeLaRoute.setDefinition("");

    // Attribut Classement administratif
    sProduit.createFeatureAttribute(route, "Classement administratif",
        "string", true);
    AttributeType classementAdministratif = route
        .getFeatureAttributeByName("Classement administratif");
    classementAdministratif.setDefinition("");
    sProduit.createFeatureAttributeValue(classementAdministratif, "Autoroute");
    sProduit.createFeatureAttributeValue(classementAdministratif,
        "Route nationale");
    sProduit.createFeatureAttributeValue(classementAdministratif,
        "Route départementale");

    // Attribut Gestionnaire
    sProduit.createFeatureAttribute(route, "Gestionnaire", "string", false);
    AttributeType gestionnaire = route
        .getFeatureAttributeByName("Gestionnaire");
    gestionnaire.setDefinition("");

    // Classe Itinéraire routier

    sProduit.createFeatureType("Itinéraire routier");
    FeatureType itineraireRoutier = (FeatureType) (sProduit
        .getFeatureTypeByName("Itinéraire routier"));
    itineraireRoutier
        .setDefinition("Un itinéraire routier est un ensemble de parcours continus empruntant des tronçons de route, "
        		+ "chemins ou sentiers et identifié par un toponyme ou un numéro.");
    itineraireRoutier.setIsAbstract(false);

    // Attribut Numéro
    sProduit.createFeatureAttribute(itineraireRoutier, "Numéro", "string",
        false);
    AttributeType numero2 = itineraireRoutier
        .getFeatureAttributeByName("Numéro");
    numero2.setDefinition("");

    // Attribut Nature
    sProduit
        .createFeatureAttribute(itineraireRoutier, "Nature", "string", true);
    AttributeType nature10 = itineraireRoutier
        .getFeatureAttributeByName("Nature");
    nature10.setDefinition("");
    sProduit.createFeatureAttributeValue(nature10,
        "Itinéraire routier portant un nom");
    sProduit.createFeatureAttributeValue(nature10, "Route européenne");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(itineraireRoutier, "Toponyme", "string",
        false);
    AttributeType toponyme16 = itineraireRoutier
        .getFeatureAttributeByName("Toponyme");
    toponyme16.setDefinition("");

    // Classe Début de section

    sProduit.createFeatureType("Début de section");
    FeatureType debutDeSection = (FeatureType) (sProduit
        .getFeatureTypeByName("Début de section"));
    debutDeSection
        .setDefinition("Une section est un ensemble continu de tronçons de route ayant le même numéro et le même gestionnaire.");
    debutDeSection.setIsAbstract(false);

    // Attribut Gestionnaire
    sProduit.createFeatureAttribute(debutDeSection, "Gestionnaire", "string",
        false);
    AttributeType gestionnaire2 = debutDeSection
        .getFeatureAttributeByName("Gestionnaire");
    gestionnaire2.setDefinition("");

    // Attribut Sens
    sProduit.createFeatureAttribute(debutDeSection, "Sens", "string", true);
    AttributeType sens2 = debutDeSection.getFeatureAttributeByName("Sens");
    sens2.setDefinition("Sens de parcours de la section.");
    sProduit.createFeatureAttributeValue(sens2, "sens du tronçon");
    sProduit.createFeatureAttributeValue(sens2,
        "sens inverse à celui du tronçon");

    // relation d'association Permet d'accéder à
    sProduit.createFeatureAssociation("Permet d'accéder à", equipementRoutier,
        tronconDeRoute, "est accessible par", "permet d'accéder à");
    // Attention penser à rajouter les attributs

    // relation d'association réseau routier
    sProduit
        .createFeatureAssociation("Réseau routier initial", tronconDeRoute,
            noeudDuReseauRoutier, "a pour noeud initial",
            "est le noeud initial de");
    sProduit.createFeatureAssociation("Réseau routier final", tronconDeRoute,
        noeudDuReseauRoutier, "a pour noeud final", "est le noeud final de");

    // relation d'association réseau de bac
    sProduit.createFeatureAssociation("Réseau de bac initial",
        liaisonMaritimeOuBac, noeudDuReseauRoutier, "a pour noeud initial",
        "est le noeud initial de");
    sProduit.createFeatureAssociation("Réseau de bac final",
        liaisonMaritimeOuBac, noeudDuReseauRoutier, "a pour noeud final",
        "est le noeud final de");

    // relation d'association communication restreinte
    sProduit.createFeatureAssociation("Communication restreinte",
        communicationRestreinte, noeudDuReseauRoutier, "concerne",
        "est concernée par");
    sProduit.createFeatureAssociation(
        "Communication restreinte tronçon initial", communicationRestreinte,
        tronconDeRoute, "a pour tronçon initial", "est le tronçon initial de");
    sProduit.createFeatureAssociation("Communication restreinte tronçon final",
        communicationRestreinte, tronconDeRoute, "a pour tronçon final",
        "est le tronçon final de");

    // relation d'association a pour successeur
    sProduit.createFeatureAssociation("A pour successeur", debutDeSection,
        debutDeSection, "A pour successeur", "a pour prédécesseur");

    /***************************************************************************
     * Ajout du thème "Réseau Ferré" *
     ************************************************************************/

    // Classe Tronçon de voie ferrée

    sProduit.createFeatureType("Tronçon de voie ferrée");
    FeatureType tronconDeVoieFerree = (FeatureType) (sProduit
        .getFeatureTypeByName("Tronçon de voie ferrée"));
    tronconDeVoieFerree.setDefinition("");
    tronconDeVoieFerree.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(tronconDeVoieFerree, "Nature", "string",
        true);
    AttributeType nature11 = tronconDeVoieFerree
        .getFeatureAttributeByName("Nature");
    nature11.setDefinition("");
    sProduit.createFeatureAttributeValue(nature11, "Normale");
    sProduit.createFeatureAttributeValue(nature11, "Tronçon à grande vitesse");
    sProduit
        .createFeatureAttributeValue(
            nature11,
            "Tronçon d'embranchement particulier, voie industrielle ou de service, ligne touristique");
    sProduit.createFeatureAttributeValue(nature11,
        "Tronçon de voie de triage ou de garage");
    sProduit.createFeatureAttributeValue(nature11, "Train à crémaillère");
    sProduit.createFeatureAttributeValue(nature11, "Funiculaire");

    // Attribut Energie de propulsion
    sProduit.createFeatureAttribute(tronconDeVoieFerree,
        "Energie de propulsion", "string", true);
    AttributeType energieDePropulsion = tronconDeVoieFerree
        .getFeatureAttributeByName("Energie de propulsion");
    energieDePropulsion.setDefinition("");
    sProduit.createFeatureAttributeValue(energieDePropulsion, "Electrique");
    sProduit.createFeatureAttributeValue(energieDePropulsion,
        "En cours d'électrification");
    sProduit.createFeatureAttributeValue(energieDePropulsion, "Non éléctrique");

    // Attribut Nombre de voies principales
    sProduit.createFeatureAttribute(tronconDeVoieFerree,
        "Nombre de voies principales", "string", true);
    AttributeType nombreDeVoiesPrincipales = tronconDeVoieFerree
        .getFeatureAttributeByName("Nombre de voies principales");
    nombreDeVoiesPrincipales.setDefinition("");
    sProduit.createFeatureAttributeValue(nombreDeVoiesPrincipales, "1 voie");
    sProduit.createFeatureAttributeValue(nombreDeVoiesPrincipales,
        "2 voies ou plus");

    // Attribut Ecartement des voies
    sProduit.createFeatureAttribute(tronconDeVoieFerree,
        "Ecartement des voies", "string", true);
    AttributeType ecartementDesVoies = tronconDeVoieFerree
        .getFeatureAttributeByName("Ecartement des voies");
    ecartementDesVoies.setDefinition("");
    sProduit.createFeatureAttributeValue(ecartementDesVoies, "Normal(1,44 m)");
    sProduit
        .createFeatureAttributeValue(ecartementDesVoies, "Etroite(<1,44 m)");
    sProduit.createFeatureAttributeValue(ecartementDesVoies,
        "Large(>1,44 m) en particulier en Espagne");

    // Attribut Position par rapport au sol
    sProduit.createFeatureAttribute(tronconDeVoieFerree,
        "Position par rapport au sol", "string", true);
    AttributeType positionParRapportAuSol2 = tronconDeVoieFerree
        .getFeatureAttributeByName("Position par rapport au sol");
    positionParRapportAuSol2.setDefinition("");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol2, "Normale");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol2,
        "Sur viaduc ou sur pont");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol2,
        "En tunnel, souterrain, couvert");

    // Attribut Classement
    sProduit.createFeatureAttribute(tronconDeVoieFerree, "Classement",
        "string", true);
    AttributeType classement = tronconDeVoieFerree
        .getFeatureAttributeByName("Classement");
    classement.setDefinition("");
    sProduit.createFeatureAttributeValue(classement, "Exploité");
    sProduit.createFeatureAttributeValue(classement, "Non exploité");
    sProduit.createFeatureAttributeValue(classement,
        "En construction ou en projet");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(tronconDeVoieFerree, "Toponyme",
        "string générique, string article, string spécifique", false);
    AttributeType toponyme17 = tronconDeVoieFerree
        .getFeatureAttributeByName("Toponyme");
    toponyme17.setDefinition("");

    // Classe Noeud du Réseau ferré
    sProduit.createFeatureType("Noeud du réseau ferré");
    FeatureType noeudDuReseauferre = (FeatureType) (sProduit
        .getFeatureTypeByName("Noeud du réseau ferré"));
    noeudDuReseauferre.setDefinition("");
    noeudDuReseauferre.setIsAbstract(false);

    // Attribut Type de noeud
    sProduit.createFeatureAttribute(noeudDuReseauferre, "Type de noeud",
        "string", true);
    AttributeType typeDeNoeud2 = noeudDuReseauferre
        .getFeatureAttributeByName("Type de noeud");
    typeDeNoeud2.setDefinition("");
    sProduit.createFeatureAttributeValue(typeDeNoeud2,
        "Gare ouverte aux voyageurs et au fret");
    sProduit.createFeatureAttributeValue(typeDeNoeud2,
        "Gare non ouverte aux voyageurs (gare de fret seulement)");
    sProduit.createFeatureAttributeValue(typeDeNoeud2,
        "Gare ou point d'arrêt ouvert aux voyageurs seulement");
    sProduit.createFeatureAttributeValue(typeDeNoeud2,
        "Embranchement, cul de sac");
    sProduit
        .createFeatureAttributeValue(typeDeNoeud2, "Changement d'attributs");
    sProduit.createFeatureAttributeValue(typeDeNoeud2,
        "Noeud arbitraire créé pour couper les grands tronçons ferrés");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(noeudDuReseauferre, "Toponyme", "string",
        false);
    AttributeType toponyme18 = noeudDuReseauferre
        .getFeatureAttributeByName("Toponyme");
    toponyme18.setDefinition("");

    // Classe Ligne de chemin de fer

    sProduit.createFeatureType("Ligne de chemin de fer");
    FeatureType ligneDeCheminDeFer = (FeatureType) (sProduit
        .getFeatureTypeByName("Ligne de chemin de fer"));
    ligneDeCheminDeFer.setDefinition("");
    ligneDeCheminDeFer.setIsAbstract(false);

    // Attribut Caractère touristique
    sProduit.createFeatureAttribute(ligneDeCheminDeFer,
        "Caractère touristique", "string", true);
    AttributeType caractereTouristique = ligneDeCheminDeFer
        .getFeatureAttributeByName("Caractère touristique");
    caractereTouristique.setDefinition("");
    sProduit.createFeatureAttributeValue(caractereTouristique,
        "Ligne touristique");
    sProduit.createFeatureAttributeValue(caractereTouristique,
        "Ligne sans caractère touristique particulier");

    // Toponyme
    sProduit.createFeatureAttribute(ligneDeCheminDeFer, "Toponyme", "string",
        false);
    AttributeType toponyme20 = ligneDeCheminDeFer
        .getFeatureAttributeByName("Toponyme");
    toponyme20.setDefinition("");

    // Relation d'association Réseau ferré
    sProduit.createFeatureAssociation("Réseau ferré initial",
        tronconDeVoieFerree, noeudDuReseauferre, "a pour noeud initial",
        "est le noeud initial de");
    sProduit.createFeatureAssociation("Réseau ferré final", ligneDeCheminDeFer,
        noeudDuReseauferre, "a pour noeud final", "est le noeud final de");

    // Relation d'association est composé de
    sProduit.createFeatureAssociation("Ligne de chemin de fer",
        ligneDeCheminDeFer, tronconDeVoieFerree, "est composée de", "compose");

    // Attention il manque les relations extra-thème

    /***************************************************************************
     * Ajout du thème "Hydrographie" *
     ************************************************************************/

    // Classe Tronçon hydrographique
    
    sProduit.createFeatureType("Tronçon hydrographique");
    FeatureType tronconHydrographique = (FeatureType) (sProduit
        .getFeatureTypeByName("Tronçon hydrographique"));
    tronconHydrographique.setDefinition("");
    tronconHydrographique.setIsAbstract(false);

    // Attribut Etat
    sProduit.createFeatureAttribute(tronconHydrographique, "Etat", "string",
        true);
    AttributeType etat = tronconHydrographique
        .getFeatureAttributeByName("Etat");
    etat.setDefinition("");
    sProduit.createFeatureAttributeValue(etat, "En attente de mise à jour");
    sProduit
        .createFeatureAttributeValue(
            etat,
            "Inconnu: l'existance d'un écoulement est certaine mais le tracé n'est pas connu avec précision");
    sProduit.createFeatureAttributeValue(etat, "Ecoulement permanent");
    sProduit.createFeatureAttributeValue(etat, "Ecoulement intermittent");
    sProduit
        .createFeatureAttributeValue(
            etat,
            "Axe fictif: arc créé pour assurer la continuité des cours d'eau à la traversée des zones d'hydrographie ou lorsque le tracé "
            + "n'est pas connu avec précision (parcours souterrain)");
    sProduit.createFeatureAttributeValue(etat, "Canal abandonné à sec");

    // Attribut Sens
    sProduit.createFeatureAttribute(tronconHydrographique, "Sens", "string",
        true);
    AttributeType sens3 = tronconHydrographique
        .getFeatureAttributeByName("Sens");
    sens3.setDefinition("");
    sProduit.createFeatureAttributeValue(sens3, "En attente de mise à jour");
    sProduit.createFeatureAttributeValue(sens3, "Inconnu");
    sProduit
        .createFeatureAttributeValue(
            sens3,
            "Sens d'écoulement dans le sens de l'arc (du noeud initial vers le noeud final)");
    sProduit.createFeatureAttributeValue(sens3,
        "Sens d'écoulement variable dont bief de passage");

    // Attribut Largeur
    sProduit.createFeatureAttribute(tronconHydrographique, "Largeur", "string",
        true);
    AttributeType largeur = tronconHydrographique
        .getFeatureAttributeByName("Largeur");
    largeur.setDefinition("");
    sProduit.createFeatureAttributeValue(largeur, "En attente de mise à jour");
    sProduit.createFeatureAttributeValue(largeur,
        "Sans objet (seulement si l'état est inconnu ou fictif");
    sProduit.createFeatureAttributeValue(largeur, "Entre 0 et 15 m");
    sProduit.createFeatureAttributeValue(largeur, "Entre 15 et 50 m");
    sProduit.createFeatureAttributeValue(largeur, "Plus de 50 m");

    // Attribut Nature
    sProduit.createFeatureAttribute(tronconHydrographique, "Nature", "string",
        true);
    AttributeType nature12 = tronconHydrographique
        .getFeatureAttributeByName("Nature");
    nature12.setDefinition("");
    sProduit.createFeatureAttributeValue(nature12, "En attente de mise à jour");
    sProduit.createFeatureAttributeValue(nature12,
        "Sans objet (seulement si l'état est inconnu ou fictif)");
    sProduit.createFeatureAttributeValue(nature12, "Cours d'eau naturel");
    sProduit.createFeatureAttributeValue(nature12,
        "Canal, chenal: voie d'eau artificielle");
    sProduit
        .createFeatureAttributeValue(
            nature12,
            "Aqueduc, conduite forcée: tuyau ou chenal artificiel conçu pour le transport de l'eau (usage hydroélectrique ou industriel)");
    sProduit.createFeatureAttributeValue(nature12,
        "Estuaire: écoulement d'un cours d'eau dans la zone d'estran");
    sProduit.createFeatureAttributeValue(nature12,
        "Tronçon allant de la cote zéro NGF à la laisse des plus basses eaux.");

    // Attribut Navigabilité
    sProduit.createFeatureAttribute(tronconHydrographique, "Navigabilité",
        "string", true);
    AttributeType navigabilite = tronconHydrographique
        .getFeatureAttributeByName("Navigabilité");
    navigabilite.setDefinition("");
    sProduit.createFeatureAttributeValue(navigabilite,
        "En attente de mise à jour");
    sProduit.createFeatureAttributeValue(navigabilite, "Inconnu");
    sProduit.createFeatureAttributeValue(navigabilite,
        "Navigable: inscrit à la nomenclature des voies navigables");
    sProduit.createFeatureAttributeValue(navigabilite, "Non navigable");
    sProduit.createFeatureAttributeValue(navigabilite, "");
    sProduit.createFeatureAttributeValue(navigabilite, "");

    // Attribut Gabarit
    sProduit.createFeatureAttribute(tronconHydrographique, "Gabarit", "string",
        true);
    AttributeType gabarit = tronconHydrographique
        .getFeatureAttributeByName("Gabarit");
    gabarit.setDefinition("");
    sProduit.createFeatureAttributeValue(gabarit, "En attente de mise à jour");
    sProduit.createFeatureAttributeValue(gabarit,
        "Sans objet (si la navigabilité est inconnu ou non navigable)");
    sProduit.createFeatureAttributeValue(gabarit, "Classe 0");
    sProduit.createFeatureAttributeValue(gabarit, "Classe 1");
    sProduit.createFeatureAttributeValue(gabarit, "Classe 2");
    sProduit.createFeatureAttributeValue(gabarit, "Classe 3");
    sProduit.createFeatureAttributeValue(gabarit, "Classe 4");
    sProduit.createFeatureAttributeValue(gabarit, "Classe 5");
    sProduit.createFeatureAttributeValue(gabarit, "Classe 6");
    sProduit.createFeatureAttributeValue(gabarit, "Classe 7");

    // Attribut Position par rapport au sol
    sProduit.createFeatureAttribute(tronconHydrographique,
        "Position par rapport au sol", "string", true);
    AttributeType positionParRapportAuSol3 = tronconHydrographique
        .getFeatureAttributeByName("Position par rapport au sol");
    positionParRapportAuSol3.setDefinition("");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol3,
        "En attente de mise à jour");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol3, "Inconnu");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol3,
        "Au sol, à ciel ouvert");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol3,
        "Elevé sur pont, arcade ou mur");
    sProduit
        .createFeatureAttributeValue(positionParRapportAuSol3, "Souterrain");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol3,
        "Au sol (tuyau posé au sol");

    // Attribut Code Hydrographique
    sProduit.createFeatureAttribute(tronconHydrographique,
        "Code Hydrographique", "string", false);
    AttributeType codeHydrographique = tronconHydrographique
        .getFeatureAttributeByName("Code Hydrographique");
    codeHydrographique.setDefinition("");

    // Attribut Sous-milieu Hydrographique
    sProduit.createFeatureAttribute(tronconHydrographique,
        "Sous-milieu Hydrographique", "string", true);
    AttributeType sousMilieuHydrographique = tronconHydrographique
        .getFeatureAttributeByName("Sous-milieu Hydrographique");
    sousMilieuHydrographique.setDefinition("");

    // Attribut Toponyme 1
    sProduit.createFeatureAttribute(tronconHydrographique, "Toponyme 1",
        "string", false);
    AttributeType toponyme19 = tronconHydrographique
        .getFeatureAttributeByName("Toponyme 1");
    toponyme19.setDefinition("");

    // Attribut Candidat 1
    sProduit.createFeatureAttribute(tronconHydrographique, "Candidat 1",
        "string", false);
    AttributeType candidat1 = tronconHydrographique
        .getFeatureAttributeByName("Candidat 1");
    candidat1.setDefinition("");

    // Attribut Toponyme 2
    sProduit.createFeatureAttribute(tronconHydrographique, "Toponyme 2",
        "string", false);
    AttributeType toponyme21 = tronconHydrographique
        .getFeatureAttributeByName("Toponyme 2");
    toponyme21.setDefinition("");

    // Attribut Candidat 2
    sProduit.createFeatureAttribute(tronconHydrographique, "Candidat 2",
        "string", false);
    AttributeType candidat2 = tronconHydrographique
        .getFeatureAttributeByName("Candidat 2");
    candidat2.setDefinition("");

    // Attribut FPKH
    sProduit.createFeatureAttribute(tronconHydrographique, "FPKH", "entier",
        false);
    AttributeType fPKH = tronconHydrographique
        .getFeatureAttributeByName("FPKH");
    fPKH.setDefinition("");

    // Attribut TPKH
    sProduit.createFeatureAttribute(tronconHydrographique, "TPKH", "entier",
        false);
    AttributeType tPKH = tronconHydrographique
        .getFeatureAttributeByName("TPKH");
    tPKH.setDefinition("");

    // Classe Noeud hydrographique////////////////////////////////////////////

    sProduit.createFeatureType("Noeud hydrographique");
    FeatureType noeudHydrographique = (FeatureType) (sProduit
        .getFeatureTypeByName("Noeud hydrographique"));
    noeudHydrographique.setDefinition("");
    noeudHydrographique.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(noeudHydrographique, "Nature", "string",
        true);
    AttributeType nature13 = noeudHydrographique
        .getFeatureAttributeByName("Nature");
    nature13.setDefinition("");
    sProduit.createFeatureAttributeValue(nature13, "En attente de mise à jour");
    sProduit.createFeatureAttributeValue(nature13,
        "Noeud hydrographique sans nature particulière");
    sProduit.createFeatureAttributeValue(nature13, "Inconnu");
    sProduit.createFeatureAttributeValue(nature13,
        "Barrage de retenue sans ouvrage de franchissement");
    sProduit.createFeatureAttributeValue(nature13,
        "Barrage de retenue avec ouvrage de franchissement");
    sProduit.createFeatureAttributeValue(nature13,
        "Barrage au fil de l'eau sans ouvrage de franchissement");
    sProduit.createFeatureAttributeValue(nature13,
        "Barrage au fil de l'eau avec ouvrage de franchissement");
    sProduit
        .createFeatureAttributeValue(nature13,
            "Ouvrage de franchissement de chutes (écluse, pente d'eau, ascenceur à bateau)");
    sProduit.createFeatureAttributeValue(nature13,
        "Chute d'eau, cascade remarquable");
    sProduit.createFeatureAttributeValue(nature13,
        "Source d'interêt touristique");
    sProduit.createFeatureAttributeValue(nature13,
        "Autres ouvrages (portes de garde)");
    sProduit
        .createFeatureAttributeValue(nature13, "Franchissement hydro/hydro");
    sProduit.createFeatureAttributeValue(nature13,
        "Embouchure, estuaire, delta");
    sProduit.createFeatureAttributeValue(nature13, "Perte");
    sProduit.createFeatureAttributeValue(nature13, "Changement d'attribut");
    sProduit.createFeatureAttributeValue(nature13, "Source simple, confluent");
    sProduit
        .createFeatureAttributeValue(
            nature13,
            "Extremité de tronçon 'zéro NGF' (Valeur Z pour l'attribut nature du tronçon) coïncidant avec la laisse des plus basses eaux");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(noeudHydrographique, "Toponyme", "string",
        false);
    AttributeType toponyme22 = noeudHydrographique
        .getFeatureAttributeByName("Toponyme");
    toponyme22.setDefinition("");

    // Attribut Candidat
    sProduit.createFeatureAttribute(noeudHydrographique, "Candidat", "string",
        false);
    AttributeType candidat3 = noeudHydrographique
        .getFeatureAttributeByName("Candidat");
    candidat3.setDefinition("");

    // Attribut Cote
    sProduit.createFeatureAttribute(noeudHydrographique, "Cote", "string",
        false);
    AttributeType cote1 = noeudHydrographique.getFeatureAttributeByName("Cote");
    cote1.setDefinition("");

    // Classe Point d'eau isolé

    sProduit.createFeatureType("Point d'eau isolé");
    FeatureType pointDEauIsole = (FeatureType) (sProduit
        .getFeatureTypeByName("Point d'eau isolé"));
    pointDEauIsole.setDefinition("");
    pointDEauIsole.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(pointDEauIsole, "Nature", "string", true);
    AttributeType nature14 = pointDEauIsole.getFeatureAttributeByName("Nature");
    nature14.setDefinition("");
    sProduit.createFeatureAttributeValue(nature14, "Inconnu");
    sProduit.createFeatureAttributeValue(nature14, "Château d'eau");
    sProduit.createFeatureAttributeValue(nature14,
        "Station de traitement des eaux");
    sProduit.createFeatureAttributeValue(nature14, "Station de pompage");
    sProduit.createFeatureAttributeValue(nature14, "Réservoir");
    sProduit.createFeatureAttributeValue(nature14,
        "Plan d'eau d'une superficie inférieure à 1 ha");

    // Attribut Toponyme
    sProduit
        .createFeatureAttribute(pointDEauIsole, "Toponyme", "string", false);
    AttributeType toponyme23 = pointDEauIsole
        .getFeatureAttributeByName("Toponyme");
    toponyme23.setDefinition("");

    // Attribut Candidat
    sProduit
        .createFeatureAttribute(pointDEauIsole, "Candidat", "string", false);
    AttributeType candidat4 = pointDEauIsole
        .getFeatureAttributeByName("Candidat");
    candidat4.setDefinition("");

    // Attribut Cote
    sProduit.createFeatureAttribute(pointDEauIsole, "Cote", "string", false);
    AttributeType cote2 = pointDEauIsole.getFeatureAttributeByName("Cote");
    cote2.setDefinition("");

    // Classe Laisse//////////////////////////////////////////////////////////

    sProduit.createFeatureType("Laisse");
    FeatureType laisse = (FeatureType) (sProduit.getFeatureTypeByName("Laisse"));
    laisse.setDefinition("");
    laisse.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(laisse, "Nature", "string", true);
    AttributeType nature15 = laisse.getFeatureAttributeByName("Nature");
    nature15.setDefinition("");
    sProduit.createFeatureAttributeValue(nature15, "Inconnu");
    sProduit.createFeatureAttributeValue(nature15,
        "Limite des plus hautes eaux naturelle non rocheuse");
    sProduit.createFeatureAttributeValue(nature15,
        "Limite des plus hautes eaux naturelle rocheuse");
    sProduit.createFeatureAttributeValue(nature15,
        "Limite des plus hautes eaux artificielle (digue, quai)");
    sProduit.createFeatureAttributeValue(nature15,
        "Limite des plus hautes eaux fermeture arbitraire d'un estuaire");
    sProduit.createFeatureAttributeValue(nature15,
        "Limite des plus basses eaux naturelle (zéro bathymétrique)");
    sProduit.createFeatureAttributeValue(nature15,
        "Limite des plus basses eaux fermeture arbitraire d'un estuaire");

    // Classe Zone d'hydrographie de texture//////////////////////////////////

    sProduit.createFeatureType("Zone d'hydrographie de texture");
    FeatureType zoneDHydrographieDeTexture = (FeatureType) (sProduit
        .getFeatureTypeByName("Zone d'hydrographie de texture"));
    zoneDHydrographieDeTexture.setDefinition("");
    zoneDHydrographieDeTexture.setIsAbstract(false);

    // Attribut Toponyme
    sProduit.createFeatureAttribute(zoneDHydrographieDeTexture, "Toponyme",
        "string", false);
    AttributeType toponyme24 = zoneDHydrographieDeTexture
        .getFeatureAttributeByName("Toponyme");
    toponyme24.setDefinition("");

    // Attribut Candidat
    sProduit.createFeatureAttribute(zoneDHydrographieDeTexture, "Candidat",
        "string", false);
    AttributeType candidat5 = zoneDHydrographieDeTexture
        .getFeatureAttributeByName("Candidat");
    candidat5.setDefinition("");

    // Classe Element surfacique//////////////////////////////////////////////

    sProduit.createFeatureType("Element surfacique");
    FeatureType elementSurfacique = (FeatureType) (sProduit
        .getFeatureTypeByName("Element surfacique"));
    elementSurfacique.setDefinition("");
    elementSurfacique.setIsAbstract(false);

    // Attribut Nature
    sProduit
        .createFeatureAttribute(elementSurfacique, "Nature", "string", true);
    AttributeType nature16 = elementSurfacique
        .getFeatureAttributeByName("Nature");
    nature16.setDefinition("");
    sProduit.createFeatureAttributeValue(nature16, "En attente de mise à jour");
    sProduit.createFeatureAttributeValue(nature16, "Inconnu");
    sProduit.createFeatureAttributeValue(nature16, "Névés, glaciers");
    sProduit.createFeatureAttributeValue(nature16, "Eau douce permanente");
    sProduit.createFeatureAttributeValue(nature16, "Eau salée permanente");
    sProduit.createFeatureAttributeValue(nature16, "Eau salée non permanente");

    // Attribut Type
    sProduit.createFeatureAttribute(elementSurfacique, "Type", "string", true);
    AttributeType type1 = elementSurfacique.getFeatureAttributeByName("Type");
    type1.setDefinition("");
    sProduit.createFeatureAttributeValue(type1, "En attente de mise à jour");
    sProduit.createFeatureAttributeValue(type1, "Inconnu");
    sProduit.createFeatureAttributeValue(type1,
        "Eau douce permanente, cours d'eau (largeur > 50 m)");
    sProduit.createFeatureAttributeValue(type1,
        "Eau douce permanente, plan d'eau, bassin, réservoir");
    sProduit.createFeatureAttributeValue(type1,
        "Eau douce permanente, ensemble de petits plans d'eau");
    sProduit.createFeatureAttributeValue(type1,
        "Eau douce permanente, traitement des eaux, station de pompage");
    sProduit.createFeatureAttributeValue(type1,
        "Eau douce permanente, bassin portuaire fluvial");
    sProduit.createFeatureAttributeValue(type1,
        "Eau douce non permanente, zone temporairement recouverte d'eau");
    sProduit
        .createFeatureAttributeValue(type1,
            "Eau douce non permanente, sable et graviers dans le lit d'un cours d'eau");
    sProduit.createFeatureAttributeValue(type1,
        "Eau salée permanente, pleine mer");
    sProduit.createFeatureAttributeValue(type1,
        "Eau salée permanente, écoulement d'eau");
    sProduit.createFeatureAttributeValue(type1,
        "Eau salée permanente, nappe d'eau");
    sProduit.createFeatureAttributeValue(type1,
        "Eau salée permanente, bassin portuaire");
    sProduit.createFeatureAttributeValue(type1,
        "Eau salée non permanente, marais salants");
    sProduit.createFeatureAttributeValue(type1,
        "Eau salée non permanente, zone rocheuse");
    sProduit.createFeatureAttributeValue(type1,
        "Eau salée non permanente, zone mixte rochers et sable");
    sProduit.createFeatureAttributeValue(type1,
        "Eau salée non permanente, zone de sable humide");
    sProduit.createFeatureAttributeValue(type1,
        "Eau salée non permanente, zone de vase");
    sProduit.createFeatureAttributeValue(type1,
        "Eau salée non permanente, zone de graviers et galets");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(elementSurfacique, "Toponyme", "string",
        false);
    AttributeType toponyme25 = elementSurfacique
        .getFeatureAttributeByName("Toponyme");
    toponyme25.setDefinition("");

    // Classe Zone hydrographique/////////////////////////////////////////////

    sProduit.createFeatureType("Zone hydrographique");
    FeatureType zoneHydrographique = (FeatureType) (sProduit
        .getFeatureTypeByName("Zone hydrographique"));
    zoneHydrographique.setDefinition("");
    zoneHydrographique.setIsAbstract(false);

    // Attribut Zone hydrographique
    sProduit.createFeatureAttribute(zoneHydrographique, "Zone hydrographique",
        "string", false);
    AttributeType zoneHydrographique2 = zoneHydrographique
        .getFeatureAttributeByName("Zone hydrographique");
    zoneHydrographique2.setDefinition("");

    // Attribut Libellé
    sProduit.createFeatureAttribute(zoneHydrographique, "Libellé", "string",
        false);
    AttributeType libelle = zoneHydrographique
        .getFeatureAttributeByName("Libellé");
    libelle.setDefinition("");

    // Classe Cours d'eau/////////////////////////////////////////////////////

    sProduit.createFeatureType("Cours d'eau");
    FeatureType coursDEau = (FeatureType) (sProduit
        .getFeatureTypeByName("Cours d'eau"));
    coursDEau.setDefinition("");
    coursDEau.setIsAbstract(false);

    // Attribut CGENELIN
    sProduit.createFeatureAttribute(coursDEau, "CGENELIN", "string", false);
    AttributeType cGENELIN = coursDEau.getFeatureAttributeByName("CGENELIN");
    cGENELIN.setDefinition("");

    // Attribut Classification
    sProduit
        .createFeatureAttribute(coursDEau, "Classification", "string", true);
    AttributeType classification2 = coursDEau
        .getFeatureAttributeByName("Classification");
    classification2.setDefinition("");
    sProduit
        .createFeatureAttributeValue(
            classification2,
            "Tout cours d'eau d'une longueur supérieure à 100 km ou tout cours d'eau se jetant dans une embouchure logique et d'une longueur supérieure à 25 km");
    sProduit
        .createFeatureAttributeValue(
            classification2,
            "Tout cours d'eau d'une longueur comprise entre 50 et 100 km ou tout cours d'eau se jetant dans une embouchure logique et d'une longueur supérieure à 10 km");
    sProduit.createFeatureAttributeValue(classification2,
        "Tout cours d'eau d'une longueur comprise entre 25 et 50 km");
    sProduit.createFeatureAttributeValue(classification2,
        "Tout cours d'eau d'une longueur comprise entre 10 et 25 km");
    sProduit.createFeatureAttributeValue(classification2,
        "Tout cours d'eau d'une longueur comprise entre 5 et 10 km");
    sProduit
        .createFeatureAttributeValue(classification2,
            "Tous les autres cours d'eau hormis ceux issus de la densification du réseau");
    sProduit.createFeatureAttributeValue(classification2,
        "Cours d'eau issus de la densification du réseau");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(coursDEau, "Toponyme", "string", false);
    AttributeType toponyme26 = coursDEau.getFeatureAttributeByName("Toponyme");
    toponyme26.setDefinition("");

    // Attribut Candidat
    sProduit.createFeatureAttribute(coursDEau, "Candidat", "string", false);
    AttributeType candidat6 = coursDEau.getFeatureAttributeByName("Candidat");
    candidat6.setDefinition("");

    // Classe Entité hydrographique de surface

    sProduit.createFeatureType("Entité hydrographique de surface");
    FeatureType entiteHydrographiqueDeSurface = (FeatureType) (sProduit
        .getFeatureTypeByName("Entité hydrographique de surface"));
    entiteHydrographiqueDeSurface.setDefinition("");
    entiteHydrographiqueDeSurface.setIsAbstract(false);

    // Attribut CGENESUR
    sProduit.createFeatureAttribute(entiteHydrographiqueDeSurface, "CGENESUR",
        "string", false);
    AttributeType cGENESUR = entiteHydrographiqueDeSurface
        .getFeatureAttributeByName("CGENESUR");
    cGENESUR.setDefinition("");

    // Attribut Sous-milieu hydrographique
    sProduit.createFeatureAttribute(entiteHydrographiqueDeSurface,
        "Sous-milieu hydrographique", "string", false);
    AttributeType sousMilieuHydrographique2 = entiteHydrographiqueDeSurface
        .getFeatureAttributeByName("Sous-milieu hydrographique");
    sousMilieuHydrographique2.setDefinition("");

    // Attribut Classe
    sProduit.createFeatureAttribute(entiteHydrographiqueDeSurface, "Classe",
        "string", true);
    AttributeType classe = entiteHydrographiqueDeSurface
        .getFeatureAttributeByName("Classe");
    classe.setDefinition("");
    sProduit.createFeatureAttributeValue(classe,
        "Les entités dont la surface est supérieure à 100 ha");
    sProduit.createFeatureAttributeValue(classe,
        "Les entités dont la surface est comprise entre 25 et 100 ha");
    sProduit.createFeatureAttributeValue(classe,
        "Les entités dont la surface est comprise entre 18 et 25 ha");
    sProduit.createFeatureAttributeValue(classe,
        "Les entités dont la surface est comprise entre 8 et 18 ha");
    sProduit.createFeatureAttributeValue(classe,
        "Les entités dont la surface est comprise entre 4 et 8 ha");
    sProduit.createFeatureAttributeValue(classe,
        "Les entités dont la surface est comprise entre 1 et 4 ha");
    sProduit.createFeatureAttributeValue(classe,
        "Les entités dont la surface inférieure à 1 ha");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(entiteHydrographiqueDeSurface, "Toponyme",
        "string", false);
    AttributeType toponyme27 = entiteHydrographiqueDeSurface
        .getFeatureAttributeByName("Toponyme");
    toponyme27.setDefinition("");

    // Attribut Candidat
    sProduit.createFeatureAttribute(entiteHydrographiqueDeSurface, "Candidat",
        "string", false);
    AttributeType candidat7 = entiteHydrographiqueDeSurface
        .getFeatureAttributeByName("Candidat");
    candidat7.setDefinition("");

    // Classe Sous-secteur////////////////////////////////////////////////////

    sProduit.createFeatureType("Sous-secteur");
    FeatureType sousSecteur = (FeatureType) (sProduit
        .getFeatureTypeByName("Sous-secteur"));
    sousSecteur.setDefinition("");
    sousSecteur.setIsAbstract(false);

    // Attribut Code
    sProduit.createFeatureAttribute(sousSecteur, "Code", "string", false);
    AttributeType code1 = sousSecteur.getFeatureAttributeByName("Code");
    code1.setDefinition("");

    // Attribut Libellé
    sProduit.createFeatureAttribute(sousSecteur, "Libellé", "string", false);
    AttributeType libelle1 = sousSecteur.getFeatureAttributeByName("Libellé");
    libelle1.setDefinition("");

    // Classe Secteur/////////////////////////////////////////////////////////

    sProduit.createFeatureType("Secteur");
    FeatureType secteur = (FeatureType) (sProduit
        .getFeatureTypeByName("Secteur"));
    secteur.setDefinition("");
    secteur.setIsAbstract(false);

    // Attribut Code
    sProduit.createFeatureAttribute(secteur, "Code", "string", false);
    AttributeType code2 = secteur.getFeatureAttributeByName("Code");
    code2.setDefinition("");

    // Attribut Libellé
    sProduit.createFeatureAttribute(secteur, "Libellé", "string", false);
    AttributeType libelle2 = secteur.getFeatureAttributeByName("Libellé");
    libelle2.setDefinition("");

    // Classe Region//////////////////////////////////////////////////////////

    sProduit.createFeatureType("Region");
    FeatureType region = (FeatureType) (sProduit.getFeatureTypeByName("Region"));
    region.setDefinition("");
    region.setIsAbstract(false);

    // Attribut Code
    sProduit.createFeatureAttribute(region, "Code", "string", false);
    AttributeType code3 = region.getFeatureAttributeByName("Code");
    code3.setDefinition("");

    // Attribut Libellé
    sProduit.createFeatureAttribute(region, "Libellé", "string", false);
    AttributeType libelle3 = region.getFeatureAttributeByName("Libellé");
    libelle3.setDefinition("");

    // Relation d'association réseau hydrographique
    sProduit.createFeatureAssociation("Réseau hydrographique initial",
        tronconHydrographique, noeudHydrographique, "a pour noeud initial",
        "est le noeud initial de");
    sProduit.createFeatureAssociation("Réseau hydrographique final",
        tronconHydrographique, noeudHydrographique, "a pour noeud final",
        "est le noeud final de");

    // Relation d'association superposition
    sProduit.createFeatureAssociation("Superposition", tronconHydrographique,
        tronconHydrographique, "se superpose à", "se superpose à");

    // Relation d'association exutoire
    sProduit.createFeatureAssociation("Exutoire", noeudHydrographique,
        zoneHydrographique, "est l'exutoire de", "a pour exutoire");

    // Relation d'association drain principale
    sProduit.createFeatureAssociation("Drain principale", coursDEau,
        zoneHydrographique, "est le drain principal de",
        "a pour drain principal");

    // Relation d'association est traversée par
    sProduit.createFeatureAssociation("Est traversée par",
        entiteHydrographiqueDeSurface, coursDEau, "est traversée par",
        "traverse");

    // Relation d'association passe par
    sProduit.createFeatureAssociation("Passe par", tronconHydrographique,
        entiteHydrographiqueDeSurface, "Passe par", "est traversé par");

    /***************************************************************************
     * Ajout du thème "Franchissement" *
     ************************************************************************/

    // Classe Franchissement

    sProduit.createFeatureType("Franchissement");
    FeatureType franchissement = (FeatureType) (sProduit
        .getFeatureTypeByName("Franchissement"));
    franchissement.setDefinition("");
    franchissement.setIsAbstract(false);

    // Attribut Toponyme
    sProduit
        .createFeatureAttribute(franchissement, "Toponyme", "string", false);
    AttributeType toponyme28 = franchissement
        .getFeatureAttributeByName("Toponyme");
    toponyme28.setDefinition("");

    // Attribut Cote
    sProduit.createFeatureAttribute(franchissement, "Cote", "string", false);
    AttributeType cote3 = franchissement.getFeatureAttributeByName("Cote");
    cote3.setDefinition("");

    // relation Tronçon de route passe par franchissement
    sProduit.createFeatureAssociation(
        "Tronçon de route passe par franchissement", franchissement,
        tronconDeRoute, "passe par", "est traversé par");

    // relation Tronçon de voie ferrée passe par franchissement
    sProduit.createFeatureAssociation(
        "Tronçon hydrographique passe par franchissement", franchissement,
        tronconDeVoieFerree, "passe par", "est traversé par");

    // Relation Tronçon hydrographique passe par franchissement
    sProduit.createFeatureAssociation(
        "Tronçon de voie ferrée passe par franchissement", franchissement,
        tronconHydrographique, "passe par", "est traversé par");

    /***************************************************************************
     * Ajout du thème "Autres équipements" *
     ************************************************************************/

    // Classe Tronçon de ligne électrique

    sProduit.createFeatureType("Tronçon de ligne électrique");
    FeatureType tronconDeLignElectrique = (FeatureType) (sProduit
        .getFeatureTypeByName("Tronçon de ligne électrique"));
    tronconDeLignElectrique.setDefinition("");
    tronconDeLignElectrique.setIsAbstract(false);

    // Attribut Type du tracé
    sProduit.createFeatureAttribute(tronconDeLignElectrique, "Type du tracé",
        "string", true);
    AttributeType typeDuTrace = tronconDeLignElectrique
        .getFeatureAttributeByName("Type du tracé");
    typeDuTrace.setDefinition("");
    sProduit.createFeatureAttributeValue(typeDuTrace, "Aérien");
    sProduit.createFeatureAttributeValue(typeDuTrace, "Souterrain");
    sProduit.createFeatureAttributeValue(typeDuTrace,
        "Fictif (prolongation des lignes électriques à l'intérieur des postes");

    // Attribut Tension
    sProduit.createFeatureAttribute(tronconDeLignElectrique, "Tension",
        "string", true);
    AttributeType tension = tronconDeLignElectrique
        .getFeatureAttributeByName("Tension");
    tension.setDefinition("");
    sProduit
        .createFeatureAttributeValue(tension, "Inférieure ou égale à 42 kV");
    sProduit.createFeatureAttributeValue(tension, "Comprise entre 42 et 63 kV");
    sProduit.createFeatureAttributeValue(tension, "Comprise entre 63 et 90 kV");
    sProduit
        .createFeatureAttributeValue(tension, "Comprise entre 90 et 150 kV");
    sProduit.createFeatureAttributeValue(tension,
        "Comprise entre 150 et 225 kV");
    sProduit.createFeatureAttributeValue(tension,
        "Comprise entre 225 et 400 kV");

    // Classe Construction élevée

    sProduit.createFeatureType("Construction élevée");
    FeatureType constructionElevee = (FeatureType) (sProduit
        .getFeatureTypeByName("Construction élevée"));
    constructionElevee.setDefinition("");
    constructionElevee.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(constructionElevee, "Nature", "string",
        true);
    AttributeType nature17 = constructionElevee
        .getFeatureAttributeByName("Nature");
    nature17.setDefinition("");
    sProduit.createFeatureAttributeValue(nature17, "Pylône");
    sProduit
        .createFeatureAttributeValue(nature17, "Tour de télécommunications");
    sProduit.createFeatureAttributeValue(nature17, "Antenne");
    sProduit.createFeatureAttributeValue(nature17, "Silo, château d'eau");
    sProduit.createFeatureAttributeValue(nature17, "Cheminée");

    // Classe Transport par câble

    sProduit.createFeatureType("Transport par câble");
    FeatureType transportParCable = (FeatureType) (sProduit
        .getFeatureTypeByName("Transport par câble"));
    transportParCable.setDefinition("");
    transportParCable.setIsAbstract(false);

    // Attribut Nature
    sProduit
        .createFeatureAttribute(transportParCable, "Nature", "string", true);
    AttributeType nature18 = transportParCable
        .getFeatureAttributeByName("Nature");
    nature18.setDefinition("");
    sProduit.createFeatureAttributeValue(nature18,
        "Téléphérique ou télécabine à usage de loisirs");
    sProduit.createFeatureAttributeValue(nature18,
        "Télésiège ou téléski à usage de loisirs");
    sProduit.createFeatureAttributeValue(nature18,
        "Transport par câble à usage privé ou industriel");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(transportParCable, "Toponyme", "string",
        false);
    AttributeType toponyme29 = transportParCable
        .getFeatureAttributeByName("Toponyme");
    toponyme29.setDefinition("");

    // Classe Aérodrome

    sProduit.createFeatureType("Aérodrome");
    FeatureType aerodrome = (FeatureType) (sProduit
        .getFeatureTypeByName("Aérodrome"));
    aerodrome.setDefinition("");
    aerodrome.setIsAbstract(false);

    // AttributNature
    sProduit.createFeatureAttribute(aerodrome, "Nature", "string", true);
    AttributeType nature19 = aerodrome.getFeatureAttributeByName("Nature");
    nature19.setDefinition("");
    sProduit.createFeatureAttributeValue(nature19, "Normal");
    sProduit.createFeatureAttributeValue(nature19, "D'altitude");
    sProduit.createFeatureAttributeValue(nature19, "Sur l'eau");

    // Attribut Accès
    sProduit.createFeatureAttribute(aerodrome, "Accès", "string", true);
    AttributeType acces3 = aerodrome.getFeatureAttributeByName("Accès");
    acces3.setDefinition("");
    sProduit.createFeatureAttributeValue(acces3,
        "Desservi par au moins une ligne régulière de transport de voyageurs");
    sProduit.createFeatureAttributeValue(acces3,
        "Desservi par aucune ligne régulière de transport de voyageurs");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(aerodrome, "Toponyme", "string", false);
    AttributeType toponyme30 = aerodrome.getFeatureAttributeByName("Toponyme");
    toponyme30.setDefinition("");

    // Classe Piste d'aérodrome

    sProduit.createFeatureType("Piste d'aérodrome");
    FeatureType pisteDAerodrome = (FeatureType) (sProduit
        .getFeatureTypeByName("Piste d'aérodrome"));
    pisteDAerodrome.setDefinition("");
    pisteDAerodrome.setIsAbstract(false);

    // Classe Cimetière

    sProduit.createFeatureType("Cimetière");
    FeatureType cimetiere = (FeatureType) (sProduit
        .getFeatureTypeByName("Cimetière"));
    cimetiere.setDefinition("");
    cimetiere.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(cimetiere, "Nature", "string", true);
    AttributeType nature20 = cimetiere.getFeatureAttributeByName("Nature");
    nature20.setDefinition("");
    sProduit.createFeatureAttributeValue(nature20, "Civil");
    sProduit.createFeatureAttributeValue(nature20, "Nécropole nationale");
    sProduit.createFeatureAttributeValue(nature20,
        "Cimetière militaire étranger");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(cimetiere, "Toponyme", "string", true);
    AttributeType toponyme31 = cimetiere.getFeatureAttributeByName("Toponyme");
    toponyme31.setDefinition("");

    // Classe Enceinte militaire

    sProduit.createFeatureType("Enceinte militaire");
    FeatureType enceinteMilitaire = (FeatureType) (sProduit
        .getFeatureTypeByName("Enceinte militaire"));
    enceinteMilitaire.setDefinition("");
    enceinteMilitaire.setIsAbstract(false);

    // Attribut Nature
    sProduit
        .createFeatureAttribute(enceinteMilitaire, "Nature", "string", true);
    AttributeType nature21 = enceinteMilitaire
        .getFeatureAttributeByName("Nature");
    nature21.setDefinition("");
    sProduit.createFeatureAttributeValue(nature21, "Fort et citadelle");
    sProduit.createFeatureAttributeValue(nature21, "Terrain militaire");
    sProduit.createFeatureAttributeValue(nature21, "Champ de tir");

    // Attribut Caractère touristique
    sProduit.createFeatureAttribute(enceinteMilitaire, "Caractère touristique",
        "string", true);
    AttributeType caractereTouristique2 = enceinteMilitaire
        .getFeatureAttributeByName("Caractère touristique");
    caractereTouristique2.setDefinition("");
    sProduit
        .createFeatureAttributeValue(
            caractereTouristique2,
            "Sans objet: uniquement et obligatoirement pour les terrains militaires et les champs de tir");
    sProduit.createFeatureAttributeValue(caractereTouristique2, "Oui");
    sProduit.createFeatureAttributeValue(caractereTouristique2, "Non");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(enceinteMilitaire, "Toponyme", "string",
        false);
    AttributeType toponyme32 = enceinteMilitaire
        .getFeatureAttributeByName("Toponyme");
    toponyme32.setDefinition("");

    // Classe Digue

    sProduit.createFeatureType("Digue");
    FeatureType digue = (FeatureType) (sProduit.getFeatureTypeByName("Digue"));
    digue.setDefinition("");
    digue.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(digue, "Nature", "string", true);
    AttributeType nature22 = digue.getFeatureAttributeByName("Nature");
    nature22.setDefinition("");
    sProduit.createFeatureAttributeValue(nature22,
        "Digue délimitant un barrage");
    sProduit.createFeatureAttributeValue(nature22, "Autre digue");

    // Classe Métro aérien

    sProduit.createFeatureType("Métro aérien");
    FeatureType metroAerien = (FeatureType) (sProduit
        .getFeatureTypeByName("Métro aérien"));
    metroAerien.setDefinition("");
    metroAerien.setIsAbstract(false);

    // Classe Sentier de grande randonnée

    sProduit.createFeatureType("Sentier de grande randonnée");
    FeatureType sentierDeGrandeRandonnee = (FeatureType) (sProduit
        .getFeatureTypeByName("Sentier de grande randonnée"));
    sentierDeGrandeRandonnee.setDefinition("");
    sentierDeGrandeRandonnee.setIsAbstract(false);

    // Attribut Numéro
    sProduit.createFeatureAttribute(sentierDeGrandeRandonnee, "Numéro",
        "string", false);
    AttributeType numero = sentierDeGrandeRandonnee
        .getFeatureAttributeByName("Numéro");
    numero.setDefinition("");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(sentierDeGrandeRandonnee, "Toponyme",
        "string", false);
    AttributeType toponyme33 = sentierDeGrandeRandonnee
        .getFeatureAttributeByName("Toponyme");
    toponyme33.setDefinition("");

    /***************************************************************************
     * Ajout du thème "Point remarquable du relief; massif boisé" *
     ************************************************************************/

    // Classe Point remarquable du relief

    sProduit.createFeatureType("Point remarquable du relief");
    FeatureType pointRemarquableDuRelief = (FeatureType) (sProduit
        .getFeatureTypeByName("Point remarquable du relief"));
    pointRemarquableDuRelief.setDefinition("");
    pointRemarquableDuRelief.setIsAbstract(false);

    // Attribut Toponyme
    sProduit.createFeatureAttribute(pointRemarquableDuRelief, "Toponyme",
        "string", false);
    AttributeType toponyme34 = pointRemarquableDuRelief
        .getFeatureAttributeByName("Toponyme");
    toponyme34.setDefinition("");

    // Attribut Classification
    sProduit.createFeatureAttribute(pointRemarquableDuRelief, "Classification",
        "string", true);
    AttributeType classification3 = pointRemarquableDuRelief
        .getFeatureAttributeByName("Classification");
    classification3.setDefinition("");
    sProduit.createFeatureAttributeValue(classification3, "2-Très important");
    sProduit.createFeatureAttributeValue(classification3, "3");
    sProduit.createFeatureAttributeValue(classification3, "4");
    sProduit.createFeatureAttributeValue(classification3, "5");
    sProduit.createFeatureAttributeValue(classification3, "6");
    sProduit.createFeatureAttributeValue(classification3, "7");
    sProduit.createFeatureAttributeValue(classification3, "8");
    sProduit.createFeatureAttributeValue(classification3, "9-Peu important");

    // Attribut Nature
    sProduit.createFeatureAttribute(pointRemarquableDuRelief, "Nature",
        "string", true);
    AttributeType nature23 = pointRemarquableDuRelief
        .getFeatureAttributeByName("Nature");
    nature23.setDefinition("");
    sProduit.createFeatureAttributeValue(nature23, "Cap, point promontoire");
    sProduit.createFeatureAttributeValue(nature23, "Cirque");
    sProduit.createFeatureAttributeValue(nature23, "Col, passage, cluse");
    sProduit.createFeatureAttributeValue(nature23, "Cratère, volcan");
    sProduit
        .createFeatureAttributeValue(
            nature23,
            "Crète, arête, ligne de faîte; chaîne de montagne, montagne, massif rocheux; mont, colline, mamelon, sommet");
    sProduit.createFeatureAttributeValue(nature23, "Coteau, versant, falaise");
    sProduit.createFeatureAttributeValue(nature23,
        "Cuvette, bassin fermé, doline, dépression");
    sProduit.createFeatureAttributeValue(nature23,
        "Défilé, gorge, canyon; val, vallée, vallon, ravin, thalweg, combe");
    sProduit.createFeatureAttributeValue(nature23, "île, îlot, presqu'île");
    sProduit.createFeatureAttributeValue(nature23,
        "Dune; isthme, cordon littoral;plage, grâve");
    sProduit.createFeatureAttributeValue(nature23, "Pic, aiguille, piton");
    sProduit.createFeatureAttributeValue(nature23, "Plaine, plateau");
    sProduit.createFeatureAttributeValue(nature23, "Récifs, brisants, rochers");
    sProduit.createFeatureAttributeValue(nature23,
        "Chaos, rocher, escarpement rocheux");
    sProduit.createFeatureAttributeValue(nature23,
        "Baie, golfe, anse, crique, calanque, espace marin divers");
    sProduit.createFeatureAttributeValue(nature23, "Bans, haut-fond");

    // Attribut Cote
    sProduit.createFeatureAttribute(pointRemarquableDuRelief, "Cote", "entier",
        false);
    AttributeType cote4 = pointRemarquableDuRelief
        .getFeatureAttributeByName("Cote");
    cote4.setDefinition("");

    // Attribut Caractère touristique
    sProduit.createFeatureAttribute(pointRemarquableDuRelief,
        "Caractère touristique", "string", true);
    AttributeType caractereTouristique3 = pointRemarquableDuRelief
        .getFeatureAttributeByName("Caractère touristique");
    caractereTouristique3.setDefinition("");
    sProduit.createFeatureAttributeValue(caractereTouristique3, "Oui");
    sProduit.createFeatureAttributeValue(caractereTouristique3, "Non");

    // Classe Massif boisé

    sProduit.createFeatureType("Massif boisé");
    FeatureType massifBoise = (FeatureType) (sProduit
        .getFeatureTypeByName("Massif boisé"));
    massifBoise.setDefinition("");
    massifBoise.setIsAbstract(false);

    // Attribut Toponyme
    sProduit.createFeatureAttribute(massifBoise, "Toponyme", "string", false);
    AttributeType toponyme35 = massifBoise
        .getFeatureAttributeByName("Toponyme");
    toponyme35.setDefinition("");

    // Attribut Classification
    sProduit.createFeatureAttribute(massifBoise, "Classification", "string",
        true);
    AttributeType classification4 = massifBoise
        .getFeatureAttributeByName("Classification");
    classification4.setDefinition("");
    sProduit.createFeatureAttributeValue(classification4,
        "Bois ou forêt de superficie supérieure à 2000 ha");
    sProduit.createFeatureAttributeValue(classification4,
        "Bois ou forêt de superficie comprise entre 1000 et 2000 ha");
    sProduit.createFeatureAttributeValue(classification4,
        "Bois ou forêt de superficie comprise entre 500 et 1000 ha");

    // Attribut Caractère touristique
    sProduit.createFeatureAttribute(massifBoise, "Caractère touristique",
        "string", true);
    AttributeType caractereTouristique4 = massifBoise
        .getFeatureAttributeByName("Caractère touristique");
    caractereTouristique4.setDefinition("");
    sProduit.createFeatureAttributeValue(caractereTouristique4, "Oui");
    sProduit.createFeatureAttributeValue(caractereTouristique4, "Non");

    /***************************************************************************
     * Ajout du thème "Administratif" *
     ************************************************************************/

    // Classe Territoire étranger

    sProduit.createFeatureType("Territoire étranger");
    FeatureType territoireEtranger = (FeatureType) (sProduit
        .getFeatureTypeByName("Territoire étranger"));
    territoireEtranger.setDefinition("");
    territoireEtranger.setIsAbstract(false);

    // Attribut Nom
    sProduit.createFeatureAttribute(territoireEtranger, "Nom", "string", true);
    AttributeType nom = territoireEtranger.getFeatureAttributeByName("Nom");
    nom.setDefinition("");
    sProduit.createFeatureAttributeValue(nom, "Belgique");
    sProduit.createFeatureAttributeValue(nom, "Luxembourg");
    sProduit.createFeatureAttributeValue(nom, "Allemagne");
    sProduit.createFeatureAttributeValue(nom, "Suisse");
    sProduit.createFeatureAttributeValue(nom, "Italie");
    sProduit.createFeatureAttributeValue(nom, "Monaco");
    sProduit.createFeatureAttributeValue(nom, "Espagne");
    sProduit.createFeatureAttributeValue(nom, "Andorre");
    sProduit.createFeatureAttributeValue(nom, "Royaume-Uni");
    sProduit.createFeatureAttributeValue(nom,
        "Domaine marin (au delà de la laisse des plus hautes mers)");

    // Atribut Numéro INSEE
    sProduit.createFeatureAttribute(territoireEtranger, "Numéro INSEE",
        "string", true);
    AttributeType numeroINSEE = territoireEtranger
        .getFeatureAttributeByName("Numéro INSEE");
    numeroINSEE.setDefinition("");
    sProduit.createFeatureAttributeValue(nom, "99131-Belgique");
    sProduit.createFeatureAttributeValue(nom, "99137-Luxembourg");
    sProduit.createFeatureAttributeValue(nom, "99109-Allemagne");
    sProduit.createFeatureAttributeValue(nom, "99140-Suisse");
    sProduit.createFeatureAttributeValue(nom, "99127-Italie");
    sProduit.createFeatureAttributeValue(nom, "99138-Monaco");
    sProduit.createFeatureAttributeValue(nom, "99134-Espagne");
    sProduit.createFeatureAttributeValue(nom, "99130-Andorre");
    sProduit.createFeatureAttributeValue(nom, "99132-Royaume-Uni");
    sProduit.createFeatureAttributeValue(nom,
        "00000-Domaine marin (au delà de la laisse des plus hautes mers)");

    // Classe Commune

    sProduit.createFeatureType("Commune");
    FeatureType commune1 = (FeatureType) (sProduit
        .getFeatureTypeByName("Commune"));
    commune1.setDefinition("");
    commune1.setIsAbstract(false);

    // Attribut Lien BDAdministrative
    sProduit.createFeatureAttribute(commune1, "Lien BDAdministrative",
        "string", false);
    AttributeType lienBDAdministrative = commune1
        .getFeatureAttributeByName("Lien BDAdministrative");
    lienBDAdministrative.setDefinition("");

    // Attribut Numéro INSEE
    sProduit.createFeatureAttribute(commune1, "Numéro INSEE", "string", false);
    AttributeType numeroINSEE2 = commune1
        .getFeatureAttributeByName("Numéro INSEE");
    numeroINSEE2.setDefinition("");

    // Attribut Centroîde
    sProduit.createFeatureAttribute(commune1, "Centroîde",
        "coordonnées en Lambert II", false);
    AttributeType centroide = commune1.getFeatureAttributeByName("Centroîde");
    centroide.setDefinition("");

    // Classe Limite administrative

    sProduit.createFeatureType("Limite administrative");
    FeatureType limiteAdministrative = (FeatureType) (sProduit
        .getFeatureTypeByName("Limite administrative"));
    limiteAdministrative.setDefinition("");
    limiteAdministrative.setIsAbstract(false);

    // Attribut Type
    sProduit.createFeatureAttribute(limiteAdministrative, "Type", "string",
        true);
    AttributeType type4 = limiteAdministrative
        .getFeatureAttributeByName("Type");
    type4.setDefinition("");
    sProduit.createFeatureAttributeValue(type4,
        "Limite côtière (laisse des plus hautes eaux)");
    sProduit.createFeatureAttributeValue(type4, "Frontière internationale");
    sProduit.createFeatureAttributeValue(type4, "Limite de région");
    sProduit.createFeatureAttributeValue(type4, "Limite de département");
    sProduit.createFeatureAttributeValue(type4, "Limite d'arrondissement");
    sProduit.createFeatureAttributeValue(type4, "Limite de pseudo-canto");
    sProduit.createFeatureAttributeValue(type4, "Limite de commune");

    // Attribut Précision
    sProduit.createFeatureAttribute(limiteAdministrative, "Précision",
        "string", true);
    AttributeType precision = limiteAdministrative
        .getFeatureAttributeByName("Précision");
    precision.setDefinition("");
    sProduit.createFeatureAttributeValue(precision,
        "Précision standard de localisation");
    sProduit
        .createFeatureAttributeValue(
            precision,
            "Précision non définie (en particulier limite s'appuyant sur des surfaces d'eau domaine public de l'Etat)");

    // Relation d'association A pour chef-lieu
    sProduit.createFeatureAssociation("A pour chef-lieu", commune1,
        zoneDHabitat, "A pour chef-lieu", "est chef-lieu de");

    /***************************************************************************
     * Ajout du thème "Occupation du sol" *
     ************************************************************************/

    // Classe Zone d'occupation du sol
    sProduit.createFeatureType("Zone d'occupation du sol");
    FeatureType zoneDOccupationDuSol = (FeatureType) (sProduit
        .getFeatureTypeByName("Zone d'occupation du sol"));
    zoneDOccupationDuSol.setDefinition("");
    zoneDOccupationDuSol.setIsAbstract(false);

    // Attribut Poste
    sProduit.createFeatureAttribute(zoneDOccupationDuSol, "Poste", "string",
        true);
    AttributeType poste = zoneDOccupationDuSol
        .getFeatureAttributeByName("Poste");
    poste.setDefinition("");
    sProduit.createFeatureAttributeValue(poste, "Bâti");
    sProduit.createFeatureAttributeValue(poste, "Zone d'activités");
    sProduit.createFeatureAttributeValue(poste, "Carrière, décharge");
    sProduit
        .createFeatureAttributeValue(
            poste,
            "Prairie, pelouse, toute culture hormis vigne, verger, bananeraie, canne à sucre, rizière");
    sProduit.createFeatureAttributeValue(poste,
        "Vigne, verger, bananeraie, canne à sucre, rizière");
    sProduit.createFeatureAttributeValue(poste, "Forêt");
    sProduit.createFeatureAttributeValue(poste, "Brousailles");
    sProduit
        .createFeatureAttributeValue(poste,
            "Plage, dune, sable, gravier, galet ou terrain nu sans couvert végétal");
    sProduit.createFeatureAttributeValue(poste, "Rocher, éboulis");
    sProduit.createFeatureAttributeValue(poste, "Mangrove");
    sProduit.createFeatureAttributeValue(poste, "Marais, tourbière");
    sProduit.createFeatureAttributeValue(poste, "Marais salants");
    sProduit.createFeatureAttributeValue(poste, "Eau libre");
    sProduit.createFeatureAttributeValue(poste, "Glacier, névé");
    sProduit.createFeatureAttributeValue(poste, "Territoires construits");
    sProduit.createFeatureAttributeValue(poste,
        "Culture, prairie, végétation naturelle hors forêt");
    sProduit.createFeatureAttributeValue(poste, "Forêt");
    sProduit.createFeatureAttributeValue(poste,
        "Plage, dune, sable, gravier, galets");
    sProduit.createFeatureAttributeValue(poste, "Rocher, éboulis");
    sProduit.createFeatureAttributeValue(poste, "Marais, tourbière");
    sProduit.createFeatureAttributeValue(poste, "Marais salants");
    sProduit.createFeatureAttributeValue(poste, "Eau libre");
    sProduit.createFeatureAttributeValue(poste, "Glaciers, névé");

    /***************************************************************************
     * FIN DU SCHEMA *
     ************************************************************************/

    Integer compteurFT = 0;
    Integer compteurAT = 0;
    Integer compteurV = 0;
    System.out.println("Récapitulons:");
    List<FeatureType> listeFT = sProduit.getFeatureTypes();
    for (GF_FeatureType type : listeFT) {
      compteurFT = compteurFT + 1;
      System.out.println("Classe: " + type.getTypeName());
      List<GF_AttributeType> listeAT = type.getFeatureAttributes();
      for (GF_AttributeType type2 : listeAT) {
        compteurAT = compteurAT + 1;
        System.out.println("Attribut: " + type2.getMemberName());
        if (type2.getValueDomainType()) {
          List<FC_FeatureAttributeValue> listeValeurs = type2.getValuesDomain();
          for (FC_FeatureAttributeValue value : listeValeurs) {
            compteurV = compteurV + 1;
            System.out.println("Valeur: " + value.getLabel());
          }
        }
      }
    }
    System.out.println("Nb de FT = " + compteurFT);
    System.out.println("Nb de AT = " + compteurAT);
    System.out.println("Nb de V = " + compteurV);

    return sProduit;
  }
}
