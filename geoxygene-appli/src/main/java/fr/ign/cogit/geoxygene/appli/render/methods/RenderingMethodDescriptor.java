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

package fr.ign.cogit.geoxygene.appli.render.methods;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.gl.GLContext;
import fr.ign.cogit.geoxygene.appli.gl.ResourcesManager;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "RenderingMethod")
public class RenderingMethodDescriptor {

    private static Logger logger = Logger.getLogger(RenderingMethodDescriptor.class);

    @XmlElement(name = "GeneralMethodReference")
    public String superMethod;

    @XmlElement(name = "SetterClassName")
    public String setterclass = "";

    @XmlElement(name = "Name")
    public String name = "";

    @XmlElement(name = "ShaderRef", type = ShaderRef.class)
    @XmlElementWrapper(name = "ShaderList")
    public ArrayList<ShaderRef> lshaderref = new ArrayList<ShaderRef>();

    @XmlElement(name = "Parameter")
    @XmlElementWrapper(name = "Parameters")
    public ArrayList<RenderingMethodParameterDescriptor> methodparameters = new ArrayList<RenderingMethodParameterDescriptor>();

    private URL location;

    public List<ShaderRef> getShadersReferences() {
        return this.lshaderref;
    }

    public String getSuperMethod() {
        return superMethod;
    }

    public static RenderingMethodDescriptor unmarshall(File XMLFile) {

        try {
            JAXBContext jc = JAXBContext.newInstance(RenderingMethodDescriptor.class, RenderingMethodParameterDescriptor.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            RenderingMethodDescriptor method = (RenderingMethodDescriptor) unmarshaller.unmarshal(XMLFile);
            method.location = XMLFile.toURI().toURL();
            return method;
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void marshall(Writer writer) {
        try {
            JAXBContext context = JAXBContext.newInstance(RenderingMethodDescriptor.class, RenderingMethodParameterDescriptor.class);
            final XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(this, xmlStreamWriter);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FactoryConfigurationError e) {
            e.printStackTrace();
        }
    }

    public void marshall(OutputStream stream) {
        try {
            JAXBContext context = JAXBContext.newInstance(RenderingMethodDescriptor.class, RenderingMethodParameterDescriptor.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(this, stream);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the rendering method in the file in parameter
     * 
     * @param fileName
     */
    public void marshall(String fileName) {
        try {
            this.marshall(new FileOutputStream(fileName));
        } catch (FileNotFoundException e) {
            RenderingMethodDescriptor.logger.error("File " + fileName + " could not be written to"); //$NON-NLS-1$//$NON-NLS-2$
        }
    }

    public String getName() {
        return this.name;
    }

    public String getSetterClassName() {
        return this.setterclass;
    }

    public ArrayList<RenderingMethodParameterDescriptor> getParameters() {
        return this.methodparameters;
    }

    public boolean hasSuperMethod() {
        return !(this.superMethod == null || this.superMethod.isEmpty());
    }

    /**
     * Get or build the GLProgram associated with this method. Return null if
     * the program is invalid.
     * 
     * @return
     */
    public GLProgram getGLProgram() {
        String progname = GeoxygeneConstants.GL_VarName_GLProgramPrefix + "-" + this.getName();
        GLProgram program = GLContext.getActiveGlContext().getProgram(progname);
        if (program == null) {
            try {
                return GLContext.getActiveGlContext().createProgram(progname, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return program;
    }

    public URL getLocation() {
        return this.location;
    }

    public String toString() {
        return this.getName();
    }

    public boolean hasParameter(String pname) {
        return this.getParameter(pname) != null;
    }

    public RenderingMethodParameterDescriptor getParameter(String pname) {
        for (RenderingMethodParameterDescriptor p : this.methodparameters) {
            if (p.getName().equalsIgnoreCase(pname))
                return p;
        }
        RenderingMethodDescriptor supermethod = null;
        if(this.superMethod != null)
            supermethod = (RenderingMethodDescriptor) ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_RenderingMethodsManagerName).getResourceByName(superMethod);
        if(supermethod != null)
            return supermethod.getParameter(pname);
        return null;
    }

    public static RenderingMethodDescriptor retrieveMethod(String method_name) {
        ResourcesManager methodManager = ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_RenderingMethodsManagerName);
        if(methodManager != null){
            return (RenderingMethodDescriptor) methodManager.getResourceByName(method_name);
        }
        return null;
    }
}
