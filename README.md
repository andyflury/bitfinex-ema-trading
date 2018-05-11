Simple Cryptocurrency Trading Strategy that makes use of the Bitfinex API (https://docs.bitfinex.com/v1/reference) and the TA4J library (https://github.com/ta4j/ta4j)

The strategy has the following logic:
* The strategy is based on live market data for BTCUSD
* It creates 10-second bars
* It then creates two exponential moving averages (one with a lookback period of 5 the other with a lookback period of 10)
* Then it calculates the difference between the two exponential moving averages
* Whenever the difference becomes positive (and was previously negative) the strategy places a buy order
* Whenever the difference becomes negative (and was previously positive) the strategy places a buy order
* The strategy sends market orders to the Bitfinex API with a quantity of 0.002 BTC

How to run the strategy:
* Add your Bitfinex API keys to application.properties
* Run ch.algotrader.ema.Application
