import java.util.*;
import java.io.*;
import java.nio.file.*;

class ConfigurationManager {
    private static volatile ConfigurationManager instance;
    private static final Object lock = new Object();
    private Map<String, String> settings;
    private boolean isLoaded;

    private ConfigurationManager() {
        settings = new HashMap<>();
        isLoaded = false;
    }

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    public void loadFromFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        if (Files.exists(path)) {
            List<String> lines = Files.readAllLines(path);
            settings.clear();
            for (String line : lines) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    settings.put(parts[0].trim(), parts[1].trim());
                }
            }
            isLoaded = true;
            System.out.println("Конфигурация файлдан жүктелді: " + filename);
        } else {
            throw new IOException("Файл табылмады: " + filename);
        }
    }

    public void loadFromDatabase(String connectionString) {
        settings.put("db.host", "localhost");
        settings.put("db.port", "5432");
        settings.put("db.name", "mydb");
        settings.put("db.user", "admin");
        isLoaded = true;
        System.out.println("Конфигурация дерекқордан жүктелді: " + connectionString);
    }

    public void loadDefault() {
        settings.put("app.name", "MyApplication");
        settings.put("app.version", "1.0.0");
        settings.put("app.theme", "light");
        settings.put("app.language", "kk");
        settings.put("max.users", "100");
        settings.put("timeout", "30");
        isLoaded = true;
        System.out.println("Конфигурация әдепкі мәндермен жүктелді");
    }

    public String getSetting(String key) {
        if (!isLoaded) {
            throw new IllegalStateException("Конфигурация жүктелмеген!");
        }
        String value = settings.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Орнатылмаған параметр: " + key);
        }
        return value;
    }

    public String getSetting(String key, String defaultValue) {
        if (!isLoaded) return defaultValue;
        return settings.getOrDefault(key, defaultValue);
    }

    public void setSetting(String key, String value) {
        if (!isLoaded) {
            loadDefault();
        }
        settings.put(key, value);
    }

    public void saveToFile(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            lines.add(entry.getKey() + " = " + entry.getValue());
        }
        Files.write(Paths.get(filename), lines);
        System.out.println("Конфигурация файлға сақталды: " + filename);
    }

    public void printAllSettings() {
        System.out.println("\n=== Барлық конфигурациялар ===");
        for (Map.Entry<String, String> entry : settings.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("=============================\n");
    }
}

class Report {
    private String header;
    private String content;
    private String footer;

    public void setHeader(String header) { this.header = header; }
    public void setContent(String content) { this.content = content; }
    public void setFooter(String footer) { this.footer = footer; }

    public String getHeader() { return header; }
    public String getContent() { return content; }
    public String getFooter() { return footer; }
}

interface IReportBuilder {
    IReportBuilder setHeader(String header);
    IReportBuilder setContent(String content);
    IReportBuilder setFooter(String footer);
    Report getReport();
}

class TextReportBuilder implements IReportBuilder {
    private Report report;

    public TextReportBuilder() {
        this.report = new Report();
    }

    @Override
    public IReportBuilder setHeader(String header) {
        report.setHeader("=== " + header + " ===\n");
        return this;
    }

    @Override
    public IReportBuilder setContent(String content) {
        report.setContent(content + "\n");
        return this;
    }

    @Override
    public IReportBuilder setFooter(String footer) {
        report.setFooter("--- " + footer + " ---\n");
        return this;
    }

    @Override
    public Report getReport() {
        return report;
    }
}

class HtmlReportBuilder implements IReportBuilder {
    private Report report;

    public HtmlReportBuilder() {
        this.report = new Report();
    }

    @Override
    public IReportBuilder setHeader(String header) {
        report.setHeader("<h1>" + header + "</h1>\n");
        return this;
    }

    @Override
    public IReportBuilder setContent(String content) {
        report.setContent("<p>" + content + "</p>\n");
        return this;
    }

    @Override
    public IReportBuilder setFooter(String footer) {
        report.setFooter("<footer>" + footer + "</footer>\n");
        return this;
    }

