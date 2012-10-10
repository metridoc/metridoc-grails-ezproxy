<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 10/8/12
  Time: 3:00 PM
  To change this template use File | Settings | File Templates.
--%>

<md:report module="ezproxy">
    <md:header>General Settings</md:header>

    <md:header>Upload Sample Ezproxy Files</md:header>
    <g:uploadForm method="post" action="save">
        <input type="file" name="file">
        <span class=buttons>
            <input type="submit">
        </span>
    </g:uploadForm>

    <g:each in="${availableFiles}" var="file">
        <div class="ezproxy-file-item">
            ${file.name}
            <g:remoteLink action="deleteFile" params="[fileToDelete: file.name]" onComplete="window.location.reload()">
                <g:img plugin="metridoc-core" dir="images/skin" file="process-stop.png" class="delete-ezproxy-file"/>
            </g:remoteLink>
        </div>
    </g:each>

    <br/>
    <md:header>Ezproxy Parser</md:header>
    <span id="ezproxyParserContainer">
        <g:formRemote method="POST" name="saveEzproxyParser" url="[action: 'updateEzproxyParser']">
            <textarea id="ezproxyParserCode" class="ui-widget-content code-box"
                      name="ezproxyParserScript">${ezproxyParser}</textarea>
            <g:render template="testData" plugin="metridoc-ezproxy" model="${params}"/>
            <span class="buttons">
                <g:actionSubmit value="Save"/>
            </span>
        </g:formRemote>
    </span>

</md:report>