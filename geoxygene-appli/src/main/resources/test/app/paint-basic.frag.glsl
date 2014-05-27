#version 150 core

in vec2 fragmentUV;
in float fragmentCurvature;
in float fragmentThickness;
in vec4 fragmentColor;
uniform float screenWidth;
uniform float screenHeight;
uniform sampler2D paperSampler;
uniform sampler2D brushSampler;

out vec4 outColor;

void main() {
//outColor = vec4( 1.0,0.0,0.0,1.0);
	outColor = vec4( fragmentColor.rgb, 1) ;
}