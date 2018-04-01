package com.example.android.courtcounter;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.android.courtcounter.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
  /////////////////////////////////////////////////////////////////////////////
  //===========================================================================
  // const
  //===========================================================================
  /////////////////////////////////////////////////////////////////////////////
  final int RUNNER_NONE =      0x0;
  final int RUNNER_ON_FIRST =  0x1;
  final int RUNNER_ON_SECOND = 0x2;
  final int RUNNER_ON_THIRD =  0x4;
  final int RUNNER_ALL =       RUNNER_ON_FIRST | RUNNER_ON_SECOND | RUNNER_ON_THIRD;

  final TeamInitData[] TEAMS_DATA = {
    new TeamInitData(R.string.team_name_blue_circles, R.drawable.blue_circles),
    new TeamInitData(R.string.team_name_green_triangles, R.drawable.green_triangles),
    new TeamInitData(R.string.team_name_red_squares, R.drawable.red_squares),
    new TeamInitData(R.string.team_name_yellow_stars, R.drawable.yellow_stars)
  };

  /////////////////////////////////////////////////////////////////////////////
  //===========================================================================
  // member variables
  //===========================================================================
  /////////////////////////////////////////////////////////////////////////////
  ActivityMainBinding m_binding;
  TeamData m_team1;
  TeamData m_team2;

  int m_strikes;
  int m_balls;
  int m_outs;
  int m_runnersOn;
  private boolean m_isTeam1AtBat;

  /////////////////////////////////////////////////////////////////////////////
  //===========================================================================
  // public
  //===========================================================================
  /////////////////////////////////////////////////////////////////////////////

  //===========================================================================
  // onBtnBall
  //===========================================================================
  public void onBtnBall(View view) {
    if (m_balls < 3) {
      setBalls(1, true);
    } else {
      atBatWalk();
      setBalls(0, false);
    }
  }

  //===========================================================================
  // onBtnDouble
  //===========================================================================
  public void onBtnDouble(View view) {
    if (isRunnerOnThird()) {
      advanceRunnerHome(RUNNER_ON_THIRD);
    }

    if (isRunnerOnSecond()) {
      advanceRunnerHome(RUNNER_ON_SECOND);
    }

    if (isRunnerOnFirst()) {
      advanceRunnerFromTo(RUNNER_ON_FIRST, RUNNER_ON_THIRD);
    }

    advanceAtBatTo(RUNNER_ON_SECOND);
    addOneHit();
    setAtBat();
    updateButtonStates();
  }

  //===========================================================================
  // onBtnFoul
  //===========================================================================
  public void onBtnFoul(View view) {
    if (m_strikes < 2) {
      setStrikes(1, true);
    }
  }

  //===========================================================================
  // onBtnHomeRun
  //===========================================================================
  public void onBtnHomeRun(View view) {
    if (isRunnerOnThird()) {
      advanceRunnerHome(RUNNER_ON_THIRD);
    }

    if (isRunnerOnSecond()) {
      advanceRunnerHome(RUNNER_ON_SECOND);
    }

    if (isRunnerOnFirst()) {
      advanceRunnerHome(RUNNER_ON_FIRST);
    }

    //this is for the player at bat
    advanceRunnerHome(RUNNER_NONE);
    addOneHit();
    setAtBat();
    updateButtonStates();
  }

  //===========================================================================
  // onBtnOut
  //===========================================================================
  public void onBtnOut(View view) {
    atBatPutOut();
    updateButtonStates();
  }

  //===========================================================================
  // onBtnOutAtFirst
  //===========================================================================
  public void onBtnOutAtFirst(View view) {
    runnerPutOut(RUNNER_ON_FIRST);
  }

  //===========================================================================
  // onBtnOutAtSecond
  //===========================================================================
  public void onBtnOutAtSecond(View view) {
    runnerPutOut(RUNNER_ON_SECOND);
  }

  //===========================================================================
  // onBtnOutAtThird
  //===========================================================================
  public void onBtnOutAtThird(View view) {
    runnerPutOut(RUNNER_ON_THIRD);
  }

  //===========================================================================
  // onBtnReset
  //===========================================================================
  public void onBtnReset(View view) {
    reset();
  }

  //===========================================================================
  // onBtnSingle
  //===========================================================================
  public void onBtnSingle(View view) {
    if (isRunnerOnFirst()) {
      if (isRunnerOnSecond()) {
        if (isRunnerOnThird()) {
          advanceRunnerHome(RUNNER_ON_THIRD);
        }
        advanceRunnerFromTo(RUNNER_ON_SECOND, RUNNER_ON_THIRD);
      }
      advanceRunnerFromTo(RUNNER_ON_FIRST, RUNNER_ON_SECOND);
    }

    advanceAtBatTo(RUNNER_ON_FIRST);
    addOneHit();
    setAtBat();
    updateButtonStates();
  }

  //===========================================================================
  // onBtnStealHome
  //===========================================================================
  public void onBtnStealHome(View view) {
    if (!isRunnerOnThird()) {
      return; //sanity check and simplicity - only a runner on third may steal home
    }

    advanceRunnerHome(RUNNER_ON_THIRD);
    updateButtonStates();
  }

  //===========================================================================
  // onBtnStealSecond
  //===========================================================================
  public void onBtnStealSecond(View view) {
    if (!isRunnerOnFirst()) {
      return; //sanity check and simplicity - only a runner on first may steal second
    }

    if (isRunnerOnSecond()) {
      //runner on second forced to advance to third

      if (isRunnerOnThird()) {
        //runner on third forced to score
        advanceRunnerHome(RUNNER_ON_THIRD);
      }
      advanceRunnerFromTo(RUNNER_ON_SECOND, RUNNER_ON_THIRD);
    }

    advanceRunnerFromTo(RUNNER_ON_FIRST, RUNNER_ON_SECOND);
    updateButtonStates();
  }

  //===========================================================================
  // onBtnStealThird
  //===========================================================================
  public void onBtnStealThird(View view) {
    if (!isRunnerOnSecond()) {
      return; //sanity check and simplicity - only a runner on second may steal third
    }

    if (isRunnerOnThird()) {
      //runner on third forced to score
      advanceRunnerHome(RUNNER_ON_THIRD);
    }

    advanceRunnerFromTo(RUNNER_ON_SECOND, RUNNER_ON_THIRD);
    updateButtonStates();
  }

  //===========================================================================
  // onBtnStrike
  //===========================================================================
  public void onBtnStrike(View view) {
    if (m_strikes < 2) {
      setStrikes(1, true);
    } else {
      atBatPutOut();
    }
  }

  //===========================================================================
  // onBtnTriple
  //===========================================================================
  public void onBtnTriple(View view) {
    if (isRunnerOnThird()) {
      advanceRunnerHome(RUNNER_ON_THIRD);
    }

    if (isRunnerOnSecond()) {
      advanceRunnerHome(RUNNER_ON_SECOND);
    }

    if (isRunnerOnFirst()) {
      advanceRunnerHome(RUNNER_ON_FIRST);
    }

    advanceAtBatTo(RUNNER_ON_THIRD);
    addOneHit();
    setAtBat();
    updateButtonStates();
  }

  /////////////////////////////////////////////////////////////////////////////
  //===========================================================================
  // protected
  //===========================================================================
  /////////////////////////////////////////////////////////////////////////////

  //===========================================================================
  // onCreate
  //===========================================================================
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    m_binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    reset();
  }

  /////////////////////////////////////////////////////////////////////////////
  //===========================================================================
  // private
  //===========================================================================
  /////////////////////////////////////////////////////////////////////////////

  //===========================================================================
  // addOneHit
  //===========================================================================
  private void addOneHit() {
    if (m_isTeam1AtBat) {
      team1SetHits(1, true);
    } else {
      team2SetHits(1, true);
    }
  }

  //===========================================================================
  // advanceAtBatTo
  //===========================================================================
  private void advanceAtBatTo(int runnerOnTo) {
    setRunnersOn(runnerOnTo, RUNNER_NONE);
  }

  //===========================================================================
  // advanceRunnerFromTo
  //===========================================================================
  private void advanceRunnerFromTo(int runnerOnFrom, int runnerOnTo) {
    setRunnersOn(runnerOnTo, runnerOnFrom);
  }

  //===========================================================================
  // advanceRunnerHome
  //===========================================================================
  private void advanceRunnerHome(int runnerOnFrom) {
    setRunnersOn(RUNNER_NONE, runnerOnFrom);

    if (m_isTeam1AtBat) {
      team1SetRuns(1, true);
    } else {
      team2SetRuns(1, true);
    }
  }

  //===========================================================================
  // areRunnersOn
  //===========================================================================
  private boolean areRunnersOn(int runnerOnFlags) {
    return((m_runnersOn & runnerOnFlags) == runnerOnFlags);
  }

  //===========================================================================
  // areBasesLoaded
  //===========================================================================
  private boolean areBasesLoaded() {
    return(areRunnersOn(RUNNER_ALL));
  }

  //===========================================================================
  // atBatPutOut
  //===========================================================================
  private void atBatPutOut() {
    if (m_outs < 2) {
      setOuts(1, true);
      setAtBat();
    } else {
      setNextHalfInning(!m_isTeam1AtBat);
    }
  }

  //===========================================================================
  // atBatWalk
  //===========================================================================
  private void atBatWalk() {
    if (isRunnerOnFirst()) {
      if (isRunnerOnSecond()) {
        if (isRunnerOnThird()) {
          advanceRunnerHome(RUNNER_ON_THIRD);
        }
        advanceRunnerFromTo(RUNNER_ON_SECOND, RUNNER_ON_THIRD);
      }
      advanceRunnerFromTo(RUNNER_ON_FIRST, RUNNER_ON_SECOND);
    }

    advanceAtBatTo(RUNNER_ON_FIRST);
    setAtBat();
    updateButtonStates();
  }

  //===========================================================================
  // isRunnerOnFirst
  //===========================================================================
  private boolean isRunnerOnFirst() {
    return(areRunnersOn(RUNNER_ON_FIRST));
  }

  //===========================================================================
  // isRunnerOnSecond
  //===========================================================================
  private boolean isRunnerOnSecond() {
    return(areRunnersOn(RUNNER_ON_SECOND));
  }

  //===========================================================================
  // isRunnerOnThird
  //===========================================================================
  private boolean isRunnerOnThird() {
    return(areRunnersOn(RUNNER_ON_THIRD));
  }

  //===========================================================================
  // reset
  //===========================================================================
  private void reset() {
    setRandomTeams();

    m_team1 = new TeamData();
    team1SetRuns(0, false);
    team1SetHits(0, false);

    m_team2 = new TeamData();
    team2SetRuns(0, false);
    team2SetHits(0, false);

    setNextHalfInning(true);
  }

  //===========================================================================
  // runnerPutOut
  //===========================================================================
  private void runnerPutOut(int runnerOn) {
    if (m_outs < 2) {
      setOuts(1, true);
      setRunnersOn(RUNNER_NONE, runnerOn);
    } else {
      setNextHalfInning(!m_isTeam1AtBat);
    }
    updateButtonStates();
  }

  //===========================================================================
  // setAtBat
  //===========================================================================
  private void setAtBat() {
    setStrikes(0, false);
    setBalls(0, false);
  }

  //===========================================================================
  // setBalls
  //===========================================================================
  private void setBalls(int value, boolean offset) {
    m_balls = offset ? m_balls + value : value;

    String format = getResources().getString(R.string.balls);
    String text = String.format(format, m_balls);
    m_binding.txtBalls.setText(text);
  }

  //===========================================================================
  // setNextHalfInning
  //===========================================================================
  private void setNextHalfInning(boolean isTeam1AtBat) {
    m_isTeam1AtBat = isTeam1AtBat;

    String offense = getResources().getString(R.string.team_position_offense);
    String defense = getResources().getString(R.string.team_position_defense);

    if (m_isTeam1AtBat) {
      m_binding.txtTeam1Position.setText(offense);
      m_binding.txtTeam2Position.setText(defense);
    } else {
      m_binding.txtTeam2Position.setText(offense);
      m_binding.txtTeam1Position.setText(defense);
    }

    setOuts(0, false);
    setRunnersOn(RUNNER_NONE, RUNNER_ALL);
    setAtBat();
    updateButtonStates();
  }

  //===========================================================================
  // setOuts
  //===========================================================================
  private void setOuts(int value, boolean offset) {
    m_outs = offset ? m_outs + value : value;

    String format = getResources().getString(R.string.outs);
    String text = String.format(format, m_outs);
    m_binding.txtOuts.setText(text);
  }

  //===========================================================================
  // setRandomTeams
  //===========================================================================
  private void setRandomTeams() {
    ArrayList<TeamInitData> teamsInitData = new ArrayList<TeamInitData>(Arrays.asList(TEAMS_DATA));

    //randomly select a name for team 1
    int randomIndex = (int)(Math.random() * teamsInitData.size());
    TeamInitData teamInitData = teamsInitData.get(randomIndex);

    //remove this entry, so that it is not randomly chosen again - we don't want the same
    // team twice
    teamsInitData.remove(randomIndex);

    m_binding.txtTeam1Name.setText(getResources().getText(teamInitData.nameResource()));
    m_binding.imgTeam1.setImageResource(teamInitData.imageResource());

    //randomly select a name for team 2
    randomIndex = (int)(Math.random() * teamsInitData.size());
    teamInitData = teamsInitData.get(randomIndex);

    m_binding.txtTeam2Name.setText(getResources().getText(teamInitData.nameResource()));
    m_binding.imgTeam2.setImageResource(teamInitData.imageResource());
  }

  //===========================================================================
  // setRunnersOn
  //===========================================================================
  private void setRunnersOn(int runnersOnAdd, int runnersOnRemove) {
    m_runnersOn |= runnersOnAdd;
    m_runnersOn &= ~runnersOnRemove;

    String format;

    if (areBasesLoaded()) {
      //check for bases loaded first
      format = getResources().getText(R.string.runners_all).toString();
    } else if (m_runnersOn == RUNNER_NONE) {
      //bases are not loaded, next check if bases are empty
      format = getResources().getText(R.string.runners_none).toString();
    } else {
      //we have either one or two runners on base

      //add all the runners on a base to to this array
      ArrayList<String> runners = new ArrayList<>();
      if (isRunnerOnFirst()) {
        runners.add(getResources().getText(R.string.runners_first).toString());
      }

      if (isRunnerOnSecond()) {
        runners.add(getResources().getText(R.string.runners_second).toString());
      }

      if (isRunnerOnThird()) {
        runners.add(getResources().getText(R.string.runners_third).toString());
      }

      //assign the correct format message depending on if there are one or two runners on base
      if (runners.size() == 1) {
        //there is only one runner on base
        format = String.format(getResources().getText(R.string.runners_singular).toString(),
          runners.get(0));
      } else {
        //there are two runners on base
        format = String.format(getResources().getText(R.string.runners_plural).toString(),
          runners.get(0), runners.get(1));
      }
    }

    m_binding.txtRunnersOn.setText(format);
  }

  //===========================================================================
  // setStrikes
  //===========================================================================
  private void setStrikes(int value, boolean offset) {
    m_strikes = offset ? m_strikes + value : value;

    String format = getResources().getString(R.string.strikes);
    String text = String.format(format, m_strikes);
    m_binding.txtStrikes.setText(text);
  }

  //===========================================================================
  // setTextFromInt
  //===========================================================================
  private void setTextFromInt(TextView textView, int value) {
    textView.setText(String.valueOf(value));
  }

  //===========================================================================
  // team1SetHits
  //===========================================================================
  private void team1SetHits(int value, boolean offset) {
    m_team1.setHits(offset ? m_team1.getHits() + value : value);

    String format = getResources().getString(R.string.hits);
    String text = String.format(format, m_team1.getHits());
    m_binding.txtTeam1Hits.setText(text);
  }

  //===========================================================================
  // team1SetRuns
  //===========================================================================
  private void team1SetRuns(int value, boolean offset) {
    m_team1.setRuns(offset ? m_team1.getRuns() + value : value);
    setTextFromInt(m_binding.txtTeam1Score, m_team1.getRuns());
  }

  //===========================================================================
  // team2SetHits
  //===========================================================================
  private void team2SetHits(int value, boolean offset) {
    m_team2.setHits(offset ? m_team2.getHits() + value : value);

    String format = getResources().getString(R.string.hits);
    String text = String.format(format, m_team2.getHits());
    m_binding.txtTeam2Hits.setText(text);
  }

  //===========================================================================
  // team2SetRuns
  //===========================================================================
  private void team2SetRuns(int value, boolean offset) {
    m_team2.setRuns(offset ? m_team2.getRuns() + value : value);
    setTextFromInt(m_binding.txtTeam2Score, m_team2.getRuns());
  }

  //===========================================================================
  // updateButtonStates
  //===========================================================================
  private void updateButtonStates() {

    //update steal-2nd button
    if (isRunnerOnFirst() && !isRunnerOnSecond()) {
      m_binding.btnSteal2nd.setEnabled(true);
    } else {
      m_binding.btnSteal2nd.setEnabled(false);
    }

    //update steal-3rd button
    if (isRunnerOnSecond() && !isRunnerOnThird()) {
      m_binding.btnSteal3rd.setEnabled(true);
    } else {
      m_binding.btnSteal3rd.setEnabled(false);
    }

    //update steal-home button
    m_binding.btnStealHome.setEnabled(isRunnerOnThird());

    //update out-at-1st button
    m_binding.btnOutAt1st.setEnabled(isRunnerOnFirst());

    //update out-at-2nd button
    m_binding.btnOutAt2nd.setEnabled(isRunnerOnSecond());

    //update out-at-3rd button
    m_binding.btnOutAt3rd.setEnabled(isRunnerOnThird());

    //update home-run button text
    m_binding.btnHomer.setText(areBasesLoaded() ?
            getResources().getString(R.string.grand_slam) :
            getResources().getString(R.string.home_run));
  }
}