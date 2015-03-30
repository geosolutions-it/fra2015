/*
 *  fra2015
 *  https://github.com/geosolutions-it/fra2015
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
package it.geosolutions.fra2015.services;

import it.geosolutions.fra2015.server.dao.EntryItemDAO;
import it.geosolutions.fra2015.server.dao.NumberValueDAO;
import it.geosolutions.fra2015.server.dao.TextValueDAO;
import it.geosolutions.fra2015.server.model.survey.EntryItem;
import it.geosolutions.fra2015.server.model.survey.NumberValue;
import it.geosolutions.fra2015.server.model.survey.TextValue;

import java.util.Arrays;
import java.util.List;

import org.hibernate.FetchMode;
import org.springframework.beans.factory.annotation.Autowired;

import com.googlecode.genericdao.search.Search;

/**
 * @author DamianoG
 * 
 * This class expose services to controller that load all the items present for a certain persisted Entity
 *
 */
public class BulkModelEntitiesLoader {

	@Autowired
	private EntryItemDAO entryItemDAO;

	@Autowired
	private TextValueDAO textValueDAO;

	@Autowired
	private NumberValueDAO textNumberDAO;

	/**
	 * Bulk loading of all persisted EntryItem 
	 * @return
	 */
	public List<EntryItem> loadAllEntryItem(){

		return entryItemDAO.findAll();
	}

	/**
	 * Bulk loading of all persisted TextValue
	 * @return
	 */
	public List<TextValue> loadAllTextValues(String iso3){

		Search searchCriteria = new Search();
		searchCriteria.addFilterEqual("country.iso3", iso3);
		searchCriteria.addFetch("entryItem.entry");
		return textValueDAO.search(searchCriteria);
	}

	/**
	 * Bulk loading of all persisted NumerciValue for country
	 * @return
	 */
	public List<NumberValue> loadAllNumericValues(String iso3){

		Search searchCriteria = new Search();
		searchCriteria.addFilterEqual("country.iso3", iso3);
		searchCriteria.addFetch("entryItem.entry");
		return textNumberDAO.search(searchCriteria);
	}

	/**
	 * Bulk loading of all persisted NumerciValue of specific questions for country
	 * @return
	 */
	public List<NumberValue> loadAllNumericValues(String iso3, Integer[] questions){

		Search searchCriteria = new Search();
		searchCriteria.addFilterEqual("country.iso3", iso3);
		searchCriteria.addFetch("entryItem.entry");
		if(questions != null && questions.length > 0) {
			searchCriteria.addFilterIn("entryItem.entry.question.id", Arrays.asList(questions));
		}
		return textNumberDAO.search(searchCriteria);
	}

	public List<EntryItem> loadTable(Integer[] questions, String table){
		Search searchCriteria = new Search();
		searchCriteria.addFetch("entry");
		searchCriteria.addFilterEqual("entry.variable", table);
		if(questions != null && questions.length > 0) {
			searchCriteria.addFilterIn("entry.question.id", Arrays.asList(questions));
		}
		searchCriteria.addSortAsc("entry.id");
		searchCriteria.addSortAsc("rowNumber");
		searchCriteria.addSortAsc("columnNumber");
		return entryItemDAO.search(searchCriteria);
	}
	
	public List<NumberValue> loadTotalValue(String iso3, Integer[] questions, String table){
		Search searchCriteria = new Search();
		searchCriteria.addFilterEqual("country.iso3", iso3);
		searchCriteria.addFetch("entryItem.entry");
		if(questions != null && questions.length > 0) {
			searchCriteria.addFilterIn("entryItem.entry.question.id", Arrays.asList(questions));
		}
		searchCriteria.addFilterEqual("entryItem.entry.variable", table);
		searchCriteria.addSortAsc("entryItem.entry.id");
		searchCriteria.addSortAsc("entryItem.rowName");
		searchCriteria.addSortAsc("entryItem.columnName");
		return textNumberDAO.search(searchCriteria);
	}
	
	public List<TextValue> loadTextValue(String iso3, Integer[] questions, String table){
		Search searchCriteria = new Search();
		searchCriteria.addFilterEqual("country.iso3", iso3);
		searchCriteria.addFetch("entryItem.entry");
		if(questions != null && questions.length > 0) {
			searchCriteria.addFilterIn("entryItem.entry.question.id", Arrays.asList(questions));
		}
		searchCriteria.addFilterEqual("entryItem.entry.variable", table);
		searchCriteria.addSortAsc("entryItem.entry.id");
		searchCriteria.addSortAsc("entryItem.rowName");
		searchCriteria.addSortAsc("entryItem.columnName");
		return textValueDAO.search(searchCriteria);
	}

	public List<EntryItem> loadAllNumericValues(List<String> iso3s, Integer[] questions){        
		Search searchCriteria = new Search();
		//searchCriteria.addFilterIn("country.iso3", iso3s);
		searchCriteria.addFilterNotNull("columnName");
		searchCriteria.addFetch("entry");
		if(questions != null && questions.length > 0) {
			searchCriteria.addFilterIn("entry.question.id", Arrays.asList(questions));
		}
		searchCriteria.addSortAsc("entry.id");
		searchCriteria.addSortAsc("rowNumber");
		searchCriteria.addSortAsc("columnNumber");
		return entryItemDAO.search(searchCriteria);
	}

	public List<NumberValue> loadAllNumericValuesWithQuestions(String iso3, Integer[] questions){
		Search searchCriteria = new Search();
		searchCriteria.addFilterEqual("country.iso3", iso3);
		searchCriteria.addFilterNotNull("entryItem.columnName");
		searchCriteria.addFetch("entryItem.entry");
		if(questions != null && questions.length > 0) {
			searchCriteria.addFilterIn("entryItem.entry.question.id", Arrays.asList(questions));
		}
		searchCriteria.addSortAsc("entryItem.entry.id");
		searchCriteria.addSortAsc("entryItem.rowName");
		searchCriteria.addSortAsc("entryItem.columnName");
		return textNumberDAO.search(searchCriteria);
	}



	public List<NumberValue> loadAllNumericValues(String iso3, Integer questions){

		Search searchCriteria = new Search();
		searchCriteria.addFilterEqual("country.iso3", iso3);
		searchCriteria.addFetch("entryItem.entry");
		searchCriteria.addFilterEqual("entryItem.entry.question.id", questions);
		return textNumberDAO.search(searchCriteria);
	}
}
