def inspectPrimitive( primitive ) {
    inspectPrimitive( primitive, 0)
}

def inspectPrimitive( primitive, level  ) {
    println "Primitive : " + primitive.getClass().getSimpleName() + " [" + level + "]"
    println "  " + primitive.toString()
    if (!primitive.isLeaf()) {
      for ( DrawingPrimitive child : primitive.getPrimitives()) {
        inspectPrimitive( child, level + 1 )
      }
    }
}
