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

package fr.ign.cogit.geoxygene.appli;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

/**
 * @author Julien Perret
 *
 */
public class SplashScreen extends JWindow {
	private static final long serialVersionUID = 1L;
	private JLabel imageLabel = new JLabel();
	private JLabel captionLabel = new JLabel();
	private JProgressBar progressBar = new JProgressBar();

	public SplashScreen(Icon image, String caption) {
		JPanel container = new JPanel(new GridBagLayout());
		container.setBorder(BorderFactory.createLineBorder(Color.black));
		container.setBackground(Color.white);
		this.imageLabel.setIcon(image);
		this.captionLabel.setText(caption);
		this.captionLabel.setFont(new Font("Arial",Font.PLAIN,10)); //$NON-NLS-1$
		this.captionLabel.setBackground(Color.white);
		this.captionLabel.setOpaque(false);
		this.captionLabel.setForeground(Color.lightGray);
		this.progressBar.setOpaque(false);
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		container.add(this.imageLabel, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(10, 10,10, 10),0,0));
		container.add(this.captionLabel,new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(0, 10, 0, 10),0,0));
		container.add(this.progressBar,new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0, 10,10, 10),0,0));
		this.getContentPane().setBackground(Color.white);
		this.getContentPane().add(container);
		this.pack();
		this.setLocationRelativeTo(null);
	}
}
