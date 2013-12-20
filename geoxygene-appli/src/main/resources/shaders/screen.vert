#version 150 core

uniform float m00 = 1.; // X homothetic value in 3x3 matrix
uniform float m02 = 0.; // X translation value in 3x3 matrix
uniform float m11 = 1.; // Y homothetic value in 3x3 matrix
uniform float m12 = 0.; // Y translation value in 3x3 matrix
uniform float screenWidth;
uniform float screenHeight;

in vec3 inPosition;
in vec2 inTextCoord;
in vec4 inColor;

out vec4 passColor;
//out vec2 passTextureCoord;

void main(void) {
	gl_Position = vec4( 2 * (inPosition.x * m00 + m02) / screenWidth - 1, 1 - 2 * ( inPosition.y * m11 + m12 ) / screenHeight, 0, 1);


//	gl_Position = inPosition;
	
	passColor = inColor;
//	passTextureCoord = inTextureCoord;
}