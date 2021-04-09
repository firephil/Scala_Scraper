## Web Scraping using [Scala 3](https://www.scala-lang.org/) & [Jsoup](https://jsoup.org/)

### Example
Fetch the Wikipedia homepage, parse it to a DOM, and select the headlines from the In the news section into a list of Elements.

```java
Document doc = Jsoup.connect("https://en.wikipedia.org/").get();
log(doc.title());
Elements newsHeadlines = doc.select("#mp-itn b a");
for (Element headline : newsHeadlines) {
log("%s\n\t%s", 
    headline.attr("title"), headline.absUrl("href"));
}
```