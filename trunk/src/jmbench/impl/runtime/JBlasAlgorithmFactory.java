/*
 * Copyright (c) 2009-2010, Peter Abeles. All Rights Reserved.
 *
 * This file is part of JMatrixBenchmark.
 *
 * JMatrixBenchmark is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * JMatrixBenchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JMatrixBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 */

package jmbench.impl.runtime;

import jmbench.impl.MatrixLibrary;
import jmbench.interfaces.AlgorithmInterface;
import jmbench.interfaces.RuntimePerformanceFactory;
import jmbench.tools.runtime.generator.ScaleGenerator;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.jblas.Decompose;
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;
import org.jblas.Solve;


/**
 * @author Peter Abeles
 */
public class JBlasAlgorithmFactory implements RuntimePerformanceFactory {

    private static abstract class MyInterface implements AlgorithmInterface
    {
        @Override
        public String getName() {
            return MatrixLibrary.JBLAS.getVersionName();
        }
    }

    @Override
    public AlgorithmInterface chol() {
        return new Chol();
    }

    public static class Chol extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix matA = convertToJBlas(inputs[0]);

            DoubleMatrix U = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                U = Decompose.cholesky(matA);
            }

            long elapsed = System.currentTimeMillis()-prev;
            outputs[0] = jblasToEjml(U);
            CommonOps.transpose(outputs[0]);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface lu() {
        return new LU();
    }

    public static class LU extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix matA = convertToJBlas(inputs[0]);

            DoubleMatrix L = null;
            DoubleMatrix U = null;
            DoubleMatrix P = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                Decompose.LUDecomposition<DoubleMatrix> lu = Decompose.lu(matA);
                L = lu.l;
                U = lu.u;
                P = lu.p;
            }

            long elapsed = System.currentTimeMillis()-prev;
            outputs[0] = jblasToEjml(L);
            outputs[1] = jblasToEjml(U);
            outputs[2] = jblasToEjml(P);
            CommonOps.transpose(outputs[2]);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface svd() {
        return null;
    }


    @Override
    public AlgorithmInterface eigSymm() {
        return new MyEig();
    }

    public static class MyEig extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix matA = convertToJBlas(inputs[0]);

            DoubleMatrix D = null;
            DoubleMatrix V = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                DoubleMatrix[] evd = Eigen.symmetricEigenvectors(matA);
                D = evd[1];
                V = evd[0];
            }

            long elapsed = System.currentTimeMillis()-prev;
            outputs[0] = jblasToEjml(D);
            outputs[1] = jblasToEjml(V);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface qr() {
        return null;
    }

    @Override
    public AlgorithmInterface det() {
        return null;
    }

    @Override
    public AlgorithmInterface invert() {
        return new Inv();
    }

    public static class Inv extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix matA = convertToJBlas(inputs[0]);

            DoubleMatrix I = DoubleMatrix.eye(matA.getRows());
            DoubleMatrix result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                result = Solve.solve(matA,I);
            }

            long elapsed = System.currentTimeMillis()-prev;
            outputs[0] = jblasToEjml(result);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface invertSymmPosDef() {
        return new InvSymmPosDef();
    }

    public static class InvSymmPosDef extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix matA = convertToJBlas(inputs[0]);

            DoubleMatrix I = DoubleMatrix.eye(matA.getRows());
            DoubleMatrix result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                result = Solve.solveSymmetric(matA,I);
            }

            long elapsed = System.currentTimeMillis()-prev;
            outputs[0] = jblasToEjml(result);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface add() {
        return new Add();
    }

    public static class Add extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix matA = convertToJBlas(inputs[0]);
            DoubleMatrix matB = convertToJBlas(inputs[1]);

            DoubleMatrix result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                result = matA.add(matB);
            }

            long elapsed = System.currentTimeMillis()-prev;
            outputs[0] = jblasToEjml(result);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface mult() {
        return new Mult();
    }

    public static class Mult extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix matA = convertToJBlas(inputs[0]);
            DoubleMatrix matB = convertToJBlas(inputs[1]);

            long prev = System.currentTimeMillis();

            DoubleMatrix result = null;

            for( long i = 0; i < numTrials; i++ ) {
                result = matA.mmul(matB);
            }

            long elapsed = System.currentTimeMillis()-prev;
            outputs[0] = jblasToEjml(result);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface multTransA() {
        return new MulTranA();
    }

    public static class MulTranA extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix matA = convertToJBlas(inputs[0]);
            DoubleMatrix matB = convertToJBlas(inputs[1]);

            DoubleMatrix result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                result = matA.transpose().mmul(matB);
            }

            long elapsed = System.currentTimeMillis()-prev;
            outputs[0] = jblasToEjml(result);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface scale() {
        return new Scale();
    }

    public static class Scale extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix matA = convertToJBlas(inputs[0]);

            DoubleMatrix result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                result = matA.mul(ScaleGenerator.SCALE);
            }

            long elapsed = System.currentTimeMillis()-prev;
            outputs[0] = jblasToEjml(result);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface solveExact() {
        return new MySolve();
    }

    @Override
    public AlgorithmInterface solveOver() {
        return null;
    }

    public static class MySolve extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix matA = convertToJBlas(inputs[0]);
            DoubleMatrix matB = convertToJBlas(inputs[1]);

            DoubleMatrix result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                result = Solve.solve(matA,matB);
            }

            long elapsed = System.currentTimeMillis()-prev;
            outputs[0] = jblasToEjml(result);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface transpose() {
        return new Transpose();
    }

    public static class Transpose extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix matA = convertToJBlas(inputs[0]);

            DoubleMatrix result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                result = matA.transpose();
            }

            long elapsed = System.currentTimeMillis()-prev;
            outputs[0] = jblasToEjml(result);
            return elapsed;
        }
    }

    public static DoubleMatrix convertToJBlas( DenseMatrix64F orig )
    {
        DoubleMatrix ret = new DoubleMatrix(orig.getNumRows(),orig.getNumCols());

        for( int i = 0; i < orig.numRows; i++ ) {
            for( int j = 0; j < orig.numCols; j++ ) {
                ret.put(i,j,orig.get(i,j)) ;
            }
        }

        return ret;
    }

    public static DenseMatrix64F jblasToEjml( DoubleMatrix orig )
    {
        if( orig == null )
            return null;

        DenseMatrix64F ret = new DenseMatrix64F(orig.getRows(),orig.getColumns());

        for( int i = 0; i < ret.numRows; i++ ) {
            for( int j = 0; j < ret.numCols; j++ ) {
                ret.set(i,j,orig.get(i,j));
            }
        }

        return ret;
    }
}