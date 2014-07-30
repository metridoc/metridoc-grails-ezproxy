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

import org.apache.commons.lang.SystemUtils

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 11/14/12
 * Time: 3:13 PM
 * To change this template use File | Settings | File Templates.
 */
class EzproxyUtils {
    static final DEFAULT_LOG_DATA = """124.193.247.47||Beijing||22||China||-||-||[31/Dec/2010:00:00:01 -0500]||GET||https://proxy.library.upenn.edu:443/||302||0||-||Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)]||-||-
72.162.123.139||Philadelphia||PA||United States||Default+datasets+documents+pwp+vanwert||lipings||[31/Dec/2010:00:00:01 -0500]||GET||http://www.sciencedirect.com:80/science?_ob=MImg&_imagekey=B6THY-4X4Y21M-4-5&_cdi=5295&_user=489256&_pii=S0169433209012380&_origin=search&_coverDate=12%2F15%2F2009&_sk=997439994&view=c&wchp=dGLbVlb-zSkzV&md5=888dc249bd136d1ef0b7e0c8cf24b136&ie=/sdarticle.pdf||200||407638||-||Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; GTB6.6; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; InfoPath.2; Tablet PC 2.0; .NET4.0C; MALC)]||96CV6QQh0Mclz5Z||__utma=247612227.1070650544.1272030987.1283660961.1287343337.5; __utmz=247612227.1287343337.5.3.utmccn=(organic)|utmcsr=google|utmctr=upenn|utmcmd=organic; __gads=ID=5faba231e298fe89:T=1292603848:S=ALNI_MYOmPxGoPHb_7KgunwVnJHvoEVfzw; __utma=261680716.755943438.1272235863.1293748992.1293767555.56; __utmz=261680716.1293767555.56.38.utmcsr=library.upenn.edu|utmccn=(referral)|utmcmd=referral|utmcct=/cgi-bin/res/sr.cgi; CookiesSupported=True; WT_FPC=id=138.238.122.25-534204896.30081899:lv=1281029689521:ss=1281029689521; SaneID=165.123.34.86-1278686716675999; CFID=68687465; CFTOKEN=9c5cc5731933f11d-BD20CE0E-5056-A348-096237A94CEE7604; MAID=1742093464; userId=adaa3489a59e701d8f328dd2bd578529; WOLSIGNATURE=26229058-23b7-493c-84c4-653cc9ada3b9; RemoteACC=7a6c7a47567a794972656c6170324b697a6133633762696d59444c3868306454313544456d4f482f5173427875714f2f736e324e6f673d3d; scopus.machineID=EBx_FILtqT58LLGCQUMgFMV; __utmv=261680716.institutional%20user; __utmc=261680716; JSESSIONID=00007_HX2UTiIWA6GuRn-sSVe76:15c67vngm; BROWSER_SUPPORTS_COOKIES=1; PHPSESSID=938fb22ed23858a24621face3d222cc0; __utma=94565761.1708027833.1272031026.1293746057.1293767331.38; __utmz=94565761.1293767331.38.36.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=penn%20library; hp=/; __utmc=94565761; proxySessionID=18084384; ezproxy=96CV6QQh0Mclz5Z
123.123.247.47||Beijing||22||China||-||-||[31/Dec/2010:00:00:02 -0500]||GET||http://global.factiva.com/doi/full/10.1021/jo0601009||200||0||-||Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)]||vO07NtNOHwIciIH||-
124.123.321.168||Shenzhen||30||China||-||-||[31/Dec/2010:00:00:09 -0500]||GET||http://proxy.library.upenn.edu:80/login?url=http://www.sciencedirect.com/||200||2049||-||Sosospider+(+http://help.soso.com/webspider.htm)]||-||-
59.77.33.100||Zhangzhou||07||China||-||-||[31/Dec/2010:00:00:14 -0500]||GET||http://proxy.library.upenn.edu:80/login?url=http://www.ajnr.org/||200||2049||-||Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.0.3) Gecko/2008092510 Ubuntu/8.04 (hardy) Firefox/3.0.3]||-||ezproxy=VtlzQhx2DykKc8Q
56.110.98.79||Bothell||WA||United States||Default+datasets+documents+pwp+vanwert||foo||[31/Dec/2010:00:00:15 -0500]||POST||http://global.factiva.com:80/ha/default.aspx?ftx=eastdil%20secured||200||44662||http://proxy.library.upenn.edu:2638/np/default.aspx?NAPC=P&inpt=Group||Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_8; en-us) AppleWebKit/530.19.2 (KHTML, like Gecko) Version/4.0.2 Safari/530.19]||vO07NtNOHwIciIH||__utma=94565761.635530968.1253892984.1293686436.1293688527.40; __utmc=94565761; __utmz=94565761.1293688527.40.30.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=upenn%20library; proxySessionID=18084464; UPennLibrary=AAAAAUq843cAAFe2AwPwAg==; __utma=261680716.2063844322.1263128892.1293760394.1293771521.18; __utmb=261680716.1.10.1293771521; __utmc=261680716; __utmz=261680716.1293771521.18.14.utmcsr=library.upenn.edu|utmccn=(referral)|utmcmd=referral|utmcct=/eresources/referencesources.html; __qca=1221316170-63414193-51782906; __qseg=Q_D|Q_T|Q_2892|Q_2866|Q_2865|Q_2355|Q_2354|Q_2353|Q_2349|Q_2348|Q_2344|Q_2339|Q_1940|Q_1286|Q_1160|Q_1159|Q_1158|Q_1156|Q_1150|Q_1149|Q_1148|Q_1147|Q_1141|Q_983; __unam=d7bc9e4-12cc8e31ded-425d811d-4; __utma=10244330.616540340.1293737754.1293737754.1293737754.1; __utmc=10244330; __utmz=10244330.1293737754.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=university%20of%20pennsylvania; fsr.a=1293688517342; Admn=; ARPT=KJPIKMScorpweb101v8090CKYOK; General=; login=; LSLogin=GL%5FUT=B&FP%5FUT=B&GL%5FCL=VO&FP%5FCL=VO&GL%5FRS=009999&FP%5FRS=009999&FP%5FFL=false%7CIF%3Dfalse%7CRR%3Dfalse&GL%5FFL=false%7CIF%3Dfalse%7CRR%3Dfalse&FP%5FFI=777947&GL%5FFI=777947; Mds=; Search=
56.110.98.79||Bothell||WA||United States||Default+datasets+documents+pwp+vanwert||foo||[31/Dec/2010:00:00:16 -0500]||GET||http://global.factiva.com:80/templates/gen/blank.asp||200||311||http://proxy.library.upenn.edu:2638/ha/default.aspx?ftx=eastdil%20secured||Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_8; en-us) AppleWebKit/530.19.2 (KHTML, like Gecko) Version/4.0.2 Safari/530.19]||vO07NtNOHwIciIH||__utma=94565761.635530968.1253892984.1293686436.1293688527.40; __utmc=94565761; __utmz=94565761.1293688527.40.30.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=upenn%20library; proxySessionID=18084464; UPennLibrary=AAAAAUq843cAAFe2AwPwAg==; __utma=261680716.2063844322.1263128892.1293760394.1293771521.18; __utmb=261680716.1.10.1293771521; __utmc=261680716; __utmz=261680716.1293771521.18.14.utmcsr=library.upenn.edu|utmccn=(referral)|utmcmd=referral|utmcct=/eresources/referencesources.html; __qca=1221316170-63414193-51782906; __qseg=Q_D|Q_T|Q_2892|Q_2866|Q_2865|Q_2355|Q_2354|Q_2353|Q_2349|Q_2348|Q_2344|Q_2339|Q_1940|Q_1286|Q_1160|Q_1159|Q_1158|Q_1156|Q_1150|Q_1149|Q_1148|Q_1147|Q_1141|Q_983; __unam=d7bc9e4-12cc8e31ded-425d811d-4; __utma=10244330.616540340.1293737754.1293737754.1293737754.1; __utmc=10244330; __utmz=10244330.1293737754.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=university%20of%20pennsylvania; fsr.a=1293688517342; Admn=; ARPT=KJPIKMScorpweb101v8090CKYOK; General=; login=; LSLogin=GL%5FUT=B&FP%5FUT=B&GL%5FCL=VO&FP%5FCL=VO&GL%5FRS=009999&FP%5FRS=009999&FP%5FFL=false%7CIF%3Dfalse%7CRR%3Dfalse&GL%5FFL=false%7CIF%3Dfalse%7CRR%3Dfalse&FP%5FFI=777947&GL%5FFI=777947; Mds=; Search=
56.110.98.79||Bothell||WA||United States||Default+datasets+documents+pwp+vanwert||foo||[31/Dec/2010:00:00:16 -0500]||GET||http://global.factiva.com:80/ha/blank.aspx?foo=true||200||519||http://proxy.library.upenn.edu:2638/ha/default.aspx?ftx=eastdil%20secured||Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_8; en-us) AppleWebKit/530.19.2 (KHTML, like Gecko) Version/4.0.2 Safari/530.19]||vO07NtNOHwIciIH||__utma=94565761.635530968.1253892984.1293686436.1293688527.40; __utmc=94565761; __utmz=94565761.1293688527.40.30.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=upenn%20library; proxySessionID=18084464; UPennLibrary=AAAAAUq843cAAFe2AwPwAg==; __utma=261680716.2063844322.1263128892.1293760394.1293771521.18; __utmb=261680716.1.10.1293771521; __utmc=261680716; __utmz=261680716.1293771521.18.14.utmcsr=library.upenn.edu|utmccn=(referral)|utmcmd=referral|utmcct=/eresources/referencesources.html; __qca=1221316170-63414193-51782906; __qseg=Q_D|Q_T|Q_2892|Q_2866|Q_2865|Q_2355|Q_2354|Q_2353|Q_2349|Q_2348|Q_2344|Q_2339|Q_1940|Q_1286|Q_1160|Q_1159|Q_1158|Q_1156|Q_1150|Q_1149|Q_1148|Q_1147|Q_1141|Q_983; __unam=d7bc9e4-12cc8e31ded-425d811d-4; __utma=10244330.616540340.1293737754.1293737754.1293737754.1; __utmc=10244330; __utmz=10244330.1293737754.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=university%20of%20pennsylvania; fsr.a=1293688517342; Admn=; ARPT=KJPIKMScorpweb101v8090CKYOK; General=; login=; LSLogin=GL%5FUT=B&FP%5FUT=B&GL%5FCL=VO&FP%5FCL=VO&GL%5FRS=009999&FP%5FRS=009999&FP%5FFL=false%7CIF%3Dfalse%7CRR%3Dfalse&GL%5FFL=false%7CIF%3Dfalse%7CRR%3Dfalse&FP%5FFI=777947&GL%5FFI=777947; Mds=; Search=
56.110.98.79||Bothell||WA||United States||Default+datasets+documents+pwp+vanwert||foo||[31/Dec/2010:00:00:16 -0500]||POST||http://global.factiva.com:80/services/AjaxService.aspx?ServiceType=factiva.com.ui.ha.DiscoveryChartService||200||642||http://proxy.library.upenn.edu:2638/ha/default.aspx?ftx=eastdil%20secured||Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_8; en-us) AppleWebKit/530.19.2 (KHTML, like Gecko) Version/4.0.2 Safari/530.19]||vO07NtNOHwIciIH||__utma=94565761.635530968.1253892984.1293686436.1293688527.40; __utmc=94565761; __utmz=94565761.1293688527.40.30.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=upenn%20library; proxySessionID=18084464; UPennLibrary=AAAAAUq843cAAFe2AwPwAg==; __utma=261680716.2063844322.1263128892.1293760394.1293771521.18; __utmb=261680716.1.10.1293771521; __utmc=261680716; __utmz=261680716.1293771521.18.14.utmcsr=library.upenn.edu|utmccn=(referral)|utmcmd=referral|utmcct=/eresources/referencesources.html; __qca=1221316170-63414193-51782906; __qseg=Q_D|Q_T|Q_2892|Q_2866|Q_2865|Q_2355|Q_2354|Q_2353|Q_2349|Q_2348|Q_2344|Q_2339|Q_1940|Q_1286|Q_1160|Q_1159|Q_1158|Q_1156|Q_1150|Q_1149|Q_1148|Q_1147|Q_1141|Q_983; __unam=d7bc9e4-12cc8e31ded-425d811d-4; __utma=10244330.616540340.1293737754.1293737754.1293737754.1; __utmc=10244330; __utmz=10244330.1293737754.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=university%20of%20pennsylvania; fsr.a=1293688517342; Admn=; ARPT=KJPIKMScorpweb101v8090CKYOK; General=; login=; LSLogin=GL%5FUT=B&FP%5FUT=B&GL%5FCL=VO&FP%5FCL=VO&GL%5FRS=009999&FP%5FRS=009999&FP%5FFL=false%7CIF%3Dfalse%7CRR%3Dfalse&GL%5FFL=false%7CIF%3Dfalse%7CRR%3Dfalse&FP%5FFI=777947&GL%5FFI=777947; Mds=; Search=
56.110.98.79||Bothell||WA||United States||Default+datasets+documents+pwp+vanwert||foo||[31/Dec/2010:00:00:16 -0500]||POST||http://global.factiva.com:80/services/AjaxService.aspx?ServiceType=factiva.com.ui.ha.DiscoveryChartService||200||905||http://proxy.library.upenn.edu:2638/ha/default.aspx?ftx=eastdil%20secured||Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_5_8; en-us) AppleWebKit/530.19.2 (KHTML, like Gecko) Version/4.0.2 Safari/530.19]||vO07NtNOHwIciIH||__utma=94565761.635530968.1253892984.1293686436.1293688527.40; __utmc=94565761; __utmz=94565761.1293688527.40.30.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=upenn%20library; proxySessionID=18084464; UPennLibrary=AAAAAUq843cAAFe2AwPwAg==; __utma=261680716.2063844322.1263128892.1293760394.1293771521.18; __utmb=261680716.1.10.1293771521; __utmc=261680716; __utmz=261680716.1293771521.18.14.utmcsr=library.upenn.edu|utmccn=(referral)|utmcmd=referral|utmcct=/eresources/referencesources.html; __qca=1221316170-63414193-51782906; __qseg=Q_D|Q_T|Q_2892|Q_2866|Q_2865|Q_2355|Q_2354|Q_2353|Q_2349|Q_2348|Q_2344|Q_2339|Q_1940|Q_1286|Q_1160|Q_1159|Q_1158|Q_1156|Q_1150|Q_1149|Q_1148|Q_1147|Q_1141|Q_983; __unam=d7bc9e4-12cc8e31ded-425d811d-4; __utma=10244330.616540340.1293737754.1293737754.1293737754.1; __utmc=10244330; __utmz=10244330.1293737754.1.1.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=university%20of%20pennsylvania; fsr.a=1293688517342; Admn=; ARPT=KJPIKMScorpweb101v8090CKYOK; General=; login=; LSLogin=GL%5FUT=B&FP%5FUT=B&GL%5FCL=VO&FP%5FCL=VO&GL%5FRS=009999&FP%5FRS=009999&FP%5FFL=false%7CIF%3Dfalse%7CRR%3Dfalse&GL%5FFL=false%7CIF%3Dfalse%7CRR%3Dfalse&FP%5FFI=777947&GL%5FFI=777947; Mds=; Search="""

