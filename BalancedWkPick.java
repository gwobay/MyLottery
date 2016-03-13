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
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import static java.nio.file.StandardCopyOption.*;

public class BalancedWkPick extends WebPageParser	
{
private static DecimalFormat dataIntF=new DecimalFormat("00");
private final static Calendar today=Calendar.getInstance();
private static int thisYear= today.get(Calendar.YEAR);
private static int thisMonth=today.get(Calendar.MONTH)+1;
private static int thisDay=today.get(Calendar.DAY_OF_MONTH);
private static int todayInt=10000*(thisYear % 1000)+100*thisMonth+thisDay;
static String dataCenter="C:\\Users\\eric\\projects\\datacenter\\";
private static int tomorrowWkDay=0;

	public BalancedWkPick(String myName)
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
		BalancedWkPick aParser=new BalancedWkPick("539");
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
BalancedWkPick aParser=new BalancedWkPick("539");
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
		if (readData==null) return null;
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

static Vector<DrawInfor> readHistoryData(){

//String baesUrl="http://www.taiwanlottery.com.tw/lotto/DailyCash/history.aspx";
//String startFrom="D539Control_history1_dlQuery";//開獎日期";
//String endWith = "intx01";
//Calendar calendar0=Calendar.getInstance();
BalancedWkPick aParser=new BalancedWkPick("539");
//
Vector<DrawInfor> allLines=null;

//Vector<String> tmpStorage=new Vector<String>();
BufferedReader mReader;
try {
	allLines=new Vector<DrawInfor>();
	
	//File historyF=new File(dataCenter+"History.txt");
	mReader=new BufferedReader(new InputStreamReader(new FileInputStream(dataCenter+"History.txt"),  "UTF-8"));
	String aLine=null;
	
	while (aLine != null){
		int i0=0;
		while (aLine.charAt(i0)<'0' || aLine.charAt(i0)>'9') i0++;
		String cleanLine=aLine.substring(i0);
		int ix=cleanLine.indexOf('(');
		String date1=cleanLine.substring(0,  ix);
		int i9=cleanLine.indexOf(')');
		int wkDay=Integer.parseInt(cleanLine.substring(ix+1, i9));
		int[] datas=getDrawData(cleanLine.substring(i9+2));
		allLines.add(aParser.getOneDrawInfo(date1, wkDay, datas));
		aLine=mReader.readLine();				
	} 
	
	mReader.close();
	
	} catch (FileNotFoundException e){
	System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
	}

	return allLines;

}

static void updateDrawData(){

String baesUrl="http://www.taiwanlottery.com.tw/lotto/DailyCash/history.aspx";
String startFrom="D539Control_history1_dlQuery";//開獎日期";
String endWith = "intx01";
//Calendar calendar0=Calendar.getInstance();
BalancedWkPick aParser=new BalancedWkPick("539");
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

public static void fillWkListAndAllTimeLeast(DrawInfor[] allLines, int[] wkTop, int[] ballCounts, int[] bestWk) 
{
	//wkTop and ballCounts will be filled with n*100+ib
	//
	Arrays.fill(wkTop, 0);
	Arrays.fill(ballCounts, 0);
	Arrays.fill(bestWk, 0);
	
	DrawInfor aDraw=allLines[0];
	//String[] terms=aDraw.date.split("/");
	//String mm=aDraw.date.substring(5, 7);
	//int currentMonth=Integer.parseInt(terms[1]);
	//String drawLine=aDraw.toString();
	int predictWeekday=aDraw.wkDay;
	if (++predictWeekday ==7) predictWeekday=1;
	//int[] currentMonthDraw=new int[40];
	//for (int i=0; i<monthlyStatistics.length; i++)
	//Arrays.fill(monthlyStatistics[i], 0);
	for (int i=0; i<weeklyStatistics.length; i++)
		Arrays.fill(weeklyStatistics[i], 0);
	double iTotal=0;
	int checkRange=allLines.length;
	//checkRange=36;
	for (int i=1; i<40; i++){
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
	int[] allBalls=new int[40];	
	Arrays.fill(allBalls, 0);
	
	int[] lastOnAt=new int[40];
	Arrays.fill(lastOnAt, -1);
	int[] lastShownAt=new int[40];
	Arrays.fill(lastShownAt, 0);
	//boolean finishAddCurrentMonth=false;
	int[] bestWeek=new int[40];
	Arrays.fill(bestWeek, 0);
	//Vector<int[]> retV=new Vector<int[]>();
	int[][] drawLines=new int[5][checkRange];
	for (int i=0; i<checkRange; i++){
		aDraw=allLines[i];
		int[] hits=aDraw.data;
		///=aDraw.date.split("/");
		//String mm=aDraw.date.substring(5, 7);
		//int month=Integer.parseInt(terms[1]);
		//int month=Integer.parseInt(aDraw.date.substring(5,7));
		//if (month != currentMonth) finishAddCurrentMonth=true;
		for (int ik=0; ik<hits.length; ik++){
			allBalls[hits[ik]]++;
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
			//monthlyStatistics[hits[ik]][month]++;
			//if (!finishAddCurrentMonth){				
				//currentMonthDraw[hits[ik]]++;
			//}
			weeklyStatistics[hits[ik]][aDraw.wkDay]++;
			if (lastShownAt[hits[ik]]==0) lastShownAt[hits[ik]]=i+1;
		}
	}
	
	for (int ib=1; ib<40; ib++){		
		ballCounts[ib]=allBalls[ib]*100+ib;
		int bestWk1=0;
		for (int k=1; k<7; k++){
			if (weeklyStatistics[ib][k] > bestWk1) {
				bestWk1=weeklyStatistics[ib][k];
				bestWk[ib]=bestWk1*100+k;
			}
		}
	}
	Arrays.sort(ballCounts);
	
	//int[] tmpPick=new int[40];
	//Arrays.fill(tmpPick, 0);
	
	int[] predictForWeekday=new int[40];
	Arrays.fill(predictForWeekday, 0);
	
	for (int ib=1; ib<40; ib++){		
		wkTop[ib]=weeklyStatistics[ib][predictWeekday]*100+ib;
		//if (ib > 34) tmpPick[allBalls[ib] % 100]=1;
	}	
	Arrays.sort(wkTop);	
}

public static int[] predictByWkTopWithAllTimeLeast(DrawInfor[] allLines) 
{
	DrawInfor aDraw=allLines[0];
	String[] terms=aDraw.date.split("/");
	//String mm=aDraw.date.substring(5, 7);
	//int currentMonth=Integer.parseInt(terms[1]);
	//String drawLine=aDraw.toString();
	int[] pickCriteria={1, 2, 3};
	//1 : simple pick 5 wk top and 5 all time least
	// 2: pick 3 wk top and all time less than avg
	// 3: pick all matched best week and 5 all time least
	int predictWeekday=aDraw.wkDay;
	if (++predictWeekday ==7) predictWeekday=1;
	//int[] currentMonthDraw=new int[40];
	//for (int i=0; i<monthlyStatistics.length; i++)
	//Arrays.fill(monthlyStatistics[i], 0);
	for (int i=0; i<weeklyStatistics.length; i++)
		Arrays.fill(weeklyStatistics[i], 0);
	double iTotal=0;
	int checkRange=allLines.length;
	//checkRange=36;
	for (int i=1; i<40; i++){
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
	int[] allBalls=new int[40];	
	Arrays.fill(allBalls, 0);
	
	int[] lastOnAt=new int[40];
	Arrays.fill(lastOnAt, -1);
	int[] lastShownAt=new int[40];
	Arrays.fill(lastShownAt, 0);
	boolean finishAddCurrentMonth=false;
	int[] bestWeek=new int[40];
	Arrays.fill(bestWeek, 0);
	//Vector<int[]> retV=new Vector<int[]>();
	int[][] drawLines=new int[5][checkRange];
	for (int i=0; i<checkRange; i++){
		aDraw=allLines[i];
		int[] hits=aDraw.data;
		///=aDraw.date.split("/");
		//String mm=aDraw.date.substring(5, 7);
		//int month=Integer.parseInt(terms[1]);
		//int month=Integer.parseInt(aDraw.date.substring(5,7));
		//if (month != currentMonth) finishAddCurrentMonth=true;
		for (int ik=0; ik<hits.length; ik++){
			allBalls[hits[ik]]++;
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
			//monthlyStatistics[hits[ik]][month]++;
			//if (!finishAddCurrentMonth){				
				//currentMonthDraw[hits[ik]]++;
			//}
			weeklyStatistics[hits[ik]][aDraw.wkDay]++;
			if (lastShownAt[hits[ik]]==0) lastShownAt[hits[ik]]=i+1;
		}
	}
	
	for (int ib=1; ib<40; ib++){		
		allBalls[ib]=allBalls[ib]*100+ib;
		int bestWk=0;
		for (int k=1; k<7; k++){
			if (weeklyStatistics[ib][k] > bestWk) {
				bestWk=weeklyStatistics[ib][k];
				bestWeek[ib]=bestWk*10+k;
			}
		}
	}
	Arrays.sort(allBalls);
	int[] tmpPick=new int[40];
	Arrays.fill(tmpPick, 0);
	
	int[] predictForWeekday=new int[40];
	Arrays.fill(predictForWeekday, 0);
	
	for (int ib=1; ib<40; ib++){		
		predictForWeekday[ib]=weeklyStatistics[ib][predictWeekday]*100+ib;
		if (ib > 34) tmpPick[allBalls[ib] % 100]=1;
	}	
	Arrays.sort(predictForWeekday);
	for (int i=39; i>=35; i--){
		//if (predictForWeekday[i] < 1) continue;
		tmpPick[predictForWeekday[i] % 100]=1;	
	}
	int[] predictions=new int[10];
	int k=0;
	for (int i=39; i>0; i--){
		if (tmpPick[i] < 1) continue;
		predictions[k++]=i;		
	}
	return Arrays.copyOf(predictions, k);
}


static int[] verifyPerformance(DrawInfor[] allDrawList){
	//double[] pdf=getRangePDF(0, allSamples.get(0).length, allSamples);;
	//data is descending
	int[] pickCriteria={0, 1, 2, 3, 4, 5, 6, 7};
	//1 : simple pick 5 wk top and 5 all time least
	// 2: pick all matched best week and 5 all time least
	
	// 3: pick 5 wk top and all time less than avg
	//	4: pick 5 wk top and all time less than avg-var/2
	// 5: only the wk top 8
	// 6: only the wk best top 
	// 7: only the wk top 5
	// 8: only the wk best top 5
	String myName="BalancedWkPick";
	int verifyLength=512;
	BufferedWriter aWriter;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("try"+myName+"1.txt")));						
		String sPred="Predic:(";
		int iLen=allDrawList.length;
	//Vector<Vector<double[] > > dailyResults=new Vector<Vector<double[] > >();
		//JackpotReader.initPerformanceList(verifyLength, 0);
		int[][] hitCounts=new int[pickCriteria.length][6];
		int[][] lastHitLocation=new int[pickCriteria.length][6];
		
		int[][][] localPerformanceList=new int[pickCriteria.length][6][verifyLength];
		int[][][] predictionTS=new int[pickCriteria.length][verifyLength+1][];
		//Arrays.fill(predictionTS, 0);
		for (int i=0; i<pickCriteria.length; i++){
		Arrays.fill(hitCounts[i], 0);
		Arrays.fill(lastHitLocation[i], 0);
		
		for (int x=0; x<6;x++)
			Arrays.fill(localPerformanceList[i][x], 0);
		}
	for (int iTest=verifyLength; iTest>=0; iTest--){
		
		int[] wkTop=new int[40];
		int[] allTimeLeast=new int[40];
		int[] bestWk=new int[40];
		DrawInfor[] sampleData=Arrays.copyOfRange(allDrawList, iTest, iLen);
		fillWkListAndAllTimeLeast(sampleData, wkTop, allTimeLeast, bestWk );
		int[] hits=null;
		
		if (iTest > 0)
			hits=allDrawList[iTest-1].data;	
		else {
			hits=new int[5];
			Arrays.fill(hits, 0);
		}
		for (int ic=0; ic<pickCriteria.length; ic++){
			int[] tmpPick=new int[40];
			Arrays.fill(tmpPick, 0);
			
			if (ic==0){
				for (int i=39; i>34; i--){
					tmpPick[wkTop[i] % 100]=1;
					tmpPick[allTimeLeast[39-i+1] % 100]=1;
				}
			}
			if (ic==1){
				for (int i=1; i<4; i++){
					//tmpPick[wkTop[i] % 100]=1;
					tmpPick[allTimeLeast[i] % 100]=1;
				}
				for (int i=39; i>0; i--){
					int ball=wkTop[i] % 100;
					int qty=wkTop[i] / 100;
					if (qty==bestWk[ball]/100)
					tmpPick[ball]=1;
				}
			}
			if (ic==4){
				for (int i=39; i>31; i--){
					tmpPick[wkTop[i] % 100]=1;
					//tmpPick[allTimeLeast[39-i+1] % 100]=1;
				}
			}  else
				if (ic==5){
					for (int i=39; i>0; i--){
						int ball=wkTop[i] % 100;
						int qty=wkTop[i] / 100;
						if (qty==bestWk[ball]/100)
						tmpPick[ball]=1;
					}
				} else
				if (ic==6){
					for (int i=39; i>34; i--){
						tmpPick[wkTop[i] % 100]=1;
						//tmpPick[allTimeLeast[39-i+1] % 100]=1;
					}
				} else
				if (ic==7){
					int ik=0;
					for (int i=39; i>0; i--){
						int ball=wkTop[i] % 100;
						int qty=wkTop[i] / 100;
						if (qty!=bestWk[ball]/100) continue;
						tmpPick[ball]=1;
						ik++;
						if (ik == 5) break;
					}
				} else
			if (ic > 1){
				for (int i=1; i<3*(ic-1); i++){
					tmpPick[wkTop[40-i] % 100]=1;
					//tmpPick[allTimeLeast[i] % 100]=1;
				}
				int[] ballCounts=new int[40];
				double avg=0;
				for (int i=39; i>0; i--){
					int ball=allTimeLeast[i] % 100;
					ballCounts[ball]=allTimeLeast[i] / 100;
					avg += ballCounts[ball];
				}
				avg /= 39;
				double r=0;
				for (int i=39; i>0; i--){
					r += (ballCounts[i]-avg)*(ballCounts[i]-avg);
				}
				r = Math.sqrt(r/39);
				double cc=avg - r*0.707;
				
				if (ic > 2)
					cc = avg - r;
				 
				for (int i=39; i>0; i--){
					if (ballCounts[i] < cc)
					tmpPick[i]=1;
				}
				if (iTest == 0) {
					System.out.println("Avg="+avg+"; R="+r);
				}
			}
			int pCount=0;
			for (int i=0; i<40; i++){
				if (tmpPick[i]==1){
					tmpPick[pCount++]=i;
				}
			}
		int[] allList=Arrays.copyOf(tmpPick, pCount);
				//predictByWkTopWithAllTimeLeast(Arrays.copyOfRange(allDrawList, iTest, iTest+256));
		
			
			
			int iHitC=0;
			
			
			sPred="Predic:(";
			for (int k=0; k<allList.length; k++)
				sPred += ""+allList[k]+", ";
						
			int[] pList=Arrays.copyOf(allList, allList.length);
			predictionTS[ic][verifyLength-iTest]=pList;
			String sDraw="++++++++++++++Draw:(";
			String sHits="Hits:(";
			for (int s=0; s<5; s++){
				sDraw += ""+hits[s]+", ";
				if (hits[s]==0) continue;
				for (int k=0; k<pList.length; k++){
					//if (pList[k]==0) continue;
					if (hits[s] != pList[k]) continue;
					iHitC++;
					sHits += ""+hits[s]+", ";
					break;
				}
			}
			hitCounts[ic][iHitC]++;
			if (iTest > 0)
				localPerformanceList[ic][iHitC][verifyLength-iTest]=1;
			if (iTest >0)
			lastHitLocation[ic][iHitC]=iTest;
			aWriter.write("=================== "+(iTest)+" ======");aWriter.newLine();
			aWriter.write(sPred+")");aWriter.newLine();
			aWriter.write(sDraw+")");aWriter.newLine();
			aWriter.write(sHits+")==>"+iHitC);aWriter.newLine();
/*
		*/
			aWriter.write("-----------------------------");aWriter.newLine();	
		}
	}
	JackpotReader.showSummaryPage(predictionTS, hitCounts, lastHitLocation, localPerformanceList, myName, aWriter);
	//JackpotReader.addSummary4NextDayPrediction(predictionTS, hitCounts, lastHitLocation, localPerformanceList, myName);
	
		aWriter.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return null;//retV;
}

	public static void main(String[] args) 
	{
		//updateDrawData();
		BalancedWkPick aParser=new BalancedWkPick("539");
		File history=new File(dataCenter+"History.txt");
		if (!history.exists()) readFromWeb();
		
		Vector<DrawInfor> allLines=null;
		
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
			
		tomorrowWkDay=allLines.get(0).wkDay+1;
		if (tomorrowWkDay==7) tomorrowWkDay=1;
		DrawInfor[] allDrawList=new DrawInfor[allLines.size()];
		allDrawList=allLines.toArray(allDrawList);
				
		verifyPerformance(allDrawList);
		
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

