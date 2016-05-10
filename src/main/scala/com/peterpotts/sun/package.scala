package com.peterpotts

package object sun {
  implicit val defaultAction = Ordering.by { action: Action =>
    action.dateTime.getMillis
  }
}

