import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.Vector;

import static java.nio.file.StandardCopyOption.*;

public class Lottery539PageParser extends WebPageParser	
{
private static DecimalFormat dataIntF=new DecimalFormat("00");
private final static Calendar today=Calendar.getInstance();
private static int thisYear= today.get(Calendar.YEAR);
private static int thisMonth=today.get(Calendar.MONTH)+1;
private static int thisDay=today.get(Calendar.DAY_OF_MONTH);
private static int todayInt=10000*(thisYear % 1000)+100*thisMonth+thisDay;
static String dataCenter="C:\\Users\\eric\\projects\\datacenter\\";
private static int tomorrowWkDay=0;

	public Lottery539PageParser(String myName)
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
			if (tags[i].length()==0) continue;
			int iRt=tags[i].indexOf(">");
			if (iRt < 0) return tags[i];
			if (++iRt == tags[i].length()) continue;
			String found1=tags[i].substring(iRt);
			String return1=found1.trim();
			if (return1.length() > 0) return return1;
		}
		return null;
	}


public String readData(String url, String startFrom, String endWith)
{
	WebPageJob aUrl=null;
	aUrl=new WebPageJob(url);
//if (zippedPage) aUrl.setZipFlag(true);
	if (aUrl.myInStream != null &&
				aUrl.getPageSrc(startFrom, endWith) && aUrl.totalRead > 0)
	return aUrl.readPage.replaceAll("&NBSP;", " ");
	
		System.out.println("!!!Failed in "+url);
		//zippedPage=aUrl.getZipFlag();
	
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

public class DrawInfor{
	String date;
	int wkDay; //1-6
	int[] data;
	public DrawInfor(String date1, int wkDay1, int[] dataIn){
		date=date1;
		wkDay=wkDay1;
		data=Arrays.copyOf(dataIn, dataIn.length);
	}
	
	
	public String toString()
	{
		String dataLine="";
		for (int i=0; i<data.length; i++){
			dataLine += "-"+data[i];
		}
		//dataLine += ""+data[data.length-1];
		return date+"("+wkDay+")"+dataLine;
	}
}

DrawInfor getOneDrawInfo(String date1, int wkDay1, int[] dataIn){
	return new DrawInfor(date1, wkDay1, dataIn);
}

static int[] getDrawData(String dataLine){
	int[] datas=new int[5];
	int iNo=0;
	int ix0=0;
	while (ix0<dataLine.length()-1){
		int i9=ix0+1;
		while (i9<dataLine.length() && dataLine.charAt(i9) >='0' && dataLine.charAt(i9)<='9') i9++;
		datas[iNo++]=Integer.parseInt(dataLine.substring(ix0, i9));
		if (iNo ==5) break;
		ix0=i9;
		while (dataLine.charAt(ix0)<'0' || dataLine.charAt(ix0) >'9') ix0++;
	}
	return datas;
}
public static void readFromWeb() 
{
String baesUrl="http://www.pilio.idv.tw/lto539/list.asp?indexpage=";
String startFrom="539.GIF";//開獎日期";
String endWith = "/table";
//Calendar calendar0=Calendar.getInstance();

Vector<DrawInfor> allLines=new Vector<DrawInfor>();
	for (int iPage=1; iPage<52; iPage++){
		Lottery539PageParser aParser=new Lottery539PageParser("539");
		String readData=aParser.readData(baesUrl+iPage, startFrom, endWith);
		String[] daily=readData.split("<TR>");
		for (int id=2; id<daily.length; id++){
			String[] cols=daily[id].split("<TD ");
			if (cols.length < 5) continue;
			String date1=aParser.removeHtmTags(cols[2]);
			Calendar calendar=//Calendar.getInstance();
					new GregorianCalendar(
					//calendar.set(
							Integer.parseInt(date1.substring(0, 4)),//+1911,
							Integer.parseInt(date1.substring(5, 7))-1,
							Integer.parseInt(date1.substring(8, 10))-1);
					//calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); 
					calendar.setFirstDayOfWeek(GregorianCalendar.SUNDAY);
					int wkDay=(calendar.get(Calendar.DAY_OF_WEEK));//+4) % 7;					
			String data=aParser.removeHtmTags(cols[3]);
			int[] datas=getDrawData(data);
			allLines.add(aParser.getOneDrawInfo(date1, wkDay, datas));			
		}
	}
	BufferedWriter aWriter;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("History.txt")));
		for (int i=0; i<allLines.size(); i++){
			aWriter.write(allLines.get(i).toString());aWriter.newLine();
		}
		aWriter.close();
	} catch (IOException e){
	System.out.println("Bad I/O");
}
}

static void setNextOnDay(BallStatistics aCase){
	if (aCase.currentIdleDays > aCase.maxIdle) {
		aCase.nextOnDay=0;
		return;
	}
	int iCase=0;
	double mean=0;
	for (int id=aCase.currentIdleDays; id <= aCase.maxIdle; id++){
		iCase += aCase.idleDistribution[id];
		mean += id * aCase.idleDistribution[id];
	}
	mean /= iCase;
	int iMean=(int)Math.round(mean);
	int iLess=aCase.maxIdle - aCase.currentIdleDays+1; 
	double c80=iCase*0.8;
	double c75=iCase*0.75;
	double c66=iCase*0.66;
	double iTt=0;//aCase.idleDistribution[iMean];
	int ix=-1;
	while (iTt < c66){
		ix++;
		iTt += aCase.idleDistribution[aCase.currentIdleDays+ix];
		//iTt += aCase.idleDistribution[iMean-ix];
	}
	aCase.predict66=ix;
	while (iTt < c75){
		ix++;
		iTt += aCase.idleDistribution[aCase.currentIdleDays+ix];
		//iTt += aCase.idleDistribution[iMean-ix];
	}
	aCase.predict75=ix;//iMean+ix-aCase.currentIdleDays;
	while (iTt < c80){
		ix++;
		iTt += aCase.idleDistribution[aCase.currentIdleDays+ix];
		//iTt += aCase.idleDistribution[iMean-ix];
	}
	aCase.predict80=ix;//iMean-ix-aCase.currentIdleDays;
	/*
	double r=0;
	for (int id=aCase.currentIdleDays; id <= aCase.maxIdle; id++){
		//iCase += aCase.idleDistribution[id];
		r += (id - mean)*(id-mean)* aCase.idleDistribution[id];
	}
	r /= iCase;
	aCase.nextOnVar=Math.sqrt(r);
	*/
	mean -= aCase.currentIdleDays;
	aCase.nextOnDay=(int)Math.floor(mean);
	return;
}

static BallStatistics[] drawnSet=new BallStatistics[40];
static int[][] monthlyStatistics=new int[40][13];
static int[][] weeklyStatistics=new int[40][7];
static DecimalFormat dF=new DecimalFormat("0.00");
//static DecimalFormat dI=new DecimalFormat("00");
static final int sampleCounts=39*39;
static int predictionWeekDay=0;
static String yesterdayDrawDate="";
static String getLatestDrawDate(){
	File history=new File(dataCenter+"History.txt");
	if (!history.exists()) readFromWeb();
	BufferedReader aReader=null;
	String date1=null;
	try {
		aReader=new BufferedReader(new FileReader(history));
		String aLine;
		while ((aLine=aReader.readLine()) != null){
			if (aLine.length()<5)continue;
			int i0=0;
			while (aLine.charAt(i0)<'0') i0++;
			//int ix=aLine.indexOf('(');
			date1=aLine.substring(i0, i0+10);
			predictionWeekDay=Integer.parseInt(aLine.substring(i0+11, i0+12));
			predictionWeekDay++;
			if (predictionWeekDay==7) predictionWeekDay=1;
			break;
		}
		aReader.close();		
	} catch (FileNotFoundException e){
	System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
		return null;
	}
	yesterdayDrawDate=date1;
	return date1;
}
static Vector<int[]> readHistory(){
	Vector<int[]> allLines=new Vector<int[]>();
	//Lottery539PageParser aParser=new Lottery539PageParser("539");
	File history=new File(dataCenter+"History.txt");
	if (!history.exists()) readFromWeb();
	BufferedReader aReader=null;
	
	try {
		aReader=new BufferedReader(new FileReader(history));
		String aLine;
		while ((aLine=aReader.readLine()) != null){
			if (aLine.length()<5)continue;
			//int ix=aLine.indexOf('(');
			//String date1=aLine.substring(0,  ix);
			int i9=aLine.indexOf(')');
			//int wkDay=Integer.parseInt(aLine.substring(ix+1, i9));
			int[] datas=getDrawData(aLine.substring(i9+2));
			allLines.add(datas);
		}
		aReader.close();		
	} catch (FileNotFoundException e){
	System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
		return null;
	}
	return allLines;
	
}
static void getAllBallStatistics(Vector<int[]> dataSource, BallStatistics[] ballSet, int checkRange)
{
	//dataSource must be ascending to be meaningful
	for (int i=1; i<40; i++){
		ballSet[i]=new BallStatistics();
		ballSet[i].checkRange=checkRange;
		ballSet[i].drawnTimeSeries=new int[checkRange];
		Arrays.fill(ballSet[i].drawnTimeSeries, 0);
		ballSet[i].idleDistribution=new int[checkRange];
		Arrays.fill(ballSet[i].idleDistribution, 0);
		ballSet[i].idleIntervalCount=0;
		ballSet[i].maxIdle=0;
		ballSet[i].minIdle=checkRange;
		ballSet[i].idleMean=0;
		ballSet[i].idleVariant=0;
		ballSet[i].currentIdleDays=-1;
	}
	int[] lastOnAt=new int[40];
	Arrays.fill(lastOnAt, -1);
	int[] lastShownAt=new int[40];
	Arrays.fill(lastShownAt, 0);
	for (int i=0; i<checkRange; i++){
		int[] hits=dataSource.get(i);
		//int month=Integer.parseInt(aDraw.date.substring(5,7));
		//if (month != currentMonth) finishAddCurrentMonth=true;
		for (int ik=0; ik<hits.length; ik++){
			ballSet[hits[ik]].drawnTimeSeries[i]=1;
			if (ballSet[hits[ik]].currentIdleDays == -1) ballSet[hits[ik]].currentIdleDays=i;
			if (lastOnAt[hits[ik]] < 0 && i >0)
				{ 
				ballSet[hits[ik]].idleDistribution[i]++;
				ballSet[hits[ik]].maxIdle = i;
				ballSet[hits[ik]].minIdle = i;
					//drawnSet[hits[ik]].idleIntervalCount++;
				}
			if (lastOnAt[hits[ik]] > 0){
				int iId = i - lastOnAt[hits[ik]]-1;
				if (iId > 0) ballSet[hits[ik]].idleDistribution[iId]++;
				if (iId > ballSet[hits[ik]].maxIdle) 				
				{
					ballSet[hits[ik]].maxIdle = iId;
					ballSet[hits[ik]].maxIdleAt=i;
				}
				if (iId < ballSet[hits[ik]].minIdle) 				
					ballSet[hits[ik]].minIdle = iId;
				//drawnSet[hits[ik]].idleIntervalCount++;
			}
			lastOnAt[hits[ik]]=i;
			/*
			monthlyStatistics[hits[ik]][month]++;
			if (!finishAddCurrentMonth){				
				currentMonthDraw[hits[ik]]++;
			}
			weeklyStatistics[hits[ik]][aDraw.wkDay]++;
			if (lastShownAt[hits[ik]]==0) lastShownAt[hits[ik]]=i+1;
			*/
		}
	}
	
}
public static int getNextWeekDay(){
	File history=new File(dataCenter+"History.txt");
	int iWeek=0;
	BufferedReader aReader=null;
	try {
		aReader=new BufferedReader(new FileReader(history));
		String aLine;
		int iL=0;
		int iR=0;
		while ((aLine=aReader.readLine()) != null){
			iL=aLine.indexOf('(');
			iR=aLine.indexOf(')');
			if (aLine.length()<5 && iL==10 &&
					iR==12)
				break;
		}
		iWeek=Integer.parseInt(aLine.substring(iL+1, iR));
		aReader.close();		
	} catch (FileNotFoundException e){
	System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
	}
	iWeek++;
	return iWeek==7?1:iWeek;	
}

