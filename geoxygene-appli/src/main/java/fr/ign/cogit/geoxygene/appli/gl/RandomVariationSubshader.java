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

package fr.ign.cogit.geoxygene.appli.gl;

import java.io.IOException;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.style.expressive.RandomVariationShaderDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLTools;

/**
 * @author JeT
 * 
 */
public class RandomVariationSubshader implements Subshader {

    private static final Logger logger = Logger
            .getLogger(RandomVariationSubshader.class.getName()); // logger

    public static final String strokePressureVariationAmplitudeUniformVarName = "pressureVariationAmplitude";
    public static final String strokePressureVariationWavelengthUniformVarName = "pressureVariationWavelength";
    public static final String strokeShiftVariationAmplitudeUniformVarName = "shiftVariationAmplitude";
    public static final String strokeShiftVariationWavelengthUniformVarName = "shiftVariationWavelength";
    public static final String strokeThicknessVariationAmplitudeUniformVarName = "thicknessVariationAmplitude";
    public static final String strokeThicknessVariationWavelengthUniformVarName = "thicknessVariationWavelength";

    private static final String randomVariationSubshaderFilename = "./src/main/resources/shaders/linepainting.subshader.penetration.glsl";

    private RandomVariationShaderDescriptor descriptor = null;

    public RandomVariationSubshader(RandomVariationShaderDescriptor descriptor) {
        if (descriptor == null) {
            throw new IllegalArgumentException("Null descriptor");
        }
        this.descriptor = descriptor;
    }

    @Override
    public void declareUniforms(GLProgram program) {
        program.addUniform(strokePressureVariationAmplitudeUniformVarName);
        program.addUniform(strokePressureVariationWavelengthUniformVarName);
        program.addUniform(strokeShiftVariationAmplitudeUniformVarName);
        program.addUniform(strokeShiftVariationWavelengthUniformVarName);
        program.addUniform(strokeThicknessVariationAmplitudeUniformVarName);
        program.addUniform(strokeThicknessVariationWavelengthUniformVarName);
    }

    /**
     * Initialize the shader before rendering (set uniforms)
     * 
     * @throws GLException
     */
    @Override
    public void setUniforms(GLProgram program) throws GLException {
        program.setUniform1f(strokePressureVariationAmplitudeUniformVarName,
                (float) (this.descriptor.getStrokePressureVariationAmplitude()));
        program.setUniform1f(
                strokePressureVariationWavelengthUniformVarName,
                (float) (this.descriptor.getStrokePressureVariationWavelength()));
        program.setUniform1f(strokeShiftVariationAmplitudeUniformVarName,
                (float) (this.descriptor.getStrokeShiftVariationAmplitude()));
        program.setUniform1f(strokeShiftVariationWavelengthUniformVarName,
                (float) (this.descriptor.getStrokeShiftVariationWavelength()));
        program.setUniform1f(
                strokeThicknessVariationAmplitudeUniformVarName,
                (float) (this.descriptor.getStrokeThicknessVariationAmplitude()));
        program.setUniform1f(strokeThicknessVariationWavelengthUniformVarName,
                (float) (this.descriptor
                        .getStrokeThicknessVariationWavelength()));

    }

    @Override
    public void configureProgram(GLProgram program) throws GLException {
        try {
            program.addFragmentShader(
                    GLTools.readFileAsString(randomVariationSubshaderFilename),
                    randomVariationSubshaderFilename);
        } catch (IOException e) {
            throw new GLException(e);
        }

    }
}
