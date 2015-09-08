#version 330

uniform sampler2D foregroundTexture;
uniform sampler2D backgroundTexture;
uniform float globalOpacity = 1;
uniform float objectOpacity = 1;
uniform int antialiasingSize = 1;
uniform int screenWidth;
uniform int screenHeight;

out vec4 outColor;
in vec4 fragmentColor;
in vec2 fragmentTextureCoord;

vec4 colorFilter( vec4 col );

// source: http://cairographics.org/operators/
vec3 overlay( vec3 A, vec3 B ) {
	vec3 resultColor;	
	resultColor.r = ( B.r <= 0.5 ) ? 2 * A.r * B.r : 1 - 2 * ( 1 - A.r) * ( 1 - B.r ); 
	resultColor.g = ( B.g <= 0.5 ) ? 2 * A.g * B.g : 1 - 2 * ( 1 - A.g) * ( 1 - B.g ); 
	resultColor.b = ( B.b <= 0.5 ) ? 2 * A.b * B.b : 1 - 2 * ( 1 - A.b) * ( 1 - B.b );

	return resultColor; 
}

void main(void) {
 //   outColor = vec4( fragmentTextureCoord.xy, 1.0, 1.0 );
 //   return;

	vec3 resultColor = vec3(0);
	vec4 foregroundColor = colorFilter( texture( foregroundTexture, fragmentTextureCoord.xy ) );
	vec4 backgroundColor = texture( backgroundTexture, fragmentTextureCoord.xy );
	
	float aA = foregroundColor.a * globalOpacity;
	float aB = backgroundColor.a;
	vec3 xA = foregroundColor.rgb;
	vec3 xB = backgroundColor.rgb;
	vec3 xaA = aA * xA;
	vec3 xaB = aB * xB;
	
	float aR = aA + aB * ( 1.0 - aA );

	vec3 f = overlay( xA, xB );
	resultColor = ( 1.0 - aB ) * xaA + ( 1- aA ) * xaB + aA * aB * f; 	
	if ( aR > 0.001 ) resultColor /= aR;

	outColor = vec4( resultColor, aR );	
}