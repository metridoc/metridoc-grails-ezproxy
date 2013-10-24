/*
  *Copyright 2013 Trustees of the University of Pennsylvania. Licensed under the
  *	Educational Community License, Version 2.0 (the "License"); you may
  *	not use this file except in compliance with the License. You may
  *	obtain a copy of the License at
  *
  *http://www.osedu.org/licenses/ECL-2.0
  *
  *	Unless required by applicable law or agreed to in writing,
  *	software distributed under the License is distributed on an "AS IS"
  *	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
  *	or implied. See the License for the specific language governing
  *	permissions and limitations under the License.  */

package metridoc.ezproxy

import groovy.transform.ToString
import org.xml.sax.SAXParseException

class EzDoiJournal {

    String doi
    String articleTitle
    String journalTitle
    String givenName
    String surName
    String volume
    String issue
    String firstPage
    String lastPage
    Integer printYear
    Integer electronicYear
    Integer onlineYear
    String printIssn
    String electronicIssn
    String printIsbn
    String electronicIsbn

    static final int TITLE_SIZE = 400
    static String ENCODING = "utf-8"
    static final DOI_URL = {String userName, String password, String doi ->
        def doiBase = "http://www.crossref.org/openurl/?noredirect=true&pid="
        def doiEncoded = URLEncoder.encode(doi, ENCODING)
        return new URL("${doiBase}${userName}:${password}&id=${doiEncoded}")
    }

    static constraints = {
        doi(unique: true)
        journalTitle(nullable: true, maxSize: TITLE_SIZE)
        articleTitle(nullable: true, maxSize: TITLE_SIZE)
        givenName(nullable: true)
        surName(nullable: true)
        volume(nullable: true)
        issue(nullable: true)
        firstPage(nullable: true)
        lastPage(nullable: true)
        printYear(nullable: true)
        electronicYear(nullable: true)
        onlineYear(nullable: true)
        printIssn(nullable: true)
        electronicIssn(nullable: true)
        printIsbn(nullable: true)
        electronicIsbn(nullable: true)
    }

    static mapping = {

    }
}
