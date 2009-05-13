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

package fr.ign.cogit.geoxygene.filter.converter;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import fr.ign.cogit.geoxygene.filter.expression.BinaryExpression;
import fr.ign.cogit.geoxygene.filter.expression.Expression;
import fr.ign.cogit.geoxygene.filter.expression.ExpressionFactory;
import fr.ign.cogit.geoxygene.filter.expression.Function;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;

/**
 * @author Julien Perret
 *
 */
public class ExpressionConverter implements Converter {

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		Expression expression = (Expression) source;
		writer.startNode(expression.getClass().getSimpleName());
		if (expression instanceof Literal) writer.setValue((String)((Literal)expression).getValue());
		else if (expression instanceof PropertyName) writer.setValue(((PropertyName)expression).getPropertyName());
		else if (expression instanceof Function) {
			writer.addAttribute("name", ((Function)expression).getName());
			for (Expression param:((Function)expression).getParameters()) {
				context.convertAnother(param,this);
			}
		} else {
			BinaryExpression binary = (BinaryExpression) expression;
			context.convertAnother(binary.getExpression1(),this);
			context.convertAnother(binary.getExpression2(),this);
		}
		writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Expression expression = ExpressionFactory.createExpression(reader.getNodeName());
		if (expression == null) {
			// a priori, c'est une fonction
			expression = ExpressionFactory.createFunction(reader.getAttribute("name"));
			List<Expression> parameters = new ArrayList<Expression>();
			while (reader.hasMoreChildren()) {
				reader.moveDown();
				Expression parameter = (Expression) context.convertAnother(expression,Expression.class);
				parameters.add(parameter);
				reader.moveUp();
			}
			((Function)expression).setParameters(parameters);
		} else if (BinaryExpression.class.isAssignableFrom(expression.getClass())) {
    		reader.moveDown();
    		((BinaryExpression)expression).setExpression1((Expression)context.convertAnother(expression,Expression.class));
    		reader.moveUp();
    		reader.moveDown();
    		((BinaryExpression)expression).setExpression2((Expression)context.convertAnother(expression,Expression.class));
    		reader.moveUp();
        } else if (expression instanceof Literal){
        	((Literal)expression).setValue(reader.getValue());
        } else if (expression instanceof PropertyName){
        	((PropertyName)expression).setPropertyName(reader.getValue());
        } else {
        	System.out.println("Expression de type inconnu "+reader.getNodeName());
        }
		return expression;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canConvert(Class classe) {return classe.equals(Expression.class);}
}
