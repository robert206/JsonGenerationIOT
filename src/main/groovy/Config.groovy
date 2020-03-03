class Config {


    def usernameIngestion

    def getUsernameIngestion() {
        return usernameIngestion
    }

    def setUsernameIngestion(String usernameIngestion) {
        this.usernameIngestion = usernameIngestion
    }

    def passwordIngestion

    def getPasswordIngestion() {
        return passwordIngestion
    }

    def setPasswordIngestion(String passwordIngestion) {
        this.passwordIngestion = passwordIngestion
    }


    def dateFrom

    def getDateFrom() {
        return dateFrom
    }

    void setDateFrom(dateFrom) {
        this.dateFrom = dateFrom
    }


    def getDateTo() {
        return dateTo
    }


    void setDateTo(dateTo) {
        this.dateTo = dateTo
    }
    def dateTo

    String token

    public getToken() {
        return token;
    }

    public setToken(token) {
        this.token = token
    }



    /**url for GET "last" request **/
    def urlGETLast
    String getUrlGETLast() {
        return urlGETLast
    }
    void setUrlGETLast (urlGETLast) {
        this.urlGETLast = urlGETLast
    }


/** read config xml from resources **/
    def readXmlConfig(String fileName, String envType) {
        // conf.data is parsed from xml and stored in Class
        Config cfg = new Config();
        ClassLoader classLoader = getClass().getClassLoader()
        File file = new File(classLoader.getResource(fileName).getFile())

        assert file.exists(): "File $file not found"
        assert file.canRead(): "File $file no read permission"
        //parse Xml file and store the specified envType config into class structure
        def config = new XmlSlurper().parse(file)
        for (def i = 0; i < config.env.size(); i++) {
            if (config.env[i].@type == envType) {
                cfg.setUsernameIngestion((String) config.env[i].usernameIngestion)
                cfg.setPasswordIngestion((String) config.env[i].passwordIngestion)
                cfg.setDateFrom((String) config.env[i].dateFrom)
                cfg.setDateTo((String) config.env[i].dateTo)
                cfg.setUrlGETLast((String) config.env[i].urlGETLast)
                cfg.setToken((String) config.env[i].token)
            }
        }
        return cfg
    }
}



