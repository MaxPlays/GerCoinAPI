# GerCoinAPI
A german CoinAPI

## Installation ##
Click [here](https://github.com/MaxPlays/GerCoinAPI/releases/latest) to download the latest version of the plugin and drag and drop it into your plugin folder. Then, start configuring the database in the config.yml after starting the server.

# Commands and permissions ##
All commands are visible by issuing the command **/coins help**  
The permission for all admin commands is **Coins.admin**

## API ##
Of course, there is an API included. To use it, simply add the plugin as a Library to your project and follow the examples below.
```java
 CoinAPI.getCoins("PlayerName", new CoinManager.CoinQuery() {
                    @Override
                    public void onSuccessfulResult(int result) {
                        p.sendMessage(result);
                    }

                    @Override
                    public void onPlayerNotExists() {
                        p.sendMessage("That player does not exist in the database");
                    }
                });

 CoinAPI.setCoins(player, 0);
 
 CoinAPI.addCoins("PlayerName", 10);
 
 CoinAPI.removeCoins(player, 10);
```
As you can see, you can either choose the player's name or the actual Player object as the first argument.
