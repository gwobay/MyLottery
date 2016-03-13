/*
 * 1.2 version.
 */

import java.io.*;
import java.util.Arrays;
/*
import java.util.*;
*/
import java.util.Date;
import java.util.Calendar;
//import java.util.regex.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.awt.geom.*;

import javax.swing.*;

import java.awt.image.*;

import javax.imageio.*;

import java.io.File;
import java.util.Vector;
import java.io.IOException;
import java.lang.Thread;

/* 
 * This is like the FontDemo applet in volume 1, except that it 
 * uses the Java 2D APIs to define and render the graphics and text.
 *
 * read data file data5day generated by Script c.data5day
 */

public class QkDataMath extends JFrame implements Runnable 
{
/**
	
	 */
	private static final long serialVersionUID = 161720875199630789L;
	//start of program
    final static int maxCharHeight = 30;
    final static int minFontSize = 12;
    final static int maxchartPoints = 400;

    final static Color bg = Color.white;
    final static Color fg = Color.black;
    final static Color red = Color.red;
    final static Color white = Color.white;

    final static BasicStroke stroke = new BasicStroke(2.0f);
    final static BasicStroke wideStroke = new BasicStroke(8.0f);

    final static float dash1[] = {10.0f};
    final static BasicStroke dashed = new BasicStroke(1.0f, 
                                                      BasicStroke.CAP_BUTT, 
                                                      BasicStroke.JOIN_MITER, 
                                                      10.0f, dash1, 0.0f);
	private static DecimalFormat dataF=new DecimalFormat("##.##");
	private static DecimalFormat dataIntF=new DecimalFormat("00");
	//static String DataSrc=new String("daily_d.txt");
    //static JFrame f;
	


    FontMetrics fontMetrics;
	static long today1stSecond=0, today930Second=0;

      
	
	Vector<String> myQuotes;
	String lastQuoteTime;
	static String mktStartTime="09:30";
	String samplingStartFrom;
	String samplingEnd;
	public String mySymbol;
	String YhSymb;
	int pVolume;
	int lastPVolTime;
	
	double priceMax, priceMin;
	int volDeltaMax, volDeltaMin;
	boolean testMode;
	String lastPrice;
	String lastVol;
	String lastChange;
	boolean isFileData;
   
	static int iMax=40;
	static int iMin=0;
	static int totalPoints=320;
	static int[] pointLine1=new int[maxchartPoints];
	static int[] pointLine2=new int[maxchartPoints];
	static int[] pointLine3=new int[maxchartPoints];
	static int[] pointLine4=new int[maxchartPoints];
	static int[] pointLine5=new int[maxchartPoints];
	static String outputDir="C:\\Users\\eric\\workspace\\GetLottery";

private class DrawData
{
public double price;
public int volDelta;
	public DrawData(double x, int y)
	{
		price=x;
		volDelta=y;
	}
	public String toString()
	{
		return "("+dataF.format(price)+", "+dataIntF.format(volDelta)+")";
	}
}

Vector<DrawData> myDrawData;
int hits;
int maxNumber;

public void init() 
{
        //Initialize drawing colors
        setBackground(bg);
        setForeground(fg);
	
}
   
	static FontMetrics pickFont(Graphics2D g2,
                         String longString,
                         int xSpace) 
	{
        	//boolean fontFits = false;
	        Font font = g2.getFont();
	        String name = font.getName();
	        int style = font.getStyle();
		g2.setFont(font = new Font(name,  style, minFontSize+20));
	        do
		{
			FontMetrics fontMetrics = g2.getFontMetrics();
	        	if ( (fontMetrics.getHeight() <= maxCharHeight)
                		 && (fontMetrics.stringWidth(longString)) <= xSpace)
			break;
			int size=g2.getFont().getSize();
			g2.setFont(font = new Font(name,  style, size-1));
        	} while (g2.getFont().getSize() > minFontSize);

	        return g2.getFontMetrics();
	}

	static int iCurrent=0;
	static int[] currentLine=null;//pointLine1;
	boolean drawSpecial;
	int[] specialData;
	Vector<int[]> samples;
	
