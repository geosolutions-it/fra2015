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

import static it.geosolutions.fra2015.mvc.controller.utils.ControllerServices.SESSION_USER;
import it.geosolutions.fra2015.entrypoint.model.Update;
import it.geosolutions.fra2015.entrypoint.model.Updates;
import it.geosolutions.fra2015.mvc.controller.utils.ControllerServices;
import it.geosolutions.fra2015.mvc.controller.utils.ControllerServices.Profile;
import it.geosolutions.fra2015.mvc.controller.utils.VariableNameUtils;
import it.geosolutions.fra2015.mvc.model.SurveyUpload;
import it.geosolutions.fra2015.server.model.survey.CompactValue;
import it.geosolutions.fra2015.server.model.survey.Country;
import it.geosolutions.fra2015.server.model.survey.EntryItem;
import it.geosolutions.fra2015.server.model.survey.NumberValue;
import it.geosolutions.fra2015.server.model.survey.Question;
import it.geosolutions.fra2015.server.model.survey.Status;
import it.geosolutions.fra2015.server.model.survey.TextValue;
import it.geosolutions.fra2015.server.model.user.User;
import it.geosolutions.fra2015.server.model.xmlexport.BasicValue;
import it.geosolutions.fra2015.server.model.xmlexport.SurveyInfo;
import it.geosolutions.fra2015.server.model.xmlexport.SurveyStatus;
import it.geosolutions.fra2015.server.model.xmlexport.UserDTO;
import it.geosolutions.fra2015.server.model.xmlexport.XmlSurvey;
import it.geosolutions.fra2015.services.BulkModelEntitiesLoader;
import it.geosolutions.fra2015.services.SurveyService;
import it.geosolutions.fra2015.services.UserService;
import it.geosolutions.fra2015.services.exception.BadRequestServiceEx;
import it.geosolutions.fra2015.services.model.UserFilter;
import it.geosolutions.fra2015.services.utils.UserUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author Lorenzo Natali
 *
 */
@Controller
public class ImportExportController {

	@Autowired
	private SurveyService surveyService;

	@Autowired
	private BulkModelEntitiesLoader bulkLoader;

	@Autowired
	private ControllerServices utils;

	@Autowired
	private UserService usersServices;

	private static Logger LOGGER = Logger.getLogger(ImportExportController.class);

	@RequestMapping(value = "/export", method = RequestMethod.GET)
	public String export(ModelMap model, HttpSession session) {

		model.addAttribute("context", "export");
		User user = (User) session.getAttribute(SESSION_USER);
		if (user == null){
			return "redirect:/";
		}
		String role = user.getRole();
		if ("reviewer".equals(role)) {

			return "reviewer";
		}

		if (Profile.VALIDATOR.toString().equalsIgnoreCase(role)){
			model.addAttribute("country", UserUtil.getSingleIso3(user));
			return "validator";
		}
		if ("admin".equals(role)) {

			return "redirect:/adminexport";
		}
		if (role.equals("contributor")) {
			ArrayList<Country> cList = new ArrayList<Country>(user.getCountriesSet());
			model.addAttribute("countries", cList);
		}
		return "index";

	}

	@RequestMapping(value = "/adminexport", method = RequestMethod.GET)
	public String adminexport(ModelMap model, HttpSession session) {

		model.addAttribute("context", "export");
		User user = (User) session.getAttribute(SESSION_USER);
		if (user == null){

			return "redirect:/";
		}
		String role = user.getRole();
		if ("reviewer".equals(role)) {
			return "redirect:/export";
		}
		if ("admin".equals(role)) {
			model.addAttribute("countries", surveyService.getCountries());
			model.addAttribute("uploadItem", new SurveyUpload());
			return "admin";
		}
		return "/";

	}
	@RequestMapping(value = "/revexport", method = RequestMethod.GET)
	public String revExport(ModelMap model, HttpSession session) {

		model.addAttribute("context", "export");
		User user = (User) session.getAttribute(SESSION_USER);
		if (user == null){

			return "redirect:/";
		}

		ArrayList<Country> cList = new ArrayList<Country>(user.getCountriesSet());
		BeanComparator beanComparator = new BeanComparator("name");
		Collections.sort(cList, beanComparator);

		model.addAttribute("countries", cList);
		String role = user.getRole();
		if ("reviewer".equals(role)) {
			model.addAttribute("allowedQuestions",ControllerServices.getAllowedQuestions(user));
			return "reviewer";
		}
		if("editor".equals(role)){
			return "editor";
		}
		return "redirect:/";

	}

