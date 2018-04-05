# Travel Dates Finder

Are you a frequent flyer? Need to know where you travelled to in the last 5 years? This program does just that! 

By providing your location history to the program, it will find dates where you travelled a large distance (aka to a different country). 


## Getting Started

These instructions will show you how to setup the program and find your trips.


### Prerequisites

* Will only find trips where your location tracking was enabled. This applies to both your phone and to Google's tracking service.
* Ensure you have Java installed


### Installing
#### Step 1 - Downloading Your Google Data
* Visit https://takeout.google.com/settings/takeout. This website lets you download all the data google has for your account.

* Click the "Select None" button

* Scroll down to find the "Location History" option.

* Enable it and ensure the format is set to "JSON Format".

* Scroll to the bottom and click next.

* Select your preferred download method and press "Create Archive" (May take some time to process).

#### Step 2

* Now that your Google Location History is downloaded you should extract the .json file onto your computer.

* Next, download the [TravelDatesFinder.jar](https://github.com/Sf298/Travel-Dates-Finder/raw/master/TravelDatesFinder.jar) file.

* Now your are all set up!



## Running the Program

* Run the jar file

* The first window will ask you to give it the location of the file.

* The second window is to provide the minimum distance you have to have travelled for it to count as changing countries. (200000 seems to be a good number).

* Now we wait. Depending on the speed of your computer and the size of the location history file, the process may take several minutes.


## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Google Geocoding API](http://code.google.com/apis/maps/documentation/geocoding/) - Coordinate to Address


## Authors

* Saud Fatayerji

## License

Free for use and modification. Please give credit where it's due!
