/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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

package fr.ign.cogit.geoxygene.util.algo;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.DefaultCoordinateSequenceFactory;
import com.vividsolutions.jts.geom.TopologyException;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbOracle;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.ImgUtil;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;


/** 
  * Test et comparaison des methodes geometriques de differentes bibliotheques.
  * Sont prevues : Oracle, Jts, Geos.
  * On peut facilement completer le test par de nouvelles bibliotheques 
  * en creant une nouvelle classe fille de GeomAlgorithms 
  * et en l'appelant dans cette application.
  * Le programme genere des images et des fichiers SVG compresses. 
  * Un fichier de comparaison des temps de calcul entre les differentes bibliotheques est aussi genere.
  * On charge un nombre progressif d'objets, en utilisant le rectangle englobant (enveloppe) des couches testees
  * et en faisant varier par un facteur la taille de cette enveloppe (variables a regler au debut du code). 
  * A l'aide d'une boucle, on charge progressivement de plus en plus d'objets.
  *
  * @author Thierry Badard, Arnaud Braun & Christophe Pele 
  * @version 1.0
  * 
  */


public class CompareLib  {

    private static IndentedPrintStream out;
    private static PrintStream err=System.err;
    
    private Geodatabase db;
    
    // classes a charger
    private Class featClass1;
    private Class featClass2;    
    
    // emprise maximale des geometries, recuperees dans les metadonnees du chargement
    private GM_Envelope baseEnvelope;
    
    // les bibliotheques a tester
    private GeomAlgorithms jts;
    private GeomAlgorithms oracle;
    private GeomAlgorithms geos;
       
    // les resultats des operateurs geometriques
    private Object resultJts;
    private Object resultOracle;
    private Object resultGeos;
    
    
    
    /* ############################################################# */
    /* variable permettant de parametrer le test, a initialiser ici  */
    /* ############################################################# */
        
    /* liste des algos a tester */
    private String[] algorithmsName = new String[] {
                                            "buffer10",
                                            "convexHull",
                                            "centroid",
                                            "intersection",
                                            "union", 
                                            "difference",
                                            "symDifference",
                                            "length", 
                                            "area", 
                                            "distance",
                                            "equals",
                                            "contains", 
                                            "intersects" };
                                            
    /* Les noms de classes de feature a charger */
    private String featClassName1 = "geoschema.feature.Topo_bati_extrait5";
    private String featClassName2 = "geoschema.feature.Topo_bati_extrait5_translate";    
    
    /* agregats geometriques issus du chargement des classes ; 
     * definir ici si c'est des GM_MultiCurve ou des GM_MultiSurface */
    private GM_Aggregate geom1 = new GM_MultiSurface();  /*new GM_MultiCurve(); */
    private GM_Aggregate geom2 = new GM_MultiSurface();  /*new GM_MultiCurve(); */    

    /* Le repertoire de sauvegarde des resultats */
    private String path = "/home/users/braun/testJtsOracle";
    
    /* definit les bibliotheques qu'on teste */    
    private boolean testJts = true;
    private boolean testOracle = true;
    private boolean testGeos = false;
    
    /* facteurs pour la boucle faisant varier la taille de l'enveloppe definissant les objets a charger */
    private double min = 0.2;
    private double max = 1.0;
    private double step = 0.2;
    
