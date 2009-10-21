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

import java.awt.Font;

import javax.swing.ImageIcon;

/**
 * Base class for GeOxygene applications.
 * 
 * @author Julien Perret
 *
 */
public class GeOxygeneApplication {
	private static ImageIcon splashImage;
	/**
	 * @return The splash image
	 */
	public synchronized static ImageIcon splashImage() {if (splashImage == null) {synchronized (GeOxygeneApplication.class) {splashImage = new ImageIcon(GeOxygeneApplication.class.getResource("/geoxygene-logo.png" ));}} return splashImage;} //$NON-NLS-1$
	/**
	 * The icon of the icon, i.e. the GeOxygene icon by default. Also used by {@link LayerViewPanel}
	 */
	private ImageIcon applicationIcon = new ImageIcon(GeOxygeneApplication.class.getResource("/icone.gif" )); //$NON-NLS-1$
	public ImageIcon getIcon() {return this.applicationIcon;}

	private Font font = new Font("Arial",Font.PLAIN,10); //$NON-NLS-1$
	/**
	 * @return The font to be used for all menus, etc.
	 */
	public Font getFont() {return this.font;}
	private MainFrame frame;
	/**
	 * @return The main frame of the application.
	 */
	public MainFrame getFrame() {return this.frame;}
	/**
	 * Constructor.
	 */
	public GeOxygeneApplication() {
		this.frame = new MainFrame("GeOxygene",this); //$NON-NLS-1$
		this.frame.setVisible(true);
	}

	/**
	 * Constructor.
	 */
	public GeOxygeneApplication(String title, ImageIcon theApplicationIcon) {
		this.applicationIcon = theApplicationIcon;
		this.frame = new MainFrame(title,this);
		this.frame.setVisible(true);
	}
	/**
	 * Exit the application. 
	 */
	public void exit() {
		this.frame.setVisible(false);
		this.frame.dispose();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SplashScreen splashScreen = new SplashScreen(splashImage(),"GeOxygene"); //$NON-NLS-1$
		splashScreen.setVisible(true);
		GeOxygeneApplication application = new GeOxygeneApplication();
		application.getFrame().newProjectFrame();
		application.getFrame().setVisible(true);
		splashScreen.setVisible(false);
		splashScreen.dispose();
	}
}
