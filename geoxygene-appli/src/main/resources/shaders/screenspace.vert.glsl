#version 150 core

in vec3 inPosition;
in vec2 inTextureCoord;
in vec4 inColor;

out vec4 vColor;
out vec2 vTextureCoord;

void main(void) {
	gl_Position = vec4(inPosition,1);
	vColor = inColor;
	vTextureCoord = inTextureCoord;
}