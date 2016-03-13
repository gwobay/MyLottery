/*

 * 1.2 version.
 */

import java.awt.image.*;
import java.text.DecimalFormat;

/* 
 * This is like the FontDemo applet in volume 1, except that it 
 * uses the Java 2D APIs to define and render the graphics and text.
 *
 * read data file data5day generated by Script c.data5day
 */

public class ImgFactory 
{
//start of program
	private static final int max1=5;//, rectX=600, rectY=500;
	private static int inhouse=0;
   private static BufferedImage[] mineImg=new BufferedImage[max1];
	private static int max=5;
	
	synchronized static BufferedImage getImg(int rectX, int rectY)
	{
	BufferedImage returnImg=null;
		if (inhouse < max)
		{
			try {
			returnImg=new BufferedImage(rectX, rectY, BufferedImage.TYPE_INT_ARGB);
			} catch (OutOfMemoryError e) {max=inhouse;}
			if (returnImg != null){mineImg[inhouse++]=returnImg;}
		}
		if (returnImg==null)
		returnImg=mineImg[inhouse++ % max];
		
		return returnImg;		 
	}
	

//end of program

public static void main(String[] args)
{
	/*
BufferedImage getImg;
for (int i=0; i<100; i++)
	{
	getImg=ImgFactory.getImg(500, 600);
		System.out.println("<"+i+">"+getImg.toString());
	}
	System.out.println("Done");
*/
	int iC=0;
	int[] set1={11,20}, set2={9, 17, 19, 33}, set3={35,36,37},
			set4={11,20,21}, set5={9, 17, 19, 38};
	
	double itt=0;
	DecimalFormat dI=new DecimalFormat("00");
	DecimalFormat dF=new DecimalFormat("0.00");
	/*
	for (int i=0; i<set1.length; i++){
		for (int k=i+1; k<set1.length; k++){
			for (int j=0; j<set2.length; j++){
				System.out.print(dI.format(set2[j])+"x"+dI.format(set1[i])+"x"+dI.format(set1[k])+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");
			}
		}
	}
	itt += iC*0.2;
	System.out.println("------- ");
	System.out.println("---- 以上 3x0.2 ---------共 "+dF.format(iC*0.2)+" --------------------");
	iC=0;
	
	for (int i=0; i<set2.length; i++){
		for (int k=i+1; k<set2.length; k++){
			for (int j=0; j<set1.length; j++){
				System.out.print(dI.format(set1[j])+"x"+dI.format(set2[i])+"x"+dI.format(set2[k])+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");
			}
		}
	}
	
	System.out.println("------- ");
	System.out.println("----- 以上  3X0.5 --------共 "+iC*0.5+" --------------------");

	iC=0;
	//DecimalFormat dI=new DecimalFormat("00");
	for (int i=0; i<set3.length; i++){
		
			for (int j=i+1; j<set3.length; j++){
				System.out.print(dI.format(set3[i])+"x"+dI.format(set3[j])+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");
			}
		
	}
	System.out.println("-------- ");
	System.out.println("---- 以上  3x0.2 ---------共 "+dF.format(iC*0.2)+" --------------------");
	
	iC=0;
	//DecimalFormat dI=new DecimalFormat("00");
	for (int i=0; i<set4.length; i++){
		
			for (int j=0; j<set5.length; j++){
				System.out.print(dI.format(set4[i])+"x"+dI.format(set5[j])+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");
			}
		
	}
	System.out.println("-------- ");
	System.out.println("---- 以上  3x0.2 ---------共 "+dF.format(iC*0.2)+" --------------------");
	
	System.out.println("----以下是說明，供參考--------------------");
	System.out.println("---A={8, 13}, B={9, 17, 19, 33, 36, 38}--C=5  ------");
	System.out.println("---D={8, 13, 9, 17, 19, 33, 36, 38}, E={9, 17, 19, 38}------");
	
	System.out.println("--以上分別是-AxAxB，AxBxB，DxD，CxE，-----------");
	
	
	int[] set6={12,15,34}, set7={9, 19, 29}, set8={8},
			set9={9, 19, 29, 8};
	
	iC=0;
	itt=0;
	for (int i=0; i<set6.length; i++){
		for (int ib=i+1; ib<set6.length; ib++){
			for (int j=0; j<set9.length; j++){
				System.out.print(dI.format(set6[i])+"x"+dI.format(set6[ib])+"x"+dI.format(set9[j])+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");
			}
		}
	}
	itt += iC*0.2;	
	System.out.println("------- ");
	System.out.println("---- 以上 3x0.2 ---------共 "+dF.format(iC*0.2)+" --------------------");

	iC=0;
	for (int i=0; i<set9.length; i++){
		for (int ib=i+1; ib<set9.length; ib++){
			for (int j=0; j<set6.length; j++){
				System.out.print(dI.format(set9[i])+"x"+dI.format(set9[ib])+"x"+dI.format(set6[j])+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");
			}
		}
	}
	itt += iC*0.2;	
	System.out.println("------- ");
	System.out.println("---- 以上 3x0.2 ---------共 "+dF.format(iC*0.2)+" --------------------");
	
	iC=0;

	for (int i=0; i<set6.length; i++){
		for (int ib=i+1; ib<set6.length; ib++){
			for (int j=0; j<set9.length; j++){
			for (int jb=j+1; jb<set9.length; jb++){
				System.out.print(dI.format(set6[i])+"x"+dI.format(set6[ib])+"x"+dI.format(set9[j])+"x"+dI.format(set9[jb])+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");
			}
		}
		}
	}
	itt += iC*0.2;	
	System.out.println("------- ");
	System.out.println("----- 以上  4x0.2 --------共 "+dF.format(iC*0.2)+" --------------------");

	iC=0;

	for (int i=0; i<set6.length; i++){
		for (int ib=i+1; ib<set6.length; ib++){
			for (int ic=ib+1; ic<set6.length; ic++){
			for (int j=0; j<set9.length; j++){
			
				System.out.print(dI.format(set6[i])+"x"+dI.format(set6[ib])+"x"+dI.format(set6[ic])+"x"+dI.format(set9[j])+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");
			}
		}
		}
	}
	itt += iC*0.2;	
	System.out.println("------- ");
	System.out.println("----- 以上  4x0.2 --------共 "+dF.format(iC*0.2)+" --------------------");

	iC=0;

	for (int i=0; i<set9.length; i++){
		for (int ib=i+1; ib<set9.length; ib++){
			for (int ic=ib+1; ic<set9.length; ic++){
			for (int j=0; j<set6.length; j++){
			
				System.out.print(dI.format(set9[i])+"x"+dI.format(set9[ib])+"x"+dI.format(set9[ic])+"x"+dI.format(set6[j])+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");
			}
		}
		}
	}
	itt += iC*0.2;	
	System.out.println("------- ");
	System.out.println("----- 以上  4x0.2 --------共 "+dF.format(iC*0.2)+" --------------------");
	
	
	iC=0;
	//DecimalFormat dI=new DecimalFormat("00");
	for (int i=0; i<set6.length; i++){
		
			for (int j=i+1; j<set6.length; j++){
				System.out.print(dI.format(set6[i])+"x"+dI.format(set6[j])+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");
			}
		
	}
	itt += iC*0.2;
	
	System.out.println("-------- ");
	System.out.println("---- 以上  3x0.2 ---------共 "+dF.format(iC*0.2)+" --------------------");
	

	iC=0;
	//DecimalFormat dI=new DecimalFormat("00");
	for (int i=0; i<set9.length; i++){
		
			for (int j=i+1; j<set9.length; j++){
				System.out.print(dI.format(set9[i])+"x"+dI.format(set9[j])+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");
			}
		
	}
	itt += iC*0.2;
	
	System.out.println("-------- ");
	System.out.println("---- 以上  3x0.2 ---------共 "+dF.format(iC*0.2)+" --------------------");
	
	iC=0;
	//DecimalFormat dI=new DecimalFormat("00");
	for (int i=0; i<set6.length; i++){
		
			for (int j=0; j<set9.length; j++){
				System.out.print(dI.format(set6[i])+"x"+dI.format(set9[j])+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");
			}
		
	}
	itt += iC*0.2;
	
	System.out.println("-------- ");
	System.out.println("---- 以上  3x0.2 ---------共 "+dF.format(iC*0.2)+" --------------------");
	
	System.out.println("----以下是說明，供參考--------------------");
	System.out.println("---A={12,15,34}, B={9, 19, 29, 8}--------");
	//System.out.println("---D={8, 13, 9, 17, 19, 33, 36, 38}, E={9, 17, 19, 38}------");
	
	System.out.println("--以上分別是-AxAxB，AxBxB，，-----------");
	System.out.println("------------------------");
	System.out.println("--總共: "+dF.format(itt)+"-----------");
	*/
	itt=0;
	
	iC=0;
	//DecimalFormat dI=new DecimalFormat("00");
	for (int i=1; i<40; i++){
		if (i==36 || i==11) continue;
		System.out.print("11x36x"+dI.format(i)+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");		
	}
	System.out.println("-------- ");
	System.out.println("---- 以上  3x0.2 ---------共 "+dF.format(iC*0.2)+" --------------------");
	System.out.println("");
	itt += iC*0.2;
	
	iC=0;
	for (int i=1; i<40; i++){
		if (i==19 || i==11) continue;
		System.out.print("11x19x"+dI.format(i)+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");		
	}
	System.out.println("-------- ");
	System.out.println("---- 以上  3x0.2 ---------共 "+dF.format(iC*0.2)+" --------------------");
	System.out.println("");
	itt += iC*0.2;
	
	iC=0;
	for (int i=1; i<40; i++){
		if (i==20 || i==11) continue;
		System.out.print("11x19x"+dI.format(i)+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");		
	}
	System.out.println("-------- ");
	System.out.println("---- 以上  3x0.2 ---------共 "+dF.format(iC*0.2)+" --------------------");
	System.out.println("");
	itt += iC*0.2;
	
	iC=0;
	for (int i=1; i<40; i++){
		if (i==19 || i==20) continue;
		System.out.print("11x19x"+dI.format(i)+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");		
	}
	System.out.println("-------- ");
	System.out.println("---- 以上  3x0.2 ---------共 "+dF.format(iC*0.2)+" --------------------");
	System.out.println("");
	itt += iC*0.2;
	
	iC=0;
	for (int i=1; i<40; i++){
		if (i==35 || i==21) continue;
		System.out.print("11x19x"+dI.format(i)+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");		
	}
	System.out.println("-------- ");
	System.out.println("---- 以上  3x0.2 ---------共 "+dF.format(iC*0.2)+" --------------------");
	System.out.println("");
	itt += iC*0.2;
	
	iC=0;

	for (int i=1; i<40; i++){
		if (i==19) continue;
		System.out.print("19x"+dI.format(i)+",   ");
				iC++;
				if (iC % 4==0) System.out.println("");		
	}
	System.out.println("-------- ");
	System.out.println("---- 以上  2x0.2 ---------共 "+dF.format(iC*0.2)+" --------------------");
	System.out.println("");
	

	
	itt += iC*0.2;
	
	
	System.out.println("----以下是說明，供參考--------------------");
	System.out.println("---{36x11, 35x21, 11x20, 19x11, 19x20 }--3x0.2 車------");
	//System.out.println("---D={8, 13, 9, 17, 19, 33, 36, 38}, E={9, 17, 19, 38}------");
	
	System.out.println("--以及 19 --- 2x0.2 車，-----------");
	System.out.println("------------------------");
	System.out.println("--總共: "+dF.format(itt)+"-----------");
	
	
}
}