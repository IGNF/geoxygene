/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.example.tutorial;

import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;

/**
 * classe exemple
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */

public class TestMyClass {
  // une connection au SGBD via le mappeur objet-relationnel
  private Geodatabase db;

  public TestMyClass() {
    // initialise la connection au SGBD et le mapping
    this.db = GeodatabaseOjbFactory.newInstance();
  }

  public static void main(String args[]) {

    // construction d'un objet "TestMaclasse"
    TestMyClass test = new TestMyClass();

    // appel des methodes
    test.creation();
    test.chargement();
    test.interroge();

  }

  // cree de nouveaux objets et les rend persistants
  public void creation() {

    // ouverture d'une transaction
    System.out.println("ouverture");
    this.db.begin();

    // creation d'un objet persistent
    MyClass obj = new MyClass();
    this.db.makePersistent(obj);

    obj.bonjour();
    obj.setField0(123);
    obj.setField1("M. et Mme. PIC ont une fille");
    obj.setField2(true);
    obj.setField3(3.14159);

    // remarque : aucun identifiant n'est affecte; il est affecte
    // automatiquement par OJB

    // commit et fermeture de la transaction
    // valide toutes les creations et modifications
    // une modif meme apres le "db.makePersistent" est repercutee
    System.out.println("commit");
    this.db.commit();
    System.out.println("commit ok");
  }

  // charge les objets de la base
  public void chargement() {

    // ouverture d'une transaction
    System.out.println("ouverture");
    this.db.begin();

    // chargement de tous objets de Maclasse
    List<?> list = this.db.loadAll(MyClass.class);

    // parcours de tous les objets
    System.out.println("nb: " + list.size());
    for (Object obj_ : list) {
      MyClass obj = (MyClass) obj_;
      System.out.println("id : " + obj.getId());
      System.out.println("classe : " + obj.getClass().getName());
      System.out.println("field0 : " + obj.getField0());
      System.out.println("field1 : " + obj.getField1());
      System.out.println("field2 : " + obj.getField2());
      System.out.println("field3 : " + obj.getField3());
    }

    System.out.println("commit");
    this.db.commit();
    System.out.println("commit ok");
  }

  //
  public void interroge() {

    // ouverture d'une transaction
    System.out.println("ouverture");
    this.db.begin();

    // la requete
    String requete = "select x from fr.ign.cogit.geoxygene.example.tutorial.MyClass where field0 > $1";
    // $1 represente un parametre (200 ici)
    List<?> list = this.db.loadOQL(requete, new Integer(100));

    // parcours de tous les objets
    System.out.println("nb: " + list.size());
    for (Object obj_ : list) {
      MyClass obj = (MyClass) obj_;
      System.out.println("id : " + obj.getId());
    }

    System.out.println("commit");
    this.db.commit();
    System.out.println("commit ok");
  }

}
