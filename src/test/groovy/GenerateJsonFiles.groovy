import groovy.json.JsonSlurper


Config cfg = new Config()
cfg = cfg.readXmlConfig("config.xml","test")
Utils ut = new Utils()

/****************************************************************************************************************************/
def dataSources = [69:"MT", 70:"VT",73:"Energy"]
def assetNames = ut.readAssetNames("AssetIds.txt")
/***************************************************************************************************************************/



List<String> timestamps = new ArrayList<>()
String fileName
File outputFile
String rootFolder = System.getProperty("user.dir")
String newline = System.getProperty("line.separator")
def fileNames = []
/** these values are used if there is no previous records in db (messages table) **/
def startingMT = "1.0"
String startingVT = "1.0"
String startingEnergy = "1.0"

for (assetName in assetNames) {

    //if it is number then it is actual assetName else its String thus Concentrator or any other group name
    if (assetName.isNumber()) {
        println "........Generating json data for Asset = $assetName and time period $cfg.dateFrom -$cfg.dateTo............"
        String response = ut.filterPOSTRequest(assetName,cfg.getToken())
        def assetId = ut.parseAssetId(response) //assetId parsed from filter request
        String urlGET =  cfg.urlGETLast.replaceAll("assetId","$assetId") // replace placeholder in GET http address with current assetId
        String respGET = ut.sendGETRequest(urlGET,cfg.token) // do GET request and save response

        /** New json slurper for accessing json node values **/
        def slurper = new JsonSlurper()
        def jsonObject = slurper.parseText(respGET) //*parse response into text
        def lastTimestamp = jsonObject.timestamp[0] // last used timestamp -if in config.xml dateFrom=0 then we use this time timestamp +1h
        def dataSourceValues = ut.getLatestDataSourcesValues(dataSources,respGET)

        String startTime
        String endTime = cfg.getDateTo() //dateTo from config.xml
        //timestamps
        if (cfg.getDateFrom() == "0") {
            startTime = lastTimestamp
        }
        else { startTime = cfg.getDateFrom()}


        timestamps = ut.generateTimestampList(startTime,endTime)//get all timestamps generated in list
        /** mt values **/
        if (dataSourceValues.containsKey(69)) {
            startingMT = dataSourceValues.get(69)
        }
        def MTValues = ut.generateValues(timestamps.size(),startingMT)

        /** VT values **/
        if (dataSourceValues.containsKey(70)) {
            startingVT = dataSourceValues.get(70)
        }
        def VTValues = ut.generateValues(timestamps.size(),startingVT)

        /** Energy values **/
        if (dataSourceValues.containsKey(73)) {
            startingEnergy = dataSourceValues.get(73)
        }
        def EnergyValues = ut.generateValues(timestamps.size(),startingEnergy)

        /** traverse every timestamp **/
        def index = 0
        def nrOfRecords = 0 //count records written into file
        for (timestamp in timestamps) {
            def currentPower = ut.generateRandomIntRange(1,5)
            Map params = [:]
            def jsonRecord //this is written to file actually
            def currentMT = MTValues[index]
            def currentVT = VTValues[index]
            def currentEnergy = EnergyValues[index]

            if (ut.isItMT(timestamp)) { //if timestamp falls into time when MT should be active
                params = [timestamp: "$timestamp",assetId:"$assetName",valueMT:"$currentMT",valueEnergy:"$currentEnergy",valuePower:"$currentPower"]
                jsonRecord = ut.getJSONRequestBody("SingleJsonTemplateMT.json",params)
            }
            else {  //else it is VT timestamp
                params = [timestamp:"$timestamp",assetId:"$assetName",valueVT:"$currentVT",valueEnergy:"$currentEnergy",valuePower:"$currentPower"]
                jsonRecord = ut.getJSONRequestBody("SingleJsonTemplateVT.json",params)
            }
            outputFile << jsonRecord
            outputFile << ","
            ++index
        }
    }
        /** else it is Group name/Concentrator name or similar **/
    else {
        fileName = assetName
        //outputFile = new File("$rootFolder/target/$fileName")
        outputFile = new File("$rootFolder\\$fileName")//make it relative afterwards

        outputFile.createNewFile()
        fileNames << outputFile.path //store filenames for last step of trimming and adding chars
        outputFile << "[ $newline"
    }

}

fileNames.each {
    println "file: $it"
}
def ts = cfg.dateTo.split("T")[0]

ut.createFinalJsonFiles(fileNames,ts)
println ("All DONE.Cheerish this moment with dancing.Files are all test/groovy folder.")























