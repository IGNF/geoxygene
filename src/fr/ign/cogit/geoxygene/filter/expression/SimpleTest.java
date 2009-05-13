/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.filter.expression;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.ign.cogit.geoxygene.filter.function.Max;

/**
 * @author Julien Perret
 *
 */
public class SimpleTest {

	@Test
	public void testArithmeticExpressions() {
		Add add = new Add();
		Literal literal1 = new Literal();
		String stringValue1 = new String("5");
		literal1.setValue(stringValue1);
		add.setExpression1(literal1);
		Literal literal2 = new Literal();
		String stringValue2 = new String("2.00");
		literal2.setValue(stringValue2);
		add.setExpression2(literal2);
		Object resultAdd = add.evaluate(null);
		System.out.println(resultAdd);
		assertTrue(resultAdd.equals(new BigDecimal("7.00")));
		Subtract sub = new Subtract();
		sub.setExpression1(literal1);
		sub.setExpression2(literal2);
		Object resultSub = sub.evaluate(null);
		System.out.println(resultSub);
		assertTrue(resultSub.equals(new BigDecimal("3.00")));
		Multiply mul = new Multiply();
		mul.setExpression1(literal1);
		mul.setExpression2(literal2);
		Object resultMul = mul.evaluate(null);
		System.out.println(resultMul);
		assertTrue(resultMul.equals(new BigDecimal("10.00")));
		Divide div = new Divide();
		div.setExpression1(literal1);
		div.setExpression2(literal2);
		Object resultDiv = div.evaluate(null);
		System.out.println(resultDiv);
		assertTrue(resultDiv.equals(new BigDecimal("2.5")));
		Function function = new Max();
		Subtract subtract = new Subtract();
		subtract.setExpression1(new Literal("100"));
		Multiply multiply = new Multiply();
		Subtract subtract2 = new Subtract();
		subtract2.setExpression1(new Literal("0"));
		subtract2.setExpression2(new Literal("20"));
		System.out.println("subtract2 = "+subtract2.evaluate(null));
		multiply.setExpression1(subtract2);
		multiply.setExpression2(new Literal("5"));
		subtract.setExpression2(multiply);
		System.out.println("subtract = "+subtract.evaluate(null));
		List<Expression> parameters = new ArrayList<Expression>();
		parameters.add(subtract);
		Multiply multiply2 = new Multiply();
		multiply2.setExpression1(new Literal("100"));
		multiply2.setExpression2(new Literal("0"));
		parameters.add(multiply2);
		function.setParameters(parameters);
		Object resultFunction = function.evaluate(null);
		System.out.println(resultFunction);
	}
}
