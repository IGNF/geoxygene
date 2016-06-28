#version 330

uniform sampler2D colorTexture1;
uniform float globalOpacity = 1;
uniform float objectOpacity = 1;
uniform int antialiasingSize = 1;
uniform int screenWidth;
uniform int screenHeight;

in vec4 fragmentColor;
in vec2 fragmentTextureCoord;

out vec4 outColor;

vec4 colorFilter( vec4 col );

// actually antialiasing is done by texture filtering at render time (so nothing to do here exect the color filt
void main(void) {
    vec2 texCoord =  fragmentTextureCoord;
    outColor = colorFilter(texture( colorTexture1, texCoord ));
    return; 
}
