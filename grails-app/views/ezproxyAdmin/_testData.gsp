%{--
  - Copyright 2013 Trustees of the University of Pennsylvania. Licensed under the
  - 	Educational Community License, Version 2.0 (the "License"); you may
  - 	not use this file except in compliance with the License. You may
  - 	obtain a copy of the License at
  - 
  - http://www.osedu.org/licenses/ECL-2.0
  - 
  - 	Unless required by applicable law or agreed to in writing,
  - 	software distributed under the License is distributed on an "AS IS"
  - 	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
  - 	or implied. See the License for the specific language governing
  - 	permissions and limitations under the License.  --}%

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
