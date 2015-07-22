<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:output method="xml" encoding="utf-8" indent="yes" />
  <xsl:strip-space elements="*" />
  <xsl:variable name="basePath" select="//meta[@id='baseUrl']/@content" />

  <xsl:template match="/">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
      <fo:layout-master-set>
        <fo:simple-page-master master-name="Pagina0"
          page-height="29.7cm" page-width="21cm" margin-top="1.0cm"
          margin-bottom="1.0cm" margin-left="1.0cm" margin-right="1.0cm">
          <fo:region-body margin-top="0.0cm" />
          <fo:region-after extent="3.0cm" />
        </fo:simple-page-master>
        <fo:simple-page-master master-name="PaginaA4"
          page-height="29.7cm" page-width="21cm" margin-top="1.0cm"
          margin-bottom="1.0cm" margin-left="1.0cm" margin-right="1.0cm">
          <fo:region-body margin-top="1.0cm" margin-bottom="1.0cm" />
          <fo:region-before extent="1.3cm" />
          <fo:region-after extent="0.5cm" />
        </fo:simple-page-master>
      </fo:layout-master-set>
      <fo:page-sequence master-reference="Pagina0">
        <fo:title>
          FRA 2015 – Country Report,
          <xsl:value-of select='//title' />
        </fo:title>
        <fo:static-content flow-name="xsl-region-after">
          <fo:block-container margin-left="20pt"
            margin-right="20pt">
            <fo:block>
              <fo:leader leader-pattern="rule" leader-length.optimum="100%"
                rule-thickness="1pt" />
            </fo:block>
            <fo:block text-align="start">
              Global Forest Resources Assessment 2015
            </fo:block>
            <fo:block text-align="end">
              Country Report
              <xsl:value-of select='//title' />
            </fo:block>
            <fo:block text-align="end" margin-bottom="20pt">
              Rome, 2014
            </fo:block>
          </fo:block-container>
        </fo:static-content>
        <fo:flow flow-name="xsl-region-body">
          <fo:block-container border="1pt solid black"
            height="100%">
            <xsl:call-template name="genFirstPage" />
          </fo:block-container>
        </fo:flow>
      </fo:page-sequence>
      <fo:page-sequence master-reference="PaginaA4">
        <fo:title>
          FRA 2015 – Feedback summary, <xsl:value-of select='//title' />
        </fo:title>
        <fo:static-content flow-name="xsl-region-before">
          <fo:block text-align="start" font-size="8pt" font-family="times">
              FRA 2015 – Feedback summary, <xsl:value-of select='//title' />
          </fo:block>
          <fo:block>
            <fo:leader leader-pattern="rule" leader-length.optimum="100%"
              rule-thickness="1pt" />
          </fo:block>
        </fo:static-content>
        <fo:static-content flow-name="xsl-region-after">
          <fo:block text-align="center" font-size="8pt" font-family="times">
            <fo:page-number />
          </fo:block>
        </fo:static-content>
        <fo:flow flow-name="xsl-region-body">
            <fo:block break-before="page" text-align="start" font-size="10pt" font-family="times"  id='{generate-id(.)}'> 
              <xsl:apply-templates select='//section[ div[@class="page-header" and ../descendant::div[contains(@class,"feedbackentry ko")]]]' />
            </fo:block>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>

  <xsl:template name="genFirstPage">
    <fo:block-container break-before='page'>
      <fo:block />
      <fo:block text-align="center" margin-top="100pt">
        <fo:external-graphic src="url('/img/fra2015logo.jpg')"
          width="280pt" height="102pt" content-width="scale-to-fit"
          content-height="scale-to-fit" />
      </fo:block>
      <fo:block text-align="center" font-size="30pt" font-weight="bold"
        margin-top="150pt">
        <xsl:value-of select='//title' />
      </fo:block>
        <fo:block text-align="center" font-size="12pt" margin-top="10pt">
          <xsl:value-of select="//div[@id='userName']" /> feedback <xsl:value-of select="//div[@id='profileName']" /> summary on <xsl:value-of select="//div[@id='currentDate']" />
        </fo:block>
      <fo:block text-align="center" font-size="24pt" font-weight="bold">
        Forestry Department
      </fo:block>
      <fo:block text-align="center" font-size="16pt">
        Food and Agriculture Organization of the United Nations
      </fo:block>
    </fo:block-container>
  </xsl:template>

    <xsl:template match='//section[ div[@class="page-header" and ../descendant::div[contains(@class,"feedbackentry ko")]]]'>
            
            <xsl:apply-templates />
       
    </xsl:template>
    
    <xsl:template match="h1" />
    <xsl:template match="h2" />
    <xsl:template match="h3" />
    <xsl:template match="h4" />
    <xsl:template match="table" />
    
    <xsl:template match="h1[../../*[descendant::div[contains(@class,'feedbackentry ko')]]]">
        <fo:block text-align="start" font-size="14pt" font-weight="bold">
            <xsl:value-of select='normalize-space(.)'/>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="h2[../*[descendant::div[contains(@class,'feedbackentry ko')]]]">
        <fo:block text-align="start" font-size="12pt" font-weight="bold"  margin-top="10pt">
            <xsl:value-of select='normalize-space(.)'/>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="h3[../*[descendant::div[contains(@class,'feedbackentry ko')]]]">
        <fo:block text-align="start">
            <xsl:value-of select='normalize-space(.)'/>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="h4[../*[descendant::div[contains(@class,'feedbackentry ko')]]]">
        <fo:block text-align="start" font-size="12pt">
            <xsl:value-of select='normalize-space(.)'/>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="p">
        <fo:block font-size="12pt" line-height="15pt"
            space-after="12pt">
            <xsl:apply-templates select="*|text()"/>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="button" />
       
    <xsl:template match="b">
        <fo:inline font-weight="bold">
            <xsl:apply-templates select="*|text()"/>
        </fo:inline>
    </xsl:template>
       
  <xsl:template match="div[contains(@class,'feedbackentry ko')]">
        <fo:block text-align="justify" border="1pt solid black" background-color="ghostwhite" margin-bottom="10pt">
            <fo:block space-before="5pt" start-indent="5pt"
                space-after="5pt" end-indent="5pt">
                <xsl:apply-templates />
            </fo:block>
        </fo:block>
    </xsl:template>
    
    <xsl:template match="span[@class='info']">
        <fo:block>
            <xsl:apply-templates />
        </fo:block>
    </xsl:template>
    
    <xsl:template match="div[@class='feedbackentry-body']">
      <fo:block>
        <fo:leader leader-pattern="rule" leader-length.optimum="100%" rule-thickness="1pt"/>
      </fo:block>
        <fo:block font-weight="bold">
            <xsl:apply-templates />
        </fo:block>
    </xsl:template>
    
    
    <!--
    
    

        <div class="form-signin feedbackentry" style="padding:0;">
            <div class="feedbackentry-head alert alert-warning">
                <b>On </b> 2013.05.17 at 10:38:15 AM UTC 
                <b>User</b> rev1 
                <b>Status</b> ko 
                </div><div class="feedbackentry-body">dccdcdcd</div></div></div>
    
    -->
</xsl:stylesheet>