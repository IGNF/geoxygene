#version 330

/* Data structure sent to subshaders */
struct DataPainting {
	float screenWidth; 		// screen width in pixels 
	float screenHeight;		// screen height in pixels
	float mapScaleDiv1000;  // map scale divide by 1000 (e.g. 1:100000 maps, this value is 100)
	int brushWidth; 	    // brush texture width in pixels
	int brushHeight;        // brush texture height in pixels
	int brushStartWidth;    // start texture length in pixels for the brush
	int brushEndWidth;      // end texture length in pixels for the brush
	float brushScale;       // size in mm of one brush pixel
	float paperScale;       // scaling factor for paper
	float sharpness;        // brush-paper blending sharpness
	
	float paperDensity;     // paper height scale factor
	float brushDensity;     // brush height scale factor
	float strokePressure;   // stroke pressure
	vec4 position;          // current point position in world coordinates
	vec2 uv;                // UV coordinates texture (u in world coordinates, v between 0 and 1)
	vec4 color;             // point color
	float thickness;        // line thickness in world coordinates
	float uMax;             // maximum u coordinate in one polyline (in wolrd coordinates)
	vec2 tan;               // tangent vector at the given point (in world coordinates)
	float curvature;        // signed curvature estimation
	
};

uniform float globalOpacity = 1.0;

uniform float brushSpace;
uniform float strokeThickness;
uniform float angle;
uniform float noiseWavelength;
uniform float brushSpaceNoiseSize;
uniform float x;
uniform float y;
uniform float z;
uniform int nbSeeds;
uniform float extremitiesLength;


float extremitiesModifier( in float u, in float uMax ) {
	return (1.0-0.6*smoothstep(uMax - extremitiesLength, uMax, u ));
//return smoothstep(0, extremitiesLength, u ) * (1.0-smoothstep(uMax - extremitiesLength, uMax, u ));
}

float strokeWidthModifier( in float u, in float uMax ) {
return 1.;
	//return extremitiesModifier( u, uMax );
}

float strokePressureModifier( in float u, in float uMax ) {
	return extremitiesModifier( u, uMax );
}


/************************************************************************************/
vec2 computeBrushTextureCoordinates( DataPainting fragmentData ) {	
	return vec2(fragmentData.uv.x, 1-fragmentData.uv.y );
}



/************************************************************************************/
vec4 computeFragmentColor( in vec4 brushColor, in vec4 paperColor, in DataPainting fragmentData ) {	
	return vec4( fragmentData.color.rgb, globalOpacity *(1.0 - brushColor.r)  );
}

