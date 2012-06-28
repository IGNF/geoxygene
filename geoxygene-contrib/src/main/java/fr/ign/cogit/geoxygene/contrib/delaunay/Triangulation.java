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

package fr.ign.cogit.geoxygene.contrib.delaunay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Classe mère de la triangulation construite sur la bibliothèque Triangle de
 * Jonathan Richard Shewchuk. Triangulation class used on top of Jonathan
 * Richard Shewchuk's Triangle library.
 * 
 * @author Bonin
 * @author Julien Perret
 * @version 1.1
 */

public class Triangulation extends AbstractTriangulation {

  /**
   * Constructor.
   */
  public Triangulation() {
    super("Triangulation"); //$NON-NLS-1$
  }

  /**
   * @param nom_logique
   */
  public Triangulation(String nom_logique) {
    super(nom_logique);
  }

  private Triangulateio jin = new Triangulateio();
  private Triangulateio jout = new Triangulateio();
  private Triangulateio jvorout = new Triangulateio();

  /**
   * Convert the node collection into an array
   */
  private void convertJin() {
    NoeudDelaunay node;
    IDirectPosition coord;
    List<Noeud> noeuds = new ArrayList<Noeud>(this.getListeNoeuds());
    this.jin.numberofpoints = noeuds.size();
    this.jin.pointlist = new double[2 * this.jin.numberofpoints];
    for (int i = 0; i < noeuds.size(); i++) {
      node = (NoeudDelaunay) noeuds.get(i);
      coord = node.getGeometrie().getPosition();
      this.jin.pointlist[2 * i] = coord.getX();
      this.jin.pointlist[2 * i + 1] = coord.getY();
    }
  }

  /**
   * Convert the edges into an array.
   */
  private void convertJinSegments() {
    ArrayList<IFeature> noeuds = new ArrayList<IFeature>(this.getListeNoeuds());
    ArrayList<IFeature> aretes = new ArrayList<IFeature>(this.getListeArcs());
    this.jin.numberofsegments = aretes.size();
    this.jin.segmentlist = new int[2 * this.jin.numberofsegments];
    for (int i = 0; i < this.jin.numberofsegments; i++) {
      this.jin.segmentlist[2 * i] = noeuds
          .indexOf(((ArcDelaunay) aretes.get(i)).getNoeudIni());
      this.jin.segmentlist[2 * i + 1] = noeuds.indexOf(((ArcDelaunay) aretes
          .get(i)).getNoeudFin());
    }
  }

