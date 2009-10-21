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

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.mode.ModeSelector;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * @author Julien Perret
 *
 */
public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	static Logger logger=Logger.getLogger(MainFrame.class.getName());

	private JDesktopPane desktopPane = new JDesktopPane() {
		private static final long serialVersionUID = 1L;
		{setDesktopManager(new DefaultDesktopManager());}
		};
	public JDesktopPane getDesktopPane() {return this.desktopPane;}

	GeOxygeneApplication application;
	private JMenuBar menuBar;
	private ModeSelector modeSelector = null;
	public ModeSelector getMode() {return this.modeSelector;}

	public MainFrame(String title, GeOxygeneApplication theApplication)  {
		super(title);
		this.application = theApplication;
		this.setIconImage(this.application.getIcon().getImage());
		this.setLayout(new BorderLayout());
		this.setResizable(true);
		this.setSize(800,600);
		this.setExtendedState(Frame.MAXIMIZED_BOTH);

		this.menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu(Messages.getString("geoxygene.MainFrame.File")); //$NON-NLS-1$
		JMenu viewMenu = new JMenu(Messages.getString("geoxygene.MainFrame.View")); //$NON-NLS-1$
		JMenu configurationMenu = new JMenu(Messages.getString("geoxygene.MainFrame.Configuration")); //$NON-NLS-1$
		JMenu helpMenu = new JMenu(Messages.getString("geoxygene.MainFrame.Help")); //$NON-NLS-1$

		//StyledLayerDescriptor sld = StyledLayerDescriptor.charge("defaultSLD.xml");
		JMenuItem openShapefileMenuItem = new JMenuItem(Messages.getString("geoxygene.MainFrame.OpenShapefile")); //$NON-NLS-1$
		openShapefileMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ProjectFrame projectFrame = (ProjectFrame) MainFrame.this.getDesktopPane().getSelectedFrame();
				if (projectFrame==null) 
					if (MainFrame.this.getDesktopPane().getAllFrames().length!=0) {
						// TODO ask the user in which frame (s)he wants to load into?
						projectFrame = (ProjectFrame) MainFrame.this.getDesktopPane().getAllFrames()[0];
					} else {
						// TODO create a new project frame?
						logger.info(Messages.getString("geoxygene.MainFrame.NoFrameToLoadInto")); //$NON-NLS-1$
						return;
					}
				JFileChooser choixFichierShape = new JFileChooser();
				/** Crée un filtre qui n'accepte que les fichier shp ou les répertoires */
				choixFichierShape.setFileFilter(new FileFilter(){
					@Override
					public boolean accept(File f) {return (f.isFile() && (f.getAbsolutePath().endsWith(".shp") || f.getAbsolutePath().endsWith(".SHP")) || f.isDirectory());} //$NON-NLS-1$ //$NON-NLS-2$
					@Override
					public String getDescription() {return Messages.getString("geoxygene.MainFrame.ShapefileDescription");} //$NON-NLS-1$
				});
				choixFichierShape.setFileSelectionMode(JFileChooser.FILES_ONLY);
				choixFichierShape.setMultiSelectionEnabled(false);
				JFrame frame = new JFrame();
				frame.setVisible(true);
				int returnVal = choixFichierShape.showOpenDialog(frame);
				frame.dispose();
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					if (logger.isDebugEnabled()) logger.debug(Messages.getString("geoxygene.MainFrame.FileChosenDebug") + choixFichierShape.getSelectedFile().getAbsolutePath()); //$NON-NLS-1$
					String shapefileName = choixFichierShape.getSelectedFile().getAbsolutePath();
					String populationName = shapefileName.substring(shapefileName.lastIndexOf("/")+1,shapefileName.lastIndexOf(".")); //$NON-NLS-1$ //$NON-NLS-2$
					ShapefileReader shapefileReader = new ShapefileReader(shapefileName, populationName, DataSet.getInstance(), true);

					Population<DefaultFeature> population = shapefileReader.getPopulation();
					if (population!=null) {
						logger.info(Messages.getString("geoxygene.MainFrame.LoadingPopulation")+population.getNom()); //$NON-NLS-1$
						projectFrame.addFeatureCollection(population,population.getNom());
					}
					shapefileReader.read();
					if (projectFrame.getLayers().size()==1) {
						try {projectFrame.getLayerViewPanel().getViewport().zoom(new GM_Envelope(shapefileReader.getMinX(),shapefileReader.getMaxX(),shapefileReader.getMinY(),shapefileReader.getMaxY()));} 
						catch (NoninvertibleTransformException e1) {e1.printStackTrace();}
					}
				}

			}
		});
		JMenuItem exitMenuItem = new JMenuItem(Messages.getString("geoxygene.MainFrame.Exit")); //$NON-NLS-1$
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				MainFrame.this.application.exit();
			}
		});
		fileMenu.add(openShapefileMenuItem);
		fileMenu.addSeparator();
		fileMenu.add(exitMenuItem);

		this.menuBar.setFont(this.application.getFont());
		this.menuBar.add(fileMenu);
		this.menuBar.add(viewMenu);
		this.menuBar.add(configurationMenu);
		this.menuBar.add(helpMenu);
		this.setJMenuBar(this.menuBar);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.desktopPane, BorderLayout.CENTER);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {MainFrame.this.application.exit();}
		});	

		this.modeSelector=new ModeSelector(this);    
	}

	@Override
	public void dispose() {
		for(JInternalFrame frame:this.desktopPane.getAllFrames()) {frame.dispose();}
		super.dispose();
	}
	/**
	 * @return
	 */
	public ProjectFrame getSelectedProjectFrame() {
		if (this.desktopPane.getSelectedFrame()==null) return null;
		return (ProjectFrame) this.desktopPane.getSelectedFrame();    	
	}

	/**
	 * @return
	 */
	public ProjectFrame[] getAllProjectFrames() {
		List<ProjectFrame> projectFrameList = new ArrayList<ProjectFrame>();
		for(JInternalFrame frame:this.desktopPane.getAllFrames())
			if (frame instanceof ProjectFrame) projectFrameList.add((ProjectFrame)frame);
		return projectFrameList.toArray(new ProjectFrame[0]);
	}

	public ProjectFrame newProjectFrame() {
		ProjectFrame projectFrame = new ProjectFrame(this.application.getIcon());
		projectFrame.setSize(this.desktopPane.getSize());
		projectFrame.setVisible(true);
		this.desktopPane.add(projectFrame,JLayeredPane.DEFAULT_LAYER);
		this.desktopPane.setSelectedFrame(projectFrame);
		return projectFrame;
	}
}
