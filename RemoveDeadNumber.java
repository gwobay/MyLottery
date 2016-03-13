import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class RemoveDeadNumber {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BufferedReader mReader;
		try {
			mReader=new BufferedReader(new InputStreamReader(new FileInputStream("predictionNextDay.txt")));	
			String aLine=null;
			int iC=0;
			do {
				aLine=mReader.readLine();
				if (aLine == null) break;
				String[] tmp=aLine.split(":");
				if (tmp.length<2){
					System.out.println(aLine+"!!!! wrong data missing :");
					continue;
				}
				int idx=++iC;//Integer.parseInt(tmp[0]);
				
				
			} while (aLine != null);
			mReader.close();
		} catch (FileNotFoundException e){
			
		} catch (IOException e){
			
		}
		
		
		BufferedWriter testLog=null;
		try {
			testLog=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("check23"+JackpotReader.drawDate+".txt")));	
		}catch (IOException e){}

	}

}
