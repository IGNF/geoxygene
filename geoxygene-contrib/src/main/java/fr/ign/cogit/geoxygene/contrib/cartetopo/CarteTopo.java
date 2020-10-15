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

package fr.ign.cogit.geoxygene.contrib.cartetopo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Stack;

import javax.swing.event.EventListenerList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ICurveSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.contrib.geometrie.Distances;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Classe racine de la carte topo. Une carte topo est une composition d'arcs, de
 * noeuds, de faces et de groupes. Une carte topo est vue comme un DataSet
 * particulier. Elle a éventuellement une topologie (SPAGHETTI, NETWORK ou MAP).
 * <p>
 * English: a topological map is an oriented graph, with arcs sorted around the
 * nodes;
 * 
 * @author Sébastien Mustière
 * @author Olivier Bonin
 * @author Julien Perret
 */
public class CarteTopo extends DataSet {

  /** Logger. */
  protected final static Logger logger = LogManager
      .getLogger(CarteTopo.class.getName());

  protected EventListenerList listenerList = new EventListenerList();

  /**
   * Adds an <code>ActionListener</code> to the carteTopo.
   * @param l the <code>ActionListener</code> to be added
   */
  public void addActionListener(ActionListener l) {
    this.listenerList.add(ActionListener.class, l);
  }

