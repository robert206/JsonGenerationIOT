import groovy.json.JsonSlurper
import groovy.text.GStringTemplateEngine

class Utils {

    //opens json request file(body) and replaces all variables with values defined in map
    def getJSONRequestBody (String fileName ,Map replaceParams) {
        ClassLoader classLoader = getClass().getClassLoader()
        File file = new File(classLoader.getResource(fileName).getFile())
        assert file.exists() : "File $file not found"
        assert file.canRead() : "File $file no read permission"
        String tmpFile = file.text.toString()
        def templateEngine = new GStringTemplateEngine()
        String body = templateEngine.createTemplate(tmpFile).make(replaceParams)
        return body
    }


    //basic url connection for REST old but it works
    def restRequest(url,method,contentType,String body,token) {
        URL requestURL = new URL(url)
        def conn = requestURL.openConnection()
        conn.setRequestMethod(method)
        conn.setRequestProperty("Content-type",contentType)
        conn.setRequestProperty("Authorization",token)
        if (method == "POST") {
            conn.setDoOutput(true)
            OutputStream str = conn.getOutputStream()
            str.write(body.getBytes())
        }
        def response = conn.content.text
        def responseCode = conn.getResponseCode()
        if (responseCode == 200) {
            return response
        }
        else {
            println("Error:Response code= $responseCode on url : $requestURL")
            return -1
        }
    }



    //send POST request filter
    def sendPOSTRequest (String url,String body,String token) {
        def response = restRequest(url,"POST","application/json",body,token)
        return response
    }

    //send GET "last" request -for retrieveing assetID
    def sendGETRequest (String url,String token) {
        def response = restRequest(url,"GET","application/json","",token)
        return response
    }


    /** receives response from "filter" POST request and parses assetId out which is then used in next GET request (last) **/
    def parseAssetId (def response) {
        def assetId = ""
        def splitted_response = response.split(',')
        splitted_response.each { line ->
            String currentLine = line
            if(currentLine.contains("assetId")) {
                String tmp = currentLine.split(":")[1]
                assetId = tmp.replace("\"","") // replace quotes with empty space
            }
        }
        return assetId
    }



    /**read AssetIds-names from file and return it as List **/
    List<String> readAssetNames (String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile())
        assert file.exists() : "File $fileName not found"
        assert file.canRead() : "File $fileName is not readable"

       def assetNames = file.readLines()
        return assetNames
    }



    /** get latest MT ,VT,Energy values **/
    Map getLatestDataSourcesValues (Map dataSourceMap, String response) {
        Map dataSourceValues = [:]
        def slurper = new JsonSlurper()
        def jsonObject = slurper.parseText(response)
        //traverse json response and compare datasourceids from given map and if match then add last value into map
        dataSourceMap.each { entry ->
            for (int i=0; i<jsonObject.values.size(); i++) {
                println "Entry key $entry.key"
                String dsId = jsonObject.values[i].datasourceId
                String tmp = dsId.replaceAll("\\[|\\]", "")
                println "tmp $tmp"
                if (entry.key == tmp.toInteger()) {
                    dataSourceValues.put(entry.key,jsonObject.values[i].value)
                }
            }
        }
        return dataSourceValues // map with latest datasource values
    }




}
