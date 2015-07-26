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
package it.geosolutions.fra2015.importer.test;

import static it.geosolutions.fra2015.importer.ReservedHeaderNames.YEAR;
import it.geosolutions.fra2015.importer.CSVBasicValueCreator;
import it.geosolutions.fra2015.importer.CSVImporterMarshaller;
import it.geosolutions.fra2015.importer.CSVLoader;
import it.geosolutions.fra2015.importer.CSVParser;
import it.geosolutions.fra2015.importer.CSVBasicValueCreator.*;
import it.geosolutions.fra2015.server.model.xmlexport.BasicValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.apache.commons.csv.CSVRecord;
import org.geotools.TestData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author DamianoG
 * 
 */
public class SurveyCSV2surveyXMLsTest extends Assert {

    public CSVLoader csv1 = null;

    public CSVLoader csv2 = null;

    public Set<Map<String, Integer>> headers = null;

    public CSVParser csvParser = null;

    public CSVRecord anExampleRecordFromCSV1 = null;

    public CSVRecord anExampleRecordFromCSV2 = null;

    @Before
    public void csvLoadingTest() throws FileNotFoundException, IOException {

        File file1 = TestData.file(this,
                "22_Aug_2014_P_version_for_FRIMS_update_entryItemAdded.csv");
        File file2 = TestData.file(this, "22_August_Annual_F_for_FRIMS_update_entryItemAdded.csv");
        csv1 = new CSVLoader(file1.getAbsolutePath());
        csv2 = new CSVLoader(file2.getAbsolutePath());

        assertNotNull(csv1.getHeader());
        assertNotNull(csv2.getHeader());
        headers = new LinkedHashSet<Map<String, Integer>>();
        headers.add(csv1.getHeader());
        headers.add(csv2.getHeader());

        assertEquals(1874, csv1.countCSVRecords());
        assertEquals(235, csv1.getCountries().size());
        assertEquals(6318, csv2.countCSVRecords());
        assertEquals(234, csv2.getCountries().size());
        List<CSVRecord> records1 = csv1.groupCSVByCountry("DZA");
        List<CSVRecord> records2 = csv2.groupCSVByCountry("DZA");
        anExampleRecordFromCSV1 = records1.get(7);
        anExampleRecordFromCSV2 = records2.get(9);
        csvParser = new CSVParser("DZA", headers, records1, records2);
        csvParser.setPrettyHeader(csv1.getPrettyHeader(), csv2.getPrettyHeader());
    }
    
    @Test
    public void questionExtraction() {
        Pattern pattern = Pattern.compile("^Q(\\d*)");
        Matcher matcher = pattern.matcher("Q2323djdkcndcn");
        matcher.find();
        matcher.groupCount();
        assertEquals("2323", matcher.group(1));
    }

    @Test
    public void groupReadRecords() {
        List<CSVRecord> list = new LinkedList<CSVRecord>();
        list.addAll(csv1.groupCSVByCountry("DZA"));
        list.addAll(csv2.groupCSVByCountry("DZA"));
        for (CSVRecord record : list) {
            assertEquals(4, record.get(YEAR.toString()).length());
        }
    }

    @Test
    public void groupByCountryTest() {

        assertEquals(8, csv1.groupCSVByCountry("DZA").size());
        assertEquals(27, csv2.groupCSVByCountry("DZA").size());
        assertEquals(8, csv1.groupCSVByCountry("SWE").size());
        assertEquals(27, csv2.groupCSVByCountry("SWE").size());
    }

    @Test
    public void evaluateHeadersTest() {
        Set<Map<String, Integer>> headerSet = csvParser.evaluateHeaders();
        assertEquals(2, headerSet.size());
        for (Map<String, Integer> map : headerSet) {
            assertTrue(!map.isEmpty());
        }
    }

    @Test
    public void findHeaderTest() {
        assertEquals(250, csvParser.findHeader(anExampleRecordFromCSV1).size());
        assertEquals(13, csvParser.findHeader(anExampleRecordFromCSV2).size());
    }

