/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.contrib.cartetopo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Classe racine de la carte topo.
 * Une carte topo est une composition d'arcs, de noeuds, de faces et de groupes.
 * Une carte topo est vue comme un DataSet particulier.
 * Elle a éventuellement une topologie (SPAGHETTI, NETWORK ou MAP).
 * <p>
 * English: a topological map is an oriented graph, with arcs sorted around the
 * nodes;
 *
 * @author Sébastien Mustière
 * @author Olivier Bonin
 * @author Julien Perret
 */

public class CarteTopo extends DataSet {
    static Logger logger = Logger.getLogger(CarteTopo.class.getName());

    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Adds an <code>ActionListener</code> to the carteTopo.
     *
     * @param l
     *            the <code>ActionListener</code> to be added
     */
    public void addActionListener(ActionListener l) {
        this.listenerList.add(ActionListener.class, l);
    }

    /**
     * Sets the list of action listeners.
     *
     * @param listenerList
     *            list of action listeners
     */
    public void setActionListeners(EventListenerList listenerList) {
        this.listenerList = listenerList;
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type. The event instance
     * is lazily created.
     *
     * @see EventListenerList
     */
    protected void fireActionPerformed(ActionEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = this.listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                // Lazily create the event:
                ((ActionListener) listeners[i + 1]).actionPerformed(event);
            }
        }
    }

    // accès aux composants de la carte topo

    /** Population des arcs de la carte topo. */
    @SuppressWarnings("unchecked")
    public Population<Arc> getPopArcs() {
        return (Population<Arc>) this.getPopulation(I18N
                .getString("CarteTopo.Edge"));} //$NON-NLS-1$

    /** Population des noeuds de la carte topo. */
    @SuppressWarnings("unchecked")
    public Population<Noeud> getPopNoeuds() {
        return (Population<Noeud>) this.getPopulation(I18N
                .getString("CarteTopo.Node"));} //$NON-NLS-1$

    /** Population des faces de la carte topo. */
    @SuppressWarnings("unchecked")
    public Population<Face> getPopFaces() {
        return (Population<Face>) this.getPopulation(I18N
                .getString("CarteTopo.Face"));} //$NON-NLS-1$

    /** Population des groupes de la carte topo. */
    @SuppressWarnings("unchecked")
    public Population<Groupe> getPopGroupes() {
        return (Population<Groupe>) this.getPopulation(I18N
                .getString("CarteTopo.Group"));} //$NON-NLS-1$

    /**
     * Liste des noeuds de la carte topo. Surcharge de
     * getPopNoeuds().getElements().
     */
    public List<Noeud> getListeNoeuds() {
        return this.getPopNoeuds().getElements();
    }

    /**
     * Liste des arcs de la carte topo. Surcharge de getPopArcs().getElements().
     */
    public List<Arc> getListeArcs() {
        return this.getPopArcs().getElements();
    }

    /**
     * Liste des faces de la carte topo. Surcharge de
     * getPopFaces().getElements().
     */
    public List<Face> getListeFaces() {
        return this.getPopFaces().getElements();
    }

    /**
     * Liste des groupes de la carte topo. Surcharge de
     * getPopGroupes().getElements().
     */
    public List<Groupe> getListeGroupes() {
        return this.getPopGroupes().getElements();
    }

    /**
     * Ajoute un noeud à la population des noeuds de la carte topo.
     * Attention : même si la carte topo est persistante, le noeud n'est pas
     * rendu persistant dans cette méthode
     */
    public void addNoeud(Noeud noeud) {
        this.getPopNoeuds().add(noeud);
    }

    /**
     * Ajoute un arc à la population des arcs de la carte topo.
     * Attention : même si la carte topo est persistante, le noeud n'est pas
     * rendu persistant dans cette méthode
     */
    public void addArc(Arc arc) {
        this.getPopArcs().add(arc);
    }

    /**
     * Ajoute une face à la population des faces de la carte topo.
     * Attention : même si la carte topo est persistante, le noeud n'est pas
     * rendu persistant dans cette méthode
     */
    public void addFace(Face face) {
        this.getPopFaces().add(face);
    }

    /**
     * Ajoute un groupe à la population des groupes de la carte topo.
     * Attention : même si la carte topo est persistante, le noeud n'est pas
     * rendu persistant dans cette méthode
     */
    public void addGroupe(Groupe groupe) {
        this.getPopGroupes().add(groupe);
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////
    // Constructeurs
    // ///////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Constructeur par défaut ;
     * ATTENTION, constructeur à éviter car aucune population n'est créée:
     * seule un objet carteTopo est créé.
     * Ce constructeur sert aux constructeurs des sous-classe de la CarteTopo.
     */
    public CarteTopo() {
    }

    /**
     * Constructeur d'une carte topo non persistante.
     * Le nom logique peut être utilisé si la carte topo apparient à un DataSet,
     * il peut être une chaîne vide sinon.
     * Par ce constructeur, la carte topo contient des arcs/noeuds/faces/groupes
     * des classes CarteTopo.Arc, CarteTopo.Noeud, CarteTopo.Face,
     * CarteTopo.Groupe.
     *
     * @param nomLogique
     *            nom de la carte topo
     */
    public CarteTopo(String nomLogique) {
        this.ojbConcreteClass = this.getClass().getName(); // nécessaire pour
                                                           // ojb
        this.setNom(nomLogique);
        this.setPersistant(false);
        this
                .addPopulation(new Population<Arc>(
                        false,
                        I18N.getString("CarteTopo.Edge"), fr.ign.cogit.geoxygene.contrib.cartetopo.Arc.class, true)); //$NON-NLS-1$
        this
                .addPopulation(new Population<Noeud>(
                        false,
                        I18N.getString("CarteTopo.Node"), fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud.class, true)); //$NON-NLS-1$
        this
                .addPopulation(new Population<Face>(
                        false,
                        I18N.getString("CarteTopo.Face"), fr.ign.cogit.geoxygene.contrib.cartetopo.Face.class, true)); //$NON-NLS-1$
        this
                .addPopulation(new Population<Groupe>(
                        false,
                        I18N.getString("CarteTopo.Group"), fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe.class, false)); //$NON-NLS-1$
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////
    // Attributs de la carte topo
    // ///////////////////////////////////////////////////////////////////////////////////////////

    // Description de la structure topologique
    /** Spaghetti = pas de relation topologique entre arcs, noeuds et faces */
    public static final int SPAGHETTI = 0;
    /** Network = topologie arc / noeuds */
    public static final int NETWORK = 1;
    /** Network = topologie arc / noeuds / faces */
    public static final int MAP = 2;
    /**
     * Niveau de topologie :
     * SPAGHETTI = pas de topologie ; NETWORK = topologie arcs/noeuds ; MAP (par
     * défaut) = topologie arcs/noeuds/faces
     * NB : utiliser les constantes SPAGHETTI, NETWORK ou MAP pour remplir cet
     * attribut.
     * Remarque codeurs : Le code se sert très peu de l'attribut "type" pour
     * l'instant. A revoir.
     */
    private int type = MAP;

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////
    // COPIE et VIDAGE de la carte topo
    // ///////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Copie d'une carte topologique avec toutes les relations topologiques.
     * Les liens "correspondants" sont aussi dupliqués.
     * <strong>ATTENTION: ne fonctionne bien que pour une carteTopo non
     * spécialisée
     * (i.e. avec carteTopo.Arc, carte.Noeud...).</strong>
     * En effet, les objets copiés appartiendront au package cartetopo.
     *
     * @param nomLogique
     *            nom de la carte topo
     * @return une copie d'une carte topologique avec toutes les relations
     *         topologiques.
     */
    public CarteTopo copie(String nomLogique) {
        // création d'une nouvelle carte
        CarteTopo carte = new CarteTopo(nomLogique);

        // copie des objets, sans relation topologique
        for (Noeud noeud : this.getPopNoeuds()) {
            Noeud noeudCopie = carte.getPopNoeuds().nouvelElement();
            noeudCopie.setGeometrie(noeud.getGeometrie());
            noeudCopie.addAllCorrespondants(noeud.getCorrespondants());
        }
        for (Arc arc : this.getPopArcs()) {
            Arc arcCopie = carte.getPopArcs().nouvelElement();
            arcCopie.setGeometrie(arc.getGeometrie());
            arcCopie.addAllCorrespondants(arc.getCorrespondants());
        }
        for (Face face : this.getPopFaces()) {
            Face faceCopie = carte.getPopFaces().nouvelElement();
            faceCopie.setGeometrie(face.getGeometrie());
            faceCopie.addAllCorrespondants(face.getCorrespondants());
        }
        for (Groupe groupe : this.getPopGroupes()) {
            Groupe groupeCopie = carte.getPopGroupes().nouvelElement();
            groupeCopie.addAllCorrespondants(groupe.getCorrespondants());
        }

        if (this.type == SPAGHETTI)
            return carte;

        // copie des relations topologiques
        List<Noeud> noeuds = new ArrayList<Noeud>(this.getPopNoeuds()
                .getElements());
        List<Noeud> noeudsCopies = new ArrayList<Noeud>(carte.getPopNoeuds()
                .getElements());
        List<Arc> arcs = new ArrayList<Arc>(this.getPopArcs().getElements());
        List<Arc> arcsCopies = new ArrayList<Arc>(carte.getPopArcs()
                .getElements());
        List<Face> faces = new ArrayList<Face>(this.getPopFaces().getElements());
        List<Face> facesCopies = new ArrayList<Face>(carte.getPopFaces()
                .getElements());

        Iterator<Arc> itArcs = this.getPopArcs().getElements().iterator();
        Iterator<Arc> itArcsCopies = carte.getPopArcs().getElements()
                .iterator();
        while (itArcs.hasNext()) {
            Arc arc = itArcs.next();
            Arc arcCopie = itArcsCopies.next();
            arcCopie.setNoeudIni(noeudsCopies.get(noeuds.indexOf(arc
                    .getNoeudIni())));
            arcCopie.setNoeudFin(noeudsCopies.get(noeuds.indexOf(arc
                    .getNoeudFin())));
            if (arc.getFaceGauche() != null) {
                arcCopie.setFaceGauche(facesCopies.get(faces.indexOf(arc
                        .getFaceGauche())));
            }
            if (arc.getFaceDroite() != null) {
                arcCopie.setFaceDroite(facesCopies.get(faces.indexOf(arc
                        .getFaceDroite())));
            }
        }

        Iterator<Groupe> itGroupes = this.getPopGroupes().getElements()
                .iterator();
        Iterator<Groupe> itGroupesCopies = carte.getPopGroupes().getElements()
                .iterator();
        while (itGroupes.hasNext()) {
            Groupe groupe = itGroupes.next();
            Groupe groupeCopie = itGroupesCopies.next();
            for (Noeud noeud : groupe.getListeNoeuds())
                groupeCopie.addNoeud(noeudsCopies.get(noeuds.indexOf(noeud)));
            for (Arc arc : groupe.getListeArcs())
                groupeCopie.addArc(arcsCopies.get(arcs.indexOf(arc)));
            for (Face face : groupe.getListeFaces())
                groupeCopie.addFace(facesCopies.get(faces.indexOf(face)));
        }
        return carte;
    }

    /**
     * enlève des arcs de la carteTopo, en enlevant aussi les relations
     * topologiques
     * les concernant (avec les faces et noeuds).
     *
     * @param arcsAEnlever
     *            liste des arcs à enlever de la carte topo
     */
    public void enleveArcs(List<Arc> arcsAEnlever) {
        for (Arc arc : arcsAEnlever) {
            if (!this.getPopArcs().remove(arc)) {
                if (logger.isDebugEnabled())
                    logger
                            .debug(I18N.getString("CarteTopo.DeletionFailed") + arc); //$NON-NLS-1$
            }
            arc.setNoeudFin(null);
            arc.setNoeudIni(null);
            arc.setFaceDroite(null);
            arc.setFaceGauche(null);
        }
    }

    /**
     * enlève les arcs qui forment une boucle, i.e. dont le noeud ini est égal
     * au noeud fin.
     */
    public void enleveArcsBoucles() {
        List<Arc> listeArcs = new ArrayList<Arc>();
        for (Arc arc : this.getPopArcs())
            if (arc.getNoeudIni().equals(arc.getNoeudFin()))
                listeArcs.add(arc);
        this.enleveArcs(listeArcs);
    }

    /**
     * enlève des noeuds de la carteTopo, en enlevant aussi les relations
     * topologiques
     * les concernant (avec les arcs et par conséquent avec les faces).
     *
     * @param noeudsAEnlever
     *            noeuds à enlever de la carte topo
     */
    public void enleveNoeuds(List<Noeud> noeudsAEnlever) {
        Iterator<Noeud> itNoeuds = noeudsAEnlever.iterator();
        Iterator<Arc> itArcs;
        Noeud noeud;
        Arc arc;
        while (itNoeuds.hasNext()) {
            noeud = itNoeuds.next();
            this.getPopNoeuds().remove(noeud);
            itArcs = noeud.getEntrants().iterator();
            while (itArcs.hasNext()) {
                arc = itArcs.next();
                arc.setNoeudFin(null);
            }
            itArcs = noeud.getSortants().iterator();
            while (itArcs.hasNext()) {
                arc = itArcs.next();
                arc.setNoeudIni(null);
            }
        }
    }

    /**
     * enlève des faces de la carteTopo, en enlevant aussi les relations
     * topologiques
     * les concernant (avec les arcs et par conséquent avec les noeuds).
     *
     * @param facesAEnlever
     *            liste des face à enlever de la carte topo
     */
    public void enleveFaces(List<Face> facesAEnlever) {
        Iterator<Face> itFaces = facesAEnlever.iterator();
        Iterator<Arc> itArcs;
        Face face;
        Arc arc;
        while (itFaces.hasNext()) {
            face = itFaces.next();
            this.getPopFaces().remove(face);
            itArcs = face.getArcsDirects().iterator();
            while (itArcs.hasNext()) {
                arc = itArcs.next();
                arc.setFaceGauche(null);
            }
            itArcs = face.getArcsIndirects().iterator();
            while (itArcs.hasNext()) {
                arc = itArcs.next();
                arc.setFaceDroite(null);
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////
    // Instanciation ou nettoyage de la topologie de réseau
    // ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Instancie la topologie de réseau d'une Carte Topo,
     * en se basant sur la géométrie 2D des arcs et des noeuds.
     * Autrement dit: crée les relation "noeud initial" et "noeud final" d'un
     * arc.
     * <ul>
     * <li><strong>ATTENTION: cette méthode ne rajoute pas de noeuds. Si un arc
     * n'a pas de noeud localisé à son extrémité, il n'aura pas de noeud initial
     * (ou final).</strong>
     * <li><strong>DE PLUS si plusieurs noeuds sont trop proches (cf. param
     * tolérance), alors un des noeuds est choisi au hasard pour la relation
     * arc/noeud, ce qui n'est pas correct.</strong>
     * <li><strong>IL EST DONC CONSEILLE DE FILTRER LES DOUBLONS AVANT SI
     * NECESSAIRE.</strong>
     * <li>NB: si cela n'avait pas été fait avant, la population des noeuds est
     * indexée dans cette méthode (dallage, paramètre = 20).
     * </ul>
     *
     * @param tolerance
     *            Le paramètre "tolerance" spécifie la distance maximale
     *            acceptée entre
     *            la position d'un noeud et la position d'une extrémité de
     *            ligne,
     *            pour considérer ce noeud comme extrémité (la tolérance peut
     *            être nulle).
     */
    public void creeTopologieArcsNoeuds(double tolerance) {
        Arc arc;
        Collection<Noeud> selection;
        // initialisation de l'index au besoin
        if (!this.getPopNoeuds().hasSpatialIndex()) {
            this.getPopNoeuds().initSpatialIndex(Tiling.class, true, 20);
        }
        for (Object a : this.getPopArcs()) {
            arc = (Arc) a;
            selection = this.getPopNoeuds().select(
                    arc.getGeometrie().startPoint(), tolerance);
            if (!selection.isEmpty()) {
                arc.setNoeudIni(selection.iterator().next());
            }
            selection = this.getPopNoeuds().select(
                    arc.getGeometrie().endPoint(), tolerance);
            if (!selection.isEmpty()) {
                arc.setNoeudFin(selection.iterator().next());
            }
        }
    }

    /**
     * crée un nouveau noeud à l'extrémité de chaque arc si il n'y en a pas.
     * Les noeuds existants sont tous conservés.
     * <ul>
     * <li>NB: la topologie arcs/noeuds est instanciée au passage.
     * <li>NB: si cela n'avait pas été fait avant, la population des noeuds est
     * indexée dans cette méthode.
     * <li>paramètres de l'index = le même que celui des arcs si il existe,
     * sinon Dallage avec à peu près 50 noeuds par dalle.
     * </ul>
     *
     * @param tolerance
     *            tolérance utilisée pour chercher des noeuds existants auxquels
     *            se raccrocher au lieu d'en créer de nouveaux.
     */
    public void creeNoeudsManquants(double tolerance) {
        Noeud noeud;
        // initialisation de l'index au besoin
        // si on peut, on prend les mêmes paramètres que le dallage des arcs
        if (!this.getPopNoeuds().hasSpatialIndex()) {
            if (this.getPopArcs().hasSpatialIndex()) {
                this.getPopNoeuds().initSpatialIndex(
                        this.getPopArcs().getSpatialIndex());
                this.getPopNoeuds().getSpatialIndex().setAutomaticUpdate(true);
            } else {
                GM_Envelope enveloppe = this.getPopArcs().envelope();
                int nb = (int) Math.sqrt(this.getPopArcs().size() / 20);
                if (nb == 0)
                    nb = 1;
                this.getPopNoeuds().initSpatialIndex(Tiling.class, true,
                        enveloppe, nb);
            }
        }
        fireActionPerformed(new ActionEvent(
                this,
                0,
                I18N.getString("CarteTopo.MissingNodesCreation"), this.getPopArcs().size())); //$NON-NLS-1$
        int index = 0;
        List<Arc> arcsVides = new ArrayList<Arc>();
        for (Object a : this.getPopArcs()) {
            Arc arc = (Arc) a;
            if (arc.getGeometrie().sizeControlPoint() == 0) {
                logger.error(I18N.getString("CarteTopo.EmptyEdge")); //$NON-NLS-1$
                arcsVides.add(arc);
                continue;
            }
            // noeud initial
            Collection<Noeud> selection = this.getPopNoeuds().select(
                    arc.getGeometrie().startPoint(), tolerance);
            if (selection.isEmpty()) {
                noeud = this.getPopNoeuds().nouvelElement(
                        new GM_Point(arc.getGeometrie().startPoint()));
            } else {
                noeud = selection.iterator().next();
                arc.getGeometrie().coord().set(0,
                        noeud.getGeometrie().getPosition());
            }
            arc.setNoeudIni(noeud);
            // noeud final
            selection = this.getPopNoeuds().select(
                    arc.getGeometrie().endPoint(), tolerance);
            if (selection.isEmpty()) {
                noeud = this.getPopNoeuds().nouvelElement(
                        new GM_Point(arc.getGeometrie().endPoint()));
            } else {
                noeud = selection.iterator().next();
                arc.getGeometrie().coord().set(
                        arc.getGeometrie().sizeControlPoint() - 1,
                        noeud.getGeometrie().getPosition());
            }
            arc.setNoeudFin(noeud);
            fireActionPerformed(new ActionEvent(this, 1, I18N
                    .getString("CarteTopo.EdgeHandled"), ++index)); //$NON-NLS-1$
        }
        this.getPopArcs().removeAll(arcsVides);
        fireActionPerformed(new ActionEvent(this, 4, I18N
                .getString("CarteTopo.MissingNodesCreated"))); //$NON-NLS-1$
    }

    /**
     * Filtrage des noeuds isolés (c'est-à-dire connectés à aucun arc).
     * Ceux-ci sont enlevés de la Carte Topo
     * IMPORTANT : La topologie de réseau doit avoir été instanciée,
     * sinon tous les noeuds sont enlevés.
     */
    public void filtreNoeudsIsoles() {
        List<Noeud> aJeter = new ArrayList<Noeud>();
        for (Noeud noeud : this.getPopNoeuds())
            if (noeud.arcs().size() == 0)
                aJeter.add(noeud);
        for (Noeud noeud : aJeter)
            this.getPopNoeuds().enleveElement(noeud);
    }

    /**
     * Filtrage des noeuds doublons (plusieurs noeuds localisés au même
     * endroit).
     * <ul>
     * <li>NB: si cela n'avait pas été fait avant, la population des noeuds est
     * indexée dans cette méthode (dallage, paramètre = 20).
     * <li>Cette méthode gère les conséquences sur la topologie, si celle-ci a
     * été instanciée auparavant.
     * <li>Cette méthode gère aussi les conséquences sur les correspondants (un
     * noeud gardé a pour correspondants tous les correspondants des doublons).
     * </ul>
     *
     * @param tolerance
     *            Le paramètre tolérance spécifie la distance maximale pour
     *            considérer deux
     *            noeuds positionnés au même endroit.
     */
    public void filtreDoublons(double tolerance) {
        List<Noeud> aJeter = new ArrayList<Noeud>();
        Collection<Noeud> selection;

        // initialisation de l'index au besoin
        if (!this.getPopNoeuds().hasSpatialIndex())
            this.getPopNoeuds().initSpatialIndex(Tiling.class, true, 20);
        for (Noeud noeud : this.getPopNoeuds()) {
            if (aJeter.contains(noeud))
                continue;
            selection = this.getPopNoeuds().select(noeud.getCoord(), tolerance);
            selection.remove(noeud);
            for (Noeud doublon : selection) {
                // on a trouvé un doublon à jeter
                // on gère les conséquences sur la topologie et les
                // correspondants
                aJeter.add(doublon);
                noeud.addAllCorrespondants(doublon.getCorrespondants());
                for (Arc a : doublon.getEntrants())
                    noeud.addEntrant(a);
                for (Arc a : doublon.getSortants())
                    noeud.addSortant(a);
            }
        }
        this.getPopNoeuds().removeAll(aJeter);
    }

    /**
     * Filtrage des noeuds "simples", c'est-à-dire avec seulement deux arcs
     * incidents,
     * si ils ont des orientations compatibles.
     * Ces noeuds sont enlevés et un seul arc est créé à la place des deux arcs
     * incidents.
     * <ul>
     * <li>Cette méthode gère les conséquences sur la topologie
     * arcs/noeuds/faces.
     * <li>Cette méthode gère aussi les conséquences sur les correspondants. (un
     * nouvel arc a pour correspondants tous les correspondants des deux arcs
     * incidents).
     * <li>Cette méthode gère les conséquences sur l'orientation
     * </ul>
     * <p>
     * <strong>IMPORTANT: la topologie arcs/noeuds doit avoir été instanciée
     * avant de lancer cette méthode</strong>
     */
    public void filtreNoeudsSimples() {
        List<GM_LineString> geometries;
        List<Arc> arcsIncidents;
        Noeud noeudIni1, noeudIni2, noeudFin1, noeudFin2;
        Arc arcTotal, arc1, arc2;
        Face faceDroite1, faceDroite2, faceGauche1, faceGauche2;

        List<Noeud> noeudsElimines = new ArrayList<Noeud>();
        for (Noeud noeud : this.getPopNoeuds()) {
            arcsIncidents = noeud.arcs();
            if (arcsIncidents.size() != 2)
                continue;
            if (arcsIncidents.get(0) == arcsIncidents.get(1))
                continue; // gestion des boucles
            if (noeud.entrantsOrientes().size() == 0)
                continue; // incompatibilité d'orientation
            if (noeud.sortantsOrientes().size() == 0)
                continue; // incompatibilité d'orientation
            if ((noeud.entrantsOrientes().size() + noeud.sortantsOrientes()
                    .size()) == 3)
                continue; // incompatibilité d'orientation

            arcTotal = this.getPopArcs().nouvelElement();
            geometries = new ArrayList<GM_LineString>();
            arc1 = arcsIncidents.get(0);
            arc2 = arcsIncidents.get(1);
            geometries.add(arc1.getGeometrie());
            geometries.add(arc2.getGeometrie());

            // création de la nouvelle géométrie
            arcTotal.setGeometrie(Operateurs.compileArcs(geometries));

            // gestion des conséquences sur l'orientation et les correspondants
            arcTotal.setOrientation(arc1.getOrientation());
            for (FT_Feature corresp : arc1.getCorrespondants()) {
                if (!arcTotal.getCorrespondants().contains(corresp))
                    arcTotal.addCorrespondant(corresp);
            }
            arc1.setCorrespondants(new ArrayList<FT_Feature>());

            for (FT_Feature corresp : arc2.getCorrespondants())
                arcTotal.addCorrespondant(corresp);
            arc2.setCorrespondants(new ArrayList<FT_Feature>());

            for (FT_Feature corresp : noeud.getCorrespondants()) {
                if (!arcTotal.getCorrespondants().contains(corresp))
                    arcTotal.addCorrespondant(corresp);
            }
            noeud.setCorrespondants(new ArrayList<FT_Feature>());

            // gestion des conséquences sur la topologie
            faceDroite1 = arc1.getFaceDroite();
            faceGauche1 = arc1.getFaceGauche();
            faceDroite2 = arc2.getFaceDroite();
            faceGauche2 = arc2.getFaceGauche();
            noeudIni1 = arc1.getNoeudIni();
            noeudFin1 = arc1.getNoeudFin();
            noeudIni2 = arc2.getNoeudIni();
            noeudFin2 = arc2.getNoeudFin();

            // conséquences sur le premier arc
            if (noeudIni1 == noeud) {
                noeudIni1.getSortants().remove(arc1);
                if (noeudFin1 != null) {
                    noeudFin1.getEntrants().remove(arc1);
                    noeudFin1.addSortant(arcTotal);

                }
                if (faceDroite1 != null) {
                    faceDroite1.getArcsIndirects().remove(arc1);
                    arcTotal.setFaceGauche(faceDroite1);
                }
                if (faceGauche1 != null) {
                    faceGauche1.getArcsDirects().remove(arc1);
                    arcTotal.setFaceDroite(faceGauche1);
                }
            } else {
                noeudFin1.getEntrants().remove(arc1);
                if (noeudIni1 != null) {
                    noeudIni1.getSortants().remove(arc1);
                    noeudIni1.addSortant(arcTotal);
                }
                if (faceDroite1 != null) {
                    faceDroite1.getArcsIndirects().remove(arc1);
                    arcTotal.setFaceDroite(faceDroite1);
                }
                if (faceGauche1 != null) {
                    faceGauche1.getArcsDirects().remove(arc1);
                    arcTotal.setFaceGauche(faceGauche1);
                }
            }

            // conséquences sur le deuxième arc
            if (noeudIni2 == noeud) {
                noeudIni2.getSortants().remove(arc2);
                if (noeudFin2 != null) {
                    noeudFin2.getEntrants().remove(arc2);
                    noeudFin2.addEntrant(arcTotal);

                }
                if (faceDroite2 != null) {
                    faceDroite2.getArcsIndirects().remove(arc2);
                }
                if (faceGauche2 != null) {
                    faceGauche2.getArcsDirects().remove(arc2);
                }
            } else {
                noeudFin2.getEntrants().remove(arc2);
                if (noeudIni2 != null) {
                    noeudIni2.getSortants().remove(arc2);
                    noeudIni2.addEntrant(arcTotal);
                }
                if (faceDroite2 != null) {
                    faceDroite2.getArcsIndirects().remove(arc2);
                }
                if (faceGauche2 != null) {
                    faceGauche2.getArcsDirects().remove(arc2);
                }
            }

            // Elimination des arcs et du noeud inutile
            this.getPopArcs().enleveElement(arc1);
            this.getPopArcs().enleveElement(arc2);
            noeudsElimines.add(noeud);
        }
        int i;
        for (i = 0; i < noeudsElimines.size(); i++) {
            this.getPopNoeuds().enleveElement(noeudsElimines.get(i));
        }
    }

    /**
     * Filtre les arcs en double (en double = même géométrie et même
     * orientation).
     * On vérifie à la fois la géométrie et la topologie, i.e. la topologie
     * arcs/noeuds doit être instanciée.
     * <strong>Attention: les conséquences sur la topologie arcs/faces ne sont
     * pas gérées.</strong>
     * TODO s'il existe des arcs en plus de 2 exemplaires, ils ne sont pas
     * enlevés je crois
     */
    public void filtreArcsDoublons() {
        List<Arc> arcs = this.getListeArcs();
        List<Arc> arcsAEnlever = new ArrayList<Arc>();
        fireActionPerformed(new ActionEvent(this, 0, I18N
                .getString("CarteTopo.DoubleEdgesFiltering"), arcs.size() - 1)); //$NON-NLS-1$
        for (int i = 0; i < arcs.size() - 1; i++) {
            Arc arci = arcs.get(i);
            if (arcsAEnlever.contains(arci))
                continue;// on a déjà décidé d'enlever cet arc
            for (int j = i + 1; j < arcs.size(); j++) {
                Arc arcj = arcs.get(j);
                if (arcj.getOrientation() != arcj.getOrientation())
                    continue;
                if (!((arci.getNoeudIni() == arcj.getNoeudIni() && arci
                        .getNoeudFin() == arcj.getNoeudFin()) || (arci
                        .getNoeudFin() == arcj.getNoeudIni() && arci
                        .getNoeudIni() == arcj.getNoeudFin())))
                    continue;
                if (!arcj.getGeom().equals(arci.getGeom()))
                    continue;
                arcsAEnlever.add(arcj);
                for (FT_Feature corresp : arcj.getCorrespondants())
                    arci.addCorrespondant(corresp);
                arcj.setCorrespondants(new ArrayList<FT_Feature>());
                arcj.setNoeudFin(null);
                arcj.setNoeudIni(null);
            }
            fireActionPerformed(new ActionEvent(this, 1, I18N
                    .getString("CarteTopo.EdgeHandled"), i + 1)); //$NON-NLS-1$
        }
        this.enleveArcs(arcsAEnlever);
        arcsAEnlever.clear();
        for (Arc arc : this.getListeArcs()) {
            if (arc.getNoeudIni() == null || arc.getNoeudFin() == null) {
                logger.warn(I18N.getString("CarteTopo.NullEdge") + arc); //$NON-NLS-1$
                arcsAEnlever.add(arc);
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(arcsAEnlever.size()
                    + I18N.getString("CarteTopo.NullEdgesToRemove")); //$NON-NLS-1$
        this.enleveArcs(arcsAEnlever);
        // if (!this.getPopArcs().removeAll(arcsAEnlever))
        // logger.warn("Attention : des arcs n'ont pas pu être enlevés");
        fireActionPerformed(new ActionEvent(this, 4, I18N
                .getString("CarteTopo.DoubleEdgesFiltered"))); //$NON-NLS-1$
    }

    /**
     * Transforme la carte topo pour la rendre planaire :
     * les arcs sont découpés à chaque intersection d'arcs,
     * et un noeud est créé à chaque extrémité d'arc.
     * <ul>
     * <li>NB: la topologie arcs/noeuds de la carte en sortie est instanciée.
     * <li>NB: les populations d'arcs et de noeuds sont indexées pendant la
     * méthode, si cela n'avait pas déjà été fait avant. Les paramètres de ces
     * index sont: 20x20 cases pour les noeuds, ~50 arcs par case pour les arcs.
     * Si cela ne convient pas: instancier les topologies avant.
     * <li>NB: les "correspondants" des arcs et noeuds suivent le découpage, de
     * même que l'attribut orientation.
     * <li>MAIS ATTENTION:
     * <ul>
     * <li>- les liens vers des groupes ne suivent pas.
     * <li>- les attributs/liens particuliers (cas où les arcs proviennent d'une
     * carteTopo spécialisée) ne suivent pas non plus
     * <li>- la topologie des faces est détruite aussi
     * </ul>
     * </ul>
     *
     * @param tolerance
     *            paramètre de tolérance sur la localisation des noeuds:
     *            deux extrémités d'arc à moins de cette distance sont
     *            considérées superposées
     *            (utilisé lors de la construction de la topologie arcs/noeuds).
     *            Ce paramètre peut être nul.
     */
    @SuppressWarnings("unchecked")
    public void rendPlanaire(double tolerance) {
        List<FT_Feature> dejaTraites = new ArrayList<FT_Feature>();
        List<Arc> arcsEnleves = new ArrayList<Arc>();
        // si pas d'arc, c'est planaire
        if (this.getPopArcs().isEmpty()) { return; }
        // initialisation de l'index des arcs au besoin
        if (!this.getPopArcs().hasSpatialIndex()) {
            this.getPopArcs().initSpatialIndex(Tiling.class, true);
        }
        fireActionPerformed(new ActionEvent(
                this,
                0,
                I18N.getString("CarteTopo.PlanarGraphCreation"), this.getPopArcs().size())); //$NON-NLS-1$
        for (int indexArc = 0; indexArc < this.getPopArcs().size(); indexArc++) {
            Arc arc = this.getPopArcs().get(indexArc);
            if (logger.isDebugEnabled()) {
                logger.debug(I18N.getString("CarteTopo.Handling") + arc); //$NON-NLS-1$
            }
            if (arcsEnleves.contains(arc) || dejaTraites.contains(arc)) {
                continue;
            }
            // les arcs qui croisent l'arc courant
            // Optimisation et blindage pour tous les cas non garanti (Seb)
            Collection<Arc> selection = this.getPopArcs().select(
                    arc.getGeometrie());
            // on enlève l'arc courant et les arcs déjà enlevés
            selection.remove(arc);
            selection.removeAll(arcsEnleves);
            List<Arc> listeInter = new ArrayList<Arc>();
            // On construit un multipoint contenant les extrémités de l'arc
            // courant
            GM_MultiPoint frontiereArc = new GM_MultiPoint();
            frontiereArc.add(new GM_Point(arc.getGeometrie().startPoint()));
            frontiereArc.add(new GM_Point(arc.getGeometrie().endPoint()));
            // pour chaque arc qui intersecte l'arc courant
            for (Arc arcSel : selection) {
                // On construit un multipoint contenant les extrémités de l'arc
                // de la sélection
                GM_MultiPoint frontiereArcSel = new GM_MultiPoint();
                frontiereArcSel.add(new GM_Point(arcSel.getGeometrie()
                        .startPoint()));
                frontiereArcSel.add(new GM_Point(arcSel.getGeometrie()
                        .endPoint()));
                // on calcule l'intersection de l'arc courant avec l'arc de la
                // sélection
                GM_Object intersection = arcSel.getGeometrie().intersection(
                        arc.getGeometrie());
                /*
                 * //modif Seb: tentative d'accélération : buggé mais je ne
                 * trouve pas pourquoi
                 * if (intersection instanceof GM_Point ) {
                 * if ( Operateurs.superposes(ptArcIni, (GM_Point)intersection)
                 * ||
                 * Operateurs.superposes(ptArcFin, (GM_Point)intersection) ) {
                 * if ( Operateurs.superposes(ptArcSelIni,
                 * (GM_Point)intersection) ||
                 * Operateurs.superposes(ptArcSelFin, (GM_Point)intersection) )
                 * continue;
                 * }
                 * listeInter.add(arcSel);
                 * continue;
                 * }
                 */
                // si l'intersection trouvée fait partie des extrémités des 2
                // arcs, alors on passe à l'arc suivant
                if (frontiereArc.contains(intersection)
                        && frontiereArcSel.contains(intersection)) {
                    continue;
                }
                // on a une intersection ailleurs que sur une extrémité
                listeInter.add(arcSel);
            }
            if (listeInter.size() == 0) {
                fireActionPerformed(new ActionEvent(this, 1, I18N
                        .getString("CarteTopo.EdgeHandled"), indexArc + 1)); //$NON-NLS-1$
                continue; // pas d'intersection avec cet arc
            }
            // on découpe tout
            GM_Object nodedLineStrings = arc.getGeometrie();
            for (Arc a : listeInter) {
                if (a.getGeometrie().isEmpty()) {
                    logger
                            .error(I18N
                                    .getString("CarteTopo.EmptyGeometryEdge") + a.getGeometrie().toString()); //$NON-NLS-1$
                } else {
                    nodedLineStrings = nodedLineStrings.union(a.getGeometrie());
                }
            }
            listeInter.add(arc); // on le rajoute pour la suite
            if (nodedLineStrings instanceof GM_LineString) {
                boolean toutesEgales = true;
                for (Arc arcSel : listeInter) {
                    toutesEgales = arcSel.getGeometrie().equals(
                            nodedLineStrings)
                            && toutesEgales;
                }
                if (toutesEgales) {
                    if (logger.isDebugEnabled())
                        logger.debug(I18N
                                .getString("CarteTopo.EqualGeometries")); //$NON-NLS-1$
                    Arc arcNouveau = this.getPopArcs().nouvelElement(
                            nodedLineStrings);
                    boolean premierArc = true;
                    for (Arc arcSel : listeInter) {
                        arcNouveau.addAllCorrespondants(arcSel
                                .getCorrespondants());
                        arcsEnleves.add(arcSel);
                        if (premierArc) {
                            // on affecte la valeur initiale de l'orientation
                            // avec le premier arc rencontré
                            arcNouveau.setOrientation(arcSel.getOrientation());
                            premierArc = false;
                        } else {
                            // ensuite, si la valeur diffère de la valeur
                            // initale, on met l'orientation dans les deux sens
                            if (arcNouveau.getOrientation() != arcSel
                                    .getOrientation())
                                arcNouveau.setOrientation(2);
                        }
                    }
                    // le nouvel arc possède la même géométrie que l'arc
                    // initial, pas la peine de revenir
                    dejaTraites.add(arcNouveau);
                } else {
                    logger
                            .error(I18N
                                    .getString("CarteTopo.PlanarGraphProblem")); //$NON-NLS-1$
                    logger.error(I18N
                            .getString("CarteTopo.IntersectionProblem")); //$NON-NLS-1$
                    logger.error(I18N.getString("CarteTopo.EdgeProblem") + arc); //$NON-NLS-1$
                    logger.error(I18N
                            .getString("CarteTopo.UnionWithSeveralEdges")); //$NON-NLS-1$
                    for (Arc a : listeInter) {
                        logger.error(this.getPopArcs().contains(a) + " - " + a); //$NON-NLS-1$
                        logger.error(a.getNoeudIni());
                        logger.error(a.getNoeudFin());
                        // if ( a.getGeometrie().isEmpty())
                        // logger.error("la géométrie de l'arc est vide "+
                        // a.getGeometrie().toString());
                        // else logger.error(" "+ a.getGeometrie().toString());
                    }
                }
                fireActionPerformed(new ActionEvent(this, 1, I18N
                        .getString("CarteTopo.EdgeHandled"), indexArc + 1)); //$NON-NLS-1$
                continue;
            }
            if (nodedLineStrings instanceof GM_MultiCurve) { // cas où il faut
                                                             // découper
                // 1: on rajoute les morceaux d'arcs découpés
                for (GM_LineString ligneDecoupe : (GM_MultiCurve<GM_LineString>) nodedLineStrings) {
                    Arc arcNouveau = this.getPopArcs().nouvelElement(
                            ligneDecoupe);
                    // on recherche à quel(s) arc(s) initial appartient chaque
                    // bout découpé
                    for (Arc arcSel : listeInter) {
                        // on devrait mettre ==0 ci-dessous, mais pour gérer les
                        // erreurs d'arrondi on met <0.01
                        if (Distances.premiereComposanteHausdorff(ligneDecoupe,
                                arcSel.getGeometrie()) < 0.01) {
                            // on appartient à lui
                            arcNouveau.addAllCorrespondants(arcSel
                                    .getCorrespondants());
                            arcNouveau.setOrientation(arcSel.getOrientation());
                            // si on appartient à l'arc initial, pas la peine de
                            // revenir
                            if (arcSel == arc)
                                dejaTraites.add(arcNouveau);
                        }
                    }
                    if (logger.isDebugEnabled()) {
                        logger
                                .debug(I18N.getString("CarteTopo.NewEdge") + arcNouveau); //$NON-NLS-1$
                    }
                }
                // 2: on virera les arcs initiaux qui ont été découpés
                for (Arc arcSel : listeInter) {
                    if (logger.isDebugEnabled())
                        logger
                                .debug(I18N
                                        .getString("CarteTopo.IntersectionFound") + arcSel); //$NON-NLS-1$
                    arcSel.setCorrespondants(new ArrayList<FT_Feature>());
                    arcsEnleves.add(arcSel);
                }
                fireActionPerformed(new ActionEvent(this, 1, I18N
                        .getString("CarteTopo.EdgeHandled"), indexArc + 1)); //$NON-NLS-1$
                continue;
            }
            // cas imprévu: OUPS
            logger.error(I18N.getString("CarteTopo.PlanarGraphProblem")); //$NON-NLS-1$
            logger
                    .error(I18N.getString("CarteTopo.UnionBug") + nodedLineStrings.getClass()); //$NON-NLS-1$
            logger
                    .error(I18N.getString("CarteTopo.EdgeProblem") + arc.getGeom().coord()); //$NON-NLS-1$
        }

        this.enleveArcs(arcsEnleves);
        // On construit les nouveaux noeuds éventuels et la topologie
        // arcs/noeuds
        this.getPopNoeuds().setElements(new ArrayList<Noeud>());
        this.creeNoeudsManquants(tolerance);

        /**
         * vérification des arcs qui s'intersectent presque à moins de tolérance
         * FIXME ATTENTION : ce bout de code est à nettoyer et à corriger
         * éventuellement.
         */
        for (Noeud noeud : this.getPopNoeuds()) {
            if (noeud.arcs().size() == 1) {
                Collection<Arc> arcs = this.getPopArcs().select(
                        noeud.getGeom().buffer(tolerance));
                arcs.removeAll(noeud.arcs());
                if (!arcs.isEmpty()) {
                    if (logger.isDebugEnabled())
                        logger
                                .debug(I18N.getString("CarteTopo.HandlingNode") + noeud); //$NON-NLS-1$
                    if (logger.isDebugEnabled())
                        logger
                                .debug(I18N
                                        .getString("CarteTopo.NumberOfNeighborNodes") + this.getPopNoeuds().select(noeud.getGeom().buffer(tolerance)).size()); //$NON-NLS-1$
                    if (logger.isDebugEnabled())
                        logger
                                .debug(I18N
                                        .getString("CarteTopo.NumberOfNeighborEdges") + arcs.size()); //$NON-NLS-1$
                    Arc arc = arcs.iterator().next();
                    if (logger.isDebugEnabled())
                        logger
                                .debug(I18N
                                        .getString("CarteTopo.EdgeSplitting") + arc); //$NON-NLS-1$
                    arc.projeteEtDecoupe(noeud);
                }
            }
        }
    }

    /**
     * Fusionne en un seul noeud, tous les noeuds proches de moins de
     * "tolerance"
     * Les correspondants suivent, la topologie arcs/noeuds aussi.
     * NB: les petits arcs qui n'ont plus de sens sont aussi éliminés.
     * Plus précisément ce sont ceux qui partent et arrivent sur un même
     * nouveau noeud créé, et restant à moins de "tolerance" de ce nouveau noeud
     * Un index spatial (dallage) est créé si cela n'avait pas été fait avant,
     * mais il est toujours conseillé de le faire en dehors de cette méthode,
     * pour controler la taille du dallage.
     *
     * @param tolerance
     *            tolérance en dessous de laquelle les noeuds sont fusionés
     */
    public void fusionNoeuds(double tolerance) {
        List<Noeud> aEnlever = new ArrayList<Noeud>();
        // initialisation de l'index spatial sur les arcs avec mise à jour
        // automatique
        if (!this.getPopArcs().hasSpatialIndex())
            this.getPopArcs().initSpatialIndex(Tiling.class, true);

        fireActionPerformed(new ActionEvent(this, 0, I18N
                .getString("CarteTopo.NodesFusion"), this.getPopNoeuds().size())); //$NON-NLS-1$

        for (int index = 0; index < this.getPopNoeuds().size(); index++) {
            Noeud noeud = this.getPopNoeuds().get(index);
            // On cherche les noeuds voisins
            Collection<Noeud> noeudsProches = this.getPopNoeuds().select(
                    noeud.getGeometrie(), tolerance);
            // On enlève les noeuds déjà sélectionnés comme à enlever
            noeudsProches.removeAll(aEnlever);
            if (noeudsProches.size() < 2)
                continue;// s'il n'y a qu'un seul noeud, c'est le noeud courant
            // Si il y a des voisins, on crée un nouveau noeud
            GM_MultiPoint points = new GM_MultiPoint();
            for (Object o : noeudsProches)
                points.add(((Noeud) o).getGeometrie());
            // on calcule le centroïde de tous les voisins
            GM_Point centroide = new GM_Point(points.centroid());
            // On crée un nouveau noeud dont la géométrie est le centroïde
            // calculé
            Noeud nouveauNoeud = this.getPopNoeuds().nouvelElement(centroide);
            // On raccroche tous les arcs à ce nouveau noeud
            for (Noeud noeudProche : noeudsProches) {
                // on associe le nouveau noeud aux correspondants du noeud
                nouveauNoeud.addAllCorrespondants(noeudProche
                        .getCorrespondants());
                noeudProche.setCorrespondants(new ArrayList<FT_Feature>());
                // on ajoute le noeud à la liste des noeuds à enlever
                if (!aEnlever.contains(noeudProche))
                    aEnlever.add(noeudProche);
                // modification de chaque arc du noeud proche à bouger
                for (Arc arc : noeudProche.arcs()) {
                    if (arc.getNoeudIni().equals(noeudProche)) {
                        arc.setNoeudIni(nouveauNoeud);
                        arc.getGeometrie().coord().set(0,
                                nouveauNoeud.getGeometrie().getPosition());
                    }
                    if (arc.getNoeudFin().equals(noeudProche)) {
                        arc.setNoeudFin(nouveauNoeud);
                        int fin = arc.getGeometrie().coord().size() - 1;
                        arc.getGeometrie().coord().set(fin,
                                nouveauNoeud.getGeometrie().getPosition());
                    }
                }
                // On enlève les arcs qui n'ont plus lieu d'être
                // (tout petit autour du nouveau noeud)
                List<Arc> listeArcsAEnlever = new ArrayList<Arc>();
                for (Arc arc : noeudProche.arcs()) {
                    if (arc.getNoeudIni() == nouveauNoeud
                            && arc.getNoeudFin() == nouveauNoeud) {
                        if ((Distances.hausdorff(arc.getGeometrie(),
                                noeudProche.getGeometrie()) <= tolerance)
                                || (arc.getGeometrie().length() < tolerance)) {
                            nouveauNoeud.addAllCorrespondants(arc
                                    .getCorrespondants());
                            arc.setCorrespondants(new ArrayList<FT_Feature>());
                            arc.setNoeudIni(null);
                            arc.setNoeudFin(null);
                            if (!listeArcsAEnlever.contains(arc))
                                listeArcsAEnlever.add(arc);
                        }
                    }
                }
                enleveArcs(listeArcsAEnlever);
            }
            fireActionPerformed(new ActionEvent(this, 1, I18N
                    .getString("CarteTopo.NodeFused"), index + 1)); //$NON-NLS-1$
        }

        // on enleve tous les anciens noeuds
        if (!aEnlever.isEmpty() && !this.getPopNoeuds().removeAll(aEnlever))
            logger.warn(I18N.getString("CarteTopo.RemovalFailed")); //$NON-NLS-1$
        List<Arc> listeArcsAEnlever = new ArrayList<Arc>();
        // suppression des arcs trop petits
        fireActionPerformed(new ActionEvent(
                this,
                2,
                I18N.getString("CarteTopo.SmallEdgesDeletion"), this.getPopArcs().size())); //$NON-NLS-1$
        int index = 0;
        for (Arc arc : this.getListeArcs()) {
            if (arc.getNoeudIni() == arc.getNoeudFin()
                    && arc.getNoeudIni() != null) {
                if ((Distances.hausdorff(arc.getGeometrie(), arc.getNoeudIni()
                        .getGeometrie()) <= tolerance)
                        || (arc.getGeometrie().length() < tolerance)) {
                    arc.getNoeudIni().addAllCorrespondants(
                            arc.getCorrespondants());
                    arc.setCorrespondants(new ArrayList<FT_Feature>());
                    arc.setNoeudIni(null);
                    arc.setNoeudFin(null);
                    if (!listeArcsAEnlever.contains(arc))
                        listeArcsAEnlever.add(arc);
                }
            } else {
                if (arc.getNoeudIni() == null || arc.getNoeudFin() == null) {
                    logger.error(I18N.getString("CarteTopo.InvalidEdge") + arc); //$NON-NLS-1$
                }
            }
            fireActionPerformed(new ActionEvent(this, 3, I18N
                    .getString("CarteTopo.EdgeHandled"), ++index)); //$NON-NLS-1$
        }
        fireActionPerformed(new ActionEvent(this, 4, I18N
                .getString("CarteTopo.NodesFused"))); //$NON-NLS-1$
        enleveArcs(listeArcsAEnlever);
    }

    /**
     * Fusionne en un seul noeud, tous les noeuds contenu dans une même
     * surface de la population de surfaces passée en paramètre.
     * Les correspondants suivent, la topologie arcs/noeuds aussi.
     * NB: les petits arcs qui n'ont plus de sens sont aussi éliminés.
     * Plus précisément ce sont ceux qui partent et arrivent sur un même
     * nouveau noeud créé, et restant dans la surface de fusion.
     * Un index spatial (dallage) est créé si cela n'avait pas été fait avant,
     * mais il est toujours conseillé de le faire en dehors de cette méthode,
     * pour controler la taille du dallage.
     *
     * @param popSurfaces
     *            population contenant les surface à fusionner en un seul noeud
     */
    public void fusionNoeuds(Population<? extends FT_Feature> popSurfaces) {
        Iterator<Noeud> itNoeudsProches;
        Noeud noeud, nouveauNoeud, noeudProche;
        Arc arc;
        List<FT_Feature> aEnlever = new ArrayList<FT_Feature>();
        Collection<Noeud> noeudsProches;
        List<Arc> arcsModifies;
        if (!this.getPopNoeuds().hasSpatialIndex()) {
            this.getPopNoeuds().initSpatialIndex(Tiling.class, true);
        }
        for (FT_Feature surf : popSurfaces) {
            noeudsProches = this.getPopNoeuds().select(surf.getGeom());
            noeudsProches.removeAll(aEnlever);
            if (noeudsProches.size() < 2) {
                continue;
            }
            // Si il y a plusieurs noeuds dans la surface, on crée un nouveau
            // noeud
            GM_MultiPoint points = new GM_MultiPoint();
            itNoeudsProches = noeudsProches.iterator();
            while (itNoeudsProches.hasNext()) {
                noeudProche = itNoeudsProches.next();
                points.add(noeudProche.getGeometrie());
            }
            GM_Point centroide = new GM_Point(points.centroid());
            nouveauNoeud = this.getPopNoeuds().nouvelElement();
            nouveauNoeud.setGeometrie(centroide);

            // On raccroche tous les arcs à ce nouveau noeud
            arcsModifies = new ArrayList<Arc>();
            itNoeudsProches = noeudsProches.iterator();
            while (itNoeudsProches.hasNext()) {
                noeudProche = itNoeudsProches.next();
                nouveauNoeud.addAllCorrespondants(noeudProche
                        .getCorrespondants());
                noeudProche.setCorrespondants(new ArrayList<FT_Feature>());
                aEnlever.add(noeudProche);
                // modification de chaque arc du noeud proche à bouger
                for (Object a : noeudProche.arcs()) {
                    arc = (Arc) a;
                    arcsModifies.add(arc);
                    if (arc.getNoeudIni() == noeudProche) {
                        arc.setNoeudIni(nouveauNoeud);
                        arc.getGeometrie().setControlPoint(0,
                                nouveauNoeud.getGeometrie().getPosition());
                    }
                    if (arc.getNoeudFin() == noeudProche) {
                        arc.setNoeudFin(nouveauNoeud);
                        int fin = arc.getGeometrie().coord().size() - 1;
                        arc.getGeometrie().setControlPoint(fin,
                                nouveauNoeud.getGeometrie().getPosition());
                    }
                }
                // On enlève les arcs qui n'ont plus lieu d'être
                // (tout petit autour du nouveau noeud)
                for (Object a : arcsModifies) {
                    arc = (Arc) a;
                    if (arc.getNoeudIni() == nouveauNoeud
                            && arc.getNoeudFin() == nouveauNoeud) {
                        if (surf.getGeom().contains(arc.getGeometrie())) {
                            nouveauNoeud.addAllCorrespondants(arc
                                    .getCorrespondants());
                            arc.setNoeudIni(null);
                            arc.setNoeudFin(null);
                            this.getPopArcs().remove(arc);
                        }
                    }
                }
            }
        }
        // on enleve tous les anciens noeuds
        Iterator<FT_Feature> itAEnlever = aEnlever.iterator();
        while (itAEnlever.hasNext()) {
            noeud = (Noeud) itAEnlever.next();
            this.getPopNoeuds().remove(noeud);
        }
    }

    /**
     * découpe la carte topo this en fonction des noeuds d'une autre carte topo
     * (ct).
     * En détail:
     * Pour chaque noeud N de la carte topo en entrée,
     * on prend chaque arc de this qui en est proche (c'est-à-dire à moins de
     * distanceMaxNoeudArc).
     * Si aucune des extrémités de cet arc est à moins de
     * distanceMaxProjectionNoeud du noeud N,
     * alors on découpe l'arc en y projetant le noeud N.
     * Si impassesSeulement = true: seules les noeuds N extrémités d'impasse
     * peuvent être projetées
     * La topologie arcs/noeuds, l'orientation et les correspondants suivent.
     * Les arcs de this sont indexés au passage si cela n'avait pas été fait
     * avant.
     */
    public void projete(CarteTopo ct, double distanceMaxNoeudArc,
            double distanceMaxProjectionNoeud, boolean impassesSeulement) {
        Arc arc;
        Noeud noeud;
        Iterator<Noeud> itNoeuds = ct.getPopNoeuds().getElements().iterator();
        Iterator<Arc> itArcs;

        if (!this.getPopArcs().hasSpatialIndex()) {
            int nb = (int) Math.sqrt(this.getPopArcs().size() / 20);
            if (nb == 0)
                nb = 1;
            this.getPopArcs().initSpatialIndex(Tiling.class, true, nb);
        }

        while (itNoeuds.hasNext()) {
            noeud = itNoeuds.next();
            if (impassesSeulement && (noeud.arcs().size() != 1))
                continue;
            itArcs = this.getPopArcs().select(noeud.getGeom(),
                    distanceMaxNoeudArc).iterator();
            while (itArcs.hasNext()) {
                arc = itArcs.next();
                if (arc.getGeometrie().startPoint().distance(
                        noeud.getGeometrie().getPosition()) < distanceMaxProjectionNoeud)
                    continue;
                if (arc.getGeometrie().endPoint().distance(
                        noeud.getGeometrie().getPosition()) < distanceMaxProjectionNoeud)
                    continue;
                arc.projeteEtDecoupe(noeud.getGeometrie());
            }
        }
    }

    /**
     * découpe la carte topo this en fonction de tous les points (noeuds et
     * points intermediaires)
     * d'une autre carte topo (ct).
     * En détail:
     * Pour chaque point P de la carte topo en entrée,
     * on prend chaque arc de this qui en est proche (c'est-à-dire à moins de
     * distanceMaxNoeudArc).
     * Si aucune des extrémités de cet arc est à moins de
     * distanceMaxProjectionNoeud du noeud P,
     * alors on découpe l'arc en y projetant le noeud P.
     * La topologie arcs/noeuds, l'orientation et les correspondants suivent.
     * Les arcs de this sont indexés au passage si cela n'avait pas été fait
     * avant.
     */
    public void projeteTousLesPoints(CarteTopo ct, double distanceMaxNoeudArc,
            double distanceMaxProjectionNoeud) {
        Arc arc, arcCT;
        Iterator<Arc> itArcsCT = ct.getPopArcs().getElements().iterator();
        Iterator<Arc> itArcs;
        Iterator<DirectPosition> itPointsCT;

        if (!this.getPopArcs().hasSpatialIndex()) {
            int nb = (int) Math.sqrt(this.getPopArcs().size() / 20);
            if (nb == 0)
                nb = 1;
            this.getPopArcs().initSpatialIndex(Tiling.class, true, nb);
        }

        while (itArcsCT.hasNext()) {
            arcCT = itArcsCT.next();
            itPointsCT = arcCT.getGeometrie().coord().getList().iterator();
            while (itPointsCT.hasNext()) {
                DirectPosition dp = itPointsCT.next();
                if (arcCT.getGeometrie().startPoint().distance(dp) < distanceMaxProjectionNoeud)
                    continue;
                if (arcCT.getGeometrie().endPoint().distance(dp) < distanceMaxProjectionNoeud)
                    continue;
                itArcs = this.getPopArcs().select(dp, distanceMaxNoeudArc)
                        .iterator();
                while (itArcs.hasNext()) {
                    arc = itArcs.next();
                    if (arc.getGeometrie().startPoint().distance(dp) < distanceMaxProjectionNoeud)
                        continue;
                    if (arc.getGeometrie().endPoint().distance(dp) < distanceMaxProjectionNoeud)
                        continue;
                    arc.projeteEtDecoupe(new GM_Point(dp));
                }
            }
        }
    }

    /**
     * découpe la carte topo this en fonction d'un ensemble de points
     * (GM_Point).
     * En détail:
     * Pour chaque point en entrée,
     * on prend chaque arc de this qui en est proche (c'est-à-dire à moins de
     * distanceMaxNoeudArc).
     * Si aucune des extrémités de cet arc est à moins de
     * distanceMaxProjectionNoeud du noeud N,
     * alors on découpe l'arc en y projetant le noeud N.
     * La topologie arcs/noeuds, l'orientation et les correspondants suivent.
     */
    public void projete(List<GM_Point> pts, double distanceMaxNoeudArc,
            double distanceMaxProjectionNoeud) {
        Arc arc;
        Iterator<GM_Point> itPts = pts.iterator();
        Iterator<Arc> itArcs;
        while (itPts.hasNext()) {
            GM_Point point = itPts.next();
            itArcs = this.getPopArcs().select(point, distanceMaxNoeudArc)
                    .iterator();
            while (itArcs.hasNext()) {
                arc = itArcs.next();
                if (arc.getGeometrie().startPoint().distance(
                        point.getPosition()) < distanceMaxProjectionNoeud)
                    continue;
                if (arc.getGeometrie().endPoint().distance(point.getPosition()) < distanceMaxProjectionNoeud)
                    continue;
                arc.projeteEtDecoupe(point);
            }
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////
    // Instanciation de la topologie de faces
    // ///////////////////////////////////////////////////////////////////////////////////////////
    /**
     * crée les faces à partir d'un graphe planaire et instancie la topologie
     * face / arcs.
     * Une face est délimitée par un cycle minimal du graphe.
     * <p>
     * Le paramètre persistant spécifie si les faces créées, ainsi que la
     * topologie, sont rendus persistants. Si oui, il faut appeler cette méthode
     * dans une transaction ouverte.
     * <ul>
     * <li>NB1 : la topologie de réseau arcs/noeuds doit avoir été instanciée.
     * <li>NB2 : une face "extérieure" est créée (sa géométrie entoure le "trou"
     * de l'extérieur qu'est le réseau. Donc, dans le cas d'une topologie
     * complete arcs/faces, tous les arcs ont une face gauche et une face à
     * droite.
     * <li>NB3 : <b>ATTENTION :</b> en cas d'un réseau non connexe, une face
     * extérieure différente est crée pour chaque partie connexe !
     * <li>NB4 : Les culs de sac ont la même face à gauche et à droite, et la
     * face "longe le cul de sac";
     * <li>NB5 : méthode en théorie conçue pour les graphes planaires uniquement
     * (testée dans ce cadre uniquement). La méthode est en théorie valable pour
     * les graphes non planaires, mais les faces créées seront étranges (on ne
     * recrée pas les intersections manquantes, on les ignore). Si il existe des
     * arcs sans noeud initial ou final (topologie de réseau pas complete),
     * alors ces arcs n'ont ni face à gauche, ni face à droite
     * </ul>
     * Depuis la version 1.6 la géométrie des faces est corrigée et n'inclue
     * donc plus un aller-retour sur les culs-de-sac. Les trous sont aussi
     * ajoutés à la géométrie des faces.
     */
    public void creeTopologieFaces() {
        List<Arc> arcsDejaTraitesADroite = new ArrayList<Arc>();
        List<Arc> arcsDejaTraitesAGauche = new ArrayList<Arc>();
        List<Arc> arcsDuCycle;
        List<Boolean> orientationsArcsDuCycle;
        GM_LineString geometrieDuCycle;
        Population<Face> popFaces = this.getPopFaces();
        List<Cycle> cycles = new ArrayList<Cycle>();

        fireActionPerformed(new ActionEvent(
                this,
                0,
                I18N.getString("CarteTopo.FaceTopologyEdges"), this.getPopArcs().size())); //$NON-NLS-1$
        int iteration = 0;
        /*
         * Parcours de tous les arcs du graphe. Puis, pour chaque arc:
         * - recherche du cycle à droite et du cycle à gauche
         * - creation des faces correspondantes
         * - on note les arcs par lesquels on est déjà passé pour ne pas refaire
         * le travail
         * TODO Regrouper le traitement à droite et à gauche des arcs.
         */
        for (Arc arc : this.getPopArcs()) {
            Face face = null;
            // a droite
            if (!arcsDejaTraitesADroite.contains(arc)) {
                Cycle cycle = arc.cycle(false);
                // face = null;
                if (cycle == null) {
                    logger
                            .error(I18N.getString("CarteTopo.RightNullCycle") + arc.getId()); //$NON-NLS-1$
                    continue;
                }
                arcsDuCycle = cycle.getArcs();
                orientationsArcsDuCycle = cycle.getOrientationsArcs();
                geometrieDuCycle = cycle.getGeometrie();
                boolean multiGeometrie = false;
                boolean simple = geometrieDuCycle.isSimple();
                if (!simple) {
                    List<Cycle> cyclesGeometrieNonSimple = construireGeometrieCycleExterieur(
                            arc, false);
                    // la géométrie est corrigée
                    if (cyclesGeometrieNonSimple.size() == 1) {
                        geometrieDuCycle = cyclesGeometrieNonSimple.get(0)
                                .getGeometrie();
                    } else {
                        for (Cycle cycleCourant : cyclesGeometrieNonSimple) {
                            boolean ccw = JtsAlgorithms.isCCW(cycleCourant
                                    .getGeometrie());
                            if (ccw) {
                                cycles.add(cycleCourant);
                            } else {
                                face = popFaces.nouvelElement(new GM_Polygon(
                                        cycleCourant.getGeometrie()));
                            }
                        }
                        multiGeometrie = true;
                    }
                }
                if (!multiGeometrie) {
                    boolean ccw = JtsAlgorithms.isCCW(geometrieDuCycle);
                    if (ccw) {
                        cycles.add(new Cycle(arcsDuCycle,
                                orientationsArcsDuCycle, geometrieDuCycle,
                                false));
                    } else {
                        face = popFaces.nouvelElement(new GM_Polygon(
                                geometrieDuCycle));
                    }
                }
                // if ( persistant ) JeuDeDonnees.db.makePersistent(face);
                this.marquerCycle(arcsDuCycle, orientationsArcsDuCycle, false,
                        face, arcsDejaTraitesAGauche, arcsDejaTraitesADroite);
            }
            // a gauche
            if (!arcsDejaTraitesAGauche.contains(arc)) {
                Cycle cycle = arc.cycle(true);
                face = null;
                if (cycle == null) {
                    if (logger.isDebugEnabled())
                        logger
                                .debug(I18N
                                        .getString("CarteTopo.LeftNullCycle") + arc.getId());continue;} //$NON-NLS-1$
                arcsDuCycle = cycle.getArcs();
                orientationsArcsDuCycle = cycle.getOrientationsArcs();
                geometrieDuCycle = cycle.getGeometrie();
                boolean multiGeometrie = false;
                boolean simple = geometrieDuCycle.isSimple();
                if (!simple) {
                    List<Cycle> cyclesGeometrieNonSimple = construireGeometrieCycleExterieur(
                            arc, true);
                    // la géométrie est corrigée
                    if (cyclesGeometrieNonSimple.size() == 1) {
                        geometrieDuCycle = cyclesGeometrieNonSimple.get(0)
                                .getGeometrie();
                    } else {
                        for (Cycle cycleCourant : cyclesGeometrieNonSimple) {
                            boolean ccw = JtsAlgorithms.isCCW(cycleCourant
                                    .getGeometrie());
                            if (!ccw) {
                                cycles.add(cycleCourant);
                            } else {
                                face = popFaces.nouvelElement(new GM_Polygon(
                                        cycleCourant.getGeometrie()));
                            }
                        }
                        multiGeometrie = true;
                    }
                }
                if (!multiGeometrie) {
                    boolean ccw = JtsAlgorithms.isCCW(geometrieDuCycle);
                    if (!ccw) {
                        cycles
                                .add(new Cycle(arcsDuCycle,
                                        orientationsArcsDuCycle,
                                        geometrieDuCycle, true));
                    } else {
                        face = popFaces.nouvelElement(new GM_Polygon(
                                geometrieDuCycle));
                    }
                }
                // if ( persistant ) JeuDeDonnees.db.makePersistent(face);
                this.marquerCycle(arcsDuCycle, orientationsArcsDuCycle, true,
                        face, arcsDejaTraitesAGauche, arcsDejaTraitesADroite);
            }
            fireActionPerformed(new ActionEvent(this, 1, I18N
                    .getString("CarteTopo.FaceTopologyEdge"), iteration++)); //$NON-NLS-1$
        }
        // détection des arcs pendants ie des culs-de-sac
        for (Arc arcCourant : this.getPopArcs()) {
            if ((arcCourant.getFaceDroite() == null)
                    || (arcCourant.getFaceGauche() == null)) {
                continue;
            }
            if (arcCourant.getFaceDroite() == arcCourant.getFaceGauche()) {
                arcCourant.setPendant(true);
            }
        }
        /*
         * création de l'index spatial.
         * On n'active pas la mise à jour automatique afin que, lorsque l'on
         * ajoute la face infinie,
         * elle n'apparaisse pas dans chaque requête select.
         */
        this.getPopFaces().initSpatialIndex(Tiling.class, false);
        GM_Envelope envelope = this.getPopArcs().envelope();
        Face faceInfinie = this.getPopFaces()
                .nouvelElement(
                        new GM_Polygon(new GM_Envelope(envelope.minX() - 1,
                                envelope.maxX() + 1, envelope.minY() - 1,
                                envelope.maxY() + 1)));
        faceInfinie.setInfinite(true);
        fireActionPerformed(new ActionEvent(this, 2, I18N
                .getString("CarteTopo.FaceTopologyCycles"), cycles.size())); //$NON-NLS-1$
        iteration = 0;
        for (Cycle cycle : cycles) {
            Face face = ((cycle.isAGauche() && cycle.getOrientationsArcs().get(
                    0).booleanValue()) || (!cycle.isAGauche() && !cycle
                    .getOrientationsArcs().get(0).booleanValue())) ? cycle
                    .getArcs().get(0).getFaceGauche() : cycle.getArcs().get(0)
                    .getFaceDroite();
            if (face == null) {
                Collection<Face> selection = this.getPopFaces().select(
                        cycle.getGeometrie());
                if (selection.isEmpty()) {
                    face = faceInfinie;
                } else {
                    selection
                            .removeAll(cycle.getListeFacesInterieuresDuCycle());
                    Iterator<Face> it = selection.iterator();
                    while (it.hasNext()) {
                        if (!it.next().getGeometrie().contains(
                                cycle.getGeometrie())) {
                            it.remove();
                        }
                    }
                    if (selection.isEmpty()) {
                        face = faceInfinie;
                    } else {
                        it = selection.iterator();
                        face = it.next();
                        // s'il y a plus d'une face qui contient celle-ci
                        while (it.hasNext()) {
                            Face f = it.next();
                            // on sélectionne la plus petite
                            if (f.getGeometrie().area() < face.getGeometrie().area()) {
                                face = f;
                            }
                        }
                    }
                }
            }
            marquerCycle(cycle, face);
            // on ajoute un trou à la géométrie de la face infinie
            if (cycle.getGeometrie().sizeControlPoint() > 3) {
                GM_Ring trou = new GM_Ring(cycle.getGeometrie());
                if (!trou.coord().isEmpty()) {
                    if (face.getGeometrie().getInterior().isEmpty()) {
                        face.getGeometrie().addInterior(trou);
                    } else {
                        // union des trous
                        GM_Polygon polygonHole = new GM_Polygon(trou);
                        List<GM_Polygon> trous = new ArrayList<GM_Polygon>();
                        for (GM_Ring ring : face.getGeometrie().getInterior()) {
                            trous.add(new GM_Polygon(ring));
                        }
                        // suppression des trous existants
                        face.getGeometrie().getInterior().clear();
                        // ajout du nouveau trou à la liste
                        trous.add(polygonHole);
                        GM_Object union = JtsAlgorithms.union(trous);
                        if (union.isPolygon()) {
                            GM_Polygon polygon = (GM_Polygon) union;
                            face.getGeometrie().addInterior(polygon.getExterior());
                        } else {
                            if (union.isMultiSurface()) {
                                GM_MultiSurface<GM_Polygon> multipolygon = (GM_MultiSurface<GM_Polygon>) union;
                                for (GM_Polygon polygon : multipolygon) {
                                    face.getGeometrie().addInterior(polygon.getExterior());
                                }
                            } else {
                                logger.error(union);
                            }
                        }
                    }
                }
            }
            fireActionPerformed(new ActionEvent(this, 3, I18N
                    .getString("CarteTopo.FaceTopologyCycle"), iteration++)); //$NON-NLS-1$
        }
        // détection des arcs pendants ie des culs-de-sac de la face Infinie
        for (Arc arcCourant : faceInfinie.arcs()) {
            if ((arcCourant.getFaceDroite() == null)
                    || (arcCourant.getFaceGauche() == null)) {
                continue;
            }
            if (arcCourant.getFaceDroite() == arcCourant.getFaceGauche()) {
                arcCourant.setPendant(true);
            }
        }
        fireActionPerformed(new ActionEvent(this, 4, I18N
                .getString("CarteTopo.FaceTopologyEnd"))); //$NON-NLS-1$
        // détection des arcs pendants ie des culs-de-sac de la face Infinie
        for (Arc arcCourant : this.getPopArcs()) {
            if (arcCourant.getFaceDroite() == null) {
                arcCourant.setFaceDroite(faceInfinie);
                if (arcCourant.getFaceGauche() == faceInfinie) {
                    arcCourant.setPendant(true);
                }
            }
            if (arcCourant.getFaceGauche() == null) {
                arcCourant.setFaceGauche(faceInfinie);
                if (arcCourant.getFaceDroite() == faceInfinie) {
                    arcCourant.setPendant(true);
                }
            }
        }
    }

    /**
     * Construire la liste des géométries corrigées d'un cycle.
     *
     * @param arcInitial
     *            premier arc du cycle
     * @param aGauche
     *            vrai si ce cycle parcours son premier arc à gauche, faux sinon
     * @return la liste des géométries corrigées du cycle
     */
    public static List<Cycle> construireGeometrieCycleExterieur(Arc arcInitial,
            boolean aGauche) {
        Stack<DirectPosition> pilePoints = new Stack<DirectPosition>();
        Stack<List<DirectPosition>> pileListesPoints = new Stack<List<DirectPosition>>();
        Stack<Arc> pileArcs = new Stack<Arc>();
        Stack<Boolean> pileOrientations = new Stack<Boolean>();
        List<Object> arcOriente;
        Arc arcCourant = arcInitial;
        boolean sensEnCours = true;
        List<Cycle> listeCycles = new ArrayList<Cycle>();
        /*
         * on parcours le cycle dans le sens anti-trigonometrique,
         * jusqu'à revenir sur this en le parcourant dans le bon sens
         * (précision utile à la gestion des cul-de-sac).
         */
        while (true) {// ajout de l'arc en cours au cycle...
            if (sensEnCours) { // arc dans le bon sens
                DirectPosition premierPoint = arcCourant.getGeometrie()
                        .startPoint();
                if (pilePoints.contains(premierPoint)) {
                    boolean dangle = false;
                    int index = pilePoints.lastIndexOf(premierPoint);
                    List<DirectPosition> pointsCycle = new ArrayList<DirectPosition>();
                    for (int i = index; i < pilePoints.size(); i++) {
                        for (int j = 0; j < pileListesPoints.get(i).size(); j++) {
                            if (!pointsCycle.contains(pileListesPoints.get(i)
                                    .get(j)))
                                pointsCycle.add(pileListesPoints.get(i).get(j));
                            else {
                                /*
                                 * si le point est le même que le dernier du
                                 * cycle en construction, on ne fait rien (le
                                 * point est doublé dans la géométrie)
                                 * sinon, ça veut dire que c'est une impasse
                                 */
                                if (pointsCycle.indexOf(pileListesPoints.get(i)
                                        .get(j)) != pointsCycle.size() - 1) {
                                    dangle = true;
                                }
                            }
                        }
                    }
                    List<Arc> arcsCycle = new ArrayList<Arc>(pileArcs.subList(
                            index, pileArcs.size()));
                    List<Boolean> orientationsCycle = new ArrayList<Boolean>(
                            pileOrientations.subList(index, pileOrientations
                                    .size()));
                    while (pilePoints.size() > index && !pilePoints.isEmpty()) {
                        pilePoints.pop();
                        pileListesPoints.pop();
                        pileArcs.pop();
                        pileOrientations.pop();
                    }
                    if (pointsCycle.size() > 2 && !dangle) { // si on a plus de
                                                             // 3 points et que
                                                             // le cycle n'est
                                                             // pas marqué comme
                                                             // étant une
                                                             // impasse, on
                                                             // l'ajoute
                        pointsCycle.add(premierPoint);
                        Cycle cycle = new Cycle(
                                arcsCycle,
                                orientationsCycle,
                                new GM_LineString(
                                        AdapterFactory
                                                .to2DDirectPositionList(new DirectPositionList(
                                                        pointsCycle))), aGauche);
                        listeCycles.add(cycle);
                    }
                }
                List<DirectPosition> listePoints = new ArrayList<DirectPosition>();
                for (int i = 0; i < arcCourant.getGeometrie()
                        .sizeControlPoint() - 1; i++) {
                    listePoints.add(arcCourant.getGeometrie()
                            .getControlPoint(i));
                }
                pilePoints.add(premierPoint);
                pileListesPoints.add(listePoints);
                pileArcs.add(arcCourant);
                pileOrientations.add(new Boolean(sensEnCours));
                arcOriente = aGauche ? arcCourant.arcPrecedentFin()
                        : arcCourant.arcSuivantFin();
            } else { // arc dans le sens inverse
                DirectPosition premierPoint = arcCourant.getGeometrie()
                        .endPoint();
                if (pilePoints.contains(premierPoint)) {
                    boolean dangle = false;
                    int index = pilePoints.lastIndexOf(premierPoint);
                    List<DirectPosition> pointsCycle = new ArrayList<DirectPosition>();
                    for (int i = index; i < pilePoints.size(); i++) {
                        for (int j = 0; j < pileListesPoints.get(i).size(); j++)
                            if (!pointsCycle.contains(pileListesPoints.get(i)
                                    .get(j)))
                                pointsCycle.add(pileListesPoints.get(i).get(j));
                            else {
                                /*
                                 * si le point est le même que le dernier du
                                 * cycle en construction, on ne fait rien (le
                                 * point est doublé dans la géométrie)
                                 * sinon, ça veut dire que c'est une impasse
                                 */
                                if (pointsCycle.indexOf(pileListesPoints.get(i)
                                        .get(j)) != pointsCycle.size() - 1) {
                                    dangle = true;
                                }
                            }
                    }
                    List<Arc> arcsCycle = new ArrayList<Arc>(pileArcs.subList(
                            index, pileArcs.size()));
                    List<Boolean> orientationsCycle = new ArrayList<Boolean>(
                            pileOrientations.subList(index, pileOrientations
                                    .size()));
                    while (pilePoints.size() > index && !pilePoints.isEmpty()) {
                        pilePoints.pop();
                        pileListesPoints.pop();
                        pileArcs.pop();
                        pileOrientations.pop();
                    }

                    if (pointsCycle.size() > 2 && !dangle) {// si on a plus de 3
                                                            // points et que le
                                                            // cycle n'est pas
                                                            // marqué comme
                                                            // étant une
                                                            // impasse, on
                                                            // l'ajoute
                        pointsCycle.add(premierPoint);
                        Cycle cycle = new Cycle(
                                arcsCycle,
                                orientationsCycle,
                                new GM_LineString(
                                        AdapterFactory
                                                .to2DDirectPositionList(new DirectPositionList(
                                                        pointsCycle))), aGauche);
                        listeCycles.add(cycle);
                    }
                }
                List<DirectPosition> listePoints = new ArrayList<DirectPosition>();
                for (int i = arcCourant.getGeometrie().sizeControlPoint() - 1; i > 0; i--) {
                    listePoints.add(arcCourant.getGeometrie()
                            .getControlPoint(i));
                }
                pilePoints.add(premierPoint);
                pileListesPoints.add(listePoints);
                pileArcs.add(arcCourant);
                pileOrientations.add(new Boolean(sensEnCours));
                arcOriente = aGauche ? arcCourant.arcPrecedentDebut()
                        : arcCourant.arcSuivantDebut();
            }
            if (arcOriente == null) {
                logger.error(I18N.getString("CarteTopo.Error")); //$NON-NLS-1$
                return null;
            }
            // au suivant...
            arcCourant = (Arc) arcOriente.get(0); // l'arc
            sensEnCours = !((Boolean) arcOriente.get(1)).booleanValue(); // le
                                                                         // sens
                                                                         // de
                                                                         // l'arc
                                                                         // par
                                                                         // rapport
                                                                         // au
                                                                         // cycle
            // c'est fini ?
            if (arcCourant == arcInitial && sensEnCours) { break; }
        }
        if (pilePoints.isEmpty()) {
            logger.error(I18N.getString("CarteTopo.EmptyBoundary")); //$NON-NLS-1$
            return null;
        }
        // ajout du dernier point pour finir la boucle du polygone
        boolean dangle = false;
        List<DirectPosition> pointsCycle = new ArrayList<DirectPosition>();
        for (int i = 0; i < pilePoints.size(); i++) {
            for (int j = 0; j < pileListesPoints.get(i).size(); j++)
                if (!pointsCycle.contains(pileListesPoints.get(i).get(j)))
                    pointsCycle.add(pileListesPoints.get(i).get(j));
                else {
                    /*
                     * si le point est le même que le dernier du cycle en
                     * construction, on ne fait rien (le point est doublé dans
                     * la géométrie)
                     * sinon, ça veut dire que c'est une impasse
                     */
                    if (pointsCycle.indexOf(pileListesPoints.get(i).get(j)) != pointsCycle
                            .size() - 1) {
                        dangle = true;
                    }
                }
        }
        List<Arc> arcsCycle = new ArrayList<Arc>(pileArcs.subList(0, pileArcs
                .size()));
        List<Boolean> orientationsCycle = new ArrayList<Boolean>(
                pileOrientations.subList(0, pileOrientations.size()));
        pointsCycle.add(pointsCycle.get(0));
        if (pointsCycle.size() <= 3 || dangle) {
            return listeCycles;
        }
        Cycle cycle = new Cycle(arcsCycle, orientationsCycle,
                new GM_LineString(AdapterFactory
                        .to2DDirectPositionList(new DirectPositionList(
                                pointsCycle))), aGauche);
        listeCycles.add(cycle);
        return listeCycles;
    }

    /**
     * détruit les relations topologique d'une face avec tous ses arcs
     * entourants
     *
     * @param face
     *            face dont la topologie doit être vidée
     */
    public void videTopologieFace(Face face) {
        for (Arc arc : face.arcs()) {
            arc.setFaceDroite(null);
            arc.setFaceGauche(null);
        }
    }

    /**
     * Ajoute des arcs et des noeuds à la carteTopo this qui ne contient que des
     * faces.
     * Ces arcs sont les arcs entourant les faces.
     * Les relations topologiques arcs/noeuds/surfaces sont instanciées au
     * passage.
     * Les trous sont gérés.
     * Les faces en entrée peuvent avoir une orientation quelconque (direct),
     * cela est géré.
     * Par contre, on ne s'appuie que sur les points intermédiaires existants
     * dans les polygones des faces :
     * les relations topologiques sont donc bien gérés uniquement si les
     * polygones ont des géométrie "compatibles".
     *
     * @param filtrageNoeudsSimples
     *            Si ce paramètre est égal à false, alors on crée un arc et deux
     *            noeuds
     *            pour chaque segment reliant des points intermédiaires des
     *            surfaces.
     *            Si ce paramètre est égal à true, alors on fusionne les arcs et
     *            on ne retient
     *            que les noeuds qui ont 3 arcs incidents ou qui servent de
     *            point initial/final à une face.
     */
    public void ajouteArcsEtNoeudsAuxFaces(boolean filtrageNoeudsSimples) {
        DirectPosition pt1, pt2;
        Iterator<DirectPosition> itPts;
        boolean sensDirect;

        // On crée un arc pour chaque segment reliant deux points intermédiaires
        // d'une surface
        // Pour deux faces adjacentes, on duplique ces arcs. On fait le ménage
        // après.
        Iterator<Face> itFaces = this.getPopFaces().getElements().iterator();
        while (itFaces.hasNext()) {
            Face face = itFaces.next();
            GM_Polygon geomFace = face.getGeometrie();
            // gestion du contour
            DirectPositionList ptsDeLaSurface = geomFace.exteriorCoord();
            sensDirect = Operateurs.sensDirect(ptsDeLaSurface);
            itPts = ptsDeLaSurface.getList().iterator();
            pt1 = itPts.next();
            while (itPts.hasNext()) {
                pt2 = itPts.next();
                Arc arc = this.getPopArcs().nouvelElement();
                GM_LineString segment = new GM_LineString();
                segment.addControlPoint(pt1);
                segment.addControlPoint(pt2);
                arc.setGeom(segment);
                if (sensDirect)
                    arc.setFaceGauche(face);
                else
                    arc.setFaceDroite(face);
                pt1 = pt2;
            }
            // gestion des trous
            Iterator<GM_Ring> itTrous = geomFace.getInterior().iterator();
            while (itTrous.hasNext()) {
                GM_Ring trou = itTrous.next();
                DirectPositionList geomTrou = trou.getPrimitive().coord();
                sensDirect = Operateurs.sensDirect(geomTrou);
                itPts = geomTrou.getList().iterator();
                pt1 = itPts.next();
                while (itPts.hasNext()) {
                    pt2 = itPts.next();
                    Arc arc = this.getPopArcs().nouvelElement();
                    GM_LineString segment = new GM_LineString();
                    segment.addControlPoint(pt1);
                    segment.addControlPoint(pt2);
                    arc.setGeom(segment);
                    if (sensDirect)
                        arc.setFaceDroite(face);
                    else
                        arc.setFaceGauche(face);
                    pt1 = pt2;
                }
            }
        }

        // indexation spatiale des arcs crées
        // on crée un dallage avec en moyenne 20 objets par case
        FT_FeatureCollection<Arc> arcsNonTraites = new FT_FeatureCollection<Arc>(
                this.getPopArcs().getElements());
        int nb = (int) Math.sqrt(arcsNonTraites.size() / 20);
        if (nb == 0)
            nb = 1;
        arcsNonTraites.initSpatialIndex(Tiling.class, true, nb);

        // filtrage des arcs en double dus aux surfaces adjacentes
        List<Arc> arcsAEnlever = new ArrayList<Arc>();
        Iterator<Arc> itArcs = this.getPopArcs().getElements().iterator();
        while (itArcs.hasNext()) {
            Arc arc = itArcs.next();
            if (!arcsNonTraites.contains(arc))
                continue;
            arcsNonTraites.remove(arc);
            Collection<Arc> arcsProches = arcsNonTraites.select(arc
                    .getGeometrie().startPoint(), 0);
            Iterator<Arc> itArcsProches = arcsProches.iterator();
            while (itArcsProches.hasNext()) {
                Arc arc2 = itArcsProches.next();
                if (arc2.getGeometrie().startPoint().equals(
                        arc.getGeometrie().startPoint(), 0)
                        && arc2.getGeometrie().endPoint().equals(
                                arc.getGeometrie().endPoint(), 0)) {
                    arcsAEnlever.add(arc2);
                    arcsNonTraites.remove(arc2);
                    if (arc2.getFaceDroite() != null)
                        arc.setFaceDroite(arc2.getFaceDroite());
                    if (arc2.getFaceGauche() != null)
                        arc.setFaceGauche(arc2.getFaceGauche());
                }
                if (arc2.getGeometrie().startPoint().equals(
                        arc.getGeometrie().endPoint(), 0)
                        && arc2.getGeometrie().endPoint().equals(
                                arc.getGeometrie().startPoint(), 0)) {
                    arcsAEnlever.add(arc2);
                    arcsNonTraites.remove(arc2);
                    if (arc2.getFaceDroite() != null)
                        arc.setFaceGauche(arc2.getFaceDroite());
                    if (arc2.getFaceGauche() != null)
                        arc.setFaceDroite(arc2.getFaceGauche());
                }
            }
        }
        this.getPopArcs().removeAll(arcsAEnlever);
        // ajout des noeuds et des relations topologiqes arc/noeud
        this.creeNoeudsManquants(0);
        // filtrage de tous les noeuds simples (degré=2)
        if (filtrageNoeudsSimples)
            this.filtreNoeudsSimples();
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////
    // Pour les calculs de plus court chemin
    // ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Initialise le poids de chaque arc comme étant égal à sa longueur;
     * NB: utile uniquement aux plus courts chemins
     */
    public void initialisePoids() {
        for (Arc arc : this.getPopArcs()) {
            if (arc.getGeometrie() == null)
                arc.setPoids(0);
            arc.setPoids(arc.longueur());
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////
    // IMPORT: remplissage de la carte topo à partir de Features
    // ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Charge en mémoire les éléments de la classe 'nomClasseGeo' et remplit
     * 'this' avec des correspondants de ces éléments.
     *
     * @param nomClasseGeo
     *            nom de la classe des éléments à importer
     */
    public void importClasseGeo(String nomClasseGeo) {
        Chargeur.importClasseGeo(nomClasseGeo, this);
    }

    /**
     * Remplit 'this' avec des correspondants des éléments de 'listeFeature'.
     *
     * @param listeFeatures
     *            liste des éléments à importer
     */
    public void importClasseGeo(FT_FeatureCollection<?> listeFeatures) {
        Chargeur.importClasseGeo(listeFeatures, this);
    }

    /**
     * Remplit 'this' avec des correspondants des éléments de 'listeFeature'.
     * Cette version de la méthode autorise la conversion des données en 2D.
     *
     * @param listeFeatures
     *            liste des éléments à importer
     * @param is2d
     *            si vrai, alors convertir les géométries en 2d
     */
    public void importClasseGeo(FT_FeatureCollection<?> listeFeatures,
            boolean is2d) {
        Chargeur.importClasseGeo(listeFeatures, this, is2d);
    }

    /**
     * Remplit 'this' avec des correspondants des éléments de 'listeFeature'.
     * Seuls les points des éléments sont importés comme noeuds de la carte.
     * @param listeFeatures
     *            liste des éléments à importer
     */
    public void importAsNodes(Collection<? extends FT_Feature> listeFeatures) {
        Chargeur.importAsNodes(listeFeatures, this);
    }

    /**
     * Remplit 'this' avec des correspondants des éléments de 'listeFeature'.
     * Seuls les points des éléments sont importés comme noeuds de la carte.
     * @param listeFeatures
     *            liste des éléments à importer
     */
    public void importAsNodes(FT_FeatureCollection<?> listeFeatures) {
        Chargeur.importAsNodes(listeFeatures.getElements(), this);
    }

    /**
     * Affecter la face passée en paramètre aux arcs du cycle
     *
     * @param cycle
     *            cycle à parcourir afin d'affecter une face à ses arcs
     * @param face
     *            face à affecter aux arcs
     */
    public void marquerCycle(Cycle cycle, Face face) {
        this.marquerCycle(cycle.getArcs(), cycle.getOrientationsArcs(), cycle
                .isAGauche(), face, null, null);
    }

    /**
     * Affecter la face passée en paramètre aux arcs parcourus
     *
     * @param arcs
     *            arcs à parcourir
     * @param orientations
     *            orientations respectives des arcs
     * @param aGauche
     *            vrai si le premier arc est parcourus par la gauche, faux s'il
     *            est parcouru par la droite
     * @param face
     *            face à affecter aux arcs (du côté déterminé par l'orientation
     *            et la valeur de aGauche)
     * @param arcsDejaTraitesAGauche
     *            liste des arcs déjà traités à gauche à maintenir
     * @param arcsDejaTraitesADroite
     *            liste des arcs déjà traités à droite à maintenir
     */
    public void marquerCycle(List<Arc> arcs, List<Boolean> orientations,
            boolean aGauche, Face face, List<Arc> arcsDejaTraitesAGauche,
            List<Arc> arcsDejaTraitesADroite) {
        Iterator<Arc> itArcsCycle = arcs.iterator();
        Iterator<Boolean> itOrientations = orientations.iterator();
        while (itArcsCycle.hasNext()) {
            Arc arcCycle = itArcsCycle.next();
            Boolean orientationOk = itOrientations.next();
            if ((orientationOk.booleanValue() && !aGauche)
                    || (!orientationOk.booleanValue() && aGauche)) {
                arcCycle.setFaceDroite(face);
                if (arcsDejaTraitesADroite != null) {
                    arcsDejaTraitesADroite.add(arcCycle);
                }
            } else {
                arcCycle.setFaceGauche(face);
                if (arcsDejaTraitesAGauche != null) {
                    arcsDejaTraitesAGauche.add(arcCycle);
                }
            }
        }
    }

    /**
     * Nettoie les pointeurs de la carte topo pour assurer une bonne liberation
     * de la memoire
     * A utiliser lorsque'on souhaite effacer une carte topo.
     */
    public void nettoyer() {

        for (Population<? extends FT_Feature> pop : getPopulations())
            for (FT_Feature f : pop)
                f.setCorrespondants(new ArrayList<FT_Feature>());

        for (Arc arc : getPopArcs()) {
            arc.setNoeudFin(null);
            arc.setNoeudIni(null);
            arc.setFaceDroite(null);
            arc.setFaceGauche(null);
        }
        for (Face face : getPopFaces()) {
            face.setArcsPendants(null);
            face.setListeGroupes(null);
        }
        if (getPopGroupes() != null) {
        	for (Groupe gr : getPopGroupes()) {
        		gr.setListeArcs(null);
        		gr.setListeNoeuds(null);
        		gr.setListeFaces(null);
        	}
        }
        emptyComposants();
        emptyPopulations();

    }
}