	@RequestMapping(value = "/importXml", method = RequestMethod.POST)
	public String importXml(ModelMap model, SurveyUpload uploadItem, BindingResult result) {
	        LOGGER.info("Importing '" + uploadItem.getCountryForImport() + "'");
		if (result.hasErrors()) {
			for (ObjectError error : result.getAllErrors()) {

				LOGGER.error("Error: " + error.getCode() + " - " + error.getDefaultMessage());
			}
			return "redirect:/adminexport";
		}
		if(uploadItem == null || uploadItem.getFileData() == null || uploadItem.getFileData().isEmpty() || StringUtils.isBlank(uploadItem.getCountryForImport())){
		        LOGGER.error("ERROR NO country provided!!'" + uploadItem.getCountryForImport() + "'");
			model.addAttribute("messageType", "warning");
			model.addAttribute("messageCode", "import.resultKO");
			model.addAttribute("messageTimeout",10000);
		}
		else{
			Boolean outcome = importFromXML(uploadItem.getFileData(), uploadItem.getCountryForImport());
			if(outcome == null){
			        LOGGER.error("An ERROR occurred while importing survey for '" + uploadItem.getCountryForImport() + "'");
				model.addAttribute("messageType", "warning");
				model.addAttribute("messageCode", "import.resultKO");
				model.addAttribute("messageTimeout",10000);
			}    
			else if(!outcome){
			        LOGGER.error("An ERROR occurred while importing survey for '" + uploadItem.getCountryForImport() + "'");
				model.addAttribute("messageType", "warning");
				model.addAttribute("messageCode", "import.countrynotmatch");
				model.addAttribute("messageTimeout",10000);
			}
			else{
			        LOGGER.info("Survey import for '" + uploadItem.getCountryForImport() + "' ended SUCCESSFULLY!");
				model.addAttribute("messageType", "success");
				model.addAttribute("messageCode", "import.resultOK");
				model.addAttribute("messageTimeout",10000);
			}
		}
		model.addAttribute("countries", surveyService.getCountries());
		model.addAttribute("uploadItem", new SurveyUpload());
		model.addAttribute("context","export");
		return "admin";
	}

