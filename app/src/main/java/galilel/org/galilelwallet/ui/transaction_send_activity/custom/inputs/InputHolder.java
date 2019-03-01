package galilel.org.galilelwallet.ui.transaction_send_activity.custom.inputs;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import galilel.org.galilelwallet.R;
import galilel.org.galilelwallet.ui.base.tools.adapter.BaseRecyclerViewHolder;

public class InputHolder extends BaseRecyclerViewHolder{

    ImageView radio_select;
    TextView txt_amount;
    TextView txt_address;
    TextView txt_confirmations_amount;
    TextView txt_date;

    public InputHolder(View itemView, int holderType) {
        super(itemView, holderType);
        txt_amount = (TextView) itemView.findViewById(R.id.txt_amount);
        radio_select = (ImageView) itemView.findViewById(R.id.radio_select);
        txt_address = (TextView) itemView.findViewById(R.id.txt_address);
        txt_confirmations_amount = (TextView) itemView.findViewById(R.id.txt_confirmations_amount);
        txt_date = (TextView) itemView.findViewById(R.id.txt_date);
    }
}
