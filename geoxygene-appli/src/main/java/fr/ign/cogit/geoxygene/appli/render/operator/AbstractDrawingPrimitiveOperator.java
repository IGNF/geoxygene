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
package fr.ign.cogit.geoxygene.appli.render.operator;

import java.util.Collection;

import fr.ign.cogit.geoxygene.appli.render.primitive.DrawingPrimitive;

/**
 * @author JeT
 * Basic implementation of Drawing operators. This class manages parameter access.
 * Classic constructor call is done like that :
 * super( new OperatorParameterXXX(id,value), new OperatorParameterXXX(id,value), ... ); 
 */
public abstract class AbstractDrawingPrimitiveOperator implements DrawingPrimitiveOperator {

  //  private final Map<ParameterId, Parameter> parameters = new LinkedHashMap<ParameterId, Parameter>(); // list of operator's parameters
  //
  //  /**
  //   * Constructor
  //   * @param parameters list of parameters handled by this operator
  //   */
  //  public AbstractDrawingPrimitiveOperator(final Parameter... parameters) {
  //    for (Parameter parameter : parameters) {
  //      if (parameter != null) {
  //        this.parameters.put(parameter.getId(), parameter);
  //      }
  //    }
  //  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.gl.DrawingPrimitiveOperator#addInputs(java.util.Collection)
   */
  @Override
  public void addInputs(final Collection<DrawingPrimitive> inputs) throws InvalidOperatorInputException {
    for (DrawingPrimitive input : inputs) {
      this.addInput(input);
    }
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.gl.DrawingPrimitiveOperator#addInputs(java.util.Collection)
   */
  @Override
  public void setInputs(final Collection<DrawingPrimitive> inputs) throws InvalidOperatorInputException {
    this.removeAllInputs();
    for (DrawingPrimitive input : inputs) {
      this.addInput(input);
    }
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.render.gl.DrawingPrimitiveOperator#addInputs(java.util.Collection)
   */
  @Override
  public void setInput(final DrawingPrimitive input) throws InvalidOperatorInputException {
    this.removeAllInputs();
    this.addInput(input);
  }

  //  /* (non-Javadoc)
  //  * @see fr.ign.cogit.geoxygene.appli.render.gl.DrawingPrimitiveOperator#setParameter(fr.ign.cogit.geoxygene.appli.render.gl.OperatorParameterId, java.lang.Object)
  //  */
  //  @Override
  //  public final void setParameter(final ParameterId parameterId, final Object parameterValue) throws InvalidParameterException {
  //    Parameter param = this.getParameter(parameterId);
  //    if (param == null) {
  //      throw new InvalidParameterException("Unknown parameter ID " + parameterId);
  //    }
  //    param.setValue(parameterValue);
  //  }
  //
  //  /* (non-Javadoc)
  //   * @see fr.ign.cogit.geoxygene.appli.render.gl.DrawingPrimitiveOperator#getParameter(fr.ign.cogit.geoxygene.appli.render.gl.OperatorParameterId)
  //   */
  //  @Override
  //  public final Parameter getParameter(final ParameterId parameterId) {
  //    return this.parameters.get(parameterId);
  //  }
  //
  //  @Override
  //  public final Collection<Parameter> getParameters() {
  //    return this.parameters.values();
  //  }

}