public static void appendsPrediction(ArrayList<String> predictionAndStatistics){
	Path storage=FileSystems.getDefault().getPath("todayPrediction"+getNextWeekDay()+".txt");
	try {
		Files.createFile(storage);		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	try {
		Files.write(storage, predictionAndStatistics, StandardOpenOption.APPEND);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
}

static String getNewDrawDataFromWeb(int lastId, BufferedWriter aWriter){
	//
//int[] retV=null;
String baesUrl="http://www.taiwanlottery.com.tw/lotto/DailyCash/history.aspx";
String startFrom="D539Control_history1_dlQuery";//開獎日期";
String endWith = "intx01";
//Calendar calendar0=Calendar.getInstance();
Lottery539PageParser aParser=new Lottery539PageParser("539");
//String fileName=dataCenter+"test539.txt";
//String bkpFileName=dataCenter+"test539BKP.txt";
Vector<String> tmpStorage=new Vector<String>();
//File history=new File(dataCenter+"test539.txt");
/*
 * <tr>
		<td>
            <table width="107%" height="314" class="table_org td_hm">
              <tr>
                <td width="91" class="td_org1 "> 期別 </td>
                <td width="98" class="td_org1 ">日期</td>
                <td colspan="6" class="td_org1 "> 獎號 </td>
              </tr>
              <tr>
                <td rowspan="2" class="td_w"> <span id="D539Control_history1_dlQuery_D539_DrawTerm_0">104000311</span></td>
                <td height="63" class="td_w">開獎<br />
                <span id="Lotto649Control_history1_dlQuery_ctl00_L649_DDate">
                <span id="D539Control_history1_dlQuery_D539_DDate_0">104/12/29</span></span></td>
                <td width="89" class="td_w">開出順序 </td>
                <td width="74" class="td_w font_black14b_center"><span id="D539Control_history1_dlQuery_SNo1_0">39</span></td>
                <td width="76" class="td_w font_black14b_center"><span id="D539Control_history1_dlQuery_SNo2_0">38</span></td>
                <td width="77" class="td_w font_black14b_center"><span id="D539Control_history1_dlQuery_SNo3_0">10</span></td>
                <td width="77" class="td_w font_black14b_center"><span id="D539Control_history1_dlQuery_SNo4_0">22</span></td>
                <td width="83" class="td_w font_black14b_center"><span id="D539Control_history1_dlQuery_SNo5_0">17</span></td>
              </tr>
              <tr>
                <td height="63" class="td_w">兌獎截止(註3)<br />
                <span id="D539Control_history1_dlQuery_D539_EDate_0">105/03/29</span></td>
                <td class="td_w">大小順序</td>
                <td class="td_w font_black14b_center"><span id="D539Control_history1_dlQuery_No1_0">10</span></td>
                <td class="td_w font_black14b_center"><span id="D539Control_history1_dlQuery_No2_0">17</span></td>
                <td class="td_w font_black14b_center"><span id="D539Control_history1_dlQuery_No3_0">22</span></td>
                <td class="td_w font_black14b_center"><span id="D539Control_history1_dlQuery_No4_0">38</span></td>
                <td class="td_w font_black14b_center"><span id="D539Control_history1_dlQuery_No5_0">39</span></td>
              </tr>
              <tr>
                <td class="td_org1">銷售金額</td>
                <td class="td_org1">獎金總額 </td>
                <td colspan="2" class="td_org1">項目</td>
                <td class="td_org1">頭獎</td>
                <td class="td_org1">貳獎 </td>
                <td class="td_org1">參獎</td>
                <td class="td_org1">肆獎</td>
              </tr>
              <tr>
                <td rowspan="3" class="td_w">
                   
                  <p><span id="D539Control_history1_dlQuery_D539_TotalAmount_0">31,693,100</span></p></td>
                <td rowspan="3" class="td_w">
                  
                  <p><span id="D539Control_history1_dlQuery_D539_Jackpot_0">9,505,000</span> </p></td>
                <td colspan="2" class="td_w">中獎注數</td>
                <td class="td_w"><span id="D539Control_history1_dlQuery_D539_CategA2_0">0</span></td>
                <td class="td_w"><span id="D539Control_history1_dlQuery_D539_CategB2_0">195</span></td>
                <td class="td_w"><span id="D539Control_history1_dlQuery_D539_CategC2_0">6,895</span></td>
                <td class="td_w"><span id="D539Control_history1_dlQuery_D539_CategD2_0">70,730</span></td>
              </tr>
              <tr>
                <td colspan="2" class="td_w">單注獎金</td>
                <td class="td_w"><span id="D539Control_history1_dlQuery_D539_CategA1_0">8,000,000</span></td>
                <td class="td_w"><span id="D539Control_history1_dlQuery_D539_CategB1_0">20,000</span></td>
                <td class="td_w"><span id="D539Control_history1_dlQuery_D539_CategC1_0">300</span></td>
                <td class="td_w"><span id="D539Control_history1_dlQuery_D539_CategD1_0">50</span></td>
              </tr>
             
          </table>
            <br />
                                                    </td>

 */
String retS=null;
		String readData=aParser.readData(baesUrl, startFrom, endWith);
		if (readData==null) {
			System.out.println("Failed to read 539 web page");
			return null;
		}
		String[] daily=readData.split("<TABLE ");
		boolean finish=false;
		String dropDate=null;
		for (int id=1; id<daily.length; id++){
			if (finish) break;			
			String[] rows=daily[id].split("<TR>");
			for (int ir=2; ir <4; ir++){
				String[] cols=rows[ir].split("<TD ");
				if (ir==2){
					String idData=aParser.removeHtmTags(cols[1]).trim();
					int newID=Integer.parseInt(idData);
					if (newID == lastId) {
						finish=true;
						break;
					}
					
					String cnOpen=aParser.removeHtmTags(cols[2]).trim();
					tmpStorage.add(idData+"     開獎");//+cnOpen);
					int iSpan=cols[2].indexOf("<SPAN");					
					dropDate=aParser.removeHtmTags(cols[2].substring(iSpan));
					int dropPos=3;
					String cnDrop="開出順序";//aParser.removeHtmTags(cols[dropPos]).trim();
					String n1=aParser.removeHtmTags(cols[dropPos+1]);
					String n2=aParser.removeHtmTags(cols[dropPos+2]);
					String n3=aParser.removeHtmTags(cols[dropPos+3]);
					String n4=aParser.removeHtmTags(cols[dropPos+4]);
					String n5=aParser.removeHtmTags(cols[dropPos+5]);	
					tmpStorage.add(dropDate+"     "+cnDrop+"\t"+n1+"\t"+n2+"\t"+n3+"\t"+n4+"\t"+n5);
				} else if (ir==3){						
					int iSpan=cols[1].indexOf("<SPAN");					
					String expDate=aParser.removeHtmTags(cols[1].substring(iSpan)).trim();
					int orderPos=2;
					String cnOrder="大小順序";//aParser.removeHtmTags(cols[orderPos]).trim();
					String sLine=expDate+"     "+cnOrder;
					String[] nS=new String[5];
					for (int i=1; i<6; i++){
						nS[i-1]=aParser.removeHtmTags(cols[orderPos+i]).trim();
						sLine += "\t"+nS[i-1];
					}		
					tmpStorage.add(sLine);//expDate+"     "+cnOrder+" "+n1+" "+n2+" "+n3+" "+n4+" "+n5);
					if (retS==null){
						int yy=Integer.parseInt(dropDate.substring(0, 3))+1911;
						int mm=Integer.parseInt(dropDate.substring(4, 6));
						int dd=Integer.parseInt(dropDate.substring(7, 9));
						Calendar calendar=//Calendar.getInstance();
								new GregorianCalendar(yy,mm-1,dd-1);
								//calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); 
						calendar.setFirstDayOfWeek(GregorianCalendar.SUNDAY);
						int wkDay=(calendar.get(Calendar.DAY_OF_WEEK));//+4) % 7;
						retS=""+yy+	dropDate.substring(3)+"("+	wkDay+")";
						//retV=new int[5];
						for (int i=0; i<5; i++) retS +="-"+nS[i];														
					}
				}			
			}
		}	
		//if (tmpStorage.size()<1) return false;
	//BufferedWriter aWriter;
	try {
		//aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("History.txt")));
		for (int i=0; i<tmpStorage.size(); i++){
			String aLine=tmpStorage.get(i);
			int i0=0;
			while (aLine.charAt(i0)< '0' || aLine.charAt(i0) > '9' ) i0++;
			aWriter.write(aLine.substring(i0));aWriter.newLine();
		}
		//aWriter.close();
	} catch (IOException e){
		System.out.println("Bad I/O");
	}
	return retS;
}

static Vector<DrawInfor> updateAndGetDrawData(){
//String baesUrl="http://www.taiwanlottery.com.tw/lotto/DailyCash/history.aspx";
//String startFrom="D539Control_history1_dlQuery";//開獎日期";
//String endWith = "intx01";
//Calendar calendar0=Calendar.getInstance();
Lottery539PageParser aParser=new Lottery539PageParser("539");
String fileName=dataCenter+"test539.txt";
String bkpFileName=dataCenter+"test539BKP.txt";
String bkpHistory=dataCenter+"bkpHistory.txt";
String history=dataCenter+"History.txt";
Vector<DrawInfor> allLines=null;

Vector<String> tmpStorage=new Vector<String>();
String newData=null;
BufferedReader mReader;
try {
	mReader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),  "UTF-8"));						
	BufferedWriter aWriter;
	
	int drawNumber=-1;
	aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(bkpFileName),  "UTF-8"));						

	//String drawDay="";
	String aLine;
	String today=null;
	while (drawNumber < 1 || today == null){		
			aLine=mReader.readLine();
			if (aLine==null) break;
			if (aLine.length()<10) continue;
			int i0=0;
			while (aLine.charAt(i0) < '0' || aLine.charAt(i0) > '9'  ) i0++;
			String okString=aLine.substring(i0);
			tmpStorage.add(okString);
			if (okString.indexOf("開出順序")>0){
				today=okString.substring(4, 9);
				if (drawNumber > 0) break;
			}
			if (okString.indexOf("開獎")>0){
				//today=okString.substring(4, 9);
				String id=okString.substring(0, 9);
				drawNumber = Integer.parseInt(id);
				if (today !=null)
				break;
			}
	}
	String myTimeZoneID=TimeZone.getDefault().getID();
	TimeZone.setDefault(TimeZone.getTimeZone("Hongkong"));

	Calendar calendar=Calendar.getInstance();
	int iHr=calendar.get(Calendar.HOUR_OF_DAY);
	if (iHr < 22) calendar.add(Calendar.DATE,  -1);
	//int iMin=calendar.get(Calendar.MINUTE);

	String lstDay=dI.format(calendar.get(Calendar.MONTH)+1)+"/"+dI.format(calendar.get(Calendar.DATE));
	if (!lstDay.equalsIgnoreCase(today) )
	newData=getNewDrawDataFromWeb(drawNumber, aWriter);
	
	TimeZone.setDefault(TimeZone.getTimeZone(myTimeZoneID));
	if (newData!=null)
	{
		for (int ip=0; ip<tmpStorage.size(); ip++){
			aWriter.write(tmpStorage.get(ip));aWriter.newLine();
		}
		do {
			aLine=mReader.readLine();
			if (aLine==null) break;
			aWriter.write(aLine);aWriter.newLine();
				
		} while (aLine != null);
	}
	mReader.close();
	aWriter.close();
	allLines=new Vector<DrawInfor>();
	
	File historyF=new File(dataCenter+"History.txt");
	mReader=new BufferedReader(new InputStreamReader(new FileInputStream(dataCenter+"History.txt"),  "UTF-8"));
	aLine=mReader.readLine();
	if (newData != null && aLine != null && newData.equalsIgnoreCase(aLine)) newData = null;
	aWriter=null;
	if (newData != null){
		bkpHistory=dataCenter+"bkpHistory.txt";
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(bkpHistory),  "utf-8"));
		aWriter.write(newData);aWriter.newLine();
		//aLine=newData;
	}
	//else aLine=mReader.readLine();
	
	while (aLine != null){
		int i0=0;
		while (aLine.charAt(i0)<'0' || aLine.charAt(i0)>'9') i0++;
		String cleanLine=aLine.substring(i0);
		if (aLine !=null && aWriter!=null){
			aWriter.write(cleanLine);aWriter.newLine();	}	
		int ix=cleanLine.indexOf('(');
		String date1=cleanLine.substring(0,  ix);
		int i9=cleanLine.indexOf(')');
		int wkDay=Integer.parseInt(cleanLine.substring(ix+1, i9));
		int[] datas=getDrawData(cleanLine.substring(i9+2));
		allLines.add(aParser.getOneDrawInfo(date1, wkDay, datas));
		aLine=mReader.readLine();				
	} 
	
	mReader.close();
	if (aWriter != null)
	aWriter.close();
	
	} catch (FileNotFoundException e){
	System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
	}
	if (newData != null){
		File myNew=new File(bkpFileName);
		File myDup=new File(fileName);
		File myOld=new File(bkpFileName+".old");
		if (myOld.exists()) myOld.delete();
		if (myDup.exists() && !myOld.exists()) myDup.renameTo(myOld);
		myNew.renameTo(myDup);
		myNew=new File(bkpHistory);
		myDup=new File(history);
		myOld=new File(history+".old");
		if (myOld.exists()) myOld.delete();
		if (myDup.exists() && !myOld.exists()) myDup.renameTo(myOld);
		myNew.renameTo(myDup);
	}	
	
	return allLines;
	/*
	BufferedWriter aWriter;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("History.txt")));
		//for (int i=0; i<allLines.size(); i++){
			//aWriter.write(allLines.get(i).toString());aWriter.newLine();
		}
		aWriter.close();
	} catch (IOException e){
	System.out.println("Bad I/O");
	}
*/
}

