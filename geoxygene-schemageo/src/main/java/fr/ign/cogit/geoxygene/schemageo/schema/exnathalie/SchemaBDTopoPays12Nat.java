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

public class SchemaBDTopoPays12Nat {

  public static SchemaConceptuelProduit creeSchemaBDTopoPays12() {

    /***************************************************************************
     * Creation du catalogue de la base de données *
     ************************************************************************/
    SchemaConceptuelProduit sProduit = new SchemaConceptuelProduit();
    sProduit.setNomSchema("Catalogue BDTOPO");
    sProduit.setBD("BDTOPO Pays V1.2");
    sProduit.setTagBD(1);
    sProduit.setDate("Décembre 2002");
    sProduit.setVersion("1.2");
    sProduit.setSource("Photogrammétrie");
    sProduit.setSujet("Composante topographique du RGE");

    /***************************************************************************
     * Ajout du thème routier *
     ************************************************************************/

    // Classe Tronçon de
    // route///////////////////////////////////////////////////

    sProduit.createFeatureType("Tronçon de route");
    FeatureType tronconDeRoute = (FeatureType) (sProduit
        .getFeatureTypeByName("Tronçon de route"));
    tronconDeRoute
        .setDefinition("Portion de voie de communication destinée aux automobiles, homogène pour l'ensemble des attributs et des relations qui la concernent. "
        		+ "Représente uniquement la chaussée, délimitée par les bas-côtés ou les trottoirs.");
    tronconDeRoute.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(tronconDeRoute, "Nature", "string", true);
    AttributeType nature1 = tronconDeRoute.getFeatureAttributeByName("Nature");
    nature1
        .setDefinition("Hiérarchiqation du réseau routier basée sur l'importance des tronçons de route pour le trafic routier.");
    sProduit.createFeatureAttributeValue(nature1, "Autoroutière");
    sProduit.createFeatureAttributeValue(nature1, "Principale");
    sProduit.createFeatureAttributeValue(nature1, "Régionale");
    sProduit.createFeatureAttributeValue(nature1, "Locale");
    sProduit.createFeatureAttributeValue(nature1, "Contre-allée");
    sProduit.createFeatureAttributeValue(nature1, "En construction");

    // Attribut Classement
    sProduit.createFeatureAttribute(tronconDeRoute, "Classement", "string",
        true);
    AttributeType classement1 = tronconDeRoute
        .getFeatureAttributeByName("Classement");
    classement1.setDefinition("Statut d'une route classée.");
    sProduit.createFeatureAttributeValue(classement1, "Autoroute");
    sProduit.createFeatureAttributeValue(classement1, "Nationale");
    sProduit.createFeatureAttributeValue(classement1, "Départementale");
    sProduit.createFeatureAttributeValue(classement1, "Autre classement");
    // Attribut Département gestionnaire
    sProduit.createFeatureAttribute(tronconDeRoute, "Département gestionnaire",
        "string", false);
    AttributeType depGest1 = tronconDeRoute
        .getFeatureAttributeByName("Département gestionnaire");
    depGest1.setDefinition("Département gestionnaire de l'objet.");
    // Attribut Fictif
    sProduit.createFeatureAttribute(tronconDeRoute, "Fictif", "booleen", false);
    AttributeType fictif1 = tronconDeRoute.getFeatureAttributeByName("Fictif");
    fictif1
        .setDefinition("La valeur 'vraie' indique que la géométrie du tronçon de route n'est pas significative. "
        		+ "La présence de ce dernier sert alors à doubler une 'surface de route' ou à raccorder une bretelle "
        		+ "à l'axe de la chaussée afin d'assurer la continuité du réseau routier linéaire.");
    // Attribut Franchissement
    sProduit.createFeatureAttribute(tronconDeRoute, "Franchissement", "string",
        true);
    AttributeType franchissement1 = tronconDeRoute
        .getFeatureAttributeByName("Franchissement");
    franchissement1
        .setDefinition("Indique la présence d'un obstacle physique dans le tracé d'une route et la manière dont il est franchissable.");
    sProduit.createFeatureAttributeValue(franchissement1, "Bac Auto");
    sProduit.createFeatureAttributeValue(franchissement1, "Gué ou radier");
    sProduit.createFeatureAttributeValue(franchissement1, "Pont");
    sProduit.createFeatureAttributeValue(franchissement1, "Tunnel");
    sProduit.createFeatureAttributeValue(franchissement1, "Sans objet");
    // Attribut Largeur de chaussée
    sProduit.createFeatureAttribute(tronconDeRoute, "Largeur de chaussée",
        "flottant", false);
    AttributeType largeurChaussee1 = tronconDeRoute
        .getFeatureAttributeByName("Largeur de chaussée");
    largeurChaussee1
        .setDefinition("Largeur de la chaussée en mètres. Pour le moment, la valeur indicative est calculée d'après le nombre de voies, et arrondie au demi-mètre.");
    // Attribut Nom
    sProduit.createFeatureAttribute(tronconDeRoute, "Nom", "string", false);
    AttributeType nom1 = tronconDeRoute.getFeatureAttributeByName("Nom");
    nom1.setDefinition("Nom de la route.");
    // Attribut Nombre de voies
    sProduit.createFeatureAttribute(tronconDeRoute, "Nombre de voies",
        "entier", false);
    AttributeType nbVoies1 = tronconDeRoute
        .getFeatureAttributeByName("Nombre de voies");
    nbVoies1
        .setDefinition("Nombre total de voies d'une route, d'une rue, ou d'une chaussée de route à chaussées séparées.");
    // Attribut Numéro de route
    sProduit.createFeatureAttribute(tronconDeRoute, "Numéro de route",
        "string", false);
    AttributeType numRoute1 = tronconDeRoute
        .getFeatureAttributeByName("Numéro de route");
    numRoute1.setDefinition("Numéro de la route.");

    // Classe Surface de route

    sProduit.createFeatureType("Surface de route");
    FeatureType surfaceDeRoute = (FeatureType) (sProduit
        .getFeatureTypeByName("Surface de route"));
    surfaceDeRoute
        .setDefinition("Partie de la chaussée d'une route caractérisée par une largeur exceptionnelle (place, carrefour, péage, parking). Zone à trafic non structuré.");
    surfaceDeRoute.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(surfaceDeRoute, "Nature", "string", true);
    AttributeType nature2 = surfaceDeRoute.getFeatureAttributeByName("Nature");
    nature2
        .setDefinition("Attribut permettant de distinguer divers types de surface de route.");
    sProduit.createFeatureAttributeValue(nature2, "Parking");
    sProduit.createFeatureAttributeValue(nature2, "Péage");
    sProduit.createFeatureAttributeValue(nature2, "Place ou carrefour");

    // Classe Tronçon de chemin

    sProduit.createFeatureType("Tronçon de chemin");
    FeatureType tronconDeChemin = (FeatureType) (sProduit
        .getFeatureTypeByName("Tronçon de chemin"));
    tronconDeChemin
        .setDefinition("Voie de communication terrestre non ferrée destinée aux pitéons, aux cycles ou aux animaux, "
        		+ "ou route sommairement revêtue (pas de revêtement de surface ou revêtement de surface très dégradé).");
    tronconDeChemin.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(tronconDeChemin, "Nature", "string", true);
    AttributeType nature3 = tronconDeChemin.getFeatureAttributeByName("Nature");
    nature3
        .setDefinition("Attribut permettant de distinguer plusieurs types de voies de communication terrestres.");
    sProduit.createFeatureAttributeValue(nature3, "Chemin empierré");
    sProduit.createFeatureAttributeValue(nature3, "Chemin");
    sProduit.createFeatureAttributeValue(nature3, "Sentier");
    sProduit.createFeatureAttributeValue(nature3, "Escalier");
    sProduit.createFeatureAttributeValue(nature3, "Piste cyclable");
    // Attribut Franchissement
    sProduit.createFeatureAttribute(tronconDeChemin, "Franchissement",
        "string", true);
    AttributeType franchissement3 = tronconDeChemin
        .getFeatureAttributeByName("Franchissement");
    franchissement3
        .setDefinition("Indique la présence d'un obstacle physique dans le tracé d'une route et la manière dont il est franchissable.");
    sProduit.createFeatureAttributeValue(franchissement3, "Bac piéton");
    sProduit.createFeatureAttributeValue(franchissement3, "Gué ou radier");
    sProduit.createFeatureAttributeValue(franchissement3, "Pont");
    sProduit.createFeatureAttributeValue(franchissement3, "Tunnel");
    sProduit.createFeatureAttributeValue(franchissement3, "Sans objet");
    // Attribut Nom
    sProduit.createFeatureAttribute(tronconDeChemin, "Nom", "string", false);
    AttributeType nom3 = tronconDeChemin.getFeatureAttributeByName("Nom");
    nom3.setDefinition("Nom du chemin.");

    /***************************************************************************
     * Ajout du thème "voies ferrées et autres moyens de transport terrestre" *
     ************************************************************************/

    // Classe Tronçon de voie ferrée

    sProduit.createFeatureType("Tronçon de voie ferrée");
    FeatureType tronconDeVoieFerree = (FeatureType) (sProduit
        .getFeatureTypeByName("Tronçon de voie ferrée"));
    tronconDeVoieFerree
        .setDefinition("Portion de voie ferrée homogène pour l'ensemble des attributs qui la concernent. "
        		+ "Dans le cas d'une ligne composée de deux à quatre voies parallèles, l'ensemble des voies est modélisé par un seul objet.");
    tronconDeVoieFerree.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(tronconDeVoieFerree, "Nature", "string",
        true);
    AttributeType nature4 = tronconDeVoieFerree
        .getFeatureAttributeByName("Nature");
    nature4
        .setDefinition("Attribut permettant de distinguer plusieurs types de voies ferrées selon leur fonction et leur état.");
    sProduit.createFeatureAttributeValue(nature4, "Voie TGV");
    sProduit.createFeatureAttributeValue(nature4, "Voie ferrée principale");
    sProduit.createFeatureAttributeValue(nature4, "Voie de service");
    sProduit.createFeatureAttributeValue(nature4, "Voie ferrée non exploitée");
    sProduit.createFeatureAttributeValue(nature4, "Transport urbain");
    sProduit.createFeatureAttributeValue(nature4, "Funiculaire ou crémaillère");
    sProduit
        .createFeatureAttributeValue(nature4, "Voie ferrée en construction");
    // Attribut Electifié
    sProduit.createFeatureAttribute(tronconDeVoieFerree, "Electrifié",
        "booleen", false);
    AttributeType electrifie4 = tronconDeVoieFerree
        .getFeatureAttributeByName("Electrifié");
    electrifie4
        .setDefinition("Energie servant à la propulsion des locomotives.");
    // Attribut Franchissement
    sProduit.createFeatureAttribute(tronconDeVoieFerree, "Franchissement",
        "string", true);
    AttributeType franchissement4 = tronconDeVoieFerree
        .getFeatureAttributeByName("Franchissement");
    franchissement4
        .setDefinition("Indique si le tronçon est situé sur un pont, dans un tunnel ou aucun des deux.");
    sProduit.createFeatureAttributeValue(franchissement4, "Pont");
    sProduit.createFeatureAttributeValue(franchissement4, "Tunnel");
    sProduit.createFeatureAttributeValue(franchissement4, "Sans objet");
    // Attribut Largeur de voie ferrée
    sProduit.createFeatureAttribute(tronconDeVoieFerree,
        "Largeur de voie ferrée", "string", true);
    AttributeType largeurVoieFerree4 = tronconDeVoieFerree
        .getFeatureAttributeByName("Largeur de voie ferrée");
    largeurVoieFerree4
        .setDefinition("Permet de distinguer les voies ferrées de largeur standard pour la France (1.44 m) des voies ferrées plus larges ou plus étroites.");
    sProduit.createFeatureAttributeValue(largeurVoieFerree4, "Etroite");
    sProduit.createFeatureAttributeValue(largeurVoieFerree4, "Normalisée");
    sProduit.createFeatureAttributeValue(largeurVoieFerree4, "Large");
    // Attribut Nombre de voies ferrées
    sProduit.createFeatureAttribute(tronconDeVoieFerree,
        "Nombre de voies ferrées", "entier", false);
    AttributeType nbVoiesFerrees4 = tronconDeVoieFerree
        .getFeatureAttributeByName("Nombre de voies ferrées");
    nbVoiesFerrees4
        .setDefinition("Indique si une ligne de chemin de fer est constituée d'une voie ferrée ou de plusieurs.");

    // Classe Aire de triage

    sProduit.createFeatureType("Aire de triage");
    FeatureType aireDeTriage = (FeatureType) (sProduit
        .getFeatureTypeByName("Aire de triage"));
    aireDeTriage
        .setDefinition("Ensemble des tronçons de voies, voies de garage, aiguillages permettant le tri des wagons et la composition des trains.");
    aireDeTriage.setIsAbstract(false);

    // Classe Transport par câble

    sProduit.createFeatureType("Transport par câble");
    FeatureType transportParCable = (FeatureType) (sProduit
        .getFeatureTypeByName("Transport par câble"));
    transportParCable
        .setDefinition("Moyen de transport constitué d'un ou plusieurs câbles porteurs.");
    transportParCable.setIsAbstract(false);
    // Attribut Nature
    sProduit
        .createFeatureAttribute(transportParCable, "Nature", "string", true);
    AttributeType nature6 = transportParCable
        .getFeatureAttributeByName("Nature");
    nature6
        .setDefinition("Attribut permettant de distinguer différentes natures de transport par câble.");
    sProduit.createFeatureAttributeValue(nature6, "Câble transporteur");
    sProduit.createFeatureAttributeValue(nature6,
        "Télécabine, téléphérique, téléski");

    /***************************************************************************
     * Ajout du thème "transport d'énergie et de fluides" *
     ************************************************************************/

    // Classe Ligne électrique

    sProduit.createFeatureType("Ligne électrique");
    FeatureType ligneElectrique = (FeatureType) (sProduit
        .getFeatureTypeByName("Ligne électrique"));
    ligneElectrique
        .setDefinition("Portion de ligne électrique homogène pour l'ensemble des attributs qui la concernent.");
    ligneElectrique.setIsAbstract(false);
    // Attribut Voltage
    sProduit.createFeatureAttribute(ligneElectrique, "Voltage", "string", true);
    AttributeType voltage7 = ligneElectrique
        .getFeatureAttributeByName("Voltage");
    voltage7.setDefinition("Tension de construction de la ligne électrique.");
    sProduit.createFeatureAttributeValue(voltage7, "63 kV");
    sProduit.createFeatureAttributeValue(voltage7, "90 kV");
    sProduit.createFeatureAttributeValue(voltage7, "150 kV");
    sProduit.createFeatureAttributeValue(voltage7, "225 kV");
    sProduit.createFeatureAttributeValue(voltage7, "400 kV");
    sProduit.createFeatureAttributeValue(voltage7, "Inconnu");

    // Classe Poste de
    // transformation///////////////////////////////////////////////////

    sProduit.createFeatureType("Poste de transformation");
    FeatureType posteTransformation = (FeatureType) (sProduit
        .getFeatureTypeByName("Poste de transformation"));
    posteTransformation
        .setDefinition("Enceinte à l'intérieur de laquelle le courant transporté par une ligne électrique est transformé.");
    posteTransformation.setIsAbstract(false);

    // Classe Tronçon de voie ferrée

    sProduit.createFeatureType("Canalisation");
    FeatureType canalisation = (FeatureType) (sProduit
        .getFeatureTypeByName("Canalisation"));
    canalisation.setDefinition("Canalisation ou tapis roulant");
    canalisation.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(canalisation, "Nature", "string", true);
    AttributeType nature9 = canalisation.getFeatureAttributeByName("Nature");
    nature9
        .setDefinition("Attribut permettant de différencier les canalisations d'eau des autres.");
    sProduit.createFeatureAttributeValue(nature9, "Eau");
    sProduit.createFeatureAttributeValue(nature9, "Autre");

    // Classe Pylône

    sProduit.createFeatureType("Pylône");
    FeatureType pylone = (FeatureType) (sProduit.getFeatureTypeByName("Pylône"));
    pylone.setDefinition("Support de ligne électrique.");
    pylone.setIsAbstract(false);

    /***************************************************************************
     * Ajout du thème "hydrographie terrestre" *
     ************************************************************************/

    // Classe Tronçon de cours d'eau

    sProduit.createFeatureType("Tronçon de cours d'eau");
    FeatureType tronconCoursDeau = (FeatureType) (sProduit
        .getFeatureTypeByName("Tronçon de cours d'eau"));
    tronconCoursDeau
        .setDefinition("Portion de cours d'eau, réel ou fictif, permanent ou temporaire, naturel ou artificiel, "
        		+ "homogène pour l'ensemble des attributs qui la concernent, et qui n'inclut pas de confluent.");
    tronconCoursDeau.setIsAbstract(false);
    // Attribut Artificialisé
    sProduit.createFeatureAttribute(tronconCoursDeau, "Artificialisé",
        "booleen", false);
    AttributeType artificialise11 = tronconCoursDeau
        .getFeatureAttributeByName("Artificialisé");
    artificialise11
        .setDefinition("Permet de distinguer les cours d'eau naturls des cours d'eau artificiels ou artificialisés.");
    // Attribut Fictif
    sProduit.createFeatureAttribute(tronconCoursDeau, "Fictif", "booleen",
        false);
    AttributeType fictif11 = tronconCoursDeau
        .getFeatureAttributeByName("Fictif");
    fictif11
        .setDefinition("La valeur 'vrai' permet de qualifier un objet dont la géométrie n'est pas significative, "
        		+ "et dont le rôle est d'assurer la continuité d'un réseau linéaire.");
    // Attribut Franchissement
    sProduit.createFeatureAttribute(tronconCoursDeau, "Franchissement",
        "string", true);
    AttributeType franchissement11 = tronconCoursDeau
        .getFeatureAttributeByName("Franchissement");
    franchissement11
        .setDefinition("Permet de distinguer les tronçons de cours d'eau libres des obstacles.");
    sProduit.createFeatureAttributeValue(franchissement11, "Barrage");
    sProduit.createFeatureAttributeValue(franchissement11, "Cascade");
    sProduit.createFeatureAttributeValue(franchissement11, "Ecluse");
    sProduit.createFeatureAttributeValue(franchissement11, "Pont-canal");
    sProduit.createFeatureAttributeValue(franchissement11, "Tunnel");
    sProduit.createFeatureAttributeValue(franchissement11, "Sans objet");
    // Attribut Nom
    sProduit.createFeatureAttribute(tronconCoursDeau, "Nom", "string", false);
    AttributeType nom11 = tronconCoursDeau.getFeatureAttributeByName("Nom");
    nom11.setDefinition("Nom du cours d'eau.");
    // Attribut Régime des eaux
    sProduit.createFeatureAttribute(tronconCoursDeau, "Régime des eaux",
        "string", true);
    AttributeType regime11 = tronconCoursDeau
        .getFeatureAttributeByName("Régime des eaux");
    regime11
        .setDefinition("Permet de caractériser un objet hydrographique en fonction du régime de ces eaux.");
    sProduit.createFeatureAttributeValue(regime11, "Permanent");
    sProduit.createFeatureAttributeValue(regime11, "Intermittent");

    // Classe Surface d'eau 

    sProduit.createFeatureType("Surface d'eau");
    FeatureType surfaceDeau = (FeatureType) (sProduit
        .getFeatureTypeByName("Surface d'eau"));
    surfaceDeau
        .setDefinition("Surface d'eau terrestre, naturelle ou artificielle.");
    surfaceDeau.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(surfaceDeau, "Nature", "string", true);
    AttributeType nature12 = surfaceDeau.getFeatureAttributeByName("Nature");
    nature12
        .setDefinition("Permet de distinguer les bassins des surfaces hydrographiques naturelles.");
    sProduit.createFeatureAttributeValue(nature12, "Bassin");
    sProduit.createFeatureAttributeValue(nature12, "Surface d'eau");
    // Attribut Régime des eaux
    sProduit.createFeatureAttribute(surfaceDeau, "Régime des eaux", "string",
        true);
    AttributeType regime12 = surfaceDeau
        .getFeatureAttributeByName("Régime des eaux");
    regime12
        .setDefinition("Permet de caractériser un objet hydrographique en fonction du régime de ces eaux.");
    sProduit.createFeatureAttributeValue(regime12, "Permanent");
    sProduit.createFeatureAttributeValue(regime12, "Intermittent");

    // Classe Point d'eau

    sProduit.createFeatureType("Point d'eau");
    FeatureType pointDeau = (FeatureType) (sProduit
        .getFeatureTypeByName("Point d'eau"));
    pointDeau
        .setDefinition("Source (captée ou non), point de production d'eau (pompage, forage, puits,...) ou point de stockage "
        		+ "d'eau de petite dimension (citerne, abreuvoir, lavoir, bassin).");
    pointDeau.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(pointDeau, "Nature", "string", true);
    AttributeType nature13 = pointDeau.getFeatureAttributeByName("Nature");
    nature13.setDefinition("Donne la nature du point d'eau.");
    sProduit.createFeatureAttributeValue(nature13, "Citerne");
    sProduit.createFeatureAttributeValue(nature13, "Fontaine");
    sProduit.createFeatureAttributeValue(nature13, "Source");
    sProduit.createFeatureAttributeValue(nature13, "Source captée");
    sProduit.createFeatureAttributeValue(nature13, "Station de pompage");
    sProduit.createFeatureAttributeValue(nature13, "Autre point d'eau");

    // Classe Tronçon de laisse

    sProduit.createFeatureType("Tronçon de laisse");
    FeatureType tronconDeLaisse = (FeatureType) (sProduit
        .getFeatureTypeByName("Tronçon de laisse"));
    tronconDeLaisse
        .setDefinition("Limite inférieure ou supérieure de l'estran.");
    tronconDeLaisse.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(tronconDeLaisse, "Nature", "string", true);
    AttributeType nature14 = tronconDeLaisse
        .getFeatureAttributeByName("Nature");
    nature14
        .setDefinition("Permet de distinguer la laisse des plus hautes mers de celle des plus basses mers.");
    sProduit.createFeatureAttributeValue(nature14, "Hautes mers");
    sProduit.createFeatureAttributeValue(nature14, "Basses mers");

    /***************************************************************************
     * Ajout du thème "surfaces d'activités et bâti" *
     ************************************************************************/

    // Classe Bâtiment

    sProduit.createFeatureType("Bâtiment");
    FeatureType batiment = (FeatureType) (sProduit
        .getFeatureTypeByName("Bâtiment"));
    batiment.setDefinition("Bâtiment de plus de 20 m2.");
    batiment.setIsAbstract(false);
    // Attribut Catégorie
    sProduit.createFeatureAttribute(batiment, "Catégorie", "string", true);
    AttributeType categorie15 = batiment.getFeatureAttributeByName("Catégorie");
    categorie15
        .setDefinition("Permet de distinguer plusieurs grandes catégories de bâtiment, selon leur fonction principale et leur aspect.");
    sProduit.createFeatureAttributeValue(categorie15, "Administratif");
    sProduit.createFeatureAttributeValue(categorie15,
        "Industriel, agricole ou commercial");
    sProduit.createFeatureAttributeValue(categorie15, "Religieux");
    sProduit.createFeatureAttributeValue(categorie15, "Sportif");
    sProduit.createFeatureAttributeValue(categorie15, "Transport");
    sProduit.createFeatureAttributeValue(categorie15, "Autre");
    // Attribut Nature
    sProduit.createFeatureAttribute(batiment, "Nature", "string", true);
    AttributeType nature15 = batiment.getFeatureAttributeByName("Nature");
    nature15
        .setDefinition("Permet de distinguer différents types de bâtiment.");
    sProduit.createFeatureAttributeValue(nature15, "Aérogare");
    sProduit.createFeatureAttributeValue(nature15, "Arc de triomphe");
    sProduit.createFeatureAttributeValue(nature15, "Arène ou théâtre antique");
    sProduit.createFeatureAttributeValue(nature15, "Bâtiment agricole");
    sProduit.createFeatureAttributeValue(nature15, "Bâtiment commercial");
    sProduit.createFeatureAttributeValue(nature15, "Bâtiment industriel");
    sProduit.createFeatureAttributeValue(nature15, "Bâtiment religieux divers");
    sProduit.createFeatureAttributeValue(nature15, "Bâtiment sportif");
    sProduit.createFeatureAttributeValue(nature15, "Chapelle");
    sProduit.createFeatureAttributeValue(nature15, "Château");
    sProduit.createFeatureAttributeValue(nature15, "Eglise");
    sProduit.createFeatureAttributeValue(nature15, "Fort, blockhaus, casemate");
    sProduit.createFeatureAttributeValue(nature15, "Gare");
    sProduit.createFeatureAttributeValue(nature15, "Mairie");
    sProduit.createFeatureAttributeValue(nature15, "Monument");
    sProduit.createFeatureAttributeValue(nature15, "Péage");
    sProduit.createFeatureAttributeValue(nature15, "Préfecture");
    sProduit.createFeatureAttributeValue(nature15, "Serre");
    sProduit.createFeatureAttributeValue(nature15, "Silo");
    sProduit.createFeatureAttributeValue(nature15, "Sous-préfecture");
    sProduit.createFeatureAttributeValue(nature15, "Tour, donjon, moulin");
    sProduit.createFeatureAttributeValue(nature15, "Tribune");
    sProduit.createFeatureAttributeValue(nature15, "Autre");
    // Attribut Hauteur
    sProduit.createFeatureAttribute(batiment, "Hauteur", "entier", false);
    AttributeType hauteur15 = batiment.getFeatureAttributeByName("Hauteur");
    hauteur15
        .setDefinition("Hauteur du bâtiment correspondant à la différence netre le Z le plus élevé du pourtour du bâtiment "
        		+ "et un point situé au pied du bâtiment. La hauteur est arrondie au mètre.");

    // Classe Construction ponctuelle

    sProduit.createFeatureType("Construction ponctuelle");
    FeatureType constructionPonctuelle = (FeatureType) (sProduit
        .getFeatureTypeByName("Construction ponctuelle"));
    constructionPonctuelle
        .setDefinition("Construction de faible emprise et de grande hauteur.");
    constructionPonctuelle.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(constructionPonctuelle, "Nature", "string",
        true);
    AttributeType nature16 = constructionPonctuelle
        .getFeatureAttributeByName("Nature");
    nature16
        .setDefinition("Permet de distinguer différentes natures de construction.");
    sProduit.createFeatureAttributeValue(nature16, "Antenne");
    sProduit.createFeatureAttributeValue(nature16, "Cheminée");
    sProduit.createFeatureAttributeValue(nature16, "Phare");
    sProduit.createFeatureAttributeValue(nature16, "Torchère");
    sProduit.createFeatureAttributeValue(nature16, "Transformateur");
    sProduit.createFeatureAttributeValue(nature16,
        "Construction ponctuelle quelconque");

    // Classe Construction linéaire

    sProduit.createFeatureType("Construction linéaire");
    FeatureType constructionLineaire = (FeatureType) (sProduit
        .getFeatureTypeByName("Construction linéaire"));
    constructionLineaire
        .setDefinition("Construction dont la forme générale est linéaire.");
    constructionLineaire.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(constructionLineaire, "Nature", "string",
        true);
    AttributeType nature17 = constructionLineaire
        .getFeatureAttributeByName("Nature");
    nature17.setDefinition("Permet de distinguer différents types d'ouvrages.");
    sProduit.createFeatureAttributeValue(nature17, "Barrage");
    sProduit.createFeatureAttributeValue(nature17, "Mur anti-bruit");
    sProduit.createFeatureAttributeValue(nature17, "Pont");
    sProduit.createFeatureAttributeValue(nature17, "Quai");
    sProduit.createFeatureAttributeValue(nature17, "Ruines");
    sProduit.createFeatureAttributeValue(nature17,
        "Construction linéaire quelconque");

    // Classe Construction surfacique

    sProduit.createFeatureType("Construction surfacique");
    FeatureType constructionSurfacique = (FeatureType) (sProduit
        .getFeatureTypeByName("Construction surfacique"));
    constructionSurfacique
        .setDefinition("Ouvrage de grande surface lié au franchissement d'un obstacle par une voie de communication, ou à l'aménagement d'une rivière ou d'un canal.");
    constructionSurfacique.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(constructionSurfacique, "Nature", "string",
        true);
    AttributeType nature18 = constructionSurfacique
        .getFeatureAttributeByName("Nature");
    nature18
        .setDefinition("Permet de distinguer différents types d'ouvrages d'art larges.");
    sProduit.createFeatureAttributeValue(nature18, "Barrage");
    sProduit.createFeatureAttributeValue(nature18, "Dalle de protection");
    sProduit.createFeatureAttributeValue(nature18, "Ecluse");
    sProduit.createFeatureAttributeValue(nature18, "Pont");
    sProduit.createFeatureAttributeValue(nature18, "Escalier");

    // Classe Réservoir

    sProduit.createFeatureType("Réservoir");
    FeatureType reservoir = (FeatureType) (sProduit
        .getFeatureTypeByName("Réservoir"));
    reservoir.setDefinition("Réservoir (eau, matières industrielles...).");
    reservoir.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(reservoir, "Nature", "string", true);
    AttributeType nature19 = reservoir.getFeatureAttributeByName("Nature");
    nature19
        .setDefinition("Permet de distinguer différents types de réservoirs.");
    sProduit.createFeatureAttributeValue(nature19, "Château d'eau");
    sProduit.createFeatureAttributeValue(nature19, "Réservoir d'eau");
    sProduit.createFeatureAttributeValue(nature19, "Réservoir industriel");
    sProduit.createFeatureAttributeValue(nature19, "Inconnue");
    // Attribut Hauteur
    sProduit.createFeatureAttribute(reservoir, "Hauteur", "entier", false);
    AttributeType hauteur19 = reservoir.getFeatureAttributeByName("Hauteur");
    hauteur19
        .setDefinition("Hauteur du réservoir correspondant à la différence netre le Z le plus élevé du pourtour "
        		+ "et un point situé au pied du réservoir. La hauteur est arrondie au mètre.");

    // Classe Terrain de sport

    sProduit.createFeatureType("Terrain de sport");
    FeatureType terrainDeSport = (FeatureType) (sProduit
        .getFeatureTypeByName("Terrain de sport"));
    terrainDeSport.setDefinition("Equipement sportif de plein air.");
    terrainDeSport.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(terrainDeSport, "Nature", "string", true);
    AttributeType nature20 = terrainDeSport.getFeatureAttributeByName("Nature");
    nature20
        .setDefinition("Permet de distinguer différents types d'équipements sportifs.");
    sProduit.createFeatureAttributeValue(nature20, "Piste de sport");
    sProduit.createFeatureAttributeValue(nature20, "Terrain de tennis");
    sProduit.createFeatureAttributeValue(nature20, "Bassin de natation");
    sProduit.createFeatureAttributeValue(nature20,
        "Terrain de sport indifférencié");

    // Classe Cimetière

    sProduit.createFeatureType("Cimetière");
    FeatureType cimetiere = (FeatureType) (sProduit
        .getFeatureTypeByName("Cimetière"));
    cimetiere.setDefinition("Endroit où reposent les morts.");
    cimetiere.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(cimetiere, "Nature", "string", true);
    AttributeType nature21 = cimetiere.getFeatureAttributeByName("Nature");
    nature21
        .setDefinition("Permet de distinguer un objet géographique à vocation militaire ou simplement géré par le Ministère de la Défense, "
        		+ "d'un objet civil. La valeur 'militaire' est également affectée aux cimetières militaires gérés par le Ministère des "
        		+ "Anciens Combattants ou par des Etats étrangers.");
    sProduit.createFeatureAttributeValue(nature21, "Militaire");
    sProduit.createFeatureAttributeValue(nature21, "Autre");

    // Classe Piste d'aérodrome

    sProduit.createFeatureType("Piste d'aérodrome");
    FeatureType pisteAerodrome = (FeatureType) (sProduit
        .getFeatureTypeByName("Piste d'aérodrome"));
    pisteAerodrome
        .setDefinition("Aire située sur un aérodrome, aménagée afin de servir au roulement des aéronefs, au décollage et à l'atterrissage.");
    pisteAerodrome.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(pisteAerodrome, "Nature", "string", true);
    AttributeType nature22 = pisteAerodrome.getFeatureAttributeByName("Nature");
    nature22
        .setDefinition("Permet de distinguer différentes natures d'aérodrome.");
    sProduit.createFeatureAttributeValue(nature22, "Piste en dur");
    sProduit.createFeatureAttributeValue(nature22, "Piste en herbe");

    // Classe Point d'activité ou d'intérêt

    sProduit.createFeatureType("Point d'activité ou d'intérêt");
    FeatureType pointActivite = (FeatureType) (sProduit
        .getFeatureTypeByName("Point d'activité ou d'intérêt"));
    pointActivite
        .setDefinition("Objet ponctuel localisant un équipement public, un site ou une zone ayant un caractère administratif, culturel, sportif, industriel ou commercial.");
    pointActivite.setIsAbstract(false);
    // Attribut Catégorie
    sProduit.createFeatureAttribute(pointActivite, "Catégorie", "string", true);
    AttributeType categorie23 = pointActivite
        .getFeatureAttributeByName("Catégorie");
    categorie23.setDefinition("Permet de distinguer le type de l'activité.");
    sProduit.createFeatureAttributeValue(categorie23, "Administratif");
    sProduit.createFeatureAttributeValue(categorie23, "Culture et loisirs");
    sProduit.createFeatureAttributeValue(categorie23, "Enseignement");
    sProduit.createFeatureAttributeValue(categorie23, "Gestion des eaux");
    sProduit.createFeatureAttributeValue(categorie23,
        "Industriel ou commercial");
    sProduit.createFeatureAttributeValue(categorie23, "Religieux");
    sProduit.createFeatureAttributeValue(categorie23, "Santé");
    sProduit.createFeatureAttributeValue(categorie23, "Sport");
    sProduit.createFeatureAttributeValue(categorie23, "Transport");
    // Attribut Nature
    sProduit.createFeatureAttribute(pointActivite, "Nature", "string", true);
    AttributeType nature23 = pointActivite.getFeatureAttributeByName("Nature");
    nature23
        .setDefinition("Précise la fonction du bâtiment ou le type de l'activité.");
    sProduit
        .createFeatureAttributeValue(nature23, "Bureau ou hôtel des postes");
    sProduit.createFeatureAttributeValue(nature23, "Caserne de pompiers");
    sProduit.createFeatureAttributeValue(nature23,
        "Divers public ou administratif");
    sProduit.createFeatureAttributeValue(nature23, "Enceinte militaire");
    sProduit.createFeatureAttributeValue(nature23,
        "Etablissement pénitentiaire");
    sProduit.createFeatureAttributeValue(nature23, "Gendarmerie");
    sProduit.createFeatureAttributeValue(nature23, "Hôtel de département");
    sProduit.createFeatureAttributeValue(nature23, "Hôtel de région");
    sProduit.createFeatureAttributeValue(nature23, "Mairie");
    sProduit.createFeatureAttributeValue(nature23, "Maison forestière");
    sProduit.createFeatureAttributeValue(nature23, "Palais de justice");
    sProduit.createFeatureAttributeValue(nature23, "Poste ou hôtel de police");
    sProduit.createFeatureAttributeValue(nature23, "Préfecture");
    sProduit.createFeatureAttributeValue(nature23, "Sous-préfecture");
    sProduit.createFeatureAttributeValue(nature23, "Camping");
    sProduit.createFeatureAttributeValue(nature23, "Dolmen");
    sProduit.createFeatureAttributeValue(nature23, "Habitation troglodytique");
    sProduit.createFeatureAttributeValue(nature23, "Maison du parc");
    sProduit.createFeatureAttributeValue(nature23, "Menhir");
    sProduit.createFeatureAttributeValue(nature23, "Musée");
    sProduit.createFeatureAttributeValue(nature23, "Parc de loisirs");
    sProduit.createFeatureAttributeValue(nature23, "Parc zoologique");
    sProduit.createFeatureAttributeValue(nature23, "Point de vue");
    sProduit.createFeatureAttributeValue(nature23, "Village de vacances");
    sProduit.createFeatureAttributeValue(nature23, "Enseignement primaire");
    sProduit.createFeatureAttributeValue(nature23, "Enseignement secondaire");
    sProduit.createFeatureAttributeValue(nature23, "Enseignement supérieur");
    sProduit.createFeatureAttributeValue(nature23, "Station de pompage");
    sProduit.createFeatureAttributeValue(nature23,
        "Usine de traitement des eaux");
    sProduit.createFeatureAttributeValue(nature23, "Aquaculture");
    sProduit.createFeatureAttributeValue(nature23, "Carrière");
    sProduit.createFeatureAttributeValue(nature23, "Centrale électrique");
    sProduit.createFeatureAttributeValue(nature23, "Divers commercial");
    sProduit.createFeatureAttributeValue(nature23, "Divers industriel");
    sProduit.createFeatureAttributeValue(nature23, "Haras national");
    sProduit.createFeatureAttributeValue(nature23, "Marais salants");
    sProduit.createFeatureAttributeValue(nature23, "Marché");
    sProduit.createFeatureAttributeValue(nature23, "Mine");
    sProduit.createFeatureAttributeValue(nature23, "Usine");
    sProduit.createFeatureAttributeValue(nature23,
        "Culte catholique ou orthodoxe");
    sProduit.createFeatureAttributeValue(nature23, "Culte protestant");
    sProduit.createFeatureAttributeValue(nature23, "Culte israélite");
    sProduit.createFeatureAttributeValue(nature23, "Culte islamique");
    sProduit.createFeatureAttributeValue(nature23, "Culte divers");
    sProduit.createFeatureAttributeValue(nature23, "Etablissement hospitalier");
    sProduit.createFeatureAttributeValue(nature23, "Etablissement thermal");
    sProduit.createFeatureAttributeValue(nature23, "Golf");
    sProduit.createFeatureAttributeValue(nature23, "Hippodrome");
    sProduit.createFeatureAttributeValue(nature23, "Piscine");
    sProduit.createFeatureAttributeValue(nature23, "Stade");
    sProduit.createFeatureAttributeValue(nature23, "Aérodrome");
    sProduit.createFeatureAttributeValue(nature23, "Aire d'autoroute");
    sProduit.createFeatureAttributeValue(nature23, "Gare voyageurs uniquement");
    sProduit.createFeatureAttributeValue(nature23, "Gare voyageurs et fret");
    sProduit.createFeatureAttributeValue(nature23, "Gare fret uniquement");
    sProduit.createFeatureAttributeValue(nature23, "Péage");

    // Classe Surface d'activité

    sProduit.createFeatureType("Surface d'activité");
    FeatureType surfaceActivite = (FeatureType) (sProduit
        .getFeatureTypeByName("Surface d'activité"));
    surfaceActivite
        .setDefinition("Enceinte d'un équipement public, d'un site ou d'une zone ayant un caractère administratif, culturel, sportif, industriel ou commercial.");
    surfaceActivite.setIsAbstract(false);
    // Attribut Catégorie
    sProduit.createFeatureAttribute(surfaceActivite, "Catégorie", "string",
        true);
    AttributeType categorie24 = surfaceActivite
        .getFeatureAttributeByName("Catégorie");
    categorie24.setDefinition("Permet de distinguer le type de l'activité.");
    sProduit.createFeatureAttributeValue(categorie24, "Administratif");
    sProduit.createFeatureAttributeValue(categorie24, "Culture et loisirs");
    sProduit.createFeatureAttributeValue(categorie24, "Enseignement");
    sProduit.createFeatureAttributeValue(categorie24, "Gestion des eaux");
    sProduit.createFeatureAttributeValue(categorie24,
        "Industriel ou commercial");
    sProduit.createFeatureAttributeValue(categorie24, "Santé");
    sProduit.createFeatureAttributeValue(categorie24, "Sport");
    sProduit.createFeatureAttributeValue(categorie24, "Transport");

    /***************************************************************************
     * Ajout du thème "occupation du sol: végétation" *
     ************************************************************************/

    // Classe Zone arborée

    sProduit.createFeatureType("Zone arborée");
    FeatureType zoneArboree = (FeatureType) (sProduit
        .getFeatureTypeByName("Zone arborée"));
    zoneArboree.setDefinition("Espace peuplé d'arbres d'essence quelconque.");
    zoneArboree.setIsAbstract(false);

    /***************************************************************************
     * Ajout du thème "orographie" *
     ************************************************************************/

    // Classe Ligne orographique

    sProduit.createFeatureType("Ligne orographique");
    FeatureType ligneOrographique = (FeatureType) (sProduit
        .getFeatureTypeByName("Ligne orographique"));
    ligneOrographique.setDefinition("Ligne de rupture de pente artificielle.");
    ligneOrographique.setIsAbstract(false);
    // Attribut Catégorie
    sProduit
        .createFeatureAttribute(ligneOrographique, "Nature", "string", true);
    AttributeType nature26 = ligneOrographique
        .getFeatureAttributeByName("Nature");
    nature26
        .setDefinition("Permet de distinguer différentes natures de lignes orographiques.");
    sProduit.createFeatureAttributeValue(nature26, "Levée");
    sProduit.createFeatureAttributeValue(nature26, "Mur de soutênement");
    sProduit.createFeatureAttributeValue(nature26, "Talus");
    sProduit.createFeatureAttributeValue(nature26, "Carrière");

    /***************************************************************************
     * Ajout du thème "zonages techiniques et administratifs" *
     ************************************************************************/

    // Classe Commune

    sProduit.createFeatureType("Commune");
    FeatureType commune = (FeatureType) (sProduit
        .getFeatureTypeByName("Commune"));
    commune
        .setDefinition("Plus petite subdivision du territoire, administrée par un maire, des adjoints et un conseil municipal.");
    commune.setIsAbstract(false);
    // Attribut Nom
    sProduit.createFeatureAttribute(commune, "Nom", "string", false);
    AttributeType nom27 = commune.getFeatureAttributeByName("Nom");
    nom27.setDefinition("Nom officiel de la commune.");
    // Attribut Code INSEE
    sProduit.createFeatureAttribute(commune, "Code INSEE", "string", false);
    AttributeType numINSEE27 = commune.getFeatureAttributeByName("Code INSEE");
    numINSEE27.setDefinition("Numéro INSEE de la commune.");
    // Attribut Statut
    sProduit.createFeatureAttribute(commune, "Statut", "string", true);
    AttributeType statut27 = commune.getFeatureAttributeByName("Statut");
    statut27
        .setDefinition("Précise le rôle le plus élevé qu'a la commune dans la hiérarchie des entités administratives.");
    sProduit.createFeatureAttributeValue(statut27, "Capitale d'Etat");
    sProduit.createFeatureAttributeValue(statut27, "Préfecture de région");
    sProduit.createFeatureAttributeValue(statut27, "Préfecture");
    sProduit.createFeatureAttributeValue(statut27, "Sous-préfecture");
    sProduit.createFeatureAttributeValue(statut27, "Chef-lieu de canton");
    sProduit.createFeatureAttributeValue(statut27, "Commune simple");
    // Attribut Canton
    sProduit.createFeatureAttribute(commune, "Canton", "string", false);
    AttributeType canton27 = commune.getFeatureAttributeByName("Canton");
    canton27.setDefinition("Nom du chef-lieu de canton.");
    // Attribut Arrondissement
    sProduit.createFeatureAttribute(commune, "Arrondissement", "string", false);
    AttributeType arrondissement27 = commune
        .getFeatureAttributeByName("Arrondissement");
    arrondissement27.setDefinition("Nom du chef-lieu d'arrondissement.");
    // Attribut Département
    sProduit.createFeatureAttribute(commune, "Département", "string", false);
    AttributeType departement27 = commune
        .getFeatureAttributeByName("Département");
    departement27
        .setDefinition("Nom du département auquel appartient la commune.");
    // Attribut Région
    sProduit.createFeatureAttribute(commune, "Région", "string", false);
    AttributeType region27 = commune.getFeatureAttributeByName("Région");
    region27
        .setDefinition("Nom de la région à laquelle appartient la commune.");
    // Attribut Population
    sProduit.createFeatureAttribute(commune, "Population", "entier", false);
    AttributeType population27 = commune
        .getFeatureAttributeByName("Population");
    population27.setDefinition("Population sans double-compte de la commune.");
    // Attribut Multi-canton
    sProduit.createFeatureAttribute(commune, "Multi-canton", "booleen", false);
    AttributeType multiCanton27 = commune
        .getFeatureAttributeByName("Multi-canton");
    multiCanton27
        .setDefinition("Permet de différencier une commune multi-canton d'une commune n'appartenant qu'à un seul canton.");

    // Classe Arrondissement municipal

    sProduit.createFeatureType("Arrondissement municipal");
    FeatureType arrondissementMunicipal = (FeatureType) (sProduit
        .getFeatureTypeByName("Arrondissement municipal"));
    arrondissementMunicipal
        .setDefinition("Subdivision administrative de certaines communes (Paris, Lyon, Marseille).");
    arrondissementMunicipal.setIsAbstract(false);
    // Attribut Nom
    sProduit.createFeatureAttribute(arrondissementMunicipal, "Nom", "string",
        false);
    AttributeType nom28 = arrondissementMunicipal
        .getFeatureAttributeByName("Nom");
    nom28
        .setDefinition("Nom de l'arrondissement (exemple 'Paris 1er arrondissement').");
    // Attribut Code INSEE
    sProduit.createFeatureAttribute(arrondissementMunicipal, "Code INSEE",
        "string", false);
    AttributeType numINSEE28 = arrondissementMunicipal
        .getFeatureAttributeByName("Code INSEE");
    numINSEE28.setDefinition("Numéro INSEE de l'arrondissement municipal.");

    /***************************************************************************
     * Ajout du thème "objets divers" *
     ************************************************************************/

    // Classe Lieu-dit habité

    sProduit.createFeatureType("Lieu-dit habité");
    FeatureType lieuDitHabite = (FeatureType) (sProduit
        .getFeatureTypeByName("Lieu-dit habité"));
    lieuDitHabite.setDefinition("Lieu-dit habité caractérisé par un nom.");
    lieuDitHabite.setIsAbstract(false);
    // Attribut Nom
    sProduit.createFeatureAttribute(lieuDitHabite, "Nom", "string", false);
    AttributeType nom29 = lieuDitHabite.getFeatureAttributeByName("Nom");
    nom29.setDefinition("Toponyme associé au lieu-dit.");
    // Attribut Importance
    sProduit
        .createFeatureAttribute(lieuDitHabite, "Importance", "entier", true);
    AttributeType importance29 = lieuDitHabite
        .getFeatureAttributeByName("Importance");
    importance29
        .setDefinition("Code permettant d'établir une hiérarchie dans la toponymie.");
    sProduit.createFeatureAttributeValue(importance29, "1");
    sProduit.createFeatureAttributeValue(importance29, "2");
    sProduit.createFeatureAttributeValue(importance29, "3");
    sProduit.createFeatureAttributeValue(importance29, "4");
    sProduit.createFeatureAttributeValue(importance29, "5");
    sProduit.createFeatureAttributeValue(importance29, "6");
    sProduit.createFeatureAttributeValue(importance29, "7");
    sProduit.createFeatureAttributeValue(importance29, "8");
    // Attribut Nature
    sProduit.createFeatureAttribute(lieuDitHabite, "Nature", "string", true);
    AttributeType nature29 = lieuDitHabite.getFeatureAttributeByName("Nature");
    nature29.setDefinition("Donne plus précisément la nature du lieu nommé.");
    sProduit.createFeatureAttributeValue(nature29, "Canton");
    sProduit.createFeatureAttributeValue(nature29, "Château");
    sProduit.createFeatureAttributeValue(nature29, "Commune");
    sProduit.createFeatureAttributeValue(nature29, "Construction");
    sProduit.createFeatureAttributeValue(nature29, "Culte");
    sProduit.createFeatureAttributeValue(nature29, "Culture");
    sProduit.createFeatureAttributeValue(nature29, "Ecart");
    sProduit.createFeatureAttributeValue(nature29, "Enseignement");
    sProduit.createFeatureAttributeValue(nature29, "Préfecture");
    sProduit.createFeatureAttributeValue(nature29, "Quartier");
    sProduit.createFeatureAttributeValue(nature29, "Refuge");
    sProduit.createFeatureAttributeValue(nature29, "Santé");
    sProduit.createFeatureAttributeValue(nature29, "Science");
    sProduit.createFeatureAttributeValue(nature29, "Sous-préfecture");

    // Classe Lieu-dit non habité

    sProduit.createFeatureType("Lieu-dit non habité");
    FeatureType lieuDitNonHabite = (FeatureType) (sProduit
        .getFeatureTypeByName("Lieu-dit non habité"));
    lieuDitNonHabite
        .setDefinition("Lieu-dit non habité et dont le nom ne se rapporte ni à un détail orographqiue, ni à un détail hydrographique.");
    lieuDitNonHabite.setIsAbstract(false);
    // Attribut Nom
    sProduit.createFeatureAttribute(lieuDitNonHabite, "Nom", "string", false);
    AttributeType nom30 = lieuDitNonHabite.getFeatureAttributeByName("Nom");
    nom30.setDefinition("Toponyme associé au lieu-dit.");
    // Attribut Importance
    sProduit.createFeatureAttribute(lieuDitNonHabite, "Importance", "entier",
        true);
    AttributeType importance30 = lieuDitNonHabite
        .getFeatureAttributeByName("Importance");
    importance30
        .setDefinition("Code permettant d'établir une hiérarchie dans la toponymie.");
    sProduit.createFeatureAttributeValue(importance30, "1");
    sProduit.createFeatureAttributeValue(importance30, "2");
    sProduit.createFeatureAttributeValue(importance30, "3");
    sProduit.createFeatureAttributeValue(importance30, "4");
    sProduit.createFeatureAttributeValue(importance30, "5");
    sProduit.createFeatureAttributeValue(importance30, "6");
    sProduit.createFeatureAttributeValue(importance30, "7");
    sProduit.createFeatureAttributeValue(importance30, "8");
    // Attribut Nature
    sProduit.createFeatureAttribute(lieuDitNonHabite, "Nature", "string", true);
    AttributeType nature30 = lieuDitNonHabite
        .getFeatureAttributeByName("Nature");
    nature30.setDefinition("Donne plus précisément la nature du lieu nommé.");
    sProduit.createFeatureAttributeValue(nature30, "Arbre");
    sProduit.createFeatureAttributeValue(nature30, "Bois");
    sProduit.createFeatureAttributeValue(nature30, "Lieu-dit");
    sProduit.createFeatureAttributeValue(nature30, "Parc");

    // Classe Oronyme

    sProduit.createFeatureType("Oronyme");
    FeatureType oronyme = (FeatureType) (sProduit
        .getFeatureTypeByName("Oronyme"));
    oronyme.setDefinition("Détail du relief portant un nom.");
    oronyme.setIsAbstract(false);
    // Attribut Nom
    sProduit.createFeatureAttribute(oronyme, "Nom", "string", false);
    AttributeType nom31 = oronyme.getFeatureAttributeByName("Nom");
    nom31.setDefinition("Nom du détail ou du relief.");
    // Attribut Importance
    sProduit.createFeatureAttribute(oronyme, "Importance", "entier", true);
    AttributeType importance31 = oronyme
        .getFeatureAttributeByName("Importance");
    importance31
        .setDefinition("Code permettant d'établir une hiérarchie dans la toponymie. La valeur 1 correspond aux détails "
        		+ "les plus importants (ex.massif de montagne), tandis que la valeur 8 caractérise des accidents de terrain mineurs "
        		+ "(ravine, ébouli, rocher caractéristique...).");
    sProduit.createFeatureAttributeValue(importance31, "1");
    sProduit.createFeatureAttributeValue(importance31, "2");
    sProduit.createFeatureAttributeValue(importance31, "3");
    sProduit.createFeatureAttributeValue(importance31, "4");
    sProduit.createFeatureAttributeValue(importance31, "5");
    sProduit.createFeatureAttributeValue(importance31, "6");
    sProduit.createFeatureAttributeValue(importance31, "7");
    sProduit.createFeatureAttributeValue(importance31, "8");
    // Attribut Nature
    sProduit.createFeatureAttribute(oronyme, "Nature", "string", true);
    AttributeType nature31 = oronyme.getFeatureAttributeByName("Nature");
    nature31.setDefinition("Donne plus précisément la nature du lieu nommé.");
    sProduit.createFeatureAttributeValue(nature31, "Cap");
    sProduit.createFeatureAttributeValue(nature31, "Carrière");
    sProduit.createFeatureAttributeValue(nature31, "Cirque");
    sProduit.createFeatureAttributeValue(nature31, "Col");
    sProduit.createFeatureAttributeValue(nature31, "Crête");
    sProduit.createFeatureAttributeValue(nature31, "Dépression");
    sProduit.createFeatureAttributeValue(nature31, "Dune");
    sProduit.createFeatureAttributeValue(nature31, "Escarpement");
    sProduit.createFeatureAttributeValue(nature31, "Gorge");
    sProduit.createFeatureAttributeValue(nature31, "Grotte");
    sProduit.createFeatureAttributeValue(nature31, "île");
    sProduit.createFeatureAttributeValue(nature31, "Isthme");
    sProduit.createFeatureAttributeValue(nature31, "Montagne");
    sProduit.createFeatureAttributeValue(nature31, "Pic");
    sProduit.createFeatureAttributeValue(nature31, "Plage");
    sProduit.createFeatureAttributeValue(nature31, "Plaine ou plateau");
    sProduit.createFeatureAttributeValue(nature31, "Récifs");
    sProduit.createFeatureAttributeValue(nature31, "Rochers");
    sProduit.createFeatureAttributeValue(nature31, "Sommet");
    sProduit.createFeatureAttributeValue(nature31, "Vallée");
    sProduit.createFeatureAttributeValue(nature31, "Versant");
    sProduit.createFeatureAttributeValue(nature31, "Volcan");

    // Classe Hydronyme

    sProduit.createFeatureType("Hydronyme");
    FeatureType hydronyme = (FeatureType) (sProduit
        .getFeatureTypeByName("Hydronyme"));
    hydronyme.setDefinition("Nom se rapportant à un détail hydrographique.");
    hydronyme.setIsAbstract(false);
    // Attribut Nom
    sProduit.createFeatureAttribute(hydronyme, "Nom", "string", false);
    AttributeType nom32 = hydronyme.getFeatureAttributeByName("Nom");
    nom32.setDefinition("Nom du détail hydrographique.");
    // Attribut Importance
    sProduit.createFeatureAttribute(hydronyme, "Importance", "entier", true);
    AttributeType importance32 = hydronyme
        .getFeatureAttributeByName("Importance");
    importance32
        .setDefinition("Code permettant d'établir une hiérarchie dans la toponymie. La valeur 1 correspond aux objets "
        		+ "les plus importants (ex. golfe, mer, fleuve), tandis que la valeur 8 caractérise des détails mineurs (petit ruisseau, mare...).");
    sProduit.createFeatureAttributeValue(importance32, "1");
    sProduit.createFeatureAttributeValue(importance32, "2");
    sProduit.createFeatureAttributeValue(importance32, "3");
    sProduit.createFeatureAttributeValue(importance32, "4");
    sProduit.createFeatureAttributeValue(importance32, "5");
    sProduit.createFeatureAttributeValue(importance32, "6");
    sProduit.createFeatureAttributeValue(importance32, "7");
    sProduit.createFeatureAttributeValue(importance32, "8");
    // Attribut Nature
    sProduit.createFeatureAttribute(hydronyme, "Nature", "string", true);
    AttributeType nature32 = hydronyme.getFeatureAttributeByName("Nature");
    nature32
        .setDefinition("Donne plus précisément la nature de l'objet nommé.");
    sProduit.createFeatureAttributeValue(nature32, "Amer");
    sProduit.createFeatureAttributeValue(nature32, "Baie");
    sProduit.createFeatureAttributeValue(nature32, "Banc");
    sProduit.createFeatureAttributeValue(nature32, "Canal");
    sProduit.createFeatureAttributeValue(nature32, "Cascade");
    sProduit.createFeatureAttributeValue(nature32, "Cours d'eau");
    sProduit.createFeatureAttributeValue(nature32, "Embouchure");
    sProduit.createFeatureAttributeValue(nature32, "Espace maritime");
    sProduit.createFeatureAttributeValue(nature32, "Glacier");
    sProduit.createFeatureAttributeValue(nature32, "Lac");
    sProduit.createFeatureAttributeValue(nature32, "Marais");
    sProduit.createFeatureAttributeValue(nature32, "Pêcherie");
    sProduit.createFeatureAttributeValue(nature32, "Perte");
    sProduit.createFeatureAttributeValue(nature32, "Point d'eau");

    // Classe Toponyme communication

    sProduit.createFeatureType("Toponyme communication");
    FeatureType toponymeCommunication = (FeatureType) (sProduit
        .getFeatureTypeByName("Toponyme communication"));
    toponymeCommunication
        .setDefinition("Installation nommée servant de noeud dans un réseau.");
    toponymeCommunication.setIsAbstract(false);
    // Attribut Nom
    sProduit.createFeatureAttribute(toponymeCommunication, "Nom", "string",
        false);
    AttributeType nom33 = toponymeCommunication
        .getFeatureAttributeByName("Nom");
    nom33.setDefinition("Nom de l'installation.");
    // Attribut Importance
    sProduit.createFeatureAttribute(toponymeCommunication, "Importance",
        "entier", true);
    AttributeType importance33 = toponymeCommunication
        .getFeatureAttributeByName("Importance");
    importance33
        .setDefinition("Code permettant d'établir une hiérarchie dans la toponymie. "
        		+ "Les trois premières valeurs ne sont pas utilisées pour ce type de détail. "
        		+ "Les suivantes vont des objets les plus importants (aéroport intenational) aux moins importants (remonte-prente, passerelle,...).");
    sProduit.createFeatureAttributeValue(importance33, "1");
    sProduit.createFeatureAttributeValue(importance33, "2");
    sProduit.createFeatureAttributeValue(importance33, "3");
    sProduit.createFeatureAttributeValue(importance33, "4");
    sProduit.createFeatureAttributeValue(importance33, "5");
    sProduit.createFeatureAttributeValue(importance33, "6");
    sProduit.createFeatureAttributeValue(importance33, "7");
    sProduit.createFeatureAttributeValue(importance33, "8");
    // Attribut Nature
    sProduit.createFeatureAttribute(toponymeCommunication, "Nature", "string",
        true);
    AttributeType nature33 = toponymeCommunication
        .getFeatureAttributeByName("Nature");
    nature33
        .setDefinition("Donne plus précisément la nature de l'objet nommé.");
    sProduit.createFeatureAttributeValue(nature33, "Aéroport");
    sProduit.createFeatureAttributeValue(nature33, "Barrage");
    sProduit.createFeatureAttributeValue(nature33, "Carrefour");
    sProduit.createFeatureAttributeValue(nature33, "Chemin");
    sProduit.createFeatureAttributeValue(nature33, "Gare");
    sProduit.createFeatureAttributeValue(nature33, "Infrastructure routière");
    sProduit.createFeatureAttributeValue(nature33, "Pont");
    sProduit.createFeatureAttributeValue(nature33, "Port");
    sProduit.createFeatureAttributeValue(nature33, "Remonte-pente");
    sProduit.createFeatureAttributeValue(nature33, "Tunnel");
    sProduit.createFeatureAttributeValue(nature33, "Voie ferrée");

    // Classe Toponyme divers

    sProduit.createFeatureType("Toponyme divers");
    FeatureType toponymeDivers = (FeatureType) (sProduit
        .getFeatureTypeByName("Toponyme divers"));
    toponymeDivers.setDefinition("Toponyme désignant un détail quelconque.");
    toponymeDivers.setIsAbstract(false);
    // Attribut Nom
    sProduit.createFeatureAttribute(toponymeDivers, "Nom", "string", false);
    AttributeType nom34 = toponymeDivers.getFeatureAttributeByName("Nom");
    nom34.setDefinition("Toponyme associé à l'objet.");
    // Attribut Importance
    sProduit.createFeatureAttribute(toponymeDivers, "Importance", "entier",
        true);
    AttributeType importance34 = toponymeDivers
        .getFeatureAttributeByName("Importance");
    importance34
        .setDefinition("Code permettant d'établir une hiérarchie dans la toponymie. "
        		+ "La valeur 1 correspond aux objets les plus importants (ex. zone militaire) tandis que la valeur 8 caractérise "
        		+ "des détails mineurs (abri, statue,...).");
    sProduit.createFeatureAttributeValue(importance34, "1");
    sProduit.createFeatureAttributeValue(importance34, "2");
    sProduit.createFeatureAttributeValue(importance34, "3");
    sProduit.createFeatureAttributeValue(importance34, "4");
    sProduit.createFeatureAttributeValue(importance34, "5");
    sProduit.createFeatureAttributeValue(importance34, "6");
    sProduit.createFeatureAttributeValue(importance34, "7");
    sProduit.createFeatureAttributeValue(importance34, "8");
    // Attribut Nature
    sProduit.createFeatureAttribute(toponymeDivers, "Nature", "string", true);
    AttributeType nature34 = toponymeDivers.getFeatureAttributeByName("Nature");
    nature34
        .setDefinition("Donne plus précisément la nature de l'objet nommé.");
    sProduit.createFeatureAttributeValue(nature34, "Belvédère");
    sProduit.createFeatureAttributeValue(nature34, "Borne");
    sProduit.createFeatureAttributeValue(nature34, "Cabane");
    sProduit.createFeatureAttributeValue(nature34, "Cimetière");
    sProduit.createFeatureAttributeValue(nature34, "Digue");
    sProduit.createFeatureAttributeValue(nature34, "Elevage");
    sProduit.createFeatureAttributeValue(nature34, "Energie");
    sProduit.createFeatureAttributeValue(nature34, "Installations militaires");
    sProduit.createFeatureAttributeValue(nature34, "Mine");
    sProduit.createFeatureAttributeValue(nature34, "Monument");
    sProduit.createFeatureAttributeValue(nature34, "Monument religieux");
    sProduit.createFeatureAttributeValue(nature34, "Pare-feu");
    sProduit.createFeatureAttributeValue(nature34, "Ruines");
    sProduit.createFeatureAttributeValue(nature34, "Sport");
    sProduit.createFeatureAttributeValue(nature34, "Vestiges");
    sProduit.createFeatureAttributeValue(nature34, "Zone d'activité");
    sProduit.createFeatureAttributeValue(nature34, "Zone d'élevage");
    sProduit.createFeatureAttributeValue(nature34, "Zone de loisirs");
    sProduit.createFeatureAttributeValue(nature34, "Zone militaire");

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
