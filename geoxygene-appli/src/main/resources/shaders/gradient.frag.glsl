#version 410

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

void main(void) {
	vec4 tcolor = texture(gradientTexture, fragmentIn.gradientUV );
	outColor = vec4( tcolor.rgb, objectOpacity * globalOpacity * tcolor.a);
}