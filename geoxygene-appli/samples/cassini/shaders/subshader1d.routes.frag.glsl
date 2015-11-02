#version 330

/* Data structure sent to subshaders */
struct DataPainting {
    float screenWidth;      // screen width in pixels 
    float screenHeight;     // screen height in pixels
    float mapScaleDiv1000;  // map scale divide by 1000 (e.g. 1:100000 maps, this value is 100)
    int brushWidth;         // brush texture width in pixels
    int brushHeight;        // brush texture height in pixels
    int brushStartWidth;    // start texture length in pixels for the brush
    int brushEndWidth;      // end texture length in pixels for the brush
    float brushScale;       // size in mm of one brush pixel
    float paperScale;       // scaling factor for paper
    float strokeSoftness;   // brush-paper blending strokeSoftness
    
    float paperRoughness;     // paper height scale factor
    float brushRoughness;     // brush height scale factor
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

/************************************************************************************/
vec2 computeBrushTextureCoordinates( DataPainting fragmentData ) {
	float uTex = fragmentData.uv.x / 100;
	float vTex = fragmentData.uv.y;
	return vec2( uTex, vTex );
}



/************************************************************************************/
vec4 computeFragmentColor( in vec4 brushColor, in vec4 paperColor, in DataPainting fragmentData ) {
	return vec4( brushColor.rgb * brushColor.rgb, brushColor.a * 5 );
}