    @Test
    public void toBasicValuesListTest() {
        // I expect 2147 since DZA loads:
        // -> 27 records with header size equal to 250 (minus 2 that are reserved)
        // -> 8 records with header size equal to 13 (minus 2 that are reserved)
        // -> about one hundred records are excluded due to the management of year 9999
        assertEquals(2154, csvParser.toBasicValuesList().size());
    }

    @Test
    public void basicValueCreatorFactoryTest() {
        assertTrue(CSVBasicValueCreator.makeCreator("8a_3*") instanceof CSVBasicValueCreator.AsterikCreator);
        assertTrue(CSVBasicValueCreator.makeCreator("3e_4(6)") instanceof CSVBasicValueCreator.BracketCreator);
        assertTrue(CSVBasicValueCreator.makeCreator("1a_6") instanceof CSVBasicValueCreator.BasicYearCreator);
        assertTrue(CSVBasicValueCreator.makeCreator("9x_2_1") instanceof CSVBasicValueCreator.StandardCreator);

        boolean failed = false;
        try {
            CSVBasicValueCreator.makeCreator("9x_2_1_w");
        } catch (IllegalArgumentException e) {
            failed = true;
        }
        if (!failed) {
            fail();
        }

        failed = false;
        try {
            CSVBasicValueCreator.makeCreator("9x_2_1_");
        } catch (IllegalArgumentException e) {
            failed = true;
        }
        if (!failed) {
            fail();
        }

        failed = false;
        try {
            CSVBasicValueCreator.makeCreator("");
        } catch (IllegalArgumentException e) {
            failed = true;
        }
        if (!failed) {
            fail();
        }

        failed = false;
        try {
            CSVBasicValueCreator.makeCreator(null);
        } catch (IllegalArgumentException e) {
            failed = true;
        }
        if (!failed) {
            fail();
        }
    }

    @Test
    public void basicValueAsterikTest() {
        BasicValueCreator creator = CSVBasicValueCreator.makeCreator("8a_3*");
        BasicValue bv = creator.create("8a_3*", "3433", "2001", 1);
        assertEquals("3433", bv.getContent());
        assertEquals("8a_3_12", bv.getReference());
        assertEquals("Number", bv.getType());
        assertEquals(new Long(1), bv.getQuestionId());
    }

    @Test
    public void basicValueBracketTest() {
        BasicValueCreator creator = CSVBasicValueCreator.makeCreator("8a_3(6)");
        BasicValue bv = null;

        bv = creator.create("8a_3(6)", "34433", "2005", 1);
        assertEquals("34433", bv.getContent());
        assertEquals("8a_3_8", bv.getReference());
        assertEquals("Number", bv.getType());
        assertEquals(new Long(1), bv.getQuestionId());
    }

    @Test
    public void basicValueYearTest() {
        BasicValueCreator creator = CSVBasicValueCreator.makeCreator("8a_3");
        BasicValue bv = creator.create("8a_3", "34433", "2005", 1);
        assertEquals("34433", bv.getContent());
        assertEquals("8a_3_3", bv.getReference());
        assertEquals("Number", bv.getType());
        assertEquals(new Long(1), bv.getQuestionId());
    }

    @Test
    public void basicValueStandardTest() {
        BasicValueCreator creator = CSVBasicValueCreator.makeCreator("8a_3_345");
        BasicValue bv = creator.create("8a_3_345", "3433", "2001", 1);
        assertEquals("3433", bv.getContent());
        assertEquals("8a_3_345", bv.getReference());
        assertEquals("Number", bv.getType());
        assertEquals(new Long(1), bv.getQuestionId());
    }

    @Test
    public void marshallTest() throws FileNotFoundException, IOException {

        File outputDir = TestData.file(this, "output");
        if (outputDir == null || !outputDir.exists() || !outputDir.isDirectory()
                || !outputDir.canWrite()) {
            throw new IllegalStateException(
                    "Output Directory doesn't exists, is not a dirtectory or it's not writable...");
        }

        CSVImporterMarshaller marshaller = new CSVImporterMarshaller(csvParser, outputDir);
        marshaller.setSurveyOutStream(System.out);
        try {
            marshaller.marshall();
        } catch (JAXBException e) {
            fail();
        }
    }

}
