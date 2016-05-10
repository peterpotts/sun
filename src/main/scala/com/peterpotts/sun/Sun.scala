package com.peterpotts.sun

import org.joda.time.{DateTime, DateTimeConstants}

import scala.math._

case class Sun(location: Location) {
  private val sinLatitude = s(location.latitude)
  private val cosLatitude = c(location.latitude)

  private def s(x: Double) = sin(toRadians(x))

  private def c(x: Double) = cos(toRadians(x))

  private def as(x: Double) = toDegrees(asin(x))

  private def ac(x: Double) = toDegrees(acos(x))

  def position(dateTime: DateTime) = {
    val localTime = dateTime.secondOfDay().get().toDouble / DateTimeConstants.SECONDS_PER_HOUR
    val dayNumber = dateTime.dayOfYear().get() - 1
    val gmtOffset = dateTime.getZone.getOffset(dateTime) / DateTimeConstants.MILLIS_PER_HOUR
    val localStandardTimeMeridian = 15 * gmtOffset
    val b = (dayNumber - 81.0) * 360.0 / 365.0
    val equationOfTime = 9.87 * s(2.0 * b) - 7.53 * c(b) - 1.5 * s(b)
    val timeCorrectionFactor = 4 * (location.longitude - localStandardTimeMeridian) + equationOfTime
    val localSolarTime = localTime + timeCorrectionFactor / 60.0
    val hourAngle = 15.0 * (localSolarTime - 12.0)
    val declination = 23.45 * s(b)
    val sinDeclination = s(declination)
    val cosDeclination = c(declination)
    val cosHourAngle = c(hourAngle)
    val elevationFactor = sinDeclination * sinLatitude + cosDeclination * cosLatitude * cosHourAngle
    val azimuthFactor = sinDeclination * cosLatitude - cosDeclination * sinLatitude * cosHourAngle
    val elevation = as(elevationFactor)
    val relativeAzimuth = ac(azimuthFactor / c(elevation))
    val azimuth = if (hourAngle < 0.0) relativeAzimuth else 360.0 - relativeAzimuth
    Position(elevation = elevation, azimuth = azimuth)
  }
}
