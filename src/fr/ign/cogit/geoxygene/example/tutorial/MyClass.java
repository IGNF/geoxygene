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
 * 
 */

package fr.ign.cogit.geoxygene.example.tutorial;

/**
 * classe exemple
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */


public class MyClass {


	/** Constructeur. */
	public MyClass() {
	}


	/** Identifiant. */
	protected int id;
	public int getId() { return this.id; }
	public void setId(int i) { this.id = i; }


	/** Attribut de type entier. */
	protected int field0;
	public int getField0() { return this.field0; }
	public void setField0(int i) { this.field0 = i; }


	/** Attribut de type String. */
	protected String field1;
	public String getField1() { return this.field1; }
	public void setField1(String string) { this.field1 = string; }


	/** Attribut de type boolean. */
	protected boolean field2;
	public boolean getField2() { return this.field2; }
	public void setField2(boolean b) { this.field2 = b; }


	/** Attribut de type double. */
	protected double field3;
	public double getField3() { return this.field3; }
	public void setField3(double d) { this.field3 = d; }


	/** Affiche "bonjour". */
	public void bonjour() {
		System.out.println("bonjour"); //$NON-NLS-1$
	}
}
