#version 150 core

in vec3 vertexPosition;
in vec2 vertexTextureCoord;
in vec4 vertexColor;

out vec4 fragmentColor;
out vec2 fragmentTextureCoord;

void main(void) {
	gl_Position = vec4(vertexPosition,1);
	fragmentColor = vertexColor;
	fragmentTextureCoord = vertexTextureCoord;
}