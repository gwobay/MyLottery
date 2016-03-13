import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
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
import java.util.Vector;

import static java.nio.file.StandardCopyOption.*;

public class Lottery539DataReader extends WebPageParser	
{
private static DecimalFormat dataIntF=new DecimalFormat("00");
private final static Calendar today=Calendar.getInstance();
private static int thisYear= today.get(Calendar.YEAR);
private static int thisMonth=today.get(Calendar.MONTH)+1;
private static int thisDay=today.get(Calendar.DAY_OF_MONTH);
private static int todayInt=10000*(thisYear % 1000)+100*thisMonth+thisDay;
static String dataCenter="C:\\Users\\eric\\projects\\datacenter\\";

	public Lottery539DataReader(String myName)
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
		Lottery539DataReader aParser=new Lottery539DataReader("539");
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
static int[][] weeklySum=new int[40][7];
static DecimalFormat dF=new DecimalFormat("0.00");
//static DecimalFormat dI=new DecimalFormat("00");
static final int sampleCounts=39*39;

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
			weeklySum[hits[ik]][aDraw.wkDay]++;
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

static void createPngFile(String fileName, Vector<int[]> dataSamples, int maxNumber, int totalPoints)
{
	int width=3*(totalPoints+5), height=width*3/4;
	BufferedImage bi=ImgFactory.getImg(width, height); //new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	Graphics g=bi.getGraphics();
	
	DataCharting aCharter=new DataCharting();
	aCharter.maxNumber=maxNumber;
	aCharter.totalPoints=totalPoints;
	aCharter.fillImg(bi, width, height, dataSamples);
	Graphics2D g2=(Graphics2D)g;
	g2.getBackground();
        
	g2.drawImage(bi, null, 0, 0);
	
	String dFF=fileName;
	File myNew=new File(dFF+".png");
	if (aCharter.putImg2File(bi, myNew) ){
		File myDup=new File(dFF+"_2.png");
		File myOld=new File(dFF+"Old.png");
		if (myOld.exists()) myOld.delete();
		if (myDup.exists() && !myOld.exists()) myDup.renameTo(myOld);
		myNew.renameTo(myDup);
	}
	return;
}

public static double[] getSineCurveCoeff(double[] timeLine, int offSet){
	
	double predV=0;
	
	double pI=Math.PI;//*2;
	int n=timeLine.length;
	
	//boolean[] bFound=new boolean[575757];
	//Arrays.fill(bFound, false);
	double[] retV=new double[2];
	
			//double offSet=45;
			Vector<double[]> mSin=new Vector<double[]>();
			//double[] values=new double[n];
			for (int x=0; x<n; x++){				
				double[] vSin=new double[n];
				for (int v=0; v<n; v++){
					vSin[v]=Math.sin(offSet+x*pI/(v+1));
				}
				mSin.add(vSin);
			}
			MyMatrix mFit=new MyMatrix(mSin);
			MyMatrix iv=MyMatrix.inverseLU(mFit);
			if (iv==null) return null;
			
			double[] pCoeff=iv.multiply(timeLine);
			double[] checkBack=mFit.multiply(pCoeff);
			for (int ik=0; ik<n; ik++){
				if (Math.abs(checkBack[ik] - timeLine[ik]) > 0.01){
					System.out.println("try "+n+" found invalid inverse matrix for element "+ik+" mismatch:"+dF.format(checkBack[ik])+" != "+dF.format(timeLine[ik]));
					return null;
				}
			}
			return pCoeff;

}

static int[] getFittingByMinError(Vector<int[]> inData, int chkCount){
	int[] outResult=new int[5];
	//double[] pdf=getRangePDF(0, inData.get(0).length, inData);
	int iSet=inData.size();
	//if (measuredAvg==null || measuredAvg.length < inData.size()){
	
		//double[] iValue=new double[inData.get(0).length];
		//Arrays.fill(iValue, 0);		
		double[] measuredAvg=new double[iSet];
		Arrays.fill(measuredAvg, 0);
		for (int s=0; s<iSet; s++){
			//outResult.add(Arrays.copyOf(iValue, iValue.length));
			int k=inData.get(s).length;
			for (int j=0; j<k; j++)
			{
				//measuredAvg[s] += (inData.get(s)[j]*measuredPDF[inData.get(s)[j]]);
				measuredAvg[s] += (inData.get(s)[j]);//*measuredPDF[inData.get(s)[j]]);
			}
			measuredAvg[s] /= k;		
		}
	//}
	
	int iLen=inData.get(0).length;
	
	for (int s=0; s<iSet; s++){
		//int k=39*7;//390;//
		
		//float fMax=-9999999, fMin=999999;
		//int maxAt=0, minAt=0;
		double[] fValue=new double[iLen];
		Arrays.fill(fValue, 0);
		double[] checkCurve=new double[chkCount];
		Arrays.fill(checkCurve, 0);
		
		Vector<double[]> tempBuffer=new Vector<double[]>();
		double minCaseValue=10000000;		
		int minWhen=0;
		double pI=Math.PI;
		//for (int  rd=0; rd < 27; rd ++){
			int offSet = 45;//5+rd*5;
			double minValue=10000000;
			for (int j=0; j<chkCount; j++)
			{
				checkCurve[j]=inData.get(s)[iLen-chkCount+j]-measuredAvg[s];
			}
		double[] coeffs=getSineCurveCoeff(checkCurve, offSet);
		if (coeffs==null ) continue;
		/* use
		 * for (int x=0; x<n; x++){ 
		 * for (int v=0; v<n; v++){
					vSin[v]=Math.sin(offSet+x*pI/(v+1));
				}
		 */
		int minAt=-1;
		for (int j=chkCount; j < iLen-1; j++)//iLen-2; j>=chkCount; j--)
		{			
			double errV=0;			
			for (int x=0; x<chkCount; x++){
				checkCurve[x]=inData.get(s)[j-chkCount+x+1]-measuredAvg[s];			
				double r=0;
				for (int v=0; v<chkCount; v++){
					r += coeffs[v]*Math.sin(offSet+x*pI/(v+1));
				}
				errV += Math.abs(r-checkCurve[x]);
			}
			fValue[j]=errV;	
			if (errV < minValue) {
				minValue=errV;	
				minAt=j;
			}
		}
		
		outResult[s]=inData.get(s)[minAt+1];
		}
			
	return outResult;
}


static void projectByTopListCurveFitting(Vector<int[]> allSamples, String fileName, int[] nextDayTopList){
	//sample is daily position in TS, ascending
	// daily draw number has the 10s added with 50
	// dailyTopList is the nextday's list
	// allRefD1 is the original daily draws
	//predicted by 1: 1 predict per pick
	//             2: 2 predicts per pick
	int[][] dailyHitLocation=new int[5][allSamples.size()];
	int[][] dailyDraw=new int[5][allSamples.size()];
	for (int d=0; d<allSamples.size()-1; d++){
		int s=0;
		for (int b=0; b<40; b++){
			int k=allSamples.get(d)[b] % 100;
			if (k < 50) continue;
			dailyHitLocation[s][d]=b;
			dailyDraw[s][d]=k-50;
			s++;
		}
		
	}
	for (int s=0; s<5; s++){
		dailyDraw[s][allSamples.size()-1]=0;
		dailyHitLocation[s][allSamples.size()-1]=0;
	}
	int iLen=allSamples.size();
	int[] checkList={6,9,12};
	int[][] hitCounts=new int[2*checkList.length][6];
	int[][] lastHitLocation=new int[2*checkList.length][6];
	//JackpotReader.initPerformanceList(testCaseCount, 0);
	int[][][] localPerformanceList=new int[2*checkList.length][6][testCaseCount];
	int[] ttPredicts=new int[2*checkList.length];
	int[] ttHits=new int[2*checkList.length];
	for (int i=0; i<2*checkList.length; i++){
		Arrays.fill(hitCounts[i], 0);
		Arrays.fill(lastHitLocation[i], 0);
		for (int x=0; x<6;x++)
		Arrays.fill(localPerformanceList[i][x], 0);		
	}
	int[][][] predictionTS=new int[2*checkList.length][testCaseCount][];
	String[] showPrediction=new String[2*checkList.length];
	//String myName=getClass().getName();
	BufferedWriter aWriter;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName+".txt")));						
	//Vector<Vector<double[] > > dailyResults=new Vector<Vector<double[] > >();
	for (int iTest=testCaseCount; iTest>0; iTest--){
		int[] hits=new int[5];
		int[] lastHits=new int[5];
		Arrays.fill(hits, 0);
		Arrays.fill(lastHits, 0);
		Vector<int[]> checkSample=new Vector<int[]>();
		for (int s=0; s<5; s++){
			//if (iTest > 0)
				hits[s]=dailyDraw[s][iLen-iTest];
			lastHits[s]=dailyDraw[s][iLen-iTest-1];
			checkSample.add(Arrays.copyOfRange(dailyHitLocation[s], 0, iLen-iTest+1));			
		}
		
		for (int iCk=0; iCk<checkList.length; iCk++){
			int checkCount=checkList[iCk];
			int[] predictBalls=new int[40];
			Arrays.fill(predictBalls, 0);
		//dailyResults.add(
		int[] allList=
				getFittingByMinError(checkSample, checkCount);
		if (allList==null) continue;
		for (int iCase=0; iCase< 2; iCase++){
			int[] dPredict=new int[allList.length*(iCase+1)];
			Arrays.fill(dPredict, 0);
			int iCheck=iCk*2+iCase;
			int iHitC=0;
			
			showPrediction[iCheck]="Predic:(";
			for (int k=0; k<allList.length; k++) 
			{
				dPredict[iCase*k]=allSamples.get(iLen-iTest)[allList[k]]%100;
				int pVal=dPredict[iCase*k];
				if (pVal > 40) pVal -= 50;
				if (predictBalls[pVal]==0) ttPredicts[iCheck]++;
				predictBalls[pVal]=1;
				if (dPredict[iCase*k] > 50) predictBalls[pVal]=11;
				if (iCase >0){
					int dPos=allList[k]+1;
					if (allList[k] <= lastHits[k])
					dPos=allList[k]-1;
					if (dPos > 39) dPos = allList[k]-1;
					else
						if (dPos < 1) dPos = allList[k]+1;
					dPredict[iCase*k+1]=allSamples.get(iLen-iTest)[dPos] % 100;
					pVal=dPredict[iCase*k+1];
					if (pVal > 50) pVal -= 50;
					if (predictBalls[pVal]==0) ttPredicts[iCheck]++;
					predictBalls[pVal]=1;
					if (dPredict[iCase*k+1] > 50) predictBalls[pVal]=11;
				}
				
			}
			
			String sDraw="++++++++++++++Draw (";
			String sHits="Hits :(";
			
			ttPredicts[iCheck]=0;
			int iPredict=0;
			for (int k=1; k<40; k++){
				if (predictBalls[k]==0) continue;
				showPrediction[iCheck] += ""+k+", ";
				ttPredicts[iCheck]++;
				if (predictBalls[k]==11){
					sHits += k+", ";
					iHitC++;
					ttHits[iCheck]++;
				}
				predictBalls[iPredict++]=k;
			}
			predictionTS[iCheck][testCaseCount-iTest]=Arrays.copyOfRange(predictBalls, 0, iPredict);
			for (int s=0; s<5; s++){
				sDraw += ""+hits[s]+", ";
			}
			hitCounts[iCheck][iHitC]++;
			if (iTest>1) {
				lastHitLocation[iCheck][iHitC]=iTest;
				localPerformanceList[iCheck][iHitC][testCaseCount-iTest]=1;
			}
			/*
			else {
				showPrediction[iCheck]="Predic:(";
				for (int k=0; k<predictBalls.length; k++){
					if (predictBalls[k]==1)
					showPrediction[iCheck] += ""+(nextDayTopList[k] % 100)+", ";
				}
			}*/
			aWriter.write("=================== "+(iTest-1)+" ==Case="+iCase);aWriter.newLine();
			aWriter.write(showPrediction[iCheck]+")");aWriter.newLine();
			aWriter.write(sDraw+")");aWriter.newLine();
			aWriter.write(sHits+")==>"+iHitC);aWriter.newLine();
/*
		*/
			aWriter.write("-----------------------------");aWriter.newLine();	
	 }
		}
	}
	String myName="ByDailyPosCurveFit";
	aWriter.write("=================== ByDailyPosCurveFit ======");aWriter.newLine();
	JackpotReader.showSummaryPage(predictionTS, hitCounts, lastHitLocation, localPerformanceList, myName, aWriter);
	/*
	for (int iCheck=0; iCheck<checkList.length; iCheck++){
		//for (int i=0; i<6; i++){
		for (int iCase=0; iCase<2; iCase++){
		aWriter.write("...chkCount.... "+checkList[iCheck]+" . case."+iCase);aWriter.newLine();
			JackpotReader.performanceList=localPerformanceList[2*iCheck+iCase];
			double[] nextCasePdf=JackpotReader.getNextCasePdf();
			double[] caseMeanVar=JackpotReader.getHitCasesMeanVariant();
			
		aWriter.write("------"+checkList[iCheck]+" has "+showPrediction[2*iCheck+iCase]);aWriter.newLine();
		for (int s=0; s<6; s++){
			aWriter.write("Hits "+s+" : ["+hitCounts[2*iCheck+iCase][s]+"]@"+lastHitLocation[2*iCheck+iCase][s]);
			aWriter.write("   	??"+dF.format(nextCasePdf[s]));
			//int days=hitCounts[s]*lastHitLocation[s];
			if (caseMeanVar[s] > 0 && lastHitLocation[2*iCheck+iCase][s] > caseMeanVar[s]) {
				double ovdToVar=(lastHitLocation[2*iCheck+iCase][s] - caseMeanVar[s])/caseMeanVar[s+6];
				aWriter.write(" OVD by "+dF.format(ovdToVar));
			}
			aWriter.newLine();

			//aWriter.write("Hits "+s+" : ["+hitCounts[s]+"]@"+lastHitLocation[s]+" ??"+dF.format(nextCasePdf[s]));aWriter.newLine();
		}
		aWriter.write("------ Performance predics/case="+ttPredicts[2*iCheck+iCase]/testCaseCount);
		aWriter.write(" predicts per hits="+ttPredicts[2*iCheck+iCase]/ttHits[2*iCheck+iCase]);
		aWriter.newLine();
		}
	}*/
		aWriter.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}

