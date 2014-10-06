#version 410

layout (location = 0) in vec4 fragmentColor;
layout (location = 1) in vec2 fragmentUV;

out vec4 outColor;

void main() {
	outColor = vec4(1.0,0.0,0.0,1.0);;
}