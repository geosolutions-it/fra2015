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
package it.geosolutions.fra2015.mvc.controller.utils;

import static it.geosolutions.fra2015.mvc.controller.utils.ControllerServices.FEEDBACK;
import static it.geosolutions.fra2015.mvc.controller.utils.ControllerServices.SESSION_USER;
import static it.geosolutions.fra2015.mvc.controller.utils.ControllerServices.SURVEY_INSTANCES;
import it.geosolutions.fra2015.server.model.survey.Entry;
import it.geosolutions.fra2015.server.model.survey.Feedback;
import it.geosolutions.fra2015.server.model.survey.Status;
import it.geosolutions.fra2015.server.model.survey.SurveyInstance;
import it.geosolutions.fra2015.server.model.user.User;
import it.geosolutions.fra2015.services.FeedbackService;
import it.geosolutions.fra2015.services.exception.BadRequestServiceEx;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.list.UnmodifiableList;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.ui.Model;

/**
 * Hold the feedback management: take as input the req and session objects, build a Feedback Instance and add to a feedback list 
 * 
 * @author DamianoG
 *
 */
public class FeedbackHandler{
    
    private static final Logger LOGGER = Logger.getLogger(FeedbackHandler.class);
    
    private static final String NO_COMMENT = "NO COMMENT PROVIDED...";
    
    private ControllerServices controllerServiceUtils;
    
    private FeedbackService feedbackService;
    
    private List<Feedback> feedbackList;
    
    public FeedbackHandler(ControllerServices controllerServiceUtils, FeedbackService feedbackService){
        
        this.controllerServiceUtils = controllerServiceUtils;
        this.feedbackService = feedbackService;
        this.feedbackList = new ArrayList<Feedback>();
    }
   
    public List<Feedback> retrieveFeedbacks(String country, Long question,
            HttpSession session, User su, Boolean harmonized) throws BadRequestServiceEx {

        Map<String, SurveyInstance> surveyInstanceMap = (Map<String, SurveyInstance>) session
                .getAttribute(SURVEY_INSTANCES);
        SurveyInstance si = surveyInstanceMap.get(country);

        List<Feedback> feedbackList = null;
        List<Feedback> harmonizedFeedbackList = null;
        try {

            feedbackList = feedbackService.loadFeedback(su, si, question);
            harmonizedFeedbackList = feedbackService.loadHarmonizedfeedbacks(si, question);
        } catch (BadRequestServiceEx e) {

           throw new BadRequestServiceEx("Errors loading feedbacks...");
        }
        feedbackList.addAll(harmonizedFeedbackList);
        return feedbackList;
    }
    
    public void storeFeedbacks() throws BadRequestServiceEx{
        
        if(this.feedbackList != null){
            feedbackService.storeFeedback(this.getFeedbackArray());
        }
        else{
            
            throw new BadRequestServiceEx("feedbackList equals to null");
        }
    }
    
    public void mergefeedbacks(List<Feedback> oldFeedbacks){
        
        List<Feedback> feedbacksMerged = new ArrayList<Feedback>();
        for(Feedback el : feedbackList){
            
            int oldFbIndex = oldFeedbacks.indexOf(el);
            if(oldFbIndex >= 0){
                Feedback oldFb = oldFeedbacks.get(oldFbIndex);
                oldFb.setStatus(el.getStatus());
                String feedbackValue = (el.getStatus().equals("ok"))?"":el.getFeedback();
                oldFb.setFeedback(feedbackValue);
                oldFb.setTimestamp(el.getTimestamp());
                feedbacksMerged.add(oldFb);
            }
            else if(!StringUtils.isEmpty(el.getFeedback()) || !el.getStatus().equals("not")){
                
                feedbacksMerged.add(el);
            }
        }
        feedbackList = feedbacksMerged;
        
    }
    
