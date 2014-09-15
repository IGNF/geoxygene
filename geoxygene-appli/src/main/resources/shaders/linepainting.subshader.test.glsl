#version 150

struct Data {
	float screenWidth;
	float screenHeight;
	float mapScaleDiv1000; // map scale
	int brushWidth; // brush texture width (pixels)
	int brushHeight; // brush texture height (pixels)
	int brushStartWidth; // brush texture width (pixels)
	int brushEndWidth; // brush texture height (pixels)
	float brushScale; // size in mm of one brush pixel
	float paperScale; // scaling factor for paper
	float sharpness; // brush-paper blending sharpness
	
	float paperDensity; // paper height scale factor
	float brushDensity; // brush height scale factor
	float strokePressure; // stroke pressure
	vec4 position;
	vec2 uv;
	vec2 paperUV;
	vec4 color;
	float curvature;
	float thickness;
	float uMax;
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
vec4 computeFragmentColor( in vec4 brushColor, in vec4 paperColor, in Data fragmentData ) {

	return vec4( paperColor.rgb , 1 );
}
