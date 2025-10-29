import java.util.ArrayList;

public class Data implements Comparable<Data>{

    // Properties of the Data class
    private String dataName;
    private Double dataSize;
    //private Integer dataDegree;
    //
    public Data(String dataTypeName, Double dataSize) {
        this.dataName = dataTypeName;
        this.dataSize = dataSize;
       // this.dataDegree=0;
    }


    // Getter methods
    public Double getSize() {
        return dataSize;
    }



    //Helper Methods
    public static ArrayList<Data> union(ArrayList<Data> dataItems,Data newDataItem){
        for (Data dataItem:dataItems) {
            if(dataItem.equals(newDataItem)){
                return dataItems;
            }
        }
        dataItems.add(newDataItem);
        return dataItems;
    }

    @Override
    public String toString() {

        return "DataType [dataName=" + dataName +
                ", dataSize=" + dataSize +
                //", dataDegree=" + dataDegree+
                "]";
    }

    @Override
    public int compareTo(Data o) {
        return dataName.compareTo(o.dataName);
    }
    ///Helper methods
    public boolean belongsTo(ArrayList<Data> dataTypeSet) {
        for (Data dataTypeItem:dataTypeSet) {
            if(dataTypeItem.equals(this)){
                return true;
            }
        }
        return false;
    }

}