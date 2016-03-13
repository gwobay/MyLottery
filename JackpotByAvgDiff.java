import java.io.BufferedReader;
import java.io.BufferedWriter;
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


public class JackpotByAvgDiff {

	String dataFileName;
	Vector<String> dailyNumberSet;
	static Vector<String> allDataSets=new Vector<String> ();
	String keyToPickUp;
	static DecimalFormat dF=new DecimalFormat("0.00");
	static DecimalFormat dI=new DecimalFormat("00");
	static String dataCenter="C:/Users/eric/projects/datacenter/";

	static HashMap<String, String> upTodayData=new HashMap<String, String>();
	
	final static String voice="01testCaseCount5ABC DEFGHIJKL,MNOPQRSTU.VWXYZ!6789?";
	public Vector<String>  getAllData()
	{
		return allDataSets;
	}
	
	static public JackpotByAvgDiff getInstance(String fileName, String whichLine){
		if (fileName==null) return null;
		JackpotByAvgDiff aReader=new JackpotByAvgDiff();
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
	
	static String drawDate="";
	static int noOfDrawYearTodate=0;
	public Vector<int[]> readData(int hits, int numberMax, int forDays){
		if (numberMax > ballMax) return null;
		int[] allList=new int[forDays];
		Arrays.fill(balls, 0);
		Arrays.fill(allList, 0);
		Vector<int[]> samples=new Vector<int[]>();
		double[] data=new double[numberMax+1];
		double[] stats=new double[numberMax+1];
		Arrays.fill(data, 0);
		Arrays.fill(stats, 0);
		//samples.add(allList);
		//samplesStat.add(data);
		Vector<String> dateLines=new Vector<String>();
		for (int i=0; i<hits; i++)
		{
			samplesStat.add(Arrays.copyOf(data, numberMax+1));
			samples.add(Arrays.copyOf(allList, forDays));
		}
		
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
		BufferedReader mReader;
		try {
			mReader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));						
			BufferedWriter aWriter;
			
			int drawNumber=-1;
			int lastDrawNumber=-1;
			String pref="out";
			if (keyToPickUp.equalsIgnoreCase("開出順序")) pref += "drop";
			aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pref+forDays+pureFileName)));						
		
			upTodayData.clear();
		String tripLine=null;
		int lastDupH=-1;
		String aLine=null;
		String lastLine=null;
		int[] lastSet=new int[5];
		lastSet[0]=0;
		String drawDay="";
		Vector<String> allData=new Vector<String>();
		do{
			
				aLine=mReader.readLine();
				if (aLine==null) break;
				if (aLine.indexOf("開獎")>0){
					drawNumber = Integer.parseInt(aLine.substring(0, 9)) % 1000;
					
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
			allData.add("("+(++iC)+")"+drawDay+":"+aLine.substring(idx+keyToPickUp.length()));
		} while (aLine != null);
		
		int totalSamples=iC*5;
		upTodayData.clear();
		
		mReader.close();
		
		System.out.println("Total "+iC+" lines of data");
		//Iterator itr=upTodayData.keySet().iterator();
		int uBnd=allData.size()<forDays?allData.size():forDays;
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
						System.out.println("Trip "+dupH+"<"+aLine+"> has "+lastLine);
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
			BufferedWriter aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pref+drawDate+pureFileName)));
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
		testResults[0][found]++;
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
	
	public static void projByDiffStatistics(int forDay, Vector<int[]> samples, int[] hits, int confidentLevel, BufferedWriter aWriter){
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
		testResults[0][found]++;
		if (forDay > 0)
		lastResults[0][found]=forDay;
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
	
	static Vector<double[]> get6DayMovingAvg(Vector<double[]> orgData){
		Vector<double[]> retV=new Vector<double[]>();
		for (int s=0; s<orgData.size(); s++){
			double[] lineData=orgData.get(s);
			double[] mvLine=new double[lineData.length-6];
			for (int x=5; x<lineData.length-1; x++){
				double v=0;
				for (int i=0; i<6; i++){
					v += lineData[x-i];
				}
				mvLine[x-5]= v / 6;
			}
			retV.add(mvLine);
		}
		return retV;
	}
	
	static Vector<double[]> get6DayMovingAvgI(Vector<int[]> orgData){
		Vector<double[]> convertData=new Vector<double[]>();
		for (int s=0; s<orgData.size(); s++){
			double[] aLine=new double[ orgData.get(s).length];
			for (int x=0; x<orgData.get(s).length; x++){
				aLine[x]=orgData.get(s)[x];
			}
			convertData.add(aLine);
		}
		return get6DayMovingAvg(convertData);
	}
	
	static double[] historicPositiveMaxDiff=new double[5];
	static double[] historicNegativeMaxDiff=new double[5];
	Vector<double[]> sixDayMovingAvg=null;
	
	public static int[][] projByAvgDiff(int forDay, Vector<int[]> samples, int[] hits, BufferedWriter aWriter, int[][][] localPerformanceList){
		int dataLength=samples.get(0).length;
		//Vector<double[]> sixDayMovingAvg=get6DayMovingAvgI(samples);
		int iEnsamble=6;
		//Vector<double[]> rSamples=new Vector<double[]>();
		double[] nextProj=new double[samples.size()];
		int maxCheckDayDiff=localPerformanceList.length; //3
		double[][] expectDistance=new double[samples.size()][maxCheckDayDiff];
		
		double[][] prjDiffs=new double[samples.size()][iEnsamble];
		String[] showPredics=new String[maxCheckDayDiff];//"Preds:(";
		int[][] retV=new int[maxCheckDayDiff][];
		for (int k=0; k<samples.size(); k++){
			//aWriter.write("/////////////// LINE  "+(k+1)+"//////////////////////");
			//aWriter.newLine();
			//no need to reverse already ascending
			double[] rSample1=new double[samples.get(k).length];
			
			double[][] mvDistanceCount=new double[3][40];
			for (int v=0; v<maxCheckDayDiff; v++){
				Arrays.fill(expectDistance[v], 0);			
				rSample1[v]=samples.get(k)[v];
			}
			
			for (int i=3; i<samples.get(k).length; i++){
				rSample1[i]=samples.get(k)[i];
				for (int v=0; v<maxCheckDayDiff; v++){					
					int d=samples.get(k)[i] - samples.get(k)[i-(1+v)];
					if (d <=0) d += 39;
					mvDistanceCount[v][d]++;
				}			
			}

			int dataLen=samples.get(k).length;
			double prjDiff=0;
			//int iniCF=6;
			for (int cf1=0; cf1<iEnsamble; cf1++){
				int cf=cf1+1;
				//aWriter.write("+++++++++ check "+(40*cf)+"+++++++++++");aWriter.newLine();
				//int[][] iFreq=new int[40][40];
				for (int j=0; j<40; j++){
					//Arrays.fill(iFreq[j], 0);
				}
				//double avgDiff=(rSample1[dataLen-1] - rSample1[dataLen-cf*40-1])/(cf*40);
				double absDiff=0;
				int iAdded=0;
				for (int ix=dataLen-cf*40-1; ix<dataLen-1; ix++){
					if (ix < 0) continue;
					//iFreq[(int)rSample1[ix]][(int)rSample1[ix+1]]++;
					double diff = rSample1[ix+1] - rSample1[ix];//Math.abs(rSample1[ix+1] - rSample1[ix]);
					if (diff <= 0) diff += 39;
					absDiff += diff;
					iAdded++;
				}
				absDiff /= iAdded; 
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
			for (int d=0; d<maxCheckDayDiff; d++){
				int cc=0;
				for (int x=1; x<40;x++) {
					expectDistance[k][d] += x*mvDistanceCount[d][x];
				}
				expectDistance[k][d] /= (samples.get(k).length - (d+1));
				expectDistance[k][d] += samples.get(k)[samples.get(k).length-1-d];
				int loc=(int)Math.round(expectDistance[k][d]);
				if (loc <= 0 ) loc += 39;
				if (loc > 39) loc %= 39;
				if (loc==0) loc=39;
				expectDistance[k][d]=loc;
				//allProjects[loc]=1;
			}			
		}
		//aWriter.write("===============================================");aWriter.newLine();
		int[] allProjects=new int[40];
		Arrays.fill(allProjects, 0);
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
			if (predicts[k] > 39) predicts[k] %= 39;
			if (predicts[k] == 0) predicts[k]=39;
			allProjects[predicts[k]]=1;			
			aWriter.write("proj=>"+predicts[k]+" ..........");
			
			//aWriter.newLine();
			
			//if (prj==hits[k]) found++;
						
			aWriter.newLine();

		}
		
		for (int dis=0; dis<maxCheckDayDiff; dis++){
			found=0;
			int[] myProjs=Arrays.copyOf(allProjects, 40);
			for (int k=0; k<5; k++){
				myProjs[(int)expectDistance[k][dis]]=1;
				//if (dis >=0 ) myProjs[(int)expectDistance[k][0]]=1;
				//if (dis >=1 ) myProjs[(int)expectDistance[k][1]]=1;
				//if (dis >=2 ) myProjs[(int)expectDistance[k][2]]=1;
			}	
			showPredics[dis]="Preds:(";
			String sFound="(";
			int iProj=0;
			int[] projs=new int[10];
			for (int k=1; k<40; k++){
				projs[iProj]=0;
				if (myProjs[k]==0) continue;
				projs[iProj]=k;
				iProj++;
				showPredics[dis] += ""+k+", ";
				for (int i=0; i<5; i++){				
					if (hits[i] != k) continue;//predicts[k]) continue;
					found++;
					sFound += ""+hits[i]+", ";
					break;
				}
			}
			retV[dis]=Arrays.copyOf(projs, iProj);
			aWriter.newLine();
			aWriter.write(showPredics[dis]+")"+iProj+"============");aWriter.newLine();
			aWriter.write("test "+forDay+" found : <<<"+sFound+")");		
			aWriter.write(">>>"+found);
			
				testResults[dis][found]++;
				if (forDay > 0)
				lastResults[dis][found]=forDay;
				if (forDay > 0)
				localPerformanceList[dis][found][testCaseCount-forDay]=1;
		}
			aWriter.newLine();
			aWriter.newLine();	
		} catch (IOException e){
			System.out.println("Bad I/O");
		}
		return retV;
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
	static int testCaseCount=JackpotReader.testCaseCount;
	static int[][] lastResults=new int[3][6];
	static int[][] testResults=new int[3][6];
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final String[] criteria={"開出順序", "大小順序"};
		for (int iType=0; iType<2; iType++){
			int setType=iType;//setTypes[iq];
			String lineType=criteria[setType];
		
		JackpotReader aReader=JackpotReader.getInstance(dataCenter+"test539.txt", lineType);
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
		Vector<double[]> sixDayMovingAvg=get6DayMovingAvgI(dateAscend);
		
		Arrays.fill(historicPositiveMaxDiff, 0);
		Arrays.fill(historicNegativeMaxDiff, 0);
		for (int s=0; s<dateAscend.size(); s++)
		for (int x=5; x<iLen1-1; x++){
			double d=dateAscend.get(s)[x] - sixDayMovingAvg.get(s)[x-5];
			if (d > 0 && d > historicPositiveMaxDiff[s]) historicPositiveMaxDiff[s]=d;
			else if (d < 0 && (-d) > historicNegativeMaxDiff[s]) historicNegativeMaxDiff[s]=-d;
		}


		//Vector<double[]> rSamples=new Vector<double[]>();
		int accuracyInPercent=80;
		int[] useSampleCounts={testCaseCount};//, 39*3, 39*2, 39};
		for (int ih=0; ih<useSampleCounts.length; ih++){
			int sampleSize=useSampleCounts[ih];
		int checkDiffCases=3;
		BufferedWriter aWriter;
		try {
			aWriter=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("projAvgDiff"+"with"+sampleSize+lineType+".txt")));
			System.out.println("JackpotByAvgDiff Creating file "+"projAvgDiff"+"with"+sampleSize+lineType+".txt");
			int[][][] localPerformanceList=new int[checkDiffCases][6][testCaseCount];
			lastResults=new int[checkDiffCases][6];
			testResults=new int[checkDiffCases][6];

			for (int i=0; i<checkDiffCases; i++){
				Arrays.fill(lastResults[i], 0);
			//projByAvgDiff1Line(1, samples.get(0), aWriter);
				Arrays.fill(testResults[i], 0);
				for (int x=0; x<6;x++)
					Arrays.fill(localPerformanceList[i][x], 0);
			}
			int[][][] predictionTS=new int[checkDiffCases][testCaseCount+1][];
			Arrays.fill(drawnAverage, 0);
			meanDiff=new double[sampleSize+4];
			Arrays.fill(meanDiff, 0);
			meanRatio=new double[sampleSize+4];
			Arrays.fill(meanRatio, 0);
			drawnMean=new double[sampleSize+4];
			Arrays.fill(drawnMean, 0);
			String[] predicList=null;
			for (int iTest=sampleSize; iTest>=0; iTest--){
				int[] hits=new int[5];
				Vector<int[]> testSample=new Vector<int[]>();
				String hitList="Draw(";
				for (int k=0; k<samples.size(); k++){
					if (iTest > 0)
					hits[k]=dateAscend.get(k)[iLen1-iTest];
					hitList += ""+hits[k]+",";
					testSample.add(Arrays.copyOfRange(dateAscend.get(k), 0, iLen1-iTest));
				}
				//projByDiffStatistics(iTest, testSample, hits, accuracyInPercent, aWriter );
				int[][] predicList1=projByAvgDiff(
						iTest, testSample, hits,  aWriter , localPerformanceList);
				for (int iD=0; iD<checkDiffCases; iD++) predictionTS[iD][testCaseCount-iTest]=Arrays.copyOf(predicList1[iD], predicList1[iD].length);
				//aWriter.write(">>>>"+hitList); aWriter.newLine();
				//getAvgPdf(iTest+1, testSample, hits, aWriter );
				aWriter.write("==========================================="); aWriter.newLine();
			}
			String myName="JackpotByAvgDiff";
			JackpotReader.showSummaryPage(predictionTS, testResults, lastResults, localPerformanceList, myName, aWriter);
			/*
			BufferedWriter predictBase=new BufferedWriter(new OutputStreamWriter(new FileOutputStream("toPredictBase.txt", true)));						
			predictBase.newLine();
			
			predictBase.write("========= JackpotByAvgDiff =============="); predictBase.newLine();
			
			aWriter.write("========= JackpotByAvgDiff =============="); aWriter.newLine();
			for (int dis=0; dis<3; dis++){
				aWriter.write("......"+predicList[dis]); aWriter.newLine();
				predictBase.write("......"+predicList[dis]); predictBase.newLine();
				
				JackpotReader.performanceList=localPerformanceList[dis];
				double[] nextCasePdf=JackpotReader.getNextCasePdf();
				double[] caseMeanVar=JackpotReader.getHitCasesMeanVariant();
				for (int i=0; i<6; i++){
					String showOnOffSeq="seq:";
					int iLL=localPerformanceList[dis][i].length-10;
					for (int ia=0; ia<10; ia++){
						showOnOffSeq += ""+localPerformanceList[dis][i][iLL+ia];
					}
					predictBase.write(showOnOffSeq+"--- ");
					aWriter.write(showOnOffSeq+"--- ");
					aWriter.write("Hits "+i+" : ["+testResults[dis][i]+"]@ "+lastResults[dis][i]+" ??"+dF.format(nextCasePdf[i]));					
					predictBase.write("Hits "+i+" : ["+testResults[dis][i]+"]@ "+lastResults[dis][i]+" ??"+dF.format(nextCasePdf[i]));
					if (caseMeanVar[i] > 0 && lastResults[dis][i] > caseMeanVar[i]) {
						double ovdToVar=(lastResults[dis][i] - caseMeanVar[i])/caseMeanVar[i+6];
						aWriter.write(" OVD by "+dF.format(ovdToVar));
						predictBase.write(" OVD by "+dF.format(ovdToVar));
					}
					aWriter.newLine();
					predictBase.newLine();
				}
				aWriter.write("......................................"); aWriter.newLine();
				predictBase.write("......................................"); predictBase.newLine();
			}
			aWriter.write("for "+sampleSize+" samples");
			aWriter.write("==========================================="); aWriter.newLine();
			
			predictBase.close();
			*/
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
			//getAvgFit(Arrays.copyOfRange(drawnMean, 1, 13), aWriter);
			
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
			for (int i=0; i<6; i++){
				aWriter.write("found "+i+" fit "+testResults[i]+" cases @"+lastResults[i]);aWriter.newLine();
			}
			aWriter.write("for "+sampleSize+" samples");
			aWriter.write("==========================================="); aWriter.newLine();
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
