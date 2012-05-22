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

package fr.ign.cogit.geoxygene.util.browser;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

/**
 * Cette classe définit un nouveau composant graphique Swing permettant
 * l'affichage des numéros d'index des objets contenus dans un attribut de type
 * Array ou Collection.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class ObjectBrowserListRuler extends JComponent {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  /** Famille de la police de caractères par défaut. */
  public static final String FONT_FAMILY = "SansSerif";
  /** Taille de la police de caractères par défaut. */
  public static final int FONT_SIZE = 10;
  /** Couleur de fond du composant graphique. */
  public static final Color RULER_COLOR = new Color(255, 255, 255);
  /** Nombre d'éléments contenus dans l'attribut à représenter. */
  private int nb_elements;
  /**
   * Taille horizontale du composant graphique, calculée en fonction du nombre
   * d'éléments et de la taille de la police de caractères.
   */
  private int size;
  /**
   * Espacement vertical calculé entre les numéros d'index représentant les
   * rangs des objets constituants la collection ou le tableau à représenter.
   */
  private double gap;

  /**
   * Constructeur principal du ObjectBrowserListRuler.
   * 
   * @param nbElements nombre d'éléments contenus dans l'attribut à représenter.
   * @param height hauteur souhaitée en pixel du composant graphique.
   */
  public ObjectBrowserListRuler(int nbElements, double height) {
    this.nb_elements = nbElements;
    this.size = ObjectBrowserListRuler.FONT_SIZE + 7
        * ((Integer.toString(nbElements)).length() - 1);
    this.gap = height / nbElements;

  }

  /**
   * Fixe la hauteur souhaitée en pixel du composant graphique.
   * 
   * @param ph la hauteur souhaitée en pixel du composant graphique.
   */
  public void setPreferredHeight(int ph) {
    this.setPreferredSize(new Dimension(this.size, ph));
  }

  @Override
  public void paintComponent(Graphics g) {
    Rectangle drawHere = g.getClipBounds();

    g.setColor(ObjectBrowserListRuler.RULER_COLOR);
    g.fillRect(drawHere.x, drawHere.y, drawHere.width, drawHere.height);

    g.setFont(new Font(ObjectBrowserListRuler.FONT_FAMILY, Font.PLAIN,
        ObjectBrowserListRuler.FONT_SIZE));
    g.setColor(Color.black);

    for (int i = 0; i < this.nb_elements; i++) {
      g.drawString(Integer.toString(i), 2,
          (int) (i * this.gap + this.gap / 2 + 1));
    }

  }

}
