package pl.gregoryiwanek.altimeter.app.map;

import android.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.core.app.*;
import androidx.fragment.app.*;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.snackbar.*;
import com.google.common.collect.*;

import java.util.*;

import butterknife.*;
import pl.gregoryiwanek.altimeter.app.R;

import static com.google.common.base.Preconditions.*;

/**
 * Consists view class of Map section.
 * Uses GoogleMap as a map representation.
 * Generates polyline path based on the session's recorded points.
 * Object of this class should be created from within {@link MapActivity}, and always with given
 * id of the session map it's creating.
 */
public class MapFragment extends Fragment implements MapContract.View {

    private static final String TAG = MapFragment.class.getName();
    @BindView(R.id.map) MapView mMapView;

    private MapContract.Presenter mPresenter;
    private GoogleMap mGoogleMap;

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void setPresenter(MapContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu_share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_facebook:
                shareClicked();
                break;
        }
        return true;
    }

    private void shareClicked() {
        ContentResolver cr = this.getActivity().getContentResolver();
        Window window = getActivity().getWindow();
        mPresenter.shareScreenShot(window, cr, mGoogleMap);
    }

    @Override
    public void updateMap(final List<LatLng> positions) {
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (positions != null && positions.size() > 0) {
            generateAMap(positions);
        }
    }

    private void generateAMap(final List<LatLng> positions) {
        mMapView.getMapAsync(mMap -> {
            mGoogleMap = mMap;
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);


            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Turn on location service.", Toast.LENGTH_SHORT).show();
            } else {
                mGoogleMap.setMyLocationEnabled(true);
                LatLng start = positions.get(0);
                LatLng end = Iterables.getLast(positions);
                addMarkerToMap(start, "Start");
                addMarkerToMap(end, "End");
                addPolylinePathToMap(positions);
                setCameraPosition(end);
            }
        });
    }

    private void addMarkerToMap(LatLng position, String title) {
        mGoogleMap.addMarker(new MarkerOptions().position(position).title(title));
    }

    private void addPolylinePathToMap(List<LatLng> positions) {
        int colorPoly = Color.argb(150, 50, 50, 255);
        PolylineOptions polyline = new PolylineOptions().addAll(positions);
        polyline.color(colorPoly);
        mGoogleMap.addPolyline(polyline);
    }

    private void setCameraPosition(LatLng focusPoint) {
        CameraPosition position = new CameraPosition.Builder().target(focusPoint).zoom(13).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
    }

    @Override
    public void showShareMenu(Intent screenshotIntent) {
        startActivity(Intent.createChooser(screenshotIntent, "Send to"));
    }

    @Override
    public void showMapEmpty() {
        showMessage("Session has no points to show on a map...");
    }

    @Override
    public void showMapLoaded() {
        showMessage("Map loaded successfully");
    }

    @SuppressWarnings("ConstantConditions")
    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    class ExpandableListAdapterr extends BaseExpandableListAdapter {

        Context context;


        @Override
        public int getGroupCount() {
            return 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            return null;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}

//public class ExpandableListAdapter extends BaseExpandableListAdapter {
//
//    private Context _context;
//    private List<String> _listDataHeader; // header titles
//    // child data in format of header title, child title
//    private HashMap<String, List<String>> _listDataChild;
//
//    public ExpandableListAdapter(Context context, List<String> listDataHeader,
//                                 HashMap<String, List<String>> listChildData) {
//        this._context = context;
//        this._listDataHeader = listDataHeader;
//        this._listDataChild = listChildData;
//    }
//
//    @Override
//    public Object getChild(int groupPosition, int childPosititon) {
//        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
//                .get(childPosititon);
//    }
//
//    @Override
//    public long getChildId(int groupPosition, int childPosition) {
//        return childPosition;
//    }
//
//    @Override
//    public View getChildView(int groupPosition, final int childPosition,
//                             boolean isLastChild, View convertView, ViewGroup parent) {
//
//        final String childText = (String) getChild(groupPosition, childPosition);
//
//        if (convertView == null) {
//            LayoutInflater infalInflater = (LayoutInflater) this._context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = infalInflater.inflate(R.layout.list_item, null);
//        }
//
//        TextView txtListChild = (TextView) convertView
//                .findViewById(R.id.lblListItem);
//
//        txtListChild.setText(childText);
//        return convertView;
//    }
//
//    @Override
//    public int getChildrenCount(int groupPosition) {
//        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
//                .size();
//    }
//
//    @Override
//    public Object getGroup(int groupPosition) {
//        return this._listDataHeader.get(groupPosition);
//    }
//
//    @Override
//    public int getGroupCount() {
//        return this._listDataHeader.size();
//    }
//
//    @Override
//    public long getGroupId(int groupPosition) {
//        return groupPosition;
//    }
//
//    @Override
//    public View getGroupView(int groupPosition, boolean isExpanded,
//                             View convertView, ViewGroup parent) {
//        String headerTitle = (String) getGroup(groupPosition);
//        if (convertView == null) {
//            LayoutInflater infalInflater = (LayoutInflater) this._context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            convertView = infalInflater.inflate(R.layout.list_group, null);
//        }
//
//        TextView lblListHeader = (TextView) convertView
//                .findViewById(R.id.lblListHeader);
//        lblListHeader.setTypeface(null, Typeface.BOLD);
//        lblListHeader.setText(headerTitle);
//
//        return convertView;
//    }
//
//    @Override
//    public boolean hasStableIds() {
//        return false;
//    }
//
//    @Override
//    public boolean isChildSelectable(int groupPosition, int childPosition) {
//        return true;
//    }
//}