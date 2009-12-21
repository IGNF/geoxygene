/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.filter.expression;

import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * @author Julien Perret
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({
    Add.class,
    Divide.class,
    Multiply.class,
    Subtract.class
})
public abstract class BinaryExpression extends Expression {
	static MathContext mc = new MathContext( (int)Math.min(9 +(long)Math.ceil(30.0),Integer.MAX_VALUE),RoundingMode.HALF_EVEN);

	@XmlElementRefs(@XmlElementRef)
	private List<Expression> parameters = new ArrayList<Expression>();
	public List<Expression> getParameters() {return this.parameters;}
	public Expression getExpression1() {return this.parameters.get(0);}
	public Expression getExpression2() {return this.parameters.get(1);}
}