	boolean drawGraphics(Graphics2D g2, Dimension d, Vector<int[]> dataSamples)
	{
		if (dataSamples==null || dataSamples.size()<1) return false;
	//int dmX=d.width;
	//int dmY=d.height;
		
        //Graphics2D g2 = (Graphics2D) g;
	String exS="15.6";
	 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //Dimension d = getSize();

        fontMetrics = pickFont(g2, exS, (int)(d.width*0.2));
	g2.setBackground(Color.white);
	int showWidth=fontMetrics.stringWidth(exS);
	int showHeight=fontMetrics.getHeight();
        //Color fg3D = Color.lightGray;

    int myChartWidth = (int)(d.width*0.95);
        //int myGridHeight = d.height/10;   //3/10 for vol, 6/10 for price  
	int myChartHeight=95*d.height/100;
	
        
	//readTodayData();
	
	g2.clearRect(0,0, d.width, d.height);
	Color[] useColors={Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.GREEN, Color.RED};

	//totalPoints=dataSamples.get(0).length;
	
	int dY=d.height/(maxNumber*11/10);
	int dX=d.width/(totalPoints+5);
	
    int xPos=myChartWidth-showWidth/2-dX;
    g2.setColor(Color.black);
    g2.setStroke(dashed);//new BasicStroke(1.0f));
    	
	g2.setColor(Color.black);
	g2.setStroke(stroke);
	for (int i=1; i<maxNumber+1; i += 2)
	{
		g2.drawString(""+i, myChartWidth-showWidth/3, i*dY);
		g2.draw(new Line2D.Double(myChartWidth-showWidth/2, i*dY, myChartWidth-showWidth/2+1, i*dY));
	}
        g2.draw(new Line2D.Double(0, 0, myChartWidth, 0));
        xPos=myChartWidth-showWidth/2;
                
        g2.draw(new Line2D.Double(xPos, 0, xPos, myChartHeight));
       
        GeneralPath[] polylineD = new GeneralPath[hits];
        for (int i=0; i<dataSamples.size(); i++){
        	polylineD[i]=new GeneralPath(GeneralPath.WIND_EVEN_ODD,totalPoints);       	
        	polylineD[i].moveTo(xPos, dataSamples.get(i)[0]*dY);
        }
                
        for (int i=0; i<totalPoints; i++)
        {
        	xPos -= dX;
        	if (dataSamples.get(0)[i]==0) 
        		{break;}
        	for (int j=0; j<dataSamples.size(); j++)
        	{        		
        		polylineD[j].lineTo(xPos, dataSamples.get(j)[i]*dY);
        		//int j=lineNumber;
        		//polylineD[j].lineTo(xPos, dataSamples.get(j)[i]*dY);
        	}
        	if (i % 5 == 0){
        		g2.drawString(""+i, xPos, 40*dY);
        		g2.draw(new Line2D.Double(xPos, 0, xPos, 40*dY));
        	}
        }
        for (int i=0; i<dataSamples.size(); i++){
        	g2.setColor(useColors[i]);
        	g2.draw(polylineD[i]);
        }

        if (drawSpecial && specialData != null && specialData.length > 5){
        	GeneralPath polylineS = new GeneralPath(GeneralPath.WIND_EVEN_ODD,specialData.length); 
        	xPos=myChartWidth-showWidth/2;
        	polylineS.moveTo(xPos, specialData[0]);
        	for (int i=0; i<specialData.length; i++){
        		xPos -= dX;
        		polylineS.lineTo(xPos, (40-specialData[i])*dY);
        	}
        	g2.setColor(Color.red);
        	g2.draw(polylineS);
        }
	return true;
	}

	BufferedImage buildImgBuf(Dimension d)
	{

	//int dmX=d.width;
	//int dmY=d.height;
	//Rectangle area=new Rectangle(d);
	BufferedImage gBufImg= ImgFactory.getImg(500, 600); //new BufferedImage(dmX, dmY, BufferedImage.TYPE_INT_ARGB);
	Graphics2D g2=gBufImg.createGraphics();
	if (drawGraphics(g2, d, samples))
		return gBufImg;
		return null;

	}

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Dimension d = getSize();
	BufferedImage toDraw=buildImgBuf(d);
	if (toDraw != null) g2.drawImage(toDraw, null, 0, 0);

