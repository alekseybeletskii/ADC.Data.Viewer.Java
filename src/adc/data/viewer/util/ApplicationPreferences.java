/*
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
 */

package adc.data.viewer.util;


import adc.data.viewer.ui.MainApp;

import java.util.prefs.Preferences;

public class ApplicationPreferences {

    private static Preferences appPreferencesRootNode = MainApp.appPreferencesRootNode;

public static void setAllPreferencesToBasicDefaults(){

    appPreferencesRootNode.put("defaultPlotType","Raw");
    appPreferencesRootNode.put("defaultPlotStyle","line");
    appPreferencesRootNode.put("defaultPlotsLayoutType","AllPlots");
    appPreferencesRootNode.putDouble("defaultWidthOfLine",1.0);
    appPreferencesRootNode.putInt("defaultPointSize",6);
    appPreferencesRootNode.putInt("defaultSGFilterLeft",50); //points
    appPreferencesRootNode.putInt("defaultSGFilterRight",50); //points
    appPreferencesRootNode.putInt("defaultSGFilterLeftOrder",1);
    appPreferencesRootNode.putDouble("defaultFixZeroShiftStart",0); //ms
    appPreferencesRootNode.putDouble("defaultFixZeroShiftEnd",0);
    appPreferencesRootNode.putBoolean("defaultFixZeroShift",false);
    appPreferencesRootNode.putDouble("defaultXAxisMin",-1);
    appPreferencesRootNode.putDouble("defaultXAxisMax",1);
    appPreferencesRootNode.putDouble("defaultYAxisMin",-1);
    appPreferencesRootNode.putDouble("defaultYAxisMax",1);
    appPreferencesRootNode.putInt("defaultSpectrogramFFTSize",256);
    appPreferencesRootNode.put("defaultSpectrogramFFTWindow","Hunning");
    appPreferencesRootNode.putInt("defaultSpectrogramFFTOverlap",50);
    appPreferencesRootNode.putBoolean("defaultUseNewDefaults",false);

}



}