static void updateDrawData(){

String baesUrl="http://www.taiwanlottery.com.tw/lotto/DailyCash/history.aspx";
String startFrom="D539Control_history1_dlQuery";//開獎日期";
String endWith = "intx01";
//Calendar calendar0=Calendar.getInstance();
Lottery539PageParser aParser=new Lottery539PageParser("539");
String fileName=dataCenter+"test539.txt";
String bkpFileName=dataCenter+"test539BKP.txt";
String bkpHistory=dataCenter+"bkpHistory.txt";
String history=dataCenter+"History.txt";

Vector<String> tmpStorage=new Vector<String>();
String newData=null;
BufferedReader mReader;
try {
	mReader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),  "utf-8"));						
	BufferedWriter aWriter;
	
	int drawNumber=-1;
	aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(bkpFileName),  "utf-8"));						

	String drawDay="";
	String aLine;
	while (drawNumber < 1){		
			aLine=mReader.readLine();
			if (aLine==null) break;
			tmpStorage.add(aLine);
			if (aLine.indexOf("開獎")>0){
				String id=aLine.substring(0, 9);
				drawNumber = Integer.parseInt(id);
				break;
			}
	}
	newData=getNewDrawDataFromWeb(drawNumber, aWriter);
	if (newData!=null)
	{
		for (int ip=0; ip<tmpStorage.size(); ip++){
			aWriter.write(tmpStorage.get(ip));aWriter.newLine();
		}
		do {
			aLine=mReader.readLine();
			if (aLine==null) break;
			aWriter.write(aLine);aWriter.newLine();
				
		} while (aLine != null);
	}
	mReader.close();
	aWriter.close();
	if (newData != null){
		File historyF=new File(dataCenter+"History.txt");
		mReader=new BufferedReader(new FileReader(historyF));
		bkpHistory=dataCenter+"bkpHistory.txt";
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(bkpHistory),  "utf-8"));
		aWriter.write(newData);aWriter.newLine();
		do {
			aLine=mReader.readLine();
			if (aLine==null) break;
			aWriter.write(aLine);aWriter.newLine();				
		} while (aLine != null);
	}
	mReader.close();
	aWriter.close();
	
	} catch (FileNotFoundException e){
	System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
	}
	if (newData != null){
		File myNew=new File(bkpFileName);
		File myDup=new File(fileName);
		File myOld=new File(bkpFileName+".old");
		if (myOld.exists()) myOld.delete();
		if (myDup.exists() && !myOld.exists()) myDup.renameTo(myOld);
		myNew.renameTo(myDup);
		myNew=new File(bkpHistory);
		myDup=new File(history);
		myOld=new File(history+".old");
		if (myOld.exists()) myOld.delete();
		if (myDup.exists() && !myOld.exists()) myDup.renameTo(myOld);
		myNew.renameTo(myDup);
	}	

	/*
	BufferedWriter aWriter;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("History.txt")));
		//for (int i=0; i<allLines.size(); i++){
			//aWriter.write(allLines.get(i).toString());aWriter.newLine();
		}
		aWriter.close();
	} catch (IOException e){
	System.out.println("Bad I/O");
	}
*/
}
static int[][] dataAfter=new int[40][40];

