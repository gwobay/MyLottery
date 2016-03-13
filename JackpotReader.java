import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;


public class JackpotReader {

	String dataFileName;
	Vector<String> dailyNumberSet;
	static Vector<String> allDataSets=new Vector<String> ();
	String keyToPickUp;
	static DecimalFormat dF=new DecimalFormat("0.00");
	static DecimalFormat dI=new DecimalFormat("00");
	static String dataCenter="C:/Users/eric/projects/datacenter/";

	static HashMap<String, String> upTodayData=new HashMap<String, String>();
	final static int testCaseCount=512;
	
	final static String voice="012345ABC DEFGHIJKL,MNOPQRSTU.VWXYZ!6789?";
	public Vector<String>  getAllData()
	{
		return allDataSets;
	}
	
	static public JackpotReader getInstance(String fileName, String whichLine){
		if (fileName==null) return null;
		JackpotReader aReader=new JackpotReader();
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

	static int threeDec=-1;
	static int[] getDataSet(String numberLine){
		int[] aData=new int[numberLine.length()/2];
		int i0=0, i9=0, iC=0;
		while (iC<5 && i9 < numberLine.length()){
			while (numberLine.charAt(i0)<'0' || numberLine.charAt(i0) > '9' ) i0++;
			i9=i0+1;
			while (i9<numberLine.length() && numberLine.charAt(i9)>='0' && numberLine.charAt(i9) <= '9' ) i9++;
			int hit=Integer.parseInt(numberLine.substring(i0, i9));
			aData[iC++]=hit;		
			i0=i9+1;	
		}
		return Arrays.copyOf(aData, iC);
	}
	static int getSame10Count(int hits, String numberLine){
		int c=0;
		int[] n=new int[hits];
		int[] counts=new int[4];
		int iRet=0;
		Arrays.fill(counts, 0);
		int i0=0, i9=0;
		try {
			while (c<hits){
				while (numberLine.charAt(i0)<'0' || numberLine.charAt(i0) > '9' ) i0++;
				i9=i0+1;
				while (i9<numberLine.length() && numberLine.charAt(i9)>='0' && numberLine.charAt(i9) <= '9' ) i9++;
				int hit=Integer.parseInt(numberLine.substring(i0, i9));
				n[c++]=hit;
				counts[hit/10]++;
				if (counts[hit/10] > iRet) iRet=counts[hit/10];
				i0=i9+1;			
			}
		} catch (IndexOutOfBoundsException e)
		{return 0;}
		
		return iRet;
	}

	static int getSameModCount(int hits, String numberLine){
		int c=0;
		int[] n=new int[hits];
		int[] counts=new int[10];
		int iRet=0;
		Arrays.fill(counts, 0);
		int i0=0, i9=0;
		try {
			while (c<hits){
				while (numberLine.charAt(i0)<'0' || numberLine.charAt(i0) > '9' ) i0++;
				i9=i0+1;
				while (i9<numberLine.length() && numberLine.charAt(i9)>='0' && numberLine.charAt(i9) <= '9' ) i9++;
				int hit=Integer.parseInt(numberLine.substring(i0, i9));
				n[c++]=hit;
				counts[hit%10]++;
				if (counts[hit%10] > iRet) iRet=counts[hit%10];
				i0=i9+1;			
			}
		} catch (IndexOutOfBoundsException e)
		{return 0;}
		
		return iRet;
	}
	
	static long combinationNK(int n, int k){
		if (k==0) return 1;
		if (k==0) return 1;
		if (k==1) return n;
		if (k==2) return (n*(n-1))/(k*(k-1));
		long ret=1;
		int iD=1;
		for (int i=n; i>n-k; i--){
			ret *=i;
			ret /= iD++;
		}
		return ret;
	}
	
	static Vector<double[]> linePDF=null;
	
	static Vector<double[]> calculateLinePDF(boolean byOrder){
		linePDF=new  Vector<double[]>();
		double total=1;
		total *= combinationNK(39, 5);
		
		for (int s=0; s<5; s++){
			double[] line1=new double[40];
			Arrays.fill(line1, 1.0/39);
			if (byOrder){
				Arrays.fill(line1, 0);
				for (int i=s+1; i<36+s; i++){
					line1[i]=combinationNK(39-i, 4-s);
					line1[i] *= combinationNK(i, s);
					line1[i]  /= total;
				}
			}
			linePDF.add(line1);
		}	
		return linePDF;	
	}
	
	static void writeLine(BufferedWriter aWriter, String text){
		try {
			aWriter.write(text);aWriter.newLine();
		} catch (IOException e){
			return;
		}
	}
	
	void showRealPDF(int range, Vector<int[]> samples, BufferedWriter aWriter){
	DecimalFormat dP=new DecimalFormat("0.00");
	String text=("------- Showing real line PDF ************");
	writeLine(aWriter, text);
		for (int s=0; s<5; s++){
			double[] bCount=new double[40];
			Arrays.fill(bCount, 0);
			for (int d=0; d<range; d++){
				bCount[samples.get(s)[d]]++;
			}
			
			text = ("Line "+(s+1)+" range "+(range)+" (");
			writeLine(aWriter, text); text ="";
			for (int i=1; i<40; i++){
				if (linePDF.get(s)[i] < 0.000001) continue;
				if (i % 6 == 0) {writeLine(aWriter, text); text="";}
				bCount[i] /= (range);
				text += ("["+i+"]"+dP.format(bCount[i]/linePDF.get(s)[i])+", ");
			}
			text += (")************");
			writeLine(aWriter, text);
		}
	}
	static int[] statisticsPeriod={24, 39, 54, 66, 78, 90, 102, 117, 156, 0}; 
	
	static void showTodayStatistics(Vector<int[]> sampledData, BufferedWriter aWriter){
		
		for (int ip=0; ip<statisticsPeriod.length; ip++){
			int[] balls=new int[40];
			Arrays.fill(balls, 0);
			for (int s=0; s<sampledData.size(); s++){
				for (int i=0; i<statisticsPeriod[ip]; i++){
					balls[sampledData.get(s)[i]]++;
				}
			}
			double mean=5.0*statisticsPeriod[ip]/39.0;
			double varian=0;
			for (int i=1; i<40; i++){
				varian += (balls[i]-mean)*(balls[i]-mean);
			}
			varian=Math.sqrt(varian/39);
			
			double meanH=mean+varian;//*1.5;
			double meanL=mean-varian;///2;
			//BufferedWriter aWriter;
			//showData(samplesStat, "Stat"+forDays+fileName);
			String strong="High freq:( ";
			String normal="norM freq:( ";
			String weak="Under freq:( ";
			String worst="Worst freq:( ";
			String allLists="all stats: ";
			//double checkCount=0;
			TreeSet<String> dataSet=new TreeSet<String>();
			for (int i=1; i<40; i++){
				//checkCount += balls[i];
				if (balls[i]>meanH) strong += (""+i+",");
				else if (balls[i]>=mean) normal += (""+i+",");				
				else if (balls[i]<meanL) worst += (""+i+",");
				else //if (stats[i]<1.281) 
					weak += (""+i+",");
				
				dataSet.add("["+dI.format(balls[i])+"]-"+i);				
				//allLists += (""+i+"["+dI.format(balls[i])+"],");
			}
			try {
			aWriter.write("\n++++++++++ Statistics for "
		              +statisticsPeriod[ip]+" days upto TODAY !!!");aWriter.newLine();
			aWriter.write(strong);aWriter.newLine();
			aWriter.write(normal+") > "+mean);aWriter.newLine();
			aWriter.write(worst);	aWriter.newLine();		
			aWriter.write(weak);aWriter.newLine();
			//aWriter.write(".....................................");aWriter.newLine();
			
			String sortList="............................SORTED :(\n";
			int i=0;
			Iterator itr=dataSet.iterator();
			while (itr.hasNext()){
				String sts=(String)itr.next();
				String[] tt=sts.split("-");
				sortList += (tt[1]+tt[0]+",");
				i++;
				if (i % 6==0)
				{
					aWriter.write(sortList);aWriter.newLine();
					sortList="";
				}
			}
			aWriter.write(sortList+")");aWriter.newLine();
			} catch (IOException e){}
		}
	}
	
	static void showLastWeekJackpotType(Vector<int[]> sampledData, BufferedWriter aWriter){
		int[] hits=new int[5];
		for (int s=0; s<sampledData.size(); s++){
			hits[s]=sampledData.get(s)[5];
		}
		for (int ip=0; ip<statisticsPeriod.length; ip++){
			int[] balls=new int[40];
			Arrays.fill(balls, 0);
			for (int s=0; s<sampledData.size(); s++){
				for (int i=6; i<6+statisticsPeriod[ip]; i++){
					balls[sampledData.get(s)[i]]++;
				}
			}
			double mean=5.0*statisticsPeriod[ip]/39.0;
			double varian=0;
			for (int i=1; i<40; i++){
				varian += (balls[i]-mean)*(balls[i]-mean);
			}
			varian=Math.sqrt(varian/39);
			
			double meanH=mean+varian;//*1.5;
			double meanL=mean-varian;///2;
			//BufferedWriter aWriter;
			//showData(samplesStat, "Stat"+forDays+fileName);
			String strong="High freq:( ";
			String normal="norM freq:( ";
			String weak="Under freq:( ";
			String worst="Worst freq:( ";
			String allLists="all stats: ";
			//double checkCount=0;
			TreeSet<String> dataSet=new TreeSet<String>();
			for (int i=1; i<40; i++){
				//checkCount += balls[i];
				if (balls[i]>meanH) strong += (""+i+",");
				else if (balls[i]>=mean) normal += (""+i+",");				
				else if (balls[i]<meanL) worst += (""+i+",");
				else //if (stats[i]<1.281) 
					weak += (""+i+",");
				
				dataSet.add("["+dI.format(balls[i])+"]-"+i);				
				//allLists += (""+i+"["+dI.format(balls[i])+"],");
			}
			String distrib="*******Distrib for one week ago draw:\n ";
			for (int s=0; s<hits.length; s++){
				int k=hits[s];
				distrib += dI.format(hits[s]);
				if (balls[hits[s]]>meanH) distrib += " stronG;";
				else if (balls[hits[s]]>=mean) distrib += " norMal; ";				
				else if (balls[hits[s]]<meanL) distrib += " Worst; ";
				else //if (stats[i]<1.281) 
					distrib += " Under; ";
			}
			try {
			aWriter.write("\n++++++++++ Statistics for "
		              +statisticsPeriod[ip]+" days upto one week ago !!!");
			aWriter.newLine();
			aWriter.write(strong);aWriter.newLine();
			aWriter.write(normal+") > "+mean);aWriter.newLine();
			aWriter.write(worst);aWriter.newLine();			
			aWriter.write(weak);aWriter.newLine();
			//aWriter.write(".....................................");aWriter.newLine();
			aWriter.write(distrib);aWriter.newLine();
			String sortList="............................SORTED :(\n";
			int i=0;
			Iterator itr=dataSet.iterator();
			while (itr.hasNext()){
				String sts=(String)itr.next();
				String[] tt=sts.split("-");
				sortList += (tt[1]+tt[0]+",");
				i++;
				if (i % 6==0)
				{
					aWriter.write(sortList+")");aWriter.newLine();
					sortList="";
				}
			}
			aWriter.write(sortList+")");aWriter.newLine();
		} catch (IOException e){}
		}
		showTodayStatistics(sampledData, aWriter);
	}
	
	
	
	static void showBiWeekJackpotType(Vector<int[]> sampledData, BufferedWriter aWriter){
		int[] hits=new int[5];
		for (int s=0; s<sampledData.size(); s++){
			hits[s]=sampledData.get(s)[11];
		}
		for (int ip=0; ip<statisticsPeriod.length; ip++){
			int[] balls=new int[40];
			Arrays.fill(balls, 0);
			for (int s=0; s<sampledData.size(); s++){
				for (int i=12; i<12+statisticsPeriod[ip]; i++){
					balls[sampledData.get(s)[i]]++;
				}
			}
			double mean=5.0*statisticsPeriod[ip]/39.0;
			double varian=0;
			for (int i=1; i<40; i++){
				varian += (balls[i]-mean)*(balls[i]-mean);
			}
			varian=Math.sqrt(varian/39);
			
			double meanH=mean+varian;//*1.5;
			double meanL=mean-varian;///2;
			//BufferedWriter aWriter;
			//showData(samplesStat, "Stat"+forDays+fileName);
			String strong="High freq:( ";
			String normal="norM freq:( ";
			String weak="Under freq:( ";
			String worst="Worst freq:( ";
			String allLists="all stats: ";
			//double checkCount=0;
			TreeSet<String> dataSet=new TreeSet<String>();
			for (int i=1; i<40; i++){
				//checkCount += balls[i];
				if (balls[i]>meanH) strong += (""+i+",");
				else if (balls[i]>=mean) normal += (""+i+",");				
				else if (balls[i]<meanL) worst += (""+i+",");
				else //if (stats[i]<1.281) 
					weak += (""+i+",");
				
				dataSet.add("["+dI.format(balls[i])+"]-"+i);				
				//allLists += (""+i+"["+dI.format(balls[i])+"],");
			}
			String distrib="******Distrib for (two week ago) draw:\n ";
			for (int s=0; s<hits.length; s++){
				int k=hits[s];
				distrib += dI.format(hits[s]);
				if (balls[hits[s]]>meanH) distrib += " stronG;";
				else if (balls[hits[s]]>=mean) distrib += " norMal; ";				
				else if (balls[hits[s]]<meanL) distrib += " Worst; ";
				else //if (stats[i]<1.281) 
					distrib += " Under; ";
			}
			try {
			aWriter.write("\n++++++++++ Statistics for "
		              +statisticsPeriod[ip]+" days upto TWO week ago !!!");
			aWriter.newLine();
			aWriter.write(strong);aWriter.newLine();
			aWriter.write(normal+") > "+mean);aWriter.newLine();
			aWriter.write(worst);aWriter.newLine();			
			aWriter.write(weak);aWriter.newLine();
			//aWriter.write(".....................................");aWriter.newLine();
			aWriter.write(distrib);aWriter.newLine();
			String sortList="............................SORTED :(\n";
			int i=0;
			Iterator itr=dataSet.iterator();
			while (itr.hasNext()){
				String sts=(String)itr.next();
				String[] tt=sts.split("-");
				sortList += (tt[1]+tt[0]+",");
				i++;
				if (i % 6==0)
				{
					aWriter.write(sortList+")");aWriter.newLine();
					sortList="";
				}
			}
			aWriter.write(sortList+")");aWriter.newLine();
		} catch (IOException e){}
		}
		showLastWeekJackpotType(sampledData, aWriter);
	}
	
	static Vector<double[]> samplesStat=new Vector<double[]>();
	static String yesterdayDrawID=null;
	static String todayDrawID=null;
	static String yesterdayDrawDate=null;
	static String drawDate="";
	static int noOfDrawYearTodate=0;
	public Vector<int[]> readData(int hits, int numberMax, int forDays){
		if (numberMax > ballMax) return null;
		Arrays.fill(balls, 0);
		Vector<int[]> samples=new Vector<int[]>();
		double[] data=new double[numberMax+1];
		double[] stats=new double[numberMax+1];
		Arrays.fill(data, 0);
		Arrays.fill(stats, 0);
		//samples.add(allList);
		//samplesStat.add(data);

		int iC=0;
		String fileName=dataFileName;
		if (fileName.indexOf("datacenter")<0){
			fileName=dataCenter+dataFileName;
		}
		int i9=dataFileName.length()-1;
		while (dataFileName.charAt(i9)!='\\' && dataFileName.charAt(i9)!='/') {
			i9--;	if (i9<0) break;
		}		
		String pureFileName=dataFileName.substring(i9+1);
		Vector<String> allData=new Vector<String>();
		
		BufferedReader mReader;
		try {
			mReader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),  "utf-8"));						
			
			
			int drawNumber=-1;
			int lastDrawNumber=-1;
			String pref="out";
			if (keyToPickUp.equalsIgnoreCase("開出順序")) pref += "drop";
			
