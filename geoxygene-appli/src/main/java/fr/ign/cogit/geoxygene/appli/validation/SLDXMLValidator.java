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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.ValidationEvent;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.gl.ResourcesManager;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodParameterDescriptor;
import fr.ign.cogit.geoxygene.style.BlendingMode;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Shadow;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.TextSymbolizer;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveParameter;
import fr.ign.cogit.geoxygene.style.filter.LayerFilter;

public class SLDXMLValidator extends XmlValidator {

    Logger logger = Logger.getLogger(SLDXMLValidator.class);
    private String SLD_SPECIFIC_METHODS_PATH;
    private final String DEFAULT_METHODS_PATH = new File(SLDXMLValidator.class.getClassLoader().getResource(".").getFile()).getAbsolutePath() + File.separator + "methods";
    private boolean force_marshalling = true;

    public SLDXMLValidator(File sldFile, Class... jaxbcontextclasses) throws FileNotFoundException {
        super(new FileInputStream(sldFile), jaxbcontextclasses);
        this.eventHandler = new ValidationEventCollector();
        SLD_SPECIFIC_METHODS_PATH = sldFile.getParentFile().getParentFile().getPath() + File.separator + "methods";
    }

    public SLDXMLValidator(InputStream stream, Class... jaxbcontextclasses) throws FileNotFoundException {
        super(stream, jaxbcontextclasses);
        this.eventHandler = new ValidationEventCollector();
        SLD_SPECIFIC_METHODS_PATH = "";
    }

    public SLDXMLValidator(StyledLayerDescriptor sld, Class... jaxbcontextclasses) {
        super(null, jaxbcontextclasses);
        if (force_marshalling) {
            StringWriter sw = new StringWriter();
            sld.marshall(sw);
            sw.flush();
            sw.toString();
            super.stream = new ByteArrayInputStream(sw.toString().getBytes(StandardCharsets.UTF_8));
        }
        this.content = sld;
        this.eventHandler = new ValidationEventCollector();
        SLD_SPECIFIC_METHODS_PATH = new File(sld.getSource().getPath()).getParentFile().getParentFile().getAbsolutePath()+File.separator+"methods";
    }

