<%--
  Created by IntelliJ IDEA.
  User: tbarker
  Date: 10/9/12
  Time: 3:31 PM
  To change this template use File | Settings | File Templates.
--%>

<div class="ezproxyData">
    <table id="ezproxyTestData" class="table table-striped table-hover">
    <thead>
    <g:each in="${headers}" var="header">
        <th>${header}</th>
    </g:each>
    </thead>
    <tbody>
    <g:each in="${rows}" var="row">
        <tr>
            <g:each in="${row}" var="cell">
                <td title="${cell}">${cell}</td>
            </g:each>
        </tr>
    </g:each>
    </tbody>
</table></div>
