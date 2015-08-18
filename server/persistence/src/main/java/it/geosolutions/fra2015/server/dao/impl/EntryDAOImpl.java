/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.geosolutions.fra2015.server.dao.impl;

import it.geosolutions.fra2015.server.dao.EntryDAO;
import it.geosolutions.fra2015.server.model.survey.Entry;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Search;

/**
 *
 * @author marco
 */
@Transactional(value = "fra2015TransactionManager")
public class EntryDAOImpl extends BaseDAO<Entry, Long> implements EntryDAO{

   @Override
    public List<Entry> search(ISearch search) {
        return super.search(search);
    }

    @Override
    public void persist(Entry... entities) {
        super.persist(entities);
    }

    @Override
    public Entry merge(Entry entity) {
        return super.merge(entity);
    }

    @Override
    public boolean remove(Entry entity) {
        return super.remove(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public Entry findByName(String variable) {
         Search searchCriteria = new Search(Entry.class);
         searchCriteria.addFilterEqual("variable", variable);
         List<Entry> entries = this.search(searchCriteria);
         if ( entries.size() > 0){
             Entry entry = entries.get(0);
             Hibernate.initialize(entry.getEntryItems());
             return entry;
         }
         return null;
    }
}
