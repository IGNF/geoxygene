#include <stdio.h>
#include <malloc.h>
#define REAL double
#include "triangle.h"
#include "fr_ign_cogit_geoxygene_contrib_delaunay_Triangulation.h"

JNIEXPORT void JNICALL Java_fr_ign_cogit_geoxygene_contrib_delaunay_Triangulation_trianguleC
  (JNIEnv * env, jobject obj, jstring joptions, jobject jin, jobject jout, jobject jvorout) {

const char * options;
struct triangulateio in, out, vorout;
jfieldID fid;
jmethodID mid;
jclass cls;
jboolean jbool;

cls = (*env)->GetObjectClass(env, jin);

options = (*env)->GetStringUTFChars(env, joptions, &jbool);

fid = (*env)->GetFieldID(env, cls, "numberofpoints", "I");
in.numberofpoints = (*env)->GetIntField(env, jin, fid);
in.pointlist = (REAL *) malloc(in.numberofpoints * 2 * sizeof(REAL));
fid = (*env)->GetFieldID(env, cls, "pointlist", "[D");
(*env)->GetDoubleArrayRegion(env, (*env)->GetObjectField(env, jin, fid), 
	0, 2*in.numberofpoints, in.pointlist);
in.pointmarkerlist = (int *) NULL;
in.numberofpointattributes = 0;

fid = (*env)->GetFieldID(env, cls, "numberofsegments", "I");
in.numberofsegments = (*env)->GetIntField(env, jin, fid);
if(in.numberofsegments !=0) {
  in.segmentlist = (int *) malloc(in.numberofsegments * 2 * sizeof(int));
  fid = (*env)->GetFieldID(env, cls, "segmentlist", "[I");
  (*env)->GetIntArrayRegion(env, (*env)->GetObjectField(env, jin, fid), 
    	0, 2*in.numberofsegments, (int*) in.segmentlist);
  in.segmentmarkerlist = (int *) NULL;
}
in.numberofregions = 0;
in.numberofholes = 0;

out.pointlist = (REAL *) NULL;
out.pointmarkerlist = (int *) NULL;
out.trianglelist = (int *) NULL;
out.segmentlist = (int *) NULL;
out.segmentmarkerlist = (int *) NULL;
out.edgelist = (int *) NULL;
out.edgemarkerlist = (int *) NULL;

in.holelist = (REAL *) NULL;
in.regionlist = (REAL *) NULL;

vorout.pointlist = (REAL *) NULL;
vorout.pointmarkerlist = (int *) NULL;
vorout.trianglelist = (int *) NULL;
vorout.segmentlist = (int *) NULL;
vorout.segmentmarkerlist = (int *) NULL;
vorout.edgelist = (int *) NULL;
vorout.edgemarkerlist = (int *) NULL;
vorout.normlist = (REAL *) NULL;

/********* Appel du code proprement dit *******/
triangulate(options, &in, &out, &vorout); 
/**********************************************/

cls = (*env)->GetObjectClass(env, jout);

fid = (*env)->GetFieldID(env, cls, "numberofpoints", "I");
(*env)->SetIntField(env, jout, fid, out.numberofpoints);

fid = (*env)->GetFieldID(env, cls, "numberofsegments", "I");
(*env)->SetIntField(env, jout, fid, out.numberofsegments);

fid = (*env)->GetFieldID(env, cls, "numberofedges", "I");
(*env)->SetIntField(env, jout, fid, out.numberofedges);

fid = (*env)->GetFieldID(env, cls, "numberoftriangles", "I");
(*env)->SetIntField(env, jout, fid, out.numberoftriangles);

fid = (*env)->GetFieldID(env, cls, "numberofcorners", "I");
(*env)->SetIntField(env, jout, fid, out.numberofcorners);

mid = (*env)->GetMethodID(env, cls, "joutInit", "()V");
(*env)->CallVoidMethod(env, jout, mid);

fid = (*env)->GetFieldID(env, cls, "pointlist", "[D");
(*env)->SetDoubleArrayRegion(env, (*env)->GetObjectField(env, jout, fid), 
	0, 2*out.numberofpoints, (jdouble*) out.pointlist);

fid = (*env)->GetFieldID(env, cls, "segmentlist", "[I");
(*env)->SetIntArrayRegion(env, (*env)->GetObjectField(env, jout, fid), 
	0, 2*out.numberofsegments, (jint*) out.segmentlist);

fid = (*env)->GetFieldID(env, cls, "edgelist", "[I");
(*env)->SetIntArrayRegion(env, (*env)->GetObjectField(env, jout, fid), 
	0, 2*out.numberofedges, (jint*) out.edgelist);

fid = (*env)->GetFieldID(env, cls, "trianglelist", "[I");
(*env)->SetIntArrayRegion(env, (*env)->GetObjectField(env, jout, fid), 
	0, 3*out.numberoftriangles, (jint*) out.trianglelist);

/*****************/

if (jvorout != NULL) {

cls = (*env)->GetObjectClass(env, jvorout);

fid = (*env)->GetFieldID(env, cls, "numberofpoints", "I");
(*env)->SetIntField(env, jvorout, fid, vorout.numberofpoints);

fid = (*env)->GetFieldID(env, cls, "numberofedges", "I");
(*env)->SetIntField(env, jvorout, fid, vorout.numberofedges);

mid = (*env)->GetMethodID(env, cls, "jvoroutInit", "()V");
(*env)->CallVoidMethod(env, jvorout, mid);

fid = (*env)->GetFieldID(env, cls, "pointlist", "[D");
(*env)->SetDoubleArrayRegion(env, (*env)->GetObjectField(env, jvorout, fid), 
	0, 2*vorout.numberofpoints, (jdouble*) vorout.pointlist);

fid = (*env)->GetFieldID(env, cls, "edgelist", "[I");
(*env)->SetIntArrayRegion(env, (*env)->GetObjectField(env, jvorout, fid), 
	0, 2*vorout.numberofedges, (jint*) vorout.edgelist);

fid = (*env)->GetFieldID(env, cls, "normlist", "[D");
(*env)->SetDoubleArrayRegion(env, (*env)->GetObjectField(env, jvorout, fid), 
	0, 2*vorout.numberofedges, (jdouble*) vorout.normlist);
}
}
