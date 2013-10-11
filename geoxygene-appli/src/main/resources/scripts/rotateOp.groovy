
def rotate( primitive, angle ) {
  RotateOperator rotator = new RotateOperator( angle );
  rotator.addInput( primitive );
  rotator.apply();
}
