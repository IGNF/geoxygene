#version 150 core

uniform float m00 = 1.; // X homothetic value in 3x3 matrix
uniform float m02 = 0.; // X translation value in 3x3 matrix
uniform float m11 = 1.; // Y homothetic value in 3x3 matrix
uniform float m12 = 0.; // Y translation value in 3x3 matrix
uniform float screenWidth;
uniform float screenHeight;

in vec3 inPosition;
in vec2 inTextureCoord;
in vec4 inColor;

out vec4 vColor;
out vec2 vTextureCoord;

void main(void) {
	gl_Position = vec4( 2 * (inPosition.x * m00 + m02) / (screenWidth-1) -1, 1 - 2 * ( inPosition.y * m11 + m12 ) / (screenHeight-1), 0, 1);

	vColor = inColor;
	vTextureCoord = inTextureCoord;
}