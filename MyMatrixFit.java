import java.sql.Time;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Vector;

import org.ejml.data.D1Matrix64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.MatrixIterator64F;


public class MyMatrixFit {

	MyMatrixFit(int x, int y){
		data=new Vector<double[]>();
		double[] numbers=new double[y];
		Arrays.fill(numbers, 0);
		for (int i=0; i<x; i++){
			data.add(Arrays.copyOf(numbers, y));
		}
		numRows=x;
		numCols=y;
	}
	
	MyMatrixFit(Vector<double[]> data){
		this.data=data;
		numRows=data.size();
		numCols=0;
		for (int i=0; i<numRows; i++){
			if (data.get(i).length > numCols)
				numCols=data.get(i).length;
		}
	}
	
	//@SuppressWarnings("unchecked")
	MyMatrixFit(MyMatrixFit copy){
		//this.data=copy.getData();
		this.numRows=copy.numRows;
		this.numCols=copy.numCols;
		data=new Vector<double[]>();
		for (int x=0; x<numRows; x++){
			data.add(Arrays.copyOf(copy.data.get(x), numCols));
		}				
	}
	/**
     * Where the raw data for the matrix is stored.  The format is type dependent.
     */
    private Vector<double[]> data;
    
   // public Vector<double[]> getData(){
    	//return data;
    //}

    /**
     * Number of rows in the matrix.
     */
    private int numRows;
    /**
     * Number of columns in the matrix.
     */
    private int numCols;

    /**
     * Used to get a reference to the internal data.
     *
     * @return Reference to the matrix's data.
     */
    @SuppressWarnings("unchecked")
	public Vector<double[]> getData() {
        return (Vector<double[]>) data.clone();
    }

	/**
	 * Changes the internal array reference.
	 */
	public void setData( Vector<double[]> data ) {
		this.data = data;
	}

    /**
     * Returns the internal array index for the specified row and column.
     *
     * @param row Row index.
     * @param col Column index.
     * @return Internal array index.
     */
    public MyMatrixFit subMatrix( int row, int col ){
    	Vector<double[]> retMx=new Vector<double[]>();
    	for (int x=row-1; x<numRows; x++){
    		retMx.add(Arrays.copyOfRange(data.get(x), col-1, numCols));
    	}
    	return new MyMatrixFit(retMx);
    };

    public MyMatrixFit subMatrix( int row, int col, int range ){
    	int rowEnd=row-1+range>numRows?numRows:row-1+range;
    	Vector<double[]> retMx=new Vector<double[]>();
    	for (int x=row-1; x<rowEnd; x++){
    		retMx.add(Arrays.copyOfRange(data.get(x), col-1, numCols));
    	}
    	return new MyMatrixFit(retMx);
    };
    
    public MyMatrixFit subOrderMatrix( int row, int col){ 
    	Vector<double[]> retMx=new Vector<double[]>();
    	for (int x=0; x<numRows; x++){
    		if (x==row-1) continue;
    		double[] tmp=Arrays.copyOf(data.get(x), numCols);    		
    		for (int i=col-1; i<numCols-1; i++){
    			tmp[i]=tmp[i+1];
    		}
    		retMx.add(Arrays.copyOf(tmp, numCols-1)); 
    	}
    	return new MyMatrixFit(retMx);
    };
    
    public double determined(MyMatrixFit a){
    	if (a.numCols==1) return a.data.get(0)[0];
    	if (a.numCols==2) return (a.data.get(0)[0]*a.data.get(1)[1] - a.data.get(1)[0]*a.data.get(0)[1]);
    	double retV=0;
    	for (int i=0; i<a.numCols; i++){
    		retV += ((i % 2)==0?1:-1)*a.elementAt(1, i+1)*determined(a.subOrderMatrix(1, i+1));
    	}
    	return retV;
    }
    
    public double determined(){
    	if (numCols==1) return data.get(0)[0];
    	if (numCols==2) return (data.get(0)[0]*data.get(1)[1] - data.get(1)[0]*data.get(0)[1]);
    	double retV=0;
    	MyMatrixFit a=this;
    	for (int i=0; i<a.numCols; i++){
    		retV += ((i % 2)==0?1:-1)*a.elementAt(1, i+1)*determined(a.subOrderMatrix(1, i+1));
    	}
    	return retV;
    }
    
    double elementAt(int x, int y){
    	return data.get(x-1)[y-1];
    }
    /**
     * Sets the element's value at the specified index.  The element at which row and column
     * modified by this function depends upon the matrix's internal structure, e.g. row-major, column-major, or block.
     *
     * @param index Index of element that is to be set.
     * @param val The new value of the index.
     */
    public void update( int x, int y , double val ) {
        // See benchmarkFunctionReturn.  Pointless return does not degrade performance.  Tested on JDK 1.6.0_21
        data.get(x-1)[y-1]= val;
    }

