package com.peterpotts.sun

case object LeftLeftBlind extends Blind(
  name = "Left-Left",
  browUp = 95.0 - 23.0 - 29.5,
  browForward = 80.0,
  leftWallForward = 123.0,
  leftWallToRightWindow = 102.0 - 80.0,
  rightWallForward = 87.0,
  leftWindowToRightWall = 104.0 + 26.0)
