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

public class CurveDataCheck extends JFrame implements Runnable 
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

static Vector<int[]>  getPrimeData(Vector<int[]> inData){
	Vector<int[]> retV=new Vector<int[]>();
	final int totalSample=inData.get(0).length;
	for (int k=0; k<inData.size(); k++){
		int[] prime=new int[totalSample];
		for (int i=0; i<totalSample-1; i++){
			prime[i]=inData.get(k)[i]-inData.get(k)[i+1];
		}
		retV.add(prime);
	}
	return retV;
}

int[] rangeBulkReplication(Vector<int[]> inData, int range){
	Vector<int[]> outResult=new Vector<int[]>();
	final int totalSample=inData.get(0).length;
	if (totalSample < range) return null;
	
	Vector<int[]> prime=getPrimeData(inData);
	int[] iValue=new int[totalSample];
	Arrays.fill(iValue, 40);
	int iSet=inData.size();
	float[] avg=new float[iSet];
	Arrays.fill(avg, 0);
	for (int i=0; i<iSet; i++){
		int[] home=Arrays.copyOf(prime.get(i), range);//inData.get(i), range);
		
		outResult.add(Arrays.copyOf(iValue, iValue.length));	
		for (int j=1; j<totalSample-range-1; j++){
			long value=0;
			for (int k=0; k<range; k++){
				value += Math.abs(prime.get(i)[j+k]-home[k]);
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

static Vector<int[]> newProject=new Vector<int[]>();

Vector<int[]> rangeReplication(Vector<int[]> inData, int range, int level){
	Vector<int[]> outResult=new Vector<int[]>();
	final int totalSample=inData.get(0).length;
	if (totalSample < range) return null;
	Vector<int[]> prime=prime1;//getPrimeData(inData);
	//Vector<int[]> prime2=getPrimeData(inData);
	int[] iValue=new int[totalSample];
	Arrays.fill(iValue, 40);
	int iSet=inData.size();
	float[] avg=new float[iSet];
	Arrays.fill(avg, 0);
	for (int i=0; i<iSet; i++){
		int[] home=Arrays.copyOf(prime.get(i), range);
		outResult.add(Arrays.copyOf(iValue, iValue.length));	
		long fMax=-9999999, fMin=999999;		
		for (int j=1; j<totalSample-range-1; j++){
			long value=0;
			for (int k=0; k<range; k++){
				value += Math.abs(prime.get(i)[j+k]-home[k]);
			}
			if (fMax < value) fMax=value;
			if (fMin > value) fMin=value;
			outResult.get(i)[j]=(int)value;
		}
		System.out.print("Line "+i+" range "+range+ "has min "+fMin+" at [pos/proj] (");
		for (int n=1; n<totalSample-range; n++)
		{
			if (outResult.get(i)[n]==fMin){
				System.out.print(""+n+"/"+(inData.get(i)[0]+prime1.get(i)[n-1])+",");
			}
			float m=outResult.get(i)[n];
				m -= fMin;
				m /= (fMax-fMin);
				m *= 38;
				outResult.get(i)[n]=39-Math.round(m);
		}	
		System.out.println(") ");
	}
	
	newProject.clear();
	for (int k=0; k<5; k++){
		//System.out.print("Line "+k+" range "+range+ "has 1st/2nd deriv (");
		for (int i=0; i<range-1; i++){
			//System.out.print(","+prime1.get(k)[i]+"/"+prime2.get(k)[i]);
		} 
		int[] projects=new int[totalSample-range-1];
		int iProj=0;
		//System.out.println(")");
		for (int i=1; i<totalSample-range-1; i++){
			if (outResult.get(k)[i] <level) continue;
			
				//System.out.print("Line "+k+", with "+outResult.get(k)[i]+" at "+i+" 1st/2nd prime(");
				for (int p=0; p<range-1; p++){
					//System.out.print(""+prime1.get(k)[i+p]+"/"+prime2.get(k)[i+p]+",");
				} 
				projects[iProj++]=inData.get(k)[0]+prime1.get(k)[i-1];
				//System.out.println(") and project:["+(inData.get(k)[0]+prime1.get(k)[i-1]));
			
		}
		//newProject.add(Arrays.copyOf(projects, iProj));
		//System.out.print("Line "+k+" range "+range+ " has projects (");
		for (int i=0; i<iProj; i++){
			updateCount(projects[i]);
		}
		//System.out.println(") ");
	}
	
	return outResult;
}


//end of program
static int[] dFreq=new int[40];
static void updateCount(int val){
			int iV=val;
			if (iV > 43) iV=-5;
			if (iV > 39) iV=39;
			if (iV < -3) iV=99;
			if (iV < 1) iV=1;
			if (iV==99) iV=0;
			dFreq[iV]++;//Integer.parseInt(aLine.substring(i0, i9));
}
static Vector<int[]> globalSample=null;
static Vector<int[]> prime1=null;
static Vector<int[]> prime2=null;

static void checkFor(String howOpen, int level){
	JackpotReader jReader=JackpotReader.getInstance("test539.txt", howOpen);//"大小順序");//"開出順序");//
	Vector<int[]> sampleData=jReader.readData(5, 39, 320);	
	globalSample=sampleData;
		
	//Vector<int[]> 
	prime1=getPrimeData(globalSample);
	//Vector<int[]> 
	prime2=getPrimeData(prime1);
	
	Arrays.fill(dFreq, 0);
	
	CurveDataCheck aCharter=new CurveDataCheck();
		
//---------------------------------------------------------------			
		
		/**/
	
		System.out.println("Checking "+howOpen+" on "+level);
	
		for (int i=4; i<13; i++){
			aCharter.rangeReplication(sampleData, i, level);			
		}
		for (int i=0; i<40; i++){
			System.out.println("at "+i+" has ("+dFreq[i]+")");
		}

}
public static void main(String[] args)
{

	checkFor("大小順序", 38);//"開出順序");//
	checkFor("大小順序", 39);
	checkFor("開出順序", 38);//
	checkFor("開出順序", 39);//
/*			
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
			*/
			
		}
		
		

}