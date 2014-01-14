#version 150 core

uniform sampler2D textureSampler;

in vec4 vColor;
in vec2 vTextureCoord;

out vec4 outColor;

void main(void) {
	//outColor = vColor;
	//outColor = new vec4( vTextureCoord.x, vTextureCoord.y, 1 , 1);
	// Override outColor with our texture pixel
	outColor = texture(textureSampler, vTextureCoord).rgba;
}