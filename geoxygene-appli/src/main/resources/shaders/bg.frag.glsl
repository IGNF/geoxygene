#version 400 core

uniform sampler2D colorTexture1;

in vec2 fragmentUV;

out vec4 outColor;

void main() {
   outColor = texture(colorTexture1, fragmentUV);
}