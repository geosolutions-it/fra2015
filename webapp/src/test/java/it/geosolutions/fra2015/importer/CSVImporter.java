/*
 *  Copyright (C) 2007-2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.fra2015.importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVRecord;
import org.geotools.TestData;

/**
 * @author DamianoG
 * 
 */
@SuppressWarnings("deprecation")
public class CSVImporter {

    public static void main(String[] args) throws FileNotFoundException, JAXBException {

        Option csv1 = OptionBuilder.withArgName("csv1").hasArg()
                .withDescription("The absolute path of the first CSV to import").create("csv1");

        Option csv2 = OptionBuilder.withArgName("csv2").hasArg()
                .withDescription("The absolute path of the second CSV to import").create("csv2");

        Option outDir = OptionBuilder
                .withArgName("outputDirectory")
                .hasArg()
                .withDescription(
                        "the absolute path of the directory where to store the xml files created")
                .create("outputDirectory");
        Options options = new Options();

        options.addOption(csv1);
        options.addOption(csv2);
        options.addOption(outDir);

        String csv1Value = "";
        String csv2Value = "";
        String outputDirectoryValue = "";
        
        // create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(options, args);
            
            if( line.hasOption( "csv1" ) && line.hasOption( "csv2" ) && line.hasOption( "outputDirectory" )) { 
                csv1Value = line.getOptionValue( "csv1" );
                csv2Value = line.getOptionValue( "csv2" );
                outputDirectoryValue = line.getOptionValue( "outputDirectory" );
            }
            else{
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "csvImporter", options );
            }
            
            
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
        
//        System.out.println(csv1Value + " - " + csv2Value + " - " + outputDirectoryValue);
        
        CSVLoader csv1Loader = new CSVLoader(csv1Value);
        CSVLoader csv2Loader = new CSVLoader(csv2Value);

        List<CSVRecord> records1 = csv1Loader.groupCSVByCountry("DZA");
        List<CSVRecord> records2 = csv2Loader.groupCSVByCountry("DZA");

        Set<Map<String, Integer>> headers = new LinkedHashSet<Map<String, Integer>>();
        headers.add(csv1Loader.getHeader());
        headers.add(csv2Loader.getHeader());
        CSVParser csvParser = new CSVParser("DZA", headers, records1, records2);
        csvParser.setPrettyHeader(csv1Loader.getPrettyHeader(), csv2Loader.getPrettyHeader());
        
        CSVMarshaller marhaller = new CSVMarshaller(csvParser, new File(outputDirectoryValue));
        marhaller.marshall();

    }
}
