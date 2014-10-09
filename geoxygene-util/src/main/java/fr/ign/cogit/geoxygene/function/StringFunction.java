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
package fr.ign.cogit.geoxygene.function;

import java.util.regex.Pattern;

import net.sourceforge.jeval.Evaluator;

/**
 * @author JeT String function use a string evaluator based on JEval to compute
 *         the function value from a human readable string
 */
public class StringFunction extends AbstractFunction1D {

    private String expression = "x * x";
    private String evalExpression = null;
    private final Pattern pattern = Pattern.compile(
            "([^a-zA-Z]|^)x([^a-zA-Z]|$)", Pattern.DOTALL);

    private final Evaluator evaluator = new Evaluator();

    /**
     * Constructor
     */
    public StringFunction() {
        super();
    }

    /**
     * @return the expression
     */
    public String getExpression() {
        return this.expression;
    }

    /**
     * @param expression
     *            the expression to set
     */
    public void setExpression(String expression) {
        this.expression = expression;
        this.evalExpression = this.pattern.matcher(expression).replaceAll(
                "$1#{x}$2");
        System.err.println("expression = " + this.expression + " => "
                + this.evalExpression);
    }

    /**
     * Constructor
     */
    public StringFunction(final String expression) {
        super();
        this.setExpression(expression);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.function.Function1D#help()
     */
    @Override
    public String help() {
        return "f(x)=" + this.getExpression();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.gl.GeoDisplacementFunction1D#displacement
     * (double)
     */
    @Override
    public Double evaluate(final double x) throws FunctionEvaluationException {
        try {
            this.evaluator.putVariable("x", String.valueOf(x));
            return this.evaluator.getNumberResult(this.getExpression());
        } catch (Exception e) {
            throw new FunctionEvaluationException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "String=" + this.getExpression();
    }

}