  /**
   * Sets the list of action listeners.
   * @param listenerList list of action listeners
   */
  public void setActionListeners(EventListenerList listenerList) {
    this.listenerList = listenerList;
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is lazily created.
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

  // Accès aux composants de la carte topo

  /** Population des arcs de la carte topo. */
  @SuppressWarnings("unchecked")
  public IPopulation<Arc> getPopArcs() {
    return (IPopulation<Arc>) this.getPopulation("Edge");
  }

  /** Population des noeuds de la carte topo. */
  @SuppressWarnings("unchecked")
  public IPopulation<Noeud> getPopNoeuds() {
    return (IPopulation<Noeud>) this.getPopulation("Node");
  }

  /** Population des faces de la carte topo. */
  @SuppressWarnings("unchecked")
  public IPopulation<Face> getPopFaces() {
    return (IPopulation<Face>) this.getPopulation("Face");
  }

  /** Population des groupes de la carte topo. */
  @SuppressWarnings("unchecked")
  public IPopulation<Groupe> getPopGroupes() {
    return (IPopulation<Groupe>) this.getPopulation("Group");
  }

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
   * Liste des faces de la carte topo. Surcharge de getPopFaces().getElements().
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
   * Ajoute un noeud à la population des noeuds de la carte topo. Attention :
   * même si la carte topo est persistante, le noeud n'est pas rendu persistant
   * dans cette méthode
   */
  public void addNoeud(Noeud noeud) {
    this.getPopNoeuds().add(noeud);
  }

  /**
   * Ajoute un arc à la population des arcs de la carte topo. Attention : même
   * si la carte topo est persistante, le noeud n'est pas rendu persistant dans
   * cette méthode
   */
  public void addArc(Arc arc) {
    this.getPopArcs().add(arc);
  }

  /**
   * Ajoute une face à la population des faces de la carte topo. Attention :
   * même si la carte topo est persistante, le noeud n'est pas rendu persistant
   * dans cette méthode
   */
  public void addFace(Face face) {
    this.getPopFaces().add(face);
  }

  /**
   * Ajoute un groupe à la population des groupes de la carte topo. Attention :
   * même si la carte topo est persistante, le noeud n'est pas rendu persistant
   * dans cette méthode
   */
  public void addGroupe(Groupe groupe) {
    this.getPopGroupes().add(groupe);
  }

  private boolean buildInfiniteFace = true;

  public boolean isBuildInfiniteFace() {
    return this.buildInfiniteFace;
  }

  public void setBuildInfiniteFace(boolean buildInfiniteFace) {
    this.buildInfiniteFace = buildInfiniteFace;
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////
  // Constructeurs
  // ///////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Constructeur par défaut ; ATTENTION, constructeur à éviter car aucune
   * population n'est créée: seule un objet carteTopo est créé. Ce constructeur
   * sert aux constructeurs des sous-classe de la CarteTopo.
   */
  public CarteTopo() {
  }

  /**
   * Constructeur d'une carte topo non persistante. Le nom logique peut être
   * utilisé si la carte topo apparient à un DataSet, il peut être une chaîne
   * vide sinon. Par ce constructeur, la carte topo contient des
   * arcs/noeuds/faces/groupes des classes CarteTopo.Arc, CarteTopo.Noeud,
   * CarteTopo.Face, CarteTopo.Groupe.
   * @param nomLogique nom de la carte topo
   */
  public CarteTopo(String nomLogique) {

    this.ojbConcreteClass = this.getClass().getName(); // nécessaire pour ojb
    this.setNom(nomLogique);
    this.setPersistant(false);
    this.addPopulation(new Population<Arc>(false, "Edge",
        fr.ign.cogit.geoxygene.contrib.cartetopo.Arc.class, true));
    this.addPopulation(new Population<Noeud>(false, "Node",
        fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud.class, true));
    this.addPopulation(new Population<Face>(false, "Face",
        fr.ign.cogit.geoxygene.contrib.cartetopo.Face.class, true));
    this.addPopulation(new Population<Groupe>(false, "Group",
        fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe.class, false));
    
 
    /*

    FeatureType arcFeatureType = new FeatureType();
    arcFeatureType.setTypeName("Edge");
    AttributeType orientationType = new AttributeType("orient", "orientation",
        "Integer");
    arcFeatureType.addFeatureAttribute(orientationType);
    AttributeType weightType = new AttributeType("poids", "poids", "Double");
    arcFeatureType.addFeatureAttribute(weightType);
    AttributeType idType = new AttributeType("id", "id", "Integer");
    arcFeatureType.addFeatureAttribute(idType);
    AttributeType idIniType = new AttributeType("idNoeudIni", "idNoeudIni",
        "Integer");
    arcFeatureType.addFeatureAttribute(idIniType);
    AttributeType idFinType = new AttributeType("idNoeudFin", "idNoeudFin",
        "Integer");
    arcFeatureType.addFeatureAttribute(idFinType);
 
    arcFeatureType.setGeometryType(GM_LineString.class);
    this.getPopArcs().setFeatureType(arcFeatureType);


    FeatureType noeudFeatureType = new FeatureType();
    noeudFeatureType.setTypeName("Node");
    AttributeType distanceType = new AttributeType("distance", "distance",
        "Double");
    noeudFeatureType.addFeatureAttribute(distanceType);
    noeudFeatureType.addFeatureAttribute(idType);
  
    noeudFeatureType.setGeometryType(GM_Point.class);
    this.getPopNoeuds().setFeatureType(noeudFeatureType);


    FeatureType faceFeatureType = new FeatureType();
   
    faceFeatureType.setTypeName("Face");
    AttributeType infiniteType = new AttributeType("infinite", "infinite",
        "Boolean");
    faceFeatureType.addFeatureAttribute(infiniteType);
    AttributeType correspondantsType = new AttributeType("corres",
        "correspondantsAsString", "String");
    faceFeatureType.addFeatureAttribute(correspondantsType);
    AttributeType sizeCorrespondantsType = new AttributeType("nbCorres",
        "sizeCorrespondants", "Integer");
    faceFeatureType.addFeatureAttribute(sizeCorrespondantsType);
    faceFeatureType.addFeatureAttribute(idType);
    faceFeatureType.setGeometryType(GM_Polygon.class);
    this.getPopFaces().setFeatureType(faceFeatureType);
    
    */
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
   * Niveau de topologie : SPAGHETTI = pas de topologie ; NETWORK = topologie
   * arcs/noeuds ; MAP (par défaut) = topologie arcs/noeuds/faces NB : utiliser
   * les constantes SPAGHETTI, NETWORK ou MAP pour remplir cet attribut.
   * Remarque codeurs : Le code se sert très peu de l'attribut "type" pour
   * l'instant. A revoir.
   */
  private int type = CarteTopo.MAP;

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
   * Copie d'une carte topologique avec toutes les relations topologiques. Les
   * liens "correspondants" sont aussi dupliqués. <strong>ATTENTION: ne
   * fonctionne bien que pour une carteTopo non spécialisée (i.e. avec
   * carteTopo.Arc, carte.Noeud...).</strong> En effet, les objets copiés
   * appartiendront au package cartetopo.
   * @param nomLogique nom de la carte topo
   * @return une copie d'une carte topologique avec toutes les relations
   *         topologiques.
   */
  public CarteTopo copie(String nomLogique) {
    // création d'une nouvelle carte
    CarteTopo carte = new CarteTopo(nomLogique);

    // copie des objets, sans relation topologique
    for (Noeud noeud : this.getPopNoeuds()) {
      Noeud noeudCopie = carte.getPopNoeuds()
          .nouvelElement(noeud.getGeometrie());
      noeudCopie.addAllCorrespondants(noeud.getCorrespondants());
    }
    for (Arc arc : this.getPopArcs()) {
      Arc arcCopie = carte.getPopArcs().nouvelElement(arc.getGeometrie());
      arcCopie.addAllCorrespondants(arc.getCorrespondants());
    }
    for (Face face : this.getPopFaces()) {
      Face faceCopie = carte.getPopFaces().nouvelElement(face.getGeometrie());
      faceCopie.addAllCorrespondants(face.getCorrespondants());
    }
    for (Groupe groupe : this.getPopGroupes()) {
      Groupe groupeCopie = carte.getPopGroupes().nouvelElement();
      groupeCopie.addAllCorrespondants(groupe.getCorrespondants());
    }

    if (this.type == CarteTopo.SPAGHETTI) {
      return carte;
    }

    // copie des relations topologiques
    List<Noeud> noeuds = new ArrayList<Noeud>(
        this.getPopNoeuds().getElements());
    List<Noeud> noeudsCopies = new ArrayList<Noeud>(
        carte.getPopNoeuds().getElements());
    List<Arc> arcs = new ArrayList<Arc>(this.getPopArcs().getElements());
    List<Arc> arcsCopies = new ArrayList<Arc>(carte.getPopArcs().getElements());
    List<Face> faces = new ArrayList<Face>(this.getPopFaces().getElements());
    List<Face> facesCopies = new ArrayList<Face>(
        carte.getPopFaces().getElements());

    Iterator<Arc> itArcs = this.getPopArcs().getElements().iterator();
    Iterator<Arc> itArcsCopies = carte.getPopArcs().getElements().iterator();
    while (itArcs.hasNext()) {
      Arc arc = itArcs.next();
      Arc arcCopie = itArcsCopies.next();
      arcCopie.setNoeudIni(noeudsCopies.get(noeuds.indexOf(arc.getNoeudIni())));
      arcCopie.setNoeudFin(noeudsCopies.get(noeuds.indexOf(arc.getNoeudFin())));
      if (arc.getFaceGauche() != null) {
        arcCopie
            .setFaceGauche(facesCopies.get(faces.indexOf(arc.getFaceGauche())));
      }
      if (arc.getFaceDroite() != null) {
        arcCopie
            .setFaceDroite(facesCopies.get(faces.indexOf(arc.getFaceDroite())));
      }
    }

    Iterator<Groupe> itGroupes = this.getPopGroupes().getElements().iterator();
    Iterator<Groupe> itGroupesCopies = carte.getPopGroupes().getElements()
        .iterator();
    while (itGroupes.hasNext()) {
      Groupe groupe = itGroupes.next();
      Groupe groupeCopie = itGroupesCopies.next();
      for (Noeud noeud : groupe.getListeNoeuds()) {
        groupeCopie.addNoeud(noeudsCopies.get(noeuds.indexOf(noeud)));
      }
      for (Arc arc : groupe.getListeArcs()) {
        groupeCopie.addArc(arcsCopies.get(arcs.indexOf(arc)));
      }
      for (Face face : groupe.getListeFaces()) {
        groupeCopie.addFace(facesCopies.get(faces.indexOf(face)));
      }
    }
    return carte;
  }

  /**
   * enlève des arcs de la carteTopo, en enlevant aussi les relations
   * topologiques les concernant (avec les faces et noeuds).
   * @param arcsAEnlever liste des arcs à enlever de la carte topo
   */
  public void enleveArcs(Collection<Arc> arcsAEnlever) {
    for (Arc arc : arcsAEnlever) {
      // logger.info("removing edge " + arc);
      this.enleveArc(arc);
    }
  }

  public void enleveArc(Arc arcAEnlever) {
    if (!this.getPopArcs().remove(arcAEnlever)) {
      if (CarteTopo.logger.isDebugEnabled()) {
        CarteTopo.logger
            .debug(I18N.getString("CarteTopo.DeletionFailed") + arcAEnlever); //$NON-NLS-1$
      }
    }
    arcAEnlever.setNoeudFin(null);
    arcAEnlever.setNoeudIni(null);
    arcAEnlever.setFaceDroite(null);
    arcAEnlever.setFaceGauche(null);
  }

  /**
   * Enlève les arcs qui forment une boucle, i.e. dont le noeud ini est égal au
   * noeud fin.
   */
  public void enleveArcsBoucles() {
    List<Arc> listeArcs = new ArrayList<Arc>();
    for (Arc arc : this.getPopArcs()) {
      if (arc.getNoeudIni().equals(arc.getNoeudFin())) {
        listeArcs.add(arc);
      }
    }
    this.enleveArcs(listeArcs);
  }

  /**
   * Enlève des noeuds de la carteTopo, en enlevant aussi les relations
   * topologiques les concernant (avec les arcs et par conséquent avec les
   * faces).
   * @param noeudsAEnlever noeuds à enlever de la carte topo
   */
  public void enleveNoeuds(Collection<Noeud> noeudsAEnlever) {
    for (Noeud noeud : noeudsAEnlever) {
      this.getPopNoeuds().remove(noeud);
      List<Arc> entrants = new ArrayList<Arc>(noeud.getEntrants());
      for (Arc arc : entrants) {
        arc.setNoeudFin(null);
      }
      List<Arc> sortants = new ArrayList<Arc>(noeud.getSortants());
      for (Arc arc : sortants) {
        arc.setNoeudIni(null);
      }
    }
  }

  /**
   * Enlève des faces de la carteTopo, en enlevant aussi les relations
   * topologiques les concernant (avec les arcs et par conséquent avec les
   * noeuds).
   * @param facesAEnlever liste des face à enlever de la carte topo
   */
  public void enleveFaces(Collection<Face> facesAEnlever) {
    List<Arc> arcsDirects = new ArrayList<Arc>();
    List<Arc> arcsIndirects = new ArrayList<Arc>();
    for (Face face : facesAEnlever) {
      this.getPopFaces().remove(face);
      arcsDirects.addAll(face.getArcsDirects());
      arcsIndirects.addAll(face.getArcsIndirects());
    }
    for (Arc arcDirect : arcsDirects) {
      arcDirect.setFaceGauche(null);
    }
    for (Arc arcIndirect : arcsIndirects) {
      arcIndirect.setFaceDroite(null);
    }
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////
  // Instanciation ou nettoyage de la topologie de réseau
  // ///////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Instancie la topologie de réseau d'une Carte Topo, en se basant sur la
   * géométrie 2D des arcs et des noeuds. Autrement dit: crée les relation
   * "noeud initial" et "noeud final" d'un arc.
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
   * @param tolerance Le paramètre "tolerance" spécifie la distance maximale
   *          acceptée entre la position d'un noeud et la position d'une
   *          extrémité de ligne, pour considérer ce noeud comme extrémité (la
   *          tolérance peut être nulle).
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
      if (arc.getGeometrie().sizeControlPoint() < 2) {
        CarteTopo.logger.warn("Edge has only "
            + arc.getGeometrie().sizeControlPoint()
            + " points and was ignored during the construction of the topology between edges and nodes");
        continue;// TODO should we remove it right now or let the user
                 // do it?
      }
      selection = this.getPopNoeuds().select(arc.getGeometrie().startPoint(),
          tolerance);
      if (!selection.isEmpty()) {
        Iterator<Noeud> it = selection.iterator();
        while (it.hasNext()) {
          Noeud n = it.next();
          double distance = arc.getGeometrie().startPoint()
              .distance(n.getGeometrie().getPosition());
          if (distance < tolerance) {

            arc.setNoeudIni(n);
            break;
          }
        }
      }
      selection = this.getPopNoeuds().select(arc.getGeometrie().endPoint(),
          tolerance);
      if (!selection.isEmpty()) {
        Iterator<Noeud> it = selection.iterator();
        while (it.hasNext()) {
          Noeud n = it.next();
          double distance = arc.getGeometrie().endPoint()
              .distance(n.getGeometrie().getPosition());
          if (distance < tolerance) {

            arc.setNoeudFin(n);
            break;
          }
        }
      }
    }
  }

  /**
   * Crée un nouveau noeud à l'extrémité de chaque arc si il n'y en a pas. Les
   * noeuds existants sont tous conservés.
   * <ul>
   * <li>NB: la topologie arcs/noeuds est instanciée au passage.
   * <li>NB: si cela n'avait pas été fait avant, la population des noeuds est
   * indexée dans cette méthode.
   * <li>Paramètres de l'index = le même que celui des arcs si il existe, sinon
   * Dallage avec à peu près 50 noeuds par dalle.
   * </ul>
   * @param tolerance tolérance utilisée pour chercher des noeuds existants
   *          auxquels se raccrocher au lieu d'en créer de nouveaux.
   */
  public void creeNoeudsManquants(double tolerance) {
    // initialisation de l'index au besoin
    // si on peut, on prend les mêmes paramètres que le dallage des arcs
    if (!this.getPopNoeuds().hasSpatialIndex()) {
      if (this.getPopArcs().hasSpatialIndex()) {
        this.getPopNoeuds()
            .initSpatialIndex(this.getPopArcs().getSpatialIndex());
        this.getPopNoeuds().getSpatialIndex().setAutomaticUpdate(true);
      } else {
        IEnvelope enveloppe = this.getPopArcs().envelope();
        int nb = (int) Math.sqrt(this.getPopArcs().size() / 20);
        if (nb == 0) {
          nb = 1;
        }
        this.getPopNoeuds().initSpatialIndex(Tiling.class, true, enveloppe, nb);
      }
    }
    this.fireActionPerformed(new ActionEvent(this, 0,
        I18N.getString("CarteTopo.MissingNodesCreation"), //$NON-NLS-1$
        this.getPopArcs().size()));
    int index = 0;
    List<Arc> arcsVides = new ArrayList<Arc>(0);
    for (Arc arc : this.getPopArcs()) {
      if (arc.getGeometrie().sizeControlPoint() == 0) {
        CarteTopo.logger.error(I18N.getString("CarteTopo.EmptyEdge")); //$NON-NLS-1$
        arcsVides.add(arc);
        continue;
      }
      // noeud initial
      Noeud noeud = null;
      Collection<Noeud> selection = this.getPopNoeuds()
          .select(arc.getGeometrie().startPoint(), tolerance);
      if (selection.isEmpty()) {
        noeud = this.getPopNoeuds()
            .nouvelElement(new GM_Point(arc.getGeometrie().startPoint()));
      } else {
        noeud = selection.iterator().next();
        arc.getGeometrie().coord().set(0, noeud.getGeometrie().getPosition());
      }
      arc.setNoeudIni(noeud);
      // noeud final
      selection = this.getPopNoeuds().select(arc.getGeometrie().endPoint(),
          tolerance);
      if (selection.isEmpty()) {
        noeud = this.getPopNoeuds()
            .nouvelElement(new GM_Point(arc.getGeometrie().endPoint()));
      } else {
        noeud = selection.iterator().next();
        arc.getGeometrie().coord().set(
            arc.getGeometrie().sizeControlPoint() - 1,
            noeud.getGeometrie().getPosition());
      }
      arc.setNoeudFin(noeud);
      this.fireActionPerformed(new ActionEvent(this, 1,
          I18N.getString("CarteTopo.EdgeHandled"), ++index)); //$NON-NLS-1$
    }
    this.getPopArcs().removeAll(arcsVides);
    this.fireActionPerformed(new ActionEvent(this, 4,
        I18N.getString("CarteTopo.MissingNodesCreated"))); //$NON-NLS-1$
  }

  /**
   * Filtrage des noeuds isolés (c'est-à-dire connectés à aucun arc). Ceux-ci
   * sont enlevés de la Carte Topo IMPORTANT : La topologie de réseau doit avoir
   * été instanciée, sinon tous les noeuds sont enlevés.
   */
  public void filtreNoeudsIsoles() {
    List<Noeud> aJeter = new ArrayList<Noeud>();
    for (Noeud noeud : this.getPopNoeuds()) {
      if (noeud.arcs().size() == 0) {
        aJeter.add(noeud);
      }
    }
    for (Noeud noeud : aJeter) {
      this.getPopNoeuds().enleveElement(noeud);
    }
  }

  /**
   * 
   * @param tolerance
   */
  public void filtreArcsNull(double tolerance) {
    List<Arc> aJeter = new ArrayList<Arc>();
    for (Arc arc : this.getPopArcs()) {
      if (((ICurveSegment) arc.getGeom()).length() < tolerance) {
        aJeter.add(arc);
      }
    }
    for (Arc arc : aJeter) {
      this.getPopArcs().enleveElement(arc);
    }
  }

  /**
   * Filtrage des noeuds doublons (plusieurs noeuds localisés au même endroit).
   * <ul>
   * <li>NB: si cela n'avait pas été fait avant, la population des noeuds est
   * indexée dans cette méthode (dallage, paramètre = 20).
   * <li>Cette méthode gère les conséquences sur la topologie, si celle-ci a été
   * instanciée auparavant.
   * <li>Cette méthode gère aussi les conséquences sur les correspondants (un
   * noeud gardé a pour correspondants tous les correspondants des doublons).
   * </ul>
   * @param tolerance Le paramètre tolérance spécifie la distance maximale pour
   *          considérer deux noeuds positionnés au même endroit.
   */
  public void filtreDoublons(double tolerance) {
    List<Noeud> aJeter = new ArrayList<Noeud>(0);
    Collection<Noeud> selection;

    // initialisation de l'index au besoin
    if (!this.getPopNoeuds().hasSpatialIndex()) {
      this.getPopNoeuds().initSpatialIndex(Tiling.class, true, 20);
    }
    for (Noeud noeud : this.getPopNoeuds()) {
      if (aJeter.contains(noeud)) {
        continue;
      }
      selection = this.getPopNoeuds().select(noeud.getCoord(), tolerance);
      selection.remove(noeud);
      for (Noeud doublon : selection) {
        // on a trouvé un doublon à jeter
        // on gère les conséquences sur la topologie et les
        // correspondants
        aJeter.add(doublon);
        noeud.addAllCorrespondants(doublon.getCorrespondants());
        for (Arc a : new ArrayList<Arc>(doublon.getEntrants())) {
          noeud.addEntrant(a);
        }
        for (Arc a : new ArrayList<Arc>(doublon.getSortants())) {
          noeud.addSortant(a);
        }
      }
    }
    this.getPopNoeuds().removeAll(aJeter);
  }

  /**
   * Filtrage des noeuds "simples", c'est-à-dire avec seulement deux arcs
   * incidents, si ils ont des orientations compatibles. Ces noeuds sont enlevés
   * et un seul arc est créé à la place des deux arcs incidents.
   * <ul>
   * <li>Cette méthode gère les conséquences sur la topologie arcs/noeuds/faces.
   * <li>Cette méthode gère aussi les conséquences sur les correspondants. (un
   * nouvel arc a pour correspondants tous les correspondants des deux arcs
   * incidents).
   * <li>Cette méthode gère les conséquences sur l'orientation
   * </ul>
   * <p>
   * <strong>IMPORTANT: la topologie arcs/noeuds doit avoir été instanciée avant
   * de lancer cette méthode</strong>
   */
  public void filtreNoeudsSimples() {
    this.filtreNoeudsSimples(false, null);
  }

  public void filtreNoeudsSimples(boolean useWeight, IGeometry filteredArea) {
    this.filtreNoeudsSimples(useWeight, filteredArea, false);
  }

  public void filtreNoeudsSimples(boolean useWeight, IGeometry filteredArea,
      boolean checkEqualCorrespondants) {
    List<Noeud> noeudsElimines = new ArrayList<Noeud>();
    for (Noeud noeud : this.getPopNoeuds()) {
      List<Arc> arcsIncidents = noeud.arcs();
      if (arcsIncidents.size() != 2) {
        continue;
      }
      if (arcsIncidents.get(0) == arcsIncidents.get(1)) {
        continue; // gestion des boucles
      }
      List<Arc> entrantsOrientes = noeud.entrantsOrientes();
      if (entrantsOrientes.isEmpty()) {
        continue; // incompatibilité d'orientation
      }
      List<Arc> sortantsOrientes = noeud.sortantsOrientes();
      if (sortantsOrientes.isEmpty()) {
        continue; // incompatibilité d'orientation
      }
      if ((entrantsOrientes.size() + sortantsOrientes.size()) == 3) {
        continue; // incompatibilité d'orientation
      }
      Arc arc1 = arcsIncidents.get(0);
      Arc arc2 = arcsIncidents.get(1);
      if (arc1 == arc2) {
        continue;
      }

      if (checkEqualCorrespondants && !(arc1.getCorrespondants()
          .containsAll(arc2.getCorrespondants())
          && arc2.getCorrespondants().containsAll(arc1.getCorrespondants()))) {
        continue;
      }

      if (useWeight && arc1.getPoids() != arc2.getPoids()) {
        continue; // different weights
      }
      if (filteredArea != null
          && !noeud.getGeometrie().intersects(filteredArea)) {
        continue; //
      }
      logger.debug("Filtering " + noeud);

      Arc arcTotal = this.getPopArcs().nouvelElement();
      if (arcTotal.getId() == 0) {
        logger.error("NULL ID for NEW EDGE " + Population.getIdNouvelElement());
      }
      List<ILineString> geometries = new ArrayList<ILineString>(2);
      geometries.add(arc1.getGeometrie());
      geometries.add(arc2.getGeometrie());
      boolean sameOrientation = ((arc1.getNoeudIni() == arc2.getNoeudFin())
          || (arc1.getNoeudFin() == arc2.getNoeudIni()));
      // création de la nouvelle géométrie
      ILineString union = Operateurs.compileArcs(geometries);
      logger.debug("\t union = " + union);
      if (union != null) {
        IDirectPosition start = union.getControlPoint(0);
        IDirectPosition end = union
            .getControlPoint(union.sizeControlPoint() - 1);
        if (arc1.getNoeudIni() == null || arc1.getNoeudFin() == null) {
          logger.error("NULL NODE FOR " + arc1);
        }
        if (arc2.getNoeudIni() == null || arc2.getNoeudFin() == null) {
          logger.error("NULL NODE FOR " + arc2);
        }
        if ((start.distance2D(arc1.getNoeudIni().getCoord()) == 0
            && end.distance2D(arc2.getNoeudFin().getCoord()) == 0)
            || (start.distance2D(arc2.getNoeudIni().getCoord()) == 0
                && end.distance2D(arc1.getNoeudFin().getCoord()) == 0)) {
          arcTotal.setGeometrie(union);
        } else {
          arcTotal.setGeometrie((ILineString) union.reverse());
          logger.debug("union of geometries");
          logger.debug(arc1.getGeometrie());
          logger.debug(arc2.getGeometrie());
          logger.debug("Reversed to " + arcTotal.getGeometrie());
        }
      } else {
        logger.debug("null union of geometries");
        logger.debug(arc1.getGeometrie());
        logger.debug(arc2.getGeometrie());
        union = (ILineString) arc1.getGeometrie().union(arc2.getGeometrie());
        logger.debug("\t new union = " + union);
        arcTotal.setGeometrie(union);
      }

      // gestion des conséquences sur l'orientation et les correspondants
      arcTotal.setOrientation(arc1.getOrientation());
      for (IFeature corresp : arc1.getCorrespondants()) {
        if (!arcTotal.getCorrespondants().contains(corresp)) {
          arcTotal.addCorrespondant(corresp);
        }
      }
      arc1.setCorrespondants(new ArrayList<IFeature>(0));
      for (IFeature corresp : arc2.getCorrespondants()) {
        if (!arcTotal.getCorrespondants().contains(corresp)) {
          arcTotal.addCorrespondant(corresp);
        }
      }
      arc2.setCorrespondants(new ArrayList<IFeature>(0));
      for (IFeature corresp : noeud.getCorrespondants()) {
        if (!arcTotal.getCorrespondants().contains(corresp)) {
          arcTotal.addCorrespondant(corresp);
        }
      }
      noeud.setCorrespondants(new ArrayList<IFeature>(0));
      // gestion des conséquences sur la topologie
      Face faceDroite1 = arc1.getFaceDroite();
      Face faceGauche1 = arc1.getFaceGauche();
      Face faceDroite2 = arc2.getFaceDroite();
      Face faceGauche2 = arc2.getFaceGauche();
      Noeud noeudIni1 = arc1.getNoeudIni();
      Noeud noeudFin1 = arc1.getNoeudFin();
      Noeud noeudIni2 = arc2.getNoeudIni();
      Noeud noeudFin2 = arc2.getNoeudFin();
      // conséquences sur le premier arc
      if (noeudIni1 == noeud) {
        noeudIni1.getSortants().remove(arc1);
        if (noeudFin1 != null) {
          noeudFin1.getEntrants().remove(arc1);
          noeudFin1.addEntrant(arcTotal);
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
          if (sameOrientation) {
            noeudFin2.addEntrant(arcTotal);
          } else {
            noeudFin2.addSortant(arcTotal);
          }
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
          if (sameOrientation) {
            noeudIni2.addSortant(arcTotal);
          } else {
            noeudIni2.addEntrant(arcTotal);
          }
        }
        if (faceDroite2 != null) {
          faceDroite2.getArcsIndirects().remove(arc2);
        }
        if (faceGauche2 != null) {
          faceGauche2.getArcsDirects().remove(arc2);
        }
      }
      // make sure the geometry is in the right order
      if (arcTotal.getGeometrie().getControlPoint(0)
          .distance2D(arcTotal.getNoeudIni().getCoord()) != 0) {
        logger.debug("Reversing geometry " + arcTotal.getGeometrie());
        arcTotal.setGeom(arcTotal.getGeometrie().reverse());
        logger.debug("New geometry " + arcTotal.getGeometrie());
        logger.debug("Initial node was in "
            + arcTotal.getNoeudIni().getCoord().toGM_Point());
      }
      // if we're using the weights as a filter, we keep it in the new edge
      if (useWeight) {
        arcTotal.setPoids(arc1.getPoids());
      }
      // Elimination des arcs et du noeud inutile
      this.getPopArcs().enleveElement(arc1);
      this.getPopArcs().enleveElement(arc2);
      noeudsElimines.add(noeud);
    }
    for (int i = 0; i < noeudsElimines.size(); i++) {
      this.getPopNoeuds().enleveElement(noeudsElimines.get(i));
    }
  }

  /**
   * Filtre les arcs en double (en double = même géométrie et même orientation).
   * On vérifie à la fois la géométrie et la topologie, i.e. la topologie
   * arcs/noeuds doit être instanciée.
   * <p>
   * <strong>Attention: les conséquences sur la topologie arcs/faces ne sont pas
   * gérées.</strong>
   * <p>
   * TODO s'il existe des arcs en plus de 2 exemplaires, ils ne sont pas enlevés
   * je crois
   */
  public void filtreArcsDoublons() {
    this.filtreArcsDoublons(0.5);
  }

  /**
   * Filtre les arcs en double (en double = même géométrie et même orientation).
   * On vérifie à la fois la géométrie et la topologie, i.e. la topologie
   * arcs/noeuds doit être instanciée.
   * <p>
   * <strong>Attention: les conséquences sur la topologie arcs/faces ne sont pas
   * gérées.</strong>
   * <p>
   * TODO s'il existe des arcs en plus de 2 exemplaires, ils ne sont pas enlevés
   * je crois
   */
  public void filtreArcsDoublons(double toleranceHaussdorf) {
    List<Arc> arcs = this.getListeArcs();
    this.fireActionPerformed(new ActionEvent(this, 0,
        I18N.getString("CarteTopo.DoubleEdgesFiltering"), arcs.size() - 1)); //$NON-NLS-1$
    Set<Arc> arcsAEnlever = new HashSet<Arc>();
    for (int i = 0; i < arcs.size() - 1; i++) {
      Arc arci = arcs.get(i);
      // CarteTopo.logger.debug("Handling Edge " + i + " / " + arcs.size()
      // + " - " + arci);
      if (arci.getNoeudIni() == null || arci.getNoeudFin() == null) {
        continue;// soit la topologie est mal instanciée, soit on a
                 // décidé de supprimer l'arc
      }
      // if (arcsAEnlever.contains(arci)) {
      // continue;// on a déjà décidé d'enlever cet arc
      // }
      for (int j = i + 1; j < arcs.size(); j++) {
        Arc arcj = arcs.get(j);
        if (arcj.getNoeudIni() == null || arcj.getNoeudFin() == null) {
          continue;// soit la topologie est mal instanciée, soit on a
                   // décidé de supprimer l'arc
        }
        // if (arcsAEnlever.contains(arcj)) {
        // continue;// on a déjà décidé d'enlever cet arc
        // }
        if (arcj.getOrientation() != arcj.getOrientation()) {
          continue;
        }
        if (!((arci.getNoeudIni() == arcj.getNoeudIni()
            && arci.getNoeudFin() == arcj.getNoeudFin())
            || (arci.getNoeudFin() == arcj.getNoeudIni()
                && arci.getNoeudIni() == arcj.getNoeudFin()))) {
          continue;
        }
        if (!arcj.getGeom().equals(arci.getGeom())) {
          double dist = Distances.hausdorff(arci.getGeometrie(),
              arcj.getGeometrie());
          // CarteTopo.logger.debug("\t Edge " + arcj);
          CarteTopo.logger.debug("\t hausdorff = " + dist);
          if (dist > toleranceHaussdorf) {
            continue;
          }
        }
        CarteTopo.logger.debug("Removing edge " + arcj);
        arcsAEnlever.add(arcj);
        arci.addAllCorrespondants(arcj.getCorrespondants());
        // for (IFeature corresp : arcj.getCorrespondants()) {
        // arci.addCorrespondant(corresp);
        // }
        arcj.setCorrespondants(new ArrayList<IFeature>(0));
        arcj.setNoeudIni(null);
        arcj.setNoeudFin(null);
      }
      this.fireActionPerformed(new ActionEvent(this, 1,
          I18N.getString("CarteTopo.EdgeHandled"), i + 1)); //$NON-NLS-1$
    }
    if (CarteTopo.logger.isDebugEnabled()) {
      CarteTopo.logger.debug(
          arcsAEnlever.size() + I18N.getString("CarteTopo.NullEdgesToRemove")); //$NON-NLS-1$
    }
    this.enleveArcs(arcsAEnlever);
    arcsAEnlever.clear();
    for (Arc arc : this.getListeArcs()) {
      if (arc.getNoeudIni() == null || arc.getNoeudFin() == null) {
        CarteTopo.logger.warn(I18N.getString("CarteTopo.NullEdge") + arc); //$NON-NLS-1$
        arcsAEnlever.add(arc);
      }
    }
    if (CarteTopo.logger.isDebugEnabled()) {
      CarteTopo.logger.debug(
          arcsAEnlever.size() + I18N.getString("CarteTopo.NullEdgesToRemove")); //$NON-NLS-1$
    }
    this.enleveArcs(arcsAEnlever);
    // if (!this.getPopArcs().removeAll(arcsAEnlever))
    // logger.warn("Attention : des arcs n'ont pas pu être enlevés");
    this.fireActionPerformed(new ActionEvent(this, 4,
        I18N.getString("CarteTopo.DoubleEdgesFiltered"))); //$NON-NLS-1$
  }

  public void cleanEdges(double tolerance) {
    // clean geometries
    for (Arc arc : this.getPopArcs()) {
      CarteTopo.logger.debug("Handling Edge " + arc);
      ILineString line = arc.getGeometrie();
      CarteTopo.cleanLineString(line, tolerance);
    }
  }

  public static void cleanLineString(ILineString line, double tolerance) {
    if (line.sizeControlPoint() == 2) {
      return;
    }
    IDirectPositionList controlPoint = line.getControlPoint();
    ListIterator<IDirectPosition> iterator = controlPoint.listIterator();
    IDirectPosition previous = iterator.next();
    int numberOfPointsRemoved = 0;
    while (iterator.hasNext()) {
      IDirectPosition current = iterator.next();
      if (current.distance(previous) < tolerance) {
        CarteTopo.logger.debug("Previous = " + new GM_Point(previous));
        CarteTopo.logger.debug("Current = " + new GM_Point(current));
        if (iterator.hasNext()) {
          // this is not the last point
          iterator.remove();
        } else {
          CarteTopo.logger.debug(
              "last " + iterator.previousIndex() + " " + iterator.nextIndex()); //$NON-NLS-1$
          // go back and remove the point before
          IDirectPosition temp = iterator.previous();
          CarteTopo.logger.debug("Temp = " + new GM_Point(temp) + " - "
              + iterator.previousIndex() + " " + iterator.nextIndex());
          temp = iterator.previous();
          CarteTopo.logger.debug("Temp = " + new GM_Point(temp) + " - "
              + iterator.previousIndex() + " " + iterator.nextIndex());
          previous = iterator.previous();
          CarteTopo.logger.debug(
              "Previous before removal = " + new GM_Point(previous) + " - "
                  + iterator.previousIndex() + " " + iterator.nextIndex());
          current = iterator.next();
          CarteTopo.logger
              .debug("Current before removal= " + new GM_Point(current) + " - "
                  + iterator.previousIndex() + " " + iterator.nextIndex());
          current = iterator.next();
          CarteTopo.logger
              .debug("Current before removal= " + new GM_Point(current) + " - "
                  + iterator.previousIndex() + " " + iterator.nextIndex());
          iterator.remove();
        }
        numberOfPointsRemoved++;
        if (controlPoint.size() == 2) {
          break;
        }
      } else {
        previous = current;
      }
    }
    if (numberOfPointsRemoved > 0) {
      CarteTopo.logger.debug(numberOfPointsRemoved + " points removed");
      CarteTopo.logger.debug(line);
    }
  }

  /**
   * Transforme la carte topo pour la rendre planaire : les arcs sont découpés à
   * chaque intersection d'arcs, et un noeud est créé à chaque extrémité d'arc.
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
   * @param tolerance paramètre de tolérance sur la localisation des noeuds:
   *          deux extrémités d'arc à moins de cette distance sont considérées
   *          superposées (utilisé lors de la construction de la topologie
   *          arcs/noeuds). Ce paramètre peut être nul.
   */
  @SuppressWarnings("unchecked")
  public void rendPlanaire(double tolerance) {

    // si pas d'arc, c'est planaire
    if (this.getPopArcs().isEmpty()) {
      return;
    }

    List<IFeature> dejaTraites = new ArrayList<IFeature>();
    List<Arc> arcsEnleves = new ArrayList<Arc>(0);

    // initialisation de l'index des arcs au besoin
    if (!this.getPopArcs().hasSpatialIndex()) {
      this.getPopArcs().initSpatialIndex(Tiling.class, true);
    } else {
      // force the automatic update of the index if it exists
      this.getPopArcs().getSpatialIndex().setAutomaticUpdate(true);
    }

    this.fireActionPerformed(new ActionEvent(this, 0,
        I18N.getString("CarteTopo.PlanarGraphCreation"), //$NON-NLS-1$
        this.getPopArcs().size()));

    for (int indexArc = 0; indexArc < this.getPopArcs().size(); indexArc++) {

      Arc currentEdge = this.getPopArcs().get(indexArc);
      if (CarteTopo.logger.isDebugEnabled()) {
        // CarteTopo.logger.debug(indexArc + " / " +
        // (this.getPopArcs().size() - 1));
        // CarteTopo.logger.debug(I18N.getString("CarteTopo.Handling") +
        // currentEdge); //$NON-NLS-1$
      }
      if (arcsEnleves.contains(currentEdge)
          || dejaTraites.contains(currentEdge)) {
        // CarteTopo.logger.debug("Already handled or removed");
        // si on a déjà traité l'arc ou qu'on l'a déjà découpé, passer
        // au suivant
        continue;
      }

      // les arcs qui croisent l'arc courant
      // Optimisation et blindage pour tous les cas non garanti (Seb)
      Collection<Arc> selection = this.getPopArcs()
          .select(currentEdge.getGeometrie());
      // on enlève l'arc courant et les arcs déjà enlevés
      selection.remove(currentEdge);
      selection.removeAll(arcsEnleves);
      // selection.removeAll(dejaTraites); // ADDED
      CarteTopo.logger.debug(selection.size() + " Edges intersected");
      List<Arc> listeInter = new ArrayList<Arc>(0);
      // On construit un multipoint contenant les extrémités de l'arc
      // courant
      GM_MultiPoint frontiereArc = new GM_MultiPoint();
      frontiereArc.add(new GM_Point(currentEdge.getGeometrie().startPoint()));
      frontiereArc.add(new GM_Point(currentEdge.getGeometrie().endPoint()));

      // pour chaque arc qui intersecte l'arc courant
      for (Arc arcSel : selection) {
        // On construit un multipoint contenant les extrémités de l'arc
        // de la sélection
        if (CarteTopo.logger.isDebugEnabled()) {
          CarteTopo.logger.debug("Treating selected edge " + arcSel); //$NON-NLS-1$
        }
        GM_MultiPoint frontiereArcSel = new GM_MultiPoint();
        frontiereArcSel.add(new GM_Point(arcSel.getGeometrie().startPoint()));
        frontiereArcSel.add(new GM_Point(arcSel.getGeometrie().endPoint()));
        // on calcule l'intersection de l'arc courant avec l'arc de la
        // sélection
        IGeometry intersection = arcSel.getGeometrie()
            .intersection(currentEdge.getGeometrie());
        CarteTopo.logger.debug("Intersection " + intersection); //$NON-NLS-1$
        /*
         * //modif Seb: tentative d'accélération : buggé mais je ne trouve pas
         * pourquoi if (intersection instanceof GM_Point ) { if (
         * Operateurs.superposes(ptArcIni, (GM_Point)intersection) ||
         * Operateurs.superposes(ptArcFin, (GM_Point)intersection) ) { if (
         * Operateurs.superposes(ptArcSelIni, (GM_Point)intersection) ||
         * Operateurs.superposes(ptArcSelFin, (GM_Point)intersection) )
         * continue; } listeInter.add(arcSel); continue; }
         */
        // si l'intersection trouvée fait partie des extrémités des 2
        // arcs, alors on passe à l'arc suivant
        if ((!(intersection instanceof GM_Aggregate)
            || (intersection instanceof GM_MultiPoint))
            && frontiereArc.contains(intersection)
            && frontiereArcSel.contains(intersection)) {
          continue;
        }
        // on a une intersection ailleurs que sur une extrémité
        listeInter.add(arcSel);
      }
      if (listeInter.isEmpty()) {
        this.fireActionPerformed(new ActionEvent(this, 1,
            I18N.getString("CarteTopo.EdgeHandled"), indexArc + 1)); //$NON-NLS-1$
        continue; // pas d'intersection avec cet arc
      }

      // on découpe tout
      IGeometry nodedLineStrings = currentEdge.getGeometrie();
      for (Arc edge : listeInter) {
        if (edge.getGeometrie().isEmpty()) {
          CarteTopo.logger.error(I18N.getString("CarteTopo.EmptyGeometryEdge") //$NON-NLS-1$
              + edge.getGeometrie().toString());
        } else {
          try {
            nodedLineStrings = nodedLineStrings.union(edge.getGeometrie());
          } catch (Exception e) {
            CarteTopo.logger
                .error("Crappy edge: " + edge.getGeometrie().toString()); //$NON-NLS-1$
          }
        }
      }
      listeInter.add(currentEdge); // on le rajoute pour la suite
      if (nodedLineStrings instanceof ILineString) {
        boolean toutesEgales = true;
        for (Arc arcSel : listeInter) {
          toutesEgales = toutesEgales
              && arcSel.getGeometrie().equals(nodedLineStrings);
        }
        if (toutesEgales) {
          if (CarteTopo.logger.isDebugEnabled()) {
            CarteTopo.logger.debug(I18N.getString("CarteTopo.EqualGeometries")); //$NON-NLS-1$
          }
          Arc arcNouveau = this.getPopArcs().nouvelElement(nodedLineStrings);
          if (arcNouveau.getId() == 0) {
            logger.error(
                "NULL ID for NEW EDGE " + Population.getIdNouvelElement());
          }

          boolean premierArc = true;
          for (Arc arcSel : listeInter) {
            arcNouveau.addAllCorrespondants(arcSel.getCorrespondants());
            arcsEnleves.add(arcSel);
            if (premierArc) {
              // on affecte la valeur initiale de l'orientation
              // avec le premier arc rencontré
              arcNouveau.setOrientation(arcSel.getOrientation());// TODO
                                                                 // Check
                                                                 // that
                                                                 // it
                                                                 // is
                                                                 // the
                                                                 // correct
                                                                 // one
              premierArc = false;
            } else {
              // ensuite, si la valeur diffère de la valeur
              // initale, on met l'orientation dans les deux sens
              if (arcNouveau.getOrientation() != arcSel.getOrientation()) {
                arcNouveau.setOrientation(2);// TODO Check that
                                             // too
              }
            }
          }
          // le nouvel arc possède la même géométrie que l'arc
          // initial, pas la peine de revenir
          dejaTraites.add(arcNouveau);
        } else {
          CarteTopo.logger
              .error(I18N.getString("CarteTopo.PlanarGraphProblem")); //$NON-NLS-1$
          CarteTopo.logger
              .error(I18N.getString("CarteTopo.IntersectionProblem")); //$NON-NLS-1$
          CarteTopo.logger
              .error(I18N.getString("CarteTopo.EdgeProblem") + currentEdge); //$NON-NLS-1$
          CarteTopo.logger
              .error(I18N.getString("CarteTopo.UnionWithSeveralEdges")); //$NON-NLS-1$
          for (Arc a : listeInter) {
            CarteTopo.logger.error(this.getPopArcs().contains(a) + " - " + a); //$NON-NLS-1$
            CarteTopo.logger.error(a.getNoeudIni());
            CarteTopo.logger.error(a.getNoeudFin());
            // if ( a.getGeometrie().isEmpty())
            // logger.error("la géométrie de l'arc est vide "+
            // a.getGeometrie().toString());
            // else logger.error(" "+ a.getGeometrie().toString());
          }
        }
        this.fireActionPerformed(new ActionEvent(this, 1,
            I18N.getString("CarteTopo.EdgeHandled"), indexArc + 1)); //$NON-NLS-1$
        continue;
      }
      if (nodedLineStrings instanceof IMultiCurve) { // cas où il faut
                                                     // découper
        // 1: on rajoute les morceaux d'arcs découpés
        for (ILineString ligneDecoupe : (IMultiCurve<ILineString>) nodedLineStrings) {
          Arc arcNouveau = this.getPopArcs().nouvelElement(ligneDecoupe);
          if (arcNouveau.getId() == 0) {
            logger.error(
                "NULL ID for NEW EDGE " + Population.getIdNouvelElement());
          }

          // on recherche à quel(s) arc(s) initial appartient chaque
          // bout découpé
          for (Arc arcSel : listeInter) {
            // on devrait mettre == 0 ci-dessous, mais pour gérer
            // les erreurs d'arrondi on met <0.01
            if (Distances.premiereComposanteHausdorff(ligneDecoupe,
                arcSel.getGeometrie()) < 0.01) {
              // on appartient à lui
              arcNouveau.addAllCorrespondants(arcSel.getCorrespondants());
              arcNouveau.setOrientation(arcSel.getOrientation());
              // si on appartient à l'arc initial, pas la peine de
              // revenir
              if (arcSel == currentEdge) {
                dejaTraites.add(arcNouveau);
              }
            }
          }
          if (CarteTopo.logger.isDebugEnabled()) {
            CarteTopo.logger
                .debug(I18N.getString("CarteTopo.NewEdge") + " " + arcNouveau); //$NON-NLS-1$
          }
        }
        // 2: on virera les arcs initiaux qui ont été découpés
        for (Arc arcSel : listeInter) {
          if (CarteTopo.logger.isDebugEnabled()) {
            CarteTopo.logger
                .debug(I18N.getString("CarteTopo.IntersectionFound") + arcSel); //$NON-NLS-1$
          }
          arcSel.setCorrespondants(new ArrayList<IFeature>(0));
          arcsEnleves.add(arcSel);
        }
        this.fireActionPerformed(new ActionEvent(this, 1,
            I18N.getString("CarteTopo.EdgeHandled"), indexArc + 1)); //$NON-NLS-1$
        continue;
      }
      // cas imprévu: OUPS
      CarteTopo.logger.error(I18N.getString("CarteTopo.PlanarGraphProblem")); //$NON-NLS-1$
      if (nodedLineStrings != null)
        CarteTopo.logger.error(
            I18N.getString("CarteTopo.UnionBug") + nodedLineStrings.getClass()); //$NON-NLS-1$
      CarteTopo.logger.error(I18N.getString("CarteTopo.EdgeProblem") //$NON-NLS-1$
          + currentEdge.getGeom().coord());
    }
    if (CarteTopo.logger.isDebugEnabled()) {
      CarteTopo.logger.debug("Removing " + arcsEnleves.size() + " edges");
    }
    this.enleveArcs(arcsEnleves);
    // On construit les nouveaux noeuds éventuels et la topologie
    // arcs/noeuds
    this.getPopNoeuds().setElements(new ArrayList<Noeud>());
    if (CarteTopo.logger.isDebugEnabled()) {
      CarteTopo.logger.debug("Creating missing nodes");
    }
    this.creeNoeudsManquants(tolerance);
    /**
     * vérification des arcs qui s'intersectent presque à moins de tolérance
     * FIXME ATTENTION : ce bout de code est à nettoyer et à corriger
     * éventuellement.
     */
    if (CarteTopo.logger.isDebugEnabled()) {
      CarteTopo.logger.debug("Checking nodes");
    }
    for (Noeud noeud : this.getPopNoeuds()) {
      if (noeud.arcs().size() == 1) {
        Collection<Arc> arcs = this.getPopArcs()
            .select(noeud.getGeom().buffer(tolerance));
        arcs.removeAll(noeud.arcs());
        if (!arcs.isEmpty()) {
          if (CarteTopo.logger.isDebugEnabled()) {
            CarteTopo.logger
                .debug(I18N.getString("CarteTopo.HandlingNode") + noeud); //$NON-NLS-1$
          }
          if (CarteTopo.logger.isDebugEnabled()) {
            CarteTopo.logger
                .debug(I18N.getString("CarteTopo.NumberOfNeighborNodes") //$NON-NLS-1$
                    + this.getPopNoeuds()
                        .select(noeud.getGeom().buffer(tolerance)).size());
          }
          if (CarteTopo.logger.isDebugEnabled()) {
            CarteTopo.logger
                .debug(I18N.getString("CarteTopo.NumberOfNeighborEdges") //$NON-NLS-1$
                    + arcs.size());
          }
          Arc arc = arcs.iterator().next();
          if (CarteTopo.logger.isDebugEnabled()) {
            CarteTopo.logger
                .debug(I18N.getString("CarteTopo.EdgeSplitting") + arc); //$NON-NLS-1$
          }
          arc.projeteEtDecoupe(noeud);
        }
      }
    }
  }

  /**
   * Fusionne en un seul noeud, tous les noeuds proches de moins de "tolerance"
   * Les correspondants suivent, la topologie arcs/noeuds aussi. NB: les petits
   * arcs qui n'ont plus de sens sont aussi éliminés. Plus précisément ce sont
   * ceux qui partent et arrivent sur un même nouveau noeud créé, et restant à
   * moins de "tolerance" de ce nouveau noeud Un index spatial (dallage) est
   * créé si cela n'avait pas été fait avant, mais il est toujours conseillé de
   * le faire en dehors de cette méthode, pour controler la taille du dallage.
   * @param tolerance tolérance en dessous de laquelle les noeuds sont fusionés
   */
  public void fusionNoeuds(double tolerance) {
    List<Noeud> aEnlever = new ArrayList<Noeud>(0);
    // initialisation de l'index spatial sur les arcs avec mise à jour
    // automatique
    if (!this.getPopArcs().hasSpatialIndex()) {
      this.getPopArcs().initSpatialIndex(Tiling.class, true);
    }

    this.fireActionPerformed(new ActionEvent(this, 0,
        I18N.getString("CarteTopo.NodesFusion"), this.getPopNoeuds().size())); //$NON-NLS-1$

    for (int index = 0; index < this.getPopNoeuds().size(); index++) {
      Noeud noeud = this.getPopNoeuds().get(index);
      // On cherche les noeuds voisins
      Collection<Noeud> noeudsProches = this.getPopNoeuds()
          .select(noeud.getGeometrie(), tolerance);
      // On enlève les noeuds déjà sélectionnés comme à enlever
      noeudsProches.removeAll(aEnlever);
      if (noeudsProches.size() < 2) {
        continue;// s'il n'y a qu'un seul noeud, c'est le noeud courant
      }
      // Si il y a des voisins, on crée un nouveau noeud
      GM_MultiPoint points = new GM_MultiPoint();
      for (Object o : noeudsProches) {
        points.add(((Noeud) o).getGeometrie());
      }
      // on calcule le centroïde de tous les voisins
      GM_Point centroide = new GM_Point(points.centroid());
      // On crée un nouveau noeud dont la géométrie est le centroïde
      // calculé
      Noeud nouveauNoeud = this.getPopNoeuds().nouvelElement(centroide);
      // On raccroche tous les arcs à ce nouveau noeud
      for (Noeud noeudProche : noeudsProches) {
        // on associe l)e nouveau noeud aux correspondants du noeud
        nouveauNoeud.addAllCorrespondants(noeudProche.getCorrespondants());
        noeudProche.setCorrespondants(new ArrayList<IFeature>(0));
        // on ajoute le noeud à la liste des noeuds à enlever
        if (!aEnlever.contains(noeudProche)) {
          aEnlever.add(noeudProche);
        }
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
        List<Arc> listeArcsAEnlever = new ArrayList<Arc>(0);
        for (Arc arc : noeudProche.arcs()) {
          if (arc.getNoeudIni() == nouveauNoeud
              && arc.getNoeudFin() == nouveauNoeud) {
            if ((Distances.hausdorff(arc.getGeometrie(),
                noeudProche.getGeometrie()) <= tolerance)
                || (arc.getGeometrie().length() < tolerance)) {
              nouveauNoeud.addAllCorrespondants(arc.getCorrespondants());
              arc.setCorrespondants(new ArrayList<IFeature>(0));
              arc.setNoeudIni(null);
              arc.setNoeudFin(null);
              if (!listeArcsAEnlever.contains(arc)) {
                listeArcsAEnlever.add(arc);
              }
            }
          }
        }
        this.enleveArcs(listeArcsAEnlever);
      }
      this.fireActionPerformed(new ActionEvent(this, 1,
          I18N.getString("CarteTopo.NodeFused"), index + 1)); //$NON-NLS-1$
    }

    // on enleve tous les anciens noeuds
    if (!aEnlever.isEmpty() && !this.getPopNoeuds().removeAll(aEnlever)) {
      CarteTopo.logger.warn(I18N.getString("CarteTopo.RemovalFailed")); //$NON-NLS-1$
    }
    List<Arc> listeArcsAEnlever = new ArrayList<Arc>(0);
    // suppression des arcs trop petits
    this.fireActionPerformed(
        new ActionEvent(this, 2, I18N.getString("CarteTopo.SmallEdgesDeletion"), //$NON-NLS-1$
            this.getPopArcs().size()));
    int index = 0;
    for (Arc arc : this.getListeArcs()) {
      if (arc.getNoeudIni() == arc.getNoeudFin() && arc.getNoeudIni() != null) {
        if ((Distances.hausdorff(arc.getGeometrie(),
            arc.getNoeudIni().getGeometrie()) <= tolerance)
            || (arc.getGeometrie().length() < tolerance)) {
          arc.getNoeudIni().addAllCorrespondants(arc.getCorrespondants());
          arc.setCorrespondants(new ArrayList<IFeature>(0));
          arc.setNoeudIni(null);
          arc.setNoeudFin(null);
          if (!listeArcsAEnlever.contains(arc)) {
            listeArcsAEnlever.add(arc);
          }
        }
      } else {
        if (arc.getNoeudIni() == null || arc.getNoeudFin() == null) {
          CarteTopo.logger.error(I18N.getString("CarteTopo.InvalidEdge") + arc); //$NON-NLS-1$
        }
      }
      this.fireActionPerformed(new ActionEvent(this, 3,
          I18N.getString("CarteTopo.EdgeHandled"), ++index)); //$NON-NLS-1$
    }
    this.fireActionPerformed(
        new ActionEvent(this, 4, I18N.getString("CarteTopo.NodesFused"))); //$NON-NLS-1$
    this.enleveArcs(listeArcsAEnlever);
  }

  /**
   * Fusionne en un seul noeud, tous les noeuds contenu dans une même surface de
   * la population de surfaces passée en paramètre. Les correspondants suivent,
   * la topologie arcs/noeuds aussi.
   * <p>
   * NB: les petits arcs qui n'ont plus de sens sont aussi éliminés. Plus
   * précisément ce sont ceux qui partent et arrivent sur un même nouveau noeud
   * créé, et restant dans la surface de fusion. Un index spatial (dallage) est
   * créé si cela n'avait pas été fait avant, mais il est toujours conseillé de
   * le faire en dehors de cette méthode, pour controler la taille du dallage.
   * @param popSurfaces population contenant les surface à fusionner en un seul
   *          noeud
   */
  public void fusionNoeuds(IPopulation<? extends IFeature> popSurfaces) {
    List<IFeature> aEnlever = new ArrayList<IFeature>();
    if (!this.getPopNoeuds().hasSpatialIndex()) {
      this.getPopNoeuds().initSpatialIndex(Tiling.class, true);
    }
    for (IFeature surf : popSurfaces) {
      Collection<Noeud> noeudsProches = this.getPopNoeuds()
          .select(surf.getGeom());
      noeudsProches.removeAll(aEnlever);
      if (noeudsProches.size() < 2) {
        continue;
      }
      // Si il y a plusieurs noeuds dans la surface, on crée un nouveau
      // noeud
      GM_MultiPoint points = new GM_MultiPoint();
      for (Noeud noeudProche : noeudsProches) {
        points.add(noeudProche.getGeometrie());
      }
      GM_Point centroide = new GM_Point(points.centroid());
      Noeud nouveauNoeud = this.getPopNoeuds().nouvelElement();
      nouveauNoeud.setGeometrie(centroide);
      // On raccroche tous les arcs à ce nouveau noeud
      List<Arc> arcsModifies = new ArrayList<Arc>();
      for (Noeud noeudProche : noeudsProches) {
        nouveauNoeud.addAllCorrespondants(noeudProche.getCorrespondants());
        noeudProche.setCorrespondants(new ArrayList<IFeature>(0));
        aEnlever.add(noeudProche);
        // modification de chaque arc du noeud proche à bouger
        for (Arc arc : noeudProche.arcs()) {
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
        for (Arc arc : arcsModifies) {
          if (arc.getNoeudIni() == nouveauNoeud
              && arc.getNoeudFin() == nouveauNoeud) {
            if (surf.getGeom().contains(arc.getGeometrie())) {
              nouveauNoeud.addAllCorrespondants(arc.getCorrespondants());
              arc.setNoeudIni(null);
              arc.setNoeudFin(null);
              this.getPopArcs().remove(arc);
            }
          }
        }
      }
    }
    // on enleve tous les anciens noeuds
    for (IFeature n : aEnlever) {
      this.getPopNoeuds().remove((Noeud) n);
    }
  }

  /**
   * Découpe la carte topo this en fonction des noeuds d'une autre carte topo
   * (ct).
   * <p>
   * En détail: Pour chaque noeud N de la carte topo en entrée, on prend chaque
   * arc de this qui en est proche (c'est-à-dire à moins de
   * distanceMaxNoeudArc). Si aucune des extrémités de cet arc est à moins de
   * distanceMaxProjectionNoeud du noeud N, alors on découpe l'arc en y
   * projetant le noeud N.
   * <p>
   * Si impassesSeulement = true: seules les noeuds N extrémités d'impasse
   * peuvent être projetées La topologie arcs/noeuds, l'orientation et les
   * correspondants suivent. Les arcs de this sont indexés au passage si cela
   * n'avait pas été fait avant.
   */
  public void projete(CarteTopo ct, double distanceMaxNoeudArc,
      double distanceMaxProjectionNoeud, boolean impassesSeulement) {

    if (!this.getPopArcs().hasSpatialIndex()) {
      int nb = (int) Math.sqrt(this.getPopArcs().size() / 20);
      if (nb == 0) {
        nb = 1;
      }
      this.getPopArcs().initSpatialIndex(Tiling.class, true, nb);
    }

    for (Noeud noeud : ct.getPopNoeuds()) {
      if (impassesSeulement && (noeud.arcs().size() != 1)) {
        continue;
      }
      Collection<Arc> closeEdges = this.getPopArcs().select(noeud.getGeom(),
          distanceMaxNoeudArc);
      // logger.debug("Project " + noeud.getGeometrie() + " selected " +
      // closeEdges.size() + " edges");
      for (Arc arc : closeEdges) {
        if (arc.getGeometrie().startPoint().distance(
            noeud.getGeometrie().getPosition()) < distanceMaxProjectionNoeud) {
          continue;
        }
        if (arc.getGeometrie().endPoint().distance(
            noeud.getGeometrie().getPosition()) < distanceMaxProjectionNoeud) {
          continue;
        }
        logger.debug("\t Splitting " + arc);
        List<Arc> splitEdges = arc.projeteEtDecoupe(noeud.getGeometrie());
        if (splitEdges == null) {
          logger.error("ARC = " + arc);
          logger.error("NOEUD = " + noeud);
        } else {
          // Test if there are other parts of the split edges where we
          // could split
          for (Arc edge : splitEdges) {
            edge.projeteEtDecoupe(noeud.getGeometrie(), 1,
                edge.getGeometrie().sizeControlPoint() - 1);
          }
        }
      }
    }
  }

  /**
   * Découpe la carte topo this en fonction de tous les points (noeuds et points
   * intermediaires) d'une autre carte topo (ct).
   * <p>
   * En détail: Pour chaque point P de la carte topo en entrée, on prend chaque
   * arc de this qui en est proche (c'est-à-dire à moins de
   * distanceMaxNoeudArc). Si aucune des extrémités de cet arc est à moins de
   * distanceMaxProjectionNoeud du noeud P, alors on découpe l'arc en y
   * projetant le noeud P. La topologie arcs/noeuds, l'orientation et les
   * correspondants suivent. Les arcs de this sont indexés au passage si cela
   * n'avait pas été fait avant.
   */
  public void projeteTousLesPoints(CarteTopo ct, double distanceMaxNoeudArc,
      double distanceMaxProjectionNoeud) {
    if (!this.getPopArcs().hasSpatialIndex()) {
      int nb = (int) Math.sqrt(this.getPopArcs().size() / 20);
      if (nb == 0) {
        nb = 1;
      }
      this.getPopArcs().initSpatialIndex(Tiling.class, true, nb);
    }
    for (Arc arcCT : ct.getPopArcs()) {
      for (IDirectPosition dp : arcCT.getGeometrie().coord()) {
        if (arcCT.getGeometrie().startPoint()
            .distance(dp) < distanceMaxProjectionNoeud) {
          continue;
        }
        if (arcCT.getGeometrie().endPoint()
            .distance(dp) < distanceMaxProjectionNoeud) {
          continue;
        }
        Collection<Arc> edges = this.getPopArcs().select(dp,
            distanceMaxNoeudArc);
        for (Arc arc : edges) {
          if (arc.getGeometrie().startPoint()
              .distance(dp) < distanceMaxProjectionNoeud) {
            continue;
          }
          if (arc.getGeometrie().endPoint()
              .distance(dp) < distanceMaxProjectionNoeud) {
            continue;
          }
          arc.projeteEtDecoupe(new GM_Point(dp));
        }
      }
    }
  }

  /**
   * Découpe la carte topo this en fonction d'un ensemble de points (GM_Point).
   * <p>
   * En détail: Pour chaque point en entrée, on prend chaque arc de this qui en
   * est proche (c'est-à-dire à moins de distanceMaxNoeudArc). Si aucune des
   * extrémités de cet arc est à moins de distanceMaxProjectionNoeud du noeud N,
   * alors on découpe l'arc en y projetant le noeud N.
   * <p>
   * La topologie arcs/noeuds, l'orientation et les correspondants suivent.
   */
  public void projete(List<IPoint> pts, double distanceMaxNoeudArc,
      double distanceMaxProjectionNoeud) {
    for (IPoint point : pts) {
      Collection<Arc> arcs = this.getPopArcs().select(point,
          distanceMaxNoeudArc);
      for (Arc arc : arcs) {
        if (arc.getGeometrie().startPoint()
            .distance(point.getPosition()) < distanceMaxProjectionNoeud) {
          continue;
        }
        if (arc.getGeometrie().endPoint()
            .distance(point.getPosition()) < distanceMaxProjectionNoeud) {
          continue;
        }
        arc.projeteEtDecoupe(point);
      }
    }
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////
  // Instanciation de la topologie de faces
  // ///////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Crée les faces à partir d'un graphe planaire et instancie la topologie face
   * / arcs. Une face est délimitée par un cycle minimal du graphe.
   * <p>
   * Le paramètre persistant spécifie si les faces créées, ainsi que la
   * topologie, sont rendus persistants. Si oui, il faut appeler cette méthode
   * dans une transaction ouverte.
   * <ul>
   * <li>NB1 : la topologie de réseau arcs/noeuds doit avoir été instanciée.
   * <li>NB2 : une face "extérieure" est créée (sa géométrie entoure le "trou"
   * de l'extérieur qu'est le réseau. Donc, dans le cas d'une topologie complete
   * arcs/faces, tous les arcs ont une face gauche et une face à droite.
   * <li>NB3 : <b>ATTENTION :</b> en cas d'un réseau non connexe, une face
   * extérieure différente est crée pour chaque partie connexe !
   * <li>NB4 : Les culs de sac ont la même face à gauche et à droite, et la face
   * "longe le cul de sac";
   * <li>NB5 : méthode en théorie conçue pour les graphes planaires uniquement
   * (testée dans ce cadre uniquement). La méthode est en théorie valable pour
   * les graphes non planaires, mais les faces créées seront étranges (on ne
   * recrée pas les intersections manquantes, on les ignore). Si il existe des
   * arcs sans noeud initial ou final (topologie de réseau pas complete), alors
   * ces arcs n'ont ni face à gauche, ni face à droite
   * </ul>
   * Depuis la version 1.6 la géométrie des faces est corrigée et n'inclue donc
   * plus un aller-retour sur les culs-de-sac. Les trous sont aussi ajoutés à la
   * géométrie des faces.
   */
  @SuppressWarnings("unchecked")
  public void creeTopologieFaces() {
    List<Arc> arcsDejaTraitesADroite = new ArrayList<Arc>();
    List<Arc> arcsDejaTraitesAGauche = new ArrayList<Arc>();
    List<Arc> arcsDuCycle;
    List<Boolean> orientationsArcsDuCycle;
    ILineString geometrieDuCycle;
    IPopulation<Face> popFaces = this.getPopFaces();
    List<Cycle> cycles = new ArrayList<Cycle>();
    this.fireActionPerformed(
        new ActionEvent(this, 0, I18N.getString("CarteTopo.FaceTopologyEdges"), //$NON-NLS-1$
            this.getPopArcs().size()));
    int iteration = 0;
    /*
     * Parcours de tous les arcs du graphe. Puis, pour chaque arc: - recherche
     * du cycle à droite et du cycle à gauche - creation des faces
     * correspondantes - on note les arcs par lesquels on est déjà passé pour ne
     * pas refaire le travail TODO Regrouper le traitement à droite et à gauche
     * des arcs.
     */
    for (Arc arc : this.getPopArcs()) {
      Face face = null;
      // a droite
      if (!arcsDejaTraitesADroite.contains(arc)) {
        Cycle cycle = arc.cycle(false);
        // face = null;
        if (cycle == null) {
          CarteTopo.logger
              .error(I18N.getString("CarteTopo.RightNullCycle") + arc.getId()); //$NON-NLS-1$
          continue;
        }
        arcsDuCycle = cycle.getArcs();
        orientationsArcsDuCycle = cycle.getOrientationsArcs();
        geometrieDuCycle = cycle.getGeometrie();
        boolean multiGeometrie = false;
        boolean simple = geometrieDuCycle.isSimple();
        if (!simple) {
          List<Cycle> cyclesGeometrieNonSimple = CarteTopo
              .construireGeometrieCycleExterieur(arc, false);
          // la géométrie est corrigée
          if (cyclesGeometrieNonSimple.size() == 1) {
            geometrieDuCycle = cyclesGeometrieNonSimple.get(0).getGeometrie();
          } else {
            for (Cycle cycleCourant : cyclesGeometrieNonSimple) {
              boolean ccw = JtsAlgorithms.isCCW(cycleCourant.getGeometrie());
              if (ccw) {
                cycles.add(cycleCourant);
              } else {
                face = popFaces
                    .nouvelElement(new GM_Polygon(cycleCourant.getGeometrie()));
                CarteTopo.logger.debug("NEW FACE = " + face.getGeometrie());
              }
            }
            multiGeometrie = true;
          }
        }
        if (!multiGeometrie) {
          if (geometrieDuCycle.sizeControlPoint() > 3) {
            boolean ccw = JtsAlgorithms.isCCW(geometrieDuCycle);
            if (ccw) {
              cycles.add(new Cycle(arcsDuCycle, orientationsArcsDuCycle,
                  geometrieDuCycle, false));
            } else {
              face = popFaces.nouvelElement(new GM_Polygon(geometrieDuCycle));
              CarteTopo.logger.debug("NEW FACE = " + face.getGeometrie());
            }
          }
        }
        // if ( persistant ) JeuDeDonnees.db.makePersistent(face);
        this.marquerCycle(arcsDuCycle, orientationsArcsDuCycle, false, face,
            arcsDejaTraitesAGauche, arcsDejaTraitesADroite);
      }
      // a gauche
      if (!arcsDejaTraitesAGauche.contains(arc)) {
        Cycle cycle = arc.cycle(true);
        face = null;
        if (cycle == null) {
          if (CarteTopo.logger.isDebugEnabled()) {
            CarteTopo.logger
                .debug(I18N.getString("CarteTopo.LeftNullCycle") + arc.getId());
          }
          continue;
        }
        arcsDuCycle = cycle.getArcs();
        orientationsArcsDuCycle = cycle.getOrientationsArcs();
        geometrieDuCycle = cycle.getGeometrie();
        boolean multiGeometrie = false;
        boolean simple = geometrieDuCycle.isSimple();
        if (!simple) {
          List<Cycle> cyclesGeometrieNonSimple = CarteTopo
              .construireGeometrieCycleExterieur(arc, true);
          // la géométrie est corrigée
          if (cyclesGeometrieNonSimple.size() == 1) {
            geometrieDuCycle = cyclesGeometrieNonSimple.get(0).getGeometrie();
          } else {
            for (Cycle cycleCourant : cyclesGeometrieNonSimple) {
              boolean ccw = JtsAlgorithms.isCCW(cycleCourant.getGeometrie());
              if (!ccw) {
                cycles.add(cycleCourant);
              } else {
                face = popFaces
                    .nouvelElement(new GM_Polygon(cycleCourant.getGeometrie()));
                CarteTopo.logger.debug("NEW FACE = " + face.getGeometrie());
              }
            }
            multiGeometrie = true;
          }
        }
        if (!multiGeometrie && geometrieDuCycle.sizeControlPoint() > 3) {
          boolean ccw = JtsAlgorithms.isCCW(geometrieDuCycle);
          if (!ccw) {
            cycles.add(new Cycle(arcsDuCycle, orientationsArcsDuCycle,
                geometrieDuCycle, true));
          } else {
            face = popFaces.nouvelElement(new GM_Polygon(geometrieDuCycle));
            if (CarteTopo.logger.isDebugEnabled()) {
              CarteTopo.logger.debug("NEW FACE = " + face.getGeometrie());
            }
          }
        }
        // if ( persistant ) JeuDeDonnees.db.makePersistent(face);
        this.marquerCycle(arcsDuCycle, orientationsArcsDuCycle, true, face,
            arcsDejaTraitesAGauche, arcsDejaTraitesADroite);
      }
      this.fireActionPerformed(new ActionEvent(this, 1,
          I18N.getString("CarteTopo.FaceTopologyEdge"), iteration++)); //$NON-NLS-1$
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
     * création de l'index spatial. On n'active pas la mise à jour automatique
     * afin que, lorsque l'on ajoute la face infinie, elle n'apparaisse pas dans
     * chaque requête select.
     */
    this.getPopFaces().initSpatialIndex(Tiling.class, false);
    Face faceInfinie = null;
    if (this.buildInfiniteFace) {
      IEnvelope envelope = this.getPopArcs().envelope();
      if (envelope != null) {
        faceInfinie = this.getPopFaces()
            .nouvelElement(new GM_Polygon(
                new GM_Envelope(envelope.minX() - 1, envelope.maxX() + 1,
                    envelope.minY() - 1, envelope.maxY() + 1)));
        faceInfinie.setInfinite(true);
      }
      // CarteTopo.logger.debug("INFINITE FACE = " +
      // faceInfinie.getGeometrie());
    }
    this.fireActionPerformed(new ActionEvent(this, 2,
        I18N.getString("CarteTopo.FaceTopologyCycles"), cycles.size())); //$NON-NLS-1$
    iteration = 0;
    for (Cycle cycle : cycles) {
      Face face = ((cycle.isAGauche()
          && cycle.getOrientationsArcs().get(0).booleanValue())
          || (!cycle.isAGauche()
              && !cycle.getOrientationsArcs().get(0).booleanValue()))
                  ? cycle.getArcs().get(0).getFaceGauche()
                  : cycle.getArcs().get(0).getFaceDroite();
      if (face == null) {
        ILineString geom = cycle.getGeometrie();
        // logger.debug("NULL Face for " + cycle);
        if (geom == null || geom.numPoints() < 2 || !geom.isValid()) {
          logger.error("PB WITH " + cycle);
          face = faceInfinie;
        } else {
          Collection<Face> selection = this.getPopFaces().select(geom);
          if (selection.isEmpty()) {
            face = faceInfinie;
          } else {
            selection.removeAll(cycle.getListeFacesInterieuresDuCycle());
            Iterator<Face> it = selection.iterator();
            while (it.hasNext()) {
              if (!it.next().getGeometrie().contains(geom)) {
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
      }
      this.marquerCycle(cycle, face);
      // on ajoute un trou à la géométrie de la face infinie
      if (this.buildInfiniteFace && faceInfinie != null) {
        // CarteTopo.logger.debug("faceInfinie " + face.getGeometrie());
        if (cycle.getGeometrie().sizeControlPoint() > 3) {
          // FIXME That does not look very efficient...
          IPolygon holePolygon = (IPolygon) new GM_Polygon(cycle.getGeometrie())
              .buffer(0);
          // CarteTopo.logger.debug("hole " + holePolygon);
          if (!holePolygon.coord().isEmpty() && holePolygon.coord().size() > 3
              && holePolygon.isValid()) {
            // if (face.getGeometrie().getInterior().isEmpty()) {
            // face.getGeometrie().addInterior(new
            // GM_Ring(cycle.getGeometrie()));
            // CarteTopo.logger.debug("Added as interior");
            // } else {

            // Correction Mickael
            IGeometry geom = faceInfinie.getGeometrie().difference(holePolygon);
            faceInfinie.setGeometrie((IPolygon) geom);

            // Former code :
            // IGeometry geom = face.getGeometrie().difference(holePolygon);
            // faceInfinie.setGeometrie((IPolygon) geom);

            // CarteTopo.logger.debug("Removed from infinite face "
            // + newPolygon);
            // }
          }
          /*
           * IRing trou = new GM_Ring(cycle.getGeometrie()); if
           * (!trou.coord().isEmpty() && trou.coord().size() > 3 &&
           * trou.isValid()) { if (face.getGeometrie().getInterior().isEmpty())
           * { face.getGeometrie().addInterior(trou); } else { // union des
           * trous IPolygon polygonHole = new GM_Polygon(trou); List<IPolygon>
           * trous = new ArrayList<IPolygon>(); for (IRing ring :
           * face.getGeometrie().getInterior()) { trous.add(new
           * GM_Polygon(ring)); } // ajout du nouveau trou à la liste
           * trous.add(polygonHole); if (CarteTopo.logger.isDebugEnabled()) {
           * CarteTopo.logger.debug("Union de " + trous.size() + " trous"); if
           * (CarteTopo.logger.isDebugEnabled()) { for (IPolygon t : trous) {
           * CarteTopo.logger.debug("trou " + t); } } } try { IGeometry union =
           * JtsAlgorithms.union(trous); if (union.isPolygon()) { GM_Polygon
           * polygon = (GM_Polygon) union; // suppression des trous existants
           * face.getGeometrie().getInterior().clear();
           * face.getGeometrie().addInterior(polygon.getExterior()); } else { if
           * (union.isMultiSurface()) { IMultiSurface<IPolygon> multipolygon =
           * (IMultiSurface<IPolygon>) union; // suppression des trous existants
           * face.getGeometrie().getInterior().clear(); for (IPolygon polygon :
           * multipolygon) {
           * face.getGeometrie().addInterior(polygon.getExterior()); } } else {
           * CarteTopo.logger.error(union); } } } catch (Exception e) {
           * CarteTopo.logger.debug("Cycle " + cycle.getGeometrie());
           * CarteTopo.logger.debug(cycle.getArcs().size() + " arcs"); for (Arc
           * arc : cycle.getArcs()) { CarteTopo.logger.debug("arc " + arc); }
           * CarteTopo.logger.debug("face " + face); } } }
           */
        }
      }
      this.fireActionPerformed(new ActionEvent(this, 3,
          I18N.getString("CarteTopo.FaceTopologyCycle"), iteration++)); //$NON-NLS-1$
    }
    // détection des arcs pendants ie des culs-de-sac de la face Infinie
    if (this.buildInfiniteFace && faceInfinie != null) {
      for (Arc arcCourant : faceInfinie.arcs()) {
        if ((arcCourant.getFaceDroite() == null)
            || (arcCourant.getFaceGauche() == null)) {
          continue;
        }
        if (arcCourant.getFaceDroite() == arcCourant.getFaceGauche()) {
          arcCourant.setPendant(true);
        }
      }
    }
    this.fireActionPerformed(
        new ActionEvent(this, 4, I18N.getString("CarteTopo.FaceTopologyEnd"))); //$NON-NLS-1$
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
   * @param arcInitial premier arc du cycle
   * @param aGauche vrai si ce cycle parcours son premier arc à gauche, faux
   *          sinon
   * @return la liste des géométries corrigées du cycle
   */
  public static List<Cycle> construireGeometrieCycleExterieur(Arc arcInitial,
      boolean aGauche) {
    Stack<IDirectPosition> pilePoints = new Stack<IDirectPosition>();
    Stack<List<IDirectPosition>> pileListesPoints = new Stack<List<IDirectPosition>>();
    Stack<Arc> pileArcs = new Stack<Arc>();
    Stack<Boolean> pileOrientations = new Stack<Boolean>();
    List<Object> arcOriente;
    Arc arcCourant = arcInitial;
    boolean sensEnCours = true;
    List<Cycle> listeCycles = new ArrayList<Cycle>();
    /*
     * on parcours le cycle dans le sens anti-trigonometrique, jusqu'à revenir
     * sur this en le parcourant dans le bon sens (précision utile à la gestion
     * des cul-de-sac).
     */
    while (true) {// ajout de l'arc en cours au cycle...
      if (sensEnCours) { // arc dans le bon sens
        IDirectPosition premierPoint = arcCourant.getGeometrie().startPoint();
        if (pilePoints.contains(premierPoint)) {
          boolean dangle = false;
          int index = pilePoints.lastIndexOf(premierPoint);
          List<IDirectPosition> pointsCycle = new ArrayList<IDirectPosition>();
          for (int i = index; i < pilePoints.size(); i++) {
            for (int j = 0; j < pileListesPoints.get(i).size(); j++) {
              if (!pointsCycle.contains(pileListesPoints.get(i).get(j))) {
                pointsCycle.add(pileListesPoints.get(i).get(j));
              } else {
                /*
                 * si le point est le même que le dernier du cycle en
                 * construction, on ne fait rien (le point est doublé dans la
                 * géométrie) sinon, ça veut dire que c'est une impasse
                 */
                if (pointsCycle.indexOf(
                    pileListesPoints.get(i).get(j)) != pointsCycle.size() - 1) {
                  dangle = true;
                }
              }
            }
          }
          List<Arc> arcsCycle = new ArrayList<Arc>(
              pileArcs.subList(index, pileArcs.size()));
          List<Boolean> orientationsCycle = new ArrayList<Boolean>(
              pileOrientations.subList(index, pileOrientations.size()));
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
            Cycle cycle = new Cycle(arcsCycle, orientationsCycle,
                new GM_LineString(AdapterFactory.to2DDirectPositionList(
                    new DirectPositionList(pointsCycle))),
                aGauche);
            listeCycles.add(cycle);
          }
        }
        List<IDirectPosition> listePoints = new ArrayList<IDirectPosition>();
        for (int i = 0; i < arcCourant.getGeometrie().sizeControlPoint()
            - 1; i++) {
          listePoints.add(arcCourant.getGeometrie().getControlPoint(i));
        }
        pilePoints.add(premierPoint);
        pileListesPoints.add(listePoints);
        pileArcs.add(arcCourant);
        pileOrientations.add(sensEnCours);
        arcOriente = aGauche ? arcCourant.arcPrecedentFin()
            : arcCourant.arcSuivantFin();
      } else { // arc dans le sens inverse
        IDirectPosition premierPoint = arcCourant.getGeometrie().endPoint();
        if (pilePoints.contains(premierPoint)) {
          boolean dangle = false;
          int index = pilePoints.lastIndexOf(premierPoint);
          List<IDirectPosition> pointsCycle = new ArrayList<IDirectPosition>();
          for (int i = index; i < pilePoints.size(); i++) {
            for (int j = 0; j < pileListesPoints.get(i).size(); j++) {
              if (!pointsCycle.contains(pileListesPoints.get(i).get(j))) {
                pointsCycle.add(pileListesPoints.get(i).get(j));
              } else {
                /*
                 * si le point est le même que le dernier du cycle en
                 * construction, on ne fait rien (le point est doublé dans la
                 * géométrie) sinon, ça veut dire que c'est une impasse
                 */
                if (pointsCycle.indexOf(
                    pileListesPoints.get(i).get(j)) != pointsCycle.size() - 1) {
                  dangle = true;
                }
              }
            }
          }
          List<Arc> arcsCycle = new ArrayList<Arc>(
              pileArcs.subList(index, pileArcs.size()));
          List<Boolean> orientationsCycle = new ArrayList<Boolean>(
              pileOrientations.subList(index, pileOrientations.size()));
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
            Cycle cycle = new Cycle(arcsCycle, orientationsCycle,
                new GM_LineString(AdapterFactory.to2DDirectPositionList(
                    new DirectPositionList(pointsCycle))),
                aGauche);
            listeCycles.add(cycle);
          }
        }
        List<IDirectPosition> listePoints = new ArrayList<IDirectPosition>();
        for (int i = arcCourant.getGeometrie().sizeControlPoint()
            - 1; i > 0; i--) {
          listePoints.add(arcCourant.getGeometrie().getControlPoint(i));
        }
        pilePoints.add(premierPoint);
        pileListesPoints.add(listePoints);
        pileArcs.add(arcCourant);
        pileOrientations.add(sensEnCours);
        arcOriente = aGauche ? arcCourant.arcPrecedentDebut()
            : arcCourant.arcSuivantDebut();
      }
      if (arcOriente == null) {
        CarteTopo.logger.error(I18N.getString("CarteTopo.Error")); //$NON-NLS-1$
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
      if (arcCourant == arcInitial && sensEnCours) {
        break;
      }
    }
    if (pilePoints.isEmpty()) {
      CarteTopo.logger.error(I18N.getString("CarteTopo.EmptyBoundary")); //$NON-NLS-1$
      return null;
    }
    // ajout du dernier point pour finir la boucle du polygone
    boolean dangle = false;
    List<IDirectPosition> pointsCycle = new ArrayList<IDirectPosition>();
    for (int i = 0; i < pilePoints.size(); i++) {
      for (int j = 0; j < pileListesPoints.get(i).size(); j++) {
        if (!pointsCycle.contains(pileListesPoints.get(i).get(j))) {
          pointsCycle.add(pileListesPoints.get(i).get(j));
        } else {
          /*
           * si le point est le même que le dernier du cycle en construction, on
           * ne fait rien (le point est doublé dans la géométrie) sinon, ça veut
           * dire que c'est une impasse
           */
          if (pointsCycle.indexOf(
              pileListesPoints.get(i).get(j)) != pointsCycle.size() - 1) {
            dangle = true;
          }
        }
      }
    }
    List<Arc> arcsCycle = new ArrayList<Arc>(
        pileArcs.subList(0, pileArcs.size()));
    List<Boolean> orientationsCycle = new ArrayList<Boolean>(
        pileOrientations.subList(0, pileOrientations.size()));
    pointsCycle.add(pointsCycle.get(0));
    if (pointsCycle.size() <= 3 || dangle) {
      return listeCycles;
    }
    Cycle cycle = new Cycle(arcsCycle, orientationsCycle,
        new GM_LineString(AdapterFactory
            .to2DDirectPositionList(new DirectPositionList(pointsCycle))),
        aGauche);
    listeCycles.add(cycle);
    return listeCycles;
  }

  /**
   * détruit les relations topologique d'une face avec tous ses arcs entourants
   * @param face face dont la topologie doit être vidée
   */
  public void videTopologieFace(Face face) {
    for (Arc arc : face.arcs()) {
      arc.setFaceDroite(null);
      arc.setFaceGauche(null);
    }
  }

  /**
   * Ajoute des arcs et des noeuds à la carteTopo this qui ne contient que des
   * faces. Ces arcs sont les arcs entourant les faces. Les relations
   * topologiques arcs/noeuds/surfaces sont instanciées au passage. Les trous
   * sont gérés. Les faces en entrée peuvent avoir une orientation quelconque
   * (direct), cela est géré. Par contre, on ne s'appuie que sur les points
   * intermédiaires existants dans les polygones des faces : les relations
   * topologiques sont donc bien gérés uniquement si les polygones ont des
   * géométrie "compatibles".
   * @param filtrageNoeudsSimples Si ce paramètre est égal à false, alors on
   *          crée un arc et deux noeuds pour chaque segment reliant des points
   *          intermédiaires des surfaces. Si ce paramètre est égal à true,
   *          alors on fusionne les arcs et on ne retient que les noeuds qui ont
   *          3 arcs incidents ou qui servent de point initial/final à une face.
   */
  public void ajouteArcsEtNoeudsAuxFaces(boolean filtrageNoeudsSimples) {
    // On crée un arc pour chaque segment reliant deux points intermédiaires
    // d'une surface
    // Pour deux faces adjacentes, on duplique ces arcs. On fait le ménage
    // après.
    for (Face face : this.getPopFaces()) {
      IPolygon geomFace = face.getGeometrie();
      // gestion du contour
      IDirectPositionList ptsDeLaSurface = geomFace.exteriorCoord();
      boolean sensDirect = Operateurs.sensDirect(ptsDeLaSurface);
      Iterator<IDirectPosition> itPts = ptsDeLaSurface.getList().iterator();
      IDirectPosition pt1 = itPts.next();
      while (itPts.hasNext()) {
        IDirectPosition pt2 = itPts.next();
        Arc arc = this.getPopArcs().nouvelElement();
        if (arc.getId() == 0) {
          logger
              .error("NULL ID for NEW EDGE " + Population.getIdNouvelElement());
        }
        GM_LineString segment = new GM_LineString(pt1, pt2);
        arc.setGeom(segment);
        if (sensDirect) {
          arc.setFaceGauche(face);
        } else {
          arc.setFaceDroite(face);
        }
        pt1 = pt2;
      }
      // gestion des trous
      Iterator<IRing> itTrous = geomFace.getInterior().iterator();
      while (itTrous.hasNext()) {
        IRing trou = itTrous.next();
        IDirectPositionList geomTrou = trou.getPrimitive().coord();
        sensDirect = Operateurs.sensDirect(geomTrou);
        itPts = geomTrou.getList().iterator();
        pt1 = itPts.next();
        while (itPts.hasNext()) {
          IDirectPosition pt2 = itPts.next();
          GM_LineString segment = new GM_LineString();
          segment.addControlPoint(pt1);
          segment.addControlPoint(pt2);
          Arc arc = this.getPopArcs().nouvelElement(segment);
          if (arc.getId() == 0) {
            logger.error(
                "NULL ID for NEW EDGE " + Population.getIdNouvelElement());
          }
          if (sensDirect) {
            arc.setFaceDroite(face);
          } else {
            arc.setFaceGauche(face);
          }
          pt1 = pt2;
        }
      }
    }

    // indexation spatiale des arcs crées
    // on crée un dallage avec en moyenne 20 objets par case
    FT_FeatureCollection<Arc> arcsNonTraites = new FT_FeatureCollection<Arc>(
        this.getPopArcs().getElements());
    int nb = (int) Math.sqrt(arcsNonTraites.size() / 20);
    if (nb == 0) {
      nb = 1;
    }
    arcsNonTraites.initSpatialIndex(Tiling.class, true, nb);

    // filtrage des arcs en double dus aux surfaces adjacentes
    List<Arc> arcsAEnlever = new ArrayList<Arc>(0);
    for (Arc arc : this.getPopArcs()) {
      if (!arcsNonTraites.contains(arc)) {
        continue;
      }
      arcsNonTraites.remove(arc);
      Collection<Arc> arcsProches = arcsNonTraites.select(arc.getGeometrie(),
          0.1);
      CarteTopo.logger.debug("checking edge " + arc);
      for (Arc arc2 : arcsProches) {
        CarteTopo.logger.debug("\t with edge " + arc2);
        // if both edges are in the same direction
        if (arc2.getGeometrie().startPoint()
            .equals2D(arc.getGeometrie().startPoint(), 0.1)
            && arc2.getGeometrie().endPoint()
                .equals2D(arc.getGeometrie().endPoint(), 0.1)) {
          arcsAEnlever.add(arc2);
          arcsNonTraites.remove(arc2);
          if (arc2.getFaceDroite() != null) {
            Face face = arc2.getFaceDroite();
            CarteTopo.logger.debug("Changing edge associated with " + face);
            arc.setFaceDroite(face);
            arc2.setFaceDroite(null);
            if (face.getArcsDirects().contains(arc2)) {
              CarteTopo.logger.error(
                  "edge still in directedges AND IT SHOULD NEVER HAVE BEEN HERE");
            }
            if (face.getArcsIndirects().contains(arc2)) {
              CarteTopo.logger.error("edge still in indirectedges");
            }
          }
          if (arc2.getFaceGauche() != null) {
            Face face = arc2.getFaceGauche();
            CarteTopo.logger.debug("Changing edge associated with " + face);
            arc.setFaceGauche(face);
            arc2.setFaceGauche(null);
            if (face.getArcsIndirects().contains(arc2)) {
              CarteTopo.logger.error(
                  "edge still in indirectedges AND IT SHOULD NEVER HAVE BEEN HERE");
            }
            if (face.getArcsDirects().contains(arc2)) {
              CarteTopo.logger.error("edge still in directedges");
            }
          }
          CarteTopo.logger.debug("same direction");
        }
        // if both edges are in opposite directions
        if (arc2.getGeometrie().startPoint()
            .equals2D(arc.getGeometrie().endPoint(), 0.1)
            && arc2.getGeometrie().endPoint()
                .equals2D(arc.getGeometrie().startPoint(), 0.1)) {
          arcsAEnlever.add(arc2);
          arcsNonTraites.remove(arc2);
          if (arc2.getFaceDroite() != null) {
            Face face = arc2.getFaceDroite();
            CarteTopo.logger.debug("Changing edge associated with " + face);
            arc.setFaceGauche(face);
            arc2.setFaceDroite(null);
            if (face.getArcsDirects().contains(arc2)) {
              CarteTopo.logger.error(
                  "edge still in directedges AND IT SHOULD NEVER HAVE BEEN HERE");
            }
            if (face.getArcsIndirects().contains(arc2)) {
              CarteTopo.logger.error("edge still in indirectedges");
            }
          }
          if (arc2.getFaceGauche() != null) {
            Face face = arc2.getFaceGauche();
            CarteTopo.logger.debug("Changing edge associated with " + face);
            arc.setFaceDroite(face);
            arc2.setFaceGauche(null);
            if (face.getArcsIndirects().contains(arc2)) {
              CarteTopo.logger.error(
                  "edge still in indirectedges AND IT SHOULD NEVER HAVE BEEN HERE");
            }
            if (face.getArcsDirects().contains(arc2)) {
              CarteTopo.logger.error("edge still in directedges");
            }
          }
          CarteTopo.logger.debug("opposite directions");
        }
      }
    }
    arcsNonTraites.clear();
    // logger.info(this.getPopArcs().size() + " edges");
    // logger.info(arcsAEnlever.size() + " edges to remove");
    this.getPopArcs().removeAll(arcsAEnlever);
    // logger.info(this.getPopArcs().size() + " edges");
    // for (Arc arc : this.getPopArcs()) {
    // arc.getFeatureCollections().remove(arcsNonTraites);
    // }
    for (Face face : this.getPopFaces()) {
      List<Arc> list = face.arcs();
      list.retainAll(arcsAEnlever);
      if (!list.isEmpty()) {
        CarteTopo.logger
            .error("remaining " + list.size() + " edges in face " + face);
        for (Arc arc : list) {
          CarteTopo.logger.error(arc);
          face.getArcsDirects().remove(arc);
          face.getArcsIndirects().remove(arc);
        }
      }
    }
    for (Arc arc : arcsNonTraites) {
      arc.getFeatureCollections().remove(arcsNonTraites);
    }
    arcsNonTraites.clear();
    arcsAEnlever.clear();
    Runtime runtime = Runtime.getRuntime();
    runtime.runFinalization();
    runtime.gc();
    // ajout des noeuds et des relations topologiqes arc/noeud
    this.creeNoeudsManquants(0);
    // filtrage de tous les noeuds simples (degré=2)
    if (filtrageNoeudsSimples) {
      this.filtreNoeudsSimples();
    }
    runtime.runFinalization();
    runtime.gc();
    // long heap6 = runtime.totalMemory() - runtime.freeMemory();
    // CarteTopo.logger.info("heap after creation of nodes " + heap6);
  }

  /**
   * Add the missing edges from the graph.
   */
  public void addMissingEdges(double threshold) {
    for (Face face : this.getPopFaces()) {
      IPolygon geomFace = face.getGeometrie();
      // gestion du contour
      IDirectPositionList ptsDeLaSurface = geomFace.exteriorCoord();
      boolean sensDirect = Operateurs.sensDirect(ptsDeLaSurface);
      for (int index = 0; index < ptsDeLaSurface.size() - 1; index++) {
        IDirectPosition pt1 = ptsDeLaSurface.get(index);
        IDirectPosition pt2 = ptsDeLaSurface.get(index + 1);
        Noeud n1 = this.insertOrSelectNode(pt1, threshold);
        Noeud n2 = this.insertOrSelectNode(pt2, threshold);
        GM_LineString segment = new GM_LineString(pt1, pt2);
        Collection<Arc> edges = this.getPopArcs().select(segment, threshold);
        boolean edgeExists = false;
        for (Arc edge : edges) {
          if (Distances.hausdorff(edge.getGeometrie(), segment) < threshold) {
            edgeExists = true;
            boolean edgeDirect = Operateurs
                .sensDirect(edge.getGeometrie().coord());
            if (sensDirect && edgeDirect || !sensDirect && !edgeDirect) {
              edge.setFaceGauche(face);
            } else {
              edge.setFaceDroite(face);
            }
          }
        }
        if (!edgeExists) {
          Arc arc = this.getPopArcs().nouvelElement(segment);
          if (arc.getId() == 0) {
            logger.error(
                "NULL ID for NEW EDGE " + Population.getIdNouvelElement());
          }
          arc.setNoeudIni(n1);
          arc.setNoeudFin(n2);
          if (sensDirect) {
            arc.setFaceGauche(face);
          } else {
            arc.setFaceDroite(face);
          }
        }
      }
    }
    // indexation spatiale des arcs crées
    // on crée un dallage avec en moyenne 20 objets par case
    FT_FeatureCollection<Arc> arcsNonTraites = new FT_FeatureCollection<Arc>(
        this.getPopArcs().getElements());
    int nb = (int) Math.sqrt(arcsNonTraites.size() / 20);
    if (nb == 0) {
      nb = 1;
    }
    arcsNonTraites.initSpatialIndex(Tiling.class, true, nb);

    // filtrage des arcs en double dus aux surfaces adjacentes
    List<Arc> arcsAEnlever = new ArrayList<Arc>(0);
    for (Arc arc : this.getPopArcs()) {
      if (!arcsNonTraites.contains(arc)) {
        continue;
      }
      arcsNonTraites.remove(arc);
      Collection<Arc> arcsProches = arcsNonTraites.select(arc.getGeometrie(),
          0.1);
      CarteTopo.logger.debug("checking edge " + arc);
      for (Arc arc2 : arcsProches) {
        CarteTopo.logger.debug("\t with edge " + arc2);
        // if both edges are in the same direction
        if (arc2.getGeometrie().startPoint()
            .equals2D(arc.getGeometrie().startPoint(), 0.1)
            && arc2.getGeometrie().endPoint()
                .equals2D(arc.getGeometrie().endPoint(), 0.1)) {
          arcsAEnlever.add(arc2);
          arcsNonTraites.remove(arc2);
          if (arc2.getFaceDroite() != null) {
            Face face = arc2.getFaceDroite();
            CarteTopo.logger.debug("Changing edge associated with " + face);
            arc.setFaceDroite(face);
            arc2.setFaceDroite(null);
            if (face.getArcsDirects().contains(arc2)) {
              CarteTopo.logger.error(
                  "edge still in directedges AND IT SHOULD NEVER HAVE BEEN HERE");
            }
            if (face.getArcsIndirects().contains(arc2)) {
              CarteTopo.logger.error("edge still in indirectedges");
            }
          }
          if (arc2.getFaceGauche() != null) {
            Face face = arc2.getFaceGauche();
            CarteTopo.logger.debug("Changing edge associated with " + face);
            arc.setFaceGauche(face);
            arc2.setFaceGauche(null);
            if (face.getArcsIndirects().contains(arc2)) {
              CarteTopo.logger.error(
                  "edge still in indirectedges AND IT SHOULD NEVER HAVE BEEN HERE");
            }
            if (face.getArcsDirects().contains(arc2)) {
              CarteTopo.logger.error("edge still in directedges");
            }
          }
          CarteTopo.logger.debug("same direction");
        }
        // if both edges are in opposite directions
        if (arc2.getGeometrie().startPoint()
            .equals2D(arc.getGeometrie().endPoint(), 0.1)
            && arc2.getGeometrie().endPoint()
                .equals2D(arc.getGeometrie().startPoint(), 0.1)) {
          arcsAEnlever.add(arc2);
          arcsNonTraites.remove(arc2);
          if (arc2.getFaceDroite() != null) {
            Face face = arc2.getFaceDroite();
            CarteTopo.logger.debug("Changing edge associated with " + face);
            arc.setFaceGauche(face);
            arc2.setFaceDroite(null);
            if (face.getArcsDirects().contains(arc2)) {
              CarteTopo.logger.error(
                  "edge still in directedges AND IT SHOULD NEVER HAVE BEEN HERE");
            }
            if (face.getArcsIndirects().contains(arc2)) {
              CarteTopo.logger.error("edge still in indirectedges");
            }
          }
          if (arc2.getFaceGauche() != null) {
            Face face = arc2.getFaceGauche();
            CarteTopo.logger.debug("Changing edge associated with " + face);
            arc.setFaceDroite(face);
            arc2.setFaceGauche(null);
            if (face.getArcsIndirects().contains(arc2)) {
              CarteTopo.logger.error(
                  "edge still in indirectedges AND IT SHOULD NEVER HAVE BEEN HERE");
            }
            if (face.getArcsDirects().contains(arc2)) {
              CarteTopo.logger.error("edge still in directedges");
            }
          }
          CarteTopo.logger.debug("opposite directions");
        }
      }
    }
    arcsNonTraites.clear();
    // logger.info(this.getPopArcs().size() + " edges");
    // logger.info(arcsAEnlever.size() + " edges to remove");
    this.getPopArcs().removeAll(arcsAEnlever);
    // logger.info(this.getPopArcs().size() + " edges");
    // for (Arc arc : this.getPopArcs()) {
    // arc.getFeatureCollections().remove(arcsNonTraites);
    // }
    for (Face face : this.getPopFaces()) {
      List<Arc> list = face.arcs();
      list.retainAll(arcsAEnlever);
      if (!list.isEmpty()) {
        CarteTopo.logger
            .error("remaining " + list.size() + " edges in face " + face);
        for (Arc arc : list) {
          CarteTopo.logger.error(arc);
          face.getArcsDirects().remove(arc);
          face.getArcsIndirects().remove(arc);
        }
      }
    }
    for (Arc arc : arcsNonTraites) {
      arc.getFeatureCollections().remove(arcsNonTraites);
    }
    arcsNonTraites.clear();
    arcsAEnlever.clear();
    Runtime runtime = Runtime.getRuntime();
    runtime.runFinalization();
    runtime.gc();
    // ajout des noeuds et des relations topologiqes arc/noeud
    this.creeNoeudsManquants(0);
    runtime.runFinalization();
    runtime.gc();
    // long heap6 = runtime.totalMemory() - runtime.freeMemory();
    // CarteTopo.logger.info("heap after creation of nodes " + heap6);
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////
  // Pour les calculs de plus court chemin
  // ///////////////////////////////////////////////////////////////////////////////////////////

  private Noeud insertOrSelectNode(IDirectPosition p, double threshold) {
    Noeud result = null;
    Collection<Noeud> nodes = this.getPopNoeuds().select(p, threshold);
    if (!nodes.isEmpty()) {
      result = nodes.iterator().next();
      if (nodes.size() != 1) {
        logger.error(nodes.size() + " nodes for point " + p);
      }
    } else {
      Collection<Arc> edges = this.getPopArcs().select(p, threshold);
      int nbOfNodesCreated = 0;
      for (Arc edge : edges) {
        result = this.splitEdge(edge, p);
        nbOfNodesCreated++;
      }
      if (nbOfNodesCreated > 1) {
        logger.error(nbOfNodesCreated + " nodes created for point " + p);
      }
      if (nbOfNodesCreated == 0) {
        result = this.getPopNoeuds().nouvelElement(p.toGM_Point());
      }
    }
    return result;
  }

  /**
   * Initialise le poids de chaque arc comme étant égal à sa longueur; NB: utile
   * uniquement aux plus courts chemins
   */
  public void initialisePoids() {
    for (Arc arc : this.getPopArcs()) {
      if (arc.getGeometrie() == null) {
        arc.setPoids(0);
      }
      arc.setPoids(arc.longueur());
    }
  }

  // ///////////////////////////////////////////////////////////////////////////////////////////
  // IMPORT: remplissage de la carte topo à partir de Features
  // ///////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Charge en mémoire les éléments de la classe 'nomClasseGeo' et remplit
   * 'this' avec des correspondants de ces éléments.
   * @param nomClasseGeo nom de la classe des éléments à importer
   */
  public void importClasseGeo(String nomClasseGeo) {
    Chargeur.importClasseGeo(nomClasseGeo, this);
  }

  /**
   * Remplit 'this' avec des correspondants des éléments de 'listeFeature'.
   * @param listeFeatures liste des éléments à importer
   */
  public void importClasseGeo(IFeatureCollection<?> listeFeatures) {
    Chargeur.importClasseGeo(listeFeatures, this);
  }

  /**
   * Remplit 'this' avec des correspondants des éléments de 'listeFeature'.
   * Cette version de la méthode autorise la conversion des données en 2D.
   * @param listeFeatures liste des éléments à importer
   * @param is2d si vrai, alors convertir les géométries en 2d
   */
  public void importClasseGeo(IFeatureCollection<?> listeFeatures,
      boolean is2d) {
    Chargeur.importClasseGeo(listeFeatures, this, is2d);
  }

  /**
   * Remplit 'this' avec des correspondants des éléments de 'listeFeature'.
   * Seuls les points des éléments sont importés comme noeuds de la carte.
   * @param listeFeatures liste des éléments à importer
   */
  public void importAsNodes(Collection<? extends IFeature> listeFeatures) {
    Chargeur.importAsNodes(listeFeatures, this);
  }

  /**
   * Remplit 'this' avec des correspondants des éléments de 'listeFeature'.
   * Seuls les points des éléments sont importés comme noeuds de la carte.
   * @param listeFeatures liste des éléments à importer
   */
  public void importAsNodes(IFeatureCollection<?> listeFeatures) {
    Chargeur.importAsNodes(listeFeatures.getElements(), this);
  }

  /**
   * Affecter la face passée en paramètre aux arcs du cycle
   * @param cycle cycle à parcourir afin d'affecter une face à ses arcs
   * @param face face à affecter aux arcs
   */
  public void marquerCycle(Cycle cycle, Face face) {
    this.marquerCycle(cycle.getArcs(), cycle.getOrientationsArcs(),
        cycle.isAGauche(), face, null, null);
  }

  /**
   * Affecter la face passée en paramètre aux arcs parcourus
   * @param arcs arcs à parcourir
   * @param orientations orientations respectives des arcs
   * @param aGauche vrai si le premier arc est parcourus par la gauche, faux
   *          s'il est parcouru par la droite
   * @param face face à affecter aux arcs (du côté déterminé par l'orientation
   *          et la valeur de aGauche)
   * @param arcsDejaTraitesAGauche liste des arcs déjà traités à gauche à
   *          maintenir
   * @param arcsDejaTraitesADroite liste des arcs déjà traités à droite à
   *          maintenir
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
   * Nettoie les pointeurs de la carte topo pour assurer une bonne liberation de
   * la memoire A utiliser lorsque'on souhaite effacer une carte topo.
   */
  public void nettoyer() {
    for (IPopulation<? extends IFeature> pop : this.getPopulations()) {
      for (IFeature f : pop) {
        f.setCorrespondants(new ArrayList<IFeature>(0));
      }
    }
    for (Arc arc : this.getPopArcs()) {
      arc.setNoeudFin(null);
      arc.setNoeudIni(null);
      arc.setFaceDroite(null);
      arc.setFaceGauche(null);
    }
    for (Face face : this.getPopFaces()) {
      face.setArcsPendants(null);
      face.setListeGroupes(null);
    }
    if (this.getPopGroupes() != null) {
      for (Groupe gr : this.getPopGroupes()) {
        gr.setListeArcs(null);
        gr.setListeNoeuds(null);
        gr.setListeFaces(null);
      }
    }
    this.emptyComposants();
    for (IPopulation<? extends IFeature> p : this.getPopulations()) {
      p.clear();
    }
    this.emptyPopulations();
  }

  public void enleveNoeud(Noeud n) {
    // logger.info("removing node " + n + " with " + n.arcs().size() +
    // " edges");
    this.getPopNoeuds().remove(n);
    this.enleveArcs(new HashSet<Arc>(n.arcs()));
  }

  /**
   * Découpe les arcs en utilisant les noeuds plus proches des arcs que la
   * distance demandée.
   * @param distance distance utilisée pour sélectionner les noeuds avec
   *          lesquels découpe
   */
  public void decoupeArcs(double distance) {
    List<Arc> edgesToRemove = new ArrayList<Arc>(0);
    List<Arc> edgesToAdd = new ArrayList<Arc>(0);
    for (Arc currentEdge : this.getPopArcs()) {
      // select close nodes
      Collection<Noeud> intersectedNodes = this.getPopNoeuds()
          .select(currentEdge.getGeometrie(), distance);
      // do not consider the nodes of the current edge
      intersectedNodes.remove(currentEdge.getNoeudIni());
      intersectedNodes.remove(currentEdge.getNoeudFin());
      if (!intersectedNodes.isEmpty()) {
        CarteTopo.logger.debug("Remaing " + intersectedNodes.size() + " nodes"); //$NON-NLS-1$ //$NON-NLS-2$
        CarteTopo.logger.debug("edge " + currentEdge.getGeometrie()); //$NON-NLS-1$
        List<ILineString> lines = new ArrayList<ILineString>();
        lines.add(currentEdge.getGeometrie());
        for (Noeud node : intersectedNodes) {
          IDirectPosition point = node.getGeometrie().getPosition();
          // force the node to be 2d
          point.setCoordinate(2, Double.NaN);
          CarteTopo.logger.debug("node " + node.getGeometrie()); //$NON-NLS-1$
          ILineString line = null;
          double min = Double.POSITIVE_INFINITY;
          for (ILineString l : lines) {
            double d = l.distance(node.getGeometrie());
            if (/* d < distance && */d < min) {
              min = d;
              line = l;
            }
          }
          // project the node on the line and insert the projection
          ILineString nodedLine = Operateurs.projectionEtInsertion(point, line);
          IDirectPosition projectedNode = null;
          min = Double.POSITIVE_INFINITY;
          // find the projected node
          for (IDirectPosition p : nodedLine.getControlPoint()) {
            double d = p.distance(point);
            if (d < min) {
              min = d;
              projectedNode = p;
            }
          }
          CarteTopo.logger
              .debug("projectNode = " + new GM_Point(projectedNode)); //$NON-NLS-1$
          if (projectedNode == null) {
            CarteTopo.logger
                .error("Could not project node on the current edge");
            continue;
          }
          projectedNode.setCoordinate(point.getCoordinate());
          CarteTopo.logger
              .debug("projectNode = " + new GM_Point(projectedNode)); //$NON-NLS-1$
          CarteTopo.logger.debug("nodedLine = " + nodedLine); //$NON-NLS-1$
          DirectPositionList list1 = new DirectPositionList();
          DirectPositionList list2 = new DirectPositionList();
          // build the 2 linestrings (cut by the projected node)
          boolean found = false;
          for (IDirectPosition p : nodedLine.getControlPoint()) {
            if (!found) {
              list1.add(p);
            }
            if (!found && p.equals2D(projectedNode)) {
              found = true;
            }
            if (found) {
              list2.add(p);
            }
          }
          lines.remove(line);
          if (list1.size() > 1) {
            lines.add(new GM_LineString(list1));
          }
          if (list2.size() > 1) {
            lines.add(new GM_LineString(list2));
          }
        }
        CarteTopo.logger.debug("Decomposed into " + lines.size() + " lines"); //$NON-NLS-1$ //$NON-NLS-2$
        edgesToRemove.add(currentEdge);
        for (ILineString l : lines) {
          CarteTopo.logger.debug(l);
          try {
            Arc edge = this.getPopArcs().getClasse().getConstructor().newInstance();// new
                                                                   // Arc();//
            // this.getPopArcs().nouvelElement(l);
            int id = Population.getIdNouvelElement() + 1;
            edge.setId(id);
            Population.setIdNouvelElement(id);
            if (edge.getId() == 0) {
              logger.error(
                  "NULL ID for NEW EDGE " + Population.getIdNouvelElement());
            }
            edge.setGeometrie(l);
            Collection<Noeud> nodes = this.getPopNoeuds()
                .select(new GM_Point(l.getControlPoint(0)), distance);
            edge.setNoeudIni(nodes.iterator().next());
            nodes = this.getPopNoeuds().select(
                new GM_Point(l.getControlPoint(l.sizeControlPoint() - 1)),
                distance);
            edge.setNoeudFin(nodes.iterator().next());
            edge.setCorrespondants(currentEdge.getCorrespondants());
            edgesToAdd.add(edge);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
    for (Arc edge : edgesToRemove) {
      edge.setCorrespondants(new ArrayList<IFeature>(0));
      edge.setNoeudIni(null);
      edge.setNoeudFin(null);
      this.getPopArcs().remove(edge);
    }
    this.getPopArcs().addAll(edgesToAdd);
  }

  /**
   * Découpe les arcs en utilisant les points plus proches des arcs que la
   * distance demandée.
   * @param distance distance utilisée pour sélectionner les points avec
   *          lesquels découpe
   */
  public void splitEdgesWithPoints(double distance) {
    List<Arc> edges = new ArrayList<Arc>(this.getPopArcs());
    for (Arc currentEdge : edges) {
      CarteTopo.cleanLineString(currentEdge.getGeometrie(), distance);
      List<Integer> points = new ArrayList<Integer>();
      for (int i = 1; i < currentEdge.getGeometrie().sizeControlPoint()
          - 1; i++) {
        IDirectPosition currentPoint = currentEdge.getGeometrie()
            .getControlPoint(i);
        Collection<Arc> closeEdges = this.getPopArcs()
            .select(currentPoint.toGM_Point().buffer(distance));
        closeEdges.remove(currentEdge);
        for (Arc edge : closeEdges) {
          if (edge.getNoeudIni().getGeometrie()
              .distance(currentPoint.toGM_Point()) > distance
              && edge.getNoeudFin().getGeometrie()
                  .distance(currentPoint.toGM_Point()) > distance) {
            points.add(i);
            break;
          }
        }
      }
      if (!points.isEmpty()) {
        this.splitEdge(currentEdge, points);
      }
    }
  }

  private Arc mergeNode(Arc a1, Arc a2, Arc a, Noeud n) {
    Noeud n1 = a1.getNoeudIni();
    Noeud n2 = a2.getNoeudFin();
    a.setNoeudIni(n1);
    a.setNoeudFin(n2);
    // a.setCoord(n1.getCoord(), n2.getCoord());
    a.setPoids(a.getGeometrie().length());
    a.setOrientation(a1.getOrientation());
    a.setCorrespondants(a1.getCorrespondants());
    // this.enleveArc(a1);
    // this.enleveArc(a2);
    this.enleveNoeud(n);
    return a;
  }

  public Noeud splitEdge(Arc a, IDirectPosition p) {
    int index = Operateurs.insertionIndex(p,
        a.getGeometrie().getControlPoint().getList());
    int orientation = a.getOrientation();
    Noeud n = this.getPopNoeuds().nouvelElement(new GM_Point(p));
    IDirectPositionList l1 = new DirectPositionList(
        new ArrayList<IDirectPosition>(
            a.getGeometrie().getControlPoint().getList().subList(0, index)));
    if (l1.isEmpty()) {
      CarteTopo.logger
          .error(p + " empty " + index + " for " + a.getGeometrie());
    }
    l1.add(p);
    IDirectPositionList l2 = new DirectPositionList(
        new ArrayList<IDirectPosition>(a.getGeometrie().getControlPoint()
            .getList().subList(index, a.getGeometrie().sizeControlPoint())));
    if (l2.isEmpty()) {
      CarteTopo.logger
          .error(p + " empty " + index + " for " + a.getGeometrie());
    }
    l2.add(0, p);
    Arc a1 = this.getPopArcs().nouvelElement(new GM_LineString(l1));
    if (a1.getId() == 0) {
      logger.error("NULL ID for NEW EDGE " + Population.getIdNouvelElement());
    }
    Arc a2 = this.getPopArcs().nouvelElement(new GM_LineString(l2));
    if (a2.getId() == 0) {
      logger.error("NULL ID for NEW EDGE " + Population.getIdNouvelElement());
    }
    a1.setNoeudIni(a.getNoeudIni());
    a1.setNoeudFin(n);
    a2.setNoeudIni(n);
    a2.setNoeudFin(a.getNoeudFin());
    // a1.setCoord(a.getNoeudIni().getCoord(), n.getCoord());
    // a2.setCoord(n.getCoord(), a.getNoeudFin().getCoord());
    a1.setPoids(a1.getGeometrie().length());
    a2.setPoids(a2.getGeometrie().length());
    a1.setOrientation(orientation);
    a2.setOrientation(orientation);
    a1.setCorrespondants(a.getCorrespondants());
    a2.setCorrespondants(a.getCorrespondants());
    this.enleveArc(a);
    return n;
  }

  private void splitEdge(Arc a, List<Integer> indices) {
    List<Noeud> nodes = new ArrayList<Noeud>(indices.size());
    for (Integer index : indices) {
      Noeud n = this.getPopNoeuds()
          .nouvelElement(new GM_Point(a.getGeometrie().getControlPoint(index)));
      nodes.add(n);
    }
    int orientation = a.getOrientation();
    List<IDirectPositionList> list = new ArrayList<IDirectPositionList>(
        indices.size());
    int previousIndex = 0;
    for (int i = 0; i < indices.size(); i++) {
      int index = indices.get(i);
      IDirectPosition p = a.getGeometrie().getControlPoint(index);
      IDirectPositionList l = new DirectPositionList(
          new ArrayList<IDirectPosition>(a.getGeometrie().getControlPoint()
              .getList().subList(previousIndex, index + 1)));
      if (l.isEmpty() || l.size() < 2) {
        CarteTopo.logger
            .error(p + " empty " + index + " for " + a.getGeometrie());
        for (Integer ind : indices) {
          CarteTopo.logger.error("" + ind);
        }
      }
      list.add(l);
      previousIndex = index;
    }
    IDirectPositionList last = new DirectPositionList(
        new ArrayList<IDirectPosition>(
            a.getGeometrie().getControlPoint().getList().subList(previousIndex,
                a.getGeometrie().sizeControlPoint())));
    if (last.isEmpty() || last.size() < 2) {
      CarteTopo.logger
          .error(" empty " + previousIndex + " for " + a.getGeometrie());
      for (Integer ind : indices) {
        CarteTopo.logger.error("" + ind);
      }
    }
    Noeud previousNode = a.getNoeudIni();
    for (int i = 0; i < list.size(); i++) {
      Arc edge = this.getPopArcs()
          .nouvelElement(new GM_LineString(list.get(i)));
      if (edge.getId() == 0) {
        logger.error("NULL ID for NEW EDGE " + Population.getIdNouvelElement());
      }
      edge.setNoeudIni(previousNode);
      edge.setNoeudFin(nodes.get(i));
      edge.setPoids(edge.getGeometrie().length());
      edge.setOrientation(orientation);
      edge.setCorrespondants(a.getCorrespondants());
      previousNode = nodes.get(i);
    }
    Arc edge = this.getPopArcs().nouvelElement(new GM_LineString(last));
    if (edge.getId() == 0) {
      logger.error("NULL ID for NEW EDGE " + Population.getIdNouvelElement());
    }
    edge.setNoeudIni(previousNode);
    edge.setNoeudFin(a.getNoeudFin());
    edge.setPoids(edge.getGeometrie().length());
    edge.setOrientation(orientation);
    edge.setCorrespondants(a.getCorrespondants());
    this.enleveArc(a);
  }

  public Groupe shortestPath(IDirectPosition x1, IDirectPosition x2, Arc a1,
      Arc a2, double max) {
    Noeud n1 = a1.getNoeudIni();
    boolean a1Split = false;
    if (!x1.equals(n1.getCoord())) {
      n1 = a1.getNoeudFin();
      if (!x1.equals(n1.getCoord())) {
        // x1 is neither of the nodes of a1 : add a new node
        n1 = this.splitEdge(a1, x1);
        a1Split = true;
        // logger.info(n1);
        // logger.info(n1.getEntrants().get(0));
        // logger.info(n1.getSortants().get(0));
      }
    }
    // logger.info("a1 split "+a1Split);
    Noeud n2 = a2.getNoeudIni();
    boolean a2Split = false;
    // logger.info("x2 " + x2);
    // logger.info("n2 " + n2);
    if (!x2.equals(n2.getCoord())) {
      n2 = a2.getNoeudFin();
      if (!x2.equals(n2.getCoord())) {
        // x2 is neither of the nodes of a2 : add a new node
        n2 = this.splitEdge(a2, x2);
        a2Split = true;
        // logger.info(n2);
        // logger.info(n2.getEntrants().get(0));
        // logger.info(n2.getSortants().get(0));
      }
    }
    // logger.info("a2 split "+a2Split);
    Groupe path = n1.plusCourtChemin(n2, max);
    if (path != null) {
      double length = path.longueur();
      path.setLength(length);
      Noeud n = n1;
      List<IDirectPosition> list = new ArrayList<IDirectPosition>(0);
      for (Arc a : path.getListeArcs()) {
        List<IDirectPosition> points = a.getGeometrie().getControlPoint()
            .getList();
        boolean direct = false;
        if (a.getNoeudIni() == n) {
          direct = true;
        }
        if (direct) {
          for (int i = 0; i < points.size(); i++) {
            IDirectPosition p = points.get(i);
            if (list.isEmpty() || !p.equals(list.get(list.size() - 1))) {
              list.add(p);
            }
          }
        } else {
          for (int i = points.size() - 1; i >= 0; i--) {
            IDirectPosition p = points.get(i);
            if (list.isEmpty() || !p.equals(list.get(list.size() - 1))) {
              list.add(p);
            }
          }
        }
        if (direct) {
          n = a.getNoeudFin();
        } else {
          n = a.getNoeudIni();
        }
      }
      if (list.size() < 2) {
        CarteTopo.logger.debug(x1.toGM_Point() + " " + x2.toGM_Point());
        CarteTopo.logger.debug(" " + a1.getGeometrie());
        CarteTopo.logger.debug(" " + a2.getGeometrie());
      }
      path.setGeom(new GM_LineString(new DirectPositionList(list)));
    }
    if (a1Split) {
      if (path != null && path.getListeArcs() != null) {
        path.getListeArcs().remove(n1.getEntrants().get(0));
        path.getListeArcs().remove(n1.getSortants().get(0));
        path.getListeArcs().add(0, a1);
      }
      Arc a = this.mergeNode(n1.getEntrants().get(0), n1.getSortants().get(0),
          a1, n1);
      this.addArc(a);
      if (a.getNoeudIni() == null) {
        CarteTopo.logger.error("initial node null " + a);
      }
      if (a.getNoeudFin() == null) {
        CarteTopo.logger.error("final node null " + a);
      }
    }
    if (a2Split) {
      if (path != null && path.getListeArcs() != null) {
        path.getListeArcs().remove(n2.getEntrants().get(0));
        path.getListeArcs().remove(n2.getSortants().get(0));
        path.getListeArcs().add(a2);
      }
      Arc a = this.mergeNode(n2.getEntrants().get(0), n2.getSortants().get(0),
          a2, n2);
      this.addArc(a);
      if (a.getNoeudIni() == null) {
        CarteTopo.logger.error("initial node null " + a);
      }
      if (a.getNoeudFin() == null) {
        CarteTopo.logger.error("final node null " + a);
      }
    }
    return path;
  }
}
