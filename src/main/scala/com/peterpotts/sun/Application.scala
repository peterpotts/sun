package com.peterpotts.sun

import com.typesafe.scalalogging.LazyLogging
import org.joda.time.DateTimeConstants._
import org.joda.time._
import spray.http.Uri

import scala.collection.immutable.IndexedSeq

//noinspection ScalaStyle
object Application extends LazyLogging {

  val localDateFormat = "MMM dd"

  def time(minuteOfDay: Int) = {
    val hourOfDay = minuteOfDay / MINUTES_PER_HOUR
    val minuteOfHour = minuteOfDay % MINUTES_PER_HOUR
    f"$hourOfDay%02d:$minuteOfHour%02d"
  }

  def message(name: String, optionalMinuteOfDay: Option[Int]): String =
    optionalMinuteOfDay.map(minuteOfDay => message(s"set $name at", minuteOfDay)).getOrElse(s"unset $name")

  def message(name: String, minuteOfDay: Int): String = s"$name ${time(minuteOfDay)}"

  val granularity = 60

  def floor(minute: Int) = (minute / granularity) * granularity

  def ceiling(minute: Int) = ((minute + granularity - 1) / granularity) * granularity

  def leftRightCloseOpen(
    sun: Sun,
    dateTimeZone: DateTimeZone,
    localDate: LocalDate): (Option[Int], Option[Int], Option[Int], Option[Int]) = {
    val dateTimeAtStartOfDay = localDate.toDateTimeAtStartOfDay(dateTimeZone)
    val minutes = 0 until MINUTES_PER_DAY
    val dateTimes = minutes.map(dateTimeAtStartOfDay.plusMinutes)
    val positions = dateTimes.map(sun.position)
    val minutePositions = minutes.zip(positions)

    val leftMinuteCloses = minutePositions.map {
      case (minute, position) => minute -> LeftBlind.close(position)
    }.trim.tail

    val rightMinuteCloses = minutePositions.map {
      case (minute, position) => minute -> RightBlind.close(position)
    }.trim.tail

    val leftCloseMinute = leftMinuteCloses.filter(_._2).map(_._1).headOption
    val leftOpenMinute = leftMinuteCloses.filter(!_._2).map(_._1).headOption
    val rightCloseMinute = rightMinuteCloses.filter(_._2).map(_._1).headOption
    val rightOpenMinute = rightMinuteCloses.filter(!_._2).map(_._1).headOption

    (leftCloseMinute, leftOpenMinute, rightCloseMinute, rightOpenMinute)
  }

  def main(args: Array[String]): Unit = {
    val today = LocalDate.now()
    println("-" * 40)
    println(s"Date: ${today.toString}")
    val gmtOffset = -8
    println(s"Time zone: $gmtOffset")
    val location = Location(latitude = 37.563, longitude = -122.3255)
    val sun = new Sun(location)
    val dateTimeZone = DateTimeZone.forOffsetHours(gmtOffset)
    val localDates = (0 to 366).map(today.plusDays)
    val position = sun.position(new DateTime())
    println("Elevation: " + position.elevation)
    println("Azimuth: " + position.azimuth)

    leftRightCloseOpen(sun, dateTimeZone, today) match {
      case (leftClose, leftOpen, rightClose, rightOpen) =>
        leftClose.foreach(minuteOfDay => println(message("Left: Sun starts at", minuteOfDay)))
        leftOpen.foreach(minuteOfDay => println(message("Left: Sun stops at", minuteOfDay)))
        rightClose.foreach(minuteOfDay => println(message("Right: Sun starts at", minuteOfDay)))
        rightOpen.foreach(minuteOfDay => println(message("Right: Sun stops at", minuteOfDay)))

        println(message("left to close", leftClose.map(floor)))
        println(message("left to open", leftOpen.map(ceiling)))
        println(message("right to close", rightClose.map(floor)))
        println(message("right to open", rightOpen.map(ceiling)))
    }

    val leftRightOpenCloses = localDates.map { localDate =>
      leftRightCloseOpen(sun, dateTimeZone, localDate) match {
        case (leftClose, leftOpen, rightClose, rightOpen) =>
          IndexedSeq(
            "left to close" -> leftClose.map(floor),
            "left to open" -> leftOpen.map(ceiling),
            "right to close" -> rightClose.map(floor),
            "right to open" -> rightOpen.map(ceiling))
      }
    }

    println("-" * 40)
    val IndexedSeq(leftCloses, leftOpens, rightCloses, rightOpens) = leftRightOpenCloses.transpose

    val localDateLeftCloses = localDates.zip(leftCloses).trim.tail
    val localDateLeftOpens = localDates.zip(leftOpens).trim.tail
    val localDateRightCloses = localDates.zip(rightCloses).trim.tail
    val localDateRightOpens = localDates.zip(rightOpens).trim.tail

    val localDateActions = IndexedSeq(localDateLeftCloses, localDateLeftOpens, localDateRightCloses, localDateRightOpens)

    implicit val localDateOrdering = new Ordering[LocalDate] {
      def compare(left: LocalDate, right: LocalDate) = left.compareTo(right)
    }

    val localDateNameMinutes = localDateActions.reduce(_ merge _)

    val localDateMessages = localDateNameMinutes.map {
      case (localDate, (name, optionalMinuteOfDay)) =>
        localDate -> message(name, optionalMinuteOfDay)
    }

    val localDateGroupedMessages = localDateMessages.groupBy(_._1).mapValues(_.map(_._2))

    var links = IndexedSeq.empty[Uri]

    localDateGroupedMessages.toIndexedSeq.sortBy(_._1).foreach {
      case (localDate, groupedMessage) =>
        val date = localDate.toString(localDateFormat)
        val message = groupedMessage.reverse.mkString(" and ")
        println(s"On $date, $message")
        val format = "yyyyMMdd'T'HHmmss'Z'"
        val dateTimeZone = DateTimeZone.getDefault
        val startOfDay = localDate.toDateTimeAtStartOfDay.toDateTime(DateTimeZone.UTC)
        val start = startOfDay.plusHours(-3).toString(format)
        val end = startOfDay.plusHours(-2).toString(format)

        val uri = Uri(
          scheme = "https",
          authority = Uri.Authority(host = Uri.Host("www.google.com")),
          path = Uri.Path./("calendar") / "render",
          query = Uri.Query(
            "action" -> "TEMPLATE",
            "text" -> s"Somfy: $message",
            "dates" -> s"$start/$end",
            "details" -> "",
            "location" -> "",
            "sf" -> "true",
            "output" -> "xml"))

        links +:= uri
    }

    println("-" * 40)
    links.foreach(println)
    println("-" * 40)
  }
}











