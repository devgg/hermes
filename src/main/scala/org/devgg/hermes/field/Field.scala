package org.devgg.hermes.field

import java.nio.channels.ReadableByteChannel

import org.devgg.hermes.result.Result

private[hermes] trait Field{
	val name: String

	def compose(data: Any): Array[Byte]
	def parse(rbc: ReadableByteChannel): Result
}
