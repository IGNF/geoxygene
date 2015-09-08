#version 330

uniform sampler2D colorTexture2;
uniform float globalOpacity = 1;
uniform float objectOpacity = 1;
uniform int antialiasingSize = 1;
uniform int screenWidth;
uniform int screenHeight;

in vec4 fragmentColor;
in vec2 fragmentTextureCoord;

out vec4 outColor;

vec4 colorFilter( vec4 col );

void main(void) {
	vec4 textureColor =  texture(colorTexture2, fragmentTextureCoord );
//    outColor = vec4( fragmentTextureCoord * vec2(0.333) + textureColor.rg * 0.666, textureColor.b, 0.5 );
//    outColor = vec4( fragmentTextureCoord, 0.0, 1.0 );
	outColor = textureColor;
}