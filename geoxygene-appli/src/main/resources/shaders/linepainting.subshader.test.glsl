#version 150

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
	
};



// v is scaled from [0..1] to [0.5-width/2..0.5+width/2]
float vTextureScale( in float width, in float v ) {
	float scaledV = 0.5 + (v - 0.5) / width;
	if ( scaledV < 0 ) return 0;
	if ( scaledV > 1 ) return 1;
	return scaledV;
}

/************************************************************************************/
vec2 computeBrushTextureCoordinates( Data fragmentData ) {
	return vec2(fragmentData.uv);
}

/************************************************************************************/
vec4 computeFragmentColor( in vec4 brushColor, in vec4 paperColor, in DataPainting fragmentData ) {

	return vec4( paperColor.rgb , 1 );
}
