import java.sql.Time;
import java.util.Arrays;
import java.util.Vector;

import org.ejml.data.D1Matrix64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.MatrixIterator64F;


public class JMatrix {

	JMatrix(int x, int y){
		data=new Vector<double[]>();
		double[] numbers=new double[y];
		Arrays.fill(numbers, 0);
		for (int i=0; i<x; i++){
			data.add(Arrays.copyOf(numbers, y));
		}
		numRows=x;
		numCols=y;
	}
	
	JMatrix(Vector<double[]> data){
		this.data=data;
		numRows=data.size();
		numCols=0;
		for (int i=0; i<numRows; i++){
			if (data.get(i).length > numCols)
				numCols=data.get(i).length;
		}
	}
	
	//@SuppressWarnings("unchecked")
	JMatrix(JMatrix copy){
		this.data=copy.getData();
		this.numRows=copy.numRows;
		this.numCols=copy.numCols;
	}
	/**
     * Where the raw data for the matrix is stored.  The format is type dependent.
     */
    private Vector<double[]> data;

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
        return (Vector<double[]>)data.clone();
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
    public JMatrix subMatrix( int row, int col ){
    	Vector<double[]> retMx=new Vector<double[]>();
    	for (int x=row-1; x<numRows; x++){
    		retMx.add(Arrays.copyOfRange(data.get(x), col-1, numCols));
    	}
    	return new JMatrix(retMx);
    };

    public JMatrix subMatrix( int row, int col, int range ){
    	int rowEnd=row-1+range>numRows?numRows:row-1+range;
    	Vector<double[]> retMx=new Vector<double[]>();
    	for (int x=row-1; x<rowEnd; x++){
    		retMx.add(Arrays.copyOfRange(data.get(x), col-1, numCols));
    	}
    	return new JMatrix(retMx);
    };
    
    public JMatrix subOrderMatrix( int row, int col){ 
    	Vector<double[]> retMx=new Vector<double[]>();
    	for (int x=0; x<numRows; x++){
    		if (x==row-1) continue;
    		double[] tmp=Arrays.copyOf(data.get(x), numCols);    		
    		for (int i=col-1; i<numCols-1; i++){
    			tmp[i]=tmp[i+1];
    		}
    		retMx.add(Arrays.copyOf(tmp, numCols-1)); 
    	}
    	return new JMatrix(retMx);
    };
    
