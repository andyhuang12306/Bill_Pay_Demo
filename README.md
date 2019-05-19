# Bill_Pay_Demo

This demo is mainly for interview test. 

Main Function:
  1. two pages one for bill list display, one for payment history list display
  2. use database with two tables bill and payment to init some bills data
  3. bill list can choose one or more bills to pay
  4. check the account balance before go to pay process, transfer bill to payment history data and save payments to database once paid successful
  5. update the bill list
  6. click the payhis to go to payment history list page
  
Technique Points:
  1. recyclerview to load list
  2. customized view for loading more view
  3. sqlite for database with contenprovider to incapsulate the operation with sqlite
  
Last Words:
  1. since it's only a simple demo with two pages, no structure level thing is considered or code template is considered
  2. no network is involved use database with init data instead
  
Any suggestion or issue finding is highly appreciated.
