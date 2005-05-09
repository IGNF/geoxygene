/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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
 *  
 */

package fr.ign.cogit.geoxygene.util.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import uk.ac.leeds.ccg.geotools.DataSource;
import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.GeoRectangle;
import uk.ac.leeds.ccg.geotools.HighlightManager;
import uk.ac.leeds.ccg.geotools.MonoShader;
import uk.ac.leeds.ccg.geotools.SelectionChangedEvent;
import uk.ac.leeds.ccg.geotools.SelectionChangedListener;
import uk.ac.leeds.ccg.geotools.SelectionManager;
import uk.ac.leeds.ccg.geotools.ShadeStyle;
import uk.ac.leeds.ccg.geotools.ShapefileReader;
import uk.ac.leeds.ccg.geotools.Theme;
import uk.ac.leeds.ccg.geotools.Viewer;
import uk.ac.leeds.ccg.raster.ImageLayer;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
  * This class instanciates the GUI of the (Geo)Object Viewer.
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

class ObjectViewerInterface extends JFrame implements Observer {
	
	/** This constant defines if themes can be selected when they are loaded or added (default = true), or not (false). */
	private static final boolean THEME_SELECTION = true;
	
	/** This constant defines if themes are displayed when they are loaded or added (default = true), or not (false). */
	private static final boolean THEME_VISIBILITY = true;

	/** It is a reference to the Geotools viewer. */
	protected Viewer view = new Viewer();
	
	/** It is a reference to the status bar of the ObjectViewer's GUI. */
	private ObjectViewerStatusBar statusbar;

	/** It is a reference to the tool bar of the GUI of the ObjectViewer. */
	private ObjectViewerToolBar jtb;

	/** It is a reference to the menu bar of the GUI of the ObjectViewer. */
	private ObjectViewerMenuBar jmb;

	/** List of themes properties */
	//private Vector themesList;
	
	/** List of properties of themes. */
	private Vector themesPropertiesList;
	
	/** List of actives themes (button is pressed) */
	private Vector activeThemes;

	/** JPanel for themes. */
	protected JPanel panel_themes = new JPanel();
	
	/** JScrollPane. */
	private JScrollPane scrolling_panel_themes ;
	
	/** Selected ID objects */
	private Vector selectedObjects ;
	


	protected ObjectViewerInterface(String titre, Geodatabase db) {
		super(titre);
		InterfaceInit(db);
	}


	public void update(Observable o, Object rect) {

	}


	private void InterfaceInit(Geodatabase db) {

		// Init the lists		
		themesPropertiesList = new Vector();
		activeThemes = new Vector();

		// Init GUI
		this.getContentPane().setLayout( new BorderLayout() );
		panel_themes.setLayout( new BoxLayout(panel_themes, BoxLayout.Y_AXIS) );
		scrolling_panel_themes = new JScrollPane(panel_themes);
		scrolling_panel_themes.setMinimumSize(new Dimension(180, 200));
		JPanel view_panel = new JPanel();
		view_panel.setLayout( new BorderLayout());
		view.setSize(300,200);
		view.setBackground(Color.white);
		view_panel.add(view);
		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, scrolling_panel_themes,	view_panel);
		jsp.setDividerLocation(0.4);
		this.getContentPane().add(jsp);		

		// Definition of the ToolBar.
		jtb = new ObjectViewerToolBar(this,db);
		this.getContentPane().add(jtb, BorderLayout.NORTH);

		// Definition of the MenuBar.
		jmb = new ObjectViewerMenuBar(this, db);
		this.setJMenuBar(jmb);

		// Definition of the StatusBar
		statusbar = new ObjectViewerStatusBar(this);
		this.getContentPane().add(statusbar, BorderLayout.SOUTH);

