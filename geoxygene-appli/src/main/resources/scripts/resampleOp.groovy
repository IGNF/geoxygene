def resample( primitive, sampleSize, keepInitialPoints ) {
  ResampleOperator resampler = new ResampleOperator(sampleSize,keepInitialPoints );
  resampler.addInput(primitive);
  resampler.apply();
}

def resample( primitive, sampleSize ) {
  ResampleOperator resampler = new ResampleOperator(sampleSize, true );
  resampler.addInput(primitive);
  resampler.apply();
}

def split( primitive ) {
  SegmentizeOperator splitter = new SegmentizeOperator( );
  splitter.addInput(primitive);
  splitter.apply();
}

def reparameterizeUnit( primitive ) {
  ReparameterizeOperator op = new ReparameterizeOperator( ReparameterizeOperator.ParameterizationType.UNIT );
  op.addInput(primitive);
  op.apply();
}

def reparameterizeLength( primitive ) {
  ReparameterizeOperator op = new ReparameterizeOperator( ReparameterizeOperator.ParameterizationType.LENGTH );
  op.addInput(primitive);
  op.apply();
}


