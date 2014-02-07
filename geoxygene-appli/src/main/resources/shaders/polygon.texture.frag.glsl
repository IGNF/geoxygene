#version 150 core

uniform sampler2D colorTexture1;
uniform float alpha = 1;
in vec2 vTextureCoord;

out vec4 outColor;

void main(void) {
	vec4 tcolor = texture(colorTexture1, vTextureCoord);
	outColor = vec4( tcolor.rgb, tcolor.a * alpha );
}