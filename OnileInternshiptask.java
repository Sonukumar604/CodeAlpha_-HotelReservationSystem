import java.io.*;
import java.util.*;

class StockTradingPlatform {
    String symbol;
    double price;

    Stock(String symbol, double price) {
        this.symbol = symbol;
        this.price = price;
    }
}

class Transaction {
    String type; 
    String symbol;
    int quantity;
    double price;
    Date date;

    Transaction(String type, String symbol, int quantity, double price) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.date = new Date();
    }

    @Override
    public String toString() {
        return date + " - " + type + " " + quantity + " " + symbol + " @ " + price;
    }
}

class Portfolio implements Serializable {
    Map<String, Integer> holdings = new HashMap<>();
    List<Transaction> transactions = new ArrayList<>();
    double cash = 10000.0; // Starting cash

    void buy(Stock stock, int quantity) {
        double cost = stock.price * quantity;
        if (cash >= cost) {
            cash -= cost;
            holdings.put(stock.symbol, holdings.getOrDefault(stock.symbol, 0) + quantity);
            transactions.add(new Transaction("BUY", stock.symbol, quantity, stock.price));
            System.out.println("Bought " + quantity + " shares of " + stock.symbol);
        } else {
            System.out.println("Insufficient funds.");
        }
    }

    void sell(Stock stock, int quantity) {
        int owned = holdings.getOrDefault(stock.symbol, 0);
        if (owned >= quantity) {
            cash += stock.price * quantity;
            holdings.put(stock.symbol, owned - quantity);
            transactions.add(new Transaction("SELL", stock.symbol, quantity, stock.price));
            System.out.println("Sold " + quantity + " shares of " + stock.symbol);
        } else {
            System.out.println("Not enough shares to sell.");
        }
    }

    void showPortfolio(Map<String, Stock> market) {
        System.out.println("\n--- Portfolio ---");
        System.out.println("Cash: $" + cash);
        double total = cash;
        for (String symbol : holdings.keySet()) {
            int qty = holdings.get(symbol);
            double price = market.get(symbol).price;
            System.out.println(symbol + ": " + qty + " shares @ $" + price + " = $" + (qty * price));
            total += qty * price;
        }
        System.out.println("Total Portfolio Value: $" + total);
    }

    void showTransactions() {
        System.out.println("\n--- Transactions ---");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }
}

public class StockTradingPlatform {
    static Map<String, Stock> market = new HashMap<>();
    static Portfolio portfolio = new Portfolio();
    static final String DATA_FILE = "portfolio.ser";

    public static void main(String[] args) {
        // Initialize market data
        market.put("AAPL", new Stock("AAPL", 180.0));
        market.put("GOOG", new Stock("GOOG", 2700.0));
        market.put("TSLA", new Stock("TSLA", 700.0));
        market.put("AMZN", new Stock("AMZN", 3400.0));
        market.put("MSFT", new Stock("MSFT", 300.0));

        // Load portfolio if exists
        loadPortfolio();

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n1. Show Market Data\n2. Buy Stock\n3. Sell Stock\n4. Show Portfolio\n5. Show Transactions\n6. Save & Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            if (choice == 1) {
                showMarket();
            } else if (choice == 2) {
                System.out.print("Enter stock symbol: ");
                String symbol = sc.next().toUpperCase();
                System.out.print("Enter quantity: ");
                int qty = sc.nextInt();
                if (market.containsKey(symbol)) {
                    portfolio.buy(market.get(symbol), qty);
                } else {
                    System.out.println("Stock not found.");
                }
            } else if (choice == 3) {
                System.out.print("Enter stock symbol: ");
                String symbol = sc.next().toUpperCase();
                System.out.print("Enter quantity: ");
                int qty = sc.nextInt();
                if (market.containsKey(symbol)) {
                    portfolio.sell(market.get(symbol), qty);
                } else {
                    System.out.println("Stock not found.");
                }
            } else if (choice == 4) {
                portfolio.showPortfolio(market);
            } else if (choice == 5) {
                portfolio.showTransactions();
            } else if (choice == 6) {
                savePortfolio();
                System.out.println("Portfolio saved. Exiting.");
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
        sc.close();
    }

    static void showMarket() {
        System.out.println("\n--- Market Data ---");
        for (Stock stock : market.values()) {
            System.out.println(stock.symbol + ": $" + stock.price);
        }
    }

    static void savePortfolio() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(portfolio);
        } catch (IOException e) {
            System.out.println("Error saving portfolio: " + e.getMessage());
        }
    }

    static void loadPortfolio() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                portfolio = (Portfolio) in.readObject();
                System.out.println("Portfolio loaded.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error loading portfolio: " + e.getMessage());
            }
        }
    }
}