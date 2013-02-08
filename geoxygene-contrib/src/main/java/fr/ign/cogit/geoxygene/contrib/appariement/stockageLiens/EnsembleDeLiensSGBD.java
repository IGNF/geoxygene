/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.contrib.appariement.stockageLiens;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.Population;

/**
 * Structure permettant le stockage des résultats de la réalisation d'un
 * appariement au sein du SGBD.
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

@SuppressWarnings({ "unchecked", "rawtypes" })
public class EnsembleDeLiensSGBD extends Population {

  public EnsembleDeLiensSGBD() {
    super(false, I18N.getString("EnsembleDeLiensSGBD.LinkSet"), //$NON-NLS-1$
        LienSGBD.class, true);
  }

  public EnsembleDeLiensSGBD(boolean persistant) {
    super(persistant, I18N.getString("EnsembleDeLiensSGBD.LinkSet"), //$NON-NLS-1$
        LienSGBD.class, true);
  }

  /**
   * Nom du l'ensemble des liens d'appariement (ex:
   * "Appariement des routes par la méthode XX")
   */
  private String nom;

  @Override
  public final String getNom() {
    return this.nom;
  }

  @Override
  public final void setNom(final String nom) {
    this.nom = nom;
  }

  /**
   * Description textuelle des paramètres utilisés pour l'appariement.
   */
  private String parametrage;

  public final String getParametrage() {
    return this.parametrage;
  }

  public final void setParametrage(final String parametrage) {
    this.parametrage = parametrage;
  }

  /**
   * Description textuelle du résultat de l'auto-évaluation des liens.
   */
  private String evaluationInterne;

  public final String getEvaluationInterne() {
    return this.evaluationInterne;
  }

  public final void setEvaluationInterne(final String evaluation) {
    this.evaluationInterne = evaluation;
  }

  /**
   * Description textuelle du résultat de l'évaluation globale des liens.
   */
  private String evaluationGlobale;

  public final String getEvaluationGlobale() {
    return this.evaluationGlobale;
  }

  public final void setEvaluationGlobale(final String evaluation) {
    this.evaluationGlobale = evaluation;
  }

  /**
   * Liste des populations auxquelles les objets ref et comp des liens sont
   * attachés sous forme de string.
   */
  private String populations;

  public final String getPopulations() {
    return this.populations;
  }

  public final void setPopulations(final String populations) {
    this.populations = populations;
  }

  /** Liste aidant à instancier la variable populations */
  private HashSet<String> listePop;

  public final HashSet<String> getListePop() {
    return this.listePop;
  }

  public final void setListePop(final HashSet<String> listePop) {
    this.listePop = listePop;
  }

  /** Liste des populations réelles */
  private List<Population<IFeature>> listePopulations;

  public final List<Population<IFeature>> getListePopulations() {
    return this.listePopulations;
  }

  public final void setListePopulations(
      final List<Population<IFeature>> listePopulations) {
    this.listePopulations = listePopulations;
  }

  /** Date de l'enregistrement */
  private String date;

  public final String getDate() {
    return this.date;
  }

  public final void setDate(final String date) {
    this.date = date;
  }

  /** Couleur du lien : rouge */
  private int rouge;

  public final int getRouge() {
    return this.rouge;
  }

  public final void setRouge(final int rouge) {
    this.rouge = rouge;
  }

  /** Couleur du lien : vert */
  private int vert;

  public final int getVert() {
    return this.vert;
  }

  public final void setVert(final int vert) {
    this.vert = vert;
  }

  /** Couleur du lien : bleu */
  private int bleu;

  public final int getBleu() {
    return this.bleu;
  }

  public final void setBleu(final int bleu) {
    this.bleu = bleu;
  }

  // ////////////////////////////////////////////////////////////////////////
  // relation BIDIRECTIONNELLE 1-n ////////////////////////
  // ////////////////////////////////////////////////////////////////////////
  /**
   * Lien bidirectionnel 1-n vers Lien. 1 objet EnsembleDeLiens est en relation
   * avec n objets LienSGBD (n pouvant etre nul). 1 objet LienSGBD est en
   * relation avec 1 objet EnsembleDeLiens au plus.
   * 
   * NB: un objet EnsembleDeLiens ne doit pas être en relation plusieurs fois
   * avec le même objet LienSGBD : il est impossible de bien gérer des relations
   * 1-n bidirectionnelles avec doublons.
   * 
   * ATTENTION: Pour assurer la bidirection, il faut modifier les listes
   * uniquement avec les methodes fournies.
   * 
   * NB: si il n'y a pas d'objet en relation, la liste est vide mais n'est pas
   * "null". Pour casser toutes les relations, faire setListe(new ArrayList())
   * ou emptyListe().
   */
  private List<LienSGBD> liensSGBD = this.getElements();

  /**
   * Récupère la liste des objets en relation.
   */
  public final List<LienSGBD> getLiensSGBD() {
    return this.liensSGBD;
  }

  /**
   * Définit la liste des objets en relation, et met à jour la relation inverse.
   */
  public final void setLiensSGBD(final List<LienSGBD> L) {
    List<LienSGBD> old = new ArrayList<LienSGBD>(this.liensSGBD);
    for (LienSGBD lien : old) {
      lien.setEnsembleLiensSGBD(null);
    }
    for (LienSGBD lien : L) {
      lien.setEnsembleLiensSGBD(this);
    }
  }

  /**
   * Récupère le ième élément de la liste des objets en relation.
   */
  public final LienSGBD getLienSGBD(final int i) {
    return this.liensSGBD.get(i);
  }

  /**
   * Ajoute un objet à la liste des objets en relation, et met à jour la
   * relation inverse.
   */
  public void addLienSGBD(LienSGBD lien) {
    if (lien == null) {
      return;
    }
    this.liensSGBD.add(lien);
    lien.setEnsembleLiensSGBD(this);
  }

  /**
   * Enlève un élément de la liste des objets en relation, et met à jour la
   * relation inverse.
   */
  public void removeLienSGBD(LienSGBD lien) {
    if (lien == null) {
      return;
    }
    this.liensSGBD.remove(lien);
    lien.setEnsembleLiensSGBD(null);
  }

  /**
   * Vide la liste des objets en relation, et met à jour la relation inverse.
   */
  public void emptyLiensSGBD() {
    List old = new ArrayList(this.liensSGBD);
    Iterator it = old.iterator();
    while (it.hasNext()) {
      LienSGBD lien = (LienSGBD) it.next();
      lien.setEnsembleLiensSGBD(null);
    }
  }

  /**
   * Détruit dans le SGBD l'ensemble de liens SGBD et les liens d'appariement
   * SGBD en correspondance (attention: il faut que les objets soient
   * persistants)
   */
  public void detruitEnsembleDeLiensSGBD(Geodatabase geodatabase) {
    Iterator<LienSGBD> it = this.getLiensSGBD().iterator();
    while (it.hasNext()) {
      LienSGBD lien = it.next();
      geodatabase.deletePersistent(lien);
    }
    geodatabase.deletePersistent(this);
  }

  // ////////////////////////////////////////////////////////////////////////
  // //////// CONVERSION ENTRE ENSEMBLE DE LIENS ET ENSEMBLE DE LIENS SGBD
  // ////////////////////////////////////////////////////////////////////////
  /**
   * Methode de conversion entre les ensembles de liens d'appariement vers les
   * ensemebles de liens SGBD
   */
  public EnsembleDeLiensSGBD conversionEnsembleLiensVersSGBD(
      final EnsembleDeLiens ensemble, final int rouge, final int vert,
      final int bleu) {
    // nom
    if (ensemble.getNom() == null || ensemble.getNom().length() == 0) {
      this.setNom(I18N.getString("EnsembleDeLiensSGBD.NA")); //$NON-NLS-1$
    } else {
      this.setNom(ensemble.getNom());
    }

    // parametrage
    if (ensemble.getParametrage() == null
        || ensemble.getParametrage().length() == 0) {
      this.setParametrage(I18N.getString("EnsembleDeLiensSGBD.NA")); //$NON-NLS-1$
    } else {
      this.setParametrage(ensemble.getParametrage());
    }

    // evaluationInterne
    if (ensemble.getEvaluationInterne() == null
        || ensemble.getEvaluationInterne().length() == 0) {
      this.setEvaluationInterne(I18N.getString("EnsembleDeLiensSGBD.NA")); //$NON-NLS-1$
    } else {
      this.setEvaluationInterne(ensemble.getEvaluationInterne());
    }

    // evaluationGlobale
    if (ensemble.getEvaluationGlobale() == null
        || ensemble.getEvaluationGlobale().length() == 0) {
      this.setEvaluationGlobale(I18N.getString("EnsembleDeLiensSGBD.NA")); //$NON-NLS-1$
    } else {
      this.setEvaluationGlobale(ensemble.getEvaluationGlobale());
    }

    // date
    this.setDate(new GregorianCalendar().getTime().toString());

    // couleur
    this.setRouge(rouge);
    this.setVert(vert);
    this.setBleu(bleu);

    // liensSGBD
    this.setListePop(new HashSet<String>());
    Iterator it = ensemble.getElements().iterator();
    while (it.hasNext()) {
      LienSGBD lienSGBD = (LienSGBD) this.nouvelElement();
      this.addLienSGBD(lienSGBD);
      lienSGBD.conversionLiensVersSGBD((Lien) it.next());
    }

    Iterator<String> itPop = this.listePop.iterator();
    String pop = ""; //$NON-NLS-1$
    while (itPop.hasNext()) {
      pop = pop.concat(itPop.next() + "|"); //$NON-NLS-1$
    }
    if (pop.length() > 0) {
        this.setPopulations(pop.substring(0, pop.length() - 1));
    }
    return this;
  }

  /**
   * Methode de conversion entre les ensembles de liens SGBD vers les ensembles
   * de liens d'appariement
   */
  public void conversionSGBDVersEnsembleLiens(final EnsembleDeLiens ensemble) {
    // nom
    ensemble.setNom(this.getNom());

    // parametrage
    ensemble.setParametrage(this.getParametrage());

    // evaluationInterne
    ensemble.setEvaluationInterne(this.getEvaluationInterne());

    // evaluationGlobale
    ensemble.setEvaluationGlobale(this.getEvaluationGlobale());

    // chargement des populations concernées par les liens d'appariement en
    // mémoire
    StringTokenizer token = new StringTokenizer(this.getPopulations(), "|"); //$NON-NLS-1$
    String pop;
    Geodatabase geodb = GeodatabaseOjbFactory.newInstance();
    this.setListePopulations(new ArrayList());
    while (token.hasMoreElements()) {
      pop = token.nextToken();
      try {
        Class classe = Class.forName(pop);
        Population population = new Population(false,
            I18N.getString("EnsembleDeLiensSGBD.Population" //$NON-NLS-1$
            ), classe, true);
        population.addCollection(geodb.loadAllFeatures(classe));
        this.getListePopulations().add(population);
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println(I18N.getString("EnsembleDeLiensSGBD.WarningClass" //$NON-NLS-1$
            ) + pop + I18N.getString("EnsembleDeLiensSGBD.StoredInLIENS" //$NON-NLS-1$
            ));
        System.out.println(I18N.getString("EnsembleDeLiensSGBD.RESOLUTION")); //$NON-NLS-1$
      }
    }
    // liensSGBD
    Iterator<LienSGBD> it = this.getLiensSGBD().iterator();
    while (it.hasNext()) {
      LienSGBD lienSGBD = it.next();
      // ensemble.getElements().add(lienSGBD.conversionSGBDVersLiens());
      // meilleure solution dans le sens où précise la population du lien
      // mais plantage au niveau du stockage au moment de la création de la
      // population
      // de l'ensemble de liens SGBD
      ensemble.add(lienSGBD.conversionSGBDVersLiens());
    }
  }
}
