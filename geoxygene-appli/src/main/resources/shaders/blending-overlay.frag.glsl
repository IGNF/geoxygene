#version 400

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

const vec3 identityElement = vec3( 1.0, 1.0, 1.0 );
const float EPSILON = 0.001;

vec4 colorFilter( vec4 col );

void main(void) {
 //   outColor = vec4( fragmentTextureCoord.xy, 1.0, 1.0 );
 //   return;

	vec3 resultColor = vec3(0);
	vec4 foregroundColor = colorFilter( texture( foregroundTexture, fragmentTextureCoord.xy ) );
	vec4 backgroundColor = texture( backgroundTexture, fragmentTextureCoord.xy );
	foregroundColor.rgb = foregroundColor.rgb * foregroundColor.a + identityElement * ( 1.0 - foregroundColor.a );
	backgroundColor.rgb = backgroundColor.rgb * backgroundColor.a + identityElement * ( 1.0 - backgroundColor.a );
	float sumAlpha = foregroundColor.a + backgroundColor.a;
	resultColor.r = ( foregroundColor.r > 0.5 ) ? 2 * foregroundColor.r * backgroundColor.r : 1 - 2 * ( 1 - foregroundColor.r) * ( 1 - backgroundColor.r ); 
	resultColor.g = ( foregroundColor.g > 0.5 ) ? 2 * foregroundColor.g * backgroundColor.g : 1 - 2 * ( 1 - foregroundColor.g) * ( 1 - backgroundColor.g ); 
	resultColor.b = ( foregroundColor.b > 0.5 ) ? 2 * foregroundColor.b * backgroundColor.b : 1 - 2 * ( 1 - foregroundColor.b) * ( 1 - backgroundColor.b );
	if ( sumAlpha > EPSILON ) resultColor /= sumAlpha;  
 
	outColor = vec4( resultColor, max( foregroundColor.a, foregroundColor.a ) );
}