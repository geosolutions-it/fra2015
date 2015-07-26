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

import it.geosolutions.fra2015.server.model.xmlexport.BasicValue;
import it.geosolutions.fra2015.server.model.xmlexport.SurveyInfo;
import it.geosolutions.fra2015.server.model.xmlexport.SurveyStatus;
import it.geosolutions.fra2015.server.model.xmlexport.UserDTO;
import it.geosolutions.fra2015.server.model.xmlexport.XmlSurvey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 * @author DamianoG
 * 
 */
public class CSVImporterMarshaller {

    private CSVParser csvParser;
    
    private OutputStream surveyOutStream;

    public CSVImporterMarshaller(CSVParser converter, File outDirectory) throws FileNotFoundException {
        if (converter == null) {
            throw new IllegalArgumentException("The passed csvParser is null!!!");
        }
        this.csvParser = converter;
        File outFile = new File(outDirectory, this.csvParser.getCountry()+".xml");
        this.surveyOutStream = new FileOutputStream(outFile);
    }

    public void marshall() throws JAXBException {
        List<BasicValue> valList = csvParser.toBasicValuesList();
        
        SurveyInfo si = new SurveyInfo();
        si.setCountry(csvParser.getCountry());
        si.setTimestamp(new Date());
        
        // TODO Check this values, especially: Which status do we want to set?
        SurveyStatus ss = new SurveyStatus();
        ss.setStatus("pendingfix");
        ss.setMessage("Imported from CSV");
        ss.setCoverage("0/21");
        ss.setLastContributorSubmission(new Date().getTime());
        ss.setLastSurveyReview(new Date().getTime());
        ss.setPreviousSurveyReview(new Date().getTime());
        ss.setReviewerSubmit("Imported from CSV");
        ss.setRevisionNumber(1);
        
        UserDTO user = new UserDTO();
        user.setName("admin");
        user.setRole("admin");
        user.setUsername("admin");
        List<UserDTO> users = new LinkedList<UserDTO>();
        
        XmlSurvey survey = new XmlSurvey();
        survey.setInfo(si);
        survey.setSurveyStatus(ss);
        survey.setBasicValues(valList);
        survey.setUsers(users);

        JAXBContext jc = null;

        jc = JAXBContext.newInstance(XmlSurvey.class);
        Marshaller um = jc.createMarshaller();
        um.marshal(survey, surveyOutStream);
    }

    /**
     * @return the surveyOutStream
     */
    public OutputStream getSurveyOutStream() {
        return surveyOutStream;
    }

    /**
     * @param surveyOutStream the surveyOutStream to set
     */
    public void setSurveyOutStream(OutputStream surveyOutStream) {
        this.surveyOutStream = surveyOutStream;
    }
    
    
}
