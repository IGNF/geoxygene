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
 * 
 */

package fr.ign.cogit.geoxygene.util.viewer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import uk.ac.leeds.ccg.geotools.MultiPanTool;
import uk.ac.leeds.ccg.geotools.OneClickZoomInTool;
import uk.ac.leeds.ccg.geotools.OneClickZoomOutTool;
import uk.ac.leeds.ccg.geotools.SelectTool;
import uk.ac.leeds.ccg.geotools.ZoomTool;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.util.browser.ObjectBrowser;
import fr.ign.cogit.geoxygene.util.viewer.ObjectViewerInterface.ObjectsIDAndSource;

/**
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

class ObjectViewerToolBar extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5951002017821866988L;
	//Default selection of tools
	public static final boolean IS_OPENFILETOOL_SELECTED = true;
	public static final boolean IS_ZOOMINTOOL_SELECTED = true;
	public static final boolean IS_ZOOMOUTTOOL_SELECTED = true;
	public static final boolean IS_ZOOMEXTENTTOOL_SELECTED = true;
	public static final boolean IS_PANTOOL_SELECTED = true;
	public static final boolean IS_FULLEXTENTTOOL_SELECTED = true;
	public static final boolean IS_SELECTIONTOOL_SELECTED = true;

	private ObjectViewerInterface objectViewerInterface;

	public ObjectViewerToolBar(
			ObjectViewerInterface objectViewerInterface,
			boolean openFileTool,
			boolean zoomInTool,
			boolean zoomOutTool,
			boolean zoomExtentTool,
			boolean panTool,
			boolean fullExtentTool,
			boolean selectionTool,
			Geodatabase db) {

		this.objectViewerInterface = objectViewerInterface;
		URL imageUrl;

		if (openFileTool) {
			imageUrl = this.getClass().getResource("images/open.gif");
			Icon openicon = new ImageIcon(imageUrl);
			final JToggleButton opentbutton =
				new JToggleButton(openicon, false);
			opentbutton.setToolTipText("Open a file");
			opentbutton.addActionListener(new GeOxygeneViewerOpenFileAction(objectViewerInterface));
			this.add(opentbutton);
		}

		if (db != null) {
			imageUrl = this.getClass().getResource("images/oxygene.gif");
			Icon geOxygeneIcon = new ImageIcon(imageUrl);
			final JToggleButton geOxygenetbutton =
				new JToggleButton(geOxygeneIcon, false);
			geOxygenetbutton.setToolTipText("Access to GeOxygene mapping repository");
			geOxygenetbutton.addActionListener(new GeOxygeneViewerOpenGeOxygeneAction(objectViewerInterface,db));
			this.add(geOxygenetbutton);
		}

		if (openFileTool || db != null)
			this.addSeparator();

		if (zoomInTool) {
			imageUrl = this.getClass().getResource("images/zoomin.gif");
			Icon zoominicon = new ImageIcon(imageUrl);
			final JToggleButton zoomintbutton =
				new JToggleButton(zoominicon, false);
			zoomintbutton.setToolTipText("Zoom in");
			zoomintbutton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setToolBarButtonsUnselected(zoomintbutton);

					System.out.println("ZoomIn tool selected !!!");

					//ZoomTool zoomintool = new ZoomTool();
					OneClickZoomInTool zoomintool = new OneClickZoomInTool();
					System.out.println(zoomintool.getDescription());
					getObjectViewerInterface().view.setTool(zoomintool);
				}
			});
			this.add(zoomintbutton);
		}

		if (zoomOutTool) {
			imageUrl = this.getClass().getResource("images/zoomout.gif");
			Icon zoomouticon = new ImageIcon(imageUrl);
			final JToggleButton zoomouttbutton =
				new JToggleButton(zoomouticon, false);
			zoomouttbutton.setToolTipText("Zoom out");
			zoomouttbutton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setToolBarButtonsUnselected(zoomouttbutton);

					System.out.println("ZoomOut tool selected !!!");

					//MultiZoomTool zoomouttool = new MultiZoomTool();
					OneClickZoomOutTool zoomouttool = new OneClickZoomOutTool();
					System.out.println(zoomouttool.getDescription());
					getObjectViewerInterface().view.setTool(zoomouttool);
				}
			});
			this.add(zoomouttbutton);
		}

		if (zoomExtentTool) {
			imageUrl = this.getClass().getResource("images/zoomextent.gif");
			Icon zoomextenticon = new ImageIcon(imageUrl);
			//final JToggleButton zoomextenttbutton = new JToggleButton(zoomextenticon,false);
			final JToggleButton zoomextenttbutton =
				new JToggleButton(zoomextenticon, true);
			zoomextenttbutton.setToolTipText("Zoom in/out to extent");
			zoomextenttbutton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setToolBarButtonsUnselected(zoomextenttbutton);

					System.out.println(
					"Zoom In/Out to extent tool selected !!!");

					//System.out.println("Zoom: "+view.getZoomAsPercent());
					ZoomTool zoomouttool = new ZoomTool();
					System.out.println(zoomouttool.getDescription());
					getObjectViewerInterface().view.setTool(zoomouttool);
				}
			});
			this.add(zoomextenttbutton);
		}

		if (panTool) {
			imageUrl = this.getClass().getResource("images/pan.gif");
			Icon panicon = new ImageIcon(imageUrl);
			final JToggleButton pantbutton = new JToggleButton(panicon, false);
			pantbutton.setToolTipText("Pan");
			pantbutton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setToolBarButtonsUnselected(pantbutton);

					System.out.println("Pan tool selected !!!");

					//PanTool pantool = new PanTool(true);
					MultiPanTool pantool = new MultiPanTool(true);
					System.out.println(pantool.getDescription());
					getObjectViewerInterface().view.setTool(pantool);
				}
			});
			this.add(pantbutton);
		}

		if (fullExtentTool) {
			imageUrl = this.getClass().getResource("images/fullextent.gif");
			Icon reseticon = new ImageIcon(imageUrl);
			final JToggleButton resettbutton =
				new JToggleButton(reseticon, false);
			resettbutton.setToolTipText("Zoom to full extent");
			resettbutton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setToolBarButtonsUnselected(resettbutton);

					System.out.println("Reset tool selected !!!");

					getObjectViewerInterface().view.setMapExtentFull();
				}
			});
			this.add(resettbutton);
		}

		if (zoomInTool || zoomOutTool || zoomExtentTool || panTool || fullExtentTool)
			this.addSeparator();

		if (selectionTool) {
			imageUrl = this.getClass().getResource("images/select.gif");
			Icon selecticon = new ImageIcon(imageUrl);
			final JToggleButton selecttbutton =
				new JToggleButton(selecticon, false);
			selecttbutton.setToolTipText("Select feature");
			selecttbutton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setToolBarButtonsUnselected(selecttbutton);

					System.out.println("Select tool selected !!!");

					SelectTool selecttool = new SelectTool();
					System.out.println(selecttool.getDescription());
					getObjectViewerInterface().view.setTool(selecttool);
				}
			});
			this.add(selecttbutton);
		}

		if (true) {
			//Icon selectIDicon = new ImageIcon("../images/select.gif");
			final JToggleButton selectIDtbutton =
				new JToggleButton("Id?", false);
			selectIDtbutton.setToolTipText("Show attributes");
			selectIDtbutton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setToolBarButtonsUnselected(selectIDtbutton);

					System.out.println("Show attributes selected !!!");

					List<FT_Feature> selectedFeatures = new ArrayList<FT_Feature>();
					Vector<ObjectsIDAndSource> selectedObjects = getObjectViewerInterface().getSelectedObjects();
					if (selectedObjects!=null) {
						for (int i=0; i<selectedObjects.size(); i++) {
							ObjectViewerInterface.ObjectsIDAndSource aSelectedObjectAndSource =
								selectedObjects.get(i);
							if (aSelectedObjectAndSource.getDataSource() instanceof GeOxygeneReader) {
								GeOxygeneReader geOxyRd = (GeOxygeneReader) aSelectedObjectAndSource.getDataSource();
								int[] selectedIDs = aSelectedObjectAndSource.getObjectsID();
								for (int j=0; j<selectedIDs.length; j++) {
									FT_Feature feature = geOxyRd.getFeatureById(selectedIDs[j]);
									if (feature != null)
										selectedFeatures.add(feature);
								}
							}
						}
						if (selectedFeatures.size() > 0) {
							if (selectedFeatures.size() == 1)
								ObjectBrowser.browse(selectedFeatures.get(0));
							else
								ObjectBrowser.browse(selectedFeatures);
						}}
				}
			});
			this.add(selectIDtbutton);
		}

	}


	public ObjectViewerToolBar(ObjectViewerInterface objectViewerInterface, Geodatabase db) {
		this (			objectViewerInterface,
				IS_OPENFILETOOL_SELECTED,
				IS_ZOOMINTOOL_SELECTED,
				IS_ZOOMOUTTOOL_SELECTED,
				IS_ZOOMEXTENTTOOL_SELECTED,
				IS_PANTOOL_SELECTED,
				IS_FULLEXTENTTOOL_SELECTED,
				IS_SELECTIONTOOL_SELECTED,
				db);
	}


	public void setToolBarButtonsUnselected(JComponent tb_button) {
		Component[] tbcomp = this.getComponents();
		int nbtbcomp = this.getComponentCount();
		String classname;

		for (int i = 0; i < nbtbcomp; i++) {
			classname = tbcomp[i].getClass().getName();
			if ((classname == "javax.swing.JToggleButton")
					&& (tbcomp[i] != tb_button))
				((JToggleButton) tbcomp[i]).setSelected(false);
		}
	}


	public ObjectViewerInterface getObjectViewerInterface() {
		return objectViewerInterface;
	}

}