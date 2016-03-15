import java.awt.Toolkit;import java.text.DecimalFormat;import java.util.Calendar;import java.util.Date;import java.util.TimeZone;

public class GetNpApi //read multiple mkts quote and create the chart for html all day long
{
static DecimalFormat IntF=new DecimalFormat("00");
public static String readData(String url, String startFrom, String endWith){	WebPageJob aUrl=null;	aUrl=new WebPageJob(url);	if (aUrl.myInStream != null &&				aUrl.getPageSrc(startFrom, endWith) && aUrl.totalRead > 0)	return aUrl.readPage.replaceAll("&NBSP;", " ");			System.out.println("!!!Failed in "+url);				if (aUrl.myInStream == null)		System.out.println("Cannot open data stream");	return null;}	
public static void main(String[] args) {
	
String myQuotesUrl = "https://www.taishinbank.com.tw";//"https://tw.stock.yahoo.com";
	String pgStartRead="<HEAD>";
	String pgEnd="</BODY>";		String foundPage=readData(myQuotesUrl, pgStartRead, pgEnd);	if (foundPage != null && foundPage.length() > 0)		System.out.println(foundPage);
	
}		
}
