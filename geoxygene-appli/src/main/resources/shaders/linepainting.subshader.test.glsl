#version 400

/* Data structure sent to subshaders */
struct DataPainting {
	float screenWidth; 		// screen width in pixels 
	float screenHeight;		// screen height in pixels
	float mapScaleDiv1000;  // map scale divide by 1000 (e.g. 1:100000 maps, this value is 100)
	int brushWidth; 	    // brush texture width in pixels
	int brushHeight;        // brush texture height in pixels
	int brushStartWidth;    // start texture length in pixels for the brush
	int brushEndWidth;      // end texture length in pixels for the brush
	float brushScale;       // size in mm of one brush pixel
	float paperScale;       // scaling factor for paper
	float sharpness;        // brush-paper blending sharpness
	
	float paperDensity;     // paper height scale factor
	float brushDensity;     // brush height scale factor
	float strokePressure;   // stroke pressure
	vec4 position;          // current point position in world coordinates
	vec2 uv;                // UV coordinates texture (u in world coordinates, v between 0 and 1)
	vec4 color;             // point color
	float thickness;        // line thickness in world coordinates
	float uMax;             // maximum u coordinate in one polyline (in wolrd coordinates)
	vec2 tan;               // tangent vector at the given point (in world coordinates)
	
};

uniform int time;

// v is scaled from [0..1] to [0.5-width/2..0.5+width/2]
float vTextureScale( in float width, in float v ) {
	float scaledV = 0.5 + (v - 0.5) / width;
	if ( scaledV < 0.0 ) return 0.0;
	if ( scaledV > 1.0 ) return 1.0;
	return scaledV;
}

/************************************************************************************/
vec2 computeBrushTextureCoordinates( DataPainting fragmentData ) {
	return vec2(fragmentData.uv.x / 1000.0 / fragmentData.brushScale, fragmentData.uv.y );
}

/************************************************************************************/
vec4 computeFragmentColor( in vec4 brushColor, in vec4 paperColor, in DataPainting fragmentData ) {
	  
const int ps = 1; // use values > 1..10 for oldskool
float iGlobalTime = time / 1000.;

float x = fragmentData.position.x * fragmentData.screenWidth ;
float y = fragmentData.position.y * fragmentData.screenHeight;

  

                if (ps > 0)

                {

                   x = float(int(x / float(ps)) * ps);

                   y = float(int(y / float(ps)) * ps);

                }

               

   float mov0 = x+y+sin(iGlobalTime)*10.+sin(x/90.)*70.+iGlobalTime*2.;

   float mov1 = (mov0 / 5. + sin(mov0 / 30.))/ 10. + iGlobalTime * 3.;

   float mov2 = mov1 + sin(mov1)*5. + iGlobalTime*1.0;

   float cl1 = sin(sin(mov1/4. + iGlobalTime)+mov1);

   float c1 = cl1 +mov2/2.-mov1-mov2+iGlobalTime;

   float c2 = sin(c1+sin(mov0/100.+iGlobalTime)+sin(y/57.+iGlobalTime/50.)+sin((x+y)/200.)*2.);

   float c3 = abs(sin(c2+cos((mov1+mov2+c2) / 10.)+cos((mov2) / 10.)+sin(x/80.)));

 

   float dc = float(16-ps);

               

                if (ps > 0)

                {

                               cl1 = float(int(cl1*dc))/dc;

                               c2 = float(int(c2*dc))/dc;

                               c3 = float(int(c3*dc))/dc;

                }

               

  return vec4( cl1,c2,c3,1.0);
}
