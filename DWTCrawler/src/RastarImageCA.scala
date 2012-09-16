
import java.io.File
import processing.core.PApplet

class RastarImageCA(prnt: PApplet) extends CrawlerAdapter {
	val FILETYPES = List(".gif", ".jpg", ".jpeg", ".tga", ".png")

	val parent: PApplet = prnt;

	def registerFileTypes(): List[String] = FILETYPES

	def acceptFile(file: File): Boolean = {
		FILETYPES.foldLeft(false)((r, c) => r || (file.getName().toLowerCase().endsWith(c)))
	}

	def getSignature(file: File): WaveletSignature = {
		new WaveletSignature(parent.loadImage(file.getAbsolutePath()), file.getAbsolutePath(), file.lastModified())
	}
}