    public List<Feedback> packageFeedbacks(List<Feedback> feedbacks, boolean packageAlsoArmonized, Status status){
        
        List<Feedback> packagedFeedbacks = new ArrayList<Feedback>();
        Map<String, Feedback> packagedFeedbacksMap = new HashMap<String, Feedback>(); 
        String recordKO = loadTemplatePackaged().get(0);
        String recordOK = loadTemplatePackaged().get(1);
        
        for(Feedback el : feedbacks){
            Feedback f = new Feedback();
            if(!el.getHarmonized()){
                f = packagedFeedbacksMap.remove(el.getFeedbackId());
                if(f == null){
                    f = new Feedback();
                    BeanUtils.copyProperties(el, f);
                    f.setFeedback("");
                }
            }
            
            String record = (el.getStatus().equals("ok"))?recordOK:recordKO;
            String colorClass = "";
            if(!StatusUtils.getReviewerSubmit(status).contains(el.getUser().getUsername())){
                       colorClass= "alert alert-warning";
            } else {
                if (el.getStatus().equals("ok")) {
                    colorClass = "alert alert-success";
                }else  if (el.getStatus().equals("ko")) {
                    colorClass = "alert alert-error";
                }else{
                    colorClass= "alert alert-warning";

                }
            }
            
            if(f.getHarmonized() != null && !f.getHarmonized()){
                
                StringBuilder sb = new StringBuilder();
                Calendar cal = GregorianCalendar.getInstance();
                cal.setTimeInMillis(el.getTimestamp());
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy.MM.dd 'at' hh:mm:ss a zzz");
                dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                sb.append(f.getFeedback());
                record = record.replace("%{colorClass}", colorClass);
                record = record.replace("%{date}", dateFormatter.format(cal.getTime()));
                record = record.replace("%{username}", el.getUser().getUsername());
                record = record.replace("%{feedbackContent}", el.getFeedback());
                record = record.replace("%{status}", el.getStatus());
                sb.append(record);
                f.setFeedback(sb.toString());
                packagedFeedbacksMap.put(f.getFeedbackId(), f);
            }
            else if(packageAlsoArmonized){
                BeanUtils.copyProperties(el, f);
                packagedFeedbacksMap.put(f.getFeedbackId()+"_Ed", f);
            }
            
        }
        
        packagedFeedbacks = new ArrayList<Feedback>(packagedFeedbacksMap.values());
        return packagedFeedbacks;
    }
    
    public void populateFeedbackList(HttpServletRequest request, HttpSession session, ControllerServices controllerServices, String countryIso3, Boolean harmonized){
        
        User user = (User)session.getAttribute(SESSION_USER);
        
        Map<String, SurveyInstance> surveyInstanceMap = (Map<String, SurveyInstance>)session.getAttribute(SURVEY_INSTANCES);
        SurveyInstance si = surveyInstanceMap.get(countryIso3);
        
        Enumeration<String> paramsNames = request.getParameterNames();
        
        while(paramsNames.hasMoreElements()){
            
            String feedbackName = (String)paramsNames.nextElement();
            if(feedbackName.startsWith(FEEDBACK)){
                
                String feedback = (String)request.getParameter(feedbackName);
                String feedbackStatus = (String)request.getParameter("STATUS"+feedbackName);
                feedbackStatus = (feedbackStatus != null)?feedbackStatus:"";
                String entryID = null;
                // This check is made in mergeFeedback now...
//                if(!StringUtils.isEmpty(feedback)){
                    entryID = VariableNameUtils.extractEntryIDfromFeedbackID(feedbackName);
                    Entry entry = controllerServices.getEntry(entryID);
                    addToFeedbackList(entry, si, user, feedback, entryID, feedbackStatus, harmonized);
//                }
                
            }
        }
        
    }
    
