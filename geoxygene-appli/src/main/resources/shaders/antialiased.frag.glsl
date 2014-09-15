#version 150 core

uniform sampler2D colorTexture1;
uniform float globalOpacity = 1;
uniform float objectOpacity = 1;
uniform int antialiasingSize = 1;
in vec4 fragmentColor;

out vec4 outColor;

void main(void) {
	ivec2 screenPixel = ivec2( gl_FragCoord.xy - vec2( .5 ));
	ivec2 textureNWPixel = screenPixel * antialiasingSize; // North-West pixel
	float sumAlpha = 0;
	vec3 sumRGB = vec3( 0. );

    for ( int j = 0; j < antialiasingSize; j++ ) { 
      for ( int i = 0; i < antialiasingSize; i++ ) {
        vec4 pixel = texelFetch(colorTexture1, textureNWPixel + ivec2( i, j ) , 0);
		sumAlpha += pixel.a;
		sumRGB += pixel.rgb * pixel.a;
      } 
    }
    vec3 interpolatedColor = sumRGB / sumAlpha;
    float interpolatedAlpha = sumAlpha / (antialiasingSize * antialiasingSize);
    //outColor = vec4( 1,0,0,1);
    outColor = vec4( interpolatedColor.rgb, globalOpacity * objectOpacity * interpolatedAlpha );
}