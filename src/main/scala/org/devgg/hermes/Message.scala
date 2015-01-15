package org.devgg.hermes

import java.io.InputStream
import java.nio.channels.ReadableByteChannel
import org.devgg.hermes.field.Field
import org.devgg.hermes.result.{Result, ComplexResult}

class Message(val id: List[Byte], val fields: List[Field]) {

	private[hermes] def compose(fields: List[_ <: Any]): Array[Byte] = {
		assert(this.fields.length == fields.length, "The unit with id '" + id + "' requires " + this.fields.length + " fields, " + fields.length + " have been provided.")
		id.toArray ++ (for ((template, data) <- this.fields.zip(fields)) yield {
			template.compose(data)
		}).reduce(_ ++ _)
	}

	private[hermes] def parse(rbc: ReadableByteChannel): (List[Byte], Result) = (id, new ComplexResult("", for (field <- fields) yield field.parse(rbc)))



	override val toString = formatToString(byteListToHexString(id) + ":", fields, "  ")
}