	g2.dispose();
	
	}

	public boolean fillImg(BufferedImage bi, int width, int height, Vector<int[]> dataSamples)
	{
	Graphics g=bi.createGraphics();
	
		Graphics2D g2 = (Graphics2D) g;
        	//BufferedImage toDraw=buildImgBuf(new Dimension(width, height));
		boolean ok=drawGraphics(g2, new Dimension(width, height), dataSamples);
		//if (toDraw == null) return false;
		g2.setBackground(Color.white);
		
		if (ok) g2.drawImage(bi, null, 0, 0);
		return ok;	
	}

synchronized boolean putImg2File(BufferedImage bi, File toFile)
{
	try {
		ImageIO.write(bi, "png", toFile);
	} catch (IOException e){return false;}
		return true;
}

void getPngFile(BufferedImage bi, int width, int height, String fileName, Vector<int[]> dataSamples)
{
	Graphics g=bi.getGraphics();

	fillImg(bi, width, height, dataSamples);
	Graphics2D g2=(Graphics2D)g;
	g2.getBackground();
        
	g2.drawImage(bi, null, 0, 0);
	
	String dFF=fileName;
	File myNew=new File(dFF+".png");
	if (putImg2File(bi, myNew) ){
		File myDup=new File(dFF+"_2.png");
		File myOld=new File(dFF+"Old.png");
		if (myOld.exists()) myOld.delete();
		if (myDup.exists() && !myOld.exists()) myDup.renameTo(myOld);
		myNew.renameTo(myDup);
	}
	return;
}
		
String forFile;
String fileBase;
int lineNumber=0;
boolean drawMultiple;
public void drawDelta(int[] deltaData)
{
	int width=600, height=500;
	BufferedImage bi=ImgFactory.getImg(width, height); //new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	Graphics g=bi.getGraphics();
	drawSpecial=true;
	specialData=deltaData;
	getPngFile(bi, width, height, fileBase+forFile+"Delta", samples);
	
}
public void run() 
{
        //ShapesDemo2D Demo2D = new ShapesDemo2D();
        //f.getContentPane().add("Center", Demo2D);
        //Demo2D.init();
        int width=600, height=500;
//System.out.println("I am here");
	//readChartData();
	//f.pack();
	//Demo2D.setSize(new Dimension(width, height));
        //f.setSize(new Dimension(width, height));
        //f.setVisible(true);
	//BufferedImage bi=(BufferedImage)Demo2D.createImage(width, height);
	BufferedImage bi=ImgFactory.getImg(width, height); //new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	Graphics g=bi.getGraphics();
	//paint(g);
	//Demo2D.paint(g);
	//int i=0;
	//if (myQuotes.size() <1) return;
	if (drawMultiple){
		getPngFile(bi, width, height, fileBase+forFile+"ALL", samples);
		return;
	}
	
	for (int i=0; i<5; i++){
		Vector<int[]> thisSample=new Vector<int[]>();
		thisSample.add(samples.get(i));
		getPngFile(bi, width, height, fileBase+forFile+"_"+i, thisSample);
	}
}

void bulkNormalization(Vector<int[]> inData){
	Vector<int[]> outResult=new Vector<int[]>();
	final int totalSample=inData.get(0).length;
	int[] iValue=new int[totalSample];
	int iSet=inData.size();
	long fMax=-9999999, fMin=999999;
	for (int i=0; i<iSet; i++){
		for (int n=1; n<totalSample; n++)
		{
			int value=inData.get(i)[n];
			if (fMax < value) fMax=value;
			if (fMin > value) fMin=value;
		}
	}
	for (int i=0; i<iSet; i++){
		for (int n=1; n<totalSample; n++)
		{
			float m=inData.get(i)[n];
				m -= fMin;
				m /= (fMax-fMin);
				m *= 38;
				inData.get(i)[n]=39-Math.round(m);
		}	
	}	
}

