#version 150 core

uniform sampler2D colorTexture1;
uniform float globalOpacity = 1;
uniform float objectOpacity = 1;

in vec4 fragmentColor;
in vec2 fragmentTextureCoord;

out vec4 outColor;

void main(void) {
	vec4 tcolor = texture(colorTexture1, fragmentTextureCoord);
	outColor = vec4( tcolor.rgb, objectOpacity * globalOpacity * tcolor.a);
}