    public double determined(JMatrix a){
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
    	JMatrix a=this;
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
int[] index2Scale;
    public JMatrix multiply(JMatrix a){
    	Vector<double[]> retV=new Vector<double[]>();
    	index2Scale=new int[a.numCols];
    	Arrays.fill(index2Scale, 0);
    	int nsC=0;
    	for (int x=0; x<numRows; x++){   		
    		double[] newRow=new double[a.numCols];
    		Arrays.fill(newRow, 0);
    		for (int y=0; y<a.numCols; y++){
	    		for (int k=0; k<numCols && k<a.numRows; k++){
	    			
	    			newRow[y] += data.get(x)[k]*a.data.get(k)[y];
	    		}
	    		if (Math.abs(newRow[y])>1000000) index2Scale[x]=y;
	    		if (Math.abs(newRow[y])<0.000001) index2Scale[x]=y;
    		}
    		retV.add(Arrays.copyOf(newRow, a.numCols));
    	}
    	JMatrix b=new JMatrix(retV);
    	b.index2Scale=index2Scale;
    	return b;
    }
    
    public JMatrix multiplyBy(JMatrix a){
    	Vector<double[]> retV=new Vector<double[]>();
    	for (int x=0; x<a.numRows; x++){
    		
    		double[] newRow=new double[numCols];
    		Arrays.fill(newRow, 0);
    		
    		for (int col=0; col<numCols; col++) {	
    			for (int y=0; y<a.numCols && y<numRows; y++){
	    			newRow[col] += a.data.get(x)[y]*data.get(col)[y];
	    		}
    		}
    		retV.add(Arrays.copyOf(newRow, numCols));
    	}
    	return new JMatrix(retV);
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
    
    public JMatrix transpose(){
    	Vector<double[]> retV=new Vector<double[]>();
    	for (int x=0; x<numCols; x++){
    	double[] aLine=new double[numRows];
    		for (int y=0; y<numRows; y++) aLine[y]=data.get(y)[x];
    		retV.add(Arrays.copyOf(aLine, numRows));
    	}
    		return new JMatrix(retV);
    }
    
    JMatrix getI(int dime){
    	Vector<double[]> retV=new Vector<double[]>();
    	double[] dd=new double[dime];
    	Arrays.fill(dd, 0);
    	for (int i=0; i<dime; i++){
    		dd[i]=1;
    		retV.add(Arrays.copyOf(dd, dime));
    		dd[i]=0;
    	}
    	return new JMatrix(retV);
    }
    
    JMatrix scalerMatrix(double scale, int forLine, int dime){
    	if (Math.abs(scale)<0.0000001){
    		return null;
    	}
    	Vector<double[]> retV=new Vector<double[]>();
    	double[] dd=new double[dime];
    	Arrays.fill(dd, 0);
    	for (int i=0; i<dime; i++){
    		dd[i]=1;
    		retV.add(Arrays.copyOf(dd, dime));
    		dd[i]=0;
    	}
    	retV.get(forLine)[forLine]=scale;
    	return new JMatrix(retV);
    }
    
    JMatrix forwardElimitor(JMatrix forThis, int whichCol){
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
    	return new JMatrix(retV);
    }
    
    //the backward still multiply from front
    JMatrix backwardElimitor(JMatrix forThis, int whichCol){
    	double a=forThis.data.get(whichCol-1)[whichCol-1];
    	if (Math.abs(a) < 0.0000001) return null;
    	JMatrix retM=getI(forThis.numCols);
    	for (int i=0; i<whichCol-1; i++){
    		retM.update(i+1, i+1, a);
    		retM.update(i+1, forThis.numCols, -forThis.data.get(i)[whichCol-1]);
    	}    	
    	return new JMatrix(retM);
    }
    
    public JMatrix inverse(){
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
    	return new JMatrix(retV);
    }
    
    static void cleanError(JMatrix a){
    	for (int i=0; i<a.numRows; i++){
    		for (int j=0; j<a.numCols; j++){
    		if (Math.abs(a.data.get(i)[j]) < 0.0000001)
    			a.data.get(i)[j]=0;
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
static double[] getCoeff(JMatrix dMtx, int startFrom, int range, int[] refD){
	double[] retC=new double[range];
	for (int i=startFrom; i<startFrom+range; i++){
		retC[i-startFrom]=refD[i];
	}
	
	return dMtx.multiply(retC);
}

static double[] getCoeff(JMatrix dMtx, int startFrom, int range, double[] refD){
	double[] retC=new double[range];
	System.arraycopy(refD, startFrom, retC, 0, range);
	return dMtx.multiply(retC);
}

static double periodBase=(2.0*22/7.0);///40;
static JMatrix getSinValuesMatrix(int startFrom, int range){
	
	int k=0;
	Vector<double[]> retV=new Vector<double[]>();
	
	for (int x=startFrom; x<startFrom+range; x++){
		double[] data=new double[range];				
		
		for (int i=1; i<range+1; i++){
			double rad=x*periodBase/i;
			data[i-1]=Math.sin(rad);
		}
		retV.add(data);
	}
	return new JMatrix(retV);
}

static double vectorX(double[] a, double[] b){
	int len=a.length>b.length?b.length:a.length;
	double retV=0;
	for (int i=0; i<len; i++) retV += (a[i]*b[i]);
	return retV;
}

static double getPrediction(double x, double[] coeff){
	double[] vars=new double[coeff.length];
	for (int i=0; i<coeff.length; i++){
		double rad=x*periodBase/(i+1);
		vars[i]=Math.sin(rad);
	}
	return vectorX(vars, coeff);
}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JackpotReader jReaderDrop=JackpotReader.getInstance("test539.txt","開出順序");//
		JackpotReader jReaderOrder=JackpotReader.getInstance("test539.txt","大小順序");//"開出順序");//
		Vector<int[]> sampleData=jReaderOrder.readData(5, 39, 330);
		Vector<int[]> dateData=new Vector<int[]>(); //save data by date ascending
			//globalSample=sampleData;
		int iLen1=sampleData.get(0).length;
		for (int n=0; n<sampleData.size(); n++){
			int[] testSeq=new int[iLen1];			
			for (int i=iLen1; i>0; i--){
				testSeq[iLen1-i]=
						sampleData.get(n)[i-1];
			}
			dateData.add(testSeq);
		}
		
		//------------- function test ------
		JMatrix b=new JMatrix(2, 2);
		b.update(1, 1, 1);
		b.update(2, 2, 4);
		b.update(2, 1, 2);
		b.update(1, 2, 3);
		double d=b.determined();
		JMatrix b_1=b.inverse();
		
		JMatrix a=new JMatrix(7, 7);
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
		for (int i=1; i<8; i++){
			for (int j=1; j<8; j++){
				a.update(i, j, Math.round(Math.random()*5));
			}
		}
		d=a.determined();
		JMatrix a_1=a.inverse();
		JMatrix m1;
		if (Math.abs(d)>0.0001) {
			m1=a.multiply(a_1);		
			cleanError(m1);
		}
		
		//a_1=a.forwardElimitor(a, 1).multiply(a);
		JMatrix eche=new JMatrix(a);
		for (int i=7; i>0; i--){
			//eche=b.forwardElimitor(eche, i).multiply(eche);
			JMatrix tmp=b.backwardElimitor(eche, i);
			if (tmp==null){
				System.out.println("Failed in "+i+" for 0 diagonal value");
				break;
			}
			eche=tmp.multiply(eche);
		}
		
		for (int range=5; range < 11; range++){
		
	       JMatrix test39=getSinValuesMatrix(iLen1-range, range);
	       long tNow=System.currentTimeMillis();
	       //d=test39.determined();
	       JMatrix work39=test39.inverse();
	       if (work39 == null){
	    	   System.out.println("Range "+range+" has no solution!! waste time:"+(System.currentTimeMillis()-tNow)/1000);
	    	   continue;
	    	   //System.exit(1);;
	       }
	       m1=test39.multiply(work39);
	        //CommonOps.invert(work39);
	        //CommonOps.invert(test39, work39);
	       for (int n=0; n<dateData.size(); n++){ 
	    	   int[] testSeq=dateData.get(n);
	    	   double[] coeff=getCoeff(work39, iLen1-range, range, testSeq);
	        //test39=getSinValuesMatrix(iLen1-range+1, range);
	    	   d=getPrediction(iLen1, coeff);
	    	   long d1=(Math.round(d) % 39);
	    	   if (d1==0) d1=39;
	    	   System.out.println("Range "+range+" Found prediction :("+d+"/"+d1+") for line "+n);
	       //double d1=getPrediction(testSeq[iLen1-1], coeff);
	    	   double[] reCheck=getCoeff(test39, 0, range, coeff);
	      
	        for (int i=0; i<range; i++){
	        	reCheck[i] -= testSeq[iLen1-range+i];
	        	if (Math.abs(reCheck[i]) > 0.000001){
	        		System.out.println("bad fit :"+reCheck[i]);
	        	}
	        }
	       }
		}
	        

	}

}
