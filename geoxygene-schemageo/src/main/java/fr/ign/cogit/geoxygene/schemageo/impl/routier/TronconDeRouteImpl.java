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
 * 
 * @author julien Gaffuri 26 juin 2009
 */
package fr.ign.cogit.geoxygene.schemageo.impl.routier;

import java.util.Date;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.schemageo.api.routier.TronconDeRoute;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.schemageo.impl.support.reseau.ArcReseauImpl;

/**
 * troncon de transport routier (route, chemin, GR, etc.)
 * 
 * @author julien Gaffuri 26 juin 2009
 * 
 */
public class TronconDeRouteImpl extends ArcReseauImpl
        implements TronconDeRoute {

    public TronconDeRouteImpl(Reseau res, boolean estFictif, ICurve geom,
            int importance) {
        super(res, estFictif, geom, importance);
    }

    private int nombreDeVoies = 0;

    @Override
    public int getNombreDeVoies() {
        return this.nombreDeVoies;
    }

    @Override
    public void setNombreDeVoies(int nombreDeVoies) {
        this.nombreDeVoies = nombreDeVoies;
    }

    private Date dateMiseEnService = null;

    @Override
    public Date getDateMiseEnService() {
        return this.dateMiseEnService;
    }

    @Override
    public void setDateMiseEnService(Date dateMiseEnService) {
        this.dateMiseEnService = dateMiseEnService;
    }

    private String etatPhysique = "";

    @Override
    public String getEtatPhysique() {
        return this.etatPhysique;
    }

    @Override
    public void setEtatPhysique(String etatPhysique) {
        this.etatPhysique = etatPhysique;
    }

    private String acces = "";

    @Override
    public String getAcces() {
        return this.acces;
    }

    @Override
    public void setAcces(String acces) {
        this.acces = acces;
    }

    private double zIni = 0;

    @Override
    public double getzIni() {
        return this.zIni;
    }

    @Override
    public void setzIni(double zIni) {
        this.zIni = zIni;
    }

    private double zFin = 0;

    @Override
    public double getzFin() {
        return this.zFin;
    }

    @Override
    public void setzFin(double zFin) {
        this.zFin = zFin;
    }

    private String nomItineraire = "";

    @Override
    public String getNomItineraire() {
        return this.nomItineraire;
    }

    @Override
    public void setNomItineraire(String nomItineraire) {
        this.nomItineraire = nomItineraire;
    }

    @Override
    public int getImportance() {
        return super.getImportance();
    }

}
