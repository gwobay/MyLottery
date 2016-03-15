import java.net.*;
import java.io.*;
import java.net.URLConnection;
import java.awt.event.*;
import java.util.zip.*;
import java.util.Vector;
import javax.swing.Timer;

public class WebPageJob implements ActionListener
{
private final static int MAXINT=999999999;
public static boolean slowSite=false;
public static long socketSleepTime=100;
public static long socketWaitTime=10000;
	URL yahooIC;
	DataInputStream myInStream;
	DataInputStream currentInStream;
	String readPage;
	String myUrl;
	boolean zippedContent;
	boolean failed;
	//Timer socketTimer;
	int totalRead;	

public void actionPerformed(ActionEvent e)
{
try { if (currentInStream != null) currentInStream.close(); currentInStream=null;
System.out.println(myUrl+" got timeout !!! ");
} 
catch (IOException er){}
}

public WebPageJob(String pageUrl)
{
failed=false;
zippedContent=false;
	try {
		myUrl=new String(pageUrl);
		yahooIC = new URL(pageUrl);
		int iTry=3;
		do {
	        		URLConnection aCon=yahooIC.openConnection();
			try {Thread.sleep(15); iTry--;}catch(InterruptedException e){break;}
		myInStream = new DataInputStream(aCon.getInputStream());
//System.out.println("URL time out setting is "+aCon.getReadTimeout());
		//myInStream = new DataInputStream(yahooIC.openStream());
		} while (myInStream == null && iTry > 0);
		readPage=new String("");
		totalRead=0;
		return;
	} catch (MalformedURLException e)
	{
		System.out.println("Bad URL");
	}
	catch (IOException e)
	{System.out.println("Cannot open URL:"+pageUrl);}
		yahooIC=null;
		readPage=null;
}

public WebPageJob()
{
		readPage=new String("");
		zippedContent=false;
		totalRead=0;
}

static void setConnectionWaitTime(int i)
{

}
void setZipFlag(boolean Y_N)
{
zippedContent=Y_N;
}

boolean getZipFlag()
{
return zippedContent;
}

boolean dataTimeout(DataInputStream inUrl) throws IOException
{
return true;
/*
long iSleep=0;
	while (iSleep < socketWaitTime)
	{
		int iA=inUrl.available();
		if (iA > 0) return false;
		try {
			iSleep += socketSleepTime; 
			Thread.sleep(socketSleepTime); 
		}
		catch (InterruptedException e){break;}
	}
System.out.println(myUrl+" got timeout !!! ");
	return true;
*/
}

int readZippedContent()
{
boolean webNoData=false;

byte[] dataBytes= new byte[50000];
int i=0;

   while (!webNoData)
{
try{
	dataBytes[i++]=currentInStream.readByte();
   } catch (EOFException e)
	{
		webNoData=true;
	}
	catch (SocketTimeoutException e)
	{
		webNoData=true;
	}
	catch (IOException e)
	{
		System.out.println("End of Connection-itf");
		return -1;
	}
   }
	if (i<5) return 0;
byte[] unZipData=new byte[10*i];
	try {
	ByteArrayInputStream zippedData=new ByteArrayInputStream(dataBytes, 0, i);
	GZIPInputStream gzipLine=new GZIPInputStream(zippedData, i+1);
	int iL=gzipLine.read(unZipData, 0, 10*i);
	gzipLine.close();
	String inflated=new String(unZipData, 0, iL, "UTF-8");

	readPage=inflated.toUpperCase();
	totalRead=inflated.length();
							
	} catch (IOException e){
	System.out.println("Bad Zipped Data");
             zippedContent=false;
		return -1;
         }
	return totalRead;
}

boolean readStreamData(int readSize)
{
if (zippedContent)
{
	return  (readZippedContent() > 0);
}
Timer socketTimer=new Timer((int)socketWaitTime, this);
	socketTimer.setRepeats(false);
	//socketTimer.addActionListener(this);
	socketTimer.start();
String pageSrc=new String("");
try 
{
	if (currentInStream==null) return false;
	byte[] byteBlock=new byte[2000+1];
	int iRead=0;
	int iSize=0;
	while (currentInStream != null && iRead >=0)
	{
			iRead=currentInStream.read(byteBlock, 0, 2000);
			if (iRead < 0) break;
			if (iRead > 0) socketTimer.restart();
			pageSrc += ( new String(byteBlock, 0, iRead));
			iSize += iRead;
			totalRead += iRead;
			if (iSize > readSize) break;
	}
	socketTimer.stop();
	if (currentInStream != null)
	currentInStream.close(); 
	currentInStream=null;
	readPage=pageSrc.toUpperCase();
	failed=false;
	return true;
}catch (IOException e) {readPage=pageSrc.toUpperCase();
	socketTimer.stop(); failed=true;return false;}
	
}

boolean badReturn(int i, String msg)
{
System.out.print("!!!<<"+msg+">>!!!");

if (i ==0)
{
	readPage=null;
	totalRead=0;
}
else
System.out.println(readPage.replaceAll("[^\\x20-\\xFE]", "@"));
	
	return false;
}

public boolean getPageSrc(int readSize)
{
	if (readPage == null) readPage=new String("");
	currentInStream=myInStream;
	readStreamData(readSize);
	if (readPage.length() < 1) return badReturn(0, "No Data");
	return true;
}

public boolean getPageSrc(String startKey, String endKey)
{
	//if (yahooIC == null) {readPage=null; return false;}
	//if (readPage == null) readPage=new String("");
	if (currentInStream==null) currentInStream=myInStream;
	readStreamData(MAXINT);
	if (readPage.length() < 1) return badReturn(0, "No data read");
	String pageSrc=readPage.toUpperCase();
	int iKey1=0;
	if (startKey.length() > 0) 
	{
		iKey1=pageSrc.indexOf(startKey.toUpperCase());
		if (iKey1 < 0) return badReturn(1, "Missing "+startKey);
	}
	int iKey2=readPage.length();
	if (endKey.length() > 0)
	{
		iKey2=pageSrc.indexOf(endKey.toUpperCase(), iKey1+1);
		if (iKey2 < 0) 
		return badReturn(2, "Missing "+endKey);
	}
	readPage=pageSrc.substring(iKey1, iKey2);
	return true;
}


public boolean getPageSrc()
{
return getPageSrc("<html", "/html>");
}

public boolean getPageSrc(DataInputStream inUrl, String startKey, String endKey)
{
	if (readPage == null) readPage=new String("");
	currentInStream=inUrl;
	return getPageSrc(startKey, endKey);
}

String readColumnString(String columnData1)
{
// possible scenario :	1).   	.... > here good data ..<..> another part of data <...
//		2).	<....> here good data ...
// 		3).	here good data <...> another part of data <...

//new method 
	String[] allCuts=columnData1.split("<");
	String columnString=new String("");
	for (int i=0; i< allCuts.length; i++)
	{
		int iR=allCuts[i].indexOf(">");
		if (iR == -1 || iR >=0 && allCuts[i].length() > iR+1)
		columnString += allCuts[i].substring(iR+1);	
	}
return columnString.replaceAll("&NBSP;", " ");
}

public Vector<String> getRowData(String rowBytes)
{
	String[] columnD=rowBytes.toUpperCase().split("</TD>");
	Vector<String> columnData=new Vector<String>();
	for (int i=0; i<columnD.length; i++)
	{
		String aColumn=readColumnString(columnD[i]);
		columnData.add(aColumn.trim());
	}
	return columnData;
}

public Vector<String> splitTableRow(String aTable)
{
	String[] rowD=aTable.toUpperCase().split("</TR>");
	Vector<String> rowData=new Vector<String>();
	for (int i=0; i<rowD.length; i++)
	{
		rowData.add(rowD[i]);
	}
	return rowData;
}

static String removeComma(String inDollar)
{
	if (inDollar.indexOf(",") < 0) return inDollar;
	String[] tokenS=inDollar.split(",");
	String retS=new String("");
	for (int i=0; i< tokenS.length; i++)
	retS += tokenS[i];
	return retS;
}

public boolean readImgData(byte[] readBytes)
{
	//int toRead=readBytes.length;
	//byte[] readBytes=new byte[toRead+1];
	if (currentInStream == null) currentInStream=myInStream;
	
	boolean successful=false;
	try 
	{
		//byte[] byteBlock=new byte[20000+1];
		int iRead=0;
		int iPos=0;
		while (iRead >=0)
		{
				iRead=currentInStream.read(readBytes, iPos, 20000);
				if (iRead < 0) break;
				iPos += iRead;
				//totalRead += iRead;
				successful=true;
				//if (iSize > toRead) break;
		}
		currentInStream.close(); currentInStream=null;
		return successful;
	}catch (IOException e) {}
		//if (successful) return readBytes;
		return successful;
		
}
}


