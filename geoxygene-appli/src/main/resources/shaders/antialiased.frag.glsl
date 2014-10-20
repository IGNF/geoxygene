#version 400

uniform sampler2D colorTexture1;
uniform float globalOpacity = 1;
uniform float objectOpacity = 1;
uniform int antialiasingSize = 1;
uniform int screenWidth;
uniform int screenHeight;

in vec4 fragmentColor;
in vec2 fragmentTextureCoord;

out vec4 outColor;

void main(void) {
//    outColor = vec4( fragmentTextureCoord.xy, 1.0, 1.0 );
//    return;

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
  // outColor = vec4(  vec3(interpolatedAlpha),1.0 );
    outColor = vec4( interpolatedColor.rgb,   interpolatedAlpha  );
//    outColor = vec4(  interpolatedColor.rgb, globalOpacity * objectOpacity * interpolatedAlpha);
//    outColor = vec4( texelFetch(colorTexture1, textureNWPixel, 0).rgb, globalOpacity*interpolatedAlpha );
}