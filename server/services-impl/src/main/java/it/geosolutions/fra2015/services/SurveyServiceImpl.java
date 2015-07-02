/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.geosolutions.fra2015.services;

import it.geosolutions.fra2015.server.dao.CountryDAO;
import it.geosolutions.fra2015.server.dao.EntryDAO;
import it.geosolutions.fra2015.server.dao.EntryItemDAO;
import it.geosolutions.fra2015.server.dao.NumberValueDAO;
import it.geosolutions.fra2015.server.dao.QuestionDAO;
import it.geosolutions.fra2015.server.dao.QuestionRevisionDAO;
import it.geosolutions.fra2015.server.dao.SurveyDAO;
import it.geosolutions.fra2015.server.dao.TextValueDAO;
import it.geosolutions.fra2015.server.model.survey.CompactValue;
import it.geosolutions.fra2015.server.model.survey.Country;
import it.geosolutions.fra2015.server.model.survey.Entry;
import it.geosolutions.fra2015.server.model.survey.EntryItem;
import it.geosolutions.fra2015.server.model.survey.NumberValue;
import it.geosolutions.fra2015.server.model.survey.Question;
import it.geosolutions.fra2015.server.model.survey.QuestionRevision;
import it.geosolutions.fra2015.server.model.survey.Status;
import it.geosolutions.fra2015.server.model.survey.SurveyInstance;
import it.geosolutions.fra2015.server.model.survey.TextValue;
import it.geosolutions.fra2015.server.model.survey.Value;
import it.geosolutions.fra2015.services.exception.BadRequestServiceEx;
import it.geosolutions.fra2015.services.exception.InternalErrorServiceEx;
import it.geosolutions.fra2015.services.exception.NotFoundServiceEx;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.genericdao.search.Search;

/**
 *
 * @author marco
 * @author Tobia Di Pisa at tobia.dipisa@geo-solutions.it
 */
public class SurveyServiceImpl implements SurveyService {

	private final static Integer ENTRIES_IN_QUESTION = 500;

	@Autowired
	private BulkModelEntitiesLoader bulkLoader;

	public abstract class ValueDAO {

		public abstract void persist(ValueDTO value);

		public abstract Value read(Long itemId, Country country);

		public abstract void merge(Value dbValue);

		public abstract void remove(long id);

		public abstract List<? extends Value> readAll(Country country, Integer questionNumber);
	}

	private static final Logger LOGGER = Logger.getLogger(SurveyServiceImpl.class);
	private CountryDAO countryDAO;
	private EntryDAO entryDAO;
	private QuestionDAO questionDAO;
	private EntryItemDAO entryItemDAO;
	private TextValueDAO textValueDAO;
	private NumberValueDAO numberValueDAO;
	private SurveyDAO surveyDAO;
	private QuestionRevisionDAO questionRevisionDAO;

	private SurveyCatalog surveyCatalog;


	/**
	 * @param surveyCatalog the surveyCatalog to set
	 */
	public void setSurveyCatalog(SurveyCatalog surveyCatalog) {
		this.surveyCatalog = surveyCatalog;
	}



	private Map<String, ValueDAO> daoMap = new HashMap<String, ValueDAO>();

	public void setSurveyDAO(SurveyDAO surveyDAO) {
		this.surveyDAO = surveyDAO;
	}



	public void setCountryDAO(CountryDAO countryDAO) {
		this.countryDAO = countryDAO;
	}

	public void setEntryDAO(EntryDAO entryDAO) {
		this.entryDAO = entryDAO;
	}

	public void setQuestionDAO(QuestionDAO questionDAO) {
		this.questionDAO = questionDAO;
	}

	public void setEntryItemDAO(EntryItemDAO entryItemDAO) {
		this.entryItemDAO = entryItemDAO;
	}

	public void setTextValueDAO(TextValueDAO textValueDAO) {
		this.textValueDAO = textValueDAO;
	}

	public void setNumberValueDAO(NumberValueDAO numberValueDAO) {
		this.numberValueDAO = numberValueDAO;
	}

