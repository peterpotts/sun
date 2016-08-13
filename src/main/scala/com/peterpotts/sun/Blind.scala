package com.peterpotts.sun

import scala.math._

class Blind(
  name: String,
  browUp: Double,
  browForward: Double,
  leftWallForward: Double,
  leftWallToRightWindow: Double,
  rightWallForward: Double,
  leftWindowToRightWall: Double) {
  private val horizon = at(21.0 / 352.0)
  private val forwardAzimuth = 138.7

  private def c(x: Double) = cos(toRadians(x))

  private def at(x: Double) = toDegrees(atan(x))

  def close(position: Position) =
    position.azimuth > forwardAzimuth - 90.0 + at(leftWallForward / leftWallToRightWindow) &&
      position.azimuth < forwardAzimuth + 90.0 - at(rightWallForward / leftWindowToRightWall) &&
      position.elevation > horizon &&
      position.elevation < at(c(position.azimuth - forwardAzimuth) * browUp / browForward)
}
