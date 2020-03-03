
int start = 13
while (start < 200000) {
    int counter = 0
    while(counter <498) {
        start=start +11
        ++counter
    }
    println ("line number = $start")
}




String fileName = "ConcentratorQ7.json"
ClassLoader classLoader = getClass().getClassLoader()
File file = new File(classLoader.getResource(fileName).getFile())

assert file.exists(): "File $file not found"
assert file.canRead(): "File $file no read permission"


int recordNumber = 0
int recordNumberMax = 498
int fileCounter = 1
String outFileName = fileName + "_" + fileCounter + ".json"

File outFile = new File(outFileName)
file.eachLine {line ->
    recordNumber =1
    while (recordNumber < recordNumberMax) {
        outFile.append(line)
        if (line.matches("}},")) {
            println ("new Record")
            ++recordNumber
            outFile.append(line)
        }
    }
    String appendaj = line.replaceAll(",","]")
    recordNumber = 1
    ++ fileCounter
    outFileName = fileName + "_" + fileCounter + ".json"
    File outfile = new File(outFileName)
}