	public void setQuestionRevisionDAO(QuestionRevisionDAO countryRevisionDAO) {
		this.questionRevisionDAO = countryRevisionDAO;
	}



	public SurveyServiceImpl() {
		daoMap.put("String", new ValueDAO() {
			@Override
			public void persist(ValueDTO value) {
				TextValue dbValue = new TextValue();
				dbValue.setCountry(value.getCountry());
				dbValue.setValue(value.getContent());
				dbValue.setEntryItem(value.getEntryItem());
				textValueDAO.persist(dbValue);
			}

			@Override
			public Value read(Long itemId, Country country) {
				Search searchCriteria = new Search(TextValue.class);
				searchCriteria.addFilterEqual("entryItem.id", itemId);
				searchCriteria.addFilterEqual("country.id", country.getId());
				searchCriteria.addFetch("entryItem");
				// TODO add unique constraint to TextValue
				List<TextValue> textValues = textValueDAO.search(searchCriteria);
				if (textValues.size() > 0) {
					TextValue textValue = textValues.get(0);
					textValue.setContent(textValue.getValue());
					return textValue;
				}
				return null;
			}

			@Override
			public List<? extends Value> readAll(Country country, Integer questionId) {
				Search searchCriteria = new Search(TextValue.class);
				if(questionId != null){
					searchCriteria.addFilterEqual("entryItem.entry.question.id", questionId);
				}
				searchCriteria.addFilterEqual("country.id", country.getId());
				searchCriteria.addFetch("entryItem");

				List<TextValue> ret = textValueDAO.search(searchCriteria);
				return ret;
			}

			@Override
			public void merge(Value value) {
				TextValue textValue = (TextValue) value;
				textValue.setValue(value.getContent());
				textValueDAO.merge(textValue);
			}

			@Override
			public void remove(long id) {
				textValueDAO.removeById(id);
			}
		});

		daoMap.put("Number", new ValueDAO() {
			@Override
			public void persist(ValueDTO value) {
				//try {
				if(!StringUtils.isBlank(value.getContent())){
					Number number;
					try{
						NumberFormat format = NumberFormat.getInstance(Locale.US); 
						number = format.parse(value.getContent());
					}catch(ParseException e){
						number =new Double(Double.NaN);

					}
					NumberValue dbValue = new NumberValue();
					dbValue.setCountry(value.getCountry());
					dbValue.setValue(number);
					dbValue.setEntryItem(value.getEntryItem());
					numberValueDAO.persist(dbValue);
				}
				else{
					StringBuilder sb = new StringBuilder();
					sb.append("Error when try to persist entryItem id:'").append(value).append("' the provided value is blank and the EntryItem has type 'Number', skip the value...");
					LOGGER.error(sb.toString());
				}
				/*} catch (ParseException ex) {
					throw new IllegalArgumentException("Value of item " + value.getEntryItem().getId() + "must be numeric.");
				}*/
			}

			@Override
			public Value read(Long itemId, Country country) {
				Search searchCriteria = new Search(NumberValue.class);
				searchCriteria.addFilterEqual("entryItem.id", itemId);
				searchCriteria.addFilterEqual("country.id", country.getId());
				// TODO add unique constraint to NumberValue
				List<NumberValue> numberValues = numberValueDAO.search(searchCriteria);
				if (numberValues.size() > 0) {
					NumberValue numberValue = numberValues.get(0);
					numberValue.setContent(String.valueOf(numberValue.getValue()));
					return numberValue;
				}
				return null;
			}

			@Override
			public List<? extends Value> readAll(Country country, Integer questionId) {
				Search searchCriteria = new Search(NumberValue.class);
				searchCriteria.addFilterEqual("entryItem.entry.question.id", questionId);
				searchCriteria.addFilterEqual("country.id", country.getId());
				searchCriteria.addFetch("entryItem");

				List<NumberValue> ret = numberValueDAO.search(searchCriteria);
				return ret;
			}

			@Override
			public void merge(Value value) {

				NumberValue numberValue = (NumberValue) value;
				NumberFormat format = NumberFormat.getInstance(Locale.US);
				Number number =null;
				try {
					number= format.parse(value.getContent() );
				}catch (ParseException ex) {
					number = new Double(Double.NaN);    
				}
				numberValue.setValue(number);
				numberValueDAO.merge(numberValue);

			}

			@Override
			public void remove(long id) {
				numberValueDAO.removeById(id);
			}
		});
	}

