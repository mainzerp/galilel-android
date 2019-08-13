package galilel.org.galilelwallet.ui.transaction_send_activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.galilelj.core.Coin;

import java.math.BigDecimal;

import galilel.org.galilelwallet.R;
import global.GalilelRate;
import galilel.org.galilelwallet.ui.base.BaseFragment;

public class AmountInputFragment extends BaseFragment implements View.OnClickListener {

    private View root;

    private EditText edit_amount, editCurrency;
    private TextView txt_currency_amount, txtShowGali,txt_local_currency;
    private ImageButton btnSwap;
    private ViewFlipper amountSwap;
    private GalilelRate galilelRate;
    private boolean inGalis = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.amount_input,container,false);
        edit_amount = (EditText) root.findViewById(R.id.edit_amount);
        //Sending amount currency
        editCurrency = (EditText) root.findViewById(R.id.edit_amount_currency);
        txt_currency_amount = (TextView) root.findViewById(R.id.txt_currency_amount);
        txt_local_currency = (TextView) root.findViewById(R.id.txt_local_currency);
        txtShowGali = (TextView) root.findViewById(R.id.txt_show_gali) ;
        //Swap type of ammounts
        amountSwap = (ViewFlipper) root.findViewById( R.id.viewFlipper );
        amountSwap.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.slide_in_left));
        amountSwap.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.slide_out_right));
        btnSwap = (ImageButton) root.findViewById(R.id.btn_swap);
        btnSwap.setOnClickListener(this);

        galilelRate = galilelModule.getRate(galilelApplication.getAppConf().getSelectedRateCoin());

        if (galilelRate != null)
            txt_local_currency.setText("0 " + galilelRate.getCode());
        else
            txt_local_currency.setText("0");

        edit_amount.setHint("0 " + getString(R.string.set_amount_gali));
        editCurrency.setHint("0 " + galilelRate.getCode());

        editCurrency.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (galilelRate != null) {
                    if (s.length() > 0) {
                        String valueStr = s.toString();
                        if (valueStr.charAt(0) == '.') {
                            valueStr = "0" + valueStr;
                        }
                        BigDecimal result = new BigDecimal(valueStr).divide(galilelRate.getRate(), 6, BigDecimal.ROUND_DOWN);
                        txtShowGali.setText(result.toPlainString() + " GALI");
                    } else {
                        txtShowGali.setText("0 " + galilelRate.getCode());
                    }
                }else {
                    txtShowGali.setText(R.string.no_rate);
                }
            }
        });

        edit_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>0) {
                    if (galilelRate != null) {
                        String valueStr = s.toString();
                        if (valueStr.charAt(0) == '.') {
                            valueStr = "0" + valueStr;
                        }
                        Coin coin = Coin.parseCoin(valueStr);
                        txt_local_currency.setText(
                                galilelApplication.getCentralFormats().format(
                                        new BigDecimal(coin.getValue() * galilelRate.getRate().doubleValue()).movePointLeft(8)
                                )
                                        + " " + galilelRate.getCode()
                        );
                    }else {
                        // rate null -> no connection.
                        txt_local_currency.setText(R.string.no_rate);
                    }
                }else {
                    if (galilelRate!=null)
                        txt_local_currency.setText("0 "+galilelRate.getCode());
                    else
                        txt_local_currency.setText(R.string.no_rate);
                }
            }
        });

        return root;
    }

    public String getAmountStr() throws Exception {
        if (edit_amount == null && editCurrency == null){
            throw new Exception("Fragment is not attached");
        }
        String amountStr = "0";
        if (inGalis) {
            amountStr = edit_amount.getText().toString();
        }else {
            // the value is already converted
            String valueStr = txtShowGali.getText().toString();
            amountStr = valueStr.replace(" GALI","");
            if(valueStr.length() > 0) {
                if (valueStr.charAt(0) == '.') {
                    amountStr = "0" + valueStr;
                }
            }
        }
        return amountStr;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btn_swap) {
            inGalis = !inGalis;
            amountSwap.showNext();
        }
    }
}