public static int[][] buildStatisticsPage(Vector<DrawInfor> allLines) 
{
	DrawInfor aDraw=allLines.get(0);
	String[] terms=aDraw.date.split("/");
	int[] data0=aDraw.data;
	int[] lastData=data0;
	//String mm=aDraw.date.substring(5, 7);
	int currentMonth=Integer.parseInt(terms[1]);
	int lastMonth=currentMonth-1;
	if (lastMonth < 1) lastMonth=12;
	int last2Month=lastMonth-1;
	if (last2Month < 1) last2Month=12;
	int last3Month=last2Month-1;
	if (last3Month < 1) last3Month=12;
	int lastMonthFound=0;
	int[] lastMonthDraw=new int[40];
	int[] last2MonthDraw=new int[40];
	int[] last3MonthDraw=new int[40];
	int[] currentMonthDraw=new int[40];
	Arrays.fill(lastMonthDraw, 0);
	Arrays.fill(last2MonthDraw, 0);
	Arrays.fill(last3MonthDraw, 0);
	Arrays.fill(currentMonthDraw, 0);
	String drawLine=aDraw.toString();
	int predictWeekday=aDraw.wkDay;
	if (++predictWeekday ==7) predictWeekday=1;
	for (int i=0; i<monthlyStatistics.length; i++)
	Arrays.fill(monthlyStatistics[i], 0);
	for (int i=0; i<weeklyStatistics.length; i++)
		Arrays.fill(weeklyStatistics[i], 0);
	double iTotal=0;
	int checkRange=allLines.size();
	//checkRange=36;
	//int[][] dataAfter=new int[40][40];
	for (int i=1; i<40; i++){
		Arrays.fill(dataAfter[i], 0);
		drawnSet[i]=new BallStatistics();
		drawnSet[i].checkRange=checkRange;
		drawnSet[i].drawnTimeSeries=new int[checkRange];
		Arrays.fill(drawnSet[i].drawnTimeSeries, 0);
		drawnSet[i].idleDistribution=new int[checkRange];
		Arrays.fill(drawnSet[i].idleDistribution, 0);
		drawnSet[i].idleIntervalCount=0;
		drawnSet[i].maxIdle=0;
		drawnSet[i].minIdle=checkRange;
		drawnSet[i].idleMean=0;
		drawnSet[i].idleVariant=0;
		drawnSet[i].currentIdleDays=-1;
	}
	int[] lastOnAt=new int[40];
	Arrays.fill(lastOnAt, -1);
	int[] lastShownAt=new int[40];
	Arrays.fill(lastShownAt, 0);
	//boolean finishAddCurrentMonth=false;
	int[] bestWeek=new int[40];
	Arrays.fill(lastOnAt, 0);
	boolean lastFitMLine=false;
	int[] fitMCount=new int[40];
	Arrays.fill(fitMCount, 0);
	//Vector<int[]> retV=new Vector<int[]>();
	int[][] drawLines=new int[5][checkRange];
	
	for (int i=0; i<checkRange; i++){
		aDraw=allLines.get(i);
		int[] hits=aDraw.data;
		terms=aDraw.date.split("/");
		//String mm=aDraw.date.substring(5, 7);
		int month=Integer.parseInt(terms[1]);
		//int month=Integer.parseInt(aDraw.date.substring(5,7));
		//if (month != currentMonth) finishAddCurrentMonth=true;
		int iFit=0;
		for (int ik=0; ik<hits.length; ik++){
			drawLines[ik][i]=hits[ik];
			drawnSet[hits[ik]].drawnTimeSeries[i]=1;
			if (drawnSet[hits[ik]].currentIdleDays == -1) drawnSet[hits[ik]].currentIdleDays=i;
			if (lastOnAt[hits[ik]] < 0 && i >0)
				{ 
					drawnSet[hits[ik]].idleDistribution[i]++;
					drawnSet[hits[ik]].maxIdle = i;
					drawnSet[hits[ik]].minIdle = i;
					//drawnSet[hits[ik]].idleIntervalCount++;
				}
			if (lastOnAt[hits[ik]] > 0){
				int iId = i - lastOnAt[hits[ik]]-1;
				if (iId > 0) drawnSet[hits[ik]].idleDistribution[iId]++;
				if (iId > drawnSet[hits[ik]].maxIdle) 				
				{
					drawnSet[hits[ik]].maxIdle = iId;
					drawnSet[hits[ik]].maxIdleAt=i;
				}
				if (iId < drawnSet[hits[ik]].minIdle) 				
					drawnSet[hits[ik]].minIdle = iId;
				//drawnSet[hits[ik]].idleIntervalCount++;
			}
			lastOnAt[hits[ik]]=i;
			monthlyStatistics[hits[ik]][month]++;
			if (month==currentMonth && i < 40){				
				currentMonthDraw[hits[ik]]++;
			}
			if (month==lastMonth && i < 80)
				lastMonthDraw[hits[ik]]++;
			if (month==last2Month && i < 90)
				last2MonthDraw[hits[ik]]++;
			if (month==last3Month && i < 120)
				last3MonthDraw[hits[ik]]++;
			weeklyStatistics[hits[ik]][aDraw.wkDay]++;
			if (lastShownAt[hits[ik]]==0) lastShownAt[hits[ik]]=i+1;
			for (int s=0; s<5; s++)
			{
				if (data0[s]!=hits[ik]) continue;
				iFit++;
				break;
			}
			if (lastFitMLine) fitMCount[hits[ik]]++;
			if (i>0) for (int s=0; s<5; s++)
				dataAfter[hits[ik]][lastData[s]]++;
		}
		lastData=hits;
		lastFitMLine=false;
		if (i> 0 && iFit >= 3) lastFitMLine = true; 
		//System.out.println("Fit "+iFit+" @"+i);
	}
	//work on statistics
	for (int id=1; id<40; id++){
		int iCount=0;
		for (int iv=drawnSet[id].minIdle; iv<=drawnSet[id].maxIdle; iv++){
			drawnSet[id].idleMean += iv*drawnSet[id].idleDistribution[iv];
			iCount += drawnSet[id].idleDistribution[iv];
		}
		drawnSet[id].idleMean /= iCount;
		double rv=0;
		for (int iv=drawnSet[id].minIdle; iv<=drawnSet[id].maxIdle; iv++){
			rv += (iv - drawnSet[id].idleMean)*(iv - drawnSet[id].idleMean)*drawnSet[id].idleDistribution[iv];
		}
		rv /= iCount;
		drawnSet[id].idleVariant = Math.sqrt(rv);
		
	}
	iTotal = 5*checkRange;//allLines.size()*5;
	iTotal /= 39;
	String statisticsFile="history"+checkRange+"Statistics"+predictWeekday+".txt";
	String todayPredictionFile="todayPrediction"+predictWeekday+".txt";
	
	BufferedWriter aWriter;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("history"+checkRange+"Statistics"+predictWeekday+".txt")));
	int[] allBalls=new int[40];	
	Arrays.fill(allBalls, 0);
	for (int ib=1; ib<40; ib++){
		int ballTotal=0;
		String ibs="Ball "+ib+" Monthly (";
		for (int iM=1; iM<13; iM++){
			ibs += (""+iM+"["+monthlyStatistics[ib][iM]+"], ");
			ballTotal += monthlyStatistics[ib][iM];
		}
		allBalls[ib]=ballTotal*100+ib;
		aWriter.write(ibs+"):"+ballTotal+"("+ib+")N:"+drawnSet[ib].currentIdleDays);aWriter.newLine();
	}
	int[] eachSum=Arrays.copyOf(allBalls, allBalls.length);
	Arrays.sort(allBalls);
	
	aWriter.write("Avg: "+iTotal);aWriter.newLine();
	aWriter.write(drawLine);aWriter.newLine();
	int[] predictForWeekday=new int[40];
	Arrays.fill(predictForWeekday, 0);
	int[] ballScores=new int[40];
	Arrays.fill(ballScores, 0);
	double[] left=new double[40];
	int[] ballsToFill=new int[40];
	Arrays.fill(ballScores, 0);
	Arrays.fill(left, 0);
	Arrays.fill(ballsToFill, 0);
	
	for (int ib=1; ib<40; ib++){
		int[] sortedWeek=Arrays.copyOf(weeklyStatistics[ib], 7);
		
		String ibs="B "+ib+"w (";
		int iTtP=0;
		for (int iM=1; iM<7; iM++){
			ibs += (""+iM+"<"+weeklyStatistics[ib][iM]+">, ");
			sortedWeek[iM]=sortedWeek[iM]*100+iM;
			if (sortedWeek[iM] > bestWeek[ib]) bestWeek[ib]=sortedWeek[iM];
			iTtP += (weeklyStatistics[ib][iM]*weeklyStatistics[ib][iM]);
		}
		Arrays.sort(sortedWeek);
		int iHap1=0;
		int iHap2=0;
		int iHap3=0;
		int checkBound1=(int)Math.round(drawnSet[ib].idleMean)+1;
		int checkBound2=(int)Math.round(2*drawnSet[ib].idleMean)+1;
		int checkBound3=(int)Math.round(3*drawnSet[ib].idleMean)+1;
		for (int ibi=0; ibi < checkBound3; ibi++){
			if (drawnSet[ib].drawnTimeSeries[ibi] > 0) {
				iHap3++;
				if (ibi < checkBound2) iHap2++;
				if (ibi < checkBound1) iHap1++;
			}
		}
		setNextOnDay(drawnSet[ib]);
		aWriter.write(ibs+"]["+ib+"]"+drawnSet[ib].currentIdleDays+" Mon("+monthlyStatistics[ib][currentMonth]+") ");
				//" M="+dF.format(drawnSet[ib].idleMean)+" R="+dF.format(drawnSet[ib].idleVariant)+" X="+drawnSet[ib].maxIdle);
		aWriter.write(" M3:"+iHap3+"^"+iHap2+"^"+iHap1+"||"+drawnSet[ib].nextOnDay);//+"r"+dF.format(drawnSet[ib].nextOnVar/2));
		//if (drawnSet[ib].nextOnDay - drawnSet[ib].nextOnVar/2 < 2)
			//aWriter.write(">>"+ dF.format(drawnSet[ib].nextOnDay - drawnSet[ib].nextOnVar/2));
		//if (drawnSet[ib].predict80 < 2 ||  drawnSet[ib].predict90 < 3)
		left[ib]=monthlyStatistics[ib][currentMonth]-currentMonthDraw[ib];
		aWriter.write(">>"+ drawnSet[ib].predict66 +">>"+drawnSet[ib].predict75+">>"+drawnSet[ib].predict80);// dF.format(drawnSet[ib].nextOnDay - drawnSet[ib].nextOnVar/2));
		aWriter.write("-["+ib+"]-"+ dF.format(left[ib]));
		for (int iw=6; iw>3; iw--){
			if (predictWeekday==sortedWeek[iw] % 100)aWriter.write("!!");
			aWriter.write("@"+(sortedWeek[iw] % 100)+"-"+(sortedWeek[iw]/100)+">");
		}
		double r=(weeklyStatistics[ib][predictWeekday]*weeklyStatistics[ib][predictWeekday]);
		r /= iTtP;
		double score= left[ib] * r;
		score *= 10000;
		 ballScores[ib]=(int)(score)*100+ib;
		
		ballsToFill[ib]=(int)Math.round(left[ib]*100)*100+ib;
		//aWriter.write("|Sc="+dF.format(left));
		aWriter.newLine();
		predictForWeekday[ib]=weeklyStatistics[ib][predictWeekday];
	}
	int[] bScores=Arrays.copyOf(ballScores, ballScores.length);
	Arrays.sort(ballScores);
	int k=0;
	for (int i=39; i>=0; i--){
		if (ballScores[i] < 1) continue;
		
		aWriter.write("["+(ballScores[i] % 100)+"]"+(ballScores[i]/100)+", ");
		k++;
		if (k % 10==0) aWriter.newLine();
	}
	aWriter.newLine();
	aWriter.newLine();
	k=0;
	for (int i=0; i<40; i++){
		if (allBalls[i] < 1) continue;
		k++;
		aWriter.write("["+(allBalls[i] % 100)+"]"+(allBalls[i]/100)+", ");
		if (k % 10==0) aWriter.newLine();
		
	}
	aWriter.newLine();
	aWriter.write("---------- ball wk status summary:------------");
	aWriter.newLine();
	aWriter.write("which one\t total\twk freq\t need to fill\tscores\tBestWeek\tp6~p72 ");
	aWriter.newLine();
	
	for (int ib=0; ib<40; ib++){
		predictForWeekday[ib] = predictForWeekday[ib]*100+ib;
	}
	k=0;
	Arrays.sort(predictForWeekday);
	for (int i=39; i>=0; i--){
		if (predictForWeekday[i] < 1) continue;
		k++;
		int which1=predictForWeekday[i] % 100;
		double mv6=0;
		double mv12=0;
		
		double mv24=0;
		double mv36=0;
		double mv72=0;
		double mv156=0;		
		double mv312=0;		
		double mv234=0;
		for (int vk=0; vk<13*39; vk++){
			if (drawnSet[which1].drawnTimeSeries[vk]<1) continue;
			if (vk < 6) mv6++;
			if (vk < 12) mv12++;
			if (vk < 24) mv24++;
			if (vk < 36) mv36++;
			if (vk < 72) mv72++;
			if (vk < 156) mv156++;
			if (vk < 312) mv312++;
			mv234++;
		}
		mv6 /= 6;
		mv12 /= 12;		
		mv24 /= 24;
		mv36 /= 36;
		mv72 /= 72;
		mv156 /= 156;
		mv312 /= 156;
		mv234 /= 13*39;
		String aLine="ball "+which1+"\t\t"+eachSum[which1]/100+"\t"+(predictForWeekday[i]/100)+"\t"+dF.format(ballsToFill[which1]*0.01/100)+"\t"+bScores[which1]/100;
		aLine += "\t"+(bestWeek[which1]/100)+"@"+(bestWeek[which1] % 100);
		aLine += "\t"+dF.format(mv6/mv234)+"\t"+dF.format(mv12/mv234)+"\t"+dF.format(mv24/mv234)+"\t"+dF.format(mv36/mv234)+"\t"+dF.format(mv72/mv234)+"\t"+dF.format(mv234);
		
		aWriter.write(aLine);
		aWriter.newLine();	
	}

	k=0;
	Arrays.sort(ballsToFill);
	for (int i=39; i>=0; i--){
		if (ballsToFill[i] < 1) continue;
		k++;
		aWriter.write("["+(ballsToFill[i] % 100)+"]"+dF.format(ballsToFill[i]*0.01/100)+", ");
		if (k % 10==0) aWriter.newLine();
		
	}
	aWriter.newLine();
	int[] tmpD=Arrays.copyOf(currentMonthDraw, 40);
	for (int ib=1; ib<40; ib++) {
		tmpD[ib] = tmpD[ib]*10+lastMonthDraw[ib];
		tmpD[ib] = tmpD[ib]*10+last2MonthDraw[ib];
		tmpD[ib] = tmpD[ib]*10+last3MonthDraw[ib];
		tmpD[ib] = tmpD[ib]*100+ib;
	}
	Arrays.sort(tmpD);
	aWriter.write("This month \tM1\tM2\tM3\tQ1\tQ0\tfit3All");aWriter.newLine();
	for (int i=39; i>=0; i--){
		if (tmpD[i] < 1) continue;
		//k++;
		int which1=tmpD[i] % 100;
		int sum=lastMonthDraw[which1]+last2MonthDraw[which1]+last3MonthDraw[which1];
		int mine=currentMonthDraw[which1];
		int addMe=mine+lastMonthDraw[which1]+last2MonthDraw[which1];
		aWriter.write("["+(which1)+"]\t"+mine+"\t"+lastMonthDraw[which1]+"\t"+last2MonthDraw[which1]+"\t"+last3MonthDraw[which1]+"\t"+sum+"\t"+addMe+"\t"+fitMCount[which1]);
		//if (k % 10==0) 
		aWriter.newLine();
		
	}
	aWriter.newLine();
	/*
	for (int ib=1; ib<40; ib++) lastMonthDraw[ib] = lastMonthDraw[ib]*100+ib;
	Arrays.sort(lastMonthDraw);
	aWriter.write("This month total ");aWriter.newLine();
	for (int i=39; i>=0; i--){
		if (lastMonthDraw[i] < 1) continue;
		//k++;
		int which1=lastMonthDraw[i] % 100;
		aWriter.write("["+(which1)+"]\t"+lastMonthDraw[i]/100+"\t"+currentMonthDraw[which1]+", ");
		//if (k % 10==0) 
			aWriter.newLine();
		
	}
	*/
	//aWriter.newLine();
	aWriter.write("Data after freq ------------------------------------");aWriter.newLine();
	for (int ib=0; ib<40; ib++){
		aWriter.write(dI.format(ib)+"  ");
	}
	aWriter.newLine();
	for (int ib=1; ib<40; ib++){
		aWriter.write(dI.format(ib)+"  ");
		for (int ix=1; ix<40; ix++)
			aWriter.write(dI.format(dataAfter[ib][ix])+"  ");
		aWriter.newLine();
	}
	aWriter.newLine();
	//JackpotReader.getDataFreqStatistics(predictForWeekday, aWriter);
	
	//String[] termk=fitMLine.split("-");

	aWriter.close();
	
	} catch (FileNotFoundException e){
		System.out.println("No file");
} catch (IOException e){
	System.out.println("Bad I/O");
}
	//File hisFile=new File(statisticsFile);
	Path pathHistory = FileSystems.getDefault().getPath(statisticsFile);
	Path pathToday = FileSystems.getDefault().getPath(todayPredictionFile);
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(todayPredictionFile)));
		File history=new File(statisticsFile);
		BufferedReader aReader =  new BufferedReader(new FileReader(history));
		String aLine=null;
		while ((aLine=aReader.readLine()) != null)
		{
			aWriter.write(aLine); aWriter.newLine();
		}
		//Files.copy(pathHistory, pathToday);
		aWriter.close();
		aReader.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return drawLines;
}

