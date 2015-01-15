package org.devgg.hermes

import java.io.{PipedInputStream, PipedOutputStream, ByteArrayInputStream}
import scala.reflect.runtime.universe._
import scala.reflect.ClassTag

//abstract class Foo[T <: Bar : ClassTag]{
//	...
//	val clazz = implicitly[ClassTag[T]].runtimeClass
//	def method1 = {
//		case CaseClass(t) if clazz.isInstance(t) => println(t) // you could use `t.asInstanceOf[T]`
//			csse _ =>
//	}
//}


object Main {
	def main(args: Array[String]) {

		val source = scala.io.Source.fromFile("src/main/resources/simple.yaml")
		val yaml = source.getLines mkString "\n"
		source.close()

		println(BigInt(1).toByteArray.deep)

		val pi = Protocol(yaml)

		println("Florian".getBytes.deep)
		println("Gauger".getBytes.deep)

		pi.setSerializer(new Serializer[String]("String", _.getBytes("utf8"), new String(_, "utf8")))
		pi.setSerializer(new Serializer[Double]("Double", BigDecimal(_).toString().getBytes("utf8"), bytes => BigDecimal.exact(new String(bytes, "utf8")).doubleValue()))
		val protocol = pi.init

//		println(protocol.messages(0).fields(0).asInstanceOf[ComplexField].fields(0).compose("Hello98").deep)
//		println(protocol.messages(0).fields(0).asInstanceOf[ComplexField].fields(0).parse(new ByteArrayInputStream(Array[Byte](72, 101, 108, 108, 111, 57, 56))))//.asInstanceOf[PrimitiveResult[String]].value)

		println(protocol.compose(Array[Byte](0), List(List("Florian", "Gauger", "GG"))).deep)



		val in = new PipedInputStream()
		val out = new PipedOutputStream(in)
		new Thread(
			new Runnable(){
				def run(){
					out.write(protocol.compose(Array[Byte](0), List(List("Florian", "Gauger", "GG"))))
//					out.write(BigInt("DEADBEEF", 16).toByteArray)
//					out.write("1.0.1".map(_.toByte).toArray)
//					out.write(BigInt("1", 16).toByteArray)
//					out.write("Florian".getBytes)
//					out.write("Gauger".getBytes)
//					do {
//						Thread.sleep(1000)
//					} while (true)
				}
			}
		).start()
		//println(protocol.setInputStream(in))
		println(protocol)
		println("123\n123\n234")


//		val dataType = new DataType[Any] {
//			var abc = (bytes: List[Byte]) => println("1")
//			override val name = "abc"
//			override def convert(bytes: List[Byte]) = abc(bytes) //todo
//		}
//		dataType.convert(List(5))
//		dataType.abc = (bytes: List[Byte]) => println("2")
//		dataType.convert(List(5))


		//println(abc.parseAll(abc.multipleMapValues, "length_v: 5\nlength_f: 12\n"))

		/*println(PP.parse("0x01:\n-firstName:\ntype:String<UTF8>\nlength_f:1\n-avgAge:\ntype:String<utf8>\nlength_v:8\n0x02:\n-firstName:\ntype:0x01\nlength_f:1\n-avgAge:\ntype:0x01\nlength_v:8\n"))
		val o = Strings

		print(PP.parse(
				 |0x01:
					|   -firstName:
		            |       length_f:1
		            |       type:String<UTF8>""".stripMargin))
*/
	}
}
