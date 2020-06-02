import org.junit.Assert;
import org.junit.Test;
import pl.mbierut.exceptions.InsufficientFundsException;
import pl.mbierut.models.*;
import pl.mbierut.models.enums.BuyOrSell;
import pl.mbierut.models.enums.Currency;
import pl.mbierut.services.TransactionService;

public class OrdersTest {

    @Test
    public void orderShouldProcessCorrectly() {
        User testUser = new User("Test", "test@test.com", "1");
        testUser.getWallet().getCurrencies().put(Currency.PLN, 100.0);
        testUser.getWallet().getCurrencies().put(Currency.AUD, 10.0);
        testUser.getWallet().getCurrencies().put(Currency.CAD, 35.1);
        testUser.getWallet().getCurrencies().put(Currency.JPY, 995.0);
        testUser.getWallet().getCurrencies().put(Currency.SEK, 3.5);

        Funds funds1 = new Funds(Currency.CAD, 15.0);
        Order order1 = new Order(funds1, Currency.USD, BuyOrSell.sell);
        try {
            testUser.getWallet().fulfillOrder(order1);
            Assert.assertEquals(35.1 - 15.0,
                    testUser.getWallet().getCurrencies().get(Currency.CAD), 0.0);
            Assert.assertEquals(15.0 * Currency.CAD.getSellRate() / Currency.USD.getBuyRate(),
                    testUser.getWallet().getCurrencies().get(Currency.USD), 0.0);
        } catch (InsufficientFundsException exception) {
            exception.printStackTrace();
        }
    }

    @Test(expected = InsufficientFundsException.class)
    public void insufficientFundsTestThrownProperly() throws InsufficientFundsException {
        User testUser = new User("Test", "test@test.com", "1");
        testUser.getWallet().getCurrencies().put(Currency.PLN, 100.0);

        Funds funds2 = new Funds(Currency.AUD, 30.0);
        Order order2 = new Order(funds2, Currency.PLN, BuyOrSell.sell);
        testUser.getWallet().fulfillOrder(order2);
    }

    @Test
    public void sendFundsSendsFundsCorrectly() {
        User user1 = new User("test1", "test@test.com", "1");
        user1.getWallet().getCurrencies().put(Currency.PLN, 10.0);

        User user2 = new User("test2", "test@test.com", "1");
        user2.getWallet().getCurrencies().put(Currency.PLN, 10.0);

        Funds funds = new Funds(Currency.PLN, 9.0);
        try {
            user1.getWallet().sendMoney(user2.getWallet(), funds);
        } catch (InsufficientFundsException e) {
            e.printStackTrace();
        }

        Assert.assertEquals(1.0, user1.getWallet().getCurrencies().get(Currency.PLN), 0.0);
        Assert.assertEquals(19.0, user2.getWallet().getCurrencies().get(Currency.PLN), 0.0);
    }

    @Test
    public void makeOrderWorksAndWritesInTransactionHistory() {
        User testUser = new User("Test", "test@test.com", "1");
        testUser.getWallet().getCurrencies().put(Currency.PLN, 100.0);
        testUser.getWallet().getCurrencies().put(Currency.AUD, 10.0);
        testUser.getWallet().getCurrencies().put(Currency.CAD, 35.1);
        testUser.getWallet().getCurrencies().put(Currency.JPY, 995.0);
        testUser.getWallet().getCurrencies().put(Currency.SEK, 3.5);

        Funds funds1 = new Funds(Currency.CAD, 15.0);
        Order order1 = new Order(funds1, Currency.USD, BuyOrSell.sell);
        TransactionService service = new TransactionService();
        service.makeOrder(order1, testUser);
        Assert.assertEquals(35.1 - 15.0,
                testUser.getWallet().getCurrencies().get(Currency.CAD), 0.0);
        Assert.assertEquals(15.0 * Currency.CAD.getSellRate() / Currency.USD.getBuyRate(),
                testUser.getWallet().getCurrencies().get(Currency.USD), 0.0);
        Assert.assertNotNull(testUser.getOrderHistory());
        System.out.println(testUser.getOrderHistory());
    }

    @Test
    public void addingFundsWorks() {
        User testUser = new User("Test", "test@test.com", "1");
        testUser.getWallet().getCurrencies().put(Currency.PLN, 100.0);
        testUser.getWallet().getCurrencies().put(Currency.AUD, 10.0);
        testUser.getWallet().addFunds(new Funds(Currency.SEK, 20.0));
        Assert.assertEquals(20.0, testUser.getWallet().getCurrencies().get(Currency.SEK), 0.0);
    }

    @Test
    public void subtractingFundsWorks() throws InsufficientFundsException {
        User testUser = new User("Test", "test@test.com", "1");
        testUser.getWallet().getCurrencies().put(Currency.PLN, 100.0);
        testUser.getWallet().getCurrencies().put(Currency.AUD, 10.0);
        testUser.getWallet().withdrawFunds(new Funds(Currency.AUD, 9.0));
        Assert.assertEquals(1.0, testUser.getWallet().getCurrencies().get(Currency.AUD), 0.0);
    }

    @Test
    public void showingRatesWorks() {
        TransactionService service = new TransactionService();
        Assert.assertNotNull(service.getBuyAndSellRates(Currency.JPY));
    }

    @Test
    public void transactionListAndSearchingByNumberFromItWork() {
        TransactionService service = new TransactionService();
        User testUser = new User("Test", "test@test.com", "1");
        testUser.getWallet().getCurrencies().put(Currency.PLN, 100.0);
        testUser.getWallet().getCurrencies().put(Currency.AUD, 10.0);
        testUser.getWallet().getCurrencies().put(Currency.CAD, 35.1);

        Funds funds = new Funds(Currency.CAD, 15.0);
        Order order = new Order(funds, Currency.USD, BuyOrSell.sell);

        service.makeOrder(order, testUser);
        Assert.assertEquals(order.toString(), service.getTransactionByNumber(1));
    }

    @Test
    public void buyingWorksProperly() {
        TransactionService service = new TransactionService();
        User testUser = new User("Test", "test@test.com", "1");
        testUser.getWallet().getCurrencies().put(Currency.PLN, 100.0);
        testUser.getWallet().getCurrencies().put(Currency.AUD, 10.0);
        testUser.getWallet().getCurrencies().put(Currency.CAD, 35.1);

        Funds funds = new Funds(Currency.USD, 5.0);
        Order order = new Order(funds, Currency.CAD, BuyOrSell.buy);

        service.makeOrder(order, testUser);
        double actualValue = 35.1 - 5.0 * Currency.USD.getBuyRate() / Currency.CAD.getSellRate();
        Assert.assertEquals(testUser.getWallet().getCurrencies().get(Currency.CAD), actualValue, 0.0);
    }
}
