# clustered-data-warehouse
Application to load CSV data to database using Java 8, Akka Framework, Regular Expressions and MySQL

###### Run mvn clean install in the extracted folder.
- Copy following files to separate folder.
- clustered-data-warehouse/target/clustered-data-warehouse-1.0.0.0-SNAPSHOT.jar 
- clustered-data-warehouse/target/lib
- conf

###### Configuring the application
- Open conf/config.ini file. It contains the configurations to run the application
- MISC section should configure the shared folder location that deal files will be copied. Application should have the read write permissions for this location.
- MYSQL_DB section should configure the MySQL database details for persisting deal information. In startup, application will create following tables in given database automatically.
```
cdw_deals(dealUniqueId, dealAmount, fileName, fromCurrency, timestamp, toCurrency)
```
 – contains the valid deals received from the file.
```
cdw_invalid_deals(dealUniqueId, dealAmount, fileName, fromCurrency, timestamp, toCurrency)
```
 – contains erroneous deals received from the file.
```
deal_count_per_currency(currency, count)
``` – contains the count of deals grouped by the currency.
- VALIDATIONS section should contain the regular expression for validate the each field in the deal. Currently it is configured to accept ant string for string fields and any double value for deal amount.