	/* (non-Javadoc)
	 * @see it.geosolutions.fra2015.services.SurveyService#updateValues(java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.String)
	 */
	@Override
	public Entry updateValues(String iso3, String entryId, Integer row, Integer col, String value) throws BadRequestServiceEx, NotFoundServiceEx {

		Entry entry = entryDAO.findByName(entryId);
		if (entry != null) {
			Search searchCriteria = new Search(EntryItem.class);
			searchCriteria.addFilterEqual("rowNumber", row);
			searchCriteria.addFilterEqual("columnNumber", col);
			searchCriteria.addFilterEqual("entry.id", entry.getId());
			List<EntryItem> items = entryItemDAO.search(searchCriteria);
			EntryItem item = null;
			if (items.isEmpty()) {
				// create a new entry item for this entry
				item = new EntryItem();
				item.setType("String"); // TODO
				item.setColumnNumber(col);
				item.setRowNumber(row);
				item.setEntry(entry);

				entry.addEntryItem(item);

				entryItemDAO.persist(item);
				entryDAO.merge(entry);
			} else {
				item = items.get(0);
			}

			// find a country with the given name
			Country country = findCountryByISO3(iso3);
			if (country == null) {
				throw new BadRequestServiceEx("Country with code " + iso3 + " does not exist.");
			}

			// retrieve previous value if it is an update
			ValueDAO valueDAO = daoMap.get(item.getType());
			Value dbValue = valueDAO.read(item.getId(), country);
			if (dbValue == null) {
				// create a new value
				ValueDTO v = new ValueDTO();
				v.setEntryItem(item);
				v.setCountry(country);
				// set value
				v.setContent(value);
				valueDAO.persist(v);
			} else {
				// update values
				dbValue.setContent(value);
				valueDAO.merge(dbValue);
			}

			return entry;
		}
		throw new BadRequestServiceEx("Entry " + entryId + " not found.");
	}

