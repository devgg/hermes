package org.devgg.hermes.field

import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel

import org.devgg.hermes._
import org.devgg.hermes.result.PrimitiveResult
import org.devgg.{hermes => S}

private[hermes] class PrimitiveField(override val name: String, private val serializerName: String, private val serializer: Serializer[_ <: Any], private val length: (Int, Boolean), private val repetitions: (Int, Boolean)) extends Field {


	override def compose(data: Any): Array[Byte] = {
		if (repetitions._2 && repetitions._1 == 1) {
			composeSingle(data)
				//case _ => throw new IllegalArgumentException("Data for the field '" + name + "' must have type '" + clazz + "'.")

		} else {
			data match {
				case data: List[Any] =>
					var composed = (for (item <- data) yield {
						composeSingle(item)
					}).reduce(_ ++ _)
					if (!repetitions._2) {
						composed = BigInt(data.length).toByteArray ++ composed
					}
					composed
				case _ => throw new IllegalArgumentException("Data for the field '" + name + "' must be provided in a List. Also the length of the List must equal the repetitions of the Field (if the repetitions are fixed).")
			}
		}
	}

	private def composeSingle(data: Any): Array[Byte] = {
		val composed = this.serializer.compose(data, this.name)
		if (length._2) {
			if (composed.length == length._1) composed else
				throw new IllegalArgumentException("Field '" + name + "' has fixed length of '" + length._1 + "'. The data provided resulted in an array of length '" + composed.length + "'.")
		} else {
			val variableLength = BigInt(composed.length).toByteArray
			if (variableLength.length <= length._1) variableLength.reverse.padTo(length._1, 0.toByte).reverse ++ composed else
				throw new IllegalArgumentException("Field '" + name + "' has variable length of '" + length._1 + "'. The data provided resulted in a length field of length '" + variableLength + "'.")

		}
	}


	override def parse(rbc: ReadableByteChannel) = {
		val reps = if (repetitions._2) {
			repetitions._1
		} else {
			BigInt(getBytes(rbc, repetitions._1)).toInt
		}
		val field: List[Any] = (for (_ <- 1 to reps) yield {
			if (length._2) {
				serializer.parse(getBytes(rbc, length._1))
			} else {
				serializer.parse(getBytes(rbc, BigInt(getBytes(rbc, length._1)).toInt))
			}
		}).toList
		if (repetitions._1 == 1 && repetitions._2) new PrimitiveResult(name, field.head) else new PrimitiveResult(name, field)
	}

	override val toString = formatToString(name + ":", List("serializer: " + serializer, specificationToString(S.length, length), specificationToString(S.repetitions, repetitions)), "   ")
}

private[hermes] object PrimitiveField {
	def apply(name: String, parserName: String, serializer: Serializer[_ <: Any], specifications: List[(String, String)]) = {
		val length: (Int, Boolean) = specifications.find(_._1.startsWith(S.length)) match {
			case Some((S.lengthFixed, uint)) => (uint.toInt, true)
			case Some((S.lengthVariable, uint)) => (uint.toInt, false)
			case _ => throw new IllegalArgumentException("Primitive field '" + name + "' does not specify a length.")
		}

		val repetitions: (Int, Boolean) = specifications.find(_._1.startsWith(S.repetitions)) match {
			case Some((S.repetitionsFixed, uint)) => (uint.toInt, true)
			case Some((S.repetitionsVariable, uint)) => (uint.toInt, false)
			case _ => (1, true)
		}
		new PrimitiveField(name, parserName, serializer, length, repetitions)
	}
}
