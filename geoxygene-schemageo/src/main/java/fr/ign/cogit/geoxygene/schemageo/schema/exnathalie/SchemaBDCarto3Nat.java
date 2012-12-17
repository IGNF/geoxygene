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
   * Cr�e le sch�ma de la BDCarto.
   * @return le sch�ma de produit de la BDCarto selon le mod�le ISO
   */
  public static SchemaConceptuelProduit creeSchemaBDCarto3() {

    /***************************************************************************
     * Creation du catalogue de la base de donn�es *
     ************************************************************************/
    SchemaConceptuelProduit sProduit = new SchemaConceptuelProduit();
    sProduit.setNomSchema("Catalogue BDCARTO");
    sProduit.setBD("BDCARTO V3");
    sProduit.setTagBD(1);
    sProduit.setDate("juin 2005");
    sProduit.setVersion("3");
    sProduit.setSource("Photogramm�trie");
    sProduit.setSujet("Composante topographique du RGE");

    /***************************************************************************
     * Ajout du th�me "B�timents, sites touristiques" *
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

    // Attribut Pr�sence d'un office de tourisme
    sProduit.createFeatureAttribute(zoneDHabitat,
        "Pr�sence d'un office de tourisme", "string", true);
    AttributeType presenceDUnOfficeDeTourisme = zoneDHabitat
        .getFeatureAttributeByName("Pr�sence d'un office de tourisme");
    presenceDUnOfficeDeTourisme.setDefinition("");
    sProduit.createFeatureAttributeValue(presenceDUnOfficeDeTourisme,
        "Sans objet");
    sProduit.createFeatureAttributeValue(presenceDUnOfficeDeTourisme, "Oui");
    sProduit.createFeatureAttributeValue(presenceDUnOfficeDeTourisme, "Non");

    // Attribut Activit�s culturelles
    sProduit.createFeatureAttribute(zoneDHabitat, "Activit�s culturelles",
        "string", true);
    AttributeType activitesCulturelles = zoneDHabitat
        .getFeatureAttributeByName("Activit�s culturelles");
    activitesCulturelles.setDefinition("");
    sProduit.createFeatureAttributeValue(activitesCulturelles, "Sans objet");
    sProduit.createFeatureAttributeValue(activitesCulturelles, "Pas de mus�e");
    sProduit.createFeatureAttributeValue(activitesCulturelles,
        "Pr�sence d'un ou plusieurs mus�es hors ville d'art");
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

    // Attribut Station baln�aire
    sProduit.createFeatureAttribute(zoneDHabitat, "Station baln�aire",
        "string", true);
    AttributeType stationBalneaire = zoneDHabitat
        .getFeatureAttributeByName("Station baln�aire");
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
        "Sans inter�t touristique");
    sProduit.createFeatureAttributeValue(importanceTouristique,
        "Importance locale");
    sProduit.createFeatureAttributeValue(importanceTouristique,
        "Importance r�gionale");
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
        .createFeatureAttributeValue(classification, "De 24950 � 99949 hab");
    sProduit.createFeatureAttributeValue(classification, "De 4950 � 24949 hab");
    sProduit.createFeatureAttributeValue(classification, "De 950 � 4949 hab");
    sProduit.createFeatureAttributeValue(classification, "De 175 � 949 hab");
    sProduit.createFeatureAttributeValue(classification,
        "Moins de 174hab quartiers de plus de 20 b�timents");
    sProduit.createFeatureAttributeValue(classification,
        "Groupes d'habitations de 4 � 10 feux ou quartier de 8 � 20 b�timents");
    sProduit.createFeatureAttributeValue(classification,
        "Groupes d'habitations de 2 � 3 feux ou quartier de 4 � 7 b�timents");

    // Classe Zone
    // d'activit�s///////////////////////////////////////////////////

    sProduit.createFeatureType("Zone d'activit�");
    FeatureType zoneDActivites = (FeatureType) (sProduit
        .getFeatureTypeByName("Zone d'activit�"));
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
    sProduit.createFeatureAttributeValue(nature1, "Pr�fecture");
    sProduit.createFeatureAttributeValue(nature1, "H�tel d�partemental");
    sProduit.createFeatureAttributeValue(nature1, "H�tel regional");
    sProduit.createFeatureAttributeValue(nature1, "H�pital");
    sProduit
        .createFeatureAttributeValue(nature1, "Etablisement d'enseignement");
    sProduit.createFeatureAttributeValue(nature1, "A�rogare");
    sProduit.createFeatureAttributeValue(nature1, "Office du tourisme");
    sProduit.createFeatureAttributeValue(nature1, "Mairie");
    sProduit.createFeatureAttributeValue(nature1, "Palais de justice");

    // Attribut Monument historique
    sProduit.createFeatureAttribute(etablissementAdministratifOuPublic,
        "Monument historique", "string", true);
    AttributeType monumentHistorique = etablissementAdministratifOuPublic
        .getFeatureAttributeByName("Monument historique");
    monumentHistorique.setDefinition("");
    sProduit.createFeatureAttributeValue(monumentHistorique, "Non class�");
    sProduit.createFeatureAttributeValue(monumentHistorique, "Inscrit");
    sProduit.createFeatureAttributeValue(monumentHistorique, "Class�");

    // Attribut Pr�sence d'un mus�e
    sProduit.createFeatureAttribute(etablissementAdministratifOuPublic,
        "Pr�sence d'un mus�e", "string", true);
    AttributeType presenceDUnMusee = etablissementAdministratifOuPublic
        .getFeatureAttributeByName("Pr�sence d'un mus�e");
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
        "Sans inter�t touristique");
    sProduit.createFeatureAttributeValue(importanceTouristique2,
        "Importance locale");
    sProduit.createFeatureAttributeValue(importanceTouristique2,
        "Importance r�gionale");
    sProduit.createFeatureAttributeValue(importanceTouristique2,
        "Importance nationale");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(etablissementAdministratifOuPublic,
        "Toponyme", "string", false);
    AttributeType toponyme3 = etablissementAdministratifOuPublic
        .getFeatureAttributeByName("Toponyme");
    toponyme3.setDefinition("");

    // Classe B�timents remarquables � vocation relgieuse ou
    // touristique///////////////////////////////////////////////////

    sProduit
        .createFeatureType("B�timents remarquables � vocation relgieuse ou touristique");
    FeatureType batimentsRemarquablesAVocationRelgieuseOuTouristique = (FeatureType) (sProduit
        .getFeatureTypeByName("B�timents remarquables � vocation relgieuse ou touristique"));
    batimentsRemarquablesAVocationRelgieuseOuTouristique.setDefinition("");
    batimentsRemarquablesAVocationRelgieuseOuTouristique.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(
        batimentsRemarquablesAVocationRelgieuseOuTouristique, "Nature",
        "string", true);
    AttributeType nature2 = batimentsRemarquablesAVocationRelgieuseOuTouristique
        .getFeatureAttributeByName("Nature");
    nature2.setDefinition("");
    sProduit.createFeatureAttributeValue(nature2, "Edifice religieux chr�tien");
    sProduit.createFeatureAttributeValue(nature2, "Mosqu�e");
    sProduit.createFeatureAttributeValue(nature2, "Synagogue");
    sProduit.createFeatureAttributeValue(nature2,
        "Edifice religieux d'autre confession");
    sProduit.createFeatureAttributeValue(nature2, "Phare");
    sProduit.createFeatureAttributeValue(nature2, "Ch�teau");
    sProduit.createFeatureAttributeValue(nature2, "Mus�e");
    sProduit.createFeatureAttributeValue(nature2,
        "Autre b�timent d'importance touristique");
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
        "Sans inter�t touristique");
    sProduit.createFeatureAttributeValue(importanceTouristique3,
        "Importance locale");
    sProduit.createFeatureAttributeValue(importanceTouristique3,
        "Importance r�gionale");
    sProduit.createFeatureAttributeValue(importanceTouristique3,
        "Importance nationale");

    // Attribut Monument historique
    sProduit.createFeatureAttribute(
        batimentsRemarquablesAVocationRelgieuseOuTouristique,
        "Monument historique", "string", true);
    AttributeType monumentHistorique1 = batimentsRemarquablesAVocationRelgieuseOuTouristique
        .getFeatureAttributeByName("Monument historique");
    monumentHistorique1.setDefinition("");
    sProduit.createFeatureAttributeValue(monumentHistorique1, "Non class�");
    sProduit.createFeatureAttributeValue(monumentHistorique1, "Inscrit");
    sProduit.createFeatureAttributeValue(monumentHistorique1, "Class�");

    // Attribut Acc�s
    sProduit.createFeatureAttribute(
        batimentsRemarquablesAVocationRelgieuseOuTouristique, "Acc�s",
        "string", true);
    AttributeType acces = batimentsRemarquablesAVocationRelgieuseOuTouristique
        .getFeatureAttributeByName("Acc�s");
    acces.setDefinition("");
    sProduit.createFeatureAttributeValue(acces, "Sans objet");
    sProduit.createFeatureAttributeValue(acces, "Ouvert au public");
    sProduit.createFeatureAttributeValue(acces, "Non ouvert au public");

    // Attribut Pr�sence d'un mus�e
    sProduit.createFeatureAttribute(
        batimentsRemarquablesAVocationRelgieuseOuTouristique,
        "Pr�sence d'un mus�e", "string", true);
    AttributeType presenceDUnMusee1 = batimentsRemarquablesAVocationRelgieuseOuTouristique
        .getFeatureAttributeByName("Pr�sence d'un mus�e");
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

    // Classe Refuge gard�, g�te
    // d'�tape///////////////////////////////////////////////////

    sProduit.createFeatureType("Refuge gard�, g�te d'�tape");
    FeatureType refugeGardeGiteDEtape = (FeatureType) (sProduit
        .getFeatureTypeByName("Refuge gard�, g�te d'�tape"));
    refugeGardeGiteDEtape.setDefinition("");
    refugeGardeGiteDEtape.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(refugeGardeGiteDEtape, "Nature", "string",
        true);
    AttributeType nature3 = refugeGardeGiteDEtape
        .getFeatureAttributeByName("Nature");
    nature3.setDefinition("");
    sProduit.createFeatureAttributeValue(nature3, "Refuge");
    sProduit.createFeatureAttributeValue(nature3, "G�te d'�tape");
    sProduit.createFeatureAttributeValue(nature3, "H�tel de montagne");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(refugeGardeGiteDEtape, "Toponyme",
        "string", false);
    AttributeType toponyme5 = refugeGardeGiteDEtape
        .getFeatureAttributeByName("Toponyme");
    toponyme5.setDefinition("");

    // Classe Equipement de
    // loisirs///////////////////////////////////////////////////

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
        "Importance r�gionale");
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

    // Classe Site et curiosit� touristique//////////////////////////////////
    sProduit.createFeatureType("Site et curiosit� touristique");
    FeatureType siteEtCuriositeTouristique = (FeatureType) (sProduit
        .getFeatureTypeByName("Site et curiosit� touristique"));
    siteEtCuriositeTouristique.setDefinition("");
    siteEtCuriositeTouristique.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(siteEtCuriositeTouristique, "Nature",
        "string", true);
    AttributeType nature5 = siteEtCuriositeTouristique
        .getFeatureAttributeByName("Nature");
    nature5.setDefinition("");
    sProduit.createFeatureAttributeValue(nature5, "Grotte, goufre am�nag�s");
    sProduit.createFeatureAttributeValue(nature5,
        "Monument m�galithique ou pr�historique");
    sProduit.createFeatureAttributeValue(nature5, "Vestige antique");
    sProduit.createFeatureAttributeValue(nature5,
        "Ruine d'�poque m�di�vale ou post�rieure");
    sProduit.createFeatureAttributeValue(nature5, "Autre curiosit�");

    // Attribut Importance touristique
    sProduit.createFeatureAttribute(siteEtCuriositeTouristique,
        "Importance touristique", "string", true);
    AttributeType importanceTouristique5 = siteEtCuriositeTouristique
        .getFeatureAttributeByName("Importance touristique");
    importanceTouristique5.setDefinition("");
    sProduit.createFeatureAttributeValue(importanceTouristique5,
        "Sans inter�t touristique");
    sProduit.createFeatureAttributeValue(importanceTouristique5,
        "Importance locale");
    sProduit.createFeatureAttributeValue(importanceTouristique5,
        "Importance r�gionale");
    sProduit.createFeatureAttributeValue(importanceTouristique5,
        "Importance nationale");

    // Attribut Monument historique
    sProduit.createFeatureAttribute(siteEtCuriositeTouristique,
        "Monument historique", "string", true);
    AttributeType monumentHistorique2 = siteEtCuriositeTouristique
        .getFeatureAttributeByName("Monument historique");
    monumentHistorique2.setDefinition("");
    sProduit.createFeatureAttributeValue(monumentHistorique2, "Non class�");
    sProduit.createFeatureAttributeValue(monumentHistorique2, "Inscrit");
    sProduit.createFeatureAttributeValue(monumentHistorique2, "Class�");

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
    sProduit.createFeatureAttributeValue(importance2, "Importance r�gionale");
    sProduit.createFeatureAttributeValue(importance2, "Importance nationale");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(siteSportif, "Toponyme", "string", false);
    AttributeType toponyme8 = siteSportif.getFeatureAttributeByName("Toponyme");
    toponyme8.setDefinition("");

    // Classe Zone r�glement�e d'inter�t
    // touristique///////////////////////////////////////////////////

    sProduit.createFeatureType("Zone r�glement�e d'inter�t touristique");
    FeatureType zoneReglementeeDInteretTouristique = (FeatureType) (sProduit
        .getFeatureTypeByName("Zone r�glement�e d'inter�t touristique"));
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
        "Parc national (zone p�riph�rique)");
    sProduit.createFeatureAttributeValue(nature7, "Parc naturel r�gional");
    sProduit.createFeatureAttributeValue(nature7, "For�t domaniale");
    sProduit.createFeatureAttributeValue(nature7, "Reserve naturelle");
    sProduit
        .createFeatureAttributeValue(nature7, "Reserve nationale de chasse");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(zoneReglementeeDInteretTouristique,
        "Toponyme", "string", false);
    AttributeType toponyme9 = zoneReglementeeDInteretTouristique
        .getFeatureAttributeByName("Toponyme");
    toponyme9.setDefinition("");

    // Classe Mus�e non
    // localis�///////////////////////////////////////////////////

    sProduit.createFeatureType("Mus�e non localis�");
    FeatureType museeNonLocalise = (FeatureType) (sProduit
        .getFeatureTypeByName("Mus�e non localis�"));
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

    // Attribut Toponyme d'un objet localis�
    sProduit.createFeatureAttribute(museeNonLocalise,
        "Toponyme d'un objet localis�", "string", false);
    AttributeType toponymeDUnObjetLocalise = museeNonLocalise
        .getFeatureAttributeByName("Toponyme d'un objet localis�");
    toponymeDUnObjetLocalise.setDefinition("");

    // Classe F�te traditionnelle et manifestation artistique
    sProduit
        .createFeatureType("F�te traditionnelle et manifestation artistique");
    FeatureType feteTraditionnelleEtManifestationArtistique = (FeatureType) (sProduit
        .getFeatureTypeByName("F�te traditionnelle et manifestation artistique"));
    feteTraditionnelleEtManifestationArtistique.setDefinition("");
    feteTraditionnelleEtManifestationArtistique.setIsAbstract(false);

    // Attribut Intitul�
    sProduit.createFeatureAttribute(
        feteTraditionnelleEtManifestationArtistique, "Intitul�", "string",
        false);
    AttributeType intitule = feteTraditionnelleEtManifestationArtistique
        .getFeatureAttributeByName("Intitul�");
    intitule.setDefinition("");

    // Attribut Description
    sProduit.createFeatureAttribute(
        feteTraditionnelleEtManifestationArtistique, "Description", "string",
        false);
    AttributeType description2 = feteTraditionnelleEtManifestationArtistique
        .getFeatureAttributeByName("Description");
    description2.setDefinition("");

    // Attribut P�riode
    sProduit
        .createFeatureAttribute(feteTraditionnelleEtManifestationArtistique,
            "P�riode", "string", false);
    AttributeType periode = feteTraditionnelleEtManifestationArtistique
        .getFeatureAttributeByName("P�riode");
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
    sProduit.createFeatureAssociation("Toponyme d'un objet localis�1",
        museeNonLocalise, etablissementAdministratifOuPublic,
        "est localis� par", "localise");
    sProduit.createFeatureAssociation("Toponyme d'un objet localis�2",
        museeNonLocalise, batimentsRemarquablesAVocationRelgieuseOuTouristique,
        "est localis� par", "localise");
    sProduit.createFeatureAssociation("Toponyme d'un objet localis�3",
        museeNonLocalise, siteEtCuriositeTouristique, "est localis� par",
        "localise");
    sProduit.createFeatureAssociation("Toponyme d'un objet localis�4",
        museeNonLocalise, zoneDHabitat, "est localis� par", "localise");
    sProduit.createFeatureAssociation("Toponyme zone d'habitat 2",
        feteTraditionnelleEtManifestationArtistique, zoneDHabitat,
        "est localis� par", "localise");

    /***************************************************************************
     * Ajout du th�me "R�seau routier" *
     ************************************************************************/

    // Classe Tron�on de
    // route///////////////////////////////////////////////////

    sProduit.createFeatureType("Tron�on de route");
    FeatureType tronconDeRoute = (FeatureType) (sProduit
        .getFeatureTypeByName("Tron�on de route"));
    tronconDeRoute
        .setDefinition("Cette classe comprend les tron�ons de routes, chemins et sentiers. Les voies en construction sont retenues, sans raccordement au r�seau existant, dans la mesure o� les terrassement ont d�but� sur le terrain.");
    tronconDeRoute.setIsAbstract(false);

    // Attribut Vocation de liaison
    sProduit.createFeatureAttribute(tronconDeRoute, "Vocation de liaison",
        "string", true);
    AttributeType vocationDeLiaison = tronconDeRoute
        .getFeatureAttributeByName("Vocation de liaison");
    vocationDeLiaison
        .setDefinition("Cet attribut mat�rialise une hi�rarchisation du r�seau routier bas�e non pas sur un crit�re administratif, mais sur l'importance des tron�on der oute pour le trafic routier. Ainsi ses 4 valeurs permettent un maillage de plus en plus dense du territoire.");
    sProduit.createFeatureAttributeValue(vocationDeLiaison, "Type autoroutier");
    sProduit.createFeatureAttributeValue(vocationDeLiaison,
        "Liaison principale");
    sProduit
        .createFeatureAttributeValue(vocationDeLiaison, "Liaison regionale");
    sProduit.createFeatureAttributeValue(vocationDeLiaison, "Liaison locale");
    sProduit.createFeatureAttributeValue(vocationDeLiaison, "Bretelle");
    sProduit.createFeatureAttributeValue(vocationDeLiaison, "Piste cyclable");

    // Attribut Nombre de chauss�es
    sProduit.createFeatureAttribute(tronconDeRoute, "Nombre de chauss�es",
        "string", true);
    AttributeType nombreDeChaussees = tronconDeRoute
        .getFeatureAttributeByName("Nombre de chauss�es");
    nombreDeChaussees.setDefinition("");
    sProduit.createFeatureAttributeValue(nombreDeChaussees, "1 chauss�e");
    sProduit.createFeatureAttributeValue(nombreDeChaussees, "2 chauss�es");

    // Attribut Nombre total de voies
    sProduit.createFeatureAttribute(tronconDeRoute, "Nombre total de voies",
        "string", true);
    AttributeType nombreTotalDeVoies = tronconDeRoute
        .getFeatureAttributeByName("Nombre total de voies");
    nombreTotalDeVoies.setDefinition("");
    sProduit.createFeatureAttributeValue(nombreTotalDeVoies, "sans objet");
    sProduit.createFeatureAttributeValue(nombreTotalDeVoies, "1 voie");
    sProduit
        .createFeatureAttributeValue(nombreTotalDeVoies, "2 voies �troites");
    sProduit.createFeatureAttributeValue(nombreTotalDeVoies,
        "3 voies (chauss�e normalis�e 10.50 m)");
    sProduit.createFeatureAttributeValue(nombreTotalDeVoies, "4 voies");
    sProduit.createFeatureAttributeValue(nombreTotalDeVoies,
        "2 voies (chauss�e normalis�e 7 m)");
    sProduit.createFeatureAttributeValue(nombreTotalDeVoies, "Plus de 4 voies");

    // Attribut Etat physique de la route
    sProduit.createFeatureAttribute(tronconDeRoute,
        "Etat physique de la route", "string", true);
    AttributeType etatPhysiqueDeLaRoute = tronconDeRoute
        .getFeatureAttributeByName("Etat physique de la route");
    etatPhysiqueDeLaRoute.setDefinition("");
    sProduit
        .createFeatureAttributeValue(etatPhysiqueDeLaRoute, "Route rev�tue");
    sProduit.createFeatureAttributeValue(etatPhysiqueDeLaRoute,
        "Route non rev�tue");
    sProduit.createFeatureAttributeValue(etatPhysiqueDeLaRoute,
        "En construction");
    sProduit.createFeatureAttributeValue(etatPhysiqueDeLaRoute,
        "Chemin d'exploitation");
    sProduit.createFeatureAttributeValue(etatPhysiqueDeLaRoute, "Sentier");

    // Attribut Acc�s
    sProduit.createFeatureAttribute(tronconDeRoute, "Acc�s", "string", true);
    AttributeType acces2 = tronconDeRoute.getFeatureAttributeByName("Acc�s");
    acces2.setDefinition("");
    sProduit.createFeatureAttributeValue(acces2, "Libre");
    sProduit.createFeatureAttributeValue(acces2, "A p�age");
    sProduit.createFeatureAttributeValue(acces2, "Interdit au public");
    sProduit.createFeatureAttributeValue(acces2, "Fermeture saisonni�re");

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

    // Attribut Appartenance au r�seau vert
    sProduit.createFeatureAttribute(tronconDeRoute,
        "Appartenance au r�seau vert", "string", true);
    AttributeType appartenanceAuReseauVert = tronconDeRoute
        .getFeatureAttributeByName("Appartenance au r�seau vert");
    appartenanceAuReseauVert.setDefinition("");
    sProduit
        .createFeatureAttributeValue(appartenanceAuReseauVert, "Appartient");
    sProduit.createFeatureAttributeValue(appartenanceAuReseauVert,
        "N'appartient pas");

    // Attribut Sens
    sProduit.createFeatureAttribute(tronconDeRoute, "Sens", "string", true);
    AttributeType sens = tronconDeRoute.getFeatureAttributeByName("Sens");
    sens.setDefinition("Le sens de circulation est g�r� de fa�on obligatoire sur les tron�ons composant les voies � chauss�es �loign�es et sur les tron�ons constituant un �changeur d�taill�; dans les autres cas, le sens est g�r� si l'information est connue.");
    sProduit.createFeatureAttributeValue(sens, "Double sens");
    sProduit.createFeatureAttributeValue(sens, "Sens unique (sens du tron�on)");
    sProduit.createFeatureAttributeValue(sens,
        "Sens unique (sens inverse � celui du tron�on)");

    // Attribut Nombre de voies chauss�e montante
    sProduit.createFeatureAttribute(tronconDeRoute,
        "Nombre de voies chauss�e montante", "string", true);
    AttributeType nombreDeVoiesChausseeMontante = tronconDeRoute
        .getFeatureAttributeByName("Nombre de voies chauss�e montante");
    nombreDeVoiesChausseeMontante
        .setDefinition("Concerne uniquement les tron�ons � chauss�es s�par�es. La chauss�e montante est la chauss�e dont la circulation se fait dans le sens noeud initial - noeud final.");
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

    // Attribut Nombre de voies chauss�e descendante
    sProduit.createFeatureAttribute(tronconDeRoute,
        "Nombre de voies chauss�e descendante", "string", true);
    AttributeType nombreDeVoiesChausseeDescendante = tronconDeRoute
        .getFeatureAttributeByName("Nombre de voies chauss�e descendante");
    nombreDeVoiesChausseeDescendante
        .setDefinition("Concerne uniquement les tron�ons � chauss�es s�par�es. La chauss�e descendante est la chauss�e dont la circulation se fait dans le sens noeud final - noeud initial.");
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
        .setDefinition("Seuls les noms de ponts, viaducs, et tunnels sont port�s par les tron�ons de route; les autres toponymes sont port�s par les itin�raires routiers.");

    // Attribut Utilisation
    sProduit.createFeatureAttribute(tronconDeRoute, "Utilisation", "string",
        true);
    AttributeType utilisation = tronconDeRoute
        .getFeatureAttributeByName("Utilisation");
    utilisation
        .setDefinition("Cet attribut permet de distinguer les tron�ons en fonction de leur utilisation potentielle pour la description de la logique de communication et/ou une repr�sentation cartographique.");
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
        .setDefinition("Cet attribut n'est rempli que pour les tron�ons en construction.");
    sProduit.createFeatureAttributeValue(dateDeMiseEnService, "Sans objet");
    sProduit.createFeatureAttributeValue(dateDeMiseEnService,
        "Date de mise en service");

    // Classe Noeud du r�seau
    // routier///////////////////////////////////////////////////

    sProduit.createFeatureType("Noeud du r�seau routier");
    FeatureType noeudDuReseauRoutier = (FeatureType) (sProduit
        .getFeatureTypeByName("Noeud du r�seau routier"));
    noeudDuReseauRoutier
        .setDefinition("Un noeud du r�seau routier correspond � une extr�mit� de tron�on de route ou de liaison maritime; il traduit une modification des conditions de circulation: ce peut �tre une intersection, un obstacle ou un changement de valeur d'attribut.");
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
            "Carrefour simple, cul de sac, carrefour am�nag� d'une extension inf�rieure � 100 m, rond-point d'un diam�tre inf�rieur � 30 m.");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Intersection repr�sentant un carrefour am�nag� d'une extension sup�rieure � 100 m sans toboggan ni passage inf�rieur.");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Intersection repr�sentant un rond-point (ou giratoire) d'un diam�tre sup�rieur � 100 m d'axe � axe.");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Carrefour am�nag� avec passage inf�rieur ou toboggan quelle que soit son extension.");
    sProduit.createFeatureAttributeValue(typeDeNoeud,
        "Intersection repr�sentant un �changeur complet.");
    sProduit.createFeatureAttributeValue(typeDeNoeud,
        "Intersection repr�sentant un �changeur partiel.");
    sProduit.createFeatureAttributeValue(typeDeNoeud,
        "Rond-point (ou giratoire) d'un diam�tre compris entre 30 et 100 m.");
    sProduit.createFeatureAttributeValue(typeDeNoeud,
        "Embarcad�re de bac ou liaison maritime.");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Embarcad�re de liaison maritime situ� hors du territoire BDCarto positionn� de fa�on fictive en limite de ce territoire.");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Barri�re interdisant la communication libre entre deux portions de route r�guli�rement ou irr�guli�rement entretenue");
    sProduit.createFeatureAttributeValue(typeDeNoeud,
        "Barri�re de douane (hors CEE).");
    sProduit.createFeatureAttributeValue(typeDeNoeud, "Changement d'attribut.");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Noeud cr�� par l'intersection entre une route nationale et la limite d'un d�partement quand il n'existe pas de noeud au lieu de l'intersection ou noeud cr�� pour d�couper des grands tron�ons de route (ex: autoroute).");
    sProduit
        .createFeatureAttributeValue(
            typeDeNoeud,
            "Noeud de communication restreinte: noeud cr�� quand il n'existe pas de noeud correspondant aux valeurs ci-dessus au lieu de la restriction.");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(noeudDuReseauRoutier, "Toponyme", "string",
        false);
    AttributeType toponyme12 = noeudDuReseauRoutier
        .getFeatureAttributeByName("Toponyme");
    toponyme12
        .setDefinition("Un noeud du r�seau routier peut porter un toponyme si l'un au moins des tron�ons connect�s appartient au r�seau class�, et si le noeud est de type 'carrefour simple', 'rond-point' ou 'carrefour am�nag� avec passage inf�rieur ou toboggan'. Un noeud composant un carrefour complexe ne porte g�n�ralement pas de toponyme.");

    // Attribut cote
    sProduit.createFeatureAttribute(noeudDuReseauRoutier, "Cote", "entier",
        false);
    AttributeType cote = noeudDuReseauRoutier.getFeatureAttributeByName("Cote");
    cote.setDefinition("Nombre entier donnant l'altitude en m�tres; ceta ttribut peut ne porter aucune valeur (inconnu). La densit� des points cot�s est d'environ 30 par feuille 1:50 000.");

    // Classe Equipement
    // routier///////////////////////////////////////////////////

    sProduit.createFeatureType("Equipement routier");
    FeatureType equipementRoutier = (FeatureType) (sProduit
        .getFeatureTypeByName("Equipement routier"));
    equipementRoutier
        .setDefinition("La classe des �quipements routiers regroupe: les aires de repos et les aires de service sur le r�seau de type autoroutier; les tunnels routiers d'une longueur inf�rieure � 200m s'ils ne correspondent pas � une intersection avec d'autres tron�ons du r�seau routier et ferr� (sinon ce sont des franchissements); les gares de p�age.");
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
    sProduit.createFeatureAttributeValue(nature8, "Gare de p�age");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(equipementRoutier, "Toponyme", "string",
        false);
    AttributeType toponyme13 = equipementRoutier
        .getFeatureAttributeByName("Toponyme");
    toponyme13.setDefinition("Un �quipement porte en g�n�ral un toponyme.");

    // Classe Liaison maritime ou bac
    // ///////////////////////////////////////////////////

    sProduit.createFeatureType("Liaison maritime ou bac");
    FeatureType liaisonMaritimeOuBac = (FeatureType) (sProduit
        .getFeatureTypeByName("Liaison maritime ou bac"));
    liaisonMaritimeOuBac
        .setDefinition("Liaison maritime ou ligne de bac reliant deux embarcad�res.");
    liaisonMaritimeOuBac.setIsAbstract(false);

    // Attribut Ouverture
    sProduit.createFeatureAttribute(liaisonMaritimeOuBac, "Ouverture",
        "string", true);
    AttributeType ouverture = liaisonMaritimeOuBac
        .getFeatureAttributeByName("Ouverture");
    ouverture.setDefinition("");
    sProduit.createFeatureAttributeValue(ouverture, "Toute l'ann�e");
    sProduit.createFeatureAttributeValue(ouverture, "En saison seulement");

    // Attribut Vocation
    sProduit.createFeatureAttribute(liaisonMaritimeOuBac, "Vocation", "string",
        true);
    AttributeType vocation = liaisonMaritimeOuBac
        .getFeatureAttributeByName("Vocation");
    vocation.setDefinition("");
    sProduit.createFeatureAttributeValue(vocation, "Pi�tons seulement");
    sProduit.createFeatureAttributeValue(vocation, "Pi�tons et automobiles");

    // Attribut Dur�e
    sProduit.createFeatureAttribute(liaisonMaritimeOuBac, "Dur�e", "entier",
        false);
    AttributeType duree = liaisonMaritimeOuBac
        .getFeatureAttributeByName("Dur�e");
    duree
        .setDefinition("Dur�e de la travers�e en minutes, pouvant �ventuellement ne porter aucune valeur (inconnu). Note: Quand il ya plusieurs temps de parcours pour une m�me liaison, c'est le temps le plus long qui est retenu.");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(liaisonMaritimeOuBac, "Toponyme", "string",
        false);
    AttributeType toponyme14 = liaisonMaritimeOuBac
        .getFeatureAttributeByName("Toponyme");
    toponyme14
        .setDefinition("Texte d'au plus 80 caract�res, pouvant �ventuellement ne porter aucune valeur (inconnu), sp�cifiant la localisation des embarcad�res de d�part et d'arriv�e.");

    // Classe Communication
    // restreinte///////////////////////////////////////////

    sProduit.createFeatureType("Communication restreinte");
    FeatureType communicationRestreinte = (FeatureType) (sProduit
        .getFeatureTypeByName("Communication restreinte"));
    communicationRestreinte
        .setDefinition("Relation topologique participant � la logique de parcours du r�seau routier: elle explicite les restrictions �ventuelles au passage d'un tron�on de route � un autre via un noeud routier commun aux deux tron�ons.");
    communicationRestreinte.setIsAbstract(false);

    // Attribut Interdiction
    sProduit.createFeatureAttribute(communicationRestreinte, "Interdiction",
        "string", true);
    AttributeType interdiction = communicationRestreinte
        .getFeatureAttributeByName("Interdiction");
    interdiction.setDefinition("");
    sProduit.createFeatureAttributeValue(interdiction, "Totale");
    sProduit.createFeatureAttributeValue(interdiction,
        "Restreinte au d�passement d'une hauteur et/ou d'un poids maximal");

    // Attribut Restriction poids
    sProduit.createFeatureAttribute(communicationRestreinte,
        "Restriction poids", "entier", false);
    AttributeType restrictionPoids = communicationRestreinte
        .getFeatureAttributeByName("Restriction poids");
    restrictionPoids
        .setDefinition("Poids maximum autoris� en tonnes (� une d�cimale).");

    // Attribut Restriction taille
    sProduit.createFeatureAttribute(communicationRestreinte,
        "Restriction taille", "entier", false);
    AttributeType restrictionTaille = communicationRestreinte
        .getFeatureAttributeByName("Restriction taille");
    restrictionTaille
        .setDefinition("Hauteur maximum autoris�e en m�tres (� deux d�cimales).");

    // Classe Carrefour
    // complexe///////////////////////////////////////////////////

    sProduit.createFeatureType("Carrefour complexe");
    FeatureType carrefourComplexe = (FeatureType) (sProduit
        .getFeatureTypeByName("Carrefour complexe"));
    carrefourComplexe
        .setDefinition("La classe des carrefours comples regroupe: les �changeurs, diffuseurs, carrefours d�nivel�s et 'tourne � gauche' d�nivel�s situ�s sur les tron�ons de type autoroutier ou de liaison principale; les ronds-points et carrefours am�nag�s d'un diam�tre sup�rieur � 100 m d'axe � axe.");
    carrefourComplexe.setIsAbstract(false);

    // Attribut Num�ro
    sProduit.createFeatureAttribute(carrefourComplexe, "Num�ro", "string",
        false);
    AttributeType numero1 = carrefourComplexe
        .getFeatureAttributeByName("Num�ro");
    numero1
        .setDefinition("Texte d'au plus 10 caract�res: num�rotation mise en place par la Direction de la S�curit� de la Circulation Routi�res. Cette num�rotation concerne uniquement les �changeurs sur autoroutes, voies express aux normes, et certaines routes nationales importantes.");

    // Attribut Nature
    sProduit
        .createFeatureAttribute(carrefourComplexe, "Nature", "string", true);
    AttributeType nature9 = carrefourComplexe
        .getFeatureAttributeByName("Nature");
    nature9.setDefinition("");
    sProduit.createFeatureAttributeValue(nature9,
        "Echangeur, sorties d'autoroute, diffuseurs, carrefours d�nivel�s");
    sProduit.createFeatureAttributeValue(nature9, "Rond-point");
    sProduit.createFeatureAttributeValue(nature9, "Carrefour am�nag�");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(carrefourComplexe, "Toponyme", "string",
        false);
    AttributeType toponyme15 = carrefourComplexe
        .getFeatureAttributeByName("Toponyme");
    toponyme15
        .setDefinition("Il s'agit du nom g�ographique de l'�changeur, du rond-point ou du carrefour am�nag�.");

    // Classe
    // Route////////////////////////////////////////////////////////////////

    sProduit.createFeatureType("Route");
    FeatureType route = (FeatureType) (sProduit.getFeatureTypeByName("Route"));
    route
        .setDefinition("Une route est un parcours class� par l'autorit� administrative nationale, r�gionale ou d�partementale, reliant entre elles des villes ou des p�les d'attraction (ports, a�roports, lieux touristiques, etc.) et identifi� par un num�ro. Ce dernier correspond au num�ro donn� par l'organisme charg� de g�rer la route et r�sulte de proc�dures administratives. Il peut �tre diff�rent de celui pr�sent sur le terrain qui peut ne pas �tre actualis� ou anticiper un classement futur. Sur le territoire national, cette classe comprend les autoroutes, les routes nationales, les routes d�partementales et les bretelles d'�changeur identifi�s par les gestionnaires. A l'�tranger, elle ne comprend que les autoroutes et les routes nationales (ou �quivalent). Des routes sp�cifiques sont g�r�es et saisies sur les bretelles d'�changeurs dans la BDCarto quand l'information est donn�es par le gestionnaires de la route ou le METLTM. En g�n�ral, le num�ro des bretelles d'�changeur est cod� sur 7 caract�res. Les autres types de classement administratif (route vicinale, foresti�res) ne sont pas g�r�s dans la BDCarto.");
    route.setIsAbstract(false);

    // Attribut Num�ro de la route
    sProduit.createFeatureAttribute(route, "Num�ro de la route", "string",
        false);
    AttributeType numeroDeLaRoute = route
        .getFeatureAttributeByName("Num�ro de la route");
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
        "Route d�partementale");

    // Attribut Gestionnaire
    sProduit.createFeatureAttribute(route, "Gestionnaire", "string", false);
    AttributeType gestionnaire = route
        .getFeatureAttributeByName("Gestionnaire");
    gestionnaire.setDefinition("");

    // Classe Itin�raire
    // routier///////////////////////////////////////////////////

    sProduit.createFeatureType("Itin�raire routier");
    FeatureType itineraireRoutier = (FeatureType) (sProduit
        .getFeatureTypeByName("Itin�raire routier"));
    itineraireRoutier
        .setDefinition("Un itin�raire routier est un ensemble de parcours continus empruntant des tron�ons de route, chemins ou sentiers et identifi� par un toponyme ou un num�ro.");
    itineraireRoutier.setIsAbstract(false);

    // Attribut Num�ro
    sProduit.createFeatureAttribute(itineraireRoutier, "Num�ro", "string",
        false);
    AttributeType numero2 = itineraireRoutier
        .getFeatureAttributeByName("Num�ro");
    numero2.setDefinition("");

    // Attribut Nature
    sProduit
        .createFeatureAttribute(itineraireRoutier, "Nature", "string", true);
    AttributeType nature10 = itineraireRoutier
        .getFeatureAttributeByName("Nature");
    nature10.setDefinition("");
    sProduit.createFeatureAttributeValue(nature10,
        "Itin�raire routier portant un nom");
    sProduit.createFeatureAttributeValue(nature10, "Route europ�enne");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(itineraireRoutier, "Toponyme", "string",
        false);
    AttributeType toponyme16 = itineraireRoutier
        .getFeatureAttributeByName("Toponyme");
    toponyme16.setDefinition("");

    // Classe D�but de
    // section/////////////////////////////////////////////////////

    sProduit.createFeatureType("D�but de section");
    FeatureType debutDeSection = (FeatureType) (sProduit
        .getFeatureTypeByName("D�but de section"));
    debutDeSection
        .setDefinition("Une section est un ensemble continu de tron�ons de route ayant le m�me num�ro et le m�me gestionnaire.");
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
    sProduit.createFeatureAttributeValue(sens2, "sens du tron�on");
    sProduit.createFeatureAttributeValue(sens2,
        "sens inverse � celui du tron�on");

    // relation d'association Permet d'acc�der �
    sProduit.createFeatureAssociation("Permet d'acc�der �", equipementRoutier,
        tronconDeRoute, "est accessible par", "permet d'acc�der �");
    // Attention penser � rajouter les attributs

    // relation d'association r�seau routier
    sProduit
        .createFeatureAssociation("R�seau routier initial", tronconDeRoute,
            noeudDuReseauRoutier, "a pour noeud initial",
            "est le noeud initial de");
    sProduit.createFeatureAssociation("R�seau routier final", tronconDeRoute,
        noeudDuReseauRoutier, "a pour noeud final", "est le noeud final de");

    // relation d'association r�seau de bac
    sProduit.createFeatureAssociation("R�seau de bac initial",
        liaisonMaritimeOuBac, noeudDuReseauRoutier, "a pour noeud initial",
        "est le noeud initial de");
    sProduit.createFeatureAssociation("R�seau de bac final",
        liaisonMaritimeOuBac, noeudDuReseauRoutier, "a pour noeud final",
        "est le noeud final de");

    // relation d'association communication restreinte
    sProduit.createFeatureAssociation("Communication restreinte",
        communicationRestreinte, noeudDuReseauRoutier, "concerne",
        "est concern�e par");
    sProduit.createFeatureAssociation(
        "Communication restreinte tron�on initial", communicationRestreinte,
        tronconDeRoute, "a pour tron�on initial", "est le tron�on initial de");
    sProduit.createFeatureAssociation("Communication restreinte tron�on final",
        communicationRestreinte, tronconDeRoute, "a pour tron�on final",
        "est le tron�on final de");

    // relation d'association a pour successeur
    sProduit.createFeatureAssociation("A pour successeur", debutDeSection,
        debutDeSection, "A pour successeur", "a pour pr�d�cesseur");

    /***************************************************************************
     * Ajout du th�me "R�seau Ferr�" *
     ************************************************************************/

    // Classe Tron�on de voie
    // ferr�e///////////////////////////////////////////////////

    sProduit.createFeatureType("Tron�on de voie ferr�e");
    FeatureType tronconDeVoieFerree = (FeatureType) (sProduit
        .getFeatureTypeByName("Tron�on de voie ferr�e"));
    tronconDeVoieFerree.setDefinition("");
    tronconDeVoieFerree.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(tronconDeVoieFerree, "Nature", "string",
        true);
    AttributeType nature11 = tronconDeVoieFerree
        .getFeatureAttributeByName("Nature");
    nature11.setDefinition("");
    sProduit.createFeatureAttributeValue(nature11, "Normale");
    sProduit.createFeatureAttributeValue(nature11, "Tron�on � grande vitesse");
    sProduit
        .createFeatureAttributeValue(
            nature11,
            "Tron�on d'embranchement particulier, voie industrielle ou de service, ligne touristique");
    sProduit.createFeatureAttributeValue(nature11,
        "Tron�on de voie de triage ou de garage");
    sProduit.createFeatureAttributeValue(nature11, "Train � cr�maill�re");
    sProduit.createFeatureAttributeValue(nature11, "Funiculaire");

    // Attribut Energie de propulsion
    sProduit.createFeatureAttribute(tronconDeVoieFerree,
        "Energie de propulsion", "string", true);
    AttributeType energieDePropulsion = tronconDeVoieFerree
        .getFeatureAttributeByName("Energie de propulsion");
    energieDePropulsion.setDefinition("");
    sProduit.createFeatureAttributeValue(energieDePropulsion, "Electrique");
    sProduit.createFeatureAttributeValue(energieDePropulsion,
        "En cours d'�lectrification");
    sProduit.createFeatureAttributeValue(energieDePropulsion, "Non �l�ctrique");

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
    sProduit.createFeatureAttributeValue(classement, "Exploit�");
    sProduit.createFeatureAttributeValue(classement, "Non exploit�");
    sProduit.createFeatureAttributeValue(classement,
        "En construction ou en projet");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(tronconDeVoieFerree, "Toponyme",
        "string g�n�rique, string article, string sp�cifique", false);
    AttributeType toponyme17 = tronconDeVoieFerree
        .getFeatureAttributeByName("Toponyme");
    toponyme17.setDefinition("");

    // Classe Noeud du R�seau
    // ferr�///////////////////////////////////////////////
    sProduit.createFeatureType("Noeud du r�seau ferr�");
    FeatureType noeudDuReseauferre = (FeatureType) (sProduit
        .getFeatureTypeByName("Noeud du r�seau ferr�"));
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
        "Gare ou point d'arr�t ouvert aux voyageurs seulement");
    sProduit.createFeatureAttributeValue(typeDeNoeud2,
        "Embranchement, cul de sac");
    sProduit
        .createFeatureAttributeValue(typeDeNoeud2, "Changement d'attributs");
    sProduit.createFeatureAttributeValue(typeDeNoeud2,
        "Noeud arbitraire cr�� pour couper les grands tron�ons ferr�s");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(noeudDuReseauferre, "Toponyme", "string",
        false);
    AttributeType toponyme18 = noeudDuReseauferre
        .getFeatureAttributeByName("Toponyme");
    toponyme18.setDefinition("");

    // Classe Ligne de chemin de
    // fer///////////////////////////////////////////////////

    sProduit.createFeatureType("Ligne de chemin de fer");
    FeatureType ligneDeCheminDeFer = (FeatureType) (sProduit
        .getFeatureTypeByName("Ligne de chemin de fer"));
    ligneDeCheminDeFer.setDefinition("");
    ligneDeCheminDeFer.setIsAbstract(false);

    // Attribut Caract�re touristique
    sProduit.createFeatureAttribute(ligneDeCheminDeFer,
        "Caract�re touristique", "string", true);
    AttributeType caractereTouristique = ligneDeCheminDeFer
        .getFeatureAttributeByName("Caract�re touristique");
    caractereTouristique.setDefinition("");
    sProduit.createFeatureAttributeValue(caractereTouristique,
        "Ligne touristique");
    sProduit.createFeatureAttributeValue(caractereTouristique,
        "Ligne sans caract�re touristique particulier");

    // Toponyme
    sProduit.createFeatureAttribute(ligneDeCheminDeFer, "Toponyme", "string",
        false);
    AttributeType toponyme20 = ligneDeCheminDeFer
        .getFeatureAttributeByName("Toponyme");
    toponyme20.setDefinition("");

    // Relation d'association R�seau ferr�
    sProduit.createFeatureAssociation("R�seau ferr� initial",
        tronconDeVoieFerree, noeudDuReseauferre, "a pour noeud initial",
        "est le noeud initial de");
    sProduit.createFeatureAssociation("R�seau ferr� final", ligneDeCheminDeFer,
        noeudDuReseauferre, "a pour noeud final", "est le noeud final de");

    // Relation d'association est compos� de
    sProduit.createFeatureAssociation("Ligne de chemin de fer",
        ligneDeCheminDeFer, tronconDeVoieFerree, "est compos�e de", "compose");

    // Attention il manque les relations extra-th�me

    /***************************************************************************
     * Ajout du th�me "Hydrographie" *
     ************************************************************************/

    // Classe Tron�on hydrographique//////////////////////////////////////////
    sProduit.createFeatureType("Tron�on hydrographique");
    FeatureType tronconHydrographique = (FeatureType) (sProduit
        .getFeatureTypeByName("Tron�on hydrographique"));
    tronconHydrographique.setDefinition("");
    tronconHydrographique.setIsAbstract(false);

    // Attribut Etat
    sProduit.createFeatureAttribute(tronconHydrographique, "Etat", "string",
        true);
    AttributeType etat = tronconHydrographique
        .getFeatureAttributeByName("Etat");
    etat.setDefinition("");
    sProduit.createFeatureAttributeValue(etat, "En attente de mise � jour");
    sProduit
        .createFeatureAttributeValue(
            etat,
            "Inconnu: l'existance d'un �coulement est certaine mais le trac� n'est pas connu avec pr�cision");
    sProduit.createFeatureAttributeValue(etat, "Ecoulement permanent");
    sProduit.createFeatureAttributeValue(etat, "Ecoulement intermittent");
    sProduit
        .createFeatureAttributeValue(
            etat,
            "Axe fictif: arc cr�� pour assurer la continuit� des cours d'eau � la travers�e des zones d'hydrographie ou lorsque le trac� n'est pas connu avec pr�cision (parcours souterrain)");
    sProduit.createFeatureAttributeValue(etat, "Canal abandonn� � sec");

    // Attribut Sens
    sProduit.createFeatureAttribute(tronconHydrographique, "Sens", "string",
        true);
    AttributeType sens3 = tronconHydrographique
        .getFeatureAttributeByName("Sens");
    sens3.setDefinition("");
    sProduit.createFeatureAttributeValue(sens3, "En attente de mise � jour");
    sProduit.createFeatureAttributeValue(sens3, "Inconnu");
    sProduit
        .createFeatureAttributeValue(
            sens3,
            "Sens d'�coulement dans le sens de l'arc (du noeud initial vers le noeud final)");
    sProduit.createFeatureAttributeValue(sens3,
        "Sens d'�coulement variable dont bief de passage");

    // Attribut Largeur
    sProduit.createFeatureAttribute(tronconHydrographique, "Largeur", "string",
        true);
    AttributeType largeur = tronconHydrographique
        .getFeatureAttributeByName("Largeur");
    largeur.setDefinition("");
    sProduit.createFeatureAttributeValue(largeur, "En attente de mise � jour");
    sProduit.createFeatureAttributeValue(largeur,
        "Sans objet (seulement si l'�tat est inconnu ou fictif");
    sProduit.createFeatureAttributeValue(largeur, "Entre 0 et 15 m");
    sProduit.createFeatureAttributeValue(largeur, "Entre 15 et 50 m");
    sProduit.createFeatureAttributeValue(largeur, "Plus de 50 m");

    // Attribut Nature
    sProduit.createFeatureAttribute(tronconHydrographique, "Nature", "string",
        true);
    AttributeType nature12 = tronconHydrographique
        .getFeatureAttributeByName("Nature");
    nature12.setDefinition("");
    sProduit.createFeatureAttributeValue(nature12, "En attente de mise � jour");
    sProduit.createFeatureAttributeValue(nature12,
        "Sans objet (seulement si l'�tat est inconnu ou fictif)");
    sProduit.createFeatureAttributeValue(nature12, "Cours d'eau naturel");
    sProduit.createFeatureAttributeValue(nature12,
        "Canal, chenal: voie d'eau artificielle");
    sProduit
        .createFeatureAttributeValue(
            nature12,
            "Aqueduc, conduite forc�e: tuyau ou chenal artificiel con�u pour le transport de l'eau (usage hydro�lectrique ou industriel)");
    sProduit.createFeatureAttributeValue(nature12,
        "Estuaire: �coulement d'un cours d'eau dans la zone d'estran");
    sProduit.createFeatureAttributeValue(nature12,
        "Tron�on allant de la cote z�ro NGF � la laisse des plus basses eaux.");

    // Attribut Navigabilit�
    sProduit.createFeatureAttribute(tronconHydrographique, "Navigabilit�",
        "string", true);
    AttributeType navigabilite = tronconHydrographique
        .getFeatureAttributeByName("Navigabilit�");
    navigabilite.setDefinition("");
    sProduit.createFeatureAttributeValue(navigabilite,
        "En attente de mise � jour");
    sProduit.createFeatureAttributeValue(navigabilite, "Inconnu");
    sProduit.createFeatureAttributeValue(navigabilite,
        "Navigable: inscrit � la nomenclature des voies navigables");
    sProduit.createFeatureAttributeValue(navigabilite, "Non navigable");
    sProduit.createFeatureAttributeValue(navigabilite, "");
    sProduit.createFeatureAttributeValue(navigabilite, "");

    // Attribut Gabarit
    sProduit.createFeatureAttribute(tronconHydrographique, "Gabarit", "string",
        true);
    AttributeType gabarit = tronconHydrographique
        .getFeatureAttributeByName("Gabarit");
    gabarit.setDefinition("");
    sProduit.createFeatureAttributeValue(gabarit, "En attente de mise � jour");
    sProduit.createFeatureAttributeValue(gabarit,
        "Sans objet (si la navigabilit� est inconnu ou non navigable)");
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
        "En attente de mise � jour");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol3, "Inconnu");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol3,
        "Au sol, � ciel ouvert");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol3,
        "Elev� sur pont, arcade ou mur");
    sProduit
        .createFeatureAttributeValue(positionParRapportAuSol3, "Souterrain");
    sProduit.createFeatureAttributeValue(positionParRapportAuSol3,
        "Au sol (tuyau pos� au sol");

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
    sProduit.createFeatureAttributeValue(nature13, "En attente de mise � jour");
    sProduit.createFeatureAttributeValue(nature13,
        "Noeud hydrographique sans nature particuli�re");
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
            "Ouvrage de franchissement de chutes (�cluse, pente d'eau, ascenceur � bateau)");
    sProduit.createFeatureAttributeValue(nature13,
        "Chute d'eau, cascade remarquable");
    sProduit.createFeatureAttributeValue(nature13,
        "Source d'inter�t touristique");
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
            "Extremit� de tron�on 'z�ro NGF' (Valeur Z pour l'attribut nature du tron�on) co�ncidant avec la laisse des plus basses eaux");

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

    // Classe Point d'eau isol�///////////////////////////////////////////////

    sProduit.createFeatureType("Point d'eau isol�");
    FeatureType pointDEauIsole = (FeatureType) (sProduit
        .getFeatureTypeByName("Point d'eau isol�"));
    pointDEauIsole.setDefinition("");
    pointDEauIsole.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(pointDEauIsole, "Nature", "string", true);
    AttributeType nature14 = pointDEauIsole.getFeatureAttributeByName("Nature");
    nature14.setDefinition("");
    sProduit.createFeatureAttributeValue(nature14, "Inconnu");
    sProduit.createFeatureAttributeValue(nature14, "Ch�teau d'eau");
    sProduit.createFeatureAttributeValue(nature14,
        "Station de traitement des eaux");
    sProduit.createFeatureAttributeValue(nature14, "Station de pompage");
    sProduit.createFeatureAttributeValue(nature14, "R�servoir");
    sProduit.createFeatureAttributeValue(nature14,
        "Plan d'eau d'une superficie inf�rieure � 1 ha");

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
        "Limite des plus basses eaux naturelle (z�ro bathym�trique)");
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
    sProduit.createFeatureAttributeValue(nature16, "En attente de mise � jour");
    sProduit.createFeatureAttributeValue(nature16, "Inconnu");
    sProduit.createFeatureAttributeValue(nature16, "N�v�s, glaciers");
    sProduit.createFeatureAttributeValue(nature16, "Eau douce permanente");
    sProduit.createFeatureAttributeValue(nature16, "Eau sal�e permanente");
    sProduit.createFeatureAttributeValue(nature16, "Eau sal�e non permanente");

    // Attribut Type
    sProduit.createFeatureAttribute(elementSurfacique, "Type", "string", true);
    AttributeType type1 = elementSurfacique.getFeatureAttributeByName("Type");
    type1.setDefinition("");
    sProduit.createFeatureAttributeValue(type1, "En attente de mise � jour");
    sProduit.createFeatureAttributeValue(type1, "Inconnu");
    sProduit.createFeatureAttributeValue(type1,
        "Eau douce permanente, cours d'eau (largeur > 50 m)");
    sProduit.createFeatureAttributeValue(type1,
        "Eau douce permanente, plan d'eau, bassin, r�servoir");
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
        "Eau sal�e permanente, pleine mer");
    sProduit.createFeatureAttributeValue(type1,
        "Eau sal�e permanente, �coulement d'eau");
    sProduit.createFeatureAttributeValue(type1,
        "Eau sal�e permanente, nappe d'eau");
    sProduit.createFeatureAttributeValue(type1,
        "Eau sal�e permanente, bassin portuaire");
    sProduit.createFeatureAttributeValue(type1,
        "Eau sal�e non permanente, marais salants");
    sProduit.createFeatureAttributeValue(type1,
        "Eau sal�e non permanente, zone rocheuse");
    sProduit.createFeatureAttributeValue(type1,
        "Eau sal�e non permanente, zone mixte rochers et sable");
    sProduit.createFeatureAttributeValue(type1,
        "Eau sal�e non permanente, zone de sable humide");
    sProduit.createFeatureAttributeValue(type1,
        "Eau sal�e non permanente, zone de vase");
    sProduit.createFeatureAttributeValue(type1,
        "Eau sal�e non permanente, zone de graviers et galets");

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

    // Attribut Libell�
    sProduit.createFeatureAttribute(zoneHydrographique, "Libell�", "string",
        false);
    AttributeType libelle = zoneHydrographique
        .getFeatureAttributeByName("Libell�");
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
            "Tout cours d'eau d'une longueur sup�rieure � 100 km ou tout cours d'eau se jetant dans une embouchure logique et d'une longueur sup�rieure � 25 km");
    sProduit
        .createFeatureAttributeValue(
            classification2,
            "Tout cours d'eau d'une longueur comprise entre 50 et 100 km ou tout cours d'eau se jetant dans une embouchure logique et d'une longueur sup�rieure � 10 km");
    sProduit.createFeatureAttributeValue(classification2,
        "Tout cours d'eau d'une longueur comprise entre 25 et 50 km");
    sProduit.createFeatureAttributeValue(classification2,
        "Tout cours d'eau d'une longueur comprise entre 10 et 25 km");
    sProduit.createFeatureAttributeValue(classification2,
        "Tout cours d'eau d'une longueur comprise entre 5 et 10 km");
    sProduit
        .createFeatureAttributeValue(classification2,
            "Tous les autres cours d'eau hormis ceux issus de la densification du r�seau");
    sProduit.createFeatureAttributeValue(classification2,
        "Cours d'eau issus de la densification du r�seau");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(coursDEau, "Toponyme", "string", false);
    AttributeType toponyme26 = coursDEau.getFeatureAttributeByName("Toponyme");
    toponyme26.setDefinition("");

    // Attribut Candidat
    sProduit.createFeatureAttribute(coursDEau, "Candidat", "string", false);
    AttributeType candidat6 = coursDEau.getFeatureAttributeByName("Candidat");
    candidat6.setDefinition("");

    // Classe Entit� hydrographique de surface////////////////////////////////

    sProduit.createFeatureType("Entit� hydrographique de surface");
    FeatureType entiteHydrographiqueDeSurface = (FeatureType) (sProduit
        .getFeatureTypeByName("Entit� hydrographique de surface"));
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
        "Les entit�s dont la surface est sup�rieure � 100 ha");
    sProduit.createFeatureAttributeValue(classe,
        "Les entit�s dont la surface est comprise entre 25 et 100 ha");
    sProduit.createFeatureAttributeValue(classe,
        "Les entit�s dont la surface est comprise entre 18 et 25 ha");
    sProduit.createFeatureAttributeValue(classe,
        "Les entit�s dont la surface est comprise entre 8 et 18 ha");
    sProduit.createFeatureAttributeValue(classe,
        "Les entit�s dont la surface est comprise entre 4 et 8 ha");
    sProduit.createFeatureAttributeValue(classe,
        "Les entit�s dont la surface est comprise entre 1 et 4 ha");
    sProduit.createFeatureAttributeValue(classe,
        "Les entit�s dont la surface inf�rieure � 1 ha");

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

    // Attribut Libell�
    sProduit.createFeatureAttribute(sousSecteur, "Libell�", "string", false);
    AttributeType libelle1 = sousSecteur.getFeatureAttributeByName("Libell�");
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

    // Attribut Libell�
    sProduit.createFeatureAttribute(secteur, "Libell�", "string", false);
    AttributeType libelle2 = secteur.getFeatureAttributeByName("Libell�");
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

    // Attribut Libell�
    sProduit.createFeatureAttribute(region, "Libell�", "string", false);
    AttributeType libelle3 = region.getFeatureAttributeByName("Libell�");
    libelle3.setDefinition("");

    // Relation d'association r�seau hydrographique
    sProduit.createFeatureAssociation("R�seau hydrographique initial",
        tronconHydrographique, noeudHydrographique, "a pour noeud initial",
        "est le noeud initial de");
    sProduit.createFeatureAssociation("R�seau hydrographique final",
        tronconHydrographique, noeudHydrographique, "a pour noeud final",
        "est le noeud final de");

    // Relation d'association superposition
    sProduit.createFeatureAssociation("Superposition", tronconHydrographique,
        tronconHydrographique, "se superpose �", "se superpose �");

    // Relation d'association exutoire
    sProduit.createFeatureAssociation("Exutoire", noeudHydrographique,
        zoneHydrographique, "est l'exutoire de", "a pour exutoire");

    // Relation d'association drain principale
    sProduit.createFeatureAssociation("Drain principale", coursDEau,
        zoneHydrographique, "est le drain principal de",
        "a pour drain principal");

    // Relation d'association est travers�e par
    sProduit.createFeatureAssociation("Est travers�e par",
        entiteHydrographiqueDeSurface, coursDEau, "est travers�e par",
        "traverse");

    // Relation d'association passe par
    sProduit.createFeatureAssociation("Passe par", tronconHydrographique,
        entiteHydrographiqueDeSurface, "Passe par", "est travers� par");

    /***************************************************************************
     * Ajout du th�me "Franchissement" *
     ************************************************************************/

    // Classe
    // Franchissement//////////////////////////////////////////////////////

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

    // relation Tron�on de route passe par franchissement
    sProduit.createFeatureAssociation(
        "Tron�on de route passe par franchissement", franchissement,
        tronconDeRoute, "passe par", "est travers� par");

    // relation Tron�on de voie ferr�e passe par franchissement
    sProduit.createFeatureAssociation(
        "Tron�on hydrographique passe par franchissement", franchissement,
        tronconDeVoieFerree, "passe par", "est travers� par");

    // Relation Tron�on hydrographique passe par franchissement
    sProduit.createFeatureAssociation(
        "Tron�on de voie ferr�e passe par franchissement", franchissement,
        tronconHydrographique, "passe par", "est travers� par");

    /***************************************************************************
     * Ajout du th�me "Autres �quipements" *
     ************************************************************************/

    // Classe Tron�on de ligne
    // �lectrique///////////////////////////////////////////

    sProduit.createFeatureType("Tron�on de ligne �lectrique");
    FeatureType tronconDeLignElectrique = (FeatureType) (sProduit
        .getFeatureTypeByName("Tron�on de ligne �lectrique"));
    tronconDeLignElectrique.setDefinition("");
    tronconDeLignElectrique.setIsAbstract(false);

    // Attribut Type du trac�
    sProduit.createFeatureAttribute(tronconDeLignElectrique, "Type du trac�",
        "string", true);
    AttributeType typeDuTrace = tronconDeLignElectrique
        .getFeatureAttributeByName("Type du trac�");
    typeDuTrace.setDefinition("");
    sProduit.createFeatureAttributeValue(typeDuTrace, "A�rien");
    sProduit.createFeatureAttributeValue(typeDuTrace, "Souterrain");
    sProduit.createFeatureAttributeValue(typeDuTrace,
        "Fictif (prolongation des lignes �lectriques � l'int�rieur des postes");

    // Attribut Tension
    sProduit.createFeatureAttribute(tronconDeLignElectrique, "Tension",
        "string", true);
    AttributeType tension = tronconDeLignElectrique
        .getFeatureAttributeByName("Tension");
    tension.setDefinition("");
    sProduit
        .createFeatureAttributeValue(tension, "Inf�rieure ou �gale � 42 kV");
    sProduit.createFeatureAttributeValue(tension, "Comprise entre 42 et 63 kV");
    sProduit.createFeatureAttributeValue(tension, "Comprise entre 63 et 90 kV");
    sProduit
        .createFeatureAttributeValue(tension, "Comprise entre 90 et 150 kV");
    sProduit.createFeatureAttributeValue(tension,
        "Comprise entre 150 et 225 kV");
    sProduit.createFeatureAttributeValue(tension,
        "Comprise entre 225 et 400 kV");

    // Classe Construction
    // �lev�e///////////////////////////////////////////////////

    sProduit.createFeatureType("Construction �lev�e");
    FeatureType constructionElevee = (FeatureType) (sProduit
        .getFeatureTypeByName("Construction �lev�e"));
    constructionElevee.setDefinition("");
    constructionElevee.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(constructionElevee, "Nature", "string",
        true);
    AttributeType nature17 = constructionElevee
        .getFeatureAttributeByName("Nature");
    nature17.setDefinition("");
    sProduit.createFeatureAttributeValue(nature17, "Pyl�ne");
    sProduit
        .createFeatureAttributeValue(nature17, "Tour de t�l�communications");
    sProduit.createFeatureAttributeValue(nature17, "Antenne");
    sProduit.createFeatureAttributeValue(nature17, "Silo, ch�teau d'eau");
    sProduit.createFeatureAttributeValue(nature17, "Chemin�e");

    // Classe Transport par
    // c�ble///////////////////////////////////////////////////

    sProduit.createFeatureType("Transport par c�ble");
    FeatureType transportParCable = (FeatureType) (sProduit
        .getFeatureTypeByName("Transport par c�ble"));
    transportParCable.setDefinition("");
    transportParCable.setIsAbstract(false);

    // Attribut Nature
    sProduit
        .createFeatureAttribute(transportParCable, "Nature", "string", true);
    AttributeType nature18 = transportParCable
        .getFeatureAttributeByName("Nature");
    nature18.setDefinition("");
    sProduit.createFeatureAttributeValue(nature18,
        "T�l�ph�rique ou t�l�cabine � usage de loisirs");
    sProduit.createFeatureAttributeValue(nature18,
        "T�l�si�ge ou t�l�ski � usage de loisirs");
    sProduit.createFeatureAttributeValue(nature18,
        "Transport par c�ble � usage priv� ou industriel");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(transportParCable, "Toponyme", "string",
        false);
    AttributeType toponyme29 = transportParCable
        .getFeatureAttributeByName("Toponyme");
    toponyme29.setDefinition("");

    // Classe
    // A�rodrome/////////////////////////////////////////////////////////////

    sProduit.createFeatureType("A�rodrome");
    FeatureType aerodrome = (FeatureType) (sProduit
        .getFeatureTypeByName("A�rodrome"));
    aerodrome.setDefinition("");
    aerodrome.setIsAbstract(false);

    // AttributNature
    sProduit.createFeatureAttribute(aerodrome, "Nature", "string", true);
    AttributeType nature19 = aerodrome.getFeatureAttributeByName("Nature");
    nature19.setDefinition("");
    sProduit.createFeatureAttributeValue(nature19, "Normal");
    sProduit.createFeatureAttributeValue(nature19, "D'altitude");
    sProduit.createFeatureAttributeValue(nature19, "Sur l'eau");

    // Attribut Acc�s
    sProduit.createFeatureAttribute(aerodrome, "Acc�s", "string", true);
    AttributeType acces3 = aerodrome.getFeatureAttributeByName("Acc�s");
    acces3.setDefinition("");
    sProduit.createFeatureAttributeValue(acces3,
        "Desservi par au moins une ligne r�guli�re de transport de voyageurs");
    sProduit.createFeatureAttributeValue(acces3,
        "Desservi par aucune ligne r�guli�re de transport de voyageurs");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(aerodrome, "Toponyme", "string", false);
    AttributeType toponyme30 = aerodrome.getFeatureAttributeByName("Toponyme");
    toponyme30.setDefinition("");

    // Classe Piste
    // d'a�rodrome/////////////////////////////////////////////////////

    sProduit.createFeatureType("Piste d'a�rodrome");
    FeatureType pisteDAerodrome = (FeatureType) (sProduit
        .getFeatureTypeByName("Piste d'a�rodrome"));
    pisteDAerodrome.setDefinition("");
    pisteDAerodrome.setIsAbstract(false);

    // Classe
    // Cimeti�re/////////////////////////////////////////////////////////////

    sProduit.createFeatureType("Cimeti�re");
    FeatureType cimetiere = (FeatureType) (sProduit
        .getFeatureTypeByName("Cimeti�re"));
    cimetiere.setDefinition("");
    cimetiere.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(cimetiere, "Nature", "string", true);
    AttributeType nature20 = cimetiere.getFeatureAttributeByName("Nature");
    nature20.setDefinition("");
    sProduit.createFeatureAttributeValue(nature20, "Civil");
    sProduit.createFeatureAttributeValue(nature20, "N�cropole nationale");
    sProduit.createFeatureAttributeValue(nature20,
        "Cimeti�re militaire �tranger");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(cimetiere, "Toponyme", "string", true);
    AttributeType toponyme31 = cimetiere.getFeatureAttributeByName("Toponyme");
    toponyme31.setDefinition("");

    // Classe Enceinte
    // militaire////////////////////////////////////////////////////

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

    // Attribut Caract�re touristique
    sProduit.createFeatureAttribute(enceinteMilitaire, "Caract�re touristique",
        "string", true);
    AttributeType caractereTouristique2 = enceinteMilitaire
        .getFeatureAttributeByName("Caract�re touristique");
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

    // Classe
    // Digue/////////////////////////////////////////////////////////////////

    sProduit.createFeatureType("Digue");
    FeatureType digue = (FeatureType) (sProduit.getFeatureTypeByName("Digue"));
    digue.setDefinition("");
    digue.setIsAbstract(false);

    // Attribut Nature
    sProduit.createFeatureAttribute(digue, "Nature", "string", true);
    AttributeType nature22 = digue.getFeatureAttributeByName("Nature");
    nature22.setDefinition("");
    sProduit.createFeatureAttributeValue(nature22,
        "Digue d�limitant un barrage");
    sProduit.createFeatureAttributeValue(nature22, "Autre digue");

    // Classe M�tro
    // a�rien//////////////////////////////////////////////////////////

    sProduit.createFeatureType("M�tro a�rien");
    FeatureType metroAerien = (FeatureType) (sProduit
        .getFeatureTypeByName("M�tro a�rien"));
    metroAerien.setDefinition("");
    metroAerien.setIsAbstract(false);

    // Classe Sentier de grande
    // randonn�e///////////////////////////////////////////

    sProduit.createFeatureType("Sentier de grande randonn�e");
    FeatureType sentierDeGrandeRandonnee = (FeatureType) (sProduit
        .getFeatureTypeByName("Sentier de grande randonn�e"));
    sentierDeGrandeRandonnee.setDefinition("");
    sentierDeGrandeRandonnee.setIsAbstract(false);

    // Attribut Num�ro
    sProduit.createFeatureAttribute(sentierDeGrandeRandonnee, "Num�ro",
        "string", false);
    AttributeType numero = sentierDeGrandeRandonnee
        .getFeatureAttributeByName("Num�ro");
    numero.setDefinition("");

    // Attribut Toponyme
    sProduit.createFeatureAttribute(sentierDeGrandeRandonnee, "Toponyme",
        "string", false);
    AttributeType toponyme33 = sentierDeGrandeRandonnee
        .getFeatureAttributeByName("Toponyme");
    toponyme33.setDefinition("");

    /***************************************************************************
     * Ajout du th�me "Point remarquable du relief; massif bois�" *
     ************************************************************************/

    // Classe Point remarquable du
    // relief//////////////////////////////////////////

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
    sProduit.createFeatureAttributeValue(classification3, "2-Tr�s important");
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
    sProduit.createFeatureAttributeValue(nature23, "Crat�re, volcan");
    sProduit
        .createFeatureAttributeValue(
            nature23,
            "Cr�te, ar�te, ligne de fa�te; cha�ne de montagne, montagne, massif rocheux; mont, colline, mamelon, sommet");
    sProduit.createFeatureAttributeValue(nature23, "Coteau, versant, falaise");
    sProduit.createFeatureAttributeValue(nature23,
        "Cuvette, bassin ferm�, doline, d�pression");
    sProduit.createFeatureAttributeValue(nature23,
        "D�fil�, gorge, canyon; val, vall�e, vallon, ravin, thalweg, combe");
    sProduit.createFeatureAttributeValue(nature23, "�le, �lot, presqu'�le");
    sProduit.createFeatureAttributeValue(nature23,
        "Dune; isthme, cordon littoral;plage, gr�ve");
    sProduit.createFeatureAttributeValue(nature23, "Pic, aiguille, piton");
    sProduit.createFeatureAttributeValue(nature23, "Plaine, plateau");
    sProduit.createFeatureAttributeValue(nature23, "R�cifs, brisants, rochers");
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

    // Attribut Caract�re touristique
    sProduit.createFeatureAttribute(pointRemarquableDuRelief,
        "Caract�re touristique", "string", true);
    AttributeType caractereTouristique3 = pointRemarquableDuRelief
        .getFeatureAttributeByName("Caract�re touristique");
    caractereTouristique3.setDefinition("");
    sProduit.createFeatureAttributeValue(caractereTouristique3, "Oui");
    sProduit.createFeatureAttributeValue(caractereTouristique3, "Non");

    // Classe Massif
    // bois�/////////////////////////////////////////////////////////

    sProduit.createFeatureType("Massif bois�");
    FeatureType massifBoise = (FeatureType) (sProduit
        .getFeatureTypeByName("Massif bois�"));
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
        "Bois ou for�t de superficie sup�rieure � 2000 ha");
    sProduit.createFeatureAttributeValue(classification4,
        "Bois ou for�t de superficie comprise entre 1000 et 2000 ha");
    sProduit.createFeatureAttributeValue(classification4,
        "Bois ou for�t de superficie comprise entre 500 et 1000 ha");

    // Attribut Caract�re touristique
    sProduit.createFeatureAttribute(massifBoise, "Caract�re touristique",
        "string", true);
    AttributeType caractereTouristique4 = massifBoise
        .getFeatureAttributeByName("Caract�re touristique");
    caractereTouristique4.setDefinition("");
    sProduit.createFeatureAttributeValue(caractereTouristique4, "Oui");
    sProduit.createFeatureAttributeValue(caractereTouristique4, "Non");

    /***************************************************************************
     * Ajout du th�me "Administratif" *
     ************************************************************************/

    // Classe Territoire
    // �tranger//////////////////////////////////////////////////

    sProduit.createFeatureType("Territoire �tranger");
    FeatureType territoireEtranger = (FeatureType) (sProduit
        .getFeatureTypeByName("Territoire �tranger"));
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
        "Domaine marin (au del� de la laisse des plus hautes mers)");

    // Atribut Num�ro INSEE
    sProduit.createFeatureAttribute(territoireEtranger, "Num�ro INSEE",
        "string", true);
    AttributeType numeroINSEE = territoireEtranger
        .getFeatureAttributeByName("Num�ro INSEE");
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
        "00000-Domaine marin (au del� de la laisse des plus hautes mers)");

    // Classe
    // Commune//////////////////////////////////////////////////////////////

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

    // Attribut Num�ro INSEE
    sProduit.createFeatureAttribute(commune1, "Num�ro INSEE", "string", false);
    AttributeType numeroINSEE2 = commune1
        .getFeatureAttributeByName("Num�ro INSEE");
    numeroINSEE2.setDefinition("");

    // Attribut Centro�de
    sProduit.createFeatureAttribute(commune1, "Centro�de",
        "coordonn�es en Lambert II", false);
    AttributeType centroide = commune1.getFeatureAttributeByName("Centro�de");
    centroide.setDefinition("");

    // Classe Limite
    // administrative////////////////////////////////////////////////

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
        "Limite c�ti�re (laisse des plus hautes eaux)");
    sProduit.createFeatureAttributeValue(type4, "Fronti�re internationale");
    sProduit.createFeatureAttributeValue(type4, "Limite de r�gion");
    sProduit.createFeatureAttributeValue(type4, "Limite de d�partement");
    sProduit.createFeatureAttributeValue(type4, "Limite d'arrondissement");
    sProduit.createFeatureAttributeValue(type4, "Limite de pseudo-canto");
    sProduit.createFeatureAttributeValue(type4, "Limite de commune");

    // Attribut Pr�cision
    sProduit.createFeatureAttribute(limiteAdministrative, "Pr�cision",
        "string", true);
    AttributeType precision = limiteAdministrative
        .getFeatureAttributeByName("Pr�cision");
    precision.setDefinition("");
    sProduit.createFeatureAttributeValue(precision,
        "Pr�cision standard de localisation");
    sProduit
        .createFeatureAttributeValue(
            precision,
            "Pr�cision non d�finie (en particulier limite s'appuyant sur des surfaces d'eau domaine public de l'Etat)");

    // Relation d'association A pour chef-lieu
    sProduit.createFeatureAssociation("A pour chef-lieu", commune1,
        zoneDHabitat, "A pour chef-lieu", "est chef-lieu de");

    /***************************************************************************
     * Ajout du th�me "Occupation du sol" *
     ************************************************************************/

    // Classe Zone d'occupation du
    // sol/////////////////////////////////////////////////
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
    sProduit.createFeatureAttributeValue(poste, "B�ti");
    sProduit.createFeatureAttributeValue(poste, "Zone d'activit�s");
    sProduit.createFeatureAttributeValue(poste, "Carri�re, d�charge");
    sProduit
        .createFeatureAttributeValue(
            poste,
            "Prairie, pelouse, toute culture hormis vigne, verger, bananeraie, canne � sucre, rizi�re");
    sProduit.createFeatureAttributeValue(poste,
        "Vigne, verger, bananeraie, canne � sucre, rizi�re");
    sProduit.createFeatureAttributeValue(poste, "For�t");
    sProduit.createFeatureAttributeValue(poste, "Brousailles");
    sProduit
        .createFeatureAttributeValue(poste,
            "Plage, dune, sable, gravier, galet ou terrain nu sans couvert v�g�tal");
    sProduit.createFeatureAttributeValue(poste, "Rocher, �boulis");
    sProduit.createFeatureAttributeValue(poste, "Mangrove");
    sProduit.createFeatureAttributeValue(poste, "Marais, tourbi�re");
    sProduit.createFeatureAttributeValue(poste, "Marais salants");
    sProduit.createFeatureAttributeValue(poste, "Eau libre");
    sProduit.createFeatureAttributeValue(poste, "Glacier, n�v�");
    sProduit.createFeatureAttributeValue(poste, "Territoires construits");
    sProduit.createFeatureAttributeValue(poste,
        "Culture, prairie, v�g�tation naturelle hors for�t");
    sProduit.createFeatureAttributeValue(poste, "For�t");
    sProduit.createFeatureAttributeValue(poste,
        "Plage, dune, sable, gravier, galets");
    sProduit.createFeatureAttributeValue(poste, "Rocher, �boulis");
    sProduit.createFeatureAttributeValue(poste, "Marais, tourbi�re");
    sProduit.createFeatureAttributeValue(poste, "Marais salants");
    sProduit.createFeatureAttributeValue(poste, "Eau libre");
    sProduit.createFeatureAttributeValue(poste, "Glaciers, n�v�");

    /***************************************************************************
     * FIN DU SCHEMA *
     ************************************************************************/

    Integer compteurFT = 0;
    Integer compteurAT = 0;
    Integer compteurV = 0;
    System.out.println("R�capitulons:");
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
