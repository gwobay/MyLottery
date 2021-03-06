/*
 * 1.2 version.
 */

import java.io.*;
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

public class QuoteCharter extends JFrame implements Runnable 
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
	Vector<int[]> samples;
	boolean drawGraphics(Graphics2D g2, Dimension d)
	{
		if (samples==null || samples.size()<1) return false;
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

	totalPoints=samples.get(0).length;
	
	int dY=d.height/(maxNumber*11/10);
	int dX=d.width/(totalPoints+5);
	
    int xPos=myChartWidth-showWidth/2-dX;
    g2.setColor(Color.black);
    g2.setStroke(dashed);//new BasicStroke(1.0f));
    while (xPos > 0)
    {
    	g2.draw(new Line2D.Double(xPos, 0, xPos, myChartHeight));
    	xPos -= dX;
    }
    	
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
       
        GeneralPath[] polylineD = new GeneralPath[5];
        for (int i=0; i<hits; i++){
        	polylineD[i]=new GeneralPath(GeneralPath.WIND_EVEN_ODD,totalPoints);       	
        	polylineD[i].moveTo(xPos, samples.get(i)[0]*dY);
        }
                
        for (int i=0; i<totalPoints; i++)
        {
        	xPos -= dX;
        	if (samples.get(0)[i]==0) break;
        	for (int j=0; j<hits; j++)
        	{
        		
        		polylineD[j].lineTo(xPos, samples.get(j)[i]*dY);
        	}
        }
        for (int i=0; i<hits; i++){
        	g2.setColor(useColors[i]);
        	g2.draw(polylineD[i]);
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
	if (drawGraphics(g2, d))
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

	public boolean fillImg(BufferedImage bi, int width, int height)
	{
	Graphics g=bi.createGraphics();
	
		Graphics2D g2 = (Graphics2D) g;
        	//BufferedImage toDraw=buildImgBuf(new Dimension(width, height));
		boolean ok=drawGraphics(g2, new Dimension(width, height));
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
	int i=0;
	/*if (myQuotes.size() <1) return;
	
	for (int i=0; i<5; i++){
		switch (i){
		case 0:currentLine=pointLine1; break;
		case 1:currentLine=pointLine2; break;
		case 2:currentLine=pointLine3; break;
		case 3:currentLine=pointLine4; break;
		case 4:currentLine=pointLine5; break;
		default:
			break;
		}
	*/	
			fillImg(bi, width, height);
	Graphics2D g2=(Graphics2D)g;
	g2.getBackground();
        //g2.setForeground(fg);	
	g2.drawImage(bi, null, 0, 0);
	//try {
	mySymbol="/539";
	String dirSymb=outputDir+mySymbol;
		File myDir=new File(dirSymb+"/");
		if (!myDir.exists()) myDir.mkdir();
		
			String dFF=dirSymb+"/"+mySymbol+"_"+i;
		File myNew=new File(dFF+".png");
		if (putImg2File(bi, myNew) ){
		File myDup=new File(dFF+"_2.png");
		File myOld=new File(dFF+"Old.png");
		if (myOld.exists()) myOld.delete();
		if (myDup.exists() && !myOld.exists()) myDup.renameTo(myOld);
		myNew.renameTo(myDup);
		
		//}
	//} catch (IOException e){System.out.println("Cannot create bmp file");}
	}
}


static void putList2File(int[] iList, String fileName){
	BufferedWriter aWriter;
	try {
		aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputDir+"/539/"+fileName+".txt")));						
	} catch (FileNotFoundException e){
			return;
	} catch (IOException e){
		return;
	}
	for (int i=0; i<iList.length; i++){
		if (iList[i]==0) break;
		try {
			aWriter.write(""+iList[i]+", ");
			//aWriter.newLine();				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	} 
	try {
		aWriter.newLine();
		aWriter.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
//end of program

public static void main(String[] args)
{
Vector<String> myQuotes=new Vector<String>();
String mktStartTime="08.00";
String mySymbol="TSE";
String fileName=mySymbol+"/"+mySymbol+"120320"+"Quote.txt";
		JackpotReader aReader=JackpotReader.getInstance("test539.txt","大小順序");
		Vector<int[]> sampleData=aReader.readData(5, 39, 72);
		//Vector<String> allData=aReader.getAllData();
		
		//myQuotes=allData;
		//int iCount=myQuotes.size();
		//iCount=iCount>72?72:iCount;
		//totalPoints=iCount-1;
	
	//for (int i=iCount-1; i>0; i--){
		//int[] vData=aReader.getNumberSet(myQuotes.get(i));
		//pointLine1[iCount-1-i]=vData[0];
		//pointLine2[iCount-1-i]=vData[1];
		//pointLine3[iCount-1-i]=vData[2];
		//pointLine4[iCount-1-i]=vData[3];
		//pointLine5[iCount-1-i]=vData[4];
	//}
	/*putList2File(pointLine1, "line1");
	putList2File(pointLine2, "line2");
	putList2File(pointLine3, "line3");
	putList2File(pointLine4, "line4");
	putList2File(pointLine5, "line5");*/
	
		QuoteCharter aCharter=new QuoteCharter();
		//aCharter.myQuotes=allData;
		aCharter.samples=sampleData;
		aCharter.hits=5;
		aCharter.maxNumber=39;
		aCharter.run();

	
}
}
