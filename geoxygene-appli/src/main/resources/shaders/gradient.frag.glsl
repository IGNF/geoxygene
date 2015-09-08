#version 330

uniform float globalOpacity = 1.0;
uniform float objectOpacity = 1.0;
uniform sampler2D gradientTexture;
uniform vec2 uvMinGradientTexture ;
uniform vec2 uvRangeGradientTexture ;
uniform float screenWidth;
uniform float screenHeight;

in VertexData {
	vec4 color;
	vec2 textureUV;
} fragmentIn;

out vec4 outColor;

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

// function declared in subshaders
vec4 computeColor( DataGradient fragmentData );

void main(void) {


	// get values in the binary gradient image
	// .xy : uv coordinates in the distance field (scaled between 0..1)
	// .zw : gradient of the distance field (scaled between 0..1)
	// to unscale U & V use uvMinGradientTexture + worldUV * uvRangeGradientTexture
	vec4 tcolor = texture(gradientTexture, fragmentIn.textureUV * vec2(1,-1) );
	vec2 worldUV = tcolor.rg;
	vec2 gradient = tcolor.ba;

	DataGradient fragmentOut = DataGradient( screenWidth, screenHeight, fragmentIn.color, fragmentIn.textureUV, worldUV, gradient, uvMinGradientTexture, uvRangeGradientTexture );
	// call subshader 
	vec4 color = computeColor( fragmentOut );
	
	// return computed color using object & global opacity
	outColor = vec4( color.rgb, objectOpacity  * color.a);

// DEBUG
//outColor = vec4( fragmentOut.worldUV, 0.0 , 1.0 );	

}