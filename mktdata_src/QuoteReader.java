import java.net.*;
import java.io.*;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;
import java.io.File;
import java.text.DecimalFormat;
import java.lang.Object;
import java.util.TreeMap;
import java.lang.Thread;
import java.util.concurrent.*;

public class QuoteReader extends Thread //read one quote and create the chart for html all day long
{
	private final static Calendar today=Calendar.getInstance();
	private static int thisYear= today.get(Calendar.YEAR);
	private static int thisMonth=today.get(Calendar.MONTH)+1;
	private static int thisDay=today.get(Calendar.DAY_OF_MONTH);
	private static int todayInt=10000*(thisYear % 1000)+100*thisMonth+thisDay;
	private static BufferedWriter deBugLog=null;
	private static DecimalFormat dataF=new DecimalFormat("##.00");
	private static DecimalFormat dateIntF=new DecimalFormat("000000");
	private static int threadID=0;
	private static int runningThread=0;
	//private ExecutorService pool;

	protected static boolean wait4Log=false;
	protected static boolean wait4Count=false;
	private static Object lockObj=new Object();
	private static boolean iAmDone=false;
	static String fileDate=null;
	static String runningDate=dataF.format(thisMonth+thisDay*0.01);

	protected String urlRootString="http://app.quotemedia.com/quotetools/jsVarsQuotesSpan.go?webmasterId=100804&df=H:mm&symbol=";;
	protected String pgStartFrom="new Object";
	protected String pgEndWith="marketCap";

boolean zippedPage;
	String logString;
	protected String urlString;
	protected int samplingPeriod; //in sec
	protected long exitTime;//=(new Date()).getTime()+10*3600; //in mili-sec
	protected String mySymbol;
	protected String YhSymb;
	protected Vector<String> myQuotes;
	
	WebPageParser myPageParser;

	TreeMap<String, Vector<String> > multipleQuotes; //added for MULTI_QUOTE_PARSER
	Vector<String> quoteSymbs; //added for MULTI_QUOTE_PARSER
	//int dataDate=0;
	int myID=0;
	boolean got1stData;
	//QuoteCharter myCharter;
	String mktStartTime;
	int failAndTry;
	long recessTmBegin; //0 for no recess,
	long recessTmEnd;

	String dataDir;

	public static void stopAllThreads()
	{
		iAmDone=true;
	}

	public static void increaseThreadID()
	{
		threadID++;
	}
	
	public static int getRunningThreadCount()
	{
		return runningThread;
	}

	public static String getTodayYYMMDD()
	{
		return dateIntF.format(todayInt);
	}
	public void setExitTime(long newTm)
	{
		exitTime=newTm;
	}

