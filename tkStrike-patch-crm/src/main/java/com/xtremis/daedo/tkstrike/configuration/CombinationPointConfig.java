package com.xtremis.daedo.tkstrike.configuration;

import com.xtremis.daedo.tkstrike.om.ActionSource;
import com.xtremis.daedo.tkstrike.service.AppStatusWorker;
import com.xtremis.daedo.tkstrike.service.MatchWorker;
import com.xtremis.daedo.tkstrike.ui.CRMCombinationsHelper;
import com.xtremis.daedo.tkstrike.ui.controller.TkStrikeMainControllerImpl;
import com.xtremis.daedo.tkstrike.ui.model.IRulesEntry;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;

/**
 * @author Administrator
 *
 */
class CombinationPointConfig {

	private CombinationPointConfig() {
	}

	static void apply(TkStrikeMainControllerImpl tkStrikeMainControllerImpl, Node rootView, MatchWorker matchWorker,
			AppStatusWorker appStatusWorker) {
		IRulesEntry rulesEntry = appStatusWorker.getRulesEntry();
		long currentRoundCountdown = matchWorker.getCurrentRoundCountdownAsMillis();
		rootView.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (CRMCombinationsHelper.key1.match(event) || CRMCombinationsHelper.keyP1.match(event)) {
					matchWorker.addBlueBodyPoint(rulesEntry.getBodyPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown, 0);
				} else if (CRMCombinationsHelper.keyCombM1.match(event)
						|| CRMCombinationsHelper.keyCombNumM1.match(event)) {
					matchWorker.addBlueBodyPoint(rulesEntry.getBodyPoints() * -1, ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown, 0);
				} else if (CRMCombinationsHelper.key2.match(event) || CRMCombinationsHelper.keyP2.match(event)) {
					matchWorker.addBlueBodyTechPoint(rulesEntry.getBodyTechPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
				} else if (CRMCombinationsHelper.keyCombM2.match(event)
						|| CRMCombinationsHelper.keyCombNumM2.match(event)) {
					matchWorker.addBlueBodyTechPoint(rulesEntry.getBodyTechPoints() * -1,
							ActionSource.SCOREBOARD_EDITOR, System.currentTimeMillis(), currentRoundCountdown);
				} else if (CRMCombinationsHelper.key3.match(event) || CRMCombinationsHelper.keyP3.match(event)) {
					matchWorker.addBlueHeadPoint(rulesEntry.getHeadPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown, 0);
				} else if (CRMCombinationsHelper.keyCombM3.match(event)
						|| CRMCombinationsHelper.keyCombNumM3.match(event)) {
					matchWorker.addBlueHeadPoint(rulesEntry.getHeadPoints() * -1, ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown, 0);
				} else if (CRMCombinationsHelper.key4.match(event) || CRMCombinationsHelper.keyP4.match(event)) {
					matchWorker.addBlueHeadTechPoint(rulesEntry.getHeadTechPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
				} else if (CRMCombinationsHelper.keyCombM4.match(event)
						|| CRMCombinationsHelper.keyCombNumM4.match(event)) {
					matchWorker.addBlueHeadTechPoint(rulesEntry.getHeadTechPoints() * -1,
							ActionSource.SCOREBOARD_EDITOR, System.currentTimeMillis(), currentRoundCountdown);
				} else if (CRMCombinationsHelper.key5.match(event) || CRMCombinationsHelper.keyP5.match(event)) {
					matchWorker.addBluePunchPoint(rulesEntry.getPunchPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
				} else if (CRMCombinationsHelper.keyCombM5.match(event)
						|| CRMCombinationsHelper.keyCombNumM5.match(event)) {
					matchWorker.addBluePunchPoint(rulesEntry.getPunchPoints() * -1, ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
				} else if (CRMCombinationsHelper.key6.match(event) || CRMCombinationsHelper.keyP6.match(event)) {
					matchWorker.addRedBodyPoint(rulesEntry.getBodyPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown, 0);
				} else if (CRMCombinationsHelper.keyCombM6.match(event)
						|| CRMCombinationsHelper.keyCombNumM6.match(event)) {
					matchWorker.addRedBodyPoint(rulesEntry.getBodyPoints() * -1, ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown, 0);
				} else if (CRMCombinationsHelper.key7.match(event) || CRMCombinationsHelper.keyP7.match(event)) {
					matchWorker.addRedBodyTechPoint(rulesEntry.getBodyTechPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
				} else if (CRMCombinationsHelper.keyCombM7.match(event)
						|| CRMCombinationsHelper.keyCombNumM7.match(event)) {
					matchWorker.addRedBodyTechPoint(rulesEntry.getBodyTechPoints() * -1, ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
				} else if (CRMCombinationsHelper.key8.match(event) || CRMCombinationsHelper.keyP8.match(event)) {
					matchWorker.addRedHeadPoint(rulesEntry.getHeadPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown, 0);
				} else if (CRMCombinationsHelper.keyCombM8.match(event)
						|| CRMCombinationsHelper.keyCombNumM8.match(event)) {
					matchWorker.addRedHeadPoint(rulesEntry.getHeadPoints() * -1, ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown, 0);
				} else if (CRMCombinationsHelper.key9.match(event) || CRMCombinationsHelper.keyP9.match(event)) {
					matchWorker.addRedHeadTechPoint(rulesEntry.getHeadTechPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
				} else if (CRMCombinationsHelper.keyCombM9.match(event)
						|| CRMCombinationsHelper.keyCombNumM9.match(event)) {
					matchWorker.addRedHeadTechPoint(rulesEntry.getHeadTechPoints() * -1, ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
				} else if (CRMCombinationsHelper.key0.match(event) || CRMCombinationsHelper.keyP0.match(event)) {
					matchWorker.addRedPunchPoint(rulesEntry.getPunchPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
				} else if (CRMCombinationsHelper.keyCombM0.match(event)
						|| CRMCombinationsHelper.keyCombNumM0.match(event)) {
					matchWorker.addRedPunchPoint(rulesEntry.getPunchPoints() * -1, ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
				}
			}
		});
	}

}
