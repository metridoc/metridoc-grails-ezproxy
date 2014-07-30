%{--
  - Copyright 2013 Trustees of the University of Pennsylvania. Licensed under the
  - 	Educational Community License, Version 2.0 (the "License"); you may
  - 	not use this file except in compliance with the License. You may
  - 	obtain a copy of the License at
  - 
  - http://www.osedu.org/licenses/ECL-2.0
  - 
  - 	Unless required by applicable law or agreed to in writing,
  - 	software distributed under the License is distributed on an "AS IS"
  - 	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
  - 	or implied. See the License for the specific language governing
  - 	permissions and limitations under the License.  --}%

<%@ page import="org.apache.commons.lang.exception.ExceptionUtils; org.apache.commons.lang.SystemUtils" %>
<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 10/8/12
  Time: 3:00 PM
  To change this template use File | Settings | File Templates.
--%>

<md:report module="ezproxy">

    <span id="ezproxyParserContainer">
        <g:form name="updateEzproxyParser" class="form-horizontal">
            <md:header>Ezproxy Files</md:header>
            <br/>
            <g:render template="/ezproxyAdmin/fileOutput"/>
            <br/>
            <br/>
            <md:header>Parsed Sample Data</md:header>
            <br/>
            <g:if test="${parseException}">
                <div class="alert alert-block alert-error">
                    <pre>${ExceptionUtils.getFullStackTrace(parseException)}</pre>
                </div>
            </g:if>
            <g:else>
                <g:render template="/ezproxyAdmin/testData" model="${params}"/>
            </g:else>
            <br/>
            <br/>


            <md:header>Ezproxy Configuration</md:header>

            <g:textArea name="config" id="code" value="${config}"/>
            <br/>
            <button type="submit" class="btn" name="_action_updateEzproxyParser">Save Changes</button>
            <button type="submit" class="btn" name="_action_resetEzproxyConfig">Reset Config</button>
        </g:form>
    </span>

</md:report>