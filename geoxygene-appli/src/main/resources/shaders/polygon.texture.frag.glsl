#version 150 core

uniform sampler2D colorTexture1;

in vec2 vTextureCoord;

out vec4 outColor;

void main(void) {
	outColor = texture(colorTexture1, vTextureCoord).rgba;
//	outColor = vec4( abs(vTextureCoord),0,1);
}