package org.devgg.hermes.field

import java.io.InputStream
import java.nio.channels.ReadableByteChannel

import org.devgg.hermes._
import org.devgg.hermes.result.ComplexResult
import org.devgg.{hermes => S}

private[hermes] class ComplexField(override val name: String, private val fields: List[Field], private val repetitions: (Int, Boolean)) extends Field {

	override def compose(fields: Any): Array[Byte] = {
		fields match {
			case fields: List[_] =>
				assert(this.fields.length == fields.length, "The field with name '" + name + "' requires " + this.fields.length + " fields, " + fields.length + " have been provided.")
				(for ((template, data) <- this.fields.zip(fields)) yield {
					template.compose(data)
				}).reduce(_ ++ _)
			case _ => throw new IllegalArgumentException("Data for the field '" + name + "' must be provided in a List. Also the length of the List must equal the repetitions of the Field (if the repetitions are fixed).")
		}

	}

	override def parse(rbc: ReadableByteChannel) = new ComplexResult(name, for (field <- fields) yield field.parse(rbc))


	override val toString = formatToString(name + ":", fields, "    ")
}

private[hermes] object ComplexField {
	def apply(name: String, fields: List[Field], specifications: List[(String, String)]) = {
		val repetitions: (Int, Boolean) = specifications.find(_._1.startsWith(S.repetitions)) match {
			case Some((S.repetitionsFixed, uint)) => (uint.toInt, true)
			case Some((S.repetitionsVariable, uint)) => (uint.toInt, false)
			case _ => (1, true)
		}
		new ComplexField(name, fields, repetitions)
	}
}
