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

package jmbench.tools.runtime;

import jmbench.tools.OutputError;
import jmbench.tools.TestResults;


/**
 * @author Peter Abeles
 */
public class RuntimeResults implements TestResults , Comparable<RuntimeResults>{
    double opsPerSec;

    long memoryUsed;
    OutputError error;

    public RuntimeResults( double opsPerSec , long memoryUsed ) {
        this.opsPerSec = opsPerSec;
        this.memoryUsed = memoryUsed;
    }

    public RuntimeResults(){}

    public String toString() {
        return "ops/sec = "+opsPerSec;
    }

    public double getOpsPerSec() {
        return opsPerSec;
    }

    public void setOpsPerSec(double opsPerSec) {
        this.opsPerSec = opsPerSec;
    }

    public OutputError getError() {
        return error;
    }

    public void setError(OutputError error) {
        this.error = error;
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public int compareTo( RuntimeResults r ) {
        if( r.opsPerSec < opsPerSec )
            return 1;
        else if( r.opsPerSec > opsPerSec )
            return -1;
        return 0;
    }
}
