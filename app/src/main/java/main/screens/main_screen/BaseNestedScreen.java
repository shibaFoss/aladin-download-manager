package main.screens.main_screen;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * BaseNestedScreen gives the basic functions that are useful for its subclass.
 */
public abstract class BaseNestedScreen extends Fragment {

    protected View fragmentView;
    private Activity mainScreen;


    protected abstract int getScreenLayout();


    protected abstract void onAfterLayoutLoad(View layoutView, Bundle savedInstanceState);


    protected abstract void onViewCreating(View view);


    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup layoutContainer, Bundle savedInstanceState) {
        this.mainScreen = getActivity();
        View view = layoutInflater.inflate(getScreenLayout(), layoutContainer, false);
        this.fragmentView = view;
        onViewCreating(view);
        return view;
    }


    public MainScreen getMainScreen() {
        return (MainScreen) this.mainScreen;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        onAfterLayoutLoad(view, savedInstanceState);
    }

}