  /**
   * Convert back the result into vertices, edges and triangles.
   */
  private void convertJout() {
    if (CarteTopo.logger.isDebugEnabled()) {
      CarteTopo.logger.debug(I18N.getString("Triangulation.ExportStart")); //$NON-NLS-1$
    }
    try {
      if (CarteTopo.logger.isDebugEnabled()) {
        CarteTopo.logger.debug(I18N.getString("Triangulation.NodeExportStart")); //$NON-NLS-1$
      }
      for (int i = this.jin.numberofpoints; i < this.jout.numberofpoints; i++) {
        this.getPopNoeuds()
            .nouvelElement()
            .setCoord(
                new DirectPosition(this.jout.pointlist[2 * i],
                    this.jout.pointlist[2 * i + 1]));
      }
      ArrayList<Noeud> noeuds = new ArrayList<Noeud>(
          this.getListeNoeuds());
      Class<?>[] signaturea = { this.getPopNoeuds().getClasse(),
          this.getPopNoeuds().getClasse() };
      Object[] parama = new Object[2];
      if (CarteTopo.logger.isDebugEnabled()) {
        CarteTopo.logger.debug(I18N.getString("Triangulation.EdgeExportStart")); //$NON-NLS-1$
      }
      for (Noeud n : this.getPopNoeuds()) {
          n.getEntrants().clear();
          n.getSortants().clear();
      }
      CarteTopo.logger.error(this.getPopArcs().size() + " edges");
      for (int i = 0; i < this.jout.numberofedges; i++) {
        Noeud n1 = noeuds.get(this.jout.edgelist[2 * i]);
        Noeud n2 = noeuds.get(this.jout.edgelist[2 * i + 1]);
        parama[0] = n1;
        parama[1] = n2;
        Set<Arc> edges = new HashSet<Arc>(n1.arcs());
        edges.retainAll(n2.arcs());
        if (edges.isEmpty()) {
            this.getPopArcs().nouvelElement(signaturea, parama).setId(i);
        } else {
            CarteTopo.logger.error("filtered edge " + i + " because of edge " + edges.iterator().next());
        }
      }
      CarteTopo.logger.error(this.getPopArcs().size() + " edges created instead of " + this.jout.numberofedges);
      Class<?>[] signaturef = { this.getPopNoeuds().getClasse(),
          this.getPopNoeuds().getClasse(), this.getPopNoeuds().getClasse() };
      Object[] paramf = new Object[3];
      if (CarteTopo.logger.isDebugEnabled()) {
        CarteTopo.logger.debug(I18N
            .getString("Triangulation.TriangleExportStart")); //$NON-NLS-1$
      }
      for (int i = 0; i < this.jout.numberoftriangles; i++) {
        paramf[0] = noeuds.get(this.jout.trianglelist[3 * i]);
        if (paramf[0] == null) {
            CarteTopo.logger.error("null node " + this.jout.trianglelist[3 * i] + " for triangle " + i);
        }
        paramf[1] = noeuds.get(this.jout.trianglelist[3 * i + 1]);
        if (paramf[1] == null) {
            CarteTopo.logger.error("null node " + this.jout.trianglelist[3 * i + 1] + " for triangle " + i);
        }
        paramf[2] = noeuds.get(this.jout.trianglelist[3 * i + 2]);
        if (paramf[2] == null) {
            CarteTopo.logger.error("null node " + this.jout.trianglelist[3 * i + 2] + " for triangle " + i);
        }
        this.getPopFaces().nouvelElement(signaturef, paramf).setId(i);
      }
      if (this.getOptions().indexOf('v') != -1) {
        if (CarteTopo.logger.isDebugEnabled()) {
          CarteTopo.logger.debug(I18N
              .getString("Triangulation.VoronoiDiagramExportStart")); //$NON-NLS-1$
        }
        IEnvelope envelope = this.getPopNoeuds().envelope();
        envelope.expandBy(100);
        this.getPopVoronoiVertices().initSpatialIndex(Tiling.class, true,
            envelope, 10);
        // l'export du diagramme de voronoi
        for (int i = 0; i < this.jvorout.numberofpoints; i++) {
          this.getPopVoronoiVertices().add(
              new Noeud(new GM_Point(new DirectPosition(
                  this.jvorout.pointlist[2 * i],
                  this.jvorout.pointlist[2 * i + 1]))));
        }
        for (int i = 0; i < this.jvorout.numberofedges; i++) {
          int indexIni = this.jvorout.edgelist[2 * i];
          int indexFin = this.jvorout.edgelist[2 * i + 1];
          if (indexFin == -1) {
            // infinite edge
            double vx = this.jvorout.normlist[2 * i];
            double vy = this.jvorout.normlist[2 * i + 1];
            Noeud c1 = this.getPopVoronoiVertices().getElements().get(indexIni);
            Noeud c2 = new Noeud();
            double vectorSize = 10000000;
            c2.setGeometrie(new GM_Point(new DirectPosition(c1.getGeometrie()
                .getPosition().getX()
                + vectorSize * vx, c1.getGeometrie().getPosition().getY()
                + vectorSize * vy)));
            GM_LineString line = new GM_LineString(new DirectPositionList(
                Arrays.asList(c1.getGeometrie().getPosition(), c2
                    .getGeometrie().getPosition())));
            IGeometry intersection = line.intersection(envelope.getGeom());
            IDirectPositionList list = intersection.coord();
            if (list.size() > 1) {
              c2.setGeometrie(list.get(1).toGM_Point());
            }
            indexFin = this.getPopVoronoiVertices().size();
            this.getPopVoronoiVertices().add(c2);
          }
          this.getPopVoronoiEdges().add(
              new Arc(this.getPopVoronoiVertices().getElements().get(indexIni),
                  this.getPopVoronoiVertices().getElements().get(indexFin)));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    this.filtreArcsDoublons();
    if (CarteTopo.logger.isDebugEnabled()) {
      CarteTopo.logger.debug(I18N.getString("Triangulation.ExportEnd")); //$NON-NLS-1$
    }
  }

  /**
   * Méthode de triangulation proprment dite en C - va chercher la bibliothèque
   * C (dll/so).
   * 
   * @param trianguleOptions
   * @param trianguleJin
   * @param trianguleJout
   * @param trianguleJvorout
   */
  private native void trianguleC(String trianguleOptions,
      Triangulateio trianguleJin, Triangulateio trianguleJout,
      Triangulateio trianguleJvorout);

  static {
    System.loadLibrary("trianguledll");} //$NON-NLS-1$

  @Override
  public void create() throws Exception {
    this.convertJin();
    if (this.getOptions().indexOf('p') != -1) {
      this.convertJinSegments();
      this.getPopArcs().setElements(new ArrayList<Arc>());
    }
    if (this.getOptions().indexOf('v') != -1) {
      this.trianguleC(this.getOptions(), this.jin, this.jout, this.jvorout);
    } else {
      this.trianguleC(this.getOptions(), this.jin, this.jout, null);
    }
    this.convertJout();
  }

  /**
   * Computes the characteristic shape of the triangulation created using the
   * points of the input feature collection.
   * <p>
   * This algorithm implements the method described in: "Efficient generation of
   * simple polygons for characterizing the shape of a set of points in the
   * plane", Matt Duckham, Lars Kulik, Mike Worboys, Antony Galton, 2008.
   * 
   * @param featureCollection a feature collection
   * @param alpha the length threshold for the characteristic shape algorithm
   * @return the characteristic shape of the input feature collection
   */
  public static GM_Polygon getCharacteristicShape(
      Collection<? extends IFeature> featureCollection, double alpha) {
    if (CarteTopo.logger.isDebugEnabled()) {
      CarteTopo.logger.debug("Creating the triangulation");
    }
    Triangulation t = new Triangulation("Triangulation");
    t.importAsNodes(featureCollection);
    try {
      t.triangule();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    if (CarteTopo.logger.isDebugEnabled()) {
      CarteTopo.logger.debug("Creation of the triangulation finished");
    }
    GM_Polygon shape = t.getCharacteristicShape(alpha);
    // cleaning up
    t.nettoyer();
    return shape;
  }

  /**
   * Computes the characteristic shape of the triangulation created using the
   * points of the input feature collection.
   * <p>
   * This algorithm implements the method described in: "Efficient generation of
   * simple polygons for characterizing the shape of a set of points in the
   * plane", Matt Duckham, Lars Kulik, Mike Worboys, Antony Galton, 2008.
   * 
   * @param featureCollection a feature collection
   * @param alpha the length threshold for the characteristic shape algorithm
   * @return the characteristic shape of the input feature collection
   */
  public static GM_Polygon getCharacteristicShape(
      IFeatureCollection<? extends IFeature> featureCollection, double alpha) {
    return Triangulation.getCharacteristicShape(
        featureCollection.getElements(), alpha);
  }
}
