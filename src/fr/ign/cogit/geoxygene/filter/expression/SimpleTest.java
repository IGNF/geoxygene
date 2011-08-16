/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.filter.expression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.filter.function.Max;

/**
 * @author Julien Perret
 */
public class SimpleTest {

  @Test
  public void testArithmeticExpressions() {
    Add add = new Add();
    Literal literal1 = new Literal();
    String stringValue1 = new String("5"); //$NON-NLS-1$
    literal1.setValue(stringValue1);
    add.getParameters().add(literal1);
    Literal literal2 = new Literal();
    String stringValue2 = new String("2.00"); //$NON-NLS-1$
    literal2.setValue(stringValue2);
    add.getParameters().add(literal2);
    Object resultAdd = add.evaluate(null);
    System.out.println(resultAdd);
    Assert.assertTrue(resultAdd.equals(new BigDecimal("7.00"))); //$NON-NLS-1$
    Subtract sub = new Subtract();
    sub.getParameters().add(literal1);
    sub.getParameters().add(literal2);
    Object resultSub = sub.evaluate(null);
    System.out.println(resultSub);
    Assert.assertTrue(resultSub.equals(new BigDecimal("3.00"))); //$NON-NLS-1$
    Multiply mul = new Multiply();
    mul.getParameters().add(literal1);
    mul.getParameters().add(literal2);
    Object resultMul = mul.evaluate(null);
    System.out.println(resultMul);
    Assert.assertTrue(resultMul.equals(new BigDecimal("10.00"))); //$NON-NLS-1$
    Divide div = new Divide();
    div.getParameters().add(literal1);
    div.getParameters().add(literal2);
    Object resultDiv = div.evaluate(null);
    System.out.println(resultDiv);
    Assert.assertTrue(resultDiv.equals(new BigDecimal("2.5"))); //$NON-NLS-1$
    Function function = new Max();
    Subtract subtract = new Subtract();
    subtract.getParameters().add(new Literal("100")); //$NON-NLS-1$
    Multiply multiply = new Multiply();
    Subtract subtract2 = new Subtract();
    subtract2.getParameters().add(new Literal("0")); //$NON-NLS-1$
    subtract2.getParameters().add(new Literal("20")); //$NON-NLS-1$
    System.out.println("subtract2 = " + subtract2.evaluate(null)); //$NON-NLS-1$
    multiply.getParameters().add(subtract2);
    multiply.getParameters().add(new Literal("5")); //$NON-NLS-1$
    subtract.getParameters().add(multiply);
    System.out.println("subtract = " + subtract.evaluate(null)); //$NON-NLS-1$
    List<Expression> parameters = new ArrayList<Expression>();
    parameters.add(subtract);
    Multiply multiply2 = new Multiply();
    multiply2.getParameters().add(new Literal("100")); //$NON-NLS-1$
    multiply2.getParameters().add(new Literal("0")); //$NON-NLS-1$
    parameters.add(multiply2);
    function.setParameters(parameters);
    Object resultFunction = function.evaluate(null);
    System.out.println(resultFunction);
  }
}
