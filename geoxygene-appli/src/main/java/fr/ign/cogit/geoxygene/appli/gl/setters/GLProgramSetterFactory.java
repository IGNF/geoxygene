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

package fr.ign.cogit.geoxygene.appli.gl.setters;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.gl.ResourcesManager;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;

public class GLProgramSetterFactory {

    public static GLProgramUniformSetter getSetter(RenderingMethodDescriptor method) {
        try {
            LinkedList<UserDefinedGLProgramSetter> userSetters = new LinkedList<>();
            // Retrieve the list of userSetters for the method and its
            // supermethods.
            RenderingMethodDescriptor current_method = method;
            while (current_method != null) {
                UserDefinedGLProgramSetter setter = GLProgramSetterFactory.getSetterForClassName(current_method.getSetterClassName());
                if (setter != null) {
                    userSetters.addFirst(setter);
                }
                if(current_method.getSuperMethod() != null){
                    current_method = (RenderingMethodDescriptor) ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_RenderingMethodsManagerName)
                            .getResourceByName(current_method.getSuperMethod());    
                }else{
                    current_method =null;
                }
                
            }
            GLProgramUniformSetter generalSetter = new GLProgramUniformSetter(userSetters);

            return generalSetter;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static UserDefinedGLProgramSetter getSetterForClassName(String setterclassname) {
        UserDefinedGLProgramSetter setter = null;
        if(setterclassname == null || setterclassname.isEmpty()){
            return null;
        }
        try {
            setter = (UserDefinedGLProgramSetter) UserDefinedGLProgramSetter.class.getClassLoader().loadClass(UserDefinedGLProgramSetter.class.getPackage().getName() + "." + setterclassname)
                    .newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            Logger.getRootLogger().error("No User-Defined GLProgramSetter named " + setterclassname + " exists. This may lead to unwanted behaviors.");
            e.printStackTrace();
        }
        return setter;
    }
}
