package de.bundeswehr.mese.sedapexpress.messagetool.controller;

import java.util.ArrayList;
import java.util.List;

import de.bundeswehr.mese.sedapexpress.messages.SEDAPExpressMessage.Acknowledgement;
import de.bundeswehr.mese.sedapexpress.messages.SEDAPExpressMessage.Classification;
import de.bundeswehr.mese.sedapexpress.messages.STATUS;
import de.bundeswehr.mese.sedapexpress.messages.STATUS.CommandState;
import de.bundeswehr.mese.sedapexpress.messages.STATUS.OperationalState;
import de.bundeswehr.mese.sedapexpress.messages.STATUS.TechnicalState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;

public class STATUSPanelController extends MessagePanelController {

    @FXML
    private TextField ammunitionLevelTextField;

    @FXML
    private Label ammunitionLevelLabel;

    @FXML
    private Label batterieLevelLabel;

    @FXML
    private TextField batterieLevelTextField;

    @FXML
    private TextField ipHostTextField;

    @FXML
    private Label cmdIdLabel;

    @FXML
    private TextField cmdIdTextField;

    @FXML
    private ComboBox<CommandState> cmdStateComboBox;

    @FXML
    private Label fuelLevelLabel;

    @FXML
    private TextField fuelLevelTextField;

    @FXML
    private Label ipHostLabel;

    @FXML
    private Label mediaLabel;

    @FXML
    private TextField mediaTextField;

    @FXML
    private ComboBox<TechnicalState> tecStateComboBox;

    @FXML
    private ComboBox<OperationalState> opsStateComboBox;

    @FXML
    private Label tecStatusLabel;

    @FXML
    private TextArea textTextArea;

    
    
