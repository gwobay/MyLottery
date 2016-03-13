/*
 * Copyright (c) 2009-2013, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Efficient Java Matrix Library (EJML).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Vector;

import org.ejml.alg.dense.decomposition.lu.LUDecompositionAlt_D64;
import org.ejml.alg.fixed.FixedOps4;
import org.ejml.data.DenseMatrix64F;
import org.ejml.data.FixedMatrix4_64F;
import org.ejml.data.FixedMatrix4x4_64F;
import org.ejml.ops.ConvertMatrixType;
import org.ejml.simple.SimpleMatrix;

/**
 * In some applications a small fixed sized matrix can speed things up a lot, e.g. 8 times faster.  One application
 * which uses small matrices is graphics and rigid body motion, which extensively uses 4x4 and 4x4 matrices.  This
 * example is to show some examples of how you can use a fixed sized matrix.
 *
 * @author Peter Abeles
 */
public class ExampleFixedSizedMatrix {

	static int[] reverseArray(int[] inData){
		int dataLength=inData.length;
		while (inData[--dataLength]==0);
		int[] retData=new int[++dataLength];
		int i=0;
		while (i< dataLength){
			retData[i]=inData[dataLength-1-i];
			i++;
		}
		return retData;
	}
	static Vector<int[]> globalSample=null;
	static Vector<double[]> curveParameters=new Vector<double[]>();
	
	public static void main(String[] args)
	{
			JackpotReader jReader=JackpotReader.getInstance("test539.txt","大小順序");
			Vector<int[]> sampleData=jReader.readData(5, 39, 300);	
			globalSample=sampleData;
		
			int points2Fit=3;
			
        // declare the matrix
        FixedMatrix4x4_64F a = new FixedMatrix4x4_64F();
        FixedMatrix4x4_64F b = new FixedMatrix4x4_64F();

        //working on polynomial a2*x^3+a1*X^2+a0*X+c at [p0, p1, p2], [p2, p3, p4], [p4, p5, p6], ...; 
        //                                           along with the derivatives of p0, p2, p4, p6, ....
        // Can assign values the usual way
        for (int iC=0; iC<globalSample.size(); iC++){
        	int[] curve=reverseArray(globalSample.get(iC));
        	double firstDerivative0=curve[1]-curve[0];
        	double[] errorPrediction=new double[curve.length];			
	        for (int k=0; k<curve.length-3; k += 2){
		        for( int i = 0; i < points2Fit; i++ ) {
		            for( int j = 0; j < points2Fit ; j++ ) {
		                a.set(i,j,Math.pow(i+1, points2Fit-j));
		            }
		            a.set(i,points2Fit , 1);
		        }
		        a.set(points2Fit, 0 , 3);
		        a.set(points2Fit, 1 , 2);
		        a.set(points2Fit, 2 , 1);
		        a.set(points2Fit, points2Fit , 0);
		        //int ik=320-k;
		        FixedMatrix4_64F v = new FixedMatrix4_64F(curve[k], curve[k+1], curve[k+2], firstDerivative0);
		        FixedMatrix4_64F result = new FixedMatrix4_64F();
		        FixedMatrix4_64F invM = new FixedMatrix4_64F();
		        FixedOps4.invert(a,b);
		        FixedOps4.mult(b,v,result);
		        firstDerivative0=3*result.a1*Math.pow(points2Fit, 2)+2*result.a2*points2Fit+result.a3;
		        double f1=result.a1*Math.pow(1, 3)+result.a2*Math.pow(1, 2)+result.a3+result.a4;
		        double f2=result.a1*Math.pow(2, 3)+result.a2*Math.pow(2, 2)+result.a3*2+result.a4;
		        double f3=result.a1*Math.pow(3, 3)+result.a2*Math.pow(3, 2)+result.a3*3+result.a4;
		        double est=result.a1*Math.pow((points2Fit+1), points2Fit)+
		        		result.a2*Math.pow((points2Fit+1), points2Fit-1)+
		        		result.a3*Math.pow((points2Fit+1), points2Fit-2)+
		        		result.a4;
		        errorPrediction[k]=est-curve[k+3];	
		        
	        }
	        System.out.println("ok");
	        
        }

        // Direct manipulation of each value is the fastest way to assign/read values
        a.a11 = 12;
        a.a23 = 64;

        // can print the usual way too
        a.print();

        // most of the standard operations are support
        FixedOps4.transpose(a,b);
        b.print();

        System.out.println("Determinant = "+FixedOps4.det(a));

        // matrix-vector operations are also supported
        // Constructors for vectors and matrices can be used to initialize its value
        FixedMatrix4_64F v = new FixedMatrix4_64F(1,2,3, 4);
        FixedMatrix4_64F result = new FixedMatrix4_64F();

        FixedOps4.mult(a,v,result);

        // Conversion into DenseMatrix64F can also be done
        DenseMatrix64F dM = ConvertMatrixType.convert(a,null);

        dM.print();

        // This can be useful if you need do more advanced operations
        SimpleMatrix sv = SimpleMatrix.wrap(dM).svd().getV();

        // can then convert it back into a fixed matrix
        FixedMatrix4x4_64F fv = ConvertMatrixType.convert(sv.getMatrix(),(FixedMatrix4x4_64F)null);

        System.out.println("Original simple matrix and converted fixed matrix");
        sv.print();
        fv.print();
        
    }
}