    public static void loadPreviousFeedbacks(Model model, FeedbackService feedbackService, HttpSession session, User su, Long question, String countryIso3){
        
        Map<String, SurveyInstance> surveyInstanceMap = (Map<String, SurveyInstance>) session.getAttribute(SURVEY_INSTANCES);
        SurveyInstance si = surveyInstanceMap.get(countryIso3);
        List<Feedback> previousFB = new ArrayList<Feedback>();
        try {
            
            previousFB = feedbackService.loadPreviousReviewFeedbacks(su, si, question);
        } catch (BadRequestServiceEx e1) {
            LOGGER.error(e1.getMessage(), e1);
        }
        for(Feedback el : previousFB){
            model.addAttribute(VariableNameUtils.buildfeedbackIDfromEntryID(el.getFeedbackId()+"_old"), el.getFeedback());
        }
    }
    
    public void prepareFeedbackModel(Model model, List<Feedback> feedbackList){
        
        if(feedbackList != null){
            
            for(Feedback el : feedbackList){
                
                String feedbackId = el.getFeedbackId();
                feedbackId = (el.getHarmonized())?feedbackId+"_Ed":feedbackId;
                model.addAttribute(VariableNameUtils.buildfeedbackIDfromEntryID(feedbackId), el.getFeedback());
                model.addAttribute(VariableNameUtils.buildfeedbackStatusIDfromEntryID(feedbackId), el.getStatus());
            }
        }
    }
    
    public List getFeedbackList(){
        
        return UnmodifiableList.decorate(feedbackList);
    }
    
    public Feedback[] getFeedbackArray(){
        
        if(feedbackList != null && !feedbackList.isEmpty()){
        
            Feedback[] array = new Feedback[feedbackList.size()]; 
            return feedbackList.toArray(array);
        }
        return null;
    }
    
    public void addToFeedbackList(Entry entry, SurveyInstance surveyInstance, User user, String feedback, String feedbackId, String status, Boolean harmonized){
        
        final Feedback f = new Feedback();
        f.setEntry(entry);
        feedback = (StringUtils.isBlank(feedback) && "ko".equals(status))?NO_COMMENT:feedback;
        f.setFeedback(feedback);
        f.setFeedbackId(feedbackId);
        f.setStatus(status);
        f.setSurvey(surveyInstance);
        f.setTimestamp(System.currentTimeMillis());
        f.setUser(user);
        f.setHarmonized(harmonized);
        feedbackList.add(f);
    }
    
    public void addAllToFeedbackList(List<Feedback> list){
        
        this.feedbackList.addAll(list);
    }
    
    private List<String> loadTemplatePackaged(){
        
        InputStream tmpltStream = this.getClass().getResourceAsStream("/packagedFeedbacksTemplate.tmplt");
        Reader r = new InputStreamReader(tmpltStream);
        List<String> templates = new ArrayList<String>();
        templates.add("%{date} - %{username} - %{feedbackContent} - %{status}<br />");
        templates.add("%{date} - %{username} - %{feedbackContent} - %{status}<br />");
        try {
            
            templates = IOUtils.readLines(r);
        } catch (IOException e) {
            
            LOGGER.error(e.getMessage(), e);
        }
        finally{
            
            if(tmpltStream != null){
                try {
                    tmpltStream.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            if(r != null){
                try {
                    r.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return templates;
    }
    public int[] getFeedbackCounter(String country,HttpSession session,boolean harmonized){
        Map<String, SurveyInstance> surveyInstanceMap = (Map<String, SurveyInstance>) session
                .getAttribute(SURVEY_INSTANCES);
        SurveyInstance si = surveyInstanceMap.get(country);
        // Update the status in surveyIstance
        si.setStatus(controllerServiceUtils.getStatusInstanceByCountry(country));
        
        if(!harmonized){
            Status s = si.getStatus();
            StatusUtils.getReviewerSubmit(s);
            return feedbackService.getFeedbackCounter(si, harmonized,StatusUtils.getReviewerSubmit(s));
        }else{
            return feedbackService.getFeedbackCounter(si, harmonized);
        }
    }
}