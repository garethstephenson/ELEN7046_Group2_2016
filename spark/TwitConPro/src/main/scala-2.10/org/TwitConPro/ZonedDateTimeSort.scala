package org.TwitConPro

import java.time.ZonedDateTime

/**
  * Created by Gareth on 2016/06/12.
  */
object ZonedDateTimeSort {
    implicit def dateTimeOrdering: Ordering[ZonedDateTime] = Ordering.fromLessThan((date2, date1) => date1.isAfter(date2))
}
