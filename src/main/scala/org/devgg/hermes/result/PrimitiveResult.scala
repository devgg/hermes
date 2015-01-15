package org.devgg.hermes.result

import org.devgg.hermes
import org.devgg.hermes._

import scala.reflect.ClassTag

class PrimitiveResult[A](val name: String, val value: A) extends Result {
	override def get[B: ClassTag](path: String*): B = {
		if (path.isEmpty) {
			value match {
				case value if implicitly[ClassTag[B]].runtimeClass.isInstance (value) => value.asInstanceOf[B]
				case _ => throw new IllegalArgumentException ("The type of '" + this.name + "' is '" + value.getClass.getCanonicalName + "'.")
			}
		} else {
			throw new IllegalArgumentException ("The primitive type '" + this.name + "' has been reached but the path '" + path.mkString(", ") + "' is nonempty.")
		}
	}

	override val toString = formatToString(name + ":", List(value), "   ")

}
