/*
 * Licensed to Cloudera, Inc. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apple.flink;

import org.apache.flink.table.functions.ScalarFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FlinkChaosMonkeyJavaUDF extends ScalarFunction {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkChaosMonkeyJavaUDF.class);

    private File throwExceptionFile = new File("/tmp/throwExceptionInUDF");

    public String eval(String s) {
        if (throwExceptionFile.exists()) {
            String msg = FlinkChaosMonkeyJavaUDF.class.getCanonicalName() + " intended exception";
            LOG.error("Throwing: " + msg);
            throw new RuntimeException(msg);
        }
        return s;
    }
}
