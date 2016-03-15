import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Vector;


public class GoogleQuoteParser extends WebPageParser	
{
private static DecimalFormat dataIntF=new DecimalFormat("00");
private final static Calendar today=Calendar.getInstance();
private static int thisYear= today.get(Calendar.YEAR);
private static int thisMonth=today.get(Calendar.MONTH)+1;
private static int thisDay=today.get(Calendar.DAY_OF_MONTH);
private static int todayInt=10000*(thisYear % 1000)+100*thisMonth+thisDay;

	public GoogleQuoteParser(String myName)
	{
		name=new String(myName);
		MULTI_QUOTE_PARSER=true;
	}
	
	@Override
	public MktQuote parsePage(String pageSrc)
	{
	MktQuote aQuote=new MktQuote();
	aQuote.date=new DecimalFormat("##.00").format(thisMonth+thisDay*0.01);
	String[] tempS=pageSrc.split("<TD ");
	String tagData;
		
		if (tempS[0].indexOf("_QUOTE") > 0) 
		{int iQ=tempS[0].indexOf("_QUOTE");
				int iN=iQ;
				while (tempS[0].charAt(iN) != '/') iN--;
				aQuote.symbol=tempS[0].substring(iN+1, iQ);}
		else 
		{
			System.out.println("WRONG!!!"+pageSrc);
			return null;
		}

		tagData=removeHtmTags(tempS[1]);
		if (tagData != null) 
		aQuote.price=tagData;
		tagData=removeHtmTags(tempS[4]);
		if (tagData != null) 
		{
			int iBad=tagData.length()-1;
//System.out.println("Vol = "+tagData+";"+tagData.charAt(iBad));
			while (tagData.charAt(iBad) > '9' && iBad > 0) iBad--;
			aQuote.vol=dataIntF.format(Double.parseDouble(tagData.substring(0, iBad+1))*10000);
		}
		if (tempS[3].indexOf("DOWN") > 0) aQuote.change="N";
		else aQuote.change="";
		tagData=removeHtmTags(tempS[3]);
		if (tagData != null) aQuote.change += tagData; 
		aQuote.bid="0"; 
		aQuote.bidSize="0"; 
		aQuote.ask="0"; 
		aQuote.askSize="0";
		aQuote.time=""; 
		return aQuote;
	}

	@Override
	public Vector<MktQuote> parsePageMultiple(String pageSrc)
	{
	if (pageSrc == null) return null;
	String[] mkts=pageSrc.split("</TABLE>");
		if (mkts.length < 2) return null;
	Vector<MktQuote> mktV=new Vector<MktQuote>();
	Calendar today=Calendar.getInstance();
	int thisHour= today.get(Calendar.HOUR_OF_DAY);
	int thisMin=today.get(Calendar.MINUTE);
		for (int i=0; i< mkts.length-1; i++)
		{
			MktQuote aMkt=	parsePage(mkts[i]);
			if (aMkt != null) 
			{
				aMkt.time=""+thisHour+":"+thisMin;
				mktV.add(aMkt);
			}
		}
		if (mktV.size() > 0)
		return mktV;
		return null;
	}

	String removeHtmTags(String aLine)
	{
	String[] tags=aLine.split("<");
		for (int i=0; i< tags.length; i++)
		{
			int iRt=tags[i].indexOf(">");
			if (iRt < 0) return tags[i];
			if (++iRt == tags[i].length()) continue;
			String found1=tags[i].substring(iRt);
			String return1=found1.trim();
			if (return1.length() > 0) return return1;
		}
		return null;
	}
}
/*
 * YhTaiPageParser.java
 * 
{ "id": "304466804484872" ,"t" : "GOOG" ,"e" : "NASDAQ" ,
"l" : "708.49" ,"l_fix" : "708.49" ,"l_cur" : "708.49" ,
"s": "1" ,"ltt":"4:00PM EDT" ,"lt" : "Oct 27, 4:00PM EDT" ,
"lt_dts" : "2015-10-27T16:00:00Z" ,"c" : "-4.29" ,
"c_fix" : "-4.29" ,"cp" : "-0.60" ,"cp_fix" : "-0.60" ,
"ccol" : "chr" ,"pcls_fix" : "712.78" ,"el": "713.97" ,
"el_fix": "713.97" ,"el_cur": "713.97" ,"elt" : "Oct 28, 8:00AM EDT" ,
"ec" : "+5.48" ,"ec_fix" : "5.48" ,"ecp" : "0.77" ,"ecp_fix" : "0.77" ,
"eccol" : "chg" ,"div" : "" ,"yld" : "" } ]


*/