static void getQuarTriple(int[][] samples)
{
	int[][][][] tripleCount=new int[40][40][40][40];
	for (int i=0; i<40; i++){
		for (int j=0; j<40; j++){
			for (int k=0; k<40; k++){
				for (int m=0; m<40; m++){
					tripleCount[i][j][k][m]=0;
				}
				
			}
		}
	}
	int[][] lastLoc=new int[40][400000];
	for (int i=0; i<40; i++)
	Arrays.fill(lastLoc[i], 0);
	for (int x=samples[0].length-1; x>=0; x--){
		for (int n1=0; n1<5-3; n1++){
			for (int n2=n1+1; n2<5-2; n2++){
				for (int n3=n2+1; n3<5-1; n3++){
					for (int n4=n3+1; n4<5-0; n4++){
					tripleCount[samples[n1][x]][samples[n2][x]][samples[n3][x]][samples[n4][x]]++;
					lastLoc[samples[n4][x]][samples[n1][x]*10000+samples[n2][x]*100+samples[n3][x]]=x+1;
				}
				}
			}
		}
	}
	
	BufferedWriter aWriter;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("qtripleList"+tomorrowWkDay+".txt")));
		System.out.println("JackpotListBestTriple Creating file "+"qtripleList.txt.txt");
		TreeSet<String> sortedData=new TreeSet<String>();
		DecimalFormat dI2=new DecimalFormat("00");
		int maxC=0;
		for (int i=0; i<40; i++){
			for (int j=0; j<40; j++){
				for (int k=0; k<40; k++){
					for (int m=0; m<40; m++){
				
					if (tripleCount[i][j][k][m]==0) continue;
					if (tripleCount[i][j][k][m] > maxC) maxC=tripleCount[i][j][k][m];
					String sTripCount=dI.format(tripleCount[i][j][k][m])+"-";
					sTripCount += dI2.format(i)+"-";
					sTripCount += dI2.format(j)+"-";
					sTripCount += dI2.format(k)+"-";
					sTripCount += dI2.format(m);
					sortedData.add(sTripCount);
					}
				}
			}
		}
		int[] summary=new int[maxC+1];
		Arrays.fill(summary, 0);
		Iterator<String> itr=sortedData.iterator();
		while (itr.hasNext()){
			String aLine=itr.next();
			String[] terms=aLine.split("-");
			int iCount=Integer.parseInt(terms[0]);
			summary[iCount]++;
			String lastAt="";
			if (iCount > 2)
			{ 
				int lastLocation=lastLoc[Integer.parseInt(terms[4])][Integer.parseInt(terms[1])*10000+Integer.parseInt(terms[2])*100+Integer.parseInt(terms[3])];
				//if (lastLocation > samples[0).length/3)
				{
				lastAt="@"+lastLocation;//(lastLoc[Integer.parseInt(terms[4])][Integer.parseInt(terms[1])*10000+Integer.parseInt(terms[2])*100+Integer.parseInt(terms[3])]);
				if ( lastLocation > samples[0].length/3)lastAt += "<---";
				aWriter.write(aLine+"  -----> "+iCount+lastAt);aWriter.newLine();	
				}
			}		
		}
		aWriter.write("====== total Days="+samples[0].length+ "=========================");aWriter.newLine();
		for (int i=0; i<summary.length; i++){
			aWriter.write(""+i+" ["+summary[i]+"],");aWriter.newLine();
		}
		aWriter.close();
		
	} catch (FileNotFoundException e){
		System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
	}

}
static void getTriple(int[][] allData){
	int[][][] tripleCount=new int[40][40][40];
	for (int i=0; i<40; i++){
		for (int j=0; i<40; i++){
			for (int k=0; i<40; i++){
				tripleCount[i][j][k]=0;
			}
		}
	}
	int[] lastLoc=new int[400000];
	Arrays.fill(lastLoc, 0);
	for (int x=allData[0].length-1; x>=0; x--){
		for (int n1=0; n1<5-2; n1++){
			for (int n2=n1+1; n2<5-1; n2++){
				for (int n3=n2+1; n3<5-0; n3++){
					tripleCount[allData[n1][x]][allData[n2][x]][allData[n3][x]]++;
					lastLoc[allData[n1][x]*10000+allData[n2][x]*100+allData[n3][x]]=x+1;
				}
			}
		}
	}
	
	BufferedWriter aWriter;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("tripleList"+tomorrowWkDay+".txt")));
		System.out.println("JackpotListBestTriple Creating file "+"tripleList.txt.txt");
		TreeSet<String> sortedData=new TreeSet<String>();
		DecimalFormat dI2=new DecimalFormat("00");
		int maxC=0;
		for (int i=0; i<40; i++){
			for (int j=0; j<40; j++){
				for (int k=0; k<40; k++){
					if (tripleCount[i][j][k]==0) continue;
					if (tripleCount[i][j][k] > maxC) maxC=tripleCount[i][j][k];
					String sTripCount=dI.format(tripleCount[i][j][k])+"-";
					sTripCount += dI2.format(i)+"-";
					sTripCount += dI2.format(j)+"-";
					sTripCount += dI2.format(k)+"-";
					sortedData.add(sTripCount);
				}
			}
		}
		String[] ovdLine=new String[100];
		Vector<String> vipList=new Vector<String>();
		int iOvd=0;
		int[] summary=new int[maxC+1];
		Arrays.fill(summary, 0);
		Iterator<String> itr=sortedData.iterator();
		while (itr.hasNext()){
			String aLine=itr.next();
			String[] terms=aLine.split("-");
			int iCount=Integer.parseInt(terms[0]);
			summary[iCount]++;
			String lastAt="";
			int lastLocation=lastLoc[Integer.parseInt(terms[1])*10000+Integer.parseInt(terms[2])*100+Integer.parseInt(terms[3])];
			for (int d=maxC/3+1; d<maxC+1; d++)
			{ 
				if (iCount == d)  
				{
				lastAt="@"+lastLocation;//(lastLoc[Integer.parseInt(terms[4])][Integer.parseInt(terms[1])*10000+Integer.parseInt(terms[2])*100+Integer.parseInt(terms[3])]);
				if ( lastLocation > allData[0].length/d)
					{
					lastAt += "<---";
					if (iCount > 6)
					vipList.add(terms[1]);
					}
				if ( lastLocation*d > allData[0].length*2.5){
					if (iCount > 4)
						vipList.add(terms[1]);
					lastAt += "	ovd";
					ovdLine[iOvd]=aLine+"-@"+lastLocation;
					iOvd++;
				}
				aWriter.write(aLine+"  -----> "+iCount+lastAt);aWriter.newLine();	
				}
			}	
			//if (iCount > samples[0).length/7){ lastAt="@"+(lastLoc[Integer.parseInt(terms[1])*10000+Integer.parseInt(terms[2])*100+Integer.parseInt(terms[3])]);
			//aWriter.write(aLine+"  -----> "+iCount+lastAt);aWriter.newLine();	}		
		}
		aWriter.write("====== total Days="+allData[0].length+ "=========================");aWriter.newLine();
		for (int i=0; i<summary.length; i++){
			aWriter.write(""+i+" ["+summary[i]+"],");aWriter.newLine();
		}
		aWriter.write("====== total Ovd="+iOvd+ "=========================");aWriter.newLine();
		
		for (int i=0; i<iOvd; i++){
			aWriter.write(ovdLine[i]);aWriter.newLine();
		}
		aWriter.close();
		
	} catch (FileNotFoundException e){
		System.out.println("No file");
} catch (IOException e){
	System.out.println("Bad I/O");
}
	
}