	public void resetUrl(String toThis)
	{
		urlString=toThis;
	}


public QuoteReader(String forThisSymb, String fromURL, String pgStart, String pgEnd)
{
	mySymbol=new String(forThisSymb);
	YhSymb=null;
	myQuotes=new Vector<String>();
	quoteSymbs=new Vector<String>(); //added for MULTI_QUOTE_PARSER
	if (mySymbol.charAt(0) != '*') quoteSymbs.add(mySymbol);
	if (fromURL != null)
	urlString = new String(fromURL);
	if (forThisSymb.charAt(0) != '*')
	urlString += forThisSymb;
	if (pgStart != null)
	pgStartFrom = new String(pgStart);
	if (pgEnd != null)
	pgEndWith = new String(pgEnd);
	samplingPeriod=60;
	myID=threadID;
	got1stData=false;
	failAndTry=5;
	recessTmBegin=0;
	exitTime=(new Date()).getTime()+10*3600*1000;
	
//pool=null;
zippedPage=false;
dataDir="";
}

public QuoteReader(String forThisSymb, String fromURL, String pgStart, String pgEnd, Vector<String> continueQ)
{
	mySymbol=new String(forThisSymb);
	YhSymb=null;
	quoteSymbs=new Vector<String>(); //added for MULTI_QUOTE_PARSER
	if (mySymbol.charAt(0) != '*') quoteSymbs.add(mySymbol);
	if (continueQ != null)
	myQuotes=continueQ;
	if (fromURL != null)
	urlString = new String(fromURL);
	if (forThisSymb.charAt(0) != '*')
	urlString += forThisSymb;
	if (pgStart != null)
	pgStartFrom = new String(pgStart);
	if (pgEnd != null)
	pgEndWith = new String(pgEnd);
	samplingPeriod=60;
	myID=threadID;
	failAndTry=5;
	recessTmBegin=0;
	got1stData=false;
	exitTime=(new Date()).getTime()+10*3600*1000;
	
//pool=null;
zippedPage=false;
dataDir="";
}

public void setPool4Multiple(int poolSize)
{
//pool = Executors.newFixedThreadPool(poolSize);
}

public void setYhSymb(String str)
{
YhSymb=str;
}
public void setMktStartTime(String stm)
{
mktStartTime=stm;
}

public void setRecessPeriod(long tmBegin, long tmEnd)
{
recessTmBegin=tmBegin;
recessTmEnd=tmEnd;
}

public void setDataDir(String dir)
{
dataDir=dir+"/";
}

public void addParserSymbol(String symb)
{
	if (quoteSymbs == null)
		quoteSymbs=new Vector<String>();
	quoteSymbs.add(symb);
	if (multipleQuotes == null) multipleQuotes=new TreeMap<String, Vector<String> >();
	Vector<String> newV=new Vector<String>();
	multipleQuotes.put(symb, newV);
}

public void buildParserSymbol() //special for yahoo personal page
{
//System.out.println("I am here ::: Building Sysmbol lists");
	if (quoteSymbs == null)
		quoteSymbs=new Vector<String>();
	String readPage=readData(urlString, pgStartFrom, pgEndWith);
	if (readPage== null)
	{
			showMe("No data for "+urlString);
			return;
	}
	Vector<MktQuote> gotData=myPageParser.parsePageMultiple(readPage);
//System.out.println("I am here :::"+readPage);
	if (gotData == null)
	{
			System.out.println("Got no Data for "+myPageParser.name);
			return;
	}
	else
	{		
		for (int i=0; i<gotData.size(); i++)
		{
			MktQuote thisQuote=gotData.get(i);
			quoteSymbs.add(thisQuote.symbol);
	
	if (multipleQuotes == null) multipleQuotes=new TreeMap<String, Vector<String> >();
			Vector<String> newV=new Vector<String>();
			multipleQuotes.put(thisQuote.symbol, newV);
		}
	}
}

public void setPageParser(WebPageParser newParser)
{
myPageParser=newParser;
	if (myPageParser.MULTI_QUOTE_PARSER) 
	{
		if (multipleQuotes == null)
		multipleQuotes=new TreeMap<String, Vector<String> >();
		if (quoteSymbs == null)
		quoteSymbs=new Vector<String>();
	}
}

public void resetSamplingPeriod(int toNew)
{
	if (toNew > 7)
	samplingPeriod=toNew;
}

void putInLogN(String aLine)
{
if (logString == null) logString=new String(aLine+"-n-");
	else
	logString += new String(aLine+"-n-");
}

void putInLog(String aLine)
{
if (logString == null) logString=new String(aLine);
	else
	logString += new String(aLine);
}

synchronized void updateLog()
{
	if (logString == null || logString.length() < 1)
	return; 
	while (wait4Log)
	{
		try{sleep(10);}catch(InterruptedException e){
			if (iAmDone) return; }
	}
	wait4Log=true;
	{
		String[] manyLines=logString.split("-n-");
		try {
			for (int i=0; i<manyLines.length; i++)
			{deBugLog.write(manyLines[i]); deBugLog.newLine();}
		} catch (IOException e){}
	}
	wait4Log=false;
	//notifyAll();
	return;
}

int convertDate2Int(String testDate)
{
//System.out.println("testDate="+testDate);

	if (testDate.indexOf("AM") > 0 || testDate.indexOf("PM") > 0)
	return todayInt;
	String[] dateS=testDate.split("/");
	if (dateS.length < 3) return 0;
	int mm=Integer.parseInt(dateS[0]);
	int dd=Integer.parseInt(dateS[1]);
	int yy=Integer.parseInt(dateS[2]) % 1000;
	
	return 10000*yy+100*mm+dd;
}

String removeComma(String aNum)
{
String[] splitC=aNum.split(",");
String new1=new String("");
for (int i=0; i<splitC.length; i++)
	new1 += splitC[i];
	return new1;
}

void toReadZippedPage(boolean Y_N)
{
zippedPage=Y_N;
}

public String readData(String url, String startFrom, String endWith)
{
	WebPageJob aUrl=null;
	aUrl=new WebPageJob(url);
if (zippedPage) aUrl.setZipFlag(true);
	if (aUrl.myInStream != null &&
				aUrl.getPageSrc(startFrom, endWith) && aUrl.totalRead > 0)
	return aUrl.readPage.replaceAll("&NBSP;", " ");
	
		System.out.println("!!!Failed in "+url);
		zippedPage=aUrl.getZipFlag();
	
	if (aUrl.myInStream == null)
		System.out.println("Cannot open data stream");
/*
	else
	{
		if (aUrl.totalRead > 0)
		{
		System.out.println("Found Data("+aUrl.totalRead+"):==>");
			if (aUrl.readPage != null)
		System.out.println(aUrl.readPage.replaceAll("[^\\x20-\\xFE]", "@"));
		System.out.println("!!!Failed to find "+startFrom+" or "+endWith);
		}
	}
*/
	return null;
}

synchronized void showMe(String Msg1)
{
	while (wait4Log)
	{
		try{sleep(5);}catch(InterruptedException e){
			if (iAmDone) return; 
			}
	}
	wait4Log=true;
	{
		try {
		deBugLog=new BufferedWriter(new FileWriter("processLog"+dateIntF.format(todayInt)+".txt",true));
		deBugLog.write(Msg1);
		deBugLog.newLine();
		deBugLog.close();
		} catch (IOException e){}
		
		//System.out.println(Msg1);System.out.println(Msg2);
	}
	wait4Log=false;
	return;
}

public void setFileDate(String YYMMDD)
{
fileDate=YYMMDD;
}

protected void checkFileToUpdateData()
{

String fileName=dataDir+mySymbol+"/"+mySymbol+(fileDate!=null?fileDate:getTodayYYMMDD())+"Quote.txt";
System.out.println("Checking "+fileName);		
	File todayF=new File(fileName);
System.out.println("Checking file : "+fileName+" has length "+todayF.length());		
	if (todayF.exists())
	{
		try{
	BufferedReader oldData=new BufferedReader(new FileReader(fileName));
	String lastPrice="0.0";
	boolean foundData=false;
	String aLine;
		while ((aLine = oldData.readLine()) != null)
		{

			String[] tokens=aLine.replace("24:", "12:").split("-");
			if (tokens.length < 5) continue;
			lastPrice=tokens[3];
			int iC=tokens[2].indexOf(":");
			if (iC < 0) continue;
try {
			int iH=Integer.parseInt(tokens[2].substring(0, iC));
			if (iH < 9 || iH > 15)
			{
			 continue;
			}
} catch (NumberFormatException e){System.out.println("Bad time data:"+tokens[2]);
		continue;}

			if (!foundData)
			{
				myQuotes.add(new String(mySymbol+"-"+runningDate+"-"+mktStartTime+"-"+lastPrice+"-0"));
				foundData=true;	
				got1stData=true;
				//if (mySymbol.compareTo("AAPL") == 0)
				{				
				//System.out.println("first data:"+aLine);
				}
			}
			myQuotes.add(aLine);						
		}
		oldData.close();		
		}catch (IOException e){  }
	}
System.out.println(mySymbol+"Found "+myQuotes.size()+" data in file");
	if (myQuotes.size() > 3)
	{
		QuoteCharter aCharter=new QuoteCharter(myQuotes, dataDir);
		aCharter.setMktStartTime(mktStartTime);
		if (YhSymb!=null) aCharter.setYhSymb(YhSymb);
		//aCharter.setTestMode(true);
		//if (myPageParser.MULTI_QUOTE_PARSER && pool != null) pool.execute(aCharter);
		//else
		//new Thread(aCharter).start();
		Thread t1=new Thread(aCharter);
			t1.start();
			try{
			t1.join();
			} catch (InterruptedException e){}
	}

}
HashMap<String, Integer> volMax;
public void setVolMax(HashMap<String, Integer> volMax1){
	volMax=volMax1;
}

void handle1Quote(MktQuote thisQuote, Vector<String> thisQV, boolean isMultiple)
{
String priceChange=null;
double lastPrice=0;
	
	if (thisQV.size() < 1)
	{
//System.out.println("working on "+thisQuote.toString());
		if (thisQuote.change != null)
		priceChange=thisQuote.change.replace('N','-');
			if (priceChange==null) priceChange="0.0";
		
		try {
		lastPrice=Double.parseDouble(thisQuote.price)-Double.parseDouble(priceChange);
		} catch (NumberFormatException e) {
			System.out.println("Bad data "+thisQuote.toString()+" <"+priceChange+">");
		}
		
		MktQuote vQuote=new MktQuote(thisQuote);
			vQuote.vol="0";
			vQuote.price=dataF.format(lastPrice);
			vQuote.time=mktStartTime;
			thisQV.add(vQuote.toString());
			got1stData=true;	
	}
	else
	{
		Integer maxV=volMax.get(thisQuote.symbol);
		if (maxV != null){
			String[] flds=thisQV.get(thisQV.size()-1).split("-");
			int lastV=-Integer.parseInt(flds[5-1]);
			lastV += Integer.parseInt(thisQuote.vol);
			int timeDelta=Integer.parseInt(thisQuote.time.substring(0, 2))*60 - Integer.parseInt(flds[3-1].substring(0, 2))*60+
					Integer.parseInt(thisQuote.time.substring(3)) - Integer.parseInt(flds[3-1].substring(3));
			if (lastV  < 0) return;		
				if (timeDelta > 0 && lastV > maxV*timeDelta) return;
		}
	}
System.out.println("I'm "+myID+" got "+thisQuote.toString());

	thisQV.add(thisQuote.toString());
	//showMe(msg1);
	if (thisQV.size() > 3)
	{
		QuoteCharter aCharter=new QuoteCharter(thisQV, thisQuote, dataDir);
			aCharter.setMktStartTime(mktStartTime);
			if (YhSymb!=null) aCharter.setYhSymb(YhSymb);
			else aCharter.setYhSymb(thisQuote.symbol);
		//if (isMultiple && pool != null) pool.execute(aCharter);
		//else
		{
			Thread t1=new Thread(aCharter);
			t1.start();
//System.out.println("start to draw at "+(new Date()).getTime());
			//try{
			//t1.join();
			//} catch (InterruptedException e){}
//System.out.println("start to draw end "+(new Date()).getTime());

		}
	}
	SaveQuote2File aSaver=new SaveQuote2File(thisQuote, dataDir);
	aSaver.start();

}

protected void myJobs() 
{
boolean endOfDay=false;
	if (mySymbol.charAt(0) != '*')
	checkFileToUpdateData();
	else 
	{
		for (int i=0; i<quoteSymbs.size(); i++)
		{
			mySymbol=quoteSymbs.get(i);
			myQuotes=multipleQuotes.get(mySymbol);
			checkFileToUpdateData();
			got1stData=false;
			if (myQuotes.size() > 0)
			got1stData=true;				
		}
		mySymbol = "*";
	}

//System.out.println("I'm "+myID+" working on "+mySymbol);
	
	MktQuote thisQuote;
	while (!endOfDay)
	{
	long timeNow=(new Date()).getTime();
	long nextSamplingTime=timeNow+samplingPeriod*1000/4;
	String readPage=readData(urlString, pgStartFrom, pgEndWith);
	boolean isMultiple=myPageParser.MULTI_QUOTE_PARSER;
		if (readPage== null)
		{
			showMe("No data for "+urlString);
			failAndTry--;
			if (failAndTry < 0)
			return;
		}
		//String msg1=new String("I'm "+myID+" on "+mySymbol);
		else if (myPageParser.MULTI_QUOTE_PARSER)
		{
		Vector<MktQuote> gotData=myPageParser.parsePageMultiple(readPage);
		
			if (gotData == null)
			{
				System.out.println("Got no Data for "+myPageParser.name);
				failAndTry--;
				if (failAndTry < 0)
				return;
			}
			else
			{
			failAndTry=3;
		
				for (int i=0; i<gotData.size(); i++)
				{
					thisQuote=gotData.get(i);	
					Vector<String> aV=multipleQuotes.get(thisQuote.symbol);
					if (aV == null) 
					{
						//System.out.println("Got no Data Vector for "+thisQuote.symbol);
						aV=new Vector<String>();
						multipleQuotes.put(thisQuote.symbol, aV);
					}
					if (aV != null) { handle1Quote(thisQuote, aV, isMultiple);}
				}
			}
		}
		else
		{
			thisQuote=myPageParser.parsePage(readPage);
			if (thisQuote.time.indexOf("CLOSED") > 0) return;
			handle1Quote(thisQuote, myQuotes, isMultiple);
			failAndTry=3;
		
		}
		timeNow=(new Date()).getTime();
		if (timeNow > exitTime) 
		{
		//System.out.println("Time "+timeNow+"passed; day ends at "+exitTime);
			return;
		}
		if (recessTmBegin > 0)
		{
			if (timeNow > recessTmBegin &&
				timeNow < recessTmEnd)
			nextSamplingTime=recessTmEnd;
		}
		try {
		long sleepTime=nextSamplingTime-timeNow;
		//System.out.println(" sleep "+sleepTime+">>"+msg1);
			if (sleepTime > 0)
			sleep(sleepTime);
                    } catch (InterruptedException e){
			if (iAmDone) return; 
			}
	}
//if (pool != null)
//cleanPool();
System.out.println("Done for the day");
	return;
}


void updateCount()
{
	while (wait4Count)
	{
		try{sleep(50);}catch(InterruptedException e){
			if (iAmDone) return; 
			}
	}
	
	synchronized (lockObj) {
		wait4Count=true;
		{
			runningThread--;
System.out.println(mySymbol+" is done!("+runningThread+")");
		}
		wait4Count=false;
	}
	return;
}

public void run()
{
	runningThread++;
	myJobs();
	updateCount();
	return;
}

public static boolean testInet() {
boolean successful=false;
    Socket sock = new Socket();
    InetSocketAddress addr = new InetSocketAddress("google.com",80);
    try {
        sock.connect(addr,3000);
	successful=true;
    } catch (IOException e) {
    } finally {}
        try {sock.close();}
        catch (IOException e) {}
        return successful;
}

public static void main(String[] args) 
{


		
}		
}