static void buildDailyHitPositionList(DrawInfor[] allLines, Vector<int[]> dailyList){
	//data is descending
	int iWhich=0;
	for (int i=0; i<40; i++) Arrays.fill(weeklySum[i], 0);
	int[][] dailyHitPositionList=new int[5][allLines.length];
	int iCase=0;
	for (int iTest=allLines.length-testCaseCount; iTest>=0; iTest--){
		int[] tmpDailyBallCount=new int[40];
		
		iCase++;
		int[] draws=null;
		int predictionWeekDay=allLines[iTest].wkDay;//+1;
		//if (predictionWeekDay > 6) predictionWeekDay=1;
		if (iTest > 0) {
			draws=allLines[iTest-1].data;
			//predictionWeekDay=allLines[iTest-1].wkDay;
		}
		else
			draws=new int[5];
		if (iCase==1){
			for (int ix=iTest; ix < allLines.length; ix++){//.iTest+1024; ix++){
				DrawInfor aDraw=allLines[ix];
				int[] hits=aDraw.data;
				for (int ik=0; ik<hits.length; ik++){
					weeklySum[hits[ik]][aDraw.wkDay]++;
					//lastShownAt[hits[ik]]=i+1;
				}
			}
		}
		else
		{
			DrawInfor aDraw=allLines[iTest];
			int[] hits=aDraw.data;
			for (int ik=0; ik<hits.length; ik++){
				weeklySum[hits[ik]][aDraw.wkDay]++;
				//lastShownAt[hits[ik]]=i+1;
			}
		}
		/*
		int[][] tmpWkBallCount=new int[40][];
		for (int k=0; k<40; k++){
			tmpWkBallCount[k]=Arrays.copyOf(weeklySum[k], 7);
		}*/
		Arrays.fill(tmpDailyBallCount, 0);
		for (int k=0; k<40; k++){
			tmpDailyBallCount[k]=weeklySum[k][predictionWeekDay]*100+k;
			if (iTest==0) continue;
			for (int s=0; s<5; s++){
				if (draws[s]==k) tmpDailyBallCount[k] += 50;
			}
			//for (int id=0; id<7; id++)
			//tmpWkBallCount[k][id]=tmpWkBallCount[k][id]*100+id;
			//Arrays.sort(tmpWkBallCount[k]);
		}
		Arrays.sort(tmpDailyBallCount);
		/*
		int[] tmpPos=new int[5];
		for (int x=0; x<5; x++){
			for (int iw=0; iw<tmpDailyBallCount.length; iw++){
				int ball=tmpDailyBallCount[iw] % 100;
				if (ball==draws[x]) tmpPos[x]=iw;
			}
		}
		Arrays.sort(tmpPos);
		if (iTest > 0){
			for (int x=0; x<5; x++){
				dailyHitPositionList[x][iWhich]=tmpPos[x];
			}
		}
		iWhich++;
		*/
		dailyList.add(tmpDailyBallCount);
	}
	//for (int x=0; x<5; x++){
		//dailyList.add(Arrays.copyOf(dailyHitPositionList[x], iWhich));
	//}
	//return tmpDailyBallCount;	
}
static int testCaseCount=JackpotReader.testCaseCount;
static DrawInfor[] workingDrawSet=null;;
static void predictBy10TailSets(int subsetCount, DrawInfor[] allLines){
	//DrawInfor[] allTime=new DrawInfor[allLines.size()];
	//int[] examineTopCounts={2, 4, 6, 8};
	BufferedWriter aWriter;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("daily10TailSets.txt")));
		System.out.println("ShowDailyTopStatistics Creating file "+"dailyTopPerformance.txt");
		int[][][] localPerformanceList=new int[subsetCount][6][testCaseCount];
		int[][] lastHitLocation=new int[subsetCount][6];
		int[][] hitCounts=new int[subsetCount][6];
		int[][][] predictionTS=new int[subsetCount][testCaseCount+1][];
		
		for (int i=0; i<10; i++){
			Arrays.fill(lastHitLocation[i], 0);
		//projByAvgDiff1Line(1, samples.get(0), aWriter);
			Arrays.fill(hitCounts[i], 0);
			for (int x=0; x<6;x++)
				Arrays.fill(localPerformanceList[i][x], 0);
		}
		
		String[] showPredicts=new String[subsetCount];
		//int[][] dailyHitPositionList=new int[5][testCaseCount];
		for (int iTest=testCaseCount; iTest>=0; iTest--){
			aWriter.write("============== "+iTest+" ==================="); aWriter.newLine();
			
			int[] draws=null;
			if (iTest > 0) {
				draws=allLines[iTest-1].data;
				//predictionWeekDay=allLines[iTest-1].wkDay;
			}
			else
				draws=new int[5];
			
			String allPredicts="Predics:( ";
			if (iTest>0)
			aWriter.write(">>>>"+allLines[iTest-1].toString()); 
			else
				aWriter.write(">>>>{0, 0, 0, 0, 0}");
			aWriter.newLine();
			
			for (int iSet=0; iSet<10; iSet++){
				aWriter.write("....... "+iSet+" ..............."); aWriter.newLine();
				int[] predictionList=new int[4];
				int iPredict=0;
				showPredicts[iSet]="Predics:( ";
				predictionList[0]=0;
				int idx=0;
				for (int i=0; i < 4 ; i++){	
					if (10*i+iSet == 0) continue;
					predictionList[idx]=10*i+iSet;
					showPredicts[iSet] += ""+predictionList[idx]+", ";	
					idx++;
				}
				int[] hitList=new int[5];
				int iGood=0;
				for (int ih=0; ih<5; ih++){					
					hitList[ih]=0;
					for (int i=0; i<4; i++){
						if (draws[ih]==0) continue;
						if (draws[ih]==predictionList[i]){
							hitList[iGood++]=draws[ih];
						}
					}
				}
				predictionTS[iSet][testCaseCount-iTest]=predictionList;
				hitCounts[iSet][iGood]++;
				if (iTest > 0){
					lastHitLocation[iSet][iGood]=iTest;
					localPerformanceList[iSet][iGood][testCaseCount-iTest]=1;
				}
				aWriter.write("pppp=>"+showPredicts[iSet]+") "+iGood); aWriter.newLine();
				
			//getAvgPdf(iTest+1, testSample, hits, aWriter );
			}
			aWriter.write("==========================================="); aWriter.newLine();
		}
		
		//Vector<int[]> plotData=new Vector<int[]>();
		//for (int x=0; x<5; x++){
			//plotData.add(dailyHitPositionList[x]);
		//}
		//createPngFile("positionInDailyOrder", plotData, 39, testCaseCount);
		String myName=" 10  Tail Sets Statistics =";
		JackpotReader.showSummaryPage(predictionTS, hitCounts, lastHitLocation, localPerformanceList, myName, aWriter);
		/*
		aWriter.write("========= 10 Sets Statistics =============="); aWriter.newLine();
		for (int dis=0; dis<10; dis++){
			aWriter.write("......"+showPredicts[dis]); aWriter.newLine();
			JackpotReader.performanceList=localPerformanceList[dis];
			double[] nextCasePdf=JackpotReader.getNextCasePdf();
			double[] caseMeanVar=JackpotReader.getHitCasesMeanVariant();
			for (int i=0; i<6; i++){
				String showOnOffSeq="seq:";
				int iLL=localPerformanceList[dis][i].length-10;
				for (int ih=0; ih<10; ih++){
					showOnOffSeq += ""+localPerformanceList[dis][i][iLL+ih];
				}
				aWriter.write(showOnOffSeq+"--- ");
				aWriter.write("Hits "+i+" : ["+hitCounts[dis][i]+"]@ "+lastHitLocation[dis][i]+" ??"+dF.format(nextCasePdf[i]));
				if (caseMeanVar[i] > 0 && lastHitLocation[dis][i] > caseMeanVar[i]) {
					double ovdToVar=(lastHitLocation[dis][i] - caseMeanVar[i])/caseMeanVar[i+6];
					aWriter.write(" OVD by "+dF.format(ovdToVar));
				}
				aWriter.newLine();
			}
			aWriter.write("......................................"); aWriter.newLine();
		}
		aWriter.write("==========================================="); aWriter.newLine();
*/
		aWriter.close();
		
	} catch (FileNotFoundException e){
		System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
	}	
}