    /**
     * <p>
     * Adds the specified value to the internal data array at the specified index.<br>
     * <br>
     * Equivalent to: this.data[index] += val;
     * </p>
     *
     * <p>
     * Intended for use in highly optimized code.  The  row/column coordinate of the modified element is
     * dependent upon the matrix's internal structure.
     * </p>
     *
     * @param index The index which is being modified.
     * @param val The value that is being added.
     */
    public double plus( int x, int y , double val ) {
        // See benchmarkFunctionReturn.  Pointless return does not degrade performance.  Tested on JDK 1.6.0_21
        return data.get(x-1)[y-1] += val;
    }

    /**
     * <p>
     * Subtracts the specified value to the internal data array at the specified index.<br>
     * <br>
     * Equivalent to: this.data[index] -= val;
     * </p>
     *
     * <p>
     * Intended for use in highly optimized code.  The  row/column coordinate of the modified element is
     * dependent upon the matrix's internal structure.
     * </p>
     *
     * @param index The index which is being modified.
     * @param val The value that is being subtracted.
     */
    public double minus( int x, int y , double val ) {
        // See benchmarkFunctionReturn.  Pointless return does not degrade performance.  Tested on JDK 1.6.0_21
        return data.get(x-1)[y-1] -= val;
    }

    /**
     * <p>
     * Multiplies the specified value to the internal data array at the specified index.<br>
     * <br>
     * Equivalent to: this.data[index] *= val;
     * </p>
     *
     * <p>
     * Intended for use in highly optimized code.  The  row/column coordinate of the modified element is
     * dependent upon the matrix's internal structure.
     * </p>
     *
     * @param index The index which is being modified.
     * @param val The value that is being multiplied.
     */
    public double times(int x, int y , double val ) {
        // See benchmarkFunctionReturn.  Pointless return does not degrade performance.  Tested on JDK 1.6.0_21
        return data.get(x-1)[y-1] *= val;
    }

    /**
     * <p>
     * Divides the specified value to the internal data array at the specified index.<br>
     * <br>
     * Equivalent to: this.data[index] /= val;
     * </p>
     *
     * <p>
     * Intended for use in highly optimized code.  The  row/column coordinate of the modified element is
     * dependent upon the matrix's internal structure.
     * </p>
     *
     * @param index The index which is being modified.
     * @param val The value that is being divided.
     */
    public double div(int x, int y  , double val ) {
        // See benchmarkFunctionReturn.  Pointless return does not degrade performance.  Tested on JDK 1.6.0_21
        return data.get(x-1)[y-1] /= val;
    }

    /**
     * <p>
     * Changes the number of rows and columns in the matrix, allowing its size to grow or shrink.
     * If the saveValues flag is set to true, then the previous values will be maintained, but
     * reassigned to new elements in a row-major ordering.  If saveValues is false values will only
     * be maintained when the requested size is less than or equal to the internal array size.
     * The primary use for this function is to encourage data reuse and avoid unnecessarily declaring
     * and initialization of new memory.
     * </p>
     *
     * <p>
     * Examples:<br>
     * [ 1 2 ; 3 4 ] -> reshape( 2 , 3 , true ) = [ 1 2 3 ; 4 0 0 ]<br>
     * [ 1 2 ; 3 4 ] -> reshape( 1 , 2 , true ) = [ 1 2 ]<br>
     * [ 1 2 ; 3 4 ] -> reshape( 1 , 2 , false ) = [ 1 2 ]<br>
     * [ 1 2 ; 3 4 ] -> reshape( 2 , 3 , false ) = [ 0 0 0 ; 0 0 0 ]
     * </p>
     *
     * @param numRows The new number of rows in the matrix.
     * @param numCols The new number of columns in the matrix.
     * @param saveValues If true then the value of each element will be save using a row-major reordering.  Typically this should be false.
     */
    
    /**
     * Equivalent to invoking reshape(numRows,numCols,false);
     *
     * @param numRows The new number of rows in the matrix.
     * @param numCols The new number of columns in the matrix.
     */
   
    public void reshape( int newRows , int newCols ) {
    	if (newCols != numCols){
    		for (int x=0; x<numRows; x++)
    		data.set(x, Arrays.copyOf(data.get(x), newCols));
    	}
        if (newRows > numRows){
        	double[] toAdd=new double[newCols];
        	Arrays.fill(toAdd, 0);
        	for (int x=numRows; x<newRows; x++){        		
        		data.add(Arrays.copyOf(toAdd, newCols));
        	}
        } else if (newRows < numRows){
        	while (numRows > newRows)
        	{
        		data.remove(numRows-1);
        	}
        }
        numRows=newRows;
        numCols=newCols;
    }

