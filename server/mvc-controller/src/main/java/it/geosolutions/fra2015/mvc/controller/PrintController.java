/*
 *  fra2015
 *  https://github.com/geosolutions-it/fra2015
 *  Copyright (C) 2007-2013 GeoSolutions S.A.S.
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
package it.geosolutions.fra2015.mvc.controller;

import it.geosolutions.fra2015.mvc.controller.utils.CharArrayWriterResponse;
import it.geosolutions.fra2015.mvc.controller.utils.ControllerServices;
import it.geosolutions.fra2015.mvc.controller.utils.ControllerServices.Profile;
import it.geosolutions.fra2015.mvc.controller.utils.FeedbackHandler;
import it.geosolutions.fra2015.mvc.view.MyReloadableResourceBundleMessageSource;
import it.geosolutions.fra2015.server.model.survey.Entry;
import it.geosolutions.fra2015.server.model.survey.EntryItem;
import it.geosolutions.fra2015.server.model.survey.Feedback;
import it.geosolutions.fra2015.server.model.survey.NumberValue;
import it.geosolutions.fra2015.server.model.survey.Question;
import it.geosolutions.fra2015.server.model.survey.TextValue;
import it.geosolutions.fra2015.server.model.user.User;
import it.geosolutions.fra2015.services.BulkModelEntitiesLoader;
import it.geosolutions.fra2015.services.FeedbackService;
import it.geosolutions.fra2015.services.SurveyService;
import it.geosolutions.fra2015.services.exception.BadRequestServiceEx;
import it.geosolutions.fra2015.services.exception.InternalErrorServiceEx;
import it.geosolutions.fra2015.services.exception.NotFoundServiceEx;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author DamianoG
 * 
 */
@Controller
public class PrintController {

	@Autowired
	private ControllerServices utils;

	@Autowired
	private SurveyService surveyService;

	@Autowired
	private MyReloadableResourceBundleMessageSource messageSource;

	@Autowired
	private BulkModelEntitiesLoader bulkLoader;

	@Autowired
	private FeedbackService feedbackService;

	@Autowired
	private ServletContext servletContext;

	private Logger LOGGER = Logger.getLogger(PrintController.class);
	private static final String CSV_SEPARATOR = ",";

