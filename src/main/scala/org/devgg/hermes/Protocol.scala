package org.devgg.hermes

import java.io.InputStream
import javax.xml.bind.DatatypeConverter
import scala.collection.mutable
import org.devgg.hermes.result.Result
import java.nio.channels.ReadableByteChannel
import java.nio.ByteBuffer
import java.util.Date

import scala.reflect.runtime.universe.TypeTag

class Protocol private[hermes] (private val id: List[Byte], private val version: List[Byte], private val messageIdSize: Int , private val messages: List[Message]) {

	def compose(messageId: Array[Byte], fields: List[_ <: Any]): Array[Byte] = id.toArray ++ (version.toArray ++ findMessage(messageId.toList).compose(fields))

	def parseOnce(rbc: ReadableByteChannel): (List[Byte], Result) = {
		val id = getBytes(rbc, this.id.length).toList
		val version = getBytes(rbc, this.version.length).toList
		if (this.id == id && this.version == version) {
			findMessage(getBytes(rbc, messageIdSize).toList).parse(rbc)
		} else {
			var errorMessage = ""
			if (this.id != id) {
				errorMessage += "Invalid protocol id, is: '" + id + "' should be '" + id + "'."
			}
			if (this.version != version) {
				errorMessage += (if(errorMessage.length > 0) " " else "") + "Invalid protocol version, is: '" + id + "' should be " + version + "'."
			}
			throw new IllegalArgumentException(errorMessage)
		}
	}

	def parse(rbc: ReadableByteChannel, callback: ((List[Byte], Result)) => Unit) {
		while (true) {
			callback(parseOnce(rbc))
		}
	}


	private def findMessage(id: List[Byte]) = messages.find(_.id == id) getOrElse(throw new IllegalArgumentException("Message with id '" + byteListToHexString(id) + "' not defined for protocol '" + byteListToHexString(this.id) + "'"))

	override val toString = "id: " + formatToString(byteListToHexString(id) + "\nversion: " + byteListToHexString(version) + "\n", messages, "")
}

import scala.util.parsing.combinator.RegexParsers



object Protocol extends RegexParsers {
	override val skipWhitespace = false

	private val pageSeparator = "---\\n".r
	private val identifier: Parser[String] = "[a-zA-Z0-9_<>.]+".r
	private val multiLineMap: Parser[String] = identifier <~ ":\\n".r

	private val map: Parser[(String, String)] = (identifier <~ ":".r) ~ (identifier <~ "\\n".r) ^^ {case id ~ value => (id , value)}
	private val field: Parser[(String, List[(String, String)])] = "-".r ~> multiLineMap ~ map.* ^^ {case name ~ specifications => (name, specifications)}

	private val configuration: Parser[Int] = map ^^ {
		case (_ @ (`messageIdSizeS`, messageIdSize)) =>  messageIdSize.toInt
	}
	private val header: Parser[(List[Byte], List[Byte])] = (map ~ map) ^^ {
		case (_ @ (`protocolIdS`, protocolId)) ~ (_ @ (`versionS`, version)) => (DatatypeConverter.parseHexBinary(protocolId).toList, DatatypeConverter.parseHexBinary(version).toList)
	}
	private val typeDefinitions: Parser[List[(String, List[(String, List[(String, String)])])]] = (multiLineMap ~ field.* ^^ {case name ~ fields => (name, fields)}).*
	private val messageDefinitions: Parser[List[(Array[Byte], List[(String, List[(String, String)])])]] = (multiLineMap ~ field.+ ^^ {case name ~ fields => (DatatypeConverter.parseHexBinary(name), fields)}).*


	private val protocol: Parser[ProtocolInitializer] = configuration~ (pageSeparator ~> header) ~ (pageSeparator ~> typeDefinitions) ~ (pageSeparator ~> messageDefinitions) ^^ {
		case config ~ head ~ typeDefs ~ messageDefs =>
			new ProtocolInitializer(head._1, head._2, config, mutable.Set(supportedSerializers.toSeq: _*), typeDefs, messageDefs)
	}

	def apply(yamlString: String): ProtocolInitializer = parseAll(protocol, preformat(yamlString)).get


	private def preformat(string: String): String = {
		(for (line <- string.lines) yield {
			line./:("", true)((agg, elem) => (agg, elem) match {
				case ((_, false), _) | (_, ' ') => agg
				case ((s, _), '#') => (s, false)
				case ((s, true), char) => (s + char, true)
			})._1
		}).fold ("") ((result, line) => if (!line.matches(" *")) result + line + "\n" else result)
	}
}