		// Mouse listener for view
		view.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				
			}
			
			public void mouseMoved(MouseEvent e) {
			  double x = view.getMapGeoPoint().getX();
			  double y = view.getMapGeoPoint().getY();
			  statusbar.setText("X="+(int)x+"  Y="+(int)y);		  
			}
		});
	}
	
	
	/** Add a new Theme with the given name to the viewer, and the kind of DataSource. */
	private void addTheme (Theme t, String sourcetype, DataSource source) {
		
		// init theme properties
		 ObjectViewerThemeProperties objectViewerThemeProperties =
			new ObjectViewerThemeProperties(this, t, sourcetype, source, THEME_SELECTION, THEME_VISIBILITY);

		// fill vectors of themes and theme properties
		themesPropertiesList.add(objectViewerThemeProperties);
		activeThemes.add(t);

		// Init
		initTheme(t,objectViewerThemeProperties );
		
		// Init "theme button"
		setThemeButton(themesPropertiesList.size()-1, objectViewerThemeProperties);

		// Add theme to geotools viewer
		view.addTheme(t);
		view.setThemeIsVisible(t, THEME_VISIBILITY, true);
		panel_themes.revalidate();
		panel_themes.repaint();
		
		// Gestion de l'ordre d'affichage
		for (int i=0; i<themesPropertiesList.size(); i++) {
			Theme th = ((ObjectViewerThemeProperties)themesPropertiesList.get(i)).getObjectViewerTheme();
			view.setThemeWaighting(th,-i);
		}		
	}
	
	
	/** Refresh theme at the given index. */
	private void setTheme (Theme t, String sourcetype, DataSource source, int index, boolean active, boolean visible) {

		// init theme properties
		 ObjectViewerThemeProperties objectViewerThemeProperties =
			new ObjectViewerThemeProperties(this, t, sourcetype, source, active, visible);
					
		// fill vectors of themes and theme properties
		themesPropertiesList.set(index,objectViewerThemeProperties);

		// Init
		initTheme(t,objectViewerThemeProperties );
		
		// Remove "theme button" and init new one
		panel_themes.remove(index);
		setThemeButton(index, objectViewerThemeProperties);

		// Add theme to geotools viewer
		view.addTheme(t);
		view.setThemeIsVisible(t, visible, true);
		panel_themes.revalidate();
		panel_themes.repaint();
	}
	
	
	private void initTheme(Theme t, ObjectViewerThemeProperties objectViewerThemeProperties ) {
		
		// Add a Shader	
		t.setShader(new MonoShader(objectViewerThemeProperties.getFillInThemeColor()));
			
		// Add a HighlightManager
		t.setHighlightManager(new HighlightManager());		
		ShadeStyle themeHighlightShadeStyle = new ShadeStyle();
		themeHighlightShadeStyle.setFillColor(
			objectViewerThemeProperties.getFillInHighlightColor());
		themeHighlightShadeStyle.setLineColor(
			objectViewerThemeProperties.getOutlineHighlightColor());
		t.setHighlightStyle(themeHighlightShadeStyle);
		objectViewerThemeProperties.setThemeHighlightManager(t.getHighlightManager());
				
		// Add a SelectionManager
		SelectionManager themeSelectMgr = new SelectionManager();
		ShadeStyle themeSelectShadeStyle = new ShadeStyle();
		themeSelectShadeStyle.setFillColor(
			objectViewerThemeProperties.getFillInSelectionColor());
		themeSelectShadeStyle.setLineColor(
			objectViewerThemeProperties.getOutlineSelectionColor());
		t.setSelectionStyle(themeSelectShadeStyle);

		themeSelectMgr.addSelectionChangedListener(new SelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent hce) {
				System.out.println("Selection sur un theme ...");
				selectedObjects = new Vector();
				for (int i = 0; i < themesPropertiesList.size(); i++) {
					Theme activetheme = ((ObjectViewerThemeProperties) (themesPropertiesList.get(i))).getObjectViewerTheme();
					if (isActive(activetheme)) {
						System.out.println("Themes: " + activetheme.getName());
						GeoData activethemeGeoData = activetheme.getGeoData();
	
						int[] selectedIdObjects = activetheme.getSelectionManager().getSelection();
						Vector selectedIdObjectsNotNullVector = new Vector();
						int nbselectedIdObjects = selectedIdObjects.length;
						for (int j = 0; j < nbselectedIdObjects; j++) {
							if (selectedIdObjects[j] != -1) {
								System.out.println("	ObjectID: " + selectedIdObjects[j]);
								selectedIdObjectsNotNullVector.add(new Integer (selectedIdObjects[j]));
							}
						}
						int[] selectedIdObjectsNotNullArray = new int[selectedIdObjectsNotNullVector.size()];
						for (int j=0; j < selectedIdObjectsNotNullArray.length; j++)
							selectedIdObjectsNotNullArray[j] = ((Integer)selectedIdObjectsNotNullVector.get(j)).intValue();
						selectedObjects.add (new ObjectsIDAndSource (selectedIdObjectsNotNullArray,
											((ObjectViewerThemeProperties)themesPropertiesList.get(i)).getDataSource() ));
					}
				}
			}
		});
		t.setSelectionManager(themeSelectMgr);
		objectViewerThemeProperties.setThemeSelectionManager(t.getSelectionManager());

	}


	/** Display objects stored in the FeatureCollection, with a given name. */
	protected void addAShapefileTheme(URL url) {
		ShapefileReader sfr = new ShapefileReader(url);
		final Theme t = sfr.getTheme();
		String theme_name = t.getName();
		String shapefile_name = theme_name.substring(theme_name.lastIndexOf("/") + 1);
		t.setName(shapefile_name);
		this.addTheme(t, Utils.SHAPEFILE, sfr);
	}


	/** Display objects stored in the FeatureCollection, in a Theme with the given name. */
	protected void addAFeatureCollectionTheme(FT_FeatureCollection fColl, String themeName) {
		GeOxygeneReader geOxyRead = new GeOxygeneReader (fColl);
		final Theme t = geOxyRead.getTheme();
		t.setName(themeName);
		this.addTheme(t, Utils.GEOXYGENE, geOxyRead);	
	}
	
	
	/** Refresh the FeatureCollection displayed in the viewer with this given name. */	
	protected void refreshAFeatureCollectionTheme (FT_FeatureCollection fColl, String themeName) {
		// Get the GeOxygeneReader of this collection
		if (themesPropertiesList.size() > 0) {
			for (int i=0; i<themesPropertiesList.size(); i++) {
				ObjectViewerThemeProperties oldThemeProp = (ObjectViewerThemeProperties) themesPropertiesList.get(i);
				Theme oldTheme = oldThemeProp.getObjectViewerTheme();
				// theme exists !!
				if (oldThemeProp.getDataSourceType().equals (Utils.GEOXYGENE) && oldTheme.getName().equals(themeName)) {
					GeOxygeneReader oldGeOxyRead = (GeOxygeneReader) oldThemeProp.getDataSource();
					int index = themesPropertiesList.indexOf(oldThemeProp);
					view.removeStaticTheme(oldTheme);
					GeOxygeneReader newGeOxyRead = new GeOxygeneReader (fColl);
					final Theme newTheme = newGeOxyRead.getTheme();
					newTheme.setName(themeName);
					this.setTheme(newTheme, Utils.GEOXYGENE, newGeOxyRead, index, oldThemeProp.isActive(), oldThemeProp.isVisible());
					activeThemes.remove (oldTheme);
					activeThemes.add(newTheme);		
					return;
				}				
			}
		}
		// Theme doesn't exist yet 
		System.out.println(" ## REFRESH : the Theme doesn't exist yet ! ");
		return;
	}
	
	
	/** Refresh the FeatureCollection displayed in the viewer with this given name with this feature.
	 * The feature must already belong to the collection. */
	// ATTENTION apres un refresh, le highlight manager continue a etre active meme si le theme est deselectionne ...
	public void refreshAFeatureCollectionTheme (FT_Feature feature, String themeName) {
		// Get the GeOxygeneReader of this collection
		if (themesPropertiesList.size() > 0) {
			for (int i=0; i<themesPropertiesList.size(); i++) {
				ObjectViewerThemeProperties themeProp = (ObjectViewerThemeProperties) themesPropertiesList.get(i);
				Theme theme = themeProp.getObjectViewerTheme();
				// theme exists !!
				if (themeProp.getDataSourceType().equals (Utils.GEOXYGENE) && theme.getName().equals(themeName)) {
					GeOxygeneReader geOxyRead = (GeOxygeneReader) themeProp.getDataSource();
					FT_FeatureCollection fColl = geOxyRead.getFeatureCollection();
					if (! fColl.getElements().contains(feature)) {
						System.out.println(" ## REFRESH : the Feature Collection does not contain the Feature !");
						return;
					}
					geOxyRead.refreshFeature(feature);
					return;
				}
			}
		}
		// Theme doesn't exist yet 
		System.out.println(" ## REFRESH : the Theme doesn't exist yet ! ");
		return;				
				
	}	
	
	
	/** Display a JPEG image */
	protected void addAnImageTheme (String fileName, int x, int y, int width, int height) {
		File fichier=new File(fileName);
		System.out.println(fichier.getAbsolutePath());
		try {
			FileInputStream fis=new FileInputStream(fichier);			
			ImageLayer imageLayer = new ImageLayer(	fis, new GeoRectangle(x,y,width,height));
			Theme t = new Theme (imageLayer);
			String theme_name = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf("."));
			t.setName(theme_name);
			this.addTheme(t, Utils.IMAGE, new ImageReader());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	protected Vector getSelectedObjects() {
		return selectedObjects;
	}
	
	
	protected Vector getActiveThemes () {
		return activeThemes;
	}
	
	
	private boolean isActive (Theme t) {
		if (activeThemes.contains(t)) return true;
		else return false;
	}
	
	
	private void setThemeButton (int index, ObjectViewerThemeProperties prop) {
		// Init "theme button"
		ObjectViewerThemeButton theme_chkbox =	new ObjectViewerThemeButton(this, prop);

		// Add a popup for the "theme button"
		ObjectViewerThemePopupMenu ThemePopup = new ObjectViewerThemePopupMenu(this,theme_chkbox,prop);

		// Add a listener for popups.
		MouseListener popupListener = new PopupListener(ThemePopup);
		theme_chkbox.addMouseListener(popupListener);		
		
		// Get "theme button" and remove it
		panel_themes.add(theme_chkbox,index);
		panel_themes.add(Box.createVerticalStrut(2));
		theme_chkbox.setPreferredSize(new Dimension(177, 30));
		theme_chkbox.setMaximumSize(new Dimension(177, 30));

	}
		
	
	/** Move up a theme */
	protected void upTheme (Theme t) {
		for (int k=1; k<themesPropertiesList.size(); k++) {
			ObjectViewerThemeProperties thPr1 = (ObjectViewerThemeProperties) themesPropertiesList.get(k);
			Theme t1 = thPr1.getObjectViewerTheme();
			if ( t1.equals(t)) {
				ObjectViewerThemeProperties thPr0 = (ObjectViewerThemeProperties) themesPropertiesList.get(k-1);
				Theme t0 = thPr0.getObjectViewerTheme();
				view.swapThemes(t0,t1);
				themesPropertiesList.set(k-1,thPr1);
				themesPropertiesList.set(k,thPr0);				
				panel_themes.remove(k-1);					
				setThemeButton(k-1,thPr1);
				panel_themes.remove(k);
				setThemeButton(k,thPr0);
				panel_themes.revalidate();
				panel_themes.repaint();		
				break;
			}
		}
	}
	
	
	/** Moves down a theme */
	protected void downTheme (Theme t) {
		for (int k=0; k<themesPropertiesList.size()-1; k++) {
			ObjectViewerThemeProperties thPr1 = (ObjectViewerThemeProperties) themesPropertiesList.get(k);
			Theme t1 = thPr1.getObjectViewerTheme();
			if ( t1.equals(t)) {	
				ObjectViewerThemeProperties thPr0 = (ObjectViewerThemeProperties) themesPropertiesList.get(k+1);
				Theme t0 = thPr0.getObjectViewerTheme();
				view.swapThemes(t0,t1);
				themesPropertiesList.set(k+1,thPr1);
				themesPropertiesList.set(k,thPr0);				
				panel_themes.remove(k+1);					
				setThemeButton(k+1,thPr1);
				panel_themes.remove(k);
				setThemeButton(k,thPr0);
				panel_themes.revalidate();
				panel_themes.repaint();						
				break;
			}
		}
	}
	
	
	protected void removeTheme (ObjectViewerThemeProperties prop) {
		int index = themesPropertiesList.indexOf(prop);
		Theme t = prop.getObjectViewerTheme();
		view.setThemeIsVisible(t,false,true);
	    panel_themes.remove(index);
		panel_themes.revalidate();
		panel_themes.repaint();
		view.removeStaticTheme(t);
		activeThemes.remove(t);
		themesPropertiesList.remove(index);		
	}
	
	
	/** Structure for IDs and DataSource */
	protected class ObjectsIDAndSource {
		public int[] objectsID;
		public DataSource source;
		public ObjectsIDAndSource (int[] ids, DataSource s) {
			objectsID = ids;
			source = s;
		}
		public int[] getObjectsID() {
			return objectsID;
		}
		public DataSource getDataSource() {
			return source;
		}
	}
		
}