    /* indique si on sauve les fichiers SVGZ et les images au format .png*/
    private boolean saveSvg = true;
    private boolean saveImage = false;
    


    
    // ########################################################################################    
    public CompareLib()
    {
        out = new IndentedPrintStream(System.out);
        out.println("Begin!");  
        
        out.print("connecting database ... ");        
        db=new GeodatabaseOjbOracle();    
        out.println("OK");

        if (testJts) jts=new JtsAlgorithms();
        if (testOracle) oracle=new OracleAlgorithms(db,0.0000000005);
        if (testGeos) geos=new GeosAlgorithms();
        
        try {
           featClass1=Class.forName(featClassName1);
           featClass2=Class.forName(featClassName2);
        } catch (Exception e) {
            err.println("## Classes geographiques non trouvées ##");
            System.exit(0);
        }
        out.println("test class 1 : "+featClass1.getName());
        out.println("test class 2 : "+featClass2.getName());

        out.println("Computing envelope...");    
  
        try {
            GM_Envelope baseEnvelope1 = db.getMetadata(featClass1).getEnvelope();
            GM_Envelope baseEnvelope2 = db.getMetadata(featClass2).getEnvelope();
            // on prend l'enveloppe maximale des deux enveloppes
            baseEnvelope1.expand(baseEnvelope2);
            baseEnvelope = (GM_Envelope)baseEnvelope1.clone();
        } catch (Exception e) {
            err.println("## Problemes en recuperant l'emprise des couches dans les metadonnees ##");
            System.exit(0);         
        }
      
        out.println("envelope : "+baseEnvelope);
        out.println("test path : "+path);
        
    }
    
    
    // ########################################################################################
    private void initFiles(String[] outDirPathTab, PrintStream[] dataOutTab) throws Exception
    {
    	out.indentRight();
    	
    	int numAlgos=algorithmsName.length;
    
    	for (int i=0; i<numAlgos; i++) {
    		String algoName=algorithmsName[i];   
    		String outDirPath=outDirPathTab[i]=path+"/"+algoName+"/";
    		File outDirFile=new File(outDirPath);
    		outDirFile.mkdirs();    
    		PrintStream dataOut=dataOutTab[i]=new PrintStream(new FileOutputStream(outDirPath+"test.dat"));
    
    		dataOut.println("# vim:ts=10");
            dataOut.println("# Les temps sont donnés en millisecondes");
    		dataOut.println();    
    		String datLine;
    		datLine=""+"factor\t"+"size\t";
            
            if (testJts) datLine=datLine+"jts\t";
            if (testOracle) datLine=datLine+"oracle\t";
            if (testGeos) datLine=datLine+"geos\t";                        
            if (testJts && testOracle ) datLine=datLine+"jts/oracle\t";
            if (testGeos && testOracle ) datLine=datLine+"geos/oracle\t";                                               

    		dataOut.println(datLine);
    		dataOut.println();
    	}    	
    	out.indentLeft();
    }
    
    
    // ########################################################################################
    private void loadInputGeom(double factor) throws Exception
    {
    	out.indentRight();
    	
    	long time;
    	time=time();
    
    	GM_Envelope baseEnvelope=(GM_Envelope)this.baseEnvelope.clone();    
    	baseEnvelope.expandBy(factor);
    	GM_Polygon bbox=new GM_Polygon(baseEnvelope);
        
        FT_FeatureCollection featList1=db.loadAllFeatures(featClass1, bbox);
        FT_FeatureCollection featList2=db.loadAllFeatures(featClass2, bbox);  
        
        // creation des agregats
        geom1.clear();
        featList1.initIterator();
        while (featList1.hasNext()) geom1.add(featList1.next().getGeom());
        geom2.clear();
        featList2.initIterator();
        while (featList2.hasNext()) geom2.add(featList2.next().getGeom());
        
        out.println("Creation: "+(time()-time)/1000.+" seconds");
        out.println("Size of geom1: "+geom1.size());
        out.println("Size of geom2: "+geom2.size());
    
        out.println("Envelope of geom1:");
        out.indentRight();
        out.println(geom1.envelope().toString());
        out.indentLeft();
    
        out.println("Envelope of geom2:");
        out.indentRight();
        out.println(geom2.envelope().toString());
        out.indentLeft();
    
        out.indentLeft();
    }


    // ########################################################################################    
    private void saveImagesCm(int nbParameters, String outDirPath, String factorString, int width, int height) throws Exception
    {
    	out.indentRight();
    	
    	Color colorG1=		Color.GREEN;
    	Color colorG2=		Color.BLUE;
    	Color colorResult=	Color.RED;
    	Color bg=			Color.WHITE;
    				
        GM_Object[] geomsJts;
        GM_Object[] geomsOracle;
        GM_Object[] geomsGeos;      
        
        Color[] colors;
        if (nbParameters==1) {
        	geomsJts= new GM_Object[] {(GM_Object)resultJts,geom1};
            geomsOracle= new GM_Object[] {(GM_Object)resultOracle,geom1};
            geomsGeos= new GM_Object[] {(GM_Object)resultGeos,geom1};
        	colors=new Color[] {colorResult,colorG1};    
        } else /*if (nbParameters==2) */ {
            geomsJts= new GM_Object[] {(GM_Object)resultJts,geom1,geom2};
            geomsOracle= new GM_Object[] {(GM_Object)resultOracle,geom1,geom2};
            geomsGeos= new GM_Object[] {(GM_Object)resultGeos,geom1,geom2};                        
            colors=new Color[] {colorG1,colorG2,colorResult};                            
        }
        
        if (testJts) {
        	String pfx1=outDirPath+"jts_"+factorString;
        	String imgPath1=pfx1+".png";
        	String svgzPath1=pfx1+".svg.z";    		
        	out.println("Saving "+pfx1+"...");
        	if (saveImage) ImgUtil.saveImage(geomsJts,imgPath1,colors,bg,width,height);
            if (saveSvg) ImgUtil.saveSvgz(geomsJts,svgzPath1,colors,bg,width,height);
        }
    			
        if (testOracle) {
            String pfx1=outDirPath+"oracle_"+factorString;
            String imgPath1=pfx1+".png";
            String svgzPath1=pfx1+".svg.z";         
            out.println("Saving "+pfx1+"...");
            if (saveImage) ImgUtil.saveImage(geomsOracle,imgPath1,colors,bg,width,height);
            if (saveSvg) ImgUtil.saveSvgz(geomsOracle,svgzPath1,colors,bg,width,height);
        }
        
        if (testGeos) {
            String pfx1=outDirPath+"geos_"+factorString;
            String imgPath1=pfx1+".png";
            String svgzPath1=pfx1+".svg.z";         
            out.println("Saving "+pfx1+"...");
            if (saveImage) ImgUtil.saveImage(geomsGeos,imgPath1,colors,bg,width,height);
            if (saveSvg) ImgUtil.saveSvgz(geomsGeos,svgzPath1,colors,bg,width,height);
        }
    
    	out.indentLeft();
    }


