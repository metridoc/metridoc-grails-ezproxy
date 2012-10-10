<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 10/9/12
  Time: 3:31 PM
  To change this template use File | Settings | File Templates.
--%>

<div id="ezproxyData"><table>
    <thead>
    <g:each in="${headers}" var="header">
        <th>${header}</th>
    </g:each>
    </thead>
    <tbody>
    <g:each in="${rows}" var="row">
        <tr>
            <g:each in="${row}" var="cell">
                <td>${cell}</td>
            </g:each>
        </tr>
    </g:each>
    </tbody>
</table></div>
