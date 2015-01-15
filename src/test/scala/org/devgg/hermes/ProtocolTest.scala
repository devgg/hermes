package org.devgg.hermes

import org.scalatest.FlatSpec
import org.devgg.hermes.{Protocol => PP}

class ProtocolTest extends FlatSpec {
	private val primitiveTypes = List("string<UTF8>, number<dec>")


//	"Protocol" should "parse simple Field" in {
//		assert(
//			PP.parse(
//				"""|0x01:
//				   |   -firstName:
//				   |       length_f:1
//				   |       type:String<UTF8>""".stripMargin).head.name == "0x01")
//	}
//
//	it should "produce IllegalArgumentException when the field's type is not specified" in {
//		intercept[IllegalArgumentException] {
//			PP.parse(
//				"""|0x01:
//				   |   -firstName:
//				   |       length_f:1""".stripMargin)
//		}
//	}
//
//	it should "produce IllegalArgumentException when a primitive field's length is not specified" in {
//		intercept[IllegalArgumentException] {
//			PP.parse(
//				"""|0x01:
//				   |   -firstName:
//				   |       reps:5
//				   |       type:String<UTF8>""".stripMargin)
//		}
//	}
//
//	it should "parse message with multiple fields" in {
//		assert(
//			PP.parse(
//				"""|0x01:
//				   |   -initials:
//				   |       length_f: 1
//				   |       type: String<UTF8>
//				   |   -lastName:
//				   |       length_v: 1
//				   |       type: String<UTF8>
//				   |   -age:
//				   |       length_f: 1
//				   |       type:Number<dec>""".stripMargin).head.fields.size == 3)
//	}
//
//	it should "parse multiple messages" in {
//		assert(
//			PP.parse(
//				"""|0x01:
//				   |   -initials:
//				   |       length_f: 1
//				   |       type: String<UTF8>
//				   |   -lastName:
//				   |       length_v: 1
//				   |       type: String<UTF8>
//				   |   -age:
//				   |       length_f: 1
//				   |       type:Number<dec>
//				   |0x02:
//				   |   -message:
//				   |       length_f:1
//				   |       type:String<UTF8>""".stripMargin).size == 2)
//	}
}