    static final DEFAULT_PARSER = {
        def data = line.split(/\\|\\|/)
        assert data.size() >= 14: "there should be at least 14 data fields"
        result.ipAddress = data[0]
        result.city = data[1]
        result.state = data[2]
        result.country = data[3]
        result.patronId = data[5]
        result.proxyDate = new java.text.SimpleDateFormat("[dd/MMM/yyyy:hh:mm:ss Z]").parse(data[6])
        result.url = data[8]
        result.ezproxyId = data[13]
    }



    static DEFAULT_PARSER_TEMPLATE = { parserText ->
        """
    import java.text.*
    import org.slf4j.*
    import metridoc.utils.*

    class EzproxyParser {

            def applicationContext

            def parse(String line, int lineNumber, String fileName) {
                def result = [:] as TreeMap
                result.lineNumber = lineNumber
                result.fileName = fileName
                ${parserText}

                return result
            }
    }
"""
    }

    static final DEFAULT_FILE_LOCATION = new File("${SystemUtils.USER_HOME}/.metridoc/files/ezproxy")

    static final DEFAULT_EZPROXY_CONFIG_MODEL = [
            storePatronId: true,
            encryptPatronId: false,
            anonymizePatronId: false,
            fileFilter: /ezproxy.log.\d{8}.gz/,
            sampleLog: DEFAULT_LOG_DATA,
            directory: "${SystemUtils.USER_HOME}/.metridoc/ezproxy/files",
            encoding: "utf-8",
            ezproxyParser: DEFAULT_PARSER,
            doDoiResolution: false,
            crossRefUserName: "foo",
            crossRefPassword: "bar"
    ]

}
