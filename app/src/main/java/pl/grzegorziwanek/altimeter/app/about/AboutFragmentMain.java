package pl.grzegorziwanek.altimeter.app.about;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.grzegorziwanek.altimeter.app.R;

public final class AboutFragmentMain extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ButterKnife.bind(this, view);

        view.setBackgroundColor(ContextCompat.getColor(this.getActivity(), R.color.colorBlack));

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.contentFrameAbout, new AboutFragmentGraphs());
        ft.commit();

        return view;
    }

    @OnClick(R.id.about_button_graphs)
    public void onButtonGraphsClick() {
        setFragmentToReplace(R.id.about_button_graphs);
    }

    @OnClick(R.id.about_button_new_graph)
    public void onButtonNewGraphClick() {
        setFragmentToReplace(R.id.about_button_new_graph);
    }

    @OnClick(R.id.about_button_details)
    public void onButtonDetailsClick() {
        setFragmentToReplace(R.id.about_button_details);
    }

    @OnClick(R.id.about_button_stats)
    public void onButtonStatsClick() {
        setFragmentToReplace(R.id.about_button_stats);
    }

    @OnClick(R.id.about_button_map)
    public void onButtonMapClick() {
        setFragmentToReplace(R.id.about_button_map);
    }

    @OnClick(R.id.about_button_author)
    public void onButtonAuthorClick() {
        setFragmentToReplace(R.id.about_button_author);
    }

    private void setFragmentToReplace(int id) {
        switch (id) {
            case R.id.about_button_graphs:
                replaceFragment(new AboutFragmentGraphs());
                break;
            case R.id.about_button_new_graph:
                replaceFragment(new AboutFragmentNew());
                break;
            case R.id.about_button_details:
                replaceFragment(new AboutFragmentDetail());
                break;
            case R.id.about_button_stats:
                replaceFragment(new AboutFragmentStats());
                break;
            case R.id.about_button_map:
                replaceFragment(new AboutFragmentMap());
                break;
            case R.id.about_button_author:
                replaceFragment(new AboutFragmentAuthor());
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.contentFrameAbout, fragment);
        ft.commit();
    }
}
