package org.devgg.hermes.result

import org.devgg.hermes._

import scala.collection.mutable.Stack
import scala.reflect.ClassTag

class ComplexResult(val name: String, val fields: List[Result]) extends Result {


	override def get[A: ClassTag](path: String*): A = {
		if (path.isEmpty) {
			this
		}
		fields.find(_.name == path.head).getOrElse(throw new IllegalArgumentException("'" + this.name + "' does not contain a field named '" + path.head + "'.")).get[A](path.tail.toSeq:_*)
	}

	override val toString = formatToString(name + ":", fields, "    ")
}
