
import processing.core.PImage
import scala.math._
import scala.collection.mutable.ListBuffer

class WaveletSignature(img: PImage, fname: String, lastmod: Long, size: Int, lev1: Int, lev2: Int) extends Serializable {
	val SIZE:Int = size
	val LEVEL1:Int = lev1
	val LEVEL2:Int = lev2

	val S1 = SIZE >> (LEVEL1 - 1)
	val S2 = SIZE >> (LEVEL2 - 1)

	var fileName: String = fname
	var lastModified: Long = lastmod

	val Y = new ListBuffer[Int]()
	val U = new ListBuffer[Int]()
	val V = new ListBuffer[Int]()
	val Ys = new ListBuffer[Int]()
	val Us = new ListBuffer[Int]()
	val Vs = new ListBuffer[Int]()

	var sigY = 0.0
	var sigU = 0.0
	var sigV = 0.0

	// Initialized only for queries
	var sigYb = 0.0
	var sigUb = 0.0
	var sigVb = 0.0
	var sigYdivb = 0.0
	var sigUdivb = 0.0
	var sigVdivb = 0.0

	if (img != null) {
		getImagedata(img)

		sigY = featureSD(reduce(Y, S1 >> 2, S1))
		sigU = featureSD(reduce(U, S1 >> 2, S1))
		sigV = featureSD(reduce(V, S1 >> 2, S1))
	}

	def this(img: PImage) = this(img, "", 0, 256, 4, 5)
	def this() = this(null, "", 0, 256, 4, 5)
	def this(img: PImage, fname: String, lastmod: Long) = this(img, fname, lastmod, 256, 4, 5)
	def this(fname: String, lastmod: Long, Size: Int, Lev1: Int, Lev2: Int) = this(null, fname, lastmod, Size, Lev1, Lev2)

	def InitSignature(percent: Int) = {
		val beta: Double = 1.0 - percent / 100.0
		sigYb = sigY * beta
		sigUb = sigU * beta
		sigVb = sigV * beta
		sigYdivb = sigY / beta
		sigUdivb = sigU / beta
		sigVdivb = sigV / beta
	}

	private def getImagedata(img: PImage) {
		if (img.width != img.height || img.width != SIZE)
			img.resize(SIZE, SIZE)

		var i = 0
		var j = 0
		val SQ = SIZE * SIZE

		val A = new Array[Int](SQ)
		val B = new Array[Int](SQ)
		val C = new Array[Int](SQ)

		img.loadPixels()
		i = 0
		img.pixels.foreach((a: Int) => {
			val r = (a & 0x00FF0000) >> 16
			val g = (a & 0x0000FF00) >> 8
			val b = a & 0x000000FF

			A(i) = (0.299 * r + 0.587 * g + 0.114 * b).toInt
			B(i) = (128 - 0.168736 * r - 0.331264 * g + 0.5 * b).toInt
			C(i) = (128 + 0.5 * r - 0.418688 * g - 0.081312 * b).toInt
			i += 1
		})

		var t: DWT_CDF_9_7 = new DWT_CDF_9_7(SIZE, SIZE, LEVEL1)

		reduce(t.forward(A, 0), S1, SIZE, Y)
		reduce(t.forward(B, 0), S1, SIZE, U)
		reduce(t.forward(C, 0), S1, SIZE, V)

		t = new DWT_CDF_9_7(SIZE, SIZE, LEVEL2)
		reduce(t.forward(A, 0), S2, SIZE, Ys)
		reduce(t.forward(B, 0), S2, SIZE, Us)
		reduce(t.forward(C, 0), S2, SIZE, Vs)
	}

	private def reduce(y2: ListBuffer[Int], stride: Int, width: Int) = {
		val ep1 = (stride - 1) * width
		val ret = new Array[Int](stride * stride)
		var cnt = 0

		for (i <- 0 to ep1 by width)
			for (j <- i until (i + stride)) {
				ret(cnt) = y2(j)
				cnt += 1
			}
		ret
	}

	private def reduce(y2: Array[Int], stride: Int, width: Int, dst: ListBuffer[Int]) = {
		val ep1 = (stride - 1) * width

		for (i <- 0 to ep1 by width)
			for (j <- i until (i + stride))
				dst += y2(j)

	}

	private def featureSD(y2: Array[Int]): Double = {
		val mean = y2.reduceLeft(_ + _) / y2.length.toDouble
		def sqDiff(v1: Double, v2: Double): Double = pow(v1 - v2, 2.0)
		val ret = y2.foldLeft(0.0)(_ + sqDiff(_, mean))
		sqrt(ret / y2.length.toDouble)
	}

}