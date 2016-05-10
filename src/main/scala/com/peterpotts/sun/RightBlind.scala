package com.peterpotts.sun

case object RightBlind extends Blind {
  def close(position: Position) =
    position.elevation > horizon &&
      position.azimuth > forwardAzimuth - 90.0 &&
      position.azimuth < forwardAzimuth + 90.0
}