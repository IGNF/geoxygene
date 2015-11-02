/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package fr.ign.cogit.geoxygene.appli.api;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFrame;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.MainFrameMenuBar;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.mode.MainFrameToolBar;
import fr.ign.cogit.geoxygene.appli.ui.Message;

/**
 * @author JeT The main frame is the main window displaying the entire
 *         application user interface. It is associated with the application
 *         Base class of all MainFrame subtypes : - FloatingMainFrame: use
 *         JInternalFrames to dock ProjectFrames - TabbedMainFrame: use
 *         JTabbedPane to dock ProjectFrames
 */
public interface MainFrame {

    /**
     * gives access to the swing frame component
     * 
     * @return
     */
    public abstract JFrame getGui();

    /**
     * gives access to the swing menu component
     * 
     * @return
     */
    public abstract MainFrameMenuBar getMenuBar();

    /**
     * Get the associated application.
     * 
     * @return the associated application
     */
    public abstract GeOxygeneApplication getApplication();

    /**
     * Return the current application mode.
     * 
     * @return the current application mode
     */
    public abstract MainFrameToolBar getMode();

    /**
     * Return the current desktop.
     * 
     * @return the current desktop
     */
    public abstract JComponent getCurrentDesktop();

    /**
     * Return the selected (current) project frame.
     * 
     * @return the selected (current) project frame
     */
    public abstract ProjectFrame getSelectedProjectFrame();

    /**
     * Return all project frames.
     * 
     * @return an array containing all project frames available in the interface
     */
    public abstract ProjectFrame[] getDesktopProjectFrames();

    /**
     * Create and return a new project frame.
     * 
     * @return the newly created project frame
     */
    public abstract ProjectFrame newProjectFrame(
            final LayerViewPanel layerViewPanel);

    /**
     * Create and return a new project frame.
     * 
     * @return the newly created project frame
     */
    public abstract ProjectFrame newProjectFrame();

    /**
     * Free all graphic resources
     */
    public abstract void dispose();

    /**
     * Display/hide frame on screen
     * 
     * @param display
     *            true = display frame. false = hide frame
     */
    public abstract void display(boolean display);

    /**
     * Set the current Project Frame selection
     */
    public abstract void setSelectedFrame(ProjectFrame projectFrame);

    /**
     * remove all ProjectFrames from the interface
     */
    public abstract void removeAllProjectFrames();

    /**
     * try to get a managed ProjectFrame with the given GUI component
     * 
     * @param gui
     *            GUI component which should match ProjectFrame.getGui()
     * @return the matching managed ProjectFrame or null
     */
    public abstract ProjectFrame getProjectFrameFromGui(Component gui);

    /**
     * Set the frame title
     */
    public void setTitle(final String title);

    /**
     * get the frame dimension (in pixels)
     */
    public abstract Dimension getSize();

    /**
     * Open a file in the current Project Frame
     * 
     * @param file
     *            file to load
     */
    public abstract boolean openFile(File file);

    /**
     * set a default layout for current desktop project frames
     */
    public abstract void organizeCurrentDesktop();

    /**
     * Add a graphic component into the main frame
     * 
     * @param component
     *            component to add
     * @param layout
     */
    public abstract void add(JComponent component, String layout);

    /**
     * Add a component that is not a ProjectFrame into this main Frame
     */
    public JComponent addComponentInFrame(String title, JComponent component);

    /**
     * Get the icon associated with this main frame
     * 
     * @return
     */
    public abstract Image getIconImage();

    /**
     * Add a GUI element into the designed desktop
     * 
     * @param desktopName
     *            desktop name
     * @param desktopContent
     *            GUI element to be added to designed desktop
     */
    void addFrameInDesktop(String desktopName, JComponent desktopContent);

    /**
     * Create a new Desktop that can handle ProjectFrames (Floating or Tabbed)
     * 
     * @param title
     *            desktop title
     * @return
     */
    public JComponent createNewDesktop(String title);

    /**
     * put a message in the message console
     */
    public void addMessage(Message.MessageType type, String message);

    /**
     * terminate all graphic components
     */
    public abstract void close();

    /**
     * Set the main frame look and feel
     * 
     * @param className
     *            class name of the look and feel
     */
    public abstract boolean setLookAndFeel(String className);


}