    // ########################################################################################    
    private long launchJts(String realAlgoName, Class[] algoParamTypes, Object[] algoParameters) throws Exception
    {
        try {
        	out.indentRight();
        
        	Class jtsAlgoClass=JtsAlgorithms.class;
        	Method jtsAlgo=jtsAlgoClass.getMethod(realAlgoName,algoParamTypes);
        	
        	out.println("JTS...");
        	long time=time();
        	try {
        		resultJts=jtsAlgo.invoke(jts,algoParameters); 
        	} catch (InvocationTargetException e) {
        		Throwable cause=e.getCause();
        		cause.printStackTrace();
        		if (cause instanceof TopologyException) {
        			CoordinateSequence jtsCoord= DefaultCoordinateSequenceFactory.instance()
                                                .create(new Coordinate[] {((TopologyException)cause).getCoordinate()});
        			DirectPosition geOxyCoord=JtsGeOxygene.makeDirectPosition(jtsCoord);
        			resultJts=new GM_Point(geOxyCoord);
        		}
        	}
        	time=time()-time;
        	out.println("JTS: "+(time/1000.)+" seconds");
        	long jtsTime=time;
        	
        	out.indentLeft();	
        	return jtsTime;
            
        } catch (Exception e) {
            e.printStackTrace();
            return 10000000;
        }            
    }


    // ########################################################################################    
    private long launchOracle(String realAlgoName, Class[] algoParamTypes, Object[] algoParameters)  
    {
        try {
        	out.indentRight();
        	
        	Class oracleAlgoClass=OracleAlgorithms.class;
        	Method oracleAlgo=oracleAlgoClass.getMethod(realAlgoName,algoParamTypes);
        			
        	out.println("Oracle...");
        	long time=time();
        	resultOracle=oracleAlgo.invoke(oracle,algoParameters); 
        	time=time()-time;
        	out.println("Oracle: "+(time/1000.)+" seconds");
        	long oracleTime=time;
        
        	out.indentLeft();
        
        	return oracleTime;

        } catch (Exception e) {
            e.printStackTrace();
            return 10000000;
        }
    }
 
 
    // ########################################################################################   
    private long launchGeos(String realAlgoName, Class[] algoParamTypes, Object[] algoParameters) throws Exception
    {
        try {
        	out.indentRight();
        	
        	Class geosAlgoClass=GeosAlgorithms.class;
        	Method geosAlgo=geosAlgoClass.getMethod(realAlgoName,algoParamTypes);
        			
        	out.println("Geos...");
        	long time=time();
        	resultGeos=geosAlgo.invoke(geos,algoParameters); 
        	time=time()-time;
        	out.println("Geos: "+(time/1000.)+" seconds");
        	long geosTime=time;
        
        	out.indentLeft();
        
        	return geosTime;
            
        } catch (Exception e) {
            e.printStackTrace();
            return 10000000;
        }            
    }


