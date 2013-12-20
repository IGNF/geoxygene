#version 150 core

uniform sampler2D textureDiffuse;

in vec4 passColor;
in vec2 passTextureCoord;

out vec4 outColor;

void main(void) {
	outColor = passColor;
	// Override outColor with our texture pixel
	//outColor = texture(textureDiffuse, passTextureCoord);
}