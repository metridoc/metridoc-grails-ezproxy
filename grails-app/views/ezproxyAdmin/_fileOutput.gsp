<g:if test="${ezproxyFiles}">

    <ul class="legend">
        <li><span class="available"></span>Available</li>
        <li><span class="done"></span>Done</li>
        %{--TODO: the commented out items below are for improved file monitoring coming in 0.53--}%
        %{--<li><span class="changed"></span>File Changed</li>--}%
        %{--<li><span class="error"></span>Error</li>--}%
    </ul>
    <br/>
    <div class="ezproxyData"><table id="ezproxyFiles">
        <tbody>
        <g:each status="count" var="file" in="${ezproxyFiles}">
            <g:if test="${count == 0}">
                <tr>
            </g:if>
            <g:if test="${count % 4 == 0 && count != 0}">
                </tr>
                <tr>
            </g:if>
            <td>
                %{--<strong><span class="doneFile">${file.name}</span></strong>--}%
                <g:if test="${file.done}">
                    <strong><span class="doneFile">${file.file.name}</span></strong>
                </g:if>
                <g:else>
                    <strong><span class="notDoneFile">${file.file.name}</span></strong>
                </g:else>
            </td>
            <g:if test="${count == ezproxyFiles.size() - 1}">
                </tr>
            </g:if>
        </g:each>
        </tbody>
    </table></div>
</g:if>
<g:else>
    There are no ezproxy files at directory ${ezproxyDirectory}
</g:else>