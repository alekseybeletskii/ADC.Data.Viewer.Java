package adc.data.viewer.ADCreader;

/**
 * Created by a on 11/13/16.
 */
 public enum DataTypesList {
    LGRAPH1("LGraph1"), LGRAPH1_2008("LGraph1_2008"), LGRAPH2("LGraph2"), SATURN("Saturn");

    private DataTypes dataType;

    public DataTypes getDataType() {
        return dataType;
    }

    DataTypesList(String fmtstr){

        switch (fmtstr) {
            case "LGraph1":
                this.dataType = new LGraph1();
                break;
            case "LGraph1_2008":
                this.dataType = new LGraph1_2008();
                break;
            case "LGraph2":
                this.dataType = new LGraph2();
                break;
            case "Saturn":
                this.dataType = new Saturn();
                break;
        }

    }


}
