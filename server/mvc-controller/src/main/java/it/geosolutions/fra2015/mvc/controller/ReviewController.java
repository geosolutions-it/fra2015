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


import it.geosolutions.fra2015.entrypoint.model.CountryValues;
import it.geosolutions.fra2015.mvc.controller.utils.ControllerServices;
import it.geosolutions.fra2015.mvc.controller.utils.ControllerServices.Profile;
import it.geosolutions.fra2015.mvc.controller.utils.FeedbackHandler;
import it.geosolutions.fra2015.mvc.controller.utils.SessionUtils;
import it.geosolutions.fra2015.mvc.controller.utils.VariableNameUtils;
import it.geosolutions.fra2015.server.model.survey.Feedback;
import it.geosolutions.fra2015.server.model.survey.Question;
import it.geosolutions.fra2015.server.model.user.User;
import it.geosolutions.fra2015.services.FeedbackService;
import it.geosolutions.fra2015.services.exception.BadRequestServiceEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author DamianoG
 * 
 */
@Controller
public class ReviewController {

    @Autowired
    private ControllerServices utils;
    
    @Autowired
    private FeedbackService feedbackService;
    
    private final Logger LOGGER = Logger.getLogger(ReviewController.class);

    @RequestMapping(value = "/survey/review/{country}/{question}", method = RequestMethod.GET)

    public String handleGet(
                @PathVariable(value = "country") String country, 
                @PathVariable(value = "question") Long question, Model model,
            HttpSession session) {
            
            
        // TODO validate country
        User su = (User) session.getAttribute("sessionUser");
        if(su==null){
            return "redirect:/login";
        }
        
        User userForQuery = new User();
        
        if(su.getRole().equalsIgnoreCase(Profile.REVIEWER.toString())){
            
            model.addAttribute("profile", Profile.REVIEWER.toString());
            setupAllowedQuestions(question, su, model);
            userForQuery = su;
        }
        else if(su.getRole().equalsIgnoreCase(Profile.EDITOR.toString())){
            
            model.addAttribute("profile", ControllerServices.Profile.EDITOR.toString());
            model.addAttribute("context", "survey");
            model.addAttribute("question", question);
            userForQuery = null;
        }
        else{
            return "redirect:/login";
        }
        
        CountryValues cvalues = SessionUtils.retrieveQuestionValueAndStoreInSession(utils, session, question, country);
        utils.prepareHTTPRequest(model, question.toString(), cvalues, false);
        
        FeedbackHandler fh = new FeedbackHandler(utils, feedbackService);
        try {
            
            List<Feedback> listF = fh.retrieveFeedbacks(country, question, model, session, userForQuery);
            if(Profile.EDITOR.toString().equalsIgnoreCase(su.getRole())){
                
                listF = fh.packageFeedbacks(listF);
            }
            fh.prepareFeedbackModel(model, listF);
        } 
        catch (BadRequestServiceEx e) {
            
            model.addAttribute("messageType", "warning");
            model.addAttribute("messageCode", "alert.savefaliure");
            LOGGER.error(e.getMessage(), e);
            return "reviewer";
        }
        
        return "reviewer";

    }

    @RequestMapping(value = "/survey/review/{country}/{question}", method = RequestMethod.POST)
    public String handlePost(HttpServletRequest request,
            @PathVariable(value = "country") String country,
            @PathVariable(value = "question") String question, HttpSession session, Model model) {
        
        model.addAttribute("question", question);
        model.addAttribute("context", "survey");
        
        // TODO validate country
        User su = (User) session.getAttribute("sessionUser");
        boolean harmonized = true;
        if(su==null){
            return "redirect:/login";
        }
        if(su.getRole().equalsIgnoreCase(Profile.REVIEWER.toString())){
            
            harmonized = false;
        }
        else if(su.getRole().equalsIgnoreCase(Profile.EDITOR.toString())){
            
            harmonized = true;
        }
        else{
            return "redirect:/login";
        }
        // Set the parameter operationWR, the domain is "WRITE" "READ"
        model.addAttribute("profile", ControllerServices.Profile.REVIEWER.toString());
        
        CountryValues cvalues = SessionUtils.retrieveQuestionValueFromSessionOrLoadFromDB(utils, session, Long.parseLong(question), country);
        utils.prepareHTTPRequest(model, question, cvalues, false);
        setupAllowedQuestions(Long.parseLong(question), su, model);
        
        // save feedbacks
        FeedbackHandler fh = new FeedbackHandler(utils, feedbackService);
        fh.populateFeedbackList(request, session, utils, country, harmonized);
        
        try {
            feedbackService.storeFeedback(fh.getFeedbackArray());
        } catch (BadRequestServiceEx e) {
            
            model.addAttribute("messageType", "warning");
            model.addAttribute("messageCode", "alert.savefaliure");
            LOGGER.error(e.getMessage(), e);
            return "reviewer";
        }
        
        //Put feedback in model
        for(Feedback el : fh.getFeedbackArray()){
            
            model.addAttribute(VariableNameUtils.buildfeedbackIDfromEntryID(el.getFeedbackId()), el.getFeedback());
        }
        
        model.addAttribute("messageType","success");
        model.addAttribute("messageCode","alert.savesuccess");

        model.addAttribute("messageTimeout", 5000);
        return "reviewer";

    }
    
    private static void setupAllowedQuestions(Long question,User su, Model model){
        Set<Question> allowed = su.getQuestions();
        List<Long> allowedQuestionNumbers = new ArrayList<Long>();
        Long min = Long.MAX_VALUE;
        for (Question q : allowed) {
            allowedQuestionNumbers.add(q.getId());
            min = q.getId() < min ? q.getId():min;
        }
        //set current question if not available
        if (!allowedQuestionNumbers.contains(question)){
            question = min;
        }
        model.addAttribute("allowedQuestions",allowedQuestionNumbers);
        model.addAttribute("context", "survey");
        model.addAttribute("question", question);
    }
}
