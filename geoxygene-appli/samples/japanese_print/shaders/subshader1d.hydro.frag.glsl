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
uniform float extremityLength = 1.0;
uniform float thickness = 1.0;
uniform float hardness = 1.0;
uniform float noiseWavelength = 300;


float hash(in float p) { return fract(sin(p) * 43758.2317); }
float hash(in vec2 p) { return hash(dot(p, vec2(87.1, 313.7))); }
vec2 hash2(in float p) {
	float x = hash(p);
	return vec2(x, hash(p+x));
}
vec2 hash2(in vec2 p) { return hash2(dot(p, vec2(87.1, 313.7))); }

float noise(in vec2 p) {
	vec2 F = floor(p), f = fract(p);
	f = f * f * (3. - 2. * f);
	return mix(
		mix(hash(F), 			 hash(F+vec2(1.,0.)), f.x),
		mix(hash(F+vec2(0.,1.)), hash(F+vec2(1.)),	  f.x), f.y);
}

vec2 noise2(in vec2 p) {
	vec2 F = floor(p), f = fract(p);
	f = f * f * (3. - 2. * f);
	return mix(
		mix(hash2(F), 			  hash2(F+vec2(1.,0.)), f.x),
		mix(hash2(F+vec2(0.,1.)), hash2(F+vec2(1.)),	f.x), f.y);
}

float fnoise(in vec2 p) {
	return .5 * noise(p) + .25 * noise(p*2.03) + .125 * noise(p*3.99);
}

/************************************************************************************/



float computeWidth( DataPainting fragmentData ) {
	return fnoise(vec2(fragmentData.uv.x,0.0) / noiseWavelength) / 2.0;
}


/************************************************************************************/
vec2 computeBrushTextureCoordinates( DataPainting fragmentData ) {
	float uTex = fragmentData.uv.x / 100.0;
	float vTex = fragmentData.uv.y;
	return vec2( uTex, vTex );
}



/************************************************************************************/
vec4 computeFragmentColor( in vec4 brushColor, in vec4 paperColor, in DataPainting fragmentData ) {
	vec2 uv = fragmentData.uv;
	float start = smoothstep(0, extremityLength, uv.x );
	float end = 1.0 - smoothstep(fragmentData.uMax - extremityLength, fragmentData.uMax, uv.x );
	float width = computeWidth( fragmentData );
	float widthOpacity = exp( -pow( 1.0 / width * thickness * (0.5 - uv.y), 2.0) ); 
	float opacity = 1.0 * start * end * widthOpacity;
	opacity = smoothstep(0.5-hardness / 2.0, 0.5 + hardness / 2.0, opacity);
	vec3 color = fragmentData.color.rgb;
	return vec4( color, globalOpacity * opacity * fragmentData.color.a);
}


