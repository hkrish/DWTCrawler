
import java.io.File
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics

class SVGCA(prnt: PApplet) extends CrawlerAdapter {
	val FILETYPES = List(".svg")

	val parent: PApplet = prnt;

	def registerFileTypes(): List[String] = FILETYPES

	def acceptFile(file: File): Boolean = {
		FILETYPES.foldLeft(false)((r, c) => r || (file.getName().toLowerCase().endsWith(c)))
	}

	def getSignature(file: File): WaveletSignature = {
		var svg = parent.loadShape(file.getAbsolutePath());
		if (svg != null) {
			var p2d: PGraphics = parent.createGraphics(256, 256, PConstants.P2D);
			p2d.background(255)
			p2d.shape(svg, 0, 0, p2d.width, p2d.height)
			new WaveletSignature(p2d.get(), file.getAbsolutePath(), file.lastModified())
		} else
			null
	}
}