/*******************************************************************************
 * 	********************* BEGIN LICENSE BLOCK *********************************
 * 	ADCDataViewer
 * 	Copyright (c) 2016 onward, Aleksey Beletskii  <beletskiial@gmail.com>
 * 	All rights reserved
 *
 * 	github: https://github.com/alekseybeletskii
 *
 * 	The ADCDataViewer software serves for visualization and simple processing
 * 	of any data recorded with Analog Digital Converters in binary or text form.
 *
 * 	Commercial support is available. To find out more contact the author directly.
 *
 * 	Redistribution and use in source and binary forms, with or without
 * 	modification, are permitted provided that the following conditions are met:
 *
 * 	  1. Redistributions of source code must retain the above copyright notice, this
 * 	     list of conditions and the following disclaimer.
 * 	  2. Redistributions in binary form must reproduce the above copyright notice,
 * 	     this list of conditions and the following disclaimer in the documentation
 * 	     and/or other materials provided with the distribution.
 *
 * 	The software is distributed to You under terms of the GNU General Public
 * 	License. This means it is "free software". However, any program, using
 * 	ADCDataViewer _MUST_ be the "free software" as well.
 * 	See the GNU General Public License for more details
 * 	(file ./COPYING in the root of the distribution
 * 	or website <http://www.gnu.org/licenses/>)
 *
 * 	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * 	ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * 	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * 	DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * 	ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * 	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * 	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * 	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * 	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * 	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * 	********************* END LICENSE BLOCK ***********************************
 ******************************************************************************/

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
