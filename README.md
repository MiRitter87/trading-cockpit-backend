# trading-cockpit-backend
Backend of the Trading Cockpit application.

## Features
The backend provides WebServices to create, read, update and delete the following business objects.

- Price Alerts
- Instruments
- Lists
- Scans

Furthermore it uses third-party WebServices.

- Yahoo finance for retrieval of current and historical stock data.

### Price Alerts
A price alert notifies you if the price of an instrument reaches a certain threshold. There are two types of alerts. One alert informs you if a price is equal or higher than your defined price. The other type informs you if the price is equal of lower your defined price. The backend queries stock quotes cyclically to check if the defined threshold has been triggered.

### Instruments
Instruments are tradable assets that come in different forms like stocks or ETFs. Currently only instruments of type 'stock' can be managed.

### Lists
Lists allow you to organize sets of instruments. The list feature allows you for example to reproduce ETFs or stock indexes. Lists are used by the scan functionality to scan and screen all stocks of multiple lists.

### Scans
A Scan consists of multiple lists that have instruments defined in them. The scan process queries historical price and volume data of the instruments. Indicators are then calculated and the instruments are ranked according to their performance of the past year.

## Technology

The Trading Cockpit is based on the following technologies and frameworks

 - **Java Persistence API** and **Hibernate** for ORM (Object Relational Mapping)
 - **HSQLDB** as database
 - **Bean Validation API** for validation of model classes
 - **Jersey** for RESTful WebServices
 - **Log4J** for application logging
 - **JUnit** for test-driven development
 - **okhttp** to access third-party WebServices
 - **Apache Maven** as build system

## Installation

1. Build the project using the Maven run configuration "trading-cockpit-backend (clean install).launch"
2. Deploy the ".war"-file to your WebServer. The application name has to be "trading-cockpit-backend". The frontend expects the WebServices under this path.
3. Move the configuration file "tradingCockpitBackend.properties" from the "target"-folder of your build path to the "conf"-folder of your WebServer.
4. Modify the configuration file according to your needs.

## Configuration
The configuration file "tradingCockpitBackend.properties" has multiple properties that control the applications behavior.

| Property   					|      Description      											|  Example |
|-------------------------------|:-----------------------------------------------------------------:|---------:|
| queryInterval.priceAlert=30 	|  Number of seconds between stock quote queries for the price alert| 30       |
| queryInterval.scan=5			|  Number of seconds between stock quote queries for the scanner	| 5		   |
| startTime.hour 				|  Application starts stock quote queries at this time 				| 15       |
| startTime.minute 				|  Application starts stock quote queries at this time 				| 30       |
| endTime.hour 					|  Application ends stock quote queries at this time 				| 22       |
| endTime.minute 				|  Application ends stock quote queries at this time 				| 0        |