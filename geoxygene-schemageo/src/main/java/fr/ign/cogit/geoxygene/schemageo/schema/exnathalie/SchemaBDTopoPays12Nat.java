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
     * Creation du catalogue de la base de donn�es *
     ************************************************************************/
    SchemaConceptuelProduit sProduit = new SchemaConceptuelProduit();
    sProduit.setNomSchema("Catalogue BDTOPO");
    sProduit.setBD("BDTOPO Pays V1.2");
    sProduit.setTagBD(1);
    sProduit.setDate("D�cembre 2002");
    sProduit.setVersion("1.2");
    sProduit.setSource("Photogramm�trie");
    sProduit.setSujet("Composante topographique du RGE");

    /***************************************************************************
     * Ajout du th�me routier *
     ************************************************************************/

    // Classe Tron�on de
    // route///////////////////////////////////////////////////

    sProduit.createFeatureType("Tron�on de route");
    FeatureType tronconDeRoute = (FeatureType) (sProduit
        .getFeatureTypeByName("Tron�on de route"));
    tronconDeRoute
        .setDefinition("Portion de voie de communication destin�e aux automobiles, homog�ne pour l'ensemble des attributs et des relations qui la concernent. Repr�sente uniquement la chauss�e, d�limit�e par les bas-c�t�s ou les trottoirs.");
    tronconDeRoute.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(tronconDeRoute, "Nature", "string", true);
    AttributeType nature1 = tronconDeRoute.getFeatureAttributeByName("Nature");
    nature1
        .setDefinition("Hi�rarchiqation du r�seau routier bas�e sur l'importance des tron�ons de route pour le trafic routier.");
    sProduit.createFeatureAttributeValue(nature1, "Autorouti�re");
    sProduit.createFeatureAttributeValue(nature1, "Principale");
    sProduit.createFeatureAttributeValue(nature1, "R�gionale");
    sProduit.createFeatureAttributeValue(nature1, "Locale");
    sProduit.createFeatureAttributeValue(nature1, "Contre-all�e");
    sProduit.createFeatureAttributeValue(nature1, "En construction");

    // Attribut Classement
    sProduit.createFeatureAttribute(tronconDeRoute, "Classement", "string",
        true);
    AttributeType classement1 = tronconDeRoute
        .getFeatureAttributeByName("Classement");
    classement1.setDefinition("Statut d'une route class�e.");
    sProduit.createFeatureAttributeValue(classement1, "Autoroute");
    sProduit.createFeatureAttributeValue(classement1, "Nationale");
    sProduit.createFeatureAttributeValue(classement1, "D�partementale");
    sProduit.createFeatureAttributeValue(classement1, "Autre classement");
    // Attribut D�partement gestionnaire
    sProduit.createFeatureAttribute(tronconDeRoute, "D�partement gestionnaire",
        "string", false);
    AttributeType depGest1 = tronconDeRoute
        .getFeatureAttributeByName("D�partement gestionnaire");
    depGest1.setDefinition("D�partement gestionnaire de l'objet.");
    // Attribut Fictif
    sProduit.createFeatureAttribute(tronconDeRoute, "Fictif", "booleen", false);
    AttributeType fictif1 = tronconDeRoute.getFeatureAttributeByName("Fictif");
    fictif1
        .setDefinition("La valeur 'vraie' indique que la g�om�trie du tron�on de route n'est pas significative. La pr�sence de ce dernier sert alors � doubler une 'surface de route' ou � raccorder une bretelle � l'axe de la chauss�e afin d'assurer la continuit� du r�seauroutier lin�aire.");
    // Attribut Franchissement
    sProduit.createFeatureAttribute(tronconDeRoute, "Franchissement", "string",
        true);
    AttributeType franchissement1 = tronconDeRoute
        .getFeatureAttributeByName("Franchissement");
    franchissement1
        .setDefinition("Indique la pr�sence d'un obstacle physique dans le trac� d'une route et la mani�re dont il est franchissable.");
    sProduit.createFeatureAttributeValue(franchissement1, "Bac Auto");
    sProduit.createFeatureAttributeValue(franchissement1, "Gu� ou radier");
    sProduit.createFeatureAttributeValue(franchissement1, "Pont");
    sProduit.createFeatureAttributeValue(franchissement1, "Tunnel");
    sProduit.createFeatureAttributeValue(franchissement1, "Sans objet");
    // Attribut Largeur de chauss�e
    sProduit.createFeatureAttribute(tronconDeRoute, "Largeur de chauss�e",
        "flottant", false);
    AttributeType largeurChaussee1 = tronconDeRoute
        .getFeatureAttributeByName("Largeur de chauss�e");
    largeurChaussee1
        .setDefinition("Largeur de la chauss�e en m�tres. Pour le moment, la valeur indicative est calcul�e d'apr�s le nombre de voies, et arrondie au demi-m�tre.");
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
        .setDefinition("Nombre total de voies d'une route, d'une rue, ou d'une chauss�e de route � chauss�es s�par�es.");
    // Attribut Num�ro de route
    sProduit.createFeatureAttribute(tronconDeRoute, "Num�ro de route",
        "string", false);
    AttributeType numRoute1 = tronconDeRoute
        .getFeatureAttributeByName("Num�ro de route");
    numRoute1.setDefinition("Num�ro de la route.");

    // Classe Surface de
    // route///////////////////////////////////////////////////

    sProduit.createFeatureType("Surface de route");
    FeatureType surfaceDeRoute = (FeatureType) (sProduit
        .getFeatureTypeByName("Surface de route"));
    surfaceDeRoute
        .setDefinition("Partie de la chauss�e d'une route caract�ris�e par une largeur exceptionnelle (place, carrefour, p�age, parking). Zone � trafic non structur�.");
    surfaceDeRoute.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(surfaceDeRoute, "Nature", "string", true);
    AttributeType nature2 = surfaceDeRoute.getFeatureAttributeByName("Nature");
    nature2
        .setDefinition("Attribut permettant de distinguer divers types de surface de route.");
    sProduit.createFeatureAttributeValue(nature2, "Parking");
    sProduit.createFeatureAttributeValue(nature2, "P�age");
    sProduit.createFeatureAttributeValue(nature2, "Place ou carrefour");

    // Classe Tron�on de
    // chemin///////////////////////////////////////////////////

    sProduit.createFeatureType("Tron�on de chemin");
    FeatureType tronconDeChemin = (FeatureType) (sProduit
        .getFeatureTypeByName("Tron�on de chemin"));
    tronconDeChemin
        .setDefinition("Voie de communication terrestre non ferr�e destin�e aux pit�ons, aux cycles ou aux animaux, ou route sommairement rev�tue (pas de rev�tement de surface ou rev�tement de surface tr�s d�grad�).");
    tronconDeChemin.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(tronconDeChemin, "Nature", "string", true);
    AttributeType nature3 = tronconDeChemin.getFeatureAttributeByName("Nature");
    nature3
        .setDefinition("Attribut permettant de distinguer plusieurs types de voies de communication terrestres.");
    sProduit.createFeatureAttributeValue(nature3, "Chemin empierr�");
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
        .setDefinition("Indique la pr�sence d'un obstacle physique dans le trac� d'une route et la mani�re dont il est franchissable.");
    sProduit.createFeatureAttributeValue(franchissement3, "Bac pi�ton");
    sProduit.createFeatureAttributeValue(franchissement3, "Gu� ou radier");
    sProduit.createFeatureAttributeValue(franchissement3, "Pont");
    sProduit.createFeatureAttributeValue(franchissement3, "Tunnel");
    sProduit.createFeatureAttributeValue(franchissement3, "Sans objet");
    // Attribut Nom
    sProduit.createFeatureAttribute(tronconDeChemin, "Nom", "string", false);
    AttributeType nom3 = tronconDeChemin.getFeatureAttributeByName("Nom");
    nom3.setDefinition("Nom du chemin.");

    /***************************************************************************
     * Ajout du th�me "voies ferr�es et autres moyens de transport terrestre" *
     ************************************************************************/

    // Classe Tron�on de voie
    // ferr�e///////////////////////////////////////////////////

    sProduit.createFeatureType("Tron�on de voie ferr�e");
    FeatureType tronconDeVoieFerree = (FeatureType) (sProduit
        .getFeatureTypeByName("Tron�on de voie ferr�e"));
    tronconDeVoieFerree
        .setDefinition("Portion de voie ferr�e homog�ne pour l'ensemble des attributs qui la concernent. Dans le cas d'une ligne compos�e de deux � quatre voies parall�les, l'ensemble des voies est mod�lis� par un seul objet.");
    tronconDeVoieFerree.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(tronconDeVoieFerree, "Nature", "string",
        true);
    AttributeType nature4 = tronconDeVoieFerree
        .getFeatureAttributeByName("Nature");
    nature4
        .setDefinition("Attribut permettant de distinguer plusieurs types de voies ferr�es selon leur fonction et leur �tat.");
    sProduit.createFeatureAttributeValue(nature4, "Voie TGV");
    sProduit.createFeatureAttributeValue(nature4, "Voie ferr�e principale");
    sProduit.createFeatureAttributeValue(nature4, "Voie de service");
    sProduit.createFeatureAttributeValue(nature4, "Voie ferr�e non exploit�e");
    sProduit.createFeatureAttributeValue(nature4, "Transport urbain");
    sProduit.createFeatureAttributeValue(nature4, "Funiculaire ou cr�maill�re");
    sProduit
        .createFeatureAttributeValue(nature4, "Voie ferr�e en construction");
    // Attribut Electifi�
    sProduit.createFeatureAttribute(tronconDeVoieFerree, "Electrifi�",
        "booleen", false);
    AttributeType electrifie4 = tronconDeVoieFerree
        .getFeatureAttributeByName("Electrifi�");
    electrifie4
        .setDefinition("Energie servant � la propulsion des locomotives.");
    // Attribut Franchissement
    sProduit.createFeatureAttribute(tronconDeVoieFerree, "Franchissement",
        "string", true);
    AttributeType franchissement4 = tronconDeVoieFerree
        .getFeatureAttributeByName("Franchissement");
    franchissement4
        .setDefinition("Indique si le tron�on est situ� sur un pont, dans un tunnel ou aucun des deux.");
    sProduit.createFeatureAttributeValue(franchissement4, "Pont");
    sProduit.createFeatureAttributeValue(franchissement4, "Tunnel");
    sProduit.createFeatureAttributeValue(franchissement4, "Sans objet");
    // Attribut Largeur de voie ferr�e
    sProduit.createFeatureAttribute(tronconDeVoieFerree,
        "Largeur de voie ferr�e", "string", true);
    AttributeType largeurVoieFerree4 = tronconDeVoieFerree
        .getFeatureAttributeByName("Largeur de voie ferr�e");
    largeurVoieFerree4
        .setDefinition("Permet de distinguer les voies ferr�es de largeur standard pour la France (1.44 m) des voies ferr�es plus larges ou plus �troites.");
    sProduit.createFeatureAttributeValue(largeurVoieFerree4, "Etroite");
    sProduit.createFeatureAttributeValue(largeurVoieFerree4, "Normalis�e");
    sProduit.createFeatureAttributeValue(largeurVoieFerree4, "Large");
    // Attribut Nombre de voies ferr�es
    sProduit.createFeatureAttribute(tronconDeVoieFerree,
        "Nombre de voies ferr�es", "entier", false);
    AttributeType nbVoiesFerrees4 = tronconDeVoieFerree
        .getFeatureAttributeByName("Nombre de voies ferr�es");
    nbVoiesFerrees4
        .setDefinition("Indique si une ligne de chemin de fer est constitu�e d'une voie ferr�e ou de plusieurs.");

    // Classe Aire de
    // triage/////////////////////////////////////////////////////////

    sProduit.createFeatureType("Aire de triage");
    FeatureType aireDeTriage = (FeatureType) (sProduit
        .getFeatureTypeByName("Aire de triage"));
    aireDeTriage
        .setDefinition("Ensemble des tron�ons de voies, voies de garage, aiguillages permettant le tri des wagons et la composition des trains.");
    aireDeTriage.setIsAbstract(false);

    // Classe Transport par
    // c�ble///////////////////////////////////////////////////

    sProduit.createFeatureType("Transport par c�ble");
    FeatureType transportParCable = (FeatureType) (sProduit
        .getFeatureTypeByName("Transport par c�ble"));
    transportParCable
        .setDefinition("Moyen de transport constitu� d'un ou plusieurs c�bles porteurs.");
    transportParCable.setIsAbstract(false);
    // Attribut Nature
    sProduit
        .createFeatureAttribute(transportParCable, "Nature", "string", true);
    AttributeType nature6 = transportParCable
        .getFeatureAttributeByName("Nature");
    nature6
        .setDefinition("Attribut permettant de distinguer diff�rentes natures de transport par c�ble.");
    sProduit.createFeatureAttributeValue(nature6, "C�ble transporteur");
    sProduit.createFeatureAttributeValue(nature6,
        "T�l�cabine, t�l�ph�rique, t�l�ski");

    /***************************************************************************
     * Ajout du th�me "transport d'�nergie et de fluides" *
     ************************************************************************/

    // Classe Ligne
    // �lectrique///////////////////////////////////////////////////

    sProduit.createFeatureType("Ligne �lectrique");
    FeatureType ligneElectrique = (FeatureType) (sProduit
        .getFeatureTypeByName("Ligne �lectrique"));
    ligneElectrique
        .setDefinition("Portion de ligne �lectrique homog�ne pour l'ensemble des attributs qui la concernent.");
    ligneElectrique.setIsAbstract(false);
    // Attribut Voltage
    sProduit.createFeatureAttribute(ligneElectrique, "Voltage", "string", true);
    AttributeType voltage7 = ligneElectrique
        .getFeatureAttributeByName("Voltage");
    voltage7.setDefinition("Tension de construction de la ligne �lectrique.");
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
        .setDefinition("Enceinte � l'int�rieur de laquelle le courant transport� par une ligne �lectrique est transform�.");
    posteTransformation.setIsAbstract(false);

    // Classe Tron�on de voie
    // ferr�e///////////////////////////////////////////////////

    sProduit.createFeatureType("Canalisation");
    FeatureType canalisation = (FeatureType) (sProduit
        .getFeatureTypeByName("Canalisation"));
    canalisation.setDefinition("Canalisation ou tapis roulant");
    canalisation.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(canalisation, "Nature", "string", true);
    AttributeType nature9 = canalisation.getFeatureAttributeByName("Nature");
    nature9
        .setDefinition("Attribut permettant de diff�rencier les canalisations d'eau des autres.");
    sProduit.createFeatureAttributeValue(nature9, "Eau");
    sProduit.createFeatureAttributeValue(nature9, "Autre");

    // Classe Pyl�ne ///////////////////////////////////////////////////

    sProduit.createFeatureType("Pyl�ne");
    FeatureType pylone = (FeatureType) (sProduit.getFeatureTypeByName("Pyl�ne"));
    pylone.setDefinition("Support de ligne �lectrique.");
    pylone.setIsAbstract(false);

    /***************************************************************************
     * Ajout du th�me "hydrographie terrestre" *
     ************************************************************************/

    // Classe Tron�on de cours
    // d'eau///////////////////////////////////////////////////

    sProduit.createFeatureType("Tron�on de cours d'eau");
    FeatureType tronconCoursDeau = (FeatureType) (sProduit
        .getFeatureTypeByName("Tron�on de cours d'eau"));
    tronconCoursDeau
        .setDefinition("Portion de cours d'eau, r�el ou fictif, permanent ou temporaire, naturel ou artificiel, homog�ne pour l'ensemble des attributs qui la concernent, et qui n'inclut pas de confluent.");
    tronconCoursDeau.setIsAbstract(false);
    // Attribut Artificialis�
    sProduit.createFeatureAttribute(tronconCoursDeau, "Artificialis�",
        "booleen", false);
    AttributeType artificialise11 = tronconCoursDeau
        .getFeatureAttributeByName("Artificialis�");
    artificialise11
        .setDefinition("Permet de distinguer les cours d'eau naturls des cours d'eau artificiels ou artificialis�s.");
    // Attribut Fictif
    sProduit.createFeatureAttribute(tronconCoursDeau, "Fictif", "booleen",
        false);
    AttributeType fictif11 = tronconCoursDeau
        .getFeatureAttributeByName("Fictif");
    fictif11
        .setDefinition("La valeur 'vrai' permet de qualifier un objet dont la g�om�trie n'est pas significative, et dont le r�le est d'assurer la continuit� d'un r�seau lin�aire.");
    // Attribut Franchissement
    sProduit.createFeatureAttribute(tronconCoursDeau, "Franchissement",
        "string", true);
    AttributeType franchissement11 = tronconCoursDeau
        .getFeatureAttributeByName("Franchissement");
    franchissement11
        .setDefinition("Permet de distinguer les tron�ons de cours d'eau libres des obstacles.");
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
    // Attribut R�gime des eaux
    sProduit.createFeatureAttribute(tronconCoursDeau, "R�gime des eaux",
        "string", true);
    AttributeType regime11 = tronconCoursDeau
        .getFeatureAttributeByName("R�gime des eaux");
    regime11
        .setDefinition("Permet de caract�riser un objet hydrographique en fonction du r�gime de ces eaux.");
    sProduit.createFeatureAttributeValue(regime11, "Permanent");
    sProduit.createFeatureAttributeValue(regime11, "Intermittent");

    // Classe Surface d'eau///////////////////////////////////////////////////

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
    // Attribut R�gime des eaux
    sProduit.createFeatureAttribute(surfaceDeau, "R�gime des eaux", "string",
        true);
    AttributeType regime12 = surfaceDeau
        .getFeatureAttributeByName("R�gime des eaux");
    regime12
        .setDefinition("Permet de caract�riser un objet hydrographique en fonction du r�gime de ces eaux.");
    sProduit.createFeatureAttributeValue(regime12, "Permanent");
    sProduit.createFeatureAttributeValue(regime12, "Intermittent");

    // Classe Point d'eau///////////////////////////////////////////////////

    sProduit.createFeatureType("Point d'eau");
    FeatureType pointDeau = (FeatureType) (sProduit
        .getFeatureTypeByName("Point d'eau"));
    pointDeau
        .setDefinition("Source (capt�e ou non), point de production d'eau (pompage, forage, puits,...) ou point de stockage d'eau de petite dimension (citerne, abreuvoir, lavoir, bassin).");
    pointDeau.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(pointDeau, "Nature", "string", true);
    AttributeType nature13 = pointDeau.getFeatureAttributeByName("Nature");
    nature13.setDefinition("Donne la nature du point d'eau.");
    sProduit.createFeatureAttributeValue(nature13, "Citerne");
    sProduit.createFeatureAttributeValue(nature13, "Fontaine");
    sProduit.createFeatureAttributeValue(nature13, "Source");
    sProduit.createFeatureAttributeValue(nature13, "Source capt�e");
    sProduit.createFeatureAttributeValue(nature13, "Station de pompage");
    sProduit.createFeatureAttributeValue(nature13, "Autre point d'eau");

    // Classe Tron�on de
    // laisse///////////////////////////////////////////////////

    sProduit.createFeatureType("Tron�on de laisse");
    FeatureType tronconDeLaisse = (FeatureType) (sProduit
        .getFeatureTypeByName("Tron�on de laisse"));
    tronconDeLaisse
        .setDefinition("Limite inf�rieure ou sup�rieure de l'estran.");
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
     * Ajout du th�me "surfaces d'activit�s et b�ti" *
     ************************************************************************/

    // Classe B�timent///////////////////////////////////////////////////

    sProduit.createFeatureType("B�timent");
    FeatureType batiment = (FeatureType) (sProduit
        .getFeatureTypeByName("B�timent"));
    batiment.setDefinition("B�timent de plus de 20 m2.");
    batiment.setIsAbstract(false);
    // Attribut Cat�gorie
    sProduit.createFeatureAttribute(batiment, "Cat�gorie", "string", true);
    AttributeType categorie15 = batiment.getFeatureAttributeByName("Cat�gorie");
    categorie15
        .setDefinition("Permet de distinguer plusieurs grandes cat�gories de b�timent, selon leur fonction principale et leur aspect.");
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
        .setDefinition("Permet de distinguer diff�rents types de b�timent.");
    sProduit.createFeatureAttributeValue(nature15, "A�rogare");
    sProduit.createFeatureAttributeValue(nature15, "Arc de triomphe");
    sProduit.createFeatureAttributeValue(nature15, "Ar�ne ou th��tre antique");
    sProduit.createFeatureAttributeValue(nature15, "B�timent agricole");
    sProduit.createFeatureAttributeValue(nature15, "B�timent commercial");
    sProduit.createFeatureAttributeValue(nature15, "B�timent industriel");
    sProduit.createFeatureAttributeValue(nature15, "B�timent religieux divers");
    sProduit.createFeatureAttributeValue(nature15, "B�timent sportif");
    sProduit.createFeatureAttributeValue(nature15, "Chapelle");
    sProduit.createFeatureAttributeValue(nature15, "Ch�teau");
    sProduit.createFeatureAttributeValue(nature15, "Eglise");
    sProduit.createFeatureAttributeValue(nature15, "Fort, blockhaus, casemate");
    sProduit.createFeatureAttributeValue(nature15, "Gare");
    sProduit.createFeatureAttributeValue(nature15, "Mairie");
    sProduit.createFeatureAttributeValue(nature15, "Monument");
    sProduit.createFeatureAttributeValue(nature15, "P�age");
    sProduit.createFeatureAttributeValue(nature15, "Pr�fecture");
    sProduit.createFeatureAttributeValue(nature15, "Serre");
    sProduit.createFeatureAttributeValue(nature15, "Silo");
    sProduit.createFeatureAttributeValue(nature15, "Sous-pr�fecture");
    sProduit.createFeatureAttributeValue(nature15, "Tour, donjon, moulin");
    sProduit.createFeatureAttributeValue(nature15, "Tribune");
    sProduit.createFeatureAttributeValue(nature15, "Autre");
    // Attribut Hauteur
    sProduit.createFeatureAttribute(batiment, "Hauteur", "entier", false);
    AttributeType hauteur15 = batiment.getFeatureAttributeByName("Hauteur");
    hauteur15
        .setDefinition("Hauteur du b�timent correspondant � la diff�rence netre le Z le plus �lev� du pourtour du b�timent et un point situ� au pied du b�timent. La hauteur est arrondie au m�tre.");

    // Classe Construction
    // ponctuelle///////////////////////////////////////////////////

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
        .setDefinition("Permet de distinguer diff�rentes natures de construction.");
    sProduit.createFeatureAttributeValue(nature16, "Antenne");
    sProduit.createFeatureAttributeValue(nature16, "Chemin�e");
    sProduit.createFeatureAttributeValue(nature16, "Phare");
    sProduit.createFeatureAttributeValue(nature16, "Torch�re");
    sProduit.createFeatureAttributeValue(nature16, "Transformateur");
    sProduit.createFeatureAttributeValue(nature16,
        "Construction ponctuelle quelconque");

    // Classe Construction
    // lin�aire///////////////////////////////////////////////////

    sProduit.createFeatureType("Construction lin�aire");
    FeatureType constructionLineaire = (FeatureType) (sProduit
        .getFeatureTypeByName("Construction lin�aire"));
    constructionLineaire
        .setDefinition("Construction dont la forme g�n�rale est lin�aire.");
    constructionLineaire.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(constructionLineaire, "Nature", "string",
        true);
    AttributeType nature17 = constructionLineaire
        .getFeatureAttributeByName("Nature");
    nature17.setDefinition("Permet de distinguer diff�rents types d'ouvrages.");
    sProduit.createFeatureAttributeValue(nature17, "Barrage");
    sProduit.createFeatureAttributeValue(nature17, "Mur anti-bruit");
    sProduit.createFeatureAttributeValue(nature17, "Pont");
    sProduit.createFeatureAttributeValue(nature17, "Quai");
    sProduit.createFeatureAttributeValue(nature17, "Ruines");
    sProduit.createFeatureAttributeValue(nature17,
        "Construction lin�aire quelconque");

    // Classe Construction
    // surfacique///////////////////////////////////////////////////

    sProduit.createFeatureType("Construction surfacique");
    FeatureType constructionSurfacique = (FeatureType) (sProduit
        .getFeatureTypeByName("Construction surfacique"));
    constructionSurfacique
        .setDefinition("Ouvrage de grande surface li� au franchissement d'un obstacle par une voie de communication, ou � l'am�nagement d'une rivi�re ou d'un canal.");
    constructionSurfacique.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(constructionSurfacique, "Nature", "string",
        true);
    AttributeType nature18 = constructionSurfacique
        .getFeatureAttributeByName("Nature");
    nature18
        .setDefinition("Permet de distinguer diff�rents types d'ouvrages d'art larges.");
    sProduit.createFeatureAttributeValue(nature18, "Barrage");
    sProduit.createFeatureAttributeValue(nature18, "Dalle de protection");
    sProduit.createFeatureAttributeValue(nature18, "Ecluse");
    sProduit.createFeatureAttributeValue(nature18, "Pont");
    sProduit.createFeatureAttributeValue(nature18, "Escalier");

    // Classe R�servoir///////////////////////////////////////////////////

    sProduit.createFeatureType("R�servoir");
    FeatureType reservoir = (FeatureType) (sProduit
        .getFeatureTypeByName("R�servoir"));
    reservoir.setDefinition("R�servoir (eau, mati�res industrielles...).");
    reservoir.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(reservoir, "Nature", "string", true);
    AttributeType nature19 = reservoir.getFeatureAttributeByName("Nature");
    nature19
        .setDefinition("Permet de distinguer diff�rents types de r�servoirs.");
    sProduit.createFeatureAttributeValue(nature19, "Ch�teau d'eau");
    sProduit.createFeatureAttributeValue(nature19, "R�servoir d'eau");
    sProduit.createFeatureAttributeValue(nature19, "R�servoir industriel");
    sProduit.createFeatureAttributeValue(nature19, "Inconnue");
    // Attribut Hauteur
    sProduit.createFeatureAttribute(reservoir, "Hauteur", "entier", false);
    AttributeType hauteur19 = reservoir.getFeatureAttributeByName("Hauteur");
    hauteur19
        .setDefinition("Hauteur du r�servoir correspondant � la diff�rence netre le Z le plus �lev� du pourtour et un point situ� au pied du r�servoir. La hauteur est arrondie au m�tre.");

    // Classe Terrain de
    // sport///////////////////////////////////////////////////

    sProduit.createFeatureType("Terrain de sport");
    FeatureType terrainDeSport = (FeatureType) (sProduit
        .getFeatureTypeByName("Terrain de sport"));
    terrainDeSport.setDefinition("Equipement sportif de plein air.");
    terrainDeSport.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(terrainDeSport, "Nature", "string", true);
    AttributeType nature20 = terrainDeSport.getFeatureAttributeByName("Nature");
    nature20
        .setDefinition("Permet de distinguer diff�rents types d'�quipements sportifs.");
    sProduit.createFeatureAttributeValue(nature20, "Piste de sport");
    sProduit.createFeatureAttributeValue(nature20, "Terrain de tennis");
    sProduit.createFeatureAttributeValue(nature20, "Bassin de natation");
    sProduit.createFeatureAttributeValue(nature20,
        "Terrain de sport indiff�renci�");

    // Classe Cimeti�re ///////////////////////////////////////////////////

    sProduit.createFeatureType("Cimeti�re");
    FeatureType cimetiere = (FeatureType) (sProduit
        .getFeatureTypeByName("Cimeti�re"));
    cimetiere.setDefinition("Endroit o� reposent les morts.");
    cimetiere.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(cimetiere, "Nature", "string", true);
    AttributeType nature21 = cimetiere.getFeatureAttributeByName("Nature");
    nature21
        .setDefinition("Permet de distinguer un objet g�ographique � vocation militaire ou simplement g�r� par le Minist�re de la D�fense, d'un objet civil. La valeur 'militaire' est �galement affect�e aux cimeti�res militaires g�r�s par le Minist�re des Anciens Combattants ou par des Etats �trangers.");
    sProduit.createFeatureAttributeValue(nature21, "Militaire");
    sProduit.createFeatureAttributeValue(nature21, "Autre");

    // Classe Piste d'a�rodrome
    // ///////////////////////////////////////////////////

    sProduit.createFeatureType("Piste d'a�rodrome");
    FeatureType pisteAerodrome = (FeatureType) (sProduit
        .getFeatureTypeByName("Piste d'a�rodrome"));
    pisteAerodrome
        .setDefinition("Aire situ�e sur un a�rodrome, am�nag�e afin de servir au roulement des a�ronefs, au d�collage et � l'atterrissage.");
    pisteAerodrome.setIsAbstract(false);
    // Attribut Nature
    sProduit.createFeatureAttribute(pisteAerodrome, "Nature", "string", true);
    AttributeType nature22 = pisteAerodrome.getFeatureAttributeByName("Nature");
    nature22
        .setDefinition("Permet de distinguer diff�rentes natures d'a�rodrome.");
    sProduit.createFeatureAttributeValue(nature22, "Piste en dur");
    sProduit.createFeatureAttributeValue(nature22, "Piste en herbe");

    // Classe Point d'activit� ou d'int�r�t
    // ///////////////////////////////////////////////////

    sProduit.createFeatureType("Point d'activit� ou d'int�r�t");
    FeatureType pointActivite = (FeatureType) (sProduit
        .getFeatureTypeByName("Point d'activit� ou d'int�r�t"));
    pointActivite
        .setDefinition("Objet ponctuel localisant un �quipement public, un site ou une zone ayant un caract�re administratif, culturel, sportif, industriel ou commercial.");
    pointActivite.setIsAbstract(false);
    // Attribut Cat�gorie
    sProduit.createFeatureAttribute(pointActivite, "Cat�gorie", "string", true);
    AttributeType categorie23 = pointActivite
        .getFeatureAttributeByName("Cat�gorie");
    categorie23.setDefinition("Permet de distinguer le type de l'activit�.");
    sProduit.createFeatureAttributeValue(categorie23, "Administratif");
    sProduit.createFeatureAttributeValue(categorie23, "Culture et loisirs");
    sProduit.createFeatureAttributeValue(categorie23, "Enseignement");
    sProduit.createFeatureAttributeValue(categorie23, "Gestion des eaux");
    sProduit.createFeatureAttributeValue(categorie23,
        "Industriel ou commercial");
    sProduit.createFeatureAttributeValue(categorie23, "Religieux");
    sProduit.createFeatureAttributeValue(categorie23, "Sant�");
    sProduit.createFeatureAttributeValue(categorie23, "Sport");
    sProduit.createFeatureAttributeValue(categorie23, "Transport");
    // Attribut Nature
    sProduit.createFeatureAttribute(pointActivite, "Nature", "string", true);
    AttributeType nature23 = pointActivite.getFeatureAttributeByName("Nature");
    nature23
        .setDefinition("Pr�cise la fonction du b�timent ou le type de l'activit�.");
    sProduit
        .createFeatureAttributeValue(nature23, "Bureau ou h�tel des postes");
    sProduit.createFeatureAttributeValue(nature23, "Caserne de pompiers");
    sProduit.createFeatureAttributeValue(nature23,
        "Divers public ou administratif");
    sProduit.createFeatureAttributeValue(nature23, "Enceinte militaire");
    sProduit.createFeatureAttributeValue(nature23,
        "Etablissement p�nitentiaire");
    sProduit.createFeatureAttributeValue(nature23, "Gendarmerie");
    sProduit.createFeatureAttributeValue(nature23, "H�tel de d�partement");
    sProduit.createFeatureAttributeValue(nature23, "H�tel de r�gion");
    sProduit.createFeatureAttributeValue(nature23, "Mairie");
    sProduit.createFeatureAttributeValue(nature23, "Maison foresti�re");
    sProduit.createFeatureAttributeValue(nature23, "Palais de justice");
    sProduit.createFeatureAttributeValue(nature23, "Poste ou h�tel de police");
    sProduit.createFeatureAttributeValue(nature23, "Pr�fecture");
    sProduit.createFeatureAttributeValue(nature23, "Sous-pr�fecture");
    sProduit.createFeatureAttributeValue(nature23, "Camping");
    sProduit.createFeatureAttributeValue(nature23, "Dolmen");
    sProduit.createFeatureAttributeValue(nature23, "Habitation troglodytique");
    sProduit.createFeatureAttributeValue(nature23, "Maison du parc");
    sProduit.createFeatureAttributeValue(nature23, "Menhir");
    sProduit.createFeatureAttributeValue(nature23, "Mus�e");
    sProduit.createFeatureAttributeValue(nature23, "Parc de loisirs");
    sProduit.createFeatureAttributeValue(nature23, "Parc zoologique");
    sProduit.createFeatureAttributeValue(nature23, "Point de vue");
    sProduit.createFeatureAttributeValue(nature23, "Village de vacances");
    sProduit.createFeatureAttributeValue(nature23, "Enseignement primaire");
    sProduit.createFeatureAttributeValue(nature23, "Enseignement secondaire");
    sProduit.createFeatureAttributeValue(nature23, "Enseignement sup�rieur");
    sProduit.createFeatureAttributeValue(nature23, "Station de pompage");
    sProduit.createFeatureAttributeValue(nature23,
        "Usine de traitement des eaux");
    sProduit.createFeatureAttributeValue(nature23, "Aquaculture");
    sProduit.createFeatureAttributeValue(nature23, "Carri�re");
    sProduit.createFeatureAttributeValue(nature23, "Centrale �lectrique");
    sProduit.createFeatureAttributeValue(nature23, "Divers commercial");
    sProduit.createFeatureAttributeValue(nature23, "Divers industriel");
    sProduit.createFeatureAttributeValue(nature23, "Haras national");
    sProduit.createFeatureAttributeValue(nature23, "Marais salants");
    sProduit.createFeatureAttributeValue(nature23, "March�");
    sProduit.createFeatureAttributeValue(nature23, "Mine");
    sProduit.createFeatureAttributeValue(nature23, "Usine");
    sProduit.createFeatureAttributeValue(nature23,
        "Culte catholique ou orthodoxe");
    sProduit.createFeatureAttributeValue(nature23, "Culte protestant");
    sProduit.createFeatureAttributeValue(nature23, "Culte isra�lite");
    sProduit.createFeatureAttributeValue(nature23, "Culte islamique");
    sProduit.createFeatureAttributeValue(nature23, "Culte divers");
    sProduit.createFeatureAttributeValue(nature23, "Etablissement hospitalier");
    sProduit.createFeatureAttributeValue(nature23, "Etablissement thermal");
    sProduit.createFeatureAttributeValue(nature23, "Golf");
    sProduit.createFeatureAttributeValue(nature23, "Hippodrome");
    sProduit.createFeatureAttributeValue(nature23, "Piscine");
    sProduit.createFeatureAttributeValue(nature23, "Stade");
    sProduit.createFeatureAttributeValue(nature23, "A�rodrome");
    sProduit.createFeatureAttributeValue(nature23, "Aire d'autoroute");
    sProduit.createFeatureAttributeValue(nature23, "Gare voyageurs uniquement");
    sProduit.createFeatureAttributeValue(nature23, "Gare voyageurs et fret");
    sProduit.createFeatureAttributeValue(nature23, "Gare fret uniquement");
    sProduit.createFeatureAttributeValue(nature23, "P�age");

    // Classe Surface d'activit�
    // ///////////////////////////////////////////////////

    sProduit.createFeatureType("Surface d'activit�");
    FeatureType surfaceActivite = (FeatureType) (sProduit
        .getFeatureTypeByName("Surface d'activit�"));
    surfaceActivite
        .setDefinition("Enceinte d'un �quipement public, d'un site ou d'une zone ayant un caract�re administratif, culturel, sportif, industriel ou commercial.");
    surfaceActivite.setIsAbstract(false);
    // Attribut Cat�gorie
    sProduit.createFeatureAttribute(surfaceActivite, "Cat�gorie", "string",
        true);
    AttributeType categorie24 = surfaceActivite
        .getFeatureAttributeByName("Cat�gorie");
    categorie24.setDefinition("Permet de distinguer le type de l'activit�.");
    sProduit.createFeatureAttributeValue(categorie24, "Administratif");
    sProduit.createFeatureAttributeValue(categorie24, "Culture et loisirs");
    sProduit.createFeatureAttributeValue(categorie24, "Enseignement");
    sProduit.createFeatureAttributeValue(categorie24, "Gestion des eaux");
    sProduit.createFeatureAttributeValue(categorie24,
        "Industriel ou commercial");
    sProduit.createFeatureAttributeValue(categorie24, "Sant�");
    sProduit.createFeatureAttributeValue(categorie24, "Sport");
    sProduit.createFeatureAttributeValue(categorie24, "Transport");

    /***************************************************************************
     * Ajout du th�me "occupation du sol: v�g�tation" *
     ************************************************************************/

    // Classe Zone arbor�e///////////////////////////////////////////////////

    sProduit.createFeatureType("Zone arbor�e");
    FeatureType zoneArboree = (FeatureType) (sProduit
        .getFeatureTypeByName("Zone arbor�e"));
    zoneArboree.setDefinition("Espace peupl� d'arbres d'essence quelconque.");
    zoneArboree.setIsAbstract(false);

    /***************************************************************************
     * Ajout du th�me "orographie" *
     ************************************************************************/

    // Classe Ligne
    // orographique///////////////////////////////////////////////////

    sProduit.createFeatureType("Ligne orographique");
    FeatureType ligneOrographique = (FeatureType) (sProduit
        .getFeatureTypeByName("Ligne orographique"));
    ligneOrographique.setDefinition("Ligne de rupture de pente artificielle.");
    ligneOrographique.setIsAbstract(false);
    // Attribut Cat�gorie
    sProduit
        .createFeatureAttribute(ligneOrographique, "Nature", "string", true);
    AttributeType nature26 = ligneOrographique
        .getFeatureAttributeByName("Nature");
    nature26
        .setDefinition("Permet de distinguer diff�rentes natures de lignes orographiques.");
    sProduit.createFeatureAttributeValue(nature26, "Lev�e");
    sProduit.createFeatureAttributeValue(nature26, "Mur de sout�nement");
    sProduit.createFeatureAttributeValue(nature26, "Talus");
    sProduit.createFeatureAttributeValue(nature26, "Carri�re");

    /***************************************************************************
     * Ajout du th�me "zonages techiniques et administratifs" *
     ************************************************************************/

    // Classe Commune///////////////////////////////////////////////////

    sProduit.createFeatureType("Commune");
    FeatureType commune = (FeatureType) (sProduit
        .getFeatureTypeByName("Commune"));
    commune
        .setDefinition("Plus petite subdivision du territoire, administr�e par un maire, des adjoints et un conseil municipal.");
    commune.setIsAbstract(false);
    // Attribut Nom
    sProduit.createFeatureAttribute(commune, "Nom", "string", false);
    AttributeType nom27 = commune.getFeatureAttributeByName("Nom");
    nom27.setDefinition("Nom officiel de la commune.");
    // Attribut Code INSEE
    sProduit.createFeatureAttribute(commune, "Code INSEE", "string", false);
    AttributeType numINSEE27 = commune.getFeatureAttributeByName("Code INSEE");
    numINSEE27.setDefinition("Num�ro INSEE de la commune.");
    // Attribut Statut
    sProduit.createFeatureAttribute(commune, "Statut", "string", true);
    AttributeType statut27 = commune.getFeatureAttributeByName("Statut");
    statut27
        .setDefinition("Pr�cise le r�le le plus �lev� qu'a la commune dans la hi�rarchie des entit�s administratives.");
    sProduit.createFeatureAttributeValue(statut27, "Capitale d'Etat");
    sProduit.createFeatureAttributeValue(statut27, "Pr�fecture de r�gion");
    sProduit.createFeatureAttributeValue(statut27, "Pr�fecture");
    sProduit.createFeatureAttributeValue(statut27, "Sous-pr�fecture");
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
    // Attribut D�partement
    sProduit.createFeatureAttribute(commune, "D�partement", "string", false);
    AttributeType departement27 = commune
        .getFeatureAttributeByName("D�partement");
    departement27
        .setDefinition("Nom du d�partement auquel appartient la commune.");
    // Attribut R�gion
    sProduit.createFeatureAttribute(commune, "R�gion", "string", false);
    AttributeType region27 = commune.getFeatureAttributeByName("R�gion");
    region27
        .setDefinition("Nom de la r�gion � laquelle appartient la commune.");
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
        .setDefinition("Permet de diff�rencier une commune multi-canton d'une commune n'appartenant qu'� un seul canton.");

    // Classe Arrondissement
    // municipal///////////////////////////////////////////////////

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
    numINSEE28.setDefinition("Num�ro INSEE de l'arrondissement municipal.");

    /***************************************************************************
     * Ajout du th�me "objets divers" *
     ************************************************************************/

    // Classe Lieu-dit habit�///////////////////////////////////////////////////

    sProduit.createFeatureType("Lieu-dit habit�");
    FeatureType lieuDitHabite = (FeatureType) (sProduit
        .getFeatureTypeByName("Lieu-dit habit�"));
    lieuDitHabite.setDefinition("Lieu-dit habit� caract�ris� par un nom.");
    lieuDitHabite.setIsAbstract(false);
    // Attribut Nom
    sProduit.createFeatureAttribute(lieuDitHabite, "Nom", "string", false);
    AttributeType nom29 = lieuDitHabite.getFeatureAttributeByName("Nom");
    nom29.setDefinition("Toponyme associ� au lieu-dit.");
    // Attribut Importance
    sProduit
        .createFeatureAttribute(lieuDitHabite, "Importance", "entier", true);
    AttributeType importance29 = lieuDitHabite
        .getFeatureAttributeByName("Importance");
    importance29
        .setDefinition("Code permettant d'�tablir une hi�rarchie dans la toponymie.");
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
    nature29.setDefinition("Donne plus pr�cis�ment la nature du lieu nomm�.");
    sProduit.createFeatureAttributeValue(nature29, "Canton");
    sProduit.createFeatureAttributeValue(nature29, "Ch�teau");
    sProduit.createFeatureAttributeValue(nature29, "Commune");
    sProduit.createFeatureAttributeValue(nature29, "Construction");
    sProduit.createFeatureAttributeValue(nature29, "Culte");
    sProduit.createFeatureAttributeValue(nature29, "Culture");
    sProduit.createFeatureAttributeValue(nature29, "Ecart");
    sProduit.createFeatureAttributeValue(nature29, "Enseignement");
    sProduit.createFeatureAttributeValue(nature29, "Pr�fecture");
    sProduit.createFeatureAttributeValue(nature29, "Quartier");
    sProduit.createFeatureAttributeValue(nature29, "Refuge");
    sProduit.createFeatureAttributeValue(nature29, "Sant�");
    sProduit.createFeatureAttributeValue(nature29, "Science");
    sProduit.createFeatureAttributeValue(nature29, "Sous-pr�fecture");

    // Classe Lieu-dit non
    // habit�///////////////////////////////////////////////////

    sProduit.createFeatureType("Lieu-dit non habit�");
    FeatureType lieuDitNonHabite = (FeatureType) (sProduit
        .getFeatureTypeByName("Lieu-dit non habit�"));
    lieuDitNonHabite
        .setDefinition("Lieu-dit non habit� et dont le nom ne se rapporte ni � un d�tail orographqiue, ni � un d�tail hydrographique.");
    lieuDitNonHabite.setIsAbstract(false);
    // Attribut Nom
    sProduit.createFeatureAttribute(lieuDitNonHabite, "Nom", "string", false);
    AttributeType nom30 = lieuDitNonHabite.getFeatureAttributeByName("Nom");
    nom30.setDefinition("Toponyme associ� au lieu-dit.");
    // Attribut Importance
    sProduit.createFeatureAttribute(lieuDitNonHabite, "Importance", "entier",
        true);
    AttributeType importance30 = lieuDitNonHabite
        .getFeatureAttributeByName("Importance");
    importance30
        .setDefinition("Code permettant d'�tablir une hi�rarchie dans la toponymie.");
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
    nature30.setDefinition("Donne plus pr�cis�ment la nature du lieu nomm�.");
    sProduit.createFeatureAttributeValue(nature30, "Arbre");
    sProduit.createFeatureAttributeValue(nature30, "Bois");
    sProduit.createFeatureAttributeValue(nature30, "Lieu-dit");
    sProduit.createFeatureAttributeValue(nature30, "Parc");

    // Classe Oronyme///////////////////////////////////////////////////

    sProduit.createFeatureType("Oronyme");
    FeatureType oronyme = (FeatureType) (sProduit
        .getFeatureTypeByName("Oronyme"));
    oronyme.setDefinition("D�tail du relief portant un nom.");
    oronyme.setIsAbstract(false);
    // Attribut Nom
    sProduit.createFeatureAttribute(oronyme, "Nom", "string", false);
    AttributeType nom31 = oronyme.getFeatureAttributeByName("Nom");
    nom31.setDefinition("Nom du d�tail ou du relief.");
    // Attribut Importance
    sProduit.createFeatureAttribute(oronyme, "Importance", "entier", true);
    AttributeType importance31 = oronyme
        .getFeatureAttributeByName("Importance");
    importance31
        .setDefinition("Code permettant d'�tablir une hi�rarchie dans la toponymie. La valeur 1 correspond aux d�tails les plus importants (ex.massif de montagne), tandis que la valeur 8 caract�rise des accidents de terrain mineurs (ravine, �bouli, rocher caract�ristique...).");
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
    nature31.setDefinition("Donne plus pr�cis�ment la nature du lieu nomm�.");
    sProduit.createFeatureAttributeValue(nature31, "Cap");
    sProduit.createFeatureAttributeValue(nature31, "Carri�re");
    sProduit.createFeatureAttributeValue(nature31, "Cirque");
    sProduit.createFeatureAttributeValue(nature31, "Col");
    sProduit.createFeatureAttributeValue(nature31, "Cr�te");
    sProduit.createFeatureAttributeValue(nature31, "D�pression");
    sProduit.createFeatureAttributeValue(nature31, "Dune");
    sProduit.createFeatureAttributeValue(nature31, "Escarpement");
    sProduit.createFeatureAttributeValue(nature31, "Gorge");
    sProduit.createFeatureAttributeValue(nature31, "Grotte");
    sProduit.createFeatureAttributeValue(nature31, "�le");
    sProduit.createFeatureAttributeValue(nature31, "Isthme");
    sProduit.createFeatureAttributeValue(nature31, "Montagne");
    sProduit.createFeatureAttributeValue(nature31, "Pic");
    sProduit.createFeatureAttributeValue(nature31, "Plage");
    sProduit.createFeatureAttributeValue(nature31, "Plaine ou plateau");
    sProduit.createFeatureAttributeValue(nature31, "R�cifs");
    sProduit.createFeatureAttributeValue(nature31, "Rochers");
    sProduit.createFeatureAttributeValue(nature31, "Sommet");
    sProduit.createFeatureAttributeValue(nature31, "Vall�e");
    sProduit.createFeatureAttributeValue(nature31, "Versant");
    sProduit.createFeatureAttributeValue(nature31, "Volcan");

    // Classe Hydronyme///////////////////////////////////////////////////

    sProduit.createFeatureType("Hydronyme");
    FeatureType hydronyme = (FeatureType) (sProduit
        .getFeatureTypeByName("Hydronyme"));
    hydronyme.setDefinition("Nom se rapportant � un d�tail hydrographique.");
    hydronyme.setIsAbstract(false);
    // Attribut Nom
    sProduit.createFeatureAttribute(hydronyme, "Nom", "string", false);
    AttributeType nom32 = hydronyme.getFeatureAttributeByName("Nom");
    nom32.setDefinition("Nom du d�tail hydrographique.");
    // Attribut Importance
    sProduit.createFeatureAttribute(hydronyme, "Importance", "entier", true);
    AttributeType importance32 = hydronyme
        .getFeatureAttributeByName("Importance");
    importance32
        .setDefinition("Code permettant d'�tablir une hi�rarchie dans la toponymie. La valeur 1 correspond aux objets les plus importants (ex. golfe, mer, fleuve), tandis que la valeur 8 caract�rise des d�tails mineurs (petit ruisseau, mare...).");
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
        .setDefinition("Donne plus pr�cis�ment la nature de l'objet nomm�.");
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
    sProduit.createFeatureAttributeValue(nature32, "P�cherie");
    sProduit.createFeatureAttributeValue(nature32, "Perte");
    sProduit.createFeatureAttributeValue(nature32, "Point d'eau");

    // Classe Toponyme
    // communication///////////////////////////////////////////////////

    sProduit.createFeatureType("Toponyme communication");
    FeatureType toponymeCommunication = (FeatureType) (sProduit
        .getFeatureTypeByName("Toponyme communication"));
    toponymeCommunication
        .setDefinition("Installation nomm�e servant de noeud dans un r�seau.");
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
        .setDefinition("Code permettant d'�tablir une hi�rarchie dans la toponymie. Les trois premi�res valeurs ne sont pas utilis�es pour ce type de d�tail. Les suivantes vont des objets les plus importants (a�roport intenational) aux moins importants (remonte-prente, passerelle,...).");
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
        .setDefinition("Donne plus pr�cis�ment la nature de l'objet nomm�.");
    sProduit.createFeatureAttributeValue(nature33, "A�roport");
    sProduit.createFeatureAttributeValue(nature33, "Barrage");
    sProduit.createFeatureAttributeValue(nature33, "Carrefour");
    sProduit.createFeatureAttributeValue(nature33, "Chemin");
    sProduit.createFeatureAttributeValue(nature33, "Gare");
    sProduit.createFeatureAttributeValue(nature33, "Infrastructure routi�re");
    sProduit.createFeatureAttributeValue(nature33, "Pont");
    sProduit.createFeatureAttributeValue(nature33, "Port");
    sProduit.createFeatureAttributeValue(nature33, "Remonte-pente");
    sProduit.createFeatureAttributeValue(nature33, "Tunnel");
    sProduit.createFeatureAttributeValue(nature33, "Voie ferr�e");

    // Classe Toponyme divers///////////////////////////////////////////////////

    sProduit.createFeatureType("Toponyme divers");
    FeatureType toponymeDivers = (FeatureType) (sProduit
        .getFeatureTypeByName("Toponyme divers"));
    toponymeDivers.setDefinition("Toponyme d�signant un d�tail quelconque.");
    toponymeDivers.setIsAbstract(false);
    // Attribut Nom
    sProduit.createFeatureAttribute(toponymeDivers, "Nom", "string", false);
    AttributeType nom34 = toponymeDivers.getFeatureAttributeByName("Nom");
    nom34.setDefinition("Toponyme associ� � l'objet.");
    // Attribut Importance
    sProduit.createFeatureAttribute(toponymeDivers, "Importance", "entier",
        true);
    AttributeType importance34 = toponymeDivers
        .getFeatureAttributeByName("Importance");
    importance34
        .setDefinition("Code permettant d'�tablir une hi�rarchie dans la toponymie. La valeur 1 correspond aux objets les plus importants (ex. zone militaire) tandis que la valeur 8 caract�rise des d�tails mineurs (abri, statue,...).");
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
        .setDefinition("Donne plus pr�cis�ment la nature de l'objet nomm�.");
    sProduit.createFeatureAttributeValue(nature34, "Belv�d�re");
    sProduit.createFeatureAttributeValue(nature34, "Borne");
    sProduit.createFeatureAttributeValue(nature34, "Cabane");
    sProduit.createFeatureAttributeValue(nature34, "Cimeti�re");
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
    sProduit.createFeatureAttributeValue(nature34, "Zone d'activit�");
    sProduit.createFeatureAttributeValue(nature34, "Zone d'�levage");
    sProduit.createFeatureAttributeValue(nature34, "Zone de loisirs");
    sProduit.createFeatureAttributeValue(nature34, "Zone militaire");

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
