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

/**
 * Package dédié aux schémas conceptuels. Le modèle utilisé est celui des normes
 * ISO. Les interfaces correspondantes sont dans le package interfacesISO. Deux
 * implémentations sont proposées : une pour les schémas de jeu (notion de
 * ApplicationSchema dans ISO) et une pour les schémas conceptuels (notion de
 * feature catalogue dans ISO). Le package d'utilitaires contient des outils
 * pour la persistence des schémas, pour leur visualisation sous forme de
 * diagramme (à completer) et pour leur exploration dans de simples browser
 * swing (à completer). Produits et leurs métadonnées. S'il s'agit d'une base de
 * données vecteur, un produit est notamment décrit par un schema conceptuel de
 * produit. Un produit est identifié par un entier (classique et pratique pour
 * l'accès par OJB) et par une chaine de caractères unique de la forme
 * PRODUIT_EE_RR où EE est le numéro d'édition, RR, le numéro de version de
 * l'édition. Cet identifiant est aussi utilisé au SIEL.
 */
package fr.ign.cogit.geoxygene.schema;

