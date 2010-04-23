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

import java.util.Random;


/**
 * Memory tests see how much memory it takes a library to perform the specified operation. For
 * each operation tested the inputs should be created and filled with random elements in a row
 * major fashion.
 *
 * @author Peter Abeles
 */
public interface MemoryFactory extends LibraryFactory {

    public void mult( int size , Random rand );

    public void add( int size , Random rand );

    public void solveEq( int size , Random rand );
    
    public void solveLS( int numRows , int numCols , Random rand );

    public void svd( int numRows , int numCols , Random rand );

    public void eig( int size , Random rand );
}
