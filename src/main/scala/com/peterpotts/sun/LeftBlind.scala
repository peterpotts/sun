package com.peterpotts.sun

import scala.math._

case object LeftBlind extends Blind {
  private val browUp = 95.0
  private val browForward = 80.0
  private val leftWallForward = 123.0
  private val leftWallToRightWindow = 102.0
  private val rightWallForward = 87.0
  private val leftWindowToRightWall = 104.0

  private def c(x: Double) = cos(toRadians(x))

  private def at(x: Double) = toDegrees(atan(x))

  def close(position: Position) =
    position.azimuth > forwardAzimuth - 90.0 + at(leftWallForward / leftWallToRightWindow) &&
      position.azimuth < forwardAzimuth + 90.0 - at(rightWallForward / leftWindowToRightWall) &&
      position.elevation > horizon &&
      position.elevation < at(c(position.azimuth - forwardAzimuth) * browUp / browForward)
}
