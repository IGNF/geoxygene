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

package fr.ign.cogit.geoxygene.appli;

import fr.ign.cogit.geoxygene.style.BlendingMode;

public abstract class GeoxygeneConstants {

    /*GLOBAL GL VARIABLES NAMES*/
    public static final String GL_VarName_M00ModelToViewMatrix = "m00";
    public static final String GL_VarName_M02ModelToViewMatrix = "m02";
    public static final String GL_VarName_M11ModelToViewMatrix = "m11";
    public static final String GL_VarName_M12ModelToViewMatrix = "m12";
    public static final String GL_VarName_ScreenWidth = "screenWidth";
    public static final String GL_VarName_ScreenHeight = "screenHeight";
    public static final String GL_VarName_FboWidth = "fboWidth";
    public static final String GL_VarName_FboHeight = "fboHeight";
    public static final String GL_VarName_AntialiasingSize = "antialiasingSize";
    public static final String GL_VarName_ActiveBlendingMode ="ActiveBlendingMode";
    public static final String GL_VarName_GLProgramPrefix = "GLProgram";
    public static final String GL_VarName_GlobalOpacityVarName = "globalOpacity";
    public static final String GL_VarName_ObjectOpacityVarName = "objectOpacity";
    public static final BlendingMode GL_VarName_DefaultBlendingMode = BlendingMode.Normal;
    public static final String GL_VarName_FBOBackgroundTexture = "backgroundTexture";
    public static final String GL_VarName_FBOForeGroundTexture = "foregroundTexture";

    
    /*GEOXYGEN CONSTANTS*/
    public static final String GEOX_Const_RenderingMethodsManagerName = "RenderingMethods";
    public static final String GEOX_Const_CurrentStyleRootURIName = "CURRENT_ROOT_URI";
    public static final String GEOX_Const_GeoxResourceScheme = "geox";
    public static final String GEOX_Const_SLDLocalScheme = "sld";

    

    
}
