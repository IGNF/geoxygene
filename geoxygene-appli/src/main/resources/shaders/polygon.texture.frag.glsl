#version 410

uniform sampler2D colorTexture1;
uniform float globalOpacity = 1.0;
uniform float objectOpacity = 1.0;
uniform vec2 textureScaleFactor = vec2(1.0, 1.0);

layout (location = 0) in vec4 fragmentColor;
layout (location = 1) in vec2 fragmentTextureCoord;

out vec4 outColor;

void main(void) {
	vec4 tcolor = texture(colorTexture1, fragmentTextureCoord * textureScaleFactor );
	if ( tcolor.a < 0.01 ) { outColor = vec4( 1,0,0.0,0.5); return; }
	outColor = vec4( tcolor.rgb, objectOpacity * globalOpacity * tcolor.a);
	
}