#version 330 core

layout (location = 0) in vec3 vertexPosition;
layout (location = 1) in vec2 vertexUV;
layout (location = 2) in vec4 vertexColor;

out vec2 fragmentUV;

void main() {
	gl_Position = vec4( vertexPosition, 1);
	fragmentUV.x = vertexUV.x;
	fragmentUV.y = vertexUV.y;
}