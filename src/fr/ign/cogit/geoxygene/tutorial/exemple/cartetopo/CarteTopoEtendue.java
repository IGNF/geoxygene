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

package fr.ign.cogit.geoxygene.tutorial.exemple.cartetopo;

import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.feature.Population;

/**
 * Carte topologique étendue qui utilise des noeuds valués
 * (degré du noeud).
 *
 * @author Eric Grosso - IGN / Laboratoire COGIT
 */
public class CarteTopoEtendue extends CarteTopo {
    /**
     * Constructeur.
     *
     * @param nomLogique nom de la carte topo
     */
    public CarteTopoEtendue(String nomLogique) {
        this.ojbConcreteClass = this.getClass().getName();
        // nécessaire pour ojb
        this.setNom(nomLogique);
        this.setPersistant(false);

        /*
         * Declaration des type de noeuds, arcs et face que la cartetopo va
         * contenir
         */
        this.addPopulation(new Population<NoeudValue>(false,
                "Noeud", NoeudValue.class, true)); //$NON-NLS-1$
        this.addPopulation(new Population<Arc>(false,
                "Arc", Arc.class, true)); //$NON-NLS-1$
        this.addPopulation(new Population<Face>(false,
                "Face", Face.class, true)); //$NON-NLS-1$
        this.addPopulation(new Population<Groupe>(false,
                "Groupe", Groupe.class, true)); //$NON-NLS-1$
    }
}