int[] rangeBulkReplication(Vector<int[]> inData, int range){
	Vector<int[]> outResult=new Vector<int[]>();
	final int totalSample=inData.get(0).length;
	if (totalSample < range) return null;
	
	int[] iValue=new int[totalSample];
	Arrays.fill(iValue, 40);
	int iSet=inData.size();
	float[] avg=new float[iSet];
	Arrays.fill(avg, 0);
	for (int i=0; i<iSet; i++){
		int[] home=Arrays.copyOf(inData.get(i), range);
		outResult.add(Arrays.copyOf(iValue, iValue.length));	
		for (int j=1; j<totalSample-range; j++){
			long value=0;
			for (int k=0; k<range; k++){
				value += Math.abs(inData.get(i)[j+k]-home[k]);
			}
			outResult.get(i)[j]=(int)value;
		}
	}
	outResult.add(Arrays.copyOf(iValue, iValue.length)); //additional set for calculated
	
	long fMax=-9999999, fMin=999999;
	
		for (int n=1; n<totalSample-range; n++)
		{
			long value=0;
			for (int i=0; i<iSet; i++){
				value += outResult.get(i)[n];
			}
			if (fMax < value) fMax=value;
			if (fMin > value) fMin=value;
			outResult.get(iSet)[n]=(int)value;
		}
		
		for (int n=1; n<totalSample-range; n++)
		{
			float m=outResult.get(iSet)[n];
				m -= fMin;
				m /= (fMax-fMin);
				m *= 38;
				outResult.get(iSet)[n]=39-Math.round(m);
		}	
	System.out.println("Range "+range+"; has max="+fMax+"; min="+fMin);
	return outResult.get(iSet);
}

Vector<int[]> rangeReplication(Vector<int[]> inData, int range){
	Vector<int[]> outResult=new Vector<int[]>();
	final int totalSample=inData.get(0).length;
	if (totalSample < range) return null;
	
	int[] iValue=new int[totalSample];
	Arrays.fill(iValue, 40);
	int iSet=inData.size();
	float[] avg=new float[iSet];
	Arrays.fill(avg, 0);
	for (int i=0; i<iSet; i++){
		int[] home=Arrays.copyOf(inData.get(i), range);
		outResult.add(Arrays.copyOf(iValue, iValue.length));	
		long fMax=-9999999, fMin=999999;		
		for (int j=1; j<totalSample-range; j++){
			long value=0;
			for (int k=0; k<range; k++){
				value += Math.abs(inData.get(i)[j+k]-home[k]);
			}
			if (fMax < value) fMax=value;
			if (fMin > value) fMin=value;
			outResult.get(i)[j]=(int)value;
		}
		for (int n=1; n<totalSample-range; n++)
		{
			float m=outResult.get(i)[n];
				m -= fMin;
				m /= (fMax-fMin);
				m *= 38;
				outResult.get(i)[n]=39-Math.round(m);
		}	
	}
	return outResult;
}

Vector<int[]> deltaAverage(Vector<int[]> inData, int range){
	Vector<int[]> outResult=new Vector<int[]>(); 
	//each line return pos delta, neg delta, abs delta
	//get delta average, delta max freq delta deviation
	final int totalSample=inData.get(0).length;
	if (totalSample < range) return null;
	int iSet=inData.size();
	float[] avg=new float[iSet];
	Arrays.fill(avg, 0);
	for (int i=0; i<iSet; i++){
		int iPos=0, iNeg=0, iAbs=0;
		long posV=0, negV=0, absV=0;
		int[] iValue=new int[range]; //to store delta
		Arrays.fill(iValue, 40);
		int[] iFreq=new int[40*2];
		Arrays.fill(iFreq, 0);
			
		long fMax=-9999999, fMin=999999;		
		for (int j=0; j<range; j++){
			iValue[j]=inData.get(i)[j]-inData.get(i)[j+1];
			iFreq[40+iValue[j]]++;
			if (iValue[j] > 0) {iPos++; posV += iValue[j]; absV += iValue[j];}
			else if (iValue[j] < 0) {iNeg++; negV += iValue[j]; absV -= iValue[j];}			
		}
		outResult.add(iFreq);
		System.out.println("Line "+i+" for range "+range+" has pos d="+posV/iPos+"; neg d="+negV/iNeg+" abs d="+absV/range);
	}
		
	return outResult;
}