static void listThreeFitAfterN1Fit(Vector<DrawInfor> allLines){
	//allLines are descending
	DrawInfor aDraw=allLines.get(allLines.size()-1);
	String firstDate=aDraw.date;
	
	HashMap<String, int[]> datedData=new HashMap<String, int[]>();
	for (int d=0; d<allLines.size(); d++){
		//DrawInfor 
		aDraw=allLines.get(d);
		datedData.put(aDraw.date, aDraw.data);		
	}
	
	int testCaseCount=512;//30*39;
	//int sampleRefSize=testCaseCount+12;
			
	BufferedWriter aWriter=null;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("fit3AfterN1Fit"+tomorrowWkDay+".txt")));
		int total=0;
		for (int i=0; i<allLines.size(); i++){
			int subTotal=0;
			int[] refDraw=allLines.get(i).data;
			for (int a3=i+1; a3<allLines.size(); a3++){
				int[] checkDraw=allLines.get(a3).data;
				int iC=0;
				for (int iref=0; iref<5; iref++){
					for (int ick=0; ick<5; ick++){
						if (refDraw[iref]!=checkDraw[ick]) continue;
						iC++;
						break;
					}
					if (iC ==3) {
						break;	
					}
				}
				if (iC<3) continue;
				//aWriter.write("Line "+i+" "+allLines.get(i).toString()+" Matches 3 w/@"+a3+" "+allLines.get(a3).toString());
				//aWriter.newLine();
				String sLink="";
				boolean found0=false;
				int cmpC=0;
				do
				{
					if (a3+cmpC+1 > allLines.size()-1) break;
					if (i+cmpC+1 > allLines.size()-1) break;
					
					int[] cmpDrawR=allLines.get(a3+cmpC+1).data;
					int[] cmpDrawF=allLines.get(i+cmpC+1).data;
					int cF=0;
					for (int iref=0; iref<5; iref++){
						for (int ick=0; ick<5; ick++){
							if (cmpDrawR[iref]!=cmpDrawF[ick]) continue;
							cF++;
							cmpC++;
							sLink += "p"+cmpC+"-"+cmpDrawF[ick]+",";
							break;
						}
						if (cF > 0) break;
					}
					if (cF==0) found0=true;
				} while (!found0);
				if (cmpC > 2) {
					//System.out.println();
					aWriter.write("Line "+i+" "+allLines.get(i).toString()+" Matches 3 w/@"+a3+" "+allLines.get(a3).toString());					
					aWriter.write(" links "+sLink);
					aWriter.newLine();
					subTotal++;
				}
			}
			if (subTotal > 0){
			aWriter.write("..............");
			aWriter.newLine();
			aWriter.write("subtotal="+subTotal);
			total += subTotal;
			aWriter.newLine();
			aWriter.write("--------------------------");
			aWriter.newLine(); }			
		}

		aWriter.write("..............");
		aWriter.newLine();
		aWriter.write("total="+total);
		aWriter.newLine();
		aWriter.write("==========================");
		aWriter.newLine();				
	aWriter.close();
	
}  catch (IOException e){
		System.out.println("Bad I/O");
	}
	//return aWriter;
}