static void predictByLeast6(DrawInfor[] allLines){
	//DrawInfor[] allTime=new DrawInfor[allLines.size()];
	int[][] ballTS=new int[40][allLines.length];
	for (int i=0; i<40; i++) Arrays.fill(ballTS[i], 0);
	for (int i=0; i<allLines.length; i++){
		//keep descending order
		int[] draws=allLines[i].data;
		for (int x=0; x<draws.length; x++) ballTS[draws[x]][i]=1;	
	}
	//int[] examineTopCounts={2, 4, 6, 8};
	BufferedWriter aWriter;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("dailyLeast6Try.txt")));
		System.out.println("ShowDailyTopStatistics Creating file "+"dailyTopPerformance.txt");
		int[][][] localPerformanceList=new int[1][6][testCaseCount];
		int[][] lastHitLocation=new int[1][6];
		int[][] hitCounts=new int[1][6];
		Arrays.fill(lastHitLocation[0], 0);
		//projByAvgDiff1Line(1, samples.get(0), aWriter);
		Arrays.fill(hitCounts[0], 0);
		for (int x=0; x<6;x++)
				Arrays.fill(localPerformanceList[0][x], 0);
		
		int[][][] predictionTS=new int[1][testCaseCount+1][];
		
		String showPredicts=null;
		//int[][] dailyHitPositionList=new int[5][testCaseCount];
		for (int iTest=testCaseCount; iTest>=0; iTest--){
			aWriter.write("============== "+iTest+" ==================="); aWriter.newLine();
			
			int[] draws=null;
			if (iTest > 0) {
				draws=allLines[iTest-1].data;
				//predictionWeekDay=allLines[iTest-1].wkDay;
			}
			else
				draws=new int[5];
			
			String allPredicts="Predics:( ";
			if (iTest>0)
			aWriter.write(">>>>"+allLines[iTest-1].toString()); 
			else
				aWriter.write(">>>>{0, 0, 0, 0, 0}");
			aWriter.newLine();
			int[] sortDelay=new int[40];
			Arrays.fill(sortDelay, 0);
			for (int i=0; i<40; i++){
				for (int x=0; x<39; x++){
					sortDelay[i] += ballTS[i][iTest+x];
				}
				sortDelay[i] *= 100;
				sortDelay[i] += i;
			}
			sortDelay[0]=100000;
			Arrays.sort(sortDelay);
			int[] predictionList=Arrays.copyOf(sortDelay, 6);
			for (int i=0; i<predictionList.length; i++) predictionList[i] %= 100;
			showPredicts="Predics:( ";
				
				for (int i=0; i < predictionList.length ; i++){					
						//predictionList[i-1]=4*iSet+i;
						showPredicts += ""+predictionList[i]+", ";					
				}
				int[] hitList=new int[5];
				int iGood=0;
				for (int ih=0; ih<5; ih++){					
					hitList[ih]=0;
					for (int i=0; i<predictionList.length; i++){
						if (draws[ih]==predictionList[i]){
							hitList[iGood++]=draws[ih];
						}
					}
				}
				predictionTS[0][testCaseCount-iTest]=predictionList;
				hitCounts[0][iGood]++;
				if (iTest > 0){
					lastHitLocation[0][iGood]=iTest;
					localPerformanceList[0][iGood][testCaseCount-iTest]=1;
				}
				aWriter.write("pppp=>"+showPredicts+") "+iGood); aWriter.newLine();
				
			//getAvgPdf(iTest+1, testSample, hits, aWriter );
			
			aWriter.write("==========================================="); aWriter.newLine();
		}
		
		//Vector<int[]> plotData=new Vector<int[]>();
		//for (int x=0; x<5; x++){
			//plotData.add(dailyHitPositionList[x]);
		//}
		//createPngFile("positionInDailyOrder", plotData, 39, testCaseCount);
		String myName=" Least 6 Sets Statistics ";
		JackpotReader.showSummaryPage(predictionTS, hitCounts, lastHitLocation, localPerformanceList, myName, aWriter);
		/*
		aWriter.write("========= Least 5 Sets Statistics =============="); aWriter.newLine();
		{
			aWriter.write("......"+showPredicts); aWriter.newLine();
			//JackpotReader.performanceList=localPerformanceList;
			double[] nextCasePdf=JackpotReader.getNextCasePdf();
			double[] caseMeanVar=JackpotReader.getHitCasesMeanVariant();
			for (int i=0; i<6; i++){
				String showOnOffSeq="seq:";
				int iLL=localPerformanceList[i].length-10;
				for (int ih=0; ih<10; ih++){
					showOnOffSeq += ""+localPerformanceList[i][iLL+ih];
				}
				aWriter.write(showOnOffSeq+"--- ");
				aWriter.write("Hits "+i+" : ["+hitCounts[i]+"]@ "+lastHitLocation[i]+" ??"+dF.format(nextCasePdf[i]));
				if (caseMeanVar[i] > 0 && lastHitLocation[i] > caseMeanVar[i]) {
					double ovdToVar=(lastHitLocation[i] - caseMeanVar[i])/caseMeanVar[i+6];
					aWriter.write(" OVD by "+dF.format(ovdToVar));
				}
				aWriter.newLine();
			}
			aWriter.write("......................................"); aWriter.newLine();
		}
		aWriter.write("==========================================="); aWriter.newLine();
*/
		aWriter.close();
		
	} catch (FileNotFoundException e){
		System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
	}	
}

