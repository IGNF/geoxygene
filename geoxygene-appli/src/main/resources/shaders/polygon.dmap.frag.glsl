#version 150 core

uniform sampler2D colorTexture1;
uniform sampler2D dMapTexture;
uniform float opacity = 1;

in vec2 fragmentTextureCoord;

out vec4 outColor;

void main(void) {
	vec2 uvDistanceMap = texture(dMapTexture, fragmentTextureCoord).xy;
	uvDistanceMap.y = 1 - uvDistanceMap.y;
	vec4 tColor = texture(colorTexture1, uvDistanceMap).rgba;
	outColor = vec4( tColor.rgb, tColor.a * opacity );
}