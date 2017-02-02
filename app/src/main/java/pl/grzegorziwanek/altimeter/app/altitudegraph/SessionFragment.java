package pl.grzegorziwanek.altimeter.app.altitudegraph;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
//        // Set up floating action button
//        FloatingActionButton fab =
//                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_task);
//
//        fab.setImageResource(R.drawable.ic_add);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mPresenter.addNewTask();
//            }
//        });

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
//        // Set up progress indicator
//        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
//                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
//        swipeRefreshLayout.setColorSchemeColors(
//                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
//                ContextCompat.getColor(getActivity(), R.color.colorAccent),
//                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
//        );
//        // Set the scrolling view in the custom SwipeRefreshLayout.
//        swipeRefreshLayout.setScrollUpChild(mListView);
//
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mPresenter.loadTasks(false);
//            }
//        });
//
        mPresenter.loadSessions(true);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Listener for clicks on tasks in the ListView.
     */
    //TODO-> populate this instance with code
    SessionItemListener mSessionItemListener = new SessionItemListener() {
        @Override
        public void onGraphClick(Session clikedSession) {

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
        System.out.println("CALLING SHOW SESSIONS");
        for (Session session : sessions) {
            System.out.println(session);
            System.out.println("CALLED IN LOOP");
        }
        mListAdapter.replaceData(sessions);
        System.out.println("SET VISIBLE");
        mSessionView.setVisibility(View.VISIBLE);
        mNoSessionsView.setVisibility(View.GONE);
        System.out.println("SET VISIBLE");
    }

    @Override
    public void showAddSession() {
        Intent intent = new Intent(getContext(), AddNewGraphActivity.class);
        startActivity(intent);
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
    public void showFilteringPopUpMenu() {

    }

    private static class SessionAdapter extends BaseAdapter {

        private List<Session> mSessions;
        private SessionItemListener mItemListener;

        public SessionAdapter(List<Session> sessions, SessionItemListener itemListener) {
            setList(sessions);
            mItemListener = itemListener;
        }

        public void replaceData(List<Session> sessions) {
            System.out.println("REPLACE DATA HAS BEEN CALLED");
            setList(sessions);
            System.out.println("REPLACE DATA HAS BEEN CALLED LIST SET, Size of sessions is: " + sessions.size());
            notifyDataSetChanged();
            System.out.println("REPLACE DATA HAS BEEN CALLED NOTIFIED");
            for (Session session : sessions) {
                System.out.println("SESSIONS LIST ELEMENT: " + session.getTitle() + " " + session.getDescription());
            }
        }

        private void setList(List<Session> sessions) {
            mSessions = checkNotNull(sessions);
        }

        @Override
        public int getCount() {
            return mSessions.size();
        }

        //TODO->REMEMBER: RETURN OBJECT, NOT GRAPHVIEW
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
            System.out.println("CALLED GET VIEW FROM FRAGMENT'S ADAPTER");
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


//            // Active/completed task UI
//            completeCB.setChecked(task.isCompleted());
//            if (task.isCompleted()) {
//                rowView.setBackgroundDrawable(viewGroup.getContext()
//                        .getResources().getDrawable(R.drawable.list_completed_touch_feedback));
//            } else {
//                rowView.setBackgroundDrawable(viewGroup.getContext()
//                        .getResources().getDrawable(R.drawable.touch_feedback));
//            }

            //TODO->set code iside
            removeItemCB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //            completeCB.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (!task.isCompleted()) {
//                        mItemListener.onCompleteTaskClick(task);
//                    } else {
//                        mItemListener.onActivateTaskClick(task);
//                    }
//                }
//            });
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

        void onGraphClick(Session clikedSession);

        void onCompleteGraphClick(Session completedSession);

        void onActivateGraphClick(Session activatedSession);
    }
}