static void predictBy10Sets(int[][] subset, DrawInfor[] allLines, String fileKey){
	//DrawInfor[] allTime=new DrawInfor[allLines.size()];
	//int[] examineTopCounts={2, 4, 6, 8};
	BufferedWriter aWriter;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("daily10Sets"+fileKey+".txt")));
		System.out.println("ShowDailyTopStatistics Creating file "+"dailyTopPerformance.txt");
		int[][][] localPerformanceList=new int[subset.length][6][testCaseCount];
		int[][] lastHitLocation=new int[subset.length][6];
		int[][] hitCounts=new int[subset.length][6];
		int[][][] predictionTS=new int[subset.length][testCaseCount+1][];
		for (int i=0; i<subset.length; i++){
			Arrays.fill(lastHitLocation[i], 0);
		//projByAvgDiff1Line(1, samples.get(0), aWriter);
			Arrays.fill(hitCounts[i], 0);
			for (int x=0; x<6;x++)
				Arrays.fill(localPerformanceList[i][x], 0);
		}
		
		String[] showPredicts=new String[subset.length];
		//int[][] dailyHitPositionList=new int[5][testCaseCount];
		for (int iTest=testCaseCount; iTest>=0; iTest--){
			aWriter.write("============== "+iTest+" ==================="); aWriter.newLine();
			
			int[] draws=null;
			if (iTest > 0) {
				draws=allLines[iTest-1].data;
				//predictionWeekDay=allLines[iTest-1].wkDay;
			}
			else
				draws=new int[5];
			
			String allPredicts="Predics:( ";
			if (iTest>0)
			aWriter.write(">>>>"+allLines[iTest-1].toString()); 
			else
				aWriter.write(">>>>{0, 0, 0, 0, 0}");
			aWriter.newLine();
			
			for (int iSet=0; iSet<subset.length; iSet++){
				aWriter.write("....... "+iSet+" ..............."); aWriter.newLine();
				int[] predictionList=subset[iSet];
				//int iPredict=0;
				showPredicts[iSet]="Predics:( ";
				
				for (int i=0; i < predictionList.length ; i++){					
						//predictionList[i-1]=4*iSet+i;
						showPredicts[iSet] += ""+predictionList[i]+", ";					
				}
				//int pCount=0;
				
				//for (int k=0; k<predictionList.length; k++)
					//if (predictionList[k]==1) 
						//{
							//sPred += ""+k+", ";
							//dPredict[pCount++]=k;
						//}
				//int[] pList=Arrays.copyOf(dPredict, pCount);
				predictionTS[iSet][testCaseCount-iTest]=predictionList;
				int[] hitList=new int[5];
				int iGood=0;
				for (int ih=0; ih<5; ih++){					
					hitList[ih]=0;
					for (int i=0; i<predictionList.length; i++){
						if (draws[ih]==predictionList[i]){
							hitList[iGood++]=draws[ih];
						}
					}
				}
				
				hitCounts[iSet][iGood]++;
				if (iTest > 0){
					lastHitLocation[iSet][iGood]=iTest;
					localPerformanceList[iSet][iGood][testCaseCount-iTest]=1;
				}
				aWriter.write("pppp=>"+showPredicts[iSet]+") "+iGood); aWriter.newLine();
				
			//getAvgPdf(iTest+1, testSample, hits, aWriter );
			}
			aWriter.write("==========================================="); aWriter.newLine();
		}
		
		//Vector<int[]> plotData=new Vector<int[]>();
		//for (int x=0; x<5; x++){
			//plotData.add(dailyHitPositionList[x]);
		//}
		//createPngFile("positionInDailyOrder", plotData, 39, testCaseCount);
		String myName=" 10 Sets Statistics =";
		JackpotReader.showSummaryPage(predictionTS, hitCounts, lastHitLocation, localPerformanceList, myName, aWriter);
		/*
		aWriter.write("========= 10 Sets Statistics =============="); aWriter.newLine();
		for (int dis=0; dis<subset.length; dis++){
			aWriter.write("......"+showPredicts[dis]); aWriter.newLine();
			JackpotReader.performanceList=localPerformanceList[dis];
			double[] nextCasePdf=JackpotReader.getNextCasePdf();
			double[] caseMeanVar=JackpotReader.getHitCasesMeanVariant();
			for (int i=0; i<6; i++){
				String showOnOffSeq="seq:";
				int iLL=localPerformanceList[dis][i].length-10;
				for (int ih=0; ih<10; ih++){
					showOnOffSeq += ""+localPerformanceList[dis][i][iLL+ih];
				}
				aWriter.write(showOnOffSeq+"--- ");
				aWriter.write("Hits "+i+" : ["+hitCounts[dis][i]+"]@ "+lastHitLocation[dis][i]+" ??"+dF.format(nextCasePdf[i]));
				if (caseMeanVar[i] > 0 && lastHitLocation[dis][i] > caseMeanVar[i]) {
					double ovdToVar=(lastHitLocation[dis][i] - caseMeanVar[i])/caseMeanVar[i+6];
					aWriter.write(" OVD by "+dF.format(ovdToVar));
				}
				aWriter.newLine();
			}
			aWriter.write("......................................"); aWriter.newLine();
		}
		aWriter.write("==========================================="); aWriter.newLine();
*/
		aWriter.close();
		
	} catch (FileNotFoundException e){
		System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
	}	
}


