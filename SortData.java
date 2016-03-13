import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Vector;


public class SortData {

	String dataFileName;
	Vector<String> dailyNumberSet;
	static Vector<String> allDataSets=new Vector<String> ();
	String keyToPickUp;
	
	
	public Vector<String>  getAllData()
	{
		return allDataSets;
	}
	
	static public SortData getInstance(String fileName, String whichLine){
		if (fileName==null) return null;
		SortData aReader=new SortData();
		aReader.dataFileName=fileName;
		aReader.keyToPickUp=whichLine;
		return aReader;
	}
	
	
	public void showData(Vector<int[]> vData, String fileName){
		Vector<int[]> outV=new Vector<int[]>();
		
		BufferedWriter aWriter;
		try {
			aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
			for (int k=0; k<vData.size(); k++){
				int[] data=vData.get(k);
			
			for (int i=0; i<data.length; i++){
				data[i] *= 100;
				data[i] += i;
			}
			Arrays.sort(data);
			for (int i=data.length-1; i>0; i--){
				int freq=data[i]/100;
				if (freq<1) break;
				aWriter.write("line "+(k+1)+" number"+ (data[i]%100)+" shows "+freq+" times");
				aWriter.newLine();
			}
			aWriter.newLine();
			}
			aWriter.close();
		} catch (FileNotFoundException e){
				return;
		} catch (IOException e){
			return;
		}
	}
	public void setKeyToPickUp(String key){
		keyToPickUp=key;
	}
	static final int ballMax=100;
	static int[] balls=new int[ballMax+1];
	static int[] dFreq=new int[40];
	
	static void updateCount(String aLine){
		int i0=0;
		int i9=0;
		int ix=0;
		while (ix < aLine.length()){
			while (i0 < aLine.length() && aLine.charAt(i0)<'0') i0++;
			if (i0==aLine.length()) break;	
			i9=i0;
			while (i9<aLine.length() && aLine.charAt(i9)> ' '&& aLine.charAt(i9)!=',') i9++;
				int iV=Integer.parseInt(aLine.substring(i0, i9));
				if (iV > 43) iV=-5;
				if (iV > 39) iV=39;
				if (iV < -3) iV=99;
				if (iV < 1) iV=1;
				if (iV==99) iV=0;
				dFreq[iV]++;//Integer.parseInt(aLine.substring(i0, i9));
				if (i9==aLine.length()) break;	
				i0=i9;
		}	
	}
	static public void readData(){
		String fileName="C:\\Users\\eric\\workspace\\GetLottery\\data.txt";
		Arrays.fill(dFreq, 0);
		BufferedReader mReader;
		try {
			mReader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));						
		} catch (FileNotFoundException e){
				return ;
		} catch (IOException e){
			return ;
		}
		int[] dataCount=new int[40];
		Arrays.fill(dataCount, 0);
		String aLine=null;
		do {
			try {
				aLine=mReader.readLine();
				if (aLine == null) break;
				int idx=aLine.indexOf("(");
				if ( idx < 0) continue;
				int iEnd=aLine.length()-1;
				while (iEnd > idx && aLine.charAt(iEnd) != ')') iEnd--;
				updateCount(aLine.substring(idx+1, iEnd));
				//int[] numbs=getNumberSet(aLine.substring(idx+1, iEnd));
				//if (numbs== null || numbs.length < 1) continue;
				//for (int i=0; i<numbs.length; i++){
					//dataCount[numbs[i]]++;
				//}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}			
		} while (aLine != null)	;
		for (int i=0; i<40; i++){
			System.out.println("at "+i+" has ("+dFreq[i]+")");
		}
		return;
	}
	
	static public int[] getNumberSet(String aLine){
		int[] temp=new int[10];
		int i0=0;
		int i9=0;
		int ix=0;
		while (ix < aLine.length()){
			while (i0 < aLine.length() && aLine.charAt(i0)<'0') i0++;
			if (i0==aLine.length()) break;	
			i9=i0;
			while (i9<aLine.length() && aLine.charAt(i9)> ' '&& aLine.charAt(i9)!=',') i9++;
				int iV=Integer.parseInt(aLine.substring(i0, i9));
				if (iV > 41) iV=-5;
				if (iV > 39) iV=39;
				if (iV < -2) iV=99;
				if (iV < 1) iV=1;
				if (iV==99) iV=0;
				temp[ix++]=iV;//Integer.parseInt(aLine.substring(i0, i9));
				if (i9==aLine.length()) break;	
				i0=i9;
		}
		//if (ix)
		int[] result=new int[ix];
		for (int i=0; i<ix; i++) result[i]=temp[i];
				
		return result;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readData();		
	}

}
