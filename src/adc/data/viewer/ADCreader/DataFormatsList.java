package adc.data.viewer.ADCreader;

/**
 * Created by a on 11/13/16.
 */
 enum DataFormatsList {

    LGRAPH1("LGraph1"), LGRAPH1_2008("LGraph1_2008"), LGRAPH2("LGraph2"), SATURN("Saturn"), TEXTFILE("TextFile");

    private  DataFormat parser;

    public DataFormat getParser() {
        return parser;
    }

    DataFormatsList (String fmtstr){

        switch (fmtstr) {
            case "LGraph1":
                this.parser = new LGraph1();
                break;
            case "LGraph1_2008":
                this.parser = new LGraph1_2008();
                break;
            case "LGraph2":
                this.parser = new LGraph2();
                break;
            case "Saturn":
                this.parser = new Saturn();
                break;
            case "TextFile":
                this.parser = new TextFile();
                break;
        }

    }


}