static void predictByDailyTop(DrawInfor[] allLines){
	//DrawInfor[] allTime=new DrawInfor[allLines.size()];
	int[] examineTopCounts={2, 4, 6, 8};
	BufferedWriter aWriter;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("dailyTopPerformance.txt")));
		System.out.println("ShowDailyTopStatistics Creating file "+"dailyTopPerformance.txt");
		int[][][] localPerformanceList=new int[examineTopCounts.length][6][testCaseCount];
		int[][] lastHitLocation=new int[examineTopCounts.length][6];
		int[][] hitCounts=new int[examineTopCounts.length][6];
		int[][][] predictionTS=new int[examineTopCounts.length][testCaseCount+1][];
		
		for (int i=0; i<4; i++){
			Arrays.fill(lastHitLocation[i], 0);
		//projByAvgDiff1Line(1, samples.get(0), aWriter);
			Arrays.fill(hitCounts[i], 0);
			for (int x=0; x<6;x++)
				Arrays.fill(localPerformanceList[i][x], 0);
		}
		
		String[] showPredicts=new String[examineTopCounts.length];
		int[][] dailyHitPositionList=new int[5][testCaseCount];
		for (int iTest=testCaseCount; iTest>=0; iTest--){
			aWriter.write("============== "+iTest+" ==================="); aWriter.newLine();
			
			for (int i=0; i<40; i++) Arrays.fill(weeklySum[i], 0);
			int[] draws=null;
			int predictionWeekDay=allLines[iTest].wkDay+1;
			if (predictionWeekDay > 6) predictionWeekDay=1;
			if (iTest > 0) {
				draws=allLines[iTest-1].data;
				//predictionWeekDay=allLines[iTest-1].wkDay;
			}
			else
				draws=new int[5];
			for (int ix=iTest; ix < allLines.length; ix++){//.iTest+1024; ix++){
				DrawInfor aDraw=allLines[ix];
				int[] hits=aDraw.data;
				for (int ik=0; ik<hits.length; ik++){
					weeklySum[hits[ik]][aDraw.wkDay]++;
					//lastShownAt[hits[ik]]=i+1;
				}
			}
			
			int[][] tmpWkBallCount=new int[40][];
			for (int k=0; k<40; k++){
				tmpWkBallCount[k]=Arrays.copyOf(weeklySum[k], 7);
			}
			int[] tmpDailyBallCount=new int[40];
			for (int k=0; k<40; k++){
				tmpDailyBallCount[k]=weeklySum[k][predictionWeekDay]*100+k;
				for (int id=0; id<7; id++)
				tmpWkBallCount[k][id]=tmpWkBallCount[k][id]*100+id;
				Arrays.sort(tmpWkBallCount[k]);
			}
			Arrays.sort(tmpDailyBallCount);
			int[] tmpPos=new int[5];
			for (int x=0; x<5; x++){
				for (int iw=0; iw<tmpDailyBallCount.length; iw++){
					int ball=tmpDailyBallCount[iw] % 100;
					if (ball==draws[x]) tmpPos[x]=iw;
				}
			}
			Arrays.sort(tmpPos);
			if (iTest > 0){
			for (int x=0; x<5; x++){
				dailyHitPositionList[x][testCaseCount - iTest]=tmpPos[x];
			}
			}
			String allPredicts="Predics:( ";
			if (iTest>0)
			aWriter.write(">>>>"+allLines[iTest-1].toString()); 
			else
				aWriter.write(">>>>{0, 0, 0, 0, 0}");
			aWriter.newLine();
			
			for (int iTop=0; iTop<examineTopCounts.length; iTop++){
				aWriter.write("....... "+iTop+" ..............."); aWriter.newLine();
				int chkTop=examineTopCounts[iTop];
				int[] predictionList=new int[chkTop];
				int iPredict=0;
				showPredicts[iTop]="Predics:( ";
				
				for (int i=tmpDailyBallCount.length-1; i>0 && iPredict < chkTop; i--){
					int nBall=tmpDailyBallCount[i] % 100;
					int nCount=tmpDailyBallCount[i]/100;
					int nMaxCount=tmpWkBallCount[nBall][6]/100;
					if (Math.abs(nCount - nMaxCount) < 3){
						predictionList[iPredict++]=nBall;
						showPredicts[iTop] += ""+nBall+", ";
					}
				}
				predictionTS[iTop][testCaseCount-iTest]=predictionList;
				int[] hitList=new int[5];
				int iGood=0;
				for (int ih=0; ih<5; ih++){					
					hitList[ih]=0;
					for (int i=0; i<chkTop; i++){
						if (draws[ih]==predictionList[i]){
							hitList[iGood++]=draws[ih];
						}
					}
				}
				
				hitCounts[iTop][iGood]++;
				if (iTest > 0){
					lastHitLocation[iTop][iGood]=iTest;
					localPerformanceList[iTop][iGood][testCaseCount-iTest]=1;
				}
				aWriter.write("pppp=>"+showPredicts[iTop]+") "+iGood); aWriter.newLine();
				
			//getAvgPdf(iTest+1, testSample, hits, aWriter );
			}
			aWriter.write("==========================================="); aWriter.newLine();
		}
		
		Vector<int[]> plotData=new Vector<int[]>();
		for (int x=0; x<5; x++){
			plotData.add(dailyHitPositionList[x]);
		}
		//createPngFile("positionInDailyOrder", plotData, 39, testCaseCount);
		String myName=" ShowDailyTopStatistics =";
		JackpotReader.showSummaryPage(predictionTS, hitCounts, lastHitLocation, localPerformanceList, myName, aWriter);
		/*
		aWriter.write("========= ShowDailyTopStatistics =============="); aWriter.newLine();
		for (int dis=0; dis<examineTopCounts.length; dis++){
			aWriter.write("......"+showPredicts[dis]); aWriter.newLine();
			JackpotReader.performanceList=localPerformanceList[dis];
			double[] nextCasePdf=JackpotReader.getNextCasePdf();
			double[] caseMeanVar=JackpotReader.getHitCasesMeanVariant();
			for (int i=0; i<6; i++){
				aWriter.write("Hits "+i+" : ["+hitCounts[dis][i]+"]@ "+lastHitLocation[dis][i]+" ??"+dF.format(nextCasePdf[i]));
				if (caseMeanVar[i] > 0 && lastHitLocation[dis][i] > caseMeanVar[i]) {
					double ovdToVar=(lastHitLocation[dis][i] - caseMeanVar[i])/caseMeanVar[i+6];
					aWriter.write(" OVD by "+dF.format(ovdToVar));
				}
				aWriter.newLine();
			}
			aWriter.write("......................................"); aWriter.newLine();
		}
		aWriter.write("==========================================="); aWriter.newLine();
*/
		aWriter.close();
		
	} catch (FileNotFoundException e){
		System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
	}	
}

static int[][] getSubSetHitTimeSeries(int[][] subsets, DrawInfor[] dataList){
	int[][] setTS=new int[subsets.length][dataList.length];
	for (int i=0;i<subsets.length; i++) Arrays.fill(setTS[i],  0);
	for (int i=0; i<dataList.length; i++){
		int tSeq=dataList.length-1-i;
		for (int k=0; k<dataList[i].data.length; k++){
			boolean found=false;
			for (int vs=0; vs<subsets.length; vs++){
				if (found) break;
				for (int vv=0; vv<subsets[vs].length; vv++){
					if (dataList[i].data[k]==subsets[vs][vv]){
						setTS[vs][tSeq]=1; // get ascending data list
						found=true;
					}
				}
			}
		}
	}
	return setTS;
}

