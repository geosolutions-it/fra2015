<%-- 
    Document   : template
    Created on : 2-ott-2012, 10.11.06
    Author     : francesco
--%>
<%@page import="it.geosolutions.fao.webapp.rte.RTEConfigurationHelper"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://ckeditor.com" prefix="ckeditor" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>FRA 2015 Introduction</title>
        <link href="includes/css/minimal.css" type="text/css" rel="stylesheet"/>
        <script src="includes/jquery-1.8.2.min.js" type="text/javascript"></script>
        <script>
            
            $(document).ready(function(){
                
                $("#bSave").click(function(event) {
                    
                    var value = CKEDITOR.instances['editor'];
                    
                    window.richtext = value.getData();
                   
                });
                
                $("#bReload").click(function(event) {
                    
                    var value = CKEDITOR.instances['editor'];
                    
                    value.setData(window.richtext);
                   
                });
                
                

            });
        </script>

    </head>
    <body>
        <%@include file="WEB-INF/jspf/header.jspf" %>
        <div id="main">
            <div id="topmenu">
                <%@include file="WEB-INF/jspf/sectionmenu.jspf" %>
            </div>
            <div class="tablelayout">
                &nbsp;
                <div class="navigation">
                    <%@include file="WEB-INF/jspf/introductionmenu.jspf" %>
                </div>
                <div class="content">
                    <form id="form1" action="introduction.jsp" method="post">
                        <label for="editor">Enter text here:</label>
                        <textarea cols="80" name="editor" rows="10"></textarea>
                        <br/>
                        <input id="bReload" type="button" value="Cancel"/>
                        <input id="bSave" type="button" value="Save"/>
                    </form>
                </div>


            </div>
        </div>
        <%@include file="WEB-INF/jspf/footer.jspf" %>
        <ckeditor:replace replace="editor" basePath="/WebApp/ckeditor/"
                          config="<%= RTEConfigurationHelper.createConfig()%>"
            
             />
    </html>
