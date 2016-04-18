package macrame.play

import scala.util.control.NoStackTrace

package object enums {
   private[enums] def throwException(s : String) = throw new Exception(s) with NoStackTrace
}
