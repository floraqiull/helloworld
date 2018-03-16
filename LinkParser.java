

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkParser {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String TEST_LINK = "http://www.google.com";
		String TEST_LINK2 = "http://www.ns.com";
		String html = "ha somehitng <a href=www.google.com>google</a>";
		
		String html2 = "ha <a href=''>google</a> somehitng";
		
		LinkParser lf = new LinkParser(html);
		List<String> ll = lf.getHrefList();
		List<String> rl = new ArrayList<String>();
		for(String l : ll){
			System.out.println(l);
			rl.add("REPLACED");
		}
		
		String content = lf.parseHTMLLinks(rl);
		System.out.println(html);
		System.out.println(content);

	}
	
	
	private String html;
	private List<String> hrefList = new ArrayList<String>();
	
	/*regex for a tag*/
	private static final String HTML_A_TAG_REGEX = "(?i)<a([^>]+)>(.+?)</a>";
	/*regex for href tag
	 * accept: href="sites"
	 * 			href='sites'
	 * 			href=sites*/
	private static final String HTML_HREF_TAG_REGEX = 
		"\\s(?i)href\\s*=\\s*(\"([^\"]+\")|'[^']+'|([^'\">\\s]+))";
	private Pattern pTag;
	private Pattern pHref;
	
	public LinkParser(String html){
		if(html != null){
			this.html = html;
			
			//init pattern
			pTag = Pattern.compile(HTML_A_TAG_REGEX);
			pHref = Pattern.compile(HTML_HREF_TAG_REGEX);
			
			//retrieve link from html
			Matcher mTag, mHref;
			mTag = pTag.matcher(html);
			while (mTag.find()) {
				System.out.println(mTag.group(1));
				String href = mTag.group(1); // href	
				mHref = pHref.matcher(href);
				
				while (mHref.find()) {	
					String sLink = mHref.group(1); // link
					hrefList.add(replaceInvalidChar(sLink));
					
				}
			}
		}
		
	}
	
	/* replace href content for html*/
	public String parseHTMLLinks(List<String> replacedLinkList) {
		if(replacedLinkList.isEmpty() || hrefList.isEmpty())
			return null;
		if(replacedLinkList.size() != hrefList.size())
			return null;
		
		StringBuffer sb = new StringBuffer();	//buffer to hold replaced content
		int currentPointer = 0;		//pointer for html
		int rlIndex = 0;
		Matcher mTag, mHref;

		mTag = pTag.matcher(html);
		while (mTag.find()) {
			String href = mTag.group(1); // href
			int aStart = mTag.start(1);
			int aEnd = mTag.end(1);
//			System.out.printf("matcher tag====start: %d, end: %d, find %s\n", matcherTag.start(1),
//					matcherTag.end(1), matcherTag.group(1));
			
			mHref = pHref.matcher(href);
			while (mHref.find()) {	
				String sLink = mHref.group(1); // link
				int hStart = mHref.start(1);
				int hEnd = mHref.end(1);
//				System.out.printf("matcher link====start: %d, end: %d, find %s\n", mHref.start(1),
//						mHref.end(1), mHref.group(1));
//				System.out.println(html.substring(aStart + hStart,
//						aStart + hEnd));
				
				//replace text
				if(sLink.contains("'") || sLink.contains("\"")){
					sb.append(html.substring(currentPointer, aStart + hStart + 1));
					currentPointer = aStart + hEnd - 1;
				} else {
					sb.append(html.substring(currentPointer, aStart + hStart));
					currentPointer = aStart + hEnd;
				}
				sb.append(replacedLinkList.get(rlIndex++));
//				System.out.println(sb.toString());	
			}
		}
		
		if(currentPointer < html.length()){
			sb.append(html.substring(currentPointer, html.length()));
		}
		
		return sb.toString();
	}
	
	private String replaceInvalidChar(String link){
		if(link != null){
			link = link.replaceAll("'", "");
			link = link.replaceAll("\"", "");
		}
		return link;
	}

	public List<String> getHrefList() {
		return hrefList;
	}

	public void setHrefList(List<String> hrefList) {
		this.hrefList = hrefList;
	}	
	
}
