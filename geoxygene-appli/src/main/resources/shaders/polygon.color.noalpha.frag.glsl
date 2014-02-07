#version 150 core

uniform float opacity = 1;
in vec4 vColor;

out vec4 outColor;

void main(void) {
	outColor = vec4( vColor.rgb, opacity);
}