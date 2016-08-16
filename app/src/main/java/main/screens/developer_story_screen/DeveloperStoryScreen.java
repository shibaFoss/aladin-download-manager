package main.screens.developer_story_screen;

import android.content.res.Configuration;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import main.screens.BaseScreen;
import net.fdm.R;

public class DeveloperStoryScreen extends BaseScreen {

    @Override
    public int getLayout() {
        return R.layout.developer_screen;
    }

    @Override
    public void onLayoutLoad() {
        this.init();
    }

    @Override
    public void onAfterLayoutLoad() {

    }

    @Override
    public void onPauseScreen() {

    }

    @Override
    public void onResumeScreen() {

    }

    @Override
    public void onExitScreen() {
        exit();
    }

    @Override
    public void onClearMemory() {

    }

    @Override
    public void onScreenOptionChange(Configuration configuration) {

    }

    public TextView title;
    private LinearLayout rootLayout;

    private void init() {
        title = (TextView) findViewById(R.id.tool_bar_title);

        rootLayout = (LinearLayout) findViewById(R.id.developer_list_row);
        addNewDeveloperCard(R.drawable.ic_shiba_image, getShibaInto());
        addNewDeveloperCard(R.drawable.ic_gourav, getGouravInfo());
        addNewDeveloperCard(R.drawable.ic_aninda, getAnindaInto());
        addNewDeveloperCard(R.drawable.ic_suman, getSumanInto());
        addNewDeveloperCard(R.drawable.ic_manoj, getManojInto());
        addSpecialThanks();
    }

    private void addSpecialThanks() {
        TextView specialThanks = new TextView(this);
        specialThanks.setText("" +
                "--------------------------------------\n" +
                "We have worked hard a lot in this project, but there are some people we have to mention here for their " +
                "special supports.\n\n" +
                "Souvik Dutta\n" +
                "Srijan Mishra\n" +
                "Swapan Paria\n" +
                "Sayan Raj\n" +
                "Tamal\n" +
                "Biswabandhu Bera");

        specialThanks.setTextSize(18.0f);
        rootLayout.addView(specialThanks);
    }

    private String getShibaInto() {
        return "Hi I\'m Shiba, and I write code for Computer. " +
                "<a href=\"https://www.facebook.com/shiboprasad1\">Facebook</a>";
    }

    public void onBackButtonClick(View view) {
        exit();
    }

    public String getAnindaInto() {
        return "Hi I'm Aninda, I design Modern Mobile Apps. <a href=\"http://facebook.com/aninda.shaw.3\">Facebook</a>";
    }

    public void addNewDeveloperCard(int imageId, String intoString) {
        View view = LayoutInflater.from(this).inflate(R.layout.developer_info_card, null);
        rootLayout.addView(view);

        ImageView image = (ImageView) view.findViewById(R.id.developer_image);
        TextView intro = (TextView) view.findViewById(R.id.developer_intro);

        image.setImageResource(imageId);
        intro.setText(intoString);
        intro.setText(Html.fromHtml(intoString));
        intro.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public String getGouravInfo() {
        return "Hi, My name is Gourav, and I'm a senior Mobile App Developer.";
    }

    public String getSumanInto() {
        return "I'm Suman and I'm a senior Front-end Developer. :)  <a href=\"http://facebook.com/suman.mujmdar\">Facebook</a>";
    }

    public String getManojInto() {
        return "Hi My name is Manoj and I'm a senior software tester.  :) <a href=\"http://facebook.com/manoj.jana.961\">Facebook</a>";
    }

}
