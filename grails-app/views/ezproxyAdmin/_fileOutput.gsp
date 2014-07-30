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

<g:if test="${ezproxyFiles}">

    <ul class="legend">
        <li><span class="available"></span>Available</li>
        <li><span class="done"></span>Done</li>
        <li><span class="error"></span>Error</li>
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

                <g:if test="${file.error}">
                    <strong>
                        <span class="errorFile">${file.file.name} (<g:link action="deleteFileData"
                                                                           id="${file.file.name}">delete</g:link>)</span>
                    </strong>
                </g:if>
                <g:elseif test="${file.done}">
                    <strong>
                        <span class="doneFile">${file.file.name} (<g:link action="deleteFileData"
                                                                          id="${file.file.name}">delete</g:link>)</span>
                    </strong>
                </g:elseif>
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
    There are no ezproxy files in directory <code>${ezproxyDirectory}</code>
</g:else>