#version 330

uniform sampler2D textureColor1;
uniform float globalOpacity = 1.0;
uniform float objectOpacity = 1.0;
uniform vec2 textureScaleFactor = vec2(1.0, 1.0);

in vec4 fragmentColor;
in vec2 fragmentTextureCoord;

out vec4 outColor;

void main(void) {
	vec4 tcolor = texture(textureColor1, fragmentTextureCoord * textureScaleFactor );
	outColor = vec4( tcolor.rgb * fragmentColor.rgb, fragmentColor.a * globalOpacity );
}