Vector<int[]> movingAverage(Vector<int[]> inData, int range){
	Vector<int[]> outResult=new Vector<int[]>();
	final int totalSample=inData.get(0).length;
	if (totalSample < range) return null;
	
	int[] iValue=new int[totalSample];
	Arrays.fill(iValue, 40);
	int iSet=inData.size();
	float[] avg=new float[iSet];
	Arrays.fill(avg, 0);
	for (int i=0; i<iSet; i++){
		//int[] home=Arrays.copyOf(inData.get(i), range);
		outResult.add(Arrays.copyOf(iValue, iValue.length));	
				
		for (int j=0; j<totalSample-range; j++){
			long value=0;
			for (int k=0; k<range; k++){
				//value += (inData.get(i)[j+k]-home[k])*(inData.get(i)[j+k]-home[k]);
				value += inData.get(i)[j+k];
			}
			value /=range;
			outResult.get(i)[j]=(int)value;
		}
	}
	return outResult;
}

Vector<int[]> getAutoCorrelation(Vector<int[]> inData){
	Vector<int[]> outResult=new Vector<int[]>();
	int[] iValue=new int[inData.get(0).length];
	Arrays.fill(iValue, 0);
	int iSet=inData.size();
	float[] avg=new float[iSet];
	Arrays.fill(avg, 0);
	for (int i=0; i<iSet; i++){
		outResult.add(Arrays.copyOf(iValue, iValue.length));
		int k=inData.get(i).length;
		for (int j=0; j<k; j++)
		{
			avg[i] += inData.get(i)[j];
		}
		avg[i] /= k;		
	}
	for (int i=0; i<iSet; i++){
		int k=inData.get(0).length;
		float fMax=-9999999, fMin=999999;
		for (int j=0; j<k; j++)
		{
			float value=0;
			for (int n=0; n<k; n++){
				float f1=inData.get(i)[n];
				if (n-j<0) continue;
				float f2=inData.get(i)[n-j];
			value += (f1-avg[i])*(f2-avg[i]);
			}
			if (fMax < value) fMax=value;
			if (fMin > value) fMin=value;
			outResult.get(i)[j]=Math.round(value);
		}
		for (int n=0; n<k; n++)
		{
			float m=outResult.get(i)[n];
				m -= fMin;
				m /= (fMax-fMin)/3;
				m *= 38;
				outResult.get(i)[n]=Math.round(m)+1;
		}	
	}
	return outResult;
}

