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