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
import jmbench.impl.wrapper.UjmpBenchmarkMatrix;
import jmbench.interfaces.AlgorithmInterface;
import jmbench.interfaces.BenchmarkMatrix;
import jmbench.interfaces.RuntimePerformanceFactory;
import jmbench.tools.runtime.generator.ScaleGenerator;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.doublematrix.DenseDoubleMatrix2D;
import org.ujmp.core.util.UJMPSettings;

/**
 *
 * @author Peter Abeles
 * @author Holger Arndt
 */
public class UjmpAlgorithmFactory implements RuntimePerformanceFactory {

    boolean useNative = true;

    public UjmpAlgorithmFactory() {
    }

    public UjmpAlgorithmFactory( boolean useNative ) {
        this.useNative = useNative;
    }

    @Override
    public void configure() {
        UJMPSettings.setUseJBlas(useNative);
    }

    private static abstract class MyInterface implements AlgorithmInterface {
		@Override
		public String getName() {
			return MatrixLibrary.UJMP.getVersionName();
		}
	}

    @Override
    public BenchmarkMatrix create(int numRows, int numCols) {
        return wrap( DenseDoubleMatrix2D.factory.zeros(numRows,numCols));
    }

    @Override
    public BenchmarkMatrix wrap(Object matrix) {
        return new UjmpBenchmarkMatrix((Matrix)matrix);
    }

    @Override
	public AlgorithmInterface chol() {
		return new CholOp();
	}

