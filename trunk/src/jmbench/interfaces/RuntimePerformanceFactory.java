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

package jmbench.interfaces;

import java.io.Serializable;


/**
 * @author Peter Abeles
 */
public interface RuntimePerformanceFactory extends LibraryFactory , Serializable  {

    void configure();

    BenchmarkMatrix create( int numRows , int numCols );

    BenchmarkMatrix wrap( Object matrix );

    AlgorithmInterface chol();

    AlgorithmInterface lu();

    AlgorithmInterface svd();

    AlgorithmInterface qr();

    AlgorithmInterface eigSymm();

    // should it test against asymmetric matrices?
//    AlgorithmInterface eigASymm();


    AlgorithmInterface det();

    AlgorithmInterface invert();

    AlgorithmInterface invertSymmPosDef();

    AlgorithmInterface add();

    AlgorithmInterface mult();

    AlgorithmInterface multTransA();

    AlgorithmInterface scale();

    AlgorithmInterface solveExact();

    AlgorithmInterface solveOver();

    AlgorithmInterface transpose();
}