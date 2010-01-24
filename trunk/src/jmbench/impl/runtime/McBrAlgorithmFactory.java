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
import jmbench.interfaces.LibraryAlgorithmFactory;
import org.apache.commons.math.linear.*;
import org.apache.commons.math.util.MathUtils;
import org.ejml.data.DenseMatrix64F;


/**
 * @author Peter Abeles
 */
public class McBrAlgorithmFactory implements LibraryAlgorithmFactory {

    private static abstract class MyInterface implements AlgorithmInterface
    {
        @Override
        public String getName() {
            return MatrixLibrary.CM.getVersionName();
        }
    }

    @Override
    public AlgorithmInterface chol() {
        return new Chol();
    }

    public static class Chol extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            BlockRealMatrix matA = convertToBlockReal(inputs[0]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                try {
                    new CholeskyDecompositionImpl(matA);
                } catch (NotSymmetricMatrixException e) {
                    throw new RuntimeException(e);
                } catch (NotPositiveDefiniteMatrixException e) {
                    throw new RuntimeException(e);
                }
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface lu() {
        return new LU();
    }

    public static class LU extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            BlockRealMatrix matA = convertToBlockReal(inputs[0]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                new LUDecompositionImpl(matA);
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface svd() {
        return new SVD();
    }

    public static class SVD extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            BlockRealMatrix matA = convertToBlockReal(inputs[0]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                SingularValueDecomposition svd = new SingularValueDecompositionImpl(matA);
                // need to call this functions so that it performs the full decomposition
                svd.getU();
                svd.getS();
                svd.getV();
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface eigSymm() {
        return new Eig();
    }

    public static class Eig extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            BlockRealMatrix matA = convertToBlockReal(inputs[0]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                EigenDecompositionImpl eig = new EigenDecompositionImpl(matA, MathUtils.SAFE_MIN);
                // need to do this so that it computes the complete eigen vector
                eig.getV();
                eig.getD();
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface qr() {
        return new QR();
    }

    public static class QR extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            BlockRealMatrix matA = convertToBlockReal(inputs[0]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                new QRDecompositionImpl(matA);
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface det() {
        return new Det();
    }

    public static class Det extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            BlockRealMatrix matA = convertToBlockReal(inputs[0]);

            long prev = System.currentTimeMillis();

            // LU decompose is a bit of a mess because of all the depreciated stuff everywhere
            // I believe this is the way the designers want you to do it
            for( long i = 0; i < numTrials; i++ ) {
                LUDecomposition lu = new LUDecompositionImpl(matA);
                lu.getDeterminant();
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface invert() {
        return new Inv();
    }

    public static class Inv extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            BlockRealMatrix matA = convertToBlockReal(inputs[0]);

            long prev = System.currentTimeMillis();

            // LU decompose is a bit of a mess because of all the depreciated stuff everywhere
            // I believe this is the way the designers want you to do it
            for( long i = 0; i < numTrials; i++ ) {
                LUDecomposition lu = new LUDecompositionImpl(matA);
                lu.getSolver().getInverse();
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface add() {
        return new Add();
    }

    public static class Add extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            BlockRealMatrix matA = convertToBlockReal(inputs[0]);
            BlockRealMatrix matB = convertToBlockReal(inputs[1]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                matA.add(matB);
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface mult() {
        return new Mult();
    }

    public static class Mult extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            BlockRealMatrix matA = convertToBlockReal(inputs[0]);
            BlockRealMatrix matB = convertToBlockReal(inputs[1]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                matA.multiply(matB);
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface multTransA() {
        return new MulTranA();
    }

    public static class MulTranA extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            BlockRealMatrix matA = convertToBlockReal(inputs[0]);
            BlockRealMatrix matB = convertToBlockReal(inputs[1]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                matA.transpose().multiply(matB);
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface scale() {
        return new Scale();
    }

    public static class Scale extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            BlockRealMatrix matA = convertToBlockReal(inputs[0]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                matA.scalarMultiply(2.5);
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface solveExact() {
        return new SolveExact();
    }

    @Override
    public AlgorithmInterface solveOver() {
        return new SolveOver();
    }

    public static class SolveExact extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            BlockRealMatrix matA = convertToBlockReal(inputs[0]);
            BlockRealMatrix matB = convertToBlockReal(inputs[1]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                LUDecomposition lu = new LUDecompositionImpl(matA);
                lu.getSolver().solve(matB);
            }

            return System.currentTimeMillis()-prev;
        }
    }

    public static class SolveOver extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            BlockRealMatrix matA = convertToBlockReal(inputs[0]);
            BlockRealMatrix matB = convertToBlockReal(inputs[1]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                QRDecomposition qr = new QRDecompositionImpl(matA);
                qr.getSolver().solve(matB);
            }
            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface transpose() {
        return new Transpose();
    }

    public static class Transpose extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            BlockRealMatrix matA = convertToBlockReal(inputs[0]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                matA.transpose();
            }

            return System.currentTimeMillis()-prev;
        }
    }
    
    /**
     * Converts DenseMatrix64F used in EML into a BlockRealMatrix found in commons-math.
     *
     * @param orig A DenseMatrix64F in EML
     * @return A BlockRealMatrix in CommonsMath
     */
    public static BlockRealMatrix convertToBlockReal( DenseMatrix64F orig )
    {
        double [][]mat = new double[ orig.numRows ][ orig.numCols ];

        for( int i = 0; i < orig.numRows; i++ ) {
            for( int j = 0; j < orig.numCols; j++ ) {
                mat[i][j] = orig.get(i,j);
            }
        }

        return new BlockRealMatrix(mat);
    }

    public static DenseMatrix64F realToEjml( RealMatrix orig )
    {
        if( orig == null )
            return null;

        DenseMatrix64F ret = new DenseMatrix64F(orig.getRowDimension(),orig.getColumnDimension());

        for( int i = 0; i < ret.numRows; i++ ) {
            for( int j = 0; j < ret.numCols; j++ ) {
                ret.set(i,j, orig.getEntry(i,j));
            }
        }

        return ret;
    }
}