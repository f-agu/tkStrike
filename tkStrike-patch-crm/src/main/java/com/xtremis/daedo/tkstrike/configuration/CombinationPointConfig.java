package com.xtremis.daedo.tkstrike.configuration;

import com.xtremis.daedo.tkstrike.om.ActionSource;
import com.xtremis.daedo.tkstrike.service.AppStatusWorker;
import com.xtremis.daedo.tkstrike.service.MatchWorker;
import com.xtremis.daedo.tkstrike.tools.LimitedLastQueue;
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

		LimitedLastQueue<Runnable> revertRunnables = new LimitedLastQueue<>(100);
		rootView.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (CRMCombinationsHelper.key1.match(event) || CRMCombinationsHelper.keyP1.match(event)) {
					// poing => 1
					matchWorker.addBluePunchPoint(rulesEntry.getPunchPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
					revertRunnables.add(() -> matchWorker.addBluePunchPoint(rulesEntry.getPunchPoints() * -1,
							ActionSource.SCOREBOARD_EDITOR, System.currentTimeMillis(), currentRoundCountdown));
				} else if (CRMCombinationsHelper.key2.match(event) || CRMCombinationsHelper.keyP2.match(event)) {
					// plastron 2 => 2
					matchWorker.addBlueBodyPoint(rulesEntry.getBodyPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown, 0);
					revertRunnables.add(() -> matchWorker.addBlueBodyPoint(rulesEntry.getBodyPoints() * -1,
							ActionSource.SCOREBOARD_EDITOR, System.currentTimeMillis(), currentRoundCountdown, 0));
				} else if (CRMCombinationsHelper.key3.match(event) || CRMCombinationsHelper.keyP3.match(event)) {
					// tete 3 => 3
					matchWorker.addBlueHeadPoint(rulesEntry.getHeadPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown, 0);
					revertRunnables.add(() -> matchWorker.addBlueHeadPoint(rulesEntry.getHeadPoints() * -1,
							ActionSource.SCOREBOARD_EDITOR, System.currentTimeMillis(), currentRoundCountdown, 0));
				} else if (CRMCombinationsHelper.key4.match(event) || CRMCombinationsHelper.keyP4.match(event)) {
					// plastron 2+2 => 4
					matchWorker.addBlueBodyTechPoint(rulesEntry.getBodyTechPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
					revertRunnables.add(() -> matchWorker.addBlueBodyTechPoint(rulesEntry.getBodyTechPoints() * -1,
							ActionSource.SCOREBOARD_EDITOR, System.currentTimeMillis(), currentRoundCountdown));
				} else if (CRMCombinationsHelper.key5.match(event) || CRMCombinationsHelper.keyP5.match(event)) {
					// tete 3+2 => 5
					matchWorker.addBlueHeadTechPoint(rulesEntry.getHeadTechPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
					revertRunnables.add(() -> matchWorker.addBlueHeadTechPoint(rulesEntry.getHeadTechPoints() * -1,
							ActionSource.SCOREBOARD_EDITOR, System.currentTimeMillis(), currentRoundCountdown));
				} else if (CRMCombinationsHelper.key6.match(event) || CRMCombinationsHelper.keyP6.match(event)) {
					// poing 1 => n°6
					matchWorker.addRedPunchPoint(rulesEntry.getPunchPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
					revertRunnables.add(() -> matchWorker.addRedPunchPoint(rulesEntry.getPunchPoints() * -1,
							ActionSource.SCOREBOARD_EDITOR, System.currentTimeMillis(), currentRoundCountdown));
				} else if (CRMCombinationsHelper.key7.match(event) || CRMCombinationsHelper.keyP7.match(event)) {
					// plastron 2 => n°7
					matchWorker.addRedBodyPoint(rulesEntry.getBodyPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown, 0);
					revertRunnables.add(() -> matchWorker.addRedBodyPoint(rulesEntry.getBodyPoints() * -1,
							ActionSource.SCOREBOARD_EDITOR, System.currentTimeMillis(), currentRoundCountdown, 0));
				} else if (CRMCombinationsHelper.key8.match(event) || CRMCombinationsHelper.keyP8.match(event)) {
					// tete 3 => n°8
					matchWorker.addRedHeadPoint(rulesEntry.getHeadPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown, 0);
					revertRunnables.add(() -> matchWorker.addRedHeadPoint(rulesEntry.getHeadPoints() * -1,
							ActionSource.SCOREBOARD_EDITOR, System.currentTimeMillis(), currentRoundCountdown, 0));
				} else if (CRMCombinationsHelper.key9.match(event) || CRMCombinationsHelper.keyP9.match(event)) {
					// plastron 2+2 => n°9
					matchWorker.addRedBodyTechPoint(rulesEntry.getBodyTechPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
					revertRunnables.add(() -> matchWorker.addRedBodyTechPoint(rulesEntry.getBodyTechPoints() * -1,
							ActionSource.SCOREBOARD_EDITOR, System.currentTimeMillis(), currentRoundCountdown));
				} else if (CRMCombinationsHelper.key0.match(event) || CRMCombinationsHelper.keyP0.match(event)) {
					// tete 3+2 => n°0
					matchWorker.addRedHeadTechPoint(rulesEntry.getHeadTechPoints(), ActionSource.SCOREBOARD_EDITOR,
							System.currentTimeMillis(), currentRoundCountdown);
					revertRunnables.add(() -> matchWorker.addRedHeadTechPoint(rulesEntry.getHeadTechPoints() * -1,
							ActionSource.SCOREBOARD_EDITOR, System.currentTimeMillis(), currentRoundCountdown));

				} else if (CRMCombinationsHelper.keyU.match(event)) {
					if (revertRunnables.isEmpty()) {
						return;
					}
					revertRunnables.removeLast().run();
				}
			}
		});
	}

}
