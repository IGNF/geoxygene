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

package fr.ign.cogit.geoxygene.appli.render.stats;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

import org.jfree.util.Log;

import fr.ign.cogit.geoxygene.appli.render.GeoxygeneGLRenderer;

/**
 * @author JeT
 * 
 */
public class RendererStatistics {
    private GeoxygeneGLRenderer renderer = null;
    private long startTime = 0;
    private long endTime = 0;
    private int nbActivate = 0;
    private long startActivateTime = 0;
    private int nbSwitch = 0;
    private long startInitializeTime = 0;
    private int nbInitialize = 0;
    private int nbFinalize = 0;
    private int nbRender = 0;
    private long activateSwitchTime = 0;
    private long initializeFinalizeTime = 0;

    /**
     * @param renderer
     */
    public RendererStatistics(GeoxygeneGLRenderer renderer) {
        super();
        this.renderer = renderer;
    }

    public void doStartRendering() {
        this.startTime = new Date().getTime();
        this.endTime = 0;
        this.nbActivate = 0;
        this.nbSwitch = 0;
        this.nbInitialize = 0;
        this.nbFinalize = 0;
        this.nbRender = 0;
        this.activateSwitchTime = 0;
        this.initializeFinalizeTime = 0;
    }

    public void doStopRendering() {
        this.endTime = new Date().getTime();
    }

    public void doActivateRenderer() {
        this.startActivateTime = new Date().getTime();
        this.nbActivate++;
    }

    public void doSwitchRenderer() {
        long endActivateTime = new Date().getTime();
        this.activateSwitchTime = endActivateTime - this.startActivateTime;
        this.nbSwitch++;
        if (this.nbSwitch != this.nbActivate) {
            Log.warn("Renderer " + this.renderer.toString()
                    + " has incoherent activate/switch count : "
                    + this.nbActivate + "/" + this.nbSwitch);
        }
    }

    public void doInitializeRenderer() {
        this.startInitializeTime = new Date().getTime();
        this.nbInitialize++;
    }

    public void doFinalizeRenderer() {
        long finalizeTime = new Date().getTime();
        this.initializeFinalizeTime = finalizeTime - this.startInitializeTime;
        this.nbFinalize++;
        if (this.nbFinalize != this.nbInitialize) {
            Log.warn("Renderer " + this.renderer.toString()
                    + " has incoherent initialize/finalize count : "
                    + this.nbInitialize + "/" + this.nbFinalize);
        }
    }

    public void doRender() {
        this.nbRender++;
    }

    public void printStatistics(OutputStream out) {
        PrintStream pos = new PrintStream(out);
        pos.println("Statistics for renderer "
                + this.renderer.getClass().getSimpleName() + " ("
                + this.renderer.toString() + ")");
        pos.println("\tRender count = " + this.nbRender);
        pos.println("\tActivate/Switch block count = "
                + this.nbActivate
                + "/"
                + this.nbSwitch
                + " ==> "
                + (this.nbActivate <= this.nbRender
                        && this.nbActivate == this.nbSwitch ? "ok" : "ERROR"));
        pos.println("\tInitialise/Finalize block count = " + this.nbActivate
                + "/" + this.nbSwitch + " ==> "
                + (this.nbActivate == this.nbRender ? "ok" : "ERROR"));
        pos.print("\tComplete cycle duration = "
                + this.toTime(this.endTime - this.startTime));
        pos.println("\tActivate/Switch duration = "
                + this.toTime(this.activateSwitchTime)
                + " "
                + this.toTime((double) this.activateSwitchTime
                        / this.nbActivate) + " per block");
        pos.println("\tInitialize/Finalize duration = "
                + this.toTime(this.initializeFinalizeTime)
                + " "
                + this.toTime((double) this.initializeFinalizeTime
                        / this.nbInitialize) + " per block");

    }

    private String toTime(double durationInMillisec) {
        int s = (int) (durationInMillisec / 1000);
        double ms = durationInMillisec - 1000 * s;
        if (s != 0) {
            return String.format("%d'%.3f\"ms", s, ms);
        }
        return String.format("%.3fms", ms);
    }
}
