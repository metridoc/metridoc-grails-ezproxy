/*
  *Copyright 2013 Trustees of the University of Pennsylvania. Licensed under the
  *	Educational Community License, Version 2.0 (the "License"); you may
  *	not use this file except in compliance with the License. You may
  *	obtain a copy of the License at
  *
  *http://www.osedu.org/licenses/ECL-2.0
  *
  *	Unless required by applicable law or agreed to in writing,
  *	software distributed under the License is distributed on an "AS IS"
  *	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
  *	or implied. See the License for the specific language governing
  *	permissions and limitations under the License.  */

package metridoc.ezproxy

class EzproxyHosts extends EzproxyBase<EzproxyHosts> {

    static Map<String, Set<String>> hostsByEzproxyId = Collections.synchronizedMap([:])

    static transients = ["hostsByEzproxyId", "url", "refUrl"]
    static mapping = {
        fileName(index: 'idx_ez_hosts_file_name')
        valid(index: "idx_ez_hosts_valid")
        urlHost(index: "idx_ez_hosts_url_host")
        ezproxyId(index: "idx_ez_hosts_ezproxy_id")
        tablePerHierarchy(false)
    }

    static constraints = {
        //size 64 accommodates for sha256 hashing
        ipAddress(maxSize: 64)
        //size 64 accommodates for sha256 hashing
        patronId(maxSize: 64, nullable: true)
        country(maxSize: 50, nullable: true)
        state(maxSize: 50, nullable: true)
        city(maxSize: 50, nullable: true)
        ezproxyId(unique: 'urlHost', maxSize: 50)
        validationError(maxSize: Integer.MAX_VALUE, nullable: true)
        dept(nullable: true)
        rank(nullable: true)
        organization(nullable: true)
        refUrlHost(nullable: true)
    }

    @Override
    void postProcess(String fileName) {
        hostsByEzproxyId.clear()
    }

    @Override
    boolean accept(Map record) {
        boolean hasEzproxyIdAndUrl = super.accept(record)

        def processed = false
        if (hasEzproxyIdAndUrl) {
            String host = new URL(record.url as String).host
            processed = alreadyProcessed(hostsByEzproxyId, record.ezproxyId as String, "urlHost", host)
        }
        def result = hasEzproxyIdAndUrl && !processed

        if (result) {
            if (log.isDebugEnabled()) {
                log.debug "record ${record} has been accepted"
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug "record ${record} has been rejected"
            }
        }
        return result
    }

    @Override
    public String toString() {
        return "EzproxyHosts{" +
                "urlHost=" + urlHost +
                ", ezproxyId=" + ezproxyId +
                '}';
    }
}
