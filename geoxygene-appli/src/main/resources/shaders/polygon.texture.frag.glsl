#version 330

uniform sampler2D textureColor1;

uniform float globalOpacity = 1.0; // from the layer 
uniform float objectOpacity = 1.0; // from the feature
uniform vec2 textureScaleFactor = vec2(1.0, 1.0);

in vec4 fragmentColor;
in vec2 fragmentTextureCoord;

out vec4 outColor;

void main(void) {
	vec4 tcolor = texture(textureColor1, fragmentTextureCoord * textureScaleFactor );
	/* tcolor.rgb/tcolor.a seems to be needed in case of a  KEEP_OUTSIDE TileDistributionTexture.*/
	outColor = vec4( tcolor.rgb/tcolor.a,tcolor.a*fragmentColor.a * globalOpacity );
}
