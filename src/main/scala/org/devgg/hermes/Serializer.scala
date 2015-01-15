package org.devgg.hermes


import scala.reflect.ClassTag
import scala.reflect.runtime.universe._


class Serializer[A: ClassTag](val name: String, private val compose: A => Array[Byte], val parse: Array[Byte] => A) {

	//todo make compose typesafe types with type parameters
	//val m = runtimeMirror(getClass.getClassLoader)
	//typeOf[A].typeSymbol.asClass.fullName

	def compose(data : Any, callee: String): Array[Byte] = {
		data match {
			case data if implicitly[ClassTag[A]].runtimeClass.isInstance(data) => compose(data.asInstanceOf[A])
			case _ => throw new IllegalArgumentException(
				"In field '" + callee + "'. Data for serializer '" + name + "' must have type '" + implicitly[ClassTag[A]].runtimeClass.getCanonicalName +
				"'. The provided data '" + data + "' has type '" + data.getClass.getCanonicalName + "'")
		}
	}

	override val toString = name + " (" + implicitly[ClassTag[A]].runtimeClass.getCanonicalName + ")"
}


