package org.devgg.hermes

import org.devgg.hermes.field.{Field, ComplexField, PrimitiveField}
import org.devgg.{hermes => S}

import scala.collection.mutable

class ProtocolInitializer(private val id: List[Byte],
                          private val version: List[Byte],
                          private val messageIdSize: Int,
                          private val serializers: mutable.Set[Serializer[_ <: Any]],
                          private val typeDefinitions: List[(String, List[(String, List[(String, String)])])],
                          private val messageDefinitions: List[(Array[Byte], List[(String, List[(String, String)])])]) {

	def setSerializer(serializer: Serializer[_ <: Any]) {
		serializers += serializer
	}

	def init = {
		val complexDataTypes = mutable.Map[String, List[Field]]()
		for (typeDefinition <- typeDefinitions) {
			complexDataTypes += (typeDefinition._1 -> processFields(typeDefinition._2, serializers, complexDataTypes))
		}

		val messages = for (messageDefinition <- messageDefinitions) yield {
			new Message(messageDefinition._1.toList, processFields(messageDefinition._2, serializers, complexDataTypes))
		}

		new Protocol(id, version, messageIdSize, messages)
	}

	private def processFields(fields: List[(String, List[(String, String)])], serializers: mutable.Set[Serializer[_ <: Any]], complexDataTypes: mutable.Map[String, List[Field]]): List[Field] = {
		for(field <- fields) yield {
			field._2.find(_._1 == S.dataType) match {
				case Some(dataTypeString) =>
					if (serializers.exists(_.name == dataTypeString._2)) {
						PrimitiveField(field._1, dataTypeString._2, serializers.find(_.name == dataTypeString._2).get, field._2)
					} else if (complexDataTypes.exists(_._1 == dataTypeString._2)) {
						ComplexField(field._1, complexDataTypes.find(_._1.equalsIgnoreCase(dataTypeString._2)).get._2, field._2)
					} else {
						throw new IllegalArgumentException("A Serializer must be provided for type '" + dataTypeString._2 + "'. (Type inference is case sensitive.)")
					}
				case None => throw new IllegalArgumentException("Field '" + field._1 + "' does not specify a type")
			}
		}
	}
}
