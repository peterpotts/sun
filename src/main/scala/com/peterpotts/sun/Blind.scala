package com.peterpotts.sun

trait Blind {
  protected val horizon = 5.0
  protected val forwardAzimuth = 140.0

  def close(position: Position): Boolean
}
