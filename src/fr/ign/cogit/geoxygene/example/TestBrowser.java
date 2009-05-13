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

package fr.ign.cogit.geoxygene.example;

import java.util.ArrayList;
import java.util.Vector;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.util.browser.ObjectBrowser;

/**
 *  Classe de test pour le navigateur d'objets graphique de GeOxygene (ObjectBrowser), permettant de
 *  montrer comment on appelle celui-ci afin de visualiser graphiquement l'état d'un objet Java.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 *
 */
public class TestBrowser
{
	public String Attribut1 = null;
	public int Attribut2;
	public String[] Attribut3 = {"1","a","f1d8"};
	public Vector<String> Attribut4= new Vector<String>();
	public ArrayList<TestBrowserObjTest> Attribut5= new ArrayList<TestBrowserObjTest>();
	public Object Attribut6= new Object();
	public double[] Attribut7 = {1,143,1.2356};
	public int[] Attribut8 = {1,2,3,4,5,6,7,8};
	public Vector<TestBrowserObjTest> Attribut9 = new Vector<TestBrowserObjTest>();
	public TestBrowserObjTest Attribut10 = new TestBrowserObjTest("Youpla boum !!!",34);
	public boolean Attribut11=true;
	public Vector<Object> Attribut12=new Vector<Object>();
	public int[][] Attribut13={{1,2,3},{4,5,6},{7,8,9}};

	protected String Attribut14;
	public Vector<Vector<Object>> Attribut15 = new Vector<Vector<Object>>();
	@SuppressWarnings("unchecked")
	protected Vector[] Attribut16 = new Vector[2];
	@SuppressWarnings("unchecked")
	protected Vector[][] Attribut17 = new Vector[2][3];

	//protected Departement Attribut18=new Departement();

	//protected StringBuffer Attribut19=new StringBuffer("test of a StringBuffer class");
	//public ArrayList Attribut20= new ArrayList();

	public TestBrowser()
	{

	}

	public TestBrowser(String atr1,int atr2)
	{
		this.Attribut1 = atr1;
		this.Attribut2 = atr2;
	}

	public String getAttribut1() {
		return Attribut1;
	}

	public double[] getAttribut7() {
		return Attribut7;
	}

	@SuppressWarnings("unchecked")
	public static void main(String args[])
	{
		TestBrowser obj = new TestBrowser("Toto",12543);
		obj.Attribut4.add("alpha");
		obj.Attribut4.add("beta");
		obj.Attribut4.add("64");
		obj.Attribut4.add("D2R2 ;-)");

		obj.Attribut5.add(new TestBrowserObjTest());
		obj.Attribut5.add(new TestBrowserObjTest());

		TestBrowserObjTest vobjelt=new TestBrowserObjTest("Yiiiiiiiiipi !!! :))",58);
		obj.Attribut9.add(vobjelt);
		obj.Attribut9.add(new TestBrowserObjTest("Pouet ...",512));
		obj.Attribut9.add(new TestBrowserObjTest("Boing :oP",1024));
		//obj.Attribut9.add(new ObjTest("Plaffff :))",2048));

		obj.Attribut12.add("texte libre");
		obj.Attribut12.add(new TestBrowserObjTest("Et zouip !!!",23));
		obj.Attribut12.add(new TestBrowserObjTest("Plaffff :))",2048));
		obj.Attribut12.add("Et zouip 2 !!! ;-))");
		obj.Attribut12.add("... et ...");
		obj.Attribut12.add(new TestBrowserObjTest("Plaffff :))",2048));
		obj.Attribut12.add("... blaaaaaaaaaammmmmmm !?!!??");
		for (int i = 0; i < 100; i++)
			obj.Attribut12.add("... blaaaaaaaaaammmmmmm !?!!??");

		Vector<Object> vect15a = new Vector<Object>();
		Vector<Object> vect15b = new Vector<Object>();
		vect15a.add(new Integer(15));
		//vect15b.add(new ObjTest());
		obj.Attribut15.add(vect15a);
		obj.Attribut15.add(vect15b);

		obj.Attribut16[0] = new Vector<Object>();
		obj.Attribut16[0].add("toto");
		obj.Attribut16[0].add(new Integer(12));
		obj.Attribut16[0].add(new TestBrowserObjTest("PimPamPoum", 146587));
		obj.Attribut16[1] = new Vector<Object>();
		obj.Attribut16[1].add("titi");
		obj.Attribut16[1].add(new Integer(52));
		obj.Attribut16[1].add(new TestBrowserObjTest("Pouet pouet !!!", 26566));

		obj.Attribut17[0][0]=new Vector();
		obj.Attribut17[0][0].add("?");
		obj.Attribut17[0][1]=new Vector();
		obj.Attribut17[0][1].add("??");
		obj.Attribut17[0][2]=new Vector();
		obj.Attribut17[0][2].add("???");

		DirectPositionList dPosList=new DirectPositionList();
		dPosList.add(new DirectPosition(0,0));
		dPosList.add(new DirectPosition(0,1));
		dPosList.add(new DirectPosition(1,1));
		dPosList.add(new DirectPosition(1,0));
		//obj.Attribut18.setGeom(new GM_LineString(dPosList));

		/* Vector v20=new Vector();
		v20.add("?");
		v20.add("??");
		v20.add("???");
		obj.Attribut20.add(v20);
		obj.Attribut20.add(v20); */

		ObjectBrowser.browse(obj);

		obj.Attribut1="Titi";

		ObjectBrowser.refresh(obj);

	}

}
