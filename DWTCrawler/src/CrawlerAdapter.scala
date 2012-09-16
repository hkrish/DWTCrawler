import java.io.File

trait CrawlerAdapter {
	def registerFileTypes(): List[String];
	def acceptFile(file: File): Boolean;
	def getSignature(file: File): WaveletSignature;
}