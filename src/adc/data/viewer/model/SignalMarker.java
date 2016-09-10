package adc.data.viewer.model;

import javafx.beans.property.*;
import javafx.scene.paint.Color;

/**
 * Model class for signals
 */
public class SignalMarker {
    private final IntegerProperty signalIndex;

    private final BooleanProperty signalSelected;
    private final StringProperty signalLabel;
    private final ObjectProperty<Color> signalColor;
    private final IntegerProperty fileNumber;
    public SignalMarker(int signalIndex, Boolean signalSelected, Color signalColor, String  signalLable, int fileNumber) {
        this.signalIndex = new SimpleIntegerProperty(signalIndex);
        this.signalSelected = new SimpleBooleanProperty(signalSelected);
        this.signalColor = new SimpleObjectProperty<>(signalColor);
        this.signalLabel = new SimpleStringProperty(signalLable);
        this.fileNumber = new SimpleIntegerProperty(fileNumber);
    }

    public int getSignalIndex() {
        return signalIndex.get();
    }

    public IntegerProperty signalIndexProperty() {
        return signalIndex;
    }

    public void setSignalIndex(int signalIndex) {
        this.signalIndex.set(signalIndex);
    }

    public int getFileNumber() { return fileNumber.get();
    }

    public IntegerProperty fileNumberProperty() {
        return fileNumber;
    }

    public void setFileNumber(int fileNumber) {
        this.fileNumber.set(fileNumber);
    }

    public Color getSignalColor() {
        return signalColor.get();
    }

    public ObjectProperty<Color> signalColorProperty() {
        return signalColor;
    }

    public void setSignalColor(Color signalColor) {
        this.signalColor.set(signalColor);
    }

    public boolean getSignalSelected() {
        return signalSelected.get();
    }

    public boolean isSignalSelected() {
        return signalSelected.get();
    }

    public BooleanProperty signalSelectedProperty() {
        return signalSelected;
    }

    public void setSignalSelected(boolean signalSelected) {
        this.signalSelected.set(signalSelected);
    }

    public String getSignalLabel() {
        return signalLabel.get();
    }

    public StringProperty signalLabelProperty() {
        return signalLabel;
    }

    public void setSignalLabel(String signalLabel) {
        this.signalLabel.set(signalLabel);
    }

}
