package com.peterpotts.sun

import com.typesafe.scalalogging.LazyLogging
import org.joda.time.{DateTime, DateTimeConstants}

import scala.math._

//noinspection ScalaStyle
object Application extends LazyLogging {
  def main(args: Array[String]): Unit = {
    val location = Location(latitude = 37.563, longitude = -122.3255)
    val sun = new Sun(location)
    val dateTime = new DateTime(2016, 5, 5, 14, 0, 0)
    val position = sun.position(dateTime)
    println(position)
  }
}

case class Location(latitude: Double, longitude: Double)

case class Position(elevation: Double, azimuth: Double)

//noinspection ScalaStyle
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
    val b = (dayNumber - 81) * 360.toDouble / 365
    val equationOfTime = 9.87 * s(2 * b) - 7.53 * c(b) - 1.5 * s(b)
    val timeCorrectionFactor = 4 * (location.longitude - localStandardTimeMeridian) + equationOfTime
    val localSolarTime = localTime + timeCorrectionFactor / 60
    val hourAngle = 15 * (localSolarTime - 12)
    val declination = 23.45 * s(b)
    val sinDeclination = s(declination)
    val cosDeclination = c(declination)
    val cosHourAngle = c(hourAngle)
    val elevation = as(sinDeclination * sinLatitude + cosDeclination * cosLatitude * cosHourAngle)
    val relativeAzimuth = ac((sinDeclination * cosLatitude - cosDeclination * sinLatitude * cosHourAngle) / c(elevation))
    val azimuth = if (hourAngle < 0) relativeAzimuth else 360 - relativeAzimuth
    Position(elevation = elevation, azimuth = azimuth)
  }
}