#version 400

uniform float globalOpacity = 1.0;
uniform float objectOpacity = 1.0;
uniform sampler2D gradientTexture;

in VertexData {
	vec4 color;
	vec2 gradientUV;
} fragmentIn;

out vec4 outColor;

struct DataGradient {
	vec4 color;
	vec2 gradientUV;
	float gradientValue;
};

vec4 computeColor( DataGradient data ) {
    return vec4(1.0,0.0,0.0,1.0);
}


void main(void) {
	vec3 tcolor = texture(gradientTexture, fragmentIn.gradientUV );
	float gradientValue = (tcolor.r + tcolor.g + tcolor.b ) / 3.0;
	DataGradient data = DataGradient( fragmentIn.color, fragmentIn.gradientUV, gradientValue ); 
	vec4 color = computeColor( data );
	outColor = vec4( color.rgb, objectOpacity * globalOpacity * color.a);
}