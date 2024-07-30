# trading-cockpit-backend
Backend of the Trading Cockpit application. 
The user interface for this application is provided by the [Trading Cockpit Frontend](https://github.com/MiRitter87/trading-cockpit-frontend).

## Features
The backend provides WebServices to create, read, update and delete the following business objects.

- Price Alerts
- Instruments
- Lists
- Scans

Besides master data management, the application retrieves quotation data using third-party data providers.
It generates charts, crawls the database of instruments and quotations providing specific result sets and is able to interpret price and volume behavior of instruments.
The features are described in more detail in the following sections.

### Price Alerts
A price alert notifies the user if the price of an instrument reaches a certain threshold. 
There are two types of alerts. One alert informs you if a price is equal or higher than your defined price. The other type informs you if the price is equal of lower your defined price. 
The backend queries stock quotes cyclically to check if the defined threshold has been triggered.
If requested, an E-Mail is sent to a recipient when an alert has been triggered.

### Instruments
Instruments are tradable assets that come in different forms like stocks or ETFs. Currently instruments of type 'stock', 'etf', 'sector','industry group' and 'ratio' can be managed.

### Lists
Lists allow you to organize sets of instruments. The list feature allows you for example to reproduce ETFs or stock indexes. Lists are used by the scan functionality to scan and screen all instruments of multiple lists.
Lists are also used in the context of statistical charts.

### Scans
A Scan consists of multiple lists that have instruments defined in them. The scan process queries historical price and volume data of the instruments using third-party data providers.
Indicators are then calculated and the instruments are ranked according to their performance of the past year.
The backend offers multiple templates to filter the scan results by different characteristics. For example stocks can be filtered that have advanced on above-average volume.
Ratio data are not queried from a third-party data provider. Instead they are calculated locally within the application based on existing instrument data.

### Dashboard
The Dashboard aims to provide a meta overview of the current state of the market.
At the end of the scan process, statistical data of the instruments are calculated. Those statistics are exposed as tabular data.

### Charts
The application generates a multitude of charts for its instruments and lists. Those charts mainly fall into two categories: statistical charts and price/volume charts.

### Health Check
The application provides a knowledge-based system to interpret the price and volume behavior of instruments.
Instruments can be analyzed according to roughly 20 time-tested rules to gauge their health.
Those rules are grouped into three profiles: up-trend confirmations, up-trend violations and selling into strength.

## Technology

The Trading Cockpit is based on the following technologies and frameworks:

 - [Jakarta Persistence](https://jakarta.ee/specifications/persistence/) and [Hibernate](https://hibernate.org/) for ORM (Object Relational Mapping)
 - [HSQLDB](https://hsqldb.org/) as database
 - [Jakarta Bean Validation](https://beanvalidation.org/) for validation of model classes
 - [Jakarta RESTful Web Services](https://jakarta.ee/specifications/restful-ws/3.0/) for RESTful WebServices
 - [Apache Log4j](https://logging.apache.org/log4j/2.x/) for application logging
 - [JUnit](https://junit.org/junit5/) for test-driven development
 - [OkHttp](https://square.github.io/okhttp/) to access third-party WebServices
 - [Apache POI](https://poi.apache.org/) to generate Excel Sheets
 - [opencsv](https://opencsv.sourceforge.net/) to parse CSV files
 - [JFreeChart](https://www.jfree.org/jfreechart/) to generate charts
 - [HtmlUnit](https://htmlunit.sourceforge.io/) to extract price information from third-party Websites
 - [Apache Maven](https://maven.apache.org/) as build system

## Deployment
The application uses Servlet Version 6 and Expression Language Version 5. The WebServer has to support those.
[Apache Tomcat](https://tomcat.apache.org/whichversion.html) Version 10.1.x supports the specifications and can be used to run the application.

1. Build the project using the Maven run configuration *trading-cockpit-backend (clean install).launch*
2. Deploy the generated ".war"-file to your WebServer. The application name has to be "trading-cockpit-backend". The frontend expects the WebServices under this path.
3. Move the configuration file *tradingCockpitBackend.properties* from the *target* folder of your build path to the *conf* folder of your WebServer.
4. Modify the configuration file according to your needs.

## Available Data Provider

| Data Provider   			| Historical Quotations							| Real-Time Quotations				| Delayed Quotations	|
|---------------------------|-----------------------------------------------|-----------------------------------|-----------------------|
| Yahoo				 		| NYSE, NDQ, AMEX, OTC, TSX, TSX/V, CSE, LSE	| NYSE, NDQ, AMEX, TSX, TSX/V, CSE	| OTC, LSE				|
| MarketWatch				| NYSE, NDQ, AMEX, OTC, TSX, TSX/V, CSE, LSE	|									|						|
| Investing					|  												| NYSE, NDQ, AMEX, TSX, LSE			| OTC, TSX/V, CSE		|
| GlobeAndMail				| NYSE, NDQ, AMEX, OTC, TSX, TSX/V, CSE			| NYSE, NDQ, AMEX, TSX, CSE			| OTC, TSX/V			|
| CNBC						|												| NYSE, NDQ, AMEX, OTC, LSE			| TSX, TSX/V			|

## Configuration
The configuration file *tradingCockpitBackend.properties* has multiple properties that control the applications behavior.

| Property   					|      Description      											|  Example 		|
|-------------------------------|:-----------------------------------------------------------------:|--------------:|
| queryInterval.priceAlert 		|  Number of seconds between stock quote queries for the price alert| 30       		|
| queryInterval.scan			|  Number of seconds between stock quote queries for the scanner	| 5		   		|
| startTime.hour 				|  Application starts stock quote queries at this time 				| 15       		|
| startTime.minute 				|  Application starts stock quote queries at this time 				| 30       		|
| endTime.hour 					|  Application ends stock quote queries at this time 				| 22       		|
| endTime.minute 				|  Application ends stock quote queries at this time 				| 0        		|
| dataProvider.scan.nyse		|  Data provider for historical quotations of exchange NYSE			| YAHOO	   		|
| dataProvider.scan.ndq			|  Data provider for historical quotations of exchange Nasdaq		| YAHOO	   		|
| dataProvider.scan.amex		|  Data provider for historical quotations of exchange AMEX			| YAHOO	   		|
| dataProvider.scan.otc			|  Data provider for historical quotations of exchange US OTC		| YAHOO	   		|
| dataProvider.scan.tsx			|  Data provider for historical quotations of exchange TSX			| YAHOO	   		|
| dataProvider.scan.tsxv		|  Data provider for historical quotations of exchange TSX/V		| YAHOO	   		|
| dataProvider.scan.cse			|  Data provider for historical quotations of exchange CSE			| YAHOO	   		|
| dataProvider.scan.lse			|  Data provider for historical quotations of exchange LSE			| YAHOO	   		|
| dataProvider.priceAlert.nyse	|  Data provider for current quotations of exchange NYSE			| YAHOO	   		|
| dataProvider.priceAlert.ndq	|  Data provider for current quotations of exchange Nasdaq			| YAHOO	   		|
| dataProvider.priceAlert.amex	|  Data provider for current quotations of exchange AMEX			| YAHOO	   		|
| dataProvider.priceAlert.otc	|  Data provider for current quotations of exchange US OTC			| YAHOO	   		|
| dataProvider.priceAlert.tsx	|  Data provider for current quotations of exchange TSX				| YAHOO    		|
| dataProvider.priceAlert.tsxv	|  Data provider for current quotations of exchange TSX/V			| YAHOO	   		|
| dataProvider.priceAlert.cse	|  Data provider for current quotations of exchange CSE				| YAHOO    		|
| dataProvider.priceAlert.lse	|  Data provider for current quotations of exchange LSE				| CNBC	   		|
| mail.smtp.server				|  The SMTP server that is used to send an E-Mail					| mail.gmx.net	|
| mail.smtp.port				|  The Port of the SMTP server										| 587			|
| mail.sender.username			|  The E-Mail address of the account from which the mails are sent	| max@gmx.de	|
| mail.sender.password			|  The password of the account										| mypassword	|

## License

Copyright © 2024, [MiRitter87](https://github.com/MiRitter87). No License.