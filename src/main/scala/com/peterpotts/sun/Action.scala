package com.peterpotts.sun

import org.joda.time.DateTime

case class Action(dateTime: DateTime, name: String, close: Boolean)
