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
            <md:header>General Settings</md:header>

            <div class="control-group">
                <label for="ezproxyDirectory" class="control-label">Ezproxy Directory:</label>

                <div class="controls">
                    <input class="userInput" id="ezproxyDirectory" type="text" name="directory"
                           value="${ezproxyDirectory}"/>
                </div>

                <label for="ezproxyFileRegex" class="control-label">Ezproxy File Regex:</label>

                <div class="controls">
                    <input class="userInput" id="ezproxyFileRegex" type="text" name="fileFilter"
                           value="${ezproxyFileFilter}"/>
                </div>

                <label for="crossRefUserName" class="control-label">CrossRef User Name:</label>

                <div class="controls">
                    <input class="userInput" id="crossRefUserName" type="text" name="crossRefUserName"
                           value="${crossRefUserName}"/>
                </div>

                <label for="crossRefPassword" class="control-label">CrossRef Password:</label>

                <div class="controls">
                    <input class="userInput" id="crossRefPassword" type="password" name="crossRefPassword"
                           value="${crossRefPassword}"/>
                </div>

                <div class="controls">
                    <button type="submit" class="btn" name="_action_updateEzproxyParser">Update</button>
                    <g:if test="${ezproxyJobIsActive}">
                        <button type="submit" class="btn" name="_action_deactivateJob">Deactivate job</button>
                    </g:if>
                    <g:else>
                        <button type="submit" class="btn" name="_action_activateJob">Activate Job</button>
                    </g:else>
                </div>

                <br/>
                <br/>

                <g:render template="fileOutput" plugin="metridoc-ezproxy"/>


                <br/>
                <br/>
                <md:header>Paste Sample Ezproxy Data</md:header>
                <textarea id="sampleLog" name="sampleLog" class="code-box">${rawSampleData}</textarea>

                <br/>
                <br/>
                <md:header>Ezproxy Parser</md:header>
                <textarea id="sampleParser" class="code-box"
                          name="sampleParser">${ezproxyParser}</textarea>

                <g:if test="${parseException}">
                    <div class="alert alert-block alert-error">
                            <pre>${ExceptionUtils.getFullStackTrace(parseException)}</pre>
                    </div>
                </g:if>
                <g:else>
                    <g:render template="testData" plugin="metridoc-ezproxy" model="${params}"/>
                </g:else>
                <br/>

                <button type="submit" class="btn" name="_action_updateEzproxyParser">Update</button>
                <g:if test="${ezproxyJobIsActive}">
                    <button type="submit" class="btn" name="_action_deactivateJob">Deactivate job</button>
                </g:if>
                <g:else>
                    <button type="submit" class="btn" name="_action_activateJob">Activate Job</button>
                </g:else>
            </div>

        </g:form>
    </span>

</md:report>