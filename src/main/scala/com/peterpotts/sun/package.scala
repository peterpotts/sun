package com.peterpotts

import scala.collection.immutable.IndexedSeq

package object sun {

  implicit class DecoratedIndexedSeqPair[K, V](pairs: IndexedSeq[(K, V)]) {
    def trim: IndexedSeq[(K, V)] = {
      def loop(pairs: IndexedSeq[(K, V)]): IndexedSeq[(K, V)] =
        pairs match {
          case head +: body +: tail =>
            if (head._2 == body._2)
              loop(head +: tail)
            else
              head +: loop(body +: tail)
          case _ => pairs
        }

      loop(pairs)
    }

    def merge(that: IndexedSeq[(K, V)])(implicit ordering: Ordering[K]): IndexedSeq[(K, V)] = {
      def loop(left: IndexedSeq[(K, V)], right: IndexedSeq[(K, V)]): IndexedSeq[(K, V)] =
        left match {
          case leftHead +: leftTail =>
            right match {
              case rightHead +: rightTail =>
                if (ordering.compare(leftHead._1, rightHead._1) < 0)
                  leftHead +: loop(leftTail, right)
                else
                  rightHead +: loop(left, rightTail)
              case _ => left
            }
          case _ => right
        }

      loop(pairs, that)
    }
  }

}
