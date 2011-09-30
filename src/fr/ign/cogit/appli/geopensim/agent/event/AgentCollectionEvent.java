/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.appli.geopensim.agent.event;

import java.util.EventObject;

import fr.ign.cogit.geoxygene.api.feature.event.FeatureCollectionEvent.Type;
import fr.ign.cogit.geoxygene.feature.FT_Feature;

/**
 * @author Julien Perret
 *
 */
public class AgentCollectionEvent extends EventObject {
    private static final long serialVersionUID = 6977794480279289305L;
//    public enum Type {ADDED,CHANGED,REMOVED}
    private Type type;
    public Type getType() {return this.type;}
    private FT_Feature feature;
    public FT_Feature getFeature() {return this.feature;}
    private String attribut;
    public String getAttribut() {return this.attribut;}
    private Object valeurAvant;
    public Object getValeurAvant() {return this.valeurAvant;}
    private Object valeurApres;
    public Object getValeurApres() {return this.valeurApres;}

    /**
     * @param source
     * @param feature
     * @param type
     */
    public AgentCollectionEvent(Object source, FT_Feature feature, Type type) {
    	super(source);
    	this.feature=feature;
    	this.type=type;
        }
    
    /**
     * @param source
     * @param feature
     * @param type
     * @param attribut
     * @param valeurAvant
     * @param valeurApres
     */
    public AgentCollectionEvent(Object source, FT_Feature feature, Type type, String attribut, Object valeurAvant, Object valeurApres) {
    	super(source);
    	this.feature=feature;
    	this.type=type;
    	this.attribut=attribut;
    	this.valeurAvant=valeurAvant;
    	this.valeurApres=valeurApres;
        }
}
