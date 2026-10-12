package saillog.io

import upickle.default.*
import saillog.model.{*, given}

object Json:
  def save(file: os.Path, trips: List[Trip]): Unit =
    os.write.over(file, write(trips, indent = 2))

  def load(file: os.Path): List[Trip] =
    read[List[Trip]](os.read(file))