    @FXML
    void initialize() {
	assert this.ammunitionLevelTextField != null : "fx:id=\"ammunitionLevelTextField\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.ipHostTextField != null : "fx:id=\"ipHostTextField\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.ammunitionLevelLabel != null : "fx:id=\"ammunitionLevelLabel\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.batterieLevelLabel != null : "fx:id=\"batterieLevelLabel\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.batterieLevelTextField != null : "fx:id=\"batterieLevelTextField\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.cmdIdLabel != null : "fx:id=\"cmdIdLabel\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.cmdIdTextField != null : "fx:id=\"cmdIdTextBox\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.cmdStateComboBox != null : "fx:id=\"cmdStateComboBox\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.fuelLevelLabel != null : "fx:id=\"fuelLevelLabel\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.fuelLevelTextField != null : "fx:id=\"fuelLevelTextField\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.ipHostLabel != null : "fx:id=\"ipHostLabel\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.mediaLabel != null : "fx:id=\"mediaLabel\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.mediaTextField != null : "fx:id=\"mediaTextField\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.opsStateComboBox != null : "fx:id=\"optStateComboBox\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.tecStateComboBox != null : "fx:id=\"tecStateComboBox\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.tecStatusLabel != null : "fx:id=\"tecStatusLabel\" was not injected: check your FXML file 'STATUSPanel.fxml'.";
	assert this.textTextArea != null : "fx:id=\"textTextArea\" was not injected: check your FXML file 'STATUSPanel.fxml'.";

	// keine Mandatory-Felder, daher kein invalid, die Felder können zum Ende hin auch gelöscht werden
	this.ammunitionLevelLabel.setVisible(false);
	this.fuelLevelLabel.setVisible(false);
	this.batterieLevelLabel.setVisible(false);
	this.cmdIdLabel.setVisible(false);
	this.ipHostLabel.setVisible(false);
	this.mediaLabel.setVisible(false);
	
	  // TOOLTIPS
    Tooltip tooltipammunitionLevel = new Tooltip("Relative remaining ammunition: <name of the weapon>#<ammunition level>#.... \n Validation when field lost focus. \n Example: OTOMELARA#123#RAM#21#MLG_1#500");
    Tooltip tooltipbatterieLevel = new Tooltip("Relative remaining batterie capacity: <name of the batterie>#<batterie level>#.... \n Validation when field lost focus. \nExample: BAT_1#50#BAT_2#100#BAT_DROHNE#50");
    Tooltip tooltipfuelLevel = new Tooltip("Relative remaining fuel capacity: <name of the fuel tank>#<ammunition level>#.... \n Validation when field lost focus. \nExample: TANK1#5200#TANK2#300#TANK3#0");
    Tooltip tooltipCmdId = new Tooltip("ID of the releated command (message)");
    Tooltip tooltipIPHost = new Tooltip("IP or hostname of the platform");
    Tooltip tooltipMedia = new Tooltip("List of video stream or image URLs with ; as separator");
    Tooltip tooltipText = new Tooltip("Human readable free text description of the status");
   	
    this.ammunitionLevelTextField.setTooltip(tooltipammunitionLevel);
    this.batterieLevelTextField.setTooltip(tooltipbatterieLevel);
    this.fuelLevelTextField.setTooltip(tooltipfuelLevel);
    this.cmdIdTextField.setTooltip(tooltipCmdId);
    this.ipHostTextField.setTooltip(tooltipIPHost);
    this.mediaTextField.setTooltip(tooltipMedia);
    this.textTextArea.setTooltip(tooltipText);
    
	// TecStateComboBox
	this.tecStateComboBox.setItems(FXCollections.observableArrayList(TechnicalState.values()));
	this.tecStateComboBox.setCellFactory(new Callback<ListView<TechnicalState>,ListCell<TechnicalState>>(){
		@Override
		public ListCell<TechnicalState> call (ListView<TechnicalState> l){
			return new ListCell<TechnicalState>() {
				@Override
				protected void updateItem(TechnicalState item, boolean empty) {
					super.updateItem(item,  empty);
					if(item == null || empty) {
						setGraphic(null);
					}else {
						setText(item.name());
					}
				}
			};
		}
	});
	this.tecStateComboBox.setButtonCell(new ListCell<>() {
	    protected void updateItem(TechnicalState item, boolean empty) {
	    	super.updateItem(item, empty);
	    	if (item == null || empty) {
	    		setGraphic(null);
	    	} else {
	    		setText(item.name());
	    	}
	    }
	});
	this.tecStateComboBox.getSelectionModel().select(1);
	
	// OpsStateComboBox
	this.opsStateComboBox.setItems(FXCollections.observableArrayList(OperationalState.values()));
	this.opsStateComboBox.setCellFactory(new Callback<ListView<OperationalState>,ListCell<OperationalState>>(){
		@Override
		public ListCell<OperationalState> call (ListView<OperationalState> l){
			return new ListCell<OperationalState>() {
				@Override
				protected void updateItem(OperationalState item, boolean empty) {
					super.updateItem(item,  empty);
					if(item == null || empty) {
						setGraphic(null);
					}else {
						setText(item.name());
					}
				}
			};
		}
	});
	this.opsStateComboBox.setButtonCell(new ListCell<>() {
	    protected void updateItem(OperationalState item, boolean empty) {
	    	super.updateItem(item, empty);
	    	if (item == null || empty) {
	    		setGraphic(null);
	    	} else {
	    		setText(item.name());
	    	}
	    }
	});
	this.opsStateComboBox.getSelectionModel().select(0);
	
	
	
	// AmmunitionLevelTextField
	ammunitionLevelTextField.focusedProperty().addListener((observable, oldValue,newValue) -> {
		if(!newValue) {
			getAmmunitionLevel(ammunitionLevelTextField.getText());	
			}
		});
    
	// FuelLevelTextField
	fuelLevelTextField.focusedProperty().addListener((observable, oldValue,newValue) -> {
 		if(!newValue) {
 			getFuelLevel(fuelLevelTextField.getText());	
 			}
 		});
	// BatterieLevelTextField
    batterieLevelTextField.focusedProperty().addListener((observable, oldValue,newValue) -> {
 		if(!newValue) {
 			getBatterieLevel(batterieLevelTextField.getText());	
 			}
 		});

    // CmdID
    cmdIdTextField.setTextFormatter(MessagePanelController.createHexFormatter());
    cmdIdTextField.textProperty().addListener((observable, oldValue,newValue) -> {
		 if (!newValue.isEmpty()) {
             try {
            	 commandID = Integer.parseInt(newValue, 16);
             } catch (NumberFormatException e) {
                 System.out.println("Ungültige Eingabe");
             }
         } else {
        	 commandID = null;
         }
     });
    
    // CmdStateComboBox
 	this.cmdStateComboBox.setItems(FXCollections.observableArrayList(CommandState.values()));
 	this.cmdStateComboBox.setCellFactory(new Callback<ListView<CommandState>,ListCell<CommandState>>(){
 		@Override
 		public ListCell<CommandState> call (ListView<CommandState> l){
 			return new ListCell<CommandState>() {
 				@Override
 				protected void updateItem(CommandState item, boolean empty) {
 					super.updateItem(item,  empty);
 					if(item == null || empty) {
 						setGraphic(null);
 					}else {
 						setText(item.name());
 					}
 				}
 			};
 		}
 	});
 	this.cmdStateComboBox.setButtonCell(new ListCell<>() {
 	    protected void updateItem(CommandState item, boolean empty) {
 	    	super.updateItem(item, empty);
 	    	if (item == null || empty) {
 	    		setGraphic(null);
 	    	} else {
 	    		setText(item.name());
 	    	}
 	    }
 	});
 	this.cmdStateComboBox.getSelectionModel().select(0);
    
    
    // IP/Host
 	this.ipHostTextField.textProperty().addListener((observable, oldValue,newValue) -> {
		 if (!newValue.isEmpty()) {
            	 hostname = ipHostTextField.getText();
         } else {
        	 this.hostname = null;
         }
     });
    
    // Media
 	 this.mediaTextField.textProperty().addListener((observable, oldValue,newValue) -> {
  		if(!newValue.isEmpty()) {
  			getMedia(mediaTextField.getText());	
  			}else {
  				this.mediaUrls = null;
  			}
  		});
    
    // Text
 	this.textTextArea.textProperty().addListener((observable, oldValue,newValue) -> {
		 if (!newValue.isEmpty()) {
			 freeText = textTextArea.getText();
        } else {
        	this.freeText = null;
        }
    });
   
    
    }
    private List<Double> ammunitionLevels = new ArrayList<>();
    private List<Double> fuelLevels = new ArrayList<>();
    private List<Double> batterieLevels = new ArrayList<>();
    private List<String> ammunitionLevelNames = new ArrayList<>();
    private List<String> fuelLevelNames = new ArrayList<>();
    private List<String> batterieLevelNames = new ArrayList<>();
    private Integer commandID;
    private String hostname;
    private List<String> mediaUrls = new ArrayList<>();
    private String freeText="";
    