    @Override
    public Report getReport() {
        return report;
    }
}

class XmlReportBuilder implements IReportBuilder {
    private Report report;

    public XmlReportBuilder() {
        this.report = new Report();
    }

    @Override
    public IReportBuilder setHeader(String header) {
        report.setHeader("<header>" + header + "</header>\n");
        return this;
    }

    @Override
    public IReportBuilder setContent(String content) {
        report.setContent("<content>" + content + "</content>\n");
        return this;
    }

    @Override
    public IReportBuilder setFooter(String footer) {
        report.setFooter("<footer>" + footer + "</footer>\n");
        return this;
    }

    @Override
    public Report getReport() {
        return report;
    }
}

class ReportDirector {
    public Report constructReport(IReportBuilder builder, String header, String content, String footer) {
        return builder.setHeader(header).setContent(content).setFooter(footer).getReport();
    }

    public void printReport(Report report, String reportType) {
        System.out.println("\n=== " + reportType + " ===");
        System.out.print(report.getHeader());
        System.out.print(report.getContent());
        System.out.print(report.getFooter());
        System.out.println("================\n");
    }
}

class Product implements Cloneable {
    private String name;
    private double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Product(Product other) {
        this.name = other.name;
        this.price = other.price;
        this.quantity = other.quantity;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() {
        return price * quantity;
    }

    @Override
    public Product clone() {
        return new Product(this);
    }

    @Override
    public String toString() {
        return String.format("%s (бағасы: %.2f, саны: %d, жиыны: %.2f)", name, price, quantity, getTotalPrice());
    }
}

class Discount implements Cloneable {
    private String type;
    private double percentage;

    public Discount(String type, double percentage) {
        this.type = type;
        this.percentage = percentage;
    }

    public Discount(Discount other) {
        this.type = other.type;
        this.percentage = other.percentage;
    }

    public String getType() { return type; }
    public double getPercentage() { return percentage; }

    public double applyDiscount(double amount) {
        return amount * (1 - percentage / 100);
    }

    @Override
    public Discount clone() {
        return new Discount(this);
    }

    @Override
    public String toString() {
        return type + " (" + percentage + "%)";
    }
}

class Order implements Cloneable {
    private List<Product> products;
    private double deliveryCost;
    private Discount discount;
    private String paymentMethod;
    private String orderId;

    public Order(String orderId) {
        this.orderId = orderId;
        this.products = new ArrayList<>();
        this.deliveryCost = 0;
        this.discount = null;
        this.paymentMethod = "Наличными";
    }

