package com.peterpotts.sun

import com.peterpotts.common.tool.ConsoleShell

class ApplicationShell(val colors: Boolean) extends ConsoleShell {
  val name = "Sun"

  val help =
    "java <class>" ::
      "" ::
      "Usage:" ::
      Nil

  override def process(input: String): List[String] = Nil
}
