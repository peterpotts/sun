package com.peterpotts.sun

trait Blind {
  protected val horizon = 5.0
  protected val forwardAzimuth = 138.7

  def close(position: Position): Boolean
}
