#version 150 core

uniform sampler2D colorTexture1;
uniform sampler2D dMapTexture;

in vec2 vTextureCoord;

out vec4 outColor;

void main(void) {
	vec2 uvDistanceMap = texture(dMapTexture, vTextureCoord).xy;
	uvDistanceMap.y = 1 - uvDistanceMap.y;
	outColor = texture(colorTexture1, uvDistanceMap).rgba;
	//outColor = vec4( vTextureCoord.x,vTextureCoord.y,0.5,1);
}