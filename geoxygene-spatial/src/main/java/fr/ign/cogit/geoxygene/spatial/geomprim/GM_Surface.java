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

package fr.ign.cogit.geoxygene.spatial.geomprim;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ISurfacePatch;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurface;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * Surface, composée de morceaux de surface. L'orientation vaut nécessairement
 * +1.
 * <P>
 * Modification de la norme suite au retour d'utilisation : on fait hériter
 * GM_SurfacePatch de GM_Surface. Du coup, on n'implémente plus l'interface
 * GM_GenericSurface.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 */
public class GM_Surface extends GM_OrientableSurface implements ISurface
/* implements GM_GenericSurface */{
  static Logger logger = LogManager.getLogger(GM_Surface.class.getName());
  // TODO : validate : pour que les patch soient contigue ?
  // pour cela il faut regarder que l'union est du type GM_Surface et non
  // GM_MultiSurface
  /** Liste des morceaux constituant la surface. */
  protected List<ISurfacePatch> patch;

  @Override
  public List<ISurfacePatch> getPatch() {
    return this.patch;
  }

  @Override
  public ISurfacePatch getPatch(int i) {
    if ((ISurfacePatch.class).isAssignableFrom(this.getClass())) {
      if (i != 0) {
        GM_Surface.logger
            .error("Recherche d'un patch avec i<>0 alors qu'un GM_SurfacePatch ne contient qu'un segment qui est lui-meme"); //$NON-NLS-1$
        return null;
      }
      return this.patch.get(i);
    }
    return this.patch.get(i);
  }

  @Override
  public void setPatch(int i, ISurfacePatch value) {
    if ((ISurfacePatch.class).isAssignableFrom(this.getClass())) {
      if (i != 0) {
        GM_Surface.logger
            .error("Affection d'un patch avec i<>0 alors qu'un GM_SurfacePatch ne contient qu'un segment qui est lui-meme. La méthode ne fait rien."); //$NON-NLS-1$
      } else {
        this.patch.set(i, value);
      }
    } else {
      this.patch.set(i, value);
    }
  }

  @Override
  public void addPatch(ISurfacePatch value) {
    if ((ISurfacePatch.class).isAssignableFrom(this.getClass())) {
      if (this.sizePatch() > 0) {
        GM_Surface.logger
            .error("Ajout d'un patch alors qu'un GM_SurfacePatch ne contient qu'un segment qui est lui-meme. La méthode ne fait rien."); //$NON-NLS-1$
      } else {
        this.patch.add(value);
      }
    } else {
      this.patch.add(value);
    }
  }

  @Override
  public void addPatch(int i, ISurfacePatch value) {
    if ((ISurfacePatch.class).isAssignableFrom(this.getClass())) {
      if (i != 0) {
        GM_Surface.logger
            .error("Ajout d'un patch avec i<>0 alors qu'un GM_SurfacePatch ne contient qu'un segment qui est lui-meme. La méthode ne fait rien."); //$NON-NLS-1$
      } else {
        this.patch.add(value);
      }
    } else {
      this.patch.add(i, value);
    }
  }

  @Override
  public void removePatch(ISurfacePatch value) {
    if ((ISurfacePatch.class).isAssignableFrom(this.getClass())) {
      GM_Surface.logger
          .error("removePatch() : Ne fait rien car un GM_SurfacePatch ne contient qu'un segment qui est lui-meme."); //$NON-NLS-1$
    } else {
      this.patch.remove(value);
    }
  }

  @Override
  public void removePatch(int i) {
    if ((ISurfacePatch.class).isAssignableFrom(this.getClass())) {
      GM_Surface.logger
          .error("removePatch() : Ne fait rien car un GM_SurfacePatch ne contient qu'un segment qui est lui-meme."); //$NON-NLS-1$
    } else {
      this.patch.remove(i);
    }
  }

  @Override
  public int sizePatch() {
    return this.patch.size();
  }

  /** Constructeur par défaut */
  public GM_Surface() {
    this.patch = new ArrayList<ISurfacePatch>(0);
    this.orientation = +1;
    this.primitive = this;
    /*
     * this.proxy[0] = this; GM_OrientableSurface proxy1 = new
     * GM_OrientableSurface(); proxy1.orientation = -1; proxy1.proxy[0] = this;
     * proxy1.proxy[1] = proxy1; proxy1.primitive = new GM_Surface(this);
     * this.proxy[1] = proxy1;
     */
  }

  /** Constructeur à partir d'un et d'un seul surface patch */
  public GM_Surface(ISurfacePatch thePatch) {
    this();
    this.addPatch(thePatch);
  }

  /**
   * Utilisé en interne (dans les constructeurs publics) pour construire la
   * surface opposée, qui est la primitive de proxy[1]. On définit ici les
   * références nécessaires. Le but est de retrouver la propriete :
   * surface.getNegative().getPrimitive().getNegative().getPrimitive() =
   * surface. La frontiere de la surface est calculee en dynamique lors de
   * l'appel a la methode getNegative().
   */
  public GM_Surface(ISurface surface) {
    this.patch = new ArrayList<ISurfacePatch>(0);
    this.orientation = +1;
    this.primitive = this;
    /*
     * this.proxy[0] = this; GM_OrientableSurface proxy1 = new
     * GM_OrientableSurface(); proxy1.orientation = -1; proxy1.proxy[0] = this;
     * proxy1.proxy[1] = proxy1; proxy1.primitive = surface; this.proxy[1] =
     * proxy1;
     */
  }

  /**
   * NON IMPLEMENTE. Vecteur normal à self au point passé en paramètre.
   */
  /*
   * public Vecteur upNormal(DirectPosition point) { return null; }
   */
  @Override
  public double perimeter() {
    // return SpatialQuery.perimeter(this); (ancienne methode avec SDOAPI)
    return this.length();
  }

  @Override
  public ILineString exteriorLineString() {
    if (this.sizePatch() == 1) {
      IPolygon poly = (IPolygon) this.getPatch(0);
      IRing ext = poly.getExterior();
      if (ext != null) {
        ICurve c = ext.getPrimitive();
        ILineString ls = c.asLineString(0.0, 0.0, 0.0);
        return ls;
      }
      GM_Surface.logger
          .error("GM_Surface::exteriorLineString() : ATTENTION frontiere null"); //$NON-NLS-1$
      return null;
    }
    GM_Surface.logger
        .error("GM_Surface::exteriorLineString() : cette méthode ne fonctionne que si la surface est composée d'un seul patch."); //$NON-NLS-1$
    return null;
  }

  @Override
  public ICurve exteriorCurve() {
    if (this.sizePatch() == 1) {
      IPolygon poly = (IPolygon) this.getPatch(0);
      IRing ext = poly.getExterior();
      if (ext != null) {
        return ext.getPrimitive();
      }
      GM_Surface.logger
          .error("GM_Surface::exteriorCurve() : ATTENTION frontiere null"); //$NON-NLS-1$
      return null;
    }
    GM_Surface.logger
        .error("GM_Surface::exteriorCurve() : cette méthode ne fonctionne que si la surface est composée d'un seul patch."); //$NON-NLS-1$
    return null;
  }

  @Override
  public IDirectPositionList exteriorCoord() {
    ICurve c = this.exteriorCurve();
    if (c != null) {
      return c.coord();
    }
    return new DirectPositionList();
  }

  @Override
  public ILineString interiorLineString(int i) {
    if (this.sizePatch() == 1) {
      IPolygon poly = (IPolygon) this.getPatch(0);
      IRing inte = poly.getInterior(i);
      if (inte != null) {
        ICurve c = inte.getPrimitive();
        ILineString ls = c.asLineString(0.0, 0.0, 0.0);
        return ls;
      }
      GM_Surface.logger
          .error("GM_Surface::interiorLineString() : ATTENTION frontiere null"); //$NON-NLS-1$
      return null;
    }
    GM_Surface.logger
        .error("GM_Surface::interiorLineString() : cette méthode ne fonctionne que si la surface est composée d'un seul patch"); //$NON-NLS-1$
    return null;
  }

  @Override
  public ICurve interiorCurve(int i) {
    if (this.sizePatch() == 1) {
      IPolygon poly = (IPolygon) this.getPatch(0);
      IRing inte = poly.getInterior(i);
      if (inte != null) {
        return inte.getPrimitive();
      }
      GM_Surface.logger
          .error("GM_Surface::interiorCurve() : ATTENTION frontiere null"); //$NON-NLS-1$
      return null;
    }
    GM_Surface.logger
        .error("GM_Surface::interiorCurve() : cette méthode ne fonctionne que si la surface est composée d'un seul patch"); //$NON-NLS-1$
    return null;
  }

  @Override
  public IDirectPositionList interiorCoord(int i) {
    ICurve c = this.interiorCurve(i);
    if (c != null) {
      return c.coord();
    }
    return new DirectPositionList();
  }

  @Override
  public IDirectPositionList coord() {
    if (this.sizePatch() == 1) {
      IPolygon poly = (IPolygon) this.getPatch(0);
      IDirectPositionList dpl = this.exteriorCurve() == null ? new DirectPositionList() : this.exteriorCurve().coord();
      for (int i = 0; i < poly.sizeInterior(); i++) {
        dpl.addAll(this.interiorCurve(i).coord());
      }
      return dpl;
    }
    GM_Surface.logger
        .error("GM_Surface::coord() : cette méthode ne fonctionne que si la surface est composée d'un seul patch"); //$NON-NLS-1$
    return null;
  }
}
