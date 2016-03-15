import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Vector;


public class YhTaiPageParser extends WebPageParser	
{
private static DecimalFormat dataIntF=new DecimalFormat("00");
private final static Calendar today=Calendar.getInstance();
private static int thisYear= today.get(Calendar.YEAR);
private static int thisMonth=today.get(Calendar.MONTH)+1;
private static int thisDay=today.get(Calendar.DAY_OF_MONTH);
private static int todayInt=10000*(thisYear % 1000)+100*thisMonth+thisDay;

	public YhTaiPageParser(String myName)
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

	static DecimalFormat dI=new DecimalFormat("00");
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
				aMkt.time=dI.format(thisHour)+":"+dI.format(thisMin);
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
<tbody> 
<tr><th><a href="http://tw.stock.yahoo.com/s/tse.php">�W��</a></th><td class="dx">8558.91</td><td class="im"><span class="downi"><i>�^</i></span></td><td class="chg down">16.00</td><td class="vol"><span>803.69��</span></td></tr> </tbody> 
 </table> 
 </div> 
 <div class="colorline"> 
 <table class="chartline e"> 
 <tbody> 
<tr><th><a href="http://tw.stock.yahoo.com/s/otc.php">�W�d</a></th><td class="dx">129.26</td><td class="im"><span class="downi"><i>�^</i></span></td><td class="chg down">0.17</td><td class="vol"><span>96.36��</span></td></tr> </tbody> 
 </table> 
 </div> 
 <div class="colorline"> 
 <table class="chartline o"> 
 <tbody> 
<tr><th><a href="http://tw.stock.yahoo.com/s/stock_cate.php?cat_id=%2523020">�q�l</a></th><td class="dx">300.58</td><td class="im"><span class="downi"><i>�^</i></span></td><td class="chg down">2.36</td><td class="vol"><span>401.90��</span></td></tr> </tbody> 
 </table> 
 </div> 
 <div class="colorline"> 
 <table class="chartline e last"> 
 <tbody> 
<tr><th><a href="http://tw.stock.yahoo.com/s/stock_cate.php?cat_id=%2523010">����</a></th><td class="dx">1042.15</td><td class="im"><span class="upi"><i>��</i></span></td><td class="chg up">9.42</td><td class="vol"><span>74.29��</span></td></tr> </tbody> 
 </table> 
 </div> 
 </div>
<div class="tbds"><label class="bigimg"> 

*/