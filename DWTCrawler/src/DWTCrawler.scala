import processing.core._
import processing.core.PConstants._
import java.awt.event._
import javax.swing.JFrame
import scala.collection.mutable.ListBuffer

object DWTCrawler extends App {
	val papp = new DWTCrawler
	val frame = new JFrame("Processing Scala Application")
	frame.getContentPane().add(papp)
	papp.init

	frame.pack
	frame.setVisible(true)
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
}

class DWTCrawler extends PApplet {
	
	override def setup() = {
		size(512, 512, JAVA2D)

		val img: PImage = loadImage("data/2007_MV_Brutale_910_1r.jpg")
		background(0)

		val sig = new WaveletSignature(img, "A", 1)
		
		WaveletSignatureFS.saveTo(sig, "/Users/hari/Documents/Work/eclipseWS/DWTCrawler/src/data/2007_MV_Brutale_910_1r.sig")
		
		val sig2 = WaveletSignatureFS.loadFrom("/Users/hari/Documents/Work/eclipseWS/DWTCrawler/src/data/2007_MV_Brutale_910_1r.sig")
		
		val img2 = getImage(sig2.Y,sig2.U,sig2.V, 32)
		image(img2, 0, 0, 512, 512)
	}

	override def draw() {

	}

	def getImage(data: ListBuffer[Int], SIZE: Int) = {
		val ret: PImage = createImage(SIZE, SIZE, RGB)

		ret.loadPixels()

		for (j <- 0 until SIZE)
			for (i <- 0 until SIZE) {
				ret.pixels(j * SIZE + i) = color(PApplet.constrain(Math.abs(data(j * SIZE + i)), 0, 255))
			}

		ret.updatePixels()
		ret
	}

	def getImage(data: ListBuffer[Int], data1: ListBuffer[Int], data2: ListBuffer[Int], SIZE: Int) = {
		val ret: PImage = createImage(SIZE, SIZE, RGB)

		ret.loadPixels()

		for (j <- 0 until SIZE)
			for (i <- 0 until SIZE) {
				ret.pixels(j * SIZE + i) = color(PApplet.constrain(Math.abs(data(j * SIZE + i)), 0, 255), PApplet.constrain(Math.abs(data1(j * SIZE + i)), 0, 255), PApplet.constrain(Math.abs(data2(j * SIZE + i)), 0, 255))
			}

		ret.updatePixels()
		ret
	}

}