static void predictByWorst1Set(int[][] subset, int[][] subsetTS, String fileKey, BufferedWriter summaryList){	
	//DrawInfor[] allTime=new DrawInfor[allLines.size()];
	//int[] examineTopCounts={2, 4, 6, 8};
int testCount=testCaseCount;
	BufferedWriter aWriter;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("dailyW1Sets"+fileKey+".txt")));
		System.out.println("Lottery539Reader Creating file "+"dailyW1Sets.txt");
		int[][][] localPerformanceList=new int[1][6][testCount];
		int[][] lastHitLocation=new int[1][6];
		int[][] hitCounts=new int[1][6];
		int[][][] predictionTS=new int[1][testCaseCount+1][];
		for (int i=0; i<1; i++){
			Arrays.fill(lastHitLocation[i], 0);
		//projByAvgDiff1Line(1, samples.get(0), aWriter);
			Arrays.fill(hitCounts[i], 0);
			for (int x=0; x<6;x++)
				Arrays.fill(localPerformanceList[i][x], 0);
		}
		
		String[] showPredicts=new String[1];
		//int[][] dailyHitPositionList=new int[5][testCaseCount];
		int iLen=subsetTS[0].length;
		int[] setDelay=new int[subset.length];
		Arrays.fill(setDelay, 0); 
		int srchBackFrom=iLen-testCount -1;
		for (int i=0; i<subset.length; i++){
			int s0=srchBackFrom;
			while (setDelay[i]==0){
				if (subsetTS[i][s0--]==0) continue;
				setDelay[i]=srchBackFrom - s0;
				break;
			}
		}
		for (int iTest=testCount; iTest>=0; iTest--){
			aWriter.write("============== "+iTest+" ==================="); aWriter.newLine();
			int[] realDraw=null;
			if (iTest > 0) realDraw=workingDrawSet[iTest-1].data;
			
			int[] draws=new int[5];
			//Arrays.fill(setDelay, 0);
			String sDraws="Draw (";
			int iDraw=0;
			if (iTest > 0) {
				int loc=iLen - iTest;
				iDraw=0;
				for (int s=0; s<subset.length; s++){
					if (subsetTS[s][loc]==1) //setDelay[s]++; 
					{
						sDraws += ""+s+", ";
						draws[iDraw++]=s;
					}
				}
				//predictionWeekDay=allLines[iTest-1].wkDay;
				sDraws=workingDrawSet[iTest-1].toString();
			}
			
			int[] sortedDelay=new int[subset.length];
			sortedDelay[0]=0;
			for (int i=1; i<subset.length; i++){
				boolean justHit=false;
				if (subsetTS[i][iLen - iTest-1]==0) setDelay[i]++;
				else justHit=true;						
				sortedDelay[i]=setDelay[i]*100+i;
				if (justHit) setDelay[i]=0;
			}
			Arrays.sort(sortedDelay);
			int[] predictionList=new int[1];
			predictionList[0]=sortedDelay[subset.length-1] % 100;
			//predictionList[1]=sortedDelay[subset.length-2] % 100;
			String allPredicts="Predics:( ";
			int[] realPredictions=new int[subset[predictionList[0]].length];
			
			for (int ip=0; ip<predictionList.length; ip++){
				for (int k=0; k<subset[ip].length; k++)
				{
					allPredicts += ""+subset[predictionList[ip]][k]+", ";
					realPredictions[subset[0].length*ip+k]=subset[predictionList[ip]][k];
				}
			}
			
			showPredicts[0]=allPredicts;
			
			int iGood=0;
			if (iTest > 0) for (int ih=0; ih<5; ih++){
				for (int ip=0; ip<realPredictions.length; ip++){
					if (realDraw[ih]==realPredictions[ip]){
						iGood++;
						break;
					}
				}
			}
			predictionTS[0][testCaseCount-iTest]=realPredictions;
				hitCounts[0][iGood]++;
				if (iTest > 0){
					lastHitLocation[0][iGood]=iTest;
					localPerformanceList[0][iGood][testCaseCount-iTest]=1;
				}
				else
				{
					showPredicts[0]="predics:(";
					for (int ip=0; ip<realPredictions.length; ip++){
						showPredicts[0] += ""+realPredictions[ip]+", ";
					}
				}
				aWriter.write(sDraws+") => "+showPredicts[0]+") "+iGood); aWriter.newLine();
				
			//getAvgPdf(iTest+1, testSample, hits, aWriter );
			
			aWriter.write("==========================================="); aWriter.newLine();
		}
		
		//Vector<int[]> plotData=new Vector<int[]>();
		//for (int x=0; x<5; x++){
			//plotData.add(dailyHitPositionList[x]);
		//}
		//createPngFile("positionInDailyOrder", plotData, 39, testCaseCount);
		String myName=" Worst 1 Sets Statistics "+fileKey;
		JackpotReader.showSummaryPage(predictionTS, hitCounts, lastHitLocation, localPerformanceList, myName, aWriter);
		/*
		aWriter.write("========= N Sets Statistics =============="); aWriter.newLine();
		summaryList.write("========="+fileKey+" Sets Statistics =============="); summaryList.newLine();
		for (int dis=0; dis<1; dis++){
			aWriter.write("......"+showPredicts[0]); aWriter.newLine();
			summaryList.write("......"+showPredicts[0]); summaryList.newLine();
			
			JackpotReader.performanceList=localPerformanceList[0];
			double[] nextCasePdf=JackpotReader.getNextCasePdf();
			double[] caseMeanVar=JackpotReader.getHitCasesMeanVariant();
			for (int i=0; i<6; i++){
				String showOnOffSeq="seq:";
				int iLL=localPerformanceList[0][i].length-10;
				for (int ih=0; ih<10; ih++){
					showOnOffSeq += ""+localPerformanceList[0][i][iLL+ih];
				}
				summaryList.write(showOnOffSeq+"--- ");
				aWriter.write("Hits "+i+" : ["+hitCounts[0][i]+"]@ "+lastHitLocation[0][i]+" ??"+dF.format(nextCasePdf[i]));
				summaryList.write("Hits "+i+" : ["+hitCounts[0][i]+"]@ "+lastHitLocation[0][i]+" ??"+dF.format(nextCasePdf[i]));
				if (caseMeanVar[i] > 0 && lastHitLocation[0][i] > caseMeanVar[i]) {
					double ovdToVar=(lastHitLocation[0][i] - caseMeanVar[i])/caseMeanVar[i+6];
					aWriter.write(" OVD by "+dF.format(ovdToVar));
					summaryList.write(" OVD by "+dF.format(ovdToVar));
				}
				
				aWriter.newLine();
				summaryList.newLine();
			}
			aWriter.write("......................................"); aWriter.newLine();
			summaryList.write("......................................"); aWriter.newLine();
		}
		
		aWriter.write("==========================================="); aWriter.newLine();
*/
		aWriter.close();
		
	} catch (FileNotFoundException e){
		System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
	}	


}


static void predictByWorst2Sets(int[][] subset, int[][] subsetTS, String fileKey, BufferedWriter summaryList){	
		//DrawInfor[] allTime=new DrawInfor[allLines.size()];
		//int[] examineTopCounts={2, 4, 6, 8};
	int testCount=testCaseCount;
		BufferedWriter aWriter;
		try {
			aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("dailyW2Sets"+fileKey+".txt")));
			System.out.println("Lottery539Reader Creating file "+"dailyW2Sets.txt");
			int[][][] localPerformanceList=new int[1][6][testCount];
			int[][] lastHitLocation=new int[1][6];
			int[][] hitCounts=new int[1][6];
			
			for (int i=0; i<1; i++){
				Arrays.fill(lastHitLocation[i], 0);
			//projByAvgDiff1Line(1, samples.get(0), aWriter);
				Arrays.fill(hitCounts[i], 0);
				for (int x=0; x<6;x++)
					Arrays.fill(localPerformanceList[i][x], 0);
			}
			int[][][] predictionTS=new int[1][testCaseCount+1][];
			String[] showPredicts=new String[1];
			//int[][] dailyHitPositionList=new int[5][testCaseCount];
			int iLen=subsetTS[0].length;
			int[] setDelay=new int[subset.length];
			Arrays.fill(setDelay, 0); 
			int srchBackFrom=iLen-testCount -1;
			for (int i=0; i<subset.length; i++){
				int s0=srchBackFrom;
				while (setDelay[i]==0){
					if (subsetTS[i][s0--]==0) continue;
					setDelay[i]=srchBackFrom - s0;
					break;
				}
			}
			for (int iTest=testCount; iTest>=0; iTest--){
				aWriter.write("============== "+iTest+" ==================="); aWriter.newLine();
				
				int[] realDraw=null;
				if (iTest > 0) realDraw=workingDrawSet[iTest-1].data;
				//else realDraw=new {0, 0, 0, 0, 0};
				int[] draws=new int[5];
				//Arrays.fill(setDelay, 0);
				String sDraws="Draw (";
				int iDraw=0;
				if (iTest > 0) {
					int loc=iLen - iTest;
					iDraw=0;
					for (int s=0; s<subset.length; s++){
						if (subsetTS[s][loc]==1) //setDelay[s]++; 
						{
							sDraws += ""+s+", ";
							draws[iDraw++]=s;
						}
					}
					//predictionWeekDay=allLines[iTest-1].wkDay;
					sDraws=workingDrawSet[iTest-1].toString();
				}
				
				int[] sortedDelay=new int[subset.length];
				sortedDelay[0]=0;
				for (int i=1; i<subset.length; i++){
					boolean justHit=false;
					if (subsetTS[i][iLen - iTest-1]==0) setDelay[i]++;
					else justHit=true;						
					sortedDelay[i]=setDelay[i]*100+i;
					if (justHit) setDelay[i]=0;
				}
				Arrays.sort(sortedDelay);
				int[] predictionList=new int[2];
				predictionList[0]=sortedDelay[subset.length-1] % 100;
				predictionList[1]=sortedDelay[subset.length-2] % 100;
				String allPredicts="Predics:( ";
				int[] realPredictions=new int[subset[0].length*2];
				
				for (int ip=0; ip<predictionList.length; ip++){
					for (int k=0; k<subset[ip].length; k++)
					{
						allPredicts += ""+subset[predictionList[ip]][k]+", ";
						realPredictions[subset[0].length*ip+k]=subset[predictionList[ip]][k];
					}
				}
				
				showPredicts[0]=allPredicts;
				/*
				int iGood=0;
				for (int ih=0; ih<iDraw; ih++){
					for (int ip=0; ip<predictionList.length; ip++){
						if (draws[ih]==predictionList[ip]){
							iGood++;
							break;
						}
					}
				}
				*/
				int iGood=0;
				if (iTest > 0) {for (int ih=0; ih<5; ih++){
					for (int ip=0; ip<realPredictions.length; ip++){
						if (realDraw[ih]==realPredictions[ip]){
							iGood++;
							break;
						}
					}
				}}
				predictionTS[0][testCaseCount-iTest]=realPredictions;
					hitCounts[0][iGood]++;
					if (iTest > 0){
						lastHitLocation[0][iGood]=iTest;
						localPerformanceList[0][iGood][testCaseCount-iTest]=1;
					}
					else
					{
						showPredicts[0]="predics:(";
						for (int ip=0; ip<realPredictions.length; ip++){
							//for (int s=0; s< subset[realPredictions[ip]].length; s++){
								showPredicts[0] += ""+realPredictions[ip]+", ";
							//}
						}
					}
					aWriter.write(sDraws+") => "+showPredicts[0]+") "+iGood); aWriter.newLine();
					
				//getAvgPdf(iTest+1, testSample, hits, aWriter );
				
				aWriter.write("==========================================="); aWriter.newLine();
			}
			
			//Vector<int[]> plotData=new Vector<int[]>();
			//for (int x=0; x<5; x++){
				//plotData.add(dailyHitPositionList[x]);
			//}
			//createPngFile("positionInDailyOrder", plotData, 39, testCaseCount);
			String myName=" Worst 2 Sets Statistics "+fileKey;
			JackpotReader.showSummaryPage(predictionTS, hitCounts, lastHitLocation, localPerformanceList, myName, aWriter);
			/*
			aWriter.write("========= 10 Sets Statistics =============="); aWriter.newLine();
			summaryList.write("========="+fileKey+" Sets Statistics =============="); summaryList.newLine();
			for (int dis=0; dis<1; dis++){
				aWriter.write("......"+showPredicts[0]); aWriter.newLine();
				summaryList.write("......"+showPredicts[0]); summaryList.newLine();
				
				JackpotReader.performanceList=localPerformanceList[0];
				double[] nextCasePdf=JackpotReader.getNextCasePdf();
				double[] caseMeanVar=JackpotReader.getHitCasesMeanVariant();
				for (int i=0; i<6; i++){
					String showOnOffSeq="seq:";
					int iLL=localPerformanceList[0][i].length-10;
					for (int ih=0; ih<10; ih++){
						showOnOffSeq += ""+localPerformanceList[0][i][iLL+ih];
					}
					summaryList.write(showOnOffSeq+"-- ");
					aWriter.write("Hits "+i+" : ["+hitCounts[0][i]+"]@ "+lastHitLocation[0][i]+" ??"+dF.format(nextCasePdf[i]));
					summaryList.write("Hits "+i+" : ["+hitCounts[0][i]+"]@ "+lastHitLocation[0][i]+" ??"+dF.format(nextCasePdf[i]));
					if (caseMeanVar[i] > 0 && lastHitLocation[0][i] > caseMeanVar[i]) {
						double ovdToVar=(lastHitLocation[0][i] - caseMeanVar[i])/caseMeanVar[i+6];
						aWriter.write(" OVD by "+dF.format(ovdToVar));
						summaryList.write(" OVD by "+dF.format(ovdToVar));
					}
					
					
					aWriter.newLine();
					summaryList.newLine();
				}
				aWriter.write("......................................"); aWriter.newLine();
				summaryList.write("......................................"); aWriter.newLine();
			}
			aWriter.write("==========================================="); aWriter.newLine();
*/
			aWriter.close();
			
		} catch (FileNotFoundException e){
			System.out.println("No file");
		} catch (IOException e){
			System.out.println("Bad I/O");
		}	
	
	
}
static int[][] getNSequenceSubset(int subsetLength){
	int subsetCount=40/subsetLength;
	if (subsetCount*subsetLength < 39) subsetCount++;
	int[][] aTenSet=new int[subsetCount][subsetLength];
	int iC=1;
	for (int iSet=0; iSet<subsetCount; iSet++){
		for (int ix=0; ix<subsetLength; ix++) {
			int n=iC++;//ix+subsetLength*iSet+1;
			if (n > 39) n -= 39;
			aTenSet[iSet][ix]=n;
		}
	}
	return aTenSet;
}

