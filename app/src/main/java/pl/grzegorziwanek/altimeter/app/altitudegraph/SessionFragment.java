package pl.grzegorziwanek.altimeter.app.altitudegraph;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.model.Session;
import pl.grzegorziwanek.altimeter.app.newgraph.AddNewGraphActivity;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by Grzegorz Iwanek on 18.01.2017.
 */
public class SessionFragment extends Fragment implements SessionContract.View {

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

    //TODO-> define that one
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

        //Set up adapter
        mListView.setAdapter(mListAdapter);

        //Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_session);
        fab.setImageResource(R.drawable.ic_vector_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewSession();
            }
        });

        //Set up progress indicator
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
                //TODO-> set methods here
            }
        });

        mPresenter.loadSessions(true);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                mPresenter.deleteCheckedSessions(getCheckedId());
                break;
            case R.id.menu_delete_all:
                mPresenter.deleteAllSessions();
                break;
            case R.id.menu_refresh:
                try {
                    copyAppDbToDownloadFolder();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

    public void copyAppDbToDownloadFolder() throws IOException {
        File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "My_data.db"); // for example "my_data_backup.db"
        File currentDB = getContext().getApplicationContext().getDatabasePath("Graphs.db"); //databaseName=your current application database name, for example "my_data.db"
        if (currentDB.exists()) {
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        }
    }

    private ArrayList<String> getCheckedId() {
        return mListAdapter.getCheckedId();
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
        public void onGraphClick(Session clickedSession) {

        }

        @Override
        public void onCompleteGraphClick(Session completedSession) {

        }

        @Override
        public void onActivateGraphClick(Session activatedSession) {

        }
    };

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showSessions(List<Session> sessions) {
        mListAdapter.replaceData(sessions);
        mSessionView.setVisibility(View.VISIBLE);
        mNoSessionsView.setVisibility(View.GONE);
    }

    @Override
    public void showAddSession() {
        Intent intent = new Intent(getContext(), AddNewGraphActivity.class);
        startActivity(intent);
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showGraphDetailsUi(String graphId) {

    }

    @Override
    public void showGraphMarkedComplete() {

    }

    @Override
    public void showGraphMarkedActive() {

    }

    @Override
    public void showCompletedGraphsCleared() {

    }

    @Override
    public void showLoadingSessionError() {

    }

    @Override
    public void showNoSessions() {

    }

    @Override
    public void showActiveFilterLabel() {

    }

    @Override
    public void showCompletedFilterLabel() {

    }

    @Override
    public void showAllFilterLabel() {

    }

    @Override
    public void showNoActiveSessions() {

    }

    @Override
    public void showNoCompletedSessions() {

    }

    @Override
    public void showSuccessfullySavedMessage() {

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
    public void showFilteringPopUpMenu() {

    }

    private static class SessionAdapter extends BaseAdapter {

        private List<Session> mSessions;
        private SessionItemListener mItemListener;

        SessionAdapter(List<Session> sessions, SessionItemListener itemListener) {
            setList(sessions);
            mItemListener = itemListener;
        }

        void replaceData(List<Session> sessions) {
            setList(sessions);
            notifyDataSetChanged();
        }

        private void setList(List<Session> sessions) {
            mSessions = checkNotNull(sessions);
        }

        ArrayList<String> getCheckedId() {
            ArrayList<String> list = new ArrayList<>();
            for (Session session : mSessions) {
                if (session.isCompleted()) {
                    list.add(session.getId());
                }
            }
            return list;
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

            //TODO-> remove checkbox?
            CheckBox removeItemCB = (CheckBox) rowView.findViewById(R.id.removeItem);

            //TODO->set code inside
            removeItemCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (session.isCompleted()) {
                        session.setCompleted(false);
                    } else {
                        session.setCompleted(true);
                    }
                }
            });

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemListener.onGraphClick(session);
                }
            });
            return rowView;
        }
    }

    //TODO-> refactor this code
    public interface SessionItemListener {

        void onGraphClick(Session clickedSession);

        void onCompleteGraphClick(Session completedSession);

        void onActivateGraphClick(Session activatedSession);
    }
}