			upTodayData.clear();
		String tripLine=null;
		int lastDupH=-1;
		String aLine=null;
		String lastLine=null;
		int[] lastSet=new int[5];
		lastSet[0]=0;
		String drawDay="";
		do{
			
				aLine=mReader.readLine();
				if (aLine==null) break;
				
				if (aLine.indexOf("開獎")>0){
					int i0=0; 
					while (aLine.charAt(i0) < '0' || aLine.charAt(i0)>'9') i0++;
					String id=aLine.substring(i0, i0+9);
					if (todayDrawID==null) todayDrawID=id;
					if (iC==1 && yesterdayDrawID==null) yesterdayDrawID=id;
					drawNumber = Integer.parseInt(id) % 1000;
					
					if (noOfDrawYearTodate <  1) {
						noOfDrawYearTodate=drawNumber;
					statisticsPeriod[statisticsPeriod.length-1]=noOfDrawYearTodate;
					}
					else
						if (lastDrawNumber > 1 && lastDrawNumber - drawNumber != 1){
							System.out.println("Draw has GAP at "+lastDrawNumber);
							System.exit(0);
						}
					lastDrawNumber=drawNumber;
				}
				int idxD=aLine.indexOf("開出順序");
				if (idxD>0){
					drawDay=aLine.substring(0, 9).replace('/', '-');
					if (drawDate.length()<3) drawDate=drawDay;
				}
			
			if (aLine == null) break;
			int idx=aLine.indexOf(keyToPickUp);
			if ( idx < 0) continue;
			
			String dup=upTodayData.put(drawDay, "1");//aLine.substring(idx));
			if (dup != null){
				System.out.println("Bad data, duplicated on "+drawDay);
				continue;
			}
			//System.out.println("Got line "+iC+" :"+aLine);
			allData.add("("+(++iC)+")"+drawDay+":"+aLine.substring(idx+keyToPickUp.length()));
		} while (aLine != null);
		
		int totalSamples=iC*5;
		upTodayData.clear();
		
		mReader.close();
		} catch (FileNotFoundException e){
			return null;
	} catch (IOException e){
		return null;
	}

		int[] allList=new int[iC];
		Arrays.fill(allList, 0);
		
		Vector<String> dateLines=new Vector<String>();
		for (int i=0; i<hits; i++)
		{
			samplesStat.add(Arrays.copyOf(data, numberMax+1));
			samples.add(Arrays.copyOf(allList, iC));
		}
		BufferedWriter aWriter;
		try {
			aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out"+forDays+pureFileName)));						
			String aLine=null;
			String lastLine=null;
			String tripLine=null;
			int lastDupH=-1;

			int[] lastSet=new int[5];
			lastSet[0]=0;
	
		System.out.println("Total "+iC+" lines of data");
		//Iterator itr=upTodayData.keySet().iterator();
		int uBnd=allData.size();//<forDays?allData.size():forDays;
		Calendar calendar0=Calendar.getInstance();
		System.out.println(calendar0.get(Calendar.DAY_OF_WEEK));
		calendar0.set(2015,8,31);
		System.out.println(calendar0.get(Calendar.DAY_OF_WEEK));
		calendar0.set(2015,9,01);
		System.out.println(calendar0.get(Calendar.DAY_OF_WEEK));
		int[] freqAfter30=new int[40];
		Arrays.fill(freqAfter30, 0);
		int i30Cases=0;
		int[] freqAfter3022=new int[40];
		Arrays.fill(freqAfter3022, 0);	
		int[] freqAfter3022Loc=new int[250];
		Arrays.fill(freqAfter3022Loc, 0);		
		int iFreqAfter3022Loc=0;
		boolean has30=false;
		Vector<String> tripleList=new Vector<String>();
		for (int id=0; id<uBnd; id++){
			
			aLine=allData.get(id);
			int idx=aLine.indexOf(":");
			if (idx < 0) break;
			String drawD=aLine.substring(idx-9, idx);
			String dataLine=aLine.substring(idx);
			//if (dataLine==null) break;
			//++iC;
				String cal=""+(Integer.parseInt(drawD.substring(0, 3))+1911)+drawD.substring(3);
				Calendar calendar=//Calendar.getInstance();
				new GregorianCalendar(
				//calendar.set(
						Integer.parseInt(drawD.substring(0, 3))+1911,
						Integer.parseInt(drawD.substring(4, 6))-1,
						Integer.parseInt(drawD.substring(7, 9))-1);
				//calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); 
				calendar.setFirstDayOfWeek(GregorianCalendar.SUNDAY);
				int wkDay=(calendar.get(Calendar.DAY_OF_WEEK));//+4) % 7;
				aWriter.write("("+iC+")"+cal+"["+wkDay+"]"+dataLine);
				//String dataLine=aLine;//aLine.substring(idx+keyToPickUp.length());
				int[] dataSet=getDataSet(dataLine);
				
				int[] countH=new int[4];
				int[] countT=new int[10];
				int[] countQm=new int[10];
				int[] countQr=new int[4];
				Arrays.fill(countH, 0);
				Arrays.fill(countT, 0);
				Arrays.fill(countQm, 0);
				Arrays.fill(countQr, 0);
				int dupH=-1,dupT=0, cH=0, cT=0, dupQm=0, dupQr=0, cQm=0, cQr=0;
				for (int i=0; i<dataSet.length; i++){
					countH[dataSet[i]/10]++;
					if (countH[dataSet[i]/10] > 2){
						dupH=dataSet[i]/10;
						cH=countH[dupH];						
					}
					countT[dataSet[i] % 10]++;
					if (countT[dataSet[i] % 10] > 2){
						dupT=dataSet[i] % 10;
						cT=countT[dupT];						
					}
					countQm[dataSet[i]/4]++;
					if (countQm[dataSet[i]/4] > 2){
						dupQm=dataSet[i]/4;
						cQm=countQm[dupQm];						
					}
					countQr[dataSet[i] % 4]++;
					if (countQr[dataSet[i] % 4] > 2){
						dupQr=dataSet[i] % 4;
						cQr=countQr[dupQr];						
					}
				}
				String toSave=dataLine;
				if (cT > 2){
					aWriter.write(" Tail-"+dupT+"["+cT+"];");
					toSave += " Tail-"+dupT+"["+cT+"];";
				} else {
					aWriter.write("..........");
					toSave += ("..........");
				}
				
				if (cH > 2){
					aWriter.write(" Head-"+dupH+"<"+cH+">");
					toSave += (" Head-"+dupH+"<"+cH+">");
					if (dupH==1 && id < noOfDrawYearTodate && lastLine!= null){
						i30Cases++;
						int ico=lastLine.indexOf(":");
						int[] nextDraws=getDataSet(lastLine.substring(ico+1));
						for (int f3=0; f3<5; f3++)
						{
							freqAfter30[nextDraws[f3]]++;
						}
						if (cH==3 && countH[3]==2){
							freqAfter3022Loc[iFreqAfter3022Loc]=id;
							iFreqAfter3022Loc++;
							for (int f3=0; f3<5; f3++)
							{
								freqAfter3022[nextDraws[f3]]++;
							}	
						}
						String sTripLine=("Trip "+dupH+"<"+aLine+"> has "+lastLine);
						
						tripleList.add(sTripLine);
					}
					//if (dupH==0)
					{
						//System.out.println("Trip "+dupH+"<"+aLine+"> has "+lastLine);
						//if (lastLine!=null)
						//System.out.println("\tnext "+lastLine);
						//if (tripLine != null)
						//System.out.println("Trip was** "+tripLine+"<"+lastDupH+">");
						//System.out.println("\t\tlast 3H "+tripLine+"<"+lastDupH+">");
					}
					tripLine=aLine;//dataLine;
					lastDupH=dupH;
				} else {
					aWriter.write("..........");
					toSave += ("..........");
				}
				if (lastSet[0] != 0){
					int iRep=0;
					for (int kx=0; kx<5; kx++){
						if (dataSet[kx]==lastSet[0] || dataSet[kx]==lastSet[1] ||dataSet[kx]==lastSet[2] ||
								dataSet[kx]==lastSet[3] ||dataSet[kx]==lastSet[4] )
								iRep++;
					}
					aWriter.write("Repeat-<"+iRep+">");
					toSave += ("Repeat-<"+iRep+">");
				}
				lastSet=Arrays.copyOf(dataSet, 5);
				/*
				if (cQm > 2){
					aWriter.write(" Qxxx-"+dupQm+"<"+cQm+">");
					toSave += (" Qxxx-"+dupQm+"<"+cQm+">");
				} else {
					aWriter.write("..........");
					toSave += ("..........");
				}
				
				if (cQr > 2){
					aWriter.write(" Qmod-"+dupQr+"<"+cQr+">");
					toSave += (" Qmod-"+dupQr+"<"+cQr+">");
				} else {
					aWriter.write("..........");
					toSave += ("..........");
				}
				*/
				aWriter.newLine();
				dateLines.add(cal+"["+wkDay+"]"+toSave);
				lastLine=aLine;
			//String data=aLine.substring(idx+keyToPickUp.length());
			//allDataSets.add(data);
			int[] number=dataSet;//getNumberSet(data);
			
			for (int i=0; i<hits; i++){				
				samplesStat.get(i)[number[i]]++;
				samples.get(i)[id]=number[i];
				stats[number[i]]++;
			}
			//if (++iC >= forDays) break;	
			iC--;
		}
		for (int i3=0; i3<tripleList.size(); i3++){
			aWriter.write(tripleList.get(i3)); aWriter.newLine();
		}
		aWriter.write("data after 3-0:");
		for (int iT=0; iT<10; iT++){
			int iCont=0;
			for (int iH=0; iH<4; iH++){
				int idx=iT+iH*10;
				if (idx<1) continue;
				aWriter.write(""+idx+"["+freqAfter30[idx]+"],");
				iCont += freqAfter30[idx];
			}
			aWriter.write(") total ---------"+iCont);
			aWriter.newLine();
		}
		aWriter.write(") total "+i30Cases+" cases");
		aWriter.newLine();
		aWriter.write("data after 3-0/22 happened at :");
		for (int i=0; i<iFreqAfter3022Loc; i++){
			
			//aWriter.write(""+freqAfter3022Loc[i]+", ");
		}
		aWriter.newLine();
		for (int i=1; i<40; i++){
			if (freqAfter3022[i]<1) continue;
			aWriter.write(""+i+"["+freqAfter3022[i]+"],");
		}
		aWriter.newLine();
			aWriter.close();
			
		} catch (FileNotFoundException e){
			return null;
	} catch (IOException e){
		return null;
	}

		boolean byOrder=false;
		if (!keyToPickUp.equalsIgnoreCase("開出順序"))	byOrder=true;		
		calculateLinePDF(byOrder);
		
		try {
			String pref="ascend";			
			aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out"+drawDate+pureFileName)));
			for (iC=dateLines.size();iC>0; iC--){
				String aLine=dateLines.get(iC-1);
				aWriter.write("("+iC+")"+aLine);aWriter.newLine();
			}
			showRealPDF(forDays, samples, aWriter);
			showBiWeekJackpotType(samples, aWriter);
			
			aWriter.close();
		} catch (FileNotFoundException e){
				return null;
		} catch (IOException e){
			return null;
		}
		
		
		return samples;
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
	
	static double[] sinc40PI=new double[60];
	static double[] sincHalfPI=new double[60];
	
