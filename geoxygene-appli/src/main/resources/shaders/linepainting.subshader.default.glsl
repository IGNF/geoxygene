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
vec2 computeBrushTextureCoordinates( Data fragmentIn ) {
	float u_w = fragmentIn.uv.x;
	float u_tex = 0;
	float strokeWidth = 1;
	float v_tex = vTextureScale( strokeWidth , fragmentIn.uv.y ) + strokeWidth * ( 1 - strokeWidth );

	float brushStartLength_w = fragmentIn.brushStartWidth * fragmentIn.brushScale;
	float brushEndLength_w = fragmentIn.brushEndWidth * fragmentIn.brushScale;
	float brushMiddleLength_w = (fragmentIn.brushWidth - fragmentIn.brushStartWidth - fragmentIn.brushEndWidth) * fragmentIn.brushScale;
	
	float brush0_tex = fragmentIn.brushStartWidth / float(fragmentIn.brushWidth);
	float brush1_tex = 1f - fragmentIn.brushEndWidth / float(fragmentIn.brushWidth);
	if ( u_w <= brushStartLength_w ) {
		u_tex = (u_w / brushStartLength_w) * brush0_tex;
	} else if ( u_w >= fragmentIn.uMax - brushEndLength_w ) {
		u_tex = ( u_w - fragmentIn.uMax ) * ( 1 - brush1_tex ) / brushEndLength_w - 1;   
	} else {
		float polylineMiddleLength_w = fragmentIn.uMax - (brushStartLength_w + brushEndLength_w);
		int nbTiles = max ( int( round( polylineMiddleLength_w / brushMiddleLength_w ) ), 1 );
		int nTile = int((u_w - brushStartLength_w )/(polylineMiddleLength_w / float(nbTiles)));
		float tileSize_w = polylineMiddleLength_w / float(nbTiles);
		u_tex = mod( u_w - brushStartLength_w, tileSize_w) / tileSize_w * ( brush1_tex - brush0_tex ) + brush0_tex; 
	}
	return vec2(u_tex, v_tex);
}

/************************************************************************************/
vec4 computeFragmentColor( in vec4 brushColor, in vec4 paperColor, in Data fragmentData ) {

	return vec4( fragmentData.color.rgb * brushColor.rgb * paperColor.rgb, fragmentData.color.a );
}
