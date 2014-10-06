#version 150 core

uniform sampler2D colorTexture1;

layout (location = 0) in vec2 fragmentUV;

layout (location = 0) out vec4 outColor;

void main() {
   outColor = texture(colorTexture1, fragmentUV);
}