    public Order(Order other) {
        this.orderId = other.orderId + "_copy";
        this.products = new ArrayList<>();
        for (Product product : other.products) {
            this.products.add(product.clone());
        }
        this.deliveryCost = other.deliveryCost;
        this.discount = other.discount != null ? other.discount.clone() : null;
        this.paymentMethod = other.paymentMethod;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void setDeliveryCost(double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderId() { return orderId; }

    public double calculateTotal() {
        double total = products.stream().mapToDouble(Product::getTotalPrice).sum();
        total += deliveryCost;
        if (discount != null) {
            total = discount.applyDiscount(total);
        }
        return total;
    }

    public void removeProduct(String productName) {
        products.removeIf(p -> p.getName().equals(productName));
    }

    @Override
    public Order clone() {
        return new Order(this);
    }

    public void printOrder() {
        System.out.println("\n=== ЗАКАЗ: " + orderId + " ===");
        System.out.println("Тауарлар:");
        for (Product p : products) {
            System.out.println("  - " + p);
        }
        System.out.println("Жеткізу құны: " + deliveryCost);
        System.out.println("Скидка: " + (discount != null ? discount : "жоқ"));
        System.out.println("Төлем әдісі: " + paymentMethod);
        System.out.println("БАРЛЫҒЫ: " + calculateTotal());
        System.out.println("====================\n");
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("ПОРОЖДАЮЩИЙ ПАТТЕРНДЕР: ДЕМОНСТРАЦИЯ");
        System.out.println("========================================\n");

        System.out.println("--- 1. SINGLETON PATTERN ---");
        System.out.println("Конфигурация менеджерінің жұмысы:\n");

        ConfigurationManager config1 = ConfigurationManager.getInstance();
        System.out.println("config1 алынды");
        config1.loadDefault();
        config1.setSetting("app.theme", "dark");
        config1.setSetting("app.language", "en");

        config1.printAllSettings();

        ConfigurationManager config2 = ConfigurationManager.getInstance();
        System.out.println("config2 алынды");

        String theme = config2.getSetting("app.theme");
        System.out.println("config2 арқылы оқылған app.theme = " + theme);

        System.out.println("\nconfig1 және config2 бірдей ме? " + (config1 == config2));

        try {
            config1.saveToFile("config.txt");
            System.out.println("\nКонфигурация файлға сақталды");

            ConfigurationManager config3 = ConfigurationManager.getInstance();
            config3.loadFromFile("config.txt");
            config3.printAllSettings();

        } catch (IOException e) {
            System.out.println("Қате: " + e.getMessage());
        }

        try {
            System.out.println("\nЖоқ параметрді оқу әрекеті:");
            config1.getSetting("nonexistent.key");
        } catch (IllegalArgumentException e) {
            System.out.println("Қате дұрыс өңделді: " + e.getMessage());
        }

        System.out.println("\n--- 2. BUILDER PATTERN ---");
        System.out.println("Отчеттарды құру:\n");

        ReportDirector director = new ReportDirector();

        TextReportBuilder textBuilder = new TextReportBuilder();
        Report textReport = director.constructReport(
                textBuilder,
                "Айлық есеп",
                "Қаңтар айында сатылым 20% өсті. Жаңа клиенттер саны: 150.",
                "Есеп беруші: Администратор"
        );
        director.printReport(textReport, "МӘТІНДІК ЕСЕП");

        HtmlReportBuilder htmlBuilder = new HtmlReportBuilder();
        Report htmlReport = director.constructReport(
                htmlBuilder,
                "Айлық есеп",
                "Қаңтар айында сатылым 20% өсті. Жаңа клиенттер саны: 150.",
                "Есеп беруші: Администратор"
        );
        director.printReport(htmlReport, "HTML ЕСЕП");

        XmlReportBuilder xmlBuilder = new XmlReportBuilder();
        Report xmlReport = director.constructReport(
                xmlBuilder,
                "Айлық есеп",
                "Қаңтар айында сатылым 20% өсті. Жаңа клиенттер саны: 150.",
                "Есеп беруші: Администратор"
        );
        director.printReport(xmlReport, "XML ЕСЕП");

        System.out.println("--- 3. PROTOTYPE PATTERN ---");
        System.out.println("Заказдарды клондау:\n");

        Order originalOrder = new Order("ORD-001");

        originalOrder.addProduct(new Product("Ноутбук", 450000, 1));
        originalOrder.addProduct(new Product("Тінтуір", 15000, 2));
        originalOrder.addProduct(new Product("Пернетақта", 25000, 1));

        originalOrder.setDeliveryCost(5000);
        originalOrder.setDiscount(new Discount("Жаңажылдық", 10));
        originalOrder.setPaymentMethod("Kaspi банк");

        System.out.println("БАСТАПҚЫ ЗАКАЗ:");
        originalOrder.printOrder();

        Order clonedOrder = originalOrder.clone();
        System.out.println("КЛОНДАЛҒАН ЗАКАЗ (өзгертулер енгізілген):");

        clonedOrder.removeProduct("Тінтуір");
        clonedOrder.addProduct(new Product("Сымсыз тінтуір", 18000, 1));
        clonedOrder.setDiscount(new Discount("Клубтық", 5));
        clonedOrder.setPaymentMethod("Картамен");

        clonedOrder.printOrder();

        System.out.println("БАСТАПҚЫ ЗАКАЗ (өзгермеген):");
        originalOrder.printOrder();

        System.out.println("originalOrder == clonedOrder? " + (originalOrder == clonedOrder));

    }
}