static int[][] getNJumpSubset(int subsetLength){
	int subsetCount=40/subsetLength;
	if (subsetCount*subsetLength < 39) subsetCount++;
	int[][] aTenSet=new int[subsetCount][subsetLength];
	int iC=1;
	for (int ix=0; ix<subsetLength; ix++){
		for (int iSet=0; iSet<subsetCount; iSet++)	{
			int n=iC++;//ix*subsetCount+iSet+1;
			if (n > 39) n -= 39;
					aTenSet[iSet][ix]=n;
		}
	}
	return aTenSet;
}

static Vector<int[]> getAllData(){
	Vector<int[]> allLines=new Vector<int[]>();
	Lottery539DataReader aParser=new Lottery539DataReader("539");
	File history=new File(dataCenter+"History.txt");
	if (!history.exists()) readFromWeb();
	BufferedReader aReader=null;
	
	try {
		aReader=new BufferedReader(new FileReader(history));
		String aLine;
		while ((aLine=aReader.readLine()) != null){
			if (aLine.length()<5)continue;
			int i0=0;
			while (aLine.charAt(i0)<'0' || aLine.charAt(i0) > '9') i0++;
			String bLine=aLine.substring(i0);
			int ix=bLine.indexOf('(');
			String date1=bLine.trim().substring(0,  ix);
			int i9=bLine.indexOf(')');
			int wkDay=Integer.parseInt(bLine.substring(ix+1, i9));
			int[] datas=getDrawData(bLine.substring(i9+2));
			allLines.add(datas);
		}
		aReader.close();		
	} catch (FileNotFoundException e){
	System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
	}
	return allLines;
}

