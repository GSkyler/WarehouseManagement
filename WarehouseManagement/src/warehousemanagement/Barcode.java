/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package warehousemanagement;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author skyle
 */
public class Barcode {
    
    private String barcodeID;
    private int numScanned;
    private String dateScanned;
    
    public Barcode(String id){
        barcodeID = id;
        numScanned = 1;
    }
    
    public String getBarcode(){
        return barcodeID;
    }
    
    public int getNumScanned(){
        return numScanned;
    }
    
    public void setNumScanned(int num){
        numScanned = num;
    }
    public void increaseNumScanned(){
        numScanned++;
    }
    public void decreaseNumScanned(){
        numScanned--;
    }
            
    public String getInfo(){
        String output = barcodeID + " ~ " + numScanned;
        return output;
    }
}
