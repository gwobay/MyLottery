import java.net.*;import java.io.*;import java.util.Date;
import java.util.Calendar;
import java.awt.*;import java.awt.event.*;import java.awt.Toolkit;
import javax.swing.*;
import java.util.regex.*;import java.util.Vector;import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;import java.util.TreeSet;
import java.util.TreeMap;import java.util.Vector;
import java.lang.Thread;import javax.swing.Timer;

public class SaveQuote2File extends Thread
{
static TreeMap<String, String> profitWarning=new TreeMap<String, String>();
static TreeMap<String, String> lossWarning=new TreeMap<String, String>();
static boolean wait4P=false, wait4L=false;
String dataDir;MktQuote toSave;String today;	

public void addWarning(String symbol, String checkP, boolean forP)	
{		
if (forP) profitWarning.put(symbol, checkP);		else		lossWarning.put(symbol, checkP);	
}	

public SaveQuote2File(MktQuote toSaveThis, String... dir)	
{
	dataDir="";		
	toSave=toSaveThis;
	today=QuoteReader.getTodayYYMMDD();
	if (dir.length > 0) dataDir=dir[0];	
}
	
public void setDir(String dir)
	{
		dataDir=dir;
	}	

public void run()	
{		
String Symb=toSave.symbol;		
String fileName=dataDir+toSave.symbol+"/"+toSave.symbol+today+"Quote.txt";		
File myDir=new File(dataDir+toSave.symbol+"/");		
	if (!myDir.exists()) myDir.mkdir();		
	try {		
BufferedWriter QuotesF=new BufferedWriter(new FileWriter(fileName,true));		
		QuotesF.write(toSave.toString());		
		QuotesF.newLine();		
		QuotesF.close();		
	} catch (IOException e){  }		
	if (!checkProfitPrice(toSave.symbol, toSave.price))		
		checkLossPrice(toSave.symbol, toSave.price);				
}	

boolean checkProfitPrice(String symbol, String price)	
{	
double ckP=0;		
	while (wait4P)		
	{		
		try{sleep(10);}catch(InterruptedException e){}
	}			
	synchronized (profitWarning) 
	{			
		wait4P=true;			
		{			
			String 		sP=profitWarning.get(symbol);				
			if (sP!=null)	ckP=Double.parseDouble(sP);			
		}			
		wait4P=false;		
	}		
	if (ckP > 0 && Double.parseDouble(price) > ckP)		
	{
		System.out.println("Price warning for "+symbol+" at "+price+" > "+ckP);				Toolkit.getDefaultToolkit().beep();			
		return true;		
	}		
return false;			
}	

boolean checkLossPrice(String symbol, String price)	
{	
double ckP=0;		
	while (wait4L)
	{
		try{sleep(10);}catch(InterruptedException e){}
	}
	synchronized (lossWarning) 
	{			
		wait4L=true;			
		{
			String sP=lossWarning.get(symbol);
			if (sP!=null)
			ckP=Double.parseDouble(sP);
		}
		wait4L=false;
	}
	if (ckP > 0 && Double.parseDouble(price) < ckP)	
	{
		System.out.println("Price warning for "+symbol+" at "+price+" < "+ckP);
		Toolkit.getDefaultToolkit().beep();
		return true;
	}
	return false;
}

}

class WarningQuotes
{
String symbol;
double checkPrice;
boolean takeProfit;	
WarningQuotes(String a, double p, boolean t)	{	}
}
