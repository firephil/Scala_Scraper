import javax.lang.model.util.Elements
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Document.OutputSettings
import org.jsoup.safety.Whitelist
//import collection.convert.ImplicitConversions.list

@main
def run() =
    println("Hello world!")
    val doc = Jsoup.connect("https://en.wikipedia.org/").get
    val newsHeadlines = doc.select("#mp-itn b a")
    println(newsHeadlines)