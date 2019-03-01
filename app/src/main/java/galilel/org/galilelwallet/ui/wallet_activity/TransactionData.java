package galilel.org.galilelwallet.ui.wallet_activity;

public class TransactionData {
    public String title;
    public String description;
    public String amount;
    public String amountLocal;
    public int imageId;

    public TransactionData(String title, String description, int imageId, String amount, String amountLocal) {
        this.title = title;
        this.description = description;
        this.imageId = imageId;
        this.amount = amount;
        this.amountLocal = amountLocal;
    }
}
