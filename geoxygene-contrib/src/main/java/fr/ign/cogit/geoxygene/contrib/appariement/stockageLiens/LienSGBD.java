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

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.Population;

/**
 * Liens stockables dans le SGBD. Resultat de l'appariement : lien entre objets
 * de BDref et objets de BDcomp. Un lien a aussi une géométrie qui est sa
 * représentation graphique.
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

public class LienSGBD extends FT_Feature {

  /**
   * Les objets de Reference pointés par le lien.
   */
  private String objetsRef;

  /**
   * @return reference object list
   */
  public final String getObjetsRef() {
    return this.objetsRef;
  }

  /**
   * @param liste reference object list
   */
  public final void setObjetsRef(final String liste) {
    this.objetsRef = liste;
  }

  /**
   * Les objets de Comparaison pointés par le lien.
   */
  private String objetsComp;

  /**
   * @return comparison object list
   */
  public final String getObjetsComp() {
    return this.objetsComp;
  }

  /**
   * @param liste comparison object list
   */
  public final void setObjetsComp(final String liste) {
    this.objetsComp = liste;
  }

  /**
   * Estimation de la qualité du lien d'appariement (mapping fait avec la table
   * Representation_Lien au besoin).
   */
  private double evaluation;

  /**
   * @return link evaluation
   */
  public final double getEvaluation() {
    return this.evaluation;
  }

  /**
   * @param theEvaluation link evaluation
   */
  public final void setEvaluation(final double theEvaluation) {
    this.evaluation = theEvaluation;
  }

  /**
   * Liste d'indicateurs temporaires utilisés pendant les calculs.
   * d'appariement.
   */
  private String indicateurs;

  /**
   * @return indicators
   */
  public final String getIndicateurs() {
    return this.indicateurs;
  }

  /**
   * @param indicators indicators
   */
  public final void setIndicateurs(final String indicators) {
    this.indicateurs = indicators;
  }

  /**
   * Texte libre. (mapping fait avec la table Representation_Lien au besoin)
   */
  private String commentaire = new String();

  /**
   * @return comment
   */
  public final String getCommentaire() {
    return this.commentaire;
  }

  /**
   * @param comment comment
   */
  public final void setCommentaire(final String comment) {
    this.commentaire = comment;
  }

  /**
   * Texte libre pour décrire le nom de l'appariement. (mapping fait avec la
   * table Representation_Lien au besoin)
   */
  private String nom = new String();

  /**
   * @return matching name
   */
  public final String getNom() {
    return this.nom;
  }

  /**
   * @param aNom matching name
   */
  public final void setNom(final String aNom) {
    this.nom = aNom;
  }

  /**
   * Texte libre pour décrire le type d'appariement (ex. "Noeud-Noeud").
   * (mapping fait avec la table Representation_Lien au besoin)
   */
  private String type = new String();

  /**
   * @return matching type
   */
  public final String getType() {
    return this.type;
  }

  /**
   * @param aType matching type
   */
  public final void setType(final String aType) {
    this.type = aType;
  }

  /**
   * Texte libre pour décrire les objets de référence pointés. (mapping fait
   * avec la table Representation_Lien au besoin).
   */
  private String reference = new String();

  /**
   * @return reference text
   */
  public final String getReference() {
    return this.reference;
  }

  /**
   * @param aReference reference text
   */
  public final void setReference(final String aReference) {
    this.reference = aReference;
  }

  /**
   * Texte libre pour décrire les objets de comparaison pointés. (mapping fait
   * avec la table Representation_Lien au besoin)
   */
  private String comparaison = new String();

  /**
   * @return comparison text
   */
  public final String getComparaison() {
    return this.comparaison;
  }

  /**
   * @param newComparaison comparison text
   */
  public final void setComparaison(final String newComparaison) {
    this.comparaison = newComparaison;
  }

  /**
   * Methode de conversion entre les liens d'appariement vers les liens SGBD.
   * @param lien matching link
   * @return db link
   */
  public final LienSGBD conversionLiensVersSGBD(final Lien lien) {
    List<?> listeObjetsRef = lien.getObjetsRef(), listeObjetsComp = lien
        .getObjetsComp(), indic = lien.getIndicateurs();
    Iterator<?> itRef = listeObjetsRef.iterator(), itComp = listeObjetsComp
        .iterator(), itIndic = indic.iterator();
    FT_Feature feature;
    String formatRef = "", //$NON-NLS-1$
    formatComp = "", //$NON-NLS-1$
    formatIndic = "", //$NON-NLS-1$
    classe = ""; //$NON-NLS-1$
    // Reference
    while (itRef.hasNext()) {
      feature = (FT_Feature) itRef.next();
      classe = feature.getClass().getName();
      if (formatRef.contains(classe) && formatRef.length() != 0) {
        formatRef = formatRef
            .replaceAll(classe, classe + " " + feature.getId()); //$NON-NLS-1$
      } else {
        formatRef = formatRef + classe + " " + feature.getId() + "|"; //$NON-NLS-1$ //$NON-NLS-2$
        this.getEnsembleLiensSGBD().getListePop().add(classe);
      }
    }
    formatRef = formatRef.substring(0, formatRef.length() - 1);
    this.setObjetsRef(formatRef);

    // Comparaison
    while (itComp.hasNext()) {
      feature = (FT_Feature) itComp.next();
      classe = feature.getClass().getName();
      if (formatComp.contains(classe) && formatComp.length() != 0) {
        formatComp = formatComp.replaceAll(classe,
            classe + " " + feature.getId()); //$NON-NLS-1$
      } else {
        formatComp = formatComp + classe + " " + feature.getId() + "|"; //$NON-NLS-1$ //$NON-NLS-2$
        this.getEnsembleLiensSGBD().getListePop().add(classe);
      }
    }
    formatComp = formatComp.substring(0, formatComp.length() - 1);
    this.setObjetsComp(formatComp);

    // Indicateurs
    while (itIndic.hasNext()) {
      formatIndic = formatIndic + (String) itIndic.next() + "|"; //$NON-NLS-1$
    }
    if (formatIndic.length() > 0) {
      this.setIndicateurs(formatIndic.substring(0, formatIndic.length() - 1));
    } else {
      this.setIndicateurs(I18N.getString("EnsembleDeLiensSGBD.NA")); //$NON-NLS-1$
    }

    // evaluation
    this.setEvaluation(lien.getEvaluation());

    // commentaire
    if (lien.getCommentaire().length() == 0) {
      this.setCommentaire(I18N.getString("EnsembleDeLiensSGBD.NA")); //$NON-NLS-1$
    } else {
      this.setCommentaire(lien.getCommentaire());
    }

    // nom
    if (lien.getNom().length() == 0) {
      this.setNom(I18N.getString("EnsembleDeLiensSGBD.NA")); //$NON-NLS-1$
    } else {
      this.setNom(lien.getNom());
    }

    // type
    if (lien.getType().length() == 0) {
      this.setType(I18N.getString("EnsembleDeLiensSGBD.NA")); //$NON-NLS-1$
    } else {
      this.setType(lien.getType());
    }

    // reference
    if (lien.getReference().length() == 0) {
      this.setReference(I18N.getString("EnsembleDeLiensSGBD.NA")); //$NON-NLS-1$
    } else {
      this.setReference(lien.getReference());
    }

    // comparaison
    if (lien.getComparaison().length() == 0) {
      this.setComparaison(I18N.getString("EnsembleDeLiensSGBD.NA")); //$NON-NLS-1$
    } else {
      this.setComparaison(lien.getComparaison());
    }

    // geometrie
    this.setGeom(lien.getGeom());

    return this;
  }

  /**
   * Methode de conversion entre les liens SGBD vers les liens d'appariement.
   * @return matching link
   */
  public final Lien conversionSGBDVersLiens() {
    Lien lien = new Lien();
    // reference
    String formatRef = this.getObjetsRef(), valeurRef, valeurRefClass, valeurRefIds, valeurRefId;
    StringTokenizer tokenRef = new StringTokenizer(formatRef, "|"); //$NON-NLS-1$
    StringTokenizer tokenRefId;
    Population<IFeature> populationCourante;
    IFeature feature;
    List<Population<IFeature>> liste = this.getEnsembleLiensSGBD()
        .getListePopulations();
    while (tokenRef.hasMoreElements()) {
      valeurRef = tokenRef.nextToken();
      valeurRefClass = valeurRef.substring(0, valeurRef.indexOf(" ")); //$NON-NLS-1$
      valeurRefIds = valeurRef.replaceFirst(valeurRefClass, ""); //$NON-NLS-1$
      tokenRefId = new StringTokenizer(valeurRefIds, " "); //$NON-NLS-1$
      Iterator<Population<IFeature>> it = liste.iterator();
      while (it.hasNext()) {
        populationCourante = it.next();
        if (valeurRefClass.equals(populationCourante.getNomClasse())) {
          while (tokenRefId.hasMoreElements()) {
            valeurRefId = tokenRefId.nextToken();
            int refId = new Integer(valeurRefId).intValue();
            Iterator<IFeature> itPop = populationCourante.getElements()
                .iterator();
            while (itPop.hasNext()) {
              feature = itPop.next();
              if (refId == feature.getId()) {
                lien.addObjetRef(feature);
                break;
              }
            }
          }
          break;
        }
      }
    }

    // comparaison
    String formatComp = this.getObjetsComp(), valeurComp, valeurCompClass, valeurCompIds, valeurCompId;
    StringTokenizer tokenComp = new StringTokenizer(formatComp, "|"); //$NON-NLS-1$
    StringTokenizer tokenCompId;
    while (tokenComp.hasMoreElements()) {
      valeurComp = tokenComp.nextToken();
      valeurCompClass = valeurComp.substring(0, valeurComp.indexOf(" ")); //$NON-NLS-1$
      valeurCompIds = valeurComp.replaceFirst(valeurCompClass, ""); //$NON-NLS-1$
      tokenCompId = new StringTokenizer(valeurCompIds, " "); //$NON-NLS-1$
      Iterator<Population<IFeature>> it = liste.iterator();
      while (it.hasNext()) {
        populationCourante = it.next();
        if (valeurCompClass.equals(populationCourante.getNomClasse())) {
          while (tokenCompId.hasMoreElements()) {
            valeurCompId = tokenCompId.nextToken();
            int compId = new Integer(valeurCompId).intValue();
            Iterator<IFeature> itPop = populationCourante.getElements()
                .iterator();
            while (itPop.hasNext()) {
              feature = itPop.next();
              if (compId == feature.getId()) {
                lien.addObjetComp(feature);
                break;
              }
            }
          }
          break;
        }
      }
    }

    // Indicateurs
    String formatIndic = this.getIndicateurs(), valeurIndic;
    StringTokenizer tokenIndic = new StringTokenizer(formatIndic, "|"); //$NON-NLS-1$
    while (tokenIndic.hasMoreElements()) {
      valeurIndic = tokenIndic.nextToken();
      lien.addIndicateur(valeurIndic);
    }

    // evaluation
    lien.setEvaluation(this.getEvaluation());

    // commentaire
    lien.setCommentaire(this.getCommentaire());

    // nom
    lien.setNom(this.getNom());

    // type
    lien.setType(this.getType());

    // reference
    lien.setReference(this.getReference());

    // comparaison
    lien.setComparaison(this.getComparaison());

    // geometrie
    lien.setGeom(this.getGeom());

    return lien;
  }

  // ////////////////////////////////////
  /**
   * The set of links.
   */
  private EnsembleDeLiensSGBD ensembleLiensSGBD;

  /**
   * Récupère l'objet en relation.
   * @return the set of links
   */
  public final EnsembleDeLiensSGBD getEnsembleLiensSGBD() {
    return this.ensembleLiensSGBD;
  }

  /**
   * Define a set of links.
   * <p>
   * Définit l'objet en relation, et met à jour la relation inverse.
   * @param ensemble set of links
   */
  public final void setEnsembleLiensSGBD(final EnsembleDeLiensSGBD ensemble) {
    EnsembleDeLiensSGBD old = this.ensembleLiensSGBD;
    this.ensembleLiensSGBD = ensemble;
    if (old != null) {
      old.getLiensSGBD().remove(this);
    }
    if (ensemble != null) {
      if (!ensemble.getLiensSGBD().contains(this)) {
        ensemble.getLiensSGBD().add(this);
      }
    }
  }
}
