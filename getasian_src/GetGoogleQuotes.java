import java.awt.Toolkit;import java.text.DecimalFormat;import java.util.Calendar;import java.util.Date;import java.util.TimeZone;

public class GetGoogleQuotes //read multiple mkts quote and create the chart for html all day long
{
static DecimalFormat IntF=new DecimalFormat("00");
	
public static void main(String[] args) 
{
String myTimeZoneID=TimeZone.getDefault().getID();
TimeZone.setDefault(TimeZone.getTimeZone("Hongkong"));
Calendar aTime=Calendar.getInstance();
	int iHr=aTime.get(Calendar.HOUR_OF_DAY);
	int iMin=aTime.get(Calendar.MINUTE);
	//int iSec=aTime.get(Calendar.SECOND);
int iNowInMins=iHr*60+iMin;
long timeNow=(new Date()).getTime();
int eodHr=13, eodMin=35; //end of day hour, min
if (args.length > 1)
{

String[] HHMM=args[0].split(":");
	eodHr=Integer.parseInt(HHMM[0]);
	if (HHMM.length > 1)
	eodMin=Integer.parseInt(HHMM[1]);
}

long timeEnd=timeNow+(((eodHr-iHr)*3600+(eodMin-iMin)*60)*1000);

//System.out.println("Program current time "+iHr+":"+iMin);
if (args.length < 1 || args[0].compareTo("test") != 0 ) //if not for test
{
	if (iHr*60+iMin > 13*60+35) 
	{
		TimeZone.setDefault(TimeZone.getTimeZone(myTimeZoneID));
		System.exit(0);
	}
	if (iHr*60+iMin < 9*60+30)
	{
		try{
			int mins=(9 - iHr)*60+(0-iMin);
			for (int i=0; i< mins; i++)
			{
		long sleeptime=60*1000;
			System.out.print("\rcurrent time "+IntF.format(iNowInMins/60)+":"+IntF.format(iNowInMins % 60));
			System.out.print(" wait for another "+(mins-i)+" mins to start.....");
		if (mins-i < 10)
		{
			if (!QuoteReader.testInet())
			{
		System.out.println("!!!--   No INTERNET CONNECT  ---!!!!");
			}
				Toolkit.getDefaultToolkit().beep();
				Toolkit.getDefaultToolkit().beep();
				Toolkit.getDefaultToolkit().beep();
		}
			Thread.sleep(sleeptime);
			iNowInMins++;
			}
		} catch (InterruptedException e){}
	}
	
}

System.out.println("Program starts at "+timeNow/1000);
System.out.println("Program should end at "+timeEnd/1000);	
	
WebPageJob.slowSite=true;
WebPageJob.socketWaitTime=20*1000;
	
String myQuotesUrl = "https://tw.finance.yahoo.com";//"https://tw.stock.yahoo.com";
	String pgStartRead="chartline";
	String pgEnd="bigimg";		while (timeNow < timeEnd) {
	QuoteReader aReader=new QuoteReader("*", myQuotesUrl, pgStartRead, pgEnd);
			YhTaiPageParser aParser=new YhTaiPageParser("TaiwanYahoo");
			aReader.setPageParser(aParser);
			aReader.addParserSymbol("TSE");
			aReader.addParserSymbol("OTC");
			aReader.addParserSymbol("ELEC");
			aReader.addParserSymbol("FIN");
			
	QuoteReader.increaseThreadID();
	aReader.setMktStartTime("08:59");			
	aReader.setExitTime(timeEnd);				aReader.resetSamplingPeriod(30);
	aReader.start();
	try{
		aReader.join();
	} catch (InterruptedException e){}
		QuoteReader.stopAllThreads();
		if (aReader.isAlive()) aReader.interrupt();
	

timeNow=(new Date()).getTime();		
if (timeNow < timeEnd)
{
Toolkit beeper=Toolkit.getDefaultToolkit();
beeper.beep();beeper.beep();beeper.beep();
}
	}
System.out.println("Program ends at "+timeNow/1000);
TimeZone.setDefault(TimeZone.getTimeZone(myTimeZoneID));
System.exit(0);
}		
}