static void getSincHalfPiData(){
		
		boolean noFile=false;
		String fileName=dataCenter+"sincHalfPiTable.txt";//"C:\\Users\\eric\\workspace\\GetLottery\\sincHalfPiTable.txt"
		//this file has the table list the integral from 0-pi, pi-2pi, 2pi-3pi .....
		Arrays.fill(sincHalfPI, 0);
		double checkValue=0;
		double last1=0;
		BufferedReader mReader;
		try {
			mReader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));	
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
				if (idx >-1 && idx < 60){
					//if (idx % 2 == 0)
				double sinc=Double.parseDouble(tmp[1]);
					
				sincHalfPI[idx]=sinc - last1;	
					last1 = sinc;
				}
			} while (aLine != null);
			mReader.close();
		} catch (FileNotFoundException e){
			noFile=true;
		} catch (IOException e){
			if (sincHalfPI[1]==0)
			noFile=true;
		}
		System.out.println("PI/2="+Math.PI/2+"Sum of Sinc is "+checkValue);
		if (sincHalfPI[1]!=0) return;
		
		System.out.println("Missing table: sincHalfPiTable.txt");
	}

	static void getSincData(){
		
		BufferedWriter aWriter;
		boolean noFile=false;
		String fileName=dataCenter+"sincTable.txt";//"C:\\Users\\eric\\workspace\\GetLottery\\sincTable.txt";
		//this file has the table list the integral from 0-pi, pi-2pi, 2pi-3pi .....
		System.out.println("Reading sinc data from "+fileName);
		Arrays.fill(sinc40PI, 0);
		double checkValue=0;
		double last1=0;
		BufferedReader mReader;
		try {
			mReader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));	
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
				if (idx >-1 && idx < 60){
					//if (idx % 2 == 0)
				double sinc=Double.parseDouble(tmp[1]);
					
						sinc40PI[idx]=sinc - last1;	
					last1 = sinc;
				}
			} while (aLine != null);
			mReader.close();
			System.out.println("Total "+iC+" lines");
		} catch (FileNotFoundException e){
			noFile=true;
		} catch (IOException e){
			if (sinc40PI[1]==0)
			noFile=true;
		}
		System.out.println("PI ="+Math.PI+"Sum of Sinc is "+checkValue);
		if (sinc40PI[1]!=0) {
			getSincHalfPiData();
			return;
		}
		
		System.out.println("Missing table: sincTable.txt");
	}

	public static class Complex{
		double re, im;
		Complex(double x, double y){
			re=x; im=y;
		}
		
		Complex(Complex c){
			re=c.re;
			im=c.im;
		}
		public Complex plus(Complex b){
			return new Complex(this.re + b.re,  this.im + b.im);
		}
		public Complex minus(Complex b){
			return new Complex(this.re - b.re,  this.im - b.im);
		}
		public Complex multiply(Complex b){
			return new Complex(this.re*b.re - this.im*b.im, this.re*b.im+b.re*this.im);
		}
	
	
	public static Complex[] array(double[] re){
		Complex[] retV=new Complex[re.length];
		for (int i=0; i<re.length; i++){
			retV[i]=new Complex(re[i], 0);
		}
		return retV;
	}
	
	public static Complex[] array(double[] re, double[] im){
		Complex[] retV=new Complex[re.length];
		for (int i=0; i<re.length; i++){
			retV[i]=new Complex(re[i], im[i]);
		}
		return retV;
	}
	
	public static Complex multiply(Complex a, Complex b){
		return new Complex(a.re*b.re - a.im*b.im, a.re*b.im+b.re*a.im);
	}
	}
	Complex[] recursiveFFT_DIT2(Complex[] sample, int sign){
		if (sample.length==1) return sample;
		double[] im=new double[sample.length];
		Arrays.fill(im, 0);
		Complex[] retV=new Complex[sample.length];
		//Arrays.fill(retV, 0);
		
		int half=sample.length/2;
		
		Complex[] even=new Complex[half];
		Complex[] odd=new Complex[half];
		for (int i=0; i<half; i++){
			even[i]=sample[2*i];
			odd[i]=sample[2*i+1];
		}
		
		Complex[] tmpE=recursiveFFT_DIT2(even, sign);
		Complex[] tmpO=recursiveFFT_DIT2(odd, sign);
		
		for (int k=0; k<half; k++){
			double shiftBy=0.5*sign*Math.PI*2*k/half;
			tmpO[k]= tmpO[k].multiply(new Complex(Math.cos(shiftBy), Math.sin(shiftBy)));
			retV[k]=tmpE[k].plus(tmpO[k]);
			retV[k+half]=tmpE[k].minus(tmpO[k]);
		}
	/*
	 * procedure rec_fft_dit2(a[], n, x[], is)
2 // complex a[0..n-1] input
3 // complex x[0..n-1] result
4 {
5 complex b[0..n/2-1], c[0..n/2-1] // workspace
6 complex s[0..n/2-1], t[0..n/2-1] // workspace
7
8if n== 1 then // end of recursion
9 {
10 x[0] := a[0]
11 return
12 }
13
14 nh := n/2
15
16 for k:=0 to nh-1 // copy to workspace
17 {
18 s[k] := a[2*k] // even indexed elements
19 t[k] := a[2*k+1] // odd indexed elements
20 }
21
22 // recursion: call two half-length FFTs:
23 rec_fft_dit2(s[], nh, b[], is)
24 rec_fft_dit2(t[], nh, c[], is)
25
26 fourier_shift(c[], nh, v=is*1/2) defined as following
	for k:=0 to nh-1
4 {
5	 c[k] := c[k] * exp(v*2.0*PI*I*k/nh)
6 }
27
28 for k:=0 to nh-1 // copy back from workspace
29 {
30 x[k] := b[k] + c[k]
31 x[k+nh] := b[k] - c[k]
32 }
33 }
*/
	 return retV;
	}
	
	static boolean inversePopulation=false;
	static Vector<double[]> pdfOnPoints=new Vector<double[]>(); //for point between 3-6
	
	public static void projByAvgDiff1Line(int lineNo, int[] samples, BufferedWriter aWriter){
		pdfOnPoints.clear();
		int iEnsamble=6;
		int iLen=samples.length;
		double[] rSamples=new double[iLen];
		double[] weighted=new double[iLen];
		//find the pdf with points from 3 to 6
		//use pdf to get the radian in disc for 
		int sampleCount=120;
		double weight=1;
		double[] adjValue=new double[sampleCount];
		
		for (int points=3; points <=6; points ++){
			double[] distanceInRadian=new double[sampleCount];
			int testCase=60;
			while (testCase > 0){
				int[] numberNext=new int[40];
				Arrays.fill(numberNext, 0);
				int[] testData=Arrays.copyOfRange(samples, testCase, testCase+sampleCount+points+1);
				int draw=samples[testCase-1];
				//set up pdf for pattern match
				for (int idx=1; idx<sampleCount; idx++){
					int iFit=0;
					for (int ik=0; ik<points; ik++){
						if (testData[idx+ik] != testData[ik]) break;
						iFit++;
					}
					if (iFit==points){
						numberNext[testData[idx-1]]++;
					}
				}
				double radiant=0;
				for (int idx=1; idx<40; idx++){
					radiant += 10;
					radiant += weight*numberNext[idx];					
				}
				if (draw > testData[0]){
					for (int idx=testData[0]; idx<draw; idx++){
						radiant += 10;
						radiant += weight*numberNext[idx];
					}
				} else
				{
					for (int idx=testData[0]; idx<40; idx++){
						radiant += 10;
						radiant += weight*numberNext[idx];
					}
					for (int idx=1; idx<draw; idx++){
						radiant += 10;
						radiant += weight*numberNext[idx];
					}
				}
				distanceInRadian[testCase]=radiant;
				testCase--;
			}
			pdfOnPoints.add(distanceInRadian);
		}
		
	}
	
	public static void populateCircle(int lineNo, int[] samples, BufferedWriter aWriter){
		int[] ballPdf=new int[40];
		int[] lastLoc=new int[40];
		Arrays.fill(ballPdf, 0);
		Arrays.fill(lastLoc, 0);
		int maxF=0;
		int minDist=39;
		for (int i=0; i<samples.length; i++){
			ballPdf[samples[i]]++;
			if (ballPdf[samples[i]] > maxF) maxF=ballPdf[samples[i]];
			if (lastLoc[samples[i]]!=0 ) {
				if (minDist > i - lastLoc[samples[i]]) minDist = i - lastLoc[samples[i]];				
			}
			lastLoc[samples[i]]=i;
		}
		int[] allBalls=new int[39*maxF];
		Arrays.fill(allBalls, 0);
		double thetaW=360;
		thetaW /= maxF;
		double[] ballSize=new double[40];
		double minSize=thetaW;
		for (int i=1; i<40; i++){
			ballSize[i]=(100+ballPdf[i]);
			ballSize[i] /= (3900+samples.length);
			ballSize[i] *= thetaW;
			if (ballSize[i]<minSize) minSize=ballSize[i];
		}
		double thetaD=(thetaW/minDist+minSize/2);
		
		//double theta1=(thetaW/(39+))
		int[][] ballToUse=new int[maxF][40];
		for (int i=0; i<maxF; i++){
			Arrays.fill(ballToUse[i], 1);
		}
		
		double lastRadiant=0;
		double currentRadiant=0;
		int lastSet=0;
		int iLocation=0;// index for allBalls
		int iCurrent=samples.length-1;
		ballToUse[0][samples[iCurrent]]=0;
		allBalls[0]=samples[iCurrent];
		while (iCurrent > 0){
			iCurrent--;
			currentRadiant += thetaD;
			int iSet=(int)(currentRadiant/thetaW);
			double thetaLeft=currentRadiant - iSet*thetaW;
			iSet %= maxF;
			
			if (ballToUse[iSet][samples[iCurrent]]==0){
				System.out.println("BALL WAS USED");
			}
			ballToUse[iSet][samples[iCurrent]]=0;
			
			int iToInsert=iSet*39-1;
			if (iSet == lastSet){
				thetaLeft=thetaD;
				iToInsert=iLocation;
			}
			lastSet=iSet;
			while (thetaLeft>0){
				iToInsert++;
				double occupiedRadiant=minSize;
				if (allBalls[iToInsert] != 0){
					occupiedRadiant=ballSize[allBalls[iToInsert]];
				}
				thetaLeft -= occupiedRadiant;
			}
			allBalls[iToInsert]=samples[iCurrent];
		}
		//start to predict
		currentRadiant += thetaD;
		int iSet=(int)(currentRadiant/thetaW);
		double thetaLeft=currentRadiant - iSet*thetaW;
		iSet %= maxF;
		

		
		int iToInsert=iSet*39-1;
		if (iSet == lastSet){
			thetaLeft=thetaD;
			iToInsert=iLocation;
		}
		lastSet=iSet;
		while (thetaLeft>0){
			iToInsert++;
			double occupiedRadiant=minSize;
			if (allBalls[iToInsert] != 0){
				occupiedRadiant=ballSize[allBalls[iToInsert]];
			}
			thetaLeft -= occupiedRadiant;
		}
		if (allBalls[iToInsert] > 0 )
			{
					//use allBalls[iToInsert] as prediction data;//samples[iCurrent];
			}
		else
		{
			//list all unUsed balls in set iSet with pdf for prediction
		}		
		
	}
	
	public static void predictOnCircle(int forDay, Vector<int[]> samples, int[] hits, BufferedWriter aWriter){
		int iEnsamble=6;
		Vector<double[]> rSamples=new Vector<double[]>();
		double[] nextProj=new double[samples.size()];
		double[][] prjDiffs=new double[samples.size()][6];
		for (int k=0; k<samples.size(); k++){
			//aWriter.write("/////////////// LINE  "+(k+1)+"//////////////////////");
			//aWriter.newLine();
			double[] rSample1=new double[samples.get(k).length];
			for (int i=0; i<samples.get(k).length; i++){
				rSample1[samples.get(k).length-1-i]=samples.get(k)[i];
			}
			int dataLen=samples.get(k).length;
			double prjDiff=0;
			int iniCF=6;
			for (int cf1=iniCF; cf1<iniCF+iEnsamble; cf1++){
				int cf=cf1-iniCF+1;
				//aWriter.write("+++++++++ check "+(40*cf)+"+++++++++++");aWriter.newLine();
				int[][] iFreq=new int[40][40];
				for (int j=0; j<40; j++){
					Arrays.fill(iFreq[j], 0);
				}
				double avgDiff=(rSample1[dataLen-1] - rSample1[dataLen-cf*40-1])/(cf*40);
				double absDiff=0;
				for (int ix=dataLen-cf*40-1; ix<dataLen-1; ix++){
					iFreq[(int)rSample1[ix]][(int)rSample1[ix+1]]++;
					double diff = Math.abs(rSample1[ix+1] - rSample1[ix]);
					if (diff < 0) diff += 39;
					absDiff += diff;
				}
				absDiff /= (cf*40); 
				for (int j=0; j<40; j++){
					//aWriter.write(""+j+":");
					for (int jx=0; jx<40; jx++){
						if (iFreq[j][jx]<1) continue;
						//aWriter.write(""+jx+"["+iFreq[j][jx]+"]");
					}
					//aWriter.write(")..................");aWriter.newLine();
				}
				//aWriter.write(")........ agv Diff="+dF.format(avgDiff)+"; absDiff="+dF.format(absDiff)+" ..........");aWriter.newLine();
				//aWriter.write("===============================================");aWriter.newLine();
				prjDiffs[k][cf-1]=absDiff;
				prjDiff += absDiff;
			}
			prjDiff /= iEnsamble;
			//aWriter.write(")........ last Draw="+samples.get(k)[0]+"; !!!!! PROJECTING DIFF="+dF.format(prjDiff)+" ..........");
			//aWriter.newLine();
			//aWriter.write(")........  ..........");
			//aWriter.newLine();
			nextProj[k]=prjDiff;
	
		}
		//aWriter.write("===============================================");aWriter.newLine();
		try {
			int found=0;
			int[] predicts=new int[5];
		for (int k=0; k<samples.size(); k++){
			
			for (int cf=6; cf<6+6; cf++){
				aWriter.write("("+cf+")::"+dF.format(samples.get(k)[0]+prjDiffs[k][cf-6])+";");					
			}
			aWriter.newLine();
			aWriter.write("->->->->->->->Line "+(k+1)+" .. real Draw="+hits[k]+"  ");
			predicts[k]=(int)Math.round(samples.get(k)[0]+nextProj[k]);
			if (predicts[k] > 39) predicts[k] -= 39;
			aWriter.write("proj=>"+predicts[k]+" ..........");
			//aWriter.newLine();
			
			//if (prj==hits[k]) found++;
						
			aWriter.newLine();
		}
		aWriter.write("test "+forDay+" found : <<<");
		for (int i=0; i<5; i++){
			for (int k=0; k<5; k++){
				if (hits[i] != predicts[k]) continue;
				found++;
				aWriter.write(""+hits[i]+", ");
			}
		}
		testResults[found]++;
			aWriter.write(">>>"+found+"============");

			aWriter.newLine();
			aWriter.newLine();	
		} catch (IOException e){
			System.out.println("Bad I/O");
		}
	
	}
	
	public static void getDataFreqStatistics(int[] ballCounts, BufferedWriter aWriter){
		//int[] checkDays={12,18,24};//{6, 12, 24, 36, 54, 72, 90, 120};
		//for (int k=0; k<checkDays.length; k++)
		//{
			
			int maxC=0;
			int minC=1000;
			for (int j=1; j<ballCounts.length; j++){
				
					//ballCounts[samples[j]]++;
					if (maxC<ballCounts[j]) maxC=ballCounts[j];
					else if (minC>ballCounts[j]) minC=ballCounts[j];
				
			}
			int[] countDistrib=new int[maxC-minC+1];
			for (int j=1; j<ballCounts.length; j++){
				countDistrib[ballCounts[j]-minC]++;
			}
			double mean=0;
			int bCount=0;
			int countMax=0;
			
			int ttShow=0;//5*checkDays[k];
			mean=0;
			for (int iv=0; iv<countDistrib.length; iv++){
				if (countDistrib[iv]<1) continue;				
				mean += (iv+minC)*countDistrib[iv];
				ttShow += countDistrib[iv];
			}
			mean /= ttShow;//countMax;//bCount;//39;
			double var1=0;
			
			for (int iv=0; iv<countDistrib.length; iv++){
				var1 += (iv-mean+minC)*(iv-mean+minC)*countDistrib[iv];//(countDistrib[iv]-mean)*(countDistrib[iv]-mean);
			}
			var1 /= ttShow;
			
			double vr=Math.sqrt(var1);
			double rangeL=mean - vr;
			double rangeU=mean + vr;
			int meanPlus=(int)mean + 1;
			int meanMinus=meanPlus - 2;
			
			String hitList="ball drawn list (";
			String predList="ball pred list :(";
			String predList2="ball !!!!!-1+1 list :(";
			for (int iv=0; iv<ballCounts.length; iv++){
				if (ballCounts[iv]>0) {
					hitList += ""+iv+"["+ballCounts[iv]+"], ";
					if (ballCounts[iv]>rangeL && ballCounts[iv]<rangeU)
						predList += ""+iv+"["+ballCounts[iv]+"], ";
					if (ballCounts[iv]>=meanMinus && ballCounts[iv]<=meanPlus)
						predList2 += ""+iv+"["+ballCounts[iv]+"], ";
				}
			}
						
			try {
				aWriter.write(">> mean="+mean+" var="+vr+" rangeB="+rangeL+" rangeU="+rangeU); 
				aWriter.newLine();
				aWriter.write(hitList);aWriter.newLine();
				aWriter.write(predList+")");aWriter.newLine();
				aWriter.write(predList2+")!!!!!");aWriter.newLine();
			} catch (IOException e){
				System.out.println("Bad I/O");
			}
	}
	
	static double[] drawnAverage=new double[512];
	static int lastStrongWeak=0; //1 strong -1 weak
	static double[] meanDiff=null;
	static double[] meanRatio=null;
	static double[] drawnMean=null;
	public static void getAvgPdf(int forDay, Vector<int[]> samples, int[] hits, BufferedWriter aWriter){
		int[] checkDays={18,24,36};//{6, 12, 24, 36, 54, 72, 90, 120};
		for (int k=0; k<checkDays.length; k++)
		{
			int[] ballDistrib=new int[40];
			int[] ballCounts=new int[40];
			Arrays.fill(ballCounts, 0);	
			int maxC=0;
			for (int j=0; j<checkDays[k]; j++){
				for (int s=0; s<5; s++){
					ballCounts[samples.get(s)[j]]++;
					if (maxC<ballCounts[samples.get(s)[j]]) maxC=ballCounts[samples.get(s)[j]];
				}
			}		
			double mean=5*checkDays[k];
			int bCount=0;
			int countMax=0;
			for (int iv=0; iv<40; iv++){
				if (ballCounts[iv]<1) continue;
				bCount++;
				if (ballCounts[iv] > countMax) countMax=ballCounts[iv];
			}
			int[] countDistrib=new int[countMax+1];
			Arrays.fill(countDistrib, 0);
			int ttShow=0;//5*checkDays[k];
			mean=0;
			for (int iv=0; iv<40; iv++){
				if (ballCounts[iv]<1) continue;
				countDistrib[ballCounts[iv]]++;
				//mean += ballCounts[iv];
			}
			for (int iv=0; iv<countMax+1; iv++){
				mean += iv*countDistrib[iv];
				ttShow += countDistrib[iv];
			}
			mean /= ttShow;//countMax;//bCount;//39;
			double var1=0;
			//for (int i=1; i<40; i++){
				//if (ballCounts[i] >0)
				//var1 += (ballCounts[i]-mean)*(ballCounts[i]-mean);
				////var1 /= 39;
			//}
			for (int iv=1; iv<countMax+1; iv++){
				var1 += (iv-mean)*(iv-mean)*countDistrib[iv];//(countDistrib[iv]-mean)*(countDistrib[iv]-mean);
			}
			var1 /= ttShow;
			
			double pointAvg=0;
			for (int i=0; i<5; i++){
				pointAvg += ballCounts[hits[i]];
			}
			pointAvg /= 5;
			double vr=Math.sqrt(var1);
			double rangeL=mean - vr-lastStrongWeak;
			double rangeU=mean + vr-lastStrongWeak;
			
			double meanPlus=mean + 1 - lastStrongWeak;
			double meanMinus=meanPlus - 2 ;
			
			String hitList="ball distrib list (";
			String predList="ball pred list :(";
			String predList2="ball !!!!!-1+1 list :(";
			for (int iv=0; iv<40; iv++){
				//if (ballCounts[iv]>0) 
				{
					hitList += ""+iv+"["+ballCounts[iv]+"], ";
					if (ballCounts[iv]>rangeL && ballCounts[iv]<rangeU)
						predList += ""+iv+"["+ballCounts[iv]+"], ";
					if (ballCounts[iv]>=meanMinus && ballCounts[iv]<=meanPlus)
						predList2 += ""+iv+"["+ballCounts[iv]+"], ";
				}
			}
			String sHitAvg=") avg=";
			hitList += " hits:(";
			double hitAg=0;
			for (int ih=0; ih<5; ih++){
				hitList += ""+hits[ih]+"["+ballCounts[hits[ih]]+"], ";
				hitAg += ballCounts[hits[ih]];
			}
			hitAg /= 5;
			sHitAvg += ""+(hitAg);
			if (k == checkDays.length-1){
				if (hitAg > mean) lastStrongWeak=1;
				else lastStrongWeak=-1;
				drawnMean[forDay]=hitAg;
				meanDiff[forDay]=hitAg - mean;
				meanRatio[forDay]=hitAg / mean  ;
			}
			try {
				aWriter.write("<<"+checkDays[k]+" days>> mean="+mean+" var="+vr+" rangeB="+rangeL+" rangeU="+rangeU); 
				aWriter.newLine();
				aWriter.write(hitList+sHitAvg);aWriter.newLine();
				aWriter.write(predList+")");aWriter.newLine();
				aWriter.write(predList2+")!!!!!");aWriter.newLine();
			} catch (IOException e){
				System.out.println("Bad I/O");
			}
	}
		
		
		
	}
	
	public static void projByDiffStatistics(int forDay, Vector<int[]> samples, int[] hits, int confidentLevel, BufferedWriter aWriter, int[][] localPerformanceList){
		int dataLength=samples.get(0).length;
		int[] allProjects=new int[15];
		int projCount=0;
		int iEnsamble=6;
		Vector<double[]> rSamples=new Vector<double[]>();
		double[] nextProj=new double[samples.size()];
		double[][] prjDiffs=new double[samples.size()][6];
		for (int k=0; k<samples.size(); k++){
			//aWriter.write("/////////////// LINE  "+(k+1)+"//////////////////////");
			//aWriter.newLine();
			//no need to reverse already ascending
			double[] rSample1=new double[samples.get(k).length];
			double[] tsDiff=new double[samples.get(k).length];
			Arrays.fill(rSample1, 0);
			Arrays.fill(tsDiff, 0);
			double diffMean=0;
			double diffVar=0;
			double minDiff=10000;//rSample1[i] - rSample1[i-1];;
			double maxDiff=-1000;//rSample1[i] - rSample1[i-1];
			rSample1[0]=samples.get(k)[0];
			int diffCount=0;
			for (int i=1; i<samples.get(k).length; i++){
				rSample1[i]=samples.get(k)[i];
				tsDiff[i]=rSample1[i] - rSample1[i-1];
				diffCount++;
				if (tsDiff[i] > maxDiff) maxDiff=tsDiff[i];
				
					if (tsDiff[i] < minDiff) minDiff=tsDiff[i];
			}
			int lowBnd=(int)Math.floor(minDiff);
			int upBnd=(int)Math.ceil(maxDiff);
			double[] diffDistribution=new double[upBnd-lowBnd+1];
			
			for (int i=1; i<tsDiff.length; i++){
				if (tsDiff[i] - lowBnd < 0){
					System.out.println("Bad index");
				}
				diffDistribution[(int)(tsDiff[i] - lowBnd)]++;
			}
			double mean=0;
			for (int i=1; i<diffDistribution.length; i++){
				diffDistribution[i] /= diffCount;
				mean += i*diffDistribution[i];
			}
			
			double var=0;
			for (int i=1; i<diffDistribution.length; i++){
				var += (i-mean)*(i-mean)*diffDistribution[i];
			}
			diffMean=mean;
			diffVar=Math.sqrt(var);
			diffVar *= (1-confidentLevel/100);
			diffMean -= minDiff;
			int dataLoc=samples.get(k).length-1;
			allProjects[projCount++]=(int)(samples.get(k)[dataLoc]+diffMean+diffVar);
			allProjects[projCount++]=(int)(samples.get(k)[dataLoc]+diffMean);
			allProjects[projCount++]=(int)(samples.get(k)[dataLoc]+diffMean-diffVar);		
		}
		try {
			int found=0;
			
			String preds="Predics:(";
			for (int i=0; i<projCount ;i++){
				while (allProjects[i]<0) allProjects[i] += 39;
				allProjects[i] %= 39;
				if (allProjects[i]==0) allProjects[i]=39;
				preds += ""+allProjects[i]+", ";
			}
			
			String sDrawn="Drawn:(";
			String sHit="Hit:(";
		
			for (int i=0; i<5; i++){
				sDrawn += ""+hits[i]+", ";
				for (int k=0; k<projCount; k++){
					if (hits[i] != allProjects[k]) continue;
					found++;
					sHit += ""+hits[i]+", ";
					break;
				}
			}
		testResults[found]++;
		lastResults[found]=forDay;
		if (forDay>0)
			localPerformanceList[found][6*39-forDay]=1;
		aWriter.write("============("+forDay+")=======================");
		aWriter.newLine();
		aWriter.write(">>>"+preds+"++++++++++++++++++++++++");
		aWriter.newLine();
		aWriter.write(">>>"+sDrawn+"<<<<<.................");
		aWriter.newLine();
		aWriter.write(">>>"+sHit+" !!!!!->"+found);
		aWriter.newLine();
		
			aWriter.write(">>>zzzzzzzzzzzzzzzzzz============");
			aWriter.newLine();	
		} catch (IOException e){
			System.out.println("Bad I/O");
		}
	
	}
	
	
	public static void projByAvgDiff(int forDay, Vector<int[]> samples, int[] hits, BufferedWriter aWriter){
		int dataLength=samples.get(0).length;
		int iEnsamble=6;
		Vector<double[]> rSamples=new Vector<double[]>();
		double[] nextProj=new double[samples.size()];
		double[][] prjDiffs=new double[samples.size()][6];
		for (int k=0; k<samples.size(); k++){
			//aWriter.write("/////////////// LINE  "+(k+1)+"//////////////////////");
			//aWriter.newLine();
			//no need to reverse already ascending
			double[] rSample1=new double[samples.get(k).length];
			for (int i=0; i<samples.get(k).length; i++){
				rSample1[i]=samples.get(k)[i];
			}
			
			int dataLen=samples.get(k).length;
			double prjDiff=0;
			int iniCF=6;
			for (int cf1=iniCF; cf1<iniCF+iEnsamble; cf1++){
				int cf=cf1-iniCF+1;
				//aWriter.write("+++++++++ check "+(40*cf)+"+++++++++++");aWriter.newLine();
				int[][] iFreq=new int[40][40];
				for (int j=0; j<40; j++){
					Arrays.fill(iFreq[j], 0);
				}
				double avgDiff=(rSample1[dataLen-1] - rSample1[dataLen-cf*40-1])/(cf*40);
				double absDiff=0;
				for (int ix=dataLen-cf*40-1; ix<dataLen-1; ix++){
					iFreq[(int)rSample1[ix]][(int)rSample1[ix+1]]++;
					double diff = Math.abs(rSample1[ix+1] - rSample1[ix]);
					if (diff < 0) diff += 39;
					absDiff += diff;
				}
				absDiff /= (cf*40); 
				//for (int j=0; j<40; j++){
					//aWriter.write(""+j+":");
					//for (int jx=0; jx<40; jx++){
						//if (iFreq[j][jx]<1) continue;
						//aWriter.write(""+jx+"["+iFreq[j][jx]+"]");
					//}
					//aWriter.write(")..................");aWriter.newLine();
				//}
				//aWriter.write(")........ agv Diff="+dF.format(avgDiff)+"; absDiff="+dF.format(absDiff)+" ..........");aWriter.newLine();
				//aWriter.write("===============================================");aWriter.newLine();
				prjDiffs[k][cf-1]=absDiff;
				prjDiff += absDiff;
			}
			prjDiff /= iEnsamble;
			//aWriter.write(")........ last Draw="+samples.get(k)[0]+"; !!!!! PROJECTING DIFF="+dF.format(prjDiff)+" ..........");
			//aWriter.newLine();
			//aWriter.write(")........  ..........");
			//aWriter.newLine();
			nextProj[k]=prjDiff;
	
		}
		//aWriter.write("===============================================");aWriter.newLine();
		try {
			int found=0;
			int[] predicts=new int[5];
		for (int k=0; k<samples.size(); k++){
			
			for (int cf=6; cf<6+6; cf++){
				aWriter.write("("+cf+")::"+dF.format(samples.get(k)[dataLength-1]+prjDiffs[k][cf-6])+";");					
			}
			aWriter.newLine();
			aWriter.write("->->->->->->->Line "+(k+1)+" .. real Draw="+hits[k]+"  ");
			predicts[k]=(int)Math.round(samples.get(k)[dataLength-1]+nextProj[k]);
			if (predicts[k] > 39) predicts[k] -= 39;
			aWriter.write("proj=>"+predicts[k]+" ..........");
			//aWriter.newLine();
			
			//if (prj==hits[k]) found++;
						
			aWriter.newLine();
		}
		aWriter.write("test "+forDay+" found : <<<");
		for (int i=0; i<5; i++){
			for (int k=0; k<5; k++){
				if (hits[i] != predicts[k]) continue;
				found++;
				aWriter.write(""+hits[i]+", ");
			}
		}
		testResults[found]++;
		lastResults[found]=forDay;
			aWriter.write(">>>"+found+"============");

			aWriter.newLine();
			aWriter.newLine();	
		} catch (IOException e){
			System.out.println("Bad I/O");
		}
	
	}
	static class EquationSet{
		MyMatrixFit myMatrix;
		double[] coeffs;
		double[] values;
		MyMatrixFit myInverse;
		int[] mapToOriginalSeq; // relation for current row to original row
	}
	static MyMatrixFit getSinValuesMatrix(double maxPeriod, double minPeriod, int startFrom, int range){
		
		//int k=0;
		Vector<double[]> retV=new Vector<double[]>();
		double pDiff=(maxPeriod - minPeriod)/(range-1);
		//double tryBase=2*Math.PI/maxPeriod;
		for (int x=startFrom; x<startFrom+range; x++){
			double[] data=new double[range];				
			
			for (int i=0; i<range; i++){
				double rad=x*2*Math.PI/(minPeriod+pDiff*i);
				data[i]=Math.sin(rad);
			}
			retV.add(data);
		}
		return new MyMatrixFit(retV);
	}

	static double getExtendedValue(double[] coef, double maxPeriod, double minPeriod, int range, double x){
		
		//int k=0;
		double retV=0;
		double pDiff=(maxPeriod - minPeriod)/(range-1);
		//double tryBase=2*Math.PI/maxPeriod;
		
			double[] data=new double[range];				
			
			for (int i=0; i<range; i++){
				double radian = x*2*Math.PI/(minPeriod+pDiff*i);
				retV += coef[i]*Math.sin(radian);				
			}
			
		
		return retV;
	}

	static void getAvgFit(double[] inData, BufferedWriter aWriter){
		
		double mean=0;
		for (int i=0; i<inData.length; i++){
			mean += inData[i];			
		}
		mean /= inData.length;
		double[] testData=new double[inData.length];
		//for (Arrays.copyOf(inData, inData.length);
		int iLastP=0;
		int maxInterval=0;
		for (int i=0; i<inData.length; i++){
			testData[i] = inData[inData.length-1-i] - mean;
			if (i>0){
				if (testData[i]*testData[iLastP] < 0){
					if (i - iLastP > maxInterval) maxInterval=i - iLastP;
					iLastP=i;
				}
			}
		}
		EquationSet wkSet=new EquationSet();
		MyMatrixFit test39=getSinValuesMatrix(maxInterval, 1, inData.length-9, 9);
		wkSet.myMatrix=test39;
		wkSet.mapToOriginalSeq=new int[9];
		wkSet.values=Arrays.copyOfRange(testData, 0, 9);//inData.length-9, inData.length);
		Vector<double[]> mData=test39.getData();
		for (int i=0; i<9; i++){
			if (Math.abs(mData.get(i)[i]) < 0.00000000001){
				boolean switchOK=false;
				for (int j=0; j<9; j++){
					if (Math.abs(mData.get(j)[i]) > 0.00000001 &&
							Math.abs(mData.get(i)[j]) > 0.00000001 ){						
						double[] old1=Arrays.copyOf(mData.get(i), 9);
						double[] new1=Arrays.copyOf(mData.get(j), 9);
						for (int k=0; k<9; k++){
							mData.get(i)[k]=new1[k];
							mData.get(j)[k]=old1[k];
						}
						switchOK=true;
						double tmp=wkSet.values[i];
						wkSet.values[i]=wkSet.values[j];
						wkSet.values[j]=tmp;
						break;
					}
				}
				if (switchOK==false){
					System.out.println("No solution for Equation ");
					return;
				}
			}
		}
		test39=new MyMatrixFit(mData);
		MyMatrixFit work39=MyMatrixFit.inverseLU(test39);
		if (work39 == null) return;
		
		double[] coeff=work39.multiply(Arrays.copyOfRange(testData, inData.length-9, inData.length));
		double pred=getExtendedValue(coeff, maxInterval, 1, 9, inData.length);
		pred += mean;
	}
	
	static int[][] performanceList=new int[6][];
	//keep tracking when the case happens not the occurring frequency
	static void initPerformanceList(int size, int value){
		for (int i=0; i<6; i++) {
			performanceList[i]=new int[size];
			Arrays.fill(performanceList[i], value);
		}
	}
	
	static double[] getHitCasesMeanVariant(){
		double[] retV= new double[6*2];
		Arrays.fill(retV, 0);
		
		for (int i=0; i<6; i++){
			int iLen=performanceList[i].length;
			int[] tmpList=Arrays.copyOf(performanceList[i], iLen);
			
			int lastLocation=-1;
			int iTotal=0;
			for (int x=0; x<iLen; x++){
				if (tmpList[x]==1){
					iTotal++;
					tmpList[x]=0;
					tmpList[x-lastLocation-1]++;
					lastLocation=x;
				}
			}
			if (iTotal==0) continue;
			//for tomorrow to happen
			//we sum over the case A=for happens > tomorrow
			// B=total cases happens > today
			int delay=iLen-1-lastLocation;
			double mean=0;
			//int totalB=0;
			for (int x=iLen-1; x>=0; x--){
				mean += x*tmpList[x];				
			}
			mean /= iTotal;
			retV[i]= mean;
			double vvr=0;
			for (int x=iLen-1; x>=0; x--){
				
				vvr += (x - mean)*(x - mean)*tmpList[x];				
			}
			vvr /= iTotal;
			retV[6+i]=Math.sqrt(vvr);
			/*
			//totalB = (totalA + performanceList[i][delay]);
			retV[i]=0;
			if (delay < iLen){
				double conditionalSum=totalA + performanceList[i][delay];
				
			retV[i]=performanceList[i][delay];
			if (conditionalSum > 0)
			retV[i] /= (totalA + performanceList[i][delay]);
			else retV[i]=1;
			} */
		}
		return retV;
	}
	
	static 
		//x	erf(x)	erfc(x)
		double[] errorFunctionValues={ 0.0	, 0.0	, 1.0
		, 0.01	, 0.011283416	, 0.988716584
		, 0.02	, 0.022564575	, 0.977435425
		, 0.03	, 0.033841222	, 0.966158778
		, 0.04	, 0.045111106	, 0.954888894
		, 0.05	, 0.056371978	, 0.943628022
		, 0.06	, 0.067621594	, 0.932378406
		, 0.07	, 0.07885772	, 0.92114228
		, 0.08	, 0.090078126	, 0.909921874
		, 0.09	, 0.101280594	, 0.898719406
		, 0.1	, 0.112462916	, 0.887537084
		, 0.11	, 0.123622896	, 0.876377104
		, 0.12	, 0.134758352	, 0.865241648
		, 0.13	, 0.145867115	, 0.854132885
		, 0.14	, 0.156947033	, 0.843052967
		, 0.15	, 0.167995971	, 0.832004029
		, 0.16	, 0.179011813	, 0.820988187
		, 0.17	, 0.189992461	, 0.810007539
		, 0.18	, 0.200935839	, 0.799064161
		, 0.19	, 0.211839892	, 0.788160108
		, 0.2	, 0.222702589	, 0.777297411
		, 0.21	, 0.233521923	, 0.766478077
		, 0.22	, 0.244295912	, 0.755704088
		, 0.23	, 0.2550226	, 0.7449774
		, 0.24	, 0.265700059	, 0.734299941
		, 0.25	, 0.27632639	, 0.72367361
		, 0.26	, 0.286899723	, 0.713100277
		, 0.27	, 0.297418219	, 0.702581781
		, 0.28	, 0.307880068	, 0.692119932
		, 0.29	, 0.318283496	, 0.681716504
		, 0.3	, 0.328626759	, 0.671373241
		, 0.31	, 0.33890815	, 0.66109185
		, 0.32	, 0.349125995	, 0.650874005
		, 0.33	, 0.359278655	, 0.640721345
		, 0.34	, 0.369364529	, 0.630635471
		, 0.35	, 0.379382054	, 0.620617946
		, 0.36	, 0.389329701	, 0.610670299
		, 0.37	, 0.399205984	, 0.600794016
		, 0.38	, 0.409009453	, 0.590990547
		, 0.39	, 0.4187387	, 0.5812613
		, 0.4	, 0.428392355	, 0.571607645
		, 0.41	, 0.43796909	, 0.56203091
		, 0.42	, 0.447467618	, 0.552532382
		, 0.43	, 0.456886695	, 0.543113305
		, 0.44	, 0.466225115	, 0.533774885
		, 0.45	, 0.47548172	, 0.52451828
		, 0.46	, 0.48465539	, 0.51534461
		, 0.47	, 0.493745051	, 0.506254949
		, 0.48	, 0.502749671	, 0.497250329
		, 0.49	, 0.511668261	, 0.488331739
		, 0.5	, 0.520499878	, 0.479500122
		, 0.51	, 0.52924362	, 0.47075638
		, 0.52	, 0.53789863	, 0.46210137
		, 0.53	, 0.546464097	, 0.453535903
		, 0.54	, 0.55493925	, 0.44506075
		, 0.55	, 0.563323366	, 0.436676634
		, 0.56	, 0.571615764	, 0.428384236
		, 0.57	, 0.579815806	, 0.420184194
		, 0.58	, 0.5879229	, 0.4120771
		, 0.59	, 0.595936497	, 0.404063503
		, 0.6	, 0.603856091	, 0.396143909
		, 0.61	, 0.611681219	, 0.388318781
		, 0.62	, 0.619411462	, 0.380588538
		, 0.63	, 0.627046443	, 0.372953557
		, 0.64	, 0.634585829	, 0.365414171
		, 0.65	, 0.642029327	, 0.357970673
		, 0.66	, 0.649376688	, 0.350623312
		, 0.67	, 0.656627702	, 0.343372298
		, 0.68	, 0.663782203	, 0.336217797
		, 0.69	, 0.670840062	, 0.329159938
		, 0.7	, 0.677801194	, 0.322198806
		, 0.71	, 0.68466555	, 0.31533445
		, 0.72	, 0.691433123	, 0.308566877
		, 0.73	, 0.698103943	, 0.301896057
		, 0.74	, 0.704678078	, 0.295321922
		, 0.75	, 0.711155634	, 0.288844366
		, 0.76	, 0.717536753	, 0.282463247
		, 0.77	, 0.723821614	, 0.276178386
		, 0.78	, 0.730010431	, 0.269989569
		, 0.79	, 0.736103454	, 0.263896546
		, 0.8	, 0.742100965	, 0.257899035
		, 0.81	, 0.748003281	, 0.251996719
		, 0.82	, 0.753810751	, 0.246189249
		, 0.83	, 0.759523757	, 0.240476243
		, 0.84	, 0.765142711	, 0.234857289
		, 0.85	, 0.770668058	, 0.229331942
		, 0.86	, 0.776100268	, 0.223899732
		, 0.87	, 0.781439845	, 0.218560155
		, 0.88	, 0.786687319	, 0.213312681
		, 0.89	, 0.791843247	, 0.208156753
		, 0.9	, 0.796908212	, 0.203091788
		, 0.91	, 0.801882826	, 0.198117174
		, 0.92	, 0.806767722	, 0.193232278
		, 0.93	, 0.811563559	, 0.188436441
		, 0.94	, 0.816271019	, 0.183728981
		, 0.95	, 0.820890807	, 0.179109193
		, 0.96	, 0.82542365	, 0.17457635
		, 0.97	, 0.829870293	, 0.170129707
		, 0.98	, 0.834231504	, 0.165768496
		, 0.99	, 0.83850807	, 0.16149193
		, 1.0	, 0.842700793	, 0.157299207
		, 1.01	, 0.846810496	, 0.153189504
		, 1.02	, 0.850838018	, 0.149161982
		, 1.03	, 0.854784211	, 0.145215789
		, 1.04	, 0.858649947	, 0.141350053
		, 1.05	, 0.862436106	, 0.137563894
		, 1.06	, 0.866143587	, 0.133856413
		, 1.07	, 0.869773297	, 0.130226703
		, 1.08	, 0.873326158	, 0.126673842
		, 1.09	, 0.876803102	, 0.123196898
		, 1.1	, 0.88020507	, 0.11979493
		, 1.11	, 0.883533012	, 0.116466988
		, 1.12	, 0.88678789	, 0.11321211
		, 1.13	, 0.88997067	, 0.11002933
		, 1.14	, 0.893082328	, 0.106917672
		, 1.15	, 0.896123843	, 0.103876157
		, 1.16	, 0.899096203	, 0.100903797
		, 1.17	, 0.902000399	, 0.097999601
		, 1.18	, 0.904837427	, 0.095162573
		, 1.19	, 0.907608286	, 0.092391714
		, 1.2	, 0.910313978	, 0.089686022
		, 1.21	, 0.912955508	, 0.087044492
		, 1.22	, 0.915533881	, 0.084466119
		, 1.23	, 0.918050104	, 0.081949896
		, 1.24	, 0.920505184	, 0.079494816
		, 1.25	, 0.922900128	, 0.077099872
		, 1.26	, 0.925235942	, 0.074764058
		, 1.27	, 0.927513629	, 0.072486371
		, 1.28	, 0.929734193	, 0.070265807
		, 1.29	, 0.931898633	, 0.068101367
		, 1.3	, 0.934007945	, 0.065992055
		, 1.31	, 0.936063123	, 0.063936877
		, 1.32	, 0.938065155	, 0.061934845
		, 1.33	, 0.940015026	, 0.059984974
		, 1.34	, 0.941913715	, 0.058086285
		, 1.35	, 0.943762196	, 0.056237804
		, 1.36	, 0.945561437	, 0.054438563
		, 1.37	, 0.947312398	, 0.052687602
		, 1.38	, 0.949016035	, 0.050983965
		, 1.39	, 0.950673296	, 0.049326704
		, 1.4	, 0.95228512	, 0.04771488
		, 1.41	, 0.953852439	, 0.046147561
		, 1.42	, 0.955376179	, 0.044623821
		, 1.43	, 0.956857253	, 0.043142747
		, 1.44	, 0.95829657	, 0.04170343
		, 1.45	, 0.959695026	, 0.040304974
		, 1.46	, 0.96105351	, 0.03894649
		, 1.47	, 0.9623729	, 0.0376271
		, 1.48	, 0.963654065	, 0.036345935
		, 1.49	, 0.964897865	, 0.035102135
		, 1.5	, 0.966105146	, 0.033894854
		, 1.51	, 0.967276748	, 0.032723252
		, 1.52	, 0.968413497	, 0.031586503
		, 1.53	, 0.969516209	, 0.030483791
		, 1.54	, 0.97058569	, 0.02941431
		, 1.55	, 0.971622733	, 0.028377267
		, 1.56	, 0.972628122	, 0.027371878
		, 1.57	, 0.973602627	, 0.026397373
		, 1.58	, 0.974547009	, 0.025452991
		, 1.59	, 0.975462016	, 0.024537984
		, 1.6	, 0.976348383	, 0.023651617
		, 1.61	, 0.977206837	, 0.022793163
		, 1.62	, 0.978038088	, 0.021961912
		, 1.63	, 0.97884284	, 0.02115716
		, 1.64	, 0.97962178	, 0.02037822
		, 1.65	, 0.980375585	, 0.019624415
		, 1.66	, 0.981104921	, 0.018895079
		, 1.67	, 0.981810442	, 0.018189558
		, 1.68	, 0.982492787	, 0.017507213
		, 1.69	, 0.983152587	, 0.016847413
		, 1.7	, 0.983790459	, 0.016209541
		, 1.71	, 0.984407008	, 0.015592992
		, 1.72	, 0.985002827	, 0.014997173
		, 1.73	, 0.9855785	, 0.0144215
		, 1.74	, 0.986134595	, 0.013865405
		, 1.75	, 0.986671671	, 0.013328329
		, 1.76	, 0.987190275	, 0.012809725
		, 1.77	, 0.987690942	, 0.012309058
		, 1.78	, 0.988174196	, 0.011825804
		, 1.79	, 0.988640549	, 0.011359451
		, 1.8	, 0.989090502	, 0.010909498
		, 1.81	, 0.989524545	, 0.010475455
		, 1.82	, 0.989943156	, 0.010056844
		, 1.83	, 0.990346805	, 0.009653195
		, 1.84	, 0.990735948	, 0.009264052
		, 1.85	, 0.99111103	, 0.00888897
		, 1.86	, 0.991472488	, 0.008527512
		, 1.87	, 0.991820748	, 0.008179252
		, 1.88	, 0.992156223	, 0.007843777
		, 1.89	, 0.992479318	, 0.007520682
		, 1.9	, 0.992790429	, 0.007209571
		, 1.91	, 0.99308994	, 0.00691006
		, 1.92	, 0.993378225	, 0.006621775
		, 1.93	, 0.99365565	, 0.00634435
		, 1.94	, 0.993922571	, 0.006077429
		, 1.95	, 0.994179334	, 0.005820666
		, 1.96	, 0.994426275	, 0.005573725
		, 1.97	, 0.994663725	, 0.005336275
		, 1.98	, 0.994892	, 0.005108
		, 1.99	, 0.995111413	, 0.004888587
		, 2.0	, 0.995322265	, 0.004677735
		, 2.01	, 0.995524849	, 0.004475151
		, 2.02	, 0.995719451	, 0.004280549
		, 2.03	, 0.995906348	, 0.004093652
		, 2.04	, 0.99608581	, 0.00391419
		, 2.05	, 0.996258096	, 0.003741904
		, 2.06	, 0.996423462	, 0.003576538
		, 2.07	, 0.996582153	, 0.003417847
		, 2.08	, 0.996734409	, 0.003265591
		, 2.09	, 0.996880461	, 0.003119539
		, 2.1	, 0.997020533	, 0.002979467
		, 2.11	, 0.997154845	, 0.002845155
		, 2.12	, 0.997283607	, 0.002716393
		, 2.13	, 0.997407023	, 0.002592977
		, 2.14	, 0.997525293	, 0.002474707
		, 2.15	, 0.997638607	, 0.002361393
		, 2.16	, 0.997747152	, 0.002252848
		, 2.17	, 0.997851108	, 0.002148892
		, 2.18	, 0.997950649	, 0.002049351
		, 2.19	, 0.998045943	, 0.001954057
		, 2.2	, 0.998137154	, 0.001862846
		, 2.21	, 0.998224438	, 0.001775562
		, 2.22	, 0.998307948	, 0.001692052
		, 2.23	, 0.998387832	, 0.001612168
		, 2.24	, 0.998464231	, 0.001535769
		, 2.25	, 0.998537283	, 0.001462717
		, 2.26	, 0.998607121	, 0.001392879
		, 2.27	, 0.998673872	, 0.001326128
		, 2.28	, 0.998737661	, 0.001262339
		, 2.29	, 0.998798606	, 0.001201394
		, 2.3	, 0.998856823	, 0.001143177
		, 2.31	, 0.998912423	, 0.001087577
		, 2.32	, 0.998965513	, 0.001034487
		, 2.33	, 0.999016195	, 0.000983805
		, 2.34	, 0.99906457	, 0.00093543
		, 2.35	, 0.999110733	, 0.000889267
		, 2.36	, 0.999154777	, 0.000845223
		, 2.37	, 0.99919679	, 0.00080321
		, 2.38	, 0.999236858	, 0.000763142
		, 2.39	, 0.999275064	, 0.000724936
		, 2.4	, 0.999311486	, 0.000688514
		, 2.41	, 0.999346202	, 0.000653798
		, 2.42	, 0.999379283	, 0.000620717
		, 2.43	, 0.999410802	, 0.000589198
		, 2.44	, 0.999440826	, 0.000559174
		, 2.45	, 0.99946942	, 0.00053058
		, 2.46	, 0.999496646	, 0.000503354
		, 2.47	, 0.999522566	, 0.000477434
		, 2.48	, 0.999547236	, 0.000452764
		, 2.49	, 0.999570712	, 0.000429288
		, 2.5	, 0.999593048	, 0.000406952
		, 2.51	, 0.999614295	, 0.000385705
		, 2.52	, 0.999634501	, 0.000365499
		, 2.53	, 0.999653714	, 0.000346286
		, 2.54	, 0.999671979	, 0.000328021
		, 2.55	, 0.99968934	, 0.00031066
		, 2.56	, 0.999705837	, 0.000294163
		, 2.57	, 0.999721511	, 0.000278489
		, 2.58	, 0.9997364	, 0.0002636
		, 2.59	, 0.999750539	, 0.000249461
		, 2.6	, 0.999763966	, 0.000236034
		, 2.61	, 0.999776711	, 0.000223289
		, 2.62	, 0.999788809	, 0.000211191
		, 2.63	, 0.999800289	, 0.000199711
		, 2.64	, 0.999811181	, 0.000188819
		, 2.65	, 0.999821512	, 0.000178488
		, 2.66	, 0.999831311	, 0.000168689
		, 2.67	, 0.999840601	, 0.000159399
		, 2.68	, 0.999849409	, 0.000150591
		, 2.69	, 0.999857757	, 0.000142243
		, 2.7	, 0.999865667	, 0.000134333
		, 2.71	, 0.999873162	, 0.000126838
		, 2.72	, 0.999880261	, 0.000119739
		, 2.73	, 0.999886985	, 0.000113015
		, 2.74	, 0.999893351	, 0.000106649
		, 2.75	, 0.999899378	, 0.000100622
		, 2.76	, 0.999905082	, 9.4918e-05
		, 2.77	, 0.99991048	, 8.952e-05
		, 2.78	, 0.999915587	, 8.4413e-05
		, 2.79	, 0.999920418	, 7.9582e-05
		, 2.8	, 0.999924987	, 7.5013e-05
		, 2.81	, 0.999929307	, 7.0693e-05
		, 2.82	, 0.99993339	, 6.661e-05
		, 2.83	, 0.99993725	, 6.275e-05
		, 2.84	, 0.999940898	, 5.9102e-05
		, 2.85	, 0.999944344	, 5.5656e-05
		, 2.86	, 0.999947599	, 5.2401e-05
		, 2.87	, 0.999950673	, 4.9327e-05
		, 2.88	, 0.999953576	, 4.6424e-05
		, 2.89	, 0.999956316	, 4.3684e-05
		, 2.9	, 0.999958902	, 4.1098e-05
		, 2.91	, 0.999961343	, 3.8657e-05
		, 2.92	, 0.999963645	, 3.6355e-05
		, 2.93	, 0.999965817	, 3.4183e-05
		, 2.94	, 0.999967866	, 3.2134e-05
		, 2.95	, 0.999969797	, 3.0203e-05
		, 2.96	, 0.999971618	, 2.8382e-05
		, 2.97	, 0.999973334	, 2.6666e-05
		, 2.98	, 0.999974951	, 2.5049e-05
		, 2.99	, 0.999976474	, 2.3526e-05
		, 3.0	, 0.99997791	, 2.209e-05
		, 3.01	, 0.999979261	, 2.0739e-05
		, 3.02	, 0.999980534	, 1.9466e-05
		, 3.03	, 0.999981732	, 1.8268e-05
		, 3.04	, 0.999982859	, 1.7141e-05
		, 3.05	, 0.99998392	, 1.608e-05
		, 3.06	, 0.999984918	, 1.5082e-05
		, 3.07	, 0.999985857	, 1.4143e-05
		, 3.08	, 0.99998674	, 1.326e-05
		, 3.09	, 0.999987571	, 1.2429e-05
		, 3.1	, 0.999988351	, 1.1649e-05
		, 3.11	, 0.999989085	, 1.0915e-05
		, 3.12	, 0.999989774	, 1.0226e-05
		, 3.13	, 0.999990422	, 9.578e-06};
	static double getErfValue(double x){
		
		int k=(int)Math.round(x*100);
		if (k<0 || k >313) return -1;
		return errorFunctionValues[k*3+1];
		
		//return errF;
		/*
		 * 
//x	erf(x)	erfc(x)
0.0		0.0			1.0
0.01	0.011283416	0.988716584
0.02	0.022564575	0.977435425
0.03	0.033841222	0.966158778
0.04	0.045111106	0.954888894
0.05	0.056371978	0.943628022
0.06	0.067621594	0.932378406
0.07	0.07885772	0.92114228
0.08	0.090078126	0.909921874
0.09	0.101280594	0.898719406
0.1		0.112462916	0.887537084
0.11	0.123622896	0.876377104
0.12	0.134758352	0.865241648
0.13	0.145867115	0.854132885
0.14	0.156947033	0.843052967
0.15	0.167995971	0.832004029
0.16	0.179011813	0.820988187
0.17	0.189992461	0.810007539
0.18	0.200935839	0.799064161
0.19	0.211839892	0.788160108
0.2		0.222702589	0.777297411
0.21	0.233521923	0.766478077
0.22	0.244295912	0.755704088
0.23	0.2550226	0.7449774
0.24	0.265700059	0.734299941
0.25	0.27632639	0.72367361
0.26	0.286899723	0.713100277
0.27	0.297418219	0.702581781
0.28	0.307880068	0.692119932
0.29	0.318283496	0.681716504
0.3		0.328626759	0.671373241
0.31	0.33890815	0.66109185
0.32	0.349125995	0.650874005
0.33	0.359278655	0.640721345
0.34	0.369364529	0.630635471
0.35	0.379382054	0.620617946
0.36	0.389329701	0.610670299
0.37	0.399205984	0.600794016
0.38	0.409009453	0.590990547
0.39	0.4187387	0.5812613
0.4		0.428392355	0.571607645
0.41	0.43796909	0.56203091
0.42	0.447467618	0.552532382
0.43	0.456886695	0.543113305
0.44	0.466225115	0.533774885
0.45	0.47548172	0.52451828
0.46	0.48465539	0.51534461
0.47	0.493745051	0.506254949
0.48	0.502749671	0.497250329
0.49	0.511668261	0.488331739
0.5		0.520499878	0.479500122
0.51	0.52924362	0.47075638
0.52	0.53789863	0.46210137
0.53	0.546464097	0.453535903
0.54	0.55493925	0.44506075
0.55	0.563323366	0.436676634
0.56	0.571615764	0.428384236
0.57	0.579815806	0.420184194
0.58	0.5879229	0.4120771
0.59	0.595936497	0.404063503
0.6		0.603856091	0.396143909
0.61	0.611681219	0.388318781
0.62	0.619411462	0.380588538
0.63	0.627046443	0.372953557
0.64	0.634585829	0.365414171
0.65	0.642029327	0.357970673
0.66	0.649376688	0.350623312
0.67	0.656627702	0.343372298
0.68	0.663782203	0.336217797
0.69	0.670840062	0.329159938
0.7		0.677801194	0.322198806
0.71	0.68466555	0.31533445
0.72	0.691433123	0.308566877
0.73	0.698103943	0.301896057
0.74	0.704678078	0.295321922
0.75	0.711155634	0.288844366
0.76	0.717536753	0.282463247
0.77	0.723821614	0.276178386
0.78	0.730010431	0.269989569
0.79	0.736103454	0.263896546
0.8		0.742100965	0.257899035
0.81	0.748003281	0.251996719
0.82	0.753810751	0.246189249
0.83	0.759523757	0.240476243
0.84	0.765142711	0.234857289
0.85	0.770668058	0.229331942
0.86	0.776100268	0.223899732
0.87	0.781439845	0.218560155
0.88	0.786687319	0.213312681
0.89	0.791843247	0.208156753
0.9		0.796908212	0.203091788
0.91	0.801882826	0.198117174
0.92	0.806767722	0.193232278
0.93	0.811563559	0.188436441
0.94	0.816271019	0.183728981
0.95	0.820890807	0.179109193
0.96	0.82542365	0.17457635
0.97	0.829870293	0.170129707
0.98	0.834231504	0.165768496
0.99	0.83850807	0.16149193
1.0		0.842700793	0.157299207
1.01	0.846810496	0.153189504
1.02	0.850838018	0.149161982
1.03	0.854784211	0.145215789
1.04	0.858649947	0.141350053
1.05	0.862436106	0.137563894
1.06	0.866143587	0.133856413
1.07	0.869773297	0.130226703
1.08	0.873326158	0.126673842
1.09	0.876803102	0.123196898
1.1		0.88020507	0.11979493
1.11	0.883533012	0.116466988
1.12	0.88678789	0.11321211
1.13	0.88997067	0.11002933
1.14	0.893082328	0.106917672
1.15	0.896123843	0.103876157
1.16	0.899096203	0.100903797
1.17	0.902000399	0.097999601
1.18	0.904837427	0.095162573
1.19	0.907608286	0.092391714
1.2		0.910313978	0.089686022
1.21	0.912955508	0.087044492
1.22	0.915533881	0.084466119
1.23	0.918050104	0.081949896
1.24	0.920505184	0.079494816
1.25	0.922900128	0.077099872
1.26	0.925235942	0.074764058
1.27	0.927513629	0.072486371
1.28	0.929734193	0.070265807
1.29	0.931898633	0.068101367
1.3		0.934007945	0.065992055
1.31	0.936063123	0.063936877
1.32	0.938065155	0.061934845
1.33	0.940015026	0.059984974
1.34	0.941913715	0.058086285
1.35	0.943762196	0.056237804
1.36	0.945561437	0.054438563
1.37	0.947312398	0.052687602
1.38	0.949016035	0.050983965
1.39	0.950673296	0.049326704
1.4		0.95228512	0.04771488
1.41	0.953852439	0.046147561
1.42	0.955376179	0.044623821
1.43	0.956857253	0.043142747
1.44	0.95829657	0.04170343
1.45	0.959695026	0.040304974
1.46	0.96105351	0.03894649
1.47	0.9623729	0.0376271
1.48	0.963654065	0.036345935
1.49	0.964897865	0.035102135
1.5		0.966105146	0.033894854
1.51	0.967276748	0.032723252
1.52	0.968413497	0.031586503
1.53	0.969516209	0.030483791
1.54	0.97058569	0.02941431
1.55	0.971622733	0.028377267
1.56	0.972628122	0.027371878
1.57	0.973602627	0.026397373
1.58	0.974547009	0.025452991
1.59	0.975462016	0.024537984
1.6		0.976348383	0.023651617
1.61	0.977206837	0.022793163
1.62	0.978038088	0.021961912
1.63	0.97884284	0.02115716
1.64	0.97962178	0.02037822
1.65	0.980375585	0.019624415
1.66	0.981104921	0.018895079
1.67	0.981810442	0.018189558
1.68	0.982492787	0.017507213
1.69	0.983152587	0.016847413
1.7		0.983790459	0.016209541
1.71	0.984407008	0.015592992
1.72	0.985002827	0.014997173
1.73	0.9855785	0.0144215
1.74	0.986134595	0.013865405
1.75	0.986671671	0.013328329
1.76	0.987190275	0.012809725
1.77	0.987690942	0.012309058
1.78	0.988174196	0.011825804
1.79	0.988640549	0.011359451
1.8		0.989090502	0.010909498
1.81	0.989524545	0.010475455
1.82	0.989943156	0.010056844
1.83	0.990346805	0.009653195
1.84	0.990735948	0.009264052
1.85	0.99111103	0.00888897
1.86	0.991472488	0.008527512
1.87	0.991820748	0.008179252
1.88	0.992156223	0.007843777
1.89	0.992479318	0.007520682
1.9		0.992790429	0.007209571
1.91	0.99308994	0.00691006
1.92	0.993378225	0.006621775
1.93	0.99365565	0.00634435
1.94	0.993922571	0.006077429
1.95	0.994179334	0.005820666
1.96	0.994426275	0.005573725
1.97	0.994663725	0.005336275
1.98	0.994892	0.005108
1.99	0.995111413	0.004888587
2.0		0.995322265	0.004677735
2.01	0.995524849	0.004475151
2.02	0.995719451	0.004280549
2.03	0.995906348	0.004093652
2.04	0.99608581	0.00391419
2.05	0.996258096	0.003741904
2.06	0.996423462	0.003576538
2.07	0.996582153	0.003417847
2.08	0.996734409	0.003265591
2.09	0.996880461	0.003119539
2.1		0.997020533	0.002979467
2.11	0.997154845	0.002845155
2.12	0.997283607	0.002716393
2.13	0.997407023	0.002592977
2.14	0.997525293	0.002474707
2.15	0.997638607	0.002361393
2.16	0.997747152	0.002252848
2.17	0.997851108	0.002148892
2.18	0.997950649	0.002049351
2.19	0.998045943	0.001954057
2.2		0.998137154	0.001862846
2.21	0.998224438	0.001775562
2.22	0.998307948	0.001692052
2.23	0.998387832	0.001612168
2.24	0.998464231	0.001535769
2.25	0.998537283	0.001462717
2.26	0.998607121	0.001392879
2.27	0.998673872	0.001326128
2.28	0.998737661	0.001262339
2.29	0.998798606	0.001201394
2.3		0.998856823	0.001143177
2.31	0.998912423	0.001087577
2.32	0.998965513	0.001034487
2.33	0.999016195	0.000983805
2.34	0.99906457	0.00093543
2.35	0.999110733	0.000889267
2.36	0.999154777	0.000845223
2.37	0.99919679	0.00080321
2.38	0.999236858	0.000763142
2.39	0.999275064	0.000724936
2.4		0.999311486	0.000688514
2.41	0.999346202	0.000653798
2.42	0.999379283	0.000620717
2.43	0.999410802	0.000589198
2.44	0.999440826	0.000559174
2.45	0.99946942	0.00053058
2.46	0.999496646	0.000503354
2.47	0.999522566	0.000477434
2.48	0.999547236	0.000452764
2.49	0.999570712	0.000429288
2.5		0.999593048	0.000406952
2.51	0.999614295	0.000385705
2.52	0.999634501	0.000365499
2.53	0.999653714	0.000346286
2.54	0.999671979	0.000328021
2.55	0.99968934	0.00031066
2.56	0.999705837	0.000294163
2.57	0.999721511	0.000278489
2.58	0.9997364	0.0002636
2.59	0.999750539	0.000249461
2.6		0.999763966	0.000236034
2.61	0.999776711	0.000223289
2.62	0.999788809	0.000211191
2.63	0.999800289	0.000199711
2.64	0.999811181	0.000188819
2.65	0.999821512	0.000178488
2.66	0.999831311	0.000168689
2.67	0.999840601	0.000159399
2.68	0.999849409	0.000150591
2.69	0.999857757	0.000142243
2.7		0.999865667	0.000134333
2.71	0.999873162	0.000126838
2.72	0.999880261	0.000119739
2.73	0.999886985	0.000113015
2.74	0.999893351	0.000106649
2.75	0.999899378	0.000100622
2.76	0.999905082	9.4918e-05
2.77	0.99991048	8.952e-05
2.78	0.999915587	8.4413e-05
2.79	0.999920418	7.9582e-05
2.8		0.999924987	7.5013e-05
2.81	0.999929307	7.0693e-05
2.82	0.99993339	6.661e-05
2.83	0.99993725	6.275e-05
2.84	0.999940898	5.9102e-05
2.85	0.999944344	5.5656e-05
2.86	0.999947599	5.2401e-05
2.87	0.999950673	4.9327e-05
2.88	0.999953576	4.6424e-05
2.89	0.999956316	4.3684e-05
2.9		0.999958902	4.1098e-05
2.91	0.999961343	3.8657e-05
2.92	0.999963645	3.6355e-05
2.93	0.999965817	3.4183e-05
2.94	0.999967866	3.2134e-05
2.95	0.999969797	3.0203e-05
2.96	0.999971618	2.8382e-05
2.97	0.999973334	2.6666e-05
2.98	0.999974951	2.5049e-05
2.99	0.999976474	2.3526e-05
3.0		0.99997791	2.209e-05
3.01	0.999979261	2.0739e-05
3.02	0.999980534	1.9466e-05
3.03	0.999981732	1.8268e-05
3.04	0.999982859	1.7141e-05
3.05	0.99998392	1.608e-05
3.06	0.999984918	1.5082e-05
3.07	0.999985857	1.4143e-05
3.08	0.99998674	1.326e-05
3.09	0.999987571	1.2429e-05
3.1		0.999988351	1.1649e-05
3.11	0.999989085	1.0915e-05
3.12	0.999989774	1.0226e-05
3.13	0.999990422	9.578e-06
		 */
	}
	static double[] getNextCasePdf(){
		double[] retV= new double[6];
		int allTotal=performanceList[0].length;
		int[] subTotal=new int[6];
		int maxDelay=0;
		for (int i=0; i<6; i++){
			subTotal[i]=0;
			int[] tmpList=Arrays.copyOf(performanceList[i], performanceList[i].length);
			int iLen=performanceList[i].length;
			int lastLocation=-1;
			for (int x=0; x<iLen; x++){
				if (tmpList[x]==1){
					subTotal[i]++;
					tmpList[x]=0;
					tmpList[x-lastLocation-1]++;
					lastLocation=x;
				}
			}
			if (subTotal[i] < iLen*0.03){
				retV[i]=0;
				break;
			}
			double mean=0;
			for (int x=0; x<lastLocation; x++){
				mean += x*tmpList[x];
			}
			mean /= subTotal[i];
			double var=0;
			for (int x=0; x<lastLocation; x++){
				var += (x-mean)*(x-mean)*tmpList[x];
			}
			var /= subTotal[i];
			var = Math.sqrt(var);
			//let's assume normal distribution
			//get mean and R first
			
			//for tomorrow to happen
			//we sum over the case A=for happens > tomorrow
			// B=total cases happens > today
			int delay=iLen-1-lastLocation;
			int totalA=0;
			//int totalB=0;
			for (int x=iLen-1; x>delay; x--){
				totalA += tmpList[x];				
			}
			if (delay > maxDelay) maxDelay=delay;
			//totalB = (totalA + performanceList[i][delay]);
			retV[i]=0;
				if (delay < iLen){
					double conditionalSum=totalA + tmpList[delay];
					
					retV[i]=tmpList[delay];
					if (conditionalSum > 0 && retV[i] > 0)
					retV[i] /= (conditionalSum);
					else {
						double arg=(delay - mean)/var;//(delay - mean)*(delay - mean)/(var*var);
						if (arg > 0){
							double v=getErfValue(arg);
							if (v > 0){
								double cf=1-v;
								double v1=getErfValue(arg + 1/var);
								if (v1 < 0) v1=0;
								retV[i]=1 - (1-v1)/(1-v);
							} else retV[i]= 0;
							//Math.exp(-arg);
				}
			}
		}
		}
		for (int i=0; i<6; i++){
			retV[i] *= subTotal[i];
			retV[i]  /= 100;
			//retV[i]  /= allTotal;
		}
		/*
		double maxR=0;
		for (int i=0; i<6; i++){
			double r= subTotal[i];
			r /= allTotal;
			for (int id=1; id<maxDelay; id++){
				if (performanceList[i][performanceList[i].length-id]==1) retV[i] *= r;
				else retV[i] *= (1-r);//subTotal[i];
			}
			retV[i] *= r;//allTotal;
			if (retV[i] > maxR) maxR=retV[i];
		}
		for (int i=0; i<6; i++) retV[i] /= maxR;
		*/
		return retV;
	}
	static void addLine(BufferedWriter aWriter, String txt){
		try {
			aWriter.write(txt);
		}catch (IOException e){
			System.out.println("Bad I/O");
		}
	}
	static void addLineN(BufferedWriter aWriter, String txt){
		try {
			aWriter.write(txt);aWriter.newLine();
		}catch (IOException e){
			System.out.println("Bad I/O");
		}
	}
	static void 
	showSummaryPage(int[][][] predictionList, int[][] hitCounts, int[][] lastHitLocation,
			int[][][] progPerformanceList, String programName, BufferedWriter aWriter ){
		//if (drawDate==null || drawDate.length()<5) {
			//if (Lottery539PageParser.yesterdayDrawDate==null || Lottery539PageParser.yesterdayDrawDate.length()<5)
				drawDate=Lottery539PageParser.getLatestDrawDate().replace('/', '-');				
		//}
				String outFileDir="C:\\Users\\eric\\projects\\"+drawDate;
				File myDir=new File(outFileDir);
				if (!myDir.exists()) myDir.mkdir();
		try {
			BufferedWriter summary=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFileDir+"\\nextPredictBase"+drawDate+".txt", true)));						
			summary.newLine();
			addLineN(aWriter, "========="+ programName +"=============="); 		
			addLineN(summary, "========="+ programName +"===="+drawDate+"=========="); 
			
			for (int dis=0; dis<hitCounts.length; dis++){
				double ttPredict=0;
				if (predictionList[dis] == null) continue;
				for (int i=0; i<predictionList[dis].length-1; i++) 
				if (predictionList[dis][i] != null) ttPredict += predictionList[dis][i].length;
				int[] lastPrediction=predictionList[dis][predictionList[dis].length-1];
				if (lastPrediction == null) continue;
				if (lastPrediction.length < 1) {
					System.out.println("No solution data in "+dis+" subset for "+programName);
				}
				String sPredictionList="predictions ( ";
				
					for (int j=0; j<lastPrediction.length; j++){
					sPredictionList += lastPrediction[j]+", ";
				}
			addLineN(aWriter, "=======for delta="+(dis)+" ================="); 
			addLineN(aWriter, sPredictionList);
			addLineN(summary, "=======for delta="+(dis)+" ================="); 
			addLineN(summary, sPredictionList);
			
			performanceList=progPerformanceList[dis];
			double[] nextCasePdf=getNextCasePdf();
			double[] caseMeanVar=getHitCasesMeanVariant();
			int ttHitBalls=0;
			for (int i=0; i<6; i++){
				String showOnOffSeq="seq:";
				int seqLen=12;
				int iLL=progPerformanceList[dis][i].length-seqLen;
				for (int ia=0; ia<seqLen; ia++){
					showOnOffSeq += ""+progPerformanceList[dis][i][iLL+ia];
				}
				showOnOffSeq +="--- ";
				showOnOffSeq += "Hits "+i+" : ["+hitCounts[dis][i]+"]@ "+lastHitLocation[dis][i]+" ??"+dF.format(nextCasePdf[i]);
				
				if (caseMeanVar[i] > 0 && lastHitLocation[dis][i] > caseMeanVar[i]) {
					double ovdToVar=(lastHitLocation[dis][i] - caseMeanVar[i])/caseMeanVar[i+6];
					showOnOffSeq += " OVD by "+dF.format(ovdToVar);
				}
				addLineN(aWriter, showOnOffSeq);
				addLineN(summary, showOnOffSeq);
				ttHitBalls += i*hitCounts[dis][i];
			}
			addLineN(aWriter,"................................");
			aWriter.write("Avg prediction count "+dF.format(ttPredict/progPerformanceList[dis][0].length));
			addLineN(aWriter," Predictions per hit "+dF.format(ttPredict/ttHitBalls));
			
			addLineN(summary,"................................");
			summary.write("Avg prediction count "+dF.format(ttPredict/progPerformanceList[dis][0].length));
			addLineN(summary," Predictions per hit "+dF.format(ttPredict/ttHitBalls));	
		}				
		addLineN(aWriter,"==========================================="); 
		addLineN(summary,"==========================================="); 
		
		summary.close();
		}catch (IOException e){
		System.out.println("Bad I/O");
	}
		
	}
	
	static void addSummary4NextDayPrediction(int[][][] predictionList, int[][] hitCounts, int[][] lastHitLocation,
			int[][][] progPerformanceList, String programName){
			
		try {
			BufferedWriter aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("nextPredictBase.txt", true)));						
			aWriter.newLine();
		aWriter.write("========="+ programName +"=============="); aWriter.newLine();		
		for (int dis=0; dis<hitCounts.length; dis++){
			double ttPredict=0;
			for (int i=0; i<predictionList[dis].length-1; i++) ttPredict += predictionList[dis][i].length;
		
			int[] lastPrediction=predictionList[dis][predictionList[dis].length-1];
			String sPredictionList="predictions ( ";
			for (int j=0; j<lastPrediction.length; j++){
				sPredictionList += lastPrediction[j]+", ";
			}
			aWriter.write("=======for delta="+(dis)+" ================="); aWriter.newLine();
			aWriter.write(sPredictionList); aWriter.newLine();
			
			performanceList=progPerformanceList[dis];
			double[] nextCasePdf=getNextCasePdf();
			double[] caseMeanVar=getHitCasesMeanVariant();
			int ttHitBalls=0;
			for (int i=0; i<6; i++){
				String showOnOffSeq="seq:";
				int iLL=progPerformanceList[dis][i].length-10;
				for (int ia=0; ia<10; ia++){
					showOnOffSeq += ""+progPerformanceList[dis][i][iLL+ia];
				}
				aWriter.write(showOnOffSeq+"--- ");
				aWriter.write("Hits "+i+" : ["+hitCounts[dis][i]+"]@ "+lastHitLocation[dis][i]+" ??"+dF.format(nextCasePdf[i]));
				if (caseMeanVar[i] > 0 && lastHitLocation[dis][i] > caseMeanVar[i]) {
					double ovdToVar=(lastHitLocation[dis][i] - caseMeanVar[i])/caseMeanVar[i+6];
					aWriter.write(" OVD by "+dF.format(ovdToVar));
				}
				aWriter.newLine();
				ttHitBalls += i*hitCounts[dis][i];
			}
			aWriter.write("................................");aWriter.newLine();
			aWriter.write("Avg prediction count "+dF.format(ttPredict/progPerformanceList[dis][0].length));
			aWriter.write(" Predictions per hit "+dF.format(ttPredict/ttHitBalls));
			
		}
				
		aWriter.write("==========================================="); aWriter.newLine();
		
		}catch (IOException e){
		System.out.println("Bad I/O");
	}
		
	}
	static int[] lastResults=new int[6];
	static int[] testResults=new int[6];
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final String[] drawStyle={"開出順序", "大小順序"};
		int select_type=0;
		JackpotReader aReader=JackpotReader.getInstance(dataCenter+"test539.txt", drawStyle[select_type]);
		Vector<int[]> samples=aReader.readData(5, 39, 512);
		int iLen1=samples.get(0).length;
		while (samples.get(0)[iLen1-1]==0) iLen1--;
		int[] testSeq=new int[iLen1];
		Vector<int[]> dateAscend=new Vector<int[]>();
		for (int s=0; s<5; s++){
			for (int i=0; i<iLen1; i++){
				testSeq[i]=
						samples.get(s)[iLen1-1-i];
			}
			dateAscend.add(Arrays.copyOf(testSeq, iLen1));
		}
		//Vector<double[]> rSamples=new Vector<double[]>();
		int accuracyInPercent=80;
		int[] useSampleCounts={39*6};//, 39*3, 39*2, 39};
		for (int ih=0; ih<useSampleCounts.length; ih++){
			int sampleSize=useSampleCounts[ih];
		BufferedWriter aWriter;
		try {
			aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("projDiffStat"+accuracyInPercent+"with"+sampleSize+".txt")));
			Arrays.fill(lastResults, 0);
			projByAvgDiff1Line(1, samples.get(0), aWriter);
			Arrays.fill(testResults, 0);
			int[][] localPerformanceList=new int[6][6*39];
			for (int x=0; x<6;x++)
				Arrays.fill(localPerformanceList[x], 0);
			Arrays.fill(drawnAverage, 0);
			meanDiff=new double[sampleSize+4];
			Arrays.fill(meanDiff, 0);
			meanRatio=new double[sampleSize+4];
			Arrays.fill(meanRatio, 0);
			drawnMean=new double[sampleSize+4];
			Arrays.fill(drawnMean, 0);
			for (int iTest=sampleSize; iTest>=0; iTest--){
				int[] hits=new int[5];
				Vector<int[]> testSample=new Vector<int[]>();
				String hitList="Draw(";
				for (int k=0; k<samples.size(); k++){
					if (iTest > 0)
					hits[k]=dateAscend.get(k)[iLen1-iTest];
					hitList += ""+hits[k]+",";
					testSample.add(Arrays.copyOfRange(dateAscend.get(k), iLen1-iTest-256, iLen1-iTest));
				}
				projByDiffStatistics(
				//projByAvgDiff(
						iTest, testSample, hits, accuracyInPercent, aWriter, localPerformanceList );
				//aWriter.write(">>>>"+hitList); aWriter.newLine();
				//getAvgPdf(iTest+1, testSample, hits, aWriter );
				aWriter.write("==========================================="); aWriter.newLine();
			}
			/*
			String mDiff="Diff ";
			String sDrawnMean="hit Mean ";
			int nCount=0;
			int pCount=0;
			double pDiff=0;
			double nDiff=0;
			double nRatio=0;
			double pRatio=0;
			for (int im1=meanDiff.length; im1 > 0; im1--){
				int im=im1-1;
				mDiff += ""+im+"["+dF.format(meanDiff[im])+"], ";
				sDrawnMean += ""+im+"["+dF.format(drawnMean[im])+"], ";
				if (meanDiff[im]<0) { nCount++; nDiff += meanDiff[im]; nRatio += meanRatio[im];}
				if (meanDiff[im]>0) { pCount++; pDiff += meanDiff[im]; pRatio += meanRatio[im];}
			}
			aWriter.write(mDiff+"/ p="+pCount+"("+dF.format(pDiff/pCount)+")["+dF.format(pRatio/pCount)+"] n="+nCount+"("+dF.format(nDiff/nCount)+")["+dF.format(nRatio/nCount)+"]"); aWriter.newLine();
			aWriter.write(sDrawnMean);aWriter.newLine();
			//
			getAvgFit(Arrays.copyOfRange(drawnMean, 1, 13), aWriter);
			
			//
			int[] hits=new int[5];
			Arrays.fill(hits, 0);
			projByAvgDiff(0, samples, hits, aWriter );
			getAvgPdf(1, samples, hits, aWriter );
			//aWriter.write("==========================================="); aWriter.newLine();
			//int[] hits=new int[5];
			Arrays.fill(hits, 0);
			
			//getAvgPdf(120, samples, hits, aWriter );
			//aWriter.write("==========================================="); aWriter.newLine();
			 * */
			JackpotReader.performanceList=localPerformanceList;
			double[] nextCasePdf=JackpotReader.getNextCasePdf();
			double[] caseMeanVar=JackpotReader.getHitCasesMeanVariant();
				for (int s=0; s<6; s++){
					aWriter.write("Hits "+s+" : ["+testResults[s]+"]@"+lastResults[s]);
					aWriter.write("   	??"+dF.format(nextCasePdf[s]));
					//int days=hitCounts[s]*lastHitLocation[s];
					if (caseMeanVar[s] > 0 && lastResults[s] > caseMeanVar[s]) {
						double ovdToVar=(lastResults[s] - caseMeanVar[s])/caseMeanVar[s+6];
						aWriter.write(" OVD by "+dF.format(ovdToVar));
					}
					aWriter.newLine();

					//aWriter.write("Hits "+s+" : ["+hitCounts[s]+"]@"+lastHitLocation[s]+" ??"+dF.format(nextCasePdf[s]));aWriter.newLine();
				}
				aWriter.close();
			aWriter.write("for "+sampleSize+" samples");
			aWriter.write("==========================================="); aWriter.newLine();
			/*
			 * double[] nextProj=new double[samples.size()];
			double[][] prjDiffs=new double[samples.size()][6];
			for (int k=0; k<samples.size(); k++){
				//aWriter.write("/////////////// LINE  "+(k+1)+"//////////////////////");
				//aWriter.newLine();
				double[] rSample1=new double[samples.get(k).length];
				for (int i=0; i<samples.get(k).length; i++){
					rSample1[samples.get(k).length-1-i]=samples.get(k)[i];
				}
				int dataLen=samples.get(k).length;
				double prjDiff=0;
				for (int cf=1; cf<7; cf++){
					//aWriter.write("+++++++++ check "+(40*cf)+"+++++++++++");aWriter.newLine();
					int[][] iFreq=new int[40][40];
					for (int j=0; j<40; j++){
						Arrays.fill(iFreq[j], 0);
					}
					double avgDiff=(rSample1[dataLen-1] - rSample1[dataLen-cf*40-1])/(cf*40);
					double absDiff=0;
					for (int ix=dataLen-cf*40-1; ix<dataLen-1; ix++){
						iFreq[(int)rSample1[ix]][(int)rSample1[ix+1]]++;
						double diff = Math.abs(rSample1[ix+1] - rSample1[ix]);
						if (diff < 0) diff += 39;
						absDiff += diff;
					}
					absDiff /= (cf*40); 
					for (int j=0; j<40; j++){
						//aWriter.write(""+j+":");
						for (int jx=0; jx<40; jx++){
							if (iFreq[j][jx]<1) continue;
							//aWriter.write(""+jx+"["+iFreq[j][jx]+"]");
						}
						//aWriter.write(")..................");aWriter.newLine();
					}
					//aWriter.write(")........ agv Diff="+dF.format(avgDiff)+"; absDiff="+dF.format(absDiff)+" ..........");aWriter.newLine();
					//aWriter.write("===============================================");aWriter.newLine();
					prjDiffs[k][cf-1]=absDiff;
					prjDiff += absDiff;
				}
				prjDiff /= 6;
				//aWriter.write(")........ last Draw="+samples.get(k)[0]+"; !!!!! PROJECTING DIFF="+dF.format(prjDiff)+" ..........");
				//aWriter.newLine();
				//aWriter.write(")........  ..........");
				//aWriter.newLine();
				nextProj[k]=prjDiff;
		
			}
			aWriter.write("===============================================");aWriter.newLine();
			
			for (int k=0; k<samples.size(); k++){
				int last1=samples.get(k)[0];
				aWriter.write("->->->->->->->Line "+(k+1)+" .. last Draw="+last1+" has ");
				aWriter.newLine();
				
				for (int cf=0; cf<6; cf++){
					aWriter.write("("+cf+")::"+dF.format(last1+prjDiffs[k][cf])+";");					
				}
				aWriter.newLine();
				aWriter.write("\t\t\t PROJECTING NEXT="+dF.format(last1+nextProj[k])+" ..........");
				aWriter.newLine();
				aWriter.newLine();
			}
			*/
		//aWriter.newLine();
			aWriter.close();
			
		} catch (FileNotFoundException e){
			System.out.println("No file");
	} catch (IOException e){
		System.out.println("Bad I/O");
	}
		}
		/*
		Complex aC=new Complex(0, 0);
		Complex[] spectrum=aReader.recursiveFFT_DIT2(Complex.array(rSample1), 1);
		for (int i=0; i<spectrum.length; i++){
			System.out.println("["+i+"]("+dF.format(spectrum[i].re)+", "+dF.format(spectrum[i].im)+") r="+
					dF.format(Math.sqrt(spectrum[i].re*spectrum[i].re+spectrum[i].im*spectrum[i].im))+" a="+
					dF.format(Math.atan(spectrum[i].im/spectrum[i].re)));
		}
		*/
		//JackPotReader bReader=JackPotReader.getInstance("testBigLuck.txt", "大小順序");
		//bReader.readData(6, 49, 25);
		//select_type=1;
		//JackpotReader bReader=JackpotReader.getInstance("test539.txt", drawStyle[select_type]);
		//Vector<int[]> samples1=bReader.readData(5, 39, 512);
		//int iC=0;
		/*
			String fileName="ellen_funding.txt";
			BufferedReader mReader;
			BufferedWriter aWriter;
			try {
				mReader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));						
				aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("out"+fileName)));
			
				double wiredAmt=0;
			String aLine=null;
			do {
					aLine=mReader.readLine();
				
				if (aLine == null) break;
				int idx=aLine.indexOf("amount");
				if ( idx < 0) continue;
				int idd=aLine.indexOf("yyyy)");
				if ( idx < 0) continue;
				int ida=aLine.indexOf("credit account");
				if ( ida < 0) continue;
				
				String dateW=aLine.substring(idd+5, idd+5+10);
				int i9=idx+6;
				while (aLine.charAt(i9)=='.' || aLine.charAt(i9)==',' || (aLine.charAt(i9)>='0' && aLine.charAt(i9)<='9') ) i9++;
				String amount=aLine.substring(idx+6, i9);
				i9=ida+"credit account".length();
				while (i9<aLine.length()  &&  aLine.charAt(i9)>='0' && aLine.charAt(i9)<='9')  i9++;
				String account=aLine.substring(ida+7, i9);
				
				//aWriter.write("("+iC+")"+aLine);
				aWriter.newLine();	
				aWriter.write("Date:"+dateW.substring(5)+"-"+
						dateW.substring(3, 5)+"-"+dateW.substring(0,3)+
						"  Amount : "+ amount+"  Acc: " +account);
				aWriter.newLine();	aWriter.write("........");aWriter.newLine();
				String[] amts=amount.split(",");
				double tmpA=0;
				for (int i=0; i<amts.length; i++){
					tmpA=tmpA*1000 + Double.parseDouble(amts[i]);
				}
				wiredAmt += tmpA; 
				
			} while (aLine!=null);
			aWriter.write("Total Amount: "+wiredAmt);
			aWriter.newLine();
				aWriter.close();
				mReader.close();
			} catch (FileNotFoundException e){
				System.out.println("No file");
		} catch (IOException e){
			System.out.println("Bad I/O");
		}
			
		*/
	
		
		
	}

}
