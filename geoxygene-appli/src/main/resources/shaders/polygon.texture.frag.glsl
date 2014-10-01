#version 150 core

uniform sampler2D colorTexture1;
uniform float globalOpacity = 1.0;
uniform float objectOpacity = 1.0;
uniform vec2 textureScaleFactor = vec2(1., 1.);

in vec4 fragmentColor;
in vec2 fragmentTextureCoord;

out vec4 outColor;

void main(void) {
	vec4 tcolor = texture(colorTexture1, fragmentTextureCoord * textureScaleFactor );
	if ( tcolor.a < 0.01 ) { outColor = vec4( 1,0,0,0.5); return; }
	outColor = vec4( tcolor.rgb, objectOpacity * globalOpacity * tcolor.a);
}