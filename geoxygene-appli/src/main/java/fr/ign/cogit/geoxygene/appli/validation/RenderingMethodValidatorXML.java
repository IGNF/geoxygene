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

package fr.ign.cogit.geoxygene.appli.validation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;

import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.appli.render.methods.ShaderRef;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveParameter;

public class RenderingMethodValidatorXML extends XmlValidator {

    private final String SHADER_PATH;

    public RenderingMethodValidatorXML(File _xml) throws FileNotFoundException {
        super(new FileInputStream(_xml), RenderingMethodDescriptor.class);
        this.eventHandler = new ValidationEventCollector();
        SHADER_PATH = _xml.getParentFile().getParentFile().getPath() + File.separator + "shaders";
    }

    public RenderingMethodValidatorXML(InputStream stream) {
        super(stream);
        this.eventHandler = new ValidationEventCollector();
        SHADER_PATH = new File(SLDXMLValidator.class.getResource(".").getFile()).getAbsolutePath() + File.separator + "shaders";

    }

    public RenderingMethodValidatorXML(RenderingMethodDescriptor erm) {
        super(null);
        StringWriter sw = new StringWriter();
        erm.marshall(sw);
        sw.flush();
        sw.toString();
        super.stream = new ByteArrayInputStream(sw.toString().getBytes(StandardCharsets.UTF_8));
        this.content = erm;
        this.eventHandler = new ValidationEventCollector();
        SHADER_PATH = "";
    }

    @Override
    protected boolean validateContent() {
        if (this.content == null)
            return false;
        RenderingMethodDescriptor erm = (RenderingMethodDescriptor) this.content;
        File shader_dir = null;
        String all_shaders = ":";
        shader_dir = new File(SHADER_PATH);
        if (shader_dir.exists() && shader_dir.isDirectory()) {
            for (File f : shader_dir.listFiles()) {
                all_shaders += f.getName() + ":";
            }
        }
        // Check that the shaders exist.
        boolean success = true;
        for (ShaderRef shader : erm.lshaderref) {
                    if (!all_shaders.contains(shader.location.toString())) {
                        String error_msg = "The shader named " + shader.location.toString() + " doesn't exist or cannot be read in the shader directory " + (shader_dir == null ? "<null>" : shader_dir.getPath())
                                + ".";
                        ValidationEventLocatorImpl locator = new ValidationEventLocatorImpl(erm);
                        this.eventHandler.handleEvent(new SLDValidationEvent(ValidationEvent.FATAL_ERROR, error_msg, ll.getLocation(erm)));
                        success = false;
                    }
            }
        // TODO : check if the parameters types and their restrictions are
        // valid.
        return success;
    }

    public boolean validateMethodParameters(List<ExpressiveParameter> expressiveParameters) {
        // TODO Auto-generated method stub
        return false;
    }

}
