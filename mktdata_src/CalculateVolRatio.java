import java.io.*;
import java.util.Vector;
import java.io.File;
import java.text.DecimalFormat;
import java.util.TreeMap;

public class CalculateVolRatio //calulate the daily quote volume ratio between that of main market and that of nasd
{
static DecimalFormat IntF=new DecimalFormat("00");
static Vector<String> symbolV=null;
static TreeMap<String, String> volEodMap=new TreeMap<String, String>();
static TreeMap<String, String> volRealMap=new TreeMap<String, String>();
static String todayDate=null;

static String removeCommas(String aLine)	
{
if (aLine.indexOf(",") <0) return aLine;	
String[] tags=aLine.split(",");
String rS="";
	for (int i=0; i< tags.length; i++)		
	{			
		rS += tags[i];
	}	
	return rS;
}

static String removeHtmTags(String aLine)	
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

static Vector<String> buildSymbList(String symbName)
{
Vector<String> symbV=new Vector<String>();
	try 	
	{		
		BufferedReader gListF=new BufferedReader(new FileReader(symbName+".txt"));		
		//int iC=0;
		String inputLine;
		while ((inputLine=gListF.readLine()) != null)		
		{			
			if (inputLine.length() < 3 || inputLine.length() > 15*5 || inputLine.charAt(0) == '>') continue;			
			String[] symb=inputLine.split(",");
			for (int i=0; i<symb.length; i++)
			{
				if (symb[i].length() < 1 || symb[i].length() > 5)
				continue;
				symbV.add(symb[i].toUpperCase());
System.out.print(symb[i]+",");
			}
		}
		gListF.close();
System.out.println(">");
	} catch (IOException e){return null;}
return symbV;

}

static String getRealVol(String forThis)
{
System.out.print("for symbol:"+forThis+">");			

String fileName="USA_data\\"+forThis+"\\"+forThis+todayDate+"Quote.txt";		
File myDir=new File("USA_data\\"+forThis+"\\");	
System.out.println("Checking "+myDir);	
	if (!myDir.exists()) {System.out.println(forThis+" not exist"); return null;}		
	String aLine, bLine=null;		
	try {		
BufferedReader QuotesF=new BufferedReader(new FileReader(fileName));
		while ((aLine=QuotesF.readLine()) != null){bLine=aLine;}
		QuotesF.close();		
	} catch (IOException e){ return null; }	
	if (bLine == null) return null;
		
String[] tokens=bLine.replace("24:", "12:").split("-");
	volRealMap.put(forThis, tokens[4]);
	return tokens[4];
					
}

static void buildEodList()
{
	try 	
	{		
		BufferedReader gListF=new BufferedReader(new FileReader("ref_NASD_vol.txt"));		
		String symbol=null;
		String aLine;
		while ((aLine=gListF.readLine()) != null)		
		{
			if (aLine.indexOf("symb-col") > 0)
			{
				int idx=aLine.indexOf("symb-col");			
				symbol=removeHtmTags(aLine.substring(idx)); 
				continue;
			}
			if (aLine.indexOf("volume-col") > 0)
			{
				int idx=aLine.indexOf("volume-col");			
						
				String volume=removeHtmTags(aLine.substring(idx)); 
				if (symbol != null) 
				{
					volEodMap.put(symbol, volume);
				String realVol=getRealVol(symbol);
				System.out.print(symbol+":"+removeCommas(volume)+":");
				if (realVol != null)
				{
					System.out.println(realVol+"<"+(Double.parseDouble(realVol)/Double.parseDouble(removeCommas(volume))));
	
				} else System.out.println("No realtime data");
				}
				symbol=null;
				continue;
			}
			
		}
		gListF.close();
	} catch (IOException e){}

}
		
public static void main(String[] args) 
{
//symbolV=buildSymbList("Nasd_Symb");
//if (symbV==null || symbV.size() < 1) System.exit(0);
	todayDate=QuoteReader.getTodayYYMMDD();
buildEodList();	
			
}
}