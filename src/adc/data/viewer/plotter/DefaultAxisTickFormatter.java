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

package adc.data.viewer.plotter;

import java.text.NumberFormat;

/**
 * DefaultAxisTickFormatter formats labels using a default number instance.
 *
 * @author Jason Winnebeck
 */
public class DefaultAxisTickFormatter implements AxisTickFormatter {
//	private final NumberFormat normalFormat = NumberFormat.getNumberInstance();
//	private final NumberFormat engFormat = new DecimalFormat( "0.###E0" );

	private NumberFormat currFormat = NumberFormat.getNumberInstance();

	public DefaultAxisTickFormatter() {
	}

	@Override
	public void setRange( double low, double high, double tickSpacing ) {
		//The below is an attempt as using engineering notation for large numbers, but it doesn't work.
//		currFormat = normalFormat;
//		double log10 = Math.log10( low );
//		if ( log10 < -4.0 || log10 > 5.0 ) {
//			currFormat = engFormat;
//		} else {
//			log10 = Math.log10( high );
//			if ( log10 < -4.0 || log10 > 5.0 ) {
//				currFormat = engFormat;
//			}
//		}

//		if (tickSpacing <= 10000.0)
//			currFormat = normalFormat;
//		else
//			currFormat = engFormat;
	}

	@Override
	public String format( Number value ) {
		return currFormat.format( value );
	}
}
