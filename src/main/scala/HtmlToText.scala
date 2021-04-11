import org.jsoup.Jsoup
import org.jsoup.internal.StringUtil
import org.jsoup.helper.Validate
import org.jsoup.nodes.{Document, Element, Node, TextNode}
import org.jsoup.select.{Elements, NodeTraversor, NodeVisitor}

import java.io.IOException
import HtmlToPlainText._
//remove if not needed
import scala.jdk.CollectionConverters._
import collection.JavaConverters._

// TODO Convert Java Collections to Scala Collections
// Converted from Java Using --> http://javatoscala.com/

object HtmlToPlainText {

  val userAgent: String = "Mozilla/5.0 (jsoup)"

  val timeout: Int = 5 * 1000

  def main(args: Array[String]) = {
    
    Validate.isTrue(
      args.length == 1 || args.length == 2,
      "usage: java -cp jsoup.jar org.jsoup.examples.HtmlToPlainText url [selector]"
    )
    val url: String = args(0)
    val selector: String = if (args.length == 2) args(1) else null
// fetch the specified URL and parse to a HTML DOM
    val doc: Document =
      Jsoup.connect(url).userAgent(userAgent).timeout(timeout).get
    
      // instatiate class
    val formatter: HtmlToPlainText = new HtmlToPlainText()

    if (selector != null) {
// get each element that matches the CSS selector
      val elements: Elements = doc.select(selector)
      elements.forEach{ element =>
        val plainText: String = formatter.getPlainText(element)
        println(plainText)
      }
    } else {
// format the whole doc
      val plainText: String = formatter.getPlainText(doc)
      println(plainText)
    }
  }

  private class FormattingVisitor extends NodeVisitor {

    val maxWidth: Int = 80
    var width: Int = 0

// holds the accumulated text
     var accum: StringBuilder = new StringBuilder()

// hit when the node is first seen
    def head(node: Node, depth: Int): Unit = {
      val name: String = node.nodeName()
      if (
        node.isInstanceOf[TextNode]
      ) // TextNodes carry all user-readable text in the DOM.
        append(node.asInstanceOf[TextNode].text())
      else if (name.==("li")) append("\n * ")
      else if (name.==("dt")) append("  ")
      else if (StringUtil.in(name, "p", "h1", "h2", "h3", "h4", "h5", "tr"))
        append("\n")
    }

// hit when all of the node's children (if any) have been visited
    def tail(node: Node, depth: Int): Unit = {
      val name: String = node.nodeName()
      if (
        StringUtil.in(name, "br", "dd", "dt", "p", "h1", "h2", "h3", "h4", "h5")
      ) append("\n")
      else if (name.==("a"))
        append(String.format(" <%s>", node.absUrl("href")))
    }

// appends text to the string builder with a simple word wrap method
   def append(text: String): Unit = {
      if (
        text.startsWith("\n")
      ) // reset counter if starts with a newline. only from formats above, not in natural text
        width = 0
      if (
        text.==(" ") &&
        (accum.length == 0 ||
          StringUtil.in(accum.substring(accum.length - 1), " ", "\n"))
      ) // don't accumulate long runs of empty spaces
        return
      if (text.length + width > maxWidth) {
// won't fit, needs to wrap
        val words: Array[String] = text.split("\\s+")
        for (i <- 0 until words.length) {
          var word: String = words(i)
          val last: Boolean = i == words.length - 1
          if ( // insert a space if not the last word
            !last
          ) word = word + " "
          if (word.length + width > maxWidth) {
// wrap and reset counter
            accum.append("\n").append(word)
            width = word.length
          } else {
            accum.append(word)
            width += word.length
          }
        }
      } else {
// fits as is, without need to wrap text
        accum.append(text)
        width += text.length
      }
    }

    override def toString(): String = accum.toString

  }

}

class HtmlToPlainText {

  def getPlainText(element: Element): String = {
    val formatter: FormattingVisitor = new FormattingVisitor()
    NodeTraversor.traverse(formatter, element)
    formatter.toString
  }

}
