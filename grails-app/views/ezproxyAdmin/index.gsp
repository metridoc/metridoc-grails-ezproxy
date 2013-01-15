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
    <g:form name="updateEzproxyParser">
        <md:header>General Settings</md:header>

        <div/>
        <label for="ezproxyDirectory">Ezproxy Directory:</label>
        <input id="ezproxyDirectory" type="text" name="directory"
               value="${ezproxyDirectory}">
        </input>
        </div>
        <div>
            <label for="ezproxyFileRegex ">Ezproxy File Regex:</label>
            <input id="ezproxyFileRegex " type="text" name="fileFilter" value="${ezproxyFileFilter}"/>
        </div>

        <br/>
        <br/>
        <g:render template="fileOutput" plugin="metridoc-ezproxy"/>


        <br/>
        <br/>
        <md:header>Paste Sample Ezproxy Data</md:header>
        <textarea id="sampleLog" class="ui-widget-content code-box"
                  name="sampleLog">${rawSampleData}</textarea>


        <md:header>Ezproxy Parser</md:header>
        <textarea id="sampleParser" class="ui-widget-content code-box"
                  name="sampleParser">${ezproxyParser}</textarea>

        <g:if test="${parseException}">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="padding: 0 0.7em;">
                    <pre>${ExceptionUtils.getFullStackTrace(parseException)}</pre>
                </div>
            </div>
        </g:if>
        <g:else>
            <g:render template="testData" plugin="metridoc-ezproxy" model="${params}"/>
        </g:else>
        <br/>

        <div class="buttons">
            <g:actionSubmit value="Update" action="updateEzproxyParser"/>
            <g:if test="${ezproxyJobIsActive}">
                <g:actionSubmit value="Deactivate Job" action="deactivateJob"/>
            </g:if>
            <g:else>
                <g:actionSubmit value="Activate Job" action="activateJob"/>
            </g:else>
        </div>

    </g:form>
    </span>

</md:report>