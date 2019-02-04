package pl.gregoryiwanek.altimeter.app.upgradepro;

import android.content.res.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.fragment.app.*;
import pl.gregoryiwanek.altimeter.app.R;

import butterknife.*;
import pl.gregoryiwanek.altimeter.app.*;

public class UpgradeProFragment extends Fragment implements UpgradeProContract.View {

    @BindView(R.id.upgrade_pro_list) ListView mListView;

    private UpgradeProContract.Presenter mPresenter;

    public static UpgradeProFragment newInstance() {
        return new UpgradeProFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upgrade_pro, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(false);
        populateListView();
        return view;
    }

    @Override
    public void setPresenter(UpgradeProContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.start();
    }

    private void populateListView() {
        UpgradeProAdapter upgradeProAdapter = new UpgradeProAdapter(
                getResources().getStringArray(R.array.upgrade_pro_list_values),
                getResources().obtainTypedArray(R.array.upgrade_pro_list_images));
        mListView.setAdapter(upgradeProAdapter);
    }

    private class UpgradeProAdapter extends BaseAdapter {

        private String[] mTextArray;
        private TypedArray mIconsArray;

        UpgradeProAdapter(String[] contentList, TypedArray imageList) {
            mTextArray = contentList;
            mIconsArray = imageList;
        }

        @Override
        public int getCount() {
            return mTextArray.length;
        }

        @Override
        public Object getItem(int position) {
            return mTextArray[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.upgrade_list_item, viewGroup, false);
            }

            TextView rowText = (TextView) rowView.findViewById(R.id.item_title);
            rowText.setText(mTextArray[position]);

            ImageView rowImage = (ImageView) rowView.findViewById(R.id.item_image);
            rowImage.setImageDrawable(mIconsArray.getDrawable(position));

            return rowView;
        }
    }
}