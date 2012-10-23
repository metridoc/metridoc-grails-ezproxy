<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 10/16/12
  Time: 2:24 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
</head>
<body>
<g:if test="${ezproxyFiles}">
    <table id="ezproxyFiles">
        <tbody>
        <g:each status="count" var="file" in="${ezproxyFiles}">
            <g:if test="${count == 0}">
                <tr>
            </g:if>
            <g:if test="${count % 5 == 0 && count != 0}">
                </tr>
                <tr>
            </g:if>
            <td>${file.name}</td>
            <g:if test="${count == ezproxyFiles.size() - 1}">
                </tr>
            </g:if>
        </g:each>
        </tbody>
    </table>
</g:if>
<g:else>
    There are no ezproxy files at directory ${ezproxyDirectory}
</g:else>
</body>
</html>