static int[] getPredictionsAfterNFits(int[][] allLines, int fitCount){
	//allLines are descending
	int[][] refSet=new int[fitCount][];
	for (int i=0; i<fitCount; i++){
		refSet[i]=allLines[i];
	}
	int[] allPredicts=new int[40];
	Arrays.fill(allPredicts, 0);
	int chkBnd=allLines.length - fitCount;
	if (chkBnd > 234) chkBnd=234;
	for (int i=1; i<chkBnd; i++){
		boolean match=true;		
		for (int k=0; k<fitCount; k++){
			boolean found=false;			
			for (int s=0; s<5; s++){
				for (int sm=0; sm<5; sm++){
					if (allLines[i+k][sm] != refSet[k][s]) continue;
					found=true;
					break;
				}
				if (found) break;
			}
			if (!found) {
				match=false;
				break;
			}
		}
		if (match){
			for (int s=0;s<allLines[i-1].length; s++)
			allPredicts[allLines[i-1][s]]++;
		}
	}
	int iC=0;
	for (int ib=1; ib<40; ib++){
		if (allPredicts[ib] < 0) continue;
		allPredicts[ib] = allPredicts[ib]*100+ib;
		iC++;
	}	
	if (iC < 1) return null;
	Arrays.sort(allPredicts);
	int[] myPredicts=Arrays.copyOfRange(allPredicts, 35, 40);
	iC=0;
	for (int s=0; s<myPredicts.length; s++){		
		if (myPredicts[s] / 100 < 1) {
			myPredicts[s]=0;
			iC++;
		}
		else myPredicts[s]=myPredicts[s] % 100;	
	}
	return Arrays.copyOfRange(myPredicts, iC, 5) ;
}

static void predictByMatchN(Vector<DrawInfor> allLines){
	//allLines are descending
	DrawInfor aDraw=allLines.get(allLines.size()-1);
	String firstDate=aDraw.date;
	
	int[][] descendingdData=new int[allLines.size()][5];
	for (int d=0; d<allLines.size(); d++){
		//DrawInfor 
		aDraw=allLines.get(d);
		descendingdData[d]=aDraw.data;		
	}
	
	int testCaseCount=512;//30*39;
	//int sampleRefSize=testCaseCount+12;
			
	BufferedWriter aWriter=null;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("byNmatchPoints"+tomorrowWkDay+".txt")));
		int[] caseId={3, 4, 5, 6};
		int[][] hitCases=new int[caseId.length][6];
		int[][] lastHitCases=new int[caseId.length][6];
		
		int[][][] localPerformanceList=new int[caseId.length][6][testCaseCount];
		for (int i=0; i<caseId.length; i++){
		//String[] showPredictions=new String[fitRange.length];
			for (int x=0; x<6;x++)
					Arrays.fill(localPerformanceList[i][x], 0);
			
				Arrays.fill(hitCases[i], 0);
				Arrays.fill(lastHitCases[i], 0);
		}
		int[][][] predictionTS=new int[caseId.length][testCaseCount+1][];
		
		int[] hitCount=new int[40];
		Arrays.fill(hitCount, 0);
		//double[][] ovdTS=new double[40][sampleRefSize];
		String sPredictions="";
		int iLen=allLines.size();
	
	//int[][] occurrence=new int[40][iLen];
	
	
	for (int iTest=testCaseCount; iTest>=0; iTest--){
		int tsPos=iLen-iTest;
		
		int[] draws=new int[5];
		Arrays.fill(draws, 0);
		String sDraws="Draws:( ";
		if (iTest > 0) draws=Arrays.copyOf(allLines.get(iTest-1).data, 5);
		for (int s=0; s<5; s++) sDraws += draws[s]+", ";
		int kday=iTest-1;
		if (kday < 0) kday=0;
		int[][] wkData=new int[iLen-iTest][];
		for (int k=iTest; k<iLen; k++){
			wkData[k-iTest]=descendingdData[k];
		}
		for (int ic=0; ic<caseId.length; ic++){
			int[] predictions=new int[40];
			Arrays.fill(predictions, 0);
			int[] predicts=getPredictionsAfterNFits(wkData, caseId[ic]);
			if (predicts==null){
				predicts=new int[1];
				predicts[0]=0;
			}
			for (int i=0; i<predicts.length; i++){				
					predictions[predicts[i]]=1;					
			}
			
			String sHit="<<<hits:( ";
			//String sHit="<<<hits:( ";
			int iHit=0;
			
			if (iTest > 0){
				for (int s=0; s<5; s++){
					if (predictions[draws[s]]==1) {
						sHit += ""+draws[s]+", ";
						iHit++;
					}
				}
			}
			int iB=0;
			int iSol=0;
			sPredictions="predictions:( ";
			//if (iTest > testCaseCount) continue;
			for (int ib=1; ib<40; ib++){
				if (predictions[ib]<1) continue;
				
				predictions[iSol++]=ib;
				sPredictions += ib+", ";
			}
			predictionTS[ic][testCaseCount-iTest]=Arrays.copyOf(predictions, iSol);

			aWriter.write("------ Test "+iTest+" Results --------------  "+iHit); aWriter.newLine();
			///aWriter.write(predx); aWriter.newLine();
			aWriter.write(sDraws+") ==> "+sHit+")>>>>"); aWriter.newLine();
			aWriter.write(sPredictions+") !!"+iHit); aWriter.newLine();
			if (iTest > 0){
				hitCases[ic][iHit]++;
				lastHitCases[ic][iHit]=iTest;				
				localPerformanceList[ic][iHit][testCaseCount-iTest]=1;
			}
		
		aWriter.write("==========================================="); aWriter.newLine();
		}
	}
	String myName="match N points ";
	JackpotReader.showSummaryPage(predictionTS, hitCases, lastHitCases, localPerformanceList, myName, aWriter);


	aWriter.close();
	
}  catch (IOException e){
		System.out.println("Bad I/O");
	}
	//return aWriter;
}

static void predictByDataAfter(Vector<DrawInfor> allLines){
	//allLines are descending
	DrawInfor aDraw=allLines.get(allLines.size()-1);
	String firstDate=aDraw.date;
	
	int[][] descendingdData=new int[allLines.size()][5];
	for (int d=0; d<allLines.size(); d++){
		//DrawInfor 
		aDraw=allLines.get(d);
		descendingdData[d]=aDraw.data;		
	}
	
	int testCaseCount=512;//30*39;
	//int sampleRefSize=testCaseCount+12;
			
	BufferedWriter aWriter=null;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("byMarkovChain"+tomorrowWkDay+".txt")));
		int[] caseId={1,2,3};//, 4, 5};
		int[][] hitCases=new int[caseId.length][6];
		int[][] lastHitCases=new int[caseId.length][6];
		
		int[][][] localPerformanceList=new int[caseId.length][6][testCaseCount];
		for (int i=0; i<caseId.length; i++){
		//String[] showPredictions=new String[fitRange.length];
			for (int x=0; x<6;x++)
					Arrays.fill(localPerformanceList[i][x], 0);
			
				Arrays.fill(hitCases[i], 0);
				Arrays.fill(lastHitCases[i], 0);
		}
		int[][][] predictionTS=new int[caseId.length][testCaseCount+1][];
		
		int[] hitCount=new int[40];
		Arrays.fill(hitCount, 0);
		//double[][] ovdTS=new double[40][sampleRefSize];
		String sPredictions="";
		int iLen=allLines.size();
	
	//int[][] occurrence=new int[40][iLen];
	
	
	for (int iTest=testCaseCount; iTest>=0; iTest--){
		int tsPos=iLen-iTest;
		
		int[] draws=new int[5];
		Arrays.fill(draws, 0);
		String sDraws="Draws:( ";
		if (iTest > 0) draws=Arrays.copyOf(allLines.get(iTest-1).data, 5);
		for (int s=0; s<5; s++) sDraws += draws[s]+", ";
		int[] todayDraws=Arrays.copyOf(allLines.get(iTest).data, 5);
		/*
		int kday=iTest-1;
		if (kday < 0) kday=0;
		int[][] wkData=new int[iLen-iTest][];
		for (int k=iTest; k<iLen; k++){
			wkData[k-iTest]=descendingdData[k];
		}
		*/
		for (int ic=0; ic<caseId.length; ic++){
			int[] predictions=new int[40];
			Arrays.fill(predictions, 0);
			for (int s=0; s<5; s++){
				int[] toSort=Arrays.copyOf(dataAfter[todayDraws[s]], 40);
				for (int sj=1; sj<40; sj++) toSort[sj]=100*toSort[sj]+sj;
				Arrays.sort(toSort);
				for (int sj=40-caseId[ic]; sj<40; sj++)
				predictions[toSort[sj] % 100]++;
			}
			//int[] predicts=getPredictionsAfterNFits(wkData, caseId[ic]);
			//if (predicts==null){
				//predicts=new int[1];
				//predicts[0]=0;
			//}
			//for (int i=0; i<predicts.length; i++){				
					//predictions[predicts[i]]=1;					
			//}
			
			String sHit="<<<hits:( ";
			//String sHit="<<<hits:( ";
			int iHit=0;
			
			if (iTest > 0){
				for (int s=0; s<5; s++){
					if (predictions[draws[s]]>0) {
						sHit += ""+draws[s]+", ";
						iHit++;
					}
				}
			}
			
			int iB=0;
			int iSol=0;
			sPredictions="predictions:( ";
			//if (iTest > testCaseCount) continue;
			String vC0="";
			for (int ib=1; ib<40; ib++){
				if (predictions[ib]<1) continue;
				if (iTest==0){
					vC0 += (ib+"["+predictions[ib]+"], ");
				}
				predictions[iSol++]=ib;
				sPredictions += ib+", ";
			}
			
			predictionTS[ic][testCaseCount-iTest]=Arrays.copyOf(predictions, iSol);

			aWriter.write("------ Test "+iTest+" Results --------------  "+iHit); aWriter.newLine();
			///aWriter.write(predx); aWriter.newLine();
			aWriter.write(sDraws+") ==> "+sHit+")>>>>"); aWriter.newLine();
			aWriter.write(sPredictions+") !!"+iHit); aWriter.newLine();
			if (iTest > 0){
				hitCases[ic][iHit]++;
				lastHitCases[ic][iHit]=iTest;				
				localPerformanceList[ic][iHit][testCaseCount-iTest]=1;
			}
			else
			{
				aWriter.write(vC0+")w/"+caseId[ic]);aWriter.newLine();
			}
		
		aWriter.write("==========================================="); aWriter.newLine();
		}
	}
	String myName="Use Data After (Markov Chain) ";
	JackpotReader.showSummaryPage(predictionTS, hitCases, lastHitCases, localPerformanceList, myName, aWriter);


	aWriter.close();
	
}  catch (IOException e){
		System.out.println("Bad I/O");
	}
	//return aWriter;
}


