<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:b="http://www.ftn.uns.ac.rs/xpath/examples" version="2.0">

    <xsl:template match="/">
        <html>
            <head>
                <title>Bookstore (XSLT)</title>
                <style type="text/css">
                    table {
                        font-family: serif;
                        border-collapse: collapse;
                        margin: 50px auto 50px auto;
                        width: 90%;
                    }
                    th, td {
                        text-align: left;
                        padding: 30px;
                    }
                    tr:nth-child(even){ background-color: #f2f2f2 }
                    th {
                        background-color: #4caf50;
                        font-family: sans-serif;
                        color: white;
                    }
                    tr { border: 1px solid darkgrey; }
                    tr:hover {
                        font-style: italic;
                        background-color: #cae8cb;
                    }
                    body { font-family: sans-serif; }
                    p { text-indent: 30px; }
                    .sup {
                        vertical-align: super;
                        padding-left: 4px;
                        font-size: small;
                        text-transform: lowercase;
                    }
                    
                </style>
            </head>
            <body>
                <h1>Bookstore (XSLT)</h1>
                <p>Total number of books in the bookstore: 
                    <b><xsl:value-of select="count(b:bookstore/b:book)"/></b>
                </p>
                <h2>Available books:</h2>
                <p>Highlighting (*) the book titles with a <b>price less than $40</b>.</p>
                <table style="border: 1px">
                    <tr bgcolor="#9acd32">
                        <th>#</th>
                        <th>Category</th>
                        <th>Title<span class="sup">category</span></th>
                        <th>Author</th>
                        <th>Year</th>
                        <th>Price</th>
                    </tr>
                    <xsl:variable name="amount" select="sum(//b:book/b:price)"/>
                    <xsl:variable name="count" select="count(//b:book)"/>
                    <xsl:for-each select="b:bookstore/b:book">
                        <xsl:sort select="@category"/>
                        <xsl:sort select="b:price"/>
                        <tr>
                            <!-- Ordinal number -->
                            <td><xsl:value-of select="position()"/></td>
                            
                            <!-- Selects: category attribute -->
                            <td>
                                <xsl:value-of select="@category"/>
                            </td>
                            <!-- Selects: book title -->
                            <td>
                                <b>
                                <xsl:choose>
                                    <xsl:when test="b:price &lt; 40">
                                        * <xsl:value-of select="b:title"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:value-of select="b:title"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                                </b>
                                <!-- Selects: category attribute -->
                                <span class="sup"><xsl:value-of select="@category"/></span>
                            </td>
                            
                            <!-- Selects: book author(s) -->
                            <td>
                            <xsl:if test="count(b:author) = 1">
                                <xsl:value-of select="b:author"/>
                            </xsl:if>    
                            <xsl:if test="count(b:author) &gt; 1">
                                <!--<ol>
                                <xsl:for-each select="b:author">
                                    <li><xsl:value-of select="text()"/></li>
                                </xsl:for-each>
                                </ol>-->
                                <xsl:for-each select="b:author">
                                    <xsl:value-of select="text()"/><br />
                                </xsl:for-each>
                            </xsl:if>
                            </td>
                            
                            <!-- Selects: year of publishing -->
                            <td>
                                <xsl:value-of select="b:year"/>
                            </td>
                            
                            <!-- Selects: book price -->
                            <td>
                                <b>($<xsl:value-of select="b:price"/>)</b>
                            </td>
                        </tr>
                    </xsl:for-each>
                    <tr>
                        <td colspan="5"><b>Average price:</b></td>
                        <td><b>$<xsl:value-of select="round($amount div $count * 100) div 100"/></b></td>
                    </tr>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
