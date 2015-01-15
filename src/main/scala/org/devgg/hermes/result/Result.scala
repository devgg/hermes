package org.devgg.hermes.result

import scala.collection.mutable.Stack
import scala.reflect.ClassTag

trait Result {
	val name: String
	def get[A: ClassTag](path: String*): A
}
