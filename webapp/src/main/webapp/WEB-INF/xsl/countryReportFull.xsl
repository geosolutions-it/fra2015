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
				<fo:simple-page-master master-name="Pagina1"
					page-height="29.7cm" page-width="21cm" margin-top="1.0cm"
					margin-bottom="1.0cm" margin-left="1.0cm" margin-right="1.0cm">
					<fo:region-body margin-top="0.0cm" />
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
			<fo:page-sequence master-reference="Pagina1">
				<fo:title>
					FRA 2015 – Country Report,
					<xsl:value-of select='//title' />
				</fo:title>
				<fo:flow flow-name="xsl-region-body">
					<fo:block-container height="100%">
						<xsl:call-template name="genSecondPage" />
					</fo:block-container>
				</fo:flow>
			</fo:page-sequence>
			<fo:page-sequence master-reference="PaginaA4">
				<fo:title>
					FRA 2015 – Country Report,
					<xsl:value-of select='//title' />
				</fo:title>
				<fo:static-content flow-name="xsl-region-before">
					<fo:block text-align="start" font-size="8pt" font-family="times">
						FRA 2015 – Country Report,
						<xsl:value-of select='//title' />
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
					<xsl:call-template name="genTOC" />
					<xsl:apply-templates select='//section[div[@class="page-header"]]' />
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

	<xsl:template name="genFirstPage">
		<fo:block-container break-before='page'>
			<fo:block />
			<fo:block text-align="center" margin-top="100pt">
					<fo:external-graphic src="url('img/fra2015logo.jpg')"
						width="280pt" height="102pt" content-width="scale-to-fit"
					content-height="scale-to-fit" />
			</fo:block>
			<fo:block text-align="center" font-size="30pt" font-weight="bold"
				margin-top="150pt">
				<xsl:value-of select='//title' />
			</fo:block>
			<fo:block text-align="center" font-size="24pt" font-weight="bold">
				Forestry Department
			</fo:block>
			<fo:block text-align="center" font-size="16pt">
				Food and Agriculture Organization of the United Nations
			</fo:block>
		</fo:block-container>
	</xsl:template>

	<xsl:template name="genSecondPage">
		<fo:block-container break-before='page'>
			<fo:block />
			<fo:block text-align="center" margin-top="20pt">
				<fo:external-graphic src="url('img/faologo.png')"
					width="40pt" height="40pt" content-width="scale-to-fit"
					content-height="scale-to-fit" />
			</fo:block>
			<fo:block text-align="center" font-weight="bold" margin-top="20pt">
				The Forest Resources Assessment Programme
			</fo:block>
			<fo:block text-align="justify" margin-top="20pt">
				Sustainably managed forests provide essential goods and services and thus play
				a vital part in sustainable development. Reliable and up-to-date
				information on the state of forest resources is crucial to support
				decision-making for investment and policy making in forestry and
				sustainable development.
			</fo:block>
			<fo:block text-align="justify" margin-top="20pt">
				FAO, at the request of its member countries, regularly monitors the
				world’s forests and their management and uses through the Forest
				Resources Assessment Programme. This country report forms part of
				the Global Forest Resources Assessment 2015 (FRA 2015). More
				information on the FRA 2015 process, publications and online
				database of results is available on the FRA web site
				(www.fao.org/forestry/fra ).
			</fo:block>
			<fo:block text-align="justify" margin-top="20pt">
				The Global Forest Resources Assessment process is coordinated by the
				Forestry Department at FAO headquarters in Rome. The contact person
				for matters related to FRA 2015 is:
			</fo:block>
			<fo:block margin-top="20pt">
				Kenneth MacDicken
			</fo:block>
			<fo:block>
				Senior Forestry Officer
			</fo:block>
			<fo:block>
				Global Forest Assessment and Reporting
			</fo:block>
			<fo:block>
				FAO Forestry Department
			</fo:block>
			<fo:block>
				Viale delle Terme di Caracalla
			</fo:block>
			<fo:block>
				Rome 00153, Italy
			</fo:block>
			<fo:block margin-top="20pt">
				E-mail: Kenneth.MacDicken@fao.org
			</fo:block>
			<fo:block margin-top="20pt">
				Readers can also use the following e-mail address: fra@fao.org
			</fo:block>
			<fo:block-container border="1pt solid black"
				margin-top="20pt" padding-start="10pt" padding-end="10pt"
				padding-before="10pt" padding-after="10pt">
				<fo:block text-align="center" font-weight="bold">
					DISCLAIMER
				</fo:block>
				<fo:block text-align="justify" margin-top="20pt">
					The designations employed and the presentation of material in this
					information product do not imply the expression of any opinion
					whatsoever on the part of the Food and Agriculture Organization of
					the United Nations concerning the legal status of any country,
					territory, city or area or of its authorities, or concerning the
					delimitation of its frontiers or boundaries.
				</fo:block>
				<fo:block text-align="justify" margin-top="20pt">
					The Global Forest Resources Assessment Country Report Series is
					designed to document and make available the information forming the
					basis for the FRA reports. The Country Reports have been compiled
					by officially nominated national correspondents in collaboration
					with FAO staff. Prior to finalisation, these reports were subject
					to review and revision by forestry authorities in the respective
					countries.
				</fo:block>
			</fo:block-container>
		</fo:block-container>
	</xsl:template>

	<xsl:template name="genTOC">
		<fo:block break-before='page'>
			<fo:block font-size="12pt" font-weight="bold">TABLE OF CONTENTS
			</fo:block>
			<xsl:for-each select='//div[@class="page-header"]'>
				<fo:block font-size="10pt" text-align-last="justify">
					<fo:basic-link internal-destination="{generate-id(.)}">
						<!-- <xsl:value-of select='count(preceding::*/section) + 1' /> <xsl:text> 
							</xsl:text> -->
						<xsl:value-of select='h1' />
						<fo:leader leader-pattern="dots" />
						<fo:page-number-citation ref-id="{generate-id(.)}" />
					</fo:basic-link>
				</fo:block>
			</xsl:for-each>
		</fo:block>
	</xsl:template>

	<xsl:template match='//section[div[@class="page-header"]]'>
		<fo:block break-before="page" text-align="start" font-size="10pt"
			font-family="times" id='{generate-id(div[@class="page-header"])}'>
			<xsl:apply-templates />
		</fo:block>
	</xsl:template>


	<xsl:template match='div[@class="page-header"]/h1'>
		<fo:block text-align="start" font-size="14pt" font-weight="bold">
			<xsl:value-of select='normalize-space(.)' />
		</fo:block>
	</xsl:template>

	<!-- <xsl:template match='section/p[@class="lead"]'> <fo:block text-align="start" 
		font-size="12pt"> <xsl:value-of select='normalize-space(.)'/> </fo:block> 
		</xsl:template> -->


	<xsl:template match='div[@class="entry"]/div'>
		<fo:block text-align="justify" border="1pt solid black"
			background-color="ghostwhite" margin-top="10pt" margin-bottom="10pt">
			<fo:block space-before="5pt" start-indent="5pt" space-after="5pt"
				end-indent="5pt">
				<xsl:apply-templates />
				<!-- <xsl:value-of select='normalize-space(.)' />  -->
			</fo:block>
		</fo:block>
	</xsl:template>

  <xsl:template match='div[contains(@id,"_feedback_")]'>
    <fo:block text-align="justify" border="1pt solid black"
      background-color="Tomato" margin-top="10pt" margin-bottom="10pt">
      <fo:block space-before="5pt" start-indent="5pt" space-after="5pt"
        end-indent="5pt">
        <xsl:apply-templates />
        <!-- <xsl:value-of select='normalize-space(.)' />  -->
      </fo:block>
    </fo:block>
  </xsl:template>
  
  <xsl:template match='button'>
  </xsl:template>
   
	<xsl:template match="table">
		<fo:table table-layout="fixed" width="100%" border-collapse="collapse"
			border-spacing="2px" border="1px solid black" margin-bottom="10pt">
			<xsl:choose>
				<xsl:when test="@cols">
					<xsl:call-template name="build-columns">
						<xsl:with-param name="cols" select="concat(@cols, ' ')" />
					</xsl:call-template>
				</xsl:when>
			</xsl:choose>
			<fo:table-body>
				<xsl:apply-templates select="*" />
			</fo:table-body>
		</fo:table>
	</xsl:template>

	<xsl:template match="tr">
	   <xsl:choose>
            <xsl:when test="*">
                  <fo:table-row keep-together.within-page="always">
							      <xsl:apply-templates select="td | th | text()" />
							    </fo:table-row>
            </xsl:when>
      </xsl:choose>	
	</xsl:template>

	<xsl:template
		match="th | td[@rownumber='0'] | td[ancestor::tr[@class='defhead']]">
		<fo:table-cell min-height="20pt" padding-start="3pt" wrap-option="wrap" hyphenate="true"
			padding-end="3pt" padding-before="3pt" padding-after="3pt"
			border="1px solid black" text-align="center" background-color="LightGreen"
			display-align="center">
			<xsl:if test=".[@class='small_column']">
        <xsl:attribute name="width">4cm</xsl:attribute>
      </xsl:if>
      <xsl:if test=".[@class='verysmall_column']">
        <xsl:attribute name="width">1cm</xsl:attribute>
      </xsl:if>      
			<xsl:if test="@colspan">
				<xsl:attribute name="number-columns-spanned">
                    <xsl:value-of select="@colspan" />
                </xsl:attribute>
			</xsl:if>
			<xsl:if test="@rowspan">
				<xsl:attribute name="number-rows-spanned">
                    <xsl:value-of select="@rowspan" />
                </xsl:attribute>
			</xsl:if>
			<xsl:if
				test="@border='1' or 
                ancestor::tr[@border='1'] or
                ancestor::table[@border='1']">
				<xsl:attribute name="border-style">
                    <xsl:text>solid</xsl:text>
                </xsl:attribute>
				<xsl:attribute name="border-color">
                    <xsl:text>black</xsl:text>
                </xsl:attribute>
				<xsl:attribute name="border-width">
                    <xsl:text>1pt</xsl:text>
                </xsl:attribute>
			</xsl:if>
			<fo:block font-weight="bold" text-align="center">
				<xsl:apply-templates select="*|text()" />
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<xsl:template match="td">
		<fo:table-cell min-height="20pt" padding-start="3pt"
			padding-end="3pt" padding-before="3pt" padding-after="3pt"
			border="1px solid black">
			<xsl:if test=".[@class='verysmall_column']">
        <xsl:attribute name="width">1cm</xsl:attribute>
      </xsl:if>   
			<xsl:if test="img">
				<xsl:attribute name="display-align">center</xsl:attribute>
				<xsl:attribute name="width">1cm</xsl:attribute>
			</xsl:if>
			<xsl:if test="@colspan">
				<xsl:attribute name="number-columns-spanned">
                    <xsl:value-of select="@colspan" />
                </xsl:attribute>
			</xsl:if>
			<xsl:if test="@rowspan">
				<xsl:attribute name="number-rows-spanned">
                    <xsl:value-of select="@rowspan" />
                </xsl:attribute>
			</xsl:if>
			<xsl:if
				test="@border='1' or 
                ancestor::tr[@border='1'] or
                ancestor::thead[@border='1'] or
                ancestor::table[@border='1']">
				<xsl:attribute name="border-style">
                    <xsl:text>solid</xsl:text>
                </xsl:attribute>
				<xsl:attribute name="border-color">
                    <xsl:text>black</xsl:text>
                </xsl:attribute>
				<xsl:attribute name="border-width">
                    <xsl:text>1pt</xsl:text>
                </xsl:attribute>
			</xsl:if>
			<xsl:variable name="align">
				<xsl:choose>
					<xsl:when test="@align">
						<xsl:choose>
							<xsl:when test="@align='center'">
								<xsl:text>center</xsl:text>
							</xsl:when>
							<xsl:when test="@align='right'">
								<xsl:text>end</xsl:text>
							</xsl:when>
							<xsl:when test="@align='justify'">
								<xsl:text>justify</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>start</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:when test="ancestor::tr[@align]">
						<xsl:choose>
							<xsl:when test="ancestor::tr/@align='center'">
								<xsl:text>center</xsl:text>
							</xsl:when>
							<xsl:when test="ancestor::tr/@align='right'">
								<xsl:text>end</xsl:text>
							</xsl:when>
							<xsl:when test="ancestor::tr/@align='justify'">
								<xsl:text>justify</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>start</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:when test="ancestor::thead">
						<xsl:text>center</xsl:text>
					</xsl:when>
					<xsl:when test="ancestor::table[@align]">
						<xsl:choose>
							<xsl:when test="ancestor::table/@align='center'">
								<xsl:text>center</xsl:text>
							</xsl:when>
							<xsl:when test="ancestor::table/@align='right'">
								<xsl:text>end</xsl:text>
							</xsl:when>
							<xsl:when test="ancestor::table/@align='justify'">
								<xsl:text>justify</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>start</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>start</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<fo:block text-align="{$align}">
				<xsl:apply-templates select="*|text()" />
			</fo:block>
		</fo:table-cell>
	</xsl:template>

	<xsl:template name="build-columns">
		<xsl:param name="cols" />

		<xsl:if test="string-length(normalize-space($cols))">
			<xsl:variable name="next-col">
				<xsl:value-of select="substring-before($cols, ' ')" />
			</xsl:variable>
			<xsl:variable name="remaining-cols">
				<xsl:value-of select="substring-after($cols, ' ')" />
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="contains($next-col, 'pt')">
					<fo:table-column column-width="{$next-col}" />
				</xsl:when>
				<xsl:when test="number($next-col) > 0">
					<fo:table-column column-width="{concat($next-col, 'pt')}" />
				</xsl:when>
				<xsl:otherwise>
					<fo:table-column column-width="proportional-column-width(1)" />
				</xsl:otherwise>
			</xsl:choose>

			<xsl:call-template name="build-columns">
				<xsl:with-param name="cols" select="concat($remaining-cols, ' ')" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="img">
		<fo:block space-after="12pt" text-align="center">
			<fo:external-graphic src="{@src}">
				<xsl:if test="@width">
					<xsl:attribute name="width">
                        <xsl:choose>
                            <xsl:when test="contains(@width, 'px')">
                                <xsl:value-of select="@width" />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat(@width, 'px')" />
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
				</xsl:if>
				<xsl:if test="@height">
					<xsl:attribute name="height">
                        <xsl:choose>
                            <xsl:when test="contains(@height, 'px')">
                                <xsl:value-of select="@height" />
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat(@height, 'px')" />
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
				</xsl:if>
			</fo:external-graphic>
		</fo:block>
	</xsl:template>

	<xsl:template match="i">
		<fo:inline font-style="italic">
			<xsl:apply-templates select="*|text()" />
		</fo:inline>
	</xsl:template>

	<xsl:template match="b">
		<fo:inline font-weight="bold">
			<xsl:apply-templates select="*|text()" />
		</fo:inline>
	</xsl:template>

	<xsl:template match="br">
		<fo:block>
		</fo:block>
	</xsl:template>

	<xsl:template match="p">
		<fo:block font-size="12pt" line-height="15pt" space-after="12pt">
			<xsl:apply-templates select="*|text()" />
		</fo:block>
	</xsl:template>

	<xsl:template match="u">
	  <fo:inline text-decoration="underline">
	    <xsl:apply-templates select="*|text()"/>
	  </fo:inline>
	</xsl:template>
	
	<xsl:template match="ol">
	  <fo:list-block provisional-distance-between-starts="1cm"
	    provisional-label-separation="0.5cm">
	    <xsl:attribute name="space-after">
	      <xsl:choose>
	        <xsl:when test="ancestor::ul or ancestor::ol">
	          <xsl:text>0pt</xsl:text>
	        </xsl:when>
	        <xsl:otherwise>
	          <xsl:text>12pt</xsl:text>
	          </xsl:otherwise>
	      </xsl:choose>
	    </xsl:attribute>
	    <xsl:attribute name="start-indent">
	      <xsl:variable name="ancestors">
	        <xsl:choose>
	          <xsl:when test="count(ancestor::ol) or count(ancestor::ul)">
	            <xsl:value-of select="1 + 
	                                  (count(ancestor::ol) + 
	                                   count(ancestor::ul)) * 
	                                  1.25"/>
	          </xsl:when>
	          <xsl:otherwise>
	            <xsl:text>1</xsl:text>
	          </xsl:otherwise>
	        </xsl:choose>
	      </xsl:variable>
	      <xsl:value-of select="concat($ancestors, 'cm')"/>
	    </xsl:attribute>
	    <xsl:apply-templates select="*"/>
	  </fo:list-block>
	</xsl:template>
	
	<xsl:template match="ol/li">
	  <fo:list-item>
	    <fo:list-item-label end-indent="label-end()">
	      <fo:block>
	        <xsl:variable name="value-attr">
	          <xsl:choose>
	            <xsl:when test="../@start">
	              <xsl:number value="position() + ../@start - 1"/>
	            </xsl:when>
	            <xsl:otherwise>
	              <xsl:number value="position()"/>
	            </xsl:otherwise>
	          </xsl:choose>
	        </xsl:variable>
	        <xsl:choose>
	          <xsl:when test="../@type='i'">
	            <xsl:number value="$value-attr" format="i. "/>
	          </xsl:when>
	          <xsl:when test="../@type='I'">
	            <xsl:number value="$value-attr" format="I. "/>
	          </xsl:when>
	          <xsl:when test="../@type='a'">
	            <xsl:number value="$value-attr" format="a. "/>
	          </xsl:when>
	          <xsl:when test="../@type='A'">
	            <xsl:number value="$value-attr" format="A. "/>
	          </xsl:when>
	          <xsl:otherwise>
	            <xsl:number value="$value-attr" format="1. "/>
	          </xsl:otherwise>
	        </xsl:choose>
	      </fo:block>
	    </fo:list-item-label>
	    <fo:list-item-body start-indent="body-start()">
	      <fo:block>
	        <xsl:apply-templates select="*|text()"/>
	      </fo:block>
	    </fo:list-item-body>
	  </fo:list-item>
	</xsl:template>
          

	<xsl:template match="ul">
		<fo:list-block provisional-distance-between-starts="4mm"
			provisional-label-separation="2mm">
			<xsl:attribute name="space-after">
      <xsl:choose>
        <xsl:when test="ancestor::ul or ancestor::ol">
          <xsl:text>0pt</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>12pt</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
      
    </xsl:attribute>
    
    <!-- 
			<xsl:attribute name="start-indent">
      <xsl:variable name="ancestors">
        <xsl:choose>
          <xsl:when test="count(ancestor::ol) or count(ancestor::ul)">
            <xsl:value-of select="1 + (count(ancestor::ol) + count(ancestor::ul)) * 1.25" />
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>1</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:value-of select="concat($ancestors, 'cm')" />
    </xsl:attribute>
    -->
			<xsl:apply-templates select="*" />
		</fo:list-block>
	</xsl:template>

	<xsl:template match="ul/li">
		<fo:list-item>
			<fo:list-item-label end-indent="label-end()">
				<fo:block>&#8226;</fo:block>
			</fo:list-item-label>
			<fo:list-item-body start-indent="body-start()">
				<fo:block>
					<xsl:apply-templates select="*|text()" />
				</fo:block>
			</fo:list-item-body>
		</fo:list-item>
	</xsl:template>
	<!-- suppress default with this template <xsl:template match="text()"/> -->
	<xsl:template match="//td/text() | //th/text() | //div[@id='cell-content']">
		<xsl:value-of select="replace(., '\{\{.*\}\}', '')" />
	</xsl:template>
	
	 <!-- ============================================
    Preformatted text is rendered in a monospaced
    font.  We also have to set the wrap-option
    and white-space-collapse properties.  
    =============================================== -->

  <xsl:template match="pre">
    <fo:block font-family="monospace"
      white-space-collapse="false" wrap-option="no-wrap"
      linefeed-treatment="preserve" white-space-treatment="preserve">
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    Sample text is rendered in a slightly larger
    monospaced font. 
    =============================================== -->

  <xsl:template match="samp">
    <fo:inline font-family="monospace" font-size="110%">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    The <small> element is rendered with a relative
    font size.  That means putting one <small>
    element inside another creates really small 
    text.  
    =============================================== -->

  <xsl:template match="small">
    <fo:inline font-size="80%">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    For strikethrough text, we use the text-decoration
    property.  
    =============================================== -->

  <xsl:template match="strike">
    <fo:inline text-decoration="line-through">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    Strongly emphasized text is simply rendered
    in bold. 
    =============================================== -->

  <xsl:template match="strong">
    <fo:inline font-weight="bold">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    For subscript text, we use the vertical-align
    property and decrease the font size.  
    =============================================== -->

  <xsl:template match="sub">
    <fo:inline vertical-align="sub" font-size="75%">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    Superscript text changes the vertical-align
    property also, and uses a smaller font.
    =============================================== -->

  <xsl:template match="sup">
    <fo:inline vertical-align="super" font-size="75%">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>
	
	
	<xsl:template match="font">
    <xsl:variable name="color">
      <xsl:choose>
        <xsl:when test="@color">
          <xsl:value-of select="@color"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>black</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="face">
      <xsl:choose>
        <xsl:when test="@face">
          <xsl:value-of select="@face"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>sans-serif</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="size">
      <xsl:choose>
        <xsl:when test="@size">
          <xsl:choose>
            <xsl:when test="contains(@size, 'pt')">
              <xsl:text>@size</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '+1'">
              <xsl:text>110%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '+2'">
              <xsl:text>120%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '+3'">
              <xsl:text>130%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '+4'">
              <xsl:text>140%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '+5'">
              <xsl:text>150%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '+6'">
              <xsl:text>175%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '+7'">
              <xsl:text>200%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '-1'">
              <xsl:text>90%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '-2'">
              <xsl:text>80%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '-3'">
              <xsl:text>70%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '-4'">
              <xsl:text>60%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '-5'">
              <xsl:text>50%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '-6'">
              <xsl:text>40%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '-7'">
              <xsl:text>30%</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '1'">
              <xsl:text>8pt</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '2'">
              <xsl:text>10pt</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '3'">
              <xsl:text>12pt</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '4'">
              <xsl:text>14pt</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '5'">
              <xsl:text>18pt</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '6'">
              <xsl:text>24pt</xsl:text>
            </xsl:when>
            <xsl:when test="@size = '7'">
              <xsl:text>36pt</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>12pt</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:when>
        <xsl:otherwise> 
          <xsl:text>12pt</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <fo:inline font-size="{$size}" font-family="{$face}"
      color="{$color}">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>
  
   <!-- ============================================
    We render the <h1> by putting a horizontal rule
    and a page break before it.  We also process 
    the id attribute; if the <h1> tag has one, we 
    use it.  If not, we see if the preceding element
    is a named anchor (<a name="x"/>).  If there is
    a named anchor before the <h1>, we use the name
    of the anchor point as the id.
    =============================================== -->

  <xsl:template match="h1">
    <fo:block break-before="page">
      <fo:leader leader-pattern="rule" leader-length.maximum="100%" leader-length.optimum="100%"/>
    </fo:block>
    <fo:block font-size="28pt" line-height="32pt"
      keep-with-next="always"
      space-after="22pt" font-family="serif">
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:when test="name(preceding-sibling::*[1]) = 'a' and
                          preceding-sibling::*[1][@name]">
            <xsl:value-of select="preceding-sibling::*[1]/@name"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    <h2> is in a slightly smaller font than an <h1>,
    and it doesn't have a page break or a line.
    =============================================== -->

  <xsl:template match="h2">
    <fo:block font-size="24pt" line-height="28pt"
      keep-with-next="always" space-after="18pt"
      font-family="serif">
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

	<xsl:template match='section/h2'>
		<fo:block text-align="start" font-size="12pt" font-weight="bold"
			margin-top="10pt">
			<xsl:value-of select='normalize-space(.)' />
		</fo:block>
	</xsl:template>

  <!-- ============================================
    <h3> is slightly smaller than <h2>.
    =============================================== -->

  <xsl:template match="h3">
    <fo:block font-size="12pt" line-height="24pt"
      keep-with-next="always" space-after="14pt"
      font-family="serif">
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    <h4> is smaller than <h3>.  For the bookmarks
    and table of contents, <h4> is the lowest level
    we include.
    =============================================== -->

  <xsl:template match="h4">
    <fo:block font-size="10pt" line-height="21pt"
      keep-with-next="always" space-after="12pt"
      font-family="serif">
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

	<xsl:template match='section/div/h4'>
		<fo:block text-align="start" font-size="12pt">
			<xsl:value-of select='normalize-space(.)' />
		</fo:block>
	</xsl:template>

  <!-- ============================================
    <h5> is pretty small, and is underlined to 
    help it stand out. 
    =============================================== -->

  <xsl:template match="h5">
    <fo:block font-size="16pt" line-height="19pt"
      keep-with-next="always" space-after="12pt"
      font-family="serif" text-decoration="underline">
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    <h6> is the smallest heading of all, and is
    underlined and italicized.  
    =============================================== -->

  <xsl:template match="h6">
    <fo:block font-size="14pt" line-height="17pt"
      keep-with-next="always" space-after="12pt"
      font-family="serif" font-style="italic"
      text-decoration="underline">
      <xsl:attribute name="id">
        <xsl:choose>
          <xsl:when test="@id">
            <xsl:value-of select="@id"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:value-of select="generate-id()"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>
  
    <!-- ============================================
    The <kbd> element is in a slightly larger 
    monospaced text.
    =============================================== -->

  <xsl:template match="kbd">
    <fo:inline font-family="monospace" font-size="110%">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    We handle the <li> elements under the <ol> and 
    <ul> elements, so there's no <li> template here.
    =============================================== -->

  <!-- ============================================
    The <nobr> element renders differently in FOP 
    1.x than it did in earlier versions. Currently
    FOP does not support the wrap-option="no-wrap" 
    attribute on the <fo:inline> element correctly, 
    so we put it in an <fo:block> element. If you're 
    using another XSL-FO processor, try this with the 
    <fo:inline> element instead to see if it 
    renders correctly. 
    =============================================== -->

  <xsl:template match="nobr">
    <fo:block wrap-option="no-wrap">
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>
  
  
  
  
  

  
  
  
  
  
  
  
  
  <!-- ============================================
    Templates for individual HTML elements begin
    here. 
    =============================================== -->

  <!-- ============================================
    Processing for the anchor tag is complex.  First
    of all, if this is a named anchor, we write an empty
    <fo:block> with the appropriate id.  (In the special
    case that the next element is an <h1>, we ignore
    the element altogether and put the id on the <h1>.)
    Next, if this is a regular anchor and the href
    starts with a hash mark (#), we create a link with
    an internal-destination.  Otherwise, we create a
    link with an external destination. 
    =============================================== -->

  <xsl:template match="a">
    <xsl:choose>
      <xsl:when test="@name">
        <xsl:if test="not(name(following-sibling::*[1]) = 'h1')">
          <fo:block line-height="0pt" space-after="0pt" 
            font-size="0pt" id="{@name}"/>
        </xsl:if>
      </xsl:when>
      <xsl:when test="@href">
        <fo:basic-link color="blue">
          <xsl:choose>
            <xsl:when test="starts-with(@href, '#')">
              <xsl:attribute name="internal-destination">
                <xsl:value-of select="substring(@href, 2)"/>
              </xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:attribute name="external-destination">
                <xsl:value-of select="@href"/>
              </xsl:attribute>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:apply-templates select="*|text()"/>
        </fo:basic-link>
        <xsl:if test="starts-with(@href, '#')">
          <xsl:text> on page </xsl:text>
          <fo:page-number-citation ref-id="{substring(@href, 2)}"/>
        </xsl:if>
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <!-- ============================================
      We render an address element in italics.
    =============================================== -->

  <xsl:template match="address">
    <fo:block font-style="italic" space-after="12pt">
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    The big element is handled with a relative 
    font size.  That means a <big> element inside
    another <big> element will be even bigger, just
    as it is in HTML. 
    =============================================== -->

  <xsl:template match="big">
    <fo:inline font-size="120%">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    A blockquote is indented on both sides.
    =============================================== -->

  <xsl:template match="blockquote">
    <fo:block start-indent="1.5cm" end-indent="1.5cm"
      space-after="12pt">
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    We're handling <center> as a block element; if
    you use it, it creates a new paragraph that's 
    centered on the page. 
    =============================================== -->

  <xsl:template match="center">
    <fo:block text-align="center">
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    The <cite> element is rendered in italics, 
    unless it's inside an italic (<i>) element. 
    We use the parent axis to check this. 
    =============================================== -->

  <xsl:template match="cite">
    <xsl:choose>
      <xsl:when test="parent::i">
        <fo:inline font-style="normal">
          <xsl:apply-templates select="*|text()"/>
        </fo:inline>
      </xsl:when>
      <xsl:otherwise>
        <fo:inline font-style="italic">
          <xsl:apply-templates select="*|text()"/>
        </fo:inline>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- ============================================
    We render <code> inline in a monospaced font.
    =============================================== -->

  <xsl:template match="code">
    <fo:inline font-family="monospace">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    We don't do anything with the <dl> element, we
    just handle the elements it contains.  Notice
    that we're ignoring any text that appears 
    in the <dl> itself; I'm not sure if that's
    the right call.
    =============================================== -->

  <xsl:template match="dl">
    <xsl:apply-templates select="*"/>
  </xsl:template>

  <!-- ============================================
    A definition term is rendered in bold.  We 
    specify keep-with-next here, although it doesn't
    always work with FOP.
    =============================================== -->

  <xsl:template match="dt">
    <fo:block font-weight="bold" space-after="2pt"
      keep-with-next="always">
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    We handle each <dd> element as an indented block
    beneath the defined term.  If the following 
    element is another <dd>, that means it's another
    definition for the same term.  In that case, 
    we don't put as much vertical space after the 
    block. 
    =============================================== -->

  <xsl:template match="dd">
    <fo:block start-indent="1cm">
      <xsl:attribute name="space-after">
        <xsl:choose>
          <xsl:when test="name(following::*[1]) = 'dd'">
            <xsl:text>3pt</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>12pt</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:attribute>
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    The HTML <em> element is typically rendered in 
    italics. 
    =============================================== -->

  <xsl:template match="em">
    <fo:inline font-style="italic">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    We render an <hr> with a leader.  Because <hr>
    is empty, we don't have to process any child
    elements. 
    =============================================== -->

  <xsl:template match="hr">
    <fo:block>
      <fo:leader leader-pattern="rule" leader-length.maximum="100%" leader-length.optimum="100%"/>
    </fo:block>
  </xsl:template>


  <!-- ============================================
    The rarely-used <tfoot> element contains some
    number of <tr> elements; we just invoke the 
    template for <tr> here. 
    =============================================== -->

  <xsl:template match="tfoot">
    <xsl:apply-templates select="tr"/>
  </xsl:template>

  <!-- ============================================
    If there's a <th> element, we process it just 
    like a normal <td>, except the font-weight is 
    always bold and the text-align is always center. 
    =============================================== -->

  <xsl:template match="th">
    <fo:table-cell
      padding-start="3pt" padding-end="3pt"
      padding-before="3pt" padding-after="3pt">
      <xsl:if test="@border='1' or 
                    ancestor::tr[@border='1'] or
                    ancestor::table[@border='1']">
        <xsl:attribute name="border-style">
          <xsl:text>solid</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="border-color">
          <xsl:text>black</xsl:text>
        </xsl:attribute>
        <xsl:attribute name="border-width">
          <xsl:text>1pt</xsl:text>
        </xsl:attribute>
      </xsl:if>
      <fo:block font-weight="bold" text-align="center">
        <xsl:apply-templates select="*|text()"/>
      </fo:block>
    </fo:table-cell>
  </xsl:template>

  <!-- ============================================
    Just like <tfoot>, the rarely-used <thead> element
    contains some number of table rows.  We just 
    invoke the template for <tr> here. 
    =============================================== -->

  <xsl:template match="thead">
    <xsl:apply-templates select="tr"/>
  </xsl:template>

  <!-- ============================================
    The title of the document is rendered in a large
    bold font, centered on the page.  This is the 
    <title> element in the <head> in <html>.
    =============================================== -->

  <xsl:template match="title">
    <fo:block space-after="18pt" line-height="27pt" 
      font-size="24pt" font-weight="bold" text-align="center">
      <xsl:apply-templates select="*|text()"/>
    </fo:block>
  </xsl:template>

  <!-- ============================================
    Teletype text is rendered in a monospaced font.
    =============================================== -->

  <xsl:template match="tt">
    <fo:inline font-family="monospace">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>

  <!-- ============================================
    The <var> element is rendered in italics. 
    =============================================== -->

  <xsl:template match="var">
    <fo:inline font-style="italic">
      <xsl:apply-templates select="*|text()"/>
    </fo:inline>
  </xsl:template>
 
  <xsl:template match='div[@id="cell-content" and not(text()) and not(descendant::text())]'>N/A</xsl:template> 
  
</xsl:stylesheet>