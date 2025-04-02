package org.app.dlms.Backend.Model;

import java.util.Date;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * View model for displaying payment information in the UI
 */
public class PaymentViewModel {
    private final IntegerProperty id;
    private final StringProperty userName;
    private final DoubleProperty amount;
    private final StringProperty type;
    private final ObjectProperty<Date> date;
    private final StringProperty status;
    private final StringProperty description;
    private final IntegerProperty relatedRecordId;
    
    public PaymentViewModel(int id, String userName, double amount, String type, Date date, 
                           String status, String description, int relatedRecordId) {
        this.id = new SimpleIntegerProperty(id);
        this.userName = new SimpleStringProperty(userName);
        this.amount = new SimpleDoubleProperty(amount);
        this.type = new SimpleStringProperty(type);
        this.date = new SimpleObjectProperty<>(date);
        this.status = new SimpleStringProperty(status);
        this.description = new SimpleStringProperty(description);
        this.relatedRecordId = new SimpleIntegerProperty(relatedRecordId);
    }
    
    // Getters and property accessors
    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }
    
    public String getUserName() { return userName.get(); }
    public StringProperty userNameProperty() { return userName; }
    
    public double getAmount() { return amount.get(); }
    public DoubleProperty amountProperty() { return amount; }
    
    public String getType() { return type.get(); }
    public StringProperty typeProperty() { return type; }
    
    public Date getDate() { return date.get(); }
    public ObjectProperty<Date> dateProperty() { return date; }
    
    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
    
    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }
    
    public int getRelatedRecordId() { return relatedRecordId.get(); }
    public IntegerProperty relatedRecordIdProperty() { return relatedRecordId; }
} 