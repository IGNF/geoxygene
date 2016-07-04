#version 330

uniform float globalOpacity = 1.0; // from the layer 
uniform float objectOpacity = 1.0; // from the feature
in vec4 fragmentColor;
in vec2 fragmentTextureCoord;

out vec4 outColor;

void main(void) {
	outColor = vec4(fragmentColor.rgb, fragmentColor.a * globalOpacity * objectOpacity );
}