static Vector<DrawInfor> getAllDrawData(){
	Vector<DrawInfor> allLines=new Vector<DrawInfor>();
	Lottery539DataReader aParser=new Lottery539DataReader("539");
	File history=new File(dataCenter+"History.txt");
	if (!history.exists()) readFromWeb();
	BufferedReader aReader=null;
	
	try {
		aReader=new BufferedReader(new FileReader(history));
		String aLine;
		while ((aLine=aReader.readLine()) != null){
			if (aLine.length()<5)continue;
			int i0=0;
			while (aLine.charAt(i0)<'0') i0++;
			String bLine=aLine.substring(i0);
			int ix=bLine.indexOf('(');
			String date1=bLine.trim().substring(0,  ix);
			int i9=bLine.indexOf(')');
			int wkDay=Integer.parseInt(bLine.substring(ix+1, i9));
			int[] datas=getDrawData(bLine.substring(i9+2));
			allLines.add(aParser.getOneDrawInfo(date1, wkDay, datas));
		}
		aReader.close();		
	} catch (FileNotFoundException e){
	System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
	}
	return allLines;
}
	public static void main(String[] args) 
	{
		Vector<DrawInfor> allLines=getAllDrawData();
		
		//for (int iCase=)
		DrawInfor[] dataList=new DrawInfor[allLines.size()];
		for (int i=0; i<allLines.size(); i++){
			dataList[i]=allLines.get(i);
		}
		workingDrawSet=dataList;
		predictByLeast6(dataList);
		int subsetCount=10;
		int subsetLength=40/subsetCount;
		int[][] aTenSet=new int[subsetCount][subsetLength];
		for (int iSet=0; iSet<subsetCount; iSet++){
			for (int ix=0; ix<subsetLength; ix++) aTenSet[iSet][ix]=ix+subsetLength*iSet+1;
		}
		//int[][] tsTenSet=getSubSetHitTimeSeries(aTenSet, dataList);
		//predictByWorstTwoSets(aTenSet, tsTenSet, "M4");
		BufferedWriter summaryList;
		try {
			summaryList=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("history4PredictOn"+(dataList[0].wkDay+1)+".txt")));
		
		int susetCount=13;
		aTenSet=getNSequenceSubset(3);
		int[][] tsTenSet=getSubSetHitTimeSeries(aTenSet, dataList);
		predictByWorst2Sets(aTenSet, tsTenSet, "M3", summaryList);
		predictByWorst1Set(aTenSet, tsTenSet, "M3A", summaryList);
		aTenSet=getNJumpSubset(3);
		tsTenSet=getSubSetHitTimeSeries(aTenSet, dataList);
		predictByWorst2Sets(aTenSet, tsTenSet, "MJ3", summaryList);
		predictByWorst1Set(aTenSet, tsTenSet, "MJ3A", summaryList);
		
		aTenSet=getNSequenceSubset(4);
		tsTenSet=getSubSetHitTimeSeries(aTenSet, dataList);
		predictByWorst2Sets(aTenSet, tsTenSet, "M4", summaryList);
		predictByWorst1Set(aTenSet, tsTenSet, "M4A", summaryList);
		aTenSet=getNJumpSubset(4);
		tsTenSet=getSubSetHitTimeSeries(aTenSet, dataList);
		predictByWorst2Sets(aTenSet, tsTenSet, "MJ4", summaryList);
		predictByWorst1Set(aTenSet, tsTenSet, "MJ4A", summaryList);
		
		aTenSet=getNSequenceSubset(5);
		tsTenSet=getSubSetHitTimeSeries(aTenSet, dataList);
		predictByWorst2Sets(aTenSet, tsTenSet, "M5", summaryList);
		predictByWorst1Set(aTenSet, tsTenSet, "M5A", summaryList);
		aTenSet=getNJumpSubset(5);
		tsTenSet=getSubSetHitTimeSeries(aTenSet, dataList);
		predictByWorst2Sets(aTenSet, tsTenSet, "MJ5", summaryList);
		predictByWorst1Set(aTenSet, tsTenSet, "MJ5A", summaryList);
		
		aTenSet=getNSequenceSubset(6);
		tsTenSet=getSubSetHitTimeSeries(aTenSet, dataList);
		predictByWorst2Sets(aTenSet, tsTenSet, "M6", summaryList);
		predictByWorst1Set(aTenSet, tsTenSet, "M6A", summaryList);
		aTenSet=getNJumpSubset(6);
		tsTenSet=getSubSetHitTimeSeries(aTenSet, dataList);
		predictByWorst2Sets(aTenSet, tsTenSet, "MJ6", summaryList);
		predictByWorst1Set(aTenSet, tsTenSet, "MJ6A", summaryList);
		
		aTenSet=getNSequenceSubset(7);
		tsTenSet=getSubSetHitTimeSeries(aTenSet, dataList);
		predictByWorst2Sets(aTenSet, tsTenSet, "M7", summaryList);
		predictByWorst1Set(aTenSet, tsTenSet, "M7A", summaryList);
		aTenSet=getNJumpSubset(7);
		tsTenSet=getSubSetHitTimeSeries(aTenSet, dataList);
		predictByWorst2Sets(aTenSet, tsTenSet, "MJ7", summaryList);
		predictByWorst1Set(aTenSet, tsTenSet, "MJ7A", summaryList);
		
		aTenSet=getNSequenceSubset(8);
		tsTenSet=getSubSetHitTimeSeries(aTenSet, dataList);
		predictByWorst2Sets(aTenSet, tsTenSet, "M8", summaryList);
		predictByWorst1Set(aTenSet, tsTenSet, "M8A", summaryList);
		aTenSet=getNJumpSubset(8);
		tsTenSet=getSubSetHitTimeSeries(aTenSet, dataList);
		predictByWorst2Sets(aTenSet, tsTenSet, "MJ8", summaryList);
		predictByWorst1Set(aTenSet, tsTenSet, "MJ8A", summaryList);
		
		summaryList.close();
		
		} catch (FileNotFoundException e){
			System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
	}
		int[] setLength={3,4,5};
		for (int i=0; i<setLength.length; i++){
		aTenSet=getNSequenceSubset(setLength[i]);		
		predictBy10Sets(aTenSet, dataList, "Len"+setLength[i]);
		}
		
		subsetLength=10;
		int[][] tailSet;
		//for (int iSet=0; iSet<subsetCount; iSet++){
			//for (int ix=0; ix<40/subsetCount; ix++) aTenSet[iSet][ix]=subsetLength*ix+iSet;
		//}
		predictBy10TailSets(10, dataList);
		
		Vector<int[]> dailyPosList=new Vector<>();
		buildDailyHitPositionList(dataList, dailyPosList);
		int[] nextDayTopList=dailyPosList.get(dailyPosList.size()-1);
		projectByTopListCurveFitting(dailyPosList, "dailyTopListFit", nextDayTopList);

		predictByDailyTop(dataList);
		System.exit(0);
		DrawInfor aDraw=allLines.get(0);
		int currentMonth=Integer.parseInt(aDraw.date.substring(6-1, 6-1+2));
		String drawLine=aDraw.toString();
		int predictWeekday=aDraw.wkDay;
		if (++predictWeekday ==7) predictWeekday=1;
		int[] currentMonthDraw=new int[40];
		for (int i=0; i<monthlyStatistics.length; i++)
		Arrays.fill(monthlyStatistics[i], 0);
		for (int i=0; i<weeklySum.length; i++)
			Arrays.fill(monthlyStatistics[i], 0);
		double iTotal=0;
		int checkRange=allLines.size();
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
		int[] lastOnAt=new int[40];
		Arrays.fill(lastOnAt, -1);
		int[] lastShownAt=new int[40];
		Arrays.fill(lastShownAt, 0);
		boolean finishAddCurrentMonth=false;
		int[] bestWeek=new int[40];
		Arrays.fill(lastOnAt, 0);
		for (int i=0; i<checkRange; i++){
			aDraw=allLines.get(i);
			int[] hits=aDraw.data;
			int month=Integer.parseInt(aDraw.date.substring(5,7));
			if (month != currentMonth) finishAddCurrentMonth=true;
			for (int ik=0; ik<hits.length; ik++){
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
				if (!finishAddCurrentMonth){				
					currentMonthDraw[hits[ik]]++;
				}
				weeklySum[hits[ik]][aDraw.wkDay]++;
				if (lastShownAt[hits[ik]]==0) lastShownAt[hits[ik]]=i+1;
			}
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
			int[] sortedWeek=Arrays.copyOf(weeklySum[ib], 7);
			
			String ibs="B "+ib+"w (";
			int iTtP=0;
			for (int iM=1; iM<7; iM++){
				ibs += (""+iM+"<"+weeklySum[ib][iM]+">, ");
				sortedWeek[iM]=sortedWeek[iM]*100+iM;
				if (sortedWeek[iM] > bestWeek[ib]) bestWeek[ib]=sortedWeek[iM];
				iTtP += (weeklySum[ib][iM]*weeklySum[ib][iM]);
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
			aWriter.write(ibs+")["+ib+"]"+drawnSet[ib].currentIdleDays+" Mon("+monthlyStatistics[ib][currentMonth]+") ");
					//" M="+dF.format(drawnSet[ib].idleMean)+" R="+dF.format(drawnSet[ib].idleVariant)+" X="+drawnSet[ib].maxIdle);
			aWriter.write(" M3:"+iHap3+"^"+iHap2+"^"+iHap1+"||"+drawnSet[ib].nextOnDay);//+"r"+dF.format(drawnSet[ib].nextOnVar/2));
			//if (drawnSet[ib].nextOnDay - drawnSet[ib].nextOnVar/2 < 2)
				//aWriter.write(">>"+ dF.format(drawnSet[ib].nextOnDay - drawnSet[ib].nextOnVar/2));
			//if (drawnSet[ib].predict80 < 2 ||  drawnSet[ib].predict90 < 3)
			left[ib]=(monthlyStatistics[ib][currentMonth]-currentMonthDraw[ib])/3.0-currentMonthDraw[ib];
			aWriter.write(">>"+ drawnSet[ib].predict66 +">>"+drawnSet[ib].predict75+">>"+drawnSet[ib].predict80);// dF.format(drawnSet[ib].nextOnDay - drawnSet[ib].nextOnVar/2));
			aWriter.write("-["+ib+"]-"+ dF.format(left[ib]));
			for (int iw=6; iw>3; iw--){
				if (predictWeekday==sortedWeek[iw] % 100)aWriter.write("!!");
				aWriter.write("@"+(sortedWeek[iw] % 100)+"-"+(sortedWeek[iw]/100)+">");
			}
			double r=(weeklySum[ib][predictWeekday]*weeklySum[ib][predictWeekday]);
			r /= iTtP;
			double score= left[ib] * r;
			score *= 10000;
			 ballScores[ib]=(int)(score)*100+ib;
			
			ballsToFill[ib]=(int)Math.round(left[ib]*100)*100+ib;
			//aWriter.write("|Sc="+dF.format(left));
			aWriter.newLine();
			predictForWeekday[ib]=weeklySum[ib][predictWeekday];
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
		aWriter.write("which one\t total\twk freq\t need to fill\tscores\tBestWeek ");
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
			String aLine="ball "+which1+"\t\t"+eachSum[which1]/100+"\t"+(predictForWeekday[i]/100)+"\t"+dF.format(ballsToFill[which1]*0.01/100)+"\t"+bScores[which1]/100;
			aLine += "\t"+(bestWeek[which1]/100)+"@"+(bestWeek[which1] % 100);
			aWriter.write(aLine);
			aWriter.newLine();	
		}
		aWriter.newLine();
		
		aWriter.newLine();
		k=0;
		Arrays.sort(ballsToFill);
		for (int i=39; i>=0; i--){
			if (ballsToFill[i] < 1) continue;
			k++;
			aWriter.write("["+(ballsToFill[i] % 100)+"]"+dF.format(ballsToFill[i]*0.01/100)+", ");
			if (k % 10==0) aWriter.newLine();
			
		}
		aWriter.newLine();
		
		//JackpotReader.getDataFreqStatistics(predictForWeekday, aWriter);
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
			Files.copy(pathHistory, pathToday);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}