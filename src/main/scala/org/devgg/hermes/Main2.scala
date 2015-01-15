package org.devgg.hermes

import java.io._
import java.util.Date
import java.io.File
import java.io.IOException
import java.nio.file._

import scala.Console
import scala.io.StdIn
import scala.concurrent.ExecutionContext
import scala.reflect.runtime.universe.TypeTag
import org.devgg.hermes.result.Result
import java.nio.channels.Channels
import java.net.{ServerSocket, Socket, InetAddress}

object Main2 {
	def main(args: Array[String]) {
		val source = scala.io.Source.fromFile("src/main/resources/messenger.yaml")
		val pi = Protocol(source.getLines mkString "\n")
		source.close()

		pi.setSerializer(new Serializer[Date]("java.util.Date", serialize(_), deserialize(_).asInstanceOf[Date]))
		pi.setSerializer(new Serializer[Array[Byte]]("Array<Byte>", bytes => bytes, bytes => bytes))
//		pi.setSerializer(new Serializer[List[List[List[Map[String, Int]]]]]("a", bytes => null, bytes => null))
		val protocol = pi.init

		val serverSocket = new ServerSocket(32456)
		val socket = serverSocket.accept()
		//val socket = new Socket(InetAddress.getByName("google.com"), 80)
//		val socket = new Socket(InetAddress.getByAddress(Array[Byte](44, 5, 199.toByte, 23)), 4444)
//		val socket = new Socket(InetAddress.getByName("www.miky.punked.us"), 4444)

		var exit = false
//		val in = new PipedInputStream()
//		val out = new PipedOutputStream(in)
//		new Thread(
//			new Runnable(){
//				def run(){
//					out.write(protocol.compose(Array[Byte](0), List(List("Florian", new Date()), List("This is msg1", "This is msg2"))))
//					while (true) {
//						Thread.sleep(1000)
//					}
//				}
//			}
//		).start()

		def handleMessage(msg: (List[Byte], Result)) {
			msg match {
				case (List(0), result) => print(result.get[Date]("metadata", "date") + " - " + result.get[String]("metadata", "user") + "\n" + result.get[String]("messages")//.mkString("\n")

											+ "\n> ")
											//println(result)
				case (List(1), result) => println(result)
				case (list, _) => throw new IOException(list.toString())
			}
		}


		val messageHandler = new Thread(
			new Runnable {
				override def run() = protocol.parse(Channels.newChannel(socket.getInputStream) , handleMessage)

			}
		)
		messageHandler.start()





		val defaultPath = "C:/Users/Florian/Desktop/Message/"
		while (!exit) {
			StdIn.readLine("> ") match {
				case msg if msg.startsWith("msg") => socket.getOutputStream.write(protocol.compose(Array[Byte](0), List(List("King", new Date()), msg.replaceFirst("msg ", ""))))//.split("//").toList)))
				case file if file.startsWith("file") => protocol.compose(Array[Byte](1), List(Files.readAllBytes(FileSystems.getDefault.getPath(defaultPath + file.replaceFirst("file ", "")))))
				case ex if ex.startsWith("exit") => exit = true; socket.getInputStream.close();
				case _ => println("unknown command\nuse 'msg ' or 'file '")
			}
		}
	}


	def serialize(obj: java.io.Serializable): Array[Byte] = {
		val bos = new ByteArrayOutputStream ()
		var out: ObjectOutputStream = null
		try {
			out = new ObjectOutputStream(bos)
			out.writeObject(obj)
			bos.toByteArray
		} finally {
			try {
				if (out != null) {
					out.close()
				}
			} catch {
				case e: IOException =>
			}
			try {
				bos.close()
			} catch {
				case e: IOException =>
			}
		}
	}

	def deserialize(bytes: Array[Byte]): AnyRef = {
		val bis = new ByteArrayInputStream(bytes)
		var in: ObjectInput = null
		try {
			in = new ObjectInputStream(bis)
			in.readObject()
		} finally {
			try {
				bis.close()
			} catch {
				case e: IOException =>
			}
			try {
				if (in != null) {
					in.close()
				}
			} catch {
				case e: IOException =>
			}
		}
	}

}
