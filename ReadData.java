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


public class ReadData {

	BufferedReader myReader;
	Vector<String> dailyNumberSet;
	static Vector<String> allDataSets=new Vector<String> ();
	String keyToPickUp;
	static int[] freq10=new int[40];
	
	static int[] freq25=new int[40];
	static int[] freq50=new int[40];
	static int[] freq75=new int[40];
	static int[] freq150=new int[40];
	static int[] freq300=new int[40];
	static Vector<int[]> v10=new  Vector<int[]>();
	static Vector<int[]> v25=new  Vector<int[]>();
	static Vector<int[]> v50=new  Vector<int[]>();
	static Vector<int[]> v75=new  Vector<int[]>();
	static Vector<int[]> v150=new  Vector<int[]>();
	static Vector<int[]> v300=new  Vector<int[]>();
	
	public Vector<String>  getAllData()
	{
		return allDataSets;
	}
	
	@SuppressWarnings({ "resource", "hiding" })
	static public ReadData getInstance(String fileName){
		if (fileName==null) return null;
		ReadData aReader=new ReadData();
		BufferedReader mReader;
		try {
			mReader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));						
		} catch (FileNotFoundException e){
				return null;
		} catch (IOException e){
			return null;
		}
		aReader.myReader=mReader;
		aReader.keyToPickUp=null;
		return aReader;
	}
	
	public void close()
	{
		if (myReader != null)
			try {
				myReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		myReader=null;
	}
	
	public void showData(Vector<int[]> vData, String fileName){
		Vector<int[]> outV=new Vector<int[]>();
		
		BufferedWriter aWriter;
		try {
			aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName+".txt")));
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
	public void readData(){
		Arrays.fill(freq10, 0);
		Arrays.fill(freq25, 0);
		Arrays.fill(freq50, 0);
		Arrays.fill(freq75, 0);
		Arrays.fill(freq150, 0);
		Arrays.fill(freq300, 0);
		v10.add(freq10); v10.add(Arrays.copyOf(freq10, 40));v10.add(Arrays.copyOf(freq10, 40));v10.add(Arrays.copyOf(freq10, 40));v10.add(Arrays.copyOf(freq10, 40));
		v25.add(freq25); v25.add(Arrays.copyOf(freq25, 40));v25.add(Arrays.copyOf(freq25, 40));
		v25.add(Arrays.copyOf(freq25, 40));v25.add(Arrays.copyOf(freq25, 40));
		v50.add(freq50); v50.add(Arrays.copyOf(freq50, 40));v50.add(Arrays.copyOf(freq50, 40));v50.add(Arrays.copyOf(freq50, 40));v50.add(Arrays.copyOf(freq50, 40));
		v75.add(freq75); v75.add(Arrays.copyOf(freq75, 40));v75.add(Arrays.copyOf(freq75, 40));v75.add(Arrays.copyOf(freq75, 40));v75.add(Arrays.copyOf(freq75, 40));
		v150.add(freq150); v150.add(Arrays.copyOf(freq150, 40));v150.add(Arrays.copyOf(freq150, 40));v150.add(Arrays.copyOf(freq150, 40));v150.add(Arrays.copyOf(freq150, 40));
		v300.add(freq300); v300.add(Arrays.copyOf(freq300, 40));v300.add(Arrays.copyOf(freq300, 40));v300.add(Arrays.copyOf(freq300, 40));v300.add(Arrays.copyOf(freq300, 40));
		
		int iC=0;
		if (myReader==null) return;
		BufferedWriter aWriter;
		try {
			aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out539.txt")));						
		} catch (FileNotFoundException e){
				return;
		} catch (IOException e){
			return;
		}
		String aLine=null;
		do{
			try {
				aLine=myReader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			if (aLine == null) break;
			int idx=aLine.indexOf(keyToPickUp);
			if ( idx < 0) continue;
			try {
				aWriter.write(aLine);
				aWriter.newLine();				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			String data=aLine.substring(idx+keyToPickUp.length());
			allDataSets.addElement(data);
			int[] number=getNumberSet(data);
			iC++;
			for (int i=0; i<5; i++){
				if (iC < 11){
					v300.get(i)[number[i]]++;
					v150.get(i)[number[i]]++;
					v75.get(i)[number[i]]++;
					v50.get(i)[number[i]]++;
					v25.get(i)[number[i]]++;
					v10.get(i)[number[i]]++;					
					continue;
				}
				if (iC < 26){
					v300.get(i)[number[i]]++;
					v150.get(i)[number[i]]++;
					v75.get(i)[number[i]]++;
					v50.get(i)[number[i]]++;
					v25.get(i)[number[i]]++;
					continue;
				}
				if (iC < 51){
					v300.get(i)[number[i]]++;
					v150.get(i)[number[i]]++;
					v75.get(i)[number[i]]++;
					v50.get(i)[number[i]]++;
					continue;
				}
				
				if (iC < 76){
					v300.get(i)[number[i]]++;
					v150.get(i)[number[i]]++;
					v75.get(i)[number[i]]++;
					continue;
				}
				if (iC < 151){
					v300.get(i)[number[i]]++;
					v150.get(i)[number[i]]++;
					continue;
				}
				if (iC < 301){
					v300.get(i)[number[i]]++;
					continue;
				}				
				
			}
		} while (aLine!=null);
		try {
			aWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		showData(v10, "Stat10");
		showData(v25, "Stat25");
		showData(v50, "Stat50");
		showData(v75, "Stat75");
		showData(v150, "Stat150");
		showData(v300, "Stat300");
		
		
	}
	
	public int[] getNumberSet(String aLine){
		int[] temp=new int[10];
		int i0=0;
		int i9=0;
		int ix=0;
		while (ix < aLine.length()){
			while (i0 < aLine.length() && aLine.charAt(i0)<'0') i0++;
			if (i0==aLine.length()) break;	
			i9=i0;
			while (i9<aLine.length() && aLine.charAt(i9)> ' ') i9++;
				temp[ix++]=Integer.parseInt(aLine.substring(i0, i9));
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
		ReadData aReader=ReadData.getInstance("test539.txt");
		aReader.setKeyToPickUp("大小順序");
		aReader.readData();
		Vector<String> allData=aReader.getAllData();
		for (int i=0; i<allData.size(); i++)
			System.out.println("No. "+i+"===>  "+allData.get(i));
	}

}
