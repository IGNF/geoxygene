/*******************************************************************************
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
 *******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.appli.geopensim.feature.macro;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * @author Julien Perret
 *
 */
public class PopulationBatiments extends MacroRepresentation<Batiment> {
	//static Logger logger=Logger.getLogger(PopulationBatiments.class.getName());
	private GeometryFactory geomFactory = new GeometryFactory();

	public PopulationBatiments(Class<? extends Batiment> classe) {
		super(classe);
	}

	public PopulationBatiments(Class<? extends Batiment> classeBatiments, int date) {
		super(classeBatiments,date);
	}

	/**
	 * Construire un tableau de géométries à partir des batiments de la population
	 * @param distance
	 * @param quadrantSegments
	 * @param endCapStyle
	 * @return tableau de géométries à partir des batiments de la population
	 */
	public Geometry[] buffersBatiments(double distance, int quadrantSegments, int endCapStyle) {
		Geometry[] buffers = new Geometry[size()];
		int index = 0;
		// construction des buffers de taille "distance" autour des batiments
		for(Object bat:this) {
			Batiment batiment = (Batiment) bat;
			IGeometry geom = batiment.getGeom();
			Geometry jtsGeom;
			try {
				jtsGeom = AdapterFactory.toGeometry(geomFactory, geom);
				Geometry jtsBuffer=jtsGeom.buffer(distance, quadrantSegments, endCapStyle);
				buffers[index++] = jtsBuffer;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return buffers;
	}

	@Override
	public void qualifier() {
		for (Batiment batiment : this) batiment.qualifier();
	}
}
