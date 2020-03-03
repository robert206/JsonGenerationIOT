import groovy.json.JsonSlurper

import java.time.LocalDate

Config cfg = new Config()
cfg = cfg.readXmlConfig("config.xml","test")


Utils ut = new Utils()
String assetId = "Replaced_value"
String timestamp = "2020-02-1 2:10:00"
String value_MT = "122";
String value_Energy ="2223"
String value_Power = "45345"
Map params = [timestamp: "$timestamp",assetId:"$assetId",valueMT:"$value_MT",valueEnergy:"$value_Energy",valuePower:"$value_Power"]
def body = ut.getJSONRequestBody("SingleJsonTemplateMT.json",params)
println ("Response = $body")

String keyword = "19081113"
Map param1 = [keyword:"$keyword"]
ClassLoader classLoader = getClass().getClassLoader()
File file1 = new File(classLoader.getResource("Filterbody.json").getFile())
def bodyFilterRequest  =file1.getText()
def bodyReplaced = ut.getJSONRequestBody("FilterBody.json",param1)
def rp = ut.sendPOSTRequest("https://forensixxapitest.azurewebsites.net/v1/filter",bodyReplaced, cfg.token)


def aId = ut.parseAssetId(rp);
println "Assetid = $aId"

def found = false


String urlGET =  cfg.urlGETLast.replaceAll("assetId",aId)
println "urlget $urlGET"
def respGET = ut.sendGETRequest(urlGET,cfg.token)
println "respGET = $respGET"


// slurper
def slurper = new JsonSlurper()
def jsonObject = slurper.parseText(respGET)
def lastTimestamp = jsonObject.timestamp[0] // returns list of all timestamps



Map dataSources = [69:"MT", 70:"VT",73:"Energy"] //useless as one cannot know which ones are which ad-hoc for all types ..just here to make it clearer.

def values = jsonObject.values
println "Last timestamp used :$lastTimestamp"


/** Map of datasources compared  **/
println "Values =" +values[0].datasourceId
println "MT values :$values"

Map dataSourceValues = ut.getLatestDataSourcesValues(dataSources,respGET)

/*
dataSources.each { entry ->
    for (int i=0;i<values.size();i++) {
        println "Entry key $entry.key"
        String dsId = values[i].datasourceId
        String tmp = dsId.replaceAll("\\[|\\]", "")
        println "tmp $tmp"
        if (entry.key == tmp.toInteger()) {
            dataSourceValues.put(entry.key,values[i].value)
        }
    }
}*/
println "Size ="+dataSourceValues.size()
dataSourceValues.each {entry ->
    println "$entry.key =$entry.value"
}



//assetNames
def assetNames = ut.readAssetNames("AssetIds.txt")
assetNames.each {line ->
    if (line.isNumber()) {
        println "number = $line"
    }
    else println "$line"
}


String s = "2020-02-01 01:00:00"
String e = "2020-03-01 05:00:00"
LocalDate start = LocalDate.parse(s);
LocalDate end = LocalDate.parse(e);
List<LocalDate> totalDates = new ArrayList<>();
while (!start.isAfter(end)) {
    totalDates.add(start);
    start = start.plusDays(1);
}
for (d in totalDates) { println "Date: $d "}