    /**
     * Creates a new iterator for traversing through a submatrix inside this matrix.  It can be traversed
     * by row or by column.  Range of elements is inclusive, e.g. minRow = 0 and maxRow = 1 will include rows
     * 0 and 1.  The iteration starts at (minRow,minCol) and ends at (maxRow,maxCol)
     *
     * @param rowMajor true means it will traverse through the submatrix by row first, false by columns.
     * @param minRow first row it will start at.
     * @param minCol first column it will start at.
     * @param maxRow last row it will stop at.
     * @param maxCol last column it will stop at.
     * @return A new MatrixIterator
     */
    public MatrixIterator64F iterator(boolean rowMajor, int minRow, int minCol, int maxRow, int maxCol)
    {
    	return null;
        //return new MatrixIterator64F(this,rowMajor, minRow, minCol, maxRow, maxCol);
    }
//int[] index2Scale;
    public MyMatrixFit multiply(MyMatrixFit a){
    	Vector<double[]> retV=new Vector<double[]>();
    	//index2Scale=new int[a.numCols];
    	//Arrays.fill(index2Scale, 0);
    	int nsC=0;
    	for (int x=0; x<numRows; x++){   		
    		double[] newRow=new double[a.numCols];
    		Arrays.fill(newRow, 0);
    		for (int y=0; y<a.numCols; y++){
	    		for (int k=0; k<numCols && k<a.numRows; k++){
	    			
	    			newRow[y] += data.get(x)[k]*a.data.get(k)[y];
	    		}
	    		//if (Math.abs(newRow[y])>1000000) index2Scale[x]=y;
	    		//if (Math.abs(newRow[y])<0.000001) index2Scale[x]=y;
    		}
    		retV.add(Arrays.copyOf(newRow, a.numCols));
    	}
    	MyMatrixFit b=new MyMatrixFit(retV);
    	//b.index2Scale=index2Scale;
    	return b;
    }
    
    public MyMatrixFit multiplyBy(MyMatrixFit a){
    	Vector<double[]> retV=new Vector<double[]>();
    	for (int x=0; x<a.numRows; x++){
    		
    		double[] newRow=new double[numCols];
    		Arrays.fill(newRow, 0);
    		
    		for (int col=0; col<numCols; col++) {	
    			for (int y=0; y<a.numCols && y<numRows; y++){
	    			newRow[col] += a.data.get(x)[y]*data.get(y)[col];
	    		}
    		}
    		retV.add(Arrays.copyOf(newRow, numCols));
    	}
    	return new MyMatrixFit(retV);
    }
    
    public double[] multiply(double[] a){
    	int vSize=numRows>a.length?a.length:numRows;
    	double[] newRow=new double[vSize];   	
    	Arrays.fill(newRow, 0);
		for (int row=0; row<vSize; row++) {
    		for (int y=0; y<vSize; y++){   			
    			newRow[row] += data.get(row)[y]*a[y];
    		}
		}
		return newRow;
    }
    
    public MyMatrixFit transpose(){
    	Vector<double[]> retV=new Vector<double[]>();
    	for (int x=0; x<numCols; x++){
    	double[] aLine=new double[numRows];
    		for (int y=0; y<numRows; y++) aLine[y]=data.get(y)[x];
    		retV.add(Arrays.copyOf(aLine, numRows));
    	}
    		return new MyMatrixFit(retV);
    }
    
    static MyMatrixFit getI(int dime){
    	Vector<double[]> retV=new Vector<double[]>();
    	double[] dd=new double[dime];
    	Arrays.fill(dd, 0);
    	for (int i=0; i<dime; i++){
    		dd[i]=1;
    		retV.add(Arrays.copyOf(dd, dime));
    		dd[i]=0;
    	}
    	return new MyMatrixFit(retV);
    }
    
    //MyMatrix inverseOfEche()
    MyMatrixFit scalerMatrix(double[] scales, int dime){
    	//this is an interner process so scales have 0-n
    	Vector<double[]> retV=new Vector<double[]>();
    	double[] dd=new double[dime];
    	Arrays.fill(dd, 0);
    	for (int i=0; i<dime; i++){
    		dd[i]=1;
    		retV.add(Arrays.copyOf(dd, dime));
    		dd[i]=0;
    	}
    	for (int k=0; k<scales.length; k++){
    		if (scales[k] != 0)
    		retV.get(k)[k]=scales[k];
    	}
    	return new MyMatrixFit(retV);
    }
    
    MyMatrixFit scanForScaleMatrix(MyMatrixFit forThis){
    	double[] dd=new double[forThis.numRows];
    	int iFound=0;
    	for (int x=0; x<forThis.numRows; x++){
    		for (int y=0; y<forThis.numCols; y++){
    			double v=Math.abs(forThis.data.get(x)[y]);
    			if (v>1000000 || v<0.000001){
    				dd[x]=1/v;
    				iFound++;
    				break;
    			}
    		}
    	}
    	if (iFound==0) return null;
    	return scalerMatrix(dd, forThis.numRows);
    }
    
