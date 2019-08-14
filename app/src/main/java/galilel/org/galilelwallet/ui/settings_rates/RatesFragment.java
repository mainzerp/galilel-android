package galilel.org.galilelwallet.ui.settings_rates;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.content.ContextCompat;

import java.util.List;

import galilel.org.galilelwallet.R;
import global.GalilelRate;
import galilel.org.galilelwallet.ui.base.BaseRecyclerFragment;
import galilel.org.galilelwallet.ui.base.tools.adapter.BaseRecyclerAdapter;
import galilel.org.galilelwallet.ui.base.tools.adapter.BaseRecyclerViewHolder;
import galilel.org.galilelwallet.ui.base.tools.adapter.ListItemListeners;

public class RatesFragment extends BaseRecyclerFragment<GalilelRate> implements ListItemListeners<GalilelRate> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setEmptyText(getString(R.string.empty_rate));
        setEmptyTextColor(ContextCompat.getColor(getContext(), R.color.grey85black));
        return view;
    }

    @Override
    protected List<GalilelRate> onLoading() {
        return galilelModule.listRates();
    }

    @Override
    protected BaseRecyclerAdapter<GalilelRate, ? extends GalilelRateHolder> initAdapter() {
        BaseRecyclerAdapter<GalilelRate, GalilelRateHolder> adapter = new BaseRecyclerAdapter<GalilelRate, GalilelRateHolder>(getActivity()) {
            @Override
            protected GalilelRateHolder createHolder(View itemView, int type) {
                return new GalilelRateHolder(itemView,type);
            }

            @Override
            protected int getCardViewResource(int type) {
                return R.layout.rate_row;
            }

            @Override
            protected void bindHolder(GalilelRateHolder holder, GalilelRate data, int position) {
                holder.txt_name.setText(data.getCode());
                if (list.get(0).getCode().equals(data.getCode()))
                    holder.view_line.setVisibility(View.GONE);
            }
        };
        adapter.setListEventListener(this);
        return adapter;
    }

    @Override
    public void onItemClickListener(GalilelRate data, int position) {
        galilelApplication.getAppConf().setSelectedRateCoin(data.getCode());
        Toast.makeText(getActivity(),getString(R.string.rate_selected),Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
    }

    @Override
    public void onLongItemClickListener(GalilelRate data, int position) {

    }

    private  class GalilelRateHolder extends BaseRecyclerViewHolder{

        private TextView txt_name;
        private View view_line;

        protected GalilelRateHolder(View itemView, int holderType) {
            super(itemView, holderType);
            txt_name = (TextView) itemView.findViewById(R.id.txt_name);
            view_line = itemView.findViewById(R.id.view_line);
        }
    }
}