	@RequestMapping(value = "/survey/print/{country}/{mode}", method = RequestMethod.POST)
	public String printWelcome(Model model, HttpSession session,  Locale locale,
			@PathVariable(value = "country") String country, 
			@PathVariable(value = "mode") String mode, 
			@RequestParam(value="excludeUnedited",required=false,defaultValue="false") Boolean excludeUnedited,
			@RequestParam(value="includeComments",required=false,defaultValue="false") Boolean includeComments,
			@RequestParam(value="printAllQuestions",required=false,defaultValue="false") Boolean printAllQuestions,
			@RequestParam(value="batchExportQuestionsDuallistbox",required=false) Integer[] questions
			) throws IllegalArgumentException, InternalErrorServiceEx, NotFoundServiceEx{

		User su = (User) session.getAttribute("sessionUser");

		//USE TO TEST!!!
		//questions = new Integer[] {0,1};
		//includeComments= true;
		//printAllQuestions= true;
		checkCountriesPermissions(su,country);
		checkQuestionsPermissions(su, questions);

		DateFormat df = new java.text.SimpleDateFormat("dd/MM/yyyy");

		Set<Object> messageKeys = messageSource.getKeys(locale);

		String countryName = surveyService.findCountryByISO3(country).getName();

		if(excludeUnedited) {
			model.addAttribute("hideEmpty",true);
		}else{
			model.addAttribute("hideEmpty",false);
		}

		if(printAllQuestions) {
			questions = new Integer[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
		}

		model.addAttribute("questions", questions); 
		model.addAttribute("context", "survey");    
		model.addAttribute("country", countryName);  
		model.addAttribute("messageKeys", messageKeys);   

		model.addAttribute("userName", su!=null?su.getUsername().toUpperCase():"");    
		model.addAttribute("currentDate", df.format(new java.util.Date()));    

		model.addAttribute("profile", ControllerServices.Profile.PRINT.toString());

		FeedbackHandler fh = new FeedbackHandler(utils, feedbackService);
		List<Feedback> listF = new ArrayList<Feedback>(0);

		if(su!= null && su.getRole() != null){
			if(su.getRole().equalsIgnoreCase(Profile.REVIEWER.toString())){
				try{
					model.addAttribute("profile", ControllerServices.Profile.PRINT_CON_FEEDBACK_REVIEWER.toString());
					model.addAttribute("profileName","REVIEWER");
					listF = fh.retrieveAllFeedbacks(country, session, su);
				} catch (BadRequestServiceEx e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if(su.getRole().equalsIgnoreCase(Profile.EDITOR.toString())){
				try{
					model.addAttribute("profile", ControllerServices.Profile.PRINT_CON_FEEDBACK_EDITOR.toString());
					model.addAttribute("profileName","EDITOR");
					listF = fh.retrieveAllFeedbacks(country, session, null);
				} catch (BadRequestServiceEx e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			if(su.getRole().equalsIgnoreCase(Profile.CONTRIBUTOR.toString())){
				try{
					model.addAttribute("profile", ControllerServices.Profile.CONTRIBUTOR.toString());
					model.addAttribute("profileName","CONTRIBUTOR");
					listF = fh.retrieveAllFeedbacks(country, session, null);
				} catch (BadRequestServiceEx e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}	

		if(mode.equalsIgnoreCase("allschema")){
			utils.prepareHTTPRequestOnlyVariablesName(model, country); 
			return "survey/printPage";
		}
		else if(mode.equalsIgnoreCase("onlyvalues") || mode.equalsIgnoreCase("onlyvalues_feedback")){

			utils.prepareEmptyElement(model, null, country, false, listF);

			utils.prepareHTTPRequest(model, null, utils.retrieveValues(null, country), false);
		}
		else if(mode.equalsIgnoreCase("onlynames")){

			utils.prepareHTTPRequest(model, null, utils.retrieveValues(null, country), true);
		}
		else{
			throw new IllegalArgumentException("the mode: '" + mode + "' doesn't exist, valid ones are 'allschema', 'onlyvalues', 'onlyvalues_feedback' and 'onlynames'");
		}

		if(!listF.isEmpty() && (mode.equalsIgnoreCase("onlyvalues_feedback") || includeComments)){
			listF = fh.packageFeedbacks(listF, true, utils.getStatusInstanceByCountry(country));
			model.addAttribute("feedbackCount",fh.getFeedbackCounter(country, session, true));
			fh.prepareFeedbackModel(model, listF);
		}
		return "survey/print";

	}

	@RequestMapping(value = "/survey/print/pdf", method = RequestMethod.POST)
	public String printWelcome(Model model, HttpSession session, HttpServletRequest req, HttpServletResponse resp, Locale locale, 
			@RequestParam(value="printAllQuestions",required=false,defaultValue="false") Boolean printAllQuestions,
			@RequestParam(value="batchExportCountriesDuallistbox",required=false) String[] countries, 
			@RequestParam(value="batchExportQuestionsDuallistbox",required=false) Integer[] questions,
			@RequestParam(value="excludeUnedited",required=false,defaultValue="false") Boolean excludeUnedited,
			@RequestParam(value="includeComments",required=false,defaultValue="false") Boolean includeComments,
			@RequestParam(value="onlyCFRQ",required=false,defaultValue="false") Boolean onlyCFRQ) throws IllegalArgumentException, InternalErrorServiceEx, NotFoundServiceEx{
		try {
			User su = (User) session.getAttribute("sessionUser");

			Cookie cookie = new Cookie("fileDownload", "true");
			cookie.setPath("/");
			cookie.setMaxAge(-1);
			resp.addCookie(cookie);			

			ServletOutputStream sos = resp.getOutputStream();

			String xslFilename = "/WEB-INF/xsl/countryReportFull.xsl" ;
			String mode = "onlyvalues";

			if(includeComments) {
				//xslFilename = "/WEB-INF/xsl/feedbackReport.xsl" ;
				//mode = "onlyvalues_feedback";
			}

			if(onlyCFRQ) {
				xslFilename = "/WEB-INF/xsl/countryReport.xsl" ;
			}

			String pathname =servletContext.getRealPath(xslFilename); 
			Source xslt = new StreamSource(pathname);
			TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
			Transformer transformer = factory.newTransformer(xslt);			
			transformer.setParameter("versionParam", "2.0");			
			String requestUrl = req.getRequestURL().toString();
			FopFactory fopFactory = FopFactory.newInstance();
			fopFactory.setStrictValidation(false); 
			String appPath = servletContext.getRealPath(""); //root of web app
			fopFactory.setBaseURL(appPath);	
			FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
			fopFactory.setBaseURL(requestUrl);				

			if(countries.length == 1) {
				String fileName = "FRA_2015_Country_Report_"+ countries[0] + "_" + su.getUsername();
				if(onlyCFRQ) {
					fileName = fileName + "_CFRQ";
				}
				fileName = fileName + ".pdf";
				resp.setContentType("application/pdf"); 
				resp.addHeader("Content-Disposition", "attachment; filename=\""+fileName+"\""); 
				fillPdfStreamForCountry(sos,transformer,fopFactory,foUserAgent,factory,countries[0],mode,req,resp,onlyCFRQ);
			}else {
				String fileName =  "FRA_2015_Countries_Report_"+su.getUsername();
				if(onlyCFRQ) {
					fileName = fileName + "_CFRQ";
				}
				fileName = fileName + ".zip";
				resp.setContentType("application/zip"); 
				resp.addHeader("Content-Disposition", "attachment; filename=\""+fileName+"\""); 
				ZipOutputStream zos = new ZipOutputStream(sos);
				zos.setLevel(9);
				for(String country : countries) {
					try {
						LOGGER.debug("Processing PDF generation for "+ country);
						String zFilename = "FRA_2015_Country_Report_" + country + ".pdf";
						ZipEntry zipEntry = new ZipEntry(zFilename);
						zos.putNextEntry(zipEntry);		
						fillPdfStreamForCountry(zos,transformer,fopFactory,foUserAgent,factory,country,mode,req,resp,onlyCFRQ);
						zos.flush();
						zos.closeEntry();
					}catch (Throwable e) {
						LOGGER.error(e.getMessage(), e);
					}
				}
				zos.close();
				sos.close();
			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	private void fillPdfStreamForCountry(OutputStream os, Transformer transformer, FopFactory fopFactory, 
			FOUserAgent foUserAgent, 
			TransformerFactory factory, 
			String country, 
			String mode, 
			HttpServletRequest req, 
			HttpServletResponse resp,
			Boolean onlyCFRQ) throws Exception {
		transformer.reset();
		String template = "/survey/print/"+country+"/"+mode;
		CharArrayWriterResponse customResponse = new CharArrayWriterResponse(resp);
		req.getRequestDispatcher(template).include(req, customResponse);
		String xml = customResponse.getOutput();
		StringReader xmlReader = new StringReader(cleanXml(xml));
		Source src = new StreamSource(xmlReader);
		if(onlyCFRQ){
			StringWriter xmlOutWriter = new StringWriter();				
			String cfrqFilterFilename = "/WEB-INF/xsl/cfrqFilter.xsl" ;
			String cfrqFilterPathname = servletContext.getRealPath(cfrqFilterFilename); 					
			Source cfrqXslt = new StreamSource(new File(cfrqFilterPathname));			
			StreamResult xmlResult = new StreamResult(xmlOutWriter);
			Transformer cfraFilter = factory.newTransformer(cfrqXslt);
			cfraFilter.setParameter("versionParam", "2.0");
			cfraFilter.transform(src, xmlResult);
			src = new StreamSource(new StringReader(cleanXml(xmlOutWriter.toString())));
		}
		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, os);	
		Result res = new SAXResult(fop.getDefaultHandler());
		transformer.transform(src, res);
	}

	@RequestMapping(value = "/survey/print/csv", method = RequestMethod.POST)
	public String printCsv(Model model, HttpSession session, HttpServletRequest req, HttpServletResponse resp, Locale locale, 
			@RequestParam(value="batchExportCountriesDuallistbox",required=false) String[] countries, 
			@RequestParam(value="batchExportQuestionsDuallistbox",required=false) Integer[] questions) throws IllegalArgumentException, InternalErrorServiceEx, NotFoundServiceEx{

		OutputStreamWriter outputwriter = null;  
		try {
			User su = (User) session.getAttribute("sessionUser");
			checkQuestionsPermissions(su, questions);

			//List<Question> questionList = surveyService.getQuestions(questions);

			String csvName =  "FRA_2015_Report_"+su.getUsername()+".csv";
			resp.setContentType("text/csv"); 
			// Tell the browser the filename, and that it needs to be downloaded instead of opened
			resp.addHeader("Content-Disposition", "attachment; filename=\""+csvName+"\"");        
			Cookie cookie = new Cookie("fileDownload", "true");
			cookie.setPath("/");
			cookie.setMaxAge(-1);
			resp.addCookie(cookie);		
			OutputStream resOs= resp.getOutputStream();  
			OutputStream buffOs= new BufferedOutputStream(resOs);   
			outputwriter = new OutputStreamWriter(buffOs);  

			Arrays.sort(countries);

			List<String> questionsHeader = new ArrayList<String>();
			questionsHeader.add("Country");
			List<EntryItem> all = bulkLoader.loadAllNumericValues(Arrays.asList(countries),questions);

			int ei4Index = -1;
			boolean customEi4Done = false;
			boolean customEi13Done = false;
			boolean customEi20Done = false;
			//Find question before 13
			Integer q13ToInsert = -1;
			Integer q13IndexToInsert = 1;
			Integer q20ToInsert = -1;
			Integer q20IndexToInsert = 1;
			for(Integer q : questions) {
				if(q>13 && q13ToInsert==-1) {
					q13ToInsert = q;
				}
				if(q>20 && q20ToInsert==-1) {
					q20ToInsert = q;
					break;
				}
			}

			for(EntryItem nValue: all) {
				EntryItem ei = nValue;
				Entry e = ei.getEntry();
				Question q = e.getQuestion();
				String nQuestion = "q"+q.getId();
				String nTable = "t"+e.getVariable();
				if(ei.getRowNumber() != null) {
					String nRow = "r"+ei.getRowNumber();
					if(ei.getColumnName() != null) {
						String nCol = "y"+ei.getColumnName()+"_"+ei.getColumnNumber();				
						String h = StringEscapeUtils.escapeCsv(nQuestion+"-"+nTable+"-"+nRow+"-"+nCol);
						if(!questionsHeader.contains(h)) {
							questionsHeader.add(h);
						}
					}	
				}
				if(nTable.equals("t4a") && !customEi4Done) {		
					//Table 4b: add a single value having the sum for the values of the table
					nQuestion = "q4";
					nTable = "t4b";
					String h = nQuestion+"-"+nTable+"-total";
					ei4Index = questionsHeader.size();
					List<EntryItem> ei4bEntryItems = bulkLoader.loadTable(questions,"4b");
					if(ei4bEntryItems.size() > 0) {
						questionsHeader.add(h);
					}	

					//Table 4c: add values
					List<EntryItem> ei4cEntryItems = bulkLoader.loadTable(questions,"4c");;
					nTable = "t4c";
					for(EntryItem ei4cValue: ei4cEntryItems) {
						String nRow = "r"+ei4cValue.getRowNumber();	
						String nCol = "c"+ei4cValue.getColumnNumber();									
						h = StringEscapeUtils.escapeCsv(nQuestion+"-"+nTable+"-"+nRow+"-"+nCol);
						questionsHeader.add(h);
					}
					customEi4Done = true;
				}
				/*Compute index for 13a and 20 */
				if((q13ToInsert == -1 || q.getId().intValue() == q13ToInsert.intValue()) && !customEi13Done) {	
					q13IndexToInsert = questionsHeader.size()-1;
					customEi13Done = true;
				}
				if((q20ToInsert == -1 || q.getId().intValue() == q20ToInsert.intValue()) && !customEi20Done) {	
					q20IndexToInsert = questionsHeader.size()-1;
					customEi20Done = true;
				}
			}

			//Table 13a: add the first two columns which contains numeric values
			List<EntryItem> ei13aEntryItems = bulkLoader.loadTable(questions,"13a");;
			String nTable = "t13a";
			String nQuestion = "q13";
			int count = 0;
			for(EntryItem ei13aValue : ei13aEntryItems) {
				if(ei13aValue.getColumnNumber()<=2) {
					String nRow = "r"+ei13aValue.getRowNumber();
					String nCol = "c"+ei13aValue.getColumnNumber();											
					String h = StringEscapeUtils.escapeCsv(nQuestion+"-"+nTable+"-"+nRow+"-"+nCol);
					questionsHeader.add(q13IndexToInsert+count,h);
					count++;
				}
			}

			//Table 20: add values		
			List<EntryItem> ei20EntryItems = bulkLoader.loadTable(questions,"20");
			nQuestion = "q20";
			nTable = "t20";
			for(EntryItem ei20Value: ei20EntryItems) {
				if(ei20Value.getColumnNumber()<=1 && ei20Value.getType().equals("Number")) {
					String nRow = "r"+ei20Value.getRowNumber();	
					String nCol = "c"+ei20Value.getColumnNumber();									
					String h = StringEscapeUtils.escapeCsv(nQuestion+"-"+nTable+"-"+nRow+"-"+nCol);
					questionsHeader.add(q20IndexToInsert+count,h);
					count++;
				}
			}

			outputwriter.write(StringUtils.join(questionsHeader, CSV_SEPARATOR));
			outputwriter.write(SystemUtils.LINE_SEPARATOR);

			List<NumberValue> numberValues;
			for(String country : countries) {
				String[] outputValues = new String[questionsHeader.size()];
				Arrays.fill(outputValues, "N/A");
				outputValues[0] = country;
				numberValues = bulkLoader.loadAllNumericValuesWithQuestions(country,questions);
				//Table 4c: add values
				numberValues.addAll(bulkLoader.loadTotalValue(country, questions, "4c"));
				//Table 20: add values
				numberValues.addAll(bulkLoader.loadTotalValue(country, questions, "20"));

				for(int i = 0 ; i <numberValues.size() ; i++) {
					NumberValue nValue = numberValues.get(i);
					EntryItem ei = nValue.getEntryItem();
					Entry e = ei.getEntry();
					Question q = e.getQuestion();
					nQuestion = "q"+q.getId();
					nTable = "t"+e.getVariable();
					String nRow = "r"+ei.getRowNumber();
					String nCol = "y"+ei.getColumnName()+"_"+ei.getColumnNumber();
					String h = StringEscapeUtils.escapeCsv(nQuestion+"-"+nTable+"-"+nRow+"-"+nCol);
					int valueIndex = questionsHeader.indexOf(h);
					if(valueIndex != -1) {
						if ("NaN".equalsIgnoreCase(nValue.getContent())){
							nValue.setContent("N/A");
						}
						outputValues[valueIndex] = StringEscapeUtils.escapeCsv(nValue.getContent());
					}else {
						nQuestion = "q"+q.getId();
						nTable = "t"+e.getVariable();
						nRow = "r"+ei.getRowNumber();	
						nCol = "c"+ei.getColumnNumber();									
						h = StringEscapeUtils.escapeCsv(nQuestion+"-"+nTable+"-"+nRow+"-"+nCol);
						valueIndex = questionsHeader.indexOf(h);
						if ("NaN".equalsIgnoreCase(nValue.getContent())){
							nValue.setContent("N/A");
						}
						outputValues[valueIndex] = StringEscapeUtils.escapeCsv(nValue.getContent());
					}
				}
				if(ei4Index > 0) {
					//Table 4b: add a single value having the sum for the values of the table
					List<NumberValue> ei4bnv = bulkLoader.loadTotalValue(country, questions,"4b");
					Double total = 0d;
					Boolean valueNan = true;
					for(NumberValue v : ei4bnv) {
						if (!"NaN".equalsIgnoreCase(v.getContent())){
							total = total + v.getValue().doubleValue();
							valueNan = false;
						}
					}
					if(valueNan) {
						outputValues[ei4Index] = "NaN";
					}else {
						outputValues[ei4Index] = StringEscapeUtils.escapeCsv(String.format( "%,.2f", total ));
					}
				}
				//Table 13a: add values
				List<TextValue> txtValues = bulkLoader.loadTextValue(country, questions, "13a");
				for(TextValue tv : txtValues) {
					EntryItem ei = tv.getEntryItem();
					Entry e = ei.getEntry();
					Question q = e.getQuestion();
					nQuestion = "q"+q.getId();
					nTable = "t"+e.getVariable();
					String nRow = "r"+ei.getRowNumber();	
					String nCol = "c"+ei.getColumnNumber();									
					String h = StringEscapeUtils.escapeCsv(nQuestion+"-"+nTable+"-"+nRow+"-"+nCol);
					int valueIndex = questionsHeader.indexOf(h);
					if(valueIndex > -1) {
						if ("NaN".equalsIgnoreCase(tv.getContent())){
							tv.setContent("N/A");
						}
						outputValues[valueIndex] = StringEscapeUtils.escapeCsv(tv.getContent());
					}
				}

				outputwriter.write(StringUtils.join(outputValues, CSV_SEPARATOR));
				outputwriter.write(SystemUtils.LINE_SEPARATOR);

			}
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}finally {
			if( outputwriter != null ) {
				try {
					outputwriter.flush();   
					outputwriter.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		return null;
	}

	private String cleanXml(String sourceXml){
		CleanerProperties props = new CleanerProperties();			 
		// set some properties to non-default values
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);
		props.setAdvancedXmlEscape(true);
		props.setTranslateSpecialEntities(true);
		props.setTransSpecialEntitiesToNCR(true);

		// do parsing
		TagNode tagNode = new HtmlCleaner(props).clean(sourceXml);
		String cleanXml = new PrettyXmlSerializer(props).getAsString(tagNode,"utf-8");
		return cleanXml;
	}

	private void checkCountriesPermissions(User su, String country) {
		//Check if user have this country, not for admin
		if(!su.getName().equals("admin")){
			BeanPropertyValueEqualsPredicate predicate = new BeanPropertyValueEqualsPredicate( "iso3", country );
			if(!CollectionUtils.exists( su.getCountriesSet(), predicate )){
				throw new IllegalArgumentException("Permission denied for country: " + country);
			}
		}
	}

	private void checkQuestionsPermissions(User su, Integer[] questions) {
		//Check if user have these questions, only for reviewers
		if(su.getRole().equals("reviewer")){
			for(Integer qId : questions) {
				BeanPropertyValueEqualsPredicate qPredicate = new BeanPropertyValueEqualsPredicate( "id",new Long(qId.longValue()));
				if(!CollectionUtils.exists( su.getQuestions(), qPredicate )){
					throw new IllegalArgumentException("Permission denied for question: " + qId);
				}
			}
		}
	}

}
