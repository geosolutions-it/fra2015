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

import it.geosolutions.fra2015.server.dao.FeedbackDAO;
import it.geosolutions.fra2015.server.model.survey.Feedback;
import it.geosolutions.fra2015.services.exception.BadRequestServiceEx;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author DamianoG
 * 
 */
public class FeedbackService {

    @Autowired
    FeedbackDAO feedbackDAO;
    
    private final Logger LOGGER = Logger.getLogger(FeedbackService.class);

    public boolean storeFeedback(Feedback... feedback) throws BadRequestServiceEx{

        try {
            
            feedbackDAO.persist(feedback);
        }
        catch (Exception e) {
            
            LOGGER.error(e.getLocalizedMessage());
            throw new BadRequestServiceEx(e.getLocalizedMessage());
        }
        return true;
    }
}
