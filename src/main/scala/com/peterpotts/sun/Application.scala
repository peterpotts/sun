package com.peterpotts.sun

import com.typesafe.scalalogging.LazyLogging
import org.joda.time.{DateTime, DateTimeZone, Hours}

import scala.collection.immutable.Stream.#::

//noinspection ScalaStyle
object Application extends LazyLogging {

  implicit class DecoratedStreamDateTime(dateTimes: Stream[DateTime]) {
    def takeYear: Stream[DateTime] = {
      val end = dateTimes.head.plusYears(1)
      dateTimes.takeWhile(_.isBefore(end))
    }

    def filterFirstOfTheDay: Stream[DateTime] = {
      def loop(dateTimes: Stream[DateTime]): Stream[DateTime] =
        dateTimes match {
          case head #:: body #:: tail =>
            if (head.toLocalDate == body.toLocalDate)
              loop(head #:: tail)
            else
              head #:: loop(body #:: tail)
        }

      loop(dateTimes)
    }

    def filterLastOfTheDay: Stream[DateTime] = {
      def loop(dateTimes: Stream[DateTime]): Stream[DateTime] =
        dateTimes match {
          case head #:: body #:: tail =>
            if (head.toLocalDate == body.toLocalDate)
              loop(body #:: tail)
            else
              head #:: loop(body #:: tail)
        }

      loop(dateTimes)
    }
  }

  implicit class DecoratedStreamAction(actions: Stream[Action]) {
    def trim: Stream[Action] = {
      def loop(actions: Stream[Action]): Stream[Action] =
        actions match {
          case head #:: body #:: tail =>
            if (head.close == body.close)
              loop(head #:: tail)
            else
              head #:: loop(body #:: tail)
          case _ => actions
        }

      loop(actions)
    }

    def merge(that: Stream[Action]): Stream[Action] = {
      def loop(left: Stream[Action], right: Stream[Action]): Stream[Action] =
        left match {
          case leftHead #:: leftTail =>
            right match {
              case rightHead #:: rightTail =>
                if (leftHead.dateTime.isBefore(rightHead.dateTime))
                  leftHead #:: loop(leftTail, right)
                else
                  rightHead #:: loop(left, rightTail)
              case _ => left
            }
          case _ => right
        }

      loop(actions, that)
    }

    def takeYear: Stream[Action] = {
      val end = actions.head.dateTime.plusYears(1)
      actions.takeWhile(_.dateTime.isBefore(end))
    }
  }

  def main(args: Array[String]): Unit = {
    val location = Location(latitude = 37.563, longitude = -122.3255)
    val sun = new Sun(location)
    val gmtOffset = -8
    val timeZone = DateTimeZone.forOffsetHours(gmtOffset)
    val now = new DateTime().toDateTime(timeZone).withSecondOfMinute(0).withMillisOfSecond(0)

//    val localDates




    val dateTimes = Stream.from(0).map(now.plusMinutes)
    val positions = dateTimes.map(sun.position)
    val dateTimePositions = dateTimes.zip(positions)


    val leftActions = dateTimePositions.map {
      case (dateTime, position) => Action(dateTime, "left", LeftBlind.close(position))
    }.trim.tail

    val rightActions = dateTimePositions.map {
      case (dateTime, position) => Action(dateTime, "right", RightBlind.close(position))
    }.trim.tail

    val actions = leftActions.merge(rightActions)

    //actions.take(10).foreach(println)

    val leftCloseDateTimes = leftActions.filter(_.close).map(_.dateTime).filterFirstOfTheDay
    val leftOpenDateTimes = leftActions.filter(!_.close).map(_.dateTime).filterLastOfTheDay
    val rightCloseDateTimes = rightActions.filter(_.close).map(_.dateTime).filterFirstOfTheDay
    val rightOpenDateTimes = rightActions.filter(!_.close).map(_.dateTime).filterLastOfTheDay



    def daily(dateTimes: Stream[DateTime]): Boolean =
      dateTimes match {
        case head #:: body #:: tail =>
          val hours = Hours.hoursBetween(head, body).getHours
          if (hours > 25) {
            println(hours)
            println(head)
            println(body)
          }
          daily(body #:: tail)
        case _ => true
      }



    println("Left close is daily : " + daily(leftCloseDateTimes.takeYear))
    println("Left open is daily : " + daily(leftOpenDateTimes.takeYear))
    //println("Right close is daily : " + daily(rightCloseDateTimes.takeYear))
    //println("Right open is daily : " + daily(rightOpenDateTimes.takeYear))

    //    def floor(dateTime: DateTime) = dateTime.withMinuteOfHour(0).withSecondOfMinute(0)
    //    def ceiling(dateTime: DateTime) = floor(dateTime).plusHours(1)
    //
    //    def strip(actions: Stream[DateTime]): Stream[DateTime] =
    //      actions match {
    //        case head #:: body #:: tail =>
    //          if (head.hourOfDay == body.hourOfDay)
    //            strip(head #:: tail)
    //          else
    //            head #:: strip(body #:: tail)
    //      }
    //
    //
    //    val rightCloseSchedules = strip(rightCloseDateTimes.map(floor)).tail
    //    val rightOpenSchedules = strip(rightOpenDateTimes.map(ceiling)).tail
    //
    //    val time = "HH:mm"
    //    val date = "MMM dd"
    //
    //    val schedules = yearOfActions(merge(
    //      rightCloseSchedules.map(Action(_, "right", close = true)),
    //      rightOpenSchedules.map(Action(_, "right", close = false)))).map {
    //      case Action(dateTime, name, close) =>
    //        val action = if (close) "close" else "open"
    //        s"${dateTime.toString(date)}: Set $name blind to $action at ${dateTime.toString(time)}"
    //    }

    //schedules.foreach(println)

  }
}