Vector<int[]> putList2File(int[] iList, String fileName, int[] refD){
	BufferedWriter aWriter;
	Vector<int[]> returnV=new Vector<int[]>();
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileBase+fileName+".txt")));						
	} catch (FileNotFoundException e){
			return null;
	} catch (IOException e){
		return null;
	}
	int iMx=0, iAt=0;
	for (int i=1; i<iList.length; i++){
		if (iList[i]==0) break;
		if (iList[i] < 20) continue;
		try {
			aWriter.write("("+i+")["+iList[i]+"], ");
			if (iList[i] > iMx){
				iMx=iList[i];
				iAt=i;
			}
			if (i % 8 == 0) aWriter.newLine();
			//aWriter.newLine();				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}					
	} 
	int[] mX=new int[iList.length/3];
	int iX=0;
	Arrays.fill(mX, 0);
	int[] m2=new int[iList.length/3];
	int i2=0;
	Arrays.fill(m2, 0);
	int[] m3=new int[iList.length/3];
	int i3=0;
	Arrays.fill(m3, 0);
	for (int i=0; i<iList.length; i++){
		if (iList[i]==iMx) mX[iX++]=i;
		if (iList[i]==iMx-1) m2[i2++]=i;
		if (iList[i]==iMx-2) m3[i3++]=i;
	}
	try {
		aWriter.newLine();
		aWriter.write(fileName+": mx "+iMx+"; [");
		for (int i=0; i<iX; i++) if (mX[i] > 1) aWriter.write(", "+refD[mX[i]]+"|"+refD[mX[i]-1]);
		aWriter.write("]");
		aWriter.newLine();
		aWriter.write(fileName+": m2x "+(iMx-1)+"; [");
		for (int i=0; i<i2; i++) if (m2[i] > 1) aWriter.write(", "+refD[m2[i]]+"|"+refD[m2[i]-1]);
		aWriter.write("]");
		aWriter.newLine();
		aWriter.write(fileName+": m3x "+(iMx-2)+"; [");
		for (int i=0; i<i3; i++) if (m3[i] > 1) aWriter.write(", "+refD[m3[i]]+"|"+refD[m3[i]-1]);
		aWriter.write("]");
		aWriter.newLine();
		if (iX >0)
		returnV.add(Arrays.copyOf(mX, iX));
		if (i2 >0)
		returnV.add(Arrays.copyOf(m2, i2));
		if (i3>0)
		returnV.add(Arrays.copyOf(m3, i3));
		aWriter.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return returnV;
}

static int[][] scanForFit(Vector<int[]> refData, int[] testData)
{
	System.out.println("Testing  (");
	for (int i=0; i<testData.length; i++){
	System.out.print(""+testData[i]+", ");
	}
	System.out.print(")");
	int length=refData.get(0).length;
	int[][] foundData=new int[5][length];	
	int[] idx=new int[5];
	Arrays.fill(idx, 0);
	int[] iMin=new int[5];
	Arrays.fill(iMin, 99);
	int[] difV=new int[length];
	difV[0]=0;
	int iGot=0;
	for (int i=1; i<length; i++){
		int iDif=0;
		for (int k=0; k<5; k++){
			iDif += Math.abs(refData.get(k)[i]-testData[k]);
		}
		difV[i]=iDif;
		int iRep=4;
		while (iRep > -1){
			if (iDif > iMin[iRep]) break;
				iRep--; 	
		}
		if (iRep < 4) {			
			for (int k=3; k>iRep; k--){
				iMin[k+1]=iMin[k];
			}
			iMin[iRep+1]=iDif;
		}
		//if (iDif < 5){
			//foundData[iDif][idx[iDif]++]=i;
		//}		
	}
	for (int i=1; i<length; i++){
		if (difV[i] > iMin[4]) continue;
		int k=4;
		while (k>-1){
			if (difV[i] == iMin[k]) break;
		    k--;
		}
		System.out.println("Found "+k+"th min "+iMin[k]+" at "+i+" (");
		for (int ix=0; ix<5; ix++){
			System.out.print(""+refData.get(ix)[i]+", ");
		}
		System.out.print(") and prediction (");
		for (int ix=0; ix<5; ix++){
			System.out.print(""+refData.get(ix)[i-1]+", ");
		}
		System.out.println(") ");
	}
	
	return foundData;
}
//end of program
static Vector<int[]> globalSample=null;
public static void main(String[] args)
{
		JackpotReader jReader=JackpotReader.getInstance("test539.txt","大小順序");
		Vector<int[]> sampleData=jReader.readData(5, 39, 320);	
		globalSample=sampleData;
		int[] testData={sampleData.get(0)[0], sampleData.get(1)[0], sampleData.get(2)[0], sampleData.get(3)[0], sampleData.get(4)[0]}; 
		scanForFit(sampleData, testData);
		
		QuoteCharter qCharter=new QuoteCharter();
		//aCharter.myQuotes=allData;
		qCharter.samples=new Vector<int[]>();//sampleData;
		for (int i=0; i<sampleData.size(); i++){
			qCharter.samples.add(Arrays.copyOf(sampleData.get(i), 72));
		}
		qCharter.hits=5;
		qCharter.maxNumber=39;
		qCharter.run();
		
		QkDataMath aCharter=new QkDataMath();
		//aCharter.myQuotes=allData;
		Vector<int[]> atf=aCharter.getAutoCorrelation(sampleData);
		if (atf.size() > 0 && atf.get(0).length > 10){	
			aCharter.fileBase="C:\\Users\\eric\\workspace\\GetLottery\\539\\";
			File myDir=new File(aCharter.fileBase);
			if (!myDir.exists()) myDir.mkdir();

			for (int i=0; i<5; i++){
			aCharter.putList2File(Arrays.copyOf(atf.get(i), 252), "autoline"+(i+1),
					globalSample.get(i));
			}

			aCharter.samples=atf;
			totalPoints=atf.get(0).length>56?56:atf.get(0).length;	
			aCharter.drawMultiple=true;
			aCharter.hits=5;
			aCharter.maxNumber=39;
			aCharter.forFile="auto";
			aCharter.run();	
			
			
			
//---------------------------------------------------------------			
			aCharter.fileBase="C:\\Users\\eric\\workspace\\GetLottery\\539\\replica\\";
			myDir=new File(aCharter.fileBase);
			if (!myDir.exists()) myDir.mkdir();
			
			Vector<int[]> rngR=new Vector<int[]>();
			Vector<int[]> rngBkl=new Vector<int[]>();
			for (int i=0; i<5; i++){
					rngR.add(aCharter.rangeBulkReplication(sampleData, 4+i));//3+i*6));
			}
			aCharter.bulkNormalization(rngR);
			for (int il=0; il<5; il++){
			    Vector<int[]> bkMax=aCharter.putList2File(Arrays.copyOf(rngR.get(il), 252), "dummy",
					globalSample.get(il));
			    System.out.print(new DecimalFormat("00").format(il+4)+": ");
			    for (int i=0; i<bkMax.size(); i++){
			    	int[] bMx=bkMax.get(i);
			    	
			    	for (int j=0; j<bMx.length; j++)
			    	{
			    		System.out.print("mx"+i+" at-"+bMx[j]+" (");
			    		for (int k=0; k<5; k++){
			    			if (bMx[j]>1)
			    			System.out.print(""+globalSample.get(k)[bMx[j]-1]+", ");
			    		}
			    		System.out.println(" ) ");
			    	}
			    }
			}
			aCharter.samples=rngR;
			totalPoints=rngR.get(0).length>100?100:rngR.get(0).length;		
			aCharter.hits=5;
			aCharter.maxNumber=39;
			aCharter.forFile="bulk";
			aCharter.run();	
			
			/**/
			for (int i=0; i<12; i++){
			rngR=aCharter.rangeReplication(sampleData, i+4);
				for (int k=0; k<5; k++){
			aCharter.putList2File(Arrays.copyOf(rngR.get(k), 252), "rep"+(i+4)+"line"+(k+1),
					globalSample.get(k));
				}	
				aCharter.samples=rngR;
				aCharter.forFile="line"+new DecimalFormat("00").format(i+4);
				aCharter.run();	
			}

			
			aCharter.fileBase="C:\\Users\\eric\\workspace\\GetLottery\\539\\";
			aCharter.hits=2;
			
			Vector<int[]> dlAvg240=aCharter.deltaAverage(sampleData, 240);
			Vector<int[]> dlAvg24=aCharter.deltaAverage(sampleData, 24);
			for (int k=1; k<5; k++){
				Vector<int[]> mvAvg=aCharter.movingAverage(sampleData, 6*k);			
				Vector<int[]> avgData=new Vector<int[]>();
				for (int i=0; i<5; i++){
					avgData.add(Arrays.copyOf(sampleData.get(i), 72));
					avgData.add(Arrays.copyOf(mvAvg.get(i), 72));
					aCharter.samples=avgData;
					totalPoints=avgData.get(0).length>72?72:avgData.get(0).length;
					aCharter.forFile="mvAvg_"+(6*k)+"_240_"+i;
					aCharter.drawDelta(Arrays.copyOf(dlAvg240.get(i), 72));
					aCharter.forFile="mvAvg_"+(6*k)+"_24_"+i;
					aCharter.drawDelta(Arrays.copyOf(dlAvg24.get(i), 72));
					avgData.clear();
				}
			}
			
		}
		
		
}
}
