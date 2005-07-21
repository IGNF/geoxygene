/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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
 *  
 */

package fr.ign.cogit.geoxygene.example;

import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.dico.GF_AssociationRole;
import fr.ign.cogit.geoxygene.dico.GF_AssociationType;
import fr.ign.cogit.geoxygene.dico.GF_AttributeType;
import fr.ign.cogit.geoxygene.dico.GF_Constraint;
import fr.ign.cogit.geoxygene.dico.GF_FeatureType;
import fr.ign.cogit.geoxygene.dico.GF_InheritanceRelation;
import fr.ign.cogit.geoxygene.dico.GF_Operation;
import fr.ign.cogit.geoxygene.dico.GF_PropertyType;


/**
 * Utilisation du dictionnaire de données : exemple de code.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 *
 */


/*
 * On renseigne dans le dico le modele suivant :
 * Une classe abstraite "entite_adm" (pour entite administrative) ayant 3 attributs :
 *  - nom (String)
 *  - geom (GM_Surface)
 *  - topo (TP_Object)
 * et 1 operation : getSurface().
 * Cette classe a 2 classes filles non abstraites : "commune" et "departement".
 * Il y a 2 associations entre commune et departement : "compose" et "prefecture"
 * Chacune des associations a 2 roles (= les extremites du lien);
 * L'association "compose" a une operation : getNumber().
 * L'association "prefecture" a un attribut : "habitant".
 * L'attribut "habitant" a lui-meme un attribut : "age_moyen"
 * L'attribut "geom" a une contrainte : "ct_geom"
 *
 * Le programme suivant :
 *  - renseigne le modele : methode creeNouveauxObjets()
 *  - effectue quelques requetes : methode interrogeDico()
 * 
 */