	/**
	 * UNTESTED
	 */
	// TODO untested!
	/* (non-Javadoc)
	 * @see it.geosolutions.fra2015.services.SurveyService#removeValues(java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.String)
	 */
	@Override
	public boolean removeValues(String iso3, String entryId, Integer row, Integer col, String value) throws BadRequestServiceEx, NotFoundServiceEx {

		Entry entry = entryDAO.findByName(entryId);

		if(entry == null) {
			throw new NotFoundServiceEx("Entry " + entryId + " not found.");
		}

		Search searchCriteria = new Search(EntryItem.class);
		searchCriteria.addFilterEqual("rowNumber", row);
		searchCriteria.addFilterEqual("columnNumber", col);
		searchCriteria.addFilterEqual("entry.id", entry.getId());
		List<EntryItem> items = entryItemDAO.search(searchCriteria);
		EntryItem item = null;
		if (items.isEmpty()) {
			return false;
		} else {
			item = items.get(0);
		}

		// find a country with the given name
		Country country = findCountryByISO3(iso3);
		if (country == null) {
			throw new NotFoundServiceEx("Country with code " + iso3 + " does not exist.");
		}

		// retrieve previous value if it is an update
		ValueDAO valueDAO = daoMap.get(item.getType());
		Value dbValue = valueDAO.read(item.getId(), country);
		if (dbValue == null) {
			return false;
		} else {
			valueDAO.remove(dbValue.getId());
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see it.geosolutions.fra2015.services.SurveyService#getQuestionCountryValues(java.lang.String, java.lang.Integer)
	 */
	@Override
	public List<CompactValue> getQuestionCountryValues(String iso3, Integer questionId) throws BadRequestServiceEx, InternalErrorServiceEx {

		return getCompactValues(iso3, questionId);
	}

	/* (non-Javadoc)
	 * @see it.geosolutions.fra2015.services.SurveyService#upsert(it.geosolutions.fra2015.server.model.survey.Entry)
	 */
	@Override
	public void upsert(Entry entry) throws BadRequestServiceEx, NotFoundServiceEx {
		List<EntryItem> items = entry.getEntryItems();
		if (items != null) {
			for (EntryItem item : items) {
				item.setEntry(entry);
				// entryItemDAO.persist(item);
			}
		}
		entryDAO.persist(entry);
	}

	/* (non-Javadoc)
	 * @see it.geosolutions.fra2015.services.SurveyService#upsert(it.geosolutions.fra2015.server.model.survey.Question)
	 */
	@Override
	public void upsert(Question question) throws BadRequestServiceEx, NotFoundServiceEx {
		for(Entry entry : question.getEntries()){
			entry.setQuestion(question);
			if(entry.getEntryItems() != null){
				for(EntryItem entryItems : entry.getEntryItems()){
					entryItems.setEntry(entry);
				}
			}
		}
		questionDAO.persist(question);
	}

	/* (non-Javadoc)
	 * @see it.geosolutions.fra2015.services.SurveyService#changeStatus(it.geosolutions.fra2015.server.model.survey.Status)
	 */
	@Override
	public String changeStatus(Status status) throws BadRequestServiceEx, NotFoundServiceEx {
		SurveyInstance survey = surveyDAO.findByCountry( status.getCountry() );
		if ( survey != null ){
			survey.getStatus().setMessage( status.getMessage() );
			survey.getStatus().setStatus( status.getStatus() );			
			survey.getStatus().setRevisionNumber( status.getRevisionNumber() );
			survey.getStatus().setLastSurveyReview( status.getLastSurveyReview() );
			survey.getStatus().setPreviousPendingFix( status.getPreviousPendingFix() );
			survey.getStatus().setLastContributorSubmission( status.getLastContributorSubmission() );
			survey.getStatus().setReviewerSubmit(status.getReviewerSubmit());
			survey.getStatus().setCoverage(status.getCoverage());
			survey.getStatus().setLastAcceptanceRequest(status.getLastAcceptanceRequest());
			survey.getStatus().setLastPendingFixSubmit(status.getLastPendingFixSubmit());
			survey.getStatus().setLastStatusAccepted(status.getLastStatusAccepted());
			survey.getStatus().setLastContributorSave(status.getLastContributorSave());
			surveyDAO.merge(survey);
			return survey.getStatus().getStatus();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see it.geosolutions.fra2015.services.SurveyService#getStatus(java.lang.String)
	 */
	@Override
	public Status getStatus(String iso3) {
		SurveyInstance survey = surveyDAO.findByCountry(iso3);
		if (survey != null) {
			return survey.getStatus();
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see it.geosolutions.fra2015.services.SurveyService#getSurveysByCountry(java.lang.String[], int, int, java.lang.String)
	 */
	@Override
	public List<SurveyInstance> getSurveysByCountry(String[] countries,int page,int entries, String orderBy){
		return surveyDAO.findByCountries(countries, page, entries, orderBy);
	}

	/* (non-Javadoc)
	 * @see it.geosolutions.fra2015.services.SurveyService#getSurveysByCountryTimestamp(java.lang.String[], int, int, java.lang.String)
	 */
	@Override
	public List<SurveyInstance> getSurveysByCountrySortTimestamp(String[] countries,int page,int entries, String orderBy, String sortType){
		return surveyDAO.getSurveysByCountrySortTimestamp(countries, page, entries, orderBy, sortType);
	}

	/**
	 * retuns a country with the given name, null otherwise
	 *
	 * @param countryName
	 * @return
	 */
	private Country findCountryByName(String name) {
		Search searchCriteria = new Search(Country.class);
		searchCriteria.addFilterEqual("name", name);
		List<Country> countries = countryDAO.search(searchCriteria);
		if (countries.size() > 0) {
			return countries.get(0);
		}
		return null;
	}

	@Override
	public List<Country> getCountries() {
		Search searchCriteria = new Search(Country.class);
		searchCriteria.addSortAsc("name");
		List<Country> countries = countryDAO.search(searchCriteria);
		return countries;
	}

	@Override
	public QuestionRevision findQuestionRevision(Country country, Question question) {
		Search searchCriteria = new Search(QuestionRevision.class);
		searchCriteria.addFilterEqual("country", country);
		searchCriteria.addFilterEqual("question", question);
		List<QuestionRevision> qr = questionRevisionDAO.search(searchCriteria);
		if (qr.size() > 0) {
			return qr.get(0);
		}
		return null;
	}

	@Override
	public boolean updateQuestionRevision(QuestionRevision revision) {

		QuestionRevision lastRevision = findQuestionRevision(revision.getCountry(),
				revision.getQuestion());
		Long version = lastRevision.getVersion();
		if (lastRevision.getVersion() <= revision.getVersion()) {
			if (!questionRevisionDAO.remove(revision)) {
				return false;
			}
			else{
				lastRevision.setVersion(version+1);
				questionRevisionDAO.merge(lastRevision);
				return true;
			}
		}

		return false;
	}

	@Override
	public Country findCountryByISO3(String iso3) {
		Search searchCriteria = new Search(Country.class);
		searchCriteria.addFilterEqual("iso3", iso3);
		List<Country> countries = countryDAO.search(searchCriteria);
		if (countries.size() > 0) {
			return countries.get(0);
		}
		return null;
	}

	private List<CompactValue> getCompactValues(String iso3, Integer questionNumber) throws BadRequestServiceEx {

		// find a country with the given name
		Country country = findCountryByISO3(iso3);
		if (country == null) {
			throw new BadRequestServiceEx("Country with code " + iso3 + " does not exist.");
		}

		List<CompactValue> values = new ArrayList<CompactValue>(ENTRIES_IN_QUESTION);

		List<NumberValue> numberValues = bulkLoader.loadAllNumericValues(iso3,questionNumber);
		for (Value value : numberValues) {
			if ("NaN".equalsIgnoreCase(value.getContent())){
				value.setContent("N/A");
			}
			EntryItem item = value.getEntryItem();
			CompactValue compact = new CompactValue(
					item.getEntry().getVariable(),
					item.getRowNumber(),
					item.getColumnNumber(),
					value.getContent());
			values.add(compact);
		}
		List<TextValue> textValues = bulkLoader.loadTextNumericValues(iso3,questionNumber);
		for (Value value : textValues) {
			EntryItem item = value.getEntryItem();
			CompactValue compact = new CompactValue(
					item.getEntry().getVariable(),
					item.getRowNumber(),
					item.getColumnNumber(),
					value.getContent());
			values.add(compact);
		}


		/*
        for (Map.Entry<String, ValueDAO> daoEntry : daoMap.entrySet()) {
            String typeName = daoEntry.getKey();
            ValueDAO valueDAO = daoEntry.getValue();
            LOGGER.debug("Loading values for Country:"+iso3 + " question:"+questionNumber + " type:"+typeName);

            List<? extends Value> valuesList = valueDAO.readAll(country, questionNumber);

            for (Value value : valuesList) {
                if(typeName.equals("Number")){
                    if ("NaN".equalsIgnoreCase(value.getContent())){
                        value.setContent("N/A");
                    }
                }
                EntryItem item = value.getEntryItem();
                CompactValue compact = new CompactValue(
                        item.getEntry().getVariable(),
                        item.getRowNumber(),
                        item.getColumnNumber(),
                        value.getContent());
                values.add(compact);
            }
        }
		 */
		return values;
	}

	public List<Value> getValues(String iso3, Integer questionNumber) throws BadRequestServiceEx {

		// find a country with the given name
		Country country = findCountryByISO3(iso3);
		if (country == null) {
			throw new BadRequestServiceEx("Country with code " + iso3 + " does not exist.");
		}
		/*
        if (questionNumber == null) {
            throw new BadRequestServiceEx("Missing question number.");
        }
		 */
		List<Value> values = new ArrayList<Value>(500);

		for (Map.Entry<String, ValueDAO> daoEntry : daoMap.entrySet()) {
			String typeName = daoEntry.getKey();
			ValueDAO valueDAO = daoEntry.getValue();
			LOGGER.debug("Loading values for Country:"+iso3 + " question:"+questionNumber + " type:"+typeName);

			List<? extends Value> valuesList = valueDAO.readAll(country, questionNumber);
			values.addAll(valuesList);
		}
		return values;
	}

	@Override
	public void insertQuestionRevision(QuestionRevision question) {
		questionRevisionDAO.persist(question);
	}


	/* (non-Javadoc)
	 * @see it.geosolutions.fra2015.services.SurveyService#findQuestion(java.lang.Long)
	 */
	@Override
	public Question findQuestion(Long questionNumber) {

		return questionDAO.find(questionNumber);
	}

	@Override
	public List<Question> getQuestions() {

		return questionDAO.findAll();
	}

	@Override
	public List<Question> getQuestions(Integer[] questionNumbers) {
		Search searchCriteria = new Search();
		searchCriteria.addFilterIn("id",Arrays.asList(questionNumbers));
		searchCriteria.addSort("id", false);
		return questionDAO.search(searchCriteria);
	}


	/* (non-Javadoc)
	 * @see it.geosolutions.fra2015.services.SurveyService#searchCountry(java.lang.String)
	 */
	@Override
	public Country searchCountry(String iso3) {
		Search searchCriteria = new Search(Country.class);
		searchCriteria.addFilterEqual("iso3", iso3);
		return countryDAO.search(searchCriteria).get(0);
	}

	@Override
	public Country findCountry(Long id) {

		return countryDAO.find(id);
	}


	@Override
	public List<Value> getEntryListByVariableName(List<String> names, String iso3) throws BadRequestServiceEx{

		Search searchCriteria = new Search(EntryItem.class);

		Country country = findCountryByISO3(iso3);
		if (country == null) {
			throw new BadRequestServiceEx("Country with code " + iso3 + " does not exist.");
		}
		searchCriteria.addFilterIn("rowName", names);
		List<Value> results = new ArrayList<Value>();
		List<EntryItem> items =  entryItemDAO.search(searchCriteria);
		for(EntryItem item : items){
			String type = item.getType();
			ValueDAO valueDAO = daoMap.get(type);
			if (valueDAO != null) {
				Value value = valueDAO.read(item.getId(), country);
				if(value!=null){

					results.add(value);
				}
			}
		}
		return results;


	}

	@Override
	public List<Value> getEntryItemsListByFieldValues(String field, List<String> fieldValues, List<String> rowNamesValue, String iso3, boolean emptyValues) throws BadRequestServiceEx{

		Search searchCriteria = new Search(EntryItem.class);

		Country country = findCountryByISO3(iso3);
		if (country == null) {
			throw new BadRequestServiceEx("Country with code " + iso3 + " does not exist.");
		}
		searchCriteria.addFilterIn(field, fieldValues);
		if(rowNamesValue != null && !rowNamesValue.isEmpty()){
			searchCriteria.addFilterIn("rowName", rowNamesValue);
		}
		List<Value> results = new ArrayList<Value>();
		List<EntryItem> items =  entryItemDAO.search(searchCriteria);
		for(EntryItem item : items){
			String type = item.getType();
			ValueDAO valueDAO = daoMap.get(type);
			if (valueDAO != null) {
				Value value = valueDAO.read(item.getId(), country);
				if(value==null && emptyValues){

					Value val = new NumberValue();
					val.setEntryItem(item);
					results.add(val);
				}
				else if(value!=null && !emptyValues){
					results.add(value);
				}
			}
		}
		return results;


	}

	private static class ValueDTO {

		private EntryItem entryItem;
		private Country country;
		private String content;

		public ValueDTO() {
		}

		public EntryItem getEntryItem() {
			return entryItem;
		}

		public void setEntryItem(EntryItem entryItem) {
			this.entryItem = entryItem;
		}

		public Country getCountry() {
			return country;
		}

		public void setCountry(Country country) {
			this.country = country;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

	}

}
