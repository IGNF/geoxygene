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

package fr.ign.cogit.geoxygene.appli.mode;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;

/**
 * @author Julien Perret
 *
 */
public class ModeSelector implements ContainerListener, KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {
    static Logger logger=Logger.getLogger(ModeSelector.class.getName());
    private List<Mode> modes = new ArrayList<Mode>();
    private Mode currentMode = null;
    private JToolBar toolBar = new JToolBar(I18N.getString("fr.ign.cogit.geoxygene.ModeSelector.ModeSelection")); //$NON-NLS-1$
    public JToolBar getToolBar() {return this.toolBar;}
    MainFrame mainFrame;

    public ModeSelector(MainFrame theMainFrame) {
	this.mainFrame = theMainFrame;
	this.mainFrame.add(this.toolBar, BorderLayout.PAGE_START);

	this.addComponent(this.mainFrame);

	this.modes.add(new ZoomMode(this.mainFrame,this));
	this.modes.add(new MoveMode(this.mainFrame,this));
	this.modes.add(new SelectionMode(this.mainFrame,this));

	
	this.toolBar.addSeparator();
	
	JButton zoomToFullExtentButton = new JButton(new ImageIcon(ModeSelector.class.getResource("/icons/16x16/zoomToFullExtent.png"))); //$NON-NLS-1$
	zoomToFullExtentButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
		ProjectFrame projectFrame = ModeSelector.this.mainFrame.getSelectedProjectFrame();
		if (projectFrame!=null) try {projectFrame.getLayerViewPanel().getViewport().zoomToFullExtent();} catch (NoninvertibleTransformException e1) {e1.printStackTrace();}
	    }
	});
	this.toolBar.add(zoomToFullExtentButton);


	this.setCurrentMode(this.modes.get(0));
    }

    @Override
    public void keyPressed(KeyEvent e) {this.currentMode.keyPressed(e);}
    @Override
    public void keyReleased(KeyEvent e) {this.currentMode.keyReleased(e);}
    @Override
    public void keyTyped(KeyEvent e) {this.currentMode.keyTyped(e);}
    @Override
    public void mouseClicked(MouseEvent e) {this.currentMode.mouseClicked(e);}
    @Override
    public void mouseEntered(MouseEvent e) {this.currentMode.mouseEntered(e);}
    @Override
    public void mouseExited(MouseEvent e) {this.currentMode.mouseExited(e);}
    @Override
    public void mousePressed(MouseEvent e) {this.currentMode.mousePressed(e);}
    @Override
    public void mouseReleased(MouseEvent e) {this.currentMode.mouseReleased(e);}
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {this.currentMode.mouseWheelMoved(e);}
    @Override
    public void mouseDragged(MouseEvent e) {this.currentMode.mouseDragged(e);}
    @Override
    public void mouseMoved(MouseEvent e) {this.currentMode.mouseMoved(e);}

    /**
     * @param mode
     */
    public void setCurrentMode(Mode mode) {
	if (this.currentMode!=null) this.currentMode.getButton().setEnabled(true);
	this.currentMode = mode;
	mode.getButton().setEnabled(false);
    }

    @Override
    public void componentAdded(ContainerEvent e) {
	//logger.info(e.getChild().getClass().getSimpleName()+" added to "+e.getComponent().getClass().getSimpleName());
	addComponent(e.getChild());
    }

    private void addComponent(Component component) {
	if (component instanceof AbstractButton) return;
	//logger.info("component "+component.getClass().getSimpleName()+" added");
	component.addKeyListener(this);
	component.addMouseWheelListener(this);
	component.addMouseListener(this);
	component.addMouseMotionListener(this);				
	if (component instanceof Container) {
	    Container container = (Container) component;
	    container.addContainerListener(this);
	    for(Component child:container.getComponents()) addComponent(child);
	}
    }

    @Override
    public void componentRemoved(ContainerEvent e) {}
}