    // ########################################################################################    
    private void launchAlgorithms(String[] outDirPathTab,	PrintStream[] dataOutTab, String factorString) throws Exception
    {
    	out.indentRight();
        
        Class[] algoParamTypes=null;
        Object[] algoParameters=null;
        Class returnType=null;
        Class geomAlgoClass=GeomAlgorithms.class;
        Method[] geomAlgos=geomAlgoClass.getMethods();
        int nbParameters=0;
        
    	for (int i=0; i<algorithmsName.length; i++) {
    		PrintStream dataOut=dataOutTab[i];
    		String outDirPath=outDirPathTab[i];
    		String realAlgoName=algorithmsName[i];
    			
    		out.println(realAlgoName);
            
            /*-- ...Choix des parametres ---------------------------------*/
            for (int j=0; j<geomAlgos.length; j++) {
                Method geomAlgo = geomAlgos[j];
                if (geomAlgo.getName().equals(realAlgoName)) {
                    nbParameters = geomAlgo.getParameterTypes().length;
                    returnType = geomAlgo.getReturnType();
                    break;
                }
            }
            if (nbParameters==1)  {
                algoParamTypes = new Class[] {GM_Object.class};
                algoParameters = new Object[] {geom1};
            } else if (nbParameters==2){
                algoParamTypes = new Class[] {GM_Object.class, GM_Object.class};                
                algoParameters = new Object[] {geom1, geom2};                
            } else {
                err.println(" ## Probleme dans le choix des parametres ## ");
            }
            
    			
    		/*-- ...Geos -------------------------------------------------*/   		
    		long geosTime=0;
            if (testGeos) geosTime=launchGeos(realAlgoName,algoParamTypes,algoParameters);
    			
    		/*-- ...Jts ----------------------------------------------------*/    		
    		long jtsTime=0;
            if (testJts) jtsTime=launchJts(realAlgoName,algoParamTypes,algoParameters);
    	
    		/*-- ...Oracle -------------------------------------------------*/    		
    		long oracleTime=0;
            if (testOracle) oracleTime=launchOracle(realAlgoName,algoParamTypes,algoParameters);
    			
    		/*-- Print line in testresult file ---------------------------*/    			
    		String datLine;
    		datLine=""+factorString+'\t'+geom1.size()+'\t';
            if (nbParameters==2) datLine += geom2.size()+'\t';
            
            if (testJts) datLine=datLine+jtsTime+'\t';
            if (testOracle) datLine=datLine+oracleTime+'\t';
            if (testGeos) datLine=datLine+geosTime+'\t';                        
            if (testJts && testOracle ) datLine=datLine+((double)jtsTime)/oracleTime+'\t';
            if (testGeos && testOracle ) datLine=datLine+((double)geosTime)/oracleTime;      
                                                
    		dataOut.println(datLine);
    	
    		/*-- Make images ----------------------------------------*/  
            if (returnType==GM_Object.class) {	
        		out.indentRight();
        		out.println("Saving images...");    
        		int width = (int) baseEnvelope.width()/100;
        		int height = (int) baseEnvelope.length()/100;
        		saveImagesCm(nbParameters,outDirPath,factorString,width,height);
        		out.indentLeft();
            } else {
                out.indentRight();
                out.indentRight();
                if (testJts) out.println("result jts : "+resultJts);
                if (testOracle) out.println("result oracle : "+resultOracle);
                if (testGeos) out.println("result geos : "+resultGeos);     
                out.indentLeft();  
                out.indentLeft();          
            }
    	}
    	
    	out.indentLeft();
    }
    
    
    // ########################################################################################
    public void run() throws Exception
    {
    	out.indentRight();
    		
    	/*-- Create output directories and testresult files --------------*/    	
    	String[] outDirPathTab=new String[algorithmsName.length]; 
    	PrintStream[] dataOutTab=new PrintStream[outDirPathTab.length];   
    	initFiles(outDirPathTab,dataOutTab);
    	
        /* boucle reglant la taille des envelopes pour le chargement des objets */
    	for (double factor=min; factor<=max; factor+=step) {
    		DecimalFormat format=new DecimalFormat();
    		format.setMaximumFractionDigits(2);
    		String factorString=format.format(factor);   		
    		out.println("Factor="+factorString);
    
    		/*-- Create input geometries ---------------------------------*/    	 
    		out.println("Creating input geometries...");
    		loadInputGeom(factor);
    		
    		/*-- Launch algorithms ---------------------------------------*/
        	out.println("Launching algorithms...");
    		launchAlgorithms(outDirPathTab,dataOutTab,factorString);
    	}    
    	out.indentLeft();
    }


    // ########################################################################################    
    public static void main(String[] args) throws Exception
    {    
    	CompareLib testApp= new CompareLib();    	
    	testApp.run();    
    }


    // ########################################################################################        
    private long time() {return System.currentTimeMillis();}


    // ########################################################################################        
    private class IndentedPrintStream extends PrintStream {
        private String indent="";

        public IndentedPrintStream(OutputStream out)
        {
            super(out);
        }

        public IndentedPrintStream(OutputStream out, boolean autoFlush) 
        {
            super(out, autoFlush);
        }

        public IndentedPrintStream(
            OutputStream out,
            boolean autoFlush,
            String encoding)
        throws UnsupportedEncodingException
        {
            super(out, autoFlush, encoding);
        }

        public void println(String x)
        {
            StringTokenizer tkz=new StringTokenizer(x, "\n");
            while (tkz.hasMoreTokens()) {
                String line=tkz.nextToken();
                super.print(indent);
                super.println(line);
            }
        }
    
        public void indentRight() {indent+="\t";}
        public void indentLeft() {indent=indent.replaceFirst("\t$","");}
    }
    
}