static void predictByChineseCalendar(Vector<DrawInfor> allLines){
	//allLines are descending
	DrawInfor aDraw=allLines.get(allLines.size()-1);
	String firstDate=aDraw.date;
	
	HashMap<String, int[]> datedData=new HashMap<String, int[]>();
	for (int d=0; d<allLines.size(); d++){
		//DrawInfor 
		aDraw=allLines.get(d);
		datedData.put(aDraw.date, aDraw.data);		
	}
	
	int testCaseCount=512;//30*39;
	//int sampleRefSize=testCaseCount+12;
			
	BufferedWriter aWriter=null;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("byChinese"+tomorrowWkDay+".txt")));
		int[] caseId={1, 2, 3, 4, 5, 6, 7};
		int[][] hitCases=new int[caseId.length][6];
		int[][] lastHitCases=new int[caseId.length][6];
		
		int[][][] localPerformanceList=new int[caseId.length][6][testCaseCount];
		for (int i=0; i<caseId.length; i++){
		//String[] showPredictions=new String[fitRange.length];
			for (int x=0; x<6;x++)
					Arrays.fill(localPerformanceList[i][x], 0);
			
				Arrays.fill(hitCases[i], 0);
				Arrays.fill(lastHitCases[i], 0);
		}
		int[][][] predictionTS=new int[caseId.length][testCaseCount+1][];
		
		int[] hitCount=new int[40];
		Arrays.fill(hitCount, 0);
		//double[][] ovdTS=new double[40][sampleRefSize];
		String sPredictions="";
		int iLen=allLines.size();
	
	//int[][] occurrence=new int[40][iLen];
	
	
	for (int iTest=testCaseCount; iTest>=0; iTest--){
		int tsPos=iLen-iTest;
		
		int[] draws=new int[5];
		Arrays.fill(draws, 0);
		String sDraws="Draws:( ";
		if (iTest > 0) draws=Arrays.copyOf(allLines.get(iTest-1).data, 5);
		for (int s=0; s<5; s++) sDraws += draws[s]+", ";
		int kday=iTest-1;
		if (kday < 0) kday=0;
		String wkDate=allLines.get(kday).date;
		int shift=0;
		for (int ic=0; ic<caseId.length; ic++){
			int[] predictions=new int[40];
			Arrays.fill(predictions, 0);
			Calendar //calendar=GregorianCalendar.getInstance();
			calendar=new GregorianCalendar(
							Integer.parseInt(wkDate.substring(0, 4)),//+1911,
							Integer.parseInt(wkDate.substring(5, 7))-1,
							Integer.parseInt(wkDate.substring(8, 10)));	
			if (iTest==0){
				calendar.add(GregorianCalendar.DATE, 1);
				if (calendar.get(GregorianCalendar.DAY_OF_WEEK)==0)
							calendar.add(GregorianCalendar.DATE, 1);
			}
			calendar.add(GregorianCalendar.DATE, -60*(caseId[ic]+shift));
			String chkDate=""+calendar.get(GregorianCalendar.YEAR)+"/";
			int mm=calendar.get(GregorianCalendar.MONTH)+1;
			if (mm < 10) chkDate += "0";
			chkDate += (mm+"/");
			mm=calendar.get(GregorianCalendar.DATE);
			if (mm < 10) chkDate += "0";
			chkDate += mm;
									
			int[] tmp=datedData.get(chkDate);
			if (ic < caseId.length-1){
				if (tmp==null && chkDate.compareTo(firstDate)>0) {
					if (shift==0) { shift++;
					ic--; 
					continue;
					}
					//predicts=new int[5];
					//Arrays.fill(predicts, 0);
				}
			}
			
			if (tmp==null) { tmp=new int[1]; tmp[0]=0;}
			int[] predicts=Arrays.copyOf(tmp, tmp.length);
			if (ic == caseId.length-1){
				//Arrays.fill(predicts, 0);
				//getPredictionFrom3Match(predictions, tsPos);
				int[] refDraw=allLines.get(iTest).data;
				int iPos=iTest;
				if (iTest==0) {
					System.out.println("Checking in location:"+iPos);
				}
				boolean found=false;
				while (iPos < iTest+234 && !found)
				{
					int[] checkDraw=allLines.get(++iPos).data;
					int iC=0;
					for (int iref=0; iref<5; iref++){
						for (int ick=0; ick<5; ick++){
							if (refDraw[iref]!=checkDraw[ick]) continue;
							iC++;
							break;
						}
						if (iC ==3) break;						
					}
					if (iC<3) continue;
					found=true;
					break;
				}

				if (found){
					if (iTest==0) {
					System.out.println("Found in location:"+iPos);
					}
					int[] checkDraw=allLines.get(--iPos).data;					
					for (int ick=0; ick<5; ick++)
						predictions[checkDraw[ick]]=1;
				}
			} else {
			for (int i=0; i<predicts.length; i++){				
					predictions[predicts[i]]=1;					
			}
			}
			String sHit="<<<hits:( ";
			//String sHit="<<<hits:( ";
			int iHit=0;
			
			if (iTest > 0){
				for (int s=0; s<5; s++){
					if (predictions[draws[s]]==1) {
						sHit += ""+draws[s]+", ";
						iHit++;
					}
				}
			}
			int iB=0;
			int iSol=0;
			sPredictions="predictions:( ";
			//if (iTest > testCaseCount) continue;
			for (int ib=1; ib<40; ib++){
				if (predictions[ib]<1) continue;
				
				predictions[iSol++]=ib;
				sPredictions += ib+", ";
			}
			predictionTS[ic][testCaseCount-iTest]=Arrays.copyOf(predictions, iSol);

			aWriter.write("------ Test "+iTest+" Results --------------  "+iHit); aWriter.newLine();
			///aWriter.write(predx); aWriter.newLine();
			aWriter.write(sDraws+") ==> "+sHit+")>>>>"); aWriter.newLine();
			aWriter.write(sPredictions+") !!"+iHit); aWriter.newLine();
			if (iTest > 0){
				hitCases[ic][iHit]++;
				lastHitCases[ic][iHit]=iTest;				
				localPerformanceList[ic][iHit][testCaseCount-iTest]=1;
			}
		
		aWriter.write("==========================================="); aWriter.newLine();
		}
	}
	String myName="Chinese Calendar fit";
	JackpotReader.showSummaryPage(predictionTS, hitCases, lastHitCases, localPerformanceList, myName, aWriter);


	aWriter.close();
	
}  catch (IOException e){
		System.out.println("Bad I/O");
	}
	//return aWriter;
}

	public static void main(String[] args) 
	{
		//updateDrawData();
		Lottery539PageParser aParser=new Lottery539PageParser("539");
		File history=new File(dataCenter+"History.txt");
		if (!history.exists()) readFromWeb();
		
		Vector<DrawInfor> allLines=updateAndGetDrawData();
		
		if (allLines==null) {
			allLines=new Vector<DrawInfor>();
			BufferedReader aReader = null;
			try {
				aReader = new BufferedReader(new FileReader(history));
				String aLine = null;//aReader.readLine();
				while ((aLine = aReader.readLine()) != null) {
					if (aLine.length() < 5)
						continue;
					int i0=0;
					while (aLine.charAt(i0) <'0' || aLine.charAt(i0) >'9') i0++;
					int ix = aLine.indexOf('(');
					String date1 = aLine.substring(i0, ix);
					int i9 = aLine.indexOf(')');
					int wkDay = Integer.parseInt(aLine.substring(ix + 1, i9));
					int[] datas = getDrawData(aLine.substring(i9 + 2));
					allLines.add(aParser.getOneDrawInfo(date1, wkDay, datas));
				}
				aReader.close();
			} catch (FileNotFoundException e) {
				System.out.println("No file");
			} catch (IOException e) {
				System.out.println("Bad I/O");
			}
		}
		
		tomorrowWkDay=allLines.get(0).wkDay+1;
		if (tomorrowWkDay > 6) tomorrowWkDay=1;
		int[] data=allLines.get(0).data;
		//listThreeFitAfterN1Fit(allLines);
		predictByMatchN(allLines);
		int[][] allData=buildStatisticsPage(allLines);
		predictByDataAfter(allLines);
		getTriple(allData);
		getQuarTriple(allData);
		predictByChineseCalendar(allLines);
		
		/*
		//File hisFile=new File(statisticsFile);
		Path pathHistory = FileSystems.getDefault().getPath(statisticsFile);
		Path pathToday = FileSystems.getDefault().getPath(todayPredictionFile);
		try {
			Files.copy(pathHistory, pathToday);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
		
	
		
/*
 <tr>
      <td width="16%" align="center"><font color="#0066cc"><b>3</b></font></td>
      <td width="16%" align="center"><font color="#0066cc"><b>2007/01/03</b></font></td>
      <td width="17%" align="center"><b>22&nbsp;,&nbsp;23&nbsp;&nbsp;27&nbsp;,&nbsp;29&nbsp;,&nbsp;30&nbsp;</b></td>
      <td width="11%" align="center"><font color="#FF0000"><b></b></font></td>
      <td width="23%" align="center"><b><font color="#0066cc">無</font></b></td>
    </tr>


</table> 
 </div> 
 </div>
<div class="tbds"><label class="bigimg"> 

*/
}

