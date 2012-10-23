<g:if test="${ezproxyFiles}">
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
            <td>${file.name}</td>
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