	public static class CholOp extends MyInterface {
		@Override
		public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs,
				long numTrials) {
			Matrix matA = inputs[0].getOriginal();

			Matrix U = null;

			long prev = System.currentTimeMillis();

			for (long i = 0; i < numTrials; i++) {
				U = DenseDoubleMatrix2D.chol.calc(matA);
			}

			long elapsedTime = System.currentTimeMillis() - prev;

			outputs[0] = new UjmpBenchmarkMatrix(U.transpose());
			return elapsedTime;
		}
	}

	@Override
	public AlgorithmInterface lu() {
		return new LU();
	}

	public static class LU extends MyInterface {
		@Override
		public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs,
				long numTrials) {
			Matrix matA = inputs[0].getOriginal();

			Matrix L = null;
			Matrix U = null;
			Matrix P = null;

			long prev = System.currentTimeMillis();

			for (long i = 0; i < numTrials; i++) {
				Matrix[] decomp = DenseDoubleMatrix2D.lu.calc(matA);

				L = decomp[0];
				U = decomp[1];
				P = decomp[2];
			}

			long elapsedTime = System.currentTimeMillis() - prev;
			outputs[0] = new UjmpBenchmarkMatrix(L);
			outputs[1] = new UjmpBenchmarkMatrix(U);
			outputs[2] = new UjmpBenchmarkMatrix(P);
			return elapsedTime;
		}
	}

	@Override
	public AlgorithmInterface svd() {
		return new SVD();
	}

	public static class SVD extends MyInterface {
		@Override
		public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs,
				long numTrials) {
			Matrix matA = inputs[0].getOriginal();

			Matrix[] svd = null;

			long prev = System.currentTimeMillis();

			for (long i = 0; i < numTrials; i++) {
				// it should be extracting all the components all the time
				svd = DenseDoubleMatrix2D.svd.calc(matA);
			}

			long elapsedTime = System.currentTimeMillis() - prev;
			outputs[0] = new UjmpBenchmarkMatrix(svd[0]);
			outputs[1] = new UjmpBenchmarkMatrix(svd[1]);
			outputs[2] = new UjmpBenchmarkMatrix(svd[2]);
			return elapsedTime;
		}
	}

	@Override
	public AlgorithmInterface eigSymm() {
		return new Eig();
	}

	public static class Eig extends MyInterface {
		@Override
		public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs,
				long numTrials) {
			DenseDoubleMatrix2D matA = inputs[0].getOriginal();

			Matrix result[] = null;

			long prev = System.currentTimeMillis();

			for (long i = 0; i < numTrials; i++) {
				result = DenseDoubleMatrix2D.eig.calc(matA);
			}

			long elapsedTime = System.currentTimeMillis() - prev;
			outputs[0] = new UjmpBenchmarkMatrix(result[1]);
			outputs[1] = new UjmpBenchmarkMatrix(result[0]);
			return elapsedTime;
		}
	}

	@Override
	public AlgorithmInterface qr() {
		return new QR();
	}

	public static class QR extends MyInterface {
		@Override
		public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs,
				long numTrials) {
			DenseDoubleMatrix2D matA = inputs[0].getOriginal();

			Matrix Q = null;
			Matrix R = null;

			long prev = System.currentTimeMillis();

			for (long i = 0; i < numTrials; i++) {
				Matrix decomp[] = DenseDoubleMatrix2D.qr.calc(matA);

				Q = decomp[0];
				R = decomp[1];
			}

			long elapsedTime = System.currentTimeMillis() - prev;
			outputs[0] = new UjmpBenchmarkMatrix(Q);
			outputs[1] = new UjmpBenchmarkMatrix(R);
			return elapsedTime;
		}
	}

	@Override
	public AlgorithmInterface det() {
		return new Det();
	}

	public static class Det extends MyInterface {
		@Override
		public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs,
				long numTrials) {
			Matrix matA = inputs[0].getOriginal();

			long prev = System.currentTimeMillis();

			for (long i = 0; i < numTrials; i++) {
				matA.det();
			}

			return System.currentTimeMillis() - prev;
		}
	}

	@Override
	public AlgorithmInterface invert() {
		return new Inv();
	}

	public static class Inv extends MyInterface {
		@Override
		public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs,
				long numTrials) {
			Matrix matA = inputs[0].getOriginal();

			Matrix result = null;

			long prev = System.currentTimeMillis();

			for (long i = 0; i < numTrials; i++) {
				result = DenseDoubleMatrix2D.inv.calc(matA);
			}

			long elapsedTime = System.currentTimeMillis() - prev;
			outputs[0] = new UjmpBenchmarkMatrix(result);
			return elapsedTime;
		}
	}

	@Override
	public AlgorithmInterface invertSymmPosDef() {
		return new InvSymmPosDef();
	}

	public static class InvSymmPosDef extends MyInterface {
		@Override
		public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs,
				long numTrials) {
			Matrix matA = inputs[0].getOriginal();

			Matrix result = null;
			Matrix eye = MatrixFactory.eye(matA.getSize());

			long prev = System.currentTimeMillis();

			for (long i = 0; i < numTrials; i++) {
				result = DenseDoubleMatrix2D.chol.solve(matA, eye);
			}

			long elapsedTime = System.currentTimeMillis() - prev;
			outputs[0] = new UjmpBenchmarkMatrix(result);
			return elapsedTime;
		}
	}

	@Override
	public AlgorithmInterface add() {
		return new Add();
	}

	public static class Add extends MyInterface {
		@Override
		public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs,
				long numTrials) {
			DenseDoubleMatrix2D matA = inputs[0].getOriginal();
			DenseDoubleMatrix2D matB = inputs[1].getOriginal();

			DenseDoubleMatrix2D result = DenseDoubleMatrix2D.factory.zeros(matA
					.getRowCount(), matA.getColumnCount());

			long prev = System.currentTimeMillis();

			for (long i = 0; i < numTrials; i++) {
				DenseDoubleMatrix2D.plusMatrix.calc(matA, matB, result);
			}

			long elapsedTime = System.currentTimeMillis() - prev;
			outputs[0] = new UjmpBenchmarkMatrix(result);
			return elapsedTime;
		}
	}

	@Override
	public AlgorithmInterface mult() {
		return new Mult();
	}

	public static class Mult extends MyInterface {
		@Override
		public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs,
				long numTrials) {
			DenseDoubleMatrix2D matA = inputs[0].getOriginal();
			DenseDoubleMatrix2D matB = inputs[1].getOriginal();

			DenseDoubleMatrix2D result = DenseDoubleMatrix2D.factory.zeros(matA
					.getRowCount(), matB.getColumnCount());

			long prev = System.currentTimeMillis();

			for (long i = 0; i < numTrials; i++) {
				DenseDoubleMatrix2D.mtimes.calc(matA, matB, result);
			}

			long elapsedTime = System.currentTimeMillis() - prev;
			outputs[0] = new UjmpBenchmarkMatrix(result);
			return elapsedTime;
		}
	}

	@Override
	public AlgorithmInterface multTransA() {
		return new MulTranA();
	}

	public static class MulTranA extends MyInterface {
		@Override
		public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs,
				long numTrials) {
			Matrix matA = inputs[0].getOriginal();
			Matrix matB = inputs[1].getOriginal();

			long prev = System.currentTimeMillis();

			Matrix result = null;

			for (long i = 0; i < numTrials; i++) {
				result = matA.transpose().mtimes(matB);
			}

			long elapsedTime = System.currentTimeMillis() - prev;
			outputs[0] = new UjmpBenchmarkMatrix(result);
			return elapsedTime;
		}
	}

	@Override
	public AlgorithmInterface scale() {
		return new Scale();
	}

	public static class Scale extends MyInterface {
		@Override
		public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs,
				long numTrials) {
			DenseDoubleMatrix2D matA = inputs[0].getOriginal();

			DenseDoubleMatrix2D result = DenseDoubleMatrix2D.factory.zeros(matA
					.getRowCount(), matA.getColumnCount());

			long prev = System.currentTimeMillis();

			for (long i = 0; i < numTrials; i++) {
				DenseDoubleMatrix2D.timesScalar.calc(matA,
						ScaleGenerator.SCALE, result);
			}

			long elapsedTime = System.currentTimeMillis() - prev;
			outputs[0] = new UjmpBenchmarkMatrix(result);
			return elapsedTime;
		}
	}

	@Override
	public AlgorithmInterface solveExact() {
		return new Solve();
	}

	public static class Solve extends MyInterface {
		@Override
		public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs,
				long numTrials) {
			Matrix matA = inputs[0].getOriginal();
			Matrix matB = inputs[1].getOriginal();

			Matrix result = null;

			long prev = System.currentTimeMillis();

			for (long i = 0; i < numTrials; i++) {
				result = matA.solve(matB);
			}

			long elapsedTime = System.currentTimeMillis() - prev;
			outputs[0] = new UjmpBenchmarkMatrix(result);
			return elapsedTime;
		}
	}

	@Override
	public AlgorithmInterface solveOver() {
		return new Solve();
	}

	@Override
	public AlgorithmInterface transpose() {
		return new Transpose();
	}

	public static class Transpose extends MyInterface {
		@Override
		public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs,
				long numTrials) {
			DenseDoubleMatrix2D matA = inputs[0].getOriginal();

			DenseDoubleMatrix2D result = DenseDoubleMatrix2D.factory.zeros(matA
					.getColumnCount(), matA.getRowCount());

			long prev = System.currentTimeMillis();

			for (long i = 0; i < numTrials; i++) {
				DenseDoubleMatrix2D.transpose.calc(matA, result);
			}

			long elapsedTime = System.currentTimeMillis() - prev;
			outputs[0] = new UjmpBenchmarkMatrix(result);
			return elapsedTime;
		}
	}

	public static DenseDoubleMatrix2D convertToUjmp(DenseMatrix64F orig) {
		DenseDoubleMatrix2D ret = DenseDoubleMatrix2D.factory.zeros(orig
				.getNumRows(), orig.getNumCols());

		for (int i = 0; i < orig.numRows; i++) {
			for (int j = 0; j < orig.numCols; j++) {
				ret.setDouble(orig.get(i, j), i, j);
			}
		}

		return ret;
	}

	public static DenseMatrix64F ujmpToEjml(Matrix orig) {
		if (orig == null)
			return null;

		DenseMatrix64F ret = new DenseMatrix64F((int) orig.getRowCount(),
				(int) orig.getColumnCount());

		for (int i = 0; i < ret.numRows; i++) {
			for (int j = 0; j < ret.numCols; j++) {
				ret.set(i, j, orig.getAsDouble(i, j));
			}
		}

		return ret;
	}

    public boolean isUseNative() {
        return useNative;
    }

    public void setUseNative(boolean useNative) {
        this.useNative = useNative;
    }
}