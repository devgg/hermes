package org.devgg

import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.util.Date
import javax.xml.bind.DatatypeConverter


import org.devgg.hermes.result.Result

import scala.reflect.runtime.universe.TypeTag

package object hermes {
	private[hermes$] val protocolIdS = "protocolId"
	private[hermes$] val versionS = "version"

	private[hermes$] val protocolIdSizeS = protocolIdS + "Size"
	private[hermes$] val versionSizeS = versionS + "Size"
	private[hermes$] val messageIdSizeS = "messageIdSize"

	private[hermes$] val dataType = "type"
	private[hermes$] val length = "length"
	private[hermes$] val repetitions = "reps"
	private[hermes$] val fixed = "f"
	private[hermes$] val variable = "v"
	private[hermes$] val specifierDelimiter = "_"
	private[hermes$] val lengthFixed = length + specifierDelimiter + fixed
	private[hermes$] val lengthVariable = length + specifierDelimiter + variable
	private[hermes$] val repetitionsFixed = repetitions + specifierDelimiter + fixed
	private[hermes$] val repetitionsVariable = repetitions + specifierDelimiter + variable

	private[hermes$] val integral = "int"
	private[hermes$] val decimal = "dec"

	private[hermes$] val string = "String"
	private[hermes$] val optionDelimiters = ("<", ">")
	private[hermes$] val number = "number"
	private[hermes$] val numberIntegral = addOptions(number, Array(integral))
	private[hermes$] val numberDecimal = addOptions(number, Array(decimal))

	private[hermes$] val supportedSerializers: Set[Serializer[_ <: Any]] = Set(
		new Serializer[String](addOptions(string, Array("utf8")), _.getBytes("utf8"), new String(_, "utf8")),
		new Serializer[BigInt](addOptions(number, Array(decimal)), _.toByteArray, BigInt(_)),
		new Serializer[BigDecimal](addOptions(number, Array(integral)), _.toString().getBytes("utf8"), bytes => BigDecimal(new String(bytes, "utf8")))
	)
	private[hermes$] def addOptions(typeBase: String, options: Array[String]) = options.foldLeft (typeBase) ((concat: String, elem: String) => concat + optionDelimiters._1 + elem + optionDelimiters._2)

	private[hermes$] def formatToString(current: String, children: List[_ <: Any] , indentation: String) = current + "\n" + indentation + (children.map(_.toString).reduce(_ + "\n" + _).lines mkString "\n" + indentation)
	private[hermes$] def byteListToHexString(byteList: List[Byte]) = DatatypeConverter.printHexBinary(byteList.toArray)
	private[hermes$] def specificationToString(specType: String, specifications: (Int, Boolean)) = specType + specifierDelimiter + (if (specifications._2) fixed else variable) + ": " + specifications._1


	private[hermes$] def getBytes(rbc: ReadableByteChannel, number: Int): Array[Byte] = {
		val bb = ByteBuffer.allocate(number)
		rbc.read(bb)
		while (bb.hasRemaining) {
			Thread.sleep(500)
			if (rbc.read(bb) == -1) {
				throw new Exception("todo")
			}
		}
		val result = new Array[Byte](number)
		bb.position(0)
		bb.get(result)
		result
	}


	private[hermes$] def getField[A: TypeTag](name: String)= castToGenericType[A](name)
	private def castToGenericType[A: TypeTag](value: Any): A = value.asInstanceOf[A]
}

