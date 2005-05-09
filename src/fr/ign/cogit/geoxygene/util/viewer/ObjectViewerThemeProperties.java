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

package fr.ign.cogit.geoxygene.util.viewer;

import java.awt.Color;
import java.util.Random;

import uk.ac.leeds.ccg.geotools.DataSource;
import uk.ac.leeds.ccg.geotools.HSVShader;
import uk.ac.leeds.ccg.geotools.HighlightManager;
import uk.ac.leeds.ccg.geotools.LineLayer;
import uk.ac.leeds.ccg.geotools.MonoShader;
import uk.ac.leeds.ccg.geotools.PolygonLayer;
import uk.ac.leeds.ccg.geotools.RandomShader;
import uk.ac.leeds.ccg.geotools.SelectionManager;
import uk.ac.leeds.ccg.geotools.ShadeStyle;
import uk.ac.leeds.ccg.geotools.Shader;
import uk.ac.leeds.ccg.geotools.Theme;
import uk.ac.leeds.ccg.geotools.UniqueShader;
import uk.ac.leeds.ccg.geotools.satShader;

/**
  * This class allow to handle properties of themes to be displayed in the (Geo)Object viewer.  
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

class ObjectViewerThemeProperties {

	//Default
	public static final Color DEFAULT_OUTLINE_THEME_COLOR = Color.BLACK;
	public static final Color DEFAULT_SELECTION_COLOR = Color.YELLOW;
	public static final Color DEFAULT_HIGHLIGHT_COLOR = Color.GREEN;
	public static final Color DEFAULT_MISSING_VALUE_SHADER_COLOR = Color.WHITE;
	public static final int DEFAULT_MISSING_VALUE_SHADER = -999999999;
	
	private ObjectViewerInterface objectViewerInterface;
	private Theme objectViewerTheme;
	
	private String dataSourceType;
	private DataSource dataSource;
	
	private Shader shader;	
	private String shadedBy = null;

	private Color fillInThemeColor;
	private Color outlineThemeColor;
	private Color fillInHighlightColor;
	private Color outlineHighlightColor;
	private Color fillInSelectionColor;
	private Color outlineSelectionColor;
	
	private SelectionManager themeSelectionManager;
	private HighlightManager themeHighlightManager;
	
	private boolean active;
	private boolean visible;


	private ObjectViewerThemeProperties() {
		
		// random color for filling it
		Random randomColor = new Random();
		float randomRed = randomColor.nextFloat();
		float randomGreen = randomColor.nextFloat();
		float randomBlue = randomColor.nextFloat();
		System.out.println("Theme Color (R: "+ randomRed * 255+ ";G: "+ randomGreen * 255+ ";B: "+ randomBlue * 255	+ ")");
		Color randomShaderColor = new Color(randomRed, randomGreen, randomBlue);
		setFillInThemeColor(randomShaderColor);
		
		// default color
		setOutlineThemeColor(DEFAULT_OUTLINE_THEME_COLOR);
		setFillInHighlightColor(DEFAULT_HIGHLIGHT_COLOR);
		setOutlineHighlightColor(DEFAULT_HIGHLIGHT_COLOR);
		setFillInSelectionColor(DEFAULT_SELECTION_COLOR);
		setOutlineSelectionColor(DEFAULT_SELECTION_COLOR);
		
	}
	

	public ObjectViewerThemeProperties(	ObjectViewerInterface objectViewerInterface,
										Theme objectViewerTheme,
										String dataSourceType,
										DataSource datasource,
										boolean IsActive,
										boolean IsVisible ) {
		this();
		setObjectViewerInterface(objectViewerInterface);
		setObjectViewerTheme(objectViewerTheme);
		setDataSource(datasource);
		setDataSourceType(dataSourceType);
		setActive(IsActive);
		setVisible(IsVisible);
	}


	public void setChanged() {
		Theme t = getObjectViewerTheme();
		
		if (	(t.getLayer() instanceof PolygonLayer)
			 && (!(t.getLayer() instanceof LineLayer)) ) {
			ShadeStyle updatedThemeShadeStyle = new ShadeStyle();
			updatedThemeShadeStyle.setLineColor(getOutlineThemeColor());
			t.setStyle(updatedThemeShadeStyle);
		}

		ShadeStyle updatedHighlightShadeStyle = new ShadeStyle();
		updatedHighlightShadeStyle.setFillColor(getFillInHighlightColor());
		updatedHighlightShadeStyle.setLineColor(getOutlineHighlightColor());
		t.setHighlightStyle(updatedHighlightShadeStyle);

		ShadeStyle updatedSelectionShadeStyle = new ShadeStyle();
		updatedSelectionShadeStyle.setFillColor(getFillInSelectionColor());
		updatedSelectionShadeStyle.setLineColor(getOutlineSelectionColor());
		t.setSelectionStyle(updatedSelectionShadeStyle);

		if (shader instanceof MonoShader) {
			MonoShader updatedShader = new MonoShader(getFillInThemeColor());
			t.setShader(updatedShader);
			
		} else if (shader instanceof RandomShader) {
			RandomShader updatedShader = new RandomShader();
			t.setShader(updatedShader);

				
		} else if (shader instanceof HSVShader) {
			//HSVShader updatedShader = new HSVShader(DEFAULT_LOW_HSVSHADER_COLOR, DEFAULT_HIGH_HSVSHADER_COLOR); marche pas
			satShader updatedShader = new satShader(getFillInThemeColor());
			updatedShader.setMissingValueCode(DEFAULT_MISSING_VALUE_SHADER);
			updatedShader.setMissingValueColor(DEFAULT_MISSING_VALUE_SHADER_COLOR);
			/*if (dataSourceType.equals(Utils.SHAPEFILE)) {
				System.out.println("Shader HSV with field = "+shadedBy);
				ShapefileReader shpRd = (ShapefileReader) dataSource;
				t = shpRd.getTheme(updatedShader,shadedBy);		
			} a faire
			
			else */if (dataSourceType.equals(Utils.GEOXYGENE)) {
				GeOxygeneReader geOxyRd = (GeOxygeneReader) dataSource;
				t = geOxyRd.getTheme(updatedShader,shadedBy);
			}
		
		
		} else if (shader instanceof UniqueShader) {
			UniqueShader updatedShader = new UniqueShader();
			updatedShader.setMissingValueCode(DEFAULT_MISSING_VALUE_SHADER);
			updatedShader.setMissingValueColor(DEFAULT_MISSING_VALUE_SHADER_COLOR);
			/*if (dataSourceType.equals(Utils.SHAPEFILE)) {
				a faire
			}
				
			else */ if (dataSourceType.equals(Utils.GEOXYGENE)) {
				GeOxygeneReader geOxyRd = (GeOxygeneReader) dataSource;
				t = geOxyRd.getTheme(updatedShader,shadedBy);
			}
		}
			
		getObjectViewerInterface().view.setThemeIsVisible(t, true, true);
		
	}



	public void setObjectViewerInterface(ObjectViewerInterface objectViewerInterface) {
		this.objectViewerInterface = objectViewerInterface;
	}

	public ObjectViewerInterface getObjectViewerInterface() {
		return objectViewerInterface;
	}

	public void setFillInThemeColor(Color color) {
		this.fillInThemeColor = color;
	}

	public Color getFillInThemeColor() {
		return fillInThemeColor;
	}

	public void setOutlineThemeColor(Color color) {
		this.outlineThemeColor = color;
	}

	public Color getOutlineThemeColor() {
		return outlineThemeColor;
	}

	public void setFillInHighlightColor(Color color) {
		this.fillInHighlightColor = color;
	}

	public Color getFillInHighlightColor() {
		return fillInHighlightColor;
	}

	public void setOutlineHighlightColor(Color color) {
		this.outlineHighlightColor = color;
	}

	public Color getOutlineHighlightColor() {
		return outlineHighlightColor;
	}

	public void setFillInSelectionColor(Color color) {
		this.fillInSelectionColor = color;
	}

	public Color getFillInSelectionColor() {
		return fillInSelectionColor;
	}

	public void setOutlineSelectionColor(Color color) {
		this.outlineSelectionColor = color;
	}

	public Color getOutlineSelectionColor() {
		return outlineSelectionColor;
	}

	public void setObjectViewerTheme(Theme t) {
		this.objectViewerTheme = t;
	}

	public Theme getObjectViewerTheme() {
		return objectViewerTheme;
	}

	public String getDataSourceType() {
		return dataSourceType;
	}

	public void setDataSourceType(String string) {
		dataSourceType = string;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource source) {
		dataSource = source;
	}
	
	public void setShader (Shader sh) {
		shader = sh;
	}
	
	public void setShader (Shader sh, String field) {
		shader = sh;
		if (field != null) shadedBy = field;
		else field = null;
	}
	
	public Shader getShader() {
		return shader;
	}
	
	public String getShadedBy() {
		return shadedBy;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean b) {
		active = b;
	}

	public HighlightManager getThemeHighlightManager() {
		return themeHighlightManager;
	}
	
	public void setThemeHighlightManager(HighlightManager manager) {
		themeHighlightManager = manager;
	}

	public SelectionManager getThemeSelectionManager() {
		return themeSelectionManager;
	}

	public void setThemeSelectionManager(SelectionManager manager) {
		themeSelectionManager = manager;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean b) {
		visible = b;
	}

}
