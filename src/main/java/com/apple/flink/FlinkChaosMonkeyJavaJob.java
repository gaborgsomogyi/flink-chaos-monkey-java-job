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

import org.apache.flink.connector.datagen.table.DataGenConnectorOptions;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableDescriptor;
import org.apache.flink.table.api.TableEnvironment;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.flink.table.api.Expressions.$;
import static org.apache.flink.table.api.Expressions.call;

public class FlinkChaosMonkeyJavaJob {

    private static final Logger LOG = LoggerFactory.getLogger(FlinkChaosMonkeyJavaJob.class);

    public static void main(String[] args) {
        LOG.info("Starting chaos monkey job");

        Options options = new Options();

        CommandLineParser parser = new DefaultParser();
        try {
            LOG.info("Parsing command line");
            CommandLine commandLine = parser.parse(options, args);

            EnvironmentSettings settings =
                    EnvironmentSettings.newInstance().inStreamingMode().build();

            LOG.info("Creating table environment");
            TableEnvironment tEnv = TableEnvironment.create(settings);

            LOG.info("Creating datagen table");
            tEnv.createTable(
                    "DatagenTable",
                    TableDescriptor.forConnector("datagen")
                            .schema(Schema.newBuilder().column("f0", DataTypes.STRING()).build())
                            .option("fields.f0.kind", "random")
                            .option("fields.f0.length", "8")
                            .option(DataGenConnectorOptions.ROWS_PER_SECOND, 1L)
                            .build());

            Table datagenTable =
                    tEnv.from("DatagenTable").select(call(new FlinkChaosMonkeyJavaUDF(), $("f0")));

            LOG.info("Creating print table");
            final String printTableName = "PrintTable";
            tEnv.createTable(
                    printTableName,
                    TableDescriptor.forConnector("print")
                            .schema(Schema.newBuilder().column("f0", DataTypes.STRING()).build())
                            .build());

            LOG.info("Executing insert");
            datagenTable.executeInsert(printTableName);
        } catch (ParseException e) {
            LOG.error("Unable to parse command line options: " + e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(FlinkChaosMonkeyJavaJob.class.getCanonicalName(), options);
        }

        LOG.info("Existing chaos monkey job");
    }
}
