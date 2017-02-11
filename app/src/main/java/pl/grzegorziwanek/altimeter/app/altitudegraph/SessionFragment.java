package pl.grzegorziwanek.altimeter.app.altitudegraph;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.details.DetailsActivity;
import pl.grzegorziwanek.altimeter.app.model.Constants;
import pl.grzegorziwanek.altimeter.app.model.Session;
import pl.grzegorziwanek.altimeter.app.newgraph.AddNewGraphActivity;
import pl.grzegorziwanek.altimeter.app.utils.NoticeDialogFragment;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Grzegorz Iwanek on 18.01.2017.
 */
public class SessionFragment extends Fragment implements SessionContract.View,
        NoticeDialogFragment.NoticeDialogListener {

    @BindView(R.id.graphs_list) ListView mListView;
    @BindView(R.id.graphsLL) LinearLayout mSessionView;
    @BindView(R.id.no_graphs) LinearLayout mNoSessionsView;

    private SessionAdapter mListAdapter;
    private SessionContract.Presenter mPresenter;

    public SessionFragment() {}

    public static SessionFragment newInstance() {
        return new SessionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new SessionAdapter(new ArrayList<Session>(0), mSessionItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull SessionContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph_altitude, container, false);

        ButterKnife.bind(this, view);
        setSessionsListAdapter();
        setFloatingActionButton();
        setProgressIndicator(view);

        mPresenter.loadSessions(true);
        setHasOptionsMenu(true);
        return view;
    }

    private void setSessionsListAdapter() {
        mListView.setAdapter(mListAdapter);
    }

    private void setFloatingActionButton() {
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_session);
        fab.setImageResource(R.drawable.ic_vector_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewSession();
            }
        });
    }

    private void setProgressIndicator(View view) {
        final SwipeRefreshLayoutChild swipeRefreshLayoutChild =
                (SwipeRefreshLayoutChild) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayoutChild.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        swipeRefreshLayoutChild.setScrollUpChild(mListView);
        swipeRefreshLayoutChild.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadSessions(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                showUpDialog("Delete checked?");
                break;
            case R.id.menu_delete_all:
                showUpDialog("Delete all?");
                break;
            case R.id.menu_refresh:
                break;
        }
        return true;
    }

    @Override
    public void onDialogPositiveClick(String callbackCode) {
        switch (callbackCode) {
            case "Delete checked?":
                mPresenter.deleteCheckedSessions(getAdapterCheckedId());
                break;
            case "Delete all?":
                mPresenter.deleteAllSessions(getAdapterAllId());
                break;
        }
    }

    private void showUpDialog(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        DialogFragment ndf = new NoticeDialogFragment();
        ndf.setArguments(args);
        ndf.show(getChildFragmentManager(), "NoticeDialogFragment");
    }

    private ArrayList<String> getAdapterCheckedId() {
        return mListAdapter.getCheckedId();
    }

    private ArrayList<String> getAdapterAllId() {
        return mListAdapter.getAllId();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
    }

    /**
     * Listener for clicks on tasks in the ListView.
     */
    //TODO-> populate this instance with code
    SessionItemListener mSessionItemListener = new SessionItemListener() {
        @Override
        public void onSessionClick(Session clickedSession) {
            mPresenter.openSessionDetails(clickedSession.getId());
        }

        @Override
        public void onCheckBoxClick(String sessionId, boolean isCompleted) {
            mPresenter.setSessionCompleted(sessionId, isCompleted);
        }
    };

    @Override
    public void setLoadingIndicator(final boolean isActive) {
        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayoutChild srl =
                (SwipeRefreshLayoutChild) getView().findViewById(R.id.swipe_refresh_layout);

        // To make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(isActive);
            }
        });
    }

    @Override
    public void showSessions(List<Session> sessions) {
        mListAdapter.replaceData(sessions);
        mSessionView.setVisibility(View.VISIBLE);
        mNoSessionsView.setVisibility(View.GONE);
    }

    @Override
    public void showEmptySessions(List<Session> sessions) {
        mListAdapter.replaceData(sessions);
        mSessionView.setVisibility(View.GONE);
        mNoSessionsView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAddSessionUi() {
        Intent intent = new Intent(getContext(), AddNewGraphActivity.class);
        startActivity(intent);
    }

    @Override
    public void showSessionDetailsUi(String sessionId) {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        startActivity(intent);
    }

    @Override
    public void showLoadingSessionError() {

    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showCheckedSessionsDeleted() {
        showMessage("Checked sessions deleted");
    }

    @Override
    public void showAllSessionsDeleted() {
        showMessage("All sessions deleted");
    }

    @Override
    public void onSessionsDeleted() {
        mListAdapter.clearInfoChecked();
    }

    private static class SessionAdapter extends BaseAdapter {

        private List<Session> mSessions;
        private final SessionItemListener mItemListener;
        private ArrayList<String> mCheckedSessions;

        SessionAdapter(List<Session> sessions, SessionItemListener itemListener) {
            setList(sessions);
            mItemListener = itemListener;
            mCheckedSessions = new ArrayList<>();
        }

        void replaceData(List<Session> sessions) {
            setList(sessions);
            notifyDataSetChanged();
        }

        private void setList(List<Session> sessions) {
            mSessions = sessions;
        }

        ArrayList<String> getCheckedId() {
            ArrayList<String> list = new ArrayList<>();
            for (String sessionId : mCheckedSessions) {
                list.add(sessionId);
            }
            return list;
        }

        ArrayList<String> getAllId() {
            ArrayList<String> list = new ArrayList<>();
            for (Session session : mSessions) {
                list.add(session.getId());
            }
            return list;
        }

        private void clearInfoChecked() {
            mCheckedSessions.clear();;
        }

        @Override
        public int getCount() {
            return mSessions.size();
        }

        @Override
        public Session getItem(int i) {
            return mSessions.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.graph_list_item, viewGroup, false);
            }

            final Session session = getItem(i);

            TextView graphText = (TextView) rowView.findViewById(R.id.title);
            graphText.setText(session.getId());

            CheckBox removeItemCB = (CheckBox) rowView.findViewById(R.id.removeItem);
            removeItemCB.setChecked(isChecked(session.getId()));
            removeItemCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (session.isCompleted()) {
                        session.setCompleted(false);
                        mCheckedSessions.remove(session.getId());
                    } else {
                        session.setCompleted(true);
                        mCheckedSessions.add(session.getId());
                    }
                    mItemListener.onCheckBoxClick(session.getId(), session.isCompleted());
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onSessionClick(session);
                }
            });

            return rowView;
        }

        private boolean isChecked(String sessionId) {
            boolean isChecked = false;
            for (String id : mCheckedSessions) {
                if (id.equals(sessionId)) {
                    isChecked = true;
                }
            }
            return isChecked;
        }
    }

    //TODO-> refactor this code
    interface SessionItemListener {

        void onSessionClick(Session clickedSession);

        void onCheckBoxClick(String sessionId, boolean isCompleted);
    }
}

//    /**TODO-> remove that in final version, or move to neutral place
//     * Database inspection.
//     * Step 1, this code: copy existing app's database on Android device to accessible location inside of the device (Downloads).
//     * Step 2, user: copy it through android's app Total Commander to visible folder.
//     * Step 3, user: check with SQLite Browser.
//     * @throws IOException
//     */
//    private void copyAppDbToDownloadFolder() throws IOException {
//        File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "My_data.db");
//        File currentDB = getContext().getApplicationContext().getDatabasePath("Graphs.db");
//        if (currentDB.exists()) {
//            FileChannel src = new FileInputStream(currentDB).getChannel();
//            FileChannel dst = new FileOutputStream(backupDB).getChannel();
//            dst.transferFrom(src, 0, src.size());
//            src.close();
//            dst.close();
//        }
//
////          assign this to call above (for example to a "refresh" click in menu)
////        try {
////            copyAppDbToDownloadFolder();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//    }