    private void getAmmunitionLevel(String input) {
    	String[] pairs =  input.split("#");
    	ammunitionLevelNames.clear();
    	ammunitionLevels.clear();
    	ammunitionLevelLabel.setVisible(false);
    	for (int i = 0; i < pairs.length - 1; i += 2) {
    		try {
    	        ammunitionLevelNames.add(pairs[i].toString());
    	        ammunitionLevels.add(Double.parseDouble(pairs[i + 1]));
    	        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
    	        	ammunitionLevelLabel.setVisible(true);
    	        	System.out.println("Ungültiges Format: " + pairs[i] + "#" + pairs[i + 1]);
    	        	ammunitionLevelNames.remove(i);
       	        }   
    	}
    }
    
    private void getFuelLevel(String input) {
    	String[] pairs =  input.split("#");
    	fuelLevels.clear();
    	fuelLevelNames.clear();
    	fuelLevelLabel.setVisible(false);
    	for (int i = 0; i < pairs.length - 1; i += 2) {
    		try {
    			fuelLevelNames.add(pairs[i].toString());
    			fuelLevels.add(Double.parseDouble(pairs[i + 1]));
    	        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
    	        	fuelLevelLabel.setVisible(true);
    	        	System.out.println("Ungültiges Format: " + pairs[i] + "#" + pairs[i + 1]);
    	        	fuelLevelNames.remove(i);
       	        }   
    	}
    }
    
    private void getBatterieLevel(String input) {
    	String[] pairs =  input.split("#");
    	batterieLevels.clear();
    	batterieLevelNames.clear();
    	batterieLevelLabel.setVisible(false);
    	for (int i = 0; i < pairs.length - 1; i += 2) {
    		try {
    			batterieLevelNames.add(pairs[i].toString());
    			batterieLevels.add(Double.parseDouble(pairs[i + 1]));
    	        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
    	        	batterieLevelLabel.setVisible(true);
    	        	System.out.println("Ungültiges Format: " + pairs[i] + "#" + pairs[i + 1]);
    	        	batterieLevelNames.remove(i);
       	        }   
    	}
    }
    
    private void getMedia(String input) {
    	String[] medias = input.split(";");
    	this.mediaUrls.clear();
    	for(int i = 0; i < medias.length; i++) {
    		this.mediaUrls.add(medias[i]);
		}
    }
    
    @Override
    public STATUS createMessage(Short number, Long time, String sender,
	    Classification classification, Acknowledgement acknowledgement, String mac) {

    	if(this.mediaUrls != null && this.mediaUrls.isEmpty()) {
    		this.mediaUrls = null;
    	}
    	
    	
	STATUS status = new STATUS(number, time, sender,
		classification, acknowledgement, mac,
		this.tecStateComboBox.getValue(),
		this.opsStateComboBox.getValue(),
		this.ammunitionLevelNames,
		this.ammunitionLevels,
		this.fuelLevelNames,
		this.fuelLevels,
		this.batterieLevelNames,
		this.batterieLevels,
		this.commandID != null ? commandID : 0,
		this.cmdStateComboBox.getValue(),
		this.hostname,
		this.mediaUrls != null ? this.mediaUrls : null,
		this.freeText != null ? this.freeText : null
		);

	return status;
    }

    @Override
    public boolean isValidFilled() {

    	return !ammunitionLevelLabel.isVisible() && !batterieLevelLabel.isVisible() && !fuelLevelLabel.isVisible(); 
    }

}