    MyMatrixFit forwardElimitor(MyMatrixFit forThis, int whichCol){
    	//MyMatrix scaler=scanForScaleMatrix(inMx);
    	//MyMatrix forThis=scaler.multiply(inMx);
    	//this is an external process so whichCol has 1-n
    	MyMatrixFit retM=getI(forThis.numCols);
    	for (int x=whichCol+1; x<=forThis.numRows; x++){
    		retM.update(x,  x,  forThis.data.get(whichCol-1)[whichCol-1]);
    		retM.update(x,  whichCol,  -forThis.data.get(x-1)[whichCol-1]);
    	}
    	return retM;
    	/*
    	Vector<double[]> retV=new Vector<double[]>();
    	double[] dd=new double[forThis.numCols];
    	Arrays.fill(dd, 0);
    	for (int i=0; i<whichCol; i++){
    		dd[i]=1;
    		retV.add(Arrays.copyOf(dd, forThis.numCols));
    		dd[i]=0;
    	}
    	for (int i=whichCol; i<forThis.numCols; i++){
    		dd[whichCol-1]= forThis.data.get(i)[whichCol-1];
        	dd[i]= -forThis.data.get(whichCol-1)[whichCol-1];    		
    		retV.add(Arrays.copyOf(dd, forThis.numCols));
    		dd[i]=0; 
    		dd[whichCol-1]=0;
    	}   	
    	return new MyMatrix(retV);
    	*/
    }
    
    //the backward still multiply from front
    MyMatrixFit backwardElimitor(MyMatrixFit forThis, int whichCol){
    	double a=forThis.data.get(whichCol-1)[whichCol-1];
    	if (Math.abs(a) < 0.0000001) return null;
    	MyMatrixFit retM=getI(forThis.numCols);
    	for (int i=0; i<whichCol-1; i++){
    		retM.update(i+1, i+1, a);
    		retM.update(i+1, whichCol, -forThis.data.get(i)[whichCol-1]);
    	}    	
    	return (retM);
    }
    
    public MyMatrixFit inverse(){
    	double det=determined();
    	if (Math.abs(det)<0.0000001) return null;
    	Vector<double[]> retV=new Vector<double[]>();
    	double[] aLine=new double[numCols];
    	for (int x=0; x<numRows; x++)
    	{
    		Arrays.fill(aLine, 0);        	
    		for (int y=0; y<numCols; y++){
    			aLine[y]=((x+y)%2==0?1:-1)*determined(subOrderMatrix(y+1, x+1));
    			aLine[y] /= det;
    		}
    		retV.add(Arrays.copyOf(aLine, numCols));
    	}
    	return new MyMatrixFit(retV);
    }
    
    static public MyMatrixFit inverseLU(MyMatrixFit a){
    	MyMatrixFit inverse=getI(a.numCols);
		double factor=1;
		double badDiag=0;
		MyMatrixFit eche=new MyMatrixFit(a);
		for (int i=a.numCols; i>1; i--){
			//eche=b.forwardElimitor(eche, i).multiply(eche);
			double m=eche.data.get(i-1)[i-1];
			if (Math.abs(m)<0.000001){ badDiag=i+1; break;}
				for (int y=0; y<eche.numCols; y++){
					eche.data.get(i-1)[y] /=m;
					inverse.data.get(i-1)[y] /=m;
				}
				
			MyMatrixFit tmp=a.backwardElimitor(eche, i);
			if (tmp==null){
				System.out.println("Failed in "+i+" for 0 diagonal value");
				break;
			}
			eche=tmp.multiply(eche);
			inverse=tmp.multiply(inverse);
		}
		double m=eche.data.get(0)[0];
		if (Math.abs(m)<0.000001){ badDiag *=10; badDiag += 1;}
		else
		for (int y=0; y<eche.numCols; y++){
			eche.data.get(0)[y] /=m;
			inverse.data.get(0)[y] /=m;
		}
		
		for (int i=1; i<a.numCols; i++){
			
			MyMatrixFit tmp=a.forwardElimitor(eche, i);
			if (tmp==null){
				System.out.println("Failed in "+i+" for 0 diagonal value");
				break;
			}
			eche=tmp.multiply(eche);
			inverse=tmp.multiply(inverse);
			
			m=eche.data.get(i)[i];
			if (Math.abs(m)<0.000001)
			{ badDiag *=10; badDiag += (i+1); break;}
			for (int y=0; y<eche.numCols; y++){
				eche.data.get(i)[y] /=m;
				inverse.data.get(i)[y] /=m;
			}				
		}
		if (badDiag > 0) return null;
		return inverse;
    }
    
    static void cleanError(MyMatrixFit a){
    	for (int i=0; i<a.numRows; i++){
    		for (int j=0; j<a.numCols; j++){
    		if (Math.abs(a.data.get(i)[j] - Math.round(a.data.get(i)[j])) < 0.0000001)
    			a.data.get(i)[j]=Math.round(a.data.get(i)[j]);
    		}
    	}
    }
    
    /**
     * {@inheritDoc}
     */
    
    public int getNumRows() {
        return numRows;
    }

    /**
     * {@inheritDoc}
     */
   
