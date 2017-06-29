package pl.gregoryiwanek.altimeter.app.mainview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import pl.gregoryiwanek.altimeter.app.BasicFragment;
import pl.gregoryiwanek.altimeter.app.R;
import pl.gregoryiwanek.altimeter.app.data.Session;
import pl.gregoryiwanek.altimeter.app.details.DetailsActivity;
import pl.gregoryiwanek.altimeter.app.recordingsession.RecordingSessionActivity;
import pl.gregoryiwanek.altimeter.app.utils.Constants;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * View of this section.
 * Consists number of inner views (textViews etc.) and layouts, also customized inner Adapter class.
 */
public class SessionFragment extends BasicFragment implements SessionContract.View{

    @BindView(R.id.graphs_list) ListView mListView;
    @BindView(R.id.graphsLL) LinearLayout mSessionView;
    @BindView(R.id.no_graphs) LinearLayout mNoSessionsView;

    private SessionAdapter mListAdapter;
    private SessionContract.Presenter mPresenter;

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
        fab.setOnClickListener(view -> mPresenter.addNewSession());
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
        swipeRefreshLayoutChild.setOnRefreshListener(() -> mPresenter.loadSessions(false));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                popUpNoticeDialog(Constants.MESSAGE_DELETE_CHECKED);
                break;
            case R.id.menu_delete_all:
                popUpNoticeDialog(Constants.MESSAGE_DELETE_ALL);
                break;
        }
        return true;
    }

    @Override
    public void onDialogPositiveClick(String callbackCode) {
        switch (callbackCode) {
            case Constants.MESSAGE_DELETE_CHECKED:
                mPresenter.deleteCheckedSessions(getAdapterCheckedId());
                break;
            case Constants.MESSAGE_DELETE_ALL:
                mPresenter.deleteAllSessions(getAdapterAllId());
                break;
            case Constants.MESSAGE_UPGRADE_TO_PRO_MAX_SAVED:
                super.openUpgradePro();
                break;
        }
    }

    @Override
    protected void popUpNoticeDialog(String title) {
        super.popUpNoticeDialog(title);
    }

    private ArrayList<String> getAdapterCheckedId() {
        return mListAdapter.getCheckedId();
    }

    private ArrayList<String> getAdapterAllId() {
        return mListAdapter.getAllId();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu_main, menu);
    }

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
        updateNumSavedSessions(sessions.size());
        mSessionView.setVisibility(View.VISIBLE);
        mNoSessionsView.setVisibility(View.GONE);
    }

    private void updateNumSavedSessions(int number) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("numSavedSessions", number);
        editor.apply();
    }

    @Override
    public void showEmptySessions(List<Session> sessions) {
        mListAdapter.replaceData(sessions);
        updateNumSavedSessions(0);
        mSessionView.setVisibility(View.GONE);
        mNoSessionsView.setVisibility(View.VISIBLE);
    }

    // TODO: 24.06.2017 remove +10 from the condition, just for now
    @Override
    public void showAddSessionUi() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (Constants.MAX_NUMBER_SESSIONS + 10 >= preferences.getInt("numSavedSessions", Constants.MAX_NUMBER_SESSIONS)) {
            Intent intent = new Intent(getContext(), RecordingSessionActivity.class);
            startActivity(intent);
        } else {
            popUpNoticeDialog(Constants.MESSAGE_UPGRADE_TO_PRO_MAX_SAVED);
        }
    }

    @Override
    public void showSessionDetailsUi(String clickedSessionId) {
        Intent intent = new Intent(getContext(), DetailsActivity.class);
        intent.putExtra("sessionId", clickedSessionId);
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

    /**
     * Private, inner adapter class. Used to populate and perform
     * operations on a List with {@link Session} objects.
     */
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
            graphText.setText(session.getTitle());

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

    /**
     * Interface defining events on the list rows.
     */
    interface SessionItemListener {

        void onSessionClick(Session clickedSession);

        void onCheckBoxClick(String sessionId, boolean isCompleted);
    }
}
