/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package warehousemanagement;

import java.io.BufferedWriter;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.Scanner;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.Scanner;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.text.Font;

/**
 *
 * @author skyle
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label dateOfFileLabel;
    @FXML
    private ListView barcodeListView;
    @FXML
    private TextField barcodeInputTF;
    @FXML
    private CheckBox addBarcodeCheckBox;
    @FXML
    private CheckBox removeBarcodeCheckBox;
    
    private ArrayList<Barcode> barcodesArrayList = new ArrayList<>();
    private ArrayList<String> barcodesOutputList = new ArrayList<>();
    private ObservableList<String> barcodesOutputOList;
    private Barcode lastBarcode;
    private StringBuffer stringBufferOfData = new StringBuffer();
//    private String file = "src/resources/barcodes.txt";
    private String fileName = "";
    private String date = "";
    private boolean addingBarcode = true;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        int num = (int)(Math.random()*10)+1;
        Barcode newBarcode = new Barcode(Integer.toString(num));
        addBarcode(newBarcode);
        
        updateOutputList();
        updateTable();
    }
    
    @FXML
    private void toggleInputFunction(){
        addingBarcode = !addingBarcode;
        if(addingBarcode){
            addBarcodeCheckBox.setDisable(true);
            addBarcodeCheckBox.setSelected(true);
            removeBarcodeCheckBox.setDisable(false);
            removeBarcodeCheckBox.setSelected(false);
            barcodeInputTF.requestFocus();
        }
        else{
            removeBarcodeCheckBox.setDisable(true);
            removeBarcodeCheckBox.setSelected(true);
            addBarcodeCheckBox.setDisable(false);
            addBarcodeCheckBox.setSelected(false);
            barcodeInputTF.requestFocus();
        }
    }
    
    @FXML
    private void undoAction(ActionEvent event){
        lastBarcode.decreaseNumScanned();
        if(lastBarcode.getNumScanned() <= 0){
            barcodesArrayList.remove(lastBarcode);
        }
        
        updateOutputList();
        updateTable();
    }
    
    @FXML
    private void handleTextFieldKeyPress(KeyEvent event){
        if(event.getCode().equals(KeyCode.ENTER) && !barcodeInputTF.getText().isEmpty()){
            if(addingBarcode){    
                String input = barcodeInputTF.getText();
                addBarcode(input);
                barcodeInputTF.clear();

                updateOutputList();
                updateTable();
            }
            else{
                String input = barcodeInputTF.getText();
                removeBarcode(input);
                barcodeInputTF.clear();
                
                updateOutputList();
                updateTable();
            }
        }
    }
    
    private void removeBarcode(String b){
        Barcode barcode = new Barcode(b);
        removeBarcode(barcode);
    }
    private void removeBarcode(Barcode barcode){
        for(Barcode b: barcodesArrayList){
            if(b.getBarcode().equals(barcode.getBarcode())){
                int startIndex = stringBufferOfData.indexOf(b.getInfo());
                int endIndex = startIndex + b.getInfo().length();
                if(b.getNumScanned() > 0){
                    b.decreaseNumScanned();
                }
                stringBufferOfData.replace(startIndex, endIndex, b.getInfo());
                lastBarcode = b;
                writeToFile();
                return;
            }
        }
    }
    
    private void addBarcode(String barcode){
        Barcode newBarcode = new Barcode(barcode);
        addBarcode(newBarcode);
    }
    
    private void addBarcode(Barcode barcode){
        try{
            PrintWriter out = new PrintWriter(fileName);
            for(Barcode b: barcodesArrayList){
                if(b.getBarcode().equals(barcode.getBarcode())){
                    int startIndex = stringBufferOfData.indexOf(b.getInfo());
                    int endIndex = startIndex + b.getInfo().length();
                    b.increaseNumScanned();
                    stringBufferOfData.replace(startIndex, endIndex, b.getInfo());
                    lastBarcode = b;
                    
//                    if(!stringBufferOfData.substring(stringBufferOfData.length()-3, stringBufferOfData.length()).equals("END")){
//                        stringBufferOfData.append("END");
//                    } 
                    writeToFile();
                    return;
                }
            }

            barcodesArrayList.add(barcode);
            lastBarcode = barcode;
//            int length = stringBufferOfData.length();
//            if(stringBufferOfData.substring(length-3, length).equals("END")){
//                System.out.println(stringBufferOfData.substring(length-3, length));
//                stringBufferOfData.delete(length-3, length);
//            }
            stringBufferOfData.append(barcode.getInfo());
            stringBufferOfData.append("\n");
//            stringBufferOfData.append("END");
            writeToFile();
        } catch(FileNotFoundException ex) {
            System.out.println("File Not Found");
        }
    }
    
    public void writeToFile(){
        try {
                BufferedWriter bufwriter = new BufferedWriter(new FileWriter(fileName));
                bufwriter.write(stringBufferOfData.toString());//writes the edited string buffer to the new file
                bufwriter.close();//closes the file
        } catch (Exception e) {//if an exception occurs
            System.out.println("Error occured while attempting to write to file: " + e.getMessage());
        }
    }
    
    public void readFromFile(){
        try{
            Scanner fileToRead = new Scanner(new File(fileName));
//            for(String line = null; fileToRead.hasNext() /*!line.isEmpty()*/; line = fileToRead.nextLine()) {
//                if(line != null){
//                    System.out.println(line);
//                    int numScanned = Integer.parseInt(line.substring(line.indexOf("-")+2));
//                    String barcode = line.substring(0, (line.indexOf("-")-1));
//                    Barcode newBarcode = new Barcode(barcode);
//                    newBarcode.setNumScanned(numScanned);
//                    barcodesArrayList.add(newBarcode);
//                    stringBufferOfData.append(line).append("\n");
//                }
//            }
            while(fileToRead.hasNextLine()){
                String line = fileToRead.nextLine();
                System.out.println(line);
                int numScanned = Integer.parseInt(line.substring(line.indexOf(":")+2));
                String barcode = line.substring(0, (line.indexOf(":")-1));
                Barcode newBarcode = new Barcode(barcode);
                newBarcode.setNumScanned(numScanned);
                barcodesArrayList.add(newBarcode);
                stringBufferOfData.append(line).append("\n");
            }
            fileToRead.close();
        } catch(FileNotFoundException ex){
            System.out.println("File not found");
        }
    }
    
    private void updateOutputList(){
        barcodesOutputList.clear();
        barcodesArrayList.forEach((b) -> {
            barcodesOutputList.add(b.getInfo());
        });
    }
    private void updateTable(){
        barcodesOutputOList = FXCollections.observableArrayList(barcodesOutputList);
        barcodeListView.setItems(barcodesOutputOList);
    }
    
    public void setDateString(){
        date += Calendar.getInstance().get(Calendar.YEAR);
        if(Calendar.getInstance().get(Calendar.MONTH)+1 < 10){
            date += "0" + (Calendar.getInstance().get(Calendar.MONTH)+1);
        }
        else{
            date += (Calendar.getInstance().get(Calendar.MONTH)+1);
        }
        if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10){
            date += "0" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
        else{
            date += Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setDateString();
        fileName = "src/resources/barcodes" + date + ".txt";
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        readFromFile();
        updateOutputList();
        updateTable();
        
        dateOfFileLabel.setText("File Date: " + (Calendar.getInstance().get(Calendar.MONTH)+1) + "/" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + Calendar.getInstance().get(Calendar.YEAR));
        barcodeInputTF.requestFocus();
    }    
    
}