    public int getNumCols() {
        return numCols;
    }

//......................... unrelated ...........................
static double[] getCoeff(MyMatrixFit dMtx, int startFrom, int range, int[] refD){
	double[] retC=new double[range];
	for (int i=startFrom; i<startFrom+range; i++){
		retC[i-startFrom]=refD[i];
	}
	
	return dMtx.multiply(retC);
}

static double[] getCoeff(MyMatrixFit dMtx, int startFrom, int range, double[] refD){
	double[] retC=new double[range];
	System.arraycopy(refD, startFrom, retC, 0, range);
	return dMtx.multiply(retC);
}

static double dataPeriod=40;
static double twoPi=(2.0*22/7.0);

static double getPrediction(double x, double[] coeff){
	
	double vars=0;//new double[coeff.length];
	double tryBase=twoPi/dataPeriod;
	for (int i=0; i<coeff.length; i++){
		double rad=x*tryBase*(i+1);
		vars += coeff[i]*Math.sin(rad);
	}
	return vars;//vectorX(vars, coeff);
}

static double[] getSineRow(double x, int len){
	double[] vars=new double[len];
	double tryBase=twoPi/dataPeriod;
	for (int i=0; i<len; i++){
		double rad=x*tryBase*(i+1);
		vars[i]=Math.sin(rad);
	}
	return vars;
}

static MyMatrixFit getSinValuesMatrix(int startFrom, int range){
	
	int k=0;
	Vector<double[]> retV=new Vector<double[]>();
	double tryBase=twoPi/dataPeriod;
	for (int x=startFrom; x<startFrom+range; x++){
		double[] data=new double[range];				
		
		for (int i=1; i<range+1; i++){
			double rad=x*tryBase*i;
			data[i-1]=Math.sin(rad);
		}
		retV.add(data);
	}
	return new MyMatrixFit(retV);
}

static double vectorX(double[] a, double[] b){
	int len=a.length>b.length?b.length:a.length;
	double retV=0;
	for (int i=0; i<len; i++) retV += (a[i]*b[i]);
	return retV;
}



static double[] getAvg(Vector<int[]> refD){
	double[] avg=new double[refD.size()];
	for (int s=0; s<refD.size(); s++){
		avg[s]=0;
		int iC=0;
		for (int i=0; i<refD.get(0).length;i++){
			if (refD.get(s)[i]==0) break;
			avg[s] += refD.get(s)[i];
			iC++;
		}
		avg[s] /= iC;
	}
	return avg;
}

static double[] getDeltaAvg(int from, int range, Vector<int[]> refD){
	double[] avg=new double[refD.size()];
	for (int s=0; s<refD.size(); s++){
		avg[s]=0;
		int iC=0;
		for (int i=from; i<from+range && i<refD.get(s).length;i++){
			if (refD.get(s)[i]==0) break;
			avg[s] += (refD.get(s)[i+1]-refD.get(s)[i]);
			iC++;
		}
		avg[s] /= iC;
	}
	return avg;
}

static DecimalFormat dF=new DecimalFormat("0.0000");
static double[] 
		getDeltaVariant(int from, int range, Vector<int[]> refD){
	double[] avg=new double[refD.size()];
	double[] avgd=new double[refD.size()];
	for (int s=0; s<refD.size(); s++){
		avg[s]=0;
		int iC=0;
		for (int i=from; i<from+range && i<refD.get(s).length;i++){
			if (refD.get(s)[i]==0) break;
			avg[s] += (refD.get(s)[i]);
			iC++;
		}
		avg[s] /= iC;
		avgd[s]=(refD.get(s)[from+range-1]-refD.get(s)[from]);
		avgd[s] /= iC;		
	}
	
	String retText="";
	
	double[] variant=new double[refD.size()];
	for (int s=0; s<refD.size(); s++){
		variant[s]=0;
		double v=0;
		int iC=0;
		for (int i=from; i<from+range && i<refD.get(s).length;i++){
			if (refD.get(s)[i]==0) break;
			double d=refD.get(s)[i]-refD.get(s)[i-1];
			d -= avgd[s];
			d = d*d;
			v += d;
			iC++;
		}
		
		variant[s] = Math.sqrt(v/iC);
		//System.out.println
		retText += ("line "+s+" range "+range+
				" has avg="+dF.format(avg[s])+" and delta avg="+dF.format(avgd[s])+" and R="+dF.format(variant[s])+"\n");
		
	}
	double[] retV=new double[refD.size()*3];
	System.arraycopy(avg, 0, retV, 0, refD.size());
	System.arraycopy(avgd, 0, retV, refD.size(), refD.size());
	System.arraycopy(variant, 0, retV, 2*refD.size(), refD.size());
	return retV;
}

static int findBestAvgFitWithinRange(double[] refV, Vector<int[]> refD)
{
	int iFound=0;
	int iLen=refD.get(0).length;
	double[] R=new double[5];
	
	for (int s=0; s<5; s++){
		String sl="Line ";
		R[s]=0;
		int iC=0;
		for (int i=0; i<iLen; i++){
			if (refD.get(s)[i]==0) break;
			iC++;
			R[s] += (refD.get(s)[i]-refV[s])*(refD.get(s)[i]-refV[s]);
		}
		R[s] /= iC;
		R[s] = Math.sqrt(R[s]);
		System.out.println("Line "+s+" has avg="+refV[s]+" and R="+R[s]);
	}
	for (int k=1; k<10; k++){
		int iRange=k*35;
		for (int s=0; s<5; s++){
		double avg=0;
		int d=iLen-1-iRange;
					
			for (int x=0; x<iRange; x++){
				avg += refD.get(s)[iLen-1-x];
			}
			avg /= iRange;
			
		double r=0;
		for (int x=0; x<iRange; x++){
			r += (refD.get(s)[iLen-1-x]-avg)*(refD.get(s)[iLen-1-x]-avg);
		}
		r=Math.sqrt(r/iRange);
		
		System.out.println("Line "+s+" range "+iRange+" has avg="+avg+" and R="+r);
		}
	}
	
	return iFound;
}

static int[] tryPredictByFitEven(int n_points, int[] refV){
	if (refV.length < n_points){
		System.out.println("too little data "+refV.length+" < "+n_points);
		return null;
	}
	int startFrom=refV.length-n_points;
	int[] retP=new int[40];
	int pred=0;
	double optimW=0;
	double[] optimCoeff=new double[n_points/2+1];
	double optim=99999999;
	int shift=5;
	for (int i=1; i<40; i++){
		for (int w=1; w<17; w++){
			Vector<double[]> mtx=new Vector<double[]>();
			for (int row=0; row<n_points/2+1; row++){
				double x=2*row+shift;
				double[] sin=new double[n_points/2+1];
				for (int y=0; y<n_points/2+1; y++){
					sin[y]=Math.sin((y+1)*w*0.5*x);
				}
				mtx.add(sin);
			}
			MyMatrixFit t=new MyMatrixFit(mtx);
			MyMatrixFit iv=inverseLU(t);
			if (iv == null) continue;
			double[] aV=new double[n_points/2+1];
			for (int ix=0; ix<aV.length-1; ix++)
			{
				aV[ix]=refV[2*ix+startFrom];
			}
			aV[n_points/2]=i;
			double[] coeff=iv.multiply(aV);
			
			double[] reCheck=t.multiply(coeff);
			for (int ic=0; ic<reCheck.length; ic++){
				if (Math.abs(reCheck[ic]-aV[ic])>0.0001)
					System.out.println("Bad matrix found mismatch at "+ic+"; "+reCheck[ic]+" != "+aV[ic]);
			}
			double check=0;
			for (int point=1; point < n_points/2; point++ ){
				double pointV=0;
				double x=2*point-1+shift;//+startFrom;
				for (int ix=0; ix<coeff.length; ix++)
				{
					pointV += Math.sin((ix+1)*w*0.5*(x))*coeff[ix];
				}
				check += Math.abs(pointV-refV[startFrom+2*point-1]);
			}
			if (check < optim){
				optim=check;
				pred=i;
				optimW=w;
				optimCoeff=Arrays.copyOf(coeff, coeff.length);
			}
		}
	}
	if (pred!=0 && optimW !=0){
		double pointV=0;
		for (int ix=0; ix<optimCoeff.length; ix++)
		{
			pointV += Math.sin((ix+1)*optimW*0.5*(startFrom+n_points))*optimCoeff[ix];
		}
		double delta=Math.sqrt(optim/(n_points/2));
		System.out.println("Found pred "+dF.format(pointV)+" with w="+(optimW*0.5)+", deviat="+optim);
		System.out.println("Found predic range "+(pointV-delta)+" to "+(pointV+delta));
	}
	return retP;
}

public static void main(String[] args) {
		// TODO Auto-generated method stub
		//JackPotReader jReaderDrop=JackPotReader.getInstance("test539.txt","開出順序");//
		JackpotReader jReaderOrder=JackpotReader.getInstance("test539.txt","大小順序");//"開出順序");//
		Vector<int[]> sampleData=jReaderOrder.readData(5, 39, 390);
		Vector<int[]> dateData=new Vector<int[]>(); //save data by date ascending
			//globalSample=sampleData;
		int iLen1=sampleData.get(0).length;
		do { if (sampleData.get(0)[iLen1-1] !=0) break; } while (--iLen1>0);
		
		double[] meanPeriod=new double[sampleData.size()];
		Arrays.fill(meanPeriod, 0);
		for (int n=0; n<sampleData.size(); n++){
			int[] tLast=new int[40];
			int[] ttTime=new int[40];
			int[] tCount=new int[40];
			Arrays.fill(tLast, 0);Arrays.fill(ttTime, 0);Arrays.fill(tCount, 0);
			int[] testSeq=new int[iLen1];			
			for (int i=iLen1; i>0; i--){
				testSeq[iLen1-i]=
						sampleData.get(n)[i-1];
				if (tLast[testSeq[iLen1-i]]!=0) {
					ttTime[testSeq[iLen1-i]] += (tLast[testSeq[iLen1-i]] - i);
					tCount[testSeq[iLen1-i]]++;
				}
				tLast[testSeq[iLen1-i]] = i;
			}
			dateData.add(testSeq);
			int iC=0;
			double pp=0;
			for (int i=0; i<40; i++){
				if (ttTime[i]==0) continue;
				iC++;
				pp += (1.0*ttTime[i])/tCount[i];
			}
			meanPeriod[n]=pp/iC;
		}
		for (int t=2; t<5; t++){
			for (int s=0; s<5; s++){
				System.out.println("Try line "+s+" with "+t*6+" points ");
		tryPredictByFitEven(t*6, dateData.get(s));
			}
		}
		
		/*
		for (int d=1; d<60; d++){
			//double[] delta=getDeltaAvg(390-d*39, d*39, dateData);
			
			//String txt
			double[] deltaV=getDeltaVariant(390-d*6, 6, dateData);			
			System.out.println("....for week "+d+"...........");
			for (int s=0; s<dateData.size(); s++){
				System.out.println("line "+s+" has avg="+dF.format(deltaV[s])+
					" and delta avg="+dF.format(deltaV[dateData.size()+s])+
					" and R="+dF.format(deltaV[2*dateData.size()+s]));
			}
			
		}
		*/
		/*
		for (int d=60; d<120; d++){
			//double[] delta=getDeltaAvg(390-d*39, d*39, dateData);
			
			//String txt
			double[] deltaV=getDeltaVariant(390-d, d, dateData);			
			System.out.println("....for range "+d+"...........");
			for (int s=0; s<dateData.size(); s++){
				System.out.println("line "+s+" has avg="+dF.format(deltaV[s])+
					" and delta avg="+dF.format(deltaV[dateData.size()+s])+
					" and R="+dF.format(deltaV[2*dateData.size()+s]));
			}
			
		}
		*/
		/*
		double[] refVariant=getDeltaVariant(1, 389, dateData);
		for (int s=0; s<dateData.size(); s++){
			System.out.println("line "+s+" has avg="+dF.format(refVariant[s])+
				" and delta avg="+dF.format(refVariant[dateData.size()+s])+
				" and R="+dF.format(refVariant[2*dateData.size()+s]));
		}
		*/
		double[] avgs=getAvg(dateData);
		Vector<double[]> meanedData=new Vector<double[]>();
		for (int s=0; s<dateData.size(); s++){
			double[] norm=new double[dateData.get(s).length];
			int[] org=dateData.get(s);
			for (int i=0; i<org.length; i++){
				norm[i]=(1.0*org[i] - avgs[s]);
			}
			meanedData.add(norm);
		}
		//findBestAvgFitWithinRange(avgs, dateData);
		//------------- function test ------
		MyMatrixFit b=new MyMatrixFit(2, 2);
		b.update(1, 1, 1);
		b.update(2, 2, 4);
		b.update(2, 1, 2);
		b.update(1, 2, 3);
		double d=b.determined();
		MyMatrixFit b_1=inverseLU(b);
		
		int dimen=12;
		MyMatrixFit a=new MyMatrixFit(dimen, dimen);
		/*
		a.update(1, 1, 1);
		a.update(2, 2, 4);
		a.update(3, 3, 8);
		
		a.update(2, 1, 2);
		a.update(1, 2, 3);
		a.update(3, 1, 2);
		a.update(1, 3, 3);
		a.update(2, 3, 2);
		a.update(3, 2, 3);
		*/
		for (int i=1; i<dimen+1; i++){
			for (int j=1; j<dimen+1; j++){
				a.update(i, j, 2+Math.round(Math.random()*5));
			}
		}
		//d=a.determined();
		long tNow=System.currentTimeMillis();
		//MyMatrix a_1=a.inverse();
		//System.out.println("Takes :"+(System.currentTimeMillis()-tNow));
		
		//if (Math.abs(d)>0.00001) {
		//MyMatrix m1=a.multiply(a_1);		
			//cleanError(m1);
			//d=m1.determined();
		//}
		
		//a_1=a.forwardElimitor(a, 1).multiply(a);
		/*
		MyMatrix eche=new MyMatrix(a);
		
		tNow=System.currentTimeMillis();
		MyMatrix inverse=getI(a.numCols);
		double factor=1;
		int badDiag=0;
		for (int i=a.numCols; i>1; i--){
			//eche=b.forwardElimitor(eche, i).multiply(eche);
			double m=eche.data.get(i-1)[i-1];
			if (Math.abs(m)<0.000001){ badDiag=i+1; break;}
				for (int y=0; y<eche.numCols; y++){
					eche.data.get(i-1)[y] /=m;
					inverse.data.get(i-1)[y] /=m;
				}
				
			MyMatrix tmp=b.backwardElimitor(eche, i);
			if (tmp==null){
				System.out.println("Failed in "+i+" for 0 diagonal value");
				break;
			}
			eche=tmp.multiply(eche);
			inverse=tmp.multiply(inverse);
		}
		double m=eche.data.get(0)[0];
		if (Math.abs(m)<0.000001){ badDiag *=10; badDiag += 1;}
		else
		for (int y=0; y<eche.numCols; y++){
			eche.data.get(0)[y] /=m;
			inverse.data.get(0)[y] /=m;
		}
		
		for (int i=1; i<a.numCols; i++){
			
			MyMatrix tmp=b.forwardElimitor(eche, i);
			if (tmp==null){
				System.out.println("Failed in "+i+" for 0 diagonal value");
				break;
			}
			eche=tmp.multiply(eche);
			inverse=tmp.multiply(inverse);
			
			m=eche.data.get(i)[i];
			if (Math.abs(m)<0.000001)
			{ badDiag *=10; badDiag += (i+1); break;}
			for (int y=0; y<eche.numCols; y++){
				eche.data.get(i)[y] /=m;
				inverse.data.get(i)[y] /=m;
			}				
		}
		System.out.println("Diag Takes :"+(System.currentTimeMillis()-tNow));
		*/
		/*
		 * m=eche.data.get(a.numCols-1)[a.numCols-1];
		
		if (Math.abs(m)<0.000001){ badDiag *=10; badDiag += a.numCols;}
		else
		for (int y=0; y<eche.numCols; y++){
			eche.data.get(a.numCols-1)[y] /=m;
			inverse.data.get(a.numCols-1)[y] /=m;
		}
		 */
		
		//for (int x=0; x<a.numRows; x++){
			//for (int y=0; y<a.numCols; y++){
				//if (Math.abs(a_1.data.get(x)[y] - inverse.data.get(x)[y])>0.00001)
					//System.out.println("Bad data at("+x+", "+y+")");
			//}
		//}
		//tNow=System.currentTimeMillis();
		//MyMatrix iCheck=a.multiply(inverse);
		//MyMatrix rCheck=a.multiply(a_1);
		//MyMatrix aCheck=inverse.multiply(a);
		int tryPeriods=13;
		int iRange_B=13;
		int[] goodDelta=new int[tryPeriods+1];
		int[] goodRange=new int[iRange_B];
		int[] goodNumber=new int[40];
		for (int delta=-2; delta<3; delta++){
			
			
		for (int range=6; range < iRange_B; range++){
			for (int n=0; n<meanedData.size(); n++){ 
			
				double testPeriod=meanPeriod[n]/4;
				dataPeriod = meanPeriod[n]+testPeriod*delta;
			double angleBase=twoPi/dataPeriod;
					
	       MyMatrixFit test39=getSinValuesMatrix(iLen1-range+1, range);
	       //tNow=System.currentTimeMillis();
	       //d=test39.determined();
	       MyMatrixFit work39=inverseLU(test39);
	       if (work39 == null){
	    	   System.out.println("Range "+range+" has no solution!! ");//waste time:"+(System.currentTimeMillis()-tNow)/1000);
	    	   continue;
	    	   //System.exit(1);;
	       }
	       /*
	       MyMatrix unitM=test39.multiply(work39);
	       for (int s=0; s<unitM.numRows; s++){
	    	   for (int y=0; y<unitM.numCols; y++){
	    		   if (s==y && Math.abs(unitM.data.get(s)[y]-1) > 0.0001 ||
	    				   s !=y && Math.abs(unitM.data.get(s)[y]) > 0.001)
	    				   {
	    			   System.out.println("Bad inverse ("+s+", "+y+") not fit");
	    			   continue;
	    			   //System.exit(1);
	    				   }
	    	   }
	    		   
	       }
	       */
	        //CommonOps.invert(work39);
	        //CommonOps.invert(test39, work39);
	       
	    	   double[] testSeq=meanedData.get(n);
	    	   double[] deviate=new double[40];
	    	   double fMin=10000;
	    	   int iAt=0;
	    	   double[] trySeq=new double[range];
    		   for (int i=0; i<range-1; i++){
    			   trySeq[i]=testSeq[iLen1-range+i];
    		   }
	    	   for (int testV=1+n; testV<36+n; testV++){
	    		   
	    		   trySeq[range-1]=(testV-avgs[n]);
	    	   double[] coeff=getCoeff(work39, 0, range, trySeq);//iLen1-range, range, testSeq);
	        //test39=getSinValuesMatrix(iLen1-range+1, range);
	    	   double half=(trySeq[range-2]+trySeq[range-3])/2;
	    	   double halfX=(iLen1-1 + iLen1-2)/2;
	    	   double previous=iLen1-range;
	    	   		//d=Math.abs(getPrediction(previous, coeff)- testSeq[iLen1-range]);//halfX, coeff)-half;
	    	   		d=Math.abs(getPrediction(halfX, coeff)-half);
	    	   		//previous--;
	    	   		//d += Math.abs(getPrediction(previous, coeff)- testSeq[iLen1-range-1]);
	    	   		deviate[testV]=Math.abs(d);
	    	   		if (fMin > deviate[testV]){
	    	   			iAt=testV;
	    	   			fMin = deviate[testV];
	    	   		}
	    	   }
	    	   if (fMin < 0.6){
	    	   System.out.println("Range "+range+" "+dF.format(dataPeriod/meanPeriod[n])+"T; prediction :("+iAt+") for line "+n+" [fit:"+fMin);
	    	   //goodDelta[delta]++;
	    	   //goodRange[range]++;
	    	   goodNumber[iAt]++;
	    	   }
	       } 
	  
		}
	        
	
		String sDelta="predictions:(";
		for (int i=0; i<goodNumber.length; i++){
			if (goodNumber[i] > 0)
			sDelta += (""+i+"["+goodNumber[i]+"],");
			goodNumber[i]=0;
		}
		System.out.println(sDelta);
		System.out.println("..............");
	}
}

}
