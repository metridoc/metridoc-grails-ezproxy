<%@ page import="org.apache.commons.lang.SystemUtils; org.apache.maven.artifact.ant.shaded.ExceptionUtils" %>
<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 10/8/12
  Time: 3:00 PM
  To change this template use File | Settings | File Templates.
--%>

<md:report module="ezproxy">

    <span id="ezproxyParserContainer">
    <g:formRemote method="POST" name="updateEzproxyParser" url="[action: 'updateEzproxyParser']"
                  onComplete="window.location.reload()">
        <md:header>General Settings</md:header>

        <div>
            <label for="ezproxyDirectory">Ezproxy Directory:</label>
        <input id="ezproxyDirectory" type="text" name="ezproxyDirectory"
               value="${ezproxyDirectory}">
        </input>
        </div>
        <div>
            <label for="ezproxyFileRegex">Ezproxy File Regex:</label>
            <input id="ezproxyFileRegex" type="text" name="ezproxyFileRegex" value="${ezproxyFileFilter}"/>
            <br />
            <br />
            <tmpl:fileOutput/>
        </div>

        <br/>
        <br/>
        <md:header>Paste Sample Ezproxy Data</md:header>
        <textarea id="rawEzproxyData" class="ui-widget-content code-box"
                  name="rawEzproxyData">${rawSampleData}</textarea>


        <md:header>Ezproxy Parser</md:header>
        <textarea id="ezproxyParserCode" class="ui-widget-content code-box"
                  name="ezproxyParserScript">${ezproxyParser}</textarea>

        <g:if test="${parseException}">
            <div class="ui-widget">
                <div class="ui-state-error ui-corner-all" style="padding: 0 0.7em;">
                    <pre>${ExceptionUtils.getFullStackTrace(parseException)}</pre>
                </div>
            </div>
        </g:if>
        <g:else>
            <tmpl:testData model="${params}"/>
        </g:else>
        <br/>

        <div class="buttons">
            <g:actionSubmit value="Update"/>
        </div>

    </g:formRemote>
    </span>

</md:report>