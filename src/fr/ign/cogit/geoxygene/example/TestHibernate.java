package fr.ign.cogit.geoxygene.example;

import java.util.List;

import fr.ign.cogit.geoxygene.datatools.hibernate.GeodatabaseHibernate;
import fr.ign.cogit.geoxygene.example.hibernate.Point_eau;
import fr.ign.cogit.geoxygene.example.hibernate.Troncon_cours_eau;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Classe de test pour la GeodatabaseHibernate. Afin de pouvoir utiliser ce
 * test, il faut activer le mapping des classes Point_eau et Troncon_cours_eau
 * dans le fichier de configuration d'Hibernate : hibernate.cfg.xml qui se
 * trouve dans la racine du répertoire src/ Pour ce faire, ajouter les lignes
 * suivantes : <mapping class="donnees.julien.Point_eau"/> <mapping
 * class="donnees.julien.Troncon_cours_eau"/>
 * @author Julien Perret
 */
public class TestHibernate {

  /**
   * méthode main pour lancer le test. Aucun argument n'est pris en compte.
   * @param args arguments d'exécution. Non pris en compte.
   */
  public static void main(String[] args) {
    // création de la base de données hibernate
    GeodatabaseHibernate db = new GeodatabaseHibernate();

    // début d'une transaction
    db.begin();

    // chargement des objets déjà dans la base
    List<Point_eau> resultPoint_eau = db.loadAll(Point_eau.class);
    int result1 = resultPoint_eau.size();
    System.out.println("result for Point_eau = " + result1);
    List<Troncon_cours_eau> resultTroncon_cours_eau = db
        .loadAll(Troncon_cours_eau.class);
    int result2 = resultTroncon_cours_eau.size();
    System.out.println("result for Troncon_cours_eau= " + result2);

    // termine la transaction
    db.commit();
    // commence une nouvelle transaction
    db.begin();

    // créée des objets point_eau et troncon_cours_eau
    Point_eau p = new Point_eau();

    p.setNature("lac" + result1);
    p.setSource("juju");
    p.setGeom(new GM_Point(new DirectPosition(10000, 10000, 100)));

    // rend les objets créés persistants
    db.makePersistent(p);

    Troncon_cours_eau t = new Troncon_cours_eau();

    t.setNom("rivière" + result1);
    t.setSource("juju");
    DirectPositionList liste = new DirectPositionList();
    liste.add(new DirectPosition(10000, 10000, 100));
    liste.add(new DirectPosition(20000, 10000, 10));
    t.setGeom(new GM_LineString(liste));

    db.makePersistent(t);

    // termine la transaction
    db.commit();

  }

}
