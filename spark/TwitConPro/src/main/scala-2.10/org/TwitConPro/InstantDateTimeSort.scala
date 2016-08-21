package org.TwitConPro

import java.time.Instant

/**
  * Created by Gareth on 2016/06/12.
  */
object InstantDateTimeSort {
    implicit def dateTimeOrdering: Ordering[Instant] = Ordering.fromLessThan((date2, date1) => date1.isAfter(date2))
}
