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
package it.geosolutions.fra2015.server.dao;

import java.util.LinkedList;

import it.geosolutions.fra2015.server.model.survey.Entry;
import it.geosolutions.fra2015.server.model.survey.EntryItem;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

/**
 * @author DamianoG
 *
 */
public class EntryDAOTest extends TestCase {

    protected final static Logger log = Logger.getLogger(EntryDAOTest.class);
    protected static EntryDAO entryDAO;
    protected static EntryItemDAO entryItemDAO;
    
    protected static ClassPathXmlApplicationContext ctx = null;
    
    public EntryDAOTest() {

        synchronized (BaseDAOTest.class) {
            if (ctx == null) {
                String[] paths = {
                    "applicationContext.xml"
                };
                ctx = new ClassPathXmlApplicationContext(paths);
                
                entryDAO = (EntryDAO) ctx.getBean("entryDAO");
                entryItemDAO = (EntryItemDAO) ctx.getBean("entryItemDAO");
            }
        }
    }
    
    @Test
    public void testCheckDAOs() {
        assertNotNull(entryDAO);
    }
    
    @Test
    public void testEntryItemLoading() {
        
        Entry entry = new Entry();
        entry.setEntryItems(new LinkedList<EntryItem>());
        entry.setType("Text");
        entry.setVariable("aVarName");
        entryDAO.persist(entry);
        
        EntryItem item = new EntryItem();
        item.setType("String"); // TODO
        item.setColumnNumber(5);
        item.setRowNumber(5);
        item.setRowName("5");
        item.setEntry(entry);
        
        entryItemDAO.persist(item);
        entryItemDAO.findAll();
        
        entry.addEntryItem(item);
        entryDAO.merge(entry);
        
        entryDAO.findAll();
        Entry resEntry = entryDAO.findByName("aVarName");
        assertEquals("5", resEntry.getEntryItems().get(0).getRowName());
    }
    
}
