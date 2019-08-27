package galilel.org.galilelwallet.rate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import java.net.HttpURLConnection;
import java.net.URL;

public class CoinMarketCapApiClient {
    private static final String URL = "https://api.coinmarketcap.com/v1/";
    public class GalilelMarket {
        public BigDecimal priceUsd;
        public BigDecimal priceBtc;
        public BigDecimal marketCapUsd;
        public BigDecimal totalSupply;
        public int rank;
        public GalilelMarket(BigDecimal priceUsd, BigDecimal priceBtc, BigDecimal marketCapUsd, BigDecimal totalSupply, int rank) {
            this.priceUsd = priceUsd;
            this.priceBtc = priceBtc;
            this.marketCapUsd = marketCapUsd;
            this.totalSupply = totalSupply;
            this.rank = rank;
        }
    }

    public GalilelMarket getGalilelPxrice() throws RequestGalilelRateException {
        try {
            GalilelMarket galilelMarket = null;
            String url = this.URL + "ticker/galilel/";
            StringBuffer stringBuffer = get(url);

            // receive response as stringBuffer
            String result = null;
            if (stringBuffer != null) {
                result = stringBuffer.toString();
                JSONArray jsonArray = new JSONArray(result);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                galilelMarket = new GalilelMarket(
                    new BigDecimal(jsonObject.getString("price_usd")),
                    new BigDecimal(jsonObject.getString("price_btc")),
                    new BigDecimal(jsonObject.getString("market_cap_usd")),
                    new BigDecimal(jsonObject.getString("total_supply")),
                    jsonObject.getInt("rank")
                );
            }
            return galilelMarket;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RequestGalilelRateException(e);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RequestGalilelRateException(e);
        }
    }

    public static StringBuffer get(String url) throws IOException {
        StringBuffer response_string = new StringBuffer();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        System.out.println("MAIK1 PostParseURL " + url);

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response_string.append(inputLine);
            }
            in.close();
        }
        return response_string;
    }

    public static class BitPayApi {
        private final String URL = "https://bitpay.com/rates";
        public interface RatesConvertor<T>{
            T convertRate(String code, String name, BigDecimal bitcoinRate);
        }

        /**
         * {"code":"BTC","name":"Bitcoin","rate":1}
         * @return
         * @throws RequestGalilelRateException
         */
        public <T> List<T> getRates(RatesConvertor<T> ratesConvertor) throws RequestGalilelRateException {
            try {
                StringBuffer stringBuffer = get(URL);

                // receive response as stringBuffer
                String result = null;
                List<T> ret = new ArrayList<>();
                if (stringBuffer != null) {
                    result = stringBuffer.toString();
                    JSONArray jsonArray = new JSONObject(result).getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String code = jsonObject.getString("code");
                        String name = jsonObject.getString("name");
                        BigDecimal rate = new BigDecimal(jsonObject.getString("rate"));
                        ret.add(ratesConvertor.convertRate(code,name,rate));
                    }
                }
                return ret;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RequestGalilelRateException(e);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new RequestGalilelRateException(e);
            }
        }
    }
}