    @Override
    protected boolean validateContent() {
        if (this.content == null)
            return false;
        StyledLayerDescriptor sld = (StyledLayerDescriptor) content;
        for (Layer layer : sld.getLayers()) {
            for (Style style : layer.getStyles()) {
                for (FeatureTypeStyle fts : style.getFeatureTypeStyles()) {
                    for (Rule rule : fts.getRules()) {
                        for (Symbolizer symbolizer : rule.getSymbolizers()) {
                            boolean success = true;
                            success &= this.validateBlendingMode(symbolizer.getBlendingMode());
                            success &= this.validateFilter(symbolizer.getFilter());
                            success &= this.validateGeomPropertyName(symbolizer.getGeometryPropertyName());
                            success &= this.validateShadow(symbolizer.getShadow());
                            success &= this.validateUOM(symbolizer.getUnitOfMeasure());
                            if (symbolizer instanceof PolygonSymbolizer) {
                                PolygonSymbolizer ps = (PolygonSymbolizer) symbolizer;
                                success &= this.validatePolygonSymbolizer(ps);
                            } else if (symbolizer instanceof LineSymbolizer) {
                                LineSymbolizer ls = (LineSymbolizer) symbolizer;
                                success &= this.validateLineSymbolizer(ls);
                            } else if (symbolizer instanceof TextSymbolizer) {
                                TextSymbolizer ts = (TextSymbolizer) symbolizer;
                                success &= this.validateTextSymbolizer(ts);
                            } else if (symbolizer instanceof PointSymbolizer) {
                                PointSymbolizer pts = (PointSymbolizer) symbolizer;
                                success &= this.validatePointSymbolizer(pts);
                            } else if (symbolizer instanceof RasterSymbolizer) {
                                RasterSymbolizer rs = (RasterSymbolizer) symbolizer;
                                success &= this.validateRasterSymbolizer(rs);
                            }
                            return success;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean validateUOM(String unitOfMeasure) {
        boolean success = unitOfMeasure.equalsIgnoreCase("http://www.opengeospatial.org/se/units/metre") || unitOfMeasure.equalsIgnoreCase("http://www.opengeospatial.org/se/units/foot")
                || unitOfMeasure.equalsIgnoreCase("http://www.opengeospatial.org/se/units/pixel");
        if (!success) {
            String message = "" + unitOfMeasure + " is not a valid Unit of Measure.";
            this.eventHandler.handleEvent(new SLDValidationEvent(ValidationEvent.ERROR, message, this.ll.getLocation(unitOfMeasure)));
        }
        return success;

    }

    private boolean validateShadow(Shadow shadow) {
        logger.debug("Shadow validation not yet implemented!");
        return true;
    }

    private boolean validateGeomPropertyName(String geometryPropertyName) {
        logger.debug("geometryPropertyName validation not yet implemented!");
        return true;
    }

    private boolean validateFilter(LayerFilter filter) {
        logger.debug("LayerFilter validation not yet implemented!");
        return true;
    }

    private boolean validateBlendingMode(BlendingMode blendingMode) {
        logger.debug("BlendingMode validation not yet implemented!");
        return true;
    }

    private boolean validateRasterSymbolizer(RasterSymbolizer rs) {
        logger.debug("RasterSymbolizer validation not yet implemented!");
        return true;
    }

    private boolean validatePointSymbolizer(PointSymbolizer pts) {
        boolean success = true;
        for (Mark m : pts.getGraphic().getMarks()) {
            success &= this.validateStroke(m.getStroke()) & this.validateFill(m.getFill());
        }
        return success;
    }

    private boolean validateTextSymbolizer(TextSymbolizer ts) {
        logger.debug("TextSymbolizer validation not yet implemented!");
        return true;
    }

    private boolean validateLineSymbolizer(LineSymbolizer ls) {
        return this.validateStroke(ls.getStroke());
    }

    /**
     * Validate the specific elements of a PolygonSymbolizer TODO : validate the
     * non-expressive parameters.
     * 
     * @param ps
     *            the PolygonSymbolizer to validate
     * @return
     */
    private boolean validatePolygonSymbolizer(PolygonSymbolizer ps) {
        return this.validateStroke(ps.getStroke()) & this.validateFill(ps.getFill());
    }

    private boolean validateStroke(Stroke stroke) {
        logger.debug("OGC Stroke validation not yet implemented!");

        if (stroke instanceof ExpressiveDescriptor) {
            ExpressiveDescriptor es = stroke.getExpressiveStroke();
            String renderingmethod = es.getRenderingMethod();
            if (renderingmethod == null) {
                String message = "The Expressive Stroke dos not provide any RenderingMethod. The Expressive parameters will be ignored.";
                ((ValidationEventCollector) this.eventHandler).handleEvent(new SLDValidationEvent(ValidationEvent.WARNING, message, ll.getLocation(stroke)));
                return true;
            }
            return this.validateRenderingMethod(es, renderingmethod, es.getExpressiveParameters());
        }
        return true;
    }

    private boolean validateFill(Fill fill) {
        logger.debug("OGC Fill validation not yet implemented!");
        if (fill instanceof ExpressiveDescriptor) {
            ExpressiveDescriptor es =fill.getExpressiveFill();
            String renderingmethod = es.getRenderingMethod();
            if (renderingmethod == null) {
                String message = "The Expressive Fill dos not provide any RenderingMethod. The Expressive parameters will be ignored.";
                ((ValidationEventCollector) this.eventHandler).handleEvent(new SLDValidationEvent(ValidationEvent.WARNING, message, ll.getLocation(fill)));
                return true;
            }
            return this.validateRenderingMethod(es, renderingmethod, es.getExpressiveParameters());
        }
        return true;
    }

    private boolean validateRenderingMethod(ExpressiveDescriptor es, String renderingmethod, List<ExpressiveParameter> parameters) {

        RenderingMethodDescriptor method = null;
        if (ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_RenderingMethodsManagerName) != null) {
            method = (RenderingMethodDescriptor) ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_RenderingMethodsManagerName).getResourceByName(renderingmethod);

        }
        if (method == null) {
            String methodpath = SLD_SPECIFIC_METHODS_PATH + File.separator + renderingmethod + ".xml";
            File f = new File(methodpath);
            if (!f.exists() || !f.isFile()) {
                methodpath = DEFAULT_METHODS_PATH + File.separator + renderingmethod + ".xml";
                f = new File(methodpath);
            }
            if (f.exists() && f.isFile()) {
                RenderingMethodValidatorXML rmv;
                try {
                    rmv = new RenderingMethodValidatorXML(f);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
                boolean valid = rmv.validate();
                if (!valid) {
                    ValidationEventCollector sldeh = (ValidationEventCollector) this.eventHandler;
                    sldeh.addEvents(((ValidationEventCollector) rmv.eventHandler).getEvents());
                    logger.debug("The Rendering Method" + methodpath + " is invalid");
                    return false;
                }
                method = (RenderingMethodDescriptor) rmv.content;

            } else {
                String message = "The rendering method " + renderingmethod + " xml descriptor file was not be found in " + SLD_SPECIFIC_METHODS_PATH + " or in " + DEFAULT_METHODS_PATH;
                ((ValidationEventCollector) this.eventHandler).handleEvent(new SLDValidationEvent(ValidationEvent.FATAL_ERROR, message, ll.getLocation(es)));
                return false;
            }
        }
        // Validate the method against the parameters.
        boolean success = true;
        if (method.hasSuperMethod()) {
            success &= this.validateRenderingMethod(es, method.superMethod, parameters);
        }
        return success & this.validateParameters(es, method.getParameters(), parameters);
    }

    private boolean validateParameters(ExpressiveDescriptor es, ArrayList<RenderingMethodParameterDescriptor> parameterdesc, List<ExpressiveParameter> parameters) {
        int errs = 0;
        for (RenderingMethodParameterDescriptor desc : parameterdesc) {
            ExpressiveParameter found = null;
            for (ExpressiveParameter param : parameters) {
                if (param.getName().equalsIgnoreCase(desc.getName())) {
                    found = param;
                }
            }
            if (found == null & desc.isRequired()) {
                errs++;
                ((ValidationEventCollector) this.eventHandler).handleEvent(new SLDValidationEvent(ValidationEvent.ERROR, "Incomplete rendering method : the required expressive parameter "
                        + desc.getName() + " is missing", ll.getLocation(es)));
            }
        }
        return errs == 0;
    }

    public Collection<ValidationEvent> getEvents() {
        return ((ValidationEventCollector) this.eventHandler).getEvents();
    }

    public StyledLayerDescriptor getContent() {
        return (StyledLayerDescriptor) this.content;
    }

}
