#version 330

// this shader draw the text layer (which is a texture)

uniform sampler2D colorTexture2;
uniform float globalOpacity = 1;
uniform float objectOpacity = 1;

in vec2 fragmentTextureCoord;

out vec4 outColor;

vec4 colorFilter( vec4 col );

void main(void) {
	vec4 textureColor =  texture(colorTexture2, fragmentTextureCoord);
	outColor = textureColor;
}
