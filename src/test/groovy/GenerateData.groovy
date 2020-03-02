import spock.lang.Specification


class GenerateData extends Specification {

    def setup() {
        Config cfg = new Config()
        cfg = cfg.readXmlConfig("config.xml","test")
    }

}