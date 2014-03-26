def cut( primitive, strokeSize, strokeGap ) {
  CutterOperator cutter = new CutterOperator(strokeSize, strokeGap );
  cutter.addInput(primitive);
  cutter.apply();
}

def cut( primitive, strokeSize ) {
  CutterOperator cutter = new CutterOperator(strokeSize, 0 );
  cutter.addInput(primitive);
  cutter.apply();
}

