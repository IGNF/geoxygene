#version 330
// example shader to extend in order to provide expressive fill rendering methods

struct DataGradient {
	float screenWidth; 		// screen width in pixels 
	float screenHeight;		// screen height in pixels
	vec4 color;
	vec2 textureUV;
	vec2 worldUV;
	vec2 gradient;
	vec2 worldUVMin;
	vec2 worldUVRange;
};

vec4 computeColor( DataGradient fragmentData ) {
	float dmax = 500;
	float distance = fragmentData.worldUV.y * fragmentData.worldUVRange.y;
	float dd = 1.0 - smoothstep( 0, dmax, distance );
	return vec4( vec3(0.6,0.7,0.8) * dd + (1-dd) * fragmentData.color.rgb, 1.0 );
}

