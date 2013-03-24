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
package it.geosolutions.fra2015.tags;

import it.geosolutions.fra2015.mvc.controller.utils.ControllerServices.Profile;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.LocaleResolver;

/**
 * @author DamianoG
 *
 */
public class FeedbackEntryTag extends ProfiledTag{
    
    private Logger LOGGER = Logger.getLogger(FeedbackEntryTag.class);
    
    private ResourceBundleMessageSource messageSource;
    private LocaleResolver localeResolver;
    private WebApplicationContext springContext;
    
    private final static String WRITE_SUFFIX = "b";
    private final static String READ_SUFFIX = "A";
    
    private String feedbackName;
    
    public int doStartTag() {
        
        switch(getProfile()){
        case  CONTRIBUTOR :
            composeContributor();
            break;
        case  REVIEWER :
            composeReviewer();
            break;
        case  EDITOR :
            composeReviewerEditor();
            break;
        default:
            composeError();
            break;
        }
        
        return (SKIP_BODY);
    }
    
    private void composeContributor() {
        
        StringBuffer feedbackID = new StringBuffer();
        String value = (String)pageContext.getRequest().getAttribute(feedbackName+"Ed_");
        boolean feedbackIsPresent = (value != null && !StringUtils.isBlank(value));
        if(feedbackIsPresent){
            try{
                JspWriter out = pageContext.getOut();
                composeStartfeedbackArea(out);
                // --- use RichTextEntry ----
                RichTextEntry rte = new RichTextEntry();
                rte.setName(feedbackName+"Ed_"/*+READ_SUFFIX*/);
                rte.setPageContext(pageContext);
                rte.forceReadMode();
                rte.doStartTag();
                // -------------------------
                composeBottomfeedbackArea(out);
            }
            catch(IOException e){
                LOGGER.error("Error in FeedbackEntry: " + e);
            }
        }
    }
    
    private void composeReviewer() {
        
        StringBuffer feedbackID = new StringBuffer();

        try{
            JspWriter out = pageContext.getOut();
            composeStartfeedbackArea(out);
            // --- use RichTextEntry ----
            RichTextEntry rte2 = new RichTextEntry();
//            +WRITE_SUFFIX
            rte2.setName(feedbackName);
            rte2.setPageContext(pageContext);
            rte2.forceWriteMode();
            rte2.doStartTag();
            // -------------------------
            composeBottomfeedbackArea(out);
        }
        catch(IOException e){
            LOGGER.error("Error in FeedbackEntry: " + e);
        }
    }
    
    private void composeReviewerEditor() {
        
        StringBuffer feedbackID = new StringBuffer();
        
        String value = (String)pageContext.getRequest().getAttribute(feedbackName);
        String valueEd = (String)pageContext.getRequest().getAttribute(feedbackName+"Ed_");
        boolean feedbackIsPresent = (value != null && !StringUtils.isBlank(value))||(valueEd != null && !StringUtils.isBlank(valueEd));
        
        if(feedbackIsPresent){
            try{
                JspWriter out = pageContext.getOut();
                composeStartfeedbackArea(out);
                // --- use RichTextEntry ----
                RichTextEntry rte = new RichTextEntry();
                rte.setName(feedbackName/*+READ_SUFFIX*/);
                rte.setPageContext(pageContext);
                rte.forceReadMode();
                rte.doStartTag();
                // -------------------------
                out.print("<br />");
                
                out.print("<div class=\"control pull-right btn btn-mini\"><a href=\"#\" >Copy the reviewer text</a></div>");
                
                out.print("<br />");
                out.print("<br />");
                // --- use RichTextEntry ----
                RichTextEntry rte2 = new RichTextEntry();
                //Little Hack: the id are placed in all jsp for all entry... so remove the last '_' for ad the EDITOR suffix
                rte2.setName(feedbackName+"Ed_"/*+WRITE_SUFFIX*/);
                rte2.setPageContext(pageContext);
                rte2.forceWriteMode();
                rte2.doStartTag();
                // -------------------------
                composeBottomfeedbackArea(out);
            }
            catch(IOException e){
                LOGGER.error("Error in FeedbackEntry: " + e);
            }
        }
    }

    private void composeError(){
        
            StringBuffer feedbackID = new StringBuffer();
            try{
                JspWriter out = pageContext.getOut();
                out.print("<p><b>Something happened wrong or this is a feedback field that is not yet full supported...</b></p>");
            }
            catch(IOException e){
                LOGGER.error("Error in FeedbackEntry: " + e);
            }
    }
    
    public void composeStartfeedbackArea(JspWriter out) throws IOException{
        out.print("<br />");
        out.print("<hr /\">");
        out.print("<div\">");
        out.print("<h3>");
        out.print(localize("feedback.title"));
        out.print("</h3>");
    }
    
    public static void composeBottomfeedbackArea(JspWriter out) throws IOException{
        out.print("<br />");
        out.print("<hr /\">");
        out.print("<div\">");
        out.print("<br />");
    }
    
    private String localize(String code) {
        if (this.springContext == null) {
            this.springContext = WebApplicationContextUtils.getWebApplicationContext(pageContext
                    .getServletContext());
        }
        if (this.messageSource == null) {
            this.messageSource = (ResourceBundleMessageSource) springContext
                    .getBean("messageSource");
        }
        if (this.localeResolver == null) {
            this.localeResolver = (LocaleResolver) springContext.getBean("localeResolver");
        }
        return messageSource.getMessage(code, null,
                localeResolver.resolveLocale((HttpServletRequest) pageContext.getRequest()));

    }
    
    /**
     * @return the feedback
     */
    public String getFeedbackName() {
        return feedbackName;
    }

    /**
     * @param feedback the feedback to set
     */
    public void setFeedbackName(String feedback) {
        this.feedbackName = feedback;
    }

    @Override
    protected void chooseMode(Profile op) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    
    @Override
    protected void chooseMode() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
}
