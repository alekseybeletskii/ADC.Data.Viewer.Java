This application allows to visualize binary files created with analog digital converters:

1. L-Card L-783M, http://www.lcard.ru/products/boards/l-783
2. L-Card e20-10, http://www.lcard.ru/products/external/e20-10
3. SATURN SDI-AD12-128H, http://www.saturn-data.com

ADC files examples are included in the "resources/sampledata/" subfolder.
Check "resources/jpg/" subfolder with screenshots.

![controlwindow](resources/jpg/main1000000points.jpg)

![plot](resources/jpg/plot1000000pointsZoom2.jpg)

Executable Jar file is located in "resources/jar/" subfolder.

An information about every signal will be extracted from an appropriate
binary *.par file  in correspondence with a chosen binary *.dat file
(or from the beginning of a chosen *.dat file in case of SATURN SDI-AD12-128H)
This information will be shown in the right part of the program's main window,
according to a selected  table row.

In addition it is possible to open data presented in a text file.
For now it should be a single file per signal - with a single data column inside.
You will be prompted to enter some data manually in this case (date, channel number, ADC rate...).


How to use:
   - "File->Open" shows a file chooser dialog, files with "dat" and "txt" extensions will be available;
   - "Plot->Export all to txt " converts all signals shown in the main table
       to the text format and save it to a subfolder named "<source>/txt"
   - "Plot->Draw Plots" plots checked signals in a separate window.
   -  When plots are shown, Savitsky-Golay filter can be applied.
   -  double click on table will check/uncheck all signals
   -  individual color can be set for every signal

Zoom plots with mouse:
   - zoom: left click + drag from a top left to a bottom right
   - rescale to a full view: left click + drag back from a bottom right to a top left

NOTE: if there are a lot of points, drawing will take some time.
      Laptop with Intel i5 renders a 1 000 000 of points about 1 minute,
      in case of drawing all data "as is".
      Feature is included that enables  rapid drawing even when there are
      millions of data points. Just use zoom to view all the details.
      Canvas drawing now successfully copes with millions of data points
      due to simple "point-per-pixel" approach.