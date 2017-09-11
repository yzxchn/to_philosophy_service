package to_philosophy.utils;

import java.io.IOException;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class WikiPageReader {
	private Document soup;
	private String base;
	
	public WikiPageReader(String link) throws IOException {
		this.soup = Jsoup.connect(link).get();
		// get the base domain name from the link
		this.base = link.substring(0, link.indexOf("/wiki/"));
	}
	
	public String getFirstValidLink() {
		Elements pTags = getPTags();
		// for each p element (which contain the main text content)
		for (Element p : pTags) {
			// remove anything contained within a pair of parenthesis
			WikiPageReader.removeParenthesis(p);
			Elements aTags = p.getElementsByTag("a");
			// for each <a> tag child (<i><a><i/> tags are ignored)
			for (Element a : aTags) {
				String href = a.attr("href");
				if (WikiPageReader.isValidLink(href)) {
					return base+href;
				}
			}
		}
		
		return null;
	}
	
	public Elements getPTags() {
		return this.soup.getElementsByTag("p");
	}
	
	/** Remove HTML text surrounded by parentheses
	 * 
	 * @param p a Jsoup Element object
	 * @return a Jsoup Element object with the HTML text surrounded by parenthesis removed.
	 */
	public static void removeParenthesis(Element p) {
		String html = p.html();	
		// only supports parenthesis pairs up to two levels
		// TODO: use stack-based parenthesis matching instead
		String newHtml = html.replaceAll("\\(([^()]*|\\([^()]*\\))*\\)", "");
		p.html(newHtml);
	}
	
	/**
	 * Check if the link points to a Wikipedia article and is not a red link
	 * @param link: a string that represents a link
	 * @return True if link is valid, false otherwise.
	 */
	public static boolean isValidLink(String link) {
		return link.contains("/wiki/") && !link.contains("redlink=1");
	}
}
