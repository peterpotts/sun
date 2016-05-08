package com.peterpotts.sun

import com.typesafe.scalalogging.LazyLogging
import org.joda.time.{DateTime, DateTimeConstants}

import scala.math._

//noinspection ScalaStyle
object Application extends LazyLogging {
  def main(args: Array[String]): Unit = {
    val location = Location(latitude = 37.563, longitude = -122.3255)
    val sun = new Sun(location)
    //val dateTime = new DateTime(2016, 5, 5, 14, 0, 0)
    val dateTime = new DateTime()
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
    val elevationFactor = sinDeclination * sinLatitude + cosDeclination * cosLatitude * cosHourAngle
    val azimuthFactor = sinDeclination * cosLatitude - cosDeclination * sinLatitude * cosHourAngle
    val elevation = as(elevationFactor)
    val relativeAzimuth = ac(azimuthFactor / c(elevation))
    val azimuth = if (hourAngle < 0) relativeAzimuth else 360 - relativeAzimuth
    Position(elevation = elevation, azimuth = azimuth)
  }
}

trait Blind {
  def close(position: Position): Boolean
}

case object LeftBlind extends Blind {
  def close(position: Position) =
    position.elevation > 0.0 &&
      position.elevation < toDegrees(atan(95.0 / 80.0)) &&
      position.azimuth > 140.0 - 90.0 + toDegrees(atan(123.0 / 102.0)) &&
      position.azimuth < 140.0 + 90.0 - toDegrees(atan(87.0 / 104.0))
}

case object RightBlind extends Blind {
  def close(position: Position) =
    position.elevation > 0.0 &&
      position.azimuth > 140.0 - 90.0 &&
      position.azimuth < 140.0 + 90.0
}