public class TestDico {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /* Attributs */
    private static Geodatabase db;
    
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /* Constructeur */
    public TestDico() {
		db = GeodatabaseOjbFactory.newInstance();		
    }
 
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /* méthode main */
    public static void main (String args[]) {
        TestDico test = new TestDico();

        test.creeNouveauxObjets();
        test.interrogeDico();
            
    }
        
             
        
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    public void creeNouveauxObjets ()  {
        
        // Debut d'une transaction
        System.out.println("Debut transaction");
        db.begin();
        
        // creation d'un nouveau Feature Type "entite"
        GF_FeatureType entite = new GF_FeatureType();
        db.makePersistent(entite);  // on le rend persistent        
        entite.setTypeName("entite_adm");
        entite.setDefinition("Entite administrative");
        entite.setIsAbstract(true);  // abstrait
        
        // creation d'un nouvel attribut "nom" sur entite
        GF_AttributeType nom = new GF_AttributeType();
        db.makePersistent(nom);  // on le rend persistent        
        nom.setMemberName("nom");
        nom.setDefinition("Nom de l'entite administrative");
        nom.setValueType("String");
        nom.setCardMin(1);
        nom.setCardMax(1);
        entite.addProperty(nom);  // on ajoute nom a la liste des attributs de entite
        // remarque : tel qu'est code le dico,
        // "entite.addProperty(nom)" execute automatiquement "nom.setFeatureType(entite)"
        
        // creation d'un nouvel attribut "geom" sur entite
        GF_AttributeType geom = new GF_AttributeType();
        db.makePersistent(geom);  // on le rend persistent        
        geom.setMemberName("geom");
        geom.setDefinition("Geometrie de l'entite administrative");
        geom.setValueType("GM_Surface");
        geom.setCardMin(1);
        geom.setCardMax(3); // peut avoir jusqu'a 3 geometries (multirepresentation)
        entite.addProperty(geom);
        
        // creation d'un nouvel attribut "topo" sur entite
        GF_AttributeType topo = new GF_AttributeType();
        db.makePersistent(topo);  // on le rend persistent        
        topo.setMemberName("topo");
        topo.setDefinition("Topologie de l'entite administrative");
        topo.setValueType("TP_Object");
        topo.setCardMin(0);     // on n'est pas oblige de fournir une topologie
        topo.setCardMax(1); 
        entite.addProperty(topo);
        
        // creation d'une operation "getSurface" sur entite
        GF_Operation op = new GF_Operation();
        db.makePersistent(op);  // on le rend persistent        
        op.setMemberName("getSurface");
        op.setDefinition("Renvoie la valeur de la surface de l'entite administrative");
        op.setSignature("entite_adm.getSurface(unit : UnitOfMeasure) : int");
        entite.addProperty(op);    // meme remarque que pour un attribut : executer automatiquement "op.setFeatureType(entite)"   
        
        // creation d'un nouveau Feature Type "departement"
        GF_FeatureType dept = new GF_FeatureType();
        db.makePersistent(dept);  // on le rend persistent        
        dept.setTypeName("departement");
        dept.setDefinition("Departement");
        dept.setIsAbstract(false);  // non abstrait - cette ligne est inutile car est false par defaut
        
        // creation d'un nouveau Feature Type "commune"
        GF_FeatureType commune = new GF_FeatureType();
        db.makePersistent(commune);  // on le rend persistent        
        commune.setTypeName("commune");
        commune.setDefinition("Commune");
        
        // creation d'une relation d'heritage entre "entite" et "dept"       
        GF_InheritanceRelation herite = new GF_InheritanceRelation();
        db.makePersistent(herite);        
        herite.setName("entite_adm/department");
        herite.setDescription("Un departement est une entite administrative");
        herite.setUniqueInstance(true); // une entite_adm est SOIT une commune, SOIT un departement, mais jamais les 2 a la fois
        dept.addGeneralization(herite); // execute automatiquement "herite.setSubType(dept) 
        entite.addSpecialization(herite); // execute automatiquement "herite.addSuperType(entite) 
        
        // creation d'une relation d'heritage entre "entite" et "commune"       
        herite = new GF_InheritanceRelation();  // on re-utilise et on re-instancie l'objet "herite" qui a deja ete dclare
        db.makePersistent(herite);        
        herite.setName("entite_adm/commune");
        herite.setDescription("Une commune est une entite administrative");
        herite.setUniqueInstance(true); // une entite_adm est SOIT une commune, SOIT un departement, mais jamais les 2 a la fois
        commune.addGeneralization(herite); 
        entite.addSpecialization(herite); 
        
        // creation d'une association "prefecture" entre commune et departement
        GF_AssociationType prefecture = new GF_AssociationType();
        db.makePersistent(prefecture);        
        prefecture.setTypeName("prefecture");
        prefecture.setDefinition("Prefecture d'un departement");
        prefecture.addLinkBetween(commune);
        prefecture.addLinkBetween(dept);
        
        // creation d'un attribut "habitant" sur l'association pour donner le nombre d'habitants de la prefecture
        // ceci est permis car GF_AssociationType herite de GF_FeatureType
        GF_AttributeType hab = new GF_AttributeType();
        db.makePersistent(hab);        
        hab.setMemberName("habitants");
        hab.setDefinition("Nombre d'habitants de la prefecture");
        hab.setValueType("int");
        hab.setCardMin(1);
        hab.setCardMax(1); 
        prefecture.addProperty(hab);
        
        // creation du role "est prefecture de " sur commune
        GF_AssociationRole est_pref_de = new GF_AssociationRole();
        db.makePersistent(est_pref_de);        
        est_pref_de.setMemberName("est_prefecture_de");
        est_pref_de.setDefinition("La commune est prefecture de ");
        est_pref_de.setValueType("departement");
        est_pref_de.setCardMin(1);
        est_pref_de.setCardMax(1);
        commune.addProperty(est_pref_de);   // execute automatiquement "est_pref_de.setFeatureType(commune) 
        prefecture.addRole(est_pref_de); // execute automatiquement "est_pref_de.setAssociationType(prefecture) 
        
        // creation du role " a pour prefecture " sur departement
        GF_AssociationRole a_pour_pref = new GF_AssociationRole();
        db.makePersistent(a_pour_pref);        
        a_pour_pref.setMemberName("a_pour_prefecture");
        a_pour_pref.setDefinition("Le departement a pour prefecture");
        a_pour_pref.setValueType("commune");
        a_pour_pref.setCardMin(1);
        a_pour_pref.setCardMax(1);
        dept.addProperty(a_pour_pref);   // execute automatiquement "a_pour_pref.setFeatureType(dept) 
        prefecture.addRole(a_pour_pref); // execute automatiquement "a_pour_pref.setAssociationType(prefecture)
  
        // creation d'une association "compose" entre commune et departement
        GF_AssociationType compose = new GF_AssociationType();
        db.makePersistent(compose);        
        compose.setTypeName("compose");
        compose.setDefinition("Communes composant un departement");
        compose.addLinkBetween(commune);
        compose.addLinkBetween(dept);
        
        // creation d'une operation "getNumber" sur "compose"
        // ceci est permis car GF_AssociationType herite de GF_FeatureType
        op = new GF_Operation();
        db.makePersistent(op);         
        op.setMemberName("getNumber");
        op.setDefinition("Renvoie le nombre de communes constituant le departement");
        op.setSignature("compose.getNumber() : int");
        compose.addProperty(op);    // meme remarque que pour un attribut : executer automatiquement "op.setFeatureType(compose)"
        
        // creation du role "compose" sur commune
        GF_AssociationRole compose_ = new GF_AssociationRole();
        db.makePersistent(compose_);        
        compose_.setMemberName("compose_");
        compose_.setDefinition("La commune compose");
        compose_.setValueType("departement");
        compose_.setCardMin(1);
        compose_.setCardMax(1);
        commune.addProperty(compose_);  
        compose.addRole(compose_);
        
        // creation du role " est_compose_de " sur departement
        GF_AssociationRole est_compose_de = new GF_AssociationRole();
        db.makePersistent(est_compose_de);        
        est_compose_de.setMemberName("est_compose_de");
        est_compose_de.setDefinition("Le departement est compose de");
        est_compose_de.setValueType("commune");
        est_compose_de.setCardMin(1);
        est_compose_de.setCardMax(1000); // au plus 1000 communes dans un departement
        dept.addProperty(est_compose_de);
        compose.addRole(est_compose_de); 
  
        // creation d'une contrainte "ct_geom" sur l'attribut "geom"
        GF_Constraint ct_geom = new GF_Constraint();
        db.makePersistent(ct_geom);        
        ct_geom.setDescription("bla bla");
        geom.addConstraint(ct_geom); 
        
        // creation d'un attribut "age_moyen" sur "habitant"
        GF_AttributeType age = new GF_AttributeType();
        db.makePersistent(age);        
        age.setMemberName("age_moyen");
        age.setDefinition("Age moyen des habitants");
        age.setValueType("int");
        age.setCardMin(1);
        age.setCardMax(1); 
        age.setCharacterize(hab);
        
        // Commit
        System.out.println("Commit");
        db.commit();

    }

    
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    public void interrogeDico ()  {
        
        // inutile d'ouvrir une transaction ici, 
        // car on ne cree pas de nouvelles données persistantes
        
        // Chargement d'un objet par son nom - requete OQL
        String query =  "select x from dico.GF_FeatureType where typeName=$1";
        String parametre = "entite_adm";
        List results = db.loadOQL(query,parametre);
        Iterator it = results.iterator();
        // On parcourt le resultat de la requete (un seul resultat ici !)
        while ( it.hasNext() ) {
            GF_FeatureType ft1 = (GF_FeatureType)it.next();
            System.out.println("identifiant de l'objet chargé : "+ft1.getId());
            System.out.println("nom de l'objet chargé : "+ft1.getTypeName());
            System.out.println("nombre de sous-classes : "+ft1.sizeSpecialization());
            if (ft1.sizeSpecialization() > 0)
                for (int i=0; i<ft1.sizeSpecialization(); i++) {
                    GF_InheritanceRelation rel = (GF_InheritanceRelation)ft1.getSpecialization().get(i);
                    System.out.println("heritage - classe fille :"+rel.getSubType().getTypeName());
                }
            System.out.println("nombre de proprietes : "+ft1.sizeProperties());
            if (ft1.sizeProperties() > 0)
                for (int i=0; i<ft1.sizeProperties(); i++) {
                    GF_PropertyType attr = (GF_PropertyType) ft1.getProperties().get(i);
                    System.out.println("propriete - "+attr.getMemberName()+" : "+attr.getDefinition());
                }
        }
        
        System.out.println("");
        
        // Chargement d'un objet par son nom - requete OQL
        query =  "select x from dico.GF_FeatureType where typeName=$1";
        parametre = "commune";
        results = db.loadOQL(query,parametre);
        it = results.iterator();
        // On parcourt le resultat de la requete (un seul resultat ici !)
        while ( it.hasNext() ) {
            GF_FeatureType ft1 = (GF_FeatureType)it.next();
            System.out.println("identifiant de l'objet chargé : "+ft1.getId());
            System.out.println("nom de l'objet chargé : "+ft1.getTypeName());
            System.out.println("nombre de sous-classes : "+ft1.sizeSpecialization());
			System.out.println("nombre de classes meres : "+ft1.sizeGeneralization());
			// on remarque qu'on ne retrouve pas toutes les proprietes par heritages !
            System.out.println("nombre de proprietes : "+ft1.sizeProperties());
            System.out.println("nombre d'associations : "+ft1.sizeMemberOf());
            if (ft1.sizeMemberOf() > 0)
                for (int i=0; i<ft1.sizeMemberOf(); i++) {
                    GF_AssociationType asso = (GF_AssociationType) ft1.getMemberOf().get(i);
                    System.out.println("association - "+asso.getTypeName()+" : "+asso.getDefinition());
                    System.out.println("nombre de roles - "+asso.sizeRoles());             
                    for (int j=0; j<asso.sizeRoles(); j++) {
                        GF_AssociationRole role = (GF_AssociationRole) asso.getRoles().get(j);
                        System.out.println("    role - "+role.getMemberName()+" : "+role.getDefinition());
                    }
                }
        }
           
    }
               
}
