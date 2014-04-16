/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli.render.texture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import utils.Pair;
import fr.ign.cogit.geoxygene.util.gl.Sample;
import fr.ign.cogit.geoxygene.util.gl.Tile;

/**
 * @author JeT
 * 
 */
public class TextureImageTileChooser implements TileChooser {

    private Random rand = new Random(0);
    private final List<Pair<TileProbability, Tile>> tilesToBeApplied = new ArrayList<Pair<TileProbability, Tile>>();

    /**
     * Default constructor
     */
    public TextureImageTileChooser() {
        this.initializeTiling();
    }

    /*
     * (non-Javadoc)
     * 
     * @see test.app.TileChooser#initializeTiling()
     */
    @Override
    public void initializeTiling() {
        this.rand = new Random(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see test.app.TileChooser#getTile(test.app.Sample)
     */
    @Override
    public Tile getTile(Sample sample) {
        double sumProbability = 0;
        double[] sumProbabilities = new double[this.tilesToBeApplied.size()];
        for (int n = 0; n < this.tilesToBeApplied.size(); n++) {
            Pair<TileProbability, Tile> pair = this.tilesToBeApplied.get(n);
            sumProbability += pair.first().getProbability(sample.getLocation().getX(), sample.getLocation().getY());
            sumProbabilities[n] = sumProbability;
        }
        if (sumProbability < 1E-6) {
            return null;
        }
        double randomValue = this.rand.nextDouble() * sumProbability;
        int n = 0;
        while (n < this.tilesToBeApplied.size()) {
            if (randomValue < sumProbabilities[n]) {
                //                System.err.println("probabilities: " + Arrays.toString(sumProbabilities) + " random value = " + randomValue + " => index = " + n + "["
                //                        + sumProbabilities[n] + "]");
                //                System.err.println("sample " + sample + " => tile " + this.tilesToBeApplied.get(n).second());
                return this.tilesToBeApplied.get(n).second();
            }
            n++;
        }
        throw new IllegalStateException("impossible case random value = " + randomValue + " max Value = " + sumProbabilities[sumProbabilities.length - 1]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see test.app.TileChooser#setTile(test.app.Sample)
     */
    @Override
    public Tile setTile(Sample sample) {
        Tile tile = this.getTile(sample);
        sample.setTile(tile);
        return tile;
    }

    /**
     * Add a managed tile with given probability
     * 
     * @param proba
     * @param tile
     */
    public void addTile(TileProbability proba, Tile tile) {
        this.tilesToBeApplied.add(new Pair<TileProbability, Tile>(proba, tile));
    }
}
