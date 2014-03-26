def inflate( primitive, viewport ) {
  width = new SinFunction( 10, 10, 0, 0 );
  shift = new SinFunction( 2, 10, 0, 0 );
  inflator = new InflateOperator( width, shift , 10 );
  inflator.setViewport( viewport )
  inflator.addInput( primitive );
  inflator.apply();
}

def inflate( primitive, scaleFactor, viewport ) {
  width = new ConstantFunction( scaleFactor );
  shift = new ConstantFunction( 0 );
  inflator = new InflateOperator( width, shift , 10 );
  inflator.setViewport( viewport )
  inflator.addInput( primitive );
  inflator.apply();
}

def inflate( primitive, width, shift, samplingRate, viewport ) {
  inflator = new InflateOperator( width, shift , samplingRate );
  inflator.setViewport( viewport )
  inflator.addInput( primitive );
  inflator.apply();
}

def homothety( primitive, factor ) {
  homo = new ExpandOperator( factor );
  homo.addInput( primitive );
  homo.apply();
}

def homothety( primitive, factor, center ) {
  homo = new ExpandOperator( factor, center );
  homo.addInput( primitive );
  homo.apply();
}