	@RequestMapping(value = "/export/{country}", method = RequestMethod.GET)
	public @ResponseBody XmlSurvey handleGet(
			@PathVariable(value = "country") String country, Model model, HttpSession session, HttpServletResponse response)
					throws IllegalArgumentException, IllegalStateException, BadRequestServiceEx {

		SurveyInfo surveyInfo = new SurveyInfo();
		surveyInfo.setCountry(country);
		Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
		surveyInfo.setTimestamp(cal.getTime());

		SurveyStatus surveyStatusDto = new SurveyStatus();
		Status surveyStatus = surveyService.getStatus(country);
		try {
			BeanUtils.copyProperties(surveyStatusDto, surveyStatus);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new IllegalStateException("Error occurred while copying DTO object into entity onject");
		}

		XmlSurvey survey = new XmlSurvey();
		survey.setInfo(surveyInfo);
		survey.setSurveyStatus(surveyStatusDto);
		List<BasicValue> valList = new ArrayList<BasicValue>();
		survey.setBasicValues(valList);

		//        List<EntryItem> entryItems = bulkLoader.loadAllEntryItem();
		List<TextValue> textValues = bulkLoader.loadAllTextValues(country);
		List<NumberValue> numberValues = bulkLoader.loadAllNumericValues(country);

		for (TextValue el : textValues) {

			Long questionId = null;
			if(el != null && el.getEntryItem() != null && el.getEntryItem().getEntry() != null){
				questionId = el.getEntryItem().getEntry().getQuestion().getId();
			}
			valList.add(buildBasicValue(questionId, el.getValue(), composeEntryItemName(el.getEntryItem()),"text"));
		}

		for (NumberValue el : numberValues) {

			Long questionId = null;
			if(el != null && el.getEntryItem() != null && el.getEntryItem().getEntry() != null){
				questionId = el.getEntryItem().getEntry().getQuestion().getId();
			}
			valList.add(buildBasicValue(questionId, String.valueOf(el.getValue().doubleValue()), composeEntryItemName(el.getEntryItem()),"numeric"));
		}

		// Get the users (Just for export not for import)
		UserFilter uf = new UserFilter();
		Country countryInstance = surveyService.findCountryByISO3(country);
		if(countryInstance == null){

			throw new IllegalStateException("Country '" + country + "' doesn't exist.");
		}
		uf.setCountries(String.valueOf(countryInstance.getId()));
		List<User> users = usersServices.getAll(null, null, uf);
		List<UserDTO> usersDTO = new ArrayList<UserDTO>();
		try {
			for(User el : users){
				UserDTO uDTO = new UserDTO();
				BeanUtils.copyProperties(uDTO, el);
				List<Long> questions = new ArrayList<Long>();
				for(Question el2 : el.getQuestions()){
					questions.add(el2.getId());
				}
				uDTO.setQuestionsId(questions);
				/**
				 * Remove the password for now, in the future maybe will be requested to export and import
				 * the users, so the password must be encrypt... 
				 */
				uDTO.setPassword("********");
				usersDTO.add(uDTO);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new IllegalStateException("Error occurred while copying DTO object into entity onject");
		}
		survey.setUsers(usersDTO);

		response.setContentType("application/force-download");
		response.setHeader("Content-Disposition","attachment; filename=\"" + country +  "-surveyExport.xml\"");

		return survey;

		//        for (EntryItem el : entryItems) {
		//
		//            valList.add(buildBasicValue("", composeEntryItemName(el)));
		//        }
	}

	private static String composeEntryItemName(EntryItem el){

		CompactValue cv = new CompactValue();
		if (el != null && el.getEntry() != null && el.getEntry().getVariable() != null) {
			cv.setVariable(el.getEntry().getVariable());
		}
		cv.setRowNumber(el.getRowNumber());
		cv.setColumnNumber(el.getColumnNumber());
		String entryItemName = VariableNameUtils.buildVariableAsShortText(cv);
		// String entryItemShortName = entryItemName.replaceFirst("_fraVariable", "");
		return entryItemName;
	}

	private static BasicValue buildBasicValue(Long questionId, String content, String reference, String type){

		BasicValue val = new BasicValue();
		val.setQuestionId(questionId);
		val.setContent(content);
		val.setReference(reference);
		val.setType(type);
		return val;
	}

	public Boolean importFromXML(CommonsMultipartFile file, String countryCheck) {

		JAXBContext jc = null;
		XmlSurvey survey = null;
		Updates updates = new Updates();
		Map<Long, List<Update>> updatesMap = new HashMap<Long, List<Update>>();
		//        List<Update> updateList = new ArrayList<Update>();
		Updates removes = new Updates();

		try {

			jc = JAXBContext.newInstance(XmlSurvey.class);
			Unmarshaller um = jc.createUnmarshaller();
			survey = (XmlSurvey) um.unmarshal(file.getInputStream());
		}
		catch (IOException e) {

			LOGGER.error(e.getMessage(), e);
			return null;
		}  catch (JAXBException e) {

			LOGGER.error(e.getMessage(), e);
			return null;
		}

		SurveyInfo info = survey.getInfo();
		String country = info.getCountry();
		if(!country.equals(countryCheck)){
			return false;
		}

		Country countryInstance = surveyService.findCountryByISO3(country);
		if(countryInstance == null){
			throw new IllegalStateException("Country '" + country + "' doesn't exist.");
		}

		LOGGER.info("Starting XML import for Country " + country);

				if(survey.getBasicValues() != null){
					for(BasicValue el : survey.getBasicValues()){

						Long qId = el.getQuestionId();
						List<Update> upList = updatesMap.get(qId);
						if(upList == null){
							List<Update> newUpList = new ArrayList<Update>();
							updatesMap.put(qId, newUpList);
							upList = newUpList;
						}
						String reference = (el.getReference().startsWith("_fraVariable_"))?el.getReference():"_fraVariable_"+el.getReference();
						VariableNameUtils.VariableName varName = VariableNameUtils.buildVariable(reference, el.getContent());
						Update up = new Update();
						up.setColumn(varName.col);
						up.setCountry(country);
						up.setRow(varName.row);
						up.setValue(varName.value);
						up.setVariable(varName.variableName);

						upList.add(up);
					}

					for(Long el : updatesMap.keySet()){
						List<Update> up = updatesMap.get(el);
						updates.setQuestion(String.valueOf(el));
						updates.setUsername("IMPORTED_FROM_XML");
						updates.setUpdates(up);
						utils.updateValuesService(updates, removes);
					}
				}
